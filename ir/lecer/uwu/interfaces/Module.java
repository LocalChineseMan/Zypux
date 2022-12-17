package ir.lecer.uwu.interfaces;

import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.handler.EventManager;
import ir.lecer.uwu.features.notifications.NotificationHelper;
import ir.lecer.uwu.tools.renders.ColorUtils;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class Module {
  public void setMc(Minecraft mc) {
    this.mc = mc;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  public void setKey(int key) {
    this.key = key;
  }
  
  public void setCategory(Category category) {
    this.category = category;
  }
  
  public void setToggled(boolean toggled) {
    this.toggled = toggled;
  }
  
  public void setToggleable(boolean toggleable) {
    this.toggleable = toggleable;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Module))
      return false; 
    Module other = (Module)o;
    if (!other.canEqual(this))
      return false; 
    if (getKey() != other.getKey())
      return false; 
    if (isToggled() != other.isToggled())
      return false; 
    if (isToggleable() != other.isToggleable())
      return false; 
    Object this$mc = getMc(), other$mc = other.getMc();
    if ((this$mc == null) ? (other$mc != null) : !this$mc.equals(other$mc))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$displayName = getDisplayName(), other$displayName = other.getDisplayName();
    if ((this$displayName == null) ? (other$displayName != null) : !this$displayName.equals(other$displayName))
      return false; 
    Object this$category = getCategory(), other$category = other.getCategory();
    return !((this$category == null) ? (other$category != null) : !this$category.equals(other$category));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Module;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getKey();
    result = result * 59 + (isToggled() ? 79 : 97);
    result = result * 59 + (isToggleable() ? 79 : 97);
    Object $mc = getMc();
    result = result * 59 + (($mc == null) ? 43 : $mc.hashCode());
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $displayName = getDisplayName();
    result = result * 59 + (($displayName == null) ? 43 : $displayName.hashCode());
    Object $category = getCategory();
    return result * 59 + (($category == null) ? 43 : $category.hashCode());
  }
  
  public String toString() {
    return "Module(mc=" + getMc() + ", name=" + getName() + ", displayName=" + getDisplayName() + ", key=" + getKey() + ", category=" + getCategory() + ", toggled=" + isToggled() + ", toggleable=" + isToggleable() + ")";
  }
  
  public static Set<IModuleList> modulesArray = new HashSet<>();
  
  protected Minecraft mc = Minecraft.getMinecraft();
  
  private String name;
  
  private String displayName;
  
  private int key;
  
  private Category category;
  
  private boolean toggled;
  
  private boolean toggleable;
  
  public Minecraft getMc() {
    return this.mc;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }
  
  public int getKey() {
    return this.key;
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  public boolean isToggled() {
    return this.toggled;
  }
  
  public boolean isToggleable() {
    return this.toggleable;
  }
  
  public Module(String name, Category category, boolean toggleable) {
    this.name = name;
    this.category = category;
    this.toggleable = toggleable;
    this.toggled = false;
  }
  
  public String setting() {
    return null;
  }
  
  public void onSetup() {}
  
  public void onEnable() {
    if (!getName().equalsIgnoreCase("clickgui"))
      NotificationHelper.send("Module", String.format("%s enabled", new Object[] { getName() }), new Color(-14418176, true), new Color(-15955968, true), 0.42D); 
    this.toggled = true;
    addiModule();
    EventManager.register(this);
  }
  
  public void onDisable() {
    if (!getName().equalsIgnoreCase("clickgui"))
      NotificationHelper.send("Module", String.format("%s disabled", new Object[] { getName() }), new Color(-65536, true), new Color(-8323072, true), 0.42D); 
    this.toggled = false;
    modulesArray.removeIf(iModuleList -> iModuleList.getModule().equals(this));
    EventManager.unregister(this);
  }
  
  public void onToggle() {}
  
  public void setEnable(boolean enable) {
    if (enable && !this.toggled) {
      onEnable();
    } else if (!enable && this.toggled) {
      onDisable();
    } 
  }
  
  public void toggle() {
    this.toggled = !this.toggled;
    onToggle();
    if (this.toggled) {
      onEnable();
      return;
    } 
    onDisable();
  }
  
  private void addiModule() {
    for (Category globalCategory : Category.values()) {
      for (Module moduleC : globalCategory.getModules()) {
        if (moduleC.getName().equals(this.name))
          modulesArray.add((setting() == null) ? new IModuleList(this, this.name, globalCategory) : new IModuleList(this, ColorUtils.colorize(new String[] { String.format("&f%s &7(%s)", new Object[] { this.name, setting() }) }), globalCategory)); 
      } 
    } 
  }
  
  public static void setupModules() {
    for (Category category : Category.values()) {
      for (Module module : category.getModules())
        module.onSetup(); 
    } 
  }
}
