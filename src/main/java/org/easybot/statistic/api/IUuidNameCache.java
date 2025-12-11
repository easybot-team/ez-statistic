package org.easybot.statistic.api;

import java.util.Optional;
import java.util.UUID;

public interface IUuidNameCache {
    void putUuidCache(String name, UUID uuid);
    Optional<UUID> getUuidCache(String name);
}
