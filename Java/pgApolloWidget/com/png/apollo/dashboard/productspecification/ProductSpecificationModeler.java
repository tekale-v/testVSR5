package com.png.apollo.dashboard.productspecification;

import javax.ws.rs.ApplicationPath;
import com.dassault_systemes.platform.restServices.ModelerBase;

@ApplicationPath("/resources/apollo")
public class ProductSpecificationModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {ProductSpecificationService.class};
	}	
	
}
