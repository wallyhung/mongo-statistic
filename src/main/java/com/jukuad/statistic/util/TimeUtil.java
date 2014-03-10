package com.jukuad.statistic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TimeUtil {
	
	
	/**
	 * 获取某一天的开始时间
	 * @param date
	 * @return
	 */
	public static Date getDayStart(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 获取某一天的结束时间
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
	
	
	/**
	 * 获取某一天的时间字符串
	 * @param date
	 * @return
	 */
	public static String getDay(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
	}
	
	/**
	 * 获取某一天的时间字符串
	 * @param date
	 * @return
	 */
	public static String getDayHour(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
		String str = format.format(date);
		return str;
	}
	
	public static String getDayLastHour(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
		String str = format.format(calendar.getTime());
		return str;
	}
	
	
	
	/**
	 * 字符串转换成日期 (Util.DATE)
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try 
		{
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 获取某天的前7天的具体日期
	 * @param date
	 * @return
	 */
	public static Date getSevenDaysBefore(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
//		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-7);
		calendar.add(Calendar.DATE, -7);    //得到前一天
		return calendar.getTime();
	}
	
	
	/**
	 * 获取某天的8天的具体日期
	 * @param date
	 * @return
	 */
	public static Date getEightDaysBefore(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-8);
		return calendar.getTime();
	}
	
	
	/**
	 * 获取某天的前一天日期
	 * @param date
	 * @return
	 */
	public static Date getLastDayEnd(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.add(Calendar.DATE, -1);    //得到前一天
		return calendar.getTime();
	}
	
	
	/**
	 * 获取系统的当前时间
	 * @return
	 */
	public static Date getCurrent()
	{
		return new Date(System.currentTimeMillis());
	}
	
	
	
	public static void main(String[] args) {
		
		Date date = StrToDate("2014-03-05 12:12:12");
		System.out.println(getDayStart(date));
		System.out.println(getDayEnd(date));
		System.out.println(getSevenDaysBefore(date));
		System.out.println(getLastDayEnd(date));
	    System.out.println(getDayLastHour(StrToDate("2013-01-03 1:30:12")));
	    
	    System.out.println(StrToDate("2014-03-04 00:00:00").getTime()-StrToDate("2014-03-01 00:00:00").getTime());
	    System.out.println();
	}
	
   
}
