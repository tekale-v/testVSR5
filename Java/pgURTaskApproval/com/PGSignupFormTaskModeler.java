/*
Project Name: P&G
Class Name: PGSignupFormTaskModeler
Clone From/Reference: N/A
Purpose: This is a modeler class and entry point for the service call from UR task approval widget.
Change History : Added for new functionalities under 2018x.5 release
for Requirement 33490,34528,33491,34529,34530,34531,34532,34533,34535
 */
package com;
import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgsignupformtasks")
public class PGSignupFormTaskModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGSignupFormTaskServices.class};
  }
}