/*
Project Name: P&G Artwork Apadtion Plan
Service Name: pgArtworkAdaptionPlanRestService
Purpose: This class is the base class to call the rest service class
*/
package com.pg.rulesengine;
import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath(ModelerBase.REST_BASE_PATH + "/getArtworkData")
public class pgArtworkAdaptionPlanRestService extends ModelerBase {
	@Override
	public Class<?>[] getServices() {
			return new Class[] { pgArtworkAdaptionPlanService.class };
	}
}
