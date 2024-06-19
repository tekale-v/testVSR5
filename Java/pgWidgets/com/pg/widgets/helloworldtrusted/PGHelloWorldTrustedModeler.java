package com.pg.widgets.helloworldtrusted;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pghelloworldtrusted")
public class PGHelloWorldTrustedModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGHelloWorldServices.class};
  }
}
