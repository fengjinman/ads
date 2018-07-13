package com.powerwin.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CountStore {
	
	private int type;
	
	public CountStore(int type) {
		this.type = type;
	}
	
	private Map<CountKeys,CountValues> map = new ConcurrentHashMap<CountKeys,CountValues>(); 
	
	public void put(CountKeys keys, CountValues vals) {
		CountValues olds = map.get(keys);
		if(olds == null) {
			map.put(keys, vals);
		} else {
			olds.add(vals);
		}
	}

	public void save() {
		ReportToDatabase.getInstance().save(type, map);
	}
}
