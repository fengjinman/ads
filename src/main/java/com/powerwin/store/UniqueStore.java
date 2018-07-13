package com.powerwin.store;



import com.powerwin.util.KeyUtil;
import com.powerwin.util.MACUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UniqueStore extends KeyValueStore<Long> {

	private final static Map<String,UniqueStore> STORES = new ConcurrentHashMap<String,UniqueStore>();
	
	public static String getPath(String area, int date, int type, int action) {
		return String.format("%s/%d/%d/%d", area, date, type, action);
	}
	
	public static UniqueStore getInstance(String area, int date, int type, int action) {

		String path = getPath(area, date, type, action);
		UniqueStore store = STORES.get(path);
		if(store == null) {
			store = new UniqueStore(path, date, type);
			STORES.put(path, store);
		}
		return store;
	}

	public UniqueStore(String path, int date, int type) {
		super(path);
	}
	
	public Long getId(int adid, String key) {
		if(key != null && key!="") {
			CharSequence cs = KeyUtil.parse(key, adid);
			if(cs.length() > 0) {
				Long value = this.get(cs);
				return value;
			}
		}
		return null;
	}

	public long getId(int adid, String mac, String udid) {
		
		if(udid != null && udid!="") {
			Long value = this.getId(adid, udid);
			if(value != null) 
				return value;
		}
		
		mac = MACUtil.parse(mac);
		if(mac != null && mac!="") {
			Long value = this.getId(adid, mac);
			if(value != null) {
				return value;
			}
		}

		return -1;
	}
	
	public boolean exists(int adid, String mac, String udid) {
		long id = this.getId(adid, mac, udid);
		return (id >= 0);
	}
	
	public void putId(int adid, String key, long id) {

		if(key != null && key!="") {

			CharSequence cs = KeyUtil.parse(key,adid);
			if(cs.length() > 0) {
				this.put(cs, id);
			}
		}
	}
	
	public void putId(int adid, String mac, String udid, long id) {

		if(udid != null && udid!="") {this.putId(adid, udid, id);}
		
		mac = MACUtil.parse(mac);
		if(mac != null && mac!="") {this.putId(adid, mac, id);}
	}
}