package com.pg.designtools.services;

import javax.ws.rs.Path;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.pg.designtools.datamanagement.StackingPattern;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

@Path("/GetSPSForTOPS")
public class GetSPSForTOPS extends RestService {

	public GetSPSForTOPS() {
		/*
		 * Currently nothing to be done here
		 */
	}

	@GET
	@Path("/getSPSName")
	public Response getSPSName(@Context HttpServletRequest req) throws Exception {

		String strSPSName = DomainConstants.EMPTY_STRING;
		String strSPSRev = DomainConstants.EMPTY_STRING;

		try {
			matrix.db.Context context = getAuthenticatedContext(req, false);

			// Create SPS
			StackingPattern objSPS = new StackingPattern(context);
			strSPSName = objSPS.createProductData(true);
			strSPSRev = objSPS.getProductDataRevision();

		} catch (DesignToolsIntegrationException e) {
			return Response.status(e.getnErrorCode()).entity(e.getStrErrorMessage()).build();
		}catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strSPSName + "." + strSPSRev).build();
	}

}
