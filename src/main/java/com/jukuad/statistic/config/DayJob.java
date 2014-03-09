package com.jukuad.statistic.config;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.pojo.DayStatistic;
import com.jukuad.statistic.service.MongoService;
import com.jukuad.statistic.service.MongoServiceImpl;
import com.jukuad.statistic.service.PushService;
import com.jukuad.statistic.service.PushServiceImpl;
import com.jukuad.statistic.service.StatisticService;

public class DayJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(DayJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		logger.info("每天的日志分析任务启动了....");
		logger.info("当前的时间为：{}",new Date().getTime());
		logger.info("Generating report -{}; date = {} ",context.getJobDetail().getFullName(), context.getJobDetail().getJobDataMap().get("date"));
		Date date = (Date) context.getJobDetail().getJobDataMap().get("date");
		StatisticService.dayStatistic(date);
		logger.info("当前的时间为：{},当天的统计任务完成，推送数据到web服务器....",new Date().getTime());
		
		//get data
		MongoService<DayStatistic> service = new MongoServiceImpl<DayStatistic>();
		List<DayStatistic> list = service.queryDayStatistic(date);
		
		PushService pushService = new PushServiceImpl();
		pushService.writeDayDataToMysql(list);
		pushService.writeDaySumToMysql(service.queryDaySum(date));
		
	}

}
