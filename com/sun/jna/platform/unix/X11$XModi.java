package com.sun.jna.platform.unix;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class XModifierKeymapRef extends Structure implements Structure.ByReference {
  public int max_keypermod;
  
  public Pointer modifiermap;
}
