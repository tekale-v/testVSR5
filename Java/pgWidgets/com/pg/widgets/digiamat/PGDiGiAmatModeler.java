package com.pg.widgets.digiamat;

import java.util.Map;
import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/soamat")
public class PGDiGiAmatModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {
			PGDiGiAmatServices.class
			};
	}
}
