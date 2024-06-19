package com.pg.dsm.preference.controller;

import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/customuserpreferences")
public class CustomUserPreferences extends ModelerBase {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public Class<?>[] getServices() {
        return new Class[]{CopyDataPreferenceRestService.class};
    }
}
