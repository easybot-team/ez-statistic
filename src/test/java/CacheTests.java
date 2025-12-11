import com.springwater.easybot.statistic.StatisticManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.UUID;

public class CacheTests {
    private static final StatisticManager manager = StatisticManager.getInstance();
    @BeforeAll
    public static void initDb() {
        manager.initDb("./caches");
    }
    
    @Test
    public void test() {
        UUID uuid = UUID.randomUUID();
        manager.getStatDb().putUuidCache("test", uuid);
        assert manager.getStatDb().getUuidCache("test").isPresent();
        assert manager.getStatDb().getUuidCache("test2").isEmpty();
    }
}

