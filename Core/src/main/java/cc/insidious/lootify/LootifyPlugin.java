package cc.insidious.lootify;

import cc.insidious.lootify.api.LootifyAPI;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.listener.GlobalEditorMenuListener;
import cc.insidious.lootify.loottable.editor.ChanceEditor;
import cc.insidious.lootify.registration.data.ConfigRegistrationHandler;
import cc.insidious.lootify.registration.data.LootTableRegistrationHandler;
import cc.insidious.lootify.registration.data.ModuleRegistrationHandler;
import cc.insidious.lootify.registration.gameplay.CommandRegistrationHandler;
import cc.insidious.lootify.registration.gameplay.ListenerRegistrationHandler;
import com.samjakob.spigui.SpiGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
public class LootifyPlugin extends JavaPlugin {

    private LootifyAPI lootifyAPI;
    private SpiGUI spiGui;
    private final List<ChanceEditor> activeEditors = new ArrayList<>();

    @Setter
    private GlobalEditorMenuListener editorListener;

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

    public Optional<ChanceEditor> getEditorFromUUID(UUID uuid) {
        return getActiveEditors().stream()
                .filter(editor -> editor.getUniqueID().equals(uuid))
                .findFirst();
    }
}
