package org.apache.maven.archiver;

public class ManifestConfiguration {
  public static final String CLASSPATH_LAYOUT_TYPE_SIMPLE = "simple";
  
  public static final String CLASSPATH_LAYOUT_TYPE_REPOSITORY = "repository";
  
  public static final String CLASSPATH_LAYOUT_TYPE_CUSTOM = "custom";
  
  private String mainClass;
  
  private String packageName;
  
  private boolean addClasspath;
  
  private boolean addExtensions;
  
  private String classpathPrefix = "";
  
  private boolean addDefaultEntries = true;
  
  private boolean addBuildEnvironmentEntries;
  
  private boolean addDefaultSpecificationEntries;
  
  private boolean addDefaultImplementationEntries;
  
  private String classpathLayoutType = "simple";
  
  private String customClasspathLayout;
  
  private boolean useUniqueVersions = true;
  
  public String getMainClass() {
    return this.mainClass;
  }
  
  public String getPackageName() {
    return this.packageName;
  }
  
  public boolean isAddClasspath() {
    return this.addClasspath;
  }
  
  public boolean isAddDefaultEntries() {
    return this.addDefaultEntries;
  }
  
  public boolean isAddBuildEnvironmentEntries() {
    return this.addBuildEnvironmentEntries;
  }
  
  public boolean isAddDefaultImplementationEntries() {
    return this.addDefaultImplementationEntries;
  }
  
  public boolean isAddDefaultSpecificationEntries() {
    return this.addDefaultSpecificationEntries;
  }
  
  public boolean isAddExtensions() {
    return this.addExtensions;
  }
  
  public void setAddClasspath(boolean addClasspath) {
    this.addClasspath = addClasspath;
  }
  
  public void setAddDefaultEntries(boolean addDefaultEntries) {
    this.addDefaultEntries = addDefaultEntries;
  }
  
  public void setAddBuildEnvironmentEntries(boolean addBuildEnvironmentEntries) {
    this.addBuildEnvironmentEntries = addBuildEnvironmentEntries;
  }
  
  public void setAddDefaultImplementationEntries(boolean addDefaultImplementationEntries) {
    this.addDefaultImplementationEntries = addDefaultImplementationEntries;
  }
  
  public void setAddDefaultSpecificationEntries(boolean addDefaultSpecificationEntries) {
    this.addDefaultSpecificationEntries = addDefaultSpecificationEntries;
  }
  
  public void setAddExtensions(boolean addExtensions) {
    this.addExtensions = addExtensions;
  }
  
  public void setClasspathPrefix(String classpathPrefix) {
    this.classpathPrefix = classpathPrefix;
  }
  
  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }
  
  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }
  
  public String getClasspathPrefix() {
    String cpp = this.classpathPrefix.replaceAll("\\\\", "/");
    if (cpp.length() != 0 && !cpp.endsWith("/"))
      cpp = cpp + "/"; 
    return cpp;
  }
  
  public String getClasspathLayoutType() {
    return this.classpathLayoutType;
  }
  
  public void setClasspathLayoutType(String classpathLayoutType) {
    this.classpathLayoutType = classpathLayoutType;
  }
  
  public String getCustomClasspathLayout() {
    return this.customClasspathLayout;
  }
  
  public void setCustomClasspathLayout(String customClasspathLayout) {
    this.customClasspathLayout = customClasspathLayout;
  }
  
  public boolean isUseUniqueVersions() {
    return this.useUniqueVersions;
  }
  
  public void setUseUniqueVersions(boolean useUniqueVersions) {
    this.useUniqueVersions = useUniqueVersions;
  }
}
