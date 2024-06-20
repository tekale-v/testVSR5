package com.pg.widgets.structuredats;

import java.util.Map;
import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/structuredats")
public class PGStructuredATSModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {
			PGStructuredATSServices.class
			};
	}
}
