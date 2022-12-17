package oshi.hardware;

public interface Processor {
  String getVendor();
  
  void setVendor(String paramString);
  
  String getName();
  
  void setName(String paramString);
  
  String getIdentifier();
  
  void setIdentifier(String paramString);
  
  boolean isCpu64bit();
  
  void setCpu64(boolean paramBoolean);
  
  String getStepping();
  
  void setStepping(String paramString);
  
  String getModel();
  
  void setModel(String paramString);
  
  String getFamily();
  
  void setFamily(String paramString);
  
  float getLoad();
}
