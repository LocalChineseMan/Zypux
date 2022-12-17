package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsUtil {
  public static LoadingCache<String, String> nameCache = CacheBuilder.newBuilder()
    .expireAfterWrite(60L, TimeUnit.MINUTES)
    .build((CacheLoader)new Object());
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private static final int MINUTES = 60;
  
  private static final int HOURS = 3600;
  
  private static final int DAYS = 86400;
  
  public static void browseTo(String uri) {
    try {
      URI link = new URI(uri);
      Class<?> desktopClass = Class.forName("java.awt.Desktop");
      Object o = desktopClass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
      desktopClass.getMethod("browse", new Class[] { URI.class }).invoke(o, new Object[] { link });
    } catch (Throwable e) {
      LOGGER.error("Couldn't open link");
    } 
  }
  
  public static String convertToAgePresentation(Long timeDiff) {
    if (timeDiff.longValue() < 0L)
      return "right now"; 
    long timeDiffInSeconds = timeDiff.longValue() / 1000L;
    if (timeDiffInSeconds < 60L)
      return ((timeDiffInSeconds == 1L) ? "1 second" : (timeDiffInSeconds + " seconds")) + " ago"; 
    if (timeDiffInSeconds < 3600L) {
      long minutes = timeDiffInSeconds / 60L;
      return ((minutes == 1L) ? "1 minute" : (minutes + " minutes")) + " ago";
    } 
    if (timeDiffInSeconds < 86400L) {
      long hours = timeDiffInSeconds / 3600L;
      return ((hours == 1L) ? "1 hour" : (hours + " hours")) + " ago";
    } 
    long days = timeDiffInSeconds / 86400L;
    return ((days == 1L) ? "1 day" : (days + " days")) + " ago";
  }
}
