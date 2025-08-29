package cc.insidious.example.api.database.redis.repository.stream;

import cc.insidious.example.api.database.redis.event.EventOuterClass;
import cc.insidious.example.api.database.redis.repository.stream.processor.IEventProcessor;

import java.util.Optional;

public interface IEventProcessorHandler {

  void registerEventProcessor(IEventProcessor processor);

  void unregisterEventProcessor(IEventProcessor processor);

  boolean hasEventProcessor(EventOuterClass.EventType eventType);

  Optional<IEventProcessor> getEventProcessor(EventOuterClass.EventType type);

  void load();
}
