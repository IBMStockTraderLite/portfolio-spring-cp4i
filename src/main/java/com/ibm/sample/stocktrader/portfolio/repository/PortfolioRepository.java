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

import java.util.List;
import java.util.Optional;

import com.ibm.sample.stocktrader.portfolio.model.Client;
import com.ibm.sample.stocktrader.portfolio.model.Portfolio;
import com.ibm.sample.stocktrader.portfolio.model.PortfolioUpdate;
import com.ibm.sample.stocktrader.portfolio.model.StockQuote;


/* Portfolio Repository interface */
public interface PortfolioRepository {

	  int save(Portfolio portfolio, Client client);
	  int update(Portfolio portfolio);
	  Optional<Portfolio> findById(int id);
	  Optional<Portfolio> findByClientId(String clientId);
	  List<Portfolio> findAll();
	  Optional<Portfolio> tradeEquities(PortfolioUpdate update, List<StockQuote>quotes);

}
