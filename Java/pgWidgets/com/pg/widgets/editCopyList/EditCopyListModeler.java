package com.pg.widgets.editCopyList;

import com.dassault_systemes.platform.restServices.ModelerBase;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/editCL")

public class EditCopyListModeler extends ModelerBase {
	public Class<?>[] getServices() {
		return new Class[] { EditCLServices.class };
	}
}
