package com.pg.designtools.util;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.util.MatrixException;

public class InterfaceManagement {
	
	public InterfaceManagement() {
		super();
	}
  
    public InterfaceManagement(Context context) {
		PRSPContext.set(context);
	}
    
    /**
     * This method is used to add given interface to the given object
     * @param context
     * @param strObjectId
     * @param strInterfaceName
     * @return 0 (success) /1 (failure)
     * @throws MatrixException
     */
    public int addInterface (Context context, String strObjectId,String strInterfaceName) throws MatrixException
	{
		if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
			if(strInterfaceName.startsWith("interface"))
				strInterfaceName = PropertyUtil.getSchemaProperty(context, strInterfaceName);
			
			if(UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
				DomainObject doObject=DomainObject.newInstance(context,strObjectId);
				doObject.addBusinessInterface(context, new BusinessInterface(strInterfaceName, new Vault(doObject.getVault())));
				return 0;
			}else {
				return 3;
			}
		}
		return 1;
	}
 
    /**
     * This method is used to remove a given interface to the given object
     * @param context
     * @param strObjectId
     * @param strInterfaceName
     * @return 0 (success) /1 (failure)
     * @throws MatrixException
     */
    public int removeInterface (Context context, String strObjectId,String strInterfaceName) throws MatrixException
	{
		if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
			if(strInterfaceName.startsWith("interface"))
				strInterfaceName = PropertyUtil.getSchemaProperty(context, strInterfaceName);
			
			if(UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
				DomainObject doObject=DomainObject.newInstance(context,strObjectId);
				doObject.removeBusinessInterface(context, new BusinessInterface(strInterfaceName, new Vault(doObject.getVault())));
				return 0;
			}else {
				return 3;
			}
		}
		return 1;
	}

    
    /**
     * This method is used to check whether given interface exists on the object
     * @param context
     * @param strObjectId
     * @param strInterfaceName
     * @return boolean
     * @throws MatrixException
     */
    public boolean checkInterfaceOnObject(Context context, String strObjectId, String strInterfaceName) throws MatrixException {
	   	boolean bHasInterface = false;
    	if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
    		if(strInterfaceName.startsWith("interface")) {
				strInterfaceName = PropertyUtil.getSchemaProperty(context, strInterfaceName);
    		}
	    	
	    	if(UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
		
				// get all the interfaces of the object
				BusinessInterfaceList busInterfaces = doObj.getBusinessInterfaces(context);
				
				if(busInterfaces.toString().contains(strInterfaceName))
					bHasInterface = true;
	    	}
	   	}
		return bHasInterface;
	}
    
  
    
    /**
     * Method to update the pngApplicationMonitoring.pngUsageExecutionCount attribute
     * @param context
     * @param strObjectId
     * @throws FrameworkException
     */
    public void updateUsageExecutionCount(Context context,String strObjectId,String strInterfaceName) throws FrameworkException {
    	VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Start of updateUsageExecutionCount method");
    	if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
    		DomainObject doObject=DomainObject.newInstance(context,strObjectId);
    		
    		if (strInterfaceName.startsWith("interface")) {
				strInterfaceName = PropertyUtil.getSchemaProperty(context, strInterfaceName);
			}
    		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strInterfaceName::"+strInterfaceName);
    		if(UIUtil.isNotNullAndNotEmpty(strInterfaceName)) {
	    		String strAttributeName=strInterfaceName+".pngUsageExecutionCount";
	    		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strAttributeName::"+strAttributeName);
	    		
	    		String strAttrValue=doObject.getInfo(context, "attribute["+strAttributeName+"]");
	    		
	    		int iAttrValue=Integer.parseInt(strAttrValue);
	    		iAttrValue=iAttrValue+1;
	    		doObject.setAttributeValue(context, strAttributeName, String.valueOf(iAttrValue));
	    		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>New value set on attribute is:"+iAttrValue);
	   		}
    	}
    }

}
