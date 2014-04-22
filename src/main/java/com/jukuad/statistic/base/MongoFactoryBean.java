package com.jukuad.statistic.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoFactoryBean extends AbstractFactoryBean<Mongo> {
	 
    // 表示服务器列表(主从复制或者分片)的字符串数组
    private String[] serverStrings;
    // mongoDB配置对象
    private MongoClientOptions mongoOptions;
    // 是否主从分离(读取从库)，默认读写都在主库
    private boolean readSecondary = false;
    // 设定写策略(出错时是否抛异常)，默认采用SAFE模式(需要抛异常)
    private WriteConcern writeConcern = WriteConcern.SAFE;
    
    private String dbName;  //数据库名
    private String username;    //用户名，可为空
    private String password;    //密码，可为空
 
    @Override
    public Class<?> getObjectType() {
        return Mongo.class;
    }
 
    @Override
    protected Mongo createInstance() throws Exception {
        Mongo mongo = initMongo();
         
        // 设定主从分离
        if (readSecondary) {
            mongo.setReadPreference(ReadPreference.secondaryPreferred());
        }
        // 设定写策略
        mongo.setWriteConcern(writeConcern);
        return mongo;
    }
     
    /**
     * 初始化mongo实例
     * @return
     * @throws Exception
     */
    private Mongo initMongo() throws Exception {
        // 根据条件创建Mongo实例
        Mongo mongo = null;
        
        List<ServerAddress> serverList = getServerList();
        
        if(username != null && password != null)
        {
        	MongoCredential credential = MongoCredential.createMongoCRCredential(username, dbName, password.toCharArray());
        	System.out.println(credential + "--------------");
        }
        
        if (serverList.size() == 0) {
            mongo = new MongoClient();
        }else if(serverList.size() == 1){
            if (mongoOptions != null) {
                mongo = new MongoClient(serverList.get(0), mongoOptions);
            }else{
                mongo = new MongoClient(serverList.get(0));
            }
        }else{
            if (mongoOptions != null) {
                mongo = new MongoClient(serverList, mongoOptions);
            }else{
                mongo = new MongoClient(serverList);
            }
        }
        return mongo;
    }
     
     
    /**
     * 根据服务器字符串列表，解析出服务器对象列表
     * <p>
     * 
     * @Title: getServerList
     *         </p>
     * 
     * @return
     * @throws Exception
     */
    private List<ServerAddress> getServerList() throws Exception {
        List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        try {
            for (String serverString : serverStrings) {
                String[] temp = serverString.split(":");
                String host = temp[0];
                if (temp.length > 2) {
                    throw new IllegalArgumentException("Invalid server address string: " + serverString);
                }
                if (temp.length == 2) {
                    serverList.add(new ServerAddress(host, Integer.parseInt(temp[1])));
                } else {
                    serverList.add(new ServerAddress(host));
                }
            }
            return serverList;
        } catch (Exception e) {
            throw new Exception("Error while converting serverString to ServerAddressList",e);
        }
    }

    ///~ setter
	public void setServerStrings(String[] serverStrings) {
		this.serverStrings = serverStrings;
	}

	public void setMongoOptions(MongoClientOptions mongoOptions) {
		this.mongoOptions = mongoOptions;
	}

	public void setReadSecondary(boolean readSecondary) {
		this.readSecondary = readSecondary;
	}

	public void setWriteConcern(WriteConcern writeConcern) {
		this.writeConcern = writeConcern;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
    
    
}