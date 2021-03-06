package com.jukuad.statistic.util;

import com.jukuad.statistic.log.AdFeedback;
import com.jukuad.statistic.log.ClientMessage;
import com.jukuad.statistic.log.SoftFeedback;
import com.jukuad.statistic.pojo.Click;
import com.jukuad.statistic.pojo.Download;
import com.jukuad.statistic.pojo.Install;
import com.jukuad.statistic.pojo.Push;
import com.jukuad.statistic.pojo.Request;
import com.jukuad.statistic.pojo.View;
/**
 * 采集日志信息转为数据库对象
 * @author Administrator
 *
 */
public class ObjectUtil {

	/**
	 * 请求数据
	 * @param mess
	 * @return
	 */
	public static Request clientMessToRequest(ClientMessage mess)
	{
		if(mess == null) return null;
		else
		{
			Request request = new Request();
			request.setCid(mess.getChannel_id());
			request.setFid(mess.getSlot_name());
			request.setImei(mess.getImei());
			request.setLoc(mess.getLocation());
			request.setName(mess.getApp_name());
			request.setPlat(mess.getPlatfrom());
			request.setTimestamp(mess.getTimestamp());
			return request;
		}
	}
	
	/**
	 * 推送数据
	 * @param data
	 * @return
	 */
	public static Push pushDataToPush(AdFeedback data)
	{
		if(data == null) return null;
		else
		{
			Push push = new Push();
			push.setFid(data.getSlot_name());
			push.setImei(data.getImei());
			push.setAdid(data.getAdid());
			push.setTimestamp(data.getTimestamp());
			return push;
		}
	}
	
	/**
	 * 展示数据
	 * @param data
	 * @return
	 */
	public static View viewDataToPush(AdFeedback data)
	{
		if(data == null) return null;
		else
		{
			View view = new View();
			view.setFid(data.getSlot_name());
			view.setImei(data.getImei());
			view.setAdid(data.getAdid());
			view.setTimestamp(data.getTimestamp());
			return view;
		}
	}
	
	/**
	 * 点击数据
	 * @param data
	 * @return
	 */
	public static Click clickDataToPush(AdFeedback data)
	{
		if(data == null) return null;
		else
		{
			Click click = new Click();
			click.setFid(data.getSlot_name());
			click.setImei(data.getImei());
			click.setAdid(data.getAdid());
			click.setTimestamp(data.getTimestamp());
			return click;
		}
	}
	
	/**
	 * 下载数据
	 * @param data
	 * @return
	 */
	public static Download downloadDataToPush(SoftFeedback data)
	{
		if(data == null) return null;
		else
		{
			Download down = new Download();
			down.setFid(data.getSlot_name());
			down.setImei(data.getImei());
			down.setAdid(data.getAdid());
			down.setAppid(data.getAppid());
			down.setTimestamp(data.getTimestamp());
			return down;
		}
	}
	
	/**
	 * 安装数据
	 * @param data
	 * @return
	 */
	public static Install installDataToPush(SoftFeedback data)
	{
		if(data == null) return null;
		else
		{
			Install install = new Install();
			install.setFid(data.getSlot_name());
			install.setImei(data.getImei());
			install.setAdid(data.getAdid());
			install.setAppid(data.getAppid());
			install.setTimestamp(data.getTimestamp());
			return install;
		}
	}

}
