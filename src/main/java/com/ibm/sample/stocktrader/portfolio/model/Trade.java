package com.ibm.sample.stocktrader.portfolio.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
/* Data Transfer Object for a Trade already executed */
public class Trade {
	
	
	@JsonProperty("trade_id")
	private String tradeId;
	@JsonProperty("portfolio_id")
	private int portfolioId;
	@JsonProperty("symbol")
	private String symbol;
	@JsonProperty("shares")
	private int shares;
	@JsonProperty("price")
	private BigDecimal price;
	@JsonProperty("commission")
	private BigDecimal commission;
	@JsonProperty("notional")
	private BigDecimal notional;
	@JsonProperty("xactionsrc")
	private String transactionSource;
	@JsonProperty("xactionts")
	private long  timeOfTrade;
	
	
	public Trade() {
		
	}

	public Trade(int portfolioId, String symbol, int shares, BigDecimal price, BigDecimal commission, long timeOfTrade ) {
		this.portfolioId = portfolioId;
		this.symbol = symbol;
		this.shares = shares;
		this.price = price;
		this.commission = commission.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.notional = (price.multiply(new BigDecimal(shares).setScale(2, BigDecimal.ROUND_HALF_EVEN))).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		this.timeOfTrade = timeOfTrade;
		
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


	public BigDecimal getPrice() {
		return price;
	}


	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	public BigDecimal getCommission() {
		return commission;
	}


	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}


	public BigDecimal getNotional() {
		return notional;
	}


	public void setNotional(BigDecimal notional) {
		this.notional = notional;
	}


	public String getTransactionSource() {
		return transactionSource;
	}


	public void setTransactionSource(String transactionSource) {
		this.transactionSource = transactionSource;
	}


	public long getTimeOfTrade() {
		return timeOfTrade;
	}


	public void setTimeOfTrade(long timeOfTrade) {
		this.timeOfTrade = timeOfTrade;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}
	
	

}
