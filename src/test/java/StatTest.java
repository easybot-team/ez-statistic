import com.springwater.easybot.statistic.StatisticManager;
import com.springwater.easybot.statistic.api.IPlayerStat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatTest {
    @BeforeAll
    public static void initDb() {
        StatisticManager.getInstance().initDb("./caches");
    }


    IPlayerStat stat189 = StatisticManager.getInstance().getPlayerStat("9cac0a81-ce71-450e-9113-7ea96f73273a");
    IPlayerStat stat1122 = StatisticManager.getInstance().getPlayerStat("2b963020-1b94-49d0-8a34-9344066fc371");
    IPlayerStat stat121 = StatisticManager.getInstance().getPlayerStat("573016f4-21b4-428b-8606-24c5d700ac1b");

    @Test
    public void doEntityKilledTests() {
        assertEquals("1", stat189.getEntityKilled("minecraft:rabbit").orElse("0"));
        assertEquals("2", stat1122.getEntityKilled("minecraft:rabbit").orElse("0"));
        assertEquals("2", stat121.getEntityKilled("minecraft:cow").orElse("0"));
    }

    @Test
    public void doEntityKilledByTests() {
        assertEquals("1", stat189.getEntityKilledBy("minecraft:creeper").orElse("0"));
        assertEquals("1", stat1122.getEntityKilledBy("minecraft:creeper").orElse("0"));
        assertEquals("1", stat121.getEntityKilledBy("minecraft:zoglin").orElse("0"));
    }

    @Test
    public void doCustomTests() {
        assertEquals("10", stat189.getCustom("minecraft:jump").orElse("0"));
        assertEquals("3607", stat1122.getCustom("minecraft:jump").orElse("0"));
        assertEquals("4", stat121.getCustom("minecraft:jump").orElse("0"));
    }

    @Test
    public void doMinedTests() {
        assertEquals("13", stat189.getMined("minecraft:leaves2").orElse("0"));
        assertEquals("13", stat1122.getMined("minecraft:leaves2").orElse("0"));
        assertEquals("1", stat121.getMined("minecraft:short_grass").orElse("0"));
    }

    @Test
    public void doBrokenTests() {
        assertEquals("1", stat189.getBroken("minecraft:wooden_pickaxe").orElse("0"));
        assertEquals("1", stat1122.getBroken("minecraft:wooden_pickaxe").orElse("0"));
        assertEquals("1", stat121.getBroken("minecraft:netherite_sword").orElse("0"));
    }

    @Test
    public void doCraftedTests() {
        assertEquals("1", stat189.getCrafted("minecraft:wooden_pickaxe").orElse("0"));
        assertEquals("1", stat1122.getCrafted("minecraft:wooden_pickaxe").orElse("0"));
        assertEquals("1", stat121.getCrafted("minecraft:netherite_sword").orElse("0"));
    }

    @Test
    public void doUsedTests() {
        assertEquals("1", stat189.getUsed("minecraft:crafting_table").orElse("0"));
        assertEquals("2", stat1122.getUsed("minecraft:diamond_sword").orElse("0"));
        assertEquals("15", stat121.getUsed("minecraft:netherite_sword").orElse("0"));
    }

    @Test
    public void doPickedUpTests() {
        assertEquals("1", stat189.getPickedUp("minecraft:wooden_pickaxe").orElse("0"));
        assertEquals("1", stat1122.getPickedUp("minecraft:wooden_pickaxe").orElse("0"));
        assertEquals("5", stat121.getPickedUp("minecraft:beef").orElse("0"));
    }

    @Test
    public void doDroppedTests() {
        assertEquals("2", stat189.getDropped("minecraft:stick").orElse("0"));
        assertEquals("2", stat1122.getDropped("minecraft:stick").orElse("0"));
        assertEquals("5", stat121.getDropped("minecraft:beef").orElse("0"));
    }
}
