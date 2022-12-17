package com.sun.jna.platform.win32;

import com.sun.jna.Structure;

public class GUITHREADINFO extends Structure {
  public int cbSize = size();
  
  public int flags;
  
  public WinDef.HWND hwndActive;
  
  public WinDef.HWND hwndFocus;
  
  public WinDef.HWND hwndCapture;
  
  public WinDef.HWND hwndMenuOwner;
  
  public WinDef.HWND hwndMoveSize;
  
  public WinDef.HWND hwndCaret;
  
  public WinDef.RECT rcCaret;
}
