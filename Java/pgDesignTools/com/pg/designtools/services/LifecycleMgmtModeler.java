package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/LifecycleMgmtInfo")
public class LifecycleMgmtModeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
        		LifecycleMgmtServices.class};
    }

}