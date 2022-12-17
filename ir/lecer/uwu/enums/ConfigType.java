package ir.lecer.uwu.enums;

public enum ConfigType {
  MODULES("zypuxclient/configModules.txt"),
  RENDERS("zypuxclient/configRenders.txt"),
  BINDS("zypuxclient/configBindings.txt"),
  SETTINGS("zypuxclient/clientSettings.txt"),
  HUD("zypuxclient/configHuds.txt"),
  CUSTOM(null);
  
  ConfigType(String path) {
    this.path = path;
  }
  
  private final String path;
  
  public String getPath() {
    return this.path;
  }
}
