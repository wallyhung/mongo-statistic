package com.jukuad.statistic.config;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.util.TimeUtil;

public class Application 
{
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static SchedulerFactory factory = new StdSchedulerFactory();
	
	private static void generateHourScheduler()
	{
		try 
		{
			// 创建一个Scheduler
			Scheduler scheduler = factory.getScheduler();
		    scheduler.start();
			// 创建一个JobDetail，指明name，groupname，以及具体的Job类名，
			//该Job负责定义需要执行任务
			JobDetail jobDetail = new JobDetail("hourStatisticJob", "hourJobGroup",HourJob.class);
			jobDetail.getJobDataMap().put("hour", TimeUtil.getDayLastHour(new Date()));
			
            // 创建一个每小时触发1次的Trigger
//			Trigger trigger = TriggerUtils.makeHourlyTrigger(1, 1);
			Trigger trigger = TriggerUtils.makeMinutelyTrigger(1, 1);
			trigger.setGroup("hourTriggerGroup");
			// 从当前时间的下一秒开始执行
			trigger.setStartTime(TriggerUtils.getEvenMinuteDate(new Date()));
			// 指明trigger的name
			trigger.setName("hourTrigger");
			// 用scheduler将JobDetail与Trigger关联在一起，开始调度任务
			scheduler.scheduleJob(jobDetail, trigger);
			
		} catch (SchedulerException e) 
		{
			logger.error(e.getMessage());
		}
	}
	
	private static void generateDayScheduler()
	{
		try 
		{
			// 创建一个Scheduler
			Scheduler scheduler = factory.getScheduler();
		    scheduler.start();
			// 创建一个JobDetail，指明name，groupname，以及具体的Job类名，
			//该Job负责定义需要执行任务
			JobDetail jobDetail = new JobDetail("dayStatisticJob", "dayJobGroup",DayJob.class);
			jobDetail.getJobDataMap().put("date", TimeUtil.getLastDayEnd(new Date()));
			
            // 创建一个每天触发一次的触发器
//			Trigger trigger = TriggerUtils.makeHourlyTrigger(24);
			Trigger trigger = TriggerUtils.makeMinutelyTrigger(1, 1);
			trigger.setGroup("dayTriggerGroup");
			// 从当前时间的下一秒开始执行
//			trigger.setStartTime(TriggerUtils.getDateOf(0,30,1));
			trigger.setStartTime(TriggerUtils.getDateOf(0,27,10));
			// 指明trigger的name
			trigger.setName("dayTrigger");
			// 用scheduler将JobDetail与Trigger关联在一起，开始调度任务
			scheduler.scheduleJob(jobDetail, trigger);
			
		} catch (SchedulerException e) 
		{
			logger.error(e.getMessage());
		}
	}
	
	public static void main(String[] args) 
	{
		generateHourScheduler();
		generateDayScheduler();
	}

}
