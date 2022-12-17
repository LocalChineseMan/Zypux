package org.lwjgl;

public abstract class PointerWrapperAbstract implements PointerWrapper {
  protected final long pointer;
  
  protected PointerWrapperAbstract(long pointer) {
    this.pointer = pointer;
  }
  
  public boolean isValid() {
    return (this.pointer != 0L);
  }
  
  public final void checkValid() {
    if (LWJGLUtil.DEBUG && !isValid())
      throw new IllegalStateException("This " + getClass().getSimpleName() + " pointer is not valid."); 
  }
  
  public final long getPointer() {
    checkValid();
    return this.pointer;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof PointerWrapperAbstract))
      return false; 
    PointerWrapperAbstract that = (PointerWrapperAbstract)o;
    if (this.pointer != that.pointer)
      return false; 
    return true;
  }
  
  public int hashCode() {
    return (int)(this.pointer ^ this.pointer >>> 32L);
  }
  
  public String toString() {
    return getClass().getSimpleName() + " pointer (0x" + Long.toHexString(this.pointer).toUpperCase() + ")";
  }
}
