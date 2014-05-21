package com.jukuad.statistic.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jukuad.statistic.config.MysqlConnection;

public class GenerateTxt 
{
	private static final Logger logger = LoggerFactory.getLogger(GenerateTxt.class);
	private static final String path = "d:/geography.txt";
	public static List<String> writeData() {
		List<String> arrs = new ArrayList<String>();
		Connection conn = null;
		try 
		{
			conn = MysqlConnection.getConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT t.`MNC`,t.`LAC`,t.`CELL`,t.`city`,t.`county` FROM geography t WHERE t.`LAC` <> '0' ORDER BY ROUND(t.`MNC`), ROUND(t.`LAC`),ROUND(t.`CELL`)");
			ResultSet result = statement.executeQuery();
			StringBuffer sb = null;
			 while (result.next()) 
	         {
				 sb = new StringBuffer();
				 sb.append(result.getString("MNC")).append("|");
				 sb.append(result.getString("LAC")).append("|");
				 sb.append(result.getString("CELL")).append("|");
				 sb.append(result.getString("city")).append("|");
				 sb.append(result.getString("county"));
				 arrs.add(sb.toString());
	         }
			
		} catch (SQLException e) {
			logger.error("数据库连接异常：{}", e.getMessage());
		} catch (Exception e) {
			logger.error("数据库插入异常：{}", e.getMessage());
		}
		return arrs;
	}
	
	public static void generate(List<String> arrs)
	{
//		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			File file = new File(path);
			if(file.exists()) file.delete();
//			fw = new FileWriter(new File(path));
//			BufferedWriter bw = new BufferedWriter(fw);
			bw = new BufferedWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
			for(String arr : arrs){
	            bw.write(arr+"\t\n");
	        }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
        	try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	public static void main(String[] args) {
		List<String> list = writeData();
		generate(list);
		System.out.println("完成-------------");
	}

}



