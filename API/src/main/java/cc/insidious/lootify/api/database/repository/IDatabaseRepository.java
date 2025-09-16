package cc.insidious.lootify.api.database.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface IDatabaseRepository<K> extends AutoCloseable {
  void saveToDatabase(K key, byte[] value);

  void saveBatch(Map<K, byte[]> entries);

  void deleteBatch(Collection<K> keys);

  Optional<byte[]> getFromDatabaseSync(K key);

  Collection<byte[]> getAllEntriesFromDatabaseSync();

  void getFromDatabase(K key, Consumer<Optional<byte[]>> consumer);

  void getAllEntriesFromDatabase(Consumer<Collection<byte[]>> consumer);

  void removeFromDatabase(K key);
}
