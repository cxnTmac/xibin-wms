package com.xibin.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 * 
 * @copyright © 2016 大连骏骁网络科技有限公司
 * @author 骏骁
 * @createDate 2016-1-24
 * @version: V1.0.0
 */
public class DateUtil extends DateUtils {

	public static String YMD = "yyyy-MM-dd";

	public static String YMDHSM = "yyyy-MM-dd HH:mm:ss";

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
			"yyyy.MM.dd HH:mm", "yyyy.MM" };

	/**
	 * 获取当前日期
	 */
	public static Date getNow() {
		return new Date();
	}

	/**
	 * 日期型字符串转化
	 */
	public static Date parseDate(String str) {
		if (str == null) {
			return null;
		}
		try {
			return parseDate(str, parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取日期字符串
	 */
	public static String formatDate(Date date) {
		return format(YMD, date);
	}

	/**
	 * 获取时分秒时间戳
	 * 
	 * @return （HH:mm:ss）
	 */
	public static Object getTime(Date date) {
		try {
			String s = DateFormatUtils.format(date, "HH:mm:ss");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			Date date1 = simpleDateFormat.parse(s);
			long ts = date1.getTime();
			return String.valueOf(ts);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取时间字符串
	 */
	public static String formatTime(Date date) {
		return format(YMDHSM, date);
	}

	public static String format(Date date, String format) {
		return format(format, date);
	}

	public static final String format(final String format, final Date date) {
		return DateFormatUtils.format(date, format);
	}

	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 *            开始时间
	 * @param after
	 *            结束时间
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24) + 1;
	}

	/**
	 * 日期路径yyyyMMdd
	 */
	public static final String datePath() {
		return DateFormatUtils.format(getNow(), "yyyy/MM/dd/");
	}

	// 获得本周一0点时间
	public static Date getWeek1() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

	// 获得本周日24点时间
	public static Date getWeek7() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getWeek1());
		cal.add(Calendar.DAY_OF_WEEK, 7);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	// 获得本月第一天0点时间
	public static Date getMonthDay1() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	// 获得本月最后一天24点时间
	public static Date getMonthDays() {
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	/**
	 * 获取当月开始时间戳
	 *
	 * @param timeStamp
	 *            毫秒级时间戳
	 * @param timeZone
	 *            如 GMT+8:00
	 * @return
	 */
	public static Long getMonthStartTime(Long timeStamp, String timeZone) {
		Calendar calendar = Calendar.getInstance();// 获取当前日期
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.setTimeInMillis(timeStamp);
		calendar.add(Calendar.YEAR, 0);
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取当月的结束时间戳
	 *
	 * @param timeStamp
	 *            毫秒级时间戳
	 * @param timeZone
	 *            如 GMT+8:00
	 * @return
	 */
	public static Long getMonthEndTime(Long timeStamp, String timeZone) {
		Calendar calendar = Calendar.getInstance();// 获取当前日期
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.setTimeInMillis(timeStamp);
		calendar.add(Calendar.YEAR, 0);
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTimeInMillis();
	}

}
