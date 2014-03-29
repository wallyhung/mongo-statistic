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
	 * 获取某一刻的具体时间
	 * @param date
	 * @return
	 */
	public static String getDayTime(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}
	
	
	/**
	 * 获取某一天小时的时间字符串
	 * @param date
	 * @return
	 */
	public static String getDayHour(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
		String str = format.format(date);
		return str;
	}
	
	/**
	 * 获取上一个小时的字符串时间
	 * @param date
	 * @return
	 */
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
	 * 获取上一个小时开始时间的时间戳
	 * @param date
	 * @return
	 */
	public static long getDayLastHourTimestamp(Date date)
	{
		return getDayLastHourDate(date).getTime();
	}
	
	/**
	 * 获取上一个小时开始时间的日期时间
	 * @param date
	 * @return
	 */
	public static Date getDayLastHourDate(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
		String str = format.format(calendar.getTime());
		try 
		{
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	
	/**
	 * 转换小时字符串为Date
	 * @param hour
	 * @return
	 */
	public static Date getDayFromHourString(String hour)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
		Date date = null;
		try 
		{
			date = format.parse(hour);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
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
		return getDayEnd(getLastDay(date));
	}
	
	
	public static Date getLastDay(Date date)
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
	
	/**
	 * 获取两个时间相差的中间小时字符串
	 * @param time
	 * @param current
	 * @return
	 */
	public static String[] getDistanceTimeHourArray(Date time,Date current)
	{
	     String timeHour = getDayHour(time);
	     String curHour = getDayHour(current);
	     int thour = Integer.parseInt(timeHour.substring(timeHour.length()-2,timeHour.length()));
	     int chour = Integer.parseInt(curHour.substring(timeHour.length()-2,curHour.length()));
//	     int size = chour - (thour + 1) ;
	     String[] str = new String[24];
	     int index = 0;
	     for (int i = thour; i < chour + 1; i++) {
	    	 String s = timeHour.substring(0, timeHour.length()-2) + getTwoHourNumber(i);
	    	 str[index] = s;
	    	 index++;
		 }
		return str;
	}
	
	private static String getTwoHourNumber(int i)
	{
		String res = Integer.valueOf(i).toString();
		if(res.length() == 1) res = "0" + res; 
		return res;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(getDayStart(new Date()));
		System.out.println(getDayEnd(new Date()));
		
		System.out.println("--------------");
		System.out.println(getLastDay(new Date()));
		System.out.println(getLastDayEnd(new Date()));
		System.out.println("--------------");
		
		
	    System.out.println(getDayLastHour(new Date()));
	    System.out.println(getDayLastHourDate(new Date()));
	    System.out.println(getDayLastHourTimestamp(new Date()));
	    System.out.println("--------------");
	    
	    System.out.println(StrToDate("2014-03-04 00:00:00").getTime()-StrToDate("2014-03-01 00:00:00").getTime());
	    String[] ss = getDistanceTimeHourArray(StrToDate("2014-03-22 07:30:00"), new Date());
	    for (String string : ss) {
			System.out.println(string);
		}
	    
	    
	    System.out.println(getDayFromHourString("2014-03-28-10"));
	}
	
   
}
