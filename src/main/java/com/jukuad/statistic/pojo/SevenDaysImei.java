package com.jukuad.statistic.pojo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value="seven_imei", noClassnameStored = true)
@Indexes(@Index(name="idx_seven_imei", value="fid,value,-start,-end"))
public class SevenDaysImei extends Imei {

	private String start;
	
	private String end;
	
	public SevenDaysImei() {
	}
	
	public SevenDaysImei(String imei) {
		super(imei);
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
	
	
}
