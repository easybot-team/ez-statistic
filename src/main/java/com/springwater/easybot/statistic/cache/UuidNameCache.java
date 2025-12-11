package com.springwater.easybot.statistic.cache;

import com.springwater.easybot.statistic.StatisticManager;
import com.springwater.easybot.statistic.logger.ILogger;
import lombok.Getter;
import com.springwater.easybot.statistic.api.IUuidNameCache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UuidNameCache implements IUuidNameCache {
    @Getter
    private final String savePath;
    private static final ILogger logger = StatisticManager.getInstance().getLogger();
    private final Map<String, UUID> memoryCache = new ConcurrentHashMap<>();

    public UuidNameCache(String savePath) {
        this.savePath = savePath;
        File dir = new File(savePath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                System.err.println("Warning: Could not create cache directory: " + savePath);
            }
        }
    }

    @Override
    public void putUuidCache(String name, UUID uuid) {
        if (name == null || uuid == null) return;
        memoryCache.put(name, uuid);
        saveToDisk(name, uuid);
    }

    @Override
    public Optional<UUID> getUuidCache(String name) {
        if (name == null) return Optional.empty();
        UUID uuid = memoryCache.get(name);
        if (uuid != null) {
            return Optional.of(uuid);
        }
        return loadFromDisk(name);
    }

    private String encodeFilename(String name) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    private void saveToDisk(String name, UUID uuid) {
        try {
            String filename = encodeFilename(name);
            Path filePath = Paths.get(savePath, filename);
            // 直接将 UUID 字符串写入文件，文件内容就是 UUID
            Files.writeString(filePath, uuid.toString());
        } catch (IOException e) {
            logger.warn("Error saving UUID to disk: " + e.getMessage());
        }
    }

    private Optional<UUID> loadFromDisk(String name) {
        try {
            String filename = encodeFilename(name);
            Path filePath = Paths.get(savePath, filename);

            if (Files.exists(filePath)) {
                String uuidStr = Files.readString(filePath);
                UUID uuid = UUID.fromString(uuidStr.trim());

                // 回填到内存缓存，下次读取就很快了
                memoryCache.put(name, uuid);

                return Optional.of(uuid);
            }
        } catch (Exception e) {
            // 文件损坏或读取失败
            logger.warn("Error loading UUID from disk: " + e.getMessage());
        }
        return Optional.empty();
    }
}