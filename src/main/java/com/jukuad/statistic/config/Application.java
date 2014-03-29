package com.jukuad.statistic.config;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
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
			jobDetail.getJobDataMap().put("hour", TimeUtil.getDayHour(new Date()));
			
			long startTime = System.currentTimeMillis() + 10*1000L;       
			SimpleTrigger trigger = new SimpleTrigger("hourTrigger",       
								                      "hourTriggerGroup",       
								                       new Date(startTime),       
								                       null,       
								                       SimpleTrigger.REPEAT_INDEFINITELY,       
								                       60*60*1000L);      
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
			JobDetail jobDetail = new JobDetail("dayStatisticJob", "dayJobGroup",DayJob.class);
			jobDetail.getJobDataMap().put("date", new Date());
            // 创建一个每天触发一次的触发器
			Trigger trigger = TriggerUtils.makeHourlyTrigger(24);
			trigger.setGroup("dayTriggerGroup");
			trigger.setStartTime(TriggerUtils.getDateOf(0,30,1));
			// 指明trigger的name
			trigger.setName("dayTrigger");
			
//			long endTime = System.currentTimeMillis() + 1*1*1000L;       
//			SimpleTrigger trigger = new SimpleTrigger("dayTrigger",       
//								                      "dayTriggerGroup",       
//								                       new Date(endTime),       
//								                       null,       
//								                       SimpleTrigger.REPEAT_INDEFINITELY,       
//								                       24*60*60*1000L);      
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
