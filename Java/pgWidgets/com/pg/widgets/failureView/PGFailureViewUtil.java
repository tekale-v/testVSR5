/*
 * PGFailureViewUtil.java
 * 
 * Added by DSM(DS) Team
 * For WD Failure View Widget related Web service.
 * 
 */

package com.pg.widgets.failureView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.aspose.slides.exceptions.Exception;
import com.dassault_systemes.enovia.criteria.util.CriteriaConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
//Changes for 22x Upgrade Start - Replacing PLMHistoryService API with HistoryAuditTrailService
import com.dassault_systemes.enovia.dcl.service.HistoryAuditTrailService; 
import com.dassault_systemes.enovia.dcl.DCLServiceUtil;
//Changes for 22x Upgrade End - Replacing PLMHistoryService API with HistoryAuditTrailService
import com.png.apollo.pgApolloConstants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

/**
 * Class PGFailureViewUtil has all the methods defined for the 'Failure View' widget activities.
 * 
 * @Since 2018x.6 OCt Release
 * @author 
 *
 */
class PGFailureViewUtil 
{
	
	private static final String HALB = "HALB";
	private static final String SHIPPABLE_HALB = "Shippable HALB";
	private static final String PRODUCTION = "Production";
	private static final String STR_PIPE = "|";
	private static final String FROM = "from[";
	private static final String OBJECT_ID = "objectId";
	private static final String STRING_KO = "KO";
	private static final String STRING_MSG = "msg";
	private static final String STRING_DATA = "data";
	private static final String STRING_OK = "OK";
	private static final String STR_COMMA = ",";
	private static final String STATE_PRELIMINARY = "Preliminary";
	private static final String STATE_RELEASE = "Release";
	private static final String ROLE_RESTRICTEDPRODUCTDATAORIGINATOR = PropertyUtil.getSchemaProperty(null,"role_IPMRestrictedOriginator");
	private static final String VAULT_ESERVICE_PRODUCTION = "eService Production";
	private static final String ATTR_PG_ERRORCLASSIFICATION = PropertyUtil.getSchemaProperty(null,"attribute_pgErrorClassification");
	private static final String REL_PG_PRIMARY_ORGANIZATION = PropertyUtil.getSchemaProperty(null,"relationship_pgPrimaryOrganization");
	private static final String ATTR_PG_WD_ERROR_DATE = PropertyUtil.getSchemaProperty(null,"attribute_pgWDErrorDate");
	private static final String ATTR_PG_WD_ERROR_MESSAGE = PropertyUtil.getSchemaProperty(null,"attribute_pgWDErrorMessage");
	private static final String ATTR_RELEASE_PHASE = PropertyUtil.getSchemaProperty(null,"attribute_ReleasePhase");
	private static final String ATTR_PG_ORIGINATINGSOURCE = PropertyUtil.getSchemaProperty(null,"attribute_pgOriginatingSource");
	private static final String ATTR_PG_SAP_TYPE= PropertyUtil.getSchemaProperty(null,"attribute_pgSAPType");
	private static final String ATTR_PG_WND_EXCP_CMT= PropertyUtil.getSchemaProperty(null,"attribute_pgWnDExcpCmt");
	private static final String REL_PG_WND_EXCP_SUPPORT_DOC= PropertyUtil.getSchemaProperty(null,"relationship_pgWnDExcpSupportDoc");
	private static final String ATTR_PG_WND_VAL_EXCP= PropertyUtil.getSchemaProperty(null,"attribute_pgWnDValExcp");
	private static final String VAULT_PRODUCTION_SELECT = "eService Production";	
	public PGFailureViewUtil(Context context) {
		
	}
	/**This method find object as per given type.
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	public Response getDataByFind(Context context, Map<String,Object> mpRequestMap) throws FrameworkException 
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		boolean bPushContext = false;
		String typeObj =(String) mpRequestMap.get("type");
		String strState=(String) mpRequestMap.get("state");
		String strSelect=(String) mpRequestMap.get("selectable");
		try {
			output.add(STRING_MSG, STRING_KO);
			ContextUtil.startTransaction(context, false);

			StringList slObj = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strSelect))
			{
				slObj = StringUtil.split(strSelect, STR_COMMA);
			}

			slObj.add(DomainConstants.SELECT_NAME);
			slObj.add(DomainConstants.SELECT_REVISION);
			slObj.add(DomainConstants.SELECT_POLICY);
			slObj.add(DomainConstants.SELECT_CURRENT);
			slObj.add(DomainConstants.SELECT_OWNER);
			String strWherePolicy ="";
			if(UIUtil.isNotNullAndNotEmpty(strState))
			{
				strWherePolicy = "current == "+strState;
			}
			MapList mlObjs = DomainObject.findObjects(context,typeObj,VAULT_ESERVICE_PRODUCTION, strWherePolicy,slObj);

			JsonArrayBuilder outArr = Json.createArrayBuilder();
			Map<String, Object> mapObj;

			byte[] bByte = new byte[1];
			bByte[0] = 0x7;//hexadecimal value of bell character
			String sBellChar = new String(bByte);
			String strValue = DomainConstants.EMPTY_STRING;
			for(int i=0;i<mlObjs.size();i++)
			{
				mapObj = (Map<String, Object>) mlObjs.get(i);
				//Remove Bell Char Start
				for (Entry<String, Object> entry : mapObj.entrySet())  
				{
					if( entry.getValue() instanceof String)
					{ 
						strValue = (String) entry.getValue();
						mapObj.put(entry.getKey(), strValue.replace(sBellChar, ","));
					}
				}
				//Remove Bell Char End
				JsonObjectBuilder jsonObj = Json.createObjectBuilder();
				PGFailureModuler.map2JsonBuilder(jsonObj, mapObj);
				outArr.add(jsonObj);

			}
			output.add(STRING_MSG, STRING_OK);
			output.add(STRING_DATA, outArr);

			ContextUtil.commitTransaction(context);

		} catch (Exception e) {
			try {
				if (ContextUtil.isTransactionActive(context)) {
					ContextUtil.abortTransaction(context);
				}
			} catch (FrameworkException e1) {
				throw e1;

			}
			throw e;

		}
		finally {
			if(bPushContext)
			{
				ContextUtil.popContext(context);
			}
		}

		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();

	}
	/**This method find FPP and return it in response.
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	public Response getFailureViewData(Context context, Map<String, Object> mpRequestMap) throws FrameworkException  {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try
		{
			JsonArrayBuilder outArr = Json.createArrayBuilder();
			checkAccess(context);
			MapList mlObjs = new MapList();
			if(hasRestrictedProductDataOriginator(context))
			{
				mlObjs = getFailureData(context, mpRequestMap);
			}
			
			Map<String, Object> mapObj;
			for(int i=0;i<mlObjs.size();i++)
			{
				mapObj = (Map<String, Object>) mlObjs.get(i);
				JsonObjectBuilder jsonObj = Json.createObjectBuilder();
				PGFailureModuler.map2JsonBuilder(jsonObj, mapObj);

				outArr.add(jsonObj);
			}
			output.add(STRING_MSG, STRING_OK);
			output.add(STRING_DATA, outArr);
		}
		catch (Exception e) {
			try {
				if (ContextUtil.isTransactionActive(context)) {
					ContextUtil.abortTransaction(context);
				}
			} catch (FrameworkException e1) {
				return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

			}
			throw e;
		} catch (FrameworkException e) {
			throw e;
		} 
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}

	/**This method find FPP data as per criteria.
	 * @param context
	 * @param programMap
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings("unchecked")
	public MapList getFailureData(Context context, Map<String,Object> programMap) throws FrameworkException {
		MapList mlObjectList = new MapList();
		MapList returnList = new MapList();

		MapList mlFinalFPPData = new MapList();
		try{

			//Get user entered field values
			String strQueryLimit = (String) programMap.get("QueryLimit");
			String strName = (String) programMap.get("NAME");
			String strSelectedType = (String) programMap.get("Type");
			String strAge = (String) programMap.get("age");
			String strOrganization = (String) programMap.get("PG_ORGANIZATION_NAME");
			//Selectables
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_PG_WD_ERROR_DATE));
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_PG_WD_ERROR_MESSAGE));
			objectSelects.add(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE));
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_RELEASE_PHASE));
			objectSelects.add(new StringBuilder(FROM).append(REL_PG_PRIMARY_ORGANIZATION).append("].to.name").toString());
			objectSelects.add(new StringBuilder(FROM).append(REL_PG_PRIMARY_ORGANIZATION).append("].to").toString());
			objectSelects.add(DomainObject.getAttributeSelect(pgApolloConstants.ATTR_PG_ASSEMBLY_TYPE));
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_PG_SAP_TYPE));
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_PG_ERRORCLASSIFICATION));
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_PG_WND_EXCP_CMT));
			objectSelects.add(new StringBuilder(FROM).append(REL_PG_WND_EXCP_SUPPORT_DOC).append("].to.name").toString());
			objectSelects.add(new StringBuilder(FROM).append(REL_PG_WND_EXCP_SUPPORT_DOC).append("].to").toString());
			objectSelects.add(DomainObject.getAttributeSelect(ATTR_PG_WND_VAL_EXCP));
			//where condition	
			StringBuilder sbWhere = new StringBuilder();

			//Originating Source where clause
			sbWhere.append("(");
			sbWhere.append("(attribute["+ ATTR_PG_ORIGINATINGSOURCE +"]").append(" ~~ ");
			sbWhere.append("\"");
			sbWhere.append("DSO");	
			sbWhere.append("\")");
			sbWhere.append("||");

			sbWhere.append("("+DomainConstants.SELECT_TYPE).append(" == ");
			sbWhere.append("\"");
			sbWhere.append(CriteriaConstants.CRITERIA);	
			sbWhere.append("\")");
			sbWhere.append(")");

			//Organization where clause
			if(!UIUtil.isNullOrEmpty(strOrganization))
			{
				if(strOrganization.contains(STR_PIPE))
				{
					sbWhere.append(" ");
					sbWhere.append("&&");
					sbWhere.append(" ");
					StringList slOrganizationWhr = StringUtil.split(strOrganization, STR_PIPE);
					String[] objectOrganizationArray = slOrganizationWhr.toArray(new String[slOrganizationWhr.size()]);
					sbWhere.append("(");
					for(int i=0;i<objectOrganizationArray.length;i++)
					{
						sbWhere.append("(from[");
						sbWhere.append(REL_PG_PRIMARY_ORGANIZATION);
						sbWhere.append("].to ~~ \"");
						sbWhere.append(objectOrganizationArray[i]);
						sbWhere.append("\")");
						if(((i+1)!=objectOrganizationArray.length))
							sbWhere.append("||");
					}
					sbWhere.append(")");

				} else {
					sbWhere.append(" ");
					sbWhere.append("&&");
					sbWhere.append(" ");
					sbWhere.append("(from[");
					sbWhere.append(REL_PG_PRIMARY_ORGANIZATION);
					sbWhere.append("].to ~~ \"");
					sbWhere.append(strOrganization);
					sbWhere.append("\")");			
				}
			}
			sbWhere.append(" ");
			sbWhere.append("&&");
			sbWhere.append(" ");
			sbWhere.append(DomainObject.getAttributeSelect(ATTR_PG_WD_ERROR_MESSAGE));
			sbWhere.append(" != ");
			sbWhere.append("\"\"");
			
			if(UIUtil.isNullOrEmpty(strName))
			{
				strName=DomainConstants.QUERY_WILDCARD;
			}

			if(UIUtil.isNullOrEmpty(strQueryLimit))
			{
				strQueryLimit = EnoviaResourceBundle.getProperty(context,"emxFramework.Search.QueryLimit");
			}

			mlObjectList = DomainObject.findObjects(context,
					 strSelectedType,						//The query type pattern
					 strName.replace(STR_PIPE,STR_COMMA),  //The query name pattern
					 DomainConstants.QUERY_WILDCARD, //The query revision pattern
					 DomainConstants.QUERY_WILDCARD,//The query owner pattern
					 VAULT_PRODUCTION_SELECT,//The query vault pattern
					 sbWhere.toString(),//The query where expression
					 DomainConstants.EMPTY_STRING,//The query name to save results
					 false, //true, if the query should find subtypes of the given types
					 objectSelects,//object that holds the list query select clauses
					 Short.parseShort(strQueryLimit),//The limit on number of objects found
					 DomainConstants.EMPTY_STRING,//File Format Search
					 DomainConstants.EMPTY_STRING,//Keyword Search
					 new StringList(DomainObject.getAttributeSelect(ATTR_PG_WD_ERROR_MESSAGE)));//Collection that hold multi value seleactable
			mlFinalFPPData  = filterFPPData(mlObjectList,strAge);
		}
		 catch (NumberFormatException | FrameworkException e) {
			throw e;
		} 
		returnList.addAll(mlFinalFPPData);
		return returnList;	
	}
	/**This method filter FPP data based on below criteria 
	 * @param mlObjectList
	 * @param strErrorDate
	 */
	@SuppressWarnings("unchecked")
	private MapList filterFPPData(MapList mlObjectList, String strAge) {
		Map<String,Object> mpFPPData;
		String strSpecSubType;
		String strSAPType;
		String strPhase;
		String strWDErrorDate;
		String strCurrent;
		boolean bShippableCondition =false; 
		boolean bDateCondition = false;
		MapList mlFinalFPPData = new MapList();
		LocalDate dateCurrent = LocalDate.now();
		LocalDate datePast = dateCurrent.minusDays( new Long(strAge));
		LocalDate dateWDErrorDate = null ; 
		String[] arrDate;
		
		for(int i=0;i<mlObjectList.size();i++)
		{
			mpFPPData = (Map<String, Object>) mlObjectList.get(i);
			//check for shippable HALB
			strSpecSubType = (String)mpFPPData.get(DomainObject.getAttributeSelect(pgApolloConstants.ATTR_PG_ASSEMBLY_TYPE));
			strSAPType = (String)mpFPPData.get(DomainObject.getAttributeSelect(ATTR_PG_SAP_TYPE));
			strPhase = (String)mpFPPData.get(DomainObject.getAttributeSelect(ATTR_RELEASE_PHASE));
			strWDErrorDate = (String)mpFPPData.get(DomainObject.getAttributeSelect(ATTR_PG_WD_ERROR_DATE));
			if(UIUtil.isNotNullAndNotEmpty(strWDErrorDate))
			{
				arrDate = (strWDErrorDate.split(" "))[0].split("/");
				dateWDErrorDate = LocalDate.of(Integer.parseInt(arrDate[2]),Integer.parseInt( arrDate[0]), Integer.parseInt(arrDate[1]));
			}
			strCurrent =(String) mpFPPData.get(DomainConstants.SELECT_CURRENT);
			bShippableCondition = (UIUtil.isNullOrEmpty(strSpecSubType) || (UIUtil.isNotNullAndNotEmpty(strSpecSubType) && !SHIPPABLE_HALB.equals(strSpecSubType))) && (UIUtil.isNullOrEmpty(strSAPType) || (UIUtil.isNotNullAndNotEmpty(strSAPType) && !HALB.equals(strSAPType)));
			bDateCondition = UIUtil.isNotNullAndNotEmpty(strWDErrorDate) && dateWDErrorDate.isAfter(datePast);
			if(bShippableCondition && PRODUCTION.equals(strPhase) && (STATE_PRELIMINARY.equals(strCurrent) || STATE_RELEASE.equals(strCurrent)) && bDateCondition )
			{
				mlFinalFPPData.add(mpFPPData);
			}
			
		}
		return mlFinalFPPData;
	}
	/**This Method use send object either for SAP or Gendoc.
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws MatrixException
	 */
	public Response resendToSAPOrGenDoc(Context context, Map<String, Object> mpRequestMap) throws MatrixException {
		String strResendType =(String) mpRequestMap.get("resendType");
		String strMsg =DomainConstants.EMPTY_STRING;
		if("SAP".equals(strResendType))
		{
			strMsg =	resendToSAP(context,mpRequestMap);
		}else if("GENDOC".equals(strResendType))
		{
			strMsg	= resendToGenDoc(context,mpRequestMap);
		}
		JsonObjectBuilder output = Json.createObjectBuilder();
		output.add(STRING_MSG, STRING_OK);
		output.add(STRING_DATA, strMsg);
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}
	/**This method re send object to Gen DOC
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws MatrixException
	 */
	private String resendToGenDoc(Context context, Map<String, Object> mpRequestMap) throws MatrixException {

		String strObjects =(String) mpRequestMap.get(OBJECT_ID);
		String[] arrObjectList = strObjects.split(",");

		String sAlertRenderGenDoc = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",context.getLocale(), "emxCPN.Alert.sAlertRederGenDoc");
		String strMsg = sAlertRenderGenDoc;
		try
		{

			if(arrObjectList != null)
			{
				String sRenderGenDoc = JPO.invoke(context, "pgIPMPDFViewUtil", null, "markForGenDocGeneration", arrObjectList, String.class);

				strMsg = sRenderGenDoc;

			} else{
				strMsg = sAlertRenderGenDoc;
			} 
		}catch(Exception exception){
			strMsg =  exception.getMessage();
		} catch (MatrixException e) {
			throw e;
		}
		return strMsg;
	}
	/**This method Resend the object to SAP.
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws MatrixException
	 */
	private String resendToSAP(Context context, Map<String, Object> mpRequestMap) throws MatrixException {

		String strMsg = DomainConstants.EMPTY_STRING;
		String strObjects =(String) mpRequestMap.get(OBJECT_ID);
		String[] arrObjectList = strObjects.split(",");
		String sResendToSAP = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",context.getLocale(), "emxCPN.Alert.sResendToSAP");
		String sErrorResendToSAP = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",context.getLocale(), "emxCPN.Alert.sErrorResendToSAP");
		String sAlertResendToSAP = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",context.getLocale(), "emxCPN.Alert.OnlyRelease.AlertResendToSAP");
		final String ATTRIBUTE_PGSENDTOSAPATTR= PropertyUtil.getSchemaProperty(context,"attribute_pgSendToSAP");
		StringList slSelect  = new StringList(7);
		slSelect.add(DomainConstants.SELECT_TYPE);
		slSelect.add(DomainObject.getAttributeSelect(ATTR_PG_ERRORCLASSIFICATION));
		slSelect.add(DomainConstants.SELECT_CURRENT);
		slSelect.add(DomainConstants.SELECT_REVISION);
		slSelect.add(DomainConstants.SELECT_NAME);
		slSelect.add(DomainConstants.SELECT_VAULT);
		slSelect.add(DomainObject.getAttributeSelect(ATTRIBUTE_PGSENDTOSAPATTR));
		String strSendToSAP = DomainConstants.EMPTY_STRING;
		String[] strEventAttr = new String[2];
		String strEventName = DomainConstants.EMPTY_STRING;
		String strResult = DomainConstants.EMPTY_STRING;
		String strToType = null;
		String strCurrent = null;
		String strName = null;
		String strRevision = null;
		String strVault = null;
		boolean state = false;
		boolean bError = false;
		String selectedId = "";
		Map map = null;
		String[] args = null;
		
		try
		{
			DomainObject dObj = DomainObject.newInstance(context);

			if(arrObjectList != null)
			{
				for (int i=0; i < arrObjectList.length ;i++)
				{

					selectedId = arrObjectList[i];
					dObj.setId(selectedId);
					map = dObj.getInfo(context,slSelect);
					if(map != null && !map.isEmpty())
					{
						strToType = (String)map.get(DomainConstants.SELECT_TYPE);
						strCurrent = (String)map.get(DomainConstants.SELECT_CURRENT);
						strRevision = (String)map.get(DomainConstants.SELECT_REVISION);
						strName = (String)map.get(DomainConstants.SELECT_NAME);
						strVault = (String)map.get(DomainConstants.SELECT_VAULT);
						strSendToSAP =(String)map.get(DomainObject.getAttributeSelect(ATTRIBUTE_PGSENDTOSAPATTR));
						strEventAttr = strSendToSAP.split(":") ;
						if(strEventAttr != null && strEventAttr.length>1){
							strEventName = strEventAttr[1];
						}else if ( strEventAttr == null || strEventAttr.length <= 1){
							strEventName = DomainConstants.STATE_PART_RELEASE;
						} 
						if(DomainConstants.STATE_PART_RELEASE.equals(strCurrent))
						{
							args = new String[]{strToType,strName,strRevision,strVault,strEventName,selectedId,"Resend"};
							//Invoking the JPO to process Resending to SAP as Real Time Event
							strResult = JPO.invoke(context, "pgDSMSAPBackGroundProcess", null, "resendFPPValidation", args, String.class);
							//If there is no error in execution of JPO Set State as TRUE which gives success message
							if(UIUtil.isNullOrEmpty(strResult)){
								state = true;
							}else{
								//If there is  error in execution of JPO Set bError as TRUE which throws error message
								bError = true;
							}
						}

					}

				}

			}
			if(state)
			{
				strMsg = sResendToSAP;
			}else if(!bError && !state)
			{
				strMsg =sAlertResendToSAP;
			}
		}
		catch(Exception ex)
		{
			bError = true;
		} catch (MatrixException e) {
			throw e;
		}

		if(bError)
		{
			strMsg =sErrorResendToSAP;
		}
		return strMsg;

	}

