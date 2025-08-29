package cc.insidious.example.api.database.redis.repository.stream.processor;

import cc.insidious.example.api.database.redis.event.EventOuterClass;

public interface IEventProcessor {
  EventOuterClass.EventType getEventType();

  void processEvent(byte[] data);
}
