package com.jukuad.statistic.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.config.MongoDBDataStore;
import com.jukuad.statistic.pojo.AdResult;
import com.jukuad.statistic.pojo.DaySum;
import com.jukuad.statistic.util.TimeUtil;

public class BaseServiceImpl<T> implements BaseService<T> 
{
	private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);
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
	
	@Override
	public AdResult queryLastData(String database) 
	{
		AdResult last = null;
		Datastore ds = MongoDBDataStore.getDBInstance(database);
		Query<AdResult> query = ds.find(AdResult.class).order("-time").limit(1);
		List<AdResult> res = query.asList();
		if(res.size() > 0) last = res.get(0);
		return last;
	}
	
	@Override
	public boolean validateStatisticLastHourLog(String database,long startLongTime) 
	{
		boolean bool = true;
		AdResult obj = this.queryLastData(database);
		if(obj != null)
		{
			if(obj.getTimestamp() > startLongTime) bool = false;
		}
		return bool;
	}
	
	@Override
	public List<T> queryTempStatisticList(Class<T> name,String database,Date date) 
	{
		List<T> res = null;
		Datastore ds = MongoDBDataStore.getDBInstance(database);
		Query<T> query = ds.createQuery(name);
		if(date != null)
		{
			logger.debug("广告每小时库查询数据，开始时间：{}，结束时间：{}。",TimeUtil.getDayStart(date).getTime(),TimeUtil.getDayEnd(date).getTime());
			query.field("time").greaterThanOrEq(TimeUtil.getDayStart(date).getTime());
			query.field("time").lessThan(TimeUtil.getDayEnd(date).getTime());
			res = query.asList();
		}
		logger.debug("广告每小时库查询数据，结果数：{}。",res == null ? 0 : res.size());
		return res;
	}
	
	public static void main(String[] args) {
		BaseService<AdResult> service = new BaseServiceImpl<AdResult>();
		AdResult result = service.queryLastData(MongoDBConnConfig.DATABASE_STATISTIC);
		System.out.println(result.getAdid() + "---------" + result.getPush() + "----------" + result.getTimestamp());
		
	}
}
