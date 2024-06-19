package com.pg.awmxmlintegration;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/resources/postPOADataForXml")
public class PGRTAAwmXmlRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTAAwmXmlService.class };
	  }
}
