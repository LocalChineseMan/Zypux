package com.sun.jna.platform.win32;

public class DomainController {
  public String name;
  
  public String address;
  
  public int addressType;
  
  public Guid.GUID domainGuid;
  
  public String domainName;
  
  public String dnsForestName;
  
  public int flags;
  
  public String clientSiteName;
  
  public static class Netapi32Util {}
}
