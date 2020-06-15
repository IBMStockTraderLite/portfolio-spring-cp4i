package com.ibm.sample.stocktrader.portfolio.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/* Data Transfer Object for a Portfolio summary */
public class TradeTotal {
	
	@JsonProperty("buy")
	private BigDecimal buy;
	@JsonProperty("sell")
	private BigDecimal sell;
	@JsonProperty("commission")
	private BigDecimal commissions;
	@JsonProperty("year")
	private String year = "all";
	@JsonProperty("portfolio_id")
	private int portfolioId;
	
	public BigDecimal getBuy() {
		return buy;
	}
	public void setBuy(BigDecimal buy) {
		this.buy = buy;
	}
	public BigDecimal getSell() {
		return sell;
	}
	public void setSell(BigDecimal sell) {
		this.sell = sell;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getPortfolioId() {
		return portfolioId;
	}
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	public BigDecimal getCommissions() {
		return commissions;
	}
	public void setCommissions(BigDecimal commissions) {
		this.commissions = commissions;
	}
	
	

}
