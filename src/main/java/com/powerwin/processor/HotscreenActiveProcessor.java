package com.powerwin.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.powerwin.entity.Ads;
import com.powerwin.entity.CallbackItem;
import com.powerwin.entity.Media;
import com.powerwin.boot.config.RedisConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 激活处理器中出现热门推荐后单独处理
 * 热门处理器
 */
public class HotscreenActiveProcessor {

	private static final Logger LOG = LogManager.getLogger(HotscreenActiveProcessor.class);
	
	private static String SQL_SCREEN_CALLBACK_INSERT = "INSERT INTO `cpa_callback_screen_%s` (type,action,invalid,saved,uid,appid,cid,adid,ad_from,data_from,"
			+ "mac,udid,openudid,ip,open_time,create_time,income,cost,score,appuserid,process,keywords,job_num) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,%s);";

	public void process(List<Object> vals) {
//		int adid = ListUtil.getInt(vals, CallbackParser.Index.ADID);
//		//int appid = ListUtil.getInt(vals, CallbackParser.Index.APPID);
//	      int appid = 8079;
//
//		int jobNum = ListUtil.getInt(vals, CallbackParser.Index.JOB_NUM);
//
//		Ads ad = AdsCache.getInstance().get(adid);
//		if (ad == null) {
//			LOG.warn("ad " + adid + " not found.");
//			return;
//		}
//		Media media = MediaCache.getInstance().get(appid);
//		if (media == null) {
//			LOG.warn("media " + appid + " not found.");
//			return;
//		}
//
//		CallbackItem item = this.parseCallbackItem(vals, media, ad);
//		int year = ListUtil.getInt(vals, BaseProcessor.Index.YEAR);
//        int mon = ListUtil.getInt(vals, BaseProcessor.Index.MON);
//        int day = ListUtil.getInt(vals, BaseProcessor.Index.DAY);
//        int hour = ListUtil.getInt(vals, BaseProcessor.Index.HOUR);
////        CallbackItem item_val = new ActiveProcessor().getHistory(year, mon, day, 6, adid, item.mac, item.udid);
//
//
//		if(ad.getState() != 4 && ad.getState() != 7) {// 广告未启动
//			LOG.warn(String
//					.format("ad not start : item.date=%d item.action=%d item.mac=%s item.udid=%s adid=%d",
//							item.date, item.action, item.mac, item.udid , adid));
//			return;
//		}
//
//		// 判断该任务是否存在
//		JSONObject jsonObj = JsonUtil.getJson(ad.getJobs());
//		JSONArray jobs = jsonObj.getJSONArray("jobs");
//		int len = jobs.size();
//		if (jobNum < 0 || jobNum >= len) {// 未找到该任务，直接返回
//			LOG.warn(String.format("jobNum error, adid：%s，jobNum：%s， jobs：%s", adid, jobNum, len));
//			return;
//		}
//
//		// 检查唯一性，如果返回0，说明是重复领取任务
//		int unqiue = checkUnique(adid, item.udid, jobNum);
//		if (unqiue == 0) {
//			LOG.warn(String.format("job has received adid：%s，udid：%s，jobNum：%s", adid, item.udid, jobNum));
//			return;
//		}
//
//		// 更新任务状态
//		updateJobStatus(jobNum, adid, ad, item.udid, item.mac);
//
//		// 多次任务对应价格读取
//		float cost = getJobCost(jobs, ad.getBilling(), jobNum);
//		DataSave.DataSaveRole role = DataSave.getRole(media, ad);
//		item.cost = DataSave.getRate(role, cost);
//
//		// 得到收入
//		item.income = getIncome(jobs, jobNum);
//		// 积分计算，积分下发，返回积分
//	       int active_num = ListUtil.getInt(vals, CallbackParser.Index.ACTIVE_NUM);
//
//		item.score = getScore(ad, media, item.cost, item, jobNum,active_num);
//
//		LOG.trace(String.format("process countvalues：adid:%d,udid:%s,job_num:%d,income:%f,cost:%.2f",
//				adid, item.udid, jobNum, item.income, item.cost));
//
//		/*int year = ListUtil.getInt(vals, Index.YEAR);
//		int mon = ListUtil.getInt(vals, Index.MON);
//		int day = ListUtil.getInt(vals, Index.DAY);
//		int hour = ListUtil.getInt(vals, Index.HOUR);*/
//		if (jobNum > 0) {
//			item.action = Define.ACTION_JOB;
//		}
//		// 明细表数据入库 cpa_callback_screen
//		saveCallScreenback(item, year, jobNum);
//
//
//		// 激活统计
//        DetailHourKeys ck = DetailHourKeys.create(year, mon, day, hour, item.type, item.data_from, item.ad_from, appid, item.uid, adid,
//                item.cid,0,0);
//        CountValues cv = CountValues.create(item.action, 1, 0, 1, 1, item.income, item.cost);
//        Counter.getInstance().add(ck, cv);
		
		// 生成各个报表数据
		/*DetailHourKeys ck = DetailHourKeys.create(year, mon, day, hour, item.type,
				item.data_from, item.ad_from, appid, item.uid, adid, item.cid);
		CountValues cv = CountValues.create(item.action, 1, 0, 1,
				1, item.income, item.cost);
		Counter.getInstance().add(ck, cv);*/
        
        
	}
	
