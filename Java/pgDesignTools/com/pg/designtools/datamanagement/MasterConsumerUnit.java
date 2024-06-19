package com.pg.designtools.datamanagement;

import java.util.HashMap;
import java.util.Map;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.tops.Dims;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.integrations.tops.Input;
import com.pg.designtools.integrations.tops.PriPack;
import com.pg.designtools.integrations.tops.PriPackInfo;
import matrix.db.AttributeList;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class MasterConsumerUnit extends CommonProductData implements IProductData,IUnitLoadModeler {

	public MasterConsumerUnit() {
		super();
	}

	public MasterConsumerUnit(Context context) {
		super();
		PRSPContext.set(context);
		setType(DataConstants.SCHEMA_TYPE_PG_MASTERCONSUMERUNIT);
		setPolicy(DataConstants.SCHEMA_POLICY_EC_PART);
		setRevision(DataConstants.CONSTANT_FIRST_REVISION);
		setAutoNameSeries(DataConstants.CONSTANT_AUTONAME_SERIES_A);
	}

	public MasterConsumerUnit(DomainObject domUnitObject) {
		domMCOP = domUnitObject;
	}

	DomainObject domMCOP = null;
	String strMCOPName = DomainConstants.EMPTY_STRING;
	String strMCOPId = DomainConstants.EMPTY_STRING;

	Double topDiameter = 0.0;
	Double botDiameter = 0.0;
	Double topLength = 0.0;
	Double topWidth = 0.0;
	Double botWidth = 0.0;
	Double bodyLength = 0.0;
	Double bodyWidth = 0.0;
	Double bodyDiameter = 0.0;
	Double neckDiameter = 0.0;
	Double neckHeight = 0.0;
	Double shHegight = 0.0;
	Double diameter = 0.0;
	Double pitch = 0.0;
	String topIndent = "";
	String botIndent = "";
	String sideIndent = "";
	String strInterFaceName = "";

	String noOfConUnits = "";

	StringList slDifferencInAttrValueFound =new StringList();
	StringBuffer sbAlertMessageForDiffFoundInAttrValue = new StringBuffer();
	DataConstants.customTOPSExceptions errorDiffInDBXMLAttrValues = DataConstants.customTOPSExceptions.ERROR_400_ATTR_VALUE_DIFFERENCE_IN_DB_XML_UNITTYPE;

	@Override
	public String createProductData() throws Exception{
		create();
		strMCOPName = getName();
		strMCOPId = getId();
		domMCOP = DomainObject.newInstance(PRSPContext.get(), strMCOPId);
		
		//41851 - PnO Ownership Assignment
		setOwnerShip(PRSPContext.get());
		
		applyProductDataTemplate(domMCOP);
		setAttributes();
		return strMCOPName;
	}

	@Override
	public void connect() throws FrameworkException {
		/**/
	}

	@Override
	public void setAttributes() throws MatrixException{
		HashMap<String, String> attrMCOPMap = new HashMap<>();
		CommonUtility.setIPClassificationClassOnECPart(DataConstants.TYPE_PG_MASTERCONSUMERUNIT, strMCOPId);

		attrMCOPMap.put(DataConstants.ATTR_RELEASE_PHASE, getReleasePhase());
		attrMCOPMap.put(DataConstants.ATTRIBUTE_LIFECYCLE_STATUS, getLifecycleStatus());
		attrMCOPMap.put(DataConstants.ATTR_V_NAME, strMCOPName);
		attrMCOPMap.put(DataConstants.ATTRIBUTE_ISVPMVISIBLE, "FALSE");

		domMCOP.setAttributeValues(PRSPContext.get(), attrMCOPMap);
	}

	@Override
	public DomainObject getDomainObject() {
		return domMCOP;
	}

	@Override
	public String getProductDataRevision() {
		return getRevision();
	}

	@Override
	public void mapAttributes(IMappableComponent outputpriPack) throws MatrixException {
		AttributeList mcopMappingAttribute = outputpriPack.getMappingAttributes();
		domMCOP.setAttributes(PRSPContext.get(), mcopMappingAttribute);
	}

	// Save TOPS XML Logic
	/* This method is wrapper for tags creation in IPS_import xml
	 */
	@Override
	public void processAttributesForXMLTagsCreation(Map mpPart,Input input) throws MatrixException {
		String strMasterConsumerUnitId = getProductDataId(mpPart,DataConstants.CONSTANT_MCOP);
		
		Map tempMap = null;
		tempMap = (Map) mpPart.get(DataConstants.CONSTANT_MCOP);
		noOfConUnits = tempMap.get(DataConstants.SELECT_ATTRIBUTE_QUANTITY).toString();
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP processAttributesForXMLTagsCreation noOfConUnits "+noOfConUnits);
			
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processAttributesForXMLTagsCreation strMasterConsumerUnitId "+strMasterConsumerUnitId); 
		if (UIUtil.isNotNullAndNotEmpty(strMasterConsumerUnitId)) {
			DomainObject domObjMasterConsumerUnit = DomainObject.newInstance(context, strMasterConsumerUnitId);
			Map mapAttributeMap = domObjMasterConsumerUnit.getAttributeMap(context);
			strInterFaceName = getInterfaceName(mapAttributeMap);
			getOuterDimensionlenWidHt(mapAttributeMap);

			if (UIUtil.isNotNullAndNotEmpty(strInterFaceName) && !DataConstants.CONSTANT_NA.equalsIgnoreCase(strInterFaceName)) {
				StringList slInterfaceAttr = getInterfaceAttrList(strInterFaceName);
				HashMap hmAttributeMap = (HashMap) getAttrMapFromInterfaceAttrList(slInterfaceAttr, domObjMasterConsumerUnit);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processAttributesForXMLTagsCreation MCOP hmAttributeMap "+hmAttributeMap); 
				getAttributesForDimsTag(hmAttributeMap);
			}
			createTags(input);
		}
	}
	
	/* Save TOPS XML*/
	public String getNoOfConUnits() {	
		return noOfConUnits;
	}				
	

	/* Save TOPS XML - This method determines the attributes as per interface Name for dims tag creation in IPS_import xml
	 */
	@Override
	public void getAttributesForDimsTag(Map hmAttributeMap) {
		if (DataConstants.INTERFACE_PG_TOPS_CAN.equals(strInterFaceName)) {
			diameter = (Double) hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_DIAMETER);
		} else if (DataConstants.INTERFACE_PG_TOPS_TUB_ROUND.equals(strInterFaceName)) {
			topDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOPDIAMETER);
			botDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BOTTOMDIAMETER);
			pitch = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_PITCH);
		} else if (DataConstants.INTERFACE_PG_TOPS_TUB_RECTANGULAR.equals(strInterFaceName)) {
			topLength = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOPDEPTH);
			topWidth = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOPWIDTH);
			botWidth = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BOTTOMWIDTH);
			pitch = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_PITCH);
		} else if (DataConstants.INTERFACE_PG_TOPS_BOTTLE_ROUND.equals(strInterFaceName)) {
			bodyDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BODYDIAMETER);
			neckDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKDIAMETER);
			neckHeight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKHEIGHT);
			shHegight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_SHOULDERHEIGHT);
		} else if (DataConstants.INTERFACE_PG_TOPS_BOTTLE_RECTANGULAR.equals(strInterFaceName)
				|| DataConstants.INTERFACE_PG_TOPS_BOTTLE_OVAL.equals(strInterFaceName)) {
			bodyLength = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BODYDEPTH);
			bodyWidth = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BODYWIDTH);
			neckDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKDIAMETER);
			neckHeight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKHEIGHT);
			shHegight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_SHOULDERHEIGHT);
		} else if (DataConstants.INTERFACE_PG_TOPS_FRA_DIMENSIONS.equals(strInterFaceName)) {
			// Pending TOPS type names
		} else if (DataConstants.INTERFACE_PG_TOPS_BLISTER_PACK.equals(strInterFaceName)) {
			topIndent = (String) hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOP_INDENT);
			botIndent = (String) hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BOTTOM_INDENT);
			sideIndent = (String) hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_SIDE_INDENT);
		}		
	}
	
	/* Save TOPS XML - This method create tags for MCOP in IPS_import xml
	 */
	@Override
	public void createTags(Input input) {
		String strUnitOfMeasure = DataConstants.CONSTANT_ZERO;
	
		if (DataConstants.UOM_MILLIMETER .equals(pgBaseUnitOfMeasure) || (DataConstants.UOM_CENTIMETER.equals(pgBaseUnitOfMeasure)) )
		{
			strUnitOfMeasure = DataConstants.CONSTANT_ONE;
		}
		if(DataConstants.CONSTANT_NA.equalsIgnoreCase(strpgBodyShape))
			strpgBodyShape = "";
		PriPack priPack = new PriPack(true, DataConstants.CONSTANT_CARTON_CAPS, DataConstants.CONSTANT_CARTON,"",strUnitOfMeasure,DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE);
		Dims dims;
		if(UIUtil.isNotNullAndNotEmpty(strInterFaceName) && !DataConstants.CONSTANT_NA.equalsIgnoreCase(strInterFaceName))
		{
			dims = new Dims(DataConstants.CONSTANT_LETTER_O, strpgBodyShape, topDiameter, botDiameter, topLength, topWidth, botWidth, bodyLength, bodyWidth, bodyDiameter, neckDiameter, neckHeight, shHegight, diameter, pitch, topIndent, botIndent, sideIndent, outerDimlen, outerDimwid, outerDimhei, strInterFaceName);
		}else{
			dims = new Dims(DataConstants.CONSTANT_LETTER_O,DataConstants.UOM_MILLIMETER , outerDimlen, outerDimwid, outerDimhei);
		}
		priPack.setDims(dims);				
		PriPackInfo priPackInfo = new PriPackInfo(DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO, DataConstants.CONSTANT_ONE, null, null, 0.0000,0.0000,0.0000,0.0000,"","",0.0000,0.0000,0.0000,"","","","",DataConstants.CONSTANT_FALSE);
		priPack.setInfo(priPackInfo);
		
		input.setPriPack(priPack);
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
	 * @throws FrameworkException
	 */
	public void compareAttributesFromXMLAndDB(Map mpAttrValueInXML,String sRELid,String relQtyFromXML) throws FrameworkException{
		
		StringList slAttrNames = new StringList();
		slAttrNames.add("attribute["+DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT+"]");
		slAttrNames.add("attribute["+DataConstants.ATTR_OUTER_DIMENSION_LENGTH+"]");
		slAttrNames.add("attribute["+DataConstants.ATTR_OUTER_DIMENSION_WIDTH+"]");
	
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP compareAttributesFromXMLAndDB slAttrNames "+slAttrNames);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP compareAttributesFromXMLAndDB mpAttrValueInXML "+mpAttrValueInXML);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP compareAttributesFromXMLAndDB relQtyFromXML "+relQtyFromXML);
		
		Map<String, String> mapExistingAttrValuesInDb = domMCOP.getInfo(context, slAttrNames);
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP compareAttributesFromXMLAndDB mapExistingAttrValuesInDb "+mapExistingAttrValuesInDb);
							
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
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP compareAttributesFromXMLAndDB slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
	}

	@Override
	/**
	 * INC10305877 DTCLD-364 
	 * This method creates a consolidated Alert Message to provide a list of Attributes whose values are different in DB and XML for Released Parts
	 */
	public void createAlertMessageForDiffInAttrValue() {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCOP createAlertMessageForDiffInAttrValue slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
		if(!slDifferencInAttrValueFound.isEmpty()) {
			
			String strCommaSeparatedValue = String.join(",", slDifferencInAttrValueFound);
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_NEW_LINE_SLASH_N);
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_DIMENSIONS);
			sbAlertMessageForDiffFoundInAttrValue.append(strCommaSeparatedValue);
			sbAlertMessageForDiffFoundInAttrValue.append(errorDiffInDBXMLAttrValues.getExceptionMessage());
			sbAlertMessageForDiffFoundInAttrValue.append(domMCOP.getName());
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_STRING_SPACE);
			sbAlertMessageForDiffFoundInAttrValue.append(domMCOP.getRevision());
		}	
	}
}
