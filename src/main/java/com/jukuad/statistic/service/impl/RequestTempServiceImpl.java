package com.jukuad.statistic.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jukuad.statistic.dao.RequestTempDAO;
import com.jukuad.statistic.log.ClientMessage;
import com.jukuad.statistic.pojo.Imei;
import com.jukuad.statistic.pojo.RequestTemp;
import com.jukuad.statistic.service.ImeiService;
import com.jukuad.statistic.service.RequestTempService;
import com.jukuad.statistic.util.Constant;
import com.jukuad.statistic.util.ObjectUtil;
@Service
public class RequestTempServiceImpl extends BaseServiceImpl<RequestTemp, ObjectId> implements
		RequestTempService 
{
	private static final Logger logger = LoggerFactory.getLogger(RequestTempService.class);
	private RequestTempDAO dao;
	private ImeiService imeService;
	
	@Autowired
	public RequestTempServiceImpl(RequestTempDAO dao,ImeiService imeService) {
		super(dao);
		this.dao = dao;
		this.imeService = imeService;
	}
	
	
	@Override
	public void parseAndSaveAndCopy(String hour, String type, String root) 
	{
		//创建Jackson全局的objectMapper 它既可以用于序列化 也可以用于反序列化
		ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		//得到JSON处理的工厂对象
		JsonFactory jsonFactory= objectMapper.getFactory();
		
		//进入读文件阶段
		InputStreamReader in = null;
		Integer idx = 1;
		try 
		{
			in = new InputStreamReader(new FileInputStream(new File(getLogPath(hour, type, root))), "UTF-8");
			BufferedReader br = new BufferedReader(in);
			String currentJsonStr= null;
			try {
				//按行读取
				while((currentJsonStr = br.readLine()) != null){
					if(currentJsonStr.trim().equals("")) continue;
					//进入反序列化阶段
					//通过JSON处理工厂对象创建JSON分析器
					JsonParser jsonParser= jsonFactory.createParser(currentJsonStr);
					try {
						//反序列化的关键
						ClientMessage object = jsonParser.readValueAs(ClientMessage.class);
						if (object != null)
						{
							RequestTemp temp = ObjectUtil.clientMessToRequest(object);
							dao.save(temp);
							Imei imei = ObjectUtil.clientMessToImei(object);
							imeService.updateImei(imei);
						}
						
					} catch (Exception e) 
					{
						logger.error("Request：日志解析数据错误在第{}行，具体的内容为：{}",idx,currentJsonStr);
						continue;
					}
					
					idx++;
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			finally{
				if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e1) {
	                	logger.error("关闭读取文件的缓冲流出错：{}。",e1.getMessage());
	                }
	            }
				if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e2) {
	                	logger.error("关闭读取文件的缓冲流出错：{}。",e2.getMessage());
	                }
	            }
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (UnsupportedEncodingException e3) {
			logger.error(e3.getMessage());
		} 
	}
	
	class ParseThread extends Thread
	{
		private String hour;
		private String type;
		private String root;
		public ParseThread(String hour,String type,String root) {
			this.hour = hour;
			this.type = type;
			this.root = root;
		}
		@Override
		public void run() {
			parseAndSaveAndCopy(hour, type, root);
		}
	}
	
	@Override
	public void parseAndSaveAndCopy(String hour, String type) {
		ExecutorService pool = Executors.newCachedThreadPool();
		for (String root : Constant.PATH) {
//			Thread t = new ParseThread(hour, type, root);
//			t.run();
			pool.submit(new ParseThread(hour, type, root));
		}
	}

}
