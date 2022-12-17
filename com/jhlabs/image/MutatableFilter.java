package com.jhlabs.image;

import java.awt.image.BufferedImageOp;

public interface MutatableFilter {
  void mutate(float paramFloat, BufferedImageOp paramBufferedImageOp, boolean paramBoolean1, boolean paramBoolean2);
}
