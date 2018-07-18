package com.powerwin.cache;


import com.alibaba.fastjson.JSONObject;
import com.powerwin.dao.DevuserDao;
import com.powerwin.entity.Ads;
import com.powerwin.util.DaoUtil;
import com.powerwin.boot.config.RedisConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 刷新redis中的缓存
 */
public class AdsCache {
	
	public static Logger LOG = LogManager.getLogger(AdsCache.class);
	public final static int CLEAR_TIME = 3600 * 24 * 7;
	private static AdsCache INSTANCE = new AdsCache();
	private static int current = 0;
	public static class AdsSorted {
		//启动
		public String debug = "";
		//释放
		public String release = "";
		//暂停
		public String pause = "";
		//手动停止
		public String stop = "";
	}
	
	public static AdsCache getInstance() {
		return INSTANCE;
	}
	
	private Map<Integer, Ads> map = new ConcurrentHashMap<Integer, Ads>();
	
	private static long update = 0;
	
	public AdsCache() {
		update();
	}
	
	public void save(OutputStream stream) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeLong(update);
		out.writeInt(map.size());
		for(Iterator<Map.Entry<Integer,Ads>> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer,Ads> entry = it.next();
			out.writeInt(entry.getKey());
			out.writeObject(entry.getValue());
		}
		out.close();
	}

	public void load(InputStream stream) throws IOException, ClassNotFoundException {
		this.update();
	}
	
	public void update() {

		Jedis jedis = null;

		try {
			DevuserDao dao = (DevuserDao)DaoUtil.getDao(DevuserDao.class);

			jedis = RedisConnection.getInstance("main");

			LOG.debug("select ads sql time : " + update + " : " + current);

			List<Map<String,Object>> items = dao.queryMap(update,update);

			AdsSorted sort =new AdsSorted();

			current = (int) (System.currentTimeMillis() / 1000);

			if (!items.isEmpty()) {

				for (Map<String,Object> vals : items) {

					Ads ads = null;
					try {

						ads = new Ads(vals);
					} catch (Exception e) {
						LOG.debug("exception ads item new");
						continue;
					}
					String statusres = "";
					int unitstatus = 1;
					//重新核算剩余金额 @todo
					//MinuteAdsPlan.pauseAdByCount(ads.getAdid());
					//广告上线，1 : 启动  2: 暂停  3: 停止
					if( ads.getUnitstatus() == 3 ) {

					    sort.stop += ","+String.valueOf(ads.getId());

					    statusres="手动停止";

					    dao.get_update(statusres,ads.getId());

					} else if( ads.getUnitstatus() == 1 ) {
						// 时间是否开始
					    int bjhourval = ads.getBjhourval();
					    int curr_start_time= current + (bjhourval*3600);
					    if ( ads.getStart_time() > curr_start_time) {
					    	//时间未开始
					        sort.pause += ","+String.valueOf(ads.getId());
	                        statusres="时间未开始";
	                        unitstatus = 2;
	                        dao.get_update2(statusres,unitstatus,ads.getId());

					    } else if ( ads.getEnd_time() <= curr_start_time ) {
					    	// 时间是否到期
					        sort.pause += ","+String.valueOf(ads.getId());
                            statusres="时间已过期";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());

					    } else if (ads.getCpstatus() != 1) {
					    	// 广告主是否被锁定
					        sort.pause += ","+String.valueOf(ads.getId());
                            statusres="广告主已锁定";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());
					    } else if ( false ) {
					    	// todo  今日预算是否耗尽
					    } else if ( isBoolenTime(ads.getBjhourval(),ads.getSenddate()) == false ) {
					    	// todo  当前小时没有在指定时间内
					        sort.pause += ","+String.valueOf(ads.getId());
                            statusres="当前小时不在时间范围";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());
					    } else {
					        statusres="投放中";
					        sort.release += ","+String.valueOf(ads.getId());
					    }
					    // 增加广告缓存
					    ads.setUnitstatus(unitstatus);
					    jedis.hset("DATA_ADS", String.valueOf(ads.getId()), ads.toString());
					    LOG.debug("online ads info: " + ads.getId());
					    if( ads.getEnd_time() + 10*60*60*24  <= curr_start_time  ) {
					    	// 时间是否过期7天，超过自动删除
					        jedis.hdel("DATA_ADS", String.valueOf(ads.getId()));
                            LOG.debug("delete ads remain———— " + ads.getId());
					    }
					    // 今日预算是否耗尽 @todo  redis
					    // 广告主是否被锁定 @todo  redis
					    // update table adsunit statusres @todo
                        // update table adsunit status @todo
					    // 判断下线时间

					} else if( ads.getUnitstatus() == 2 ) {
					    int bjhourval = ads.getBjhourval();
                        int curr_start_time= current + (bjhourval*3600);
						//时间未开始
						if ( ads.getStart_time() > curr_start_time) {
                            sort.pause += ","+String.valueOf(ads.getId());
                            statusres="时间未开始";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());
                        } else if ( ads.getEnd_time() <= curr_start_time ) {
							// 时间是否到期
                            sort.pause += ","+String.valueOf(ads.getId());
                            statusres="时间已过期";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());
                        } else if (ads.getCpstatus() != 1) {
							// 广告主是否被锁定
                            sort.pause += ","+String.valueOf(ads.getId());
                            statusres="广告主已锁定";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());
                        } else if ( false ) {
							// todo  今日预算是否耗尽
                        } else if (  isBoolenTime(ads.getBjhourval(),ads.getSenddate()) == false ) {
							// todo  当前小时没有在指定时间内
                            sort.pause += ","+String.valueOf(ads.getId());
                            statusres="当前小时不在时间范围";
                            unitstatus = 2;
							dao.get_update2(statusres,unitstatus,ads.getId());
                        } else {
                            statusres="投放中";
                            unitstatus = 1;
							dao.get_update2(statusres,unitstatus,ads.getId());
                            sort.release += ","+String.valueOf(ads.getId());
                        }
                        // 增加广告缓存
                        ads.setUnitstatus(unitstatus);
                        jedis.hset("DATA_ADS", String.valueOf(ads.getId()), ads.toString());
                        LOG.debug("online ads info: " + ads.getId());
                        if( ads.getEnd_time() + 7*60*60*24  <= curr_start_time  ) {
                        	// 时间是否过期7天，超过自动删除
                            jedis.hdel("DATA_ADS", String.valueOf(ads.getId()));
                            LOG.debug("delelte ads remain———— " + ads.getId());
                        }
					    // 时间是否开始
                        // 时间是否到期
                        // 今日预算是否耗尽 @todo  redis
                        // 广告主是否被锁定 @todo  redis
                        // update table adsunit statusres @todo
                        //  update table adsunit status @todo
                        // 时间是否过期7天，超过自动删除
					} else {
					    jedis.hdel("DATA_ADS", String.valueOf(ads.getId()));
                        LOG.debug("delelte ads unit:" + ads.getId() + ", state: " + ads.getStatus());
                        statusres="手动停止[未知状态]";
                        unitstatus = 3;
						dao.get_update2(statusres,unitstatus,ads.getId());
                        sort.stop += ","+String.valueOf(ads.getId());
					}

					//储存在map中 相当于程序缓存，给线程类使用
					map.put(ads.getId(), ads);
				}
			}


			update = current - 120;
			jedis.set("DATA_ADS_UPDATE", String.valueOf(update));
			try {
                JSONObject obj = new JSONObject();
                obj.put("debug", sort.debug);
                obj.put("release", sort.release);
                obj.put("pause", sort.pause);
                jedis.hset("DATA_ADS", "sorts", obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
		} catch (Exception e) {
			LOG.error("ads cache error : " + e.getMessage());
		}finally{
			if(jedis != null){
				RedisConnection.close("main",jedis);
			}
		}
	}
	
	public  boolean isBoolenTime(int bjtime, String strlist){
	     long mts = System.currentTimeMillis() + (bjtime * 60*60*1000);
	     Calendar cal = new GregorianCalendar();
	     cal.setTimeInMillis(mts);
	     int hour = cal.get(Calendar.HOUR_OF_DAY);
	     String hour_s= String.valueOf(hour);
	     String[] arrlist = strlist.split(",");
	     boolean isret=  Arrays.asList(arrlist).contains(hour_s);
	     return isret;  
	}
	 
	public Ads get(int mid) {
	        return map.get(mid);
	   }
	
	public static void main(String[] args) {
	   
		//AdsCache.getInstance();
	}
}
