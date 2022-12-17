package org.apache.logging.log4j.core.util.datetime;

import java.io.IOException;
import java.util.Calendar;

class TwentyFourHourField implements FastDatePrinter.NumberRule {
  private final FastDatePrinter.NumberRule mRule;
  
  TwentyFourHourField(FastDatePrinter.NumberRule rule) {
    this.mRule = rule;
  }
  
  public int estimateLength() {
    return this.mRule.estimateLength();
  }
  
  public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
    int value = calendar.get(11);
    if (value == 0)
      value = calendar.getMaximum(11) + 1; 
    this.mRule.appendTo(buffer, value);
  }
  
  public void appendTo(Appendable buffer, int value) throws IOException {
    this.mRule.appendTo(buffer, value);
  }
}
