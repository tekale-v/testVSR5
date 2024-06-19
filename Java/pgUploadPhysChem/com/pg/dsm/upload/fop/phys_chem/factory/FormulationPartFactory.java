/*
 **   FormulationPartFactory.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds factory classes.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.factory;

import com.pg.dsm.upload.fop.phys_chem.services.fop.FormulationPartService;

import java.lang.reflect.InvocationTargetException;

public class FormulationPartFactory {

    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    private FormulationPartFactory() {
    }

    /**
     * Method to get the instance of Formulation Part Service.
     *
     * @return FormulationPartService
     * @throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
     * @since DSM 2018x.5
     */
    public static FormulationPartService getFormulationPartService() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return (FormulationPartService) Class.forName("com.pg.dsm.upload.fop.phys_chem.services.fop.FormulationPartService").getConstructor().newInstance();
    }
}
