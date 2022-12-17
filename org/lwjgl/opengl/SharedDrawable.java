package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;

public final class SharedDrawable extends DrawableGL {
  public SharedDrawable(Drawable drawable) throws LWJGLException {
    this.context = (ContextGL)((DrawableLWJGL)drawable).createSharedContext();
  }
  
  public ContextGL createSharedContext() {
    throw new UnsupportedOperationException();
  }
}
