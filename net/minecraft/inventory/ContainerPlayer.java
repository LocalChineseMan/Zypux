package net.minecraft.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

class null extends Slot {
  null(IInventory inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }
  
  public int getSlotStackLimit() {
    return 1;
  }
  
  public boolean isItemValid(ItemStack stack) {
    return (stack == null) ? false : ((stack.getItem() instanceof ItemArmor) ? ((((ItemArmor)stack.getItem()).armorType == k_f)) : ((stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull) ? false : ((k_f == 0))));
  }
  
  public String getSlotTexture() {
    return ItemArmor.EMPTY_SLOT_NAMES[k_f];
  }
}
