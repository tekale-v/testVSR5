package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.evp.messaging.utils.UIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class PGPerfCharsUtil {


	/**
	 * Method to convert Map to JSON object
	 * @param mObjectAttributeValues
	 * @return
	 */
	public JsonObjectBuilder getJsonObjectFromMap(Map<?,?> mObjectAttributeValues) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		for (Map.Entry<?, ?> entry : mObjectAttributeValues.entrySet()) {
			jsonReturnObj.add((String) entry.getKey(), (String) entry.getValue());
		}
		return jsonReturnObj;
	}
	
	public JsonArrayBuilder getJsonDataFromMapList(MapList mlPerfCharList) {
    JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
    if (mlPerfCharList != null && !mlPerfCharList.isEmpty()) {
      Iterator<Map<?, ?>> itrPerfChar = mlPerfCharList.iterator();
      while (itrPerfChar.hasNext()) {
        Map<?, ?> mpPerfCharInfoMap = itrPerfChar.next();
        JsonObjectBuilder jsonObjInfo = getJsonObjectFromMap(mpPerfCharInfoMap);
        jsonArrObjInfo.add(jsonObjInfo);
      } 
    } 
    return jsonArrObjInfo;
  }


	/**
	 * Method to convert Access Mask details to prog method
	 * @param strObjectId, strAccessKey  modify|FromConnect|FromDisconnect
	 * @return map with access details for objectId
	 */
	public boolean getAccessDetailsForMask(Context context,String strObjectId , String strAccessKey) throws Exception {
		String strHasAccess = DomainConstants.EMPTY_STRING;
		boolean bhasAccess= true;
		boolean bhasAccessTemp;
		Map mpAccessDetails = new HashMap();
		StringList slAccessKeys = new StringList();
		String loggedInRole = PersonUtil.getActiveSecurityContext(context);
		System.out.println("--- getAccessDetailsForMask loggedInRole---"+ loggedInRole);

		if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strAccessKey))
		{
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			System.out.println("--- getAccessDetailsForMask all 3 heree---"+ (FrameworkUtil.hasAccess(context, domObject , "modify,FromConnect,FromDisconnect")));
			if(strAccessKey.contains("|"))
			{
				slAccessKeys= FrameworkUtil.split(strAccessKey, "|");
				for(int j=0 ; j<slAccessKeys.size() ; j++)
				{
					bhasAccessTemp= FrameworkUtil.hasAccess(context, domObject , slAccessKeys.get(j));
					bhasAccess = bhasAccess && bhasAccessTemp;
				}
			}
			else
			{
				bhasAccess = (FrameworkUtil.hasAccess(context, domObject , strAccessKey));
				//slAccessKeys.add(strHasAccess);
			}
			System.out.println("--- getAccessDetailsForMask bhasAccess---"+ bhasAccess);
			//mpAccessDetails = domObject.getInfo(context, slAccessKeys);
		}
		return bhasAccess;
	}

	/**

	 * This method checks for the pgOriginatingSource attribute for the object

	 * @param context

	 * @param strObjectId

	 * @return boolean

	 * @throws FrameworkException

	 */

	public boolean isOfDSOOrigin(Context context, String strObjectId) throws FrameworkException

	{

		boolean isDSO = false;
		try

		{

			if(UIUtil.isNotNullAndNotEmpty(strObjectId))

			{

				DomainObject doObject = DomainObject.newInstance(context, strObjectId);

				String strAttrValue = doObject.getInfo(context, "attribute["+ PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE +"]");

				if(PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equalsIgnoreCase(strAttrValue))

				{

					isDSO = true;

				}

			}

		}

		catch(FrameworkException fme){

			throw fme;

		}

		return isDSO;

	}

	/**
	 * DSO 2013x.4 : This method hides/displays the EditAll button depending upon the logged in user(Who should be Owner Or CoOwner)
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return boolean
	 */
	public boolean displayToOwnerAndCoOwner(Context context, String strCtxtPartId)
	{
		boolean displayEditAll = false;
		try
		{
			//String strCtxtPartId = pgDSOCommonUtils_mxJPO.INSTANCE.getParam(args, "objectId");
			String strCtxtUser = context.getUser();
			StringList slSelectable = new StringList(2);
			//DSM 2015x.1 IP Security Changes - START
			boolean isCoOwner = false;
			String strOwner = "";
			String strCurrent = "";
			slSelectable.addElement(DomainConstants.SELECT_OWNER);
			slSelectable.addElement(DomainConstants.SELECT_CURRENT);

			DomainObject doObj = DomainObject.newInstance(context);
			if(UIUtil.isNotNullAndNotEmpty(strCtxtPartId))
			{
				doObj.setId(strCtxtPartId);
				Map mapInfo = (Map)doObj.getInfo(context, slSelectable);
				if(!mapInfo.isEmpty())
				{
					strOwner = (String)mapInfo.get(DomainConstants.SELECT_OWNER);
					strCurrent = (String)mapInfo.get(DomainConstants.SELECT_CURRENT);
				}
				isCoOwner = FrameworkUtil.hasAccess(context, doObj , "modify");

				if(UIUtil.isNotNullAndNotEmpty(strOwner) && strOwner.equalsIgnoreCase(strCtxtUser))
				{
					displayEditAll = true;
				}
				else if(!displayEditAll && isCoOwner)
				{
					displayEditAll = true;
				}
				//DSM 2015x.1 IP Security Changes - START
			}
		}
		catch(Exception ex)
		{
			
		}
		return displayEditAll;
	}

	public Map ObjectInfoMap(Context context, String strObjectId, StringList slSelectables)throws Exception {
		Map mInfoMap = new HashMap();
		if(UIUtil.isNotNullAndNotEmpty(strObjectId))
		{
			DomainObject doProductData = DomainObject.newInstance(context, strObjectId);
			mInfoMap = doProductData.getInfo(context, slSelectables);
		}
		
		return mInfoMap;
	}
	
	
	/**
	 * Access Function if context user has Particular Role assigned
	 * Contract Manufacturer 
	 * Command: pgDSMPQRAddExistingPlants
	 * 
	 * @param context
	 * @param sContextUser
	 * @return boolean
	 * @throws Exception
	 */
	public boolean hasRoleForAccess(Context context, String strRole) throws Exception {
		boolean bHasRoleForAccess = false;
		try {
			matrix.db.Person person = new matrix.db.Person(context.getUser());
			if (person.isAssigned(context,strRole)) {
				bHasRoleForAccess = true;
			}

		} catch (Exception ex) {
			throw ex;
		}
		return bHasRoleForAccess;
	}
	
	/**Method to obtain Test Method details
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	public String fetchTesMethodDetails(Context context, String strJsonInput)throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strTestMethodId = jsonInputData.getString("testMethod_id");
			String strAttributes = jsonInputData.getString("attribute_Name");	

			StringList slAtributeList = FrameworkUtil.split(strAttributes, ",");
			StringList slSelectables = new StringList();
			slSelectables.addElement(DomainObject.getAttributeSelect("pgAuthoringApplication"));
			for(String str : slAtributeList)
			{
				slSelectables.addElement(DomainObject.getAttributeSelect(str));
			}
			Map mpTMDetails = ObjectInfoMap(context, strTestMethodId, slSelectables);
			String strAuthApp = "";
			Map mpTMInfo = new HashMap();
			if(null != mpTMDetails && !mpTMDetails.isEmpty())
			{
				strAuthApp = (String)mpTMDetails.get("attribute[pgAuthoringApplication]");

				if(UIUtil.isNotNullAndNotEmpty(strAuthApp) && "Nexus".equalsIgnoreCase(strAuthApp))
				{
					mpTMInfo.put("isNexus", "true");
				}
				else
				{
					mpTMInfo.put("isNexus", "false");
				}
				mpTMInfo.putAll(mpTMDetails);
			}
			jsonReturnObj = getJsonObjectFromMap(mpTMInfo);
		}
		catch(Exception ex)
		{
			throw ex;
		}
		return jsonReturnObj.build().toString();
	}
	
  // DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -START
	/**
 * This method used to obtain details from Nexus config object
 * @param context
 * @param args
 * @return int
 * @throws Exception 
 */

public MapList getNexusConfigObjectDetails(Context context, StringList slObjSelect) throws Exception 
{
	MapList mlreturnList = null;
	try{
		
		mlreturnList  = DomainObject.findObjects(context,
				PGPerfCharsConstants.TYPE_PGCONFIGURATIONADMIN,//typePattern
				PGPerfCharsConstants.CONST_NEXUSDSMINTGRATION_CONFIG,//namePattern
				PGPerfCharsConstants.CONSTANT_STRING_HYPHEN,//revPattern
				DomainConstants.QUERY_WILDCARD,//ownerPattern
				PGPerfCharsConstants.VAULT_ESERVICE_PRODUCTION,//vaultPattern
				null,//whereExpression
				false,//expandType
				slObjSelect);
	}
	catch (Exception ex)
	{
		throw ex;
	}
	return mlreturnList;
}
//DSM DEFECT Id: 57037 Data Types Captured in Nexus Characteristics Table Should Not Be Visible -END
}
