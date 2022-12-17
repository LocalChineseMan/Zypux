package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;

class null implements Predicate<Entity> {
  public boolean apply(Entity p_apply_1_) {
    return (p_apply_1_ instanceof EntitySheep || p_apply_1_ instanceof EntityRabbit);
  }
}
