package com.jukuad.statistic.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import com.jukuad.statistic.pojo.Push;

public interface PushDAO extends DAO<Push, ObjectId> 
{
	
}
