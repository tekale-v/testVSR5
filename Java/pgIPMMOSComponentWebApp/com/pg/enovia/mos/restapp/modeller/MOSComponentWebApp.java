package com.pg.enovia.mos.restapp.modeller;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.pg.enovia.mos.restapp.services.MosComponentRestService;

@ApplicationPath("/resources/v1/moscomponentmodeller")
public class MOSComponentWebApp extends ModelerBase {
	public Class<?>[] getServices() {
		return new Class[] { MosComponentRestService.class };
	}
}