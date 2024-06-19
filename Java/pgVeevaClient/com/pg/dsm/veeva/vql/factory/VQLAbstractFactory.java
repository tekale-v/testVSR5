/*
 **   VQLAbstractFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Interface
 **
 */
package com.pg.dsm.veeva.vql.factory;

import com.pg.dsm.veeva.vql.VQL;

public interface VQLAbstractFactory {
	public VQL getVQL() throws Exception;
}
