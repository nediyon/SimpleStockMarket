package com.nediyon.util;

import com.hazelcast.mapreduce.aggregation.PropertyExtractor;
import com.nediyon.models.StockOrder;

@SuppressWarnings("serial")
public class StockShareQuantityPropertyExtractor implements PropertyExtractor<StockOrder, Integer> {

    @Override
    public Integer extract(StockOrder order) {
        return order.getSharesQuantity();
    }
}
