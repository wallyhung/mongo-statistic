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
import com.jukuad.statistic.pojo.AppDayStatistic;
import com.jukuad.statistic.pojo.AppResult;
import com.jukuad.statistic.pojo.Attach;
import com.jukuad.statistic.pojo.Click;
import com.jukuad.statistic.pojo.Download;
import com.jukuad.statistic.pojo.Install;
import com.jukuad.statistic.pojo.Request;
import com.jukuad.statistic.pojo.View;
import com.jukuad.statistic.util.ObjectUtil;
import com.jukuad.statistic.util.TimeUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;

public class AppMongService<T> implements MongoService<T>
{
	private static final Logger logger = LoggerFactory.getLogger(AppMongService.class);
	@Override
	public void executeMapReduce(Class<T> name, String database) 
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
		
		if(View.class.equals(name))
		{
			map = "function(){" 
		               + "emit(this.fid, {count : 1})"
					   + "}";
			reduce = "function (key, values) {"
					  + "var res = {count : 0};"
					  + "for (var i = 0; i < values.length; i++) {"
					  + "res.count += values[i].count;"
					  + "}"
					  + "return res;"
					  +"}";
		}
		
		String outputCollection = "tmp";
		
	  //获取数据库一个实例
	  Datastore ds = MongoDBDataStore.getDBInstance(database);  
	  DBObject query = new BasicDBObject(); 
	  MapReduceOutput out = ds.getCollection(name).mapReduce(map, reduce, outputCollection, query);
	  //处理查询结果并入库
	  analyzeMapReduceResult(name,out);
	}

	/**
	 * 处理请求数和展示数的map结果
	 * @param name
	 * @param out
	 */
	private void analyzeMapReduceResult(Class<T> name, MapReduceOutput out) 
	{
		DBCollection dc = out.getOutputCollection();
        DBCursor cursor = dc.find();
        //获取一个数据库实例
        Datastore statistic = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
        logger.info("app:{}:mapreduce执行状况：{}",name,dc.getStats());
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			AppResult res = new AppResult();
			String id =  (String) dbObject.get("_id");
			res.setFid(id);
			
			DBObject valueOb = (DBObject) dbObject.get("value");
			if(Request.class.equals(name))
			{
				
				Double count = (Double) valueOb.get("count");
				res.setRequest(count.longValue());
				res.setFirst( (Long) valueOb.get("time"));
				res.setTimestamp(System.currentTimeMillis());
				Key<AppResult> key = statistic.save(res);
				logger.info("分析请求数，并入库：{}",key.getId());
			}
			else
			{
				String count = String.valueOf(valueOb.get("count"));
				Query<AppResult> query = statistic.createQuery(AppResult.class).field("fid").equal(id);
				UpdateOperations<AppResult> ops = statistic.createUpdateOperations(AppResult.class).set("view", Double.valueOf(count).longValue());
				statistic.update(query, ops);
				logger.info("分析展示数，并入库：{}",ops.toString());
			}
		}
	}
	
	@Override
	public void executeAppByAdTypeMapReduce(Class<T> name, String database) 
	{
		String map = "function(){" 
	               + "emit(this.fid, {count : 1,adid:this.adid,type:this.type, appid:this.appid})"
				   + "}";
		
		String reduce = "function (key, values) {"
				  + "var res = {count:0,wall:0,oth:0};"
				  + "for (var i = 0; i < values.length; i++) {"
				  + "res.count += values[i].count;"
			  		+ "if(values[i].adid == values[i].appid) res.wall += values[i].count;"
					+ "else res.oth += values[i].count;"
			  	  + "}"
				   + "return res; "
				  + "}";
		
		if(Click.class.equals(name))
		{
			map = "function(){" 
		               + "emit(this.fid, {count : 1,adid:this.adid,type:this.type, appid:this.appid})"
					   + "}";
			reduce = "function (key, values) {"
					  + "var res = {count:0,cpc:0,wall:0,oth:0};"
					  + "for (var i = 0; i < values.length; i++) {"
					  + "res.count += values[i].count;"
					  + "if(values[i].type == 1){"
				  		+ "if(values[i].adid == values[i].appid) res.wall += values[i].count;"
						+ "else res.oth += values[i].count;"
				  	  + "}"
					  + "else if(values[i].type == 2){"
					    + "res.cpc += values[i].count;"
					  + "}"
					  + "}"
					   + "return res; "
					  + "}";
		}
		
		String outputCollection = "tmp";
	   //获取数据库一个实例
	   Datastore ds = MongoDBDataStore.getDBInstance(database);  
	   DBObject query = new BasicDBObject(); 
	   MapReduceOutput out = ds.getCollection(name).mapReduce(map, reduce, outputCollection, query);
	 
	   //处理查询结果并入库
	   analyzeAppMapReduceResult(name,out);
	}
	
	/**
	 * 处理app点击、下载、安装的数据
	 * @param name
	 * @param out
	 */
	private void analyzeAppMapReduceResult(Class<T> name, MapReduceOutput out) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC); 
		DBCollection dc = out.getOutputCollection();
		logger.info("根据广告的类型，统计应用的数据,{}。mapreduce执行状况：{}",name,dc.getStats());
        DBCursor cursor = dc.find();
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			String id = (String) dbObject.get("_id");
			DBObject value = (DBObject) dbObject.get("value");
			
			Query<AppResult> query = ds.createQuery(AppResult.class).field("fid").equal(id);
			UpdateOperations<AppResult> ops = null;
			
			String wall = String.valueOf(value.get("wall"));
			String oth = String.valueOf(value.get("oth"));
			
			if(Click.class.equals(name))
			{
				String cpc = String.valueOf(value.get("cpc"));
				//证明只走了map函数，当前发布id只有一个一次点击
				Attach attach = null;
				if(cpc.equals("null"))
				{
					Double typeStr = (Double) value.get("type");
					int type = typeStr.intValue();
					String adid = (String) value.get("adid");
					String appid = (String) value.get("appid");
					attach = parseMapResult(type, adid, appid);
					
				}
				else attach = new Attach(Double.valueOf(cpc).longValue(), Double.valueOf(wall).longValue(), Double.valueOf(oth).longValue());
				ops = ds.createUpdateOperations(AppResult.class).set("click", attach);
				logger.info("分析广告点击数，并入库：{}",ops.toString());
			}
			if(Download.class.equals(name))
			{
				Attach attach = null;
				if(wall.equals("null"))
				{
					Double typeStr = (Double) value.get("type");
					int type = typeStr.intValue();
					String adid = (String) value.get("adid");
					String appid = (String) value.get("appid");
					attach = parseMapResult(type, adid, appid);
				}
				else attach = new Attach(Double.valueOf(wall).longValue(), Double.valueOf(oth).longValue());
				ops = ds.createUpdateOperations(AppResult.class).set("download", attach);
				logger.info("分析广告下载数，并入库：{}",ops.toString());
			}
			if(Install.class.equals(name))
			{
				Attach attach = null;
				if(wall.equals("null"))
				{
					Double typeStr = (Double) value.get("type");
					int type = typeStr.intValue();
					String adid = (String) value.get("adid");
					String appid = (String) value.get("appid");
					attach = parseMapResult(type, adid, appid);
				}
				else attach = new Attach(Double.valueOf(wall).longValue(), Double.valueOf(oth).longValue());
				ops = ds.createUpdateOperations(AppResult.class).set("install", attach);
				logger.info("分析广告安装数，并入库：{}",ops.toString());
			}
			ds.update(query, ops);
		}
	}
	
	/**
	 * 解析单个结果（木有reduce的结果）
	 * @param type
	 * @param adid
	 * @param appid
	 * @return
	 */
	private Attach parseMapResult(int type,String adid,String appid)
	{
		long cpc = 0;
		long wall = 0;
		long oth = 0;
		if(type ==1)
		{
			if(adid.equals(appid)) wall = 1;
			else oth = 1;
		}
		else cpc = 1;
		return new Attach(cpc, wall, oth);
	}
	
	@Override
	public void executeTempMapReduce(Date date) 
	{
		String map = "function(){" 
	               + "	emit(this.fid, {"
	                   + " count: 1,request: this.request,"
	                   + " view: this.view,click: this.click,"
	                   + " download: this.download,install: this.install,"
	                   + " first: this.first"
	                   + " })"
				   + "}";
		String reduce = "function (key, values) {"
				  + "var res = {count: 0, request: 0, view: 0, c_cpc: 0, c_wall: 0, c_oth: 0, d_wall: 0, d_oth: 0, i_wall: 0, i_oth: 0, first: ''};"
				  + "res.first = values[0].first;"	
				  + "for (var i = 0; i < values.length; i++) {"	
					  + "res.count += values[i].count; "		     
					  + "res.request += values[i].request;"		  
					  + "res.view += values[i].view;"	
					  + "if(values[i].click != null){"
						  + "res.c_cpc += values[i].click.cpc;"		
						  + "res.c_wall += values[i].click.wall;"	
						  + "res.c_oth += values[i].click.oth;"
					  + "}"
					  + "if(values[i].download != null){"
						  + "res.d_wall += values[i].download.wall;"	
						  + "res.d_oth += values[i].download.oth;"
					  + "}"
					  + "if(values[i].install != null){"
						  + "res.i_wall += values[i].install.wall;"	
						  + "res.i_oth += values[i].install.oth;"	
				      + "}"
					  + "if (res.first > values[i].first){"
							+ "res.first = values[i].first; "	
							+ "}"
						+ "}"
				  + "return res;"
				+"}";
			
		String outputCollection = "tmp";
		
		 Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
	     BasicDBObject query = new BasicDBObject(); 
	     BasicDBObject time = new BasicDBObject(); 
	     //此处特别注意：不要使用put方法，否则查询无效，比如：new BasicDBObject().put("$gt", updateTime)
	     time.append("$gte", TimeUtil.getDayStart(date).getTime()).append("$lt", TimeUtil.getDayEnd(date).getTime());
	     query.append("time", time);
	     MapReduceOutput out = ds.getCollection(AppResult.class).mapReduce(map, reduce, outputCollection, query);
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
        logger.info("{}某天的数据mapreduce执行状况：{}....",new Date().getTime(),dc.getStats());
        
        while (cursor.hasNext()) 
        {
			DBObject dbObject = (DBObject) cursor.next();
			AppDayStatistic res = new AppDayStatistic();
			
			DBObject valueOb = (DBObject) dbObject.get("value");
			String request = String.valueOf(valueOb.get("request"));
			String view = String.valueOf(valueOb.get("view"));
			
			String click_cpc = String.valueOf(valueOb.get("c_cpc"));
			//判断单个fid，木有走reduce的数据
			Attach click_Attach = null;
			Attach down_Attach = null;
			Attach ins_Attach = null;
			if(click_cpc.equals("null"))
			{
				res = parseSingleFid(valueOb,res);
			}
			else
			{
				String click_wall = String.valueOf(valueOb.get("c_wall"));
				String click_oth = String.valueOf(valueOb.get("c_oth"));
				
				String down_wall = String.valueOf(valueOb.get("d_wall"));
				String down_oth = String.valueOf(valueOb.get("d_oth"));
				
				String ins_wall = String.valueOf(valueOb.get("i_wall"));
				String ins_oth = String.valueOf(valueOb.get("i_oth"));
				
				click_Attach = new Attach(Double.valueOf(click_cpc).longValue(), Double.valueOf(click_wall).longValue(), Double.valueOf(click_oth).longValue());
				down_Attach = new Attach(Double.valueOf(down_wall).longValue(), Double.valueOf(down_oth).longValue());
				ins_Attach = new Attach(Double.valueOf(ins_wall).longValue(), Double.valueOf(ins_oth).longValue());
				
				res.setClick(click_Attach);
				res.setDownload(down_Attach);
				res.setInstall(ins_Attach);
			}
			
			String id = (String) dbObject.get("_id");
			if(id == null) res.setFid("wwwwww");
			else res.setFid(id);
			res.setRequest(Double.valueOf(request).longValue());
			res.setView(Double.valueOf(view).longValue());
			
			res.setDay(TimeUtil.getDay(date));
			res.setTimestamp(System.currentTimeMillis());
			
			//获取用户的日活、留存、新增
			Integer[] size = this.statisticDayUserData(date, id, MongoDBConnConfig.DATABASE_TEMP);
			res.setAlive(size[0]);
			res.setRemain(size[1]);
			res.setNew_u(size[2]);
			
			Key<AppDayStatistic> key = statistic.save(res);
			logger.info("{}:{}分析临时数据汇总，并入库：{}",AppDayStatistic.class,date,key.getId());
        }
	}
	
	
	
	/**
	 * 解析单个发布id数据
	 * @param valueOb
	 * @param res
	 * @return
	 */
	private AppDayStatistic parseSingleFid(DBObject valueOb, AppDayStatistic res) 
	{
		DBObject clickOb = (DBObject) valueOb.get("click");
		DBObject downloadOb = (DBObject) valueOb.get("download");
		DBObject installOb = (DBObject) valueOb.get("install");
		
		String click_cpc = String.valueOf(clickOb.get("cpc"));
		String click_wall = String.valueOf(clickOb.get("wall"));
		String click_oth = String.valueOf(clickOb.get("oth"));
		
		String down_wall = String.valueOf(downloadOb.get("wall"));
		String down_oth = String.valueOf(downloadOb.get("oth"));
		
		String ins_wall = String.valueOf(installOb.get("wall"));
		String ins_oth = String.valueOf(installOb.get("oth"));
		
		Attach click_Attach = new Attach(Double.valueOf(click_cpc).longValue(), Double.valueOf(click_wall).longValue(), Double.valueOf(click_oth).longValue());
		Attach down_Attach = new Attach(Double.valueOf(down_wall).longValue(), Double.valueOf(down_oth).longValue());
		Attach ins_Attach = new Attach(Double.valueOf(ins_wall).longValue(), Double.valueOf(ins_oth).longValue());
		
		res.setClick(click_Attach);
		res.setDownload(down_Attach);
		res.setInstall(ins_Attach);
		
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> queryDistinctDocument(Class<T> name,
			Map<String, Object> paramMap, String database) 
	{
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);  
        DBObject query = new BasicDBObject(); 
        
        if(paramMap.get("fid") != null)
        	query.put("fid", paramMap.get("fid"));
        
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
	public Integer[] statisticDayUserData(Date date,String id,String database) 
	{
        Integer[] size = new Integer[3];
        //获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(database);
        //获取日活用户
        BasicDBObject query = new BasicDBObject(); 
        query.append("fid", id);
        query.append("time", new BasicDBObject().append("$gte", TimeUtil.getDayStart(date).getTime()).append("$lt", TimeUtil.getDayEnd(date).getTime()));  
		List<Request> list = ds.getCollection(Request.class).distinct("imei", query);
		size[0] = null == list ? 0 : list.size();
		
		//获取留存用户的数据
		query.append("time", new BasicDBObject().append("$gte", TimeUtil.getSevenDaysBefore(date).getTime()).append("$lt", TimeUtil.getDayEnd(date).getTime()));
		List<Request> rlist =  ds.getCollection(Request.class).distinct("imei", query);
		size[1] = null == rlist ? 0 : rlist.size();
		
		//先获取当天的上一天到前8天的日活用户
		Date before = TimeUtil.getEightDaysBefore(date);
		Date last = TimeUtil.getLastDayEnd(date);
		
		query.append("time", new BasicDBObject().append("$gte", TimeUtil.getDayStart(before).getTime()).append("$lt", TimeUtil.getDayEnd(last).getTime()));
		List<Request> elist =  ds.getCollection(Request.class).distinct("imei", query);
		size[2] = ObjectUtil.getDiffSize(elist, list);
				
		return size;
	}
	
	@Override
	public void executeDayMapReduce(Class<T> name, String database,Date date) {
	}
	
	@Override
	public long queryNewAppsCount(String database, Date date) {
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List queryDayStatistic(Date date) 
	{
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);
		Query<AppDayStatistic> query = ds.createQuery(AppDayStatistic.class);
		query.field("day").equal(TimeUtil.getDay(date));
		return query.asList();
	}


}
