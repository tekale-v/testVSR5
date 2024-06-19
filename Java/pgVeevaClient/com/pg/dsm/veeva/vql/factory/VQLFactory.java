/*
 **   VQLFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory bean
 **
 */
package com.pg.dsm.veeva.vql.factory;

import com.pg.dsm.veeva.vql.VQL;

public class VQLFactory {
	public static VQL getVQL(VQLAbstractFactory factory) throws Exception {
		return factory.getVQL();
	}
}
