package com.pg.widgets.nexusPerformanceChars;

import static com.matrixone.apps.domain.MultiValueSelects.RELATIONSHIP_REFERENCE_DOCUMENT;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.framework.ui.UITableCustom;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.png.apollo.pgApolloConstants;

import matrix.db.AccessConstants;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGPerfCharsFetchDataColumns {

	// Method 'Test Method' for Originating Source = DSO : Start
	// DSO 2013x.4 : Added changes for Test Method Name column of
	// pgVPDPerformanceCharacterisitcTable and pgVPDTestMethodView- START
	/**
	 * Overriding existing method to implement DSO logic
	 * Copied from method pgDSOUtil:pgGetTestMethods
	 */
	public MapList pgGetTestMethods(Context context, Map<?,?> programMap) throws Exception {
		MapList mlNameIdList = new MapList();		
		// DSM 2015x.1: Generic Export : work around for programHTMLOutput - START
		String isPGExport = (String) programMap.get("isPGExport");
		// DSM 2015x.1: Generic Export : work around for programHTMLOutput - END

		MapList mlObjectList = (MapList) programMap.get("objectList");
		Map mapParamList = (Map) programMap.get("paramList");

		String strParentOID = (String) mapParamList.get("parentOID");
		if (UIUtil.isNullOrEmpty(strParentOID)) {
			strParentOID = (String) mapParamList.get("objectId");
		}
		String strParentCurrent = "";
		boolean isReleased = false;
		if (UIUtil.isNotNullAndNotEmpty(strParentOID)) {
			strParentCurrent = DomainObject.newInstance(context, strParentOID).getInfo(context,
					DomainConstants.SELECT_CURRENT);
			if (PGPerfCharsConstants.STATE_RELEASE.equalsIgnoreCase(strParentCurrent)) {
				isReleased = true;
			}
		}

		String strReportFormat = (String) mapParamList.get("reportFormat");

		DomainObject doObj = DomainObject.newInstance(context);
		Iterator objListItr = mlObjectList.iterator();

		boolean isLayeredProductPart = false;

		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String charObjId = (String) perMap.get(DomainConstants.SELECT_ID);
			String objReadAccess = (String) perMap.get("objReadAccess");
			if (UIUtil.isNotNullAndNotEmpty(objReadAccess)
					&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess)) {
				StringList objectSelects = new StringList(4);
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainObject.SELECT_NAME);
				objectSelects.add(DomainObject.SELECT_TYPE);
				objectSelects.add("last.id");

				doObj.setId(charObjId);

				MapList objList = null;
				StringList relSelects = new StringList(1);
				String sRelRefDoc = DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT;
				String sPropertiesRel = "Properties Testing Requirements";

				objList = doObj.getRelatedObjects(context, sRelRefDoc + "," + sPropertiesRel,
						PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION, objectSelects, relSelects, true, false,
						(short) 1, null, null, 0);

				StringList slTemp = new StringList();
				String strTMId = "";
				HashMap nameIDMap = new HashMap();

				isLayeredProductPart = false;
				if (perMap.containsKey("isLayeredProductPart")) {
					isLayeredProductPart = (boolean) perMap.get("isLayeredProductPart");
					if (isLayeredProductPart) {
						objList = doObj.getRelatedObjects(context,
								pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD, // relationship pattern
								PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION, // type pattern
								objectSelects, // object selects
								relSelects, // relationship selects
								false, // to direction
								true, // from direction
								(short) 1, // recursion level
								null, // object where clause
								null, // relationship where clause
								0);// object limits
					}
				}

				for (int i = 0; i < objList.size(); i++) {
					Hashtable testMehtodsObjs = (Hashtable) objList.get(i);
					String pgTMName = (String) testMehtodsObjs.get("name");
					strTMId = (String) testMehtodsObjs.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strTMId) && !isReleased) {
						nameIDMap.put(pgTMName, strTMId);
					}
					if (!slTemp.contains(pgTMName)) {
						slTemp.add(pgTMName);
					}

					if (isLayeredProductPart) {
						nameIDMap.put(pgTMName, strTMId);
					}

				}

				if (isLayeredProductPart) {
					slTemp.sort();
					nameIDMap.put("sortedNames", slTemp);
				} else {
					if (isReleased) {
						nameIDMap = getHigherRevisionTMObj(context, slTemp, charObjId);
					} else {
						slTemp.sort();
						nameIDMap.put("sortedNames", slTemp);
					}

				}

				StringList slTMNames = (StringList) nameIDMap.get("sortedNames");

				for (int i = 0; i < slTMNames.size(); i++) {
					Map<String,String> mpObjIdNameMap = new HashMap<>();
					String pgTMName = slTMNames.get(i);
					if (UIUtil.isNotNullAndNotEmpty(pgTMName)) {
						if ("true".equalsIgnoreCase(isPGExport)) {
							mpObjIdNameMap.put(DomainConstants.SELECT_NAME, pgTMName);
							mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
							
						} else {
							String pgTMId = (String) nameIDMap.get(pgTMName);
							DomainObject domTM = DomainObject.newInstance(context, pgTMId);
							boolean bHasReadAccess = FrameworkUtil.hasAccess(context, domTM, "read");
							
							if (strReportFormat != null && strReportFormat.length() > 0) {
								mpObjIdNameMap.put(DomainConstants.SELECT_NAME, pgTMName);
								mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
							} else {
								mpObjIdNameMap.put(DomainConstants.SELECT_NAME, pgTMName);
								if(bHasReadAccess) {
									mpObjIdNameMap.put(DomainConstants.SELECT_ID, pgTMId);
								} else {
									mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
								}
							}
						}
					}
					mlNameIdList.add(mpObjIdNameMap);
				}

			}
		}
		return mlNameIdList;
	}

	/**
	 * DSO 2013x.5 - ALM : 4033 : Test method appears multiple times for a
	 * performance characteristc : Start
	 * 
	 * @param context
	 * @param StringList
	 * @param String
	 * @return HashMap
	 * @throws Exception
	 * @since DSO
	 */
	public static HashMap getHigherRevisionTMObj(Context context, StringList slTMNames, String charObjId)
			throws Exception {
		HashMap returnMap = new HashMap();
		String strLastRevCurrent = DomainConstants.EMPTY_STRING;
		String strLastReleasedID = DomainConstants.EMPTY_STRING;
		DomainObject doLastRev = DomainObject.newInstance(context);
		/*
		 * DSM (DS) 2015.1 - Fix for Children of revised assemblies are inheriting the
		 * wrong revision - END
		 */
		DomainObject dob;
		for (Object obj : slTMNames) {
			String strTMId = DomainConstants.EMPTY_STRING;
			String strLastTMId = DomainConstants.EMPTY_STRING;
			StringList slTMIds = new StringList();
			String strCommand = PGPerfCharsConstants.STR_MQL_COL_DATA_PRINT_OBJ;
			String strResult = MqlUtil.mqlCommand(context, strCommand, charObjId,
					"from[Reference Document|to.name=='" + obj.toString() + "'].to.id", "|");
			if (UIUtil.isNullOrEmpty(strResult)) {
				strResult = MqlUtil.mqlCommand(context, strCommand, charObjId,
						"to[Reference Document|from.name=='" + obj.toString() + "'].from.id", "|");
			}
			// DSM 2015x.1 Modified to handle both Test Method and pgTestMethod
			if (UIUtil.isNotNullAndNotEmpty(strResult)) {
				slTMIds = FrameworkUtil.split(strResult, "|");
				strTMId = (String) slTMIds.get(0);
				if (UIUtil.isNotNullAndNotEmpty(strTMId)) {
					dob = DomainObject.newInstance(context, strTMId);
					BusinessObject revisionObj = dob.getLastRevision(context);
					strLastTMId = (String) revisionObj.getObjectId(context);
					/*
					 * DSM (DS) 2015.1 - Fix for Children of revised assemblies are inheriting the
					 * wrong revision - START
					 */
					if (UIUtil.isNotNullAndNotEmpty(strLastTMId)) {
						doLastRev.setId(strLastTMId);
						strLastRevCurrent = doLastRev.getInfo(context, DomainConstants.SELECT_CURRENT);
						if (!PGPerfCharsConstants.STATE_RELEASE.equalsIgnoreCase(strLastRevCurrent)) {
							strLastReleasedID = doLastRev.getPreviousRevision(context).getObjectId();
							if (UIUtil.isNotNullAndNotEmpty(strLastReleasedID)) {
								strLastTMId = strLastReleasedID;
							}
						}
					}
					/*
					 * DSM (DS) 2015.1 - Fix for Children of revised assemblies are inheriting the
					 * wrong revision - END
					 */
				}
			}

			returnMap.put(obj.toString(), strLastTMId);
		}
		returnMap.put("sortedNames", slTMNames);
		return returnMap;
	}
	// DSO 2013x.4 : Added changes for Test Method Name column of
	// pgVPDPerformanceCharacterisitcTable and pgVPDTestMethodView - END
	// Method to fetch 'Test Method' for Originating Source = DSO : End

	// Method to fetch 'Test Method' for Originating Source != DSO : Start
	/**
	 * Method to get TS for non DSO objects
	 * Copied from the method pgIPMProductData:pgGetTestMethods
	 * 
	 * @param context
	 * @param programMap
	 * @return
	 * @throws Exception
	 */
	public MapList pgGetTestMethodsNonDSO(Context context, Map<?,?> programMap) throws Exception {
		MapList mlNameIdList = new MapList();
		MapList mlObjectList = (MapList) programMap.get("objectList");
		Map mapParamList = (Map) programMap.get("paramList");
		String strReportFormat = (String) mapParamList.get("reportFormat");
		boolean isCtxtPushed = false;
		Iterator objListItr = mlObjectList.iterator();
		try {
		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String objReadAccess = (String) perMap.get("objReadAccess");
			if (UIUtil.isNotNullAndNotEmpty(objReadAccess)
					&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess)) {

				String charObjId = (String) perMap.get(DomainConstants.SELECT_ID);
				StringList objectSelects = new StringList(4);
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainObject.SELECT_NAME);
				objectSelects.add(DomainObject.SELECT_TYPE);
				objectSelects.add("last.id");

				DomainObject doObj = DomainObject.newInstance(context, charObjId);

				String TMLogic = doObj.getInfo(context,
						"attribute[" + PropertyUtil.getSchemaProperty(context, "attribute_pgTMLogic") + "]");
				MapList objList = null;
				StringList relSelects = new StringList(1);
				String sRelRefDoc = DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT;
				String sPropertiesRel = "Properties Testing Requirements";

				objList = doObj.getRelatedObjects(context, sRelRefDoc + "," + sPropertiesRel, // relationshipPattern
						"pgTestMethod," + PGPerfCharsConstants.TEST_METHOD_SPECIFICATION, // typePattern
						true, // getTo
						false, // getFrom
						(short) 1, // recurseToLevel
						objectSelects, // objectSelects
						relSelects, // relationshipSelects
						null, // objectWhere
						null, // relationshipWhere
						null, // PostRelPattern
						"pgTestMethod," + PGPerfCharsConstants.TEST_METHOD_SPECIFICATION, // PostTypePattern
						null);

				StringList sortedName = new StringList();

				HashMap nameIDMap = new HashMap();

				DomainObject domTMObj = null;
				String strCssTypeValue = null;
				for (int i = 0; i < objList.size(); i++) {
					Hashtable testMehtodsObjs = (Hashtable) objList.get(i);
					String pgTMName = (String) testMehtodsObjs.get("name");
					String pgTMId = (String) testMehtodsObjs.get(DomainConstants.SELECT_ID);
					//DSM (DS) 2022x : Push context required to get information of ATTRIBUTE_PGCSSTYPE as context user doesn't have access to perform this action
					ContextUtil.pushContext(context);
					isCtxtPushed = true;
					domTMObj = DomainObject.newInstance(context, pgTMId);
					strCssTypeValue = domTMObj.getAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCSSTYPE);
					ContextUtil.popContext(context);
					isCtxtPushed = false;

					if (!"TAMU".equalsIgnoreCase(strCssTypeValue)) {
						sortedName.add(pgTMName);
						nameIDMap.put(pgTMName, pgTMId);
					}
					else {
						sortedName.add("");
						nameIDMap.put("", "");
					}
				}

				sortedName.sort();

				int iCounter = 0;
				for (int i = 0; i < objList.size(); i++) {
					String pgTMName = sortedName.get(i);
					if ((null != pgTMName) && (!pgTMName.isEmpty())) {
						iCounter++;
						Map<String,String> mpObjIdNameMap = new HashMap<>();
						String pgTMId = (String) nameIDMap.get(pgTMName);
						domTMObj = DomainObject.newInstance(context, pgTMId);
						boolean hasReadAccess = domTMObj.checkAccess(context, (short) AccessConstants.cRead);
						
						if (iCounter > 1) {
							Map<String,String> mpTMLogicObjIdNameMap = new HashMap<>();
							mpTMLogicObjIdNameMap.put(DomainConstants.SELECT_NAME, TMLogic);
							mpTMLogicObjIdNameMap.put(DomainConstants.SELECT_ID, "");
							mlNameIdList.add(mpTMLogicObjIdNameMap);
						} 	

						if (strReportFormat != null && strReportFormat.length() > 0) {
							mpObjIdNameMap.put(DomainConstants.SELECT_NAME, pgTMName);
							mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
						} else {
							mpObjIdNameMap.put(DomainConstants.SELECT_NAME, pgTMName);
							if(hasReadAccess) {
								mpObjIdNameMap.put(DomainConstants.SELECT_ID, pgTMId);
							} else {
								mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
							}
						}
						mlNameIdList.add(mpObjIdNameMap);
								
					}

				}
			}
		}
	} finally {
				if (isCtxtPushed)
				ContextUtil.popContext(context);
			}
		return mlNameIdList;
	}
	// Method to fetch 'Test Method' for Originating Source != DSO : End
	
	/**
	 * DSO 2013x.4 - Column Function for getting derived Path
	 * Copied from method emxCPNCharacteristicList:getDerivedPathForRow
	 * 
	 * @param context
	 * @param args
	 * @return List
	 * @throws exception
	 */
	public MapList getDerivedPathForRow(Context context, Map<?, ?> programMap) throws Exception {
		MapList mlNameIdList = new MapList();
		boolean isContextPushed = false;
		try {
			MapList mlObjectList = (MapList) programMap.get("objectList");
			// FSD_ChangeManagement_and_Release_Process sec 1.9.9- START
			Map mlParamList = (Map) programMap.get("paramList");
			String strselectedTable = (String) mlParamList.get("selectedTable");
			// Added for 22x Upgrade - ALM#48431 - Starts
			if (UIUtil.isNullOrEmpty(strselectedTable)) {
				strselectedTable = (String) mlParamList.get("table");
			}
			// Added for 22x Upgrade - ALM#48431 - Ends
			String strParentId = (String) mlParamList.get("parentOID");
			// Added for 22x Upgrade - ALM#48431 - Starts
			if (UIUtil.isNullOrEmpty(strParentId)) {
				strParentId = (String) mlParamList.get("objectId");
			}

			String strIsStructureCompare = (String) mlParamList.get("IsStructureCompare");

			String strPGExport = (String) programMap.get("isPGExport");
			boolean isPGExport = false;
			boolean isExport = false;
			String strReportFormat = (String) mlParamList.get("reportFormat");
			if (strReportFormat != null) {
				isExport = true;
			}
			if (strPGExport != null) {
				isPGExport = true;
			}

			String strCharwhere = "attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]=="
					+ PGPerfCharsConstants.STR_LOCAL;

			String strMasterPDF = (String) mlParamList.get("MasterPDF");

			String strRowMasterId = DomainConstants.EMPTY_STRING;
			String strRowMasterName = DomainConstants.EMPTY_STRING;
			String strRowType = DomainConstants.EMPTY_STRING;
			DomainObject masterObj = null;
			String strConnectedPFId = DomainConstants.EMPTY_STRING;
			String strParentType = DomainConstants.EMPTY_STRING;
			String derivedPath = DomainConstants.EMPTY_STRING;

			strselectedTable = UITableCustom.getSystemTableName(context, strselectedTable);
			String strMasterPartId = DomainConstants.EMPTY_STRING;
			String strMasterPartName = DomainConstants.EMPTY_STRING;
			boolean bMasterExists = false;
			// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - ends
			MapList partFamilyList = new MapList();

			String strSpecInheritanceType = DomainConstants.EMPTY_STRING;
			String strParentName = DomainConstants.EMPTY_STRING;

			MapList lowerPartFamilyMapList = null;
			StringList upperPartFamilyList = null;
			StringList lowerPartFamilyListLower = null;
			String strParentRefType = "";
			StringList partFamilyisList = new StringList();
			DomainObject doPartFamily = null;
			if (UIUtil.isNotNullAndNotEmpty(strParentId)) {
				DomainObject parentObj = DomainObject.newInstance(context, strParentId);
				// DSM (DS) 2015x.2 - ALM 9005 - Wrong MCOP found in Master Sepcification
				// -starts
				StringList slStmtList = new StringList(3);
				slStmtList.add("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
				slStmtList.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from[Part Family].id");
				slStmtList.add(DomainConstants.SELECT_TYPE);
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - STARTS
				slStmtList.add(DomainConstants.SELECT_NAME);
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - ENDS
				// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - STARTS
				slStmtList.add(DomainConstants.SELECT_REVISION);
				// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - ENDS
				// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Starts
				slStmtList.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id");
				// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - STARTS
				String selectMasterRevision = "to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.revision";
				String selectMasterName = "to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.name";
				slStmtList.add(selectMasterName);
				slStmtList.add(selectMasterRevision);

				Map parentObjInfo = parentObj.getInfo(context, slStmtList);
				strParentRefType = (String) parentObjInfo
						.get("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");

				// DSM 2015x.1 below code is to check whether the spec belongs to the same part
				// family heirarchy -starts
				String partFamilyId = (String) parentObjInfo
						.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from[Part Family].id");
				// DSM(DS) 2015x.4 ALM 14639-Related Spec Not Flowing from Upper Level MCUP to
				// FPP - start
				strParentType = (String) parentObjInfo.get(DomainConstants.SELECT_TYPE);

				strParentName = com.matrixone.apps.cpn.util.BusinessUtil.strcat(
						(String) parentObjInfo.get(DomainConstants.SELECT_NAME), PGPerfCharsConstants.BLANK_SPACE,
						(String) parentObjInfo.get(DomainConstants.SELECT_REVISION));

				strMasterPartId = (String) parentObjInfo.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
						+ "].frommid[" + PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id");
				// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - STARTS
				strMasterPartName = (String) parentObjInfo.get(selectMasterName);
				// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - ENDS
				if (UIUtil.isNotNullAndNotEmpty(strMasterPartId) && UIUtil.isNotNullAndNotEmpty(strMasterPartName)) {
					// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - STARTS
					strMasterPartName = com.matrixone.apps.cpn.util.BusinessUtil.strcat(strMasterPartName,
							PGPerfCharsConstants.BLANK_SPACE, (String) parentObjInfo.get(selectMasterRevision));
					// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - ENDS
					bMasterExists = true;
				}

				// DSM (DS) 2018x.3 Modified for Master-Title view Table - starts
				if (UIUtil.isNotNullAndNotEmpty(partFamilyId)
						&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE.equals(strselectedTable)
						&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
								.equals(strselectedTable)) {
					// DSM (DS) 2018x.3 Modified for Master-Title view Table - ends

					partFamilyisList = getPartFamilyListFromHeirarchy(context, partFamilyId);

				}

			}
			if (UIUtil.isNullOrEmpty(strselectedTable)) {
				strselectedTable = (String) mlParamList.get("table");
			}
			// FSD_ChangeManagement_and_Release_Process sec 1.9.9- END
			if (mlObjectList != null && !mlObjectList.isEmpty()) {
				Iterator objListItr = mlObjectList.iterator();
				String strPersonUserAgent = PropertyUtil.getSchemaProperty(context,
						PGPerfCharsConstants.PERSON_USER_AGENT);
				ContextUtil.pushContext(context, strPersonUserAgent, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
				isContextPushed = true;

				while (objListItr.hasNext()) {
					Map<String,String> mpObjIdNameMap = new HashMap<>();
					Map objMap = (Map) objListItr.next();
					if (objMap != null && !objMap.isEmpty()) {
						// DSM (DS) 2015x.1 - Added code to check the read access of the objects : Start
						String objReadAccess = (String) objMap.get("objReadAccess");

						if ((UIUtil.isNotNullAndNotEmpty(objReadAccess)
								&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess))
								&& ("pgVPDPerformanceCharacteristicTable".equals(strselectedTable)
										|| "ENCDocumentSummary".equals(strselectedTable)
										|| "pgVPDAPPDocumentSummary".equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
												.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
												.equals(strselectedTable))) {

							StringBuffer tempValBuffer = new StringBuffer();
							String charId = (String) objMap.get("id");
							String strRelationship = (String) objMap.get("relationship");
							// DSM(DS) 2015x.4 ALM 14639-Related Spec Not Flowing from Upper Level MCUP to
							// FPP - starts
							strRowMasterId = (String) objMap.get("strMasterID");
							strRowMasterName = (String) objMap.get("strMasterName");
							strRowType = (String) objMap.get("strRowType");
							derivedPath = (String) objMap.get("derivedPath");
							// SJV4: Added for SmartScope :: START

							// DSM - Ops- Defect # 53086-correct Part number is not fetched in Path colum in
							// Exported PDF - starts
							DomainObject CharObj = DomainObject.newInstance(context, charId);
							String sInheritedFromPlatform = (String) CharObj.getInfo(context, "to[" + strRelationship
									+ "].attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_INHERITED_FROM_PLATFORM + "]");
							String sPRPRev = "";
							// DSM - Ops- Defect # 53086-correct Part number is not fetched in Path colum in
							// Exported PDF - Ends

							String sPRPPath = "";

							String sPlatformId = "";
							if ("TRUE".equalsIgnoreCase(sInheritedFromPlatform)) {
								DomainObject parentObj = DomainObject.newInstance(context, strParentId);
								//// DSM - Ops- Defect # 53086-correct Part number is not fetched in Path colum
								//// in Exported PDF - Starts
								StringList slStmtList1 = new StringList(3);
								slStmtList1.add(
										"to[" + PGPerfCharsConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP + "].from.name");
								slStmtList1.add("to[" + PGPerfCharsConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP
										+ "].from.revision");
								slStmtList1.add(
										"to[" + PGPerfCharsConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP + "].from.id");
								Map mPRPObjInfo = parentObj.getInfo(context, slStmtList1);
								sPRPPath = (String) mPRPObjInfo.get(
										"to[" + PGPerfCharsConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP + "].from.name");
								sPRPRev = (String) mPRPObjInfo.get("to["
										+ PGPerfCharsConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP + "].from.revision");
								sPlatformId = (String) mPRPObjInfo.get(
										"to[" + PGPerfCharsConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP + "].from.id");
							}

							if (UIUtil.isNotNullAndNotEmpty(strRowMasterId)
									&& UIUtil.isNotNullAndNotEmpty(strParentType)
									&& PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
									&& ("pgVPDAPPDocumentSummary".equals(strselectedTable)
											|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
													.equals(strselectedTable))) {
								masterObj = DomainObject.newInstance(context, strRowMasterId);
								strConnectedPFId = masterObj.getInfo(context,
										"to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from["
												+ DomainConstants.TYPE_PART_FAMILY + "].id");
								partFamilyisList = getPartFamilyListFromHeirarchy(context, strConnectedPFId);
							}

							if (UIUtil.isNotNullAndNotEmpty(strRelationship)
									&& strRelationship.equals(PGPerfCharsConstants.REL_PG_INHERITED_CAD_SPEC)) {
								strRelationship = strRelationship + ","
										+ PGPerfCharsConstants.RELATIONSHIP_PART_SPECIFICATION;
							}
							DomainObject charObj = DomainObject.newInstance(context, charId);

							String specificationObjId = "";
							String strRefType = "";
							String connectedPFid = "";
							StringList connectedMasterNameList = new StringList();
							StringList connectedMasteridList = new StringList();

							Map<String, StringList> MasterPFMap = new HashMap<String, StringList>();
							// Modified by DSM(DS) for 2015x.4 ALM 13251 ENDS
							StringList connectedMasterPFList = new StringList();
							// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Starts
							strSpecInheritanceType = (String) objMap
									.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
							if (UIUtil.isNullOrEmpty(strSpecInheritanceType)) {
								strSpecInheritanceType = (String) objMap
										.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
							}
							// DSM(DS) 2015x.4 ALM 14639-Related Spec Not Flowing from Upper Level MCUP to
							// FPP - start
							if (UIUtil.isNullOrEmpty(strSpecInheritanceType)) {
								strSpecInheritanceType = (String) objMap.get("strInheritanceType");
							}

							if (PGPerfCharsConstants.STR_LOCAL.equalsIgnoreCase(strSpecInheritanceType)
									&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE
											.equals(strselectedTable)
									&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
											.equals(strselectedTable)) {

								if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
										&& (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
												|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
														.equals(strselectedTable))
										&& UIUtil.isNotNullAndNotEmpty(strRowMasterName)
										&& UIUtil.isNotNullAndNotEmpty(strRowMasterId)) {
									connectedMasteridList.add(strRowMasterId);
									connectedMasterNameList.add(strRowMasterName);
								}
								else if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
										&& (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
												|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
														.equals(strselectedTable))
										&& (PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT.equals(strRowType)
												|| PGPerfCharsConstants.TYPE_PG_INNERPACK.equals(strRowType)
												|| PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT.equals(strRowType))) {
									mpObjIdNameMap = new HashMap<>();
									if(derivedPath.contains("|")) {
										StringList slNameIdList = StringUtil.split(derivedPath, "|");
										mpObjIdNameMap.put(DomainConstants.SELECT_NAME, slNameIdList.get(0));
										mpObjIdNameMap.put(DomainConstants.SELECT_ID, slNameIdList.get(1));
										mlNameIdList.add(mpObjIdNameMap);
									} else {
										mpObjIdNameMap.put(DomainConstants.SELECT_NAME, derivedPath);
										mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
										mlNameIdList.add(mpObjIdNameMap);
									}
									continue;
								}
								else if ((PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
												.equals(strselectedTable))
										&& bMasterExists) {
									connectedMasteridList.add(strMasterPartId);
									connectedMasterNameList.add(strMasterPartName);
								} else {
									connectedMasteridList.add(strParentId);
									connectedMasterNameList.add(strParentName);
								}
								// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Ends
							} else if ("TRUE".equalsIgnoreCase(sInheritedFromPlatform)) // Add For Smart Scope Start
							{
								connectedMasteridList.add(sPlatformId);
								connectedMasterNameList.add(com.matrixone.apps.cpn.util.BusinessUtil.strcat(sPRPPath,
										PGPerfCharsConstants.BLANK_SPACE, sPRPRev));
							} // Add for Smart Scope End
							else {
								StringList selectStmts = new StringList();
								selectStmts.add(DomainConstants.SELECT_NAME);
								// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - STARTS
								selectStmts.add(DomainConstants.SELECT_REVISION);
								// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision - ENDS
								selectStmts.add(DomainConstants.SELECT_ID);
								selectStmts.add("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
								selectStmts.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
										+ "].from[Part Family].id");

								StringList selectRelStmts = new StringList(1);
								selectRelStmts.add("attribute[" + RELATIONSHIP_REFERENCE_DOCUMENT + "]");
								selectRelStmts.add("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");

								String strInheritanceType = null;

								MapList connectedPartList = charObj.getRelatedObjects(context, strRelationship,
										DomainConstants.QUERY_WILDCARD, selectStmts, selectRelStmts, true, false,
										(short) 1, "", strCharwhere, (short) 0);

								connectedPartList.sort(DomainConstants.SELECT_NAME, "ascending", "String");
								if (connectedPartList != null && connectedPartList.size() > 0) {
									Iterator mapItr = connectedPartList.iterator();
									Map map = null;
									connectedMasterNameList = new StringList();
									connectedMasteridList = new StringList();
									connectedMasterPFList = new StringList();

									while (mapItr.hasNext()) {
										map = (Map) mapItr.next();
										strRefType = (String) map
												.get("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
										strInheritanceType = (String) map.get(
												"attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
										if (("M".equals(strRefType)
												&& ("pgVPDAPPDocumentSummary".equals(strselectedTable)
														|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
																.equals(strselectedTable)))
												|| "pgVPDPerformanceCharacteristicTable".equals(strselectedTable)
												|| PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
														.equals(strselectedTable)
												|| (((("M".equals(strParentRefType) && "M".equals(strRefType))
														|| ("R".equals(strParentRefType) && "R".equals(strRefType)))
														&& "ENCDocumentSummary".equals(strselectedTable))))
										{
											if (UIUtil.isNotNullAndNotEmpty(strInheritanceType)
													&& strInheritanceType.equals(PGPerfCharsConstants.STR_LOCAL)) {
												connectedMasteridList.add((String) map.get(DomainConstants.SELECT_ID));
												// DSM (DS) 2015x.5 ALM 13483 - Path shows Obsolete MPP revision -
												// STARTS
												connectedMasterNameList.add(com.matrixone.apps.cpn.util.BusinessUtil
														.strcat((String) map.get(DomainConstants.SELECT_NAME),
																PGPerfCharsConstants.BLANK_SPACE,
																(String) map.get(DomainConstants.SELECT_REVISION)));
												Object pfObjecIds = map
														.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
																+ "].from[Part Family].id");
												// Modified by DSM(DS) for 2015x.4 ALM 13251 STARTS
												connectedMasterPFList = new StringList();

												if (null != pfObjecIds) {
													if (pfObjecIds instanceof String) {
														connectedMasterPFList.add((String) pfObjecIds);
													} else {
														connectedMasterPFList.addAll((StringList) pfObjecIds);
													}
												}
												MasterPFMap.put((String) map.get(DomainConstants.SELECT_ID),
														connectedMasterPFList);

											}
										}

									}

									if (!"pgVPDPerformanceCharacteristicTable".equals(strselectedTable)
											&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
													.equals(strselectedTable))
									{

										if (connectedMasteridList.size() > 1) {
											StringList masterIDlist = new StringList();
											StringList masterNamelist = new StringList();
											for (int i = 0; i < connectedMasteridList.size(); i++) {
												StringList MasterPFList = MasterPFMap
														.get((String) connectedMasteridList.get(i));
												boolean isSameHier = false;
												if (!MasterPFList.isEmpty()) {
													for (int l = 0; l < MasterPFList.size(); l++) {
														if (partFamilyisList.contains((String) MasterPFList.get(l))) {
															isSameHier = true;
															break;
														}
													}
												}

												if (isSameHier) {
													masterIDlist.add((String) connectedMasteridList.get(i));
													masterNamelist.add((String) connectedMasterNameList.get(i));
												}
											}
											connectedMasteridList = masterIDlist;
											connectedMasterNameList = masterNamelist;
										}
									}
									if (connectedMasterNameList.isEmpty()) {
										map = (Map) connectedPartList.get(0);
										connectedMasteridList.add((String) map.get(DomainConstants.SELECT_ID));
										connectedMasterNameList.add((String) map.get(DomainConstants.SELECT_NAME));
									}
								}
							}

							if (!connectedMasterNameList.isEmpty()) {
								for (int i = 0; i < connectedMasterNameList.size(); i++) {
									mpObjIdNameMap = new HashMap<>();
									if (isPGExport) {
										if (PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
												.equals(strselectedTable)
												|| PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART
														.equals(strParentType)) {
											mpObjIdNameMap.put(DomainConstants.SELECT_NAME, connectedMasterNameList.get(i));
											mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
										}
										if (UIUtil.isNotNullAndNotEmpty(strMasterPDF)
												&& "pdf".equalsIgnoreCase(strMasterPDF)) {
											mpObjIdNameMap.put(DomainConstants.SELECT_NAME, connectedMasterNameList.get(i));
											mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
										}
										// Added by DSM(Sogeti)-2015x.1 for PDF Views (Req ID-3985) on 27-May-2016 -
										// Ends
										else {
											mpObjIdNameMap.put(DomainConstants.SELECT_NAME, connectedMasterNameList.get(i));
											mpObjIdNameMap.put(DomainConstants.SELECT_ID, connectedMasteridList.get(i));

										}
									}

									if (isExport) {
										if (UIUtil.isNotNullAndNotEmpty(strReportFormat)
												&& ("CSV".equalsIgnoreCase(strReportFormat)
														|| "HTML".equalsIgnoreCase(strReportFormat)
														|| "text".equalsIgnoreCase(strReportFormat))) {
											// DSM (DS) 2018x.3 Modified for Master-Title view Table - starts
											if (PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
													.equals(strselectedTable)
													|| PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART
															.equals(strParentType)) {
												mpObjIdNameMap.put(DomainConstants.SELECT_NAME, connectedMasterNameList.get(i));
												mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
											}
										} else {											
											mpObjIdNameMap.put(DomainConstants.SELECT_NAME, connectedMasterNameList.get(i));
											mpObjIdNameMap.put(DomainConstants.SELECT_ID, connectedMasteridList.get(i));
										}
									}
									if (!isPGExport && !isExport) {
										mpObjIdNameMap.put(DomainConstants.SELECT_NAME, connectedMasterNameList.get(i));
										mpObjIdNameMap.put(DomainConstants.SELECT_ID, connectedMasteridList.get(i));
									}
									mlNameIdList.add(mpObjIdNameMap);
								}
							}
						} else {
							if ((UIUtil.isNotNullAndNotEmpty(objReadAccess)
									&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess))) {
								derivedPath = (String) objMap.get("derivedPath");
								if (UIUtil.isNotNullAndNotEmpty(derivedPath)) {
									mpObjIdNameMap = new HashMap<>();
									if(derivedPath.contains("|")) {
										StringList slNameIdList = StringUtil.split(derivedPath, "|");
										mpObjIdNameMap.put(DomainConstants.SELECT_NAME, slNameIdList.get(0));
										mpObjIdNameMap.put(DomainConstants.SELECT_ID, slNameIdList.get(1));
										mlNameIdList.add(mpObjIdNameMap);
									} else {
										mpObjIdNameMap.put(DomainConstants.SELECT_NAME, derivedPath);
										mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
										mlNameIdList.add(mpObjIdNameMap);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception Ex) {
			
			throw Ex;

		}

		finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}

		return mlNameIdList;
	}

	// DSM(DS) 2015x.4 ALM 14639-Related Spec Not Flowing from Upper Level MCUP to
	// FPP - start
	/**
	 * Given the part famlily id , the method returns all the part family ids that
	 * are connected to that part family heirarchy.
	 * 
	 * @param context
	 * @param partFamilyId
	 * @return StringList
	 * @throws exception Since DSM (DS) 2015x.4
	 */
	// DSM(DS) 2015x.4 - ALM 15191 misleading Master Part path and Title on Master
	// Specification tab of FPP - starts
	public StringList getPartFamilyListFromHeirarchy(Context context, String partFamilyId) throws Exception {
		StringList partFamilyisList = new StringList();
		// DSM(DS) 2015x.4 - ALM 15191 misleading Master Part path and Title on Master
		// Specification tab of FPP - End
		if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
			DomainObject doPartFamily = DomainObject.newInstance(context, partFamilyId);

			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			Map partFamilyMap = null;
			try {
				// this gets all the part families that are in that heirarchy
				MapList partFamilyMapList = doPartFamily.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_SUBCLASS,
						pgV3Constants.TYPE_PARTFAMILY, objectSelects, null, true, false, (short) 0, null, null, 0);
				if (!partFamilyMapList.isEmpty()) {
					Iterator itr = partFamilyMapList.iterator();
					while (itr.hasNext()) {
						partFamilyMap = (Map) itr.next();
						partFamilyisList.add((String) partFamilyMap.get(DomainConstants.SELECT_ID));
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return partFamilyisList;
	}

	/**
	 * DSO 2015x.4.1 March Downtime - Column Function for getting Master Part
	 * Details ALM - 16403 - Performance of FPC related specs screen is to slow
	 * 
	 * Copied from method emxCPNCharacteristicList:getMasterPartDetails
	 * 
	 * @param context
	 * @param args
	 * @return Vector
	 * @throws exception
	 */
	public StringList getMasterPartDetails(Context context, Map<?, ?> programMap) throws Exception {
		StringList slColumnValuesList = new StringList();
		try {
			StringBuffer sbColumValues = new StringBuffer();
			HashMap paramList = (HashMap) programMap.get("paramList");
			MapList objectList = (MapList) programMap.get("objectList");
			String strselectedTable = (String) paramList.get("selectedTable");
			// String reportFormat = (String)paramMap.get("reportFormat");

			// DSM (DS) 2018x.2 ALM-13483 Path shows Obsolete MPP revision - TO show blank
			// value in "Master Part" in Export - Start
			boolean isexport = false;
			boolean isPGExport = false;
			String strPGExport = (String) programMap.get("isPGExport");
			String strReportFormat = (String) paramList.get("exportFormat");
			if (strReportFormat != null) {
				isexport = true;
			}
			if (strPGExport != null) {
				isPGExport = true;
			}
			// DSM (DS) 2018x.2 ALM-13483 Path shows Obsolete MPP revision - TO show blank
			// value in "Master Part" in Export - End

			String strParentId = (String) paramList.get("parentOID");
			DomainObject domParentObj = null;
			if (UIUtil.isNotNullAndNotEmpty(strParentId)) {
				domParentObj = DomainObject.newInstance(context, strParentId);
			}
			String strobjectId = (String) paramList.get("objectId");
			String strMasterPDF = (String) paramList.get("MasterPDF");

			String strURL = DomainConstants.EMPTY_STRING;
			String strId = DomainConstants.EMPTY_STRING;
			String strobjReadAccess = DomainConstants.EMPTY_STRING;
			String strInheritanceType = DomainConstants.EMPTY_STRING;
			String strMasterName = DomainConstants.EMPTY_STRING;
			String strMasterTitle = DomainConstants.EMPTY_STRING;
			String strMasterID = DomainConstants.EMPTY_STRING;
			String derivedPath = DomainConstants.EMPTY_STRING;
			String strRowType = DomainConstants.EMPTY_STRING;
			String strRowTitle = DomainConstants.EMPTY_STRING;
			String strRefId = DomainConstants.EMPTY_STRING;
			String strRefObjId = DomainConstants.EMPTY_STRING;
			Map objMap = null;

			for (Iterator iterator = objectList.iterator(); iterator.hasNext();) {
				objMap = (Map) iterator.next();
				sbColumValues = new StringBuffer();
				strId = (String) objMap.get("id");
				strobjReadAccess = (String) objMap.get("objReadAccess");
				strInheritanceType = (String) objMap.get("strInheritanceType");
				if (UIUtil.isNullOrEmpty(strInheritanceType)) {
					strInheritanceType = (String) objMap
							.get(DomainObject.getAttributeSelect(PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE));
				}
				if (UIUtil.isNullOrEmpty(strInheritanceType)) {
					strInheritanceType = (String) objMap
							.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
				}

				if (PGPerfCharsConstants.STR_LOCAL.equals(strInheritanceType)) {
					slColumnValuesList.add(DomainConstants.EMPTY_STRING);
				} else {

					strMasterName = (String) objMap.get("strMasterName");
					strMasterTitle = (String) objMap.get("strMasterTitle");
					strMasterID = (String) objMap.get("strMasterID");
					derivedPath = (String) objMap.get("derivedPath");
					strRowType = (String) objMap.get("strRowType");
					strRowTitle = (String) objMap.get("strRowTitle");

					if (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equalsIgnoreCase(strselectedTable)
							&& domParentObj.isKindOf(context, PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART)) {
						strRefObjId = derivedPath.substring(derivedPath.lastIndexOf("objectId=") + 9);
						if (UIUtil.isNotNullAndNotEmpty(strRefObjId)) {
							strRefId = strRefObjId.substring(0, strRefObjId.indexOf("'"));
						}
					}

					if (UIUtil.isNotNullAndNotEmpty(strRowTitle) && UIUtil.isNullOrEmpty(strMasterTitle)) {
						strMasterTitle = strRowTitle;
					}
					// DSM (DS) 2018x.2 ALM-13483 Path shows Obsolete MPP revision - TO show blank
					// value in "Master Part" in Export - Start
					if (isexport) {
						if (UIUtil.isNotNullAndNotEmpty(strReportFormat)
								&& ("CSV".equals(strReportFormat) || "HTML".equalsIgnoreCase(strReportFormat)
										|| "text".equalsIgnoreCase(strReportFormat))) {
							slColumnValuesList.add(DomainConstants.EMPTY_STRING);
						} else {
							strURL = XSSUtil.encodeForHTML(context,
									"../common/emxTable.jsp?program=emxCPNCharacteristicList:getObjectsForMasterTable&table=pgDSOMasterPartDetails&selection=multiple&header=emxCPN.Characteristics.MasterPartDetails&HelpMarker=emxhelprelatedspecificationspd&parentRelName=relationship_PartSpecification&suiteKey=CPN&objectId="
											+ strobjectId + "&objReadAccess=" + strobjReadAccess
											+ "&strInheritanceType=" + strInheritanceType + "&strMasterName="
											+ strMasterName + "&strMasterTitle=" + strMasterTitle + "&strMasterID="
											+ strMasterID + "&selectedTable=" + strselectedTable + "&parentOID="
											+ strParentId + "&MasterPDF=" + strMasterPDF + "&strRowType=" + strRowType
											+ "&id=" + strId + "&strRefId=" + strRefId);
							sbColumValues.append("<a  onclick=\"javascript:emxTableColumnLinkClick('").append(strURL)
									.append("','', '', 'false', 'popup', '')\">Click Here</a>");
							slColumnValuesList.add(XSSUtil.encodeForXML(context, sbColumValues.toString()));
						}
					}

					if (isPGExport) {
						if (UIUtil.isNotNullAndNotEmpty(strPGExport) && ("true".equalsIgnoreCase(strPGExport))) {
							slColumnValuesList.add(DomainConstants.EMPTY_STRING);
						} else {
							strURL = XSSUtil.encodeForHTML(context,
									"../common/emxTable.jsp?program=emxCPNCharacteristicList:getObjectsForMasterTable&table=pgDSOMasterPartDetails&selection=multiple&header=emxCPN.Characteristics.MasterPartDetails&HelpMarker=emxhelprelatedspecificationspd&parentRelName=relationship_PartSpecification&suiteKey=CPN&objectId="
											+ strobjectId + "&objReadAccess=" + strobjReadAccess
											+ "&strInheritanceType=" + strInheritanceType + "&strMasterName="
											+ strMasterName + "&strMasterTitle=" + strMasterTitle + "&strMasterID="
											+ strMasterID + "&selectedTable=" + strselectedTable + "&parentOID="
											+ strParentId + "&MasterPDF=" + strMasterPDF + "&strRowType=" + strRowType
											+ "&id=" + strId + "&strRefId=" + strRefId);
							sbColumValues.append("<a  onclick=\"javascript:emxTableColumnLinkClick('").append(strURL)
									.append("','', '', 'false', 'popup', '')\">Click Here</a>");
							slColumnValuesList.add(sbColumValues.toString());
						}
					}
					// DSM (DS) 2018x.2 ALM-13483 Path shows Obsolete MPP revision - TO show blank
					// value in "Master Part" in Export - Start
					if (!isPGExport && !isexport) {
						strURL = XSSUtil.encodeForHTML(context,
								"../common/emxTable.jsp?program=emxCPNCharacteristicList:getObjectsForMasterTable&table=pgDSOMasterPartDetails&selection=multiple&header=emxCPN.Characteristics.MasterPartDetails&HelpMarker=emxhelprelatedspecificationspd&parentRelName=relationship_PartSpecification&suiteKey=CPN&objectId="
										+ strobjectId + "&objReadAccess=" + strobjReadAccess + "&strInheritanceType="
										+ strInheritanceType + "&strMasterName=" + strMasterName + "&strMasterTitle="
										+ strMasterTitle + "&strMasterID=" + strMasterID + "&selectedTable="
										+ strselectedTable + "&parentOID=" + strParentId + "&MasterPDF=" + strMasterPDF
										+ "&strRowType=" + strRowType + "&id=" + strId + "&strRefId=" + strRefId);
						sbColumValues.append("<a  onclick=\"javascript:emxTableColumnLinkClick('").append(strURL)
								.append("','', '', 'false', 'popup', '')\">Click Here</a>");
						slColumnValuesList.add(sbColumValues.toString());
					}
				}
			}

		} catch (Exception e) {
			
			throw e;
		}

		return slColumnValuesList;
	}
	
	/**
	 * Gets the performance characteristics : pgCharacteristic and Characteristic Specifics data and merges them and shows in characteristic column
	 * Copied from the method pgIPMProductData:pgGetCharacteristicColumnVal
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	public StringList pgGetCharacteristicColumnVal(Context context, Map<?, ?> programMap) throws FrameworkException {
		MapList mlObjectList = (MapList) programMap.get("objectList");
		Map mlParamList = (Map) programMap.get("paramList");
		String reportFormat = (String) mlParamList.get("reportFormat");
		StringList slReturnVal = new StringList();
		Iterator objListItr = mlObjectList.iterator();

		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String charObjId = (String) perMap.get(DomainConstants.SELECT_ID);
			DomainObject doObj = DomainObject.newInstance(context, charObjId);
			StringList slObjSelectsList = new StringList();
			slObjSelectsList.add(PGPerfCharsConstants.SELECT_ATTR_PG_CHARACTERISTIC);
			slObjSelectsList.add(PGPerfCharsConstants.SELECT_ATTR_PG_CHARACTERISTIC_SPECIFICS);
			
			Map<?,?> mpCharsInfoMap = doObj.getInfo(context, slObjSelectsList);
			String pgCharInfo = (String) mpCharsInfoMap.get(PGPerfCharsConstants.SELECT_ATTR_PG_CHARACTERISTIC);
			String pgCharSpecsInfo = (String) mpCharsInfoMap.get(PGPerfCharsConstants.SELECT_ATTR_PG_CHARACTERISTIC_SPECIFICS);
			StringBuilder tempVal = new StringBuilder();
			if (reportFormat != null && reportFormat.length() > 0)
			{
				tempVal.append(pgCharInfo).append("\n").append(pgCharSpecsInfo);
			}
			else
			{
				pgCharInfo = pgCharInfo.replaceAll("[<]", "&lt;");
				pgCharInfo = pgCharInfo.replaceAll("[>]", "&gt;");
				pgCharSpecsInfo = pgCharSpecsInfo.replaceAll("[<]", "&lt;");
				pgCharSpecsInfo = pgCharSpecsInfo.replaceAll("[>]", "&gt;");
				tempVal.append(pgCharInfo).append("<hr/>").append(pgCharSpecsInfo);
			}
			slReturnVal.add(tempVal.toString());
		}

		if (slReturnVal.isEmpty()) {
			slReturnVal.add("");
		}
		
		return slReturnVal;
	}
	/**
	 * Gets the performance characteristics : Pg Nexus Parameter List ID 
	 *
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException 
	 * @throws Exception
	 */
	public StringList getNexusParametrListIDForRow(Context context, Map<?, ?> programMap) throws MatrixException {
		MapList mlObjectList = (MapList) programMap.get("objectList");
		StringList slReturnVal = new StringList();
		Iterator objListItr = mlObjectList.iterator();
		String strNexusParPCId =DomainConstants.EMPTY_STRING;
		String strNexusParListId =DomainConstants.EMPTY_STRING;
		boolean isCtxtPushed = false;
		try {
		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String pcObjId = (String) perMap.get(DomainConstants.SELECT_ID);
			DomainObject doObj = DomainObject.newInstance(context, pcObjId);

			if(UIUtil.isNotNullAndNotEmpty(pcObjId)) {
				ContextUtil.pushContext(context);
				isCtxtPushed = true;
				strNexusParPCId = doObj.getInfo(context, PGPerfCharsConstants.SELECT_ATTR_PG_NEXUS_PARAMETER_ID);
				if(UIUtil.isNotNullAndNotEmpty(strNexusParPCId) && new BusinessObject(strNexusParPCId).exists(context)) {
					DomainObject doPCObj = DomainObject.newInstance(context, strNexusParPCId);
					strNexusParListId = doPCObj.getInfo(context, PGPerfCharsConstants.SELECT_ATTR_PG_NEXUS_PARAMETER_LIST_ID);
				} else {
					strNexusParPCId =DomainConstants.EMPTY_STRING;
					strNexusParListId=DomainConstants.EMPTY_STRING;
				}
			}
			slReturnVal.add(strNexusParListId);
		}
		if (slReturnVal.isEmpty()) {
			slReturnVal.add("");
		} 
		}finally {
			if (isCtxtPushed)
			ContextUtil.popContext(context);
		}
		return slReturnVal;
	}
	/**
	 * Gets the reference document names from relationship Properties testing requirements
	 * Method copied from pgIPMProductData:pgGetReferenceDocGCAS
	 * 
	 * @param context
	 * @param programMap
	 * @return
	 * @throws Exception
	 */
	public MapList pgGetReferenceDocGCAS(Context context, Map<?, ?> programMap) throws Exception {
		MapList mlNameIdList = new MapList();
		String isPGExport = (String) programMap.get("isPGExport");
		MapList mlObjectList = (MapList) programMap.get("objectList");
		Map mapParamList = (Map) programMap.get("paramList");
		MapList objList = null;
		Iterator objListItr = mlObjectList.iterator();
		String strReportFormat = (String) mapParamList.get("reportFormat");

		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String objReadAccess = (String) perMap.get("objReadAccess");
			StringBuffer objTMRDTypes = new StringBuffer();

			if (UIUtil.isNotNullAndNotEmpty(objReadAccess)
					&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess)) {
				String charObjId = (String) perMap.get(DomainConstants.SELECT_ID);
				String charObjType = (String) perMap.get(DomainConstants.SELECT_TYPE);

				DomainObject doObj = DomainObject.newInstance(context, charObjId);

				if (UIUtil.isNullOrEmpty(charObjType))
					charObjType = doObj.getInfo(context, DomainConstants.SELECT_TYPE);

				StringList objectSelect = new StringList(2);
				objectSelect.add(DomainConstants.SELECT_ID);
				objectSelect.add("last.id");
				objectSelect.add(DomainConstants.SELECT_NAME);
				objectSelect.add(DomainConstants.SELECT_TYPE);
				objectSelect.add("attribute[pgCSSType]");

				String sRelRefDoc = PropertyUtil.getSchemaProperty(null,"relationship_ReferenceDocument");

				String sRelPropTestReqr = PropertyUtil.getSchemaProperty(context,
						"relationship_PropertiesTestingRequirements");

				String relType = sRelRefDoc + "," + sRelPropTestReqr;

				if (UIUtil.isNotNullAndNotEmpty(charObjType)
						&& charObjType.equalsIgnoreCase(PGPerfCharsConstants.TYPE_PG_STABILITY_RESULTS))
					objTMRDTypes.append(PGPerfCharsConstants.TYPE_PG_IRM_DOC_TYPES);
				else
					objTMRDTypes.append(PGPerfCharsConstants.TYPE_PG_TMRD_TYPES).append(",")
							.append(pgV3Constants.TYPE_PGTESTMETHOD).append(",")
							.append(pgV3Constants.TYPE_PGSTACKINGPATTERN);

				objList = doObj.getRelatedObjects(context, relType, objTMRDTypes.toString(), objectSelect, null, true,
						false, (short) 1, null, null, 0);

				Iterator objIter = objList.iterator();
				Map objMap = null;
				HashMap nameIDMap = new HashMap();
				StringList sortedNames = new StringList();
				DomainObject domObj = null;
				String strParentId = (String) mapParamList.get("parentOID");
				String objOriginatingSource = null;
				if (null != strParentId) {
					DomainObject parentObj = DomainObject.newInstance(context, strParentId);
					objOriginatingSource = (String) parentObj.getInfo(context, PGPerfCharsConstants.SELECT_ATTR_PGORIGINATINGSOURCE);
				}

				while (objIter.hasNext()) {
					objMap = (Map) objIter.next();
					String objType = (String) objMap.get(DomainConstants.SELECT_TYPE);
					String objPGCssType = (String) objMap.get("attribute[pgCSSType]");

					if (PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equals(objOriginatingSource)) {
						if (!objType.equals("pgTestMethod")) {
							String objId = (String) objMap.get("id");
							String objName = (String) objMap.get(DomainConstants.SELECT_NAME);

							if (!sortedNames.contains(objName)) {
								sortedNames.add(objName);
								nameIDMap.put(objName, objId);
							}
						}
					} else {
						if (objType.equals("pgTestMethod") && (!objPGCssType.equals("TAMU")))
							continue;

						String objId = (String) objMap.get(DomainConstants.SELECT_ID);
						String objName = (String) objMap.get(DomainConstants.SELECT_NAME);
						sortedNames.add(objName);
						nameIDMap.put(objName, objId);
					}

				}

				sortedNames.sort();

				for (Iterator namesItr = sortedNames.iterator(); namesItr.hasNext();) {
					Map<String,String> mpObjIdNameMap = new HashMap<>();
					String objName = (String) namesItr.next();
					String objId = (String) nameIDMap.get(objName);

					if (UIUtil.isNotNullAndNotEmpty(isPGExport) && "true".equalsIgnoreCase(isPGExport)) {
						mpObjIdNameMap.put(DomainConstants.SELECT_NAME, objName);
						mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");

					} else {
						if (strReportFormat != null && strReportFormat.length() > 0) {
							mpObjIdNameMap.put(DomainConstants.SELECT_NAME, objName);
							mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");

						} else {
							domObj = DomainObject.newInstance(context, objId);
							boolean hasReadAccess = domObj.checkAccess(context, (short) AccessConstants.cRead);

							if (hasReadAccess) {
								mpObjIdNameMap.put(DomainConstants.SELECT_NAME, objName);
								mpObjIdNameMap.put(DomainConstants.SELECT_ID, objId);
							} else {
								mpObjIdNameMap.put(DomainConstants.SELECT_NAME, objName);
								mpObjIdNameMap.put(DomainConstants.SELECT_ID, "");
							}
						}
					}
					
					mlNameIdList.add(mpObjIdNameMap);
				}
			}
		}
		return mlNameIdList;
	}
	
	 /**
	 * DSM (DS) 2015x.1.2  - Column Function for getting Title of the Derived Master 
	 * 
	 * Method copied from pgDSOUtil:getDerivedTitleForRow
	 * 
	 * @param context
	 * @param programMap
	 * @return List List of Title value of each object
	 * @throws exception   
	 */	
	public StringList getDerivedTitleForRow(Context context, Map<?, ?> programMap) throws Exception {
		StringList slReturnValueList = new StringList();
		final String SELECT_ATTR_REF_TYPE = "attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]";
		final String SELECT_ATTR_TITLE = "attribute[" + PGPerfCharsConstants.ATTRIBUTE_TITLE + "]";
		final String SELECT_TO_CLASS_ITEM_FROM_PART_FAMILY_ID = "to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
				+ "].from[" + DomainConstants.TYPE_PART_FAMILY + "].id";
		final String SELECT_ATTR_REL_REF_DOC = "attribute[" + RELATIONSHIP_REFERENCE_DOCUMENT + "]";
		final String SELECT_ATTR_PG_INHERIT_TYPE = "attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]";

		boolean isContextPushed = false;

		try {
			MapList mlObjectList = (MapList) programMap.get("objectList");

			String strIsPGExport = (String) programMap.get("isPGExport");
			Map paramMap = (Map<?, ?>) programMap.get("paramList");
			String strselectedTable = (String) paramMap.get("selectedTable");
			// Added for 22x Upgrade - ALM#48431 - Starts
			if (UIUtil.isNullOrEmpty(strselectedTable)) {
				strselectedTable = (String) paramMap.get("table");
			}

			String strIsStructureCompare = (String) paramMap.get("IsStructureCompare");

			strselectedTable = UITableCustom.getSystemTableName(context, strselectedTable);
			String strMasterPartId = DomainConstants.EMPTY_STRING;
			String strMasterPartName = DomainConstants.EMPTY_STRING;
			String strMasterPartTitle = DomainConstants.EMPTY_STRING;
			boolean bMasterExists = false;
			// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - ends
			String strselectedFilter = (String) paramMap.get("pgVPDCPNCharacteristicDerivedFilter");
			String strParentId = (String) paramMap.get("parentOID");
			// Added for 22x Upgrade - ALM#48431 - Starts
			if (UIUtil.isNullOrEmpty(strParentId)) {
				strParentId = (String) paramMap.get("objectId");
			}
			// Added for 22x Upgrade - ALM#48431 - Ends

			String strMasterPDF = (String) paramMap.get("MasterPDF");

			// DSM(DS) 2018x.5 -ALM 33092 : Where clause for getRelated Characteristic
			// -Start
			String strCharwhere = "attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]=="
					+ PGPerfCharsConstants.FILTER_LOCAL;
			// DSM(DS) 2019x.5 -ALM 33092 : Where clause for getRelated Characteristic -End

			MapList partFamilyList = new MapList();

			String strSpecInheritanceType = DomainConstants.EMPTY_STRING;
			String strParentName = DomainConstants.EMPTY_STRING;
			String strParentTitle = DomainConstants.EMPTY_STRING;
			String strParentType = DomainConstants.EMPTY_STRING;
			// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
			// 13268 - ENDS
			MapList lowerPartFamilyMapList = null;
			StringList upperPartFamilyList = null;
			StringList lowerPartFamilyListLower = null;
			String strParentRefType = DomainConstants.EMPTY_STRING;
			StringList partFamilyisList = new StringList();
			DomainObject partFamilyObj = null;
			String objReadAccess = "";
			// DSM(DS) 2015x.4 ALM 15191 - misleading Master Part path and Title on Master
			// Specification tab of FPP - starts
			String strRowMasterId = DomainConstants.EMPTY_STRING;
			String strRowMasterName = DomainConstants.EMPTY_STRING;
			String strRowMasterTitle = DomainConstants.EMPTY_STRING;
			String strRowType = DomainConstants.EMPTY_STRING;
			String strRowId = DomainConstants.EMPTY_STRING;
			String strRowTitle = DomainConstants.EMPTY_STRING;
			DomainObject masterObj = null;
			String derivedPath = DomainConstants.EMPTY_STRING;
			if (UIUtil.isNotNullAndNotEmpty(strParentId)) {
				DomainObject parentObj = DomainObject.newInstance(context, strParentId);
				StringList slParentObj = new StringList(2);
				slParentObj.add(SELECT_ATTR_REF_TYPE);
				slParentObj.add(SELECT_TO_CLASS_ITEM_FROM_PART_FAMILY_ID);
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - STARTS
				slParentObj.add(DomainConstants.SELECT_NAME);
				slParentObj.add("attribute[Title]");
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - ENDS
				// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Starts
				slParentObj.add(DomainConstants.SELECT_TYPE);
				slParentObj.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id");
				slParentObj.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.name");
				slParentObj.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.attribute[Title]");
				// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - ends

				Map<?, ?> mapParentObj = parentObj.getInfo(context, slParentObj);
				strParentRefType = (String) mapParentObj.get(SELECT_ATTR_REF_TYPE);

				// DSM (DS) 2015x.1.2 below code is to check whether the spec belongs to the
				// same part family heirarchy -starts
				String partFamilyId = (String) mapParentObj.get(SELECT_TO_CLASS_ITEM_FROM_PART_FAMILY_ID);
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - STARTS
				strParentName = (String) mapParentObj.get(DomainConstants.SELECT_NAME);
				strParentTitle = (String) mapParentObj.get("attribute[Title]");
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - ENDS
				// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Starts
				strParentType = (String) mapParentObj.get(DomainConstants.SELECT_TYPE);
				strMasterPartId = (String) mapParentObj.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
						+ "].frommid[" + PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id");
				strMasterPartName = (String) mapParentObj.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
						+ "].frommid[" + PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.name");
				strMasterPartTitle = (String) mapParentObj.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
						+ "].frommid[" + PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.attribute[Title]");
				if (UIUtil.isNotNullAndNotEmpty(strMasterPartId) && UIUtil.isNotNullAndNotEmpty(strMasterPartName)) {
					bMasterExists = true;
				}
				// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - ends
				StringList objectSelects = new StringList();
				objectSelects.add(DomainConstants.SELECT_NAME);
				objectSelects.add(DomainConstants.SELECT_ID);

				if (UIUtil.isNotNullAndNotEmpty(partFamilyId)
						&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE.equals(strselectedTable)
						&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
								.equals(strselectedTable))
				{
					partFamilyisList = getPartFamilyListFromHeirarchy(context, partFamilyId);
				}

			}
			if (UIUtil.isNullOrEmpty(strselectedTable)) {
				strselectedTable = (String) paramMap.get("table");
			}

			if (mlObjectList != null && !mlObjectList.isEmpty()) {
				Iterator objListItr = mlObjectList.iterator();

				StringBuilder tempValBuffer = new StringBuilder();
				DomainObject charObj = null;
				String strSpecObjId = DomainConstants.EMPTY_STRING;
				String strRefType = DomainConstants.EMPTY_STRING;
				String strConnectedPFId = DomainConstants.EMPTY_STRING;
				StringList connectedMasterNameList = new StringList();
				StringList connectedMasterTitleList = new StringList();
				StringList connectedMasterIdList = new StringList();

				StringList selectStmts = new StringList(4);
				selectStmts.add(DomainConstants.SELECT_NAME);
				selectStmts.add(DomainConstants.SELECT_ID);
				selectStmts.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				selectStmts.add(SELECT_ATTR_REF_TYPE);
				// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
				// 13268 - STARTS
				selectStmts.add("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from[Part Family].id");
				StringList connectedMasterPFList = new StringList();
				// Modified by DSM(DS) for 2015x.4 ALM 13251 STARTS
				Map<String, StringList> MasterPFMap = new HashMap<String, StringList>();

				StringList selectRelStmts = new StringList(2);
				selectRelStmts.add(SELECT_ATTR_REL_REF_DOC);
				selectRelStmts.add(SELECT_ATTR_PG_INHERIT_TYPE);

				String strInheritanceType = null;
				MapList connectedPartList = null;
				String strTitle = "";
				Iterator mapItr = null;
				Map map = null;
				DomainObject connectedMasterObj = null;
				StringList masterTitleList = new StringList();
				StringList masterNamelist = new StringList();

				if (UIUtil.isNotNullAndNotEmpty(strselectedFilter) && strselectedFilter.equals("Local")) {
					while (objListItr.hasNext()) {
						Map objMap = (Map) objListItr.next();
						slReturnValueList.add(DomainConstants.EMPTY_STRING);
					}
					return slReturnValueList;
				}

				ContextUtil.pushContext(context, PGPerfCharsConstants.PERSON_AGENT, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
				isContextPushed = true;

				while (objListItr.hasNext()) {
					Map objMap = (Map) objListItr.next();
					if (objMap != null && !objMap.isEmpty()) {
						// DSM (DS) 2015x.1.2 Added code to check the read access of the objects : Start
						objReadAccess = (String) objMap.get("objReadAccess");
						if ((UIUtil.isNotNullAndNotEmpty(objReadAccess)
								&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess))
								&& (PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE
												.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_ENC_DOCUMENTSUMMARY.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
												.equals(strselectedTable)))
						{

							String strCharId = (String) objMap.get("id");
							String strRelationship = (String) objMap.get("relationship");

							strRowMasterId = (String) objMap.get("strMasterID");
							strRowMasterName = (String) objMap.get("strMasterName");
							strRowMasterTitle = (String) objMap.get("strMasterTitle");
							strRowTitle = (String) objMap.get("strRowTitle");
							strRowType = (String) objMap.get("strRowType");
							derivedPath = (String) objMap.get("derivedPath");
							if (UIUtil.isNotNullAndNotEmpty(strRowMasterId)
									&& UIUtil.isNotNullAndNotEmpty(strParentType)
									&& PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
									&& ("pgVPDAPPDocumentSummary".equals(strselectedTable)
											|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
													.equals(strselectedTable))) {
								// DSM (DS) 2015x.4.1 March Downtime ALM - 16403 - Performance of FPC related
								// specs screen is to slow - Ends
								masterObj = DomainObject.newInstance(context, strRowMasterId);
								strConnectedPFId = masterObj.getInfo(context,
										"to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from["
												+ DomainConstants.TYPE_PART_FAMILY + "].id");
								
								partFamilyisList = getPartFamilyListFromHeirarchy(context, strConnectedPFId);
							}
							// DSM(DS) 2015x.4 ALM 15191 - misleading Master Part path and Title on Master
							// Specification tab of FPP - ends

							if (UIUtil.isNotNullAndNotEmpty(strRelationship)
									&& strRelationship.equals(PGPerfCharsConstants.REL_PG_INHERITED_CAD_SPEC)) {
								strRelationship = strRelationship + ","
										+ PGPerfCharsConstants.RELATIONSHIP_PART_SPECIFICATION;
							}
							charObj = DomainObject.newInstance(context, strCharId);

							// DSM (DS) 2018x.3 - ALM 31989 - Showing empty value in Master Title column for
							// root node in Detailed Compare Report - START
							if (UIUtil.isNotNullAndNotEmpty(strIsStructureCompare)
									&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equalsIgnoreCase(strIsStructureCompare)
									&& !charObj.isKindOf(context, CPNCommonConstants.TYPE_CHARACTERISTIC)) {
								slReturnValueList.add(DomainConstants.EMPTY_STRING);
								continue;
							}
							// DSM (DS) 2018x.3 - ALM 31989 - Showing empty value in Master Title column for
							// root node in Detailed Compare Report - END

							tempValBuffer = new StringBuilder();
							connectedMasterNameList.removeAll(connectedMasterNameList);
							connectedMasterTitleList.removeAll(connectedMasterTitleList);
							connectedMasterIdList.removeAll(connectedMasterIdList);
							// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
							// 13268 - STARTS
							// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Starts
							strSpecInheritanceType = (String) objMap
									.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
							if (UIUtil.isNullOrEmpty(strSpecInheritanceType)) {
								strSpecInheritanceType = (String) objMap
										.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
							}

							// DSM(DS) 2015x.4 ALM 15191 - misleading Master Part path and Title on Master
							// Specification tab of FPP - starts
							if (UIUtil.isNullOrEmpty(strSpecInheritanceType)) {
								strSpecInheritanceType = (String) objMap.get("strInheritanceType");
							}
							// DSM (DS) 2015x.4.1 March Downtime ALM - 16403 - Performance of FPC related
							// specs screen is to slow - Starts
							if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
									&& (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
											|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
													.equals(strselectedTable))
									&& (PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT.equals(strRowType)
											|| PGPerfCharsConstants.TYPE_PG_INNERPACK.equals(strRowType)
											|| PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT.equals(strRowType))) {
								connectedMasterTitleList.add(strRowMasterTitle);
								slReturnValueList.add(strRowMasterTitle);
								return slReturnValueList;
							}
							if (PGPerfCharsConstants.STR_LOCAL.equalsIgnoreCase(strSpecInheritanceType)
									&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE
											.equals(strselectedTable)
									&& !"pgVPDPerformanceCharacteristicMasterPathTable".equals(strselectedTable)) {
								if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
										&& (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
												|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
														.equals(strselectedTable))
										&& UIUtil.isNotNullAndNotEmpty(strRowMasterName)
										&& UIUtil.isNotNullAndNotEmpty(strRowMasterId)) {
									// DSM (DS) 2015x.4.1 March Downtime ALM - 16403 - Performance of FPC related
									// specs screen is to slow - Ends
									connectedMasterIdList.add(strRowMasterId);
									connectedMasterNameList.add(strRowMasterName);
									connectedMasterTitleList.add(strRowMasterTitle);
								}
								else if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(strParentType)
										&& (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
												|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
														.equals(strselectedTable))
										&& (PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT.equals(strRowType)
												|| PGPerfCharsConstants.TYPE_PG_INNERPACK.equals(strRowType)
												|| PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT.equals(strRowType))) {
									// DSM (DS) 2015x.4.1 March Downtime ALM - 16403 - Performance of FPC related
									// specs screen is to slow - Ends
									if (UIUtil.isNotNullAndNotEmpty(strRowTitle)) {
										slReturnValueList.add(strRowTitle);
										continue;
									}

								}
								else if ((PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY.equals(strselectedTable)
										|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
												.equals(strselectedTable))
										&& bMasterExists) {
									connectedMasterIdList.add(strMasterPartId);
									connectedMasterNameList.add(strMasterPartName);
									connectedMasterTitleList.add(strMasterPartTitle);
								} else {
									connectedMasterIdList.add(strParentId);
									connectedMasterNameList.add(strParentName);
									connectedMasterTitleList.add(strParentTitle);
								}
								// Modified for DSM(DS) 2015x.4 ALM 13484 added ttile and name - Ends

							} else {
								connectedPartList = charObj.getRelatedObjects(context, strRelationship, // relationship
																										// pattern
										DomainConstants.QUERY_WILDCARD, // object pattern
										selectStmts, // object selects
										selectRelStmts, // relationship selects
										true, // to direction
										false, // from direction
										(short) 1, // recursion level
										"", // object where clause
										strCharwhere, // relationship where clause
										(short) 0);

								// DSM(DS) 2018x.5 -ALM 33092 : Where clause for getRelated Characteristic -End

								connectedPartList.sort("name", "ascending", "String");

								if (connectedPartList != null && connectedPartList.size() > 0) {
									mapItr = connectedPartList.iterator();
									// Map map = null;
									connectedMasterNameList = new StringList();
									connectedMasterTitleList = new StringList();
									connectedMasterIdList = new StringList();
									// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
									// 13268 - STARTS
									connectedMasterPFList = new StringList();
									// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
									// 13268 - ENDS
									// DomainObject connectedMasterObj = null;
									while (mapItr.hasNext()) {
										map = (Map) mapItr.next();
										strRefType = (String) map
												.get("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
										strInheritanceType = (String) map.get(SELECT_ATTR_PG_INHERIT_TYPE);

										// DSM (DS) 2015x.1.2 - JRH - Master Part Title is populating for Local
										// Characteristics -start
										if (UIUtil.isNotNullAndNotEmpty(strRefType) && "R".equals(strRefType)) {
											String strConnectedRefId = (String) map.get("id");
											DomainObject conRefObj = DomainObject.newInstance(context,
													strConnectedRefId);
											// 2015x.1.2 - Start
											if (UIUtil.isNotNullAndNotEmpty(strInheritanceType)
													&& strInheritanceType.equals(PGPerfCharsConstants.STR_REFERENCED)) {
												strTitle = (String) conRefObj.getInfo(context,
														"to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
																+ "].frommid["
																+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE
																+ "].torel.to." + SELECT_ATTR_TITLE);
											} else {
												strTitle = DomainConstants.EMPTY_STRING;
											}

										}
										// DSM (DS) 2015x.2 - ALM 9997 "Master Part Title" column is populated with
										// local part title. - START
										else if (UIUtil.isNullOrEmpty(strRefType) || "U".equals(strRefType)) {
											strTitle = DomainConstants.EMPTY_STRING;
										}

										else if (("M".equals(strRefType)
												&& (PGPerfCharsConstants.STR_PG_VPD_APP_DOCUMENTSUMMARY
														.equals(strselectedTable)
														|| PGPerfCharsConstants.STR_PG_DSO_MASTERPART_DETAILS_TABLE
																.equals(strselectedTable)))
												|| PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE
														.equals(strselectedTable)
												|| (((("M".equals(strParentRefType) && "M".equals(strRefType))
														|| ("R".equals(strParentRefType) && "R".equals(strRefType)))
														&& PGPerfCharsConstants.STR_ENC_DOCUMENTSUMMARY
																.equals(strselectedTable)))
												|| PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
														.equals(strselectedTable))
										{
											// DSM (DS) 2015x.4.1 March Downtime ALM - 16403 - Performance of FPC
											// related specs screen is to slow - ends
											strTitle = (String) map.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
										}
										if (UIUtil.isNotNullAndNotEmpty(strInheritanceType)
												&& strInheritanceType.equals(PGPerfCharsConstants.STR_LOCAL)) {
											connectedMasterTitleList.add(strTitle);
											connectedMasterNameList.add((String) map.get(DomainConstants.SELECT_NAME));
											connectedMasterIdList.add((String) map.get(DomainConstants.SELECT_ID));
											// DSM(DS) 2015x.4 Modified for ALM 13423-[PC] PC Table Cannot be Saved
											// Successfully- starts
											// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
											// 13268 - STARTS
											Object pfObjecIds = map
													.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM
															+ "].from[Part Family].id");
											// Modified by DSM(DS) for 2015x.4 ALM 13251 STARTS
											connectedMasterPFList = new StringList();
											// Modified by DSM(DS) for 2015x.4 ALM 13251 ENDS
											if (null != pfObjecIds) {
												if (pfObjecIds instanceof String) {
													connectedMasterPFList.add((String) pfObjecIds);
												} else {
													connectedMasterPFList.addAll((StringList) pfObjecIds);
												}
											}
											// Modified by DSM(DS) for 2015x.4 ALM 13251 STARTS
											MasterPFMap.put((String) map.get(DomainConstants.SELECT_ID),
													connectedMasterPFList);
										}
									}

									if (!PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE
											.equals(strselectedTable)
											&& !PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_MASTER_PATH_TABLE
													.equals(strselectedTable))
									// DSM (DS) 2018x.3 Modified for Master-Title view Table - starts
									{
										if (connectedMasterIdList.size() > 1) {
											for (int i = 0, nSize = connectedMasterIdList.size(); i < nSize; i++) {
												StringList MasterPFList = MasterPFMap
														.get((String) connectedMasterIdList.get(i));
												boolean isSameHier = false;
												if (!MasterPFList.isEmpty()) {
													for (int l = 0; l < MasterPFList.size(); l++) {
														if (partFamilyisList.contains((String) MasterPFList.get(l))) {
															isSameHier = true;
															break;
														}
													}
												}

												if (isSameHier) {
													masterTitleList.add((String) connectedMasterTitleList.get(i));
													masterNamelist.add((String) connectedMasterNameList.get(i));
												}

											}
											connectedMasterTitleList = masterTitleList;
											connectedMasterNameList = masterNamelist;

										}
									}

									if (connectedMasterNameList.isEmpty()) {
										map = (Map) connectedPartList.get(0);
										connectedMasterTitleList
												.add((String) map.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
										connectedMasterNameList.add((String) map.get(DomainConstants.SELECT_NAME));
									}
								}

							}
							// Modified by DSM(DS) for Performance Improvement on Specs & Docs page ALM
							// 13268 - ENDS
							if (connectedMasterNameList.isEmpty())
								slReturnValueList.add(DomainConstants.EMPTY_STRING);
							else {
								String strTempVal = "";
								String strDelimiter = "";
								for (int i = 0, nSize = connectedMasterNameList.size(); i < nSize; i++) {
									tempValBuffer.append(strDelimiter);
									tempValBuffer.append((String) connectedMasterTitleList.get(i));
									strDelimiter = " | ";
								}
								strTempVal = tempValBuffer.toString();
								slReturnValueList.add(strTempVal);
							}

						} else { // DSM (DS) 2015x.1.2 Added code to check the read access of the objects : Start
							if ((UIUtil.isNotNullAndNotEmpty(objReadAccess)
									&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess))) {

								String strDerivedPath = (String) objMap.get("derivedPath");
								// Null Check for Derived Path -START
								if (UIUtil.isNotNullAndNotEmpty(strDerivedPath)) {
									slReturnValueList.add(strDerivedPath);
								} else {
									slReturnValueList.add(DomainConstants.EMPTY_STRING);
								}
								// DSM (DS) 2015x.1.2 Null Check for Derived Path -END
							}
							// DSM (DS) 2015x.1.2 - Added code to check the read access of the objects : End
							else {
								slReturnValueList.add(DomainConstants.EMPTY_STRING);
							}
						}
					}
				}
			}
		} catch (Exception Ex) {
			
			throw Ex;

		}
		// DSM (DS) 2015x.5 ALM 16966 - Suppliers cant see right path of performance
		// characteristic - STARTS
		finally {
			if (isContextPushed)
				ContextUtil.popContext(context);
		}
		// DSM (DS) 2015x.5 ALM 16966 - Suppliers cant see right path of performance
		// characteristic - ENDS
		return slReturnValueList;
	}
	
}