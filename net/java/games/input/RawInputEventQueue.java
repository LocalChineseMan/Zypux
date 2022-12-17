package net.java.games.input;

import java.io.IOException;
import java.util.List;

final class RawInputEventQueue {
  private final Object monitor = new Object();
  
  private List devices;
  
  public final void start(List devices) throws IOException {
    this.devices = devices;
    QueueThread queue = new QueueThread(this);
    synchronized (this.monitor) {
      queue.start();
      while (!queue.isInitialized()) {
        try {
          this.monitor.wait();
        } catch (InterruptedException e) {}
      } 
    } 
    if (queue.getException() != null)
      throw queue.getException(); 
  }
  
  private final RawDevice lookupDevice(long handle) {
    for (int i = 0; i < this.devices.size(); i++) {
      RawDevice device = this.devices.get(i);
      if (device.getHandle() == handle)
        return device; 
    } 
    return null;
  }
  
  private final void addMouseEvent(long handle, long millis, int flags, int button_flags, int button_data, long raw_buttons, long last_x, long last_y, long extra_information) {
    RawDevice device = lookupDevice(handle);
    if (device == null)
      return; 
    device.addMouseEvent(millis, flags, button_flags, button_data, raw_buttons, last_x, last_y, extra_information);
  }
  
  private final void addKeyboardEvent(long handle, long millis, int make_code, int flags, int vkey, int message, long extra_information) {
    RawDevice device = lookupDevice(handle);
    if (device == null)
      return; 
    device.addKeyboardEvent(millis, make_code, flags, vkey, message, extra_information);
  }
  
  private final void poll(DummyWindow window) throws IOException {
    nPoll(window.getHwnd());
  }
  
  private static final void registerDevices(DummyWindow window, RawDeviceInfo[] devices) throws IOException {
    nRegisterDevices(0, window.getHwnd(), devices);
  }
  
  private final native void nPoll(long paramLong) throws IOException;
  
  private static final native void nRegisterDevices(int paramInt, long paramLong, RawDeviceInfo[] paramArrayOfRawDeviceInfo) throws IOException;
  
  private final class RawInputEventQueue {}
}
