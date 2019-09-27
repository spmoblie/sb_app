package com.sbwg.sxb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CalendarUtil {

    private String TAG = CalendarUtil.class.getSimpleName();

    private static CalendarUtil cswdu = null;

    private CalendarUtil() {

    }

    public static CalendarUtil getInstance() {
        if (cswdu == null) {
            cswdu = new CalendarUtil();
        }
        return cswdu;
    }

    /** 
     * @title 获取周六和周日是工作日的情况（手工维护） 
     *    注意，日期必须写全：2020-1-1必须写成：2020-01-01
     * @author 
     * @return 周末是工作日的列表
     */
    private List<String> getWeekendIsWorkDateList() {
        List<String> list = new ArrayList<>();
        list.add("2019-09-29");
        list.add("2019-10-12");
        return list;
    }

    /** 
    * @title 获取周一到周五是假期的情况（手工维护） 
    *    注意，日期必须写全：2020-1-1必须写成：2020-01-01
    * @author
    * @return 平时是假期的列表
    */
    private List<String> getWeekdayIsHolidayList() {
        List<String> list = new ArrayList<>();
        list.add("2019-09-13");
        list.add("2019-10-01");
        list.add("2019-10-02");
        list.add("2019-10-03");
        list.add("2019-10-04");
        list.add("2019-10-05");
        list.add("2019-10-06");
        list.add("2019-10-07");
        return list;
    }

    /** 
    * @title 判断是否为工作日 
    * @detail 工作日计算: 
    *           1、正常工作日，并且为非假期 
    *           2、周末被调整成工作日 
    * @author  
    * @return 是工作日返回true，非工作日返回false 
    */
    public boolean isWorkDay(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            //平时
            return !getWeekdayIsHolidayList().contains(sdf.format(calendar.getTime()));
        } else {
            //周末
            return getWeekendIsWorkDateList().contains(sdf.format(calendar.getTime()));
        }
    }

    /**
     * 业务规则-获得第二个工作日
     * @param strDate
     * @return
     */
    public String getSpecWorkDate(String strDate) {
        String workDay = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        //传进来的日期 往后加一天
        cal.add(Calendar.DAY_OF_YEAR, 1);
        int workDayFlag = 0;
        for (int i = 0; i < 15; i++) {
            if (isWorkDay(cal) && workDayFlag < 2) {
                Date time = cal.getTime();
                if (workDayFlag == 1) {
                    workDay = sdf.format(time);
                    break;
                }
                workDayFlag++;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return workDay;
    }
}
