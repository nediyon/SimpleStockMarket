package com.nediyon.market;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.mapreduce.aggregation.Aggregation;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.PropertyExtractor;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.UuidUtil;
import com.nediyon.data.StockOrderTransactionMapStore;
import com.nediyon.data.StockPriceMapStore;
import com.nediyon.models.Stock;
import com.nediyon.models.StockOrder;
import com.nediyon.util.StockPredicate;
import com.nediyon.util.StockShareQuantityPropertyExtractor;
import com.nediyon.util.StockVolumeWeigthedPricePropertyExtractor;

public class SimpleMarketPlace implements EntryAddedListener<String, StockOrder>, EntryEvictedListener<String, StockOrder> {
	
	Logger logger = Logger.getLogger(SimpleMarketPlace.class);
	
	private static SimpleMarketPlace instance = null;
	
	private HazelcastInstance hzInstance = null;
	
	private ExecutorService messageExecutor = null;
	
	private IQueue<StockOrder> stockOrders = null;
	
	private IMap<String, StockOrder> stockTransactions = null;
	
	private IMap<String, Stock> stockPrices = null;
		
    protected SimpleMarketPlace() {
    	
    	Config config = createNewConfig();
    	hzInstance = Hazelcast.newHazelcastInstance( config );
    	
    	//This is our order queue
    	stockOrders = hzInstance.getQueue("stockOrders");
    	
    	//This is our stock map
    	stockPrices = hzInstance.getMap("stockPrices");
    	stockPrices.loadAll(true); //Here we load the predefined data from the specific Map Store StockPriceMapStore
    	
    	//This is our transaction map
    	stockTransactions = hzInstance.getMap("stockTransactions");
    	stockTransactions.addEntryListener(this, true); 
    	stockTransactions.loadAll(true); //Here we load the predefined data from the specific Map Store StockOrderTransactionMapStore
    	   	
    }
    
    public static SimpleMarketPlace getInstance() {
      if(instance == null) {
         instance = new SimpleMarketPlace();
      }
      return instance;
    }
    
    private static Config createNewConfig() {
    	XmlConfigBuilder configBuilder = new XmlConfigBuilder();
        Config config = configBuilder.build();

        StockOrderTransactionMapStore stockTransactionStore = new StockOrderTransactionMapStore();
        MapStoreConfig mapStoreConfig1 = new MapStoreConfig();
        mapStoreConfig1.setImplementation(stockTransactionStore);
        mapStoreConfig1.setWriteDelaySeconds(0);
        MapConfig mapConfig1 = config.getMapConfig("stockTransactions");
        //Stock Order Transaction eviction time 15 min
        //mapConfig1.setTimeToLiveSeconds(900);
        mapConfig1.setMapStoreConfig(mapStoreConfig1);
        
        StockPriceMapStore stockPriceStore = new StockPriceMapStore();
        MapStoreConfig mapStoreConfig2 = new MapStoreConfig();
        mapStoreConfig2.setImplementation(stockPriceStore);
        mapStoreConfig2.setWriteDelaySeconds(0);
        MapConfig mapConfig2 = config.getMapConfig("stockPrices");
        mapConfig2.setMapStoreConfig(mapStoreConfig2);
        
        return config;
    }
    
    public void publishOrder(StockOrder order) throws InterruptedException{
    	stockOrders.put(order);
    	if(messageExecutor == null){
    		//Start the order processing thread
    		startOrderProcessor();
    	}
    }
    
    /**
	 * Calculate the Volume Weighted Stock Price 
	 * 
	 * @return The Volume Weighted Stock Price
	 */
	public Double getVolumeWeightedStockPrice(String symbol) {
		
		// The Predicate to select only specific Stock Order
        Predicate<String, StockOrder> stockPredicate = new StockPredicate(symbol);
        
        // The PropertyExtractor to extract price value from the stock Order transaction
        PropertyExtractor<StockOrder, Double> pricePropertyExtractor = new StockVolumeWeigthedPricePropertyExtractor();		
        // The supplier to handle extracted prices from selected Stock
        Supplier<String, StockOrder, Double> priceExtractor = Supplier.all(pricePropertyExtractor);
        // Predicate and PropertyExtractor (through the previous Supplier) together
        Supplier<String, StockOrder, Double> priceSupplier = Supplier.fromPredicate(stockPredicate, priceExtractor);
        //The aggregation to perform for price
        Aggregation<String, Double, Double> priceAggregation = Aggregations.doubleSum();
        
        Double volumeWeigthedStockPrice = stockTransactions.aggregate(priceSupplier, priceAggregation);
        
        
        // The PropertyExtractor to extract quantity value from the stock Order transaction
        PropertyExtractor<StockOrder, Integer> quantityPropertyExtractor = new StockShareQuantityPropertyExtractor();
        // The supplier to handle extracted quantity from selected Stock
        Supplier<String, StockOrder, Integer> quantityExtractor = Supplier.all(quantityPropertyExtractor);
        // Predicate and PropertyExtractor (through the previous Supplier) together
        Supplier<String, StockOrder, Integer> quantitySupplier = Supplier.fromPredicate(stockPredicate, quantityExtractor);
        //The aggregation to perform for total quantity
        Aggregation<String, Integer, Integer> quantityAggregation = Aggregations.integerSum();
        
        Integer totalQuantity = stockTransactions.aggregate(quantitySupplier, quantityAggregation);

		return volumeWeigthedStockPrice/totalQuantity;
	}
	
