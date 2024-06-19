package com.pg.widgets.route;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;


@ApplicationPath("/pgroute")
public class PGRouteModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGRouteServices.class };
  }
}
