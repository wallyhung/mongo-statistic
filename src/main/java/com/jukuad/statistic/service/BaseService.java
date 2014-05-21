package com.jukuad.statistic.service;

import java.util.List;

public interface BaseService<T,K> 
{
	/**
	 * 查询数据库排序好后的第一个实体
	 * @param order
	 * @return
	 */
	T findNewest(String order);
	
	/**
	 * 查询数据库一小时的数据
	 * @param hour
	 * @return
	 */
	List<T> findByHour(String hour);
	
	/**
	 * 查询一段时间戳内的数据
	 * @param start
	 * @param end
	 * @return
	 */
	List<T> findBetweenTime(long start,long end);
	
	/**
	 * 查询一段时间内的数据（hour，day）
	 * @param start
	 * @param end
	 * @param fields （hour，day）
	 * @return
	 */
	List<T> findBetweenStringTime(String start,String end,String fields);
	
	/**
	 * 查询数据库一天的数据
	 * @param day
	 * @return
	 */
	List<T> findByDay(String day);
	
	/**
	 * 复制集合
	 * @param target
	 */
	void copyCollection(String target);
	
	
	/************************解析日志公共接口**************************/
	String getLogPath(String hour,String type,String root);
	
	boolean existNewLogs(String hour,String type,String root);
	
	boolean existNewLogs(String hour,String type);
	
	int getTaskCount(String hour);
	
	void parseAndSaveAndCopy(String hour,String type,String root);
	
	void parseAndSaveAndCopy(String hour,String type);
	
	
}
