package cc.insidious.example.registration.gameplay;

import cc.insidious.example.ExamplePlugin;
import cc.insidious.example.api.registration.IRegistrationHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;

@RequiredArgsConstructor
public class ListenerRegistrationHandler implements IRegistrationHandler {

  private final ExamplePlugin instance;

  @Override
  public void registerObjects() {
    PluginManager manager = this.instance.getServer().getPluginManager();
  }
}
