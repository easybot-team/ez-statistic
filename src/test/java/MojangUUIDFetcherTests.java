import com.springwater.easybot.statistic.utils.MojangUUIDFetcher;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MojangUUIDFetcherTests {
    @Test
    public void testExists() {
        assertEquals(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), MojangUUIDFetcher.getOnlineUUID("Notch").orElse(UUID.randomUUID()));
        assertEquals(UUID.fromString("2d4ce3bc-1ef0-405d-8afd-7bd4df975b29"), MojangUUIDFetcher.getOnlineUUID("MiuxuE").orElse(UUID.randomUUID()));
    }
}
