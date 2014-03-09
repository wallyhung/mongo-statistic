package com.jukuad.statistic.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MysqlConnection;
import com.jukuad.statistic.pojo.DayStatistic;
import com.jukuad.statistic.pojo.DaySum;

public class PushServiceImpl implements PushService
{
	private static final Logger logger = LoggerFactory.getLogger(PushService.class);
	
	private Connection connection;
	
	@Override
	public void writeDayDataToMysql(List<DayStatistic> list) 
	{
		//get connection
		connection = MysqlConnection.getConnection();
		PreparedStatement preparedStatement = null;
		try
		{
			preparedStatement = connection.prepareStatement("insert into  day_report values (?, ?, ?,?)");
			for (DayStatistic dayStatistic : list) {
				preparedStatement.setString(1, dayStatistic.getId().toString());
				preparedStatement.setString(2, dayStatistic.getFid());
				preparedStatement.setString(3, dayStatistic.getAdid());
				preparedStatement.setString(4, dayStatistic.getDay());
				preparedStatement.addBatch();
			}
			
	        preparedStatement.executeBatch();
		} catch (SQLException e) 
		{
			  // 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态   
	          try 
	          {
				if(!connection.isClosed())
				  {   
					connection.rollback();//4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；   
					  logger.error("插入错误，事物回滚:{}",e.getMessage());
//					  mysql.connection.setAutoCommit(true);   
				  }
			} catch (SQLException e1) 
			{
				logger.error("提交异常:{}",e.getMessage());
			}   
		}finally
		{
			MysqlConnection.CloseAll(null, preparedStatement, connection);
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
			preparedStatement = connection.prepareStatement("insert into  day_sum values (?, ?, ?,?)");
			preparedStatement.setString(1, sum.getId().toString());
			preparedStatement.setDouble(2, sum.getPush());
			preparedStatement.setDouble(3, sum.getClick());
			preparedStatement.setDouble(4, sum.getView());
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
