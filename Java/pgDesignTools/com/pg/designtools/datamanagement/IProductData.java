package com.pg.designtools.datamanagement;

import java.util.Map;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.integrations.tops.Input;
import matrix.util.MatrixException;
import matrix.util.StringList;

public interface IProductData {

	public abstract String createProductData() throws Exception;

	public abstract void setAttributes() throws MatrixException;

	public abstract void connect() throws FrameworkException;

	public abstract DomainObject getDomainObject();

	public abstract String getProductDataRevision();

	public abstract void setRelationship(String relName, String relId);

	// methods to support Parsing handlers
	public abstract void mapAttributes(IMappableComponent parsedNode) throws MatrixException;

	public abstract void processAttributesForXMLTagsCreation(Map newMap,Input input) throws MatrixException ;
	
	public abstract void getAttributesForDimsTag(Map hmAttributeMap);
	
	public abstract void createTags(Input input) ;
	
	//Methods for MPAP,MPMP,MRMP,MPP,ShapePart
	public StringList getValidParents() ;
	
	public StringList getValidChild() ;
	
	public boolean isValidParent(String strPartType) ;
	
	public boolean isValidChild(String strPartType) ;
	

}