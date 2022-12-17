package com.google.common.collect;

import java.util.Iterator;

class null extends FluentIterable<T> {
  public UnmodifiableIterator<T> iterator() {
    return (UnmodifiableIterator<T>)new TreeTraverser.BreadthFirstIterator(TreeTraverser.this, root);
  }
  
  private final class TreeTraverser {}
}
