package com.powerwin.cache;
import com.powerwin.boot.config.RedisConnection;
import com.powerwin.entity.MediaPrice;
import com.powerwin.util.DaoUtil;
import redis.clients.jedis.Jedis;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class MeidaPriceCache {
	
	private static MeidaPriceCache INSTANCE = new MeidaPriceCache();
	
	public static MeidaPriceCache getInstance() {
		return INSTANCE;
	}
	
	private long update = 0;
	public Map<String, MediaPrice> map = new ConcurrentHashMap<String, MediaPrice>();
	public MeidaPriceCache() {
		update();
	}
	
	public void update() {
		
		Jedis jedis = null;
		try {
			//todo 数据库没有media_price这张表   并且这段代码一定会被执行  在redis有数据，点击、激活线程能够启动的时候
			jedis = RedisConnection.getInstance("main");
//			String sql = "SELECT `adid`,`appid`,`effect_time`,`price` FROM `media_price` WHERE isable=1 AND update_time>?";
//			Object dao = DaoUtil.getDao(x.class);
//			List<List<Object>> items = dao.xxx(update);
			List<List<Object>> items = null;
			if (!items.isEmpty()) {
				for (List<Object> vals : items) {
					MediaPrice price = new MediaPrice(vals);
					String key = String.format("%d-%d", price.appid, price.adid);
					map.put(key, price);
					if(jedis != null) {
						jedis.hset("DATA_MEDIA_PRICE", key, price.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(jedis != null) {
				RedisConnection.close("main",jedis);
			}
		}
		update = System.currentTimeMillis() / 1000-60;
		if(jedis != null){
			jedis.set("DATA_MEDIAPRICE_UPDATE", String.valueOf(update));
		}
	}
	
	public MediaPrice get(int appid, int adid) {
		String key = String.format("%d-%d", appid, adid);
		return map.get(key);
	}
}