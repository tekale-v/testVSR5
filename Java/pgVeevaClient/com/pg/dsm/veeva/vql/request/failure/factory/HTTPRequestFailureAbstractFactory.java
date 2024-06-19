/*
 **   HTTPRequestFailureAbstractFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory class
 **
 */
package com.pg.dsm.veeva.vql.request.failure.factory;

import com.pg.dsm.veeva.vql.request.failure.HTTPRequestFailure;

public interface HTTPRequestFailureAbstractFactory {
	public HTTPRequestFailure createRequestFailure();
}
