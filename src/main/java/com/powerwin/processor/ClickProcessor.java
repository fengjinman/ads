package com.powerwin.processor;

import com.powerwin.boot.config.Define;
import com.powerwin.boot.config.RedisConnection;
import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.entity.*;
import com.powerwin.parser.ActionParser;
import com.powerwin.store.CachedValue;
import com.powerwin.store.DBHistoryStore;
import com.powerwin.store.FileStore;
import com.powerwin.store.RedisStore;
import com.powerwin.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.List;

public class ClickProcessor extends CallbackProcessor {

	public static Logger LOG = LogManager.getLogger(ClickProcessor.class);

	public List<Object>[] process(List<Object> vals) {
		
		int action, type, appid, adid, root = 0;
		String session = "", did = "", ssid = "", localip = "", openudid = ""	, rload = "";
		long disk = 0;
		try {
			action = (Integer) vals.get(Index.ACTION);
			type = (Integer) vals.get(Index.TYPE);

			appid = (Integer) vals.get(Index.APPID);
			adid = (Integer) vals.get(Index.ADID);
		} catch (Exception e) {
			LOG.warn("data item has error");
			return null;
		}

		String mac = "";
		String udid = "";
		try{
			session = ListUtil.getString(vals, ActionParser.Index.SESSION);
			did = ListUtil.getString(vals, ActionParser.Index.DID);
			ssid = ListUtil.getString(vals, ActionParser.Index.SSID);
			localip = ListUtil.getString(vals, ActionParser.Index.LOCALIP);
			openudid = ListUtil.getString(vals, ActionParser.Index.OPENUDID);
			disk = ListUtil.getLong(vals, ActionParser.Index.DISK);
			rload = ListUtil.getString(vals, ActionParser.Index.RLOAD);
			root = ListUtil.getInt(vals, ActionParser.Index.ROOT);

			mac = ListUtil.getString(vals, Index.MAC);
			udid = ListUtil.getString(vals, Index.UDID);

			//time = ListUtil.getInt(vals, ActionParser.Index.TIME);
			//uptime = ListUtil.getInt(vals, ActionParser.Index.UPTIME);
		} catch (Exception e) { }

		//控量处理
		int remain = RemainActiveUtil.getRemain(adid);
//		LOG.info("click : adid={} udid={} | remain num : {}", adid, udid, remain);
		/*if(remain <= 0) {
			LOG.debug("click remain num : " + adid + " , " + remain + " , " + udid);
//			return null;
		}*/

		if (adid == 0 || appid == 0 || action == 0 || type == 0) {
			LOG.warn(String.format("data item has error adid=%d appid=%d action=%d type=%d", adid, appid, action, type));
			return null;
		}

		//从MediaCache的内部属性map集合中获得，在写入redis之前被保存到map中
		Media media = MediaCache.getInstance().get(appid);
		if (media == null) {
			LOG.warn("media " + appid + " not found.");
			return null;
		}

		boolean isStop = false;
		//从AdsCache的内部属性map集合中获得，在写入redis之前被保存到map中
		Ads ad = AdsCache.getInstance().get(adid);
		if (ad == null) {
			LOG.warn("ad " + adid + " not found.");
			return null;
		}

		type = ad.getType();

		//1 投放  2停止  3软删除 4测试（控制墙是否显示） 开发者行为
		//媒体分类 1 应用 2 渠道
		if(media.getState() != 1 && media.getState() != 4 && media.getType() != 2) {
			isStop = true;
		}

		//状态 1新广告,2审核通过,，3拒绝，4启动 5 停止 6软删除，7调试，8暂停, 9 留存
		// todo 原本有一个代表手动停止的字段  ad.getIs_hand_stop() == 1
		if(ad.getStatus() == 5 ){
			//手动停止的广告，不用做留存了
			isStop = true;
		} else if(ad.getStatus() == 5 || ad.getStatus() == 8   ||  ad.getStatus() == 9){
			//留存广告，超过1周的    时间为int型，精确到秒
			int current = (int) (System.currentTimeMillis()/1000);
			// todo  原本此处是修改时间，现在是结束时间
			if(current - ad.getEnd_time() > 3600 * 24 * 7){
				isStop = true;
			}
		} else if(ad.getStatus() != 4 && ad.getStatus() != 7 && media.getType() != 2) {
			isStop = true;
		}

		// 1 应用 3 渠道
		int data_from = media.getType() == 1 ? 1 : 3;


		int year = ListUtil.getInt(vals, Index.YEAR);
		int mon = ListUtil.getInt(vals, Index.MON);
		int day = ListUtil.getInt(vals, Index.DAY);
		int hour = ListUtil.getInt(vals, Index.HOUR);

		//IOS系统版本
		String osver = String.valueOf(vals.get(Index.OSVER));

//		int uid = media.getUid();
//		int cid = ad.getCid();
//		int ad_from = ad.getfrom();
		// 媒体表中 类型 1: ios 2: android 3: WAP站
		int ad_from = media.getType();
		int uid = 0;
		int cid = 0;
		int date = year * 10000 + mon * 100 + day;
		int game_id = 0;
		int adplanid = ad.getPlanid();

		DetailHourKeys ck = DetailHourKeys.create(year, mon, day, hour, type, data_from, ad_from, appid, uid, adid, cid,game_id,adplanid);

		/*
		int vunique = checkUnique(date, type, action, adid, appid, mac, udid);
		if(vunique == 1) {
			CountValues cv = CountValues.create(action, 1, 0, 1, 0, 0, 0);
			Counter.getInstance().add(ck, cv);
		}
		*/
		//特殊控量
		RemainActiveUtil.count(udid, mac, ad);

		try {
			//频次控制  临时注释
//			UserControl.controlClick(ad, udid);
		} catch (Exception e) {
			LOG.error("UserControl click:"+e.getMessage());
		}

		CallbackItem item = this.getHistory(year, mon, day, type, adid, mac, udid);
		if (item != null && item.isToday) {	//isToday 表示当天

			CountValues cv = CountValues.create(action, 1, 0, 0, 0, 0, 0);

			// todo 这里有写入数据库的操作
			Counter.getInstance().add(ck, cv);

			LOG.warn(String.format("Click action exists for : item.date=%d item.id=%d item.adid=%d item.mac=%s item.udid=%s action=%d adid=%d mac=%s udid=%s",
							item.date, item.id, item.adid, item.mac, item.udid, action, adid, mac, udid));

			return null;
		}

		// 点击数据入库
		item = CallbackItem.parseFromLog(vals, media, ad);
//		item.cid = ad.getCid();
//		item.uid = media.getUid();
		item.cid = 0;
		item.uid = 0;

		int invalid = isStop ? 4 : 0;

		if(invalid == 0 && ( media.getType() == 1) ) {
			int test = 0;
			// todo 需要重新考虑 城市和ip
//			if(media.getCitys() != null && !media.getCitys().isEmpty()) {
//				IP2Location.IPInfo info = IP2Location.find(item.ip);
//				if(info != null && media.inCitys(info.location)) {
//					test = 1;
//				}
//			}

			// todo 现无hours
//			if(media.getHours() != null && !media.getHours().isEmpty()) {
//				String strHour = String.valueOf(hour);
//				if(media.inHours(strHour)) {
//					test += 1;
//				}
//			}
			if(test == 2) {

				invalid = 8;
			}
		}

		//是否启用防作弊	0代表启用  media.getIsEnable() == 0
		if(true){
			/**
			 * 1、 验证规则（	第5位替换成5、第8位替换成8、第15位替换成7、第25位替换成4）
			 * 2、验证是否重复 （广告+session）唯一性，按天提重
			 */
			if(did != null && did.length() > 0) {
				invalid = RedisStore.getInstance().addDidCheat(ad, session, did, udid);
			}

			/**
			 * 防作弊2：SSID
			 * 收集udid 和 ssid 的映射关系
			 */
			if(ssid != null && ssid.length() > 0) {
				RedisStore.getInstance().addSsidCheat(udid, ssid, localip);
			}

			/**
			 * 防作弊3：OPENUDID
			 * 收集udid 和 openudid 的映射关系
			 */
			if(openudid != null && openudid.length() > 0){
				RedisStore.getInstance().addOpenudidCheat(udid, openudid);
			}

			//磁盘点击记录
			if(disk > 0){
				RedisStore.getInstance().addClickDisk(mac, udid, adid, disk);
			}

			//redownload
			if(rload != null && rload.length() > 0) {
				boolean r = Boolean.parseBoolean(rload);
				if(r){
					 if(invalid == 0){
						 invalid = 16;
					 }
				}
			}

			//越狱扣量
			if(root == 1){
				 if(invalid == 0) {
					 invalid = 17;
				 }
			}
		}

		//媒体特殊处理 media.getMid() == 5656
		if(true) {
			if(osver.contains("7.")){
				 if(invalid == 0) {
					 invalid = 10;
				 }		//osver系统版本作弊屏蔽
			}
		}

		//评分规则
		//ScoreRule.count(mac, udid, time, uptime);

		//计费
		float income = 0;
		float cost = 0;

		//计费
//		String billing = ad.getBilling();
		String billing = "";
		if (billing.indexOf('1') >= 0) {
//			income = ad.getPriceClickIncome();
			income = 0;

			cost = this.getPrice(appid, adid);
			if(cost == 0) {
//				cost = ad.getPriceClickCost();
				cost = 0;
			}

			item.income = income;
			item.cost = cost;

			RedisStore.getInstance().addClick(item.mac + item.udid, adid);
		}

		item.invalid = invalid;
		boolean save = isStop;

		int actived = 0;

		//媒体所有广告前10不扣量，渠道针对单一广告前50不扣量
		CachedValue appcounter = null;
		String appKey = "";
		if(media.getType() == Define.MEDIA_TYPE_CHANNEL){
			appcounter = CachedValue.getInstance("CHANNEL_ACTIVED_NUM");
			appKey = String.format("%d%d", appid,adid);
		}else {
			appcounter = CachedValue.getInstance("APP_ACTIVED_NUM");
			appKey = String.valueOf(appid);
		}
		String value = appcounter.get(appKey);
		if(value != null) {
			actived = Integer.parseInt(value);
		}

		String s = "";
		DataSave.DataSaveRole role = DataSave.getRole(media, ad);
		if(invalid > 0 || isStop) {
			save = true;
			item.saved = 1;

			if(invalid > 0) {
				s = "invalid";
			}else {
				s = "isStop";
			}
		} else {
			if(type == 1 || type == 2){ //cpa
				int min = (media.getType() == Define.MEDIA_TYPE_APP) ? Define.ACTIVE_SAVE_APP : Define.ACTIVE_SAVE_CHANNEL;
				save = (media.getState() == 4 || actived < min) ? false : DataSave.getSave(role);
			}else{//cpc
				save = (media.getState() == 4) ? false : DataSave.getSave(role);
			}
			item.saved = save ? 1 : 0;
			s = "cpa";
			//cost = item.saved > 0 ? 0 : DataSave.getRate(role, cost);
		}

		cost = (item.saved == 1 || invalid > 0) ? 0 : DataSave.getRate(role, cost);
//		条件之一：media.getMid() == 7714
		if(type > 2) {
			LOG.debug(String.format("test click save : %d, rate : %d, cost=%2f, invalid=%d", role.getSave(), role.getRate() , cost, invalid));
		}

		LOG.trace(String.format("insert item to database click mac=%s udid=%s appid=%d adid=%d cost=%2f invalid=%d saved=%s", mac, udid, appid, adid, cost, invalid,s));
		DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK).put(adid, mac,udid, item);

		//存一份redis 唯一键值
		int vcount = 1;
		int vinvalid =  (invalid > 0 ? 1 : 0);
		int vsaved = (save ? 0 : 1);

		CountValues cv = CountValues.create(action, vcount, vinvalid, 1, vsaved, income, cost);
		Counter.getInstance().add(ck, cv);

		//广告ID：15477 ，特殊回调模式，所以增加点击记录
		if(adid == 15477 ){
			Jedis jMigu = null;
			try {
				jMigu = RedisConnection.getInstance("migu");
				String rKey = "MIGU_"+ DateUtils.formatMigu(new Date());
				jMigu.hset(rKey, udid, "1");
			} catch (Exception e) {
			}finally {
				if(jMigu != null) {
					RedisConnection.close("migu", jMigu);
				}
			}
		}

		return null;
	}
}