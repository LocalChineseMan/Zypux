package ir.lecer.uwu.tools.utilities;

import ir.lecer.uwu.enums.LogLevels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerUtils {
  private LoggerUtils() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final Logger logger = LogManager.getLogger();
  
  public static void console(Enum<LogLevels> level, String text) {
    if (level.equals(LogLevels.INFO)) {
      logger.info(text);
    } else if (level.equals(LogLevels.WARN)) {
      logger.warn(text);
    } else if (level.equals(LogLevels.ERROR)) {
      logger.error(text);
    } 
  }
}
