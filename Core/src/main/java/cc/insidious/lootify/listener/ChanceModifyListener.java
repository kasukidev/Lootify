package cc.insidious.lootify.listener;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootEntryWrapper;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import cc.insidious.lootify.config.LangConfig;
import cc.insidious.lootify.loottable.editor.ChanceEditor;
import cc.insidious.lootify.menu.EditorMenu;
import cc.insidious.lootify.util.LootifyUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChanceModifyListener implements Listener {

    private final LootifyPlugin instance;
    private final ILootTableHandler lootTableHandler;

    public ChanceModifyListener(LootifyPlugin instance) {
        this.instance = instance;
        this.lootTableHandler = this.instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChanceEditor editor = this.instance.getEditorFromUUID(player.getUniqueId()).orElse(null);
        if (editor == null) return;

        Double value = parseDouble(event.getMessage().trim());
        if (value == null) {
            LootifyUtil.sendError(player, LangConfig.INVALID_CHANCE);
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        this.updateLootEntry(player, editor, value);
        this.instance.getActiveEditors().remove(editor);
        LootifyUtil.sendSuccess(player, LangConfig.CHANCE_UPDATED.replace("%amount%", String.valueOf(value)));
    }

    private void updateLootEntry(Player player, ChanceEditor editor, double value) {
        LootTableWrapper table = editor.getLootTableWrapper();
        LootEntryWrapper entry = editor.getLootEntryWrapper().setChance(value);
        table = table.removeLootEntry(editor.getLootEntryWrapper()).addLootEntry(entry).setChanged(true);
        lootTableHandler.addToCache(table);

        EditorMenu editorMenu = new EditorMenu(this.instance, table);

        Bukkit.getScheduler().runTask(this.instance, () ->
                editorMenu.openMenu(player)
        );
    }

    private Double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
