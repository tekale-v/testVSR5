package com.pg.widgets.myreleaseddocuments;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgreleaseddocuments")
public class PGMyReleasedDocumentsModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGMyReleasedDocumentsServices.class};
  }
}
