package com.pg.widgets.reportgenerator;

import com.dassault_systemes.platform.restServices.ModelerBase;
/**
 * Modeler class for report generator widget
 * @since 2018x.5
 */
import javax.ws.rs.ApplicationPath;
@ApplicationPath("/pgreportgenerator")
public class PGReportGeneratorModeler extends ModelerBase
{
  public Class<?>[] getServices()
  {
    return new Class[] {PGReportGeneratorServices.class, PGReportTemplateServices.class};
  }
}
