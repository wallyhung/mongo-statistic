package com.jukuad.statistic.config;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.context.annotation.Bean;

import com.jukuad.statistic.base.DatastoreFactoryBean;
import com.jukuad.statistic.base.MongoClientOptionsBean;
import com.jukuad.statistic.base.MongoFactoryBean;
import com.jukuad.statistic.base.MorphiaFactoryBean;
import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;

public class SpringMongoConfig 
{
	public @Bean MongoClientOptions mongoClientOptions()
	{
		MongoClientOptions options = null;
		try {
			options = new MongoClientOptionsBean().getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return options;
	}
	
	public @Bean Mongo mongo()
	{
		Mongo client = null;
		try {
			client = new MongoFactoryBean().getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return client;
	}
	
	public @Bean Morphia morphia()
	{
		Morphia morphia = null;
		try {
			morphia = new MorphiaFactoryBean().getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return morphia;
	}
	
	
	public @Bean Datastore datastore()
	{
		Datastore datastore = null;
		try {
			datastore = new DatastoreFactoryBean().getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datastore;
	}
	
	
	

	

}
