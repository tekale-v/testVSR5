package com.pg.designtools.datamanagement;

import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.integrations.tops.Input;

import matrix.util.MatrixException;
import matrix.util.StringList;

public class MasterPackagingMaterialPart extends CommonProductData implements IProductData {

	public MasterPackagingMaterialPart() {
		super();
		getValidParents();
		getValidChild();
	}
	
	StringList slValidParents = new StringList();
	
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
		if(slValidParents.isEmpty()) {
			slValidParents.add(DataConstants.TYPE_SHAPE_PART);
			slValidParents.add(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART);
		}
		return slValidParents;
	}

	@Override
	public StringList getValidChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidParent(String strPartType) {
		return slValidParents.contains(strPartType);
	}

	@Override
	public boolean isValidChild(String strPartType) {
		// TODO Auto-generated method stub
		return false;
	}

}