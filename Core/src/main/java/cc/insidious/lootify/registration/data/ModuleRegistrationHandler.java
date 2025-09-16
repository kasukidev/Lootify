package cc.insidious.lootify.registration.data;

import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.LootifyAPI;
import cc.insidious.lootify.api.loottable.ILootTableHandler;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.loottable.SQLiteLootTableHandler;
import cc.insidious.lootify.utilities.pair.Pair;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class ModuleRegistrationHandler implements IRegistrationHandler {

    private final LootifyPlugin instance;
    private final LootifyAPI lootifyAPI;

    @Override
    public void registerObjects() {
        Stream.of(Pair.from(ILootTableHandler.class, new SQLiteLootTableHandler(this.instance)))
                .forEachOrdered(pair -> this.lootifyAPI.register(pair.getKey(), pair.getValue()));
    }
}
