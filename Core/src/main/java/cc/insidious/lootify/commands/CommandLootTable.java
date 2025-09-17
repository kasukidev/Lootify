package cc.insidious.lootify.commands;

import cc.insidious.fethmusmioma.annotation.Command;
import cc.insidious.fethmusmioma.annotation.Parameter;
import cc.insidious.fethmusmioma.annotation.SubCommand;
import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import cc.insidious.lootify.config.LangConfig;
import cc.insidious.lootify.menu.LootTableMenu;
import cc.insidious.lootify.util.LootifyUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandLootTable {
    private final LootifyPlugin instance;
    private final ILootTableHandler lootTableHandler;

    public CommandLootTable(LootifyPlugin instance) {
        this.instance = instance;
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    @Command(label = "lootify", aliases = {"loottable"}, permission = "lootify.admin")
    public void executeLootTable(Player player){
        LootTableMenu lootTableMenu = new LootTableMenu(this.instance);
        lootTableMenu.openMenu(player);
    }

    @SubCommand(label = "delete", parent = "lootify")
    public void executeDelete(Player player, @Parameter(name = "id") String id){
        Optional<LootTableWrapper> wrapperOpt = lootTableHandler.getFromCache(id.toUpperCase());

        if(!wrapperOpt.isPresent()){
            LootifyUtil.sendError(player, LangConfig.NO_LOOTTABLE_FOUND.replace("%id%", id.toUpperCase()));
            return;
        }

        LootifyUtil.sendSuccess(player, LangConfig.LOOTTABLE_DELETED.replace("%id%", id.toUpperCase()));
        lootTableHandler.removeFromCache(id.toUpperCase());
        lootTableHandler.removeFromDatabase(id.toUpperCase());
    }
}
