package com.pg.widgets.rtautil;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/rtautil")

public class RTAUtilModeler extends ModelerBase
{
	  public Class<?>[] getServices()
	  {
	    return new Class[] {RTAUtilServices.class};
	  }
}
