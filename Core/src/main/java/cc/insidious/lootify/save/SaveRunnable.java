package cc.insidious.lootify.save;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.loottable.util.LootTableSaveUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveRunnable extends BukkitRunnable {
    private final ILootTableHandler lootTableHandler;
    public SaveRunnable(LootifyPlugin instance){
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
        long delay = 20 * 60L;
        this.runTaskTimer(instance, delay, delay);
    }

    @Override
    public void run() {
        saveLootTables();
    }

    private void saveLootTables(){
        if(this.lootTableHandler == null){
            return;
        }

        LootTableSaveUtil.processLootTableSaving(lootTableHandler);
    }

}
