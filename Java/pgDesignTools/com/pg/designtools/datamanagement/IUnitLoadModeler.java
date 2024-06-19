package com.pg.designtools.datamanagement;

import java.util.Map;
import com.matrixone.apps.domain.util.FrameworkException;

public interface IUnitLoadModeler {
	
	public abstract void compareAttributesFromXMLAndDB(Map mpAttrValueInXML,String sRELid,String relQtyFromXML) throws FrameworkException;
	public abstract void createAlertMessageForDiffInAttrValue();
}
