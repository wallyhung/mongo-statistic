package com.jukuad.statistic.pojo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity(value="request_temp", noClassnameStored = true)  
@Indexes(@Index(name="idx_distinct_imei_temp", value="fid,imei,-time"))
public class RequestTemp extends Request
{
}
