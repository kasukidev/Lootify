package cc.insidious.example.api.database.mysql.impl;

import cc.insidious.example.api.database.mysql.AbstractMySQLRepository;
import cc.insidious.example.api.database.repository.ICacheRepository;
import cc.insidious.example.api.database.repository.impl.MapCacheRepository;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMySQLMapRepository<K, V> extends AbstractMySQLRepository<K>
    implements ICacheRepository<K, V> {

  private final MapCacheRepository<K, V> cacheRepository;

  protected AbstractMySQLMapRepository(HikariDataSource dataSource, String tableName) {
    super(dataSource, tableName);
    this.cacheRepository = new MapCacheRepository<>();
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
