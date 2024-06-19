package com.pg.widgets.apprequest;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/v1/apprequestmanagement")
public class PGAPPRequestModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {PGAPPRequestRestServices.class, PGAPPRequestAPIServices.class};
	}
	
}
