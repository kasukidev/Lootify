package cc.insidious.lootify.menu;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.helper.LootTableHelper;
import cc.insidious.lootify.api.loottable.wrapper.LootEntryWrapper;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import cc.insidious.lootify.config.LangConfig;
import cc.insidious.lootify.loottable.editor.ChanceEditor;
import cc.insidious.lootify.util.LootifyUtil;
import cc.insidious.lootify.utilities.item.ItemStackSerializer;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EditorMenu {
    private final LootifyPlugin instance;
    private LootTableWrapper lootTableWrapper;
    private final ILootTableHandler lootTableHandler;

    public EditorMenu(LootifyPlugin instance, LootTableWrapper lootTableWrapper) {
        this.instance = instance;
        this.lootTableWrapper = lootTableWrapper;
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    public void openMenu(Player player) {
        SGMenu menu = instance.getSpiGui().create("Editing: " + lootTableWrapper.getTableId().toUpperCase(), 5);
        menu.setAutomaticPaginationEnabled(true);

        lootTableWrapper.getLootEntries().forEach(entry -> menu.addButton(getEditorButton(entry)));

        player.playSound(player.getLocation(), XSound.BLOCK_ENDER_CHEST_OPEN.get(), 1, 1);
        player.openInventory(menu.getInventory());

        instance.getEditorListener().getEditingPlayers().put(player.getUniqueId(), lootTableWrapper);
    }

    private SGButton getEditorButton(LootEntryWrapper wrapper){
        LootTableHelper lootTableUtil = new LootTableHelper(lootTableHandler);
        ItemStack stack = ItemStackSerializer.deserialize(wrapper.getItemStack());
        ItemMeta meta = stack.getItemMeta();
        String name = LootifyUtil.getItemName(stack);
        List<String> lore = (meta != null && meta.hasLore())
                ? new ArrayList<>(meta.getLore())
                : new ArrayList<>();


        lore.addAll(Lists.newArrayList(
                "",
                "&8&m------------------------",
                "&b&lAbsolute Chance:&f " + lootTableUtil.getAbsoluteChance(lootTableWrapper, wrapper) + "%",
                "&b&lRelative Chance:&f " + wrapper.getChance() + "%",
                "",
                "&7Left-Click to modify this entry's chance",
                "&7Right-Click to delete this loot table entry",
                "&8&m------------------------"
        ));

        return new SGButton(new ItemBuilder(stack.getType())
                .name(name)
                .lore(lore)
                .amount(stack.getAmount())
                .build()).withListener(event -> {
                    Player player = (Player) event.getWhoClicked();
                    handleClick(event.getClick(), player, wrapper);
        });
    }

    private void handleClick(ClickType clickType, Player player, LootEntryWrapper wrapper){
        if(clickType.equals(ClickType.RIGHT)){
            lootTableWrapper = lootTableWrapper.removeLootEntry(wrapper).setChanged(true);
            lootTableHandler.addToCache(lootTableWrapper);
            openMenu(player);
            return;
        }

        Optional<ChanceEditor> chanceEditorOpt = instance.getEditorFromUUID(player.getUniqueId());

        if(chanceEditorOpt.isPresent()){
            instance.getActiveEditors().remove(chanceEditorOpt.get());
            initChanceEditor(player, wrapper);
            return;
        }

        initChanceEditor(player, wrapper);
    }

    private void initChanceEditor(Player player, LootEntryWrapper lootEntryWrapper){
        ChanceEditor chanceEditor = new ChanceEditor(player.getUniqueId(), lootTableWrapper, lootEntryWrapper);
        LootifyUtil.sendSuccess(player, LangConfig.TYPE_PERCENT_IN_CHAT);
        instance.getActiveEditors().add(chanceEditor);
        player.closeInventory();
    }
}
