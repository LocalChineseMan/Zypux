package org.lwjgl.opengl;

public final class ARBTransformFeedbackInstanced {
  public static void glDrawTransformFeedbackInstanced(int mode, int id, int primcount) {
    GL42.glDrawTransformFeedbackInstanced(mode, id, primcount);
  }
  
  public static void glDrawTransformFeedbackStreamInstanced(int mode, int id, int stream, int primcount) {
    GL42.glDrawTransformFeedbackStreamInstanced(mode, id, stream, primcount);
  }
}
