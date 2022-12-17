package net.minecraft.client.gui;

import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.storage.SaveFormatComparator;
import org.apache.commons.lang3.StringUtils;

class List extends GuiSlot {
  public List(Minecraft mcIn) {
    super(mcIn, GuiSelectWorld.width, GuiSelectWorld.height, 32, GuiSelectWorld.height - 64, 36);
  }
  
  protected int getSize() {
    return GuiSelectWorld.access$000(GuiSelectWorld.this).size();
  }
  
  protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
    GuiSelectWorld.access$102(GuiSelectWorld.this, slotIndex);
    boolean flag = (GuiSelectWorld.access$100(GuiSelectWorld.this) >= 0 && GuiSelectWorld.access$100(GuiSelectWorld.this) < getSize());
    (GuiSelectWorld.access$200(GuiSelectWorld.this)).enabled = flag;
    (GuiSelectWorld.access$300(GuiSelectWorld.this)).enabled = flag;
    (GuiSelectWorld.access$400(GuiSelectWorld.this)).enabled = flag;
    (GuiSelectWorld.access$500(GuiSelectWorld.this)).enabled = flag;
    if (isDoubleClick && flag)
      GuiSelectWorld.this.func_146615_e(slotIndex); 
  }
  
  protected boolean isSelected(int slotIndex) {
    return (slotIndex == GuiSelectWorld.access$100(GuiSelectWorld.this));
  }
  
  protected int getContentHeight() {
    return GuiSelectWorld.access$000(GuiSelectWorld.this).size() * 36;
  }
  
  protected void drawBackground() {
    GuiSelectWorld.this.drawDefaultBackground();
  }
  
  protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
    SaveFormatComparator saveformatcomparator = GuiSelectWorld.access$000(GuiSelectWorld.this).get(entryID);
    String s = saveformatcomparator.getDisplayName();
    if (StringUtils.isEmpty(s))
      s = GuiSelectWorld.access$600(GuiSelectWorld.this) + " " + (entryID + 1); 
    String s1 = saveformatcomparator.getFileName();
    s1 = s1 + " (" + GuiSelectWorld.access$700(GuiSelectWorld.this).format(new Date(saveformatcomparator.getLastTimePlayed()));
    s1 = s1 + ")";
    String s2 = "";
    if (saveformatcomparator.requiresConversion()) {
      s2 = GuiSelectWorld.access$800(GuiSelectWorld.this) + " " + s2;
    } else {
      s2 = GuiSelectWorld.access$900(GuiSelectWorld.this)[saveformatcomparator.getEnumGameType().getID()];
      if (saveformatcomparator.isHardcoreModeEnabled())
        s2 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET; 
      if (saveformatcomparator.getCheatsEnabled())
        s2 = s2 + ", " + I18n.format("selectWorld.cheats", new Object[0]); 
    } 
    GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s, p_180791_2_ + 2, p_180791_3_ + 1, 16777215);
    GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s1, p_180791_2_ + 2, p_180791_3_ + 12, 8421504);
    GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s2, p_180791_2_ + 2, p_180791_3_ + 12 + 10, 8421504);
  }
  
  class GuiSelectWorld {}
}
