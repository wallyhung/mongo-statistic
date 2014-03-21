package com.jukuad.statistic.service;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface MongoService<T>
{
	/**
	 * 请求、、推送、展示
	 * 结果保存临时统计文档中
	 * @param name
	 * @param database  map collection
	 */
	void executeMapReduce(Class<T> name,String database);
	
	/**
	 * 应用的点击、下载、安装的统计
	 * 统计点击数：先分为cpc和cpa，再在cpa中分积分墙推荐应用和非积分墙推荐应用
	 * 统计下载数：直接在cpa中分积分墙和非积分墙
	 * 统计安装数：直接在cpa中分积分墙和非积分墙
	 * @param name
	 * @param database
	 */
	void executeAppByAdTypeMapReduce(Class<T> name,String database);
	
	/**
	 * 对某一天的临时数据做汇总map
	 * 结果保存一天的统计文档中
	 * @param date
	 */
	void executeTempMapReduce(Date date);
	
	
	/**
	 * 根据标示统计一天的用户数据
	 * 
	 * adid 统计一天广告的终端数
	 * fid  统计一天应用的终端新增数、留存、日活数
	 * 
	 * @param date
	 * @param id
	 * @return
	 */
	Integer[] statisticDayUserData(Date date,String id,String database);
	
	/**
	 * 查询某天新增的应用数
	 * @param name
	 * @param database
	 * @param date
	 * @return
	 */
	long queryNewAppsCount(String database,Date date);
	
	
	/**
	 * 获取符合条件的去重集合
	 * @param name
	 * @param paramMap
	 * @param database
	 * @return
	 */
	List<T>  queryDistinctDocument(Class<T> name, Map<String,Object> paramMap,String database);
	
	
 	
	/**
	 * 统计一天的数据
	 * @param name
	 * @param database
	 */
	void executeDayMapReduce(Class<T> name,String database,Date date);
	
	/**
	 * 查询每日的统计结果
	 * @param date
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List queryDayStatistic(Date date);

}
