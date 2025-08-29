package cc.insidious.example.api.database.redis.repository.stream.publisher;

import cc.insidious.example.api.constant.ExampleConstant;
import cc.insidious.example.api.database.redis.AbstractRedisHandler;
import cc.insidious.example.api.database.redis.event.EventOuterClass;
import cc.insidious.example.api.database.redis.repository.stream.consumer.AbstractRedisStreamConsumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public abstract class AbstractRedisStreamPublisher {

  private final AbstractRedisHandler redisHandler;

  private final XAddParams ADD_PARAMETERS =
      new XAddParams().id(StreamEntryID.NEW_ENTRY).approximateTrimming();

  private static final Charset CHARACTER_SET = ExampleConstant.CHARSET;

  public void publish(EventOuterClass.Event event, String streamKey) {
    this.redisHandler.runCommand(
        jedis -> {
          Map<byte[], byte[]> map = new HashMap<>();
          map.put(
              AbstractRedisStreamConsumer.EVENT_DATA_KEY.getBytes(CHARACTER_SET),
              event.toByteArray());

          jedis.xadd(streamKey.getBytes(CHARACTER_SET), ADD_PARAMETERS, map);
        });
  }

  public void publish(EventOuterClass.EventBundle eventBundle, String streamKey) {
    this.redisHandler.runCommand(
        jedis -> {
          Map<byte[], byte[]> map = new HashMap<>();
          map.put(streamKey.getBytes(CHARACTER_SET), eventBundle.toByteArray());

          jedis.xadd(streamKey.getBytes(CHARACTER_SET), map, ADD_PARAMETERS);
        });
  }
}
