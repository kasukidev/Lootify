package cc.insidious.example.api.database.repository.impl;

import cc.insidious.example.api.database.repository.ICacheRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MapCacheRepository<K, V> implements ICacheRepository<K, V> {

  private final Map<K, V> cache = new ConcurrentHashMap<>();

  @Override
  public void addToCache(K key, V value) {
    this.cache.put(key, value);
  }

  @Override
  public void removeFromCache(K key) {
    this.cache.remove(key);
  }

  @Override
  public Optional<V> getFromCache(K key) {
    return Optional.ofNullable(this.cache.get(key));
  }

  @Override
  public Collection<V> getAllValuesFromCache() {
    return this.cache.values();
  }

  @Override
  public boolean existsInCache(K key) {
    return this.cache.containsKey(key);
  }
}
