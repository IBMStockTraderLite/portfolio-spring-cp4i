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

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ibm.sample.stocktrader.portfolio.model.StockQuote;
/* REST client  for Stock Quote Service */
public class StockQuoteClient {

	private static final String RESOURCE_PATH = "/stock-quote";

	private String REQUEST_URI;
	private RestTemplate restTemplate;

	public StockQuoteClient(RestTemplate restTemplate, String host) {
		this.restTemplate = restTemplate;
		this.REQUEST_URI = host + RESOURCE_PATH;

	}

	public List<StockQuote> getQuote(String symbols) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder
			    .fromUriString(REQUEST_URI)
			    // Add query parameter
			    .queryParam("symbols", symbols);

		ResponseEntity<List<StockQuote>> response = restTemplate.exchange(uriBuilder.toUriString(),HttpMethod.GET,null,new ParameterizedTypeReference<List<StockQuote>>(){});

		List<StockQuote> stockQuotes = response.getBody();

		return stockQuotes;
	}

}
