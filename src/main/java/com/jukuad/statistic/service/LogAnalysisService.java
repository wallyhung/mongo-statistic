package com.jukuad.statistic.service;

import java.io.File;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.config.MongoDBDataStore;
import com.jukuad.statistic.log.AdFeedback;
import com.jukuad.statistic.log.ClientMessage;
import com.jukuad.statistic.log.SoftFeedback;
import com.jukuad.statistic.util.Constant;
import com.jukuad.statistic.util.LogFileParser;
import com.jukuad.statistic.util.TimeUtil;

public class LogAnalysisService 
{
	private static final Logger logger = LoggerFactory.getLogger(LogAnalysisService.class);
	
	public static ExecutorService executor = Executors.newCachedThreadPool();
	
	//日志文件夹目录
	private static final String logpath = "d:\\bin\\logs\\";
	
	/**
	 * 检查是否有新的日志文件
	 * @return
	 */
	public static boolean existNewLogs()
	{
		boolean bool = false;
		String path = logpath + Constant.PATH_REQUEST + "/" + TimeUtil.getDayLastHour(new Date()) + ".log";
		File file = new File(path);
		if(file.exists()) bool = true;
		return bool;
	}
	
	public static CountDownLatch analyzeLog()
	{
		//清理缓存数据库
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_TEMP);
		ds.getMongo().dropDatabase(MongoDBConnConfig.DATABASE_TEMP);
		
		int taskNum = 6;
		final CountDownLatch count = new CountDownLatch(taskNum);
		//提交6个日志分析任务
		System.out.println("6个任务放入：" + new Date());
		logger.info("{}个日志分析任务将运行：{}。",taskNum,new Date().getTime());
		executor.submit(new LogFileParser<ClientMessage>(ClientMessage.class,logpath + Constant.PATH_REQUEST + "/" + TimeUtil.getDayLastHour(new Date()) + ".log",count));
		executor.submit(new LogFileParser<AdFeedback>(AdFeedback.class,logpath + Constant.PATH_PUSH + "/" + TimeUtil.getDayLastHour(new Date()) + ".log",count));
		executor.submit(new LogFileParser<AdFeedback>(AdFeedback.class,logpath + Constant.PATH_VIEW + "/" + TimeUtil.getDayLastHour(new Date()) + ".log",count));
		executor.submit(new LogFileParser<AdFeedback>(AdFeedback.class,logpath + Constant.PATH_CLICK + "/" + TimeUtil.getDayLastHour(new Date()) + ".log",count));
		executor.submit(new LogFileParser<SoftFeedback>(SoftFeedback.class,logpath + Constant.PATH_DOWNLOAD + "/" + TimeUtil.getDayLastHour(new Date()) + ".log",count));
		executor.submit(new LogFileParser<SoftFeedback>(SoftFeedback.class,logpath + Constant.PATH_INSTALL + "/" + TimeUtil.getDayLastHour(new Date()) + ".log",count));
		return count;
	}
    
}
