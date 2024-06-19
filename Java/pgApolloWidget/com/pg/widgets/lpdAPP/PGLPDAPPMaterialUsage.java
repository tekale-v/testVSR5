package com.pg.widgets.lpdAPP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.widgets.util.PGWidgetUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.dashboard.util.ApolloWidgetServiceUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGLPDAPPMaterialUsage extends pgApolloConstants{
	 private static final org.apache.log4j.Logger loggerTrace = org.apache.log4j.Logger.getLogger(PGLPDAPPMaterialUsage.class);
	public static final String ATTR_PG_ROLLUP_NETWEIGHT_TO_COP = PropertyUtil.getSchemaProperty("attribute_pgRollUpNetWeightToCOP");
	public static final String RELATIONSHIP_SECONDARYORGANIZATION = PropertyUtil.getSchemaProperty("relationship_pgSecondaryOrganization");
	public static final String INIT_CAPS_NO = "No";
	public static final String INIT_CAPS_YES = "Yes";
	
	public static JsonObject getAPPEBOMDetails(matrix.db.Context context , String strAPPId)throws Exception{
		boolean bContextPushed=false;
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArray jsonArray = null;
		String strLanguage = context.getSession().getLanguage();
		
		//Check is LPD APP
		Map programMap = new HashMap();
		programMap.put("objectId", strAPPId);
		programMap.put("objectType", TYPE_ASSEMBLED_PRODUCT_PART);
		String []args = JPO.packArgs(programMap);
		boolean isLPDAPP = (boolean)JPO.invoke(context, "pgDSMLayeredProductUtil", null, "isLayeredProductPart", args, Object.class);
		
		if(!isLPDAPP) {
			throw new MatrixException("Only LPD APP is supported");
		}
		
		try {
			
			if(!bContextPushed)
			{					
				//Push Pop context to get BOM details . If we not use the push context we will not be getting the full BOM details . This behavior is same as the Getting BOM details in Enovia which uses push context too. 
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PERSON_USER_AGENT), null, context.getVault().getName());
				bContextPushed = true;
			}
			
			String strBusSelectAttributeList = pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.EBOM.BusSelect");
			StringList strBusSelect = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strBusSelectAttributeList))
			{
				strBusSelect = StringUtil.split(strBusSelectAttributeList, CONSTANT_STRING_PIPE);   
			}
			strBusSelect.add(SELECT_PHYSICAL_ID);
			strBusSelect.add(DomainConstants.SELECT_ID);
			
			String strRelSelectAttributeList = pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.EBOM.RelSelect");
			StringList strRelSelect = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strRelSelectAttributeList))
			{
				strRelSelect = StringUtil.split(strRelSelectAttributeList, CONSTANT_STRING_PIPE);   
			}
			strRelSelect.add(DomainRelationship.SELECT_ID);
			strRelSelect.add(CONSTANT_STRING_SELECT_FROMMID+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+CONSTANT_STRING_SELECT_RELID);
			Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_EBOM);
			
			DomainObject appObj = DomainObject.newInstance(context , strAPPId);
			MapList ebomList =appObj.getRelatedObjects(context,
					relPattern.getPattern(),
					DomainConstants.QUERY_WILDCARD,
					strBusSelect,//Object Select
					strRelSelect,//rel Select
					false,//get To
					true,//get From
					(short)1,//recurse level
					null,//where Clause
					null,
					0);	
			
			MapList eBOMFinalList = new MapList();
			Map eBOMMap;
			StringList subRelId;
			Object ebomsubsObj; 
			Iterator<Map> ebomListItr = ebomList.iterator();
			
			StringList subSelectList =subSelectList(strBusSelect);
			subSelectList.addAll(strRelSelect);
			Map tempsubMap;
			Set tempSubKeySet;
			Iterator tempSubKeySetItr;
			String strSubKey;
			String strSubValue;
			Map substituteMap;
			StringList allObjIDList= new StringList();
			while(ebomListItr.hasNext()) {
				eBOMMap =ebomListItr.next();
				eBOMMap.put("Primary-Sub", "Primary");
				eBOMMap.put("displayType", EnoviaResourceBundle.getTypeI18NString(context, (String)eBOMMap.get(DomainConstants.SELECT_TYPE), strLanguage));
				eBOMMap.put("displayState", EnoviaResourceBundle.getStateI18NString(context,DomainConstants.POLICY_EC_PART ,(String)eBOMMap.get(DomainConstants.SELECT_CURRENT), strLanguage));
				subRelId = new StringList();
				if(eBOMMap.containsKey(CONSTANT_STRING_SELECT_FROMMID+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+CONSTANT_STRING_SELECT_RELID)) {
					ebomsubsObj=  eBOMMap.get(CONSTANT_STRING_SELECT_FROMMID+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+CONSTANT_STRING_SELECT_RELID);
					if(ebomsubsObj instanceof String)
						subRelId.add((String)ebomsubsObj);
					else if(ebomsubsObj instanceof StringList)
						subRelId = (StringList)ebomsubsObj;
					eBOMMap.remove(CONSTANT_STRING_SELECT_FROMMID+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].id");
				}
				eBOMMap = updateMapForMultiValueRelationships (eBOMMap, false);
				allObjIDList.add((String)eBOMMap.get(SELECT_PHYSICAL_ID));
				String [] subRelIDArray = subRelId.toArray(new String[subRelId.size()]);
				MapList subList= DomainRelationship.getInfo(context,subRelIDArray,subSelectList);
				eBOMFinalList.add(eBOMMap);
				Iterator subListItr =subList.iterator();
				
				while(subListItr.hasNext()) {
					tempsubMap =(Map)subListItr.next();
					tempsubMap = updateMapForMultiValueRelationships (tempsubMap, true);
					tempSubKeySet =tempsubMap.keySet();
					tempSubKeySetItr=tempSubKeySet.iterator();
					substituteMap=new HashMap();
					while(tempSubKeySetItr.hasNext()) {
						strSubKey=(String)tempSubKeySetItr.next();
						strSubValue=(String)tempsubMap.get(strSubKey);
						substituteMap.put(strSubKey.replaceFirst("to.",""), strSubValue);
					}
					substituteMap.put("Primary-Sub", pgApolloConstants.STR_SUBSTITUTE);
					substituteMap.put("displayType", EnoviaResourceBundle.getTypeI18NString(context, (String)substituteMap.get(DomainConstants.SELECT_TYPE), strLanguage));
					substituteMap.put("displayState", EnoviaResourceBundle.getStateI18NString(context,DomainConstants.POLICY_EC_PART ,(String)substituteMap.get(DomainConstants.SELECT_CURRENT), strLanguage));
					allObjIDList.add((String)substituteMap.get(SELECT_PHYSICAL_ID));
					eBOMFinalList.add(substituteMap);
				}

			}
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
				bContextPushed = false;
			}
			
			MapList updatedMaplist =updateEBOMMapList(context,eBOMFinalList);
			MapList accessControlMaplist =getAccessableMaplist(context ,updatedMaplist ,allObjIDList);
			
			jsonArray =ApolloWidgetServiceUtil.convertMapListToJsonArray(accessControlMaplist);
			output.add("EBOM", jsonArray);
		}
		finally {
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
				bContextPushed = false;
			}
		}
		return output.build();
		
	}
	
	private static MapList getAccessableMaplist(Context  context , MapList eBOMFinalList , StringList allObjIDList)throws Exception{
		MapList returnList = new MapList();
		StringList slAccessableAttributeList = StringUtil.split(pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.EBOM.AccessableAttributes"),CONSTANT_STRING_PIPE);
		
		String [] objIDArray = allObjIDList.toArray(new String[allObjIDList.size()]);
		StringList slSelects = new StringList(SELECT_HAS_READ_ACCESS);
		slSelects.add(DomainConstants.SELECT_ID);
		MapList accessMapList =DomainObject.getInfo(context, objIDArray, slSelects);
		Map tempMap;
		Map accessMap = new HashMap();
		Iterator accessMapListItr =accessMapList.iterator();
		while(accessMapListItr.hasNext()) {
			tempMap =(Map)accessMapListItr.next();
			accessMap.put(tempMap.get(DomainConstants.SELECT_ID), tempMap.get(SELECT_HAS_READ_ACCESS));
		}
		Iterator eBOMFinalListItr =eBOMFinalList.iterator();
		Set keyset;
		String strKey;
		String strObjectID;
		Iterator keySetItr;
		Map returnMap;
		String strHasAccess;
		while(eBOMFinalListItr.hasNext()) {
			tempMap=(Map)eBOMFinalListItr.next();
			strObjectID =(String)tempMap.get(DomainConstants.SELECT_ID);
			strHasAccess =(String)accessMap.get(strObjectID);
			keyset =tempMap.keySet();
			keySetItr=keyset.iterator();
			returnMap = new HashMap();
			while(keySetItr.hasNext()) {
				strKey=(String)keySetItr.next();
				if((UIUtil.isNullOrEmpty(strHasAccess) || (UIUtil.isNotNullAndNotEmpty(strHasAccess) && !STR_TRUE_FLAG.equalsIgnoreCase(strHasAccess))) &&  !slAccessableAttributeList.contains(strKey)){
					returnMap.put(strKey,STR_NO_ACCESS);
				}
				else {
					returnMap.put(strKey,tempMap.get(strKey));
				}
				
			}
			returnList.add(returnMap);
		}
		return returnList;
		
	}
	
	
	private static StringList subSelectList(StringList busSelect) {
		Iterator<String> busSelectItr = busSelect.iterator();
		StringList subBusSelect = new StringList();
		String strtempSelect;
		StringBuilder sbSelectBuilder;
		while(busSelectItr.hasNext()) {
			strtempSelect =busSelectItr.next();
			sbSelectBuilder=new StringBuilder("to.");
			sbSelectBuilder.append(strtempSelect);
			subBusSelect.add(sbSelectBuilder.toString());
		}
		
		return subBusSelect;
		
	}
	
	private static MapList updateEBOMMapList(Context context , MapList ebomMaplist) throws MatrixException {
		String strLanguage = context.getSession().getLanguage();
		MapList returnList = new MapList();
		Iterator ebomMaplistItr = ebomMaplist.iterator();
		Map ebomMap;
		Map mpTemp;
		String strApplication;

		while(ebomMaplistItr.hasNext()) {
			ebomMap =(Map)ebomMaplistItr.next();
			strApplication=(String)ebomMap.get(SELECT_ATTRIBUTE_APPLICATION);
			if(UIUtil.isNotNullAndNotEmpty(strApplication)) {
				mpTemp = new HashMap();
				mpTemp.put("applications", strApplication);					
				strApplication = getIdFromPhysicalId(context,mpTemp);
				ebomMap.put(SELECT_ATTRIBUTE_APPLICATION, strApplication);
			}
			else {
				ebomMap.put(SELECT_ATTRIBUTE_APPLICATION, DomainConstants.EMPTY_STRING);
			}			
			ebomMap.put("displayType", EnoviaResourceBundle.getTypeI18NString(context, (String)ebomMap.get(DomainConstants.SELECT_TYPE), strLanguage));
			returnList.add(ebomMap);
		}
		
		return returnList;
	}


	/**
	 * Method to update Map for multi value Rel selectable
	 * @param ebomMap
	 * @param isSubstitute
	 * @return
	 */
	public static Map updateMapForMultiValueRelationships (Map ebomMap, boolean isSubstitute) {
		String strReportedFunction;
		StringList slReportedFunction;
		
		String sReportFunctionSelectable = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_PGPDTEMPLATES_TOP_GPLI_REPORTED_FUNCTION).append(pgApolloConstants.CONSTANT_STRING_SELECT_TONAME).toString();
		if(isSubstitute)
		{
			sReportFunctionSelectable = new StringBuilder("to.").append(sReportFunctionSelectable).toString();
		}		
		if(ebomMap.containsKey(sReportFunctionSelectable))
		{
			slReportedFunction = pgApolloCommonUtil.getStringListMultiValue(ebomMap.get(sReportFunctionSelectable));
			if(!slReportedFunction.isEmpty())
			{
				strReportedFunction = StringUtil.join(slReportedFunction, pgApolloConstants.CONSTANT_STRING_COMMA);
			}
			else
			{
				strReportedFunction = DomainConstants.EMPTY_STRING;
			}
			
			if(UIUtil.isNotNullAndNotEmpty(strReportedFunction)) {
				
				ebomMap.put(STR_MAP_RESULT_KEY_REPORTEDFUNCTION, strReportedFunction);
			}
			else {
				ebomMap.put(STR_MAP_RESULT_KEY_REPORTEDFUNCTION, DomainConstants.EMPTY_STRING);
			}
			
			ebomMap.remove(sReportFunctionSelectable);
		}	
		
		return ebomMap;
	}
	
	public static String getIdFromPhysicalId(Context context , Map argsMap) throws FrameworkException
	{
		StringBuilder strBuilder = new StringBuilder();
		String strApplications = (String) argsMap.get("applications");
		StringList selectList = new StringList(DomainConstants.SELECT_NAME);
		StringList appList = new StringList(StringUtil.split(strApplications, ","));
		String[] valueArray = appList.toArray(new String[appList.size()]);
		MapList mapApplications = DomainObject.getInfo(context, valueArray, selectList);
		Iterator it = mapApplications.iterator();
		Map objMap = null;
		while(it.hasNext()){
			objMap = (Map<?,?>) it.next();
			strBuilder.append(objMap.get(DomainConstants.SELECT_NAME));
			if(it.hasNext())
				strBuilder.append(",");
		}
		return strBuilder.toString();
	}
	
	
	public static JsonObject getPropertyForAPP (matrix.db.Context context , String strAPPId)throws Exception{

		JsonObjectBuilder output = Json.createObjectBuilder();
		
		//Check is LPD APP
		Map programMap = new HashMap();
		programMap.put("objectId", strAPPId);
		programMap.put("objectType", TYPE_ASSEMBLED_PRODUCT_PART);
		String []args = JPO.packArgs(programMap);
		boolean isLPDAPP = (boolean)JPO.invoke(context, "pgDSMLayeredProductUtil", null, "isLayeredProductPart", args, Object.class);

		if(!isLPDAPP) {
			throw new MatrixException("Only LPD APP is supported");
		}
		
		
		StringList multiValueSelectList = new StringList(4);
		String strPrimaryOrgMultiSelectRel= new StringBuilder(CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PRIMARYORGANIZATION).append(CONSTANT_STRING_SELECT_TONAME).toString();
		String strSecondaryOrgMultiSelectRel= new StringBuilder(CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_SECONDARYORGANIZATION).append(CONSTANT_STRING_SELECT_TONAME).toString();
		String strIntendedMarketMultiSelectRel= new StringBuilder(CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PG_INTENDED_MARKETS).append(CONSTANT_STRING_SELECT_TONAME).toString();
		String strReportedFunctionMultiSelectRel= new StringBuilder(CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_PGPDTEMPLATES_TOP_GPLI_REPORTED_FUNCTION).append(CONSTANT_STRING_SELECT_TONAME).toString();
		
		multiValueSelectList.add(strPrimaryOrgMultiSelectRel);
		multiValueSelectList.add(strSecondaryOrgMultiSelectRel);
		multiValueSelectList.add(strIntendedMarketMultiSelectRel);
		multiValueSelectList.add(strReportedFunctionMultiSelectRel);
		
		
		try {
			JsonObject attributeObject = null;
			Map attributeDetaislMap =getSelecatbleAttributeDetails(context);
			Set attributeSelectable =attributeDetaislMap.keySet();
			StringList attributeSelectableList = new StringList();
			attributeSelectableList.addAll(attributeSelectable);
			DomainObject appObject = DomainObject.newInstance(context, strAPPId);
			// Fix for 22x Upgrade MultiValueList Changes - Start
			Map attributeValueMap =appObject.getInfo(context,attributeSelectableList,multiValueSelectList);
			// Fix for 22x Upgrade MultiValueList Changes - End
			StringList strGroupSequenceList =StringUtil.split(pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.Property.AppAttributes.Group.Sequence"),CONSTANT_STRING_PIPE);
			Map groupedMap =transformAttributeMapforGrouping(context,attributeValueMap, attributeDetaislMap);
			attributeObject =ApolloWidgetServiceUtil.convertAttributeMapToJson(groupedMap,strGroupSequenceList);
			output.add("Attributes", attributeObject);
		}
		finally {
		
		}

		return output.build();
}
	
	
	private static Map getSelecatbleAttributeDetails (Context context)throws Exception{
		Map<String, Map> returnMap = new HashMap();
		StringList strAttributeGroupList = StringUtil.split(pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.Property.AppAttributes"),CONSTANT_STRING_PIPE);
		Iterator strAttributeGroupListItr = strAttributeGroupList.iterator();
		String strAttributedetails;
		StringList strAttributedetailsList;
		String strAttributeName;
		Map attributedetailsMap;
		while(strAttributeGroupListItr.hasNext()) {
			
			strAttributedetails=(String)strAttributeGroupListItr.next();
			strAttributedetailsList =StringUtil.split(strAttributedetails, CONSTANT_STRING_TILD);
			strAttributeName=strAttributedetailsList.get(0);
			attributedetailsMap= new HashMap();
			attributedetailsMap.put("Attribute", strAttributeName);
			attributedetailsMap.put("attributeDisplayName", strAttributedetailsList.get(1));
			attributedetailsMap.put("group", strAttributedetailsList.get(2));
			attributedetailsMap.put("attributetype", strAttributedetailsList.get(3));
			if(strAttributedetailsList.size()==5) {
				attributedetailsMap.put("attributeSortId", strAttributedetailsList.get(4));
			}
			else {
				attributedetailsMap.put("attributeSortId", "999");
			}
			returnMap.put(strAttributeName, attributedetailsMap);
		}
		return returnMap;
	}
	
	public static Map transformAttributeMapforGrouping(Context  context , Map attributeValueMap , Map attributeDetailsMap)throws MatrixException{
		Map attributeGroupMap = new HashMap();
		String strSeperator =new StringBuilder(CONSTANT_STRING_COMMA).append(CONSTANT_STRING_SPACE).toString();
		Set steAttributeSet = attributeDetailsMap.keySet();
		Iterator steAttributeSetItr =steAttributeSet.iterator();
		String strAttributeName;
		StringList strAttributevalueList;
		String strAttributeValue;
		String strdisplayGroup;
		Map tempAttributeMap;
		MapList attributeMapList;
		while(steAttributeSetItr.hasNext()) {
			strAttributeName =(String)steAttributeSetItr.next();	
			strAttributevalueList =pgApolloCommonUtil.getStringListFromObject(attributeValueMap.get(strAttributeName));
			strAttributeValue=StringUtil.join(strAttributevalueList,strSeperator );
			if(strAttributeName.equals(DomainConstants.SELECT_TYPE)) {
				strAttributeValue=EnoviaResourceBundle.getTypeI18NString(context, strAttributeValue, context.getSession().getLanguage());
			}
			if(strAttributeName.equals(DomainObject.getAttributeSelect(ATTR_PG_ROLLUP_NETWEIGHT_TO_COP))) {
				strAttributeValue = (strAttributeValue.equalsIgnoreCase(STR_TRUE_FLAG)) ? INIT_CAPS_YES : INIT_CAPS_NO;
			}
			tempAttributeMap =(Map)attributeDetailsMap.get(strAttributeName);
			strdisplayGroup=(String)tempAttributeMap.get("group");
			tempAttributeMap.put("value", strAttributeValue);
			if(attributeGroupMap.containsKey(strdisplayGroup)) {
				attributeMapList =(MapList)attributeGroupMap.get(strdisplayGroup);
				attributeMapList.add(tempAttributeMap);

			}
			else {
				attributeMapList = new MapList();
				attributeMapList.add(tempAttributeMap);

			}
			attributeGroupMap.put(strdisplayGroup, attributeMapList);
		}
		return attributeGroupMap;
		}
	
	
	/**
	 * Method to get Basic Object Info
	 * @param context
	 * @param sPartId
	 * @return
	 * @throws Exception
	 */
	public static JsonObject getObjectBasicInfo (matrix.db.Context context , String sPartId)throws Exception {		
		
		JsonObjectBuilder output = Json.createObjectBuilder();			
		try
		{
			JsonObjectBuilder itemsObject = Json.createObjectBuilder();
			JsonArrayBuilder payloadDataArray = Json.createArrayBuilder();
			JsonObject objectInfo = null;

			if(UIUtil.isNotNullAndNotEmpty(sPartId))
			{
				
				StringList slObjectSelectable = new StringList();
				slObjectSelectable.add(DomainConstants.SELECT_TYPE);
				slObjectSelectable.add(DomainConstants.SELECT_NAME);
				slObjectSelectable.add(DomainConstants.SELECT_REVISION);
				slObjectSelectable.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				slObjectSelectable.add(DomainConstants.SELECT_CURRENT);
				slObjectSelectable.add(DomainConstants.SELECT_OWNER);
				slObjectSelectable.add(DomainConstants.SELECT_ORIGINATED);
				
				if(UIUtil.isNotNullAndNotEmpty(sPartId))
				{
					DomainObject doObject = DomainObject.newInstance(context, sPartId);

					Map mapObjetInfo = doObject.getInfo(context, slObjectSelectable);
					String personCTX = PersonUtil.getDefaultSecurityContext(context, context.getUser());

					mapObjetInfo.put("contextId", "ctx::"+personCTX);
					
					objectInfo = PGWidgetUtil.getJSONFromMap(context, mapObjetInfo);	

					payloadDataArray.add(objectInfo);
					
					itemsObject.add("items", payloadDataArray);
					
					output.add("data", itemsObject.build());
					
				}

			}	
			
		}
		catch(Exception ex) {
			  loggerTrace.error(ex.getMessage(), ex);
			  throw ex;
		}
		return output.build();
	
	}
}
