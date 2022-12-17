package net.minecraft.entity.ai;

import java.util.Comparator;
import net.minecraft.entity.Entity;

public class Sorter implements Comparator<Entity> {
  private final Entity theEntity;
  
  public Sorter(Entity theEntityIn) {
    this.theEntity = theEntityIn;
  }
  
  public int compare(Entity p_compare_1_, Entity p_compare_2_) {
    double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
    double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
    return (d0 < d1) ? -1 : ((d0 > d1) ? 1 : 0);
  }
  
  public static class EntityAINearestAttackableTarget {}
}
