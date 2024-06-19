package com.pg.designtools.datamanagement;

import java.util.HashMap;
import java.util.Map;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.tops.Dims;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.integrations.tops.IPack;
import com.pg.designtools.integrations.tops.IPackInfo;
import com.pg.designtools.integrations.tops.Input;
import com.pg.designtools.integrations.tops.Values;
import matrix.db.AttributeList;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class MasterInnerPackUnit extends CommonProductData implements IProductData,IUnitLoadModeler {

	public MasterInnerPackUnit() {
		super();
	}

	public MasterInnerPackUnit(Context context) {
		super();
		PRSPContext.set(context);
		setType(DataConstants.SCHEMA_TYPE_PG_MASTERINNERPACKUNIT);
		setPolicy(DataConstants.SCHEMA_POLICY_EC_PART);
		setRevision(DataConstants.CONSTANT_FIRST_REVISION);
		setAutoNameSeries(DataConstants.CONSTANT_AUTONAME_SERIES_A);
	}

	public MasterInnerPackUnit(DomainObject domUnitObject) {
		domMIP = domUnitObject;
	}

	public DomainObject getRelatedMIP(DomainObject domMCUP) throws FrameworkException {

		MapList mlMIP = null;
		String strConnectedMIPId;
		DomainObject domMIPObj = null;
		StringList busSelects = new StringList(1);
		busSelects.add(DomainConstants.SELECT_ID);

		mlMIP = domMCUP.getRelatedObjects(PRSPContext.get(), // Context
				DomainConstants.RELATIONSHIP_EBOM, // Relationship Pattern
				DataConstants.TYPE_PG_MASTERINNERPACKUNIT, // Type Pattern
				busSelects, // Object Selects
				null, // Relationship Selects
				false, // get TO
				true, // get From
				(short) 1, // Recurrence Level
				null, // Object Where
				null, // RelationShip Where
				0);

		if (!mlMIP.isEmpty()) {
			Map<String, String> mpMIP = (Map) mlMIP.get(0);
			strConnectedMIPId = mpMIP.get(DomainConstants.SELECT_ID);
			domMIPObj = DomainObject.newInstance(PRSPContext.get(), strConnectedMIPId);
		}
		return domMIPObj;
	}

	DomainObject domMIP = null;
	String strMIPName = DomainConstants.EMPTY_STRING;
	String strMIPId = DomainConstants.EMPTY_STRING;
	String noOfConUnits = "";
	String noOfInterUnits = "";

	StringList slDifferencInAttrValueFound =new StringList();
	StringBuffer sbAlertMessageForDiffFoundInAttrValue = new StringBuffer();
	DataConstants.customTOPSExceptions errorDiffInDBXMLAttrValues = DataConstants.customTOPSExceptions.ERROR_400_ATTR_VALUE_DIFFERENCE_IN_DB_XML_UNITTYPE;

	@Override
	public String createProductData() throws Exception{

		create();
		strMIPName = getName();
		strMIPId = getId();
		domMIP = DomainObject.newInstance(PRSPContext.get(), strMIPId);
		
		//41851 - PnO Ownership Assignment
		setOwnerShip(PRSPContext.get());
		
		applyProductDataTemplate(domMIP);
		setAttributes();
		return strMIPName;
	}

	@Override
	public void setAttributes() throws MatrixException {
		HashMap<String, String> attrMIPMap = new HashMap<>();
		CommonUtility.setIPClassificationClassOnECPart(DataConstants.TYPE_PG_MASTERINNERPACKUNIT, strMIPId);

		attrMIPMap.put(DataConstants.ATTR_RELEASE_PHASE, getReleasePhase());
		attrMIPMap.put(DataConstants.ATTRIBUTE_LIFECYCLE_STATUS, getLifecycleStatus());
		attrMIPMap.put(DataConstants.ATTR_V_NAME, strMIPName);
		attrMIPMap.put(DataConstants.ATTRIBUTE_ISVPMVISIBLE, "FALSE");

		domMIP.setAttributeValues(PRSPContext.get(), attrMIPMap);
	}

	@Override
	public void connect() throws FrameworkException {
		/**/
	}

	@Override
	public DomainObject getDomainObject() {
		return domMIP;
	}

	@Override
	public String getProductDataRevision() {
		return getRevision();
	}

	@Override
	public void mapAttributes(IMappableComponent outputIPack) throws MatrixException {
		AttributeList mipMappingAttribute = outputIPack.getMappingAttributes();
		domMIP.setAttributes(PRSPContext.get(), mipMappingAttribute);
	}
	
	// Save TOPS XML Logic
	/* This method is wrapper for tags creation in IPS_import xml
	*/
	@Override
	public void processAttributesForXMLTagsCreation(Map mpPart, Input input) throws MatrixException {
		String strMasterInnerPackUnitId = getProductDataId(mpPart,DataConstants.CONSTANT_MIP);
        
		Map tempMap = null;
		tempMap = (Map) mpPart.get(DataConstants.CONSTANT_MIP);
		noOfInterUnits = tempMap.get(DataConstants.SELECT_ATTRIBUTE_QUANTITY).toString();
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP processAttributesForXMLTagsCreation noOfInterUnits "+noOfInterUnits);
		
		if (UIUtil.isNotNullAndNotEmpty(strMasterInnerPackUnitId)) {
			DomainObject domObjMasterInnerPackUnit = DomainObject.newInstance(context, strMasterInnerPackUnitId);
			Map mapAttributeMap = domObjMasterInnerPackUnit.getAttributeMap(context);
			pgBaseUnitOfMeasure = (String)mapAttributeMap.get(DataConstants.ATTRIBUTE_PG_DIMENSTION_UOM);

			getOuterDimensionlenWidHt(mapAttributeMap);
			
			createTags(input);
		}	
	}

	/* Save TOPS XML*/
	public void setNoOfConUnits(String strNoOfConUnits) {
		noOfConUnits = strNoOfConUnits;
	}
	/* Save TOPS XML*/
	public String getNoOfInterUnits() {
		return noOfInterUnits;
	}

	@Override
	public void getAttributesForDimsTag(Map hmAttributeMap) {
		// No need to implement 		
	}
	
	/* Save TOPS XML - This method create tags for MCOP in IPS_import xml
	 */
	@Override
	public void createTags(Input input) {
		String strUnitOfMeasure = DataConstants.CONSTANT_ZERO;
		
		if (DataConstants.UOM_MILLIMETER.equals(pgBaseUnitOfMeasure) || (DataConstants.UOM_CENTIMETER.equals(pgBaseUnitOfMeasure)))
		{
			strUnitOfMeasure = DataConstants.CONSTANT_ONE;
		} 
		
		IPack iPack = new IPack(true, DataConstants.CONSTANT_RSC, strUnitOfMeasure,DataConstants.CONSTANT_LETTER_V , DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE);
		if (outerDimlen > 0)
		{
			iPack = new IPack(true, DataConstants.CONSTANT_RSC, strUnitOfMeasure, DataConstants.CONSTANT_LETTER_F, DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE);
		}
		Dims dims = new Dims(DataConstants.CONSTANT_LETTER_O, DataConstants.UOM_MILLIMETER, outerDimlen, outerDimwid, outerDimhei);
		iPack.setDims(dims);
		IPackInfo iPackInfo = new IPackInfo("", "",DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO , 0.0, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, 0.0000, false,DataConstants.CONSTANT_FALSE);						
		Values values = new Values(noOfConUnits, DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO);
		iPackInfo.setValues(values);				
		iPack.setInfo(iPackInfo);	
		input.setiPack(iPack);	
	}

	@Override
	public StringList getValidParents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringList getValidChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidParent(String strPartType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidChild(String strPartType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/**
	 * INC10305877 DTCLD-364 Start
	 * This method compares the attribute values from DB and xml for Released Parts
	 * @param mpAttrValueInXML
	 * @param sRELid
	 * @param relQtyFromXML
	 * @param mipMappingAttribute
	 * @throws FrameworkException
	 */
	public void compareAttributesFromXMLAndDB(Map mpAttrValueInXML, String sRELid, String relQtyFromXML) throws FrameworkException {
		
		StringList slAttrNames = new StringList();
		slAttrNames.add("attribute["+DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT+"]");
		slAttrNames.add("attribute["+DataConstants.ATTR_OUTER_DIMENSION_LENGTH+"]");
		slAttrNames.add("attribute["+DataConstants.ATTR_OUTER_DIMENSION_WIDTH+"]");
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP compareAttributesFromXMLAndDB slAttrNames "+slAttrNames);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP compareAttributesFromXMLAndDB mpAttrValueInXML "+mpAttrValueInXML);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP compareAttributesFromXMLAndDB relQtyFromXML "+relQtyFromXML);
		
		Map<String, String> mapExistingAttrValuesInDb = domMIP.getInfo(context, slAttrNames);
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP compareAttributesFromXMLAndDB mapExistingAttrValuesInDb "+mapExistingAttrValuesInDb);
							
		if(!mapExistingAttrValuesInDb.get("attribute["+DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT+"]").equalsIgnoreCase((String) mpAttrValueInXML.get(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT))) {
			slDifferencInAttrValueFound.add(DataConstants.CONSTANT_ATTR_OUTER_DIMENSION_HEIGHT);
		}
		if(!mapExistingAttrValuesInDb.get("attribute["+DataConstants.ATTR_OUTER_DIMENSION_LENGTH+"]").equalsIgnoreCase((String) mpAttrValueInXML.get(DataConstants.ATTR_OUTER_DIMENSION_LENGTH))) {
			slDifferencInAttrValueFound.add(DataConstants.CONSTANT_ATTR_OUTER_DIMENSION_LENGTH);	
		}
		if(!mapExistingAttrValuesInDb.get("attribute["+DataConstants.ATTR_OUTER_DIMENSION_WIDTH+"]").equalsIgnoreCase((String) mpAttrValueInXML.get(DataConstants.ATTR_OUTER_DIMENSION_WIDTH))) {
			slDifferencInAttrValueFound.add(DataConstants.CONSTANT_ATTR_OUTER_DIMENSION_WIDTH);
		}
		
		if(UIUtil.isNotNullAndNotEmpty(sRELid))
		{
			String strExistingRelQtyInDb = DomainRelationship.getAttributeValue(context, sRELid,DomainConstants.ATTRIBUTE_QUANTITY);
			if(!strExistingRelQtyInDb.equalsIgnoreCase(relQtyFromXML)) {
				slDifferencInAttrValueFound.add(DomainConstants.ATTRIBUTE_QUANTITY);
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP compareAttributesFromXMLAndDB slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
	}

	@Override
	/**
	 * INC10305877 DTCLD-364 
	 * This method creates a consolidated Alert Message to provide a list of Attributes whose values are different in DB and XML for Released Parts
	 */
	public void createAlertMessageForDiffInAttrValue() {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MIP createAlertMessageForDiffInAttrValue slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
		if(!slDifferencInAttrValueFound.isEmpty()) {
			
			String strCommaSeparatedValue = String.join(",", slDifferencInAttrValueFound);
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_NEW_LINE_SLASH_N);
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_DIMENSIONS);
			sbAlertMessageForDiffFoundInAttrValue.append(strCommaSeparatedValue);
			sbAlertMessageForDiffFoundInAttrValue.append(errorDiffInDBXMLAttrValues.getExceptionMessage());
			sbAlertMessageForDiffFoundInAttrValue.append(domMIP.getName());
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_STRING_SPACE);
			sbAlertMessageForDiffFoundInAttrValue.append(domMIP.getRevision());
		}	
	}
}
