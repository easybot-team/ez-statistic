package com.springwater.easybot.statistic.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class MojangUUIDFetcher {

    private static final String MOJANG_API_TEMPLATE = "https://api.mojang.com/users/profiles/minecraft/%s";
    // 建议复用 Gson 实例以提高性能
    private static final Gson GSON = new Gson();

    /**
     * 使用 Gson 获取 Mojang 正版玩家 UUID。
     *
     * @param playerName 玩家名称
     * @return Optional<UUID> 包含玩家的 UUID，如果失败或不存在则返回 Empty
     */
    public static Optional<UUID> getOnlineUUID(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return Optional.empty();
        }

        try {
            URL url = new URI(String.format(MOJANG_API_TEMPLATE, playerName)).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5秒连接超时
            connection.setReadTimeout(5000);    // 5秒读取超时
            if (connection.getResponseCode() != 200) {
                return Optional.empty();
            }

            try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject jsonResponse = GSON.fromJson(reader, JsonObject.class);
                if (jsonResponse != null && jsonResponse.has("id")) {
                    String rawId = jsonResponse.get("id").getAsString();
                    return Optional.of(parseTrimmedUUID(rawId));
                }
            }

        } catch (Exception ignored) {
            
        }

        return Optional.empty();
    }

    /**
     * 将 Mojang 返回的 32位无横线 UUID 转换为 Java 标准 UUID 对象。
     * 例如: "56561f9d50124888981f9642694b2f15" -> 56561f9d-5012-4888-981f-9642694b2f15
     */
    private static UUID parseTrimmedUUID(String trimmedUuid) {
        if (trimmedUuid == null || trimmedUuid.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID string: " + trimmedUuid);
        }

        // 高效插入连字符
        StringBuilder sb = new StringBuilder(trimmedUuid);
        sb.insert(20, "-");
        sb.insert(16, "-");
        sb.insert(12, "-");
        sb.insert(8, "-");

        return UUID.fromString(sb.toString());
    }
}