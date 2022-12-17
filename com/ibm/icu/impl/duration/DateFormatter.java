package com.ibm.icu.impl.duration;

import java.util.Date;
import java.util.TimeZone;

public interface DateFormatter {
  String format(Date paramDate);
  
  String format(long paramLong);
  
  DateFormatter withLocale(String paramString);
  
  DateFormatter withTimeZone(TimeZone paramTimeZone);
}
