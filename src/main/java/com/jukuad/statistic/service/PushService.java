package com.jukuad.statistic.service;

import java.util.List;

import com.jukuad.statistic.pojo.AdDayStatistic;
import com.jukuad.statistic.pojo.AppDayStatistic;
import com.jukuad.statistic.pojo.DaySum;

public interface PushService 
{
	/**
	 * 将每天的统计数据入库
	 * @param list
	 */
	void writeDayDataToMysql(List<AppDayStatistic> applist,List<AdDayStatistic> adlist);
	
	/**
	 * 保存每天的总数据
	 * @param sum
	 */
	void writeDaySumToMysql(DaySum sum);
	
	
	/**
	 * 一次解析三条数据
	 * @param applist
	 * @param adlist
	 * @param sum
	 */
	void writeDayDataToMysql(List<AppDayStatistic> applist,List<AdDayStatistic> adlist,DaySum sum);
}