	private CallbackItem parseCallbackItem(List<Object> vals, Media media, Ads ad) {
//		CallbackItem item = new CallbackItem();
//		int year = ListUtil.getInt(vals, BaseProcessor.Index.YEAR);
//        int mon = ListUtil.getInt(vals, BaseProcessor.Index.MON);
//        int day = ListUtil.getInt(vals, BaseProcessor.Index.DAY);
//        int type = ListUtil.getInt(vals, CallbackParser.Index.TYPE);
//        String mac = ListUtil.getString(vals, CallbackParser.Index.MAC);
//        String udid = ListUtil.getString(vals, CallbackParser.Index.UDID);
//	    item = new ActiveProcessor().getHistory(year, mon, day, type, ad.getAdid(), mac, udid);
//
//
//		item.action = ListUtil.getInt(vals, CallbackParser.Index.ACTION);
//		item.uid = media.getUid();
//		item.appid = 8079;
//
//		item.cid = ad.getCid();
//		item.adid = ListUtil.getInt(vals, CallbackParser.Index.ADID);
//		item.ad_from = ad.getDataFrom();
//		//item.data_from = 1;
//		item.data_from = media.getType() == 1 ? 1 : 3;
//
//
//		item.openudid = ListUtil.getString(vals, CallbackParser.Index.OPENUDID);
//		item.ip = ListUtil.getLong(vals, BaseProcessor.Index.IP);
//		//item.appuserid = ListUtil.getString(vals, CallbackParser.Index.APP_USER_KEY);
//		item.appuserid = item.appuserid ;
//		//item.appuserid = ListUtil.getString(vals, 20);
//
//		if (item.appuserid != null && item.appuserid.length() > 0) {
//			try {
//				item.appuserid = URLDecoder.decode(item.appuserid, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//			}
//		}
//		item.open_time = new DateTime(vals).timestamp();
//		item.create_time = new DateTime(vals).timestamp();
//
//		item.income = 0;
//		item.cost = 0;
//		item.score = 0;
		
//		return item;
		return new CallbackItem();
	}
	
	/**
	 * 得到当前任务支出金额
	 * @param
	 * @param	billing		计价方式
	 * @param	jobNum	任务次序
	 * @return
	 */
	private static float getJobCost(JSONArray jobs, String billing, int jobNum) {
		float cost = 0.0f;
		JSONObject job = jobs.getJSONObject(jobNum);
		if (billing.indexOf('3') >= 0) {// 计价方式为job激活
			cost = Float.parseFloat(job.get("const").toString());
		}
		return cost;
	}
	
	/**
	 * 得到当前任务收入
	 * @param
	 * @return
	 */
	private static float getIncome(JSONArray jobs, int jobNum) {
		JSONObject job = jobs.getJSONObject(jobNum);
		float income = Float.parseFloat(job.get("sale_const").toString());
		return income;
	}
	
