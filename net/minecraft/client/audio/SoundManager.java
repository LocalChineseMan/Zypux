package net.minecraft.client.audio;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

class null implements Runnable {
  public void run() {
    SoundSystemConfig.setLogger((SoundSystemLogger)new Object(this));
    SoundManager.this.getClass();
    SoundManager.access$102(SoundManager.this, new SoundManager.SoundSystemStarterThread(SoundManager.this, null));
    SoundManager.access$302(SoundManager.this, true);
    SoundManager.access$100(SoundManager.this).setMasterVolume(SoundManager.access$400(SoundManager.this).getSoundLevel(SoundCategory.MASTER));
    SoundManager.access$000().info(SoundManager.access$500(), "Sound engine started");
  }
  
  class SoundManager {}
}
