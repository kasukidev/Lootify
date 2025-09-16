package cc.insidious.lootify.loottable.cache;

import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LootTableCacheHandler {
    private final Map<String, LootTableWrapper> cache = new ConcurrentHashMap<>();

    public void addToCache(LootTableWrapper lootTableWrapper) {
        this.cache.put(lootTableWrapper.getTableId(), lootTableWrapper);
    }

    public void removeFromCache(String id) {
        this.cache.remove(id);
    }

    public Optional<LootTableWrapper> getFromCache(String id) {
        return Optional.ofNullable(this.cache.get(id));
    }

    public Collection<LootTableWrapper> getAllFromCache() {
        return this.cache.values();
    }
}
