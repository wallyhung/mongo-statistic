package com.jukuad.statistic.service;

public interface WebStatistic 
{
	/**
	 * 统计一天某个用户的所有应用情况
	 * @param userid
	 * @param time
	 * @return
	 */
	public String dayStatisticForAllApp(String userid,String time);
	
	
	/**
	 * 统计某个用户的某个应用的情况
	 * @param userid
	 * @param appid
	 * @param time
	 * @return
	 */
	public String dayStatisticForOneApp(String userid,String appid,String time);
	
	/**
	 * 统计某个用户的一段时间内的所有应用情况
	 * @param userid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String dayStatisticForAllAppSomeDays(String userid,String startTime,String endTime);
	
	/**
	 * 统计某个用户的一段时间内的一个应用情况
	 * @param userid
	 * @param appid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String dayStatisticForOneAppSomeDays(String userid,String appid,String startTime,String endTime);
	
	
	/**
	 * 统计某个用户的一个月内所有应用情况
	 * @param userid
	 * @param month
	 * @return
	 */
	public String monthStatisticForAllAppOneMonth(String userid,String month);
	
	/**
	 * 统计某个用户的一个月内某个应用的情况
	 * @param userid
	 * @param appid
	 * @param month
	 * @return
	 */
	public String monthStatisticForOneAppOneMonth(String userid,String appid,String month);
	
	/**
	 * 统计某个用户的某几个月份的所有应用情况
	 * @param userid
	 * @param startMonth
	 * @param endMonth
	 * @return
	 */
	public String monthStatisticForAllAppSeveralMonth(String userid,String startMonth,String endMonth);
	
	/**
	 * 统计某个用户的某几个月份的某一个应用情况
	 * @param userid
	 * @param appid
	 * @param startMonth
	 * @param endMonth
	 * @return
	 */
	public String monthStatisticForOneAppSeveralMonth(String userid,String appid,String startMonth,String endMonth);

}
