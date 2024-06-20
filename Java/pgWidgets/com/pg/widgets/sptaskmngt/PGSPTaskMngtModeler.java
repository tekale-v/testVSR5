package com.pg.widgets.sptaskmngt;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgsptaskmngt")
public class PGSPTaskMngtModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGSPTaskMngtServices.class};
  }
}
