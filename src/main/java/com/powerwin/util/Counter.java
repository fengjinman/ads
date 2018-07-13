package com.powerwin.util;


import com.powerwin.entity.CountStoreMap;
import com.powerwin.entity.CountValues;
import com.powerwin.entity.DetailHourKeys;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Counter {
	
	public static Counter counter = new Counter();
	public static Counter getInstance() {
		return counter;
	}

	private Queue<CountStoreMap> storeQueue = new ConcurrentLinkedQueue<CountStoreMap>();
	
	public CountStoreMap switchStore() {
		if(storeQueue.size() == 0){

			return null;
		}
		CountStoreMap store = storeQueue.poll();
		storeQueue.add(new CountStoreMap());
		return store;
	}
	
	public CountStoreMap getStore() {
		if(storeQueue.isEmpty()) {
			CountStoreMap store = new CountStoreMap();
			storeQueue.add(store);
			return store;
		}
		return storeQueue.peek();
	}

	public void add(DetailHourKeys ck, CountValues cv) {
		CountStoreMap storeMap = getStore();
		storeMap.getMap(ck.type).put(ck, cv);
	}
}
