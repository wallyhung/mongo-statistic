package com.jukuad.statistic.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.config.MongoDBDataStore;
import com.jukuad.statistic.pojo.Click;
import com.jukuad.statistic.pojo.DayStatistic;
import com.jukuad.statistic.pojo.Download;
import com.jukuad.statistic.pojo.Install;
import com.jukuad.statistic.pojo.Push;
import com.jukuad.statistic.pojo.Request;
import com.jukuad.statistic.pojo.View;
import com.jukuad.statistic.util.TimeUtil;

public class StatisticService 
{
	
	private static final Logger logger = LoggerFactory.getLogger(StatisticService.class);
	/**
	 * 进行日志分析并
	 * 隔两小时统计
	 */
	public static void statistic()
	{
		//获取日志分析任务的线程数
		CountDownLatch count = LogAnalysisService.analyzeLog();
		try 
		{
			count.await();
			logger.info("日志分析任务完成：{}。开始定时统计任务",new Date().getTime());
			
			MongoService<Request> se = new MongoServiceImpl<Request>();
			se.executeRequestMapReduce(Request.class, MongoDBConnConfig.DATABASE_TEMP);
			
			MongoService<Push> push = new MongoServiceImpl<Push>();
			push.executeMapReduce(Push.class, MongoDBConnConfig.DATABASE_TEMP,"adid");
			
			MongoService<View> view = new MongoServiceImpl<View>();
			view.executeMapReduce(View.class, MongoDBConnConfig.DATABASE_TEMP,"fid");
			
			MongoService<Click> click = new MongoServiceImpl<Click>();
			click.executeMapReduce(Click.class, MongoDBConnConfig.DATABASE_TEMP,"fid");
			click.executeMapReduce(Click.class, MongoDBConnConfig.DATABASE_TEMP,"adid");
			
			MongoService<Download> down = new MongoServiceImpl<Download>();
			down.executeMapReduce(Download.class, MongoDBConnConfig.DATABASE_TEMP,"fid");
			down.executeMapReduce(Download.class, MongoDBConnConfig.DATABASE_TEMP,"adid");
			
			MongoService<Install> install = new MongoServiceImpl<Install>();
			install.executeMapReduce(Install.class, MongoDBConnConfig.DATABASE_TEMP,"fid");
			install.executeMapReduce(Install.class, MongoDBConnConfig.DATABASE_TEMP,"adid");
			
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	
	/**
	 * 统计用户数
	 * mapper:fid   统计新增、留存、日活用户数
	 * mapper:adid   统计终端数
	 * @param date
	 * @param mapper  统计标示
	 */
	private static void updateUserData(Date date,String mapper)
	{
		//获取数据库一个实例
        Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_STATISTIC);  
        //这里特别注意key和条件关系式"="之间要有空格  
        Query<DayStatistic> query = ds.createQuery(DayStatistic.class);
        query.field("day").equal(TimeUtil.getDay(date));
        query.field(mapper).notEqual(null);
    	List<DayStatistic> list = query.asList();
    	UpdateOperations<DayStatistic> ops = null;
    	for (DayStatistic dayStatistic : list) 
    	{
    		if("fid".equals(mapper))
    		{
    			//统计用户数据
        		Integer[] userint = statistic(mapper, date, dayStatistic.getFid());
        		ops = ds.createUpdateOperations(DayStatistic.class).set("alive", userint[0])
        														   .set("remain", userint[1])
        														   .set("new_u", userint[2]);
    		}
    		else
    		{
    			if("adid".equals(mapper))
        		{
        			//统计用户数据
            		Integer[] userint = statistic(mapper, date, dayStatistic.getAdid());
            		ops = ds.createUpdateOperations(DayStatistic.class).set("alive", userint[0]);
        		}
    		}
    		ds.update(dayStatistic, ops);
		}
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public static void dayStatistic(Date date)
	{
		//首先对应用数据进行map reduce操作
		MongoService service = new MongoServiceImpl();
		service.executeTempMapReduce(date, "fid");
		service.executeTempMapReduce(date, "adid");
		
		//分别对发布的应用和广告做用户统计
		updateUserData(date, "fid");
		updateUserData(date, "adid");
    	
    	//对一天的推送做总统计
		service.daySatistic(date);
	}
	
	
	
	/**
	 * 根据发布ID和广告ID做两种终端用户的统计
	 * 发布ID，统计新增数、留存、日活数
	 * 广告ID，统计终端数（日活数）
	 * @param mapper 统计标示
	 * @param time
	 * @param id
	 * @return
	 */
	private static Integer[] statistic(String mapper,Date time,String id)
	
	{
		Integer[] size = new Integer[3];
		
		if("fid".equals(mapper))
		{
			MongoService<Request> imeiService = new MongoServiceImpl<Request>();
			
			Map<String, Object> paramMap = new HashMap<String, Object>(3);
			paramMap.put(mapper, id);
			paramMap.put("start", TimeUtil.getDayStart(time).getTime());
			paramMap.put("end", TimeUtil.getDayEnd(time).getTime());
			List<Request> list =  imeiService.queryDistinctDoc(Request.class, paramMap, MongoDBConnConfig.DATABASE_TEMP);
			size[0] = null == list ? 0 : list.size();
			
			//获取留存用户的数据
			paramMap.put("start", TimeUtil.getSevenDaysBefore(time).getTime());
			paramMap.put("end", time.getTime());
			List<Request> rlist =  imeiService.queryDistinctDoc(Request.class, paramMap, MongoDBConnConfig.DATABASE_TEMP);
			size[1] = null == rlist ? 0 : list.size();
			
			//先获取当天的上一天到前8天的日活用户
			Date before = TimeUtil.getEightDaysBefore(time);
			Date last = TimeUtil.getLastDayEnd(time);
			
			paramMap.put("start", TimeUtil.getDayStart(before).getTime());
			paramMap.put("end", TimeUtil.getDayEnd(last).getTime());
			List<Request> elist =  imeiService.queryDistinctDoc(Request.class, paramMap, MongoDBConnConfig.DATABASE_TEMP);
			size[2] = getDiffSize(elist, list);
			
		}
		else
		{
            MongoService<Push> imeiService = new MongoServiceImpl<Push>();
			
			Map<String, Object> paramMap = new HashMap<String, Object>(3);
			paramMap.put(mapper, id);
			paramMap.put("start", TimeUtil.getDayStart(time).getTime());
			paramMap.put("end", TimeUtil.getDayEnd(time).getTime());
			List<Push> list =  imeiService.queryDistinctDoc(Push.class, paramMap, MongoDBConnConfig.DATABASE_TEMP);
			size[0] = null == list ? 0 : list.size();
		}
		return size;
	}
	
	
	
	/**
	 * 比较两个数组的不同元素的个数
	 * @param list1
	 * @param list2
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static int getDiffSize(List list1,List list2)
	{
		int sum = 0;
		Object[] array_1 =  list1.toArray();
		Object[] array_2 =  list2.toArray();
		Arrays.sort(array_1);
		Arrays.sort(array_2);
		
		int len = array_2.length;  
		for (int i = 0; i < len; i++)  
		{  
		    if (Arrays.binarySearch(array_1, array_2[i]) < 0)  sum++;
		}  
		return sum;
	}
	
	
	
	public static void main(String[] args) 
	{
		statistic();
		dayStatistic(new Date());
	}
	

}
