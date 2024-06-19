/*
 **   DocumentsVQLFactory.java
 **   Description - Introduced as part of Veeva integration.      
 **   Factory bean
 **
 */
package com.pg.dsm.veeva.vql.factory;

import java.text.ParseException;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.DocumentsVQL;
import com.pg.dsm.veeva.vql.VQL;

public class DocumentsVQLFactory implements VQLAbstractFactory {

	Configurator configurator;
	public DocumentsVQLFactory(Configurator configurator) {
		this.configurator = configurator;
	}
	@Override
	public VQL getVQL() throws FrameworkException, ParseException {
		// TODO Auto-generated method stub
		return new DocumentsVQL(configurator);
	}
}