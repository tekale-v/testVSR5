package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/TOPStoPLM")
public class RestVPDTOPStoEnovia extends ModelerBase {

	public Class<?>[] getServices() {
		return new Class[] { VPDTOPStoEnovia.class };
	}
}