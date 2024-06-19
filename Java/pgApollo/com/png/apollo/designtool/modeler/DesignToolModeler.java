/*
 * Added by APOLLO Team
 * For CATIA Automation Web Services
 */

package com.png.apollo.designtool.modeler;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.png.apollo.designtool.getData.ExtractAPPFORVPMReference;
import com.png.apollo.designtool.getData.ExtractDataForDesignTool;
import com.png.apollo.designtool.getData.LayeredProductCoreMaterialUtility;
import com.png.apollo.designtool.getData.ReadWriteXMLForPLMDTDocument;

@ApplicationPath("/resources/getDesignToolData")
public class DesignToolModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {ExtractDataForDesignTool.class,ExtractAPPFORVPMReference.class, LayeredProductCoreMaterialUtility.class, ReadWriteXMLForPLMDTDocument.class};
	}
	
}
