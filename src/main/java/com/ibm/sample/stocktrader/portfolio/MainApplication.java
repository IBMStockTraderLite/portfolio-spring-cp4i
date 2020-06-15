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
package com.ibm.sample.stocktrader.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

/* Main app */
@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		ConfigurableEnvironment environment = new StandardEnvironment();
		SpringApplication springApplication = new SpringApplication(MainApplication.class);
		String eventStreamsState = System.getenv("EVENT_STREAMS_STATE");
		String salesforceIntegrationState = System.getenv("SALESFORCE_INTEGRATION_STATE");
		
		// Always enable default profile
		environment.setActiveProfiles("default");

		// Enable properties for EventStreams if needed
		if ((eventStreamsState != null)  && (eventStreamsState.equals("enabled")))
			 environment.addActiveProfile("kafka");
		

       // Enable properties for Salesforce integration if needed
		if ((salesforceIntegrationState != null)  && (salesforceIntegrationState.equals("enabled")))
		   environment.addActiveProfile("appconnect");

		springApplication.setEnvironment(environment);
		springApplication.run(args);  // run spring boot application
	}

}
