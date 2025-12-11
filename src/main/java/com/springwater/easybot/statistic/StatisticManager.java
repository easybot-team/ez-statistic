package com.springwater.easybot.statistic;

import com.springwater.easybot.statistic.logger.DefaultLoggerAdapter;
import com.springwater.easybot.statistic.logger.ILogger;
import lombok.Getter;
import lombok.Setter;
import com.springwater.easybot.statistic.api.IPlayerStat;
import com.springwater.easybot.statistic.api.IUuidNameCache;
import com.springwater.easybot.statistic.api.IStatisticManager;
import com.springwater.easybot.statistic.cache.UuidNameCache;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StatisticManager implements IStatisticManager {
    @Getter
    private static final StatisticManager instance = new StatisticManager();
    @Getter
    private IUuidNameCache statDb;
    @Setter
    private Path savePath = Paths.get("stats");
    @Setter
    @Getter
    private ILogger logger = new DefaultLoggerAdapter();
    
    public void initDb(String savePath){
        statDb = new UuidNameCache(savePath);
    }
    
    public IPlayerStat getPlayerStat(String uuidOrName) {
        return new PlayerStat(uuidOrName, savePath);
    }
    private StatisticManager() {
    }
}
