package com.ibm.icu.util;

import com.ibm.icu.text.DateFormatSymbols;

public class FormatConfiguration {
  private String pattern;
  
  private String override;
  
  private DateFormatSymbols formatData;
  
  private Calendar cal;
  
  private ULocale loc;
  
  private FormatConfiguration() {}
  
  public String getPatternString() {
    return this.pattern;
  }
  
  public String getOverrideString() {
    return this.override;
  }
  
  public Calendar getCalendar() {
    return this.cal;
  }
  
  public ULocale getLocale() {
    return this.loc;
  }
  
  public DateFormatSymbols getDateFormatSymbols() {
    return this.formatData;
  }
}
