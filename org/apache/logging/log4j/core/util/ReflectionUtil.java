package org.apache.logging.log4j.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class ReflectionUtil {
  public static <T extends java.lang.reflect.AccessibleObject & Member> boolean isAccessible(T member) {
    Objects.requireNonNull(member, "No member provided");
    return (Modifier.isPublic(((Member)member).getModifiers()) && Modifier.isPublic(((Member)member).getDeclaringClass().getModifiers()));
  }
  
  public static <T extends java.lang.reflect.AccessibleObject & Member> void makeAccessible(T member) {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic isAccessible : (Ljava/lang/reflect/AccessibleObject;)Z
    //   4: ifne -> 19
    //   7: aload_0
    //   8: invokevirtual isAccessible : ()Z
    //   11: ifne -> 19
    //   14: aload_0
    //   15: iconst_1
    //   16: invokevirtual setAccessible : (Z)V
    //   19: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #58	-> 0
    //   #59	-> 14
    //   #61	-> 19
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   0	20	0	member	Ljava/lang/reflect/AccessibleObject;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   0	20	0	member	TT;
  }
  
  public static void makeAccessible(Field field) {
    Objects.requireNonNull(field, "No field provided");
    if ((!isAccessible(field) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible())
      field.setAccessible(true); 
  }
  
  public static Object getFieldValue(Field field, Object instance) {
    makeAccessible(field);
    if (!Modifier.isStatic(field.getModifiers()))
      Objects.requireNonNull(instance, "No instance given for non-static field"); 
    try {
      return field.get(instance);
    } catch (IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    } 
  }
  
  public static Object getStaticFieldValue(Field field) {
    return getFieldValue(field, null);
  }
  
  public static void setFieldValue(Field field, Object instance, Object value) {
    makeAccessible(field);
    if (!Modifier.isStatic(field.getModifiers()))
      Objects.requireNonNull(instance, "No instance given for non-static field"); 
    try {
      field.set(instance, value);
    } catch (IllegalAccessException e) {
      throw new UnsupportedOperationException(e);
    } 
  }
  
  public static void setStaticFieldValue(Field field, Object value) {
    setFieldValue(field, null, value);
  }
  
  public static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
    Objects.requireNonNull(clazz, "No class provided");
    try {
      Constructor<T> constructor = clazz.getDeclaredConstructor(new Class[0]);
      makeAccessible(constructor);
      return constructor;
    } catch (NoSuchMethodException ignored) {
      try {
        Constructor<T> constructor = clazz.getConstructor(new Class[0]);
        makeAccessible(constructor);
        return constructor;
      } catch (NoSuchMethodException e) {
        throw new IllegalStateException(e);
      } 
    } 
  }
  
  public static <T> T instantiate(Class<T> clazz) {
    Objects.requireNonNull(clazz, "No class provided");
    Constructor<T> constructor = getDefaultConstructor(clazz);
    try {
      return constructor.newInstance(new Object[0]);
    } catch (LinkageError|InstantiationException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      Throwables.rethrow(e.getCause());
      throw new InternalError("Unreachable");
    } 
  }
}
