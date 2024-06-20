package com.pg.widgets.platformsecurity;

import com.dassault_systemes.platform.restServices.ModelerBase;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/pgplatformsecurity")
public class PGIPSecurityModeler
  extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGIPSecurityServices.class };
  }
}
