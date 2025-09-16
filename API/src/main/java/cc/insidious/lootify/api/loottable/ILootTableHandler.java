package cc.insidious.lootify.api.loottable;

import cc.insidious.lootify.api.data.ILoadable;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public interface ILootTableHandler extends ILoadable {

    void addToCache(LootTableWrapper lootTableWrapper);
    void removeFromCache(String id);
    Optional<LootTableWrapper> getFromCache(String id);
    Collection<LootTableWrapper> getAllFromCache();

    void saveToDatabase(LootTableWrapper lootTableWrapper);
    void removeFromDatabase(String id);
    void saveAllToDatabase(Collection<LootTableWrapper> lootTableWrappers);
    void removeAllFromDatabase(Collection<String> ids);
    void getFromDatabase(String id, Consumer<Optional<LootTableWrapper>> consumer);
    void getAllFromDatabase(Consumer<Collection<LootTableWrapper>> consumer);
}