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


public class PhoneUtil 
{
	private static final Logger logger = LoggerFactory.getLogger(PhoneUtil.class);
	private static final Map<Integer,String> map = new HashMap<Integer, String>();
	private static String path = "d:/手机号段-20140401-296010.txt";
	private static Phone phone = new Phone();
	public PhoneUtil() {
	}
	static
	{
		//进入读文件阶段
		InputStreamReader in = null;
		try 
		{
			in = new InputStreamReader(new FileInputStream(new File(path)), "gb2312");
			BufferedReader br = new BufferedReader(in);
			String currentJsonStr= null;
			try {
				//按行读取
				while((currentJsonStr = br.readLine()) != null){
					if(currentJsonStr.trim().equals("")) continue;
					//进入反序列化阶段
					//通过JSON处理工厂对象创建JSON分析器
					String phone_suff = currentJsonStr.substring(0,7);
					map.put(Integer.parseInt(phone_suff), currentJsonStr);
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
	
	private static Phone parse(String no) 
	{
		if(null != no && !"".equals(no))
		{
			int suff = Integer.parseInt(no.substring(0, 7));
			String text = map.get(suff);
			if(text != null)
			{
				String[] vals = text.split("\\|");
				phone.setProvince(vals[1]);
				phone.setCity(vals[2]);
				phone.setNet(vals[5]);
			}
		}
		return phone;
	}
	
	public static String getProvince(String no)
	{
		phone = parse(no);
		return phone.getProvince();
	}
	
	public static String getCity(String no)
	{
		phone = parse(no);
		return phone.getCity();
	}
	
	public static String getNet(String no)
	{
		phone = parse(no);
		return phone.getNet();
	}
}
