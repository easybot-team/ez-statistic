package com.springwater.easybot.statistic.adapter;

public class EasyBotBukkitAdapter {
    public static String convert(String data) {
        if (data.matches("^[A-Z_]+$")) {
            return data.toLowerCase(); // 如: DETECTOR_RAIL -> detector_rail
        }
        return data;
    }
}
