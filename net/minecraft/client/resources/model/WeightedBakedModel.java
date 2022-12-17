package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class Builder {
  private List<WeightedBakedModel.MyWeighedRandomItem> listItems = Lists.newArrayList();
  
  public Builder add(IBakedModel p_177677_1_, int p_177677_2_) {
    this.listItems.add(new WeightedBakedModel.MyWeighedRandomItem(p_177677_1_, p_177677_2_));
    return this;
  }
  
  public WeightedBakedModel build() {
    Collections.sort(this.listItems);
    return new WeightedBakedModel(this.listItems);
  }
  
  public IBakedModel first() {
    return ((WeightedBakedModel.MyWeighedRandomItem)this.listItems.get(0)).model;
  }
  
  static class WeightedBakedModel {}
  
  public static class WeightedBakedModel {}
}
