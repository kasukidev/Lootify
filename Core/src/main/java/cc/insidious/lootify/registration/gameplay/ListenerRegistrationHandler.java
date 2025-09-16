package cc.insidious.lootify.registration.gameplay;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class ListenerRegistrationHandler implements IRegistrationHandler {

    private final LootifyPlugin instance;

    @Override
    public void registerObjects() {
        PluginManager manager = this.instance.getServer().getPluginManager();
        Stream.of()
                .filter(Listener.class::isInstance)
                .map(Listener.class::cast)
                .forEach(listener -> manager.registerEvents(listener, this.instance));
    }
}
