package com.pg.addressdataresponse.ws.rulesmanager;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/resources/postPOADataFoAddressData")
public class PGRTAAddressRestService extends ModelerBase{
	 public Class<?>[] getServices()
	  {
	    return new Class[] { PGRTAAddressService.class };
	  }
}
