package com.google.common.base;

import java.io.Serializable;
import javax.annotation.Nullable;

final class ValueOfFunction<T extends Enum<T>> implements Function<String, T>, Serializable {
  private final Class<T> enumClass;
  
  private static final long serialVersionUID = 0L;
  
  private ValueOfFunction(Class<T> enumClass) {
    this.enumClass = Preconditions.<Class<T>>checkNotNull(enumClass);
  }
  
  public T apply(String value) {
    try {
      return Enum.valueOf(this.enumClass, value);
    } catch (IllegalArgumentException e) {
      return null;
    } 
  }
  
  public boolean equals(@Nullable Object obj) {
    return (obj instanceof ValueOfFunction && this.enumClass.equals(((ValueOfFunction)obj).enumClass));
  }
  
  public int hashCode() {
    return this.enumClass.hashCode();
  }
  
  public String toString() {
    return "Enums.valueOf(" + this.enumClass + ")";
  }
}
