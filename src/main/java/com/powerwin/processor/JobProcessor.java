package com.powerwin.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.powerwin.boot.config.Define;
import com.powerwin.boot.config.RedisConnection;
import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.entity.*;
import com.powerwin.parser.CallbackParser;
import com.powerwin.store.DBHistoryStore;
import com.powerwin.store.FileStore;
import com.powerwin.store.RedisStore;
import com.powerwin.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;


/**
 * 任务处理器
 */
public class JobProcessor extends CallbackProcessor {
	
	public static Logger LOG = LogManager.getLogger(JobProcessor.class);

	public List<Object>[] process(List<Object> vals) {
		
		int action=0, type=0, adid=0,active_num=1;
		String mac,udid,openudid;
		action = ListUtil.getInt(vals, CallbackParser.Index.ACTION);
		type = ListUtil.getInt(vals,CallbackParser.Index.TYPE);
		adid = ListUtil.getInt(vals,CallbackParser.Index.ADID);
		mac = ListUtil.getString(vals,CallbackParser.Index.MAC);
		udid = ListUtil.getString(vals,CallbackParser.Index.UDID);
		openudid = ListUtil.getString(vals,CallbackParser.Index.OPENUDID);
		try{
			active_num = ListUtil.getInt(vals,CallbackParser.Index.ACTIVE_NUM); //0开始active，1以后都是job
		}catch(Exception e) {
			active_num = 1;
		}

		if (action == 0 || type == 0 || adid == 0) {
			LOG.trace(String.format("data item has error adid=%d action=%d type=%d", adid, action, type));
			return null;
		}

		if(mac == null && udid == null && openudid == null) {
			LOG.trace("data item has error");
			return null;
		}

		int year = ListUtil.getInt(vals, Index.YEAR);
		int mon = ListUtil.getInt(vals, Index.MON);
		int day = ListUtil.getInt(vals, Index.DAY);

		CallbackItem item = this.getHistory(year, mon, day, type, adid, mac, udid);
		//不存在点击,已经激活
		if(item == null) {
			LOG.trace(String.format("Pre action not found for : action=%d adid=%d mac=%s udid=%s openudid=%s active_num=%d", action, adid, mac, udid, openudid,active_num));
			return null;
		//} else if(item.action != Define.ACTION_ACTIVE) {
			//	LOG.trace(String.format("Action founded for : action=%d adid=%d mac=%s udid=%s openudid=%s", action, adid, mac, udid, openudid));
			//	return null;
		}

		Media media = MediaCache.getInstance().get(item.appid);
		if (media == null) {
			LOG.debug("media "+item.appid+" not found.");
			return null;
		}

		Ads ad = AdsCache.getInstance().get(adid);
		if (ad == null) {
			LOG.debug("ad "+adid+" not found.");
			return null;
		}

		int remain = RemainActiveUtil.getRemain(adid);
        LOG.info("job : adid={"+adid+"} udid={"+udid+"} | remain num : {"+remain+"}");

		int isPay = 0;
		//ad.getOptions()
		Map<String, Integer> ADS_OPTIONS = Cheat.parseAdOptions("");
		isPay = ADS_OPTIONS.get(Cheat.KEY_ISPAY);

		float income = 0;
		float cost = 0;
		int len = 2;

		//多次激活对应价格读取，判断
		try {
//			String json = ad.getJobs();
			String json = "";
			JSONObject jsonObj = JSONObject.parseObject(json);
			JSONArray jobs = jsonObj.getJSONArray("jobs");
			len = jobs.size();
			if(active_num < 0 || active_num >= len){
				LOG.debug("job active_num error,num："+ active_num+" jobs:" + len);
				return null;
			}
			JSONObject job = (JSONObject) jobs.get(active_num);
//			String billing = ad.getBilling();
			String billing = "";
			if(billing.indexOf('3') >= 0) {
				//表示job激活
				if(active_num == 1){
					//广告主消费只记录第一次激活的钱
					//income = Float.parseFloat(jsonObj.get("income").toString());
				}else{
					action = Define.ACTION_JOB_NEXT;
				}
				cost = Float.parseFloat(job.get("const").toString());
				LOG.warn(String.format("jobs sucess adid=%d,cost=%.2f", adid,cost));
			}
		}catch(Exception e){
//			income = ad.getPriceCallbackIncome();
//			cost = ad.getPriceCallbackCost();
			income = 0;
			cost = 0;
			LOG.warn(String.format("jobs exception adid=%d,exception=%s", adid,e.getMessage()));
		}

		//任务都不扣钱
		DataSave.DataSaveRole role = DataSave.getRole(media,ad);
		//cost = item.saved == 0 ? 0 : DataSave.getRate(role, cost);
		cost = DataSave.getRate(role, cost);

		//下发积分计算
		float score = 0;
		if (cost > 0 && media.getType() == 1) {
			MediaApp app = (MediaApp) media;
//			float ratio = app.getRatio();
			float ratio = 0;
			if (ratio > 0) {
				score = cost * ratio;
			}
		}

		item.score = score;
		item.openudid = openudid;

		int unique = 1;
		int count = 1;
		int invalid = item.invalid = 0;
		if(action == Define.ACTION_JOB_NEXT){	//特殊表示，用于区别多次激活
			invalid = active_num - 1;
		}
		int saved = item.saved = 0;

		int hour = ListUtil.getInt(vals, Index.HOUR);
		int data_from = item.data_from;
		int ad_from = item.ad_from;
//		int uid = media.getUid();
//		int appid = media.getMid();
//		int cid = ad.getCid();
		int uid = 0;
		int appid = 0;
		int cid = 0;

		LOG.info(String.format("user job score mac:%s,udid:%s,openudid:%s,appid:%d,adid:%d,score:%f,active_num:%d", mac,udid,openudid,item.appid,adid,item.score,active_num));
		if (item.saved == 1 || item.invalid > 0) {
			LOG.info("media job save : " + item.uid + " " + item.appid + " " + item.appuserid + " " + item.mac + " " + item.udid + " " +item.openudid + " " + adid + " " +item.score + " " + active_num);
		} else {
			if (media.getType() == 1) {
				MediaApp app = (MediaApp) media;
				//ad.getProcess_name()
				UserScore.sendScore(app, ad, item.appuserid, item.mac, item.udid, item.openudid, item.score, "", active_num, isPay);
				LOG.info("job : adid={"+adid+"} udid={"+udid+"} | user send score " + item.uid + " " + item.appid + " " + item.appuserid + " " + item.mac + " " + item.udid + " " +item.openudid + " " +item.score);
			} else {
				if (item.appuserid != null && item.appuserid != null) {
					String url;
					try {
						url = URLDecoder.decode(item.appuserid, "UTF-8");

						if (url.length() > 4) {
							String proto = url.substring(0, 4).toLowerCase();
							if (proto.equals("http")) {
								url = url.replaceAll("&amp;", "&");

								JSONObject obj = new JSONObject();
								obj.put("protocol", "http");
								obj.put("method", "get");
								obj.put("url", url);
								LOG.debug("channel callback : " + url);

								Jedis jedis = RedisConnection
										.getInstance("values");
								jedis.rpush("ACTION_HTTP_REQUEST",
										obj.toString());
								RedisConnection.close("values", jedis);
							} else {
								LOG.error("error proto channel "+item.appid+" callback : " + url);
							}
						} else {
							LOG.error("error proto channel "+item.appid+" callback : " + url);
						}
					} catch (UnsupportedEncodingException e) {
						LOG.error("error on decode channel "+item.appid+" callback : "
								+ item.appuserid);
					}
				} else {
					LOG.debug("channel has no callback" + item.appid
							+ item.appuserid);
				}
			}
		}

		//todo
		LOG.trace(String.format("job process countvalues：active_num:%d,income:%f,cost:%f",active_num,income,cost));
		DetailHourKeys ck = DetailHourKeys.create(year,mon,day,hour,type,data_from,ad_from,appid,uid,adid,cid,0,0);
		CountValues cv = CountValues.create(action, count, invalid, unique, saved, income, cost);	//invalid > 0 ? 1 : 0
		Counter.getInstance().add(ck, cv);


		//同步激活时间，用于控制深度任务的显示
		Jedis jedisProcess = null;
		try {
			jedisProcess = RedisConnection.getInstance("process");

			String keyProcess = "DATA_DEVICE_ADID";
			String field = String.format("%s%s%d", mac, udid , adid);
			String info = jedisProcess.hget(keyProcess, field);

			int isAllFinish = (len == active_num + 1) ? 1 : 0;
			if(isAllFinish == 1){
				//记录所有完成任务的设备
				RedisStore.getInstance().addJob(item.mac + item.udid, adid);
			}
			int atime = (int) (System.currentTimeMillis()/1000);
			if(info==null) {
				String v = String.format("%d,%d,%d,%d,%d", isAllFinish,atime,active_num+1,atime,atime);
				//String v = String.format("%d,%d,%d,%d", isAllFinish,atime,active_num+1,atime);
				jedisProcess.hset(keyProcess, field, v);
			}else {
				String[] infoArr = info.split(",");
				if(infoArr[0].equals("0")) {
					int ctime = infoArr[1]==null?atime:Integer.parseInt(infoArr[1]);
					//int fctime = infoArr[4]==null?ctime:Integer.parseInt(infoArr[4]);
					int fctime = 0;
					try {
						fctime = infoArr[4]==null?0:Integer.parseInt(infoArr[4]);
					} catch (Exception e) {
						fctime = 0;
					}
					String v = String.format("%d,%d,%d,%d,%d", isAllFinish,ctime,active_num+1,atime,fctime);
					//String v = String.format("%d,%s,%d,%d", isAllFinish,infoArr[1],active_num+1,atime);
					jedisProcess.hset(keyProcess, field, v);
				}
			}
		} catch (Exception e) {
			LOG.warn(String.format("update redis process action=%d mac=%s udid=%s adid=%d appid=%d active_num=%s exception : %s ",action, mac, udid, adid, appid, active_num, e.getMessage()));
			//return null;
		}finally{
			if(jedisProcess != null) {
				RedisConnection.close("process",jedisProcess);
			}
		}
		int date = this.date(vals);
		//明细数据入库
		item.create_time = this.datetime(vals).timestamp();
		item.action = 5;
		//item.invalid = invalid;
		if(date == item.date) {
			int result = DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK).update(item);
			if(result <= 0) {
				LOG.error(String.format("update job : action=%d adid=%d mac=%s udid=%s openudid=%s",action, adid, mac, udid, openudid));
				return null;
			}
		} else {
			long result = DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK).put(adid, mac, udid, item);
			if(result <= 0) {
				LOG.error(String.format("update job : action=%d adid=%d mac=%s udid=%s openudid=%s",action, adid, mac, udid, openudid));
				return null;
			}
		}
		return null;
	}


}