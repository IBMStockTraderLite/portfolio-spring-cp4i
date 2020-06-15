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
package com.ibm.sample.stocktrader.portfolio.rest;

import org.springframework.web.client.RestTemplate;

import com.ibm.sample.stocktrader.portfolio.model.Trade;
import com.ibm.sample.stocktrader.portfolio.model.TradeTotal;
/* REST client for TradeHistory service */
public class TradeHistoryClient {


  private static final String RESOURCE_PATH = "/trade-history";

	private String BASE_REQUEST_URI;
	private RestTemplate restTemplate;

	public TradeHistoryClient(RestTemplate restTemplate, String host) {
		this.restTemplate = restTemplate;
		this.BASE_REQUEST_URI = host + RESOURCE_PATH;

	}

	public Trade storeTradeHistory(Trade tradeHistory) {

		return restTemplate.postForObject(BASE_REQUEST_URI + "/trade/" + String.valueOf(tradeHistory.getPortfolioId()), tradeHistory, Trade.class);

	}

	public TradeTotal getTotals(int portfolioId) {

		return restTemplate.getForObject(BASE_REQUEST_URI + "/totals/" + String.valueOf(portfolioId),  TradeTotal.class);

	}





}
