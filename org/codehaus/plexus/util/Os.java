package org.codehaus.plexus.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Os {
  public static final java.lang.String FAMILY_DOS = "dos";
  
  public static final java.lang.String FAMILY_MAC = "mac";
  
  public static final java.lang.String FAMILY_NETWARE = "netware";
  
  public static final java.lang.String FAMILY_OS2 = "os/2";
  
  public static final java.lang.String FAMILY_TANDEM = "tandem";
  
  public static final java.lang.String FAMILY_UNIX = "unix";
  
  public static final java.lang.String FAMILY_WINDOWS = "windows";
  
  public static final java.lang.String FAMILY_WIN9X = "win9x";
  
  public static final java.lang.String FAMILY_ZOS = "z/os";
  
  public static final java.lang.String FAMILY_OS400 = "os/400";
  
  public static final java.lang.String FAMILY_OPENVMS = "openvms";
  
  private static final Set<java.lang.String> validFamilies = setValidFamilies();
  
  private static final java.lang.String PATH_SEP = System.getProperty("path.separator");
  
  public static final java.lang.String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
  
  public static final java.lang.String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
  
  public static final java.lang.String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);
  
  public static final java.lang.String OS_FAMILY = getOsFamily();
  
  private java.lang.String family;
  
  private java.lang.String name;
  
  private java.lang.String version;
  
  private java.lang.String arch;
  
  public Os() {}
  
  public Os(java.lang.String family) {
    setFamily(family);
  }
  
  private static Set<java.lang.String> setValidFamilies() {
    Set<java.lang.String> valid = new HashSet<>();
    valid.add("dos");
    valid.add("mac");
    valid.add("netware");
    valid.add("os/2");
    valid.add("tandem");
    valid.add("unix");
    valid.add("windows");
    valid.add("win9x");
    valid.add("z/os");
    valid.add("os/400");
    valid.add("openvms");
    return valid;
  }
  
  public void setFamily(java.lang.String f) {
    this.family = f.toLowerCase(Locale.US);
  }
  
  public void setName(java.lang.String name) {
    this.name = name.toLowerCase(Locale.US);
  }
  
  public void setArch(java.lang.String arch) {
    this.arch = arch.toLowerCase(Locale.US);
  }
  
  public void setVersion(java.lang.String version) {
    this.version = version.toLowerCase(Locale.US);
  }
  
  public boolean eval() throws Exception {
    return isOs(this.family, this.name, this.arch, this.version);
  }
  
  public static boolean isFamily(java.lang.String family) {
    return isOs(family, null, null, null);
  }
  
  public static boolean isName(java.lang.String name) {
    return isOs(null, name, null, null);
  }
  
  public static boolean isArch(java.lang.String arch) {
    return isOs(null, null, arch, null);
  }
  
  public static boolean isVersion(java.lang.String version) {
    return isOs(null, null, null, version);
  }
  
  public static boolean isOs(java.lang.String family, java.lang.String name, java.lang.String arch, java.lang.String version) {
    boolean retValue = false;
    if (family != null || name != null || arch != null || version != null) {
      boolean isFamily = true;
      boolean isName = true;
      boolean isArch = true;
      boolean isVersion = true;
      if (family != null)
        if (family.equalsIgnoreCase("windows")) {
          isFamily = OS_NAME.contains("windows");
        } else if (family.equalsIgnoreCase("os/2")) {
          isFamily = OS_NAME.contains("os/2");
        } else if (family.equalsIgnoreCase("netware")) {
          isFamily = OS_NAME.contains("netware");
        } else if (family.equalsIgnoreCase("dos")) {
          isFamily = (PATH_SEP.equals(";") && !isFamily("netware") && !isFamily("windows") && !isFamily("win9x"));
        } else if (family.equalsIgnoreCase("mac")) {
          isFamily = OS_NAME.contains("mac");
        } else if (family.equalsIgnoreCase("tandem")) {
          isFamily = OS_NAME.contains("nonstop_kernel");
        } else if (family.equalsIgnoreCase("unix")) {
          isFamily = (PATH_SEP.equals(":") && !isFamily("openvms") && (!isFamily("mac") || OS_NAME.endsWith("x")));
        } else if (family.equalsIgnoreCase("win9x")) {
          isFamily = (isFamily("windows") && (OS_NAME.contains("95") || OS_NAME.contains("98") || OS_NAME.contains("me") || OS_NAME.contains("ce")));
        } else if (family.equalsIgnoreCase("z/os")) {
          isFamily = (OS_NAME.contains("z/os") || OS_NAME.contains("os/390"));
        } else if (family.equalsIgnoreCase("os/400")) {
          isFamily = OS_NAME.contains("os/400");
        } else if (family.equalsIgnoreCase("openvms")) {
          isFamily = OS_NAME.contains("openvms");
        } else {
          isFamily = OS_NAME.contains(family.toLowerCase(Locale.US));
        }  
      if (name != null)
        isName = name.toLowerCase(Locale.US).equals(OS_NAME); 
      if (arch != null)
        isArch = arch.toLowerCase(Locale.US).equals(OS_ARCH); 
      if (version != null)
        isVersion = version.toLowerCase(Locale.US).equals(OS_VERSION); 
      retValue = (isFamily && isName && isArch && isVersion);
    } 
    return retValue;
  }
  
  private static java.lang.String getOsFamily() {
    Set<java.lang.String> families = null;
    if (!validFamilies.isEmpty()) {
      families = validFamilies;
    } else {
      families = setValidFamilies();
    } 
    for (java.lang.String fam : families) {
      if (isFamily(fam))
        return fam; 
    } 
    return null;
  }
  
  public static boolean isValidFamily(java.lang.String theFamily) {
    return validFamilies.contains(theFamily);
  }
  
  public static Set<java.lang.String> getValidFamilies() {
    return new HashSet<>(validFamilies);
  }
}
