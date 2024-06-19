/*
 **   UsersEmailVQLFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory bean
 **
 */
package com.pg.dsm.veeva.vql.factory;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.UsersEmailVQL;
import com.pg.dsm.veeva.vql.VQL;
import java.net.URISyntaxException;
import java.text.ParseException;

public class UsersEmailVQLFactory implements VQLAbstractFactory {
	Configurator configurator;
	public UsersEmailVQLFactory(Configurator configurator) {
		this.configurator = configurator;
	}
	@Override
	public VQL getVQL() throws URISyntaxException, FrameworkException, ParseException {
		// TODO Auto-generated method stub
		return new UsersEmailVQL(configurator);
	}
}