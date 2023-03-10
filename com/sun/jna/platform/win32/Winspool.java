package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Winspool extends StdCallLibrary {
  public static final Winspool INSTANCE = (Winspool)Native.loadLibrary("Winspool.drv", Winspool.class, W32APIOptions.UNICODE_OPTIONS);
  
  public static final int PRINTER_ENUM_DEFAULT = 1;
  
  public static final int PRINTER_ENUM_LOCAL = 2;
  
  public static final int PRINTER_ENUM_CONNECTIONS = 4;
  
  public static final int PRINTER_ENUM_FAVORITE = 4;
  
  public static final int PRINTER_ENUM_NAME = 8;
  
  public static final int PRINTER_ENUM_REMOTE = 16;
  
  public static final int PRINTER_ENUM_SHARED = 32;
  
  public static final int PRINTER_ENUM_NETWORK = 64;
  
  public static final int PRINTER_ENUM_EXPAND = 16384;
  
  public static final int PRINTER_ENUM_CONTAINER = 32768;
  
  public static final int PRINTER_ENUM_ICONMASK = 16711680;
  
  public static final int PRINTER_ENUM_ICON1 = 65536;
  
  public static final int PRINTER_ENUM_ICON2 = 131072;
  
  public static final int PRINTER_ENUM_ICON3 = 262144;
  
  public static final int PRINTER_ENUM_ICON4 = 524288;
  
  public static final int PRINTER_ENUM_ICON5 = 1048576;
  
  public static final int PRINTER_ENUM_ICON6 = 2097152;
  
  public static final int PRINTER_ENUM_ICON7 = 4194304;
  
  public static final int PRINTER_ENUM_ICON8 = 8388608;
  
  public static final int PRINTER_ENUM_HIDE = 16777216;
  
  boolean EnumPrinters(int paramInt1, String paramString, int paramInt2, Pointer paramPointer, int paramInt3, IntByReference paramIntByReference1, IntByReference paramIntByReference2);
  
  public static class Winspool {}
  
  public static class Winspool {}
}
