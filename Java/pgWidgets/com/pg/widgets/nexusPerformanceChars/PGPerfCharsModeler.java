package com.pg.widgets.nexusPerformanceChars;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

/**
 * Modeler class for Performance Characteristics web services
 * 
 */
@ApplicationPath("/nexusPerformanceChars")
public class PGPerfCharsModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {
			PGPerfCharsServices.class
		};
	}
}
