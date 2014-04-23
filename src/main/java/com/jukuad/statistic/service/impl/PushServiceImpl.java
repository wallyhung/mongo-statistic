package com.jukuad.statistic.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jukuad.statistic.dao.PushDAO;
import com.jukuad.statistic.pojo.Push;
import com.jukuad.statistic.service.PushService;
@Service
public class PushServiceImpl extends BaseServiceImpl<Push, ObjectId> implements
		PushService 
{
   @Autowired
   public PushServiceImpl(PushDAO dao) {
	   super(dao);
   }
}
