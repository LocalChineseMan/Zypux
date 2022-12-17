package net.minecraft.util;

class Entry<V> {
  final int hashEntry;
  
  V valueEntry;
  
  Entry<V> nextEntry;
  
  final int slotHash;
  
  Entry(int p_i1552_1_, int p_i1552_2_, V p_i1552_3_, Entry<V> p_i1552_4_) {
    this.valueEntry = p_i1552_3_;
    this.nextEntry = p_i1552_4_;
    this.hashEntry = p_i1552_2_;
    this.slotHash = p_i1552_1_;
  }
  
  public final int getHash() {
    return this.hashEntry;
  }
  
  public final V getValue() {
    return this.valueEntry;
  }
  
  public final boolean equals(Object p_equals_1_) {
    if (!(p_equals_1_ instanceof Entry))
      return false; 
    Entry<V> entry = (Entry<V>)p_equals_1_;
    Object object = Integer.valueOf(getHash());
    Object object1 = Integer.valueOf(entry.getHash());
    if (object == object1 || (object != null && object.equals(object1))) {
      Object object2 = getValue();
      Object object3 = entry.getValue();
      if (object2 == object3 || (object2 != null && object2.equals(object3)))
        return true; 
    } 
    return false;
  }
  
  public final int hashCode() {
    return IntHashMap.access$000(this.hashEntry);
  }
  
  public final String toString() {
    return getHash() + "=" + getValue();
  }
}
