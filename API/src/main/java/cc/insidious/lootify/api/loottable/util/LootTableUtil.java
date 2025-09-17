package cc.insidious.lootify.api.loottable.util;

import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootEntryWrapper;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import cc.insidious.lootify.utilities.item.ItemBuilder;
import cc.insidious.lootify.utilities.item.ItemStackSerializer;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LootTableUtil {
    private ILootTableHandler handler;

    public LootTableUtil(ILootTableHandler lootTableHandler) {
        this.handler = lootTableHandler;
    }

    public ItemStack getRandomEntry(LootTableWrapper wrapper) {
        List<LootEntryWrapper> entries = wrapper.getLootEntries();
        double totalChance = getTotalChance(wrapper);

        if (entries.isEmpty()) return this.fillerItemStack;
        if (totalChance <= 0) return this.fillerItemStack;

        double random = Math.random() * totalChance;

        for (LootEntryWrapper entry : entries) {
            random -= entry.getChance();
            if (random <= 0) {
                return ItemStackSerializer.deserialize(entry.getItemStack());
            }
        }

        return this.fillerItemStack;
    }

    public List<ItemStack> getRandomEntries(LootTableWrapper wrapper, int amount){
        List<ItemStack> stackList = new ArrayList<>();

        if(amount <= 0){
            return stackList;
        }

        for(int i = 0; i < amount; i++){
            stackList.add(getRandomEntry(wrapper));
        }

        return stackList;
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

    public double getAbsoluteChance(LootTableWrapper wrapper, LootEntryWrapper entry) {
        double totalChance = getTotalChance(wrapper);

        if (totalChance <= 0) {
            return 0.0;
        }

        double result = (entry.getChance() / totalChance) * 100.0;
        return Math.round(result * 100.0) / 100.0;
    }


    private double getTotalChance(LootTableWrapper lootTableWrapper){
        double chance = 0.0;

        for(LootEntryWrapper lootEntryWrapper : lootTableWrapper.getLootEntries()){
            chance += lootEntryWrapper.getChance();
        }

        return chance;
    }

    private final ItemStack fillerItemStack = new ItemBuilder(XMaterial.PAPER)
            .setName("&c&lEmpty Loot Table")
            .setLore(Lists.newArrayList(
                    "&7There is no loot specified in this loot table!"
            ))
            .build();
}
