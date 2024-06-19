package com.pg.designtools.datamanagement;

import java.util.HashMap;
import java.util.Map;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.tops.Dims;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.integrations.tops.Input;
import com.pg.designtools.integrations.tops.Shipper;
import com.pg.designtools.integrations.tops.ShipperInfo;
import com.pg.designtools.integrations.tops.Values;
import matrix.db.AttributeList;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class MasterCustomerUnit extends CommonProductData implements IProductData,IUnitLoadModeler {

	DomainObject domMCUP = null;
	String strMCUPName = DomainConstants.EMPTY_STRING;
	String strMCUPId = DomainConstants.EMPTY_STRING;

	String strInterFaceName="";
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

	String noOfConUnits = "";
	String noOfInterUnits = "";

	StringList slDifferencInAttrValueFound =new StringList();
	StringBuffer sbAlertMessageForDiffFoundInAttrValue = new StringBuffer();
	DataConstants.customTOPSExceptions errorDiffInDBXMLAttrValues = DataConstants.customTOPSExceptions.ERROR_400_ATTR_VALUE_DIFFERENCE_IN_DB_XML_UNITTYPE;

	public MasterCustomerUnit() {
		super();
	}

	public MasterCustomerUnit(Context context) {
		super();
		PRSPContext.set(context);
		setType(DataConstants.SCHEMA_TYPE_PG_MASTERCUSTOMERUNIT);
		setPolicy(DataConstants.SCHEMA_POLICY_EC_PART);
		setRevision(DataConstants.CONSTANT_FIRST_REVISION);
		setAutoNameSeries(DataConstants.CONSTANT_AUTONAME_SERIES_A);
	}

	public MasterCustomerUnit(DomainObject domUnitObject) {
		domMCUP = domUnitObject;
	}

	public DomainObject getRelatedMCUP(DomainObject domTUP) throws FrameworkException {

		MapList mlMCUP = null;
		String strConnectedMCUPId;
		DomainObject domMCUPObj = null;
		StringList busSelects = new StringList(1);
		busSelects.add(DomainConstants.SELECT_ID);

		mlMCUP = domTUP.getRelatedObjects(PRSPContext.get(), // Context
				DomainConstants.RELATIONSHIP_EBOM, // Relationship Pattern
				DataConstants.TYPE_PG_MASTERCUSTOMERUNIT, // Type Pattern
				busSelects, // Object Selects
				null, // Relationship Selects
				false, // get TO
				true, // get From
				(short) 1, // Recurrence Level
				null, // Object Where
				null, // RelationShip Where
				0);

		if (!mlMCUP.isEmpty()) {
			Map<String, String> mpMCUP = (Map) mlMCUP.get(0);
			strConnectedMCUPId = mpMCUP.get(DomainConstants.SELECT_ID);
			domMCUPObj = DomainObject.newInstance(PRSPContext.get(), strConnectedMCUPId);
		}
		return domMCUPObj;
	}

	@Override
	public String createProductData() throws Exception {
		create();
		strMCUPName = getName();
		strMCUPId = getId();
		
		domMCUP = DomainObject.newInstance(PRSPContext.get(), strMCUPId);
		
		//41851 - PnO Ownership Assignment
		setOwnerShip(PRSPContext.get());
		
		applyProductDataTemplate(domMCUP);
		setAttributes();
		return strMCUPName;
	}

	@Override
	public void setAttributes() throws MatrixException{

		HashMap<String, String> attrMCUPMap = new HashMap<>();
		CommonUtility.setIPClassificationClassOnECPart(DataConstants.TYPE_PG_MASTERCUSTOMERUNIT, strMCUPId);

		attrMCUPMap.put(DataConstants.ATTR_RELEASE_PHASE, getReleasePhase());
		attrMCUPMap.put(DataConstants.ATTRIBUTE_LIFECYCLE_STATUS, getLifecycleStatus());
		attrMCUPMap.put(DataConstants.ATTR_V_NAME, strMCUPName);
		attrMCUPMap.put(DataConstants.ATTRIBUTE_ISVPMVISIBLE, "FALSE");

		domMCUP.setAttributeValues(PRSPContext.get(), attrMCUPMap);
	}

	@Override
	public void connect() throws FrameworkException{
		/**/
	}

	@Override
	public DomainObject getDomainObject() {
		return domMCUP;
	}

	@Override
	public String getProductDataRevision() {
		return getRevision();
	}

	@Override
	public void mapAttributes(IMappableComponent outputShipper) throws MatrixException {

		AttributeList mcupMappingAttribute = outputShipper.getMappingAttributes();
		domMCUP.setAttributes(PRSPContext.get(), mcupMappingAttribute);
	}
	
	// Save TOPS XML Logic
	/* This method is wrapper for tags creation in IPS_import xml
	 */
	@Override
	public void processAttributesForXMLTagsCreation(Map mpPart,Input input) throws MatrixException {
		String strMasterCustomerUnitId = getProductDataId(mpPart,DataConstants.CONSTANT_MCUP);
        
		if (UIUtil.isNotNullAndNotEmpty(strMasterCustomerUnitId)) {
			DomainObject domObjMasterCustomerUnit = DomainObject.newInstance(context, strMasterCustomerUnitId);
			Map mapAttributeMap = domObjMasterCustomerUnit.getAttributeMap(context);
			strInterFaceName = getInterfaceName(mapAttributeMap);

			getOuterDimensionlenWidHt(mapAttributeMap);

			if (UIUtil.isNotNullAndNotEmpty(strInterFaceName) && !DataConstants.CONSTANT_NA.equalsIgnoreCase(strInterFaceName)) {
				StringList slInterfaceAttr = getInterfaceAttrList(strInterFaceName);
				HashMap hmAttributeMap = (HashMap) getAttrMapFromInterfaceAttrList(slInterfaceAttr, domObjMasterCustomerUnit);
				getAttributesForDimsTag(hmAttributeMap);
			}
			createTags(input);
		}
		
	}
				
	/* Save TOPS XML*/
	public void setNoOfConUnits(String strNoOfConUnits) {
		noOfConUnits = strNoOfConUnits;	
	}
	/* Save TOPS XML*/
	public void setNoOfInterUnits(String strNoOfInterUnits) {
		noOfInterUnits = strNoOfInterUnits;
		
	}

	/* Save TOPS XML - This method determines the attributes as per interface Name for dims tag creation in IPS_import xml
	 */
	@Override
	public void getAttributesForDimsTag(Map hmAttributeMap) {
		if(DataConstants.INTERFACE_PG_TOPS_DRUM.equals(strInterFaceName))
		{
			diameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_DIAMETER);
		} else if(DataConstants.INTERFACE_PG_TOPS_BUCKET_ROUND.equals(strInterFaceName)){
				topDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOPDIAMETER);
				botDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BOTTOMDIAMETER);
				pitch = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_PITCH);
		} else if(DataConstants.INTERFACE_PG_TOPS_BUCKET_RECTANGULAR.equals(strInterFaceName)){
				topLength = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOPDEPTH);
				topWidth = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_TOPWIDTH);
				botWidth = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BOTTOMWIDTH);
				pitch = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_PITCH);
		} else if(DataConstants.INTERFACE_PG_TOPS_BOTTLE_ROUND.equals(strInterFaceName)){
				bodyDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BODYDIAMETER);
				neckDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKDIAMETER);
				neckHeight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKHEIGHT);
				shHegight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_SHOULDERHEIGHT);
		} else if(DataConstants.INTERFACE_PG_TOPS_BOTTLE_RECTANGULAR.equals(strInterFaceName) || DataConstants.INTERFACE_PG_TOPS_BOTTLE_OVAL.equals(strInterFaceName)){
				bodyLength = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BODYDEPTH);
				bodyWidth = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_BODYWIDTH);
				neckDiameter = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKDIAMETER);
				neckHeight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_NECKHEIGHT);
				shHegight = (Double)hmAttributeMap.get(DataConstants.ATTRIBUTE_PG_SHOULDERHEIGHT);
		} else if(DataConstants.INTERFACE_PG_TOPS_FRA_DIMENSIONS.equals(strInterFaceName)){
				//Pending TOPS type names
		} 	
	}

	/* Save TOPS XML - This method create tags for MCOP in IPS_import xml
	 */
	@Override
	public void createTags(Input input) {
		String strUnitOfMeasure = DataConstants.CONSTANT_ZERO;
		
		if (DataConstants.UOM_MILLIMETER.equals(pgBaseUnitOfMeasure) || (DataConstants.UOM_CENTIMETER.equals(pgBaseUnitOfMeasure)) )
		{
			strUnitOfMeasure = DataConstants.CONSTANT_ONE;
		}
		if(DataConstants.CONSTANT_NA.equalsIgnoreCase(strpgBodyShape))
			strpgBodyShape = "";
		
		Shipper shipper = new Shipper(true, DataConstants.CONSTANT_RSC_FEFCO_0201, "",strUnitOfMeasure, DataConstants.CONSTANT_LETTER_V,DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE);
		Dims dims ;
		if(UIUtil.isNotNullAndNotEmpty(strInterFaceName) && !DataConstants.CONSTANT_NA.equalsIgnoreCase(strInterFaceName)){
			dims = new Dims(DataConstants.CONSTANT_LETTER_O, strpgBodyShape, topDiameter, botDiameter, topLength, topWidth, botWidth, bodyLength, bodyWidth, bodyDiameter, neckDiameter, neckHeight, shHegight, diameter, pitch, topIndent, botIndent, sideIndent,outerDimlen, outerDimwid, outerDimhei, strInterFaceName);
		}else if (outerDimlen > 0)
		{
			shipper = new Shipper(true, DataConstants.CONSTANT_RSC_FEFCO_0201, "",strUnitOfMeasure, DataConstants.CONSTANT_LETTER_F, DataConstants.CONSTANT_ONE);
			 dims = new Dims(DataConstants.CONSTANT_LETTER_O, DataConstants.UOM_MILLIMETER, outerDimlen, outerDimwid, outerDimhei);
		} else {
			dims = new Dims(DataConstants.CONSTANT_LETTER_O, DataConstants.UOM_MILLIMETER, 0.00, 0.00, 0.00);
		}
		shipper.setDims(dims);				
		ShipperInfo shipperInfo = new ShipperInfo("", "", DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO, 0.00, 0.00, 0.00, 0.00, false, DataConstants.CONSTANT_FALSE);
		Values values = null;
		if("".equals(noOfInterUnits)) {
			values = new Values(noOfConUnits, DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO);
		} else {
			values = new Values(noOfInterUnits, DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO,DataConstants.CONSTANT_ZERO);
		}
		shipperInfo.setValues(values);				
		shipper.setInfo(shipperInfo);	
		
		input.setShipper(shipper);		
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
	public void compareAttributesFromXMLAndDB(Map mpAttrValueInXML, String sRELid, String relQtyFromXML) throws FrameworkException {
		
		StringList slAttrNames = new StringList();
		slAttrNames.add("attribute["+DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT+"]");
		slAttrNames.add("attribute["+DataConstants.ATTR_OUTER_DIMENSION_LENGTH+"]");
		slAttrNames.add("attribute["+DataConstants.ATTR_OUTER_DIMENSION_WIDTH+"]");
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCUP compareAttributesFromXMLAndDB slAttrNames "+slAttrNames);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCUP compareAttributesFromXMLAndDB mpAttrValueInXML "+mpAttrValueInXML);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCUP compareAttributesFromXMLAndDB relQtyFromXML "+relQtyFromXML);
		
		Map<String, String> mapExistingAttrValuesInDb = domMCUP.getInfo(context, slAttrNames);
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCUP compareAttributesFromXMLAndDB mapExistingAttrValuesInDb "+mapExistingAttrValuesInDb);
							
		if(!mapExistingAttrValuesInDb.get("attribute["+DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT+"]").equalsIgnoreCase((String) mpAttrValueInXML.get(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT))) {
			slDifferencInAttrValueFound.add(DataConstants.CONSTANT_ATTR_OUTER_DIMENSION_HEIGHT);
		}
		if(!mapExistingAttrValuesInDb.get("attribute["+DataConstants.ATTR_OUTER_DIMENSION_LENGTH+"]").equalsIgnoreCase((String) mpAttrValueInXML.get(DataConstants.ATTR_OUTER_DIMENSION_LENGTH))) {
			slDifferencInAttrValueFound.add(DataConstants.CONSTANT_ATTR_OUTER_DIMENSION_LENGTH);	
		}
		if(!mapExistingAttrValuesInDb.get("attribute["+DataConstants.ATTR_OUTER_DIMENSION_WIDTH+"]").equalsIgnoreCase((String) mpAttrValueInXML.get(DataConstants.ATTR_OUTER_DIMENSION_WIDTH))) {
			slDifferencInAttrValueFound.add(DataConstants.CONSTANT_ATTR_OUTER_DIMENSION_WIDTH);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCUP compareAttributesFromXMLAndDB slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
	}

	@Override
	/**
	 * INC10305877 DTCLD-364 
	 * This method creates a consolidated Alert Message to provide a list of Attributes whose values are different in DB and XML for Released Parts
	 */
	public void createAlertMessageForDiffInAttrValue() {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  MCUP createAlertMessageForDiffInAttrValue slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
		if(!slDifferencInAttrValueFound.isEmpty()) {
			
			String strCommaSeparatedValue = String.join(",", slDifferencInAttrValueFound);
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_NEW_LINE_SLASH_N);
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_DIMENSIONS);
			sbAlertMessageForDiffFoundInAttrValue.append(strCommaSeparatedValue);
			sbAlertMessageForDiffFoundInAttrValue.append(errorDiffInDBXMLAttrValues.getExceptionMessage());
			sbAlertMessageForDiffFoundInAttrValue.append(domMCUP.getName());
			sbAlertMessageForDiffFoundInAttrValue.append(DataConstants.CONSTANT_STRING_SPACE);
			sbAlertMessageForDiffFoundInAttrValue.append(domMCUP.getRevision());
		}	
	}
	
}
