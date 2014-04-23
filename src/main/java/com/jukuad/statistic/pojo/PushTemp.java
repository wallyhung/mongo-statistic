package com.jukuad.statistic.pojo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;


@Entity(value="push_temp", noClassnameStored = true)
@Indexes(@Index(name="idx_ad_distinct_imei_temp", value="adid,imei,-time"))
public class PushTemp extends Push
{

}