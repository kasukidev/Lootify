package cc.insidious.lootify.util;

import cc.insidious.lootify.config.LangConfig;
import cc.insidious.lootify.utilities.CC;
import com.cryptomorin.xseries.XSound;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class LootifyUtil {
    public void sendSuccess(Player player, String message){
        sendPrefix(player, message);
        player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.get(), 1, 1);
    }

    public void sendError(Player player, String message){
        sendPrefix(player, message);
        player.playSound(player.getLocation(), XSound.BLOCK_LAVA_POP.get(), 1, 1);
    }

    public void sendPrefix(Player player, String message){
        player.sendMessage(CC.chat(LangConfig.SERVER_PREFIX + " " + message));
    }

    public String getItemName(ItemStack i) {
        return i.hasItemMeta() && i.getItemMeta().hasDisplayName()
                ? i.getItemMeta().getDisplayName()
                : "&f" + Arrays.stream(i.getType().name().split("_")).map(s -> s.charAt(0)+s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }
}
