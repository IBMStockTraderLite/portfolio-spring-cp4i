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

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ibm.sample.stocktrader.portfolio.model.SalesforceContact;
/* REST client  for Salesforce flow */
public class SalesforceFlowClient {

  	private String baseUrl;
    private String path;
    private String apiKey;
	  private RestTemplate restTemplate;

  	Logger logger = LoggerFactory.getLogger(SalesforceFlowClient.class);

  	public SalesforceFlowClient(RestTemplate restTemplate, String baseUrl, String path, String apiKey) {
  	    this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.path = path;
        this.apiKey = apiKey;
    }
    
    public SalesforceContact getContact(String contactId) {

        return restTemplate.getForObject(baseUrl + path + "/" + contactId,  SalesforceContact.class);
      
    }

  	public SalesforceContact addContact(SalesforceContact newContact) {
    		HttpHeaders headers = new HttpHeaders();
    		headers.setContentType(MediaType.APPLICATION_JSON);
    		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    		headers.add("X-IBM-Client-Id", apiKey);
    		HttpEntity<SalesforceContact> request = new HttpEntity<>(newContact, headers);
    		ResponseEntity<SalesforceContact> response = restTemplate.postForEntity(baseUrl + path, request, SalesforceContact.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            logger.debug("Request Successful");
        } else {
            logger.debug("Request Failed: " + response.getStatusCode());
        }

    		return response.getBody();

  	}


}
