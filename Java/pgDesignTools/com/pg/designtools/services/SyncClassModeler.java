package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/SyncClassInfo")
public class SyncClassModeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
        		SyncClassServices.class};
    }

}