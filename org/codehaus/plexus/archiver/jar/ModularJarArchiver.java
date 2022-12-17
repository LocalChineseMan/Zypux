package org.codehaus.plexus.archiver.jar;

public abstract class ModularJarArchiver extends JarArchiver {
  private String moduleMainClass;
  
  private String manifestMainClass;
  
  private String moduleVersion;
  
  public String getModuleMainClass() {
    return this.moduleMainClass;
  }
  
  public void setModuleMainClass(String moduleMainClass) {
    this.moduleMainClass = moduleMainClass;
  }
  
  public String getModuleVersion() {
    return this.moduleVersion;
  }
  
  public void setModuleVersion(String moduleVersion) {
    this.moduleVersion = moduleVersion;
  }
  
  protected String getManifestMainClass() {
    return this.manifestMainClass;
  }
  
  protected Manifest createManifest() {
    Manifest manifest = super.createManifest();
    if (manifest != null)
      this
        .manifestMainClass = manifest.getMainAttributes().getValue("Main-Class"); 
    return manifest;
  }
  
  public void reset() {
    this.manifestMainClass = null;
    super.reset();
  }
}
