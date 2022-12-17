package org.lwjgl.opengl;

import java.awt.Canvas;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import org.lwjgl.LWJGLException;

interface AWTCanvasImplementation {
  PeerInfo createPeerInfo(Canvas paramCanvas, PixelFormat paramPixelFormat, ContextAttribs paramContextAttribs) throws LWJGLException;
  
  GraphicsConfiguration findConfiguration(GraphicsDevice paramGraphicsDevice, PixelFormat paramPixelFormat) throws LWJGLException;
}
