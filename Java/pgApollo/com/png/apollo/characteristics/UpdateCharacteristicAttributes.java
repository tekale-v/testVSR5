/*
 * Added by APOLLO Team
 * For Characteristic Related Webservice
 */

package com.png.apollo.characteristics;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.enovia.characteristic.util.JsonUtil;
import com.dassault_systemes.enovia.criteria.util.CriteriaConstants;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloConstants;
import com.dassault_systemes.enovia.characteristic.model.Characteristic;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;

@Path("UpdateCharacteristicAttributes")
public class UpdateCharacteristicAttributes extends RestService {
	private static final org.apache.log4j.Logger loggerTrace = org.apache.log4j.Logger.getLogger(UpdateCharacteristicAttributes.class);

	/**
	 * Method to update Characteristic Specifics
	 * @param req
	 * @param strValue
	 * @param strID
	 * @return
	 * @throws Exception
	 */
	@Path("/updateCharacteristicSpecifics")
	@GET
	public Object updateCharacteristicSpecifics(@Context HttpServletRequest req, @FormParam("value") String strValue,@FormParam("id") String strID ) throws Exception
	{
		String output = "failed";
		try {
			matrix.db.Context ctx = authenticate(req);
			if (UIUtil.isNotNullAndNotEmpty(strID)) {
				DomainObject dobj = DomainObject.newInstance(ctx, strID);
				dobj.setAttributeValue(ctx, pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS, strValue);
				output = "SUCCESS";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}		
		return Response.status(200).entity(output).build();
	}

	//PG APOLLO PDT Changes - Added for updating Characteristic value - Start
	/**
	 * Method to update Characteristic
	 * @param req
	 * @param strValue
	 * @param strId
	 * @return
	 * @throws Exception
	 */
	@Path("/updateCharacteristic")
	@GET
	public Object updateCharacteristic(@Context HttpServletRequest req, @FormParam("value") String strValue,@FormParam("id") String strId ) throws Exception
	{
		String output = "failed";
		try {
			matrix.db.Context ctx = authenticate(req);
			if (UIUtil.isNotNullAndNotEmpty(strId)) {
				DomainObject dobj = DomainObject.newInstance(ctx, strId);
				dobj.setAttributeValue(ctx, DomainConstants.ATTRIBUTE_TITLE, strValue);
				output = "SUCCESS";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}		
		return Response.status(200).entity(output).build();
	}
	//PG APOLLO PDT Changes - Added for updating Characteristic value - End
	/**
	 * 	Method to update Characteristic attributes
	 * @param req
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@Path("/updateCharacteristicAttributes")	
	@POST
	public Object updateCharacteristicAttributes(@Context HttpServletRequest req, String paramString ) throws Exception
	{
		String output = "failed";    
		matrix.db.Context ctx = authenticate(req);
		try{
			JsonObject localJsonObject = JsonUtil.toJsonObject(paramString);
			String objectId = localJsonObject.getString("objectid");
			DomainObject doObj = null;
			Map mAttributeData = new HashMap();
		
			if (UIUtil.isNotNullAndNotEmpty(objectId)) {
				doObj = DomainObject.newInstance(ctx, objectId);				
				String charspecifics = localJsonObject.getString("charspecifics");
				mAttributeData.put(pgApolloConstants.ATTRIBUTE_PGCHARSPECIFICS, charspecifics);
				String actionrequired = localJsonObject.getString("actionrequired");
				mAttributeData.put(pgApolloConstants.ATTRIBUTE_PGACTIONREQUIRED, actionrequired);
				String reporttype = localJsonObject.getString("reporttype");
				mAttributeData.put(pgApolloConstants.ATTRIBUTE_PGREPORTTYPE, reporttype);
				String lowerSpecLimit = localJsonObject.getString("lowerSpecLimit");
				String lowerRoutineRelease = localJsonObject.getString("lowerRoutineRelease");
				String upperSpecLimit = localJsonObject.getString("upperSpecLimit");
				String upperRoutineRelease = localJsonObject.getString("upperRoutineRelease");
				
	            Characteristic charObjPLMParam = new Characteristic(ctx, objectId, true);
	            charObjPLMParam.setSpecificationLimitValues(lowerSpecLimit, upperSpecLimit);
	            charObjPLMParam.setRoutineReleaseLimitValues(lowerRoutineRelease, upperRoutineRelease);
	            charObjPLMParam.commit(ctx);
	            //APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific starts
				//String application = localJsonObject.getString("application");
				String designSpecifics = localJsonObject.getString("designSpecifics");
				//mAttributeData.put(pgApolloConstants.ATTRIBUTE_PGAPPLICATION, application);
				mAttributeData.put(pgApolloConstants.ATTRIBUTE_PG_DESIGNSPECIFICS, designSpecifics);
				//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific ends
				//APOLLO 2018x.6 A10-925 - New attribute on Characteristic for Category Specifics - starts
				String categorySpecifics = localJsonObject.getString("categorySpecifics"); 
				mAttributeData.put(pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS, categorySpecifics);
				//APOLLO 2018x.6 A10-925 - New attribute on Characteristic for Category Specifics - ends
				doObj.setAttributeValues(ctx, mAttributeData);
				output = "SUCCESS";
			}
		}catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		
		return Response.status(200).entity(output).build();
	}	
	/**
	 * Method to update Release Criteria
	 * @param req
	 * @param strValue
	 * @param strID
	 * @return
	 * @throws Exception
	 */
	@Path("/updateReleaseCriteria")
	@GET
	public Object updateReleaseCriteria(@Context HttpServletRequest req, @FormParam("value") String strValue,@FormParam("id") String strID ) throws Exception
	{
		String output = "failed";
		try {
			matrix.db.Context ctx = authenticate(req);
			if (UIUtil.isNotNullAndNotEmpty(strID)) {
				DomainObject dobj = DomainObject.newInstance(ctx, strID);
				dobj.setAttributeValue(ctx, pgApolloConstants.ATTRIBUTE_PGRELEASECRITERIA, strValue);
				output = "SUCCESS";				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return Response.status(200).entity(output).build();
	}
	/**
	 * Method to update Structured Release Criteria
	 * @param req
	 * @param strItemID
	 * @return
	 * @throws Exception
	 */
	@Path("/getStructuredReleaseCriteria")
	@GET
	public Object getStructuredReleaseCriteria(@Context HttpServletRequest req, @FormParam("itemId") String strItemID ) throws Exception
	{
		String output = "failed";
		try {
			matrix.db.Context ctx = authenticate(req);
			if (UIUtil.isNotNullAndNotEmpty(strItemID)) {
				DomainObject dobj = DomainObject.newInstance(ctx, strItemID);
				output = dobj.getInfo(ctx, "attribute["+ pgApolloConstants.ATTRIBUTE_PG_STRUCTURED_RELEASE_CRITERIA_REQUIRED + "]");				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return Response.status(200).entity(output).build();
	}
	
	//PG APOLLO ALM Requirement 31404 - Add new column on Characteristic Page to show Criteria Title - Start
	/**
	 * Method to Characteristic Master/Criteria Details
	 * @param req
	 * @param strObjectId
	 * @return
	 * @throws Exception
	 */
	@Path("/getCharacteristicMasterCriteria")
	@GET
	public Object getCharacteristicMasterCriteria(@Context HttpServletRequest req, @FormParam("objectId") String strObjectId ) throws Exception
	{
		StringBuffer output = new StringBuffer();
		try 
		{
			matrix.db.Context ctx = authenticate(req);
			
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) 
			{
				DomainObject dobj = DomainObject.newInstance(ctx, strObjectId);				
							
				//If Loop is used for Export Characteristic Functionality
				if(dobj.isKindOf(ctx, pgApolloConstants.TYPE_ASSEMBLED_PRODUCT_PART))
				{			
					StringList slFinalOut = new StringList();
					
					String keyCharId;
					Map mpIDToCriteriaID;
					Map mapCMCriteriaForEachChar;
					
					Map mapCharIdToCMCriteria = getFilteredCMCriteriaMap(ctx, dobj);					
					
					mpIDToCriteriaID = (Map)mapCharIdToCMCriteria.get(CriteriaConstants.CRITERIA);
					
					if(!mapCharIdToCMCriteria.isEmpty() && null != mpIDToCriteriaID && !mpIDToCriteriaID.isEmpty())
					{	
						StringList slTitle;
						StringList slCriteriaHyperLink = null;
						String strCharMasterName;
						String strCharMasterNameHyperlink;
						String strCharMasterRev;
						
						Set<String> keySet = mpIDToCriteriaID.keySet();
						Iterator<String> itrKey = keySet.iterator();

						while (itrKey.hasNext()) 
						{
							keyCharId = itrKey.next();	
							mapCMCriteriaForEachChar = (Map)mapCharIdToCMCriteria.get(keyCharId);
							slTitle = (StringList)mapCMCriteriaForEachChar.get("CriteriaTitleList");
							slCriteriaHyperLink = (StringList)mapCMCriteriaForEachChar.get("CriteriaHyperLinkList");
							strCharMasterNameHyperlink = (String)mapCMCriteriaForEachChar.get("CMHyperLink");
							strCharMasterName = (String)mapCMCriteriaForEachChar.get("CMName");
							strCharMasterRev = (String)mapCMCriteriaForEachChar.get("CMRevision");

							slFinalOut.addElement(new StringBuilder(keyCharId)
									.append(pgApolloConstants.CONSTANT_STRING_HASH)
									.append(StringUtil.join(slTitle, pgApolloConstants.CONSTANT_STRING_PIPE))
									.append(pgApolloConstants.CONSTANT_STRING_HASH)
									.append(StringUtil.join(slCriteriaHyperLink, pgApolloConstants.CONSTANT_STRING_PIPE))
									.append(pgApolloConstants.CONSTANT_STRING_HASH).append(strCharMasterNameHyperlink)
									.append(pgApolloConstants.CONSTANT_STRING_HASH).append(strCharMasterName)
									.append(pgApolloConstants.CONSTANT_STRING_HASH).append(strCharMasterRev).toString());						
						}
						
						output.append(StringUtil.join(slFinalOut, "$"));
					}					
					
				}
				
			}
		} 
		catch (Exception e) 
		{
			loggerTrace.error(e.getMessage() ,e);	
			throw e;
		}
		return Response.status(200).entity(output.toString()).build();
	}

	
	/**
	 * Method to get Filtered CM Criteria Map
	 * @param ctx
	 * @param dobj
	 * @return
	 * @throws Exception
	 */
	public static Map getFilteredCMCriteriaMap(matrix.db.Context ctx, DomainObject dobj)	throws Exception 
	{
		StringBuffer strWhereClauseBuffer = new StringBuffer();
		strWhereClauseBuffer = strWhereClauseBuffer.append("to[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].attribute[").append(pgApolloConstants.ATTRIBUTE_EVALUATED_CRITERIA).append("]");
		String strSelectEvaluatedCriteria = strWhereClauseBuffer.toString();				
		//Apollo 2018x.6 A10-608 - Starts
		Map mapCharInfo = null;
		String strSelectCharMasterId = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMID).toString();
		//Apollo 2018x.6 A10-608 - Ends
		StringList slSelectable = new StringList(5);
		slSelectable.addElement(DomainConstants.SELECT_ID);
		slSelectable.addElement(strSelectEvaluatedCriteria);
		//Apollo 2018x.6 A10-608 - Starts
		slSelectable.addElement(strSelectCharMasterId);
		//Apollo 2018x.6 A10-608 - Ends
		//Apollo 2018x.6 A10-914 - Starts
		String strSelectCharMasterName = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMNAME).toString();
		slSelectable.addElement(strSelectCharMasterName);
		//Apollo 2018x.6 A10-914 - Ends
		//Apollo 2018x.6 A10-940 - Starts
		String strSelectCharMasterRev = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMREVISION).toString();
		slSelectable.addElement(strSelectCharMasterRev);
		//Apollo 2018x.6 A10-940 - Ends
		
		StringList slCriteriaPhysicalIds = new StringList();

		Map mpCriteriaTitlemap = new HashMap();
		
		String sPlmParameterObjectId = DomainObject.EMPTY_STRING;
		Map mpIDToCriteriaID = new HashMap();				
		
		MapList mlConnectedObjects = dobj.getRelatedObjects(ctx,			// Context
															pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION, //Relationship Pattern
															pgApolloConstants.TYPE_PLM_PARAMETER, //Type Pattern
															slSelectable, //Object selects
															null,		  //relationship selects
															false,		  //to direction
															true,		  //from direction
															(short)1,	  //recursion level
															null,		  //object where clause
															null,		  //relationship where clause
															0);			// limit
		
		
		//Get the list of all Criteria Physical Ids for every PlmParameter object connected
		if(null != mlConnectedObjects && !mlConnectedObjects.isEmpty())
		{						
			Map mTemp = new HashMap();
			StringList slCriterialTempIdList = new StringList();
			Object tempObject = null;
			for(int iCount=0; iCount<mlConnectedObjects.size(); iCount++)
			{
				mTemp = new HashMap();
				mTemp = (Map) mlConnectedObjects.get(iCount);							
				sPlmParameterObjectId = (String)mTemp.get(DomainObject.SELECT_ID);							
				slCriterialTempIdList = new StringList();
				if(mTemp.containsKey(strSelectEvaluatedCriteria))
				{
					tempObject = mTemp.get(strSelectEvaluatedCriteria);    
					if ((tempObject instanceof StringList)) {
						slCriterialTempIdList = (StringList)tempObject;
					} else if ((tempObject instanceof String) && !tempObject.toString().isEmpty()) {
						slCriterialTempIdList.add((String)tempObject);
					}								
					slCriteriaPhysicalIds.addAll(slCriterialTempIdList);								
				}
				//Apollo 2018x.6 A10-608 - Starts
				mTemp.put(strSelectEvaluatedCriteria, slCriterialTempIdList);
				mpIDToCriteriaID.put(sPlmParameterObjectId, mTemp);
				//Apollo 2018x.6 A10-608 - Ends
			}
		}
		
		Map mapCharacteristicCriteriaMappedDetails = getCharacteristicCriteriaMappedDetails(ctx, slCriteriaPhysicalIds, mpIDToCriteriaID);		
		
		Map mapCharIdToCMCriteria = getCMandCriteriaDetailsForEachChar(mpIDToCriteriaID, mapCharacteristicCriteriaMappedDetails);
		mapCharIdToCMCriteria.put(CriteriaConstants.CRITERIA, mpIDToCriteriaID);
		return mapCharIdToCMCriteria;
	}

	

	/**
	 * Method to get CM and Criteria Details for each Char
	 * @param mpIDToCriteriaID
	 * @param mpCriteriaTitlemap
	 * @return
	 */
	private static Map getCMandCriteriaDetailsForEachChar(Map mpIDToCriteriaID,	Map mapCharacteristicCriteriaMappedDetails) 
	{
		Map mapCharIdToCMCriteria = new HashMap();
		Map mapCharInfo;
		String keyCharId;
		Map mapCMCriteriaForEachChar;
		String strSelectCharMasterId = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMID).toString();
		String strSelectCharMasterName = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMNAME).toString();
		String strSelectCharMasterRev = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMREVISION).toString();

