package com.jukuad.statistic.service;

import java.util.Date;
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
			logger.error("日志分析任务完成：{}。开始定时统计任务",new Date().getTime());
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_REQUEST))
			{
				MongoService<Request> request = new AppMongService<Request>();
				request.executeMapReduce(Request.class, MongoDBConnConfig.DATABASE_TEMP);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_PUSH))
			{
				MongoService<Push> push = new AdMongService<Push>();
				push.executeMapReduce(Push.class, MongoDBConnConfig.DATABASE_TEMP);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_VIEW))
			{
				MongoService<View> appview = new AppMongService<View>();
				MongoService<View> adview = new AdMongService<View>();
				appview.executeMapReduce(View.class, MongoDBConnConfig.DATABASE_TEMP);
				adview.executeMapReduce(View.class, MongoDBConnConfig.DATABASE_TEMP);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_CLICK))
			{
				MongoService<Click> appclick = new AppMongService<Click>();
				MongoService<Click> adclick = new AdMongService<Click>();
				appclick.executeAppByAdTypeMapReduce(Click.class, MongoDBConnConfig.DATABASE_TEMP);
				adclick.executeMapReduce(Click.class, MongoDBConnConfig.DATABASE_TEMP);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_DOWNLOAD))
			{
				MongoService<Download> appdown = new AppMongService<Download>();
				MongoService<Download> addown = new AdMongService<Download>();
				appdown.executeAppByAdTypeMapReduce(Download.class, MongoDBConnConfig.DATABASE_TEMP);
				addown.executeMapReduce(Download.class, MongoDBConnConfig.DATABASE_TEMP);
			}
			
			if(LogAnalysisService.existNewLogs(Constant.PATH_INSTALL))
			{
				MongoService<Install> appins = new AppMongService<Install>();
				MongoService<Install> adins = new AdMongService<Install>();
				appins.executeAppByAdTypeMapReduce(Install.class, MongoDBConnConfig.DATABASE_TEMP);
				adins.executeMapReduce(Install.class, MongoDBConnConfig.DATABASE_TEMP);
			}
			
			logger.error("日志每小时分析任务完成：{}。等待下一次统计任务",new Date().getTime());
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
	}
	
	
	/**
	 * 对一天的数据做统计
	 * @param date
	 */
	public static void dayStatistic(Date date)
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
	
	public static void main(String[] args) {
		dayStatistic(new Date());
	}
	
	

}
