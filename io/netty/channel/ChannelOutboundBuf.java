package io.netty.channel;

public interface MessageProcessor {
  boolean processMessage(Object paramObject) throws Exception;
}
