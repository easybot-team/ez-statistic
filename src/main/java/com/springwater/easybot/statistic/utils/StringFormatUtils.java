package com.springwater.easybot.statistic.utils;

/**
 * 字符串格式转换工具类
 */
public class StringFormatUtils {

    private static final char UNDERLINE = '_';
    private static final char COLON = ':';
    private static final char DOT = '.';

    private StringFormatUtils() {
    }

    /**
     * 驼峰转下划线
     * 示例: "doFunTest" -> "do_fun_test"
     *
     * @param param 驼峰字符串
     * @return 下划线字符串
     */
    public static String camelToSnake(String param) {
        if (param == null || param.trim().isEmpty()) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    char prev = param.charAt(i - 1);
                    if (Character.isLowerCase(prev) ||
                            (Character.isUpperCase(prev) && i + 1 < len && Character.isLowerCase(param.charAt(i + 1)))) {
                        sb.append(UNDERLINE);
                    }
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    /**
     * 下划线转驼峰
     * 示例: "do_fun_test" -> "doFunTest"
     *
     * @param param 下划线字符串
     * @return 驼峰字符串
     */
    public static String snakeToCamel(String param) {
        if (param == null || param.trim().isEmpty()) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        boolean nextUpperCase = false;

        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (!sb.isEmpty()) {
                    nextUpperCase = true;
                }
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 到大驼峰
     */
    public static String toBigCamel(String param) {
        return Character.toUpperCase(param.charAt(0)) + param.substring(1);
    }



    /**
     * 冒号转点
     * 示例: "dd:ff:aa" -> "dd.ff.aa"
     *
     * @param str 输入字符串
     * @return 转换后的字符串
     */
    public static String colonToDot(String str) {
        if (str == null) {
            return null;
        }
        return str.replace(COLON, DOT);
    }

    /**
     * 点转冒号
     * 示例: "dd.ff.aa" -> "dd:ff:aa"
     *
     * @param str 输入字符串
     * @return 转换后的字符串
     */
    public static String dotToColon(String str) {
        if (str == null) {
            return null;
        }
        return str.replace(DOT, COLON);
    }

    /**
     * 通用分隔符转换 (扩展方法)
     *
     * @param str     字符串
     * @param oldChar 旧分隔符
     * @param newChar 新分隔符
     * @return 转换后的字符串
     */
    public static String convertDelimiter(String str, char oldChar, char newChar) {
        if (str == null) {
            return null;
        }
        return str.replace(oldChar, newChar);
    }
}
