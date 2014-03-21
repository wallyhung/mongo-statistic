package com.jukuad.statistic.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.config.MongoDBDataStore;
import com.jukuad.statistic.pojo.DaySum;
import com.jukuad.statistic.util.TimeUtil;

public class BaseServiceImpl<T> implements BaseService<T> 
{
	@Override
	public Query<T> query(Class<T> name, Map<String, Object> paramMap,String database) 
	{
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        //这里特别注意key和条件关系式"="之间要有空格  
        Query<T> query = ds.createQuery(name);
        if(paramMap.get("fid") != null)
        	query.field("fid").equal(paramMap.get("fid"));
        if(paramMap.get("start") != null)
        	query.field("time").greaterThanOrEq(paramMap.get("start"));
        if(paramMap.get("end") != null)
        	query.field("time").lessThan(paramMap.get("end"));
        return query;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public List queryDayStatistic(Date date) {
		return null;
	}
	
	@Override
	public DaySum queryDaySum(Date date) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
		return ds.find(DaySum.class, "day", TimeUtil.getDay(date)).get();
	}
}
