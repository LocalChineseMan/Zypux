package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

class null implements Predicate<Entity> {
  public boolean apply(Entity p_apply_1_) {
    if (!(p_apply_1_ instanceof EntityPlayer))
      return false; 
    if (((EntityPlayer)p_apply_1_).capabilities.disableDamage)
      return false; 
    double d0 = EntityAIFindEntityNearestPlayer.this.maxTargetRange();
    if (p_apply_1_.isSneaking())
      d0 *= 0.800000011920929D; 
    if (p_apply_1_.isInvisible()) {
      float f = ((EntityPlayer)p_apply_1_).getArmorVisibility();
      if (f < 0.1F)
        f = 0.1F; 
      d0 *= (0.7F * f);
    } 
    return (p_apply_1_.getDistanceToEntity((Entity)EntityAIFindEntityNearestPlayer.access$000(EntityAIFindEntityNearestPlayer.this)) > d0) ? false : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestPlayer.access$000(EntityAIFindEntityNearestPlayer.this), (EntityLivingBase)p_apply_1_, false, true);
  }
}
