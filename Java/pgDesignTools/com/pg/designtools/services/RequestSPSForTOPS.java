package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/RequestSPSForTOPS")
public class RequestSPSForTOPS extends ModelerBase {

	public Class<?>[] getServices() {
		return new Class[] { GetSPSForTOPS.class };
	}
}