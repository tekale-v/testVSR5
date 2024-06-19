/*
 * Added by APOLLO Team
 * Webservice to populate range values in CATIA
 */

package com.png.integ.designtool.modeler;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.png.integ.designtool.getData.UpdateRangeForCATIAAttributes;

@ApplicationPath("/resources/getIntegDesignToolData")
public class DesignToolModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {UpdateRangeForCATIAAttributes.class};
	}

}
