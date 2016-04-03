package com.nediyon.util;

import java.util.Map.Entry;

import com.hazelcast.query.Predicate;
import com.nediyon.models.StockOrder;

@SuppressWarnings("serial")
public class StockPredicate implements Predicate<String, StockOrder> {

    private String symbol;

    public StockPredicate() {
    }

    public StockPredicate(String symbol) {
        this.symbol = symbol;
    }

	@Override
	public boolean apply(Entry<String, StockOrder> mapEntry) {
		return symbol.equals(mapEntry.getValue().getStockSymbol());
	}
}