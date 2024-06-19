package com.pg.designtools.datamanagement;

import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.ebom.CollaborateEBOMJobFacility;
import com.pg.designtools.integrations.tops.Input;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PKGVPMPart extends CommonProductData implements IProductData {
	
	public PKGVPMPart(Context context) {
		PRSPContext.set(context);
	}
	
	public PKGVPMPart() {
		super();
		getValidParents();
		getValidChild();
	}
	
	StringList slValidChild = new StringList();

	@Override
	public String createProductData() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttributes() throws MatrixException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect() throws FrameworkException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DomainObject getDomainObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProductDataRevision() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processAttributesForXMLTagsCreation(Map newMap, Input input) throws MatrixException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getAttributesForDimsTag(Map hmAttributeMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createTags(Input input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StringList getValidParents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringList getValidChild() {
		if(slValidChild.isEmpty()) {
			slValidChild.add(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART);
			slValidChild.add(DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART);
			slValidChild.add(DataConstants.TYPE_PG_MASTER_PRODUCT_PART);
			slValidChild.add(DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART);
		}
		return slValidChild;
	}

	@Override
	public boolean isValidParent(String strPartType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidChild(String strPartType) {
		return slValidChild.contains(strPartType);
	}
	
	/**
	 * Added method to revise Shape Part from Enovia (A10-1253 Req 47307)
	 * @param context
	 * @param strShapePartObjectId
	 * @return objectId of revised Shape Part
	 * @throws MatrixException
	 */
	public String reviseShapePart(Context context,String strShapePartObjectId) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of PKGVPMPart: reviseShapePart method");
		VPLMIntegTraceUtil.trace(context, ">>> strShapePartObjectId::"+strShapePartObjectId);
		String strNewRevObjectId="";
		if(UIUtil.isNotNullAndNotEmpty(strShapePartObjectId)){
			
			DomainObject doObject=DomainObject.newInstance(context,strShapePartObjectId);
			CollaborateEBOMJobFacility collabEBOM=new CollaborateEBOMJobFacility();
			
			StringList slSelects=collabEBOM.getECPartSelectables();
			slSelects.add("from["+DataConstants.REL_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");
			slSelects.add("from["+DataConstants.REL_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"]."+DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
			
			Map mpObjectInfo=doObject.getInfo(context,slSelects);
			VPLMIntegTraceUtil.trace(context,">>>mpObjectInfo::"+mpObjectInfo);
			
			String strVPMRefID=(String)mpObjectInfo.get("from["+DataConstants.REL_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");
			
			String strIsVPMControlled = (String)mpObjectInfo.get("from["+DataConstants.REL_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"]."+DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
			
			VPLMIntegTraceUtil.trace(context,">>>strVPMRefID::"+strVPMRefID+" strIsVPMControlled::"+strIsVPMControlled);
			
			if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strIsVPMControlled)){
				
				CommonProductData cpd =new CommonProductData();
				cpd.transferControlToEnterprise(context,DomainObject.newInstance(context,strVPMRefID));
			}
			
			strNewRevObjectId=collabEBOM.reviseProductData(context, doObject, mpObjectInfo);
			VPLMIntegTraceUtil.trace(context,">>>strNewRevObjectId::"+strNewRevObjectId);
			
			if(UIUtil.isNotNullAndNotEmpty(strNewRevObjectId)){
				String[] hmArgs=new String[3];
				hmArgs[0]=strShapePartObjectId;
				hmArgs[1]=(String)mpObjectInfo.get(DomainConstants.SELECT_TYPE);
				hmArgs[2]=strNewRevObjectId;
				JPO.invoke(context,"pgDSOCATIAIntegration",null,"autoSyncEnterprisePartToNewCATIAVersion",hmArgs,String.class);
				VPLMIntegTraceUtil.trace(context,">>>VPMReference is revised");
			}
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of reviseShapePart method");
		return strNewRevObjectId;
	}

}
