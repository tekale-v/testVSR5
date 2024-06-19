package com.pg.excessresponse.ws.rulesmanager;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
//Added by RTA Capgemini Offshore for 18x.6 early-SIT Req 40330
//Added by by RTA Capgemini Offshore for 22x.3 Aug_23_CW Req 46916
@ApplicationPath("/resources/postCEDataForExess")
public class PGRTAExcessRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTAExcessService.class };
	  }
}

