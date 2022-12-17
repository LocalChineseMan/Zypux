package com.google.common.collect;

import java.lang.reflect.Field;

final class FieldSetter<T> {
  private final Field field;
  
  private FieldSetter(Field field) {
    this.field = field;
    field.setAccessible(true);
  }
  
  void set(T instance, Object value) {
    try {
      this.field.set(instance, value);
    } catch (IllegalAccessException impossible) {
      throw new AssertionError(impossible);
    } 
  }
  
  void set(T instance, int value) {
    try {
      this.field.set(instance, Integer.valueOf(value));
    } catch (IllegalAccessException impossible) {
      throw new AssertionError(impossible);
    } 
  }
  
  static final class Serialization {}
}
