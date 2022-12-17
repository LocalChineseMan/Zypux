package org.lwjgl.opengl;

public interface Handler {
  void handleMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString);
  
  public static interface ARBDebugOutputCallback {}
}
