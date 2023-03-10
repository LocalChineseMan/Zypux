package com.ibm.icu.impl.duration;

import java.util.TimeZone;

public interface PeriodBuilderFactory {
  PeriodBuilderFactory setAvailableUnitRange(TimeUnit paramTimeUnit1, TimeUnit paramTimeUnit2);
  
  PeriodBuilderFactory setUnitIsAvailable(TimeUnit paramTimeUnit, boolean paramBoolean);
  
  PeriodBuilderFactory setMaxLimit(float paramFloat);
  
  PeriodBuilderFactory setMinLimit(float paramFloat);
  
  PeriodBuilderFactory setAllowZero(boolean paramBoolean);
  
  PeriodBuilderFactory setWeeksAloneOnly(boolean paramBoolean);
  
  PeriodBuilderFactory setAllowMilliseconds(boolean paramBoolean);
  
  PeriodBuilderFactory setLocale(String paramString);
  
  PeriodBuilderFactory setTimeZone(TimeZone paramTimeZone);
  
  PeriodBuilder getFixedUnitBuilder(TimeUnit paramTimeUnit);
  
  PeriodBuilder getSingleUnitBuilder();
  
  PeriodBuilder getOneOrTwoUnitBuilder();
  
  PeriodBuilder getMultiUnitBuilder(int paramInt);
}
