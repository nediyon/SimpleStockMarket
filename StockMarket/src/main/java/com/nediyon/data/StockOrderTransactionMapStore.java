package com.nediyon.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.MapStore;
import com.nediyon.models.StockOrder;


public class StockOrderTransactionMapStore implements MapStore<String, StockOrder> {

    private ConcurrentMap<String, StockOrder> store = new ConcurrentHashMap<String, StockOrder>();

    public StockOrderTransactionMapStore() {
		super();
	}

	@Override
    public void store(String key, StockOrder value) {
        store.put(key, value);
    }

    @Override
    public void storeAll(Map<String, StockOrder> map) {
        Set<Map.Entry<String, StockOrder>> entrySet = map.entrySet();
        for (Map.Entry<String, StockOrder> entry : entrySet) {
        	String key = entry.getKey();
        	StockOrder value = entry.getValue();
            store(key, value);
        }
    }

    @Override
    public void delete(String key) {
    }

    @Override
    public void deleteAll(Collection<String> keys) {
    }

    @Override
    public StockOrder load(String key) {
        return store.get(key);
    }

    @Override
    public Map<String, StockOrder> loadAll(Collection<String> keys) {
        Map<String, StockOrder> map = new HashMap<String, StockOrder>();
        for (String key : keys) {
        	StockOrder value = load(key);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public Set<String> loadAllKeys() {
        return store.keySet();
    }
}