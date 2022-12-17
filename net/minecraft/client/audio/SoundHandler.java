package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

class null implements ISoundEventAccessor<SoundPoolEntry> {
  final ResourceLocation field_148726_a = new ResourceLocation(s1, soundlist$soundentry.getSoundEntryName());
  
  public int getWeight() {
    SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.access$000(SoundHandler.this).getObject(this.field_148726_a);
    return (soundeventaccessorcomposite1 == null) ? 0 : soundeventaccessorcomposite1.getWeight();
  }
  
  public SoundPoolEntry cloneEntry() {
    SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.access$000(SoundHandler.this).getObject(this.field_148726_a);
    return (soundeventaccessorcomposite1 == null) ? SoundHandler.missing_sound : soundeventaccessorcomposite1.cloneEntry();
  }
}
