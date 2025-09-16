package cc.insidious.lootify.api.database.sqlite.impl;

import cc.insidious.lootify.api.database.repository.ICacheRepository;
import cc.insidious.lootify.api.database.repository.impl.CaffieneCacheRepository;
import cc.insidious.lootify.api.database.sqlite.AbstractSQLiteRepository;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Optional;

@Getter
public abstract class AbstractSQLiteCacheRepository<K, V> extends AbstractSQLiteRepository<K>
    implements ICacheRepository<K, V> {

  private final CaffieneCacheRepository<K, V> cacheRepository;

  protected AbstractSQLiteCacheRepository(
      JavaPlugin plugin, String name, String tableName, Cache<K, V> cache) {
    super(plugin, name, tableName);
    this.cacheRepository = new CaffieneCacheRepository<>(cache);
  }

  protected AbstractSQLiteCacheRepository(JavaPlugin plugin, String name, String tableName) {
    super(plugin, name, tableName);
    this.cacheRepository = new CaffieneCacheRepository<>();
  }

  @Override
  public void addToCache(K key, V value) {
    this.cacheRepository.addToCache(key, value);
  }

  @Override
  public void removeFromCache(K key) {
    this.cacheRepository.removeFromCache(key);
  }

  @Override
  public Optional<V> getFromCache(K key) {
    return this.cacheRepository.getFromCache(key);
  }

  @Override
  public boolean existsInCache(K key) {
    return this.cacheRepository.existsInCache(key);
  }

  @Override
  public Collection<V> getAllValuesFromCache() {
    return this.cacheRepository.getAllValuesFromCache();
  }
}
