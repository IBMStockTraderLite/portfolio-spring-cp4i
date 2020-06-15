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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ibm.sample.stocktrader.portfolio.model.Client;
import com.ibm.sample.stocktrader.portfolio.model.SalesforceContact;
/* Client Repository iimplementation */
@Repository
public class JdbcClientRepository implements ClientRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int save(Client client) {
		if (client.getContactId() == null)
				return jdbcTemplate.update(
					"insert into client (clientId, firstName, lastName, email, phone, preferredContactMethod) values(?,?,?,?,?,?)",
					client.getClientId(), client.getFirstName(), client.getLastName(), client.getEmail(), client.getPhone(),
					client.getPreferredContactMethod());
		else
				return jdbcTemplate.update(
						"insert into client (clientId, firstName, lastName, email, phone, preferredContactMethod, salesforceContactId) values(?,?,?,?,?,?,?)",
						client.getClientId(), client.getFirstName(), client.getLastName(), client.getEmail(), client.getPhone(),
						client.getPreferredContactMethod(), client.getContactId());
	}

	@Override
	public int update(SalesforceContact contact) {
		return jdbcTemplate.update(
			"update client set firstname=?, lastname=?, email=?, phone=? where salesforceContactId=?",
			contact.getFirstName(),contact.getLastName(),contact.getEmail(),contact.getPhone(), contact.getContactId());

	}

	@Override
	public Optional<Client> findById(String id) {
		return jdbcTemplate.queryForObject("select * from client where clientid = ?", new Object[] { id },
				(rs, rowNum) -> Optional.of(new Client(rs.getString("clientid"), rs.getString("firstName"),
						rs.getString("LastName"), rs.getString("email"), rs.getString("phone"), rs.getString("preferredContactMethod"), rs.getString("salesforceContactId")) ));
	}

	@Override
	public Optional<Client> findByPortfolioId(int portfolioId) {
		return jdbcTemplate.queryForObject("select c.email, c.preferredContactMethod, c.phone, c.clientId, c.lastName, c.firstName, c.salesforceContactId from client c, portfolio p  where c.clientid = p.clientId and p.portfolioId = ?", new Object[] { portfolioId},
				(rs, rowNum) -> Optional.of(new Client(rs.getString("clientid"), rs.getString("firstName"),
						rs.getString("LastName"), rs.getString("email"), rs.getString("phone"),rs.getString("preferredContactMethod"), rs.getString("salesforceContactId")) ));
	}

}
