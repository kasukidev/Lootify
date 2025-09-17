package cc.insidious.lootify.listener;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootEntryWrapper;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import cc.insidious.lootify.menu.EditorMenu;
import cc.insidious.lootify.utilities.item.ItemStackSerializer;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GlobalEditorMenuListener implements Listener {

    private final LootifyPlugin instance;
    private final ILootTableHandler lootTableHandler;

    private final Map<UUID, LootTableWrapper> editingPlayers = new HashMap<>();

    public GlobalEditorMenuListener(LootifyPlugin instance) {
        this.instance = instance;
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        LootTableWrapper wrapper = editingPlayers.get(player.getUniqueId());
        if (wrapper == null) return;

        String menuTitle = "Editing: " + wrapper.getTableId().toUpperCase();
        if (!menuTitle.equalsIgnoreCase(event.getView().getTitle())) return;
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(player.getInventory())) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == null || clicked.getType() == XMaterial.AIR.get()) return;

        event.setCancelled(true);
        wrapper = wrapper.addLootEntry(LootEntryWrapper.from(UUID.randomUUID(), ItemStackSerializer.serialize(clicked), 1.0)).setChanged(true);
        lootTableHandler.addToCache(wrapper);
        new EditorMenu(this.instance, wrapper).openMenu(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();

        LootTableWrapper wrapper = editingPlayers.get(player.getUniqueId());
        if (wrapper == null) return;

        String menuTitle = "Editing: " + wrapper.getTableId().toUpperCase();
        if (!menuTitle.equalsIgnoreCase(event.getView().getTitle())) return;

        editingPlayers.remove(player.getUniqueId());
    }
}
