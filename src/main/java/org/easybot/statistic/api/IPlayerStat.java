package org.easybot.statistic.api;
import java.util.Optional;

/**
 * 玩家统计数据访问接口
 */
public interface IPlayerStat {

    /**
     * 重新加载玩家统计数据
     */
    void reloadData();

    /**
     * 获取自定义统计值
     * @param name 统计名称
     * @return 统计值的Optional包装
     */
    Optional<String> getCustom(String name);

    /**
     * 获取击杀实体统计值
     * @param name 实体名称
     * @return 统计值的Optional包装
     */
    Optional<String> getEntityKilled(String name);

    /**
     * 获取被实体击杀统计值
     * @param name 实体名称
     * @return 统计值的Optional包装
     */
    Optional<String> getEntityKilledBy(String name);

    /**
     * 获取挖掘方块统计值
     * @param name 方块名称
     * @return 统计值的Optional包装
     */
    Optional<String> getMined(String name);

    /**
     * 获取物品损坏统计值
     * @param name 物品名称
     * @return 统计值的Optional包装
     */
    Optional<String> getBroken(String name);

    /**
     * 获取物品合成统计值
     * @param name 物品名称
     * @return 统计值的Optional包装
     */
    Optional<String> getCrafted(String name);

    /**
     * 获取物品使用统计值
     * @param name 物品名称
     * @return 统计值的Optional包装
     */
    Optional<String> getUsed(String name);

    /**
     * 获取物品拾取统计值
     * @param name 物品名称
     * @return 统计值的Optional包装
     */
    Optional<String> getPickedUp(String name);

    /**
     * 获取物品丢弃统计值
     * @param name 物品名称
     * @return 统计值的Optional包装
     */
    Optional<String> getDropped(String name);
}
