package cc.insidious.lootify.api.loottable.wrapper;

import cc.insidious.lootify.api.loottable.LoottableOuterClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper class for a ProfileOuterClass.Profile object.
 *
 * <p>Provides helper methods for managing homes, invulnerability status,
 * and tracking changes to the profile.
 */
@RequiredArgsConstructor
@Getter
public class LootTableWrapper {

    private final LoottableOuterClass.Loottable loottable;
    private boolean changed;

    public String getTableId(){
        return loottable.getTableId();
    }

    public List<LootEntryWrapper> getLootEntries() {
        return loottable.getLootEntryList()
                .stream()
                .map(LootEntryWrapper::new)
                .collect(Collectors.toList());
    }

    public boolean hasChanged() {
        return this.changed;
    }

    public LootTableWrapper addLootEntry(LootEntryWrapper wrapper) {
        LoottableOuterClass.Loottable.Builder builder = this.loottable.toBuilder();
        builder.addLootEntry(wrapper.getLootentry());
        return new LootTableWrapper(builder.build());
    }

    public LootTableWrapper removeLootEntry(LootEntryWrapper wrapper) {
        LoottableOuterClass.Loottable.Builder builder = this.loottable.toBuilder();

        builder.clearLootEntry();

        loottable.getLootEntryList().stream()
                .filter(lootentry -> !lootentry.equals(wrapper.getLootentry()))
                .forEach(builder::addLootEntry);

        return new LootTableWrapper(builder.build());
    }

    public LootTableWrapper setChanged(boolean changed) {
        this.changed = changed;
        return this;
    }

    public static LootTableWrapper from(String id) {
        return new LootTableWrapper(LoottableOuterClass.Loottable.newBuilder()
                .setTableId(id)
                .build());
    }
}
