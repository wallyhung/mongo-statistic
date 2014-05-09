package com.jukuad.statistic.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import com.jukuad.statistic.pojo.RequestTemp;

public interface RequestTempDAO extends DAO<RequestTemp, ObjectId> {

}
