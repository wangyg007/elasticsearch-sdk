/**
 *
 */
package com.poly.demo.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 时间处理的工具类
 *
 * @author wagnyg
 * @version 1.0
 * @created 2019-4-3
 */
@Slf4j
public class DateUtil {

    private DateUtil(){

    }

    protected static final Timestamp ZERO_TIMESTAMP = new Timestamp(0);
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    public static Date str2Date(String str, String pattern) {
        Date date = null;
        DateFormat format;
        try {
            format = new SimpleDateFormat(pattern, Locale.CHINESE);
            date = format.parse(str);
        } catch (Exception e) {
            try {
                format = new SimpleDateFormat(pattern, Locale.ENGLISH);
                date = format.parse(str);
            } catch (Exception e2) {
                log.error("str2Date error:",e2);
            }
        }
        return date;
    }

    public static Date str2Date(String str) {
        return str2Date(str, FORMAT_DATE);
    }

    /**
     * @param str
     * @return
     */
    public static Date str2DateTry(String str) {
        try {
            return parseDate(str, new String[]{
                    FORMAT_DATETIME + ".SSS", FORMAT_DATETIME, FORMAT_DATE, "MM-dd", "EEE MMM dd HH:mm:ss z yyyy"
            });
        } catch (Exception e) {
            log.error("str2DateTry error:",e);
        }
        return null;
    }

    /**
     * @param str
     * @param patterns
     * @return
     */
    private static Date parseDate(String str, String[] patterns) {
        Date date = null;
        for (String pattern : patterns) {
            date = str2Date(str, pattern);
            if (date != null) {
                return date;
            }
        }
        return date;
    }

    public static Date str2DateTime(String str) {
        return str2Date(str, FORMAT_DATETIME);
    }

    public static Timestamp str2Timestamp(String str) {
        Timestamp rs = null;
        try {
            rs = Timestamp.valueOf(str);
        } catch (Exception e) {
            Date date = str2DateTry(str);
            if (date != null) {
                rs = new Timestamp(date.getTime());
            }
            log.error("str2Timestamp error:",e);
        }
        return rs;
    }

    public static Timestamp toTimestamp(Date date) {
        if (null==date){
            return null;
        }
        return new Timestamp(date.getTime());
    }

    public static String date2Str(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        String str = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            str = format.format(date);
        } catch (Exception e) {
            log.error("date2Str error:",e);
        }
        return str;
    }

    public static String date2Str(Date date){
        return date2Str(date, FORMAT_DATE);
    }

    public static String datetime2Str(Date date) {
        return date2Str(date, FORMAT_DATETIME);
    }

    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp nowDay() {
        return toTimestamp(getToday());
    }

    public static Date getToday() {
        return formatDate(new Date(), FORMAT_DATE);
    }

    public static Date formatDate(Date date) {
        return formatDate(date, FORMAT_DATE);
    }

    public static Timestamp formatDate(Timestamp date) {
        return formatTimestamp(date, FORMAT_DATE);
    }

    public static Date formatDatetime(Date date) {
        return formatDate(date, FORMAT_DATETIME);
    }

    public static Date formatDate(Date date, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(format.format(date));
        } catch (ParseException e) {
            log.error("formatDate error:",e);
        }
        return null;
    }

    public static Timestamp formatTimestamp(Timestamp date, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date parse = format.parse(format.format(date));
            return toTimestamp(parse);
        } catch (ParseException e) {
            log.error("formatTimestamp error:",e);
        }
        return null;
    }

    public static Date add(Date date, TimeUnit unit, long amount) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MILLISECOND, (int) unit.toMillis(amount));
            return c.getTime();
        } catch (Exception e) {
            log.error("add1 error:",e);
        }
        return null;
    }

    public static Date add(Date date, int field, int amount) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(field, amount);
            return c.getTime();
        } catch (Exception e) {
            log.error("add2 error:",e);
        }
        return null;
    }

    public static Timestamp add(Timestamp date, int field, int amount) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(field, amount);
            return new Timestamp(c.getTimeInMillis());
        } catch (Exception e) {
            log.error("add3 error:",e);
        }
        return null;
    }

    public static Date addYear(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static Date addMonth(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    public static Date addDay(Date date, int amount) {
        return add(date, Calendar.DATE, amount);
    }

    public static Date addWeek(Date date, int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    public static Date addHour(Date date, int amount) {
        return add(date, Calendar.HOUR, amount);
    }

    public static Date addMinute(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    public static Date addSecond(Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    public static Date getMonthHead(Date date) {
        return formatDate(date, "yyyy-MM-01");
    }

    public static Date getMonthTail(Date date, String pattern) {
        return formatDate(addSecond(addMonth(getMonthHead(date), 1), -1), pattern);
    }

    public static boolean isSameDay(Date date1, Date date2) {//date2Str(date1)
        String ds1 = date2Str(date1);
        String ds2 = date2Str(date2);
        if(StringUtils.isNotEmpty(ds1) && StringUtils.isNotEmpty(ds2)){
            return ds1.equalsIgnoreCase(ds2);
        }
        return false;
    }

    /**
     * date1 - date2的时间差
     *
     * @param date1
     * @param date2
     * @param unit
     * @return
     */
    public static long getTimeDiff(Date date1, Date date2, TimeUnit unit) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("date1:" + date1 + ", date2:" + date2 + " both cannot be null!!");
        }
        return unit.convert(date1.getTime() - date2.getTime(), TimeUnit.MILLISECONDS);
    }

    public static long getTimeDiff(Timestamp date1, Timestamp date2, TimeUnit unit) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("date1:" + date1 + ", date2:" + date2 + " both cannot be null!!");
        }
        return unit.convert(date1.getTime() - date2.getTime(), TimeUnit.MILLISECONDS);
    }

}
