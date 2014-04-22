package com.jukuad.statistic.service.impl;

import org.mongodb.morphia.dao.DAO;
import org.springframework.stereotype.Service;

import com.jukuad.statistic.service.BaseService;

@Service
public class BaseServiceImpl<T,K> implements BaseService<T,K>
{
	
	private DAO<T, K> baseDAO;
	
	public BaseServiceImpl() {
	}
	
	public BaseServiceImpl(DAO<T, K> baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	@Override
	public T find() {
		return baseDAO.find().get();
	}
	
}

