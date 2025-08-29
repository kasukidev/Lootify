package cc.insidious.example.registration.data;

import cc.insidious.example.ExamplePlugin;
import cc.insidious.example.api.registration.IRegistrationHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConfigRegistrationHandler implements IRegistrationHandler {

  private final ExamplePlugin instance;

  @Override
  public void registerObjects() {}
}
