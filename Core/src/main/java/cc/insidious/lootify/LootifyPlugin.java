package cc.insidious.lootify;

import cc.insidious.lootify.api.LootifyAPI;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.registration.data.ConfigRegistrationHandler;
import cc.insidious.lootify.registration.data.LootTableRegistrationHandler;
import cc.insidious.lootify.registration.data.ModuleRegistrationHandler;
import cc.insidious.lootify.registration.gameplay.CommandRegistrationHandler;
import cc.insidious.lootify.registration.gameplay.ListenerRegistrationHandler;
import com.samjakob.spigui.SpiGUI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

@Getter
public class LootifyPlugin extends JavaPlugin {

    private LootifyAPI lootifyAPI;
    private SpiGUI spiGui;

    @Override
    public void onEnable() {
        new ConfigRegistrationHandler(this).registerObjects();
        this.lootifyAPI = new LootifyAPI();
        this.spiGui = new SpiGUI(this);

        Stream.of(new ModuleRegistrationHandler(this, this.lootifyAPI),
                        new CommandRegistrationHandler(this),
                        new ListenerRegistrationHandler(this))
                .forEachOrdered(IRegistrationHandler::registerObjects);

        new LootTableRegistrationHandler(this).registerObjects();
    }

    public void onDisable() {
        this.lootifyAPI.shutdown();
    }
}
