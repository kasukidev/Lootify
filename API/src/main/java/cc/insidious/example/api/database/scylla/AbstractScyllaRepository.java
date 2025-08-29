package cc.insidious.example.api.database.scylla;

import cc.insidious.example.api.database.repository.IDatabaseRepository;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.*;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Getter
public abstract class AbstractScyllaRepository<K> implements IDatabaseRepository<K> {

  private final String tableName;
  private final CqlSession cqlSession;

  private final String createTableQuery;
  private final String getEntryQuery;
  private final String getAllEntriesQuery;
  private final String removeFromDatabaseQuery;
  private final String saveToDatabaseQuery;
  private final String saveBatchToDatabaseQuery;
  private final String removeBatchFromDatabaseQuery;
  private final String idName;
  private final String columnName;

  private final Executor executor;

  protected AbstractScyllaRepository(
      String host, int port, boolean auth, String username, String password, String tableName) {
    this.tableName = tableName;

    CqlSessionBuilder builder =
        CqlSession.builder()
            .addContactPoint(new InetSocketAddress(host, port))
            .withLocalDatacenter("datacenter1");

    if (auth) {
      builder.withAuthCredentials(username, password);
    }

    this.cqlSession = builder.build();

    this.idName = "id";
    this.columnName = "data";
    this.createTableQuery =
        String.format(
            "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s BLOB)", this.tableName, idName, columnName);
    this.getEntryQuery = String.format("SELECT data FROM %s WHERE %s = ?", this.tableName, idName);
    this.getAllEntriesQuery = String.format("SELECT * FROM %s", this.tableName);
    this.removeFromDatabaseQuery = String.format("DELETE FROM %s WHERE id = ?", this.tableName);
    this.saveToDatabaseQuery =
        String.format(
            "INSERT INTO %s (%s, %s) VALUES (?, ?)", this.tableName, this.idName, this.columnName);
    this.saveBatchToDatabaseQuery =
        String.format(
            "INSERT INTO %s (%s, %s) VALUES (?, ?)", this.tableName, this.idName, this.columnName);
    this.removeBatchFromDatabaseQuery =
        String.format("DELETE FROM %s WHERE id = ?", this.tableName);

    this.executor = Executors.newFixedThreadPool(2);

    this.createTable();
  }

  private void createTable() {
    this.executor.execute(() -> this.cqlSession.execute(createTableQuery));
  }

  @Override
  public void getFromDatabase(K key, Consumer<Optional<byte[]>> consumer) {
    this.executor.execute(
        () -> {
          PreparedStatement preparedStatement = this.cqlSession.prepare(this.getEntryQuery);

          BoundStatement boundStatement = preparedStatement.bind(key.toString());

          Row row = this.cqlSession.execute(boundStatement).one();

          if (row != null) {
            ByteBuffer byteBuffer = row.getByteBuffer(this.columnName);
            if (byteBuffer != null) {
              byte[] byteArray = new byte[byteBuffer.remaining()];
              byteBuffer.get(byteArray);
              consumer.accept(Optional.of(byteArray));
            }
            return;
          }

          consumer.accept(Optional.empty());
        });
  }

  @Override
  public void getAllEntriesFromDatabase(Consumer<Collection<byte[]>> consumer) {
    this.executor.execute(() -> consumer.accept(this.getAllEntriesFromDatabaseSync()));
  }

  @Override
  public void saveToDatabase(K key, byte[] value) {
    this.executor.execute(
        () -> {
          PreparedStatement preparedStatement = this.cqlSession.prepare(this.saveToDatabaseQuery);
          BoundStatement boundStatement =
              preparedStatement.bind(key.toString(), ByteBuffer.wrap(value));
          this.cqlSession.execute(boundStatement);
        });
  }

  @Override
  public void removeFromDatabase(K key) {
    this.executor.execute(
        () -> {
          PreparedStatement preparedStatement =
              this.cqlSession.prepare(this.removeFromDatabaseQuery);
          BoundStatement boundStatement = preparedStatement.bind(key.toString());
          this.cqlSession.execute(boundStatement);
        });
  }

  @Override
  public Optional<byte[]> getFromDatabaseSync(K key) {
    PreparedStatement preparedStatement = this.cqlSession.prepare(this.getEntryQuery);

    BoundStatement boundStatement = preparedStatement.bind(key.toString());

    Row row = this.cqlSession.execute(boundStatement).one();

    if (row != null) {
      ByteBuffer byteBuffer = row.getByteBuffer(this.columnName);
      if (byteBuffer != null) {
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byteArray);
        return Optional.of(byteArray);
      }
    }

    return Optional.empty();
  }

  @Override
  public Collection<byte[]> getAllEntriesFromDatabaseSync() {
    ResultSet resultSet = this.cqlSession.execute(this.getAllEntriesQuery);
    Iterator<Row> iterator = resultSet.iterator();
    List<byte[]> results = new ArrayList<>();
    while (iterator.hasNext()) {
      Row row = iterator.next();
      ByteBuffer byteBuffer = row.getByteBuffer(this.columnName);

      if (byteBuffer != null) {
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        results.add(bytes);
      }
    }

    return results;
  }

  @Override
  public void close() {
    this.cqlSession.close();
  }

  @Override
  public void saveBatch(Map<K, byte[]> map) {
    this.executor.execute(
        () -> {
          PreparedStatement preparedStatement =
              this.cqlSession.prepare(this.saveBatchToDatabaseQuery);

          BatchStatementBuilder batchBuilder = BatchStatement.builder(BatchType.LOGGED);

          for (Map.Entry<K, byte[]> entry : map.entrySet()) {
            String id = entry.getKey().toString();
            byte[] data = entry.getValue();

            BoundStatement statement = preparedStatement.bind(id, ByteBuffer.wrap(data));
            batchBuilder.addStatement(statement);
          }

          this.cqlSession.execute(batchBuilder.build());
        });
  }

  @Override
  public void deleteBatch(Collection<K> keys) {
    this.executor.execute(
        () -> {
          PreparedStatement preparedStatement =
              this.cqlSession.prepare(this.removeBatchFromDatabaseQuery);

          BatchStatementBuilder batchBuilder = BatchStatement.builder(BatchType.LOGGED);

          for (K key : keys) {
            String id = key.toString();
            BoundStatement boundStatement = preparedStatement.bind(id);
            batchBuilder.addStatement(boundStatement);
          }

          this.cqlSession.execute(batchBuilder.build());
        });
  }
}
