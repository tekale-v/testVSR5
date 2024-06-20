package com.pg.widgets.digiamat;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import matrix.db.Context;
import matrix.util.StringList;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;

import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;


public class PGDiGiAmatCreateEditUtil {

	private static final Logger logger = Logger.getLogger(PGDiGiAmatCreateEditUtil.class.getName());

	static StringList slObjectSelects = new StringList();

	/**
	 * Creates the Digital AMAT , updates attributes and connects the related
	 * objects with it
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	Response createDigiAmat(Context context, Map<?, ?> mpRequestMap) {
		String autoName = "";
		String strNewObjectId = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			String symbolicTypeName 	= PropertyUtil.getAliasForAdmin(context, "Type", PGDiGiAmatConstants.TYPE_PG_DIGI_AMAT, true);
			String symbolicPolicyName 	= PropertyUtil.getAliasForAdmin(context, "Policy", PGDiGiAmatConstants.POLICY_PG_DIGI_AMAT, true);
			autoName =  FrameworkUtil.autoName(context,
					symbolicTypeName,
					null,
					symbolicPolicyName,
					null,
					null,
					true,
					true);
			
			Map<String, String> attrMap = new HashMap<>();
			Map<?, ?> mpAttributeMap = (Map<?, ?>) mpRequestMap.get(PGDiGiAmatConstants.KEY_ATTRIBUTES);
			attrMap=getAttrKeyValMap(mpAttributeMap);
			boolean isUnique = isRuleUniqueValidation(context,attrMap);
			
			if(!isUnique)
			{
				output.add("Error", "Entered org , Part Type and Sub type already exist ...");
				return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();

			}else
			{
				ContextUtil.startTransaction(context, true);
				DomainObject domObj = DomainObject.newInstance(context);
				
				domObj.createObject(context, PGDiGiAmatConstants.TYPE_PG_DIGI_AMAT, autoName, DomainConstants.EMPTY_STRING, PGDiGiAmatConstants.POLICY_PG_DIGI_AMAT, context.getVault().getName());
				strNewObjectId= domObj.getId(context);
				updateBasic(context, strNewObjectId, mpRequestMap);

				updateSoAmatAttributes(context, strNewObjectId, mpAttributeMap);
				output = prepareResponse(context, strNewObjectId);
				ContextUtil.commitTransaction(context);
			}
			
		} catch (Exception excep) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}



	/**
	 * Method to form the object select list
	 * 
	 * @param strObjSelects
	 */
	private void updateObjectSelectList(String strObjSelects) {

		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_REVISION);

