package com.pg.widgets.fei;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgFEI")
public class PGFEIModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGFEIServices.class};
  }
}
