package cc.insidious.lootify.config;

import cc.insidious.config.Config;
import cc.insidious.config.annotation.ConfigAnnotation;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends Config {
    @ConfigAnnotation(path = "enable_example")
    public static boolean ENABLE_EXAMPLE = false;

    public MainConfig(JavaPlugin plugin) {
        super(plugin, "config");
    }
}
