package com.ibm.icu.impl.duration;

import java.util.TimeZone;

public interface DurationFormatterFactory {
  DurationFormatterFactory setPeriodFormatter(PeriodFormatter paramPeriodFormatter);
  
  DurationFormatterFactory setPeriodBuilder(PeriodBuilder paramPeriodBuilder);
  
  DurationFormatterFactory setFallback(DateFormatter paramDateFormatter);
  
  DurationFormatterFactory setFallbackLimit(long paramLong);
  
  DurationFormatterFactory setLocale(String paramString);
  
  DurationFormatterFactory setTimeZone(TimeZone paramTimeZone);
  
  DurationFormatter getFormatter();
}
