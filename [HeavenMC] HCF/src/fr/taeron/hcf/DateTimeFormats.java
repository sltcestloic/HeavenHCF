package fr.taeron.hcf;

import java.time.*;

import net.minecraft.util.org.apache.commons.lang3.time.FastDateFormat;

import java.text.*;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DateTimeFormats
{
    public static final TimeZone SERVER_TIME_ZONE;
    public static final ZoneId SERVER_ZONE_ID;
    public static final FastDateFormat DAY_MTH_HR_MIN_SECS;
    public static final FastDateFormat DAY_MTH_YR_HR_MIN_AMPM;
    public static final FastDateFormat DAY_MTH_HR_MIN_AMPM;
    public static final FastDateFormat HR_MIN_AMPM;
    public static final FastDateFormat HR_MIN_AMPM_TIMEZONE;
    public static final FastDateFormat HR_MIN;
    public static final FastDateFormat MIN_SECS;
    public static final FastDateFormat KOTH_FORMAT;
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS;
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING;
    
    static {
        SERVER_TIME_ZONE = TimeZone.getTimeZone("Europe/Copenhagen");
        SERVER_ZONE_ID = DateTimeFormats.SERVER_TIME_ZONE.toZoneId();
        DAY_MTH_HR_MIN_SECS = FastDateFormat.getInstance("dd/MM HH:mm:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        DAY_MTH_YR_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM/yy hh:mma", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        DAY_MTH_HR_MIN_AMPM = FastDateFormat.getInstance("dd/MM hh:mma", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        HR_MIN_AMPM = FastDateFormat.getInstance("hh:mma", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        HR_MIN_AMPM_TIMEZONE = FastDateFormat.getInstance("hh:mma z", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        HR_MIN = FastDateFormat.getInstance("hh:mm", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        MIN_SECS = FastDateFormat.getInstance("mm:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        KOTH_FORMAT = FastDateFormat.getInstance("m:ss", DateTimeFormats.SERVER_TIME_ZONE, Locale.FRANCE);
        REMAINING_SECONDS = new ThreadLocal() {
            protected DecimalFormat initialValue() {
                return new DecimalFormat("0.#");
            }
        };
        REMAINING_SECONDS_TRAILING = new ThreadLocal() {
            protected DecimalFormat initialValue() {
                return new DecimalFormat("0.0");
            }
        };
    }
}
