package com.powerwin.boot.timetask;


import com.alibaba.fastjson.JSONObject;
import com.powerwin.boot.config.Define;
import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.boot.config.Configuration;
import com.powerwin.boot.config.RedisConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务1
 *
 * 刷新缓存
 *
 * 一分钟延迟，一分钟执行一次
 */
public class ObjectCache extends TimerTask {

	public static Logger LOG = LogManager.getLogger(ObjectCache.class);
	public static String SAVE_PATH = Configuration.getInstance().getProperty("path.dump.dir", "/tmp");
	
	public static void update() {
		
		MediaCache.getInstance().update();
		AdsCache.getInstance().update();
		//cleanRestAds();
	}
	
	public void run() {
		update();
		LOG.debug("update cache from database");
	}
	
	private static Timer timer = null;
	private static ObjectCache updateTask = new ObjectCache();

	public static void start() {
		if (timer != null) {
			return;
		}
		load();
		timer = new Timer();
		try {
			//一分钟延迟，一分钟执行一次
			timer.schedule(updateTask, 60000, 60000);
		} catch (IllegalStateException e) {
			LOG.info("任务取消");
		}
	}
	
	public static void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		save();
	}
	
	public static void load(){
		try {
		    //AdsCache.getInstance().update();// todo 测试
			String mPath = SAVE_PATH+"/media.cache";
			File mFile = new File(mPath);
			if(mFile.exists()){
				FileInputStream mIn = new FileInputStream(mPath);
				MediaCache.getInstance().load(mIn);
				mIn.close();
				mFile.delete();
				LOG.debug("media load sucess");
			}else {
				try {

					MediaCache.getInstance().update();
				} catch (Exception e) {
					LOG.error(e);
				}
			}
			String aPath = SAVE_PATH+"/ads.cache";
			File aFile = new File(aPath);
			if(aFile.exists()){
				FileInputStream aIn = new FileInputStream(aPath);
				AdsCache.getInstance().load(aIn);
				aIn.close();
				aFile.delete();
				LOG.debug("ads load sucess");
			}else {
				AdsCache.getInstance().update();
			}
			
		} catch (Exception e) {
			LOG.error("start load fail" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void save(){
		try {
			FileOutputStream aOut = new FileOutputStream(SAVE_PATH+"/ads.cache");
			AdsCache.getInstance().save(aOut);
			aOut.close();
			LOG.debug("ads save sucess");
			
			FileOutputStream mOut = new FileOutputStream(SAVE_PATH+"/media.cache");
			MediaCache.getInstance().save(mOut);
			mOut.close();
			LOG.debug("media save sucess");
		} catch (IOException e) {
			LOG.error("stop save fail" + e.getMessage());
		}
	}
	
	private static void cleanRestAds() {
		Jedis jedis = null;
		try {
			jedis = RedisConnection.getInstance("main");
			for (int i = 1; i < Define.TYPES.length; i++) {
				String key = "DATA_ADS_TYPE_" + i;
				Map<String, String> ads = jedis.hgetAll(key);
				Collection<String> values = ads.values();
				for (String adsStr : values) {
					JSONObject adsJson = JSONObject.parseObject(adsStr);
					long update_time = 0;
					try {
						update_time = adsJson.getLong("update_time");
					} catch (Exception e) {
						LOG.debug("get update_time error:" + adsJson);
						continue;
					}
//					if ((adsJson.getInt("state") == 5 || adsJson.getInt("state") == 8 || adsJson.getInt("state") == 9)
//							&& (System.currentTimeMillis() / 1000 - update_time) >= 3600 * 24 * 7) {
//						jedis.hdel(key, String.valueOf(adsJson.getInt("adid")));
//						LOG.debug("remove remain ads from timer " + adsJson.get("adid"));
//					}
				}
			}
		}catch(Exception e){
			LOG.error("remove remain ads exception " + e.getMessage());
		}finally {
			if(jedis!= null) RedisConnection.close("main", jedis);
		}
	}
	
	public static void main(String[] args) {
		update();
	}
}