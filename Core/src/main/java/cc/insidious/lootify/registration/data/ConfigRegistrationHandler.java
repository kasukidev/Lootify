package cc.insidious.lootify.registration.data;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.config.LangConfig;
import cc.insidious.lootify.config.MainConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ConfigRegistrationHandler implements IRegistrationHandler {

    private final LootifyPlugin instance;

    private MainConfig mainConfig;

    private LangConfig langConfig;

    @Override
    public void registerObjects() {
        this.mainConfig = new MainConfig(this.instance);
        this.langConfig = new LangConfig(this.instance);
    }
}
