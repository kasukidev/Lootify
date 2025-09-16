package cc.insidious.lootify.utilities.duration;

import cc.insidious.lootify.utilities.date.DateTimeFormats;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class DurationUtils {

  /**
   * Parses a string describing measures of time (e.g. "1d 1m 1s") to milliseconds
   *
   * <p>Source:
   * http://stackoverflow.com/questions/4015196/is-there-a-java-library-that-converts-strings-describing-measures-of-time-e-g
   *
   * @param input the string to parse
   * @return the parsed time in milliseconds or -1 if could not
   */
  public long parse(String input) {
    if (input == null || input.isEmpty()) {
      return -1L;
    }

    long result = 0L;
    StringBuilder number = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isDigit(c)) {
        number.append(c);
        continue;
      }

      String str;
      if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
        result += convert(Integer.parseInt(str), c);
        number = new StringBuilder();
      }
    }

    return result;
  }

  /**
   * Source:
   * http://stackoverflow.com/questions/4015196/is-there-a-java-library-that-converts-strings-describing-measures-of-time-e-g
   */
  private long convert(int value, char unit) {
    switch (unit) {
      case 'y' | 'Y':
        return value * TimeUnit.DAYS.toMillis(365L);
      case 'M':
        return value * TimeUnit.DAYS.toMillis(30L);
      case 'w' | 'W':
        {
          return value * TimeUnit.DAYS.toMillis(7L);
        }
      case 'd' | 'D':
        return value * TimeUnit.DAYS.toMillis(1L);
      case 'h' | 'H':
        return value * TimeUnit.HOURS.toMillis(1L);
      case 'm':
        return value * TimeUnit.MINUTES.toMillis(1L);
      case 's' | 'S':
        return value * TimeUnit.SECONDS.toMillis(1L);
      default:
        return -1L;
    }
  }

  private final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
  private final long HOUR = TimeUnit.HOURS.toMillis(1L);

  public String getRemaining(long millis) {
    return getRemaining(millis, true, true);
  }

  public String getRemaining(long millis, boolean milliseconds) {
    return getRemaining(millis, milliseconds, true);
  }

  public String getRemaining(long duration, boolean milliseconds, boolean trail) {
    if (milliseconds && duration < MINUTE) {
      return (trail
                  ? DateTimeFormats.REMAINING_SECONDS_TRAILING
                  : DateTimeFormats.REMAINING_SECONDS)
              .get()
              .format(duration * 0.001)
          + 's';
    } else {
      return DurationFormatUtils.formatDuration(
          duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
    }
  }
}
