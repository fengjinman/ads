package com.powerwin.processor;
import com.powerwin.boot.config.Define;
import com.powerwin.boot.config.RedisConnection;
import com.powerwin.cache.AdsCache;
import com.powerwin.cache.MediaCache;
import com.powerwin.dao.CallbackTableDemoMapper;
import com.powerwin.dao.CpcDayMapper;
import com.powerwin.dao.CpcHourMapper;
import com.powerwin.entity.*;
import com.powerwin.parser.ActionParser;
import com.powerwin.store.DBHistoryStore;
import com.powerwin.store.FileStore;
import com.powerwin.store.RedisStore;
import com.powerwin.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JumpProcessor extends CallbackProcessor {

	public static Logger LOG = LogManager.getLogger(JumpProcessor.class);


	CpcHourMapper cpcHourMapper = (CpcHourMapper)DaoUtil.getDao(CpcHourMapper.class);
	CpcDayMapper cpcDayMapper = (CpcDayMapper)DaoUtil.getDao(CpcDayMapper.class);
	CallbackTableDemoMapper callbackTableMapper = (CallbackTableDemoMapper)DaoUtil.getDao(CallbackTableDemoMapper.class);

	public List<Object>[] process(List<Object> vals) {
		
		int action, type, appid, adid, root = 0;
		String session = "",did = "", ssid = "", localip = "", openudid = "" , rload = "";
		long disk = 0;
		try {
			action = (Integer) vals.get(Index.ACTION);
			type = (Integer) vals.get(Index.TYPE);
			
			appid = (Integer) vals.get(Index.APPID);
			adid = (Integer) vals.get(Index.ADID);
		} catch (Exception e) {
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
		} catch (Exception e) {}
		
		//控量处理
		int remain = RemainActiveUtil.getRemain(adid);
//		LOG.info("jump : adid={} udid={} | remain num : {}", adid, udid, remain);
		/*if(remain <= 0) {
			LOG.debug("jump remain num : " + adid + " , " + remain + " , " + udid);
//			return null;
		}*/
		
		if (adid == 0 || appid == 0 || action == 0 || type == 0) {
			LOG.trace(String.format("data item has error adid=%d appid=%d action=%d type=%d", adid, appid, action, type));
			return null;
		}
		
		Media media = MediaCache.getInstance().get(appid);
		if (media == null) {
			LOG.debug("media " + appid + " not found.");
			return null;
		}

		Ads ad = AdsCache.getInstance().get(adid);
		if (ad == null) {
			LOG.debug("ad " + adid + " not found.");
			return null;
		}

		boolean isStop = false;
		//1 投放  2停止  3软删除 4测试（控制墙是否显示） 开发者行为
		//媒体分类 1 应用 2 渠道
		if(media.getState() != 1 && media.getState() != 4 && media.getType() != 2) {
			isStop = true;
		}
		
		//状态 1新广告,2审核通过,，3拒绝，4启动 5 停止 6软删除，7调试，8暂停, 9 留存
		// todo 手动关闭条件 ad.getIs_hand_stop() == 1
		//手动停止的广告，不用做留存了
		if(ad.getStatus() == 5){
			isStop = true;
		} else if(ad.getStatus() == 5 ||  ad.getStatus() == 8   || ad.getStatus() == 9){
			//留存广告，超过1周的
			int current = (int) (System.currentTimeMillis()/1000);
			// 原为：ad.getUpdateTime()
			if(current - ad.getEnd_time() > 3600 * 24 * 7){
				isStop = true;
			}
		} else if(ad.getStatus() != 4 && ad.getStatus() != 7 && media.getType() != 2) {
			isStop = true;
		}

		// 1 应用 3 渠道
		int data_from = media.getType() == 1 ? 1 : 3;
        //	int ad_from = ad.getDataFrom();
		int ad_from = 0;

		int year = ListUtil.getInt(vals, Index.YEAR);
		int mon = ListUtil.getInt(vals, Index.MON);
		int day = ListUtil.getInt(vals, Index.DAY);
		int hour = ListUtil.getInt(vals, Index.HOUR);

		String osver = String.valueOf(vals.get(Index.OSVER));	//IOS系统版本
		
//		int uid = media.getUid();
//		int cid = ad.getCid();
		int uid = 0;
		int cid = 0;
		int date = year * 10000 + mon * 100 + day;
		int game_id = 0;
		int adplanid = 0;
		
		DetailHourKeys ck = DetailHourKeys.create(year, mon, day, hour, type, data_from, ad_from, appid, uid, adid, cid,game_id,adplanid);
		int unique = checkUnique(date, type, action, adid, appid, mac, udid);
		int saved = 1;
		
		boolean save = false;
		if(isStop) {
			save = true;
			saved = 0;
		} /*else if(unique == 1) {
			DataSaveRole role = DataSave.getRole(media, ad);
			save = DataSave.getSave(role);
			if(save) saved = 0;
		}*/
		
		//特殊控量    
		RemainActiveUtil.count(udid, mac, ad);
		
		if(unique == 1) {
			CallbackItem item = this.getHistory(year, mon, day, type, adid, mac, udid);
			DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK).exists(adid, mac, udid);
			if (item == null) {
				
				item = CallbackItem.parseFromLog(vals, media, ad);
//				item.cid = ad.getCid();
//				item.uid = media.getUid();
				item.cid = 0;
				item.uid = 0;

				/*
				if(media.getState() != 1 && media.getState() != 4 && media.getType() != 2) {
					isStop = true;
				}
				
				if(ad.getState() != 4 && ad.getState() != 7 && media.getType() != 2) {
					isStop = true;
				}
				*/
				
				int invalid =  0; //(media.getState() == 1) ? 0 : 4;
				//是否启用防作弊	liuhuiya  media.getIsEnable() == 0
				if(true){
					/**
					 * DID SESSION
					 */
					if(did != null && did.length() > 0) {
						invalid = RedisStore.getInstance().addDidCheat(ad, session, did, udid);
					}
					
					/**
					 * 防作弊2：SSID
					 * 收集udid 和 ssid 的映射关系
					 */
					if(ssid != null && ssid != null) {
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
				
				//媒体特殊处理   media.getMid() == 5656
				if(true) {
					if(osver.contains("7.")){
						 if(invalid == 0) {
							 //osver系统版本作弊屏蔽
							 invalid = 10;
						 }
					}
				}

				item.invalid = item.invalid == 0 ? invalid : item.invalid;
				if(item.invalid > 0){
					item.saved = 1;
				}else {
					DataSave.DataSaveRole role = DataSave.getRole(media, ad);
					save = DataSave.getSave(role);
					item.saved = save ? 1 : 0;
				}
				if(save || item.saved > 0) {
					saved = 0;
				}
				item.action = Define.ACTION_CLICK;

				LOG.trace(String.format("update item to database jump mac=%s udid=%s appid=%d adid=%d invalid=%d saved=%d", mac, udid, appid, adid, invalid,item.saved));
//				DBHistoryStore.getInstance(FileStore.STORE_STORE, date, type, Define.ACTION_CLICK).put(adid, mac,udid, item);
//				// todo
//				CountValues cv = CountValues.create(Define.ACTION_CLICK, 1, 0, 1, saved, 0, 0);
//				Counter.getInstance().add(ck, cv);
				CpcHour cpc_hour = getCpcHour(year,mon,day,hour,type,data_from,ad_from,game_id,1,0,unique,saved);
				cpcHourMapper.insert(cpc_hour);

				CpcDay cpc_day = getCpcDay(year,mon,day,type,data_from,ad_from,game_id,1,0,unique,saved);
				cpcDayMapper.insert(cpc_day);

				StringBuffer tablename = getCallbackTablename();
				CallbackTableDemo callback = getCallBackInstance(data_from,ad_from,game_id,saved,action,adid,appid,udid,uid,cid,0,ad.getPlanid());
				callbackTableMapper.insert(tablename,callback);
			}
		}
		
		int vcount = 1;
		int vunique = 1;
		int vsaved = saved;
		/*
		if(RAND.nextInt(100) < 16) {
			vcount = vunique = vsaved = 0;
		}
		*/
		// todo
//		CountValues cv = CountValues.create(action, vcount, 0, vunique, vsaved, 0, 0);
//		Counter.getInstance().add(ck, cv);
		CpcHour cpc_hour = getCpcHour(year,mon,day,hour,type,data_from,ad_from,game_id,1,0,unique,saved);
		cpcHourMapper.insert(cpc_hour);

		CpcDay cpc_day = getCpcDay(year,mon,day,type,data_from,ad_from,game_id,1,0,unique,saved);
		cpcDayMapper.insert(cpc_day);

		StringBuffer tablename = getCallbackTablename();
		CallbackTableDemo callback = getCallBackInstance(data_from,ad_from,game_id,saved,action,adid,appid,udid,uid,cid,0,ad.getPlanid());
		callbackTableMapper.insert(tablename,callback);
		
		//广告ID：15477 ，特殊回调模式，所以增加点击记录
		if(adid == 15477 ) {
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

	/**
	 * 填充CallbackTableDemo对象
	 * @param data_from
	 * @param ad_from
	 * @param game_id
	 * @param saved
	 * @param action
	 * @param adid
	 * @param appid
	 * @param udid
	 * @param uid
	 * @param cid
	 * @param is_bool_monitor
	 * @param planid
	 * @return
	 */
	public CallbackTableDemo getCallBackInstance(int data_from,int ad_from,int game_id,int saved,int action,int adid,int appid,String udid,int uid,int cid,int is_bool_monitor,int planid){
		CallbackTableDemo c = new CallbackTableDemo();
		c.setDataFrom(data_from);
		c.setAdFrom(ad_from);
		c.setGameId(game_id);
		c.setSaved((short) saved);
		c.setCid(cid);
		c.setUid(uid);
		c.setAdid(adid);
		c.setAction((short) action);
		c.setIsBoolMonitor((short)is_bool_monitor);
		c.setAppid(appid);
		c.setAdplanid(planid);
		c.setUdid(udid);
		return c;
	}

	/**
	 * 生成回调表名
	 * @return
	 */
	public StringBuffer getCallbackTablename(){
		StringBuffer base = new StringBuffer("cpc_callback_");
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String time = sdf.format(d);
		base.append(time);
		return base;
	}

	/**
	 * 填充CpcHour对象
	 * @param year
	 * @param mon
	 * @param day
	 * @param hour
	 * @param type
	 * @param data_from
	 * @param ad_from
	 * @param game_id
	 * @param jump_count
	 * @param jump_invalid
	 * @param jump_unique
	 * @param jump_saved
	 * @return
	 */
	public CpcHour getCpcHour(int year,int mon,int day,int hour,int type,int data_from,int ad_from,int game_id,int jump_count,int jump_invalid,int jump_unique,int jump_saved){
		CpcHour c = new CpcHour();
		c.setYear((short)year);
		c.setMon((short)mon);
		c.setDay((short)day);
		c.setHour((short)hour);
		c.setType((short)type);
		c.setDataFrom((short)data_from);
		c.setAdFrom((short)ad_from);
		c.setGameId((short)game_id);
		c.setJumpCount(jump_count);
		c.setJumpInvalid(jump_invalid);
		c.setJumpUnique(jump_unique);
		c.setJumpSaved(jump_saved);
		return c;
	}

	/**
	 * 填充CpcDay对象
	 * @param year
	 * @param mon
	 * @param day
	 * @param type
	 * @param data_from
	 * @param ad_from
	 * @param game_id
	 * @param jump_count
	 * @param jump_invalid
	 * @param jump_unique
	 * @param jump_saved
	 * @return
	 */
	public CpcDay getCpcDay(int year,int mon,int day,int type,int data_from,int ad_from,int game_id,int jump_count,int jump_invalid,int jump_unique,int jump_saved){
		CpcDay c = new CpcDay();
		c.setYear((short)year);
		c.setMon((short)mon);
		c.setDay((short)day);
		c.setType((short)type);
		c.setDataFrom((short)data_from);
		c.setAdFrom((short)ad_from);
		c.setGameId((short)game_id);
		c.setJumpCount(jump_count);
		c.setJumpInvalid(jump_invalid);
		c.setJumpUnique(jump_unique);
		c.setJumpSaved(jump_saved);
		return c;
	}
}