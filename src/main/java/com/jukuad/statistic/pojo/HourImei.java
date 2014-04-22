package com.jukuad.statistic.pojo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value="hour_imei", noClassnameStored = true)
@Indexes({@Index(name="idx_hour_imei", value="fid,value,-hour"),
	     @Index(name="idx_hour_ad_imei", value="adid,value,-hour")})
public class HourImei extends Imei {
	
	private String hour;
	
	public HourImei() {
	}
	
	public HourImei(String imei) 
	{
		super(imei);
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

}
