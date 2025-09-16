package cc.insidious.lootify.api.loottable.util;

import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class LootTableUtil {
    private ILootTableHandler handler;

    public LootTableUtil(ILootTableHandler lootTableHandler) {
        this.handler = lootTableHandler;
    }

    public ItemStack getRandomEntry(LootTableWrapper wrapper){
        return null;
    }

    public void setupLootTable(String id){
        Optional<LootTableWrapper> lootTableWrapper = handler.getFromCache(id.toUpperCase());

        if(lootTableWrapper.isPresent()){
            return;
        }

        LootTableWrapper wrapper = LootTableWrapper.from(id.toUpperCase()).setChanged(true);
        handler.addToCache(wrapper);
        return;
    }
}
