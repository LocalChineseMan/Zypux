package com.ibm.icu.impl.duration;

import java.util.Date;
import java.util.TimeZone;

public interface DurationFormatter {
  String formatDurationFromNowTo(Date paramDate);
  
  String formatDurationFromNow(long paramLong);
  
  String formatDurationFrom(long paramLong1, long paramLong2);
  
  DurationFormatter withLocale(String paramString);
  
  DurationFormatter withTimeZone(TimeZone paramTimeZone);
}
