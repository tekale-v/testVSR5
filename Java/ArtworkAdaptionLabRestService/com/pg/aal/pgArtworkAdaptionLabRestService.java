/*
Project Name: P&G Artwork Apadtion Plan
Service Name: pgArtworkAdaptionPlanRestService
Purpose: This class is the base class to call the rest service class
*/
package com.pg.aal;
import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath(ModelerBase.REST_BASE_PATH + "/postArtworkData")
public class pgArtworkAdaptionLabRestService extends ModelerBase {
	@Override
	public Class<?>[] getServices() {
		return new Class[] { pgArtworkAdaptionLabService.class };
	}
}
