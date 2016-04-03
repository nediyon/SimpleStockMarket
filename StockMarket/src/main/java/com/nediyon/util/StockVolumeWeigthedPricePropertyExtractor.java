package com.nediyon.util;

import com.hazelcast.mapreduce.aggregation.PropertyExtractor;
import com.nediyon.models.StockOrder;

@SuppressWarnings("serial")
public class StockVolumeWeigthedPricePropertyExtractor implements PropertyExtractor<StockOrder, Double> {

    @Override
    public Double extract(StockOrder order) {
        return order.getPrice()*order.getSharesQuantity();
    }
}