package com.pg.widgets.lpdAPP;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;



@ApplicationPath("/pglpdapp")
public class PGLPDAPPModler extends ModelerBase {
	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {PGLPDAPPService.class};
	}
	
}