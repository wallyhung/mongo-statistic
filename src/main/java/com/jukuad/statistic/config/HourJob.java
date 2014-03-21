package com.jukuad.statistic.config;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.service.LogAnalysisService;
import com.jukuad.statistic.service.StatisticService;
import com.jukuad.statistic.util.Constant;

public class HourJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(HourJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		logger.error("每小时的日志分析任务启动了....");
		logger.info("当前的时间为：{}",new Date().getTime());
		logger.error("Generating report -{}; hour = {} ",context.getJobDetail().getFullName(), context.getJobDetail().getJobDataMap().get("hour"));
		
		//检测是否有请求日志
		if(LogAnalysisService.existNewLogs(Constant.PATH_REQUEST))
		{
			//统计任务
			StatisticService.statistic();
			//推送统计结果到web服务器
		}
		else
		{
			logger.error("当前的时间为：{},当前木有新的日志产生，定时任务继续等待....",new Date().getTime());
		}
		
	}

}
