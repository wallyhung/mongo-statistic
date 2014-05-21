package com.jukuad.statistic.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
	 * 获取下一个小时开始时间的日期时间
	 * @param date
	 * @return
	 */
	public static Date getDayNextHourDate(String hour)
	{
		Date date = getDayFromHourString(hour);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.add(Calendar.HOUR_OF_DAY, 1);
		return calendar.getTime();
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
		calendar.add(Calendar.DATE, -7);    
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
	 * 获取一个月前的时间
	 * @param date
	 * @return
	 */
	public static Date getOneMonthBefore(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)-30);
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
	
	/**
	 * 获取两个小时字符串相差的小时数组
	 * @param hour        当前小时
	 * @param dataHour    数据库数据的小时记录
	 * @return
	 */
	public static List<String> getDistanceTimeHourArray(String hour,String dataHour)
	{
		List<String> res = new ArrayList<String>();
		String[] s1 = hour.split("-");
		String[] s2 = dataHour.split("-");
		int day1 = Integer.parseInt(s1[2]);
		int day2 = Integer.parseInt(s2[2]);
		int hour1 = Integer.parseInt(s1[3]);
		int hour2 = Integer.parseInt(s2[3]);
		if(day1 == day2)
		{
			for (int i = hour2 + 1; i < hour1 + 1; i++) {
				String s = hour.substring(0, hour.length()-2) + getTwoHourNumber(i);
				res.add(s);
			}
		}
		else
		{
			for(int day = day2; day < day1 + 1;day++)
			{
				String dayFormat = hour.substring(0, hour.length()-5) + getTwoHourNumber(day) + "-";
				int start = 0;
				int end = 24;
				if(day == day2) start = hour2 + 1;
				if(day == day1) end = hour1 + 1;
				for (int i = start; i < end; i++) 
				{
					String s = dayFormat + getTwoHourNumber(i);
					res.add(s);
				}
			}
		}
		return res;
	}
	
	
	private static String getTwoHourNumber(int i)
	{
		String res = Integer.valueOf(i).toString();
		if(res.length() == 1) res = "0" + res; 
		return res;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(StrToDate("2014-04-23 00:00:00").getTime());
		System.out.println(StrToDate("2014-04-23 23:59:59").getTime());
//		System.out.println(getSevenDaysBefore(StrToDate("2014-04-01 23:59:59")));
//		System.out.println(getEightDaysBefore(StrToDate("2014-04-01 23:59:59")));
//		System.out.println(getOneMonthBefore(StrToDate("2014-04-01 23:59:59")));
	    
//	    System.out.println(getDayStart(new Date()).getTime());
//	    System.out.println(getDayEnd(new Date()).getTime());
//	    System.out.println(getLastDay(new Date()).getTime());
		
//		System.out.println(getDayFromHourString("2014-03-31-06"));
//		System.out.println(getDayNextHourDate("2014-03-31-06"));
		
		String hour1 = "2014-04-13-02";
		String hour2 = "2014-04-13-03";
		List<String> ss = getDistanceTimeHourArray(hour2, hour1);
		for (String string : ss) {
			System.out.println(string);
		}
		System.out.println(1398728736630l-1398726008466l);
	}
}
