/*
 **   IFormulationPartService.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Formulation Part Services class.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.interfaces.bo;

import java.util.Map;

public interface IFormulationPartService {
    IFormulationPart getFormulationPart(Map<?, ?> map);
}
