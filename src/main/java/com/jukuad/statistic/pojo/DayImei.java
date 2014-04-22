package com.jukuad.statistic.pojo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
@Entity(value="day_imei", noClassnameStored = true)
@Indexes({@Index(name="idx_day_imei", value="fid,value,-day"),
	      @Index(name="idx_day_ad_imei", value="adid,value,-day")})
public class DayImei extends Imei {

	private String day;
	
	public DayImei() {
	}
	
	public DayImei(String imei) {
		super(imei);
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	
}
