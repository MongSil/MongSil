package kr.co.tacademy.mongsil.mongsil;

import android.util.Log;

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
    private static Date now = new Date(getCurrentTime());

    private static long getCurrentTime() {
        return System.currentTimeMillis();
    }


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
    private static SimpleDateFormat timeCompareFormat =
            new SimpleDateFormat("HH:mm", Locale.KOREA);


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

    public static String getNow() {
        return dateFormat.format(now);
    }


    // 현재 날짜에서 얼마나 날짜가 지났는지 계산하는 메소드
    public static String dateCalculate(String date) {
        try {
            Date tempDate = dateFormat.parse(date);

            if (dateCompareFormat.format(tempDate).equals(
                    dateCompareFormat.format(now))) {
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
        Log.e("dateCalculate :", date + "오류");
        return " ";
    }

    // 내 댓글 보기 // 현재 날짜에서 얼마나 날짜가 지났는지 계산하는 메소드
    public static String replyDateCalculate(String date) {
        try {
            Date tempDate = dateFormat.parse(date);

            if (dateCompareFormat.format(tempDate).equals(
                    dateCompareFormat.format(now))) {
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
        Log.e("replyDateCalculate :", date + "오류");
        return " ";
    }

    // 글 작성 시간을 보여주는 메소드
    public static String PostTime(String time) {
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
        try {
            Date tempTime = timeFormat.parse(time);
            return new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(tempTime);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        Log.e("PostTime :", time + "오류");
        return " ";
    }

    // 글 작성 후 미리보기할 때 시간을 보여주는 메소드
    public static String PreviewPostTime() {
        return new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(now);
    }

    // 현재 시간에서 얼마나 시간이 지났는지 계산하는 메소드
    public static String timeCalculate(String time) {
        try {
            Date tempTime = dateFormat.parse(time);
            if (timeCompareFormat.format(tempTime).equals(
                    timeCompareFormat.format(now))) {
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
                } else if (hourSubTime >= 1) {
                    return String.valueOf(hourSubTime) + "시간 전";
                } else if (hourSubTime < 1) {
                    return String.valueOf(minuteSubTime) + "분 전";
                }
            }
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        Log.e("timeCalculate :", time + "오류");
        return " ";
    }
}
