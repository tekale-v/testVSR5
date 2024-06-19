/*
 **   ArtworkFailureViewFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Failure View factory interface.
 **
 */
package com.pg.dsm.veeva.helper.enovia.failure.factory;

import com.pg.dsm.veeva.helper.enovia.failure.FailureView;

public interface FailureViewAbstractFactory {
	public FailureView createFailureView();
}
