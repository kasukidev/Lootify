package cc.insidious.example.api.database.mysql;

import cc.insidious.example.api.database.repository.IDatabaseRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
public abstract class AbstractMySQLRepository<K> implements IDatabaseRepository<K> {

  private final HikariDataSource dataSource;
  private final String dataLabel;
  private final String setupTableQuery;
  private final String saveToDatabaseQuery;
  private final String getAllEntriesQuery;
  private final String getFromDatabaseQuery;
  private final String removeFromDatabaseQuery;
  private final String saveBatchQuery;
  private final String removeBatchQuery;

  protected AbstractMySQLRepository(HikariDataSource dataSource, String tableName) {
    this.dataSource = dataSource;
    this.dataLabel = "byte_data";

    this.setupTableQuery =
        String.format(
            "CREATE TABLE IF NOT EXISTS `%s` (data_key VARCHAR(255) PRIMARY KEY, %s MEDIUMBLOB)",
            tableName, this.dataLabel);
    this.saveToDatabaseQuery =
        String.format(
            "INSERT INTO `%s` (data_key, %s) VALUES (?, ?) ON DUPLICATE KEY UPDATE %s = ?",
            tableName, this.dataLabel, this.dataLabel);
    this.getAllEntriesQuery = String.format("SELECT %s FROM %s", this.dataLabel, tableName);
    this.getFromDatabaseQuery =
        String.format("SELECT %s FROM %s WHERE data_key = ? LIMIT 1", this.dataLabel, tableName);
    this.removeFromDatabaseQuery = String.format("DELETE FROM %s WHERE data_key = ?", tableName);
    this.saveBatchQuery =
        String.format(
            "INSERT INTO %s (data_key, %s) VALUES (?, ?) ON DUPLICATE KEY UPDATE %s = VALUES(%s)",
            tableName, this.dataLabel, this.dataLabel, this.dataLabel);
    this.removeBatchQuery = String.format("DELETE FROM %s WHERE data_key = ?", tableName);
    this.setupTable();
  }

  private void setupTable() {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(this.setupTableQuery)) {
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveToDatabase(K key, byte[] value) {
    CompletableFuture.runAsync(
        () -> {
          try (Connection connection = dataSource.getConnection();
              PreparedStatement statement = connection.prepareStatement(this.saveToDatabaseQuery)) {
            statement.setString(1, key.toString());
            statement.setBytes(2, value);
            statement.setBytes(3, value);
            statement.executeUpdate();
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  @Override
  public void getAllEntriesFromDatabase(Consumer<Collection<byte[]>> consumer) {
    CompletableFuture.runAsync(() -> consumer.accept(this.getAllEntriesFromDatabaseSync()));
  }

  @Override
  public void getFromDatabase(K key, Consumer<Optional<byte[]>> consumer) {
    CompletableFuture.runAsync(() -> consumer.accept(this.getFromDatabaseSync(key)));
  }

  @Override
  public void removeFromDatabase(K key) {
    CompletableFuture.runAsync(
        () -> {
          try (Connection connection = dataSource.getConnection();
              PreparedStatement statement =
                  connection.prepareStatement(this.removeFromDatabaseQuery)) {
            statement.setString(1, key.toString());
            statement.executeUpdate();
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  @Override
  public void saveBatch(Map<K, byte[]> batch) {
    CompletableFuture.runAsync(
        () -> {
          try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(this.saveBatchQuery);

            for (Map.Entry<K, byte[]> entry : batch.entrySet()) {
              String key = entry.getKey().toString();
              byte[] value = entry.getValue();
              statement.setString(1, key);
              statement.setBytes(2, value);
              statement.addBatch();
            }

            statement.executeBatch();
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  @Override
  public void deleteBatch(Collection<K> batch) {
    CompletableFuture.runAsync(
        () -> {
          try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(this.removeBatchQuery);
            for (K key : batch) {
              statement.setString(1, key.toString());
              statement.addBatch();
            }

            statement.executeBatch();
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  @Override
  public Optional<byte[]> getFromDatabaseSync(K key) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(this.getFromDatabaseQuery)) {
      statement.setString(1, key.toString());
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          byte[] data = resultSet.getBytes(this.dataLabel);
          return Optional.ofNullable(data);
        }
        return Optional.empty();
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public Collection<byte[]> getAllEntriesFromDatabaseSync() {
    List<byte[]> allValues = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(this.getAllEntriesQuery);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          byte[] data = resultSet.getBytes(this.dataLabel);
          allValues.add(data);
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return allValues;
  }
}
