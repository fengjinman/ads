package com.powerwin.cache;


import com.powerwin.dao.DevuserDao;
import com.powerwin.entity.*;
import com.powerwin.util.DaoUtil;
import com.powerwin.boot.config.RedisConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;


import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存媒体数据到redis
 */
public class MediaCache {
	
	public static Logger LOG = LogManager.getLogger(MediaCache.class);
	
	private static MediaCache INSTANCE = new MediaCache();
	
	public static MediaCache getInstance() {
		return INSTANCE;
	}
	
	private Map<Integer, MediaChannel> channelMap = new ConcurrentHashMap<Integer, MediaChannel>();
	private Map<Integer, MediaApp> appMap = new ConcurrentHashMap<Integer, MediaApp>();
	
	private long update = 0;
	public MediaCache() {
		update();
	}
	
	public Iterator<MediaApp> iterator() {
		return appMap.values().iterator();
	}

	public void updateApps(Jedis jedis, long lastUpdate) {

		DevuserDao devuserMapper = (DevuserDao) DaoUtil.getDao(DevuserDao.class);

		List<Map<String,Object>> items = devuserMapper.queryData(update,update);

		if (!items.isEmpty()) {
			for (Map<String,Object> vals : items) {
				MediaApp media = new MediaApp(vals);

				//在这里为appMap赋值
				appMap.put(media.getId(), media);

				// 1: 开发者是否锁定
				// 2: 开发者是否认证通过
				// 3: 媒体是否启动 或者测试
				if( media.getStatus() ==  1 &&  media.getState() == 1 && (  media.getMedstatus() == 1 || media.getMedstatus() ==2 ) ) {
					//满足条件缓存
					if(jedis != null) {
						jedis.hset("DATA_MEDIA", String.valueOf(media.getAppkey()), media.toString());
					}
				} else {
					//不满足条件删除
					if(jedis != null) {
						jedis.hdel("DATA_MEDIA",  String.valueOf(media.getAppkey()));
					}
				}
			}
		}
	}
	
//	public void updateChannels(SQLConnection conn, Jedis jedis, long lastUpdate) {

//	}

	public void update() {

		Jedis jedis = RedisConnection.getInstance("main");

		String updateString = jedis.get("DATA_MEDIA_UPDATE");

		long redisUpdate = (updateString == null || updateString=="") ? 0 : Long.parseLong(updateString);

		updateApps(jedis,redisUpdate);

		update = System.currentTimeMillis()/1000-60;

		jedis.set("DATA_MEDIA_UPDATE", String.valueOf(update));

		if(jedis != null){

			RedisConnection.close("main",jedis);

		}

	}
	
	public void save(OutputStream stream) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(stream);	
		out.writeLong(update);
		out.writeInt(appMap.size());
		for(Iterator<Map.Entry<Integer,MediaApp>> it = appMap.entrySet().iterator();it.hasNext();) {
			Map.Entry<Integer,MediaApp> entry = it.next();
			out.writeInt(entry.getKey());
			out.writeObject(entry.getValue());
		}
		out.close();
	}
	
	public void load(InputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(stream);

		long update = in.readLong();
		int size = in.readInt();

		for(int i=0;i<size;i++) {
			int key = in.readInt();
			MediaApp value = (MediaApp)in.readObject();
			appMap.put(key, value);
		}
		in.close();
		this.update = update;
		this.update();
	}
	
	public MediaChannel getChannel(int mid) {
		return channelMap.get(mid);
	}
	
	public MediaApp getApp(int mid) {
		return appMap.get(mid);
	}

	public Media get(int mid) {
		Media media = appMap.get(mid);
		if(media != null) {
			return media;
		}
		
		return channelMap.get(mid);
	}
	
	public static void main(String[] args) {
		MediaCache.getInstance().update();
	}
}
