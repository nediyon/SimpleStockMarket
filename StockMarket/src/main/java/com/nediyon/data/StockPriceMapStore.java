package com.nediyon.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.hazelcast.core.MapStore;
import com.nediyon.models.Stock;
import com.nediyon.models.StockType;

/*
 * Normally this store must connect to a provider such as db, services, etc... to load/store data, in our case
 * we use a ConcurrentMap to read/store data, but we need to initialize given values in initData method
 */
public class StockPriceMapStore implements MapStore<String, Stock> {

	private ConcurrentMap<String, Stock> store = new ConcurrentHashMap<String, Stock>();
	
	 public StockPriceMapStore() {
			super();
			initData();
	}

    private void initData() {
    	store.put("TEA", new Stock("TEA", StockType.COMMON, 0.0, 0.0, 100.0));
    	store.put("POP", new Stock("POP", StockType.COMMON, 8.0, 0.0, 100.0));
    	store.put("ALE", new Stock("ALE", StockType.COMMON, 23.0, 0.0, 60.0));
    	store.put("GIN", new Stock("GIN", StockType.PREFERRED, 8.0, 0.2, 100.0));
    	store.put("JOE", new Stock("JOE", StockType.COMMON, 13.0, 0.0, 250.0));
	}

	@Override
    public void store(String key, Stock value) {
        store.put(key, value);
    }

    @Override
    public void storeAll(Map<String, Stock> map) {
        Set<Map.Entry<String, Stock>> entrySet = map.entrySet();
        for (Map.Entry<String, Stock> entry : entrySet) {
        	String key = entry.getKey();
        	Stock value = entry.getValue();
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
    public Stock load(String key) {
        return store.get(key);
    }

    @Override
    public Map<String, Stock> loadAll(Collection<String> keys) {
        Map<String, Stock> map = new HashMap<String, Stock>();
        for (String key : keys) {
        	Stock value = load(key);
            map.put(key, value);
        }
        return map;
    }

    @Override
    public Set<String> loadAllKeys() {
        return store.keySet();
    }
}