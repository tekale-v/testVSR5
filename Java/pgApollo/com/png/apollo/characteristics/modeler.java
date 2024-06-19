/*
 * Added by APOLLO Team
 * For Characteristic Related Webservice
 */

package com.png.apollo.characteristics;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("AttributeRangeModeler")
public class modeler extends ModelerBase{

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {RangeService.class, CharcteristicsReferenceDocumentWebService.class, GetRealParameterDisplayIUnits.class, UpdateCharacteristicAttributes.class};
	}
}
