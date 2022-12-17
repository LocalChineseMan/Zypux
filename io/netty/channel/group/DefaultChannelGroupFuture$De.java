package io.netty.channel.group;

import java.util.Map;

final class DefaultEntry<K, V> implements Map.Entry<K, V> {
  private final K key;
  
  private final V value;
  
  DefaultEntry(K key, V value) {
    this.key = key;
    this.value = value;
  }
  
  public K getKey() {
    return this.key;
  }
  
  public V getValue() {
    return this.value;
  }
  
  public V setValue(V value) {
    throw new UnsupportedOperationException("read-only");
  }
}
