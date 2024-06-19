package com.pg.claimresponse.ws.rulesmanager;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
//Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 42530
@ApplicationPath("/resources/postCEDataForClaim")
public class PGRTAClaimRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTAClaimService.class };
	  }
}

