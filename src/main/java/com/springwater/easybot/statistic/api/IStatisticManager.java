package com.springwater.easybot.statistic.api;

public interface IStatisticManager {
    IUuidNameCache getStatDb();
    IPlayerStat getPlayerStat(String uuidOrName);
}
