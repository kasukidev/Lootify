package cc.insidious.lootify.api.loottable.repository;

import cc.insidious.lootify.api.constant.LootifyConstant;
import cc.insidious.lootify.api.database.sqlite.AbstractSQLiteRepository;
import org.bukkit.plugin.java.JavaPlugin;

public class SQLiteLootTableRepository extends AbstractSQLiteRepository<String> {

    public SQLiteLootTableRepository(JavaPlugin instance) {
        super(instance, LootifyConstant.SQLITE_DATABASE_NAME, LootifyConstant.LOOTTABLE_TABLE_NAME);
    }

    @Override
    public void close() throws Exception {

    }
}