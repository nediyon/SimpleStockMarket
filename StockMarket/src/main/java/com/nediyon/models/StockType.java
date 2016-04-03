package com.nediyon.models;

public enum StockType {
	/**
	 * Common stocks, the dividend yield is calculated with last dividend.
	 */
	COMMON,
	
	/**
	 * Preferred stocks, the dividend yield is calculated with fixed dividend.
	 */
	PREFERRED
}