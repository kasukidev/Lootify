package cc.insidious.lootify.loottable;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.LoottableOuterClass;
import cc.insidious.lootify.api.loottable.repository.SQLiteLootTableRepository;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import cc.insidious.lootify.loottable.cache.LootTableCacheHandler;
import cc.insidious.lootify.loottable.util.LootTableSaveUtil;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SQLiteLootTableHandler implements ILootTableHandler {
    private final LootTableCacheHandler lootTableCacheHandler;
    private final SQLiteLootTableRepository sqLiteLootTableRepository;

    public SQLiteLootTableHandler(LootifyPlugin instance) {
        this.lootTableCacheHandler = new LootTableCacheHandler();
        this.sqLiteLootTableRepository = new SQLiteLootTableRepository(instance);
    }

    @Override
    public void addToCache(LootTableWrapper warpWrapper) {
        this.lootTableCacheHandler.addToCache(warpWrapper);
    }

    @Override
    public void removeFromCache(String id) {
        this.lootTableCacheHandler.removeFromCache(id);
    }

    @Override
    public Optional<LootTableWrapper> getFromCache(String id) {
        return this.lootTableCacheHandler.getFromCache(id);
    }

    @Override
    public Collection<LootTableWrapper> getAllFromCache() {
        return this.lootTableCacheHandler.getAllFromCache();
    }

    @Override
    public void saveToDatabase(LootTableWrapper warpWrapper) {
        this.sqLiteLootTableRepository.saveToDatabase(warpWrapper.getTableId(), warpWrapper.getLoottable().toByteArray());
    }

    @Override
    public void removeFromDatabase(String id) {
        this.sqLiteLootTableRepository.removeFromDatabase(id);
    }

    @Override
    public void saveAllToDatabase(Collection<LootTableWrapper> warpWrappers) {
        Map<String, byte[]> map = new HashMap<>();
        for (LootTableWrapper warpWrapper : warpWrappers) {
            map.put(warpWrapper.getTableId(), warpWrapper.getLoottable().toByteArray());
        }

        this.sqLiteLootTableRepository.saveBatch(map);
    }

    @Override
    public void removeAllFromDatabase(Collection<String> ids) {
        this.sqLiteLootTableRepository.deleteBatch(ids);
    }

    private Optional<LootTableWrapper> processBytes(byte[] input) {
        try {
            LoottableOuterClass.Loottable loottable = LoottableOuterClass.Loottable.parseFrom(input);
            return Optional.of(new LootTableWrapper(loottable));
        } catch (InvalidProtocolBufferException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void getFromDatabase(String id, Consumer<Optional<LootTableWrapper>> consumer) {
        this.sqLiteLootTableRepository.getFromDatabase(
                id,
                optional -> {
                    if (!optional.isPresent()) {
                        consumer.accept(Optional.empty());
                        return;
                    }

                    consumer.accept(this.processBytes(optional.get()));
                });
    }

    @Override
    public void getAllFromDatabase(Consumer<Collection<LootTableWrapper>> consumer) {
        this.sqLiteLootTableRepository.getAllEntriesFromDatabase(
                list -> consumer.accept(
                        list.stream()
                                .map(this::processBytes)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList())));
    }

    @Override
    public void load() {
        this.getAllFromDatabase(list -> list.forEach(this::addToCache));
    }

    @Override
    public void unload() {
        LootTableSaveUtil.processLootTableSaving(this);
    }
}
