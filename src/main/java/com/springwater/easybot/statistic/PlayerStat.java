package com.springwater.easybot.statistic;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.springwater.easybot.statistic.adapter.EasyBotBukkitAdapter;
import com.springwater.easybot.statistic.api.IPlayerStat;
import com.springwater.easybot.statistic.api.IUuidNameCache;
import com.springwater.easybot.statistic.utils.MojangUUIDFetcher;
import com.springwater.easybot.statistic.utils.StringFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerStat implements IPlayerStat {
    private String uuidOrName;
    private String filePath;
    private final Path statsDirectory;
    private DocumentContext context;
    private final Logger logger = LoggerFactory.getLogger(PlayerStat.class);
    private final IUuidNameCache cacheDb = StatisticManager.getInstance().getStatDb();

    private static final Map<String, String> OLD_CUSTOM_MAPPING = Map.of(
            "play_time", "play_one_minute"
    );

    private static final Map<String, String> NEW_CUSTOM_MAPPING = OLD_CUSTOM_MAPPING
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));


    public PlayerStat(String uuidOrName, Path statsDirectory) {
        this.uuidOrName = uuidOrName;
        this.statsDirectory = statsDirectory;
        this.filePath = statsDirectory.resolve(uuidOrName + ".json").toString();
        reloadData();
    }

    public PlayerStat(String rawJson) {
        statsDirectory = null;
        context = JsonPath.parse(rawJson);
        this.uuidOrName = EMPTY_UUID.toString();
        filePath = null;
    }

    private static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public void reloadData() {
        if (this.statsDirectory == null) return;

        // 尝试直接加载指定的文件路径
        if (tryLoadContext(Paths.get(this.filePath))) {
            return;
        }
        // 获取缓存 UUID
        String cachedUuidStr = cacheDb.getUuidCache(this.uuidOrName)
                .orElse(EMPTY_UUID)
                .toString();
        if (tryLoadContext(this.statsDirectory.resolve(cachedUuidStr + ".json"))) {
            return;
        }

        // 计算离线 UUID
        UUID offlineUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.uuidOrName).getBytes(StandardCharsets.UTF_8));

        // 尝试加载离线模式的数据文件
        if (tryLoadContext(this.statsDirectory.resolve(offlineUuid + ".json"))) {
            this.cacheDb.putUuidCache(this.uuidOrName, offlineUuid);
            return;
        }

        // 尝试获取正版 UUID 并加载
        try {
            Optional<UUID> uuidOnlineOpt = MojangUUIDFetcher.getOnlineUUID(this.uuidOrName);
            if (uuidOnlineOpt.isPresent()) {
                String onlineUuidStr = uuidOnlineOpt.get().toString();
                // 更新缓存映射
                this.cacheDb.putUuidCache(this.uuidOrName, offlineUuid);
                Path onlinePath = this.statsDirectory.resolve(onlineUuidStr + ".json");

                // 只有当文件存在且加载成功时，才更新实例的 uuid 字段
                if (tryLoadContext(onlinePath)) {
                    this.uuidOrName = onlineUuidStr;
                    return;
                }
            } else {
                logger.error("玩家 {} 的正版 UUID 未找到", this.uuidOrName);
            }
        } catch (Exception e) {
            // 捕获获取 UUID 过程中的潜在网络异常，防止中断流程
            logger.error("获取玩家 {} 在线 UUID 时发生错误", this.uuidOrName, e);
        }

        // 全部失败
        logger.error("未能加载玩家 {} 的数据 (路径: {})", this.uuidOrName, this.filePath);
        this.context = null;
    }

    /**
     * 尝试从指定路径加载 JSON 数据到 context。
     *
     * @param path 文件路径
     * @return 如果加载成功返回 true，文件不存在或出错返回 false
     */
    private boolean tryLoadContext(Path path) {
        if (path == null || !Files.exists(path)) {
            return false;
        }
        try (InputStream is = Files.newInputStream(path)) {
            this.context = JsonPath.parse(is);
            this.filePath = path.toString();
            return true;
        } catch (IOException e) {
            logger.error("读取文件失败: {}", path, e);
            return false;
        }
    }

    private Optional<String> get(String category, String keyName, String oldJsonPath) {
        if (context == null) return Optional.empty();
        keyName = EasyBotBukkitAdapter.convert(keyName);
        String value = null;
        try {
            // 尝试读取新版格式
            String newJsonPath = "$.stats['" + category + "']['" + keyName + "']";
            Object obj = this.context.read(newJsonPath);
            value = obj.toString();
        } catch (PathNotFoundException e) {
            // 新版失败，尝试读取旧版格式
            if (oldJsonPath != null) {
                try {
                    Object obj = this.context.read(oldJsonPath);
                    value = obj.toString();
                } catch (PathNotFoundException ignored) {
                    // 旧版也未找到
                }
            }
        }
        return Optional.ofNullable(value);
    }

    public Optional<String> getCustom(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:custom";
        
        String newKeyName = NEW_CUSTOM_MAPPING.getOrDefault(
                StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase(),
                StringFormatUtils.camelToSnake(nk.getPath())
        );
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(newKeyName);
        
        String oldKeyName = OLD_CUSTOM_MAPPING.getOrDefault(
                nk.getPath().toLowerCase(),
                nk.getPath()
        );
        String oldPath = "$['stat." + StringFormatUtils.snakeToCamel(oldKeyName) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getEntityKilled(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:killed";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.killEntity." + StringFormatUtils.toBigCamel(nk.getPath()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getEntityKilledBy(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:killed_by";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.entityKilledBy." + StringFormatUtils.toBigCamel(nk.getPath()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getMined(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:mined";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.mineBlock." + StringFormatUtils.colonToDot(nk.toString()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getBroken(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:broken";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.breakItem." + StringFormatUtils.colonToDot(nk.toString()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getCrafted(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:crafted";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.craftItem." + StringFormatUtils.colonToDot(nk.toString()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getUsed(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:used";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.useItem." + StringFormatUtils.colonToDot(nk.toString()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getPickedUp(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:picked_up";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.pickup." + StringFormatUtils.colonToDot(nk.toString()) + "']";

        return get(category, keyName, oldPath);
    }

    public Optional<String> getDropped(String name) {
        NamespaceKey nk = new NamespaceKey(name);
        String category = "minecraft:dropped";
        String keyName = nk.getNamespace() + ":" + StringFormatUtils.camelToSnake(nk.getPath()).toLowerCase();
        String oldPath = "$['stat.drop." + StringFormatUtils.colonToDot(nk.toString()) + "']";

        return get(category, keyName, oldPath);
    }
}