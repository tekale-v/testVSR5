package com.pg.widgets.myfavorites;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgfavorites")
public class PGFavoritesModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGFavoritesServices.class};
  }
}
