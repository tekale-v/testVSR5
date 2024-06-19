package com.png.apollo.characteristics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dassault_systemes.enovia.criteria.interfaces.ENOCriteriaServices;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.AccessUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.AccessList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.StringList;



public class CharacteristicSyncUtil {
	
	public static final String SELECT_ATTRIBUTE_EVALUATED_CRITERIA = "attribute["+pgApolloConstants.ATTRIBUTE_EVALUATED_CRITERIA+"]";
	public static final String CONSTANT_STRING_SELECT_FROM_CURRENT = "].from.current";
	public static final String STR_CRITERIA_PHYSICAL_ID ="CriteriaPhysicalId";
	public static final String STR_CMID ="CMID";
	public static final String STR_EVAL_CRITERIA ="EvalCriteria";
	static final String STR_APOLLO_CHAR_SYNC_TRACE= "apolloCharSync";
	/**
	 * This method will return of Characteristic and Characteristic Master Map for a given APP
	 * @param context
	 * @param strAppObjectId
	 * @return
	 * @throws Exception
	 */
	public static Map getCharacteristicsCharMasterMap(Context context, String strAppObjectId) throws Exception{
		Map returnMap = new HashMap();
		try
		{
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: getCharacteristicsCharMasterMap");
			if(UIUtil.isNotNullAndNotEmpty(strAppObjectId))
			{
				DomainObject appObject = DomainObject.newInstance(context, strAppObjectId);
				StringBuilder strSelectSB = new StringBuilder();
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_TO);
				strSelectSB.append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC);
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMID);
				String strSelecteCharMaster=strSelectSB.toString();
				//Apollo 2018x.5 FeildFix for SR00662477- Start
				strSelectSB = new StringBuilder();
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_TO);
				strSelectSB.append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC);
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET);
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_DOT);
				strSelectSB.append(SELECT_ATTRIBUTE_EVALUATED_CRITERIA);
				
				String strSelecteCriteria=strSelectSB.toString();
				//Apollo 2018x.5 FeildFix for SR00662477- End
				StringList slSelectable = new StringList(3);
				slSelectable.addElement(DomainConstants.SELECT_ID);
				slSelectable.addElement(strSelecteCharMaster);
				//Apollo 2018x.5 FeildFix for SR00662477- Start
				slSelectable.addElement(strSelecteCriteria);
				//Apollo 2018x.5 FeildFix for SR00662477- End
				
				MapList mlConnectedObjects = appObject.getRelatedObjects(context,
						pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION,
						pgApolloConstants.TYPE_PLM_PARAMETER,
						slSelectable,
						null,
						false,
						true,
						(short)1,
						null,
						null,
						0);
				
				if(null != mlConnectedObjects && !mlConnectedObjects.isEmpty()) {
					Map mTemp = null;
					String sPlmParameterObjectId =null;
					String strCharMasterObjectId =null;
					//Apollo 2018x.5 FeildFix for SR00662477- Start
					Map tempMap =null;
					//Apollo 2018x.5 FeildFix for SR00662477- End
					for(int iCount=0; iCount<mlConnectedObjects.size(); iCount++) 
					{
						mTemp = (Map) mlConnectedObjects.get(iCount);							
						sPlmParameterObjectId = (String)mTemp.get(DomainConstants.SELECT_ID);
						if(mTemp.containsKey(strSelecteCharMaster)) {
							strCharMasterObjectId =(String)mTemp.get(strSelecteCharMaster);
							//Apollo 2018x.5 FeildFix for SR00662477- Start							
							tempMap = new HashMap();
							tempMap.put(STR_CMID,strCharMasterObjectId);
							tempMap.put(STR_EVAL_CRITERIA, mTemp.get(strSelecteCriteria));
							returnMap.put(sPlmParameterObjectId, tempMap);
							//Apollo 2018x.5 FeildFix for SR00662477- End
						}
					}				
				}
			}
		}
		catch (Exception ex) {
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: getCharacteristicsCharMasterMap ====> Exception"+ex);
			throw ex;
		}
		context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: getCharacteristicsCharMasterMap====> Return"+returnMap);
		return returnMap;
	}
	
	/**
	 * This method will sync all characteristic with criteria on an Object
	 * @param context
	 * @param args
	 * @param previousCharMap
	 * @throws Exception
	 */
	public static void syncCharacteristics(Context context , String strAppObjectId , Map previousCharMap) throws Exception {
		boolean bContextPushed=false;
		try 
		{
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncCharacteristics");
			if (UIUtil.isNotNullAndNotEmpty(strAppObjectId)) 
			{
				DomainObject appObject = DomainObject.newInstance(context, strAppObjectId);				
				
				StringBuilder strSelectSB = new StringBuilder();
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_TO);
				strSelectSB.append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC);
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET);
				strSelectSB.append(pgApolloConstants.CONSTANT_STRING_DOT);
				strSelectSB.append(SELECT_ATTRIBUTE_EVALUATED_CRITERIA);
				String strSelectEvaluatedCriteria = strSelectSB.toString();				
				strSelectSB = new StringBuilder();
				String strSelecteCharMaster=strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_FROMID).toString();
				strSelectSB = new StringBuilder();
				String strSelectRelDerivedCharId=strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(pgApolloConstants.CONSTANT_STRING_SELECT_RELID).toString();
				strSelectSB = new StringBuilder();
				String strSelecteCharMasterState=strSelectSB.append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.RELATIONSHIP_DERIVED_CHARACTERISTIC).append(CONSTANT_STRING_SELECT_FROM_CURRENT).toString();
				StringList slSelectable = new StringList(4);
				slSelectable.addElement(DomainConstants.SELECT_ID);
				slSelectable.addElement(strSelectEvaluatedCriteria);
				slSelectable.addElement(strSelecteCharMaster);
				slSelectable.addElement(strSelectRelDerivedCharId);
				slSelectable.addElement(strSelecteCharMasterState);
				MapList mlConnectedObjects = appObject.getRelatedObjects(context,
						pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION,
						pgApolloConstants.TYPE_PLM_PARAMETER,
						slSelectable,
						null,
						false,
						true,
						(short)1,
						null,
						null,
						0);
				context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncCharacteristics===> mlConnectedObjects "+mlConnectedObjects);
				if(null != mlConnectedObjects && !mlConnectedObjects.isEmpty()) {

					//Get Valid Criteria
					List appliCableCMList =ENOCriteriaServices.getApplicableCriteriaOutput(context, strAppObjectId);
					context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncCharacteristics===> appliCableCMList "+appliCableCMList);
					Iterator appliCableCMListItr = appliCableCMList.iterator();
					Map tempMap =null;
					Set validCriteria =new HashSet();
					while (appliCableCMListItr.hasNext())
					{
						tempMap =(Map)appliCableCMListItr.next();
						validCriteria.addAll(pgApolloCommonUtil.getStringListFromObject((Object) tempMap.get(STR_CRITERIA_PHYSICAL_ID)));
					}

					StringList slCriterialTempIdList = null;
					Object tempObject = null;
					String sPlmParameterObjectId =null;
					String strCharMasterObjectId = null;
					String strPreviousCharMasterId =null;
					Map mTemp = null;
					Object objPrevEvalCriteria = null;
					StringList strListPreviousEvalCriteria = null;

					StringList deleteObjectIds =new StringList();
					for(int iCount=0; iCount<mlConnectedObjects.size(); iCount++) {
						mTemp = (Map) mlConnectedObjects.get(iCount);							
						sPlmParameterObjectId = (String)mTemp.get(DomainConstants.SELECT_ID);				
						strCharMasterObjectId =(String)mTemp.get(strSelecteCharMaster);
						if(null != previousCharMap && !previousCharMap.isEmpty() && previousCharMap.containsKey(sPlmParameterObjectId))
						{
							tempMap=(Map)previousCharMap.get(sPlmParameterObjectId);
							strPreviousCharMasterId=(String)tempMap.get(STR_CMID);
							objPrevEvalCriteria=tempMap.get(STR_EVAL_CRITERIA);
							if((objPrevEvalCriteria instanceof StringList)) {
								strListPreviousEvalCriteria=(StringList)tempMap.get(STR_EVAL_CRITERIA);
							}
							else if(objPrevEvalCriteria instanceof String) {
								strListPreviousEvalCriteria = new StringList(1);
								strListPreviousEvalCriteria.add((String)objPrevEvalCriteria);
							}
								
						}
						
						if((mTemp.containsKey(strSelecteCharMasterState) && pgApolloConstants.STATE_OBSOLETE.equals(mTemp.get(strSelecteCharMasterState)))) {
							deleteObjectIds.add(sPlmParameterObjectId);
						}
						else {

							slCriterialTempIdList = new StringList();
							if(mTemp.containsKey(strSelectEvaluatedCriteria))
							{
								tempObject = mTemp.get(strSelectEvaluatedCriteria);    
								if ((tempObject instanceof StringList)) {
									slCriterialTempIdList = (StringList)tempObject;
								} else if ((tempObject instanceof String) && UIUtil.isNotNullAndNotEmpty(tempObject.toString())) {
									slCriterialTempIdList.add((String)tempObject);
								}
							}
							if(slCriterialTempIdList.isEmpty()&& ! strListPreviousEvalCriteria.isEmpty()) {
								deleteObjectIds.add(sPlmParameterObjectId);

							}
							else {
								boolean isValid =isCharValid(slCriterialTempIdList , validCriteria);
								if(!isValid) {
									deleteObjectIds.add(sPlmParameterObjectId);
								}

								else {
									if(UIUtil.isNotNullAndNotEmpty(strCharMasterObjectId) && UIUtil.isNotNullAndNotEmpty(strPreviousCharMasterId) && !strPreviousCharMasterId.equals(strCharMasterObjectId)) {
										syncTMRD(context ,strCharMasterObjectId, sPlmParameterObjectId);
									} 
								}
							}
						}

					}

					if(!deleteObjectIds.isEmpty()) {
						context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncCharacteristics===> deleteObjectIds "+deleteObjectIds);
						String[] idsToDelete=BusinessUtil.toStringArray(deleteObjectIds);
						ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, pgApolloConstants.PERSON_USER_AGENT), null, context.getVault().getName());
						bContextPushed = true;
						DomainObject.deleteObjects(context, idsToDelete);
					}
				}

			}

		}
		catch(Exception ex) {
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncCharacteristics===> Exception "+ex);
			ex.printStackTrace();
			//throw ex;
		}finally{
			if(bContextPushed)
			{

				ContextUtil.popContext(context);
			}
		}
	}
	

	/**
	 * This method will check if the Characteristic is still valid. 
	 * @param context
	 * @param existingCriteriaList
	 * @param validCriteriaList
	 * @return
	 */
	private static boolean isCharValid(StringList existingCriteriaList , Set validCriteriaList) throws Exception{
		boolean returnFlag = false;
		String strtempCriteriaId=null;
			for(int i=0 ; i < existingCriteriaList.size() ; i++) 
			{
				strtempCriteriaId =existingCriteriaList.get(i);
				if(validCriteriaList.contains(strtempCriteriaId))
				{
					returnFlag= true;
					}
			}		
		return returnFlag;
	}
	
		
	/**
	 * This method will sync sync TMRD relationship from characteristic master and characteristic.
	 * @param context
	 * @param strCharacteristicMasterID
	 * @param strCharacteristicId
	 * @throws Exception
	 */
	public static void syncTMRD(Context context , String strCharacteristicMasterID ,  String strCharacteristicId)throws Exception{

		
		try {
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD");
			
			String sContextUser = context.getUser();
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD===> Context User "+sContextUser);
			
			DomainObject charObject = DomainObject.newInstance(context, strCharacteristicId);
			StringList charRedDocList = charObject.getInfoList(context, "from["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].to."+DomainConstants.SELECT_ID);
			DomainObject charMasterObject = DomainObject.newInstance(context, strCharacteristicMasterID);
			StringList charMasterRefdocList = charMasterObject.getInfoList(context, "from["+pgApolloConstants.RELATIONSHIP_PARAMETERAGGREGATION+"].to["+pgApolloConstants.TYPE_PLM_PARAMETER+"].from["+DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT+"].to."+DomainConstants.SELECT_ID);
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD===> Existing TMRD "+charRedDocList);
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD===> CM TMRD "+charMasterRefdocList);
			StringList toConnectRefDocList =new StringList();
			StringList toDisconnectConnectRefDocList =new StringList();
			Iterator charMasterRefdocListItr =charMasterRefdocList.iterator();
			String strTempRefdocId =DomainConstants.EMPTY_STRING;
			//Find To be connected TMRD
			while(charMasterRefdocListItr.hasNext()) {
				
				strTempRefdocId =(String)charMasterRefdocListItr.next();
				if(! charRedDocList.contains(strTempRefdocId)) {
					toConnectRefDocList.add(strTempRefdocId);
				}
			}
			
			//Find to be Disconnect TMRD.
			Iterator refDocListItr = charRedDocList.iterator();
			while(refDocListItr.hasNext()) {
				strTempRefdocId =(String)refDocListItr.next();
				if(!charMasterRefdocList.contains(strTempRefdocId)) {
					toDisconnectConnectRefDocList.add(strTempRefdocId);
				}
			}
			
			String sTMRDToBeDisConnected;
			boolean hasToDisconnectAccess = false;
			boolean hasToConnectAccess = false;
			BusinessObject boToBeDisconnected;
			BusinessObject boToBeConnected;
			BusinessObjectList boList = new BusinessObjectList();
			AccessList accessList = null;
			matrix.db.Access access = new matrix.db.Access();


			if(!toDisconnectConnectRefDocList.isEmpty()) {
				RelationshipType refDocRel = new RelationshipType(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT);
				String tmDArray[] = toDisconnectConnectRefDocList.toArray(new String[toDisconnectConnectRefDocList.size()]);
				Iterator toDisconnectConnectRefDocListItr = toDisconnectConnectRefDocList.iterator();
				while(toDisconnectConnectRefDocListItr.hasNext()) {		
					
					sTMRDToBeDisConnected = (String)toDisconnectConnectRefDocListItr.next();
					boToBeDisconnected = new BusinessObject(sTMRDToBeDisConnected);
					
					hasToDisconnectAccess = boToBeDisconnected.getAccessMask(context).hasToDisconnectAccess();	
					context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD===> hasToDisconnectAccess : "+hasToDisconnectAccess+" for "+sTMRDToBeDisConnected);
					boList.clear();
					boList = new BusinessObjectList();
					access = new matrix.db.Access();
					if(!hasToDisconnectAccess)
					{						
						boList.add(boToBeDisconnected);
						access.setToDisconnectAccess(true);
						access.setUser(sContextUser);
						accessList = new AccessList(access);
						AccessUtil.grantAccess(context, boList, accessList, pgApolloConstants.PERSON_USER_AGENT);
					}
					
					charObject.disconnect(context, refDocRel, true, boToBeDisconnected);					
					
					if(!hasToDisconnectAccess)
					{
						AccessUtil.revokeAccess(context, boList, new StringList(sContextUser));
					}	
					
					
				}			
			}
			
			if(!toConnectRefDocList.isEmpty())
			{
				boList.clear();	
				boList = new BusinessObjectList();
				access = new matrix.db.Access();
				for(String sToBeConnectedTMRDId : toConnectRefDocList)
				{
					boToBeConnected = new BusinessObject(sToBeConnectedTMRDId);					
					hasToConnectAccess = boToBeConnected.getAccessMask(context).hasToConnectAccess();
					
					context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD===> hasToConnectAccess : "+hasToConnectAccess+" for "+sToBeConnectedTMRDId);

					if(!hasToConnectAccess)
					{
						boList.add(boToBeConnected);
					}					
				}				
				
				if(!boList.isEmpty())
				{
					access.setToConnectAccess(true);
					access.setUser(sContextUser);
					accessList = new AccessList(access);
					AccessUtil.grantAccess(context, boList, accessList, pgApolloConstants.PERSON_USER_AGENT);					
				}				
				
				String tmDArray[] = toConnectRefDocList.toArray(new String[toConnectRefDocList.size()]);
				DomainRelationship.connect(context, charObject, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, true, tmDArray);
				
				
				if(!boList.isEmpty())
				{
					AccessUtil.revokeAccess(context, boList, new StringList(sContextUser));
				}				
				context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD===> boList "+boList);

			}
		}
		catch(Exception e) {
			context.printTrace(STR_APOLLO_CHAR_SYNC_TRACE, "Method ::: syncTMRD====> Exception "+e);
			throw e;
		}
		
	}
}
