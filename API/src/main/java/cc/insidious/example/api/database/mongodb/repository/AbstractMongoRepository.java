package cc.insidious.example.api.database.mongodb.repository;

import cc.insidious.example.api.database.repository.IDatabaseRepository;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.Binary;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public abstract class AbstractMongoRepository<K> implements IDatabaseRepository<K> {

  private final MongoCollection<Document> collection;
  private static final String DATA_KEY = "data";
  private static final String ID_FIELD = "_id";
  private static final ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(true);
  
  private final Executor executor = Executors.newFixedThreadPool(2);

  @Override
  public Optional<byte[]> getFromDatabaseSync(K key) {
    Document document = this.collection.find(Filters.eq(ID_FIELD, key.toString())).first();

    if (document == null || !document.containsKey(DATA_KEY)) {
      return Optional.empty();
    }

    Binary binary = document.get(DATA_KEY, Binary.class);
    return Optional.of(binary.getData());
  }

  @Override
  public void saveToDatabase(K key, byte[] value) {
    this.executor.execute(
        () -> {
          Document document =
              new Document(ID_FIELD, key.toString()).append(DATA_KEY, new Binary(value));
          this.collection.replaceOne(Filters.eq(ID_FIELD, key.toString()), document, REPLACE_OPTIONS);
        });
  }

  @Override
  public void removeFromDatabase(K key) {
    this.executor.execute(() -> this.collection.deleteOne(Filters.eq(ID_FIELD, key.toString())));
  }

  @Override
  public Collection<byte[]> getAllEntriesFromDatabaseSync() {
    List<byte[]> list = new ArrayList<>();

    FindIterable<Document> documents = this.collection.find();
    try (MongoCursor<Document> cursor = documents.cursor()) {
      while (cursor.hasNext()) {
        Document document = cursor.next();
        if (!document.containsKey(DATA_KEY)) {
          continue;
        }

        Binary binary = document.get(DATA_KEY, Binary.class);
        list.add(binary.getData());
      }
    }
    return list;
  }

  @Override
  public void getAllEntriesFromDatabase(Consumer<Collection<byte[]>> consumer) {
    this.executor.execute(() -> consumer.accept(this.getAllEntriesFromDatabaseSync()));
  }

  @Override
  public void saveBatch(Map<K, byte[]> entries) {
    this.executor.execute(
        () -> {
          List<Document> documents = new ArrayList<>();

          for (Map.Entry<K, byte[]> entry : entries.entrySet()) {
            Document document =
                new Document(ID_FIELD, entry.getKey().toString())
                    .append(DATA_KEY, new Binary(entry.getValue()));
            documents.add(document);
          }

          this.collection.insertMany(documents);
        });
  }

  @Override
  public void deleteBatch(Collection<K> keys) {
    this.executor.execute(() -> this.collection.deleteMany(Filters.in(ID_FIELD, keys)));
  }

  @Override
  public void getFromDatabase(K key, Consumer<Optional<byte[]>> consumer) {
    this.executor.execute(() -> consumer.accept(this.getFromDatabaseSync(key)));
  }
}