	/**
	 * Calculate the dividend based on the ticker price
	 * 
	 * @param symbol The stock symbol
	 * @return The dividend
	 */
	public Double dividend(String symbol) {
		
		Stock stock = stockPrices.get(symbol);
		Double tickerPrice = stock.getTickerPrice();
		if(tickerPrice == 0)
			return 0.0;
		switch(stock.getStockType()) {
			case COMMON:
				if(stock.getLastDividend() == 0)
					return 0.0;
				return stock.getLastDividend()/tickerPrice;
			case PREFERRED:
				return stock.getFixedDividend()*stock.getParValue()/tickerPrice;
			default:
				return 0.0;
		}
	}
	
	/**
	 * Calculate P/E Ratio based on the ticker price
	 * 
	 * @param symbol The stock symbol
	 * @return The P/E Ratio
	 */
	public Double PERatio(String symbol) {
		Stock stock = stockPrices.get(symbol);
		Double tickerPrice = stock.getTickerPrice();
		Double divident = dividend(symbol);
		if(tickerPrice == 0 || divident == 0)
			return 0.0;
		return tickerPrice / divident;
	}
    
    
    /**
	 * Calculate the GBCE All Share Index for all stocks
	 * 
	 * @return The GBCE All Share Index
	 */
	public Double getAllShareIndex() {
		
		Double allShareIndex = 0.0;
		Set<String> symbolSet = stockPrices.keySet();
		int stockSize = symbolSet.size();
		for(String symbol: symbolSet) {
			allShareIndex+=getVolumeWeightedStockPrice(symbol);
		}
		return Math.pow(allShareIndex, 1.0 / stockSize);
	}
	
	
	private void startOrderProcessor() {
		messageExecutor = Executors.newSingleThreadExecutor();
		messageExecutor.execute( new Runnable() {
		      public void run() {
		    	try {
		    		while(true){
						StockOrder orderToProcess = stockOrders.take();
						//Process order
						processStockOrder(orderToProcess);
					}
				} catch (InterruptedException e) {
				}
		      }
		  } );
	}
	
	private void processStockOrder(StockOrder orderToProcess) {
		/*
		 * Stock order is processed here...
		 */
		String idTrx = UuidUtil.newSecureUuidString();
		//Map contains only last 15 minutes transaction, older ones automatically will be evicted.
		long evictionInMiliseconds = 15 * 60 * 1000;
		long time = System.currentTimeMillis() - evictionInMiliseconds;
		long dif = orderToProcess.getOperationDate().getTime()- time;
		
		stockTransactions.put(idTrx, orderToProcess, dif, TimeUnit.MILLISECONDS);
		logger.info("Stock " + orderToProcess.getStockSymbol() + " Order Transaction  Opeartion Type: "+ orderToProcess.getOperationType().toString() + ", Price : "+orderToProcess.getPrice() + ", Quantity : "+ orderToProcess.getSharesQuantity());
		logger.info("Time to live in IMDG : " + dif + " miliseconds");
	}

	@Override
	public void entryAdded(EntryEvent<String, StockOrder> event) {
		//When a stock order is processed and added to transaction map, then we update the ticker price on the stock
		if(event.getValue() !=null){
			String symbol = event.getValue().getStockSymbol();
			Double tickerPrice = event.getValue().getPrice();
			StockTickerPriceEntryProcessor stockTickerPriceEntryProcessor = new StockTickerPriceEntryProcessor(tickerPrice);
			stockPrices.executeOnKey(symbol, stockTickerPriceEntryProcessor);
		}
	}
	
	@Override
	public void entryEvicted(EntryEvent<String, StockOrder> event) {
		logger.info("EVICTION Stock " + event.getOldValue().getStockSymbol() + " Order Transaction  Opeartion Type: "+ event.getOldValue().getOperationType().toString() + " with Price : "+event.getOldValue().getPrice() + " and Quantity : "+ event.getOldValue().getSharesQuantity());
	}
	
	public void shotdown(){
		if(messageExecutor!=null)
			messageExecutor.shutdownNow();	
		if(hzInstance!=null)
			hzInstance.shutdown();
	}
	
	@SuppressWarnings("serial")
	private static class StockTickerPriceEntryProcessor extends AbstractEntryProcessor<String, Stock> {
		
		private Double tickerPrice = 0.0;
		
		public StockTickerPriceEntryProcessor(Double tickerPrice) {
			super();
			this.tickerPrice = tickerPrice;
		}

		@Override
		public Object process(Entry<String, Stock> entry) {
			Stock stock = entry.getValue();
			stock.setTickerPrice(tickerPrice);
			entry.setValue(stock);
			return null;
		}
		
        
    }

	
	
}
