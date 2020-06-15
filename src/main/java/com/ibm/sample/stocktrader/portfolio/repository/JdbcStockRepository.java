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

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ibm.sample.stocktrader.portfolio.model.Stock;

@Repository
public class JdbcStockRepository implements StockRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int update(Stock stock) {
		return jdbcTemplate.update(
				"update stock set shares = ?, price = ?, total = ?, commission = ?, lastQuoted = ? where portfolioId = ? and symbol = ?",
				stock.getShares(), stock.getPrice(), stock.getTotal(), stock.getCommission(), new Timestamp(stock.getLastQuoted()),
				stock.getPortfolioId(), stock.getSymbol());
	}

	@Override
	public int save(Stock stock) {
		return jdbcTemplate.update(
				"insert into stock (portfolioId, symbol, shares, price, total, commission, lastQuoted) values(?,?,?,?,?,?,?)",
				stock.getPortfolioId(), stock.getSymbol(), stock.getShares(), stock.getPrice(), stock.getTotal(),
				stock.getCommission(), new Timestamp(stock.getLastQuoted()));
	}

	@Override
	public List<Stock> findByPortfolio(int portfolioId) {
		return jdbcTemplate.query("select * from stock where portfolioId = ? ", new Object[] { portfolioId },
				(rs, rowNum) -> new Stock(rs.getInt("portfolioId"), rs.getString("symbol"), rs.getInt("shares"),
						rs.getBigDecimal("price"), rs.getBigDecimal("total"), rs.getBigDecimal("commission"),
						rs.getTimestamp("lastQuoted").getTime()));
	}
	
	@Override
	public List<String> findSymbolsByPortfolio(int portfolioId) {
		return jdbcTemplate.query("select symbol from stock where portfolioId = ? ", new Object[] { portfolioId },
				(rs, rowNum) -> new String(rs.getString("symbol")));
	}

}
