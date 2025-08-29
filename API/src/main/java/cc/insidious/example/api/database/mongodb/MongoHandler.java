package cc.insidious.example.api.database.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Collections;

@Getter
public class MongoHandler implements AutoCloseable {

  private final MongoClient mongoClient;
  private final MongoDatabase mongoDatabase;

  public MongoHandler(
      String host,
      int port,
      boolean auth,
      String username,
      String password,
      String database,
      boolean uriMode,
      String uri) {

    if (uriMode) {
      this.mongoClient = MongoClients.create(uri);
      this.mongoDatabase = mongoClient.getDatabase(database);
      return;
    }
    if (auth) {
      MongoClientSettings settings =
          MongoClientSettings.builder()
              .applyToClusterSettings(
                  builder ->
                      builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
              .credential(
                  MongoCredential.createCredential(username, database, password.toCharArray()))
              .build();

      this.mongoClient = MongoClients.create(settings);
      this.mongoDatabase = mongoClient.getDatabase(database);
      return;
    }
    MongoClientSettings settings =
        MongoClientSettings.builder()
            .applyToClusterSettings(
                builder -> builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
            .build();

    this.mongoClient = MongoClients.create(settings);
    this.mongoDatabase = mongoClient.getDatabase(database);
  }

  @Override
  public void close() {
    if (this.mongoClient != null) {
      this.mongoClient.close();
    }
  }

  public MongoCollection<Document> getCollection(String name) {
    return this.mongoDatabase.getCollection(name);
  }
}
