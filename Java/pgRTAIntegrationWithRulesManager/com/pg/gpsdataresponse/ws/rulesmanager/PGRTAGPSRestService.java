package com.pg.gpsdataresponse.ws.rulesmanager;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/resources/postPOADataForGPSFixedCes")
public class PGRTAGPSRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTAGPSService.class };
	  }
}
