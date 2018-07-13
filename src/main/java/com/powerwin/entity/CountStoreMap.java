package com.powerwin.entity;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CountStoreMap {
	//大stores
	public Map<Integer,CountStore> stores = new ConcurrentHashMap<Integer,CountStore>();

	
	//如果不存在创建一个type-store 返回的是空的对象
	public CountStore getMap(int type) {
		CountStore store = stores.get(type);
		if(store == null) {
			store = new CountStore(type);
			stores.put(type, store);
		}
		return store;
	}
	
	public void save() {
        //迭代大stores 将每个CountStore存储到数据库
		for(Iterator<CountStore> it=stores.values().iterator();it.hasNext();) {
			CountStore store = it.next();
			store.save();
		}
	}
}
