package com.nediyon.models;

import java.io.Serializable;
import java.util.Date;

public class StockOrder implements Serializable {
	
	private static final long serialVersionUID = 2194834020418705529L;

	private String stockSymbol = null;
	
	private Date operationDate = null;
	
	private StockOrderType operationType = null;
	
	private int sharesQuantity = 0;
	
	private double price = 0.0;

	public StockOrder(String stockSymbol, Date operationDate, StockOrderType operationType, int sharesQuantity,
			double price) {
		super();
		this.stockSymbol = stockSymbol;
		this.operationDate = operationDate;
		this.operationType = operationType;
		this.sharesQuantity = sharesQuantity;
		this.price = price;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public StockOrderType getOperationType() {
		return operationType;
	}

	public void setOperationType(StockOrderType operationType) {
		this.operationType = operationType;
	}

	public int getSharesQuantity() {
		return sharesQuantity;
	}

	public void setSharesQuantity(int sharesQuantity) {
		this.sharesQuantity = sharesQuantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

}
