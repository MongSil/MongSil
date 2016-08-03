package kr.co.tacademy.mongsil.mongsil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by ccei on 2016-08-03.
 */
public class TimeData {
    // 현재 시간
    private static Date now = new Date();
    private static long currentTime = System.currentTimeMillis();

    // day formats
    public static String dayFormat =
            new SimpleDateFormat("dd", Locale.ENGLISH).format(now);
    public static String weakFormat =
            new SimpleDateFormat("EEEE", Locale.ENGLISH).format(now);
    public static String monthFormat =
            new SimpleDateFormat("MMMM", Locale.ENGLISH).format(now);

    // day current time
    public static long dayTime = TimeUnit.MILLISECONDS.toDays(currentTime);
    public static long hourTime = TimeUnit.MILLISECONDS.toHours(currentTime);
    public static long secondTime = TimeUnit.MILLISECONDS.toSeconds(currentTime);
}
