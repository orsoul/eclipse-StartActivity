package com.fanfull.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式工具
 */
public class DateUtils {

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
	 * @param milliseconds
	 *            指定时间
	 * @param format
	 *            时间格式,例如:yyyy-MM-dd HH:mm:ss
	 * @return 指定时间的 字符串形式; 若 format 格式非法, 返回 null
	 */
	public static String getStringTime(long milliseconds, String format) {
		Date date = new Date(milliseconds);
		return getStringTime(date, format);
	}

	/**
	 * 将字符串形式的时间 转成 Date
	 * 
	 * @param strTime
	 *            字符串的形式的时间,如:2000-05-30 16:25:55
	 * @param format
	 *            时间的字符串形式,如:yyyy-MM-dd HH:mm:ss
	 * @return Date
	 */
	public static Date parseString2Date(String strTime, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format,
				Locale.getDefault());
		// 从 第0个字符开始解析
		ParsePosition pos = new ParsePosition(0);
		Date date = formatter.parse(strTime, pos);
		return date;
	}

	/**
	 * 将字符串形式的时间 转成 milliseconds
	 * 
	 * @param strTime
	 *            字符串的形式的时间,如:2000-05-30 16:25:55
	 * @param format
	 *            时间的字符串形式,如:yyyy-MM-dd HH:mm:ss
	 * @return milliseconds
	 */
	public static long parseString2Milliseconds(String strTime, String format) {
		return parseString2Date(strTime, format).getTime();
	}

	public static void main(String[] args) {

	}

	public static String getFormatDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssS");
		String time = format.format(new Date());
		return time;
	}
}
