package cc.insidious.example.api.database.repository.impl;

import cc.insidious.example.api.database.repository.ICacheRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CaffieneCacheRepository<K, V> implements ICacheRepository<K, V> {

  private final Cache<K, V> cache;

  public CaffieneCacheRepository(Cache<K, V> cache) {
    this.cache = cache;
  }

  public CaffieneCacheRepository() {
    this.cache = Caffeine.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build();
  }

  @Override
  public void addToCache(K key, V value) {
    this.cache.put(key, value);
  }

  @Override
  public void removeFromCache(K key) {
    this.cache.invalidate(key);
  }

  @Override
  public Optional<V> getFromCache(K key) {
    return Optional.ofNullable(this.cache.asMap().get(key));
  }

  @Override
  public boolean existsInCache(K key) {
    return this.cache.asMap().containsKey(key);
  }

  @Override
  public Collection<V> getAllValuesFromCache() {
    return this.cache.asMap().values();
  }
}
