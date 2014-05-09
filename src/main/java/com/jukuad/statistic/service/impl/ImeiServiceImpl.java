package com.jukuad.statistic.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jukuad.statistic.dao.ImeiDAO;
import com.jukuad.statistic.pojo.Imei;
import com.jukuad.statistic.service.ImeiService;

@Service
public class ImeiServiceImpl extends BaseServiceImpl<Imei, ObjectId> implements
		ImeiService {
	
	private ImeiDAO dao;
	
	@Autowired
	public ImeiServiceImpl(ImeiDAO dao) {
		super(dao);
		this.dao = dao;
	}
	
	
	@Override
	public void updateTime(Imei imei) 
	{
		Imei data = dao.createQuery().field("value").equal(imei.getValue()).get();
		if(data != null)
		{
			dao.delete(data);
		}
		dao.save(imei);
	}
	
	

}
