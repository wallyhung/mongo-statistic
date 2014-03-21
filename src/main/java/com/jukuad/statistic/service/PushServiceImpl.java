package com.jukuad.statistic.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MysqlConnection;
import com.jukuad.statistic.pojo.AdDayStatistic;
import com.jukuad.statistic.pojo.AppDayStatistic;
import com.jukuad.statistic.pojo.DaySum;

public class PushServiceImpl implements PushService
{
	private static final Logger logger = LoggerFactory.getLogger(PushService.class);
	
	private Connection connection;
	
	@Override
	public void writeDayDataToMysql(List<AppDayStatistic> applist,List<AdDayStatistic> adlist) 
	{
		//get connection
		connection = MysqlConnection.getConnection();
		PreparedStatement appStatement = null;
		PreparedStatement adStatement = null;
		try
		{
			
			/*
			  `oid` varchar(30) NOT NULL COMMENT '对应mongo库的objectid',
			  `adid` varchar(32) NOT NULL COMMENT '广告ID',
			  `push` bigint(20) DEFAULT '0' COMMENT '推送数',
			  `view` bigint(20) DEFAULT '0' COMMENT '展示数',
			  `click` bigint(20) DEFAULT '0' COMMENT '点击数',
			  `download` bigint(20) DEFAULT '0' COMMENT '下载数',
			  `install` bigint(20) DEFAULT '0' COMMENT '安装数',
			  `day` varchar(12) DEFAULT NULL COMMENT '统计时间',*/
			adStatement = connection.prepareStatement("insert into  ad_day_report values (?,?,?,?,?,?,?,?)");
			for (AdDayStatistic ad : adlist) {
				adStatement.setString(1, ad.getId().toString());
				adStatement.setString(2, ad.getAdid());
				adStatement.setLong(3, ad.getPush());
				adStatement.setLong(4, ad.getView());
				adStatement.setLong(5, ad.getClick());
				adStatement.setLong(6, ad.getDownload());
				adStatement.setLong(7, ad.getInstall());
				adStatement.setString(8, ad.getDay());
				
				adStatement.addBatch();
			}
			adStatement.executeBatch();
	        
		  /*  `oid` varchar(30) NOT NULL COMMENT '对应mongo库的objectid',
			  `fid` varchar(32) NOT NULL COMMENT '发布ID',
			  `request` bigint(20) DEFAULT '0' COMMENT '请求数',
			  `view` bigint(20) DEFAULT '0' COMMENT '展示数',
			  `cpc` bigint(20) DEFAULT '0' COMMENT 'cpc点击数',
			  `c_wall` bigint(20) DEFAULT '0' COMMENT '积分墙推荐点击数',
			  `c_oth` bigint(20) DEFAULT '0' COMMENT '非墙点击数',
			  `d_wall` bigint(20) DEFAULT '0' COMMENT '积分墙下载数',
			  `d_oth` bigint(20) DEFAULT '0' COMMENT '非墙下载数',
			  `i_wall` bigint(20) DEFAULT '0' COMMENT '积分墙安装数',
			  `i_oth` bigint(20) DEFAULT '0' COMMENT '非墙安装数',
			  `new_u` bigint(20) DEFAULT '0' COMMENT '新增用户',
			  `remain` bigint(20) DEFAULT '0' COMMENT '留存用户',
			  `alive` bigint(20) DEFAULT '0' COMMENT '日活用户',
			  `first` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '第一次请求时间',
			  `day` varchar(12) DEFAULT NULL COMMENT '统计时间',*/
			appStatement = connection.prepareStatement("insert into  app_day_report values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			for (AppDayStatistic app : applist) {
				appStatement.setString(1, app.getId().toString());
				appStatement.setString(2, app.getFid());
				appStatement.setLong(3, app.getRequest());
				appStatement.setLong(4, app.getView());
				appStatement.setLong(5, app.getClick().getCpc());
				appStatement.setLong(6, app.getClick().getWall());
				appStatement.setLong(7, app.getClick().getOth());
				appStatement.setLong(8, app.getDownload().getWall());
				appStatement.setLong(9, app.getDownload().getOth());
				appStatement.setLong(10, app.getInstall().getWall());
				appStatement.setLong(11, app.getInstall().getOth());
				appStatement.setLong(12, app.getNew_u());
				appStatement.setLong(13, app.getRemain());
				appStatement.setLong(14, app.getAlive());
				appStatement.setLong(15, app.getTimestamp());
				appStatement.setString(16, app.getDay());
				appStatement.addBatch();
			}
			appStatement.executeBatch();
			
		} catch (SQLException e) 
		{
			  // 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态   
	          try 
	          {
				if(!connection.isClosed())
				  {   
					connection.rollback();//4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；   
					  logger.error("插入错误，事物回滚:{}",e.getMessage());
				  }
			} catch (SQLException e1) 
			{
				logger.error("提交异常:{}",e.getMessage());
			}   
		}finally
		{
//			MysqlConnection.CloseAll(null, appStatement, connection);
//			MysqlConnection.CloseAll(null, adStatement, connection);
		}
	}
	
	
	@Override
	public void writeDaySumToMysql(DaySum sum) 
	{
		//get connection
		connection = MysqlConnection.getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			/*`oid` varchar(30) NOT NULL COMMENT '对应mongo主键objectid',
			  `new_a` bigint(20) DEFAULT '0' COMMENT '新增应用数',
			  `push` bigint(20) DEFAULT '0' COMMENT '推送数',
			  `view` bigint(20) DEFAULT '0' COMMENT '展示数',
			  `click` bigint(20) DEFAULT '0' COMMENT '点击数',
			  `alive` bigint(20) DEFAULT '0' COMMENT '终端数',
			  `day` varchar(12) DEFAULT NULL COMMENT '统计时间',*/
			preparedStatement = connection.prepareStatement("insert into  day_sum values (?,?,?,?,?,?,?)");
			preparedStatement.setString(1, sum.getId().toString());
			preparedStatement.setLong(2, sum.getNew_a());
			preparedStatement.setLong(3, sum.getPush());
			preparedStatement.setLong(4, sum.getView());
			preparedStatement.setLong(5, sum.getClick());
			preparedStatement.setLong(6, sum.getAlive());
			preparedStatement.setString(7, sum.getDay());
	        preparedStatement.executeUpdate();
		} catch (SQLException e) 
		{
			  // 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态   
	          try 
	          {
				if(!connection.isClosed())
				  {   
					  connection.rollback();//4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；   
					  logger.error("插入错误，事物回滚:{}",e.getMessage());
				  }
			} catch (SQLException e1) 
			{
				logger.error("提交异常:{}",e.getMessage());
			}   
		}finally
		{
			MysqlConnection.CloseAll(null, preparedStatement,connection);
		}
	}

}
