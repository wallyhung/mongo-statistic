package com.jukuad.statistic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jukuad.statistic.config.MongoDBConnConfig;
import com.jukuad.statistic.config.MongoDBDataStore;
import com.jukuad.statistic.log.AdFeedback;
import com.jukuad.statistic.log.ClientMessage;
import com.jukuad.statistic.log.SoftFeedback;
import com.jukuad.statistic.pojo.BaseEntity;

public class LogFileParser<T> implements Runnable
{
	
	private static final Logger logger = LoggerFactory.getLogger(LogFileParser.class);
	//解析的文件对应的pojo
	private Class<T> className;
	
	//日志文件路径
	private String path;
		
	//当前线程运行的任务数
	private CountDownLatch count;
	
	public LogFileParser() {
	}
	
	public LogFileParser(Class<T> className,String path,CountDownLatch count)
	{
		this.path = path;
		this.className = className;
		this.count = count;
	}
	
	@SuppressWarnings("resource")
	public ArrayList<T> parse()
	{
		
		//创建Jackson全局的objectMapper 它既可以用于序列化 也可以用于反序列化
		ObjectMapper objectMapper = new ObjectMapper();
		//得到JSON处理的工厂对象
		JsonFactory jsonFactory= objectMapper.getFactory();
		
		//进入读文件阶段
		FileReader fr = null;
		Integer idx = 1;
		List<T> list = new ArrayList<T>();
		try 
		{
			fr = new FileReader(new File(path));
			BufferedReader br = new BufferedReader(fr);
			String currentJsonStr= null;
			try {
				//按行读取
				while((currentJsonStr = br.readLine()) != null){
					currentJsonStr = new String(currentJsonStr.getBytes(), "UTF-8");
					if(currentJsonStr.trim().equals("")) continue;
					//进入反序列化阶段
					//通过JSON处理工厂对象创建JSON分析器
					JsonParser jsonParser= jsonFactory.createParser(currentJsonStr);
					//反序列化的关键
					T object = jsonParser.readValueAs(className);
					
					if (object != null){
						list.add(object);
					}
					idx++;
				}
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} 
		return (ArrayList<T>) list;
	}
	
	
	private BaseEntity parseDBObject(T t)
	{
		BaseEntity entity = null;
		if(path.indexOf(Constant.PATH_REQUEST) > -1)
			entity = ObjectUtil.clientMessToRequest((ClientMessage) t);
		if(path.indexOf(Constant.PATH_PUSH) > -1)
			entity = ObjectUtil.pushDataToPush((AdFeedback) t);
		if(path.indexOf(Constant.PATH_VIEW) > -1)
			entity = ObjectUtil.viewDataToPush((AdFeedback) t);
		if(path.indexOf(Constant.PATH_CLICK) > -1)
			entity = ObjectUtil.clickDataToPush((AdFeedback) t);
		if(path.indexOf(Constant.PATH_DOWNLOAD) > -1)
			entity = ObjectUtil.downloadDataToPush((SoftFeedback) t);
		if(path.indexOf(Constant.PATH_INSTALL) > -1)
			entity = ObjectUtil.installDataToPush((SoftFeedback) t);
		return entity;
	}
	
	
	
	@Override
	public void run() 
	{
		//得到Morphia框架的Datastore对象用于数据库操作
		Datastore ds = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_TEMP);
		Datastore back = MongoDBDataStore.getDBInstance(MongoDBConnConfig.DATABASE_DATA);
		List<T> list = this.parse();
		for (T t : list) 
		{
			BaseEntity entity = parseDBObject(t);
			Key<BaseEntity> key = ds.save(entity);
			Key<BaseEntity> backkey = back.save(entity);
			if (key != null && backkey != null)
			{
			logger.info("已存入:"+ key.getId() + ",元素序列:");
			}else
			{
				System.out.println("元素序列"+ "发生错误,JSON:");
				logger.error("元素序列"+ "发生错误,JSON:");
				break;
			}
		}
		count.countDown();
		logger.info("{}个日志分析任务完成：{}，当前线程运行的任务数为{}。",className,new Date().getTime(),count.getCount());
	}

}
