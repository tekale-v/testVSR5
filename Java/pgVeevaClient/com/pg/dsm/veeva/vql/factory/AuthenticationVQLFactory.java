/*
 **   AuthenticationVQLFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Authentication factory bean
 **
 */
package com.pg.dsm.veeva.vql.factory;

import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.AuthenticationVQL;
import com.pg.dsm.veeva.vql.VQL;

public class AuthenticationVQLFactory implements VQLAbstractFactory {

	Configurator configurator;
	public AuthenticationVQLFactory(Configurator configurator) {
		this.configurator = configurator;
	}
	@Override
	public VQL getVQL() throws Exception {
		// TODO Auto-generated method stub
		return new AuthenticationVQL(configurator);
	}

}
