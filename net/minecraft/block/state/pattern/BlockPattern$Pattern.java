package net.minecraft.block.state.pattern;

import com.google.common.base.Objects;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PatternHelper {
  private final BlockPos pos;
  
  private final EnumFacing finger;
  
  private final EnumFacing thumb;
  
  private final LoadingCache<BlockPos, BlockWorldState> lcache;
  
  private final int field_181120_e;
  
  private final int field_181121_f;
  
  private final int field_181122_g;
  
  public PatternHelper(BlockPos posIn, EnumFacing fingerIn, EnumFacing thumbIn, LoadingCache<BlockPos, BlockWorldState> lcacheIn, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_) {
    this.pos = posIn;
    this.finger = fingerIn;
    this.thumb = thumbIn;
    this.lcache = lcacheIn;
    this.field_181120_e = p_i46378_5_;
    this.field_181121_f = p_i46378_6_;
    this.field_181122_g = p_i46378_7_;
  }
  
  public BlockPos getPos() {
    return this.pos;
  }
  
  public EnumFacing getFinger() {
    return this.finger;
  }
  
  public EnumFacing getThumb() {
    return this.thumb;
  }
  
  public int func_181118_d() {
    return this.field_181120_e;
  }
  
  public int func_181119_e() {
    return this.field_181121_f;
  }
  
  public BlockWorldState translateOffset(int palmOffset, int thumbOffset, int fingerOffset) {
    return (BlockWorldState)this.lcache.getUnchecked(BlockPattern.translateOffset(this.pos, getFinger(), getThumb(), palmOffset, thumbOffset, fingerOffset));
  }
  
  public String toString() {
    return Objects.toStringHelper(this).add("up", this.thumb).add("forwards", this.finger).add("frontTopLeft", this.pos).toString();
  }
}
