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
import com.jukuad.statistic.pojo.AdDayStatistic;
import com.jukuad.statistic.pojo.AdResult;
import com.jukuad.statistic.pojo.AppDayStatistic;
import com.jukuad.statistic.pojo.Click;
import com.jukuad.statistic.pojo.DaySum;
import com.jukuad.statistic.pojo.Download;
import com.jukuad.statistic.pojo.Install;
import com.jukuad.statistic.pojo.Push;
import com.jukuad.statistic.pojo.View;
import com.jukuad.statistic.util.Constant;
import com.jukuad.statistic.util.TimeUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;

public class AdMongService<T> implements MongoService<T>
{
	private static final Logger logger = LoggerFactory.getLogger(AdMongService.class);
	@Override
	public void executeMapReduce(Class<T> name, String database,String hour) 
	{
		String map = "function(){" 
	               + "emit(this.adid, {count : 1})"
				   + "}";
		
		String reduce = "function (key, values) {"
					  + "var res = {count:0};"
					  + "values.forEach(function(val) {"
					  + "res.count += val.count; "
					  + "});"
					  + "return res;"
					  +"}";
		String outputCollection = "tmp";
		
		//下载或安装是统计应用id，算着广告的效果数
		if(Install.class.equals(name) || Download.class.equals(name))
		{
			map = "function(){" 
		               + "emit(this.appid, {count : 1})"
					   + "}";
			
			reduce = "function (key, values) {"
						  + "var res = {count:0};"
						  + "values.forEach(function(val) {"
						  + "res.count += val.count; "
						  + "});"
						  + "return res;"
						  +"}";
			
		}
		
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        DBObject query = new BasicDBObject(); 
        MapReduceOutput out = ds.getCollection(name).mapReduce(map, reduce, outputCollection, query);
        
        //处理查询结果并入库
        analyzeMapReduceResult(name,out,hour);
	}

