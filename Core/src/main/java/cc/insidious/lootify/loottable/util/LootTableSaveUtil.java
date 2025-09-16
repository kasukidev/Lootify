package cc.insidious.lootify.loottable.util;


import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class LootTableSaveUtil {
    public final int BATCH_SIZE = 100;
    public void processLootTableSaving(ILootTableHandler lootTableHandler) {
        List<LootTableWrapper> lootTableWrappers = new ArrayList<>(lootTableHandler.getAllFromCache());
        lootTableWrappers.removeIf(lootTableWrapper -> !lootTableWrapper.hasChanged());

        if (lootTableWrappers.isEmpty()) {
            return;
        }

        int size =  lootTableWrappers.size();

        if (size <= BATCH_SIZE) {
            processBatch(lootTableHandler, lootTableWrappers);
            return;
        }

        for (int index = 0; index < size; index += BATCH_SIZE) {
            int end = Math.min(index + BATCH_SIZE, size);
            List<LootTableWrapper> batchList = lootTableWrappers.subList(index, end);
            processBatch(lootTableHandler, batchList);
        }
    }

    private void processBatch(ILootTableHandler lootTableHandler, List<LootTableWrapper> lootTableWrappers) {
        lootTableHandler.saveAllToDatabase(lootTableWrappers);
        lootTableWrappers.forEach(lootTableWrapper -> {
            lootTableWrapper = lootTableWrapper.setChanged(false);
            lootTableHandler.addToCache(lootTableWrapper);
        });
    }


}