package cc.insidious.lootify.menu;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Lists;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.entity.Player;

public class LootTableMenu {
    private final LootifyPlugin instance;
    private final ILootTableHandler lootTableHandler;

    public LootTableMenu(LootifyPlugin instance) {
        this.instance = instance;
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    public void openMenu(Player player){
        SGMenu menu = instance.getSpiGui().create("Loot Tables", 5);
        menu.setAutomaticPaginationEnabled(true);

        lootTableHandler.getAllFromCache().forEach(wrapper -> menu.addButton(getLootTableButton(wrapper)));

        player.playSound(player.getLocation(), XSound.BLOCK_ENDER_CHEST_OPEN.get(), 1, 1);
        player.openInventory(menu.getInventory());
    }

    private SGButton getLootTableButton(LootTableWrapper wrapper){
        return new SGButton(new ItemBuilder(XMaterial.ENDER_CHEST.parseItem())
                .name("&b&l" + wrapper.getTableId().toUpperCase())
                .lore(Lists.newArrayList(
                        "&7&l• &d&lItems:&f " + wrapper.getLootEntries().size(),
                        "",
                        "&7Click to edit this loot table!"
                ))
                .build()).withListener(event -> {
                    Player player = (Player) event.getWhoClicked();
                    EditorMenu editorMenu = new EditorMenu(this.instance, wrapper);
                    editorMenu.openMenu(player);
        });

    }
}
