/*
 **   HTTPRequestFailureFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory class
 **
 */
package com.pg.dsm.veeva.vql.request.failure.factory;

import com.pg.dsm.veeva.vql.request.failure.HTTPRequestFailure;

public class HTTPRequestFailureFactory {
	public static HTTPRequestFailure getFailure(HTTPRequestFailureAbstractFactory factory) {
		return factory.createRequestFailure();
	}
}
