package com.ibm.sample.stocktrader.portfolio.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/* Data Transfer Object for a list of  Portfolio objects */
public class Portfolios {
	
	@JsonProperty("data")
	private List<Portfolio> portfolios;
	
	public Portfolios() {
		
	}
	
	public Portfolios(List<Portfolio> portfolios) {
	   this.portfolios  = portfolios;
	}

	public List<Portfolio> getPortfolios() {
	   return portfolios;
	}

	public void setPortfolios(List<Portfolio> portfolios) {
	   this.portfolios = portfolios;
	}
	
	

}
