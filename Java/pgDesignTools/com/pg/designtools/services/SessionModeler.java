package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/SessionInfo")
public class SessionModeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
        		SessionServices.class};
    }

}