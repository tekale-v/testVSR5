/*
 **   FailureViewFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Failure View factory class
 **
 */
package com.pg.dsm.veeva.helper.enovia.failure.factory;

import com.pg.dsm.veeva.helper.enovia.failure.FailureView;

public class FailureViewFactory {
	public static FailureView getFailure(FailureViewAbstractFactory factory) {
		return factory.createFailureView();
	}

}