		StringList slObjSelectArgList = StringUtil.split(strObjSelects, ",");
		for (int i = 0; i < slObjSelectArgList.size(); i++) {
			String strObjSelect = slObjSelectArgList.get(i);
			if (!slObjectSelects.contains(strObjSelect)) {
				slObjectSelects.add(strObjSelect);
			}
		}
	}

	/**
	 * Updates Attributes values on new Object created
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param mpAttribute
	 * @return
	 * @throws Exception
	 */
	public void updateSoAmatAttributes(Context context, String strNewObjectId, Map<?, ?> mpAttribute ) {
		try {
			Map<String, String> attrMap = new HashMap<>();
			Map<String, StringList> mAttributeDataMap = new HashMap<>();
			String strAttributeName = null;
			String strAttributeValue = null;
			String strAttributeWidgetName = null;
			DomainObject domNewObjSoAmat = DomainObject.newInstance(context, strNewObjectId);

			StringList slBAValues = new StringList();
			
			attrMap=getAttrKeyValMap(mpAttribute);
			attrMap.put("pgDigitalAmatUniqueKey",attrMap.get("pgDigitalAmatPartType")+"|"+attrMap.get("pgAssemblyType")+"|"+attrMap.get("pgOrganization") );
			
			if (!attrMap.isEmpty()) {
				domNewObjSoAmat.setAttributeValues(context, attrMap);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
		}
	}



	/**
	 * @param mpAttribute
	 * @param attrMap
	 */
	public Map getAttrKeyValMap(Map<?, ?> mpAttribute) {
		Map<String, String> attrMap = new HashMap<>();
		String strAttributeName;
		String strAttributeValue;
		String strAttributeWidgetName;
		if (null != mpAttribute && mpAttribute.size() > 0) {
			for (Map.Entry<?, ?> entry : mpAttribute.entrySet()) {
				strAttributeWidgetName = (String) entry.getKey();
				strAttributeName = strAttributeWidgetName.substring(strAttributeWidgetName.indexOf("[") + 1,
						strAttributeWidgetName.lastIndexOf("]"));
				strAttributeValue = (String) entry.getValue();
				if ((UIUtil.isNotNullAndNotEmpty(strAttributeName)
						&& UIUtil.isNotNullAndNotEmpty(strAttributeValue))) 
				{
					
						attrMap.put(strAttributeName, strAttributeValue);
					
				}
			}
		
		}
		return attrMap;
	}

	public boolean isRuleUniqueValidation(Context context, Map<String, String> attrMap) throws Exception{
		boolean isUniqueFlag = true;

		MapList mlResult = new MapList();
		String spayloadPartType = attrMap.get("pgDigitalAmatPartType");
		String spayloadOrganizations = attrMap.get("pgOrganization");
		String uniqueKey = "Test Method Specification,Quality Specification:pgOrganization-pgDigitalAmatPartType-pgAssemblyType;All:pgOrganization-pgDigitalAmatPartType";
		StringList slRuleList = StringUtil.split(uniqueKey, ";");
		StringList slObjSelects = new StringList(3);
		slObjSelects.add(DomainConstants.SELECT_ID);
		
		Map<String, String> mKeyValueMap = new HashMap<>();

		for (int i=0;i<slRuleList.size();i++)
		{
			String strRuleDefination=(String)slRuleList.get(i);
			StringList slRuleDefinationList = StringUtil.split(strRuleDefination, ":");
			mKeyValueMap.put(slRuleDefinationList.get(0), slRuleDefinationList.get(1));
		}
		StringBuffer strWhereClause = new StringBuffer();
		Iterator iterate = mKeyValueMap.entrySet().iterator();
		
		while(iterate.hasNext()) 
		{
		    Map.Entry map = (Map.Entry) iterate.next();
		    String strPartTypes = (String) map.getKey();
		    String strRuleParameter = (String) map.getValue();
			int temp = 1;
		    StringList slPartTypes = StringUtil.split(strPartTypes, ",");
			StringList slRuleListToValidate = StringUtil.split(strRuleParameter, "-");
			
			//When Part Type is TMS / Quality Specification 
		    if (slPartTypes.contains(spayloadPartType))
		    {
		    	
		    	mlResult = checkDigitalAmatOjectExistance(context , slRuleListToValidate , attrMap);
		    	if(!mlResult.isEmpty())
		    		isUniqueFlag = false;
		    }else //if(slPartTypes.contains("ALL"))//ALL case
		    {
		    	mlResult = checkDigitalAmatOjectExistance(context , slRuleListToValidate , attrMap);
		    	if(!mlResult.isEmpty())
		    		isUniqueFlag = false;
			        temp = 10;
		    }
		}
		return isUniqueFlag;
	}

	/**
	 * checkDigitalAmatOjectExistance
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param alConnections
	 * @return
	 * @throws Exception
	 */
	public MapList checkDigitalAmatOjectExistance(Context context, StringList slRuleListToValidate,Map<String, String> attrMap)throws Exception {
		StringBuffer strWhereClause = new StringBuffer();
		StringList slObjSelects = new StringList(1);
		slObjSelects.add(DomainConstants.SELECT_ID);
		MapList mlResult= new MapList();
		try {
			for (int cnt = 0; cnt < slRuleListToValidate.size(); cnt++) {
				strWhereClause.append(PGDiGiAmatConstants.PREFIX_ATTRIBUTE_SELECT);
				strWhereClause.append((String) slRuleListToValidate.get(cnt));
				strWhereClause.append(PGDiGiAmatConstants.SUFFIX_ATTRIBUTE_SELECT);
				strWhereClause.append("=='");
				strWhereClause.append(attrMap.get((String) slRuleListToValidate.get(cnt)));
				strWhereClause.append("' ");

				if (!(cnt == (slRuleListToValidate.size() - 1)))
					strWhereClause.append(" && ");
			}
			mlResult = DomainObject.findObjects(context, PGDiGiAmatConstants.TYPE_PG_DIGI_AMAT,
					DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
					PGDiGiAmatConstants.VAULT_ESERVICE_PRODUCTION, strWhereClause.toString(), false, slObjSelects);
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		return mlResult;
	}


	/**
	 * creates new Object
	 * 
	 * @param context
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strPolicy
	 * @return
	 * @throws Exception
	 */
	public String createObject(Context context, String strType, String strName, String strRevision, String strPolicy) {
		String strObjectId = null;
		try {
			DomainObject dObj = DomainObject.newInstance(context);
			dObj.createObject(context, strType, strName, strRevision, strPolicy, null);
			strObjectId = dObj.getObjectId(context);
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
		}
		return strObjectId;
	}

	/**
	 * updates Description on the Object
	 * 
	 * @param context
	 * @param strObjectId
	 * @param strDescription
	 * @return
	 * @throws Exception
	 */
	public void updateBasic(Context context, String strObjectId, Map<?, ?> mRequestMap) {
		DomainObject dObj = null;
		String strDescription = null;
		String strOwner = null;
		try {
			dObj = DomainObject.newInstance(context, strObjectId);
			strDescription = (String) mRequestMap.get(DomainConstants.SELECT_DESCRIPTION);
			strOwner = context.getUser();
			if (UIUtil.isNotNullAndNotEmpty(strDescription))
				dObj.setDescription(context, strDescription);
			if (UIUtil.isNotNullAndNotEmpty(strOwner)) {
				dObj.setOwner(context, strOwner);
				dObj.setAttributeValue(context, DomainConstants.ATTRIBUTE_ORIGINATOR, strOwner);
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
		}
	}

	/**
	 * edits the Structured ATS attributes and connections
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	Response editDiGiAmat(Context context, Map<?, ?> mpRequestMap) {
		String strObjectId = null;
		DomainObject dObj = null;
		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			strObjectId = (String) mpRequestMap.get(DomainConstants.SELECT_ID);
			dObj = DomainObject.newInstance(context, strObjectId);
			String strDesc = (String) mpRequestMap.get(DomainConstants.SELECT_DESCRIPTION);
			if (UIUtil.isNotNullAndNotEmpty(strDesc))
				dObj.setDescription(context, strDesc);
			Map<?, ?> attrMap = (Map<?, ?>) mpRequestMap.get(PGDiGiAmatConstants.KEY_ATTRIBUTES);
			
		
			
			boolean isUnique = editStructuredATSAttributes(context, strObjectId, attrMap);
			if(!isUnique)
			{
				output.add("Error", "Entered org , Part Type and Sub type already exist ...");
				return Response.status(HttpServletResponse.SC_EXPECTATION_FAILED).entity(output.build().toString()).build();

			}
			output = prepareResponse(context, strObjectId);
			jsonArrObjInfo.add(output);
			jsonData.add(PGDiGiAmatConstants.KEY_DATA, jsonArrObjInfo);
			ContextUtil.commitTransaction(context);
		} catch (Exception excep) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonData.build().toString()).build();
	}

	/**
	 * Edits Attributes values on new Object created
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param mpAttribute
	 * @return
	 * @throws Exception
	 */
	public boolean editStructuredATSAttributes(Context context, String strObjectId, Map<?, ?> mpAttribute)
			throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		boolean isUnique = false;
		try {
			Map<String, String> attrMap = new HashMap<>();
			String strAttributeValue = null;
			String strAttributeWidgetName = null;
			DomainObject dObjSATS = null;

			if (null != mpAttribute && mpAttribute.size() > 0) {
				for (Map.Entry<?, ?> entry : mpAttribute.entrySet()) {
					strAttributeWidgetName = (String) entry.getKey();
					String strAttributeName = strAttributeWidgetName.substring(strAttributeWidgetName.indexOf("[") + 1,
							strAttributeWidgetName.lastIndexOf("]"));
					strAttributeValue = (String) entry.getValue();
					if (UIUtil.isNotNullAndNotEmpty(strAttributeValue)) {
						attrMap.put(strAttributeName, strAttributeValue);
					}
				}
			}
			isUnique = isRuleUniqueValidation(context,attrMap);
			
			if (isUnique && attrMap.size() > 0) {
				dObjSATS = DomainObject.newInstance(context, strObjectId);
				attrMap.put("pgDigitalAmatUniqueKey",attrMap.get("pgDigitalAmatPartType")+"|"+attrMap.get("pgAssemblyType")+"|"+attrMap.get("pgOrganization") );
				dObjSATS.setAttributeValues(context, attrMap);
			}
		
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
		return isUnique;
	}


	/**
	 * This method is used to delete connected Performance Characteristics Operations to SATS
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return String
	 * @throws Exception
	 */
	public String deleteSoAmat(Context context, Map mpRequestMap) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strTargetId = (String) mpRequestMap.get(PGDiGiAmatConstants.KEY_TARGET_ID);
		String strReturnVal = null;
		String strObjectId = null;
		try {
			StringList slList = FrameworkUtil.split(strTargetId, PGDiGiAmatConstants.CONSTANT_STRING_PIPE);
			for (String strSoAmatObj : slList) 
			{
				strObjectId = strSoAmatObj;
				if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
					DomainObject.deleteObjects(context, new String[]{strObjectId});
				}else {
				output.add(PGDiGiAmatConstants.KEY_STATUS,PGDiGiAmatConstants.STRING_ERR_DELETE);
				strReturnVal = output.build().toString();
				}
			}
		}
		catch (Exception excep) {
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return output.build().toString();
		}
		return strReturnVal;
	}	

	/**
	 * Method to get the object selects details for newly created STAS object
	 * 
	 * @param context
	 * @param strNewObjectId
	 * @param strObjSelect
	 * @return
	 * @throws Exception
	 */
	private JsonObjectBuilder prepareResponse(Context context, String strNewObjectId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strNewObjectId)) {
				String strLanguage = context.getSession().getLanguage();
				slObjectSelects.remove(PGDiGiAmatConstants.KEY_DISPLAY_TYPE);
				DomainObject dobSATSobj = DomainObject.newInstance(context, strNewObjectId);

				Map<?, ?> objInfoMap = dobSATSobj.getInfo(context, slObjectSelects);
				StringList slTemp;
				StringBuilder sbValues;
				String strValue = DomainConstants.EMPTY_STRING;

				for (int i = 0; i < slObjectSelects.size(); i++) {
					String strObjSelectKey = slObjectSelects.get(i);
					 Object objValue = objInfoMap.get(strObjSelectKey);
						if (objValue == null || "".equals(objValue)) {
							objValue = DomainConstants.EMPTY_STRING;
						}
						if (objValue instanceof String) {
							strValue = (String) objValue;
						} else if (objValue instanceof StringList) {
							slTemp = (StringList) objValue;
							sbValues = new StringBuilder();
							for (int j = 0; j < slTemp.size(); j++) {
								if (UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
									sbValues.append(slTemp.get(j));
								}
								if (j != slTemp.size() - 1) {
									sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
								}
							}
							strValue = sbValues.toString();
						}
					
					output.add(strObjSelectKey, strValue);
				}
				String strType = (String) objInfoMap.get(DomainConstants.SELECT_TYPE);
				String strTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
						PGDiGiAmatConstants.STR_SCHEMA_TYPE, strType, strLanguage);
				if (UIUtil.isNullOrEmpty(strTypeDisplayName)) {
					strTypeDisplayName = strType;
				}
				output.add(PGDiGiAmatConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
			}

		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGDiGiAmatConstants.EXCEPTION_MESSAGE, excep);
			throw excep;
		}
		return output;
	}
}
