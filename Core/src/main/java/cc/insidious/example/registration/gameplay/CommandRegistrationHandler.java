package cc.insidious.example.registration.gameplay;

import cc.insidious.example.ExamplePlugin;
import cc.insidious.example.api.registration.IRegistrationHandler;
import cc.insidious.fethmusmioma.CommandHandler;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class CommandRegistrationHandler implements IRegistrationHandler {

  private final ExamplePlugin instance;

  @Override
  public void registerObjects() {
    CommandHandler commandHandler = new CommandHandler(this.instance, "example");

    Stream.of()
            .forEach(commandHandler::registerCommand);
  }
}
