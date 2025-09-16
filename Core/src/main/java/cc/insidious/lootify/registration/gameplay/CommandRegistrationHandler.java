package cc.insidious.lootify.registration.gameplay;

import cc.insidious.fethmusmioma.CommandHandler;
import cc.insidious.lootify.LootifyPlugin;
import cc.insidious.lootify.api.registration.IRegistrationHandler;
import cc.insidious.lootify.commands.CommandLootTable;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public class CommandRegistrationHandler implements IRegistrationHandler {

    private final LootifyPlugin instance;

    @Override
    public void registerObjects() {
        CommandHandler commandHandler = new CommandHandler(this.instance, "lootify");

        Stream.of(new CommandLootTable(this.instance))
                .forEach(commandHandler::registerCommand);
    }
}
