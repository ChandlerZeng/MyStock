package com.chandler.red.mystock.util;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间格式工具类
 */
public class DateUtil
{
    public static final long ONE_MINUTE_MILLIS = 60 * 1000;
    public static final long ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS;
    public static final long ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS;


    public static Date parseToDate(String s)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        if (s == null || s.length() < 5)
        {
            return null;
        }
        try
        {
            date = simpleDateFormat.parse(s);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public static Date stringToMMDate(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static long stringToMMDateLong(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    public static long stringToMMDateLong2(String str) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            // Fri Feb 24 00:00:00 CST 2012
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return date.getTime();
    }

    public static Date parseToDate(String s, String style)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        Date date = null;
        if (s == null || s.length() < 5)
        {
            return null;
        }
        try
        {
            date = simpleDateFormat.parse(s);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }


    public static String parseToString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String parseStringToMMDate(String curentTime){
        String s = null;
        if(curentTime!=null){
            s = transformToShow(Long.parseLong(curentTime));
        }
        return s;
    }

    public static String parseToStringWithoutSS(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseToHHMMString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String parseToHHString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseTommString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("mm");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseToDate(long time)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String getCurrentDayString()
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseToMD(long time)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String str = formatter.format(now.getTime());
        return str;
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    /**
     * <pre>
     * 判断date和当前日期是否在同一周内
     * 注:
     * Calendar类提供了一个获取日期在所属年份中是第几周的方法，对于上一年末的某一天
     * 和新年初的某一天在同一周内也一样可以处理，例如2012-12-31和2013-01-01虽然在
     * 不同的年份中，但是使用此方法依然判断二者属于同一周内
     * </pre>
     *
     * @param date
     * @return
     */
    public static boolean isSameWeekWithToday(Date date) {

        if (date == null) {
            return false;
        }

        // 0.先把Date类型的对象转换Calendar类型的对象
        Calendar todayCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();

        todayCal.setTime(new Date());
        dateCal.setTime(date);

        // 1.比较当前日期在年份中的周数是否相同
        if (todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }


    /**
     * 判断俩日期是否为同一年
     *
     * @param targetTime
     * @param compareTime
     * @return boolean
     */
    public static boolean isSameYear(Date targetTime, Date compareTime)
    {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    /**
     * 判断俩日期是否为同一年
     *
     * @param targetTime
     * @param compareTime
     * @return boolean
     */
    public static boolean isSameYear(long targetTime, long compareTime)
    {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTimeInMillis(targetTime);
        int tarYear = tarCalendar.get(Calendar.YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTimeInMillis(compareTime);
        int comYear = compareCalendar.get(Calendar.YEAR);

        return tarYear == comYear;
    }

    /**
     * 判断俩日期是否为同一月
     *
     * @param targetTime
     * @param compareTime
     * @return boolean
     */
    public static boolean isSameMonth(Date targetTime, Date compareTime)
    {
        if(!isSameYear(targetTime,compareTime)){
            return false;
        }
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarMonth = tarCalendar.get(Calendar.MONTH);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comMonth = compareCalendar.get(Calendar.MONTH);

        return tarMonth == comMonth;
    }

    /**
     * 判断俩日期是否为同一月
     *
     * @param targetTime
     * @param compareTime
     * @return boolean
     */
    public static boolean isSameMonth(long targetTime, long compareTime)
    {
        if(!isSameYear(targetTime,compareTime)){
            return false;
        }
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTimeInMillis(targetTime);
        int tarMonth = tarCalendar.get(Calendar.MONTH);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTimeInMillis(compareTime);
        int comMonth = compareCalendar.get(Calendar.MONTH);

        return tarMonth == comMonth;
    }

    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate=sdf.parse(sdf.format(smdate));
            bdate=sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static int daysBetween(long smdate,long bdate)
    {
        long between_days=(bdate-smdate)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static boolean isSameDay(long smdate,long bdate)
    {
        long between_days=(bdate-smdate)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days))==0;
    }

    public static long daysBetweenInMillies(Date smdate, Date bdate)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            smdate=sdf.parse(sdf.format(smdate));
            bdate=sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1);

        return between_days;
    }

    /**
     * 计算俩日期相差的天数(俩日期需为同一年)
     *
     * @param targetTime
     * @param compareTime
     * @return int 若为负数,则targetTime比compareTime日期要早.
     */
    public static int calculateDayStatus(Date targetTime, Date compareTime)
    {
        Calendar tarCalendar = Calendar.getInstance();
        tarCalendar.setTime(targetTime);
        int tarDayOfYear = tarCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTime(compareTime);
        int comDayOfYear = compareCalendar.get(Calendar.DAY_OF_YEAR);

        return tarDayOfYear - comDayOfYear;
    }


    public static String getSendTimeDistance(long sendTime)
    {
        String timeDistance = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date d2 = df.parse(parseToString(sendTime));
            Date d1 = df.parse(parseToString(System.currentTimeMillis()));
            long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

            // 判断时间是否大于一天
            if (diff < (24 * 60 * 60 * 1000))
            {
                // 判断时间是否大于一小时
                if (diff > (60 * 60 * 1000))
                {
                    timeDistance = DateUtil.parseToHHString(diff) + "小时前";
                }
                else
                {
                    timeDistance = DateUtil.parseTommString(diff) + "分钟前";
                }
            }
            else
            {
                timeDistance = DateUtil.parseToMD(sendTime);
            }
        }
        catch (Exception e)
        {
            Log.e("", "计算时间错误");
        }
        return timeDistance;
    }

    public static long getMinuteDistance(long s1,long s2){
        long distance = 0;
        try{
            long diff = s1-s2;
            distance = diff/60/1000;
        }catch (Exception e){
            Log.e("DateUtil","getHourDistance parse long error");
        }
        return distance;
    }

    public static long getMinuteDistance(String s1, String s2){
        long distance = 0;
        try{
            long diff = Long.parseLong(s1)-Long.parseLong(s2);
            distance = diff/60/1000;
        }catch (Exception e){
            Log.e("DateUtil","getHourDistance parse long error");
        }
        return distance;
    }

    public static long getHourDistance(String s1, String s2){
        long distance = 0;
        try{
            long diff = Long.parseLong(s1)-Long.parseLong(s2);
            distance = diff/60/60/1000;
        }catch (Exception e){
            Log.e("DateUtil","getHourDistance parse long error");
        }
        return distance;
    }

    public static String transformToShow(String dateText){
        if(TextUtils.isEmpty(dateText))return "";
        Date date = stringToMMDate(dateText);
        if(date!=null){
            return transformToShow(date.getTime());
        }else {
            return "";
        }
    }

    /**
     * 将时间戳转换为用于显示的格式(单位为豪秒)
     *
     * @param timestampInMillis
     * @return String 一般型如"MM-dd HH:mm"的格式
     */
    public static String transformToShow(long timestampInMillis)
    {
        String timeStr;
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        Date date = new Date(timestampInMillis);
        Date currentDate = new Date();
        long durationTime = currentDate.getTime() - date.getTime();
//        if(durationTime<0){
//            durationTime = Math.abs(durationTime);
//        }
        if (durationTime>=0 && durationTime <= 1 * ONE_MINUTE_MILLIS) // 十分钟内
        {
            timeStr = "刚刚";
        }
        else if (durationTime>=0 && durationTime < ONE_HOUR_MILLIS) // 一时内
        {
            timeStr = (durationTime / ONE_MINUTE_MILLIS) + "分钟前";
        }
        else if (isSameYear(date, currentDate))
        {
            int dayStatus = calculateDayStatus(date, currentDate);
            if (dayStatus == 0) // 今天
            {
//                timeStr = (durationTime / ONE_HOUR_MILLIONS) + "小时前";
                sdf.applyPattern("HH:mm");
                timeStr = "今天 "+sdf.format(date);
            }
            else if (dayStatus == -1) // 昨天
            {
                sdf.applyPattern("HH:mm");
                timeStr = "昨天 " + sdf.format(date);
            }else if(dayStatus>=-7 && isSameWeekWithToday(date)){
                sdf.applyPattern("EEEE");
                String week = sdf.format(date);
                sdf.applyPattern("HH:mm");
                timeStr = week + " "+sdf.format(date);
            }
            else
            {
                sdf.applyPattern("yyyy-MM-dd HH:mm");
                timeStr = sdf.format(date);
            }
        }
        else
        {
        sdf.applyPattern("yyyy-MM-dd HH:mm");
            timeStr = sdf.format(date);
        }

        return timeStr;
    }

    public static boolean isExchangeTime(long curTime){
        Calendar calendar = Calendar.getInstance();
        int curDay =  calendar.get(Calendar.DAY_OF_WEEK);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMin = calendar.get(Calendar.MINUTE);
        Log.i("DataUtil","curDay:"+curDay+" curHour:"+curHour+" curMin:"+curMin);
        if(curDay==1 || curDay==7){//星期日或六
            return false;
        }
        if(curHour<9 || curHour >=15){
            return false;
        }
        if(curHour == 9 && curMin<15){
            return false;
        }
        return true;
    }
}