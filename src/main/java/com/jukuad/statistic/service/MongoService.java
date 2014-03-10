package com.jukuad.statistic.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

import com.jukuad.statistic.pojo.DayStatistic;
import com.jukuad.statistic.pojo.DaySum;
import com.mongodb.DBCollection;


public interface MongoService<T>
{
	/**
	 * 查询符合条件的结果集
	 * @param name
	 * @param paramMap(三个key：fid,start,end)
	 * @return
	 */
	Query<T> query(Class<T> name, Map<String,Object> paramMap,String database);
	
	/**
	 * 根据fid查询数据
	 * @param name
	 * @param fid
	 * @param database
	 * @return
	 */
	Query<T> query(Class<T> name, String fid,String database);
	
	/**
	 * 查询符合条件结果集的去重集合
	 * @param name
	 * @param paramMap
	 * @param database
	 * @return
	 */
	List<T> queryDistinctDoc(Class<T> name, Map<String,Object> paramMap,String database);
	
	
	DBCollection queryCollection(Class<T> name, Map<String,Object> paramMap,String database);
	
	/**
	 * 推送数、展示、点击、下载、安装等数据临时做map
	 * 结果保存临时统计文档中
	 * @param name
	 * @param database  map collection
	 * @param mapper    分组标示
	 */
	void executeMapReduce(Class<T> name,String database,String mapper);
	
	/**
	 * 对请求数据临时做map
	 * 结果保存到临时统计文档中
	 * @param name
	 * @param database
	 */
	void executeRequestMapReduce(Class<T> name,String database);
	
	
	/**
	 * 根据mapper做一天的数据总统计
	 * @param name
	 * @param database
	 * @param mapper
	 */
	void executeDayMapReduce(String database,String mapper,Date date);
	
	/**
	 * 每日统计数据
	 * 统计应用的新增数、应用的展示数、广告的推送数、点击数、终端数
	 * 结果保存到daysum文档中
	 * @param date
	 */
	void daySatistic(Date date);
	
	/**
	 * 对某一天的临时数据做汇总map
	 * 结果保存一天的统计文档中
	 * @param date
	 */
	void executeTempMapReduce(Date date,String mapper);
	
	
	Key<T> saveEntity(Class<T> name,String database,T entity);
	
	void updateEntity(Class<T> name,String database,String fid,String pro,Object proValue);
	
	
	/**
	 * 查询某天新增的应用数
	 * @param name
	 * @param database
	 * @param date
	 * @return
	 */
	long queryNewAppsCount(String database,Date date);
	
	
	/**
	 * 分别查询两种统计的统计结果
	 * @param date
	 * @param mapper
	 * @return
	 */
	List<DayStatistic> queryDayStatistic(Date date,String mapper);
	
	
	/**
	 * 两种统计结果一次查询
	 * @param date
	 * @return
	 */
	List<DayStatistic> queryDayStatistic(Date date);
	
	/**
	 * 查询一天的总统计
	 * （包括应用的新增数、展示数、广告的推送、点击、下载、安装、终端数等）
	 * @param date
	 * @return
	 */
	DaySum queryDaySum(Date date);

}
