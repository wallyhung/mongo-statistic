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
import com.jukuad.statistic.util.TimeUtil;

public class HourJob implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(HourJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		logger.info("每小时的日志分析任务启动了....");
		logger.info("Generating report -{}; 调度器执行的时间为： {} ",context.getJobDetail().getFullName(), context.getJobDetail().getJobDataMap().get("hour"));
		
		String hour = TimeUtil.getDayLastHour(new Date());
		logger.info("当前进行的小时统计为： {}",hour);
		//检测是否有请求日志
		if(LogAnalysisService.existNewLogs(Constant.PATH_REQUEST,hour))
		{
			//统计任务
			StatisticService.hourStatistic(hour);
			//推送统计结果到web服务器
		}
		else
		{
			logger.info("当前的时间为：{},上个小时木有新的日志产生，定时任务继续等待....",TimeUtil.getDayTime(new Date()));
		}
	}
}
