/*
 **   RenditionVQLFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory bean
 **
 */
package com.pg.dsm.veeva.vql.factory;

import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.RenditionVQL;
import com.pg.dsm.veeva.vql.VQL;
import java.net.URISyntaxException;

public class RenditionVQLFactory implements VQLAbstractFactory {
	Configurator configurator;
	public RenditionVQLFactory(Configurator configurator) {
		this.configurator = configurator;
	}
	@Override
	public VQL getVQL() throws URISyntaxException {
		// TODO Auto-generated method stub
		return new RenditionVQL(configurator);
	}
}