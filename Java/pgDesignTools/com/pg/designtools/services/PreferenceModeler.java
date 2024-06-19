package com.pg.designtools.services;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/PreferenceInfo")
public class PreferenceModeler extends ModelerBase {

    public Class<?>[] getServices() {
        return new Class[] {
        		PreferenceServices.class};
    }

}