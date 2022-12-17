package com.ibm.icu.impl.duration;

public interface PeriodFormatterFactory {
  PeriodFormatterFactory setLocale(String paramString);
  
  PeriodFormatterFactory setDisplayLimit(boolean paramBoolean);
  
  PeriodFormatterFactory setDisplayPastFuture(boolean paramBoolean);
  
  PeriodFormatterFactory setSeparatorVariant(int paramInt);
  
  PeriodFormatterFactory setUnitVariant(int paramInt);
  
  PeriodFormatterFactory setCountVariant(int paramInt);
  
  PeriodFormatter getFormatter();
}
