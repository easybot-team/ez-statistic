package com.springwater.easybot.statistic;

import lombok.Getter;

import java.util.Objects;

public class NamespaceKey {
    public static final String DEFAULT_NAMESPACE = "minecraft";
    private static final char SEPARATOR = ':';
    @Getter
    private final String namespace;
    @Getter
    private final String path;
    // 用于标记解析时原本是否包含了命名空间
    @Getter
    private final boolean hasExplicitNamespace;

    /**
     * 构造函数：解析字符串
     *
     * @param fullString 输入字符串，如 "minecraft:walk_one_cm" 或 "walk_one_cm"
     */
    public NamespaceKey(String fullString) {
        if (fullString == null || fullString.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        int colonIndex = fullString.indexOf(SEPARATOR);

        if (colonIndex >= 0) {
            this.namespace = fullString.substring(0, colonIndex);
            this.path = fullString.substring(colonIndex + 1);
            this.hasExplicitNamespace = true;
        } else {
            this.namespace = DEFAULT_NAMESPACE;
            this.path = fullString;
            this.hasExplicitNamespace = false;
        }
    }

    /**
     * 构造函数：直接指定 namespace 和 path
     */
    public NamespaceKey(String namespace, String path) {
        this.namespace = (namespace == null || namespace.isEmpty()) ? DEFAULT_NAMESPACE : namespace;
        this.path = path;
        this.hasExplicitNamespace = true;
    }

    /**
     * 获取完整标识符
     *
     * @return "namespace:path"
     */
    @Override
    public String toString() {
        return namespace + SEPARATOR + path;
    }

    public String toOriginalString() {
        if (hasExplicitNamespace) {
            return toString();
        } else {
            return path;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamespaceKey that = (NamespaceKey) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }
}