	/**This Method contain logic to remove WD error message from selected object.
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws FrameworkException
	 */
	public Response removeErrorMessageFromObject(Context context, Map<String, Object> mpRequestMap) throws FrameworkException 
	{
		@SuppressWarnings("unchecked")
		ArrayList<Map<String,Object>> alObjectList =(ArrayList<Map<String,Object>>) mpRequestMap.get("objectMap");
		DomainObject domObj;
		String strDescription = ATTR_PG_WD_ERROR_MESSAGE +" :  was : ";
		Map<String,Object> mpData;
		
		//Changes for 22x upgrade
		HistoryAuditTrailService addHistoryForFPP= DCLServiceUtil.getHistoryAuditTrailService(context);
		for(int i=0;i<alObjectList.size();i++)
		{
			mpData =alObjectList.get(i);
			domObj = DomainObject.newInstance(context, (String)mpData.get(DomainConstants.SELECT_ID));
			try{
			//Pushing context to update pgWDErrorMessage attribute on FPP which could be Release state OR Context user will not have access always to the FPP in Release state. 
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"person_UserAgent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			domObj.setAttributeValue(context, ATTR_PG_WD_ERROR_MESSAGE , "");
			}
			finally
			{
			ContextUtil.popContext(context);
			}
			//Changes for 22x Upgrade Start
			addHistoryForFPP.customHistoryUpdation(context, (String)mpData.get("id"),DomainConstants.EMPTY_STRING,strDescription + ((String)mpData.get("attribute[pgWDErrorMessage]")),"Modify");
			//Changes for 22x Upgrade End
			
		}
		JsonObjectBuilder output = Json.createObjectBuilder();
		output.add(STRING_MSG, STRING_OK);
		output.add(STRING_DATA, "Removed");
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}

	/**This Method check context user for role Restricted Product Data Originator and send response.
	 * @param context
	 * @return
	 * @throws FrameworkException
	 */
	public Response checkAccess(Context context) throws FrameworkException {
		boolean hasRPDORole = hasRestrictedProductDataOriginator(context);	
		JsonObjectBuilder output = Json.createObjectBuilder();
		output.add(STRING_MSG, STRING_OK);
		output.add(STRING_DATA, hasRPDORole);
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}
	/**This Method check context user for role Restricted Product Data Originator
	 * @param context
	 * @return
	 * @throws FrameworkException
	 */
	private boolean hasRestrictedProductDataOriginator(Context context) throws FrameworkException {
		return PersonUtil.hasAssignment(context, ROLE_RESTRICTEDPRODUCTDATAORIGINATOR);
	}



}
