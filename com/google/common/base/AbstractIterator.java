package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;
import java.util.NoSuchElementException;

@GwtCompatible
abstract class AbstractIterator<T> implements Iterator<T> {
  private State state = State.NOT_READY;
  
  private T next;
  
  protected abstract T computeNext();
  
  protected final T endOfData() {
    this.state = State.DONE;
    return null;
  }
  
  public final boolean hasNext() {
    Preconditions.checkState((this.state != State.FAILED));
    switch (null.$SwitchMap$com$google$common$base$AbstractIterator$State[this.state.ordinal()]) {
      case 1:
        return false;
      case 2:
        return true;
    } 
    return tryToComputeNext();
  }
  
  private boolean tryToComputeNext() {
    this.state = State.FAILED;
    this.next = computeNext();
    if (this.state != State.DONE) {
      this.state = State.READY;
      return true;
    } 
    return false;
  }
  
  public final T next() {
    if (!hasNext())
      throw new NoSuchElementException(); 
    this.state = State.NOT_READY;
    T result = this.next;
    this.next = null;
    return result;
  }
  
  public final void remove() {
    throw new UnsupportedOperationException();
  }
  
  private enum AbstractIterator {
  
  }
}
