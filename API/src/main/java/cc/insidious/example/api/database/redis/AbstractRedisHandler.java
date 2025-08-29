package cc.insidious.example.api.database.redis;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
public abstract class AbstractRedisHandler implements AutoCloseable {

  private final String password;
  private final String channel;
  private final String proxyChannel;

  private final JedisPool jedisPool;

  private final JedisPool subscriptionPool;

  protected AbstractRedisHandler(
      String host, int port, String password, String channel, String proxyChannel) {
    this.password = password;
    this.channel = channel;
    this.proxyChannel = proxyChannel;

    this.jedisPool = new JedisPool(host, port);
    this.subscriptionPool = new JedisPool(host, port);
  }

  public void runCommand(Consumer<Jedis> consumer) {
    CompletableFuture.runAsync(
            () -> {
              try (Jedis jedis = this.jedisPool.getResource()) {
                if (!password.isEmpty()) {
                  jedis.auth(password);
                }

                consumer.accept(jedis);
              } catch (Exception exception) {
                exception.printStackTrace();
              }
            })
        .exceptionally(
            exception -> {
              if (exception != null) {
                exception.printStackTrace();
              }
              return null;
            });
  }

  @Override
  public void close() {
    if (this.jedisPool != null) {
      this.jedisPool.close();
    }
    if (this.subscriptionPool != null) {
      this.subscriptionPool.close();
    }
  }
}
