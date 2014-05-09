package com.jukuad.statistic.dao;

import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import com.jukuad.statistic.pojo.Imei;

public interface ImeiDAO extends DAO<Imei, ObjectId> {

}
