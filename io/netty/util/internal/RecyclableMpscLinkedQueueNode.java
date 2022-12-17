package io.netty.util.internal;

import io.netty.util.Recycler;

public abstract class RecyclableMpscLinkedQueueNode<T> extends MpscLinkedQueueNode<T> {
  private final Recycler.Handle handle;
  
  protected RecyclableMpscLinkedQueueNode(Recycler.Handle handle) {
    if (handle == null)
      throw new NullPointerException("handle"); 
    this.handle = handle;
  }
  
  final void unlink() {
    super.unlink();
    recycle(this.handle);
  }
  
  protected abstract void recycle(Recycler.Handle paramHandle);
}
