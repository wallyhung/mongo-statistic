package com.jukuad.statistic.dao.impl;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jukuad.statistic.dao.ImeiDAO;
import com.jukuad.statistic.pojo.Imei;
@Repository
public class ImeiDAOImpl extends BasicDAO<Imei, ObjectId> implements ImeiDAO {

	@Autowired
	protected ImeiDAOImpl(Datastore ds) {
		super(ds);
	}

}
