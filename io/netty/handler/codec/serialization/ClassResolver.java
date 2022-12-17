package io.netty.handler.codec.serialization;

public interface ClassResolver {
  Class<?> resolve(String paramString) throws ClassNotFoundException;
}
