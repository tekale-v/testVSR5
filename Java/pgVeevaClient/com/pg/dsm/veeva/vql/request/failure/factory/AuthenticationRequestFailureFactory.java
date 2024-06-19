/*
 **   AuthenticationRequestFailureFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory class
 **
 */
package com.pg.dsm.veeva.vql.request.failure.factory;

import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.json.Response;
import com.pg.dsm.veeva.vql.request.failure.AuthenticationRequestFailure;

public class AuthenticationRequestFailureFactory implements HTTPRequestFailureAbstractFactory {
	private String operation; 
	private Exception exception;
	Configurator configurator;
	private Response response;
		
	public AuthenticationRequestFailureFactory(String operation, Exception exception, Configurator configurator, Response response) {
		this.operation = operation;
		this.exception = exception;
		this.configurator = configurator;
		this.response = response;
	}

	@Override
	public AuthenticationRequestFailure createRequestFailure() {
		// TODO Auto-generated method stub
		return new AuthenticationRequestFailure(operation, exception, configurator, response);
	}
}
