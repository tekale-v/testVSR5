package com.pg.widgets.studyprotocol;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgmystudyprotocol")
public class PGStudyProtocolModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGStudyProtocolServices.class};
  }
}