	/**
	 * 积分计算，积分下发，返回积分
	 * @param ad
	 * @param media
	 * @param cost
	 * @param item
	 * @param jobNum
	 * @return
	 */
	//private static float getScore(Ads ad, Media media, float cost, CallbackItem item, int jobNum) {
	private static float getScore(Ads ad, Media media, float cost, CallbackItem item, int jobNum, int active_num) {

//		float score = 0.0f;
//		float ratio = 0.0f;
//		MediaApp app;
//		if (cost > 0 && media.getType() == 1) {// 积分计算
//			app = (MediaApp) media;
//			ratio = app.getRatio();
//			if (ratio > 0) {
//				score = cost * ratio;
//			}
//		}
//		LOG.trace(String.format("user job score mac:%s,udid:%s,appid:%d,adid:%d,cost:%.2f,ratio:%.2f,score:%f,job_num:%d",
//				item.mac, item.udid, item.appid, item.adid, cost, ratio, score, jobNum));
//		if (media.getType() == 1) {// 媒体类型是应用，下发积分
//			app = (MediaApp) media;
//			item.score=score;
//			sendScoreToUser(active_num, ad, media, item, 0);
//
//
//			//UserScore.sendScore(app, ad, item.appuserid, item.mac, item.udid,
//				//	item.openudid, score, ad.getProcess_name(), jobNum);
//		}
//		return score;
		return 0;
	}
	
	
	
	 /**
     * 下发积分给用户
     * @param active_num 激活次数
     * @param ad 广告
     * @param media 媒体
     * @param item 回调报表数据
     * @param isPay 是否付费任务
     */
    private static void sendScoreToUser(int active_num, Ads ad, Media media, CallbackItem item, int isPay) {
        
        
        
//        //if (item.saved == 1 || item.invalid > 0) {
//        if ((item.saved == 1 || item.invalid > 0) && item.type != 6) {
//            LOG.info("media active save : " + item.uid + " " + item.appid + " " + item.appuserid + " " + item.mac + " "
//                    + item.udid + " " + item.openudid + " " + item.score);
//        } else {
//            if (media.getType() == 1) {
//                MediaApp app = (MediaApp) media;
//                UserScore.sendScore(app, ad, item.appuserid, item.mac, item.udid, item.openudid, item.score,
//                        ad.getProcess_name(), active_num, isPay);
//                LOG.info("active : adid={} udid={} | user send score " + item.uid + " " + item.appid + " "
//                        + item.appuserid + " " + item.mac + " " + item.udid + " " + item.openudid + " " + item.score,
//                        ad.getAdid(), item.udid);
//            } else {
//                if (item.appuserid != null && item.appuserid!="") {
//                    String url;
//                    try {
//                        url = URLDecoder.decode(item.appuserid, "UTF-8");
//                        if (url.length() > 4) {
//                            String proto = url.substring(0, 4).toLowerCase();
//                            if (proto.equals("http")) {
//                                url = url.replaceAll("&amp;", "&");
//                                JSONObject obj = new JSONObject();
//                                obj.put("protocol", "http");
//                                obj.put("method", "get");
//                                obj.put("url", url);
//                                LOG.debug("channel callback : " + url);
//                                Jedis jedis = RedisConnection.getInstance("values");
//                                jedis.rpush("ACTION_HTTP_REQUEST", obj.toString());
//                                RedisConnection.close("values", jedis);
//                            } else {
//                                LOG.error("error proto channel " + item.appid + " callback : " + url);
//                            }
//                        } else {
//                            LOG.error("error proto channel " + item.appid + " callback : " + url);
//                        }
//                    } catch (UnsupportedEncodingException e) {
//                        LOG.error("error on decode channel " + item.appid + " callback : " + item.appuserid);
//                    }
//                } else {
//                    LOG.debug("channel has no callback" + item.appid + item.appuserid);
//                }
//            }
//        }
    }
	
	/**
	 * 
	 * @param item
	 * @param year
	 * @param jobNum
	 */
//	private void saveCallScreenback(CallbackItem item, int year, int jobNum) {
//		SQLConnection cpaConn = null;
//		try {
//			cpaConn = SQLConnection.getInstance("hotscreen");
//			cpaConn.insert(String.format(SQL_SCREEN_CALLBACK_INSERT, year, jobNum), item.toObjects());
//		} catch (Exception e) {
//			LOG.error("save callbackscreen error", e);
//		} finally {
//			if (cpaConn != null) {cpaConn.close();}
//		}
//	}
	
	/**
	 * 检查唯一性，如果返回0，说明是重复领取任务
	 * @param adid
	 * @param udid
	 * @param jobNum
	 * @return
	 */
	private static int checkUnique(int adid, String udid, int jobNum) {
		Jedis jedis = null;
		int unique = 1;
		try {
			jedis = RedisConnection.getInstance("main");
			String jobStatusData = jedis.hget("DATA_JOB_STATUS", String.format("%s_%s", adid, udid));
			if (jobStatusData != null && !"".equals(jobStatusData)) {
				JSONObject jobStatusObject = JSONObject.parseObject(jobStatusData);
				String jobStatusStr = jobStatusObject.getString("jobstatus");// 得到任务状态
				String[] jobStatusArray = jobStatusStr.split(",");
				if ("2".equals(jobStatusArray[jobNum])) {// 如果当前上报任务已领取
					unique = 0;
				}
			}
		} catch (Exception e) {
			LOG.error(String.format("checkunique error adid：%s，udid：%s，jobNum：%s", adid, udid, jobNum), e);
		} finally {
			if (jedis != null) {RedisConnection.close("main", jedis);}
		}
		return unique;
	}
	
	/**
	 * 更新任务状态
	 * @param jobNum					任务次数
	 * @param adid						广告ID
	 * @param ad
	 * @param
	 */
	private static void updateJobStatus(int jobNum, int adid, Ads ad,
			String udid, String mac) {
//		Jedis jedis = null;
//		StringBuilder sb = new StringBuilder();
//		int[] jobStatus = null;
//		String jobStatusStr;// 任务状态
//		String uid = "";// 用户ID
//		String wingold = "";// 赢取金币数
//		String name = "";// 用户名称
//		String channel = "";// 渠道ID
//		try {
//			jedis = RedisConnection.getInstance("main");
//			String jobStatusData = jedis.hget("DATA_JOB_STATUS",
//					String.format("%s_%s", adid, udid));
//			if (jobStatusData != null && jobStatusData.length() > 0) {// redis已设置了任务状态
//				JSONObject jobStatusObject = JSONObject.parseObject(jobStatusData);
//				uid = jobStatusObject.getString("uid");
//				wingold = jobStatusObject.getString("wingold");
//				name = jobStatusObject.getString("name");
//				channel = jobStatusObject.getString("channel");
//				jobStatusStr = jobStatusObject.getString("jobstatus");// 得到任务状态
//				String[] jobStatusArray = jobStatusStr.split(",");
//				jobStatus = new int[jobStatusArray.length];
//				for (int i = 0, length = jobStatusArray.length; i < length; i++) {
//					jobStatus[i] = Integer.parseInt(jobStatusArray[i]);
//				}
//			}
//
//			int remainNum = RemainActiveUtil.getRemain(adid);// 剩余量
//			if (jobNum == 0 && remainNum > 0) {// 根据第一个任务控量
//				if (remainNum == 1) {// 剩余一个量广告暂停
//					DailyClean.setAdsStateToPause(adid);
//				}
//				RemainActiveUtil activeUtil = RemainActiveUtil.getInstance();
//				// 消耗剩余激活量
//				activeUtil.remainActive("-", adid, 1);
//				RedisStore.getInstance().addActive(mac + udid, adid);
//				// 更新任务状态
//				if (jobStatusData == null || jobStatusData.length() == 0) {
//					JSONArray jobs = new JSONObject(ad.getJobs()).getJSONArray("jobs");
//					jobStatus = new int[jobs.length()];// 该用户对该广告所有任务状态默认置为不可领取
//				}
//				jobStatus[0] = 2;// 第一个任务设为已领取
//			} else {// 将相应任务置为已领取
//				jobStatus[jobNum] = 2;// 将任务设为已领取
//			}
//			// 将任务状态更新入redis
//			for (int j = 0, length = jobStatus.length; j < length; j++) {
//				if (j == (length - 1)) {
//					sb.append(jobStatus[j]);
//					break;
//				}
//				sb.append(jobStatus[j]).append(",");
//			}
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("uid", uid);
//			jsonObject.put("wingold", wingold);
//			jsonObject.put("name", name);
//			jsonObject.put("channel", channel);
//			jsonObject.put("jobstatus", sb.toString());
//			jedis.hset("DATA_JOB_STATUS", String.format("%s_%s", adid, udid), jsonObject.toString());
//			LOG.trace(String.format("DATA_JOB_STATUS adid：%s，imei：%s，result：%s",
//					adid, udid, jsonObject.toString()));
//		} catch (Exception e) {
//			LOG.error(String.format("update jobstatus error adid：%s，udid：%s", adid, udid), e);
//		} finally {
//			if (jedis != null) RedisConnection.close("main", jedis);
//		}
	}
	
}