	/**
	 * 处理map查询结果
	 * @param name
	 * @param out
	 */
	private void analyzeMapReduceResult(Class<T> name, MapReduceOutput out,String hour) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC); 
		DBCollection dc = out.getOutputCollection();
		logger.info("广告统计:{}数据,mapreduce执行状况：{}",name,dc.getStats());
        DBCursor cursor = dc.find();
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			String id =  (String) dbObject.get("_id");
			DBObject valObj = (DBObject) dbObject.get("value") ;
			String count = String.valueOf(valObj.get("count"));
			
			//判断当前的mapper和classname是否为push,创建广告统计信息
			if(Push.class.equals(name))
			{
				AdResult res = new AdResult();
				res.setAdid(id);
                res.setPush(Double.valueOf(count).longValue());
				res.setTimestamp(System.currentTimeMillis());
				res.setHour(hour);
				Key<AdResult> key = ds.save(res);
				logger.debug("分析广告推送数，并入库：{}",key.getId());
			}
			else
			{
				Query<AdResult> query = ds.createQuery(AdResult.class).field("adid").equal(id).field("hour").equal(hour);
				UpdateOperations<AdResult> ops = null;
				if(Download.class.equals(name))
				{
					//查询的结果是否存在此广告的下载，此结果可能木有点击以前的数，只有下载、安装（应用墙的应用）
					AdResult res = query.asList() != null && query.asList().size() > 0 ? query.asList().get(0) : null;
					if(res == null)
					{
						res = new AdResult();
						res.setDownload(Double.valueOf(count).longValue());
						res.setAdid(id);
						res.setHour(hour);
						res.setTimestamp(System.currentTimeMillis());
						Key<AdResult> key = ds.save(res);
						logger.debug("分析广告下载数，查询到此应用的广告id不存在，新建并入库：{}",key.getId());
					}else
					{
						ops = ds.createUpdateOperations(AdResult.class).set("download", Double.valueOf(count).longValue());
						ds.update(query, ops);
					}
				}
				else
				{
					if(View.class.equals(name)) ops = ds.createUpdateOperations(AdResult.class).set("view", Double.valueOf(count).longValue());
					if(Click.class.equals(name)) ops = ds.createUpdateOperations(AdResult.class).set("click", Double.valueOf(count).longValue());
					if(Install.class.equals(name)) ops = ds.createUpdateOperations(AdResult.class).set("install", Double.valueOf(count).longValue());
					ds.update(query, ops);
				}
			}
		}
	}
	
	@Override
	public void executeAppByAdTypeMapReduce(Class<T> name, String database,String hour) {
	}
	
	
	
	/**
	 * 将每小时的临时统计做结果汇总
	 */
	@Override
	public void executeTempMapReduce(Date date) 
	{
		String map = "function(){" 
	               + "	emit(this.adid,{count:1,"
                   + "	push:this.push,view:this.view,"
                   + "	click:this.click,download:this.download,"
                   + "install:this.install})"
			   + "}";
		String reduce = "function (key, values) {"
				  + "var res = {count : 0,push:0,view:0,click:0,download:0,install:0};"
				  + "for (var i = 0; i < values.length; i++) {"	
					  + "res.count += values[i].count;  "		     
					  + "res.push += values[i].push;"	 	
					  + "res.view += values[i].view; "			
					  + "res.click += values[i].click; 	"		
					  + "res.download += values[i].download;"	 
					  + "res.install += values[i].install; "		
						+ "}"
				  + "return res;"
				+"}";
		
		String outputCollection = "tmp";
		
	 	//获取数据库一个实例
	     Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
	     BasicDBObject query = new BasicDBObject(); 
	     BasicDBObject time = new BasicDBObject(); 
	     //此处特别注意：不要使用put方法，否则查询无效，比如：new BasicDBObject().put("$gt", updateTime)
	     time.append("$gte", TimeUtil.getDayStart(date).getTime()).append("$lt", TimeUtil.getDayEnd(date).getTime());
	     query.append("time", time);
	     MapReduceOutput out = ds.getCollection(AdResult.class).mapReduce(map, reduce, outputCollection, query);
	     //处理查询结果并入库
	     analyzeTempMapReduceResult(out,date);
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
	 */
	private void analyzeTempMapReduceResult(MapReduceOutput out, Date date) 
	{
		DBCollection dc = out.getOutputCollection();
        DBCursor cursor = dc.find();
        //获取一个数据库实例
        Datastore statistic = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
        logger.info("{}:一天的广告数据mapreduce执行状况：{}....",TimeUtil.getDay(date),dc.getStats());
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			AdDayStatistic res = new AdDayStatistic();
			
			DBObject valueOb = (DBObject) dbObject.get("value");
			String push = String.valueOf(valueOb.get("push"));
			String view = String.valueOf(valueOb.get("view"));
			String click = String.valueOf(valueOb.get("click"));
			String download = String.valueOf(valueOb.get("download"));
			String install = String.valueOf(valueOb.get("install"));
			
			String id = (String) dbObject.get("_id");
			
			res.setAdid(id);
			res.setPush(Double.valueOf(push).longValue());
			res.setView(Double.valueOf(view).longValue());
			res.setClick(Double.valueOf(click).longValue());
			res.setDownload(Double.valueOf(download).longValue());
			res.setInstall(Double.valueOf(install).longValue());
			res.setDay(TimeUtil.getDay(date));
			res.setTimestamp(System.currentTimeMillis());
			
			//获取广告的终端数
			Integer[] size = this.statisticDayUserData(date, id, MongoDBConnConfig.DATABASE_DATA);
			res.setAlive(size[0]);
			
			Key<AdDayStatistic> key = statistic.save(res);
			logger.debug("{}:{}分析临时数据汇总，并入库：{}","AdDayStatistic",TimeUtil.getDay(date),key.getId());
        }
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> queryDistinctDocument(Class<T> name,
			Map<String, Object> paramMap, String database) 
	{
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        DBObject query = new BasicDBObject(); 
        
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Integer[] statisticDayUserData(Date date, String id, String database) 
	{
		Integer[] size = new Integer[3];
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);
        //获取日活用户
        BasicDBObject query = new BasicDBObject(); 
        query.append("adid", id);
        query.append("time", new BasicDBObject().append("$gte", TimeUtil.getDayStart(date).getTime()).append("$lt", TimeUtil.getDayEnd(date).getTime()));  
		List<Push> list = ds.getCollection(Push.class).distinct("imei", query);
		size[0] = null == list ? 0 : list.size();
		return size;	
	}
	
	@Override
	public long queryNewAppsCount(String database, Date date) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(database);
		
		BasicDBObject query = new BasicDBObject(); 
        query.append("day", TimeUtil.getDay(date));
        query.append("time", new BasicDBObject("$gt", date.getTime()-Constant.TEMPSTAMP_THREE));  
        return ds.getCollection(AppDayStatistic.class).getCount(query);
	}
	

	@Override
	public void executeDayMapReduce(Class<T> name, String database,Date date) 
	{
		String map = "function(){" 
                + "emit("
                + "this.day,"
                + "{count: 1, push:this.push,view:this.view,click:this.click,alive:this.alive}"
                + "); "
			    + "}";

		String reduce = "function (key, values) {"
					  + "var res = {count : 0,push:0,view:0,click:0,alive:0};"
					  + "for (var i = 0; i < values.length; i++) {"	
						  + "res.count += values[i].count;"		     
						  + "res.push += values[i].push;"	 
						  + "res.view += values[i].view; "	
						  + "res.click += values[i].click; 	"		
						  + "res.alive += values[i].alive; "	 
						  + "}"
					  + "return res;"
					+"}";

	 String outputCollection = "tmp";
	
		//获取数据库一个实例
	  Datastore ds = MongoDBDataStore.getDBInstance(database);
	  BasicDBObject query = new BasicDBObject(); 
	  query.append("day", TimeUtil.getDay(date));
	  MapReduceOutput out = ds.getCollection(AdDayStatistic.class).mapReduce(map, reduce, outputCollection, query);
	  //处理查询结果并入库
	  analyzeDayMapReduceResult(out,date);
	}

	/**
	 * 每日统计结果分析
	 * 并保存每日新增的应用数
	 * @param out
	 * @param date
	 */
	private void analyzeDayMapReduceResult(MapReduceOutput out, Date date) 
	{
		DBCollection dc = out.getOutputCollection();
        DBCursor cursor = dc.find();
        //获取一个数据库实例
        Datastore statistic = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
        logger.info("{}：一天的广告的总统计，mapreduce执行状况：{}",TimeUtil.getDay(date),dc.getStats());
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			DBObject valueOb = (DBObject) dbObject.get("value");
			
			DaySum res = new DaySum();
			
			String push = String.valueOf(valueOb.get("push")) ;
			String view = String.valueOf(valueOb.get("view")) ;
			String click = String.valueOf(valueOb.get("click"));
			String alive = String.valueOf(valueOb.get("alive"));
			
			res.setPush(Double.valueOf(push).longValue());
			res.setView(Double.valueOf(view).longValue());
			res.setClick(Double.valueOf(click).longValue());
			res.setAlive(Double.valueOf(alive).longValue());
			res.setTimestamp(System.currentTimeMillis());
			res.setDay((String) dbObject.get("_id"));
			
			//分析一天新增的应用数
			long count = this.queryNewAppsCount(MongoDBConnConfig.DATABASE_STATISTIC, date);
			logger.info("分析一天新增的应用数：{}。",count);
			res.setNew_u(count);
			Key<DaySum> key = (Key<DaySum>) statistic.save(res);
			logger.debug("分析一天广告的总计数据，并入库：{}",key.getId());
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List queryDayStatistic(Date date) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
		Query<AdDayStatistic> query = ds.createQuery(AdDayStatistic.class);
		query.field("day").equal(TimeUtil.getDay(date));
		return query.asList();
	}
	

}
