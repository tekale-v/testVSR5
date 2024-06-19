package com.pg.widgets.util;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgutility")
public class PGUtilityModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGUtilityServices.class};
  }
}
