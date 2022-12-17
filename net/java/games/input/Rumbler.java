package net.java.games.input;

public interface Rumbler {
  void rumble(float paramFloat);
  
  String getAxisName();
  
  Component.Identifier getAxisIdentifier();
}
