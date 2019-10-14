package com.songbao.sxb.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.songbao.sxb.AppApplication;
import com.songbao.sxb.R;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

@SuppressLint({"SimpleDateFormat", "DefaultLocale"})
public class TimeUtil {

    long start;

    public TimeUtil() {
        start = System.currentTimeMillis();
    }

    public void log(String tag, String msg) {
        LogUtil.i(tag, msg + (System.currentTimeMillis() - start) / 1000 + "." + ((System.currentTimeMillis() - start) / 100 % 10) + "秒");
    }

    /**
     * 获取当前时间
     * @param pattern  yyyy-MM-dd
     *                 HH:mm:ss
     *                 yyyy-MM-dd HH:mm:ss
     *                 yyyy-MM-dd HH:mm:ss am
     *                 yyyy-MM-dd HH:mm:ss pm
     *                 yyyy/MM/dd
     *                 ......
     *                 yyyy.MM.dd-HH.mm.ss
     *                 ......
     */
    public static String getNowString(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(new Date());
    }

    /**
     * 获取当前时间
     */
    public static Date getNowDate(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(new Date());
        return formatter.parse(dateString, new ParsePosition(8));
    }

    /**
     * 获取当前时间
     */
    public static long getNowLong(String pattern) {
        return getNowDate(pattern).getTime();
    }

    /**
     * 获取当前小时
     * @return HH
     */
    public static String getHour() {
        return getNowString("yyyy-MM-dd HH:mm:ss").substring(11, 13);
    }

    /**
     * 获取当前分钟
     * @return mm
     */
    public static String getMinute() {
        return getNowString("yyyy-MM-dd HH:mm:ss").substring(14, 16);
    }

    /**
     * 将文本格式时间转换为对象格式时间
     * @param strDate 文本格式
     */
    public static Date strToDate(String pattern, String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.parse(strDate, new ParsePosition(0));
    }

    /**
     * 将对象格式时间转换为文本格式时间
     * @param dateDate 时间对象
     */
    public static String dateToStr(String pattern, Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(dateDate);
    }

    /**
     * 将文本格式时间转换为毫秒格式时间
     * @param strDate 文本格式
     */
    public static long strToLong(String pattern, String strDate) {
        return dateToLong(strToDate(pattern, strDate));
    }

    /**
     * 将毫秒格式时间转换为文本格式时间
     * @param longDate 毫秒格式
     */
    public static String longToStr(String pattern, long longDate) {
        return dateToStr(pattern, longToDate(longDate));
    }

    /**
     * 将对象格式时间转换为毫秒格式时间
     * @param dateDate 时间对象
     */
    public static long dateToLong(Date dateDate) {
        return dateDate.getTime();
    }

    /**
     * 将毫秒格式时间转换为文对象格式时间
     * @param longDate 毫秒格式
     */
    public static Date longToDate(long longDate) {
        return new Date(longDate + 0);
    }

    /**
     * 将毫秒格式时间转换成自定义的文本格式时间：0s - 86400s
     * @param longDate 毫秒格式
     * @return 0s - 86400s
     */
    public static String longToStrS(long longDate) {
        Context ctx = AppApplication.getInstance().getApplicationContext();
        long showTime = longDate % 86400;
        return showTime + ctx.getString(R.string.second);
    }

    /**
     * 将毫秒格式时间转换成自定义的文本格式时间：1分1秒
     * @param longDate 毫秒格式
     * @return 1分1秒
     */
    public static String longToStrMS(long longDate) {
        Context ctx = AppApplication.getInstance().getApplicationContext();

        int day = (int) (longDate / 86400);
        long dayTime = longDate % 86400;
        int hour = (int) (dayTime / 3600);
        long hourTime = dayTime % 3600;
        int minute = (int) (hourTime / 60);
        long minuteTime = hourTime % 60;

        // 30分钟有效期
        /*if (time <= 0 || day > 0 || hour > 0 || minute > 30) {
			return "";
		}*/
        return minute + ctx.getString(R.string.minute) + minuteTime + ctx.getString(R.string.second);
    }

    /**
     * 将毫秒格式时间转换成自定义的文本格式时间：1天1时1分1秒
     * @param longDate 毫秒格式
     * @return 1天1时1分1秒
     */
    public static String longToStrDHMS(long longDate) {
        Context ctx = AppApplication.getInstance().getApplicationContext();

        int day = (int) (longDate / 86400);
        long dayTime = longDate % 86400;
        int hour = (int) (dayTime / 3600);
        long hourTime = dayTime % 3600;
        int minute = (int) (hourTime / 60);
        long minuteTime = hourTime % 60;

        return day + ctx.getString(R.string.day) + hour + ctx.getString(R.string.hour) + minute + ctx.getString(R.string.minute) + minuteTime + ctx.getString(R.string.second);
    }

    /**
     * 将毫秒格式时间转换成自定义的文本格式时间：[天,时,分,秒]
     * @param longDate 毫秒格式
     * @return [天,时,分,秒]
     */
    public static Integer[] longToIntDHMS(long longDate) {
        Integer[] times = new Integer[4];
        int day = (int) (longDate / 86400);
        long dayTime = longDate % 86400;
        int hour = (int) (dayTime / 3600);
        long hourTime = dayTime % 3600;
        int minute = (int) (hourTime / 60);
        long minuteTime = hourTime % 60;
        int second = (int) minuteTime;
        times[0] = day;
        times[1] = hour;
        times[2] = minute;
        times[3] = second;
        return times;
    }

    /**
     * 将传入的时间格式转换为指定的时间格式
     */
    public static String strToStrYMD(String pattern, String strDate) {
        return dateToStr(pattern, strToDate(pattern, strDate));
    }

    /**
     * 将传入的时间格式转换为指定的时间格式
     * @param strDate yyyy-MM-dd HH:mm:ss
     * @return String MM-dd HH:mm
     */
    public static String strToStrMdHm(String strDate) {
        if (strDate.length() < 16) return "";
        return strDate.substring(5, 16);
    }

    /**
     * 将传入的时间格式转换为指定的时间格式
     * @param strDate yyyy-MM-dd HH:mm:ss
     * @return String MM月dd日 HH:mm
     */
    public static String strToStrItem(String strDate) {
        if (strDate.length() < 16) return "";
        String newDate = strDate.substring(5, 16);
        newDate = newDate.replace("-", "月");
        newDate = newDate.replace(" ", "日 ");
        return newDate;
    }

    /**
     * 将传入的时间格式转换为指定的时间格式
     * @param longDate 毫秒格式
     * @return String MM月dd日 HH:mm
     */
    public static String longToStrItem(long longDate) {
        return strToStrItem(longToStr("yyyy-MM-dd HH:mm:ss", longDate));
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day;
        try {
            Date date1 = myFormatter.parse(sj1);
            Date date2 = myFormatter.parse(sj2);
            day = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowDate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d = strToDate("yyyy-MM-dd", nowDate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            return format.format(d);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断是否润年
     *
     * @param date
     * @return
     */
    public static boolean isLeapYear(String date) {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate("yyyy-MM-dd", date);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dat yyyy-MM-dd
     * @return
     */
    public static String getEndDateOfMonth(String dat) {
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    /**
     * 判断二个时间是否在同一个周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }

}
