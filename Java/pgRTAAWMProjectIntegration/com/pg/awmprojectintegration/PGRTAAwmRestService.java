package com.pg.awmprojectintegration;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/resources/postPOADataForProjectUpdate")
public class PGRTAAwmRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTAAwmService.class };
	  }
}
