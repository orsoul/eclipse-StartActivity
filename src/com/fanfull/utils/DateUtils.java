package com.fanfull.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.SystemClock;

/*作用：格式化时间戳  将从1970-01-01到现在的时间戳格式化为2016-03-08 00的格式 年月日 时 
 * author:zyp
 * time: 2016-03-08
 * 
 * 返回值 是一个格式化后的字符串*/
public class DateUtils {

	/**
	 * 从服务器同步系统时间
	 * 
	 * @param str
	 */
	public static void syncDateTime(String str) {
		if (null == str) {
			return;
		}
		try {
			Calendar c = Calendar.getInstance();
			// 年 月-1 日 时 分 秒
			int year = Integer.parseInt(str.substring(0, 4));
			int month = Integer.parseInt(str.substring(4, 6));
			int day = Integer.parseInt(str.substring(6, 8));
			int hour = Integer.parseInt(str.substring(8, 10));
			int minutes = Integer.parseInt(str.substring(10, 12));
			int seconds = Integer.parseInt(str.substring(12, 14));
			c.set(year, month - 1, day, hour, minutes, seconds);
			long when = c.getTimeInMillis();
			if (when / 1000 < Integer.MAX_VALUE) {
				SystemClock.setCurrentTimeMillis(when); // 需要系统权限才能执行
			} else {
				LogsUtil.e("同步时间失败");
			}
		} catch (NumberFormatException e) {
			// TODO: handle exception
			LogsUtil.e("同步时间格式化数据失败");
		} catch (ExceptionInInitializerError e) {
			// TODO: handle exception
			LogsUtil.e("同步时间格式化数据失败");
		}

	}
	
	/** 标准的 时间字符串格式: "yyyy-MM-dd HH:mm:ss" */
	public final static String FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * @param date
	 *            指定时间, 为null表示当前时间
	 * @param format
	 *            时间格式,例如:yyyy-MM-dd HH:mm:ss
	 * @return 指定时间的 字符串形式; 若 format 格式非法, 返回 null
	 */
	public static String getStringTime(Date date, String format) {
		SimpleDateFormat formatter = null;
		try {
			formatter = new SimpleDateFormat(format, Locale.getDefault());
		} catch (Exception e) {
			// format 格式非法
			return null;
		}
		if (null == date) {
			date = new Date();
		}
		String dateString = formatter.format(date);
		return dateString;
	}
	/**
	 * @param milliseconds 指定时间
	 * @param format 时间格式,例如:yyyy-MM-dd HH:mm:ss
	 * @return 指定时间的 字符串形式; 若 format 格式非法, 返回 null
	 */
	public static String getStringTime(long milliseconds, String format) {
		Date date = new Date(milliseconds);
		return getStringTime(date, format);
	}
	/**
	 * 将字符串形式的时间 转成 Date
	 * @param strTime
	 *            字符串的形式的时间,如:2000-05-30 16:25:55
	 * @param format
	 *            时间的字符串形式,如:yyyy-MM-dd HH:mm:ss
	 * @return Date
	 */
	public static Date parseString2Date(String strTime, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
		// 从 第0个字符开始解析
		ParsePosition pos = new ParsePosition(0);
		Date date = formatter.parse(strTime, pos);
		return date;
	}
	/**
	 * 将字符串形式的时间 转成 milliseconds
	 * @param strTime 字符串的形式的时间,如:2000-05-30 16:25:55
	 * @param format 时间的字符串形式,如:yyyy-MM-dd HH:mm:ss
	 * @return milliseconds
	 */
	public static long parseString2Milliseconds (String strTime, String format) {
		return parseString2Date(strTime, format).getTime();
	}

	public static void main(String[] args) {

	}
	
	public static String getFormatDate(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
		String time = format.format(new Date());
		return time;
	}
}
