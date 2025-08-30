package cc.insidious.example;

import cc.insidious.example.api.ExampleAPI;
import cc.insidious.example.api.registration.IRegistrationHandler;
import cc.insidious.example.registration.data.ConfigRegistrationHandler;
import cc.insidious.example.registration.data.ModuleRegistrationHandler;
import cc.insidious.example.registration.gameplay.CommandRegistrationHandler;
import cc.insidious.example.registration.gameplay.ListenerRegistrationHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

@Getter
public class ExamplePlugin extends JavaPlugin {

  private ExampleAPI exampleAPI;

  @Override
  public void onEnable() {
    new ConfigRegistrationHandler(this).registerObjects();
    this.exampleAPI = new ExampleAPI();

    Stream.of(
            new ModuleRegistrationHandler(this, this.exampleAPI),
            new CommandRegistrationHandler(this),
            new ListenerRegistrationHandler(this))
        .forEachOrdered(IRegistrationHandler::registerObjects);
  }

  public void onDisable() {
    this.exampleAPI.shutdown();
  }
}
