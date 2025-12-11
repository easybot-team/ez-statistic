package org.easybot.statistic;

import lombok.Getter;
import lombok.Setter;
import org.easybot.statistic.api.IPlayerStat;
import org.easybot.statistic.api.IUuidNameCache;
import org.easybot.statistic.api.IStatisticManager;
import org.easybot.statistic.cache.UuidNameCache;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StatisticManager implements IStatisticManager {
    @Getter
    private static final StatisticManager instance = new StatisticManager();
    @Getter
    private IUuidNameCache statDb;
    @Setter
    private Path savePath = Paths.get("stats");
    public void initDb(String savePath){
        statDb = new UuidNameCache(savePath);
    }
    
    public IPlayerStat getPlayerStat(String uuidOrName) {
        return new PlayerStat(uuidOrName, savePath);
    }
    private StatisticManager() {
    }
}
