package com.pg.widgets.taskmanagement;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgtaskmanagement")
public class PGTaskManagementModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGTaskManagementServices.class};
  }
}
