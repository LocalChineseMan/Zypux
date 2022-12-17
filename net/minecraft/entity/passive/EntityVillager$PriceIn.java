package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.util.Tuple;

class PriceInfo extends Tuple<Integer, Integer> {
  public PriceInfo(int p_i45810_1_, int p_i45810_2_) {
    super(Integer.valueOf(p_i45810_1_), Integer.valueOf(p_i45810_2_));
  }
  
  public int getPrice(Random rand) {
    return (((Integer)getFirst()).intValue() >= ((Integer)getSecond()).intValue()) ? ((Integer)getFirst()).intValue() : (((Integer)getFirst()).intValue() + rand.nextInt(((Integer)getSecond()).intValue() - ((Integer)getFirst()).intValue() + 1));
  }
}
