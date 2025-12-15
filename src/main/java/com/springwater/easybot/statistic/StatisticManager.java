package com.springwater.easybot.statistic;

import com.springwater.easybot.statistic.api.IPlayerStat;
import com.springwater.easybot.statistic.api.IUuidNameCache;
import com.springwater.easybot.statistic.api.IStatisticManager;
import com.springwater.easybot.statistic.cache.UuidNameCache;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.*;

public class StatisticManager implements IStatisticManager {
    @Getter
    private static final StatisticManager instance = new StatisticManager();

    @Getter
    private IUuidNameCache statDb;

    @Setter
    private Path savePath = Paths.get("stats");
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long EXPIRE_DURATION_MS = 10_000L;

    private final ExecutorService fileIoExecutor = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "player-stat-loader");
        t.setDaemon(true);
        return t;
    });

    private StatisticManager() {
        ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "StatCache-Cleaner");
            t.setDaemon(true);
            return t;
        });
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredEntries, 1, 1, TimeUnit.SECONDS);
    }

    public void initDb(String savePath) {
        statDb = new UuidNameCache(savePath);
    }

    @Override
    public IPlayerStat getPlayerStat(String uuidOrName) {
        CacheEntry entry = cache.computeIfAbsent(uuidOrName, k -> new CacheEntry(k, savePath, fileIoExecutor));
        entry.refreshAccessTime();

        try {
            return entry.getStatBlocking();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            cache.remove(uuidOrName, entry); // 加载失败，从缓存中移除
            throw new RuntimeException("Failed to load player stat for " + uuidOrName, e.getCause());
        }
    }

    @Override
    public IPlayerStat loadPlayerStat(String rawJson) {
        return new PlayerStat(rawJson);
    }

    private void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> {
            boolean expired = (now - entry.getValue().getLastAccessTime()) > EXPIRE_DURATION_MS;
            if (expired && entry.getValue().futureStat.isDone()) {
                // 只有当任务完成且已过期时才移除
                return true;
            }
            // 如果任务还没完成，即使时间到了也不移除
            return false;
        });
    }

    @Getter
    private static class CacheEntry {
        private final CompletableFuture<IPlayerStat> futureStat;
        private volatile long lastAccessTime;

        public CacheEntry(String uuidOrName, Path savePath, ExecutorService executor) {
            this.futureStat = CompletableFuture.supplyAsync(() -> new PlayerStat(uuidOrName, savePath), executor);
            this.lastAccessTime = System.currentTimeMillis();
        }

        public void refreshAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        public IPlayerStat getStatBlocking() throws InterruptedException, ExecutionException {
            return futureStat.get();
        }
    }
}
