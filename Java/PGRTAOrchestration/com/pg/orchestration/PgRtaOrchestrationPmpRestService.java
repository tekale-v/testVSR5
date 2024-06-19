package com.pg.orchestration;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath(ModelerBase.REST_BASE_PATH + "/connectPMPPOAData")
	public class PgRtaOrchestrationPmpRestService extends ModelerBase {
		@Override
		public Class<?>[] getServices() {
				return new Class[] { PgRtaOrchestrationService.class };
		}
}