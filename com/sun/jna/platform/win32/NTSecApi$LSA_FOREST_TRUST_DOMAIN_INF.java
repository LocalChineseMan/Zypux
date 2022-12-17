package com.sun.jna.platform.win32;

import com.sun.jna.Structure;

public class LSA_FOREST_TRUST_DOMAIN_INFO extends Structure {
  public WinNT.PSID.ByReference Sid;
  
  public NTSecApi.LSA_UNICODE_STRING DnsName;
  
  public NTSecApi.LSA_UNICODE_STRING NetbiosName;
}
