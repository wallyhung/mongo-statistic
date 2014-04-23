package com.jukuad.statistic.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jukuad.statistic.dao.RequestTempDAO;
import com.jukuad.statistic.pojo.RequestTemp;

@Repository
public class RequestTempDAOImpl extends BasicDAO<RequestTemp, ObjectId> implements
		RequestTempDAO {

	@Autowired
	protected RequestTempDAOImpl(Datastore ds) {
		super(ds);
	}

}
