package com.ibm.sample.stocktrader.portfolio.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
/* Data Transfer Object for a list of Stocks */
public class Stocks {
	
	@JsonProperty("data")
	private List<Stock> stocks;
	
	public Stocks() {
		
	}
	
	public Stocks(List<Stock> stocks) {
		this.stocks = stocks;
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}
	
	

}
