package ir.lecer.uwu.features.notifications;

import com.google.common.collect.Lists;
import ir.lecer.uwu.impl.hud.Notifications;
import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public final class NotificationHelper {
  private NotificationHelper() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static List<Notification> getRunningNotficiations() {
    return runningNotficiations;
  }
  
  private static final List<Notification> runningNotficiations = Collections.synchronizedList(Lists.newArrayList());
  
  private static final LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>();
  
  private static void render(Notification notification) {
    if (!Notifications.isEnabled())
      return; 
    pendingNotifications.add(notification);
  }
  
  public static void send(String title, String message, Color lineColor, Color backgroundColor, double lengthTime) {
    render(new Notification(title, message, lineColor, backgroundColor, lengthTime));
  }
  
  public static void render() {
    Iterator<Notification> iterator = runningNotficiations.iterator();
    while (iterator.hasNext()) {
      Notification next = iterator.next();
      if (!next.isRunning()) {
        iterator.remove();
        continue;
      } 
      next.render();
    } 
    if (pendingNotifications.isEmpty() || runningNotficiations.size() > 5)
      return; 
    Notification notification = pendingNotifications.poll();
    if (notification == null)
      return; 
    notification.start();
    runningNotficiations.add(notification);
  }
  
  public static void clear() {
    pendingNotifications.clear();
    runningNotficiations.clear();
  }
}
