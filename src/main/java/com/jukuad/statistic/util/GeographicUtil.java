package com.jukuad.statistic.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeographicUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(GeographicUtil.class);
	private static final Map<String,String> map = new HashMap<String, String>();
	private static String path = "d:/geography.txt";
	public GeographicUtil() {
	}
	static
	{
		//进入读文件阶段
		InputStreamReader in = null;
		try 
		{
			in = new InputStreamReader(new FileInputStream(new File(path)), "utf-8");
			BufferedReader br = new BufferedReader(in);
			String currentJsonStr= null;
			try {
				//按行读取
				while((currentJsonStr = br.readLine()) != null){
					if(currentJsonStr.trim().equals("")) continue;
					String[] vals = currentJsonStr.split("\\|");
					map.put(String.format("%s|%s|%s", vals[0],vals[1],vals[2]), String.format("%s|%s",vals[3],vals[4]));
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			finally{
				if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e1) {
	                	logger.error("关闭读取文件的缓冲流出错：{}。",e1.getMessage());
	                }
	            }
				if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e2) {
	                	logger.error("关闭读取文件的缓冲流出错：{}。",e2.getMessage());
	                }
	            }
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	private static String parse(String ua) 
	{
		if(ua != null && !"".equals(ua) && ua.indexOf("//") > -1)
		{
			int index = ua.indexOf("//");
			ua = ua.substring(index + 2,ua.length());
			String[] uas = ua.split("/");
			String key = String.format("%s|%s|%s", uas[1],uas[2],uas[3]);
			return map.get(key);
		}
		else return "";
	}
	
	public static void main(String[] args) {
		String ua = "Juku.com/Android/4.1.2/GT-I8268/JZO54K.I8268ZMAME1/PXA988/aruba3gcmcc/358585050032968//460/0/55148/185076745";
		System.out.println(parse(ua));
	}
	
	
}
