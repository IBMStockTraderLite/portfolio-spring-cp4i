package com.ibm.sample.stocktrader.portfolio.model;

/*
Copyright 2017-2019 IBM Corp All Rights Reserved

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
import com.fasterxml.jackson.annotation.JsonProperty;

/* Data Transfer Object for updating a Portfolio via a trade */
public class PortfolioUpdate {

    @JsonProperty("symbol")
	private String symbol;
    @JsonProperty("shares")
	private int shares;
    @JsonProperty("portfolioId")
	private int portfolioId;
	
	
	public PortfolioUpdate() {
		
	}
	
    public PortfolioUpdate(int portfolioId, String symbol, int shares) {
    	this.portfolioId = portfolioId;
    	this.symbol = symbol;
    	this.shares = shares;
	}
    	

	public int getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getShares() {
		return shares;
	}
	public void setShares(int shares) {
		this.shares = shares;
	}	
	

}
