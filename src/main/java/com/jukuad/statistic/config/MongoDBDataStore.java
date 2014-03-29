package com.jukuad.statistic.config;

import java.net.UnknownHostException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
 * MongoDB联合Morphia框架生成DataStore的类
 * @author wally
 *
 */
public class MongoDBDataStore 
{
	/**
	 * 生成数据库Datastore对象
	 * @return 返回Datastore对象 发生异常返回为null
	 */
	public static Datastore getDBInstance(String database)
	{
		MongoClient connection = null;
		try 
		{
			connection = new MongoClient(new ServerAddress(MongoDBConnConfig.SERVER, MongoDBConnConfig.PORT));
		} catch (UnknownHostException e) {
			return null;
		} catch (MongoException e) {
			return null;
		}
		Morphia morphia= new Morphia();
		return morphia.createDatastore(connection, database);
	}
}