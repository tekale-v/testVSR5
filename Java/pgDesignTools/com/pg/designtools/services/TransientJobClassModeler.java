package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/TransientJobInfo")
public class TransientJobClassModeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
        		TransientJobClassServices.class};
    }

}