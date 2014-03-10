package com.jukuad.statistic.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.config.MongoDBDataStore;
import com.jukuad.statistic.pojo.Click;
import com.jukuad.statistic.pojo.DayStatistic;
import com.jukuad.statistic.pojo.DaySum;
import com.jukuad.statistic.pojo.Download;
import com.jukuad.statistic.pojo.Install;
import com.jukuad.statistic.pojo.Push;
import com.jukuad.statistic.pojo.Result;
import com.jukuad.statistic.pojo.View;
import com.jukuad.statistic.util.Constant;
import com.jukuad.statistic.util.TimeUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;

public class MongoServiceImpl<T> implements MongoService<T> 
{
	private static final Logger logger = LoggerFactory.getLogger(MongoService.class);
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
	
	@Override
	public Query<T> query(Class<T> name, String fid, String database) {
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        //这里特别注意key和条件关系式"="之间要有空格  
        Query<T> query = ds.createQuery(name);
        if(fid != null && !"".equals(fid))
        	query.field("fid").equal(fid);
        return query;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> queryDistinctDoc(Class<T> name,
			Map<String, Object> paramMap, String database) {
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        DBObject query = new BasicDBObject(); 
        
        if(paramMap.get("fid") != null)
        	query.put("fid", paramMap.get("fid"));
        
        if(paramMap.get("adid") != null)
        	query.put("adid", paramMap.get("adid"));
        
        BasicDBObject time = new BasicDBObject();
        if(paramMap.get("start") != null)
        	time.append("$gte", paramMap.get("start"));
        if(paramMap.get("end") != null)
        	time.append("$lt", paramMap.get("end"));
        
        query.put("time", time);  
		List<T> list = ds.getCollection(name).distinct("imei", query);
        return list;
	}
	
	@Override
	public DBCollection queryCollection(Class<T> name,
			Map<String, Object> paramMap, String database) {
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        DBObject query = new BasicDBObject(); 
        if(paramMap.get("fid") != null)
        	query.put("fid", paramMap.get("fid"));
        if(paramMap.get("start") != null)
        	query.put("time", new BasicDBObject("$gte", paramMap.get("start")));  
        if(paramMap.get("end") != null)
        	query.put("time", new BasicDBObject("$lt", paramMap.get("end")));  
        return ds.getCollection(name);
	}
	
	@Override
	public void executeMapReduce(Class<T> name,String database,String mapper) 
	{
		String map = "function(){" 
	               + "emit(this." + mapper +", {count : 1})"
				   + "}";
		
		String reduce = "function (key, values) {"
					  + "var count = 0;"
					  + "values.forEach(function(val) {"
					  + "count += val.count; "
					  + "});"
					  + "return count;"
					  +"}";
		String outputCollection = "result";
		
    	//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        DBObject query = new BasicDBObject(); 
        MapReduceOutput out = ds.getCollection(name).mapReduce(map, reduce, outputCollection, query);
        
        //处理查询结果并入库
        analyzeMapReduceResult(name,out,mapper);
	}
	
	
	/**
	 * 推送数、展示数、点击数、下载数、安装数
	 * 分小时的map统计结果入库
	 * @param name
	 * @param out
	 * @param mapper 
	 */
	private void analyzeMapReduceResult(Class<T> name, MapReduceOutput out, String mapper) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC); 
		DBCollection dc = out.getOutputCollection();
		logger.info("{}数据,mapreduce执行状况：{}",dc.getStats());
        DBCursor cursor = dc.find();
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			String id = (String) dbObject.get("_id");
			
			Double count = (Double) dbObject.get("value");
			
			//判断当前的mapper和classname是否为push,创建广告统计信息
			if("adid".equals(mapper) && Push.class.equals(name))
			{
				Result res = new Result();
				res.setAdid(id);
                res.setPush(count.longValue());
				res.setTimestamp(System.currentTimeMillis());
				Key<Result> key = ds.save(res);
				logger.info("分析广告推送数，并入库：{}",key.getId());
			}
			else
			{
				Query<Result> query = ds.createQuery(Result.class).field(mapper).equal(id);
				UpdateOperations<Result> ops = null;
				if(View.class.equals(name)) ops = ds.createUpdateOperations(Result.class).set("view", count.longValue());
				if(Click.class.equals(name)) ops = ds.createUpdateOperations(Result.class).set("click", count.longValue());
				if(Download.class.equals(name)) ops = ds.createUpdateOperations(Result.class).set("download", count.longValue());
				if(Install.class.equals(name)) ops = ds.createUpdateOperations(Result.class).set("install", count.longValue());
				ds.update(query, ops);
			}
			
		}
		
	}
	
	@Override
	public void updateEntity(Class<T> name, String database, String fid,
			String pro, Object proValue) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(database); 
		Query<T> query = ds.createQuery(name).field("fid").equal(fid);
		UpdateOperations<T> ops = ds.createUpdateOperations(name).set(pro, proValue);
		ds.update(query, ops);
	}

	@Override
	public void executeRequestMapReduce(Class<T> name, String database) 
	{
		String map = "function(){" 
	               + "emit(this.fid, {count : 1,time : this.time})"
				   + "}";
		
		String reduce = "function (key, values) {"
					  + "var res = {count : 0,time : ''};"
					  + "res.time = values[0].time;"
					  + "for (var i = 0; i < values.length; i++) {"
						    + "res.count += values[i].count; "
							+ "if (res.time > values[i].time) {"
							+ "res.time = values[i].time; "
							+"}"
						+"}"
						+"return res;"
					+"}";
		String outputCollection = "result";
		
	 	//获取数据库一个实例
	     Datastore ds = MongoDBDataStore.getDBInstance(database);  
	     DBObject query = new BasicDBObject(); 
	     MapReduceOutput out = ds.getCollection(name).mapReduce(map, reduce, outputCollection, query);
	     //处理查询结果并入库
	     analyzeMapReduceResult(out);
	}
	
	
	public void executeTempMapReduce(Date date,String mapper)
	{
		String map = "function(){" 
	               + "	emit(this."+ mapper +",{count:1,request:this.request,"
	                   + "	view:this.view,"
	                   + "	click:this.click,download:this.download,"
	                   + "install:this.install,first:this.first})"
				   + "}";
		String reduce = "function (key, values) {"
				  + "var res = {count : 0,request:0,view:0,click:0,download:0,install:0,first : ''};"
				  + "res.first = values[0].first;"	
				  + "for (var i = 0; i < values.length; i++) {"	
					  + "res.count += values[i].count;  "		     
					  + "res.request += values[i].request;  "		  
					  + "res.view += values[i].view; "			
					  + "res.click += values[i].click; 	"		
					  + "res.download += values[i].download;"	 
					  + "res.install += values[i].install; "		
						+ "if (res.first > values[i].first) {"
							+ "res.first = values[i].first; "	
							+ "}"
						+ "}"
				  + "return res;"
				+"}";
		if("adid".equals(mapper))
		{
			map = "function(){" 
		               + "	emit(this."+ mapper +",{count:1,"
		                   + "	push:this.push,"
		                   + "	click:this.click,download:this.download,"
		                   + "install:this.install})"
					   + "}";
			reduce = "function (key, values) {"
					  + "var res = {count : 0,push:0,click:0,download:0,install:0};"
					  + "for (var i = 0; i < values.length; i++) {"	
						  + "res.count += values[i].count;  "		     
						  + "res.push += values[i].push;"	 		
						  + "res.click += values[i].click; 	"		
						  + "res.download += values[i].download;"	 
						  + "res.install += values[i].install; "		
							+ "}"
					  + "return res;"
					+"}";
		}
		
		
		
		String outputCollection = "tmp";
		
	 	//获取数据库一个实例
	     Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
	     BasicDBObject sql = new BasicDBObject(); 
	     BasicDBObject query = new BasicDBObject(); 
	     //此处特别注意：不要使用put方法，否则查询无效，比如：new BasicDBObject().put("$gt", updateTime)
	     query.append("$gte", TimeUtil.getDayStart(date).getTime()).append("$lt", TimeUtil.getDayEnd(date).getTime());
	     sql.append("time", query);
	     sql.append(mapper, new BasicDBObject("$ne",null));
	     MapReduceOutput out = ds.getCollection(Result.class).mapReduce(map, reduce, outputCollection, sql);
	     //处理查询结果并入库
	     analyzeDayMapReduceResult(out,date,mapper);
	}
	
	
	/**
	 * 对一天的日志进行map操作
	 * 保存数据到日统计文档中
	 * 
	 * 注意：mapreduce的结果，如果临时的分组结果条数，只为1将不会进行reduce操作
	 * 得到的结果数请求数为long，转化保存，此处统一准话为string，再转成long
	 * 
	 * @param out
	 * @param date
	 * @param mapper 
	 */
	private void analyzeDayMapReduceResult(MapReduceOutput out, Date date, String mapper) 
	{
		DBCollection dc = out.getOutputCollection();
        DBCursor cursor = dc.find();
        //获取一个数据库实例
        Datastore statistic = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
        logger.info("{}某天的数据mapreduce执行状况：{},按{}分组....",new Date().getTime(),dc.getStats(),mapper);
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			DayStatistic res = new DayStatistic();
			
			DBObject valueOb = (DBObject) dbObject.get("value");
			String click = String.valueOf(valueOb.get("click"));
			String download = String.valueOf(valueOb.get("download"));
			String install = String.valueOf(valueOb.get("install"));
			
			
			
			if("adid".equals(mapper))
			{
				res.setAdid((String) dbObject.get("_id"));
				String push = String.valueOf(valueOb.get("push"));
				res.setPush(Double.valueOf(push).longValue());
			}
			else
			{
				res.setFid((String) dbObject.get("_id"));
				String request = String.valueOf(valueOb.get("request")) ;
				String view = String.valueOf(valueOb.get("view"));
				res.setRequest(Double.valueOf(request).longValue());
				res.setView(Double.valueOf(view).longValue());
				//保存此应用的第一次请求时间
				res.setTimestamp((Long) valueOb.get("first"));
			}
			
			res.setClick(Double.valueOf(click).longValue());
			res.setDownload(Double.valueOf(download).longValue());
			res.setInstall(Double.valueOf(install).longValue());
			res.setDay(TimeUtil.getDay(date));
			Key<DayStatistic> key = statistic.save(res);
			logger.info("分析请求数，并入库：{}",key.getId());
		}
	}
	
	
	@Override
	public void executeDayMapReduce(String database,
			String mapper,Date date) 
	{
		String map = "function(){" 
	                + "emit("
	                + "this.day,"
	                + "{count: 1, view:this.view}"
	                + "); "
				    + "}";
	
		String reduce = "function (key, values) {"
					  + "var res = {count : 0,view:0};"
					  + "for (var i = 0; i < values.length; i++) {"	
						  + "res.count += values[i].count;"		     
						  + "res.view += values[i].view; "			
						  + "}"
					  + "return res;"
					+"}";
		
		if("adid".equals(mapper))
		{
			 map = "function(){" 
	                + "emit("
	                + "this.day,"
	                + "{count: 1, push:this.push,click:this.click,alive:this.alive}"
	                + "); "
				    + "}";
			 reduce = "function (key, values) {"
					  + "var res = {count : 0,push:0,click:0,alive:0};"
					  + "for (var i = 0; i < values.length; i++) {"	
						  + "res.count += values[i].count;"		     
						  + "res.push += values[i].push;"	 		
						  + "res.click += values[i].click; 	"		
						  + "res.alive += values[i].alive; "	 
						  + "}"
					  + "return res;"
					+"}";
		}
	
		String outputCollection = "tmp";
	
		//获取数据库一个实例
	  Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
	  BasicDBObject query = new BasicDBObject(); 
	  query.append(mapper, new BasicDBObject("$ne", null));
	  MapReduceOutput out = ds.getCollection(DayStatistic.class).mapReduce(map, reduce, outputCollection, query);
	  //处理查询结果并入库
	  analyzeSumMapReduceResult(out,date,mapper);
		
	}
	
	
	
	@Override
	public void daySatistic(Date date) 
	{
		//先统计广告的数据保存入库
		executeDayMapReduce(MongoDBConnConfig.DATABASE_STATISTIC, "adid", date);
		//再统计应用的展示数
		executeDayMapReduce(MongoDBConnConfig.DATABASE_STATISTIC, "fid", date);
	}

	/**
	 * 分析每天的统计数据  
	 * 保存到daysum文档中
	 * @param out
	 * @param date
	 * @param mapper
	 * mapper:adid 先统计广告在每天库里生产一条新数据
	 * mapper:fid  再统计应用的新增数、展示数、并更新数据库
	 */
	private void analyzeSumMapReduceResult(MapReduceOutput out, Date date,String mapper) 
	{
		DBCollection dc = out.getOutputCollection();
        DBCursor cursor = dc.find();
        //获取一个数据库实例
        Datastore statistic = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
        logger.info("{}天{}的总统计，mapreduce执行状况：{}",new Date(),mapper,dc.getStats());
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			DBObject valueOb = (DBObject) dbObject.get("value");
			
			if("adid".equals(mapper))
			{
				DaySum res = new DaySum();
				
				String push = String.valueOf(valueOb.get("push")) ;
				String click = String.valueOf(valueOb.get("click"));
				String alive = String.valueOf(valueOb.get("alive"));
				
				res.setPush(Double.valueOf(push).longValue());
				res.setClick(Double.valueOf(click).longValue());
				res.setAlive(Double.valueOf(alive).longValue());
				res.setTimestamp(System.currentTimeMillis());
				res.setDay((String) dbObject.get("_id"));
				
				//分析一天新增的应用数
				long count = this.queryNewAppsCount(MongoDBConnConfig.DATABASE_STATISTIC, date);
				logger.info("分析一天新增的应用数：{}。",count);
				res.setNew_u(count);
				Key<DaySum> key = (Key<DaySum>) statistic.save(res);
				logger.info("分析一天广告的总计数据，并入库：{}",key.getId());
			}
			
			else
			{
				//若是一天的应用展示数统计、则直接入库
				String view = String.valueOf(valueOb.get("view"));
				Query<DaySum> query = statistic.createQuery(DaySum.class).field("day").equal(TimeUtil.getDay(date));
				UpdateOperations<DaySum> ops = statistic.createUpdateOperations(DaySum.class).set("view", Double.valueOf(view).longValue());
				statistic.update(query, ops);
			}
		}
	}

	@Override
	public long queryNewAppsCount(String database, Date date) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(database);
//		Query<T> query= ds.createQuery(name);
//		query.field("day").equal(TimeUtil.getDay(date));
//		query.field("fid").notEqual(null);
//		query.field("time").greaterThan(date.getTime()-Constant.TEMPSTAMP_THREE);
		
		BasicDBObject query = new BasicDBObject(); 
        query.append("day", TimeUtil.getDay(date));
        query.append("fid", new BasicDBObject("$ne", null));
        query.append("time", new BasicDBObject("$gt", date.getTime()-Constant.TEMPSTAMP_THREE));  
        return ds.getCollection(DayStatistic.class).getCount(query);
	}
	
	
	
	@Override
	public Key<T> saveEntity(Class<T> name, String database, T entity) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(database);
		return ds.save(entity);
	}
	
	
	private void analyzeMapReduceResult(MapReduceOutput out)
	{
		DBCollection dc = out.getOutputCollection();
        DBCursor cursor = dc.find();
        //获取一个数据库实例
        Datastore statistic = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
        logger.info("请求数mapreduce执行状况：{}",dc.getStats());
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			Result res = new Result();
			res.setFid((String) dbObject.get("_id"));
			
			DBObject valueOb = (DBObject) dbObject.get("value");
			Double count = (Double) valueOb.get("count");
			res.setRequest(count.longValue());
			res.setFirst( (Long) valueOb.get("time"));
			res.setTimestamp(System.currentTimeMillis());
			
			Key<Result> key = statistic.save(res);
			logger.info("分析请求数，并入库：{}",key.getId());
		}
	}
	
	@Override
	public List<DayStatistic> queryDayStatistic(Date date, String mapper) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
		Query<DayStatistic> query = ds.createQuery(DayStatistic.class);
		query.field(mapper).notEqual(null);
		query.field("day").equal(TimeUtil.getDay(date));
		return query.asList();
	}
	
	@Override
	public List<DayStatistic> queryDayStatistic(Date date) {
		
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
		Query<DayStatistic> query = ds.createQuery(DayStatistic.class);
		query.field("day").equal(TimeUtil.getDay(date));
		return query.asList();
	}
	
	@Override
	public DaySum queryDaySum(Date date) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
		return ds.find(DaySum.class, "day", TimeUtil.getDay(date)).get();
	}
}
