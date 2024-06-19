package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/InterfaceInfo")
public class InterfaceModeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
        		InterfaceServices.class};
    }

}