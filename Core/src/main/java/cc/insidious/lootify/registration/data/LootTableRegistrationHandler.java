package cc.insidious.lootify.registration.data;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.helper.LootTableHelper;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.config.MainConfig;

import java.util.stream.Stream;

public class LootTableRegistrationHandler implements IRegistrationHandler {
    private final LootifyPlugin instance;
    private final ILootTableHandler lootTableHandler;

    public LootTableRegistrationHandler(LootifyPlugin instance) {
        this.instance = instance;
        this.lootTableHandler = instance.getLootifyAPI().get(ILootTableHandler.class);
    }

    @Override
    public void registerObjects() {
        LootTableHelper lootifyHelper = new LootTableHelper(lootTableHandler);

        if(MainConfig.ENABLE_EXAMPLE)
            Stream.of("lootify_testing").forEachOrdered(lootifyHelper::setupLootTable);
    }
}
