package com.jukuad.statistic.service;

import java.util.List;

import com.jukuad.statistic.pojo.DayStatistic;
import com.jukuad.statistic.pojo.DaySum;

public interface PushService 
{
	/**
	 * 将每天的统计数据入库
	 * @param list
	 */
	void writeDayDataToMysql(List<DayStatistic> list);
	
	/**
	 * 保存每天的总数据
	 * @param sum
	 */
	void writeDaySumToMysql(DaySum sum);

}
