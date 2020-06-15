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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ibm.sample.stocktrader.portfolio.model.Client;
import com.ibm.sample.stocktrader.portfolio.model.SalesforceContact;
import com.ibm.sample.stocktrader.portfolio.model.Portfolio;
import com.ibm.sample.stocktrader.portfolio.model.PortfolioUpdate;
import com.ibm.sample.stocktrader.portfolio.model.Portfolios;
import com.ibm.sample.stocktrader.portfolio.model.StockQuote;
import com.ibm.sample.stocktrader.portfolio.model.Stocks;
import com.ibm.sample.stocktrader.portfolio.model.Trade;
import com.ibm.sample.stocktrader.portfolio.model.TradeTotal;
import com.ibm.sample.stocktrader.portfolio.repository.JdbcClientRepository;
import com.ibm.sample.stocktrader.portfolio.repository.JdbcPortfolioRepository;
import com.ibm.sample.stocktrader.portfolio.repository.JdbcStockRepository;


@RestController
public class PortfolioRestController {

	@Autowired
	JdbcClientRepository jdbcClientRepository;

	@Autowired
	JdbcStockRepository jdbcStockRepository;

	@Autowired
	JdbcPortfolioRepository jdbcPortfolioRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired(required = false)
	JmsTemplate jmsTemplate;


	Logger logger = LoggerFactory.getLogger(PortfolioRestController.class);

	private StockQuoteClient stockQuoteClient;

	@Value("${app.stockquote.host}")
	private String stockQuoteServiceHost;

	@Value("${app.tradehistory.host}")
	private String tradeHistoryServiceHost;

	@Value("${app.mq.qname:NONE}")
	private String qName;

   @Value("${app.salesforce.integration.flow.url:NONE}")
	private String salesforceIntegrationFlowUrl;

	@Value("${app.salesforce.integration.flow.path:NONE}")
	private String salesforceIntegrationFlowPath;

	@Value("${app.salesforce.integration.flow.apikey:NONE}")
	private String salesforceIntegrationFlowApiKey;

	private TradeHistoryClient tradeHistoryClient = null;

	private SalesforceFlowClient salesforceFlowClient = null;

	private BigDecimal roiAfterTrade(TradeTotal previousTotals, BigDecimal currentPortfolioValue, Trade latest) {

		double roi = 0.0;
		double pfValue = currentPortfolioValue.doubleValue();
		double totalSell = previousTotals.getSell().doubleValue();
		double totalBuy = previousTotals.getBuy().doubleValue();
		double totalCommissions = previousTotals.getCommissions().doubleValue() + latest.getCommission().doubleValue();
		logger.debug("pfValue =" + pfValue);
		logger.debug("totalSell=" + totalSell);
		logger.debug("totalBuy=" + totalBuy);
		logger.debug("shares=" + latest.getShares());
		logger.debug("price =" + latest.getPrice().doubleValue());
		logger.debug("notional=" + latest.getNotional().doubleValue());
		logger.debug("previuos commissions =" + previousTotals.getCommissions().doubleValue() );
		logger.debug("commission =" + latest.getCommission().doubleValue());
        logger.debug("total commissions =" + totalCommissions );

		if (latest.getShares() < 0)
			totalSell += (latest.getNotional().doubleValue() * -1.0);
		else
			totalBuy += latest.getNotional().doubleValue();

		roi = (((pfValue + totalSell) - (totalBuy + totalCommissions)) / (totalBuy + totalCommissions)) * 100.0;
		logger.debug("roi=" + roi); 
		return BigDecimal.valueOf(roi).setScale(2, BigDecimal.ROUND_HALF_EVEN);

	}

	private BigDecimal currentRoi(TradeTotal currentTotals, BigDecimal currentPortfolioValue) {

		double pfValue = currentPortfolioValue.doubleValue();
		double totalSell = currentTotals.getSell().doubleValue();
		double totalBuy = currentTotals.getBuy().doubleValue();
		double totalCommissions = currentTotals.getCommissions().doubleValue();

		if ((totalBuy + totalCommissions) == 0.0) {
			return BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		} else {
			double roi = (((pfValue + totalSell) - (totalBuy + totalCommissions)) / (totalBuy + totalCommissions))
					* 100.0;
			return BigDecimal.valueOf(roi).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}

	}

	@GetMapping("/portfolio/stocks/{id}")
	public Stocks getStocks(@PathVariable String id) {
		int portfolioId = Integer.parseInt(id);
		return new Stocks(jdbcStockRepository.findByPortfolio(portfolioId));
	}

	@GetMapping("/portfolio/portfolios/{id}")
	public Portfolio getPortfolio(@PathVariable String id) {
		int portfolioId = Integer.parseInt(id);
		Portfolio current = jdbcPortfolioRepository.findById(portfolioId).get();
		if (tradeHistoryClient == null) {
			tradeHistoryClient = new TradeHistoryClient(restTemplate, tradeHistoryServiceHost);
		}
		TradeTotal currentTotals = tradeHistoryClient.getTotals(current.getPortfolioId());
		current.setRoi(currentRoi(currentTotals, current.getTotal()));
		return current;
	}

	@GetMapping("/portfolio/portfolios/client/{id}")
	public Client getClient(@PathVariable String id) {
		String salesforceIntegrationState = System.getenv("SALESFORCE_INTEGRATION_STATE");
		int portfolioId = Integer.parseInt(id);
		Client client = jdbcClientRepository.findByPortfolioId(portfolioId).get();

		if ((salesforceIntegrationState.equals("enabled")) && (!client.getContactId().equals("N/A"))) {
			logger.debug("Calling Salesforce integration flow ...");
			if (salesforceFlowClient == null) {
				salesforceFlowClient = new SalesforceFlowClient(restTemplate, salesforceIntegrationFlowUrl, salesforceIntegrationFlowPath, salesforceIntegrationFlowApiKey);
			}
			SalesforceContact contact = salesforceFlowClient.getContact(client.getContactId());
			if (contact != null)  {
			  logger.debug("Found Salesforce contact with contactId=" + client.getContactId());
			  logger.debug("Client phone " + client.getPhone());
			  logger.debug("Contact phone " + contact.getPhone());
               if ((!client.getFirstName().equals(contact.getFirstName()))  || (!client.getLastName().equals(contact.getLastName())) || (!client.getEmail().equals(contact.getEmail())) ||  (!client.getPhone().equals(contact.getPhone()))) {
				  logger.debug("Updating local contact data with new data from Salesforce");
				  jdbcClientRepository.update(contact);
				  client.setFirstName(contact.getFirstName());
				  client.setLastName(contact.getLastName());
				  client.setEmail(contact.getEmail());
				  client.setPhone(contact.getPhone());
			   }
			   else 
			      logger.debug("Client data and contact data identical");
			  
			}
			else 
			   logger.error("Error calling Salesforce integration flow  to get contact with contactId=" + client.getContactId());
	   }
		return client;
	}

	@GetMapping("/portfolio/portfolios")
	public Portfolios getPortfolios() {
		return new Portfolios(jdbcPortfolioRepository.findAll());
	}

	@GetMapping("/portfolio/about")
	public String about() {
		return "Portfolio Service V2.0";
	}

	/*@PutMapping(path = "/portfolio/salesforce/{contactId}", consumes = "application/json", produces = "text/plain")
	public String  syncContact(@RequestBody SalesforceContact contact, @PathVariable String contactId) {
		int rc =  jdbcClientRepository.update(contact);
		logger.info("sync event for " + contactId);
		logger.info("jdbctemplate returns " + rc);
		logger.info("phone number is " + contact.getPhone());
		return "rc = "  + String.valueOf(rc);
	}*/

