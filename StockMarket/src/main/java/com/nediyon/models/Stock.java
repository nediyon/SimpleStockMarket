package com.nediyon.models;

import java.io.Serializable;

public class Stock implements Serializable {
	
	private static final long serialVersionUID = -6340903460924700726L;

	private String symbol = null;
	
	private StockType stockType = StockType.COMMON;
	
	private double lastDividend = 0.0;
	
	private double fixedDividend = 0.0;
	
	private double parValue = 0.0;
	
	private double tickerPrice = 0.0;


	public Stock(String symbol, StockType stockType, double lastDividend, double fixedDividend, double parValue) {
		super();
		this.symbol = symbol;
		this.stockType = stockType;
		this.lastDividend = lastDividend;
		this.fixedDividend = fixedDividend;
		this.parValue = parValue;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public StockType getStockType() {
		return stockType;
	}

	public void setStockType(StockType stockType) {
		this.stockType = stockType;
	}

	public double getLastDividend() {
		return lastDividend;
	}

	public void setLastDividend(double lastDividend) {
		this.lastDividend = lastDividend;
	}

	public double getFixedDividend() {
		return fixedDividend;
	}

	public void setFixedDividend(double fixedDividend) {
		this.fixedDividend = fixedDividend;
	}

	public double getParValue() {
		return parValue;
	}

	public void setParValue(double parValue) {
		this.parValue = parValue;
	}

	public double getTickerPrice() {
		return tickerPrice;
	}

	public void setTickerPrice(double tickerPrice) {
		this.tickerPrice = tickerPrice;
	}

}
