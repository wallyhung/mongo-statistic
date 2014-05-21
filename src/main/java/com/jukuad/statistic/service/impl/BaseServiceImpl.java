package com.jukuad.statistic.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.bson.BSONException;
import org.mongodb.morphia.dao.DAO;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jukuad.statistic.service.BaseService;
import com.jukuad.statistic.util.Constant;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

@Service
public class BaseServiceImpl<T,K> implements BaseService<T,K>
{
	private static final Logger logger = LoggerFactory.getLogger(BaseService.class);
	private DAO<T, K> baseDAO;
	private Class<T> entityClass;
	public BaseServiceImpl() {
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseServiceImpl(DAO<T, K> baseDAO) {
		this.baseDAO = baseDAO;
		this.entityClass = null;
        Class c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
            this.entityClass = (Class<T>) parameterizedType[0];
        }
	}
	
	@Override
	public T findNewest(String order)
	{
		return baseDAO.createQuery().order(order).get();
	}
	
	@Override
	public List<T> findByHour(String hour) {
		return baseDAO.createQuery().field("hour").equal(hour).asList();
	}
	
	@Override
	public List<T> findByDay(String day) {
		return baseDAO.createQuery().field("day").equal(day).asList();
	}
	
	@Override
	public List<T> findBetweenStringTime(String start, String end, String fields) 
	{
		Query<T> query = baseDAO.createQuery().field(fields).greaterThanOrEq(start)
				                              .field(fields).lessThanOrEq(end);
		return query.asList();
	}
	
	@Override
	public List<T> findBetweenTime(long start, long end) 
	{
		Query<T> query = baseDAO.createQuery().field("time").greaterThanOrEq(start)
                                              .field("time").lessThanOrEq(end);
		return query.asList();
	}
	
	@Override
	public void copyCollection(String target) 
	{
        final int OBJECT_BUFFER_SIZE = 2000;
        int rowNumber = 0;
        List<DBObject> objects;
        DB db = baseDAO.getDatastore().getDB();
        DBCollection collection = baseDAO.getCollection();
        final long totalRows = collection.count();
        DBCursor cursor;
        logger.debug("Mongo query result size: " + totalRows);
            // Loop design based on this:
            // http://stackoverflow.com/questions/18525348/better-way-to-move-mongodb-collection-to-another-collection/20889762#20889762
            // Use multiple threads to improve
            do {
                logger.debug(String.format("Mongo buffer starts row %d - %d copy into %s", rowNumber, (rowNumber + OBJECT_BUFFER_SIZE) - 1, target));
                cursor = collection.find().sort(new BasicDBObject("$natural", 1)).skip(rowNumber).limit(OBJECT_BUFFER_SIZE);
                objects = cursor.toArray();
                try {
                    if (objects.size() > 0) {
                    	db.getCollection(target).insert(objects,WriteConcern.SAFE);
                    }
                } catch (final BSONException e) {
                    logger.warn(String.format("Mongodb copy %s %s: mongodb error. A row between %d - %d will be skipped.",
                    		collection.getName(), target, rowNumber, rowNumber + OBJECT_BUFFER_SIZE));
                    logger.error(e.getMessage());
                } catch (Exception e) {
                	logger.error("Mongodb copy {} into {}: {}.",
            			    collection.getName(), target,e.getMessage(),rowNumber, rowNumber + OBJECT_BUFFER_SIZE);
                	logger.error("data backup failure.");
                	break;
				}
                rowNumber = rowNumber + objects.size();
            } while (rowNumber < totalRows);
	}
	
	@Override
	public int getTaskCount(String hour)
	{
		int n = 0;
		for (String root : Constant.PATH) {
			if(existNewLogs(hour, Constant.PATH_REQUEST,root))  n++;
			if(existNewLogs(hour, Constant.PATH_PUSH,root))     n++;
			if(existNewLogs(hour, Constant.PATH_VIEW,root))     n++;
			if(existNewLogs(hour, Constant.PATH_CLICK,root))    n++;
			if(existNewLogs(hour, Constant.PATH_DOWNLOAD,root)) n++;
			if(existNewLogs(hour, Constant.PATH_INSTALL,root))  n++;
		}
		return n;
	}
	
	@Override
	public String getLogPath(String hour, String type, String root) {
		return root + type + "/" + hour + ".log";
	}
	
	@Override
	public boolean existNewLogs(String hour, String type, String root) {
		boolean bool = false;
		File file = new File(getLogPath(hour,type, root));
		if(file.exists()) bool = true;
		return bool;
	}
	
	@Override
	public boolean existNewLogs(String hour, String type) 
	{
		boolean bool = false;
		for (String root : Constant.PATH) {
			bool = existNewLogs(hour, type,root);
			if(bool) break;
		}
		return bool;
	}
	
	@Override
	public void parseAndSaveAndCopy(String hour,String type,String root) 
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
						T object = jsonParser.readValueAs(entityClass);
						if (object != null){
							baseDAO.save(object);
						}
						
					} catch (Exception e) 
					{
						logger.error("{}：日志解析数据错误在第{}行，具体的内容为：{}",entityClass,idx,currentJsonStr);
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
	
	@Override
	public void parseAndSaveAndCopy(String hour, String type) {
		for (String root : Constant.PATH) {
			parseAndSaveAndCopy(hour, type, root);
		}
	}
	
}

