package cc.insidious.lootify.config;

import cc.insidious.config.Config;
import cc.insidious.config.annotation.ConfigAnnotation;
import org.bukkit.plugin.java.JavaPlugin;

public class LangConfig extends Config {
    @ConfigAnnotation(path = "server_prefix")
    public static String SERVER_PREFIX = "&8[&b&lRavage&d&lPvP&8]";

    @ConfigAnnotation(path = "no_loottable_found")
    public static String NO_LOOTTABLE_FOUND = "&cNo loottable has been found named %id%!";

    @ConfigAnnotation(path = "loottable_deleted")
    public static String LOOTTABLE_DELETED = "&aSuccessfully deleted the loottable named %id%!";

    @ConfigAnnotation(path = "type_percent_in_chat")
    public static String TYPE_PERCENT_IN_CHAT = "&aType the new percent for this loot table entry in chat!";

    @ConfigAnnotation(path = "invalid_chance")
    public static String INVALID_CHANCE = "&cInvalid chance, please input an integer or double.";

    @ConfigAnnotation(path = "chance_updated")
    public static String CHANCE_UPDATED = "&aUpdated chance to %amount%%!";

    public LangConfig(JavaPlugin plugin) {
        super(plugin, "lang");
    }
}
