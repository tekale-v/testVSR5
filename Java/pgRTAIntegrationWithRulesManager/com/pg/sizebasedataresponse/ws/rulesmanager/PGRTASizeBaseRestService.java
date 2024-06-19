package com.pg.sizebasedataresponse.ws.rulesmanager;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/resources/postPOADataForSizeBasedData")
public class PGRTASizeBaseRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTASizeBaseService.class };
	  }
}
