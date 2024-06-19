package com.png.pli.designintl.modeler;

import javax.ws.rs.ApplicationPath;
import matrix.db.*;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.png.pli.designintl.modeler.pgPLIDesignIntlToolService;

@ApplicationPath("/resources/designintl")
public class pgPLIDesignIntlToolModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {pgPLIDesignIntlToolService.class};
	}

}
