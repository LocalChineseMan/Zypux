package com.ibm.icu.impl.duration;

import java.util.TimeZone;

public interface PeriodBuilder {
  Period create(long paramLong);
  
  Period createWithReferenceDate(long paramLong1, long paramLong2);
  
  PeriodBuilder withLocale(String paramString);
  
  PeriodBuilder withTimeZone(TimeZone paramTimeZone);
}
