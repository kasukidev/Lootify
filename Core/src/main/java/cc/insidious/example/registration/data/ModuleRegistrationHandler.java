package cc.insidious.example.registration.data;

import cc.insidious.example.ExamplePlugin;
import cc.insidious.example.api.ExampleAPI;
import cc.insidious.example.api.registration.IRegistrationHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModuleRegistrationHandler implements IRegistrationHandler {

  private final ExamplePlugin instance;
  private final ExampleAPI exampleAPI;

  @Override
  public void registerObjects() {}
}
