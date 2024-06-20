package com.pg.widgets.mydocuments;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgmydocuments")
public class PGMyDocumentsModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGMyDocumentsServices.class};
  }
}
