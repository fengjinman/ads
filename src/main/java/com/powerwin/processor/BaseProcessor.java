package com.powerwin.processor;



import com.powerwin.boot.config.Define;
import com.powerwin.cache.MeidaPriceCache;
import com.powerwin.entity.DateTime;
import com.powerwin.entity.MediaPrice;
import com.powerwin.store.FileStore;
import com.powerwin.store.UniqueStore;
import com.powerwin.util.ListUtil;

import java.util.List;

/**
 * 处理器基础抽象类
 * 有内部接口，定义规范
 */
public abstract class BaseProcessor implements Processor {
	
	public static int[] DB_VALUES = {
		Index.ACTION, Index.SAVED, Index.UID, Index.APPID, Index.CID, Index.ADID, Index.FROM, 
		Index.MAC, Index.UDID, Index.OPENUDID, Index.IP
	};

	public interface Index extends Define.SourceIndex {
		int CID = 22;
		int ADID = 23;
		
		int AD_FROM = 24;
		
		int COUNT = 25;
		int INVALID = 26;
		int UNIQUE = 27;
		int SAVED = 28;
		
		int INCOME = 29;
		int COST = 30;

		int INTERVAL = 31;
	}

	public int processMediaType(int from) {
		if(from == 1 || from == 2) {
			return 1;
		}
		else if(from == 3) {
			return 2;
		}
		else{
			return 0;
		}
	}


	/**
	 * 返回类型为20180711类型的int日期
	 * @param vals
	 * @return
	 */
	public int date(List<Object> vals) { 
		int year = ListUtil.getInt(vals, Index.YEAR);
		int mon = ListUtil.getInt(vals, Index.MON);
		int day = ListUtil.getInt(vals, Index.DAY);
		
		return year*10000 + mon *100 + day;
	}
	
	public DateTime datetime(List<Object> vals) {
		int year = ListUtil.getInt(vals, Index.YEAR);
		int mon = ListUtil.getInt(vals, Index.MON);
		int day = ListUtil.getInt(vals, Index.DAY);
		
		int hour = ListUtil.getInt(vals, Index.HOUR);
		int min = ListUtil.getInt(vals, Index.MIN);
		int sec = ListUtil.getInt(vals, Index.SEC);
		
		return new DateTime(year, mon, day, hour, min, sec);
	}


	/**
	 * 检查唯一性
	 * @param date
	 * @param type
	 * @param action
	 * @param adid
	 * @param appid
	 * @param mac
	 * @param udid
	 * @return
	 */
	public int checkUnique(int date, int type, int action, int adid, int appid, String mac, String udid) { 
		UniqueStore store = UniqueStore.getInstance(FileStore.STORE_TEMP, date, type, action);
		Long exists = store.getId(adid, appid+mac+udid);
		if(exists != null) 
		{
			return 0;
		}
		
		store.putId(adid, appid+mac+udid, 1);
		return 1;
	}
	
	public float getPrice(int appid, int adid) {
		MediaPrice price = MeidaPriceCache.getInstance().get(appid, adid);
		if(price == null){
			return 0;
		}
		
		if(price.effect_time > System.currentTimeMillis()/1000) {
			return 2.0f;
		}
		
		return price.price;
	}
}
