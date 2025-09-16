package cc.insidious.lootify.api.database.repository;

import java.util.Collection;
import java.util.Optional;

public interface ICacheRepository<K, V> {

  void addToCache(K key, V value);

  void removeFromCache(K key);

  Optional<V> getFromCache(K key);

  Collection<V> getAllValuesFromCache();

  boolean existsInCache(K key);
}
