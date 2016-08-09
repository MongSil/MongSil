package kr.co.tacademy.mongsil.mongsil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by ccei on 2016-08-03.
 */
public class TimeData {
    // 현재 시간
    private static long currentTime = System.currentTimeMillis();
    private static Date now = new Date(currentTime);

    // day formats
    public static String dayFormat =
          new SimpleDateFormat("dd", Locale.ENGLISH).format(now);
    public static String weakFormat =
          new SimpleDateFormat("EEEE", Locale.ENGLISH).format(now);
    public static String monthFormat =
          new SimpleDateFormat("MMMM", Locale.ENGLISH).format(now);

    // date formats
    private static SimpleDateFormat dateFormat =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private static SimpleDateFormat dateCompareFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);


    // Calendar 객체 생성
    private static Calendar cal = Calendar.getInstance();

    public static boolean compareDate(String firstDate, String secondDate) {
        try {
            long first = dateCompareFormat.parse(firstDate).getTime();
            long second = dateCompareFormat.parse(secondDate).getTime();

            if(first < second) {
                return true;
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return false;
    }

    // 현재 날짜에서 얼마나 날짜가 지났는지 계산하는 메소드
    public static String dateCalculate(String date) {
        try {
            Date tempDate = dateFormat.parse(date);

            if (tempDate.getTime() / (1000*60*60*24)
                    == now.getTime() / (1000*60*60*24) ) {
                return "Today";
            } else if (tempDate.before(now)) {
                int subDay = (int)((now.getTime() - tempDate.getTime()) / (1000*60*60*24));
                cal.setTime(now);
                cal.add(Calendar.DATE, -subDay);
                return new SimpleDateFormat("MMMM d", Locale.ENGLISH)
                        .format(cal.getTime());
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "error";
    }

    // 내 댓글 보기 // 현재 날짜에서 얼마나 날짜가 지났는지 계산하는 메소드
    public static String replyDateCalculate(String date) {
        try {
            Date tempDate = dateFormat.parse(date);

            if (tempDate.getTime() / (1000*60*60*24)
                    == now.getTime() / (1000*60*60*24) ) {
                return "오늘";
            } else if (tempDate.before(now)) {
                int subDay = (int)((now.getTime() - tempDate.getTime()) / (1000*60*60*24));
                cal.setTime(now);
                cal.add(Calendar.DATE, -subDay);
                return new SimpleDateFormat("MM월 d일", Locale.ENGLISH)
                        .format(cal.getTime());
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "error";
    }

    // 글 작성 시간을 보여주는 메소드
    public static String PostTime(String time) {
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        try {
            Date tempTime = timeFormat.parse(time);
            return new SimpleDateFormat("HH:mm a", Locale.ENGLISH).format(tempTime);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "error";
    }

    // 현재 시간에서 얼마나 시간이 지났는지 계산하는 메소드
    public static String timeCalculate(String time) {
        try {
            Date tempTime = dateFormat.parse(time);
            if (tempTime.getTime() / (1000*60*60*24)
                    == now.getTime() / (1000*60*60*24)) {
                return "지금";
            } else if (tempTime.before(now)) {
                long subTime = (now.getTime() - tempTime.getTime()) / 1000;

                long daySubTime = subTime / (60 * 60 * 24);
                long hourSubTime = subTime / (60 * 60);
                long minuteSubTime = ((subTime / 60)) % 60;

                if (daySubTime > 60) {
                    long monthSubTime = daySubTime / 30;
                    return String.valueOf(monthSubTime) + "달 전";
                }

                if (hourSubTime > 23) {
                    return String.valueOf(daySubTime) + "일 전";
                }

                for (int i = 22; i > 0; i--) {
                    if (hourSubTime > i) {
                        return String.valueOf(hourSubTime) + "시간 전";
                    }
                }
                if (hourSubTime < 1) {
                    return String.valueOf(minuteSubTime) + "분 전";
                }
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return "error";
    }
}
