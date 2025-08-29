package cc.insidious.example.utilities.date;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.time.FastDateFormat;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

@UtilityClass
public class DateTimeFormats {
  private final AtomicBoolean loaded = new AtomicBoolean(false);
  public FastDateFormat DAY_MTH_HR_MIN_SECS;
  public FastDateFormat DAY_MTH_YR_HR_MIN_AMPM;
  public FastDateFormat DAY_MTH_HR_MIN_AMPM;
  public FastDateFormat HR_MIN_AMPM;
  public FastDateFormat HR_MIN_AMPM_TIMEZONE;
  public FastDateFormat HR_MIN;
  public FastDateFormat KOTH_FORMAT;

  public void setup(TimeZone timeZone) throws IllegalStateException {
    Preconditions.checkArgument(!loaded.getAndSet(true), "Already loaded");

    DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss", timeZone, Locale.ENGLISH);
    DAY_MTH_YR_HR_MIN_AMPM =
        FastDateFormat.getInstance("dd/MM/yy hh:mma", timeZone, Locale.ENGLISH);
    DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", timeZone, Locale.ENGLISH);
    HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", timeZone, Locale.ENGLISH);
    HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", timeZone, Locale.ENGLISH);
    HR_MIN = FastDateFormat.getInstance("hh:mm", timeZone, Locale.ENGLISH);
    KOTH_FORMAT = FastDateFormat.getInstance("m:ss", timeZone, Locale.ENGLISH);
  }

  // The format used to show one decimal without a trailing zero.
  public final ThreadLocal<DecimalFormat> REMAINING_SECONDS =
      ThreadLocal.withInitial(() -> new DecimalFormat("0.#"));

  public final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING =
      ThreadLocal.withInitial(() -> new DecimalFormat("0.0"));
}
