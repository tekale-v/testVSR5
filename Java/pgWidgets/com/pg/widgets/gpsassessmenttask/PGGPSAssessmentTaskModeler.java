package com.pg.widgets.gpsassessmenttask;

import com.dassault_systemes.platform.restServices.ModelerBase;

import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pggpsassessmenttask")
public class PGGPSAssessmentTaskModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGGPSAssessmentTaskServices.class};
  }
}
