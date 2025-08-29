package cc.insidious.example.api.database.mongodb.repository.impl;

import cc.insidious.example.api.database.mongodb.repository.AbstractMongoRepository;
import cc.insidious.example.api.database.repository.ICacheRepository;
import cc.insidious.example.api.database.repository.impl.MapCacheRepository;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;
import java.util.*;

@Getter
public abstract class AbstractMongoMapRepository<K, V> extends AbstractMongoRepository<K>
    implements ICacheRepository<K, V> {

  private final MongoCollection<Document> collection;
  private final MapCacheRepository<K, V> cacheRepository;

  protected AbstractMongoMapRepository(MongoCollection<Document> collection) {
    super(collection);
    this.collection = collection;
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
