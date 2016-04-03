package com.nediyon;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.nediyon.market.SimpleMarketPlace;
import com.nediyon.models.StockOrder;
import com.nediyon.models.StockOrderType;


public class App 
{
	static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args ) throws InterruptedException
    {
        SimpleMarketPlace marketPlace = SimpleMarketPlace.getInstance();
        
        startTrade();
        
        DecimalFormat df = new DecimalFormat("#0.00"); 
        Double divident = marketPlace.dividend("TEA");
        Double per = marketPlace.PERatio("TEA");
        Double price = marketPlace.getVolumeWeightedStockPrice("TEA");
        
        logger.info("Stock                 : TEA");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        divident = marketPlace.dividend("POP");
        per = marketPlace.PERatio("POP");
        price = marketPlace.getVolumeWeightedStockPrice("POP");
        
        logger.info("Stock                 : POP");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        divident = marketPlace.dividend("ALE");
        per = marketPlace.PERatio("ALE");
        price = marketPlace.getVolumeWeightedStockPrice("ALE");
        
        logger.info("Stock                 : ALE");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        divident = marketPlace.dividend("GIN");
        per = marketPlace.PERatio("GIN");
        price = marketPlace.getVolumeWeightedStockPrice("GIN");
        
        logger.info("Stock                 : GIN");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        divident = marketPlace.dividend("JOE");
        per = marketPlace.PERatio("JOE");
        price = marketPlace.getVolumeWeightedStockPrice("JOE");
        
        logger.info("Stock                 : JOE");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        Double GBCE  = marketPlace.getAllShareIndex();
        logger.info(" All Sheare Index : "+ df.format(GBCE));
        logger.info("------------------------------");
        
        logger.info(" We are waiting 10 seconds to see how IMDG evict elements older then 15 minutes then to see new result... ");
        Thread.sleep(10000);
        
        divident = marketPlace.dividend("TEA");
        per = marketPlace.PERatio("TEA");
        price = marketPlace.getVolumeWeightedStockPrice("TEA");
        
        logger.info("Stock                 : TEA");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        divident = marketPlace.dividend("POP");
        per = marketPlace.PERatio("POP");
        price = marketPlace.getVolumeWeightedStockPrice("POP");
        
        logger.info("Stock                 : POP");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        divident = marketPlace.dividend("ALE");
        per = marketPlace.PERatio("ALE");
        price = marketPlace.getVolumeWeightedStockPrice("ALE");
        
        logger.info("Stock                 : ALE");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        divident = marketPlace.dividend("GIN");
        per = marketPlace.PERatio("GIN");
        price = marketPlace.getVolumeWeightedStockPrice("GIN");
        
        logger.info("Stock                 : GIN");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        divident = marketPlace.dividend("JOE");
        per = marketPlace.PERatio("JOE");
        price = marketPlace.getVolumeWeightedStockPrice("JOE");
        
        logger.info("Stock                 : JOE");
        logger.info("Divident Yield        : "+df.format(divident));
        logger.info("P/E Ratio             : "+df.format(per));
        logger.info("Volume Weighted Price : "+df.format(price));
        logger.info("------------------------------");
        
        
        GBCE  = marketPlace.getAllShareIndex();
        logger.info(" All Sheare Index : "+ df.format(GBCE));
        logger.info("------------------------------");
        
        marketPlace.shotdown();
    }

	private static void startTrade() {
		SimpleMarketPlace marketPlace = SimpleMarketPlace.getInstance();	
		try {
			Date now = new Date();
			// Date 14 minutes 58 seconds ago
			Date startTime = new Date(now.getTime() - (15 * 60 * 1000) + (2 * 1000));
			// Date 14 minutes 57 seconds ago
			Date startTime2 = new Date(now.getTime() - (15 * 60 * 1000) + (3 * 1000));
			
			marketPlace.publishOrder(new StockOrder("TEA", startTime, StockOrderType.BUY, 80, 10.50));
			marketPlace.publishOrder(new StockOrder("TEA", startTime2, StockOrderType.SELL, 100, 11.50));
			marketPlace.publishOrder(new StockOrder("TEA", new Date(), StockOrderType.SELL, 70, 12.50));
			marketPlace.publishOrder(new StockOrder("POP", startTime, StockOrderType.BUY, 80, 7.50));
			marketPlace.publishOrder(new StockOrder("TEA", new Date(), StockOrderType.BUY, 400, 10.01));
			marketPlace.publishOrder(new StockOrder("POP", new Date(), StockOrderType.SELL, 50, 6.55));
			marketPlace.publishOrder(new StockOrder("ALE", new Date(), StockOrderType.SELL, 300, 24.05));
			marketPlace.publishOrder(new StockOrder("GIN", startTime2, StockOrderType.BUY, 120, 19.51));
			marketPlace.publishOrder(new StockOrder("GIN", new Date(), StockOrderType.BUY, 40, 18.58));
			marketPlace.publishOrder(new StockOrder("JOE", new Date(), StockOrderType.BUY, 150, 18.50));
			marketPlace.publishOrder(new StockOrder("ALE", startTime, StockOrderType.SELL, 110, 20.50));
			marketPlace.publishOrder(new StockOrder("ALE", new Date(), StockOrderType.BUY, 110, 20.50));
			marketPlace.publishOrder(new StockOrder("GIN", new Date(), StockOrderType.SELL, 70, 12.97));
			marketPlace.publishOrder(new StockOrder("JOE", new Date(), StockOrderType.BUY, 220, 20.53));
			marketPlace.publishOrder(new StockOrder("TEA", new Date(), StockOrderType.BUY, 30, 13.01));
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}
