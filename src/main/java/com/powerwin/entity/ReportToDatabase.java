package com.powerwin.entity;

import com.powerwin.dao.CallbackTableDemoMapper;
import com.powerwin.dao.CpcDayMapper;
import com.powerwin.dao.CpcHourMapper;
import com.powerwin.util.DaoUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class ReportToDatabase {
	
	public static Logger LOG = LogManager.getLogger(ReportToDatabase.class);

	CpcHourMapper cpcHourMapper = (CpcHourMapper) DaoUtil.getDao(CpcHourMapper.class);
	CpcDayMapper cpcDayMapper = (CpcDayMapper)DaoUtil.getDao(CpcDayMapper.class);
	CallbackTableDemoMapper callbackTableMapper = (CallbackTableDemoMapper)DaoUtil.getDao(CallbackTableDemoMapper.class);


	private static ReportToDatabase store = new ReportToDatabase();
	
	public static ReportToDatabase getInstance() {
		return store;
	}

	public void saveError(String dbname, String sql) {
//		SimpleFileWriter store = SimpleFileWriter.getInstance("sqlerrors/"+TimeCache.date+"/"+dbname+TimeCache.hour+".sql");
//		try {
//			store.write(sql +";\n");
//		} catch (IOException e) {
//		}
	}
	
	public void saveSQL(String dbname, String sql) {
//		SimpleFileWriter store = SimpleFileWriter.getInstance("sqls/"+TimeCache.date+"/"+dbname+TimeCache.hour+".sql");
//		try {
//			store.write(sql +";\n");
//		} catch (IOException e) {
//		}
	}
	
	public void save(int type, Map<CountKeys,CountValues> map) {

//		String dbname = Define.getDataSourceName(type);

//		CountKeys keys = null;
//		SQLConnection conn = SQLConnection.getInstance(dbname);

		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
//			String sql = CountSQL.create(entry.getKey(), entry.getValue());

			CountKeys key = entry.getKey();
			String[] fileds = key.getFileds();

			if("hour".equals(fileds[4])){
				System.out.println("[-- 插入cpc_hour开始 --]");
				CpcHour cpc_hour = getCpcHour(entry.getKey(),entry.getValue());
				int insert = cpcHourMapper.insert(cpc_hour);
				if(insert<=0){
					LOG.error("insert fail result data is "+insert);
				}else{
					LOG.info("insert cpc_hour success , result data is "+ insert);
				}
				System.out.println("[-- 插入cpc_hour结束 --]");
			}else if("adplanid".equals(fileds[12])){
				StringBuffer tablename = getCallbackTablename();
				System.out.println("[-- 插入"+tablename+"开始 --]");
				CallbackTableDemo callback = getCallBackInstance(entry.getKey(),entry.getValue());
				int insert = callbackTableMapper.insert(tablename, callback);
				if(insert<=0){
					LOG.error("insert fail result data is "+insert);
				}else{
					LOG.info("insert "+tablename+" success , result data is "+ insert);
				}
				System.out.println("[-- 插入"+tablename+"结束 --]");
			}else{
				System.out.println("[-- 插入cpc_day开始 --]");
				CpcDay cpc_day = getCpcDay(entry.getKey(),entry.getValue());
				int insert = cpcDayMapper.insert(cpc_day);
				if(insert<=0){
					LOG.error("insert fail result data is "+insert);
				}else{
					LOG.info("insert cpc_day success , result data is "+ insert);
				}
				System.out.println("[-- 插入cpc_day结束 --]");
			}

//			saveSQL(dbname, sql);
//			int result = conn.update(sql, null);
//			if(result <= 0) {
//				saveError(dbname, sql);
//			}
//			LOG.debug("report to database result : "+result+" sql : " + sql);

//			if(keys == null){
//				keys = entry.getKey();
//			}
		}
//		conn.close();

//		if(keys instanceof DetailHourKeys) {
//			reportAdsHour(type, map);
//			reportMediaHour(type, map);
//			reportDetailDay(type, map);
//			reportSumHour(type, map);
//		} else if(keys instanceof AdsHourKeys) {
//			reportAdsDay(type, map);
//		} else if(keys instanceof MediaHourKeys) {
//			reportMediaDay(type, map);
//		} else if(keys instanceof SumHourKeys) {
//			reportSumDay(type, map);
//		}
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

	private CallbackTableDemo getCallBackInstance(CountKeys ck, CountValues cv){
		Object kvs[] = ck.getValues();
		Object vvs[] = cv.getValues();
		CallbackTableDemo c = new CallbackTableDemo();
//		created,year,mon,day,type,data_from,ad_from,appid,uid,adid,cid,game_id,adplanid
		return null;
	}


	private CpcDay getCpcDay(CountKeys ck, CountValues cv){
		Object kvs[] = ck.getValues();
		Object vvs[] = cv.getValues();
//		created,year,mon,day, type,data_from, ad_from, adid,cid,game_id,adplanid
		CpcDay c = new CpcDay();
		c.setCreated((Integer) kvs[0]);
		c.setYear((Short) kvs[1]);
		c.setMon((Short) kvs[2]);
		c.setDay((Short) kvs[3]);
		c.setType((Short) kvs[4]);
		c.setDataFrom((Short) kvs[5]);
		c.setAdFrom((Short) kvs[6]);
		c.setGameId((Short) kvs[9]);
//		show_count,show_invalid,show_unique,show_saved,
//		jump_count, jump_invalid,jump_unique,jump_saved,
//		click_count,click_invalid,click_unique,click_saved,click_income,click_cost,
//		active_count,active_invalid,active_unique,active_saved,active_income,active_cost,
//		job_count,job_invalid,job_saved,job_unique,job_income,job_cost,
//		job_count1, job_count2, job_count3, job_count4,
//		job_count5, job_count6, job_count7, job_count8, job_count9
		c.setShowCount((Integer) vvs[0]);
		c.setShowInvalid((Integer) vvs[1]);
		c.setShowUnique((Integer) vvs[2]);
		c.setShowSaved((Integer) vvs[3]);

		c.setJumpCount((Integer) vvs[4]);
		c.setJumpInvalid((Integer) vvs[5]);
		c.setJumpUnique((Integer) vvs[6]);
		c.setJumpSaved((Integer) vvs[7]);

		c.setClickCount((Integer) vvs[8]);
		c.setClickInvalid((Integer) vvs[9]);
		c.setClickUnique((Integer) vvs[10]);
		c.setClickSaved((Integer) vvs[11]);
		c.setClickIncome((Float) vvs[12]);
		c.setClickCost((Float) vvs[13]);

		c.setActiveCount((Integer) vvs[14]);
		c.setActiveInvalid((Integer) vvs[15]);
		c.setActiveUnique((Integer) vvs[16]);
		c.setActiveSaved((Integer) vvs[17]);
		c.setActiveIncome((Float) vvs[18]);
		c.setActiveCost((Float) vvs[19]);

		c.setJobCount((Integer) vvs[20]);
		c.setJobInvalid((Integer) vvs[21]);
		c.setJobSaved((Integer) vvs[22]);
		c.setJobUnique((Integer) vvs[23]);
		c.setJobIncome((Float) vvs[24]);
		c.setJobCost((Float) vvs[25]);
		c.setJobCount1((Integer) vvs[26]);
		c.setJobCount2((Integer) vvs[27]);
		c.setJobCount3((Integer) vvs[28]);
		c.setJobCount4((Integer) vvs[29]);
		c.setJobCount5((Integer) vvs[30]);
		c.setJobCount6((Integer) vvs[31]);
		c.setJobCount7((Integer) vvs[32]);
		c.setJobCount8((Integer) vvs[33]);
		c.setJobCount9((Integer) vvs[34]);
		return c;
	}

	private CpcHour getCpcHour(CountKeys ck, CountValues cv){

		Object kvs[] = ck.getValues();
		Object vvs[] = cv.getValues();
//		created,year,mon,day,hour,type,data_from,ad_from,adid,cid,game_id,adplanid
		CpcHour c = new CpcHour();
		c.setCreated((Integer) kvs[0]);
		c.setYear((Short) kvs[1]);
		c.setMon((Short) kvs[2]);
		c.setDay((Short) kvs[3]);
		c.setHour((Short) kvs[4]);
		c.setType((Short) kvs[5]);
		c.setDataFrom((Short) kvs[6]);
		c.setAdFrom((Short) kvs[7]);
		c.setGameId((Short) kvs[10]);

//		show_count,show_invalid,show_unique,show_saved,
//		jump_count, jump_invalid,jump_unique,jump_saved,
//		click_count,click_invalid,click_unique,click_saved,click_income,click_cost,
//		active_count,active_invalid,active_unique,active_saved,active_income,active_cost,
//		job_count,job_invalid,job_saved,job_unique,job_income,job_cost,
//		job_count1, job_count2, job_count3, job_count4,
//		job_count5, job_count6, job_count7, job_count8, job_count9

		c.setShowCount((Integer) vvs[0]);
		c.setShowInvalid((Integer) vvs[1]);
		c.setShowUnique((Integer) vvs[2]);
		c.setShowSaved((Integer) vvs[3]);

		c.setJumpCount((Integer) vvs[4]);
		c.setJumpInvalid((Integer) vvs[5]);
		c.setJumpUnique((Integer) vvs[6]);
		c.setJumpSaved((Integer) vvs[7]);

		c.setClickCount((Integer) vvs[8]);
		c.setClickInvalid((Integer) vvs[9]);
		c.setClickUnique((Integer) vvs[10]);
		c.setClickSaved((Integer) vvs[11]);
		c.setClickIncome((Float) vvs[12]);
		c.setClickCost((Float) vvs[13]);

		c.setActiveCount((Integer) vvs[14]);
		c.setActiveInvalid((Integer) vvs[15]);
		c.setActiveUnique((Integer) vvs[16]);
		c.setActiveSaved((Integer) vvs[17]);
		c.setActiveIncome((Float) vvs[18]);
		c.setActiveCost((Float) vvs[19]);

		c.setJobCount((Integer) vvs[20]);
		c.setJobInvalid((Integer) vvs[21]);
		c.setJobSaved((Integer) vvs[22]);
		c.setJobUnique((Integer) vvs[23]);
		c.setJobIncome((Float) vvs[24]);
		c.setJobCost((Float) vvs[25]);
		c.setJobCount1((Integer) vvs[26]);
		c.setJobCount2((Integer) vvs[27]);
		c.setJobCount3((Integer) vvs[28]);
		c.setJobCount4((Integer) vvs[29]);
		c.setJobCount5((Integer) vvs[30]);
		c.setJobCount6((Integer) vvs[31]);
		c.setJobCount7((Integer) vvs[32]);
		c.setJobCount8((Integer) vvs[33]);
		c.setJobCount9((Integer) vvs[34]);
		return c;
	}

	private void reportSumDay(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			SumHourKeys key = (SumHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(SumDayKeys.create(key), value);
		}
		store.save();
	}
	
	private void reportSumHour(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			DetailHourKeys key = (DetailHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(SumHourKeys.create(key), value);
		}
		store.save();
	}

	private void reportDetailDay(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			DetailHourKeys key = (DetailHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(DetailDayKeys.create(key), value);
		}
		store.save();
	}

	private void reportMediaDay(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			MediaHourKeys key = (MediaHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(MediaDayKeys.create(key), value);
		}
		store.save();
	}

	private void reportAdsDay(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			AdsHourKeys key = (AdsHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(AdsDayKeys.create(key), value);
		}
		store.save();
	}

	private void reportMediaHour(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			DetailHourKeys key = (DetailHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(MediaHourKeys.create(key), value);
		}
		store.save();
	}

	private void reportAdsHour(int type, Map<CountKeys, CountValues> map) {
		CountStore store = new CountStore(type);
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			DetailHourKeys key = (DetailHourKeys)entry.getKey();
			CountValues value = entry.getValue().clone();
			
			store.put(AdsHourKeys.create(key), value);
		}
		store.save();
	}

	//广告预算控制
	/*
	private void updateAdsToday(int type, Map<CountKeys, CountValues> map) {
		
		Map<AdsRunStateKey,AdsRunState> states = new HashMap<AdsRunStateKey,AdsRunState>();
		
		for(Iterator<Entry<CountKeys,CountValues>> it = map.entrySet().iterator();it.hasNext();) {
			Entry<CountKeys,CountValues> entry = it.next();
		
			AdsDayKeys key = (AdsDayKeys)entry.getKey();
			CountValues value = entry.getValue();
			
			AdsRunStateKey nk = new AdsRunStateKey(key.created, key.adid);
			
			AdsRunState state = states.get(nk);
			if(state == null) {
				if(value.active_income > 0 || value.active_count > 0) {
					state = new AdsRunState(value.active_count, value.active_income+value.job_income);
				}
				if(state != null && (state.getNum() > 0 || state.getMoney() > 0)) states.put(nk, state);
			} else {
				if(value.active_count > 0 || value.active_income > 0) {
					state.add(value.active_count, value.active_income);
				}
			}
		}

		Jedis jedis = RedisConnection.getInstance("main");
		for(Iterator<Entry<AdsRunStateKey, AdsRunState>> it = states.entrySet().iterator();it.hasNext();) {
			Entry<AdsRunStateKey, AdsRunState> entry = it.next();
			AdsRunStateKey key = entry.getKey();

			Ads ad = AdsCache.getInstance().get(key.getAdid());
			if(ad.getState() == 4 && (ad.getNum() > 0 || ad.getMoney() > 0)) {
				
				AdsRunState state = entry.getValue();
				
				String rkey = "DATA_ADS_"+key.getCreated();
				String stateString = jedis.hget(rkey, String.valueOf(key.getAdid()));
				if(stateString == null) {
					jedis.hset(rkey, String.valueOf(key.getAdid()), state.toString());
				} else {
					AdsRunState old = new AdsRunState(stateString);
					old.add(state.getNum(), state.getMoney());
					jedis.hset(rkey, String.valueOf(key.getAdid()), old.toString());
					state = old;
				}
				
				int num = state.getNum();
				float money = state.getMoney();
				
				if((ad.getNum() > 0 && num >= ad.getNum()) || (ad.getMoney() > 0 && money >= ad.getMoney())) {
					DailyClean.setAdsStateToPause(key.getAdid());
				}
			}
		}
		RedisConnection.close("main", jedis);
	}
	*/
}
