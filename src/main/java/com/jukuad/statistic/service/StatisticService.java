package com.jukuad.statistic.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.pojo.AdDayStatistic;
import com.jukuad.statistic.pojo.AdResult;
import com.jukuad.statistic.pojo.AppResult;
import com.jukuad.statistic.pojo.Click;
import com.jukuad.statistic.pojo.Download;
import com.jukuad.statistic.pojo.Install;
import com.jukuad.statistic.pojo.Push;
import com.jukuad.statistic.pojo.Request;
import com.jukuad.statistic.pojo.View;
import com.jukuad.statistic.util.Constant;
import com.jukuad.statistic.util.TimeUtil;

public class StatisticService 
{
	
	private static final Logger logger = LoggerFactory.getLogger(StatisticService.class);
	/**
	 * 进行日志分析并
	 * 隔一小时统计
	 * @param hour 小时的时间字符串、文件名
	 */
	public static void statistic(String hour)
	{
		//获取日志分析任务的线程数
		CountDownLatch count = LogAnalysisService.analyzeLog(hour);
		try 
		{
			count.await();
			logger.info("{}.日志分析任务完成,开始小时统计任务...",hour);
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_REQUEST,hour))
			{
				MongoService<Request> request = new AppMongService<Request>();
				request.executeMapReduce(Request.class, MongoDBConnConfig.DATABASE_TEMP,hour);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_PUSH,hour))
			{
				MongoService<Push> push = new AdMongService<Push>();
				push.executeMapReduce(Push.class, MongoDBConnConfig.DATABASE_TEMP,hour);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_VIEW,hour))
			{
				MongoService<View> appview = new AppMongService<View>();
				MongoService<View> adview = new AdMongService<View>();
				appview.executeMapReduce(View.class, MongoDBConnConfig.DATABASE_TEMP,hour);
				adview.executeMapReduce(View.class, MongoDBConnConfig.DATABASE_TEMP,hour);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_CLICK,hour))
			{
				MongoService<Click> appclick = new AppMongService<Click>();
				MongoService<Click> adclick = new AdMongService<Click>();
				appclick.executeAppByAdTypeMapReduce(Click.class, MongoDBConnConfig.DATABASE_TEMP,hour);
				adclick.executeMapReduce(Click.class, MongoDBConnConfig.DATABASE_TEMP,hour);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_DOWNLOAD,hour))
			{
				MongoService<Download> appdown = new AppMongService<Download>();
				MongoService<Download> addown = new AdMongService<Download>();
				appdown.executeAppByAdTypeMapReduce(Download.class, MongoDBConnConfig.DATABASE_TEMP,hour);
				addown.executeMapReduce(Download.class, MongoDBConnConfig.DATABASE_TEMP,hour);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_INSTALL,hour))
			{
				MongoService<Install> appins = new AppMongService<Install>();
				MongoService<Install> adins = new AdMongService<Install>();
				appins.executeAppByAdTypeMapReduce(Install.class, MongoDBConnConfig.DATABASE_TEMP,hour);
				adins.executeMapReduce(Install.class, MongoDBConnConfig.DATABASE_TEMP,hour);
			}
			logger.info("{}.小时统计任务完成了，等待下一次小时统计任务...",hour);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
	
	/**
	 * 小时统计
	 * @param date
	 */
	public static void hourStatistic(String hour)
	{
		//判断是否需要统计某个小时的日志
		BaseService<AdResult> service = new BaseServiceImpl<AdResult>();
		//需要统计的小时日志是在下小时执行
		Date date = TimeUtil.getDayFromHourString(hour);
		boolean bool = service.validateStatisticLastHourLog(MongoDBConnConfig.DATABASE_STATISTIC,date.getTime()+Constant.TEMPSTAMP_HOUR);
		if(bool) statistic(hour);
		else logger.info("{}.小时统计任务已经做了，无需重新统计...", hour);
	}
	
	/**
	 * 对一天的数据做统计
	 * @param date
	 */
	public static void dayStatistic(Date date)
	{
		BaseService<AdResult> service = new BaseServiceImpl<AdResult>();
		List<AdResult> list = service.queryTempStatisticList(AdResult.class, MongoDBConnConfig.DATABASE_STATISTIC, date);
		if(list != null && list.size() > 0)
		{
			//首先对应用数据进行map reduce操作
			MongoService<AppResult> appService = new AppMongService<AppResult>();
			MongoService<AdResult> adService = new AdMongService<AdResult>();
			
			appService.executeTempMapReduce(date);
			adService.executeTempMapReduce(date);
			
	    	//对一天的推送做总统计
			MongoService<AdDayStatistic> adday = new AdMongService<AdDayStatistic>();
			adday.executeDayMapReduce(AdDayStatistic.class, MongoDBConnConfig.DATABASE_STATISTIC, date);
		}
		else logger.info("昨天的数据出现异常...");
	}
	
	
	/**
	 * 系统崩掉后辅助的统计
	 * @param date
	 */
	public static void distanceStatistic(Date date)
	{
		BaseService<AdResult> service = new BaseServiceImpl<AdResult>();
		AdResult lastData = service.queryLastData(MongoDBConnConfig.DATABASE_STATISTIC);
		String[] distance = TimeUtil.getDistanceTimeHourArray(new Date(lastData.getTimestamp()), date);
//		String[] distance = TimeUtil.getDistanceTimeHourArray(TimeUtil.StrToDate("2014-03-28 00:00:00"), TimeUtil.StrToDate("2014-03-28 23:59:59"));
		for (String hour : distance) {
			statistic(hour);
		}
	}
	
	public static void main(String[] args) {
		distanceStatistic(new Date());
		String map = "function(){" 
	               + "emit(this.appid, {count : 1})"
				   + "}";
		
		String reduce = "function (key, values) {"
					  + "var res = {count:0};"
					  + "values.forEach(function(val) {"
					  + "res.count += val.count; "
					  + "});"
					  + "return res;"
					  +"}";
		System.out.println(map);
		System.out.println(reduce);
	}
}
