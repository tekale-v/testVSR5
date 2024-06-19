import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.RelationshipType;

import com.matrixone.apps.domain.util.ContextUtil;

import java.util.Properties;

import matrix.db.Page;

import java.io.StringReader;

public class pgFPPManualRollup {

	public static Properties configuration = new Properties();
	
	/**
	  * This method is to check Config status an to intiate the process
	  * @param context the eMatrix <code>Context</code> object
	  * @param args    holds arguments.
	  * @return void
	  * @throws Exception
	  * Modified for 2018x.5 Rollup Configuration requirements 33937
	*/
	public static void loadRollupPageConfig(Context context)throws Exception {	
		try {
			Page page = new Page(pgV3Constants.ROLLUPPAGEFILE);
			page.open(context);
			String content = page.getContents(context);
			page.close(context);
			configuration.load(new StringReader(content));
		}catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	/**
	 * 
	 * Connects FPP to Rollup Data
	 * @param context the eMatrix <code>Context</code> object
	 * @param args    holds StringList as arguments.
	 * @return void
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void performFPPRollup(Context context, String[] args) throws Exception {
		   
		    String strFPPObjectId = args[0];
			String strRollupType = args[1];
			String strFPPName = args[2];			
			//Modified by DSM(sogeti) for 2018x.3 rollup requirement 30845, 30855 & 30854 - Starts
			String strFPPSpecSubtype = args[3];
			//Modified by DSM(sogeti) for 2018x.3 rollup requirement 30845, 30855 & 30854 - Ends
		try {
			
			loadRollupPageConfig(context);
			//Modified by DSM(sogeti) for 2018x.3 rollup requirement 30845, 30855 & 30854 - Starts
			String STR_ALLOWED_TYPE_SHIPPABLE_HALB = configuration.getProperty("Str_Allowed_RollupTypes_ShippableHALB");
		    //Modified by DSM(sogeti) for 2018x.3 rollup requirement 30845, 30855 & 30854 - Ends
			
			    //Modified by DSM(sogeti) for 2018x.3 rollup requirement 30845, 30855 & 30854 - Starts
			if(configuration.getProperty("Str_RollUp_Enable_"+strRollupType).equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE) &&
						((pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strFPPSpecSubtype) && STR_ALLOWED_TYPE_SHIPPABLE_HALB.contains(strRollupType)) || !pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strFPPSpecSubtype))){
				//Modified by DSM(sogeti) for 2018x.3 rollup requirement 30845, 30855 & 30854 - Ends
				StringList slProductList = new StringList();
				
				//disconnects all existing rollup data
				disconnetFPPExistingData(context, strFPPObjectId, strRollupType);
				//perform the Roll Up Logic
				processIntermediatesOfFPP(context, strFPPObjectId, strRollupType, slProductList);
				slProductList.clear();
				if (pgV3Constants.KEY_GHS.equalsIgnoreCase(strRollupType) || pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION.equalsIgnoreCase(strRollupType)) {
					//To update Corrosive attribute value for the GHS.
					updateCorrosiveForGHS(context, strFPPObjectId);
				}
				if (pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION.equalsIgnoreCase(strRollupType)) {
					checkWareHouseClassification(context, strFPPObjectId, strRollupType);
				}	
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		
	}
    
 	/** 
	 * This method process the COPs and PAP's of FPP
	 * @param context the eMatrix <code>Context</code> object
	 * @param String strFPPObjectId holds FPP Id 
	 * @param String strRollupType holds Rollup Type
	 * @param StringList slProductList holds existing connection physical ids
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processIntermediatesOfFPP(Context context, String strFPPObjectId, String strRollupType, StringList slProductList)
			throws Exception {
		try {			
			String STR_ALLOWED_TYPE_SUBSTITUTE_CHILDCOPS = configuration.getProperty("Str_Allowed_RollupTypes_SubstitutesOfChildCOP");
			String STR_ALLOWED_PAP_SUBSTITUTE_TYPES = configuration.getProperty("Str_Allowed_PAP_Substitutes");
			String STR_ALLOWED_COPBULKSUB_PRODUCT_PARTS=  configuration.getProperty("Str_Allowed_COPBulk_Substitute_ProductParts");
			String STR_ALLOWED_TYPES_NLEVEL = configuration.getProperty("Str_Allowed_RollupTypes_NLevel");
						
			Map mpCOPMap;
			MapList mlFPPChildData;
			String strID;
			String strObjectType;
			String strSubStituteId ;
			String strSubStituteType;
			String strFrommid = pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID;
			String strFromType = pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE;
			String strFromPhysicalId = pgV3Constants.SELECT_EBOM_SUBSTITUTE_PHYSICALID;
			String sFPPId = strFPPObjectId;
			String sRollupType = strRollupType;
			StringList slCOPsIdList;
			StringList slCOPsSubType;
			StringList slCOPsSubPhysicalId;
			int iCOPSubSize;
			String strSubstituteId;
			String strSubstituteType;
			String strSubPhysicalId;
			String strPhysicalId;
			int iLevel;
			String strSubstitutePhysicalId;
			
			if (BusinessUtil.isNotNullOrEmpty(sFPPId)) {				
				mlFPPChildData = getConnectedIntermediates(context, sFPPId, sRollupType, true);		
				int iSizeOfChildData = mlFPPChildData.size();
				if (null != mlFPPChildData && iSizeOfChildData > 0) {
					MapList mlCOPSubstituteIds = new MapList();
					MapList mlGetProductIds = new MapList();
					Map<String, String> mpProductMap;
					Map<String, String> mpSubstitueMap;
					StringList slPAPSubstituteIds;
					StringList slPAPSubstituteTypes;
					StringList slPAPSubstitutePhysicalIds;
					
					for (int i = 0; i < iSizeOfChildData; i++) {
						
						mpCOPMap = (Map) mlFPPChildData.get(i);
						strID = (String) (mpCOPMap).get(DomainConstants.SELECT_ID);
						strObjectType = (String) (mpCOPMap).get(DomainConstants.SELECT_TYPE);
						strPhysicalId = (String)(mpCOPMap).get(pgV3Constants.PHYSICALID);
						iLevel = Integer.parseInt((String) (mpCOPMap).get(DomainConstants.SELECT_LEVEL));
						
						if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)
								&& STR_ALLOWED_TYPE_SUBSTITUTE_CHILDCOPS.contains(sRollupType)) {

							slCOPsIdList = getSelectableList(mpCOPMap, strFrommid);
							slCOPsSubType = getSelectableList(mpCOPMap, strFromType);
							slCOPsSubPhysicalId = getSelectableList(mpCOPMap, strFromPhysicalId);
							iCOPSubSize = slCOPsIdList.size();
							for (int j = 0; j < iCOPSubSize; j++) {
								strSubstituteId = slCOPsIdList.get(j);
								strSubstituteType = slCOPsSubType.get(j);
								strSubPhysicalId = slCOPsSubPhysicalId.get(j);
								// Modified by DSM for 2018x.5 requirements 33585, 33590, 33565, 33584 - Starts
								if (STR_ALLOWED_COPBULKSUB_PRODUCT_PARTS.contains(strSubstituteType)) {
									// Processing Sub Product Parts of BULK COP
									mpProductMap = new HashMap<>();
									mpProductMap.put(DomainConstants.SELECT_ID, strSubstituteId);
									mpProductMap.put(DomainConstants.SELECT_TYPE, strSubstituteType);
									mpProductMap.put(pgV3Constants.PHYSICALID, strSubPhysicalId);
									mlGetProductIds.add(mpProductMap);
								} else if (!(strSubstituteType
										.equalsIgnoreCase(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART)
										&& !(configuration.getProperty("Str_Allowed_RollupTypes_PAP")
												.contains(sRollupType)))) {

									mpSubstitueMap = new HashMap<>();
									mpSubstitueMap.put(DomainConstants.SELECT_ID, strSubstituteId);
									mpSubstitueMap.put(DomainConstants.SELECT_TYPE, strSubstituteType);
									mpSubstitueMap.put(pgV3Constants.PHYSICALID, strSubPhysicalId);
									mlCOPSubstituteIds.add(mpSubstitueMap);
								}
								// Modified by DSM for 2018x.5 requirements 33585, 33590, 33565, 33584 - Ends
							}
						} else {
							// Getting Substitute PAP of PAP for warehousing classification 
							if (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strObjectType)
									&& pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION
											.equalsIgnoreCase(sRollupType)) {
								slPAPSubstituteIds = getSelectableList(mpCOPMap,strFrommid);
								slPAPSubstituteTypes = getSelectableList(mpCOPMap,strFromType);
								slPAPSubstitutePhysicalIds = getSelectableList(mpCOPMap,strFromPhysicalId);
								for (int k = 0; k < slPAPSubstituteTypes.size(); k++) {
									strSubStituteType = slPAPSubstituteTypes.get(k);
									strSubStituteId = slPAPSubstituteIds.get(k);
									strSubstitutePhysicalId = slPAPSubstitutePhysicalIds.get(k);
									if(STR_ALLOWED_PAP_SUBSTITUTE_TYPES.contains(strSubStituteType)){
										mpSubstitueMap = new HashMap<>();
										mpSubstitueMap.put(DomainConstants.SELECT_ID, strSubStituteId);
										mpSubstitueMap.put(DomainConstants.SELECT_TYPE, strSubStituteType);
										mpSubstitueMap.put(pgV3Constants.PHYSICALID, strSubstitutePhysicalId);
										mlCOPSubstituteIds.add(mpSubstitueMap);
									}											
								}								
							}
						}
						  //Added By DSM Team for Defect ID : 28419--END
						if (!(STR_ALLOWED_TYPES_NLEVEL.contains(sRollupType) && !pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType)
								&& iLevel != 1)) {
							mpProductMap = new HashMap<>();
							mpProductMap.put(DomainConstants.SELECT_ID, strID);
							mpProductMap.put(DomainConstants.SELECT_TYPE, strObjectType);
							mpProductMap.put(pgV3Constants.PHYSICALID, strPhysicalId);
							mlGetProductIds.add(mpProductMap);
						}
					}
					if (STR_ALLOWED_TYPE_SUBSTITUTE_CHILDCOPS.contains(sRollupType)) {
						//To call this method to process the connected Substitute COPs and PAPs.
						processSubstituteCOPChilds(context, mlCOPSubstituteIds, sRollupType, sFPPId,slProductList);
					}
					//To call this method to process the connected COPs and PAPs
					processConnectedProducts(context, mlGetProductIds, sRollupType, true, sFPPId,slProductList);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/** 
	 * This method process the substitute COPs
	 * @param context the eMatrix <code>Context</code> object
	 * @param MapList mlCOPSubstituteIds holds COPIds 
	 * @param String sRollupType holds Rollup Type
	 * @param String sFPPId holds FPP Id
	 * @param StringList slProductList holds existing connection physical ids
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processSubstituteCOPChilds(Context context, MapList mlCOPSubstituteIds, String sRollupType,
			String sFPPId,StringList slProductList) throws Exception {
		String STR_ALLOWED_TYPES_NLEVEL = configuration.getProperty("Str_Allowed_RollupTypes_NLevel");
		try {		  
            int iSizeOfCOPIds = mlCOPSubstituteIds.size();
			int iSizeOfCOPChildData = 0;
			if (iSizeOfCOPIds > 0) {
				String strCOPId;
				MapList mlCOPChildData;
				Map mpCOPMap;
				MapList mlGetProductIds = new MapList();               
				String sFPPObjectId = sFPPId;
				String strRollupType = sRollupType;
				Map<String, String> mpIntermediate ;
				
				for (int i = 0; i < iSizeOfCOPIds; i++) {
					mpIntermediate = (Map<String, String>) mlCOPSubstituteIds.get(i);
					strCOPId = mpIntermediate.get(DomainConstants.SELECT_ID);
					mlGetProductIds.add(mpIntermediate);
					
					if (BusinessUtil.isNotNullOrEmpty(strCOPId) && !STR_ALLOWED_TYPES_NLEVEL.contains(sRollupType)) {
						mlCOPChildData = getConnectedIntermediates(context, strCOPId, sRollupType, false);	
						iSizeOfCOPChildData = mlCOPChildData.size();
						if (iSizeOfCOPChildData > 0) {
							for (int j = 0; j < iSizeOfCOPChildData; j++) {
								mpCOPMap = (Map) mlCOPChildData.get(j);
								mlGetProductIds.add(mpCOPMap);
							}
						}
					}
				}
				processConnectedProducts(context, mlGetProductIds, strRollupType, false, sFPPObjectId,slProductList);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/** 
	 * This method process the product data for FPP connection
	 * @param context the eMatrix <code>Context</code> object
	 * @param StringList slObjectList holds intermediates details
	 * @param String sRollupType holds Rollup Type
	 * @param boolean bConsiderSubstitute holds the true or false. 
	 *                 true  - consider substitues
	 *				  false - dont consider substitues
	 * @param String sFPPId holds FPP Id
	 * @param StringList slProductList holds existing connection physical ids
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processConnectedProducts(Context context, MapList mlGetProductIds, String sRollupType,
			boolean bConsiderSubstitute, String sFFPId,StringList slProductPartList) throws Exception {

		Map mpProductMap = new HashMap();
		try {
			String STR_ALLOWED_PRODUCT_TYPES = configuration.getProperty("Str_Allowed_ProductParts_Types");			
			String STR_ALLOWED_PRODUCTPART_CHILDS = configuration.getProperty("Str_Allowed_ProductPart_Childs");
			String strRollupType = sRollupType;
			String sFPPObjectId = sFFPId;
			Map productMap;
			String sObjectid;
			String sObjectType;
			MapList mlProductList;
			int iSizeOfProductPart = 0;
			int iSizeOfObjectList = mlGetProductIds.size();
			Map<String, String> mpIntermediate;		

			if (null != mlGetProductIds && iSizeOfObjectList > 0) {
				for (int i = 0; i < iSizeOfObjectList; i++) {
					mpProductMap.clear();
					mpIntermediate = (Map<String, String>) mlGetProductIds.get(i);
					sObjectid =  mpIntermediate.get(DomainConstants.SELECT_ID);
					sObjectType = mpIntermediate.get(DomainConstants.SELECT_TYPE);
					
					if(BusinessUtil.isNotNullOrEmpty(sObjectType) && STR_ALLOWED_PRODUCT_TYPES.contains(sObjectType) && !STR_ALLOWED_PRODUCTPART_CHILDS.contains(strRollupType)) {
						
						processProductConnections(context, mpIntermediate, mpProductMap, false ,strRollupType);
						
					}else if(BusinessUtil.isNotNullOrEmpty(sObjectid)){
						mlProductList = getConnectedProducts(context, sObjectid, strRollupType, bConsiderSubstitute);		
						if(STR_ALLOWED_PRODUCT_TYPES.contains(sObjectType)) 
							mlProductList.add(mpIntermediate);
						iSizeOfProductPart = mlProductList.size();
						if (null != mlProductList && iSizeOfProductPart > 0) {
							for (int j = 0; j < iSizeOfProductPart; j++) {
								productMap = (Map) mlProductList.get(j);
								
								if(bConsiderSubstitute && STR_ALLOWED_PRODUCT_TYPES.contains(sObjectType)) {
									processProductConnections(context, productMap, mpProductMap, false,strRollupType);
								}else {
								processProductConnections(context, productMap, mpProductMap, bConsiderSubstitute,strRollupType);
								}
							}
						}
					}
					// This method is calling for each COP & PAP.				
					processRollupData(context, mpProductMap, strRollupType, sFPPObjectId, slProductPartList);
				}				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	
	/** 
	 * This method process the rollup data for FPP connection
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map mpProductMap holds product parts details
	 * @param String sRollupType holds Rollup Type
	 * @param String sFPPId holds FPP Id
	 * @param StringList slProductList holds existing connection physical ids
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processRollupData(Context context, Map mpProductMap, String sRollupType, String sFPPId,StringList slProductList)
			throws Exception {

		DomainObject domProductObj = null;
		MapList mlRollupObjectDataList;
		String strPhysicalId;
		String strProductId;
		StringList slObjectSelects = new StringList(3);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		String sTypeName = sRollupType;
		String sFPPObjectId = sFPPId;
		String strRelationshipId ;
		StringList slCountriesList;
		//Modified by DSM for 2018x.5 Certification Rollup Requirement - Starts
		StringList slRelSelects = new StringList(5);
		if (pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS.equalsIgnoreCase(sTypeName)) {
			slRelSelects.add(pgV3Constants.SELECT_CERTIFICATION_COUNTRYID);
			slRelSelects.add(pgV3Constants.SELECT_CERTIFICATION_AREAID);
			slRelSelects.add(pgV3Constants.SELECT_CERTIFICATION_REGIONID);
			slRelSelects.add(pgV3Constants.SELECT_CERTIFICATION_GROUPID);
			slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);			
		}
		//Modified by DSM for 2018x.5 Certification Rollup Requirement - Ends
		Map mpRollupData = new HashMap();
		Map tempMap = new HashMap();
		StringList slRollupObjList = new StringList();
		String strRollupObjId = DomainConstants.EMPTY_STRING;
		int iSizeOfRollUpObject;
		int iRollupDataSize;
		
		//Modified by DSM for 2018x.5 Certification Rollup Requirement - Starts
		StringList slAreasList;
		StringList slRegionsList;
		StringList slGroupsList;
		Map<String,StringList> mpCertificationMap;
		//Modified by DSM for 2018x.5 Certification Rollup Requirement - Ends
		String strProductDataRollupRel = configuration.getProperty("Str_ProductData_Relationship_"+sRollupType);			
		boolean bContextPushed = false;			
		try {
			Iterator iterator = mpProductMap.entrySet().iterator();
			while (iterator.hasNext()) {
				mpRollupData.clear();
			    slRollupObjList.clear();
				Map.Entry pair = (Map.Entry) iterator.next();
				strPhysicalId = (String) pair.getKey(); // Physical Id of the APP/DPP/FOP
				strProductId = (String) pair.getValue(); // Object Id of the APP/DPP/FOP

				if (sTypeName.equalsIgnoreCase(pgV3Constants.KEY_GHS)) {
					sTypeName = pgV3Constants.TYPE_COPYLIST;
				}else if (sTypeName.equalsIgnoreCase(pgV3Constants.TYPE_PGSTABILITYRESULTS)) {
					sTypeName = configuration.getProperty("Str_Stability_Document_Types");
			    }
				
				if (UIUtil.isNotNullAndNotEmpty(strProductId)) {
					domProductObj = DomainObject.newInstance(context, strProductId);
					//Added By DSM Team for Defect ID : 28509--START
					ContextUtil.pushContext(context,"User Agent", null, context.getVault().getName());
					bContextPushed = true;
					//Added By DSM Team for Defect ID : 28509--End
					mlRollupObjectDataList = domProductObj.getRelatedObjects(context, strProductDataRollupRel, // relationship// pattern
																				sTypeName, // Type pattern
																				slObjectSelects, // object selects
																				slRelSelects, // rel selects
																				false, // to side
																				true, // from side
																				(short) 1, // recursion level
																				null, // object where clause
																				null, // rel where clause
																				0);

					//Added By DSM Team for Defect ID : 28509--START
					ContextUtil.popContext(context);
					bContextPushed = false;
					//Added By DSM Team for Defect ID : 28509--End
					if (sTypeName.equalsIgnoreCase(pgV3Constants.TYPE_COPYLIST) || configuration.getProperty("Str_Stability_Document_Types").contains(sTypeName)) {
						sTypeName = sRollupType;
					}
					iSizeOfRollUpObject = mlRollupObjectDataList.size();
					if (null != mlRollupObjectDataList && iSizeOfRollUpObject > 0) {
			
						for (int j = 0; j < iSizeOfRollUpObject; j++) {							
							mpCertificationMap  = new HashMap<>(); 
							tempMap = (Map) mlRollupObjectDataList.get(j);
							strRollupObjId = (String) tempMap.get(DomainConstants.SELECT_ID);
							if (pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS.equalsIgnoreCase(sTypeName)) {
								strRelationshipId = (String)tempMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
								slCountriesList = getSelectableList(tempMap, pgV3Constants.SELECT_CERTIFICATION_COUNTRYID);
								slAreasList = getSelectableList(tempMap, pgV3Constants.SELECT_CERTIFICATION_AREAID);
								slRegionsList = getSelectableList(tempMap, pgV3Constants.SELECT_CERTIFICATION_REGIONID);
								slGroupsList = getSelectableList(tempMap, pgV3Constants.SELECT_CERTIFICATION_GROUPID);
								
								mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGCOUNTRIESCERTIED, slCountriesList);								
								mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGPLIAREACERTIFIED, slAreasList);								
								mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGPLIREGIONCERTIFIED, slRegionsList);															
								mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGPLIGROUPCERTIFIED, slGroupsList);
								
								strRollupObjId = strRollupObjId + ":" + strRelationshipId;
								mpRollupData.put(strRelationshipId, mpCertificationMap);
							}
							slRollupObjList.add(strRollupObjId);
						}					
						mpRollupData.put(pgV3Constants.PHYSICALID, strPhysicalId);
						mpRollupData.put(pgV3Constants.KEY_ROLLUPOBJECTLIST , slRollupObjList);
					}

					iRollupDataSize = mpRollupData.size();
					if(iRollupDataSize > 0)
						connectRollupData(context, mpRollupData, sTypeName, sFPPObjectId, slProductList);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			if(bContextPushed){
			    ContextUtil.popContext(context);
			}
		}
	}

	/** 
	 * This method connects the rollup data to FPP
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map mpRollupData holds rollup products details
	 * @param String sTypeName holds Rollup Type
	 * @param String sFPPId holds FPP Id
	 * @param StringList slProductList holds existing connection physical ids
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void connectRollupData(Context context, Map mpRollupData, String sTypeName, String sFPPId, StringList slProductList)
			throws Exception {
		boolean bContextPushed = false;
		try {
			String strFPPObjectId = sFPPId;
			if (UIUtil.isNotNullAndNotEmpty(strFPPObjectId)) {
				String strFPPRollupRel = configuration.getProperty("Str_FPPRollupRel_"+sTypeName);
				
				String strPhysicalId;
				String strRollUpObjectId;
				String strConnectionId;
				StringList slRollupIdList;
				boolean isFrom = true;
				boolean ignoreDuplicates = true;		
				String strRelId;
				StringList slObjectList;
				
				Map<String, StringList> mCertificationConnections = new HashMap<>();
				DomainObject domFPP = DomainObject.newInstance(context, strFPPObjectId);
				RelationshipType rFPPtoRollUpRel = new RelationshipType(strFPPRollupRel);

				Map mRelConnectedProduct = null;
				Map mRollUpData = mpRollupData;
				strPhysicalId = (String) mRollUpData.get(pgV3Constants.PHYSICALID);
				slRollupIdList = (StringList) mRollUpData.get(pgV3Constants.KEY_ROLLUPOBJECTLIST);
				
				int iSizeOfRollUpObject = slRollupIdList.size();
				if (slRollupIdList != null && iSizeOfRollUpObject > 0 && !slProductList.contains(strPhysicalId)) {
					Iterator itr = null;
					//Added By DSM Team for Defect ID : 28509--START
					ContextUtil.pushContext(context,"User Agent", null, context.getVault().getName());
					bContextPushed = true;
					//Added By DSM Team for Defect ID : 28509--End
					for (int i = 0; i < iSizeOfRollUpObject; i++) {
						strRollUpObjectId = slRollupIdList.get(i);
						slProductList.add(strPhysicalId);
						if (pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS.equalsIgnoreCase(sTypeName)) {
							slObjectList = FrameworkUtil.split(strRollUpObjectId, ":");
							strRollUpObjectId = slObjectList.get(0);
							strRelId = slObjectList.get(1);
							if(mRollUpData.containsKey(strRelId)) {
								mCertificationConnections = (Map) mRollUpData.get(strRelId);
							}
						}
						String[] rollupObjects = { strRollUpObjectId };
						try {
							mRelConnectedProduct = DomainRelationship.connect(context, domFPP, rFPPtoRollUpRel, isFrom,
								rollupObjects, ignoreDuplicates);
								
							if (mRelConnectedProduct != null && mRelConnectedProduct.size() > 0 && !pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION.equalsIgnoreCase(sTypeName)) {
							itr = mRelConnectedProduct.entrySet().iterator();
							while (itr.hasNext()) {
								Map.Entry mapRelId = (Map.Entry) itr.next();
								strConnectionId = mapRelId.getValue().toString();
									if(UIUtil.isNotNullAndNotEmpty(strConnectionId) && UIUtil.isNotNullAndNotEmpty(strPhysicalId))
									    DomainRelationship.setAttributeValue(context, strConnectionId,
											pgV3Constants.ATTRIBUTE_PGPRODUCTPARTPHYSICALID, strPhysicalId);
							
									
									if(pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS.equalsIgnoreCase(sTypeName) && !mCertificationConnections.isEmpty()){
									    processCertificationConnections(context,mCertificationConnections,strConnectionId);
								}
							}
						}
					//Added By DSM Team for Defect ID : 28430 & 28433 --START
					}catch(Exception ex) {
						ex.printStackTrace();
					}
					//Added By DSM Team for Defect ID : 28430 & 28433 --End
					}
				    //Added By DSM Team for Defect ID : 28509--START
					ContextUtil.popContext(context);
					bContextPushed = false;
					//Added By DSM Team for Defect ID : 28509--End
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}finally {
			if(bContextPushed){
			    ContextUtil.popContext(context);
			}
		}

	}

	/** 
	 * This method process the connected Alternates details to mpProductMap
	 * @param context the eMatrix <code>Context</code> object
	 * @param String sProductId holds Product Object Id
	 * @param Map mpProductMap holds map of connected rollup objects id
	 * @param String strRollupType holds rollup type
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processProductsAlternates(Context context, String sProductId, Map mpProductMap, String sRollupType) throws Exception{
		try {
			String STR_ALLOWED_TYPE_ALTERNATES = configuration.getProperty("Str_Allowed_RollupTypes_Alternates");
			if (BusinessUtil.isNotNullOrEmpty(sRollupType) && STR_ALLOWED_TYPE_ALTERNATES.contains(sRollupType)) {
			String strTypePattern = configuration.getProperty("Str_AllowedTypes_Alternates");
			DomainObject domProductDataObj = DomainObject.newInstance(context, sProductId);
			
			StringList selectStmts = new StringList(3);
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			selectStmts.addElement(pgV3Constants.PHYSICALID);
			
			String strAlternateProductId;
			String strAlternatePhysicalId;
			Map tempMap;
			
			MapList mlAlternatesList = domProductDataObj.getRelatedObjects(context,
																		DomainConstants.RELATIONSHIP_ALTERNATE, // relationship pattern
																		strTypePattern, // object pattern
																		selectStmts, // object selects
																		null, // relationship selects
																		false, // to direction
																		true, // from direction
																		(short) 1, // recursion level
																		DomainConstants.EMPTY_STRING, // object where clause
																		DomainConstants.EMPTY_STRING, (short) 0);
			for (int j = 0; j < mlAlternatesList.size(); j++) {
				tempMap = (Map) mlAlternatesList.get(j);
				strAlternateProductId = (String) tempMap.get(DomainConstants.SELECT_ID);
				strAlternatePhysicalId = (String) tempMap.get(pgV3Constants.PHYSICALID);
				if (null != strAlternatePhysicalId && null != strAlternateProductId) {
					mpProductMap.put(strAlternatePhysicalId, strAlternateProductId);
				}
			}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}
    
	/** 
	 * This method process the connected MPP details to mpProductMap
	 * @param context the eMatrix <code>Context</code> object
	 * @param String sProductId holds Product Object Id
	 * @param Map mpProductMap holds map of connected rollup objects id
	 * @param String strRollupType holds rollup type
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processMasterProductData(Context context, String sProductId, Map mpProductMap, String sRollupType) throws Exception{
		try {

			String STR_ALLOWED_TYPE_MASTER_PRODUCTPARTS = configuration.getProperty("Str_Allowed_RollupTypes_MasterProducts");
			if ((UIUtil.isNotNullAndNotEmpty(sRollupType)&& STR_ALLOWED_TYPE_MASTER_PRODUCTPARTS.contains(sRollupType))) {
			DomainObject domProductDataObj = DomainObject.newInstance(context, sProductId);
			StringList selectStmts = new StringList(4);
			selectStmts.addElement(pgV3Constants.SELECT_MASTERID_FROMPRODUCTPART);
			selectStmts.addElement(pgV3Constants.SELECT_MASTERPHYSICALID_FROMPRODUCTPART);
			selectStmts.addElement(pgV3Constants.SELECT_MASTERSTATE_FROMPRODUCTPART);
			selectStmts.addElement(pgV3Constants.SELECT_MASTERTYPE_FROMPRODUCTPART);

			String strMasterProductId;
			String strMasterPhysicalId;
			String strMPPType;
			String strMPPState;
			Map tempMap;
			
			MapList mlMasterProductList = domProductDataObj.getRelatedObjects(context,
																				pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM, // relationship pattern
																				pgV3Constants.TYPE_PARTFAMILY, // object pattern
																				null, // object selects
																				selectStmts, // relationship selects
																				true, // to direction
																				false, // from direction
																				(short) 1, // recursion level
																				DomainConstants.EMPTY_STRING, // object where clause
																				DomainConstants.EMPTY_STRING, (short) 0);
			for (int j = 0; j < mlMasterProductList.size(); j++) {
				tempMap = (Map) mlMasterProductList.get(j);
				strMPPType = (String) tempMap.get(pgV3Constants.SELECT_MASTERTYPE_FROMPRODUCTPART);
				strMPPState = (String) tempMap.get(pgV3Constants.SELECT_MASTERSTATE_FROMPRODUCTPART);
				
				if(UIUtil.isNotNullAndNotEmpty(strMPPType) && UIUtil.isNotNullAndNotEmpty(strMPPState) && strMPPType.equalsIgnoreCase(pgV3Constants.TYPE_MASTERPRODUCTPART) && strMPPState.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {
					strMasterProductId = (String) tempMap.get(pgV3Constants.SELECT_MASTERID_FROMPRODUCTPART);
					strMasterPhysicalId = (String) tempMap.get(pgV3Constants.SELECT_MASTERPHYSICALID_FROMPRODUCTPART);
					
					if (null != strMasterPhysicalId && null != strMasterProductId) {
						mpProductMap.put(strMasterPhysicalId, strMasterProductId);
					}	
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

	}
	
	/** 
	 * This method process the connected APP/DPP/FOP details to mpProductMap
	 * @param context the eMatrix <code>Context</code> object
	 * @param String sProductId holds Product Object Id
	 * @param Map mpProductMap holds map of connected rollup objects id
	 * @param String strRollupType holds rollup type
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processProducedBy(Context context, String sProductId, Map mpProductMap, String strRollupType)
			throws Exception {

		try {

			String STR_ALLOWED_TYPE_PRODUCE_BY = configuration.getProperty("Str_Allowed_RollupTypes_ProduceBy");
			String STR_ALLOWED_PRODUCTPART_CHILDS = configuration.getProperty("Str_Allowed_ProductPart_Childs");
			if(UIUtil.isNotNullAndNotEmpty(strRollupType) && STR_ALLOWED_TYPE_PRODUCE_BY.contains(strRollupType)) {
				
			DomainObject domProductDataObj = DomainObject.newInstance(context, sProductId);
			StringList selectStmts = new StringList(2);
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(pgV3Constants.PHYSICALID);
			Map tempMap;
						
			String strTypePattern = configuration.getProperty("Str_Allowed_ProductType_ProducedBy");
			String strPostTypePattern = configuration.getProperty("Str_Allowed_ProductType_ProducedBy");
			if(pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION.equalsIgnoreCase(strRollupType)) {
				strPostTypePattern = configuration.getProperty("Str_Allowed_ProductType_ForWareHouse");
			}
		
			MapList mlProducedByList = domProductDataObj.getRelatedObjects(context, 
																			pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL, // relationship pattern
																			strTypePattern, // Type pattern
																			true, // to side
																			false, // from side
																			(short) 1, // recursion level
																			selectStmts, // object selects
																			null, // rel selects
																			DomainConstants.EMPTY_STRING, // object where clause
																			null, // relWhereClause
																			0, //limit
																			null, // postRelPattern,
																			strPostTypePattern, // PostPattern
																			null);
			int iSizeOfProducedByList = mlProducedByList.size();
				String sPhysicalId;
				String sProductPartId;
				MapList mlChildProducts;
				int iChildProducts;
				Map mpProduct;
			for (int j = 0; j < iSizeOfProducedByList; j++) {
				tempMap = (Map) mlProducedByList.get(j);
					sPhysicalId = (String) tempMap.get(pgV3Constants.PHYSICALID);
					sProductPartId = (String) tempMap.get(DomainConstants.SELECT_ID);
					mpProductMap.put(sPhysicalId, sProductPartId);
					processMasterProductData(context,sProductPartId, mpProductMap, strRollupType);
					if(STR_ALLOWED_PRODUCTPART_CHILDS.equalsIgnoreCase(strRollupType)) {
						mlChildProducts = getConnectedProducts(context, sProductPartId, strRollupType, false);
						iChildProducts = mlChildProducts.size();
						for (int k = 0; k < iChildProducts; k++) {
							mpProduct = (Map) mlChildProducts.get(k);
							mpProductMap.put(mpProduct.get(pgV3Constants.PHYSICALID), mpProduct.get(DomainConstants.SELECT_ID));
						}
					}
				}				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
		
	/** 
	 * This method disconnects the connected rollup objects from FPP
	 * @param context the eMatrix <code>Context</code> object
	 * @param String strFPPObjectId holds FPP Id 
	 * @param String strRollupType holds rollup type
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void disconnetFPPExistingData(Context context, String strFPPObjectId, String strRollupType)
			throws Exception {
		boolean bContextPushed = false;
		try {
			String strConnectionId;
			//Added by DSM(Sogeti) for 2018x.2 Defect-29480 - Starts			
			String strOwner;
			String strPersonSYSCTRLM = configuration.getProperty("Str_Rollup_Ctrlm_User");
			//Added by DSM(Sogeti) for 2018x.2 Defect-29480 - Ends
			
			DomainObject domFPPObj = DomainObject.newInstance(context, strFPPObjectId);
			
			StringList slObjectSelects = new StringList(1);
			slObjectSelects.add(DomainConstants.SELECT_ID);
			
			StringList slRelSelects = new StringList(2);
			slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			//Modified by DSM(Sogeti) for 2018x.2 Defect-29480 - Starts
			if (strRollupType.equalsIgnoreCase(pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION)) {
				slRelSelects.add(DomainConstants.SELECT_OWNER);
			}
			//Modified by DSM(Sogeti) for 2018x.2 Defect-29480 - Ends
			
			String strRelationship = configuration.getProperty("Str_FPPRollupRel_"+strRollupType);
			
            StringList slDisconnection = new StringList();
			int iFPPData = 0;
			Map tempMap;
			
			if (strRollupType.equalsIgnoreCase(pgV3Constants.KEY_GHS)) {
				strRollupType = pgV3Constants.TYPE_COPYLIST;
			}else if(strRollupType.equalsIgnoreCase(pgV3Constants.TYPE_PGSTABILITYRESULTS)) {
				strRollupType =  configuration.getProperty("Str_Stability_Document_Types");
			 }
			//Added By DSM Team for Defect ID : 28509--START
			ContextUtil.pushContext(context,"User Agent", null, context.getVault().getName());
			bContextPushed = true;
			//Added By DSM Team for Defect ID : 28509--End
			MapList mlFPPData = domFPPObj.getRelatedObjects(context, strRelationship, // relationship pattern
					strRollupType, // Type pattern
					slObjectSelects, // object selects
					slRelSelects, // rel selects
					false, // to side
					true, // from side
					(short) 1, // recursion level
					null, // object where clause
					null, // rel where clause
					0);
				iFPPData = mlFPPData.size();
			if((mlFPPData != null) && (iFPPData>0)) {				
				for (int i = 0; i < iFPPData; i++) {
					tempMap = (Map<?, ?>) mlFPPData.get(i);
					strConnectionId = (String) tempMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					//Modified by DSM(Sogeti) for 2018x.2 Defect-29480 - Starts
					if (strRollupType.equalsIgnoreCase(pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION)) {
						strOwner = (String) tempMap.get(DomainConstants.SELECT_OWNER);
						//Modified by DSM(Sogiti) fo 2018x.5 Req# 35975 Starts
						if (UIUtil.isNotNullAndNotEmpty(strOwner) && (strOwner.equalsIgnoreCase(strPersonSYSCTRLM) || pgV3Constants.PERSON_USER_AGENT.equalsIgnoreCase(strOwner)))
							slDisconnection.add(strConnectionId);
						//Modified by DSM(Sogiti) fo 2018x.5 Req# 35975 Ends
					}else{
						slDisconnection.add(strConnectionId);
					}
                    //Modified by DSM(Sogeti) for 2018x.2 Defect-29480 - Ends					
				}
				String[] aDisconnections = slDisconnection.toArray(new String[slDisconnection.size()]);
				DomainRelationship.disconnect(context, aDisconnections);

				//Added By DSM Team for Defect ID : 28509--START
				ContextUtil.pushContext(context,"User Agent", null, context.getVault().getName());
				bContextPushed = false;
				//Added By DSM Team for Defect ID : 28509--End
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			if(bContextPushed){
			    ContextUtil.popContext(context);
			}
		}
	}

	/** 
	 * This method updates the corrosive attribute value on FPP
	 * @param context the eMatrix <code>Context</code> object
	 * @param String strFPPObjectId holds FPP Id 
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void updateCorrosiveForGHS(Context context, String strFPPObjectId) throws Exception{
		try {
			DomainObject domFPPObj = DomainObject.newInstance(context, strFPPObjectId);
			String strGHSCode;
			StringList slObjectSelects = new StringList(3);			
			slObjectSelects.addElement(DomainConstants.SELECT_TYPE);
			slObjectSelects.addElement(DomainConstants.SELECT_NAME);
			//Modified by DSM(Sogeti)2018x.2 for Defect:28984 - Starts
			slObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGHSCODE);
			//Modified by DSM(Sogeti)2018x.2 for Defect:28984 - Ends
			
			String strTypePattern = configuration.getProperty("Str_AllowedTypes_Corrosive");
			String strRelPattern = configuration.getProperty("Str_AllowedRelationship_Corrosive");
			String strPostTypePattern = configuration.getProperty("Str_AllowedPostTypes_Corrosive");
			
	        String strType;
			String strName;
			Map tempMap;
				
			MapList mlRollupData = domFPPObj.getRelatedObjects(context,strRelPattern, // relationship pattern
																strTypePattern, // Type pattern
																false, // to side
																true, // from side
																(short) 2, // recursion level
																slObjectSelects, // object selects
																null, // rel selects
																DomainConstants.EMPTY_STRING, // object where clause
																null, // relWhereClause
																0, //limit
																null, // postRelPattern,
																strPostTypePattern, // PostPattern
																null);// Map post pattern
			
			String sCorrosive = domFPPObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGCORROSIVE);
			boolean bflag = false;
			String sCorrosiveVal = pgV3Constants.KEY_NO_VALUE;
			int iSizeOfRollUpData = mlRollupData.size();
			for (int j = 0; j < iSizeOfRollUpData; j++) {
				tempMap = (Map) mlRollupData.get(j);
				strType = (String) tempMap.get(DomainConstants.SELECT_TYPE);
				strName = (String) tempMap.get(DomainConstants.SELECT_NAME);
				//Modified by DSM(Sogeti)2018x.2 for Defect:28984 - Starts
				if(UIUtil.isNotNullAndNotEmpty(strType) && (strType.equalsIgnoreCase(pgV3Constants.TYPE_WARNINGSTATEMENTSCOPY) || strType.	equalsIgnoreCase(pgV3Constants.TYPE_WARNINGSTATEMENTSMASTERCOPY))){
					strGHSCode = (String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGGHSCODE);
					//Modified by DSM(Sogeti)2018x for Defect - 28354 - Starts
					if ((strGHSCode.contains(pgV3Constants.KEY_H314))) {
						if(sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE))
							bflag = true;
						///Modified by DSM(Sogeti)2018x for Defect - 28354 - Ends
						sCorrosiveVal = pgV3Constants.KEY_YES_VALUE;
						break;
					} else if (!(strGHSCode.contains(pgV3Constants.KEY_H314))&& sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
						bflag = true;
						sCorrosiveVal = pgV3Constants.KEY_NO_VALUE;
					}
					//Modified by DSM(Sogeti)2018x.2 for Defect:28984 - Ends
				}else{
					//Modified by DSM(Sogeti)2018x for Defect - 28354 Starts
					if(UIUtil.isNotNullAndNotEmpty(strName) && strName.equalsIgnoreCase(pgV3Constants.CORROSIVE)){
						if(sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE))
						bflag = true;
					//Modified by DSM(Sogeti)2018x for Defect - 28354 Ends
						sCorrosiveVal = pgV3Constants.KEY_YES_VALUE;
						break;	
					}else if(UIUtil.isNotNullAndNotEmpty(strName) && !strName.equalsIgnoreCase(pgV3Constants.CORROSIVE) && sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)){
					   bflag = true;
					   sCorrosiveVal = pgV3Constants.KEY_NO_VALUE;
					}
				}
			}
			if (bflag || (mlRollupData.isEmpty() && sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE))) {
				domFPPObj.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCORROSIVE,sCorrosiveVal);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	 /** 
	 * This method will check if warehouseclassification attached to the FPP, if not then it will add "No Special Storage Required" object to the FPP.
	 * @param context the eMatrix <code>Context</code> object
	 * @param args
	 * @returns vois
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void checkWareHouseClassification(Context context, String strFPPObjectId, String strRollupType) throws Exception{

		MapList mlWarehouseClassificationData = new MapList();
		MapList returnMapList = null;
		DomainObject domFPP = DomainObject.newInstance(context,strFPPObjectId);
		StringList slObjectSelect = new StringList(2);
		slObjectSelect.addElement(DomainConstants.SELECT_ID);
		slObjectSelect.addElement(DomainConstants.SELECT_NAME);
		boolean bContextPushed = false;		
		try {
			String strWarehousingValue = configuration.getProperty("Str_WarehouseClassification_Default");
			
			mlWarehouseClassificationData = domFPP.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGTOWAREHOUSECLASSIFICATION, strRollupType, slObjectSelect, null, false, true, (short) 1, "", null, 0);
			int iSize = mlWarehouseClassificationData.size();
			if (mlWarehouseClassificationData.isEmpty() && iSize == 0) { 
				try {
					returnMapList = DomainObject.findObjects(context,
							strRollupType,					//typePattern
							strWarehousingValue,			//namepattern
							pgV3Constants.SYMBOL_STAR,		//revpattern
							pgV3Constants.SYMBOL_STAR,		//owner pattern
							context.getVault().getName(),	//vault pattern
							null,							//where exp
							false,							//expandType
							slObjectSelect);				//objectSelects
					String[] rollupObjects = { (String)((HashMap)returnMapList.get(0)).get(DomainConstants.SELECT_ID) };
					//DSM(Sogeti)2018x.2 modified for Defect 29251 - Starts
					ContextUtil.pushContext(context,"User Agent", null, context.getVault().getName());
					bContextPushed = true;
					DomainRelationship.connect(context, domFPP, pgV3Constants.RELATIONSHIP_PGTOWAREHOUSECLASSIFICATION, true, rollupObjects, false);
					ContextUtil.popContext(context);
					bContextPushed = false;
					//DSM(Sogeti)2018x.2 modified for Defect 29251 - Ends
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(bContextPushed){
			    ContextUtil.popContext(context);
			}
		}

	}
	
	/** 
	 * This method process the connected Substitute Parts
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map mpProductData holds product data
	 * @param Map mpProductMap holds map of connected rollup objects id
	 * @param String strRollupType holds rollup type
	 * @param boolean bNlevel holds the true or false
	 * @throws Exception
	 * Added for Defect-29816
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processConnectedSubstitutes(Context context, Map mpProductData, Map mpProductMap, String sRollupType, boolean bNLevel) throws Exception{
		try {
			MapList mlSubstitutesList = getSusbtituteProducts(context, mpProductData, sRollupType, bNLevel);
			int iSusbstituteList = mlSubstitutesList.size();
			Map mpSubstitute;
			String sProductId;
			String sProductType;
			String sProductPhysicalId;
			
			for(int i = 0; i < iSusbstituteList; i++) {
				mpSubstitute =   (Map) mlSubstitutesList.get(i);
				sProductId = (String) mpSubstitute.get(DomainConstants.SELECT_ID);
				sProductType = (String) mpSubstitute.get(DomainConstants.SELECT_TYPE);
				sProductPhysicalId = (String) mpSubstitute.get(pgV3Constants.PHYSICALID);

				if (pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sProductType)
								|| pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(sProductType)) {
					processProducedBy(context, sProductId, mpProductMap, sRollupType);
				} 
				if (configuration.getProperty("Str_AllowedProducts_ForMasters").contains(sProductType)) {
					//Process Masters for substitutes
					processMasterProductData(context, sProductId, mpProductMap, sRollupType);
				}
				if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductType)) {
						// to get alternates of substitutes
					processProductsAlternates(context, sProductId, mpProductMap, sRollupType);
				}
				if (configuration.getProperty("Str_AllowedProducts_ForSubstitutes").contains(sProductType))
					mpProductMap.put(sProductPhysicalId, sProductId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	/*
	 * @description: This method to provides values of specific key from Map
	 * @param mpProductMap	: holds product map
	 		  strKey	 	: contains key 
	 * @return StringList	: contains selectable list
	 * @throws Exception
	 * Modified for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static StringList getSelectableList(Map mpProductMap, String strKey) {

		StringList slSelectableList = new StringList();
		try {
	
			Object objSubstitutes = mpProductMap.get(strKey);
	
			if (objSubstitutes != null) {
				if (objSubstitutes instanceof StringList) {
					slSelectableList = (StringList) objSubstitutes;
				} else {
					slSelectableList.add(objSubstitutes.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return slSelectableList;		
	}
	/*
	 * @description: This method process connections on product part
	 * @param context
	 		  productMap 			: holds product data 
	 		  mpProductMap 			: holds map of connected rollup objects id
	 		  sRollupType  		 	: holds rollup type
	 		  bConsiderSubstitute  	: holds the true or false
	 * @return void
	 * @throws Exception
	 * Added for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static void processProductConnections(Context context, Map productMap, Map mpProductMap,
		Boolean bConsiderSubstitute, String strRollupType) throws Exception{
	
		String STR_ALLOWED_TYPE_SUBSTITUTES_PRODUCTPARTS = configuration.getProperty("Str_Allowed_RollupTypes_SubstitutesOfProductParts");
		String STR_ALLOWED_TYPES_NLEVEL = configuration.getProperty("Str_Allowed_RollupTypes_NLevel");
		String STR_ALLOWED_PRODUCT_TYPES = configuration.getProperty("Str_Allowed_ProductParts_Types");
		try {
			
			if (productMap != null) {
				String sProductId = (String) productMap.get(DomainConstants.SELECT_ID);
				String sProductType = (String) productMap.get(DomainConstants.SELECT_TYPE);
				String sPhysicalId = (String) productMap.get(pgV3Constants.PHYSICALID);
				
				// to get the Product By data
				if ((pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sProductType)
								|| pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(sProductType))) {
					processProducedBy(context, sProductId, mpProductMap, strRollupType);
				}else if(STR_ALLOWED_PRODUCT_TYPES.contains(sProductType)){
					// to get the Alternate Products
					processProductsAlternates(context, sProductId, mpProductMap, strRollupType);
					
					// to get the Master Parts
					processMasterProductData(context, sProductId, mpProductMap, strRollupType);
					
					mpProductMap.put(sPhysicalId, sProductId);

				}
                // to get the Substitute
				
				if (STR_ALLOWED_TYPE_SUBSTITUTES_PRODUCTPARTS.contains(strRollupType) && bConsiderSubstitute) {
					processConnectedSubstitutes(context, productMap, mpProductMap, strRollupType, false);
				
					if (STR_ALLOWED_TYPES_NLEVEL.contains(strRollupType)) {
						processConnectedSubstitutes(context, productMap, mpProductMap, strRollupType, true);
					}	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}	
	/*
	 * @description: This method process connected products
	 * @param context
	 		  sObjectId 			: holds intermediate Id 
	 		  sRollupType  		 	: holds rollup type
	 		  bConsiderSubstitute  	: holds the true or false
	 * @return MapList
	 * @throws Exception
	 * Added for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static MapList getConnectedProducts(Context context, String sObjectId, String sRollupType, boolean bConsiderSubstitute) throws FrameworkException{
		MapList mlProductList = new MapList();
		try {
			String STR_ALLOWED_TYPES_NLEVEL = configuration.getProperty("Str_Allowed_RollupTypes_NLevel");
			

			StringBuilder sbObjWhere = new StringBuilder();
			sbObjWhere.append(configuration.getProperty("Str_ObjectWhere_Obsolete_StateCheck"));
			if (sRollupType.equalsIgnoreCase(pgV3Constants.TYPE_PGPLIWAREHOUSEINGCLASSIFICATION)) {
				sbObjWhere.append(pgV3Constants.SYMBOL_SPACE).append(configuration.getProperty("Str_ObjectWhere_WareHouse_AttributeCheck"));
			}
			
			StringList slObjectSelects = new StringList(4);
			slObjectSelects.add(DomainConstants.SELECT_ID);
			slObjectSelects.add(DomainConstants.SELECT_TYPE);
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			slObjectSelects.add(pgV3Constants.PHYSICALID);
			
			StringList slRelSelects = new StringList(6);
			if (bConsiderSubstitute) {
				slRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
				slRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PHYSICALID);
				slRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
				// Modified by DSM(sogeti) for defect-29816 - Starts
				if (STR_ALLOWED_TYPES_NLEVEL.contains(sRollupType)) {
					slRelSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID);
					slRelSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_PHYSICALID);
					slRelSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE);
				}
			}
			
			String strTypePattern = configuration.getProperty("Str_AllowedType_ProductParts_" + sRollupType);
			String strPostTypePattern = configuration.getProperty("Str_AllowedPostTypePattern_ProductParts_" + sRollupType);
			String strRelPattern = configuration.getProperty("Str_AllowedRelationship_" + sRollupType);
			
			StringBuilder sbPostRelPattern = new StringBuilder();
			sbPostRelPattern.append(pgV3Constants.RELATIONSHIP_EBOM);

			if (STR_ALLOWED_TYPES_NLEVEL.contains(sRollupType)) {
				sbPostRelPattern.append(pgV3Constants.SYMBOL_COMMA);
				sbPostRelPattern.append(pgV3Constants.RELATIONSHIP_PLBOM);
			}

			short recurseToLevel = (short) 1;
			if (STR_ALLOWED_TYPES_NLEVEL.contains(sRollupType))
				recurseToLevel = (short) 0;
			
			
			DomainObject domObj = DomainObject.newInstance(context);
			if (BusinessUtil.isNotNullOrEmpty(sObjectId)) {
				domObj.setId(sObjectId);
				 mlProductList = domObj.getRelatedObjects(context, 
						 								strRelPattern, // relationshippattern
						 								strTypePattern, // Type pattern
						 								false, // to side
						 								true, // from side
						 								recurseToLevel, // recursion level
						 								slObjectSelects, // object selects
						 								slRelSelects, // rel selects
						 								sbObjWhere.toString(), // object where clause
						 								null, // relWhereClause
						 								0, //limit
						 								sbPostRelPattern.toString(), // postRelPattern,
						 								strPostTypePattern,// PostPattern
						 								null);// Map post pattern				
			}			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return mlProductList;	
	}
	
	/*
	 * @description: This method get Connected Intermediate objects
	 * @param context
	 		  sObjectId 			: holds Object Id 
	 		  sRollupType  		 	: holds rollup type
	 		  bConsiderSubstitute  	: holds the true or false
	 * @return MapList
	 * @throws Exception
	 * Added for 2018x.5 Rollup Configuration requirements 33937
	 */
	public static MapList getConnectedIntermediates(Context context, String sObjectId, String sRollupType, boolean bConsiderSubstitutes) 
			throws FrameworkException{
		MapList mlIntermediates = new MapList();
		
		try {
			String strPostTypePattern = configuration.getProperty("Str_AllowedPostTypePattern_Intermediates");
			StringBuilder sbObjectWhere = new StringBuilder();
			sbObjectWhere.append(configuration.getProperty("Str_ObjectWhere_Obsolete_StateCheck"));
			
			String strType = configuration.getProperty("Str_Allowed_Intermediates_" + sRollupType);
			
			StringList slEBOMObjSelects = new StringList(4);
			slEBOMObjSelects.add(DomainConstants.SELECT_ID);
			slEBOMObjSelects.add(DomainConstants.SELECT_TYPE);
			slEBOMObjSelects.add(DomainConstants.SELECT_NAME);
			slEBOMObjSelects.add(pgV3Constants.PHYSICALID);
			
			StringList slEBOMRelSelects = new StringList(3);
			if(bConsiderSubstitutes) {
				slEBOMRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
				slEBOMRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
				slEBOMRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PHYSICALID);
			}

			if (BusinessUtil.isNotNullOrEmpty(sObjectId)) {
				DomainObject domObject = DomainObject.newInstance(context,sObjectId);
				mlIntermediates = domObject.getRelatedObjects(context, 
																pgV3Constants.RELATIONSHIP_EBOM, // relationship pattern
																strType, // Type pattern
																false, // to side
																true, // from side
																(short) 0, // recursion level
																slEBOMObjSelects, // object selects
																slEBOMRelSelects, // rel selects
																sbObjectWhere.toString(), // object where clause
																null, // relWhereClause
														0, //limit
																pgV3Constants.RELATIONSHIP_EBOM, // postRelPattern,
																strPostTypePattern, // PostPattern
														null);// Map post pattern
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return mlIntermediates;
	}
	
	/*
	 * @description: This method get connected Substitutes Objects
	 * @param context
	 		  mpProductData : holds Product data details
	 		  sRollupType  	: holds rollup type
	 		  bNLevel		: holds the true or false
	 * @return MapList
	 * @throws Exception
	 * Added for 2018x.5 Rollup requirements 33937, 33585, 33590, 33565, 33584
	 */
	public static MapList getSusbtituteProducts(Context context, Map mpProductData, String sRollupType, boolean bNLevel) throws Exception{
		MapList mlProductList = new MapList();
		try {
			String strFrommid;
			String strFromPhysicalId;
			String strFromType;
			String strSubstituteType;
			String strSubstituteId;
			String strSubstitutePhysicalId;
			Map<String, String> mpProductMap;
			
			if(bNLevel){
				strFrommid = pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID;
				strFromPhysicalId = pgV3Constants.SELECT_FBOM_SUBSTITUTE_PHYSICALID;
				strFromType= pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE;
			}else{
				strFrommid = pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID;
				strFromPhysicalId = pgV3Constants.SELECT_EBOM_SUBSTITUTE_PHYSICALID;
				strFromType = pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE;
			}

			StringList slSubstitutesList = getSelectableList(mpProductData,strFrommid);				
			StringList slSubstitutePhysicalIdList = getSelectableList(mpProductData,strFromPhysicalId);				
			StringList slSusbtituteType = getSelectableList(mpProductData,strFromType);
				
			MapList mlIntermediateList;
			int iProductListSize = slSubstitutesList.size();
			int iIntermediateSize;
			Map<String, String> mpIntermediate;
			for (int i = 0; i < iProductListSize ; i++) {
				mpProductMap = new HashMap<>();
				strSubstituteType =	slSusbtituteType.get(i);
				strSubstituteType = ( strSubstituteType == null) ? DomainConstants.EMPTY_STRING : strSubstituteType;
				strSubstituteId = slSubstitutesList.get(i);
				strSubstituteId = ( strSubstituteId == null) ? DomainConstants.EMPTY_STRING : strSubstituteId;
				strSubstitutePhysicalId = slSubstitutePhysicalIdList.get(i);
				strSubstitutePhysicalId = ( strSubstitutePhysicalId == null) ? DomainConstants.EMPTY_STRING : strSubstitutePhysicalId;
				mpProductMap.put(DomainConstants.SELECT_ID,strSubstituteId);
				mpProductMap.put(DomainConstants.SELECT_TYPE, strSubstituteType);
				mpProductMap.put(pgV3Constants.PHYSICALID, strSubstitutePhysicalId);
				mlProductList.add(mpProductMap);
				if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) {
					mlIntermediateList = getConnectedIntermediates(context, strSubstituteId, sRollupType, false);
					mlIntermediateList.add(mpProductMap);
						iIntermediateSize = mlIntermediateList.size();
						for (int j = 0; j < iIntermediateSize ; j++) {
							mpIntermediate = (Map<String, String>) mlIntermediateList.get(j);
							mlProductList.addAll(getConnectedProducts(context, mpIntermediate.get(DomainConstants.SELECT_ID), sRollupType, false));
						}	
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return mlProductList;
	}
	/**
	 * This method proceess Certification connections 
	 * @param context
	 * @param mCerificationConnections contains certification connections
	 * @param strConnectionId holds EBOM relationship ID
	 * @throws FrameworkException
	 * Added by DSM as part for 2018x.5 Requirements
	 */
	public static void processCertificationConnections(Context context,Map<String,StringList> mCertificationConnections, String strConnectionId)
			throws FrameworkException {
		
		try {
			if(!mCertificationConnections.isEmpty()){
				StringList slConnectionsList;
				String sRelName;
				Iterator itrConnections;
				String sObjectId;
				String sCommandStatement = "add connection \"$1\" from \"$2\" torel \"$3\"";
				for (Map.Entry<String, StringList> entry : mCertificationConnections.entrySet()) {
					sRelName = entry.getKey();
					slConnectionsList = entry.getValue();
					itrConnections = slConnectionsList.iterator();
					while (itrConnections.hasNext()) {
						sObjectId = (String) itrConnections.next();
						if(UIUtil.isNotNullAndNotEmpty(sObjectId) && UIUtil.isNotNullAndNotEmpty(strConnectionId)) {
						     MqlUtil.mqlCommand(context, sCommandStatement, sRelName, sObjectId, strConnectionId);
						}
					}
				}
			}
		}catch (Exception ex) {
			ex.printStackTrace();	
			throw ex;
		}
	}
}
