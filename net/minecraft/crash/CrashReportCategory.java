package net.minecraft.crash;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;

final class null implements Callable<String> {
  public String call() throws Exception {
    try {
      return String.format("ID #%d (%s // %s)", new Object[] { Integer.valueOf(this.val$i), this.val$blockIn.getUnlocalizedName(), this.val$blockIn.getClass().getCanonicalName() });
    } catch (Throwable var2) {
      return "ID #" + i;
    } 
  }
}
