package ir.lecer.uwu.tools.utilities;

import java.time.Instant;

public class TimerUtils {
  private final Instant endtime;
  
  public boolean isEnd() {
    return this.endtime.isBefore(Instant.now());
  }
  
  public TimerUtils(long durationInMillis) {
    Instant starttime = Instant.now();
    this.endtime = starttime.plusMillis(durationInMillis);
  }
  
  public Long getTimer() {
    return Long.valueOf(this.endtime.minusMillis(Instant.now().getEpochSecond()).getEpochSecond());
  }
}
