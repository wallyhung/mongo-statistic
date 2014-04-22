package com.jukuad.statistic.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jukuad.statistic.dao.RequestDAO;
import com.jukuad.statistic.pojo.Request;

@Component
public class RequestDAOImpl extends BasicDAO<Request, ObjectId> implements RequestDAO {
	@Autowired
	protected RequestDAOImpl(Datastore datastore) {
		super(datastore);
	}
}
