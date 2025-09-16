package cc.insidious.lootify.menu;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.wrapper.LootTableWrapper;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.entity.Player;

public class EditorMenu {
    private final LootifyPlugin instance;
    private final LootTableWrapper wrapper;

    public EditorMenu(LootifyPlugin instance, LootTableWrapper wrapper) {
        this.instance = instance;
        this.wrapper = wrapper;
    }

    public void openMenu(Player player){
        SGMenu menu = instance.getSpiGui().create("Editing: " + wrapper.getTableId().toUpperCase(), 5);
        menu.setAutomaticPaginationEnabled(true);

        player.playSound(player.getLocation(), XSound.BLOCK_ENDER_CHEST_OPEN.get(), 1, 1);
        player.openInventory(menu.getInventory());
    }
}
