package com.springwater.easybot.statistic;

import lombok.Getter;
import lombok.Setter;
import com.springwater.easybot.statistic.api.IPlayerStat;
import com.springwater.easybot.statistic.api.IUuidNameCache;
import com.springwater.easybot.statistic.api.IStatisticManager;
import com.springwater.easybot.statistic.cache.UuidNameCache;

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

    /**
     * 获取玩家统计数据（带滑动窗口缓存）
     */
    public IPlayerStat getPlayerStat(String uuidOrName) {
        CacheEntry entry = cache.computeIfAbsent(uuidOrName, k ->
                new CacheEntry(new PlayerStat(k, savePath))
        );
        // 刷新最后访问时间（滑动窗口
        entry.refreshAccessTime();
        return entry.getStat();
    }

    @Override
    public IPlayerStat loadPlayerStat(String rawJson) {
        return new PlayerStat(rawJson);
    }

    /**
     * 清理过期缓存的任务
     */
    private void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();
        cache.entrySet().removeIf(entry -> (now - entry.getValue().getLastAccessTime()) > EXPIRE_DURATION_MS);
    }

    /**
     * Stat的缓存
     */
    @Getter
    private static class CacheEntry {
        private final IPlayerStat stat;
        private volatile long lastAccessTime;
        public CacheEntry(IPlayerStat stat) {
            this.stat = stat;
            this.lastAccessTime = System.currentTimeMillis();
        }
        public void refreshAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }

    }
}