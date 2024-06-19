/*
 **   DocumentDataSetVQLFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Document Data Set factory bean
 **
 */
package com.pg.dsm.veeva.vql.factory;

import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.DocumentDataSetVQL;
import com.pg.dsm.veeva.vql.VQL;
import java.net.URISyntaxException;

public class DocumentDataSetVQLFactory implements VQLAbstractFactory {
	Configurator configurator;
	public DocumentDataSetVQLFactory(Configurator configurator) {
		this.configurator = configurator;
	}
	@Override
	public VQL getVQL() throws URISyntaxException {
		// TODO Auto-generated method stub
		return new DocumentDataSetVQL(configurator);
	}
}
