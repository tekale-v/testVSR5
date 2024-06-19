package com.pg.designtools.datamanagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.datahandlers.SimpleXML;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.integrations.tops.IPack;
import com.pg.designtools.integrations.tops.Input;
import com.pg.designtools.integrations.tops.PriPack;
import com.pg.designtools.integrations.tops.Shipper;
import com.pg.designtools.integrations.tops.Unitload;
import com.pg.designtools.integrations.tops.UnitloadInfo;
import com.pg.designtools.util.ChangeManagement;
import com.pg.designtools.util.IPManagement;

import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class TransportUnitPart extends CommonProductData implements IProductData,IUnitLoadModeler {

	public TransportUnitPart() {
		super();
	}

	public TransportUnitPart(Context context) {
		super();
		PRSPContext.set(context);
		setType(DataConstants.SCHEMA_TYPE_PG_TRANSPORTUNIT);
		setPolicy(DataConstants.SCHEMA_POLICY_EC_PART);
		setRevision(DataConstants.CONSTANT_FIRST_REVISION);
		setAutoNameSeries(DataConstants.CONSTANT_AUTONAME_SERIES_A);
	}

	DomainObject domTUP = null;
	DomainObject domSPS = null;
	StringList slEnoviaMap = new StringList();

	public TransportUnitPart(DomainObject domSPSObj, DomainObject domExistingTUPObj) {

		domSPS = domSPSObj;
		domTUP = domExistingTUPObj;
	}

	String strTUPId = DomainConstants.EMPTY_STRING;
	String strTUPName = DomainConstants.EMPTY_STRING;
	String strPalletTypeValue ;
	StringList slDifferencInAttrValueFound =new StringList();
	StringBuffer sbAlertMessageForDiffFoundInAttrValue = new StringBuffer();

	public String getSbAlertMessageForDiffFoundInAttrValue() {
		return sbAlertMessageForDiffFoundInAttrValue.toString();
	}

	StringBuffer sbAlertMessageForDiffFoundInAttrValueTUP = new StringBuffer();
	DataConstants.customTOPSExceptions errorDiffInDBXMLAttrValues = DataConstants.customTOPSExceptions.ERROR_400_ATTR_VALUE_DIFFERENCE_IN_DB_XML_UNITTYPE;
	DataConstants.customTOPSExceptions errorLackOfAccess = DataConstants.customTOPSExceptions.ERROR_400_PRODUCT_DATA_ACCESS;
	

	/*
	 * This Method will create the Object and return its Name classModeler is the
	 * object for IProductSpecification. Below Method calls the create() method from
	 * ProductSpecification.java
	 */
	
	@Override
	public String createProductData() throws Exception {
		VPLMIntegTraceUtil.trace(context, ">> START of TUP createProductData method");
		create();
		strTUPId = getId();
		strTUPName = getName();
		domTUP = DomainObject.newInstance(PRSPContext.get(), strTUPId);
		VPLMIntegTraceUtil.trace(context, ">> strTUPName:::"+strTUPName);
		//41851 - PnO Ownership Assignment
		setOwnerShip(PRSPContext.get());
		
		applyProductDataTemplate(domTUP);
		setAttributes();		

		//37884 - Usage Tracking
		String [] newArgs = { "TOPS", "StartedInTOPS", strTUPId };
		JPO.invoke(PRSPContext.get(), "pgDTAutomationMetricTracking", null, "addUsageTrackingToData", newArgs);

		return strTUPName;
	}

	/*
	 * Below method sets the attributes Value on TUP We did not use the method
	 * setAttributes() from productSpecification.java because 1. We need to pass
	 * JSONObject to it 2. We need to call 2 methods setAttributes() and update()
	 * from productSpecification.java just to set the attributes values. hence its
	 * easier and better to use api setAttributeValues in below method itself
	 * instead of creating JSON and invoking 2 methods
	 */
	
	@Override
	public void setAttributes() throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">> START of TUP setAttributes method");
		HashMap<String, String> attrTUPMap = new HashMap<>();

		CommonUtility.setIPClassificationClassOnECPart(DataConstants.TYPE_PG_TRANSPORTUNIT, strTUPId);

		attrTUPMap.put(DataConstants.ATTR_RELEASE_PHASE, getReleasePhase());
		attrTUPMap.put(DataConstants.ATTRIBUTE_LIFECYCLE_STATUS, getLifecycleStatus());
		attrTUPMap.put(DataConstants.ATTR_V_NAME, strTUPName);
		attrTUPMap.put(DataConstants.ATTRIBUTE_ISVPMVISIBLE, "FALSE");

		//DTCLD-777 START
		//Segment
		String strSegment=getSegmentValue();
		if(UIUtil.isNotNullAndNotEmpty(strSegment)) {
			//for segment, we get the object id from preference. Need to get the name of the picklist to be saved on the attribute
			DomainObject doSegment=DomainObject.newInstance(PRSPContext.get(),strSegment);
			attrTUPMap.put(DataConstants.ATTRIBUTE_SEGMENT, doSegment.getInfo(PRSPContext.get(), DomainConstants.SELECT_NAME));
			connectData(domTUP, DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT, strSegment, true);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> Connected Segment with TUP");
		}

		//Primary Organization
		String strPrimaryOrg=getPrimaryOrganization();
		if(UIUtil.isNotNullAndNotEmpty(strPrimaryOrg)) {
			IPManagement ipMgmt=new IPManagement(PRSPContext.get());
			MapList mlData=ipMgmt.findObject(PRSPContext.get(), DataConstants.TYPE_ORG_CHANGE_MGMT, strPrimaryOrg, DataConstants.SEPARATOR_STAR,
					new StringList(DomainConstants.SELECT_ID));
			
			if(null!=mlData && !mlData.isEmpty()) {
				Map mpData=(Map)mlData.get(0);
				connectData(domTUP,DataConstants.REL_PG_PRIMARY_ORGANIZATION,(String)mpData.get(DomainConstants.SELECT_ID),true);
			}
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> Connected Primary Org with TUP");
		} 
		//DTCLD-777 END
		VPLMIntegTraceUtil.trace(context, ">> attrTUPMap::"+attrTUPMap);
		domTUP.setAttributeValues(PRSPContext.get(), attrTUPMap);
		domTUP.setDescription(PRSPContext.get(), strTUPName);
	}

	@Override
	public DomainObject getDomainObject() {
		return domTUP;
	}

	@Override
	public String getProductDataRevision() {
		return null;
	}

	@Override
	public void connect() throws FrameworkException {
		/*
		 * Currently nothing is to be connected to TUP
		 * 
		 */
	}

	public StringList getRelatedStackingPattern() throws FrameworkException {

		return domTUP.getInfoList(PRSPContext.get(),
				"to[" + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + "].from.last.id");
	}

	public void createAndConnectTUPBOMStructure(Shipper inputshipper, Unitload inputUnitload, PriPack inputPriPack,
			IPack inputIPack) throws Exception {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>> TUP: createAndConnectTUPBOMStructure  START ");
		DomainObject domMCUP = null;
		DomainObject domMIP = null;
		DomainObject domMCOP = null;
		MasterConsumerUnit objectMCOP = null;
		MasterInnerPackUnit objectMIP = null;
		MasterCustomerUnit objectMCUP = null;
		TransportUnitPart objectTUP = null;

		VPLMIntegTraceUtil.trace(PRSPContext.get(), "createAndConnectTUPBOMStructure  inputshipper " + inputshipper);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "createAndConnectTUPBOMStructure  inputUnitload " + inputUnitload);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "createAndConnectTUPBOMStructure  inputPriPack " + inputPriPack);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "createAndConnectTUPBOMStructure  inputIPack " + inputIPack);

		if (inputshipper != null && !"NONE".equalsIgnoreCase(inputshipper.getEnoviaMap())) {
			setslEnoviaMapValue(inputshipper.getEnoviaMap());	
		}
		if (inputUnitload != null && !"NONE".equalsIgnoreCase(inputUnitload.getEnoviaMap())) {
			setslEnoviaMapValue(inputUnitload.getEnoviaMap());
		}
		if (inputPriPack != null && !"NONE".equalsIgnoreCase(inputPriPack.getEnoviaMap())) {
			setslEnoviaMapValue(inputPriPack.getEnoviaMap());
		}
		if (inputIPack != null && !"NONE".equalsIgnoreCase(inputIPack.getEnoviaMap())) {
			setslEnoviaMapValue(inputIPack.getEnoviaMap());
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "createAndConnectTUPBOMStructure  slEnoviaMap " + slEnoviaMap);

		// MCOP (Default pripack)
		if (slEnoviaMap.contains(DataConstants.MCOP)) {
			objectMCOP = new MasterConsumerUnit(PRSPContext.get());
			objectMCOP.createProductData();
			domMCOP = objectMCOP.getDomainObject();
		}
		// MCUP (Default Shipper)
		if (slEnoviaMap.contains(DataConstants.MCUP)) {
			objectMCUP = new MasterCustomerUnit(PRSPContext.get());
			objectMCUP.createProductData();
			domMCUP = objectMCUP.getDomainObject();
		}
		// TUP (Default Unitload)
		if (domTUP == null && slEnoviaMap.contains(DataConstants.TUP)) {
				objectTUP = new TransportUnitPart(PRSPContext.get());
				objectTUP.createProductData();
				domTUP = objectTUP.getDomainObject();
		}
		// MIP (Default IPack)
		if (slEnoviaMap.contains(DataConstants.MIP)) {
			objectMIP = new MasterInnerPackUnit(PRSPContext.get());
			objectMIP.createProductData();
			domMIP = objectMIP.getDomainObject();
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>> TUP: createAndConnectTUPBOMStructure Before connect of BOM");

		connectBOMStructure(domMCUP, domMIP, domMCOP);

		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>> TUP: createAndConnectTUPBOMStructure  END ");
	}

	public void setslEnoviaMapValue(String strEnoviaMapValue) {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> setslEnoviaMapValue strEnoviaMapValue "+strEnoviaMapValue);
		if (strEnoviaMapValue.contains("and")) {
			String[] splits = strEnoviaMapValue.split("and");
			String firstElement = splits[0].trim();
			String secondElement = splits[1].trim();
			if (!slEnoviaMap.contains(firstElement)) {
				slEnoviaMap.add(firstElement);
			}
			if (!slEnoviaMap.contains(secondElement)) {
				slEnoviaMap.add(secondElement);
			}
		}else {
			slEnoviaMap.add(strEnoviaMapValue);
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> setslEnoviaMapValue slEnoviaMap "+slEnoviaMap);
	}

	public void connectBOMStructure(DomainObject domMCUP, DomainObject domMIP, DomainObject domMCOP)
			throws FrameworkException {
		context = PRSPContext.get();
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>>>> TUP: connectBOMStructure  START ");
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>> TUP: connectBOMStructure  domTUP " + domTUP);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>> TUP: connectBOMStructure  domMCUP " + domMCUP);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>> TUP: connectBOMStructure  domMIP " + domMIP);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>> TUP: connectBOMStructure  domMCOP " + domMCOP);

		Map<String, String> mpRelAttributes = new HashMap<>();
		mpRelAttributes.put(DataConstants.ATTRIBUTE_FIND_NUMBER, DataConstants.CONSTANT_FIND_NUMBER_ONE);
		if (domTUP != null && domMCUP != null) {
			DomainRelationship domreltupmcup = DomainRelationship.connect(context, domTUP, DomainConstants.RELATIONSHIP_EBOM, domMCUP);
			domreltupmcup.setAttributeValues(context, mpRelAttributes);
		}
		if (domMCUP != null && domMIP != null) {
			DomainRelationship domrelmcupmip = DomainRelationship.connect(context, domMCUP, DomainConstants.RELATIONSHIP_EBOM, domMIP);
			domrelmcupmip.setAttributeValues(context, mpRelAttributes);
		}
		if (domMIP != null && domMCOP != null) {
			DomainRelationship domrelmipmcop = DomainRelationship.connect(context, domMIP, DomainConstants.RELATIONSHIP_EBOM, domMCOP);
			domrelmipmcop.setAttributeValues(context, mpRelAttributes);
		}

		if (domMIP == null && domMCUP != null && domMCOP != null) {
			DomainRelationship domrelmcupmcop = DomainRelationship.connect(context, domMCUP, DomainConstants.RELATIONSHIP_EBOM, domMCOP);
			domrelmcupmcop.setAttributeValues(context, mpRelAttributes);
		}
	
		if (domTUP != null && domSPS != null && UIUtil.isNotNullAndNotEmpty(strTUPId)) {
			DomainRelationship.connect(context, domTUP, DataConstants.REL_PART_SPECIFICATION, domSPS);
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>>>>>>>>> TUP: connectBOMStructure  END ");
	}

	/**
	 * @throws MatrixException
	 * @throws IOException
	 * @throws Exception
	 */
	public void updateAttributesOnTUPBOMWithExtAddlInfo(Map<String, StringList> mapTUPBOMInfo,
			SimpleXML objectSimpleXML) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>> updateAttributesOnTUPBOMWithExtAddlInfo START ");

		for (Map.Entry<String, StringList> entry : mapTUPBOMInfo.entrySet()) {

			String strUnitType = entry.getKey();
			StringList unitTypeObjData = entry.getValue();

			String sTYPE = unitTypeObjData.elementAt(1).trim();
			String sNAME = unitTypeObjData.elementAt(2).trim();
			String sREV = unitTypeObjData.elementAt(3).trim();
			String sVault = unitTypeObjData.elementAt(4).trim();
			String sRELid = "";

			if (unitTypeObjData.size() == 6) {
				sRELid = unitTypeObjData.elementAt(5).trim();
			}
			BusinessObject bo = new BusinessObject(sTYPE, sNAME, sREV, sVault);

			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>> updateAttributesOnTUPBOMWithExtAddlInfo bo " + bo);
			if (bo.exists(PRSPContext.get())) {
				updateAttributesonTUPBOMStructure(bo, objectSimpleXML, strUnitType, sRELid);
			}
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>> updateAttributesOnTUPBOMWithExtAddlInfo END");
	}

	public void updateAttributesonTUPBOMStructure(BusinessObject bo, SimpleXML objectSimpleXML, String strUnitType,
			String sRELid) throws IOException, MatrixException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>> updateAttributesonTUPBOMStructure START ");


		String strBOId=bo.getObjectId(PRSPContext.get());
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "updateAttributesonTUPBOMStructure strBOId:::"+strBOId);
		DomainObject domUnitObject=DomainObject.newInstance(PRSPContext.get(),strBOId);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "updateAttributesonTUPBOMStructure domUnitObject " + domUnitObject);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), "updateAttributesonTUPBOMStructure sRELid " + sRELid);
		
		String strCurrentState = domUnitObject.getInfo(context, DomainConstants.SELECT_CURRENT);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>> updateAttributesonTUPBOMStructure strCurrentState "+strCurrentState);

		try {
		// MCOP
			if (strUnitType.equals(DataConstants.TYPE_PG_MASTERCONSUMERUNIT)) {
				if (objectSimpleXML.getMCOP() != null) {
					Map<String, String> mpMCOPAttr = new HashMap<>();
					mpMCOPAttr.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT,
							objectSimpleXML.getMCOP().getheight());
					mpMCOPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, objectSimpleXML.getMCOP().getdepth());
					mpMCOPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, objectSimpleXML.getMCOP().getwidth());
					
					MasterConsumerUnit objectMCOP = new MasterConsumerUnit(domUnitObject);
					//INC10305877 DTCLD-364 Start	
					if(DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)){

						objectMCOP.compareAttributesFromXMLAndDB(mpMCOPAttr,sRELid,objectSimpleXML.getMCOP().getqty());
						objectMCOP.createAlertMessageForDiffInAttrValue();
						sbAlertMessageForDiffFoundInAttrValue = sbAlertMessageForDiffFoundInAttrValue.append(objectMCOP.sbAlertMessageForDiffFoundInAttrValue);
						VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>> updateAttributesonTUPBOMStructure MCOP sbAlertMessageForDiffFoundInAttrValue "+sbAlertMessageForDiffFoundInAttrValue);
					}
					//INC10305877 DTCLD-364 End
					if(!DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)){
					domUnitObject.setAttributeValues(PRSPContext.get(), mpMCOPAttr);

					if (UIUtil.isNotNullAndNotEmpty(sRELid)) {	
						DomainRelationship.setAttributeValue(PRSPContext.get(), sRELid,
								DomainConstants.ATTRIBUTE_QUANTITY, objectSimpleXML.getMCOP().getqty());
					}
						if(objectSimpleXML.getOutputPriPack() != null) {
							objectSimpleXML.getOutputPriPack().processMappingAttributes();	
							objectMCOP.mapAttributes(objectSimpleXML.getOutputPriPack());
						}
				}
				}
			}
			// Master Inner Pack
			else if (strUnitType.equals(DataConstants.TYPE_PG_MASTERINNERPACKUNIT)) {
				if (objectSimpleXML.getMIP() != null) {
					Map<String, String> mpMIPAttr = new HashMap<>();
					mpMIPAttr.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT,
							objectSimpleXML.getMIP().getheight());
					mpMIPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, objectSimpleXML.getMIP().getdepth());
					mpMIPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, objectSimpleXML.getMIP().getwidth());
					
					MasterInnerPackUnit objectMIP = new MasterInnerPackUnit(domUnitObject);
					//INC10305877 DTCLD-364 Start
					if(DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)){
						
						objectMIP.compareAttributesFromXMLAndDB(mpMIPAttr,sRELid,objectSimpleXML.getMIP().getqty());
						objectMIP.createAlertMessageForDiffInAttrValue();
						sbAlertMessageForDiffFoundInAttrValue = sbAlertMessageForDiffFoundInAttrValue.append(objectMIP.sbAlertMessageForDiffFoundInAttrValue);
						VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>> updateAttributesonTUPBOMStructure MIP sbAlertMessageForDiffFoundInAttrValue "+sbAlertMessageForDiffFoundInAttrValue);
					}
					//INC10305877 DTCLD-364 End

					if(!DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)) {
					domUnitObject.setAttributeValues(PRSPContext.get(), mpMIPAttr);

					if (UIUtil.isNotNullAndNotEmpty(sRELid)) {
						DomainRelationship.setAttributeValue(PRSPContext.get(), sRELid,
								DomainConstants.ATTRIBUTE_QUANTITY, objectSimpleXML.getMIP().getqty());
						}
						if(objectSimpleXML.getOutputIPack() != null) {
							objectSimpleXML.getOutputIPack().processMappingAttributes();
							objectMIP.mapAttributes(objectSimpleXML.getOutputIPack());
						}
					}
				}
			}
			// Master Customer Unit
			else if (strUnitType.equals(DataConstants.TYPE_PG_MASTERCUSTOMERUNIT)) {
				if (objectSimpleXML.getMCUP() != null) {
					Map<String, String> mpMCUPAttr = new HashMap<>();
					mpMCUPAttr.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT,
							objectSimpleXML.getMCUP().getheight());
					mpMCUPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, objectSimpleXML.getMCUP().getdepth());
					mpMCUPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, objectSimpleXML.getMCUP().getwidth());
					
					MasterCustomerUnit objectMCUP = new MasterCustomerUnit(domUnitObject);
					//INC10305877 DTCLD-364 Start		
					if(DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)){
					
						objectMCUP.compareAttributesFromXMLAndDB(mpMCUPAttr,sRELid,objectSimpleXML.getMCUP().getqty());
						objectMCUP.createAlertMessageForDiffInAttrValue();
						sbAlertMessageForDiffFoundInAttrValue = sbAlertMessageForDiffFoundInAttrValue.append(objectMCUP.sbAlertMessageForDiffFoundInAttrValue);
						VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>> updateAttributesonTUPBOMStructure MCUP sbAlertMessageForDiffFoundInAttrValue "+sbAlertMessageForDiffFoundInAttrValue);
					}
					//INC10305877 DTCLD-364 End
					if(!DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)) {
					domUnitObject.setAttributeValues(PRSPContext.get(), mpMCUPAttr);
					}
					if (UIUtil.isNotNullAndNotEmpty(sRELid)) {
						DomainRelationship.setAttributeValue(PRSPContext.get(), sRELid,
								DomainConstants.ATTRIBUTE_QUANTITY, objectSimpleXML.getMCUP().getqty());
				}
					if(objectSimpleXML.getOutputShipper() != null && !DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)) {
						objectSimpleXML.getOutputShipper().processMappingAttributes();	
						objectMCUP.mapAttributes(objectSimpleXML.getOutputShipper());
					}
				}
			}
			// Transport Unit
			else if (strUnitType.equals(DataConstants.TYPE_PG_TRANSPORTUNIT)) {
				domTUP = domUnitObject;
				if(!DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)) {
				domTUP.setDescription(PRSPContext.get(), objectSimpleXML.getStrExtDesignDesc());
				}

				if (objectSimpleXML.getTUP() != null) {
					Map<String, String> mpTUPAttr = new HashMap<>();
					mpTUPAttr.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT,
							objectSimpleXML.getTUP().getheight());
					mpTUPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, objectSimpleXML.getTUP().getdepth());
					mpTUPAttr.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, objectSimpleXML.getTUP().getwidth());

					//INC10305877 DTCLD-364 Start
					if(DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)){
						
						compareAttributesFromXMLAndDB(mpTUPAttr,sRELid,objectSimpleXML.getTUP().getqty());
						createAlertMessageForDiffInAttrValue();
						sbAlertMessageForDiffFoundInAttrValue = sbAlertMessageForDiffFoundInAttrValue.append(sbAlertMessageForDiffFoundInAttrValueTUP);
						VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>> updateAttributesonTUPBOMStructure TUP sbAlertMessageForDiffFoundInAttrValue "+sbAlertMessageForDiffFoundInAttrValue);
					}
					//INC10305877 DTCLD-364 End
					
					if(!DataConstants.STATE_RELEASE.equalsIgnoreCase(strCurrentState)) {
					domUnitObject.setAttributeValues(PRSPContext.get(), mpTUPAttr);

					if (UIUtil.isNotNullAndNotEmpty(sRELid)) {
						DomainRelationship.setAttributeValue(PRSPContext.get(), sRELid,
								DomainConstants.ATTRIBUTE_QUANTITY, objectSimpleXML.getTUP().getqty());
						}
						if(objectSimpleXML.getOutputUnitload() != null) {
							objectSimpleXML.getOutputUnitload().processMappingAttributes();
							mapAttributes(objectSimpleXML.getOutputUnitload());
					}
			}
		}
			}
		} catch (MatrixException ex) {
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ex.getMessage());
			StringList slOwnerModificationAccessList = new StringList();
			slOwnerModificationAccessList.add(DataConstants.CONSTANT_ACCESS_MODIFY);
			boolean hasAccess = FrameworkUtil.hasAccess(PRSPContext.get(), domUnitObject,
					slOwnerModificationAccessList);
			if (!hasAccess) {
				throw new DesignToolsIntegrationException(errorLackOfAccess.getExceptionCode(),
						errorLackOfAccess.getExceptionMessage()+" "+domUnitObject.getName()+" "+domUnitObject.getRevision());
			} else {
				throw new MatrixException(ex.getMessage());
			}
		}
	}

	@Override
	public void mapAttributes(IMappableComponent outputUnitload) throws MatrixException {
		AttributeList tupMappingAttribute = outputUnitload.getMappingAttributes();
		domTUP.setAttributes(PRSPContext.get(), tupMappingAttribute);
	}
	
	
	/**
	 * This method 
	 * @param mlCACOInfoList
	 * @return
	 */
	public String getTUPChangeActionID(MapList mlCACOInfoList) {

		String strTUPChangeActionID = null ;

		if (!mlCACOInfoList.isEmpty()) {
			Iterator<?> itrCAList = mlCACOInfoList.iterator();
			Map<?, ?> mNodeInfo = null;
			String strCAState ;

			while (itrCAList.hasNext()) {
				mNodeInfo = (Map<?, ?>) itrCAList.next();
				strCAState = (String) mNodeInfo.get(DomainConstants.SELECT_CURRENT);
				if (!(ChangeConstants.STATE_COMPLETE.equals(strCAState)
						|| ChangeConstants.CANCELLED.equals(strCAState))) {
					strTUPChangeActionID = (String) mNodeInfo.get(DomainConstants.SELECT_ID);
					break;
				}
			}
		}
		return strTUPChangeActionID;
	}
	
	/**
	 * @param domTUP
	 * @return mlSPS
	 * @throws FrameworkException
	 *             This method accepts TUP and generates its related SPS maplist
	 */
	public MapList getRelatedSPS(Context context, DomainObject domObjTUP) throws FrameworkException {

		MapList mlSPS = null;

		StringList slbusSelStmts = new StringList();
		slbusSelStmts.add(DomainConstants.SELECT_ID);
		slbusSelStmts.add(DomainConstants.SELECT_TYPE);
		slbusSelStmts.add(DomainConstants.SELECT_NAME);
		slbusSelStmts.add(DomainConstants.SELECT_REVISION);
		slbusSelStmts.add(DomainConstants.SELECT_VAULT);
		slbusSelStmts.add(DataConstants.SELECT_ATTRIBUTE_GEOMETRY_SHAPE);
		slbusSelStmts.add(DataConstants.SELECT_ATTR_PG_PALLETTYPE);
		//Added for DTCLD-670
		slbusSelStmts.add(DataConstants.SELECT_ATTR_PG_SPS_ORIGINATION);

		StringList slRelSelStmts = new StringList();
		slRelSelStmts.add(DomainRelationship.SELECT_ID);
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>  getRelatedSPS domObjTUP " + domObjTUP);
		mlSPS = domObjTUP.getRelatedObjects(context, // Context
				DataConstants.REL_PART_SPECIFICATION, // Relationship Pattern
				DataConstants.TYPE_PG_STACKINGPATTERN, // Type Pattern
				slbusSelStmts, // Object Selects
				slRelSelStmts, // Relationship Selects
				false, // get TO
				true, // get From
				(short) 1, // Recurrence Level
				null, // Object Where
				null, // RelationShip Where
				0);
		
		return mlSPS;
	}
	
	/**
	 * Save TOPS XML - This method determines CA Id for TUP
	 * @param strTransportUnitId
	 * @return
	 * @throws FrameworkException
	 */
	public String processChangeManagement (Context context,String strTransportUnitId) throws FrameworkException {
		ChangeManagement changeManagement = new ChangeManagement();
		MapList mlCACOInfoList = changeManagement.getCACOInfoList(context,strTransportUnitId);
		
		return getTUPChangeActionID(mlCACOInfoList);
	}

	@Override
	public void processAttributesForXMLTagsCreation(Map newMap,Input input) throws MatrixException {
		
		createTags (input);
	}

	@Override
	public void getAttributesForDimsTag(Map hmAttributeMap) {
		//Currently no need to implement
		
	}

	/* /* Save TOPS XML - This method create tags for TUP in IPS_import xml
	 */
	@Override
	public void createTags(Input input) {
		
		String strUnitOfMeasure = DataConstants.CONSTANT_ONE;
		Unitload unitload = new Unitload(true, DomainConstants.EMPTY_STRING,strPalletTypeValue,strUnitOfMeasure,DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ONE);
		UnitloadInfo unitloadInfo = new UnitloadInfo(DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO, DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO, DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO, DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO,DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO, DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO, DataConstants.CONSTANT_ZERO_DOT_ZERO_ZERO);
		unitload.setInfo(unitloadInfo);	
		
		input.setUnitload(unitload);
		
	}
	
	/**
	 * @param sPalletTypeValue
	 */
	public void setstrPalletTypeValue(String sPalletTypeValue) {
		strPalletTypeValue = sPalletTypeValue;
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
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  TUP compareAttributesFromXMLAndDB slAttrNames "+slAttrNames);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  TUP compareAttributesFromXMLAndDB mpAttrValueInXML "+mpAttrValueInXML);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  TUP compareAttributesFromXMLAndDB relQtyFromXML "+relQtyFromXML);
		
		Map<String, String> mapExistingAttrValuesInDb = domTUP.getInfo(context, slAttrNames);
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  TUP compareAttributesFromXMLAndDB mapExistingAttrValuesInDb "+mapExistingAttrValuesInDb);
							
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
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  TUP compareAttributesFromXMLAndDB slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
	}

	@Override
	/**
	 * INC10305877 DTCLD-364 
	 * This method creates a consolidated Alert Message to provide a list of Attributes whose values are different in DB and XML for Released Parts
	 */
	public void createAlertMessageForDiffInAttrValue() {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  TUP createAlertMessageForDiffInAttrValue slDifferencInAttrValueFound "+slDifferencInAttrValueFound);
		if(!slDifferencInAttrValueFound.isEmpty()) {
			
			String strCommaSeparatedValue = String.join(",", slDifferencInAttrValueFound);
			sbAlertMessageForDiffFoundInAttrValueTUP.append(DataConstants.CONSTANT_NEW_LINE_SLASH_N);
			sbAlertMessageForDiffFoundInAttrValueTUP.append(DataConstants.CONSTANT_DIMENSIONS);
			sbAlertMessageForDiffFoundInAttrValueTUP.append(strCommaSeparatedValue);
			sbAlertMessageForDiffFoundInAttrValueTUP.append(errorDiffInDBXMLAttrValues.getExceptionMessage());
			sbAlertMessageForDiffFoundInAttrValueTUP.append(domTUP.getName());
			sbAlertMessageForDiffFoundInAttrValueTUP.append(DataConstants.CONSTANT_STRING_SPACE);
			sbAlertMessageForDiffFoundInAttrValueTUP.append(domTUP.getRevision());
		}	
	}
}
