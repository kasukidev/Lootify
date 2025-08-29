package cc.insidious.example.api.database.mysql.impl;

import cc.insidious.example.api.database.mysql.AbstractMySQLRepository;
import cc.insidious.example.api.database.repository.ICacheRepository;
import cc.insidious.example.api.database.repository.impl.CaffieneCacheRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;

@Getter
public abstract class AbstractMySQLCacheRepository<K, V> extends AbstractMySQLRepository<K>
    implements ICacheRepository<K, V> {

  private final CaffieneCacheRepository<K, V> cacheRepository;

  protected AbstractMySQLCacheRepository(
      HikariDataSource hikariDataSource, String tableName, Cache<K, V> cache) {
    super(hikariDataSource, tableName);
    this.cacheRepository = new CaffieneCacheRepository<>(cache);
  }

  protected AbstractMySQLCacheRepository(HikariDataSource hikariDataSource, String tableName) {
    super(hikariDataSource, tableName);
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
