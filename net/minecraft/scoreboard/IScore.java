package net.minecraft.scoreboard;

import com.google.common.collect.Maps;
import java.util.Map;

public enum EnumRenderType {
  INTEGER("integer"),
  HEARTS("hearts");
  
  private static final Map<String, EnumRenderType> field_178801_c;
  
  private final String field_178798_d;
  
  static {
    field_178801_c = Maps.newHashMap();
    for (EnumRenderType iscoreobjectivecriteria$enumrendertype : values())
      field_178801_c.put(iscoreobjectivecriteria$enumrendertype.func_178796_a(), iscoreobjectivecriteria$enumrendertype); 
  }
  
  EnumRenderType(String p_i45548_3_) {
    this.field_178798_d = p_i45548_3_;
  }
  
  public String func_178796_a() {
    return this.field_178798_d;
  }
  
  public static EnumRenderType func_178795_a(String p_178795_0_) {
    EnumRenderType iscoreobjectivecriteria$enumrendertype = field_178801_c.get(p_178795_0_);
    return (iscoreobjectivecriteria$enumrendertype == null) ? INTEGER : iscoreobjectivecriteria$enumrendertype;
  }
}
