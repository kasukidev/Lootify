package cc.insidious.lootify.loottable.editor;

import cc.insidious.lootify.api.loottable.wrapper.LootEntryWrapper;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ChanceEditor {
    private final UUID uniqueID;
    private LootTableWrapper lootTableWrapper;
    private LootEntryWrapper lootEntryWrapper;

    public ChanceEditor(UUID uniqueID, LootTableWrapper lootTableWrapper, LootEntryWrapper lootEntryWrapper) {
        this.uniqueID = uniqueID;
        this.lootTableWrapper = lootTableWrapper;
        this.lootEntryWrapper = lootEntryWrapper;
    }
}
