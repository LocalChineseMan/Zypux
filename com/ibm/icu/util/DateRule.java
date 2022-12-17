package com.ibm.icu.util;

import java.util.Date;

public interface DateRule {
  Date firstAfter(Date paramDate);
  
  Date firstBetween(Date paramDate1, Date paramDate2);
  
  boolean isOn(Date paramDate);
  
  boolean isBetween(Date paramDate1, Date paramDate2);
}
