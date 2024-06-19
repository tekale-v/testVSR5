package com.pg.widgets.learningplan;

import com.dassault_systemes.platform.restServices.ModelerBase;

import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pglearningplan")
public class PGLearningPlanModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGLearningPlanServices.class, PGTestCaseManagementServices.class};
  }
}
