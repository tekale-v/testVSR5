package com.pg.widgets.relateddocexplorer;

import com.dassault_systemes.platform.restServices.ModelerBase;

import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgreldocexplorer")
public class PGRelDocExplorerModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGRelDocExplorerServices.class};
  }
}
