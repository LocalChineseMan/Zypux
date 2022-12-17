package ir.lecer.uwu.interfaces;

import ir.lecer.uwu.enums.Category;

public class IModuleList {
  private final Module module;
  
  private final String title;
  
  private final Category category;
  
  public IModuleList(Module module, String title, Category category) {
    this.module = module;
    this.title = title;
    this.category = category;
  }
  
  public Module getModule() {
    return this.module;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public Category getCategory() {
    return this.category;
  }
}
