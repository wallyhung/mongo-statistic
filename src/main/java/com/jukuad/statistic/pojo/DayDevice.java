package com.jukuad.statistic.pojo;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity(value="day_device", noClassnameStored = true)
public class DayDevice 
{
	@Id
	@JsonIgnore(value= true)
	private ObjectId id;
	private String day;
	private String value;
	@Embedded
	private Imei imei;
	
//	private DBRef imei;
	
	public DayDevice() {
	}
	
	public DayDevice(String value) {
		this.value = value;
	}
	
	///~ getter and setter
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public Imei getImei() {
		return imei;
	}

	public void setImei(Imei imei) {
		this.imei = imei;
	}
	
	
	

}
