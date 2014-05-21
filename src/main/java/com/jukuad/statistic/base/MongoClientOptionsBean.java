package com.jukuad.statistic.base;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.MongoClientOptions;

public class MongoClientOptionsBean extends AbstractFactoryBean<MongoClientOptions> 
{
	private boolean autoConnectRetry = false;
	private int maxAutoConnectRetryTime = 0;
	private int connectionsPerHost = 100;
	private int connectTimeout = 10000;
	private boolean cursorFinalizerEnabled = true;
	private int maxWaitTime = 120000;
	private int threadsAllowedToBlockForConnectionMultiplier = 5;
	private int socketTimeout = 0;
	private boolean socketKeepAlive = false;
	private boolean safe = true;
	private int w = 0;
	private int wtimeout = 0;
	private boolean fsync = false;
	private boolean j = false;
	

	@Override
	public Class<?> getObjectType() {
		return MongoClientOptions.class;
	}

	@Override
	protected MongoClientOptions createInstance() throws Exception 
	{
		
		MongoClientOptions mongoOptions = MongoClientOptions.builder().autoConnectRetry(autoConnectRetry)
																	  .connectionsPerHost(connectionsPerHost)
																	  .connectTimeout(connectTimeout)
																	  .socketKeepAlive(socketKeepAlive)
																	  .socketTimeout(socketTimeout)
																	  .maxWaitTime(maxWaitTime)
																	  .maxAutoConnectRetryTime(maxAutoConnectRetryTime)
																	  .build();
		return mongoOptions;
	}

	public void setAutoConnectRetry(boolean autoConnectRetry) {
		this.autoConnectRetry = autoConnectRetry;
	}

	public void setMaxAutoConnectRetryTime(int maxAutoConnectRetryTime) {
		this.maxAutoConnectRetryTime = maxAutoConnectRetryTime;
	}

	public void setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setCursorFinalizerEnabled(boolean cursorFinalizerEnabled) {
		this.cursorFinalizerEnabled = cursorFinalizerEnabled;
	}

	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public void setThreadsAllowedToBlockForConnectionMultiplier(
			int threadsAllowedToBlockForConnectionMultiplier) {
		this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setSocketKeepAlive(boolean socketKeepAlive) {
		this.socketKeepAlive = socketKeepAlive;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	public void setW(int w) {
		this.w = w;
	}

	public void setWtimeout(int wtimeout) {
		this.wtimeout = wtimeout;
	}

	public void setFsync(boolean fsync) {
		this.fsync = fsync;
	}

	public void setJ(boolean j) {
		this.j = j;
	}
	

}
