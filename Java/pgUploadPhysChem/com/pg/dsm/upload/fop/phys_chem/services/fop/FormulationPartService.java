/*
 **   FormulationPartService.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Service class.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.services.fop;

import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPartService;
import com.pg.dsm.upload.fop.phys_chem.models.bo.FormulationPartBean;

import java.util.Map;

public class FormulationPartService implements IFormulationPartService {
    @Override
    public IFormulationPart getFormulationPart(Map<?, ?> map) {
        return FormulationPartBean.getInstance(map);
    }
}