		StringBuffer strWhereClauseBuffer = new StringBuffer();
		strWhereClauseBuffer = strWhereClauseBuffer.append("to[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].attribute[").append(pgApolloConstants.ATTRIBUTE_EVALUATED_CRITERIA).append("]");
		String strSelectEvaluatedCriteria = strWhereClauseBuffer.toString();
		MapList mlCriteriaList;
		Map mapCriteria;
		String sCriteriaPhysicalId;
		String sCriteriaTitle;
		
		//Prepare final output
		if(null != mpIDToCriteriaID && !mpIDToCriteriaID.isEmpty())
		{							
			StringList slCriteriaId = new StringList();
			StringList slTitle = new StringList();
			//Apollo 2018x.6 A10-608 - Starts
			StringList slCriteriaHyperLink = null;
			//Apollo 2018x.6 A10-608 - Ends
			Set<String> keySet = mpIDToCriteriaID.keySet();
			Iterator<String> itrKey = keySet.iterator();
			//Apollo 2018x.6 A10-914 - Starts
			String strCharMasterId;
			String strCharMasterName;
			String strCharMasterNameHyperlink;
			//Apollo 2018x.6 A10-914 - Ends
			//Apollo 2018x.6 A10-940 - Starts
			String strCharMasterRev;
			//Apollo 2018x.6 A10-940 - Ends
			while (itrKey.hasNext()) 
			{
				keyCharId = itrKey.next();							
				slCriteriaId = new StringList();
				slTitle = new StringList();
				//Apollo 2018x.6 A10-608 - Starts
				slCriteriaHyperLink = new StringList();
				mapCharInfo = (Map) mpIDToCriteriaID.get(keyCharId);
				slCriteriaId = (StringList) mapCharInfo.get(strSelectEvaluatedCriteria);
				//Apollo 2018x.6 A10-940 - Starts
				strCharMasterRev = DomainConstants.EMPTY_STRING;
				//Apollo 2018x.6 A10-940 - Ends
				//Apollo 2018x.6 A10-914 - Starts
				strCharMasterName = DomainConstants.EMPTY_STRING;
				strCharMasterNameHyperlink = DomainConstants.EMPTY_STRING;
				if(mapCharInfo.containsKey(strSelectCharMasterId) && mapCharInfo.containsKey(strSelectCharMasterName))
				{								
					strCharMasterId = (String) mapCharInfo.get(strSelectCharMasterId);							
					strCharMasterName = (String) mapCharInfo.get(strSelectCharMasterName);
					//Apollo 2018x.6 A10-940 - Starts
					strCharMasterRev = (String) mapCharInfo.get(strSelectCharMasterRev);
					//Apollo 2018x.6 A10-940 - Ends
					strCharMasterNameHyperlink = new StringBuilder("<a href=\"javascript:window.open('../../common/emxTree.jsp?mode=insert&objectId=").append(strCharMasterId).append("','600','400','false','popup');\" >").append(strCharMasterName).append("</a>").toString();
				}
				
				//Apollo 2018x.6 A10-914 - Ends
				//Apollo 2018x.6 A10-608 - Ends	
				
				mlCriteriaList = (MapList)mapCharacteristicCriteriaMappedDetails.get(keyCharId);
				
				if(null != mlCriteriaList && !mlCriteriaList.isEmpty())
				{
					for(Object objMap : mlCriteriaList)
					{
						mapCriteria = (Map)objMap;
						sCriteriaPhysicalId = (String)mapCriteria.get(DomainConstants.SELECT_PHYSICAL_ID);
						sCriteriaTitle = (String)mapCriteria.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);

						if(UIUtil.isNotNullAndNotEmpty(sCriteriaPhysicalId))
						{
							slTitle.add(sCriteriaTitle) ;
							//Apollo 2018x.6 A10-608 - Starts
							slCriteriaHyperLink.add(("<a href=\"javascript:window.open('"+new StringBuilder("../../common/emxTree.jsp?mode=insert&objectId=").append(sCriteriaPhysicalId).toString()+"','600','400','false','popup');\" >"+sCriteriaTitle+"</a>"));
							//Apollo 2018x.6 A10-608 - Ends
						}
					}
				}						
				
				mapCMCriteriaForEachChar = new HashMap();
				mapCMCriteriaForEachChar.put("CriteriaTitleList", slTitle);
				mapCMCriteriaForEachChar.put("CriteriaHyperLinkList", slCriteriaHyperLink);
				mapCMCriteriaForEachChar.put("CMHyperLink", strCharMasterNameHyperlink);
				mapCMCriteriaForEachChar.put("CMName", strCharMasterName);
				mapCMCriteriaForEachChar.put("CMRevision", strCharMasterRev);
				
				mapCharIdToCMCriteria.put(keyCharId, mapCMCriteriaForEachChar);				
			}			
			
		}
		
		return mapCharIdToCMCriteria;
	}

	/**
	 * Method to get Filtered Applicable Criteria Details
	 * @param mapAllCriteriaDetails
	 * @param slCriteriaId
	 * @return
	 */
	private static MapList getFilterdApplicableCriteriaDetails(Map mapAllCriteriaDetails, StringList slCriteriaId) 
	{
		MapList mlReturnList = new MapList();

		if(null != slCriteriaId && !slCriteriaId.isEmpty())
		{
			Map mapLocalCriteria;
			
			MapList mlLocalReturnList = new MapList();

			for(String sCriteriaId : slCriteriaId)
			{
				mapLocalCriteria = (Map)mapAllCriteriaDetails.get(sCriteriaId);				
				if(null != mapLocalCriteria && !mapLocalCriteria.isEmpty())
				{
					mlLocalReturnList.add(mapLocalCriteria);
				}
			}
			
			mlLocalReturnList.sort(DomainConstants.SELECT_ORIGINATED,"descending", "date");

			Map mapFinalMap;
			String sLogicalId;
			Set<String> setLogicalId = new HashSet();
			
			for(Object objMap : mlLocalReturnList)
			{
				mapFinalMap = (Map)objMap;				
				sLogicalId = (String)mapFinalMap.get(pgApolloConstants.SELECT_LOGICAL_ID);
				
				if(!setLogicalId.contains(sLogicalId))
				{
					setLogicalId.add(sLogicalId);
					mlReturnList.add(mapFinalMap);
				}
			}
		}
		
		return mlReturnList;
	}

	/**
	 * Method to get Characteristic and associated Criteria Details
	 * @param ctx
	 * @param slCriteriaPhysicalIds
	 * @param mpIDToCriteriaID
	 * @return
	 * @throws FrameworkException
	 */
	private static Map getCharacteristicCriteriaMappedDetails(matrix.db.Context ctx, StringList slCriteriaPhysicalIds, Map mpIDToCriteriaID) throws FrameworkException 
	{
		Map mapCharInfo;
		Map mapAllCriteriaDetails = new HashMap();
		
		StringBuffer sbEvaluateCriteriaSelectable = new StringBuffer();
		sbEvaluateCriteriaSelectable = sbEvaluateCriteriaSelectable.append("to[").append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append("].attribute[").append(pgApolloConstants.ATTRIBUTE_EVALUATED_CRITERIA).append("]");
		String sSelectEvaluatedCriteria = sbEvaluateCriteriaSelectable.toString();	
		
		StringList slCriteriaSelectable = new StringList(4);
		slCriteriaSelectable.add(pgApolloConstants.SELECT_PHYSICAL_ID);
		slCriteriaSelectable.add(pgApolloConstants.SELECT_LOGICAL_ID);
		slCriteriaSelectable.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slCriteriaSelectable.add(DomainConstants.SELECT_ORIGINATED);
		
		if(!slCriteriaPhysicalIds.isEmpty())
		{
			MapList mlObjectList = DomainObject.getInfo(ctx, slCriteriaPhysicalIds.toArray(new String[slCriteriaPhysicalIds.size()]), slCriteriaSelectable);
			if(null != mlObjectList && !mlObjectList.isEmpty())
			{	
				Map mapCriteria;
				String sCriteriaPhysicalId;
				
				for(Object objMap : mlObjectList)
				{
					mapCriteria = (Map)objMap;
					sCriteriaPhysicalId = (String)mapCriteria.get(pgApolloConstants.SELECT_PHYSICAL_ID);					
					mapAllCriteriaDetails.put(sCriteriaPhysicalId, mapCriteria);
				}				
			}

		}
		
		MapList mlLocalCriteriaList;
		Map mapCharacteristicCriteriaMappedDetails = new HashMap();
		
		if(!mpIDToCriteriaID.isEmpty())
		{							
			StringList slCriteriaId;
			
			Set<String> keySet = mpIDToCriteriaID.keySet();
			Iterator<String> itrKey = keySet.iterator();
			String sKeyCharId;
			
			while (itrKey.hasNext()) 
			{
				sKeyCharId = itrKey.next();							
				mapCharInfo = (Map) mpIDToCriteriaID.get(sKeyCharId);
				slCriteriaId = (StringList) mapCharInfo.get(sSelectEvaluatedCriteria);
				mlLocalCriteriaList = getFilterdApplicableCriteriaDetails(mapAllCriteriaDetails, slCriteriaId);
				mapCharacteristicCriteriaMappedDetails.put(sKeyCharId, mlLocalCriteriaList);
			}
		}
		return mapCharacteristicCriteriaMappedDetails;
	}	
	//PG APOLLO A10-352 ALM Requirement 31404 - Add new column on Characteristic Page to show Criteria name - End
	
	//PG APOLLO - A10-368 PDT Changes - Added to fetch Spell Check URL from emxSystem.properties page - Start
	/**
	 * Method to check get spell checker URL
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@Path("/getSpellCheckURL")
	@GET
	public Object getSpellCheckURL(@Context HttpServletRequest req) throws Exception
	{
		StringBuffer output = new StringBuffer();
		try {
			matrix.db.Context ctx = authenticate(req);
			output.append("../").append(EnoviaResourceBundle.getProperty(ctx, "emxSystem", ctx.getLocale(),"emxFramework.spellchecker.URL"));			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}		
		return Response.status(200).entity(output.toString()).build();
	}
	//PG APOLLO - A10-368 PDT Changes - Added to fetch Spell Check URL from emxSystem.properties page - End
	
	//Apollo 2018x.6 ALM 37109 A10-722 - Starts
	/**
	 * Method to check context user has Part and Specification Admin role or not.
	 * @param req
	 * @return
	 * @throws Exception
	 */
	@Path("/getAccessOnCharacteristic")
	@GET
	public Object getAccessOnCharacteristic(@Context HttpServletRequest req) throws MatrixException, IOException
	{
		String strReturn = pgApolloConstants.STR_FALSE_FLAG;
		matrix.db.Context context = authenticate(req);
		if(context.isAssigned(pgApolloConstants.ROLE_PARTANDSPECIFICATIONADMIN))
		{
			strReturn = pgApolloConstants.STR_TRUE_FLAG;
		}		
		return Response.status(200).entity(strReturn).build();
	}
	//Apollo 2018x.6 ALM 37109 A10-722 - Ends
}
