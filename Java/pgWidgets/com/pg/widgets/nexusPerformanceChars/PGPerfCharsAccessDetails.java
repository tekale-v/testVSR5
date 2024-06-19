package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.dassault_systemes.enovia.formulation.enumeration.FormulationPolicy;
import com.dassault_systemes.enovia.formulation.enumeration.FormulationState;
import com.dassault_systemes.enovia.template.enumeration.TemplateRelationship;
import com.matrixone.apps.configuration.ConfigurationConstants;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.cpn.ProductData;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class PGPerfCharsAccessDetails {

	public static PGPerfCharsUtil pgPerfCharsUtil = new PGPerfCharsUtil();
	public static final String CONST_COMMAND_DELECT_PRRFCHAR = "CPNProductDataViewDeleteCharacteristic";
	public static final String CONST_COMMAND_EDIT_PRRFCHAR = "CPNCharacteristicViewEditAll";
	public static final String CONST_COMMAND_EXPORT_TO_EXCEL_PRRFCHAR = "pgExportToExcel";
	public static final String CONST_COMMAND_IMPORT_TO_EXCEL_PRRFCHAR = "pgImportFromExcel";
	public static final String CONST_COMMAND_COPY_FROM_PRRFCHAR = "pgCopyFrom";
	public static final String CONST_COMMAND_VALIDATE_CHARACTERISTICS_PRRFCHAR = "pgValidateCharacteristic";
	public static final String CONST_COMMAND_PERFORMANCE_CHARACTERISTICS_VALIDATE_PRRFCHAR = "pgPerformanceCharacterasticsValidation";
	public static final String CONST_TABLE_VPDPERFORMANCECHARACTERISTIC = "pgVPDPerformanceCharacteristicTable";
	public static final String CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH = "pgVPDPerformanceCharacteristicMasterPathTable";
	public static final String CONST_TABLE_VPDTESTMETHODVIEW = "pgVPDTestMethodView";
	public static final String CONST_TABLE_VPDVALUEVIEW = "pgVPDValuesView";
	public static final String CONST_COLUMN_ISDERIVED = "ColProg_Path";
	public static final String CONST_COLUMN_PATH = "Path";
	public static final String CONST_COLUMN_MASTER_PART = "attribute[Reference Type]";
	public static final String CONST_COLUMN_PG_TEST_METHOD = "ColProg_TestMethodName";
	public static final String CONST_COLUMN_TEST_METHOD = "Test Method";
	public static final String CONST_COLUMN_LOWER_ROUTINE_RELEASE_LIMIT = "attribute[pgLowerRoutineReleaseLimit]";
	public static final String CONST_COLUMN_UPPER_ROUTINE_RELEASE_LIMIT = "attribute[pgUpperRoutineReleaseLimit]";
	/*public static final String CONST_COLUMN_RELEASE_CRITERIA = "Release Criteria";
	public static final String CONST_COLUMN_RELEASE_CRITERIA2 = "ReleaseCriteria2"; */
	public static final String CONST_COLUMN_DERIVED_TITLE = "DerivedTitle";

	public String getAccessDetails(Context context, String strJsonInput) throws Exception {
		JsonObjectBuilder jsonReturnObjCommand = Json.createObjectBuilder();
		JsonObjectBuilder jsonReturnObjTable = Json.createObjectBuilder();
		JsonObjectBuilder jsonReturnObjBasics = Json.createObjectBuilder();
		JsonObjectBuilder jsonFinalReturnObj = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		MapList mlReturnList = new MapList();
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		String sObjectId = jsonInputData.getString("objectId");
		String strSelectedTable = jsonInputData.getString("selectedTable");
		String strpgVPDCPNCharacteristicDerivedFilter = jsonInputData.getString("pgVPDCPNCharacteristicDerivedFilter");
		JsonArray strCommandNames = jsonInputData.getJsonArray("ActionItems");
		JsonArray strColumn = jsonInputData.getJsonArray("columns");
		Map mBasics = getBasicDetails(context,sObjectId);
		Map mCommandReturnMap = getCommandAccess(context,sObjectId,strCommandNames, strSelectedTable, strpgVPDCPNCharacteristicDerivedFilter);
		Map mTableReturnmap = getTableAccess(context,sObjectId, strSelectedTable,strColumn, strpgVPDCPNCharacteristicDerivedFilter);
		jsonReturnObjCommand = pgPerfCharsUtil.getJsonObjectFromMap(mCommandReturnMap);
		jsonReturnObjTable = pgPerfCharsUtil.getJsonObjectFromMap(mTableReturnmap);
		jsonReturnObjBasics = pgPerfCharsUtil.getJsonObjectFromMap(mBasics);
		//jsonArrObjInfo.add(jsonReturnObj);
		jsonFinalReturnObj.add("ActionItems", jsonReturnObjCommand);
		jsonFinalReturnObj.add("ColumnsAccess", jsonReturnObjTable);
		jsonFinalReturnObj.add("Basics", jsonReturnObjBasics);
		return jsonFinalReturnObj.build().toString();

	}

	public Map getBasicDetails(Context context, String sObjectId) throws Exception{
		StringList slSelectables = new StringList(DomainObject.SELECT_TYPE);
		String strTypeProperty = DomainObject.EMPTY_STRING;
		Map mBasics = pgPerfCharsUtil.ObjectInfoMap(context,  sObjectId,  slSelectables);
		if(!mBasics.isEmpty())
		{
			String strType = (String)mBasics.get(DomainObject.SELECT_TYPE);
			strTypeProperty = FrameworkUtil.getAliasForAdmin(context, "type",strType, false);
			//mBasics.put(DomainObject.SELECT_TYPE, strTypeProperty);
			mBasics.replace(DomainObject.SELECT_TYPE, strTypeProperty);
		}
		return mBasics;
	}

	private Map getCommandAccess(Context context,String sObjectId,JsonArray  jsonCommandNames,String strSelectedTable,String strpgVPDCPNCharacteristicDerivedFilter) {
		Map mAccessDetails = new HashMap();
		boolean bAccess = true;
		//StringList slSelectedCommand = StringUtil.split(strCommandNames, ",");
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			for(int i=0; i<jsonCommandNames.size(); i++) {
				String strCommand = jsonCommandNames.getString(i);
				{
					bAccess = getCommandAccess(context, strCommand, strSelectedTable, sObjectId , strpgVPDCPNCharacteristicDerivedFilter);
					mAccessDetails.put(strCommand, String.valueOf(bAccess));
				}
			}

		}
		return mAccessDetails;
	}

	private Map getTableAccess(Context context,String sObjectId , String strSelectedTable, JsonArray jsonColumn, String strpgVPDCPNCharacteristicDerivedFilter) {
		Map mAccessDetails = new HashMap();
		boolean bAccess = true;
		//StringList slSelectedCommand = StringUtil.split(strCommandNames, ",");
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			for(int i=0; i<jsonColumn.size(); i++) {
				String strColumn = jsonColumn.getString(i);
				{
					bAccess = getTableColumnAccess(context, strColumn, strSelectedTable, sObjectId , strpgVPDCPNCharacteristicDerivedFilter);
					mAccessDetails.put(strColumn, String.valueOf(bAccess));
				}
			}
		}
		return mAccessDetails;
	}

	private boolean getCommandAccess(Context context, String strSelectedCommand, String strSelectedTable, String strObjectId, String strpgVPDCPNCharacteristicDerivedFilter) {
		boolean bReturnAccess = false;
		boolean hasMaskAccess;
		Map mpAccessDetails = new HashMap();
		if(UIUtil.isNotNullAndNotEmpty(strSelectedCommand))
		{
			try {
				switch(strSelectedCommand) 
				{ 
				case CONST_COMMAND_DELECT_PRRFCHAR: 
					bReturnAccess = checkDBT(context,strSelectedTable);
					break; 

				case CONST_COMMAND_EDIT_PRRFCHAR: 
					hasMaskAccess = pgPerfCharsUtil.getAccessDetailsForMask(context,strObjectId, "modify|FromConnect|FromDisconnect");
					bReturnAccess = isCharacteristicTableEdtiable(context, strpgVPDCPNCharacteristicDerivedFilter, strObjectId, strSelectedTable);
					if(hasMaskAccess && bReturnAccess)
						bReturnAccess = true;
					else 
						bReturnAccess = false;
					break; 

				case CONST_COMMAND_EXPORT_TO_EXCEL_PRRFCHAR:
					bReturnAccess = (showPerformanceCharCommandForProduct(context, strpgVPDCPNCharacteristicDerivedFilter, strObjectId, strSelectedTable) && pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId));
					break;

				case CONST_COMMAND_IMPORT_TO_EXCEL_PRRFCHAR:
					bReturnAccess = (showPerformanceCharCommands(context,  strObjectId, strSelectedTable , strpgVPDCPNCharacteristicDerivedFilter) && pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId));
					break;

				case CONST_COMMAND_COPY_FROM_PRRFCHAR:
					bReturnAccess = (showPerformanceCharCommands(context,  strObjectId, strSelectedTable , strpgVPDCPNCharacteristicDerivedFilter) && pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId));
					break;

				case CONST_COMMAND_VALIDATE_CHARACTERISTICS_PRRFCHAR:
					bReturnAccess = hasAccessToValidateCommand(context,  strObjectId);
					break;	

				case CONST_COMMAND_PERFORMANCE_CHARACTERISTICS_VALIDATE_PRRFCHAR:
					bReturnAccess = (showPerformanceCharCommands(context,  strObjectId, strSelectedTable , strpgVPDCPNCharacteristicDerivedFilter) && pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId));
					break;		
				} 
			} catch (Exception e) {
				
			}
		}
		return bReturnAccess;
	}

	private boolean getTableColumnAccess(Context context, String strSelectedColumn, String strSelectedTable, String strObjectId, String strpgVPDCPNCharacteristicDerivedFilter) {

		boolean hasAccessToColumn = false;
		StringList slSelects = new StringList();
		Map mInofMap = new HashMap();
		boolean hasMaskAccess;
		Map mpAccessDetails = new HashMap();
		try {
			if(UIUtil.isNotNullAndNotEmpty(strSelectedTable) && UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				DomainObject doProductData = DomainObject.newInstance(context, strObjectId);
				slSelects.add(DomainObject.SELECT_TYPE);
				slSelects.add(PGPerfCharsConstants.CONST_INTERFACE_PART_FAMILY);
				slSelects.add(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_REFERENCE_TYPE));
				slSelects.add(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED));
				mInofMap = pgPerfCharsUtil.ObjectInfoMap(context, strObjectId,slSelects);
				if((CONST_COLUMN_ISDERIVED.equalsIgnoreCase(strSelectedColumn) || CONST_COLUMN_DERIVED_TITLE.equalsIgnoreCase(strSelectedColumn)) && (CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable))) {
					if(CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) &&( !((String)mInofMap.get(DomainObject.SELECT_TYPE)).equalsIgnoreCase(PGPerfCharsConstants.TYPE_CPGPRODUCT)  && ( ((String)mInofMap.get(PGPerfCharsConstants.CONST_INTERFACE_PART_FAMILY)).equalsIgnoreCase("FALSE") || !((String)mInofMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_REFERENCE_TYPE))).equalsIgnoreCase("M") )))
					{
						hasAccessToColumn = true;
					}else
					{
						if(!((String)mInofMap.get(DomainObject.SELECT_TYPE)).equalsIgnoreCase(PGPerfCharsConstants.TYPE_CPGPRODUCT) ) {
							hasAccessToColumn = true;
						}
					}
				}
				else if(CONST_COLUMN_PATH.equalsIgnoreCase(strSelectedColumn) && (CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable)))
				{
					if(((String)mInofMap.get(DomainObject.SELECT_TYPE)).equalsIgnoreCase(PGPerfCharsConstants.TYPE_CPGPRODUCT) && pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId))
					{
						hasAccessToColumn = true;
					}
				}
				else if(CONST_COLUMN_MASTER_PART.equalsIgnoreCase(strSelectedColumn) &&  (CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable)))
				{
					if(((String)mInofMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_REFERENCE_TYPE))).equalsIgnoreCase("M"))
					{
						hasAccessToColumn = true;
					}
				}
				else if(CONST_COLUMN_PG_TEST_METHOD.equalsIgnoreCase(strSelectedColumn) && ( CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDTESTMETHODVIEW.equalsIgnoreCase(strSelectedTable)  ))
				{
					hasAccessToColumn = pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId);
				}
				else if(CONST_COLUMN_TEST_METHOD.equalsIgnoreCase(strSelectedColumn) && ( CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDTESTMETHODVIEW.equalsIgnoreCase(strSelectedTable)  ))
				{
					hasAccessToColumn = !pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId);
				}
				else if((CONST_COLUMN_LOWER_ROUTINE_RELEASE_LIMIT.equalsIgnoreCase(strSelectedColumn) || CONST_COLUMN_UPPER_ROUTINE_RELEASE_LIMIT.equalsIgnoreCase(strSelectedColumn)) && ( CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable) ))
				{
					hasAccessToColumn =  (!pgPerfCharsUtil.hasRoleForAccess(context,PGPerfCharsConstants.ROLE_PGCONTRACTMANUFACTURER) && !pgPerfCharsUtil.hasRoleForAccess(context,PGPerfCharsConstants.ROLE_PGCONTRACTSUPPLIER) && validateAccesstoRoutineReleaseColumns(context,strObjectId)) ;
				}
				/*else if(CONST_COLUMN_RELEASE_CRITERIA.equalsIgnoreCase(strSelectedColumn) && ( CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDVALUEVIEW.equalsIgnoreCase(strSelectedTable)))
				{
					if(((String)mInofMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED))).equalsIgnoreCase(PGPerfCharsConstants.CONST_YES))
						hasAccessToColumn = true;
				}
				else if(CONST_COLUMN_RELEASE_CRITERIA2.equalsIgnoreCase(strSelectedColumn) && (CONST_TABLE_VPDVALUEVIEW.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTIC.equalsIgnoreCase(strSelectedTable) || CONST_TABLE_VPDPERFORMANCECHARACTERISTICMASTERPATH.equalsIgnoreCase(strSelectedTable)  ))
				{
					if(UIUtil.isNullOrEmpty((String)mInofMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED))) || ((String)mInofMap.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED))).equalsIgnoreCase(PGPerfCharsConstants.CONST_NO))
						hasAccessToColumn = true;
				}  */
				else{
				hasAccessToColumn = true;
				}
				return hasAccessToColumn;
			}


		}  catch (Exception e) {
			
		}

		return hasAccessToColumn;
	}


	/**This method is a Access Program to enable/disable the Extended filter in the Table Tab of the FPP/Other BOM types.
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return
	 * @throws Exception
	 */

	public Boolean checkDBT(Context context, String strSelectedTable) throws Exception
	{
		String strWeightCharTable = FrameworkProperties.getProperty(context, "emxCPN.Table.WeightCharacteristic");
		//String strSelectedTable = (String) programMap.get("selectedTable");
		if(UIUtil.isNotNullAndNotEmpty(strSelectedTable) && strSelectedTable.equals(strWeightCharTable))
		{
			return false;
		}
		else
		{
			return true;
		}

	}

	/**
	 * Checks whether the pgVPDCPNCharacteristicDerivedFilter Local and forwards to the super class check if true. If there is no pgVPDCPNCharacteristicDerivedFilter then forwards to the superclass method.
	 * @param context
	 * @param args
	 * @return true if Edit All command is to be shown else false.
	 * @throws Exception
	 */
	public boolean isCharacteristicTableEdtiable(Context context,String pgVPDCPNCharacteristicDerivedFilter, String strObjectId , String strSelectedTable) throws Exception
	{
		//String pgVPDCPNCharacteristicDerivedFilter = UTILS.getParam(args, "pgVPDCPNCharacteristicDerivedFilter");
		String rangeLocal = FrameworkProperties.getProperty(context, "emxCPN.MasterCharacteristics.DerivedRange.Local");
		boolean isCharTableEditable = false;
		//DSO 2013x.4 :Weight Characteristic Table Changes- START
		/*DSO 2013x.4 - Changes to handle Owner and CoOwner check : START*/
		//pgDSOUtil_mxJPO pgDSOUtilInstance = new pgDSOUtil_mxJPO(context, args);
		/*DSO 2013x.4 - Changes to handle Owner and CoOwner check : END*/
		DomainObject domObj = null;
		boolean isDSO = false;
		String strFrom = "";

		//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648,14862: Performance Characteristic for CPG Product and Configuration Option : START
		boolean bIsProduct = false;
		String strCPGProductType = PGPerfCharsConstants.TYPE_CPG_PRODUCT;

		if(UIUtil.isNotNullAndNotEmpty(strObjectId))
		{
			domObj = DomainObject.newInstance(context, strObjectId);
			//DSO 2013x.4 ALM fix for 3165 - START
			isDSO = pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId);	
			bIsProduct = domObj.isKindOf(context, strCPGProductType);
		}

		if(isDSO && bIsProduct){
			if(UIUtil.isNotNullAndNotEmpty(strSelectedTable) && "pgVPDPerformanceCharacteristicTable".equals(strSelectedTable) && PGPerfCharsConstants.STATE_PRELIMINARY.equals((String)domObj.getInfo(context, DomainConstants.SELECT_CURRENT))){		    		
				isCharTableEditable =  true;
			}
		}
		//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648,14862: Performance Characteristic for CPG Product and Configuration Option : END

		if(UIUtil.isNotNullAndNotEmpty(strSelectedTable) && "pgWeightCharacteristic".equals(strSelectedTable) && (domObj !=null) && isDSO)
		{
			//DSO 2013x.4 ALM fix for 3165 - END
			/*DSO 2013x.4 - Changes to handle Owner and CoOwner check : START*/
			if((PGPerfCharsConstants.STATE_PRELIMINARY.equals((String)domObj.getInfo(context, DomainConstants.SELECT_CURRENT)))  && 
					(!PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals((String)domObj.getInfo(context, DomainConstants.SELECT_TYPE))) &&
					pgPerfCharsUtil.displayToOwnerAndCoOwner(context, strObjectId))
				/*DSO 2013x.4 - Changes to handle Owner and CoOwner check : END*/
			{
				isCharTableEditable =  true;
			}
			else
			{
				isCharTableEditable =  false;
			}
		}
		//DSO 2013x.4 ALM fix for 3165 - START
		//DSO 2015x.1 Issue fix for DSO15X-303 - START
		else if(isDSO)
		{
			//DSO 2015x.1 Issue fix for DSO15X-293 - START
			//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648,14862: Performance Characteristic for CPG Product and Configuration Option : START
			/*if(UIUtil.isNotNullAndNotEmpty(pgVPDCPNCharacteristicDerivedFilter) 
    				&& ("All".equalsIgnoreCase(pgVPDCPNCharacteristicDerivedFilter)
        					|| "Referenced".equalsIgnoreCase(pgVPDCPNCharacteristicDerivedFilter))){*/
			if(!bIsProduct && UIUtil.isNotNullAndNotEmpty(pgVPDCPNCharacteristicDerivedFilter) 
					&& ("All".equalsIgnoreCase(pgVPDCPNCharacteristicDerivedFilter)
							|| "Referenced".equalsIgnoreCase(pgVPDCPNCharacteristicDerivedFilter))){
				//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648,14862: Performance Characteristic for CPG Product and Configuration Option : END
				isCharTableEditable =  false;
			}else if(pgPerfCharsUtil.displayToOwnerAndCoOwner(context, strObjectId)){
				isCharTableEditable =  true;

				//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648,14862: Performance Characteristic for CPG Product and Configuration Option : START 				
				//Only display in Preliminary state of Product even if the user is co-owner
				String strCurrent = (String)domObj.getInfo(context, DomainConstants.SELECT_CURRENT);				
				if(bIsProduct && !strCurrent.equals(PGPerfCharsConstants.STATE_PRELIMINARY)){
					// Disable Edit All button					
					isCharTableEditable =  false;
				}
				//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648,14862: Performance Characteristic for CPG Product and Configuration Option : START
			}
			//DSO 2015x.1 Issue fix for DSO15X-293 - END    		
		}
		//DSO 2015x.1 Issue fix for DSO15X-303 - END
		else
		{
			isCharTableEditable = isCharacteristicTableEdtiableBase(context, strObjectId ,  strSelectedTable , strFrom);
		}
		return isCharTableEditable;
	}


	public boolean isCharacteristicTableEdtiableBase(Context context,String strObjectId, String strSelectedTable , String strFrom) throws Exception {

		/*  HashMap programMap = (HashMap)JPO.unpackArgs(args);
        String strSelectedTable = (String)programMap.get("selectedTable");
        String strObjectId = (String)programMap.get("objectId"); */
		DomainObject doPD = DomainObject.newInstance(context, strObjectId);
		String strCurrent = doPD.getInfo(context, DomainConstants.SELECT_CURRENT);
		if(strCurrent.equals(FormulationState.IPM_PART_PRELIMNARY.getState(context, FormulationPolicy.IPM_PART.getPolicy(context)))) {
			return true;
		} else if(!strCurrent.equals(FormulationState.IPM_PART_RELEASED.getState(context, FormulationPolicy.IPM_PART.getPolicy(context)))) {
			return false;
		}

		/* wqz : IR-058891V6R2011x : 	Null Pointer Exception on shared table properties page : 17-JUN-2010 : Start */  
		//String strFrom = (String)programMap.get("From");
		if(strFrom != null && strFrom.equals("SharedTable")) {
			if(!strCurrent.equals(FormulationState.IPM_PART_PRELIMNARY.getState(context, FormulationPolicy.IPM_PART.getPolicy(context)))) {
				return false;
			}
		}
		/* wqz : IR-058891V6R2011x : 	Null Pointer Exception on shared table properties page : 17-JUN-2010 : End */  

		String strTypeSym = EnoviaResourceBundle.getProperty(context, "emxCPN.Characteristic.table."+strSelectedTable);

		ProductData doProdData = new ProductData(strObjectId);
		String owner = doProdData.getInfo(context,DomainConstants.SELECT_OWNER);
		/* boolean isCoOwner = doProdData.isCoOwner(context,context.getUser());*/
		boolean isMainUser = PersonUtil.hasAssignment(context,CPNCommonConstants.ROLE_IPM_IPM_MAINTENANCE_USER);

		/* if(isCoOwner || context.getUser().equals(owner) || owner.equals("Corporate"))*/
		if(context.getUser().equals(owner) || "Corporate".equals(owner))
		{
			if(strCurrent.equals(FormulationState.IPM_PART_RELEASED.getState(context,  FormulationPolicy.IPM_PART.getPolicy(context))) && !isMainUser) {
				return false;
			}
		} else {
			return false;
		}
		StringBuffer sbSelect = new StringBuffer(100);
		sbSelect.append("from[").append(TemplateRelationship.TEMPLATE.get(context)).append("].to.id");
		String strTemplateId = doPD.getInfo(context, sbSelect.toString());

		DomainObject doPDT = DomainObject.newInstance(context, strTemplateId);

		StringList slRelSelect = StringList.create("attribute[" + CPNCommonConstants.ATTRIBUTE_MAINTAINABLE + "].value");
		StringList slBusSelect = StringList.create("attribute[" + CPNCommonConstants.ATTRIBUTE_TITLE + "].value");

		MapList mlRelatedConfigReports = doPDT.getRelatedObjects(context,
				CPNCommonConstants.RELATIONSHIP_TEMPLATE_SECTION,
				CPNCommonConstants.TYPE_TEMPLATE_SECTION,
				slBusSelect,
				slRelSelect,
				false,
				true,
				(short)1,
				null,
				null,
				0);

		String strAllCharTables = EnoviaResourceBundle.getProperty(context, "emxCPN.Characteristic.AvailableCharacteristic");
		StringList slAllCharTables = FrameworkUtil.split(strAllCharTables, ",");

		for(int i=0; i<mlRelatedConfigReports.size();i++) {
			Map sectionInfoMap = (Map)mlRelatedConfigReports.get(i);

			String strMaintainable = (String)sectionInfoMap.get("attribute[" + CPNCommonConstants.ATTRIBUTE_MAINTAINABLE + "].value");
			String strSectionName = (String)sectionInfoMap.get("attribute[" + CPNCommonConstants.ATTRIBUTE_TITLE + "].value");

			String strJPOAndFunctionAndTable = "";
			try {
				/* wqz : IR-059186V6R2011x : No access to add maintainable characteristics tables in a released PD which are containing spaces in the naming convention : 17-JUN-2010 : Start */  
				//  			  strJPOAndFunctionAndTable = FrameworkProperties.getProperty("emxCPN." + strSectionName +  ".JPOAndFunctionAndTable");
				strJPOAndFunctionAndTable = EnoviaResourceBundle.getProperty(context, "emxCPN." + strSectionName.replace(' ', '_') +  ".JPOAndFunctionAndTable");
				/* wqz : IR-059186V6R2011x : No access to add maintainable characteristics tables in a released PD which are containing spaces in the naming convention : 17-JUN-2010 : End */  
			} catch(Exception e) {
				continue;
			}
			StringList slJPOAndFunctionAndTable = FrameworkUtil.split(strJPOAndFunctionAndTable, ":");
			String strType = "";

			if(slJPOAndFunctionAndTable.size() >= 3) {
				String strFunctionType =  (String)slJPOAndFunctionAndTable.get(1);
				if(strFunctionType != null) {
					StringList slFunctionType = FrameworkUtil.split(strFunctionType, "|");
					if(slFunctionType.size() >=2) {
						strType = (String)slFunctionType.get(1);
					}
				}
			}

			if(strTypeSym.equals(strType)) {
				if("True".equalsIgnoreCase(strMaintainable)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	//Added for P&G DSM CONFIGMGMT:Req. Id 14862: Performance Characteristic for CPG Product and Configuration Option: START
	// This method is added to display characteristics resequence command in case of CPG Product
	/**
	 * @param context ENOVIA context object
	 * @param args arguments
	 * @return boolean value
	 * @throws Exception
	 */
	//showPerformanceCharCommandForProduct(context, strpgVPDCPNCharacteristicDerivedFilter, strObjectId, strSelectedTable)
	public boolean showPerformanceCharCommandForProduct(Context context, String strpgVPDCPNCharacteristicDerivedFilter, String strCtxtObjectId, String strSelectedTable) throws Exception{
		boolean showCommand = false;
		try{
			showCommand = showPerformanceCharCommands(context,strCtxtObjectId, strSelectedTable, strpgVPDCPNCharacteristicDerivedFilter);			
			//Map programMap = JPO.unpackArgs(args);
			//String strCtxtObjectId = (String) programMap.get("objectId");

			if(UIUtil.isNotNullAndNotEmpty(strCtxtObjectId)) {		
				DomainObject domObj = DomainObject.newInstance(context,strCtxtObjectId);
				String TYPE_CPG_PRODUCT = PGPerfCharsConstants.TYPE_CPG_PRODUCT;
				StringList busSel =  new StringList(2);
				busSel.add("type.kindof["+ TYPE_CPG_PRODUCT + "]");
				busSel.add(DomainConstants.SELECT_CURRENT);		

				Map busMap = domObj.getInfo(context, busSel);
				String state = (String) busMap.get(DomainConstants.SELECT_CURRENT);
				String kindOf= (String) busMap.get("type.kindof["+ TYPE_CPG_PRODUCT + "]");		

				if("true".equalsIgnoreCase(kindOf))
				{	
					if(PGPerfCharsConstants.STATE_PRELIMINARY.equalsIgnoreCase(state))
						showCommand = true;			    	
				} 
			}		
		}catch(Exception ex){
			
			throw ex;
		} 
		return showCommand;
	}

	/**
	 * This is an access function to show/hide Import and Export commands
	 * @param context
	 * @param args : Program arguments
	 * @return 'true' if filter value is "Local" else false
	 * @throws Exception
	 */
	public boolean showPerformanceCharCommands(Context context,String strCtxtObjectId , String strSelectedTable, String strFilterValue ) throws Exception{

		boolean showCommands = false;
		try{
			//Map programMap = JPO.unpackArgs(args);
			String strWeightCharTable = FrameworkProperties.getProperty(context, "emxCPN.Table.WeightCharacteristic");
			//String strSelectedTable = (String) programMap.get("selectedTable");
			//String strFilterValue = (String)programMap.get("pgVPDCPNCharacteristicDerivedFilter");
			if(strWeightCharTable.equals(strSelectedTable))
				return false;

			//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648: Performance Characteristic for Configuration Management : START		
			//String strCtxtObjectId = (String) programMap.get("objectId");
			boolean bFlag = false;
			if(UIUtil.isNotNullAndNotEmpty(strCtxtObjectId)) {				
				DomainObject domObj = DomainObject.newInstance(context,strCtxtObjectId);
				String srtCPGProductType = PGPerfCharsConstants.TYPE_CPG_PRODUCT;				
				StringList busSel =  new StringList(2);
				busSel.add("type.kindof["+ srtCPGProductType + "]");
				busSel.add(DomainConstants.SELECT_CURRENT);		

				Map busMap = domObj.getInfo(context, busSel);
				String state = (String) busMap.get(DomainConstants.SELECT_CURRENT);
				String kindOf= (String) busMap.get("type.kindof["+ srtCPGProductType + "]");		

				if("true".equalsIgnoreCase(kindOf))
				{	
					if(PGPerfCharsConstants.STATE_PRELIMINARY.equalsIgnoreCase(state) &&  UIUtil.isNotNullAndNotEmpty(strFilterValue) && "Local".equalsIgnoreCase(strFilterValue)){
						bFlag = true; 			    	 
						return bFlag;
					}
				}
			}
			//P&G DSM CONFIGMGMT:Req.Id 13703, 13613, 13829, 13648: Performance Characteristic for Configuration Management : END

			//DSM(DS) 2015x.5.1 - Stability Result implementation : START
			//If FPP / COP already has an Stability Result connected to it, user should not be able to add new Stability Result or perform Add Existing Stability Result on the same.
			if(PGPerfCharsConstants.STABILITYRESULT_TABLE.equalsIgnoreCase(strSelectedTable)){
				//emxCPNCharacteristicList_mxJPO emxCPNCharacteristicList = new emxCPNCharacteristicList_mxJPO(context, args);
				return canAddStabitlityResult(context, strCtxtObjectId);
			}
			//DSM(DS) 2015x.5.1 - Stability Result implementation : END
			//Modified for generalizing the commands access. If in Performance Characteristics screen command displayed to only local filter else this filter will be blank and should be displayed by default.
			if(UIUtil.isNullOrEmpty(strFilterValue) || (UIUtil.isNotNullAndNotEmpty(strFilterValue) && "Local".equalsIgnoreCase(strFilterValue))){
				//pgDSOUtil_mxJPO pgDSOUtilInstance = new pgDSOUtil_mxJPO(context, args);
				showCommands = displayToOwnerCoOwnerAndPrelimStateSORole(context, strCtxtObjectId);

			}
		}catch(Exception ex){
			
			throw ex;
		}
		return showCommands;
	}


	/**
	 * DSM(DS) 2015x.5.1 - Stability Results view implementation
	 * This method determines whether the context COP/FOP has already connected Stabitlity Result
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean canAddStabitlityResult(Context context, String strObjectId)throws Exception{
		boolean canAddStabilityResult = false;
		try{
			//String strObjectId = UTILS.getParam(args, "objectId");
			//DSM (DS) 2015x.5.1 ALM 19070 - Insert Button not visible in Stability report if characteristics is connected to FPP - START	
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				String strTypeSelectable = "from["+PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC+"].to."+DomainConstants.SELECT_TYPE;
				DomainObject doCtxtObject = DomainObject.newInstance(context, strObjectId);
				//Get the list of objects connected by relationship Characteristic
				StringList slConnectedStabilityResultType = doCtxtObject.getInfoList(context, strTypeSelectable);

				if(!slConnectedStabilityResultType.isEmpty()){	
					//If the context object is FPP or COP
					if(doCtxtObject.isKindOf(context, PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART) || doCtxtObject.isKindOf(context, PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT)){					
						//Check if the list contains object of type Stability Result then do not display the commands
						canAddStabilityResult = (!slConnectedStabilityResultType.contains(PGPerfCharsConstants.TYPE_PG_STABILITY_RESULTS));
					}else {
						canAddStabilityResult = true;
					}
				}
				//If no objects are connected by relationship Characteristic then display command
				else{
					canAddStabilityResult = true;
				}
				//DSM (DS) 2015x.5.1 ALM 19070 - Insert Button not visible in Stability report if characteristics is connected to FPP - END	
			}
		}catch(Exception ex){
			throw ex;
		}
		return canAddStabilityResult;
	}

	/**
	 * DSO 2013x.4 : This method hides/displays the EditAll button depending upon the logged in user(Who should be Owner Or CoOwner) and state is preliminary
	 * Display if the User has Standard Office Role and the state is Preliminary
	 * This is implemented for Access Program and Function on Ownership Screen
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return boolean
	 */
	public boolean displayToOwnerCoOwnerAndPrelimStateSORole(Context context, String strCtxtPartId)
	{
		boolean displayEditAll = false;
		try
		{
			String strOwner = "";
			String strCurrent = "";
			String strOriginatingSource = "";
			String strType = "";
			String strMaterialType = PropertyUtil.getSchemaProperty(context,"type_pgMaterialComposition");
			//String strCtxtPartId = pgDSOCommonUtils_mxJPO.INSTANCE.getParam(args, "objectId");
			String strCtxtUser = context.getUser();
			StringList slSelectable = new StringList(2);
			//DSM 2015x.1 IP Security Changes - START
			//StringList slCoOwner = new StringList();
			boolean isCoOwner = false;
			slSelectable.addElement(DomainConstants.SELECT_OWNER);
			slSelectable.addElement(DomainConstants.SELECT_CURRENT);
			slSelectable.addElement("attribute["+PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE+"]");
			slSelectable.addElement(DomainConstants.SELECT_TYPE);
			DomainObject doObj = DomainObject.newInstance(context);
			if(UIUtil.isNotNullAndNotEmpty(strCtxtPartId))
			{
				doObj.setId(strCtxtPartId);

				Map mapInfo = (Map)doObj.getInfo(context, slSelectable);

				if(!mapInfo.isEmpty())
				{
					strOwner = (String)mapInfo.get(DomainConstants.SELECT_OWNER);
					strCurrent = (String)mapInfo.get(DomainConstants.SELECT_CURRENT);
					strType = (String)mapInfo.get(DomainConstants.SELECT_TYPE);
					//DSM (DS) 2018x.5 - Change Mgmt FSD - Req 33608- enable "Upload Technical Support File" for CA owner/co-owner/SO Users - START 
					boolean hasAccessForCA =  (ChangeConstants.TYPE_CHANGE_ACTION.equals(strType)) 
							&& (PGPerfCharsConstants.STATE_CA_PREPARE.equals(strCurrent) || PGPerfCharsConstants.STATE_CA_INWORK.equals(strCurrent));
					//DSM (DS) 2018x.5 - Change Mgmt FSD - Req 33608- enable "Upload Technical Support File" for CA owner/co-owner/SO Users - END 
					strOriginatingSource = (String)mapInfo.get("attribute["+PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE+"]");
					//DSM (DS) 2018x.5 - Change Mgmt FSD - Req 33608- enable "Upload Technical Support File" for CA owner/co-owner/SO Users - START 
					if((UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && "DSO".equalsIgnoreCase(strOriginatingSource)) || hasAccessForCA){
						//DSM (DS) 2018x.5 - Change Mgmt FSD - Req 33608- enable "Upload Technical Support File" for CA owner/co-owner/SO Users - END
						if(UIUtil.isNotNullAndNotEmpty(strOwner) && UIUtil.isNotNullAndNotEmpty(strCurrent) && strOwner.equalsIgnoreCase(strCtxtUser) && PGPerfCharsConstants.STATE_PRELIMINARY.equalsIgnoreCase(strCurrent))
						{
							displayEditAll = true;
							return displayEditAll;
						}


						//slCoOwner = doObj.getInfoList(context, "to["+ CPNCommonConstants.RELATIONSHIP_COOWNED +"].from."+ DomainConstants.SELECT_NAME);
						isCoOwner = FrameworkUtil.hasAccess(context, doObj , "modify");

						//if(!displayEditAll && !slCoOwner.isEmpty() && slCoOwner.contains(strCtxtUser) && STATE_PRELIMINARY.equalsIgnoreCase(strCurrent))
						if(!displayEditAll && isCoOwner && PGPerfCharsConstants.STATE_PRELIMINARY.equalsIgnoreCase(strCurrent))
						{
							displayEditAll = true;
							return displayEditAll;
						}

						//DSM 2015x.1 IP Security Changes - END
						boolean hasSORole = PersonUtil.hasAssignment(context, PGPerfCharsConstants.ROLE_GSO);

						if(UIUtil.isNotNullAndNotEmpty(strCurrent) && (PGPerfCharsConstants.STATE_PRELIMINARY.equalsIgnoreCase(strCurrent)) && hasSORole)
						{
							displayEditAll = true;
							return displayEditAll;
						}
						//DSO 2013x.5 ALM 3810 - To Handle Access expression on Material types from the Material context and not from PDP context - Start

						//DSM CONFIGMGMT:ALM - 11866  To Handle Access expression on Configuration Option -START
						if(strType.equals(ConfigurationConstants.TYPE_CONFIGURATION_OPTION) && ((UIUtil.isNotNullAndNotEmpty(strOwner) && strOwner.equalsIgnoreCase(strCtxtUser)) || isCoOwner || hasSORole))
						{
							displayEditAll = true;
							return displayEditAll;
						}
						//DSM CONFIGMGMT:ALM - 11866  To Handle Access expression on Configuration Option -END

						//DSM (DS) 2018x.5 - Change Mgmt FSD - Req 33608- enable "Upload Technical Support File" for CA owner/co-owner/SO Users - START 
						if(hasAccessForCA && ((UIUtil.isNotNullAndNotEmpty(strOwner) && strOwner.equalsIgnoreCase(strCtxtUser)) || isCoOwner || hasSORole))
						{
							return true;
						}
						//DSM (DS) 2018x.5 - Change Mgmt FSD - Req 33608- enable "Upload Technical Support File" for CA owner/co-owner/SO Users - END


					}else if(UIUtil.isNotNullAndNotEmpty(strType) && PGPerfCharsConstants.TYPE_INTERNAL_MATERIAL.equalsIgnoreCase(strType)){
						if(UIUtil.isNotNullAndNotEmpty(strOwner) && strOwner.equalsIgnoreCase(strCtxtUser)){
							displayEditAll = true;
							return displayEditAll;
						}
						//DSO 2013x.5 ALM 3810 - To Handle Access expression on Material types from the Material context and not from PDP context - End
					}
					//Apollo 2018x.6 A10-753 - Starts
					else if(UIUtil.isNotNullAndNotEmpty(strType) && PGPerfCharsConstants.TYPE_CRITERIA.equalsIgnoreCase(strType))
					{
						if(UIUtil.isNotNullAndNotEmpty(strOwner) && strOwner.equalsIgnoreCase(strCtxtUser) && PGPerfCharsConstants.CRITERIA_STATE_IN_WORK.equalsIgnoreCase(strCurrent))	
						{							
							displayEditAll = true;
							return displayEditAll;
						}
					}
					//Apollo 2018x.6 A10-753 - Ends
					else{
						displayEditAll = true;
					}
				}
			}
		}
		catch(Exception ex)
		{
			
		}
		return displayEditAll;
	}

	/**
	 * DSO15X.1 : Access Function for Validate Performance Characteristic command.
	 * @param context
	 * @param args
	 * @return boolean
	 * @throws Exception
	 */
	public boolean hasAccessToValidateCommand(Context context,String strObjectId)throws Exception{
		boolean hasAccessToCommand = false;
		try {
			//HashMap inputMap = (HashMap) JPO.unpackArgs(args);
			//String strObjectId = (String)inputMap.get("objectId");
			if(pgPerfCharsUtil.isOfDSOOrigin(context, strObjectId)){
				DomainObject doProductData = DomainObject.newInstance(context, strObjectId);
				StringList slCharacteristicList = doProductData.getInfoList(context, "from["+PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC+"].to."+PGPerfCharsConstants.SELECT_ID);
				if(slCharacteristicList != null && !slCharacteristicList.isEmpty() && slCharacteristicList.size()>0)
				{
					hasAccessToCommand = true;
				}
			}
		} catch (Exception ex) {
			
		}
		return hasAccessToCommand;
	}
	
	
	 /* DSO 2013x.4 - CPN Restricted Routine Release access on Performance characteristic table based

     * @param context the matrix context

     * @param args

     * @throws Exception if operation fails

     * @return boolean

     * @since DSO.

     */

    public  boolean validateAccesstoRoutineReleaseColumns(Context context,String strContextObjId) throws Exception

    {

        //declaration
		//DSM (DS) 2015x.2 : ALM# 9892 - RMP is displaying Routine Release Limit fields : START
		//boolean bhasAccess = true;
        boolean bhasAccess = false;
		//DSM (DS) 2015x.2 : ALM# 9892 - RMP is displaying Routine Release Limit fields : START
        try

        {

            //Declarations

           // HashMap programMap = (HashMap)JPO.unpackArgs(args);

            //String strContextObjId  = (String)programMap.get("objectId");

            //Property file Entry
			//DSM (DS) 2015x.2 : ALM# 9892 - RMP is displaying Routine Release Limit fields : START
            //String strAllowedTypeList 			= FrameworkProperties.getProperty(context, "emxCPN.ProductDataCreation.PerformanceCharacteristic.ValidateTypeforRoutineReleaseupdate");
			String strAllowedTypeList 			= FrameworkProperties.getProperty(context, "emxCPN.ProductDataCreation.PerformanceCharacteristic.showRoutineReleaseLimts");
			//DSM (DS) 2015x.2 : ALM# 9892 - RMP is displaying Routine Release Limit fields : END

            StringList AllowedTypeList 			= FrameworkUtil.split(strAllowedTypeList, "|");

            String strrelsymbname 				= FrameworkProperties.getProperty(context, "emxCPN.ProductDataCreation.PerformanceCharacteristic.relcharacteristicsymbname");

            //get the Type info for the Object

            DomainObject domContextObject = DomainObject.newInstance(context, strContextObjId);

            String strTypeinfo= domContextObject.getInfo(context, DomainConstants.SELECT_TYPE);

            String strSymbolicnameofType = FrameworkUtil.getAliasForAdmin(context, "type", strTypeinfo, true);



            //Check if the type is allowed in the list

            if (UIUtil.isNotNullAndNotEmpty(strSymbolicnameofType)  && AllowedTypeList.contains(strSymbolicnameofType)){
				//DSM (DS) 2015x.2 : ALM# 9892 - RMP is displaying Routine Release Limit fields : START
                //bhasAccess = false;
				bhasAccess = true;
				//DSM (DS) 2015x.2 : ALM# 9892 - RMP is displaying Routine Release Limit fields : END
            }

        }

        catch (Exception e)

        {

           

        }

        return bhasAccess;

    }//end of Method

}
