package com.pg.widgets.myprojects;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/myprojects")

public class PGMyProjectsModeler extends ModelerBase {

	public Class<?>[] getServices() {
		return new Class[] { PGMyProjectsServices.class };
	}

}
