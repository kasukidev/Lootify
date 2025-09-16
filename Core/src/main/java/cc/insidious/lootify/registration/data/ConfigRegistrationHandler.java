package cc.insidious.lootify.registration.data;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.config.MainConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConfigRegistrationHandler implements IRegistrationHandler {

    private final LootifyPlugin instance;

    private MainConfig mainConfig;

    @Override
    public void registerObjects() {
        this.mainConfig = new MainConfig(this.instance);
    }
}
