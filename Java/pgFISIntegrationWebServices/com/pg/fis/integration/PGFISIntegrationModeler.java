package com.pg.fis.integration;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;
import com.pg.fis.integration.common.services.PGFISGenericServices;
import com.pg.fis.integration.formulas.services.PGFISFormulationServices;
import com.pg.fis.integration.material.services.PGFISMaterialServices;
import com.pg.fis.integration.physchemproperties.services.PGFISPhysChemPropertyService;
import com.pg.fis.integration.picklist.services.PGFISPickListServices;
import com.pg.fis.integration.securitymngt.services.PGFISSecurityMngtServices;
import com.pg.fis.integration.user.services.PGFISUserServices;


@ApplicationPath(ModelerBase.REST_BASE_PATH + "/fisintegration")
public class PGFISIntegrationModeler extends ModelerBase {
	@Override
	public Class<?>[] getServices() {
			return new Class[] { PGFISFormulationServices.class, PGFISSecurityMngtServices.class, PGFISPickListServices.class, PGFISMaterialServices.class, PGFISPhysChemPropertyService.class, PGFISUserServices.class, PGFISGenericServices.class };
	}

}