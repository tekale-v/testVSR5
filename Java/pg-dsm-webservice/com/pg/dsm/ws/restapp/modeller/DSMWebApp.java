package com.pg.dsm.ws.restapp.modeller;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.pg.dsm.ws.restapp.services.DSMRestService;

@ApplicationPath("/resources/pg/dsm")
public class DSMWebApp extends ModelerBase {
	public Class<?>[] getServices() {
		return new Class[] { DSMRestService.class };
	}
}
