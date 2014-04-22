package com.jukuad.statistic.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jukuad.statistic.dao.PushDAO;
import com.jukuad.statistic.pojo.Push;
@Component
public class PushDAOImpl extends BasicDAO<Push, ObjectId> implements PushDAO {

	@Autowired
	protected PushDAOImpl(Datastore datastore) {
		super(datastore);
	}

}