	@PutMapping(path = "/portfolio/portfolios", consumes = "application/json", produces = "application/json")
	public Portfolio updatePortfolio(@RequestBody PortfolioUpdate update) {

		String symbolsParam = null;
		String eventStreamsState = System.getenv("EVENT_STREAMS_STATE");

		List<String> symbols = jdbcStockRepository.findSymbolsByPortfolio(update.getPortfolioId());
		if ((symbols != null) && (symbols.size() > 0)) {
			StringBuilder strBuilder = new StringBuilder(update.getSymbol());
			for (String symbol : symbols) {
				if (!symbol.equals(update.getSymbol()))
					strBuilder.append("," + symbol);
			}
			symbolsParam = strBuilder.toString();

		} else
			symbolsParam = update.getSymbol();

		logger.debug("Stock quote svc host " + stockQuoteServiceHost);
		logger.debug("symbolsParam is " + symbolsParam);
		stockQuoteClient = new StockQuoteClient(restTemplate, stockQuoteServiceHost);
		List<StockQuote> quotes = stockQuoteClient.getQuote(symbolsParam);
		for (StockQuote quote : quotes) {
			if (!"ok".equals(quote.getStatus())) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, quote.getReason());
			}
		}

		logger.debug("StockQuote services returned " + quotes.size() + " quotes");
		logger.debug("eventStreamsState is " + eventStreamsState);
		Portfolio updatedPortfolio = jdbcPortfolioRepository.tradeEquities(update, quotes).get();
		Trade lastTrade = updatedPortfolio.getLastTrade();
		if (tradeHistoryClient == null) {
			tradeHistoryClient = new TradeHistoryClient(restTemplate, tradeHistoryServiceHost);
		}
		TradeTotal previousTotals = tradeHistoryClient.getTotals(updatedPortfolio.getPortfolioId());
		updatedPortfolio.setRoi(roiAfterTrade(previousTotals, updatedPortfolio.getTotal(), lastTrade));

		if (eventStreamsState.equals("disabled")) {
			logger.debug("Replicating transaction data via Event Streams");
			lastTrade.setTransactionSource("Portfolio Service");
			tradeHistoryClient.storeTradeHistory(updatedPortfolio.getLastTrade());
		} else {
			logger.debug("Replicating transaction data via Event Streams");
			lastTrade.setTransactionSource("Event Streams");
			ObjectMapper mapper = new ObjectMapper();

			try {
				 jmsTemplate.convertAndSend(qName, mapper.writeValueAsString(lastTrade));
				 logger.debug("Transaction sent to MQ queue: " + qName);
			} catch (JmsException e) {
				// TODO Auto-generated
				logger.error("Error sending transacion to local MQ qmgr: " + e.getLocalizedMessage());
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				logger.error("Error sending transaction to local MQ qmgr: " + e.getLocalizedMessage());
			}

		}
		return updatedPortfolio;

	}

	@PostMapping(path = "/portfolio/portfolios", consumes = "application/json", produces = "application/json")
	// return 201 instead of 200
	@ResponseStatus(HttpStatus.CREATED)
	public Portfolio createPortfolio(@RequestBody Client client) {
		String salesforceIntegrationState = System.getenv("SALESFORCE_INTEGRATION_STATE");
		Portfolio portfolio = new Portfolio();
		client.setClientId(UUID.randomUUID().toString());
		portfolio.setClientId(client.getClientId());
		logger.debug("salesforceIntegrationState = " + salesforceIntegrationState);

		if (salesforceIntegrationState.equals("enabled")) {
		 	logger.debug("Calling Salesforce integration flow ...");
			 if (salesforceFlowClient == null) {
				 salesforceFlowClient = new SalesforceFlowClient(restTemplate, salesforceIntegrationFlowUrl, salesforceIntegrationFlowPath, salesforceIntegrationFlowApiKey);
			 }
			 SalesforceContact contact = salesforceFlowClient.addContact(new SalesforceContact(client.getFirstName(), client.getLastName(), client.getEmail(), client.getPhone()));
			 if (contact != null) {
			    client.setContactId(contact.getContactId());
					logger.debug("Salesforce integration flow returned contact Id " + contact.getContactId());
			 }
		}
		jdbcPortfolioRepository.save(portfolio, client);
		return jdbcPortfolioRepository.findByClientId(client.getClientId()).get();
	}

	@GetMapping("/portfolio/readiness")
	public String ready() {
		return "SpringBoot Portfolio microservice ready !";
	}

}
