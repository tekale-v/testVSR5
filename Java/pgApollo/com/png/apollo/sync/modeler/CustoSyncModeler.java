/*
 * Added by APOLLO Team
 * For Collaborate with Assembled Product Part - Custom Sync
 */

package com.png.apollo.sync.modeler;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.png.apollo.sync.ebom.GenerateEBOMService;

@ApplicationPath("/resources/custosync")
public class CustoSyncModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {GenerateEBOMService.class};
	}	
	
}
