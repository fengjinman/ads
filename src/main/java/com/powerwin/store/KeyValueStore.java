package com.powerwin.store;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;

import java.util.List;

public class KeyValueStore<T> {
	
	private RadixTree<T> tree = null;
	private FileStore store = null;

	public KeyValueStore(String type) {
		tree = new ConcurrentRadixTree<T>(new SmartArrayBasedNodeFactory() , false);
		
		if(type != null) {
			store = FileStore.getInstance(type+".idx");
			load();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		List<Object> kvs =  store.read();
		if(kvs == null) return;
		
		for(int i=0;i<kvs.size();i+=2) {
			Object ok = kvs.get(i);
			CharSequence key = null;
			if(ok != null && (ok instanceof CharSequence)) {
				key = (CharSequence)ok;
				
				if(i+1 >= kvs.size()) {
					System.out.println("Index file error : " + store.name);
					break;
				}
				
				T val = (T)kvs.get(i+1);
				
				if(key != null && val != null) {
					tree.put(key, val);
				}
			}
		}
	}

	public T put(CharSequence key, T value) {
		
		T o = tree.put(key, value);
		
		if(store != null) store.write(key, value);
		
		return o;
	}
	
	public T get(CharSequence key) {
		return tree.getValueForExactKey(key);
	}
	
	public boolean remove(CharSequence key) {
		return tree.remove(key);
	}
}
