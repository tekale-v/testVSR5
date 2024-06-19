package com.pg.orchestration;
/*
Project Name: REQ 35895 - Orchestration Phase-1: Orchestration is an integration between PEGA and RTA(Enovia) system
Service Name: pgRTAOrchestrationRestService
Purpose: This class is the base class to call the rest service class
*/
import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

//POA Create Request
@ApplicationPath(ModelerBase.REST_BASE_PATH + "/createPOAData")
public class PgRtaOrchestrationRestService extends ModelerBase {
	@Override
	public Class<?>[] getServices() {
			return new Class[] { PgRtaOrchestrationService.class };
	}

}
