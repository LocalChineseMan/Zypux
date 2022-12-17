package com.ibm.icu.impl.duration;

public interface PeriodFormatter {
  String format(Period paramPeriod);
  
  PeriodFormatter withLocale(String paramString);
}
