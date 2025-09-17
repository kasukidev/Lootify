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

    public LangConfig(JavaPlugin plugin) {
        super(plugin, "lang");
    }
}
