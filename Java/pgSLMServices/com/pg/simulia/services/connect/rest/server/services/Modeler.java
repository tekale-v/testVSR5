package com.pg.simulia.services.connect.rest.server.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/pgslmservices")
public class Modeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
                FcsImage.class,
                SecurityClass.class,
                Session.class,
                Simulation.class,
                SimulationCategory.class,
                SimulationJob.class,
                Simulations.class,
                SimulationTemplate.class,
                SimulationTemplates.class
                };
    }

}

