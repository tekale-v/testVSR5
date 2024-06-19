/*
 **   QueryDocumentsRequestFailureFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Implementation class
 **
 */
package com.pg.dsm.veeva.vql.request.failure.factory;

import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.json.Response;
import com.pg.dsm.veeva.vql.request.failure.HTTPRequestFailure;
import com.pg.dsm.veeva.vql.request.failure.QueryDocumentsRequestFailure;

public class QueryDocumentsRequestFailureFactory implements HTTPRequestFailureAbstractFactory {
	private String operation; 
	private Exception exception;
	Configurator configurator;
	private Response response;
		
	public QueryDocumentsRequestFailureFactory(String operation, Exception exception, Configurator configurator, Response response) {
		this.operation = operation;
		this.exception = exception;
		this.configurator = configurator;
		this.response = response;
	}

	@Override
	public HTTPRequestFailure createRequestFailure() {
		// TODO Auto-generated method stub
		return new QueryDocumentsRequestFailure(operation, exception, configurator, response);
	}
}
