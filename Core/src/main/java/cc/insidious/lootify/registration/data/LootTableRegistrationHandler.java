package cc.insidious.lootify.registration.data;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.loottable.helper.LootTableHelper;
import cc.insidious.lootify.api.registration.IRegistrationHandler;

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
        LootTableHelper lootTableUtil = new LootTableHelper(lootTableHandler);
        Stream.of("lootify")
                .forEachOrdered(lootTableUtil::setupLootTable);
    }
}
