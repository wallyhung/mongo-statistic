package com.jukuad.statistic.service;

import org.bson.types.ObjectId;

import com.jukuad.statistic.pojo.Imei;

public interface ImeiService extends BaseService<Imei, ObjectId>{
	
	void updateImei(Imei imei);

}
