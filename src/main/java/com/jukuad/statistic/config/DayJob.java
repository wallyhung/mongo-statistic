package com.jukuad.statistic.config;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.pojo.AdDayStatistic;
import com.jukuad.statistic.pojo.AppDayStatistic;
import com.jukuad.statistic.pojo.DaySum;
import com.jukuad.statistic.service.AdMongService;
import com.jukuad.statistic.service.AppMongService;
import com.jukuad.statistic.service.BaseService;
import com.jukuad.statistic.service.BaseServiceImpl;
import com.jukuad.statistic.service.MongoService;
import com.jukuad.statistic.service.PushService;
import com.jukuad.statistic.service.PushServiceImpl;
import com.jukuad.statistic.service.StatisticService;
import com.jukuad.statistic.util.TimeUtil;

public class DayJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(DayJob.class);
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		logger.info("每天的日志分析任务启动了....");
		logger.debug("Generating report -{}; 调度器执行的时间为： {} ",context.getJobDetail().getFullName(), context.getJobDetail().getJobDataMap().get("date"));
		
		Date date = TimeUtil.getLastDay(new Date());
		logger.info("当前进行的一天统计为：",date);
		
		StatisticService.dayStatistic(date);
		logger.info("昨天的统计任务完成，推送数据到web服务器....");
		
		//get data
		MongoService<AppDayStatistic> appservice = new AppMongService<AppDayStatistic>();
		MongoService<AdDayStatistic> adservice = new AdMongService<AdDayStatistic>();
		
		List<AppDayStatistic> applist = appservice.queryDayStatistic(date);
		List<AdDayStatistic> adlist = adservice.queryDayStatistic(date);
		BaseService<DaySum> service = new BaseServiceImpl<DaySum>();
		
		PushService pushService = new PushServiceImpl();
		pushService.writeDayDataToMysql(applist, adlist, service.queryDaySum(date));
		
		logger.info("昨天的统计结果推送完成....");
	}

}
