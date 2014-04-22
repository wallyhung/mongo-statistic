package com.jukuad.statistic.service.impl;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jukuad.statistic.dao.RequestDAO;
import com.jukuad.statistic.pojo.Request;
import com.jukuad.statistic.service.RequestService;

@Service
public class RequestServiceImpl extends BaseServiceImpl<Request, ObjectId> implements
		RequestService 
{
	private RequestDAO requestDAO;

	@Autowired
	public RequestServiceImpl(RequestDAO requestDAO) {
		super(requestDAO);
	}
	

}
