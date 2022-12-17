package ir.lecer.uwu.enums;

import ir.lecer.uwu.impl.hud.Crosshair;
import ir.lecer.uwu.impl.hud.Hotbar;
import ir.lecer.uwu.impl.hud.Keystroke;
import ir.lecer.uwu.impl.hud.ModuleList;
import ir.lecer.uwu.impl.hud.Notifications;
import ir.lecer.uwu.impl.hud.Scoreboard;
import ir.lecer.uwu.impl.hud.UIDesigner;
import ir.lecer.uwu.impl.hud.Watermark;
import ir.lecer.uwu.impl.movement.Fly;
import ir.lecer.uwu.impl.movement.Speed;
import ir.lecer.uwu.impl.movement.Sprint;
import ir.lecer.uwu.impl.render.Blur;
import ir.lecer.uwu.impl.render.ClickGUI;
import ir.lecer.uwu.impl.render.Performance;
import ir.lecer.uwu.interfaces.Module;
import java.awt.Color;
import net.minecraft.util.ResourceLocation;

public enum Category {
  COMBAT("Combat", "for pvp and attacks", new Color(16711680), new ResourceLocation("zypux/textures/clickgui/combat.png"), new Module[0]),
  MOVEMENT("Movement", "Entity movements", new Color(-26368), new ResourceLocation("zypux/textures/clickgui/movement.png"), new Module[] { (Module)new Sprint(), (Module)new Speed(), (Module)new Fly() }),
  PLAYER("Player", "Healing, Utilities and more", new Color(16252672), new ResourceLocation("zypux/textures/clickgui/player.png"), new Module[0]),
  WORLD("World", "Blocks or breaking and placing", new Color(65312), new ResourceLocation("zypux/textures/clickgui/world.png"), new Module[0]),
  EXPLOITS("Exploits", "Bugs, crashers and disablers", new Color(53503), new ResourceLocation("zypux/textures/clickgui/exploits.png"), new Module[0]),
  RENDER("Render", "3D Rendering", new Color(5631), new ResourceLocation("zypux/textures/clickgui/render.png"), new Module[] { (Module)new ClickGUI(), (Module)new Performance(), (Module)new Blur() }),
  HUD("Hud", "2D Rendering", new Color(15073535), new ResourceLocation("zypux/textures/clickgui/hud.png"), new Module[] { (Module)new Watermark(), (Module)new ModuleList(), (Module)new UIDesigner(), (Module)new Scoreboard(), (Module)new Hotbar(), (Module)new Keystroke(), (Module)new Notifications(), (Module)new Crosshair() });
  
  private final String name;
  
  private final String desc;
  
  private final Color color;
  
  private final ResourceLocation logo;
  
  private final Module[] modules;
  
  Category(String name, String desc, Color color, ResourceLocation logo, Module... modules) {
    this.name = name;
    this.desc = desc;
    this.color = color;
    this.logo = logo;
    this.modules = modules;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDesc() {
    return this.desc;
  }
  
  public Color getColor() {
    return this.color;
  }
  
  public ResourceLocation getLogo() {
    return this.logo;
  }
  
  public Module[] getModules() {
    return this.modules;
  }
}
