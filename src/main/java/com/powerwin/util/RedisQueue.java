package com.powerwin.util;

import com.powerwin.boot.config.RedisConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

/**
 * 从此类的pop方法中，可以获得redis中的数据组成的字符串
 */
public class RedisQueue {
	
	public static Logger LOG = LogManager.getLogger(RedisQueue.class);
	
	public final static RedisQueue INSTANCE = new RedisQueue();
	
	public final static RedisQueue getInstance() {
		return INSTANCE;
	}

	public String pop(String name) {
		Jedis jedis = RedisConnection.getInstance("queue");
		if(jedis == null) {
			LOG.warn("get connection faild");
			return null;
		}
		
		try {
			String line = jedis.lpop(name);
			return line;
		} catch (Exception e) {
			LOG.warn("queue pop faild");
			return null;
		} finally {
			RedisConnection.close("queue",jedis);
		}
	}
}
