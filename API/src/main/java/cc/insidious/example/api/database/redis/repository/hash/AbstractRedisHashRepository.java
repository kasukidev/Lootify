package cc.insidious.example.api.database.redis.repository.hash;

import cc.insidious.example.api.constant.ExampleConstant;
import cc.insidious.example.api.database.redis.AbstractRedisHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractRedisHashRepository {

  private final AbstractRedisHandler redisHandler;

  protected AbstractRedisHashRepository(AbstractRedisHandler redisHandler) {
    this.redisHandler = redisHandler;
  }

  public void saveHashToRedis(String hashKey, Map<byte[], byte[]> map) {
    this.redisHandler.runCommand(
        jedis -> jedis.hset(hashKey.getBytes(ExampleConstant.CHARSET), map));
  }

  public void removeHashFromRedis(String hashKey) {
    this.redisHandler.runCommand(jedis -> jedis.del(hashKey.getBytes(ExampleConstant.CHARSET)));
  }

  public void getHashFromRedis(String hashKey, Consumer<Optional<Map<byte[], byte[]>>> consumer) {
    this.redisHandler.runCommand(
        jedis ->
            consumer.accept(Optional.of(jedis.hgetAll(hashKey.getBytes(ExampleConstant.CHARSET)))));
  }

  public void saveHashFieldToRedis(String hashKey, String fieldKey, byte[] fieldValue) {
    this.redisHandler.runCommand(
        jedis ->
            jedis.hset(
                hashKey.getBytes(ExampleConstant.CHARSET),
                fieldKey.getBytes(ExampleConstant.CHARSET),
                fieldValue));
  }

  public void removeHashFieldFromRedis(String hashKey, String fieldKey) {
    this.redisHandler.runCommand(
        jedis ->
            jedis.hdel(
                hashKey.getBytes(ExampleConstant.CHARSET),
                fieldKey.getBytes(ExampleConstant.CHARSET)));
  }

  public void getHashFieldFromRedis(
      String hashKey, String fieldKey, Consumer<Optional<byte[]>> consumer) {
    this.redisHandler.runCommand(
        jedis ->
            consumer.accept(
                Optional.ofNullable(
                    jedis.hget(
                        hashKey.getBytes(ExampleConstant.CHARSET),
                        fieldKey.getBytes(ExampleConstant.CHARSET)))));
  }
}
