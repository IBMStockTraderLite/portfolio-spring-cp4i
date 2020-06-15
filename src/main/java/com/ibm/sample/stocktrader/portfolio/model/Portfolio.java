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
package com.ibm.sample.stocktrader.portfolio.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
/* Data Transfer Object for a Portfolio */
public class Portfolio {

	@JsonProperty("portfolioId")
	private int portfolioId = 0;
	@JsonProperty("clientName")
	private String clientName = null;
	@JsonProperty("total")
	private BigDecimal total = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	@JsonProperty("balance")
	private BigDecimal balance = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	@JsonProperty("commissions")
	private BigDecimal commissions = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	@JsonProperty("freeTrades")
	private int freeTrades = 0;
	@JsonProperty("loyalty")
	private String loyalty = "BRONZE";
	@JsonProperty("roi")
	private BigDecimal roi =  BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);;
	
	
	
	@JsonIgnore
	private Trade lastTrade;
	

	@JsonIgnore
	private String clientId = null;
	
	

	public Portfolio() {

	}
	
	
	public Portfolio(int portfolioId, BigDecimal total, BigDecimal balance, BigDecimal commissions,
			int freeTrades, String loyalty, String clientName) {
		this.portfolioId = portfolioId;
		this.balance = balance;
		this.total = total;
		this.commissions = commissions;
		this.freeTrades = freeTrades;
		this.loyalty = loyalty;
		this.clientName = clientName;
	}
	
	
	
	

	public Trade getLastTrade() {
		return lastTrade;
	}


	public void setLastTrade(Trade lastTrade) {
		this.lastTrade = lastTrade;
	}



	public String getClientName() {
		return clientName;
	}


	public void setClientName(String clientName) {
		this.clientName = clientName;
	}


	public String getLoyalty() {
		return loyalty;
	}

	public void setLoyalty(String loyalty) {
		this.loyalty = loyalty;
	}

	public int getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getCommissions() {
		return commissions;
	}

	public void setCommissions(BigDecimal commissions) {
		this.commissions = commissions;
	}

	public int getFreeTrades() {
		return freeTrades;
	}

	public void setFreeTrades(int freeTrades) {
		this.freeTrades = freeTrades;
	}


	public BigDecimal getRoi() {
		return roi;
	}


	public void setRoi(BigDecimal roi) {
		this.roi = roi;
	}


}
