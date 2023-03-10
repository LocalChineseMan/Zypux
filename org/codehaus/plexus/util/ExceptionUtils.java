package org.codehaus.plexus.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ExceptionUtils {
  static final java.lang.String WRAPPED_MARKER = " [wrapped] ";
  
  protected static java.lang.String[] CAUSE_METHOD_NAMES = new java.lang.String[] { "getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested" };
  
  public static void addCauseMethodName(java.lang.String methodName) {
    if (methodName != null && methodName.length() > 0) {
      List<java.lang.String> list = new ArrayList<>(Arrays.asList(CAUSE_METHOD_NAMES));
      list.add(methodName);
      CAUSE_METHOD_NAMES = list.<java.lang.String>toArray(new java.lang.String[0]);
    } 
  }
  
  public static Throwable getCause(Throwable throwable) {
    return getCause(throwable, CAUSE_METHOD_NAMES);
  }
  
  public static Throwable getCause(Throwable throwable, java.lang.String[] methodNames) {
    Throwable cause = getCauseUsingWellKnownTypes(throwable);
    if (cause == null) {
      for (java.lang.String methodName : methodNames) {
        cause = getCauseUsingMethodName(throwable, methodName);
        if (cause != null)
          break; 
      } 
      if (cause == null)
        cause = getCauseUsingFieldName(throwable, "detail"); 
    } 
    return cause;
  }
  
  public static Throwable getRootCause(Throwable throwable) {
    Throwable cause = getCause(throwable);
    if (cause != null) {
      throwable = cause;
      while ((throwable = getCause(throwable)) != null)
        cause = throwable; 
    } 
    return cause;
  }
  
  private static Throwable getCauseUsingWellKnownTypes(Throwable throwable) {
    if (throwable instanceof SQLException)
      return ((SQLException)throwable).getNextException(); 
    if (throwable instanceof InvocationTargetException)
      return ((InvocationTargetException)throwable).getTargetException(); 
    return null;
  }
  
  private static Throwable getCauseUsingMethodName(Throwable throwable, java.lang.String methodName) {
    Method method = null;
    try {
      method = throwable.getClass().getMethod(methodName, null);
    } catch (NoSuchMethodException noSuchMethodException) {
    
    } catch (SecurityException securityException) {}
    if (method != null && Throwable.class.isAssignableFrom(method.getReturnType()))
      try {
        return (Throwable)method.invoke(throwable, new Object[0]);
      } catch (IllegalAccessException illegalAccessException) {
      
      } catch (IllegalArgumentException illegalArgumentException) {
      
      } catch (InvocationTargetException invocationTargetException) {} 
    return null;
  }
  
  private static Throwable getCauseUsingFieldName(Throwable throwable, java.lang.String fieldName) {
    Field field = null;
    try {
      field = throwable.getClass().getField(fieldName);
    } catch (NoSuchFieldException noSuchFieldException) {
    
    } catch (SecurityException securityException) {}
    if (field != null && Throwable.class.isAssignableFrom(field.getType()))
      try {
        return (Throwable)field.get(throwable);
      } catch (IllegalAccessException illegalAccessException) {
      
      } catch (IllegalArgumentException illegalArgumentException) {} 
    return null;
  }
  
  public static int getThrowableCount(Throwable throwable) {
    int count = 0;
    while (throwable != null) {
      count++;
      throwable = getCause(throwable);
    } 
    return count;
  }
  
  public static Throwable[] getThrowables(Throwable throwable) {
    List<Throwable> list = new ArrayList<>();
    while (throwable != null) {
      list.add(throwable);
      throwable = getCause(throwable);
    } 
    return list.<Throwable>toArray(new Throwable[0]);
  }
  
  public static int indexOfThrowable(Throwable throwable, Class type) {
    return indexOfThrowable(throwable, type, 0);
  }
  
  public static int indexOfThrowable(Throwable throwable, Class type, int fromIndex) {
    if (fromIndex < 0)
      throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex); 
    Throwable[] throwables = getThrowables(throwable);
    if (fromIndex >= throwables.length)
      throw new IndexOutOfBoundsException("Throwable index out of range: " + fromIndex); 
    for (int i = fromIndex; i < throwables.length; i++) {
      if (throwables[i].getClass().equals(type))
        return i; 
    } 
    return -1;
  }
  
  public static void printRootCauseStackTrace(Throwable t, PrintStream stream) {
    java.lang.String[] trace = getRootCauseStackTrace(t);
    for (java.lang.String aTrace : trace)
      stream.println(aTrace); 
    stream.flush();
  }
  
  public static void printRootCauseStackTrace(Throwable t) {
    printRootCauseStackTrace(t, System.err);
  }
  
  public static void printRootCauseStackTrace(Throwable t, PrintWriter writer) {
    java.lang.String[] trace = getRootCauseStackTrace(t);
    for (java.lang.String aTrace : trace)
      writer.println(aTrace); 
    writer.flush();
  }
  
  public static java.lang.String[] getRootCauseStackTrace(Throwable t) {
    Throwable[] throwables = getThrowables(t);
    int count = throwables.length;
    ArrayList<java.lang.String> frames = new ArrayList<>();
    List<java.lang.String> nextTrace = getStackFrameList(throwables[count - 1]);
    for (int i = count; --i >= 0; ) {
      List<java.lang.String> trace = nextTrace;
      if (i != 0) {
        nextTrace = getStackFrameList(throwables[i - 1]);
        removeCommonFrames(trace, nextTrace);
      } 
      if (i == count - 1) {
        frames.add(throwables[i].toString());
      } else {
        frames.add(" [wrapped] " + throwables[i].toString());
      } 
      for (java.lang.String aTrace : trace)
        frames.add(aTrace); 
    } 
    return frames.<java.lang.String>toArray(new java.lang.String[0]);
  }
  
  private static void removeCommonFrames(List<java.lang.String> causeFrames, List<java.lang.String> wrapperFrames) {
    int causeFrameIndex = causeFrames.size() - 1;
    int wrapperFrameIndex = wrapperFrames.size() - 1;
    while (causeFrameIndex >= 0 && wrapperFrameIndex >= 0) {
      java.lang.String causeFrame = causeFrames.get(causeFrameIndex);
      java.lang.String wrapperFrame = wrapperFrames.get(wrapperFrameIndex);
      if (causeFrame.equals(wrapperFrame))
        causeFrames.remove(causeFrameIndex); 
      causeFrameIndex--;
      wrapperFrameIndex--;
    } 
  }
  
  public static java.lang.String getStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    t.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
  
  public static java.lang.String getFullStackTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    Throwable[] ts = getThrowables(t);
    for (Throwable t1 : ts) {
      t1.printStackTrace(pw);
      if (isNestedThrowable(t1))
        break; 
    } 
    return sw.getBuffer().toString();
  }
  
  public static boolean isNestedThrowable(Throwable throwable) {
    if (throwable == null)
      return false; 
    if (throwable instanceof SQLException)
      return true; 
    if (throwable instanceof InvocationTargetException)
      return true; 
    for (java.lang.String CAUSE_METHOD_NAME : CAUSE_METHOD_NAMES) {
      try {
        Method method = throwable.getClass().getMethod(CAUSE_METHOD_NAME, null);
        if (method != null)
          return true; 
      } catch (NoSuchMethodException noSuchMethodException) {
      
      } catch (SecurityException securityException) {}
    } 
    try {
      Field field = throwable.getClass().getField("detail");
      if (field != null)
        return true; 
    } catch (NoSuchFieldException noSuchFieldException) {
    
    } catch (SecurityException securityException) {}
    return false;
  }
  
  public static java.lang.String[] getStackFrames(Throwable t) {
    return getStackFrames(getStackTrace(t));
  }
  
  static java.lang.String[] getStackFrames(java.lang.String stackTrace) {
    java.lang.String linebreak = System.getProperty("line.separator");
    StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
    List<java.lang.String> list = new LinkedList<>();
    while (frames.hasMoreTokens())
      list.add(frames.nextToken()); 
    return list.<java.lang.String>toArray(new java.lang.String[0]);
  }
  
  static List<java.lang.String> getStackFrameList(Throwable t) {
    java.lang.String stackTrace = getStackTrace(t);
    java.lang.String linebreak = System.getProperty("line.separator");
    StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
    List<java.lang.String> list = new LinkedList<>();
    boolean traceStarted = false;
    while (frames.hasMoreTokens()) {
      java.lang.String token = frames.nextToken();
      int at = token.indexOf("at");
      if (at != -1 && token.substring(0, at).trim().length() == 0) {
        traceStarted = true;
        list.add(token);
        continue;
      } 
      if (traceStarted)
        break; 
    } 
    return list;
  }
}
