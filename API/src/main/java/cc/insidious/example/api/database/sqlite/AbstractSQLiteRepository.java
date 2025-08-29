package cc.insidious.example.api.database.sqlite;

import cc.insidious.example.api.database.repository.IDatabaseRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public abstract class AbstractSQLiteRepository<K> implements IDatabaseRepository<K> {

  private final String connectionString;
  private final String dataLabel;
  private final String setupTableQuery;
  private final String saveToDatabaseQuery;
  private final String getAllEntriesQuery;
  private final String getFromDatabaseQuery;
  private final String removeFromDatabaseQuery;
  private final String saveBatchQuery;
  private final String removeBatchQuery;

  private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

  static {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to load SQLite JDBC driver", e);
    }
  }

  protected AbstractSQLiteRepository(JavaPlugin instance, String name, String tableName) {
    this.connectionString =
        "jdbc:sqlite:" + instance.getDataFolder().getAbsolutePath() + "/" + name + ".db";
    this.dataLabel = "byte_data";

    this.setupTableQuery =
        String.format(
            "CREATE TABLE IF NOT EXISTS `%s` (data_key VARCHAR(255) PRIMARY KEY, %s BLOB)",
            tableName, this.dataLabel);
    this.saveToDatabaseQuery =
        String.format(
            "INSERT OR REPLACE INTO `%s` (data_key, %s) VALUES (?, ?)", tableName, this.dataLabel);
    this.getAllEntriesQuery = String.format("SELECT %s FROM %s", this.dataLabel, tableName);
    this.getFromDatabaseQuery =
        String.format("SELECT %s FROM %s WHERE data_key = ? LIMIT 1", this.dataLabel, tableName);
    this.removeFromDatabaseQuery = String.format("DELETE FROM %s WHERE data_key = ?", tableName);
    this.saveBatchQuery =
        String.format(
            "INSERT OR REPLACE INTO `%s` (data_key, %s) VALUES (?, ?)", tableName, this.dataLabel);
    this.removeBatchQuery = String.format("DELETE FROM %s WHERE data_key = ?", tableName);
    this.setupTable();
  }

  private void setupTable() {
    EXECUTOR.execute(
        () -> {
          try (Connection connection = DriverManager.getConnection(this.connectionString);
              PreparedStatement statement = connection.prepareStatement(this.setupTableQuery)) {
            statement.executeUpdate();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
  }

  public void saveToDatabase(K key, byte[] value) {
    EXECUTOR.execute(
        () -> {
          try (Connection connection = DriverManager.getConnection(this.connectionString);
              PreparedStatement statement = connection.prepareStatement(this.saveToDatabaseQuery)) {
            statement.setString(1, key.toString());
            statement.setBytes(2, value);
            statement.executeUpdate();
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  public void getAllEntriesFromDatabase(Consumer<Collection<byte[]>> consumer) {
    EXECUTOR.execute(() -> consumer.accept(this.getAllEntriesFromDatabaseSync()));
  }

  public void getFromDatabase(K key, Consumer<Optional<byte[]>> consumer) {
    EXECUTOR.execute(() -> consumer.accept(this.getFromDatabaseSync(key)));
  }

  public void removeFromDatabase(K key) {
    EXECUTOR.execute(
        () -> {
          try (Connection connection = DriverManager.getConnection(this.connectionString)) {
            try (PreparedStatement statement =
                connection.prepareStatement(this.removeFromDatabaseQuery)) {
              statement.setString(1, key.toString());
              statement.executeUpdate();
            }
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  public void saveBatch(Map<K, byte[]> batch) {
    EXECUTOR.execute(
        () -> {
          try (Connection connection = DriverManager.getConnection(this.connectionString)) {
            try (PreparedStatement statement = connection.prepareStatement(this.saveBatchQuery)) {
              for (Map.Entry<K, byte[]> entry : batch.entrySet()) {
                String key = entry.getKey().toString();
                byte[] value = entry.getValue();

                statement.setString(1, key);
                statement.setBytes(2, value);
                statement.addBatch();
              }

              statement.executeBatch();
            }
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  @Override
  public void deleteBatch(Collection<K> keys) {
    EXECUTOR.execute(
        () -> {
          try (Connection connection = DriverManager.getConnection(this.connectionString)) {
            try (PreparedStatement statement = connection.prepareStatement(this.removeBatchQuery)) {
              for (K key : keys) {
                statement.setString(1, key.toString());
                statement.addBatch();
              }

              statement.executeBatch();
            }
          } catch (SQLException exception) {
            exception.printStackTrace();
          }
        });
  }

  @Override
  public Optional<byte[]> getFromDatabaseSync(K key) {
    try (Connection connection = DriverManager.getConnection(this.connectionString)) {
      try (PreparedStatement statement = connection.prepareStatement(this.getFromDatabaseQuery)) {
        statement.setString(1, key.toString());
        try (ResultSet resultSet = statement.executeQuery()) {
          if (resultSet.next()) {
            byte[] data = resultSet.getBytes(this.dataLabel);
            return Optional.ofNullable(data);
          }
          return Optional.empty();
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public Collection<byte[]> getAllEntriesFromDatabaseSync() {
    List<byte[]> allValues = new ArrayList<>();

    try (Connection connection = DriverManager.getConnection(this.connectionString)) {
      try (PreparedStatement statement = connection.prepareStatement(this.getAllEntriesQuery)) {
        try (ResultSet resultSet = statement.executeQuery()) {
          while (resultSet.next()) {
            byte[] data = resultSet.getBytes(this.dataLabel);
            allValues.add(data);
          }
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
    return allValues;
  }
}
