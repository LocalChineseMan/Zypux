package com.sun.jna.platform.dnd;

import java.awt.Point;
import java.awt.dnd.DropTargetEvent;

public interface DropTargetPainter {
  void paintDropTarget(DropTargetEvent paramDropTargetEvent, int paramInt, Point paramPoint);
}
