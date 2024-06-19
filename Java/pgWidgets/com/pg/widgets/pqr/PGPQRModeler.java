package com.pg.widgets.pqr;

import com.dassault_systemes.platform.restServices.ModelerBase;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/pgpqr")
public class PGPQRModeler
  extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGPQRServices.class };
  }
}
