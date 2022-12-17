package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class PRINTER_INFO_4 extends Structure {
  public String pPrinterName;
  
  public String pServerName;
  
  public WinDef.DWORD Attributes;
  
  public PRINTER_INFO_4() {}
  
  public PRINTER_INFO_4(int size) {
    super((Pointer)new Memory(size));
  }
}
