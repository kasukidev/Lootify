package cc.insidious.lootify.commands;

import cc.insidious.fethmusmioma.annotation.Command;
import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.menu.LootTableMenu;
import org.bukkit.entity.Player;

public class CommandLootTable {
    private final LootifyPlugin instance;

    public CommandLootTable(LootifyPlugin instance) {
        this.instance = instance;
    }

    @Command(label = "loottable", permission = "lootify.admin")
    public void executeLootTable(Player player){
        LootTableMenu lootTableMenu = new LootTableMenu(this.instance);
        lootTableMenu.openMenu(player);
    }
}
