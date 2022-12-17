package ir.lecer.uwu.ui.clickgui;

import ir.lecer.uwu.Zypux;
import ir.lecer.uwu.enums.Category;
import ir.lecer.uwu.events.events.ClickGUICloseEvent;
import ir.lecer.uwu.events.events.ClickGUIOpenEvent;
import ir.lecer.uwu.events.events.PreClickGUIRenderEvent;
import ir.lecer.uwu.interfaces.Module;
import ir.lecer.uwu.tools.font.FontUtils;
import ir.lecer.uwu.tools.renders.ColorUtils;
import ir.lecer.uwu.tools.renders.RenderUtils;
import ir.lecer.uwu.tools.tasks.TaskManager;
import ir.lecer.uwu.ui.clickgui.elements.Element;
import ir.lecer.uwu.ui.clickgui.elements.ModuleButton;
import ir.lecer.uwu.ui.clickgui.elements.menu.ElementSlider;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ClickGUIRenderer extends GuiScreen {
  private final ResourceLocation hudEditorImage = new ResourceLocation("zypux/textures/clickgui/hudeditor.png");
  
  private final ResourceLocation loadingImage = new ResourceLocation("zypux/textures/clickgui/loading.png");
  
  public boolean opened = true;
  
  public static ArrayList<Panel> panels = new ArrayList<>();
  
  private ModuleButton mb = null;
  
  public SettingsManager settingsManager;
  
  public static String underlineChar;
  
  public ClickGUIRenderer() {
    this.settingsManager = (Zypux.getInstance()).settingsManager;
    playAnimation();
    double pwidth = 120.0D;
    double pheight = 14.0D;
    double px = 10.0D;
    double py = 10.0D;
    double pxplus = 130.0D;
    for (Category category : Category.values()) {
      Panel panel = new Panel(category.getName(), (Enum<Category>)category, px, py, 120.0D, 14.0D, false, this);
      panels.add(panel);
      for (Module module : category.getModules())
        panel.Elements.add(new ModuleButton(module, panel)); 
      px += 130.0D;
    } 
    Collections.reverse(panels);
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    ScaledResolution resolution = new ScaledResolution(this.mc);
    for (Module module : Category.RENDER.getModules()) {
      if (module.getName().equalsIgnoreCase("clickgui")) {
        String backgroundMode = (Zypux.getInstance()).settingsManager.getSettingByName("Background Mode").getStringValue().toLowerCase();
        Color bgac = (Zypux.getInstance()).settingsManager.getSettingByName("Background A Color").getColor();
        Color bgbc = (Zypux.getInstance()).settingsManager.getSettingByName("Background B Color").getColor();
        switch (backgroundMode) {
          case "none":
            RenderUtils.rect(0.0F, 0.0F, resolution.getScaledWidth(), resolution.getScaledHeight(), bgac.getRGB());
            break;
          case "vergradient":
            RenderUtils.drawGradientRect(0.0D, 0.0D, resolution.getScaledWidth(), resolution.getScaledHeight(), bgac.getRGB(), bgbc.getRGB());
            break;
          case "horgradient":
            RenderUtils.drawGradientHRect(0.0F, 0.0F, resolution.getScaledWidth(), resolution.getScaledHeight(), bgac.getRGB(), bgbc.getRGB());
            break;
        } 
      } 
    } 
    PreClickGUIRenderEvent preClickGUIRenderEvent = new PreClickGUIRenderEvent(mouseX, mouseY);
    preClickGUIRenderEvent.call();
    if (Mouse.hasWheel()) {
      int wheel = Mouse.getDWheel();
      boolean handledScroll = false;
      if (!handledScroll)
        handleScroll(wheel); 
    } 
    for (Panel panel : panels) {
      panel.drawScreen(mouseX, mouseY);
      for (Category category : Category.values()) {
        if (panel.getCategory().equals(category))
          RenderUtils.image(category.getLogo(), panel.x + panel.width - 13.0D, panel.y + 1.0D, 12.0D, 12.0D, 255.0F); 
      } 
    } 
    RenderUtils.borderedCircle(25, resolution.getScaledHeight() - 25, 20.0F, (new Color(3583))
        .getRGB(), ColorUtils.getClickGUIBorderColor);
    RenderUtils.image(this.hudEditorImage, 10, resolution.getScaledHeight() - 40, 30, 30, 255.0F);
    this.mb = null;
    label91: for (Panel panel : panels) {
      if (panel != null && panel.visible && panel.extended && panel.Elements != null && panel.Elements.size() > 0)
        for (ModuleButton button : panel.Elements) {
          if (button.listening) {
            this.mb = button;
            break label91;
          } 
        }  
    } 
    for (Panel panel : panels) {
      if (panel.extended && panel.visible && panel.Elements != null)
        for (ModuleButton button : panel.Elements) {
          if (button.extended && button.menuElements != null && !button.menuElements.isEmpty()) {
            double off = 0.0D;
            for (Element element : button.menuElements) {
              element.offset = off;
              element.update();
              RenderUtils.rect(element.x, element.y, element.x + element.width + 2.0D, element.y + element.height, (new Color(1493172224, true)).getRGB());
              element.drawScreen(mouseX, mouseY, partialTicks);
              off += element.height;
            } 
            Element firstElement = button.menuElements.get(0);
            Element lastElement = button.menuElements.get(button.menuElements.size() - 1);
            RenderUtils.rect((int)firstElement.x, (int)(firstElement.y - 1.0D), (int)(firstElement.x + firstElement.width + 1.0D), (int)firstElement.y, ColorUtils.getClickGUIBorderColor);
            RenderUtils.rect((int)lastElement.x, (int)(lastElement.y + lastElement.height - 1.0D), (int)(lastElement.x + lastElement.width + 1.0D), (int)(lastElement.y + lastElement.height), ColorUtils.getClickGUIBorderColor);
            RenderUtils.rect((int)(firstElement.x - 1.0D), (int)(firstElement.y - 1.0D), (int)lastElement.x, (int)(lastElement.y + lastElement.height), ColorUtils.getClickGUIBorderColor);
            RenderUtils.rect((int)(firstElement.x + firstElement.width + 1.0D), (int)(firstElement.y - 1.0D), (int)(lastElement.x + lastElement.width + 2.0D), (int)(lastElement.y + lastElement.height), ColorUtils.getClickGUIBorderColor);
            RenderUtils.drawShadow((int)(firstElement.x - 1.0D), (int)(firstElement.y - 1.0D), (int)(lastElement.getWidth() + 2.0D), (int)(off + 1.0D), 9, true);
          } 
        }  
    } 
    if (this.mb != null) {
      RenderUtils.rect(0.0F, 0.0F, width, height, (new Color(-1090519040, true)).getRGB());
      GL11.glPushMatrix();
      GL11.glTranslatef(resolution.getScaledWidth() / 2.0F, resolution.getScaledHeight() / 2.0F, 0.0F);
      GL11.glScalef(3.0F, 3.0F, 0.0F);
      FontUtils.comfortaa_r.drawCenteredString("Listening...", 0, -10, -1, true);
      GL11.glScalef(0.5F, 0.5F, 0.0F);
      FontUtils.comfortaa_r.drawCenteredString("Press 'ESCAPE' to unbind " + this.mb.module.getName() + ((this.mb.module.getKey() > -1) ? (" (" + Keyboard.getKeyName(this.mb.module.getKey()) + ")") : ""), 0, 0, -1, true);
      GL11.glPopMatrix();
      RenderUtils.image(this.loadingImage, (resolution.getScaledWidth() / 2.0F + 140.0F), (resolution.getScaledHeight() / 2.0F - 30.0F), 48.0D, 48.0D, 255.0F);
    } 
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (this.opened) {
      Mouse.setGrabbed(false);
      this.opened = false;
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    try {
      if (this.mb != null)
        return; 
      ScaledResolution resolution = new ScaledResolution(this.mc);
      if (isHoveredConfig(mouseX, mouseY, resolution.getScaledHeight()) && 
        mouseButton == 0) {
        this.mc.displayGuiScreen(null);
        this.mc.displayGuiScreen((GuiScreen)Zypux.hudEditor);
      } 
      for (Panel panel : panels) {
        if (panel.extended && panel.visible && panel.Elements != null)
          for (ModuleButton button : panel.Elements) {
            if (button.extended)
              for (Element element : button.menuElements) {
                if (element.mouseClicked(mouseX, mouseY, mouseButton))
                  return; 
              }  
          }  
      } 
      for (Panel panel : panels) {
        if (panel.mouseClicked(mouseX, mouseY, mouseButton))
          return; 
      } 
      super.mouseClicked(mouseX, mouseY, mouseButton);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public void mouseReleased(int mouseX, int mouseY, int state) {
    if (this.mb != null)
      return; 
    for (Panel panel : panels) {
      if (panel.extended && panel.visible && panel.Elements != null)
        for (ModuleButton button : panel.Elements) {
          if (button.extended)
            for (Element element : button.menuElements)
              element.mouseReleased(mouseX, mouseY, state);  
        }  
    } 
    for (Panel panel : panels)
      panel.mouseReleased(state); 
    super.mouseReleased(mouseX, mouseY, state);
  }
  
  protected void keyTyped(char typedChar, int keyCode) {
    try {
      for (Panel panel : panels) {
        if (panel != null && panel.visible && panel.extended && panel.Elements != null && panel.Elements.size() > 0)
          for (ModuleButton element : panel.Elements) {
            if (element.keyTyped(keyCode))
              return; 
            if (element.extended)
              for (Element menuElement : element.menuElements)
                menuElement.keyTyped(typedChar, keyCode);  
          }  
      } 
      super.keyTyped(typedChar, keyCode);
    } catch (Throwable $ex) {
      throw $ex;
    } 
  }
  
  public void initGui() {
    if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof net.minecraft.entity.player.EntityPlayer) {
      if (this.mc.entityRenderer.theShaderGroup != null)
        this.mc.entityRenderer.theShaderGroup.deleteShaderGroup(); 
      this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    } 
    ClickGUIOpenEvent clickGUIOpenEvent = new ClickGUIOpenEvent();
    clickGUIOpenEvent.call();
  }
  
  public void onGuiClosed() {
    this.opened = true;
    if (this.mc.entityRenderer.theShaderGroup != null) {
      this.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
      this.mc.entityRenderer.theShaderGroup = null;
    } 
    for (Panel panel : panels) {
      if (panel.extended && panel.visible && panel.Elements != null)
        for (ModuleButton button : panel.Elements) {
          if (button.extended)
            for (Element element : button.menuElements) {
              if (element instanceof ElementSlider)
                ((ElementSlider)element).dragging = false; 
            }  
        }  
    } 
    ClickGUICloseEvent clickGUICloseEvent = new ClickGUICloseEvent();
    clickGUICloseEvent.call();
  }
  
  public static void playAnimation() {
    TaskManager.async(() -> {
          while (true) {
            try {
              while (true) {
                underlineChar = "";
                Thread.sleep(400L);
                underlineChar = "_";
                Thread.sleep(400L);
              } 
              break;
            } catch (Exception exception) {}
          } 
        });
  }
  
  private boolean isHoveredConfig(int mouseX, int mouseY, int y) {
    return (mouseX >= 8 && mouseX <= 40 && mouseY >= y - 40 && mouseY <= y - 10);
  }
  
  private void handleScroll(int wheel) {
    if (wheel == 0)
      return; 
    for (Panel panel : panels)
      panel.y += wheel / 4.0D; 
  }
}
