package ir.lecer.uwu.events.handler;

import java.util.Iterator;

public class ArrayHelper<T> implements Iterable<T> {
  private T[] elements;
  
  public ArrayHelper(T[] array) {
    this.elements = array;
  }
  
  public ArrayHelper() {
    this.elements = (T[])new Object[0];
  }
  
  public void add(T t) {
    if (t != null) {
      Object[] array = new Object[size() + 1];
      for (int i = 0; i < array.length; i++) {
        if (i < size()) {
          array[i] = get(i);
        } else {
          array[i] = t;
        } 
      } 
      set((T[])array);
    } 
  }
  
  public boolean contains(T t) {
    Object[] array;
    for (int lenght = (array = (Object[])array()).length, i = 0; i < lenght; i++) {
      T entry = (T)array[i];
      if (entry.equals(t))
        return true; 
    } 
    return false;
  }
  
  public void remove(T t) {
    if (contains(t)) {
      Object[] array = new Object[size() - 1];
      boolean b = true;
      for (int i = 0; i < size(); i++) {
        if (b && get(i).equals(t)) {
          b = false;
        } else {
          array[b ? i : (i - 1)] = get(i);
        } 
      } 
      set((T[])array);
    } 
  }
  
  public T[] array() {
    return this.elements;
  }
  
  public int size() {
    return (array()).length;
  }
  
  public void set(T[] array) {
    this.elements = array;
  }
  
  public T get(int index) {
    return array()[index];
  }
  
  public void clear() {
    this.elements = (T[])new Object[0];
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public Iterator<T> iterator() {
    return (Iterator<T>)new Object(this);
  }
}
