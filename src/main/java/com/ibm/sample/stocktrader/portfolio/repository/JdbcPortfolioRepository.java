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
package com.ibm.sample.stocktrader.portfolio.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.sample.stocktrader.portfolio.model.Client;
import com.ibm.sample.stocktrader.portfolio.model.Portfolio;
import com.ibm.sample.stocktrader.portfolio.model.PortfolioUpdate;
import com.ibm.sample.stocktrader.portfolio.model.Stock;
import com.ibm.sample.stocktrader.portfolio.model.StockQuote;
import com.ibm.sample.stocktrader.portfolio.model.Trade;
/* Portfolio Repository implementation */
@Repository
public class JdbcPortfolioRepository implements PortfolioRepository {

	Logger logger = LoggerFactory.getLogger(PortfolioRepository.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// Loyalty levels
	private static final String BASIC = "Basic";
	private static final String BRONZE = "Bronze";
	private static final String SILVER = "Silver";
	private static final String GOLD = "Gold";
	private static final String PLATINUM = "Platinum";

	private BigDecimal getCommission(String loyalty) {
		// TODO: turn this into an ODM business rule
		BigDecimal commission = new BigDecimal(9.99);
		if (loyalty != null) {
			if (loyalty.equalsIgnoreCase(BRONZE)) {
				commission = new BigDecimal(8.99).setScale(2, BigDecimal.ROUND_HALF_EVEN);
			} else if (loyalty.equalsIgnoreCase(SILVER)) {
				commission = new BigDecimal(7.99).setScale(2, BigDecimal.ROUND_HALF_EVEN);
			} else if (loyalty.equalsIgnoreCase(GOLD)) {
				commission = new BigDecimal(6.99).setScale(2, BigDecimal.ROUND_HALF_EVEN);
			} else if (loyalty.equalsIgnoreCase(PLATINUM)) {
				commission = new BigDecimal(5.99).setScale(2, BigDecimal.ROUND_HALF_EVEN);
			}
		}

		return commission;
	}

	// Non ODM version - determines loyalty level on its own
	private String processLoyaltyLevel(double overallTotal) {
		String loyalty = null;

		if (overallTotal <= 10000.0)
			loyalty = BASIC.toUpperCase();
		else if ((overallTotal > 10000.0) && (overallTotal <= 50000.0))
			loyalty = BRONZE.toUpperCase();
		else if ((overallTotal > 50000.0) && (overallTotal <= 100000.0))
			loyalty = SILVER.toUpperCase();
		else if ((overallTotal > 100000.0) && (overallTotal <= 1000000.0))
			loyalty = GOLD.toUpperCase();
		else
			loyalty = PLATINUM.toUpperCase();

		return loyalty;
	}


	@Transactional
	@Override
	public int save(Portfolio portfolio, Client client) {
		int first = 0;

		if (client.getContactId() == null)
		   first = jdbcTemplate.update(
				"insert into client (clientId, firstName, lastName, email, preferredContactMethod, phone) values(?,?,?,?,?,?)",
				client.getClientId(), client.getFirstName(), client.getLastName(), client.getEmail(),
				client.getPreferredContactMethod(), client.getPhone());
    else
				first = jdbcTemplate.update(
				 "insert into client (clientId, firstName, lastName, email, preferredContactMethod, phone, salesforceContactId) values(?,?,?,?,?,?,?)",
				 client.getClientId(), client.getFirstName(), client.getLastName(), client.getEmail(),
				 client.getPreferredContactMethod(), client.getPhone(), client.getContactId());
				 
		return jdbcTemplate.update(
				"insert into  portfolio (clientId,total,loyalty,commissions,freeTrades) values(?,?,?,?,?)",
				portfolio.getClientId(), portfolio.getTotal(), portfolio.getLoyalty(), portfolio.getCommissions(),
				portfolio.getFreeTrades()) + first;

	}

	@Override
	public int update(Portfolio portfolio) {
		return jdbcTemplate.update(
				"update portfolio set total = ?,loyalty = ?, balance= ?, commissions = ?, freeTrades = ? where portfolioId = ? and clientId = ?",
				portfolio.getTotal(), portfolio.getLoyalty(), portfolio.getBalance(), portfolio.getCommissions(),
				portfolio.getFreeTrades(), portfolio.getPortfolioId(), portfolio.getClientId());
	}

	@Override
	public Optional<Portfolio> findById(int id) {

		return jdbcTemplate.queryForObject(
				"select p.portfolioId,p.total,p.loyalty,p.balance,p.commissions,p.freeTrades, c.firstName, c.lastName  from portfolio p, client c where c.clientId = p.clientId and  portfolioId = ?",
				new Object[] { id },
				(rs, rowNum) -> Optional.of(new Portfolio(rs.getInt("portfolioId"), rs.getBigDecimal("total"),
						rs.getBigDecimal("balance"), rs.getBigDecimal("commissions"), rs.getInt("freeTrades"),
						rs.getString("loyalty"), rs.getString("firstName") + " " + rs.getString("lastName"))));

	}

	@Override
	public Optional<Portfolio> findByClientId(String clientId) {

		return jdbcTemplate.queryForObject(
				"select p.portfolioId,p.total,p.loyalty,p.balance,p.commissions,p.freeTrades, c.firstName, c.lastName  from portfolio p, client c where c.clientId = p.clientId and  p.clientId = ?",
				new Object[] { clientId },
				(rs, rowNum) -> Optional.of(new Portfolio(rs.getInt("portfolioId"), rs.getBigDecimal("total"),
						rs.getBigDecimal("balance"), rs.getBigDecimal("commissions"), rs.getInt("freeTrades"),
						rs.getString("loyalty"), rs.getString("firstName") + " " + rs.getString("lastName"))));

	}

	@Override
	public List<Portfolio> findAll() {
		return jdbcTemplate.query(
				"select p.portfolioId,p.total,p.loyalty,p.balance,p.commissions,p.freeTrades, c.firstName, c.lastName  from portfolio p, client c where c.clientId = p.clientId order by p.portfolioId",
				(rs, rowNum) -> new Portfolio(rs.getInt("portfolioId"), rs.getBigDecimal("total"),
						rs.getBigDecimal("balance"), rs.getBigDecimal("commissions"), rs.getInt("freeTrades"),
						rs.getString("loyalty"), rs.getString("firstName") + " " + rs.getString("lastName")));
	}


	@Transactional
	@Override
	public Optional<Portfolio> tradeEquities(PortfolioUpdate update, List<StockQuote> quotes) {

		Map<String, BigDecimal> quoteDict = new HashMap<String, BigDecimal>();
		BigDecimal overallTotal = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);

		for (StockQuote quote : quotes) {
			logger.debug("Putting " + quote.getSymbol() + " and " + quote.getPrice() + " in quoteDict");
			quoteDict.put(quote.getSymbol(), quote.getPrice());
		}

		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

		Portfolio currentPortfolio = jdbcTemplate.queryForObject(
				"select p.portfolioId,p.total,p.loyalty,p.balance,p.commissions,p.freeTrades, c.firstName, c.lastName  from portfolio p, client c where c.clientId = p.clientId and  p.portfolioId = ?",
				new Object[] { update.getPortfolioId() },
				(rs, rowNum) -> Optional.of(new Portfolio(rs.getInt("portfolioId"), rs.getBigDecimal("total"),
						rs.getBigDecimal("balance"), rs.getBigDecimal("commissions"), rs.getInt("freeTrades"),
						rs.getString("loyalty"), rs.getString("firstName") + " " + rs.getString("lastName"))))
				.get();

		BigDecimal commission = getCommission(currentPortfolio.getLoyalty());
		BigDecimal balance = currentPortfolio.getBalance().setScale(2, BigDecimal.ROUND_HALF_EVEN);

		int free = currentPortfolio.getFreeTrades();
		if (free > 0) { // use a free trade if available
			free--;
			commission = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_EVEN);

			logger.debug("Using free trade for " + currentPortfolio.getPortfolioId());

			currentPortfolio.setFreeTrades(free);

		} else {
			BigDecimal commissions = currentPortfolio.getCommissions();
			commissions = commissions.add(commission);

			balance = balance.subtract(commission);

			logger.debug(
					"Charging commission of $" + commission + " for Portfolio " + currentPortfolio.getPortfolioId());

			currentPortfolio.setCommissions(commissions.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		}

		List<Stock> stocks = jdbcTemplate.query("select * from stock where portfolioId = ? ",
				new Object[] { update.getPortfolioId() },
				(rs, rowNum) -> new Stock(rs.getInt("portfolioId"), rs.getString("symbol"), rs.getInt("shares"),
						rs.getBigDecimal("price"), rs.getBigDecimal("total"), rs.getBigDecimal("commission"),
						rs.getTimestamp("lastQuoted").getTime()));

		boolean newHolding = true;

		if ((stocks != null) && (stocks.size() > 0)) {
			for (Stock stock : stocks) {
				if (update.getSymbol().equals(stock.getSymbol())) {
					stock.setShares(update.getShares() + stock.getShares());
					stock.setCommission(stock.getCommission().add(commission));
					balance = balance.subtract(quoteDict.get(stock.getSymbol())
							.multiply(new BigDecimal(update.getShares()).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
					newHolding = false;
				}
				stock.setLastQuoted(currentTimestamp.getTime());
				stock.setPrice(quoteDict.get(stock.getSymbol()));
				stock.setTotal(stock.getPrice().multiply(new BigDecimal(stock.getShares())));
				overallTotal = overallTotal.add(stock.getTotal());

				if (stock.getShares() > 0) {
					jdbcTemplate.update(
							"update stock set shares = ?, price = ?, total = ?, commission = ?, lastQuoted = ? where portfolioId = ? and symbol = ?",
							stock.getShares(), stock.getPrice(), stock.getTotal(), stock.getCommission(),
							new Timestamp(stock.getLastQuoted()), stock.getPortfolioId(), stock.getSymbol());

				} else {
					jdbcTemplate.update("delete from  stock where portfolioId = ? and symbol = ?",
							stock.getPortfolioId(), stock.getSymbol());
				}

			}
		}

		if (newHolding) {
			BigDecimal price = quoteDict.get(update.getSymbol());
			BigDecimal total = price.multiply(new BigDecimal(update.getShares()));
			overallTotal = overallTotal.add(total);
			balance = balance.subtract(quoteDict.get(update.getSymbol())
					.multiply(new BigDecimal(update.getShares()).setScale(2, BigDecimal.ROUND_HALF_EVEN)));
			logger.debug("Just before insert symbol is " + update.getSymbol());
			Stock stock = new Stock(update.getPortfolioId(), update.getSymbol(), update.getShares(), price, total,
					commission, currentTimestamp.getTime());
			jdbcTemplate.update(
					"insert into stock (portfolioId, symbol, shares, price, total, commission, lastQuoted) values(?,?,?,?,?,?,?)",
					stock.getPortfolioId(), stock.getSymbol(), stock.getShares(), stock.getPrice(), stock.getTotal(),
					stock.getCommission(), new Timestamp(stock.getLastQuoted()));

		}

		currentPortfolio.setBalance(balance.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		logger.debug("Updating total to " + overallTotal.toString());
		currentPortfolio.setTotal(overallTotal);
		currentPortfolio.setLoyalty(processLoyaltyLevel(overallTotal.doubleValue()));

		Trade lastTrade = new Trade(update.getPortfolioId(), update.getSymbol(), update.getShares(),
				quoteDict.get(update.getSymbol()), commission, currentTimestamp.getTime());
		lastTrade.setTradeId(UUID.randomUUID().toString());
		lastTrade.setTimeOfTrade(currentTimestamp.getTime());
		currentPortfolio.setLastTrade(lastTrade);
		logger.debug("Time stamp = " + currentTimestamp.getTime());
		jdbcTemplate.update(
				"update portfolio set total = ?,loyalty = ?, balance= ?, commissions = ?, freeTrades = ? where portfolioId = ?",
				currentPortfolio.getTotal(), currentPortfolio.getLoyalty(), currentPortfolio.getBalance(),
				currentPortfolio.getCommissions(), currentPortfolio.getFreeTrades(), currentPortfolio.getPortfolioId());

		return Optional.of(currentPortfolio);
	}

}
