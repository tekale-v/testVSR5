package com.pdfview.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.PerformanceCharacteristic;
import com.pdfview.impl.FPP.PerformanceCharacteristics;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.AccessConstants;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetPerformanceCharacteristics implements CPNCommonConstants{
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetPerformanceCharacteristics(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public PerformanceCharacteristics getComponent() throws MatrixException {
		return getPerformanceChracteristicDSO(_context, _OID);

	}

	/**
	 *  Method to retrieve the values of PerformanceCharacteristics table
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws MatrixException
	 */
	private PerformanceCharacteristics getPerformanceChracteristicDSO(Context context, String strObjectId)
			throws MatrixException {
		PerformanceCharacteristics perCharacteristic = new PerformanceCharacteristics();
		List<PerformanceCharacteristic> lsPerChara = perCharacteristic.getPerformanceCharacteristic();
		long startTime = new Date().getTime();
		Map<String, String> perCharaList = null;
		try {
			ContextUtil.pushContext(context);
			Map mpBOMSubsSST = new HashMap();
			Map mpPath = null;
			Map mParList = null;
			Map mObjPath = null;
			Map mpTMName = null;
			Map<String, String> mpTMLogic = null;
			MapList mlPerChara = new MapList();
			MapList objectListTM = null;
			Map paramList = null;
			Map mpTM = null;
			Map mMasData = new HashMap();
			String strMasterId = DomainConstants.EMPTY_STRING;
			MapList mlPerCharaofMaster = new MapList();
			MapList mlObjListPath = null;
			Map<String, String> perCharaListMaster = null;
			StringList slId = new StringList();
			String strPathForPC = DomainConstants.EMPTY_STRING;
			
			String strId = DomainConstants.EMPTY_STRING;
			String sRel = DomainConstants.EMPTY_STRING;
			String strOS = DomainConstants.EMPTY_STRING;
			String strTMLogic = DomainConstants.EMPTY_STRING;
			String strOtherTMNumber = DomainConstants.EMPTY_STRING;
			String strTsetMethodSpe = DomainConstants.EMPTY_STRING;
			String strOrigin = DomainConstants.EMPTY_STRING;
			String strReferenceDoc = DomainConstants.EMPTY_STRING;
			String strSampling = DomainConstants.EMPTY_STRING;
			String strRetestingUOM = DomainConstants.EMPTY_STRING;
			String strRetesting = DomainConstants.EMPTY_STRING;
			String strSSubGroup = DomainConstants.EMPTY_STRING;
			String strPlantTestingLvl = DomainConstants.EMPTY_STRING;
			String strLowerSpecificationLimit = DomainConstants.EMPTY_STRING;
			String strLowerRoutineReleaseLimit = DomainConstants.EMPTY_STRING;
			String strLowerTarget = DomainConstants.EMPTY_STRING;
			String strTarget = DomainConstants.EMPTY_STRING;
			String strUpperTarget = DomainConstants.EMPTY_STRING;
			String strUpperRoutineReleaseLimit = DomainConstants.EMPTY_STRING;
			String strUpperSpecificationLimit = DomainConstants.EMPTY_STRING;
			String strContext = DomainConstants.EMPTY_STRING;
			String strReportToNearest = DomainConstants.EMPTY_STRING;
			String strUnitofMeasureMasterList = DomainConstants.EMPTY_STRING;
			String strReportType = DomainConstants.EMPTY_STRING;
			String strRoutineReleaseCriteria = DomainConstants.EMPTY_STRING;
			String strReleaseCriteria = DomainConstants.EMPTY_STRING;
			String strBasis = DomainConstants.EMPTY_STRING;
			String strActionRequiredList = DomainConstants.EMPTY_STRING;
			String strCharstopgPLICriticalityFactor = DomainConstants.EMPTY_STRING;
			String strApplication = DomainConstants.EMPTY_STRING;
			String strPCharstopgPLITestGroup = DomainConstants.EMPTY_STRING;
			String strTM = DomainConstants.EMPTY_STRING;
			String strChg = DomainConstants.EMPTY_STRING;
			String strChara = DomainConstants.EMPTY_STRING;
			String strCharaSpe = DomainConstants.EMPTY_STRING;
			String strMasterTitle = DomainConstants.EMPTY_STRING;
			String sHasReadAcess = DomainConstants.EMPTY_STRING;
			String[] argsPath = null;
			Vector vTM = null;
			Vector vPath  = null;
			String[] argsTM  = null;
			Vector vReferenceDoc =  null;
			boolean hasReadAccess = false;
			DomainObject domPer = null;
			sHasReadAcess = pgV3Constants.CONST_TRUE;
			if (null != strObjectId && !"".equals(strObjectId)) {
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
				mpBOMSubsSST.put("objectId", strObjectId);
				mpBOMSubsSST.put("selectedTable", "pgVPDPerformanceCharacteristicTable");
				mpBOMSubsSST.put("pgVPDCPNCharacteristicDerivedFilter", "All");
				mpBOMSubsSST.put("Mode", "PDF");
				String[] args = JPO.packArgs(mpBOMSubsSST);
				mlPerChara = getMicroChar(context, args);
				StringList relSelects = new StringList(1);
				relSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id");
				MapList mlMasterList = doObj.getRelatedObjects(context, // Context
						pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM, // relPattern
						pgV3Constants.TYPE_PARTFAMILY, // typePattern
						null, // objectSelects
						relSelects, // relationshipSelects
						true, // getTo - Get Parent Data
						false, // getFrom - Get Child Data
						(short) 1, // recurseToLevel
						DomainConstants.EMPTY_STRING, // objectWhere
						DomainConstants.EMPTY_STRING); // relationshipWhere
				if (null != mlMasterList && !mlMasterList.isEmpty()) {
					for (Iterator i = mlMasterList.iterator(); i.hasNext();) {
						mMasData = (Map) i.next();
						
						strMasterId = (String) mMasData
								.get("frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id");
					}
					mpBOMSubsSST.put("objectId", strMasterId);
					if (!(null == strMasterId || UIUtil.isNullOrEmpty(strMasterId)
							|| strMasterId.equals(DomainConstants.EMPTY_STRING)))
					{
						String[] args1 = JPO.packArgs(mpBOMSubsSST);
						mlPerCharaofMaster = getMicroChar(context, args1);
						for (Iterator i1 = mlPerCharaofMaster.iterator(); i1.hasNext();) {
							perCharaListMaster = (Map) i1.next();
							slId.add((String) perCharaListMaster.get("id"));
						}
					}
				}
				int nPerCharaSize = mlPerChara.size();
				StringList selectStmts = new StringList(29);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGTMLOGIC);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODNUMBER);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODSPECIFICS);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAMPLING);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGSUBGROUP);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERROUTINRRELEASELIMIT);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERTARGET);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGTARGET);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERTARGET);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERROUTINERELEASELIMIT);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGREPORTTONEAREST);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGROUTINERELEASECRITERIA);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGRELEASECRITERIA);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASIS);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGAPPLICATION);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGACTIONREQUIRED);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGUNITOFMEASURE);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODORIGIN);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGTESTGROUP);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGRETESTINGUOM);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTINGRETESTING);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTING);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGREPORTTYPE);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGCRITICALITYFACTOR);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTICSPECIFICS);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
				selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTIC);
				if (nPerCharaSize > 0) {
					for (Iterator iterator = mlPerChara.iterator(); iterator.hasNext();) {
						PerformanceCharacteristic performanceCharacteristic=new PerformanceCharacteristic();
						perCharaList = (Map) iterator.next();
						strId = (String) perCharaList.get("id");
						sRel = (String) perCharaList.get("relationship");
						domPer = DomainObject.newInstance(context, strId);
						mpTMLogic = domPer.getInfo(context, selectStmts);
						hasReadAccess = domPer.checkAccess(context, (short) AccessConstants.cRead);
						if (!hasReadAccess)
							sHasReadAcess = PDFConstant.CONST_FALSE;
						mpPath = new HashMap();
						mlObjListPath = new MapList();
						mObjPath = new HashMap();
						mObjPath.put("id", strId);
						mObjPath.put("objReadAccess", sHasReadAcess);
						mObjPath.put("relationship", sRel);
						mlObjListPath.add(mObjPath);
						mParList = new HashMap();
						mParList.put("selectedTable", "pgVPDPerformanceCharacteristicTable");
						mParList.put("parentOID", strObjectId);
						mParList.put("MasterPDF", "pdf");
						mParList.put("pgVPDCPNCharacteristicDerivedFilter", "All");
						mpPath.put("objectList", mlObjListPath);
						mpPath.put("paramList", mParList);
						argsPath = JPO.packArgs(mpPath);
						vPath = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxCPNCharacteristicList",
								"getDerivedPathForRow", argsPath);
						if (!vPath.isEmpty()) {
							strPathForPC = (String) vPath.get(0);
						}

						strChg = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
						strChara = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTIC);
						strCharaSpe = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTICSPECIFICS);
						performanceCharacteristic.setChg(StringHelper.validateString1(strChg));
						performanceCharacteristic.setCharacteristics(StringHelper.validateString1(strChara));
						performanceCharacteristic.setCharacteristicSpecifics(StringHelper.validateString1(strCharaSpe));
						performanceCharacteristic.setPath(StringHelper.getHrefRemovedData(StringHelper.validateString1(strPathForPC)));
						mpTMName = new HashMap();
						mpTMName.put("id", strId);
						mpTMName.put("objReadAccess", "TRUE");
						objectListTM = new MapList();
						objectListTM.add(mpTMName);
						paramList = new HashMap();
						paramList.put("reportFormat", "PDF");
						paramList.put("parentOID", strObjectId);
						mpTM = new HashMap();
						mpTM.put("objectList", objectListTM);
						mpTM.put("paramList", paramList);
						argsTM = JPO.packArgs(mpTM);
						strOS = doObj.getAttributeValue(context, pgV3Constants.ATTRIBUTE_PGORIGINATINGSOURCE);
						if (pgV3Constants.DSM_ORIGIN.equalsIgnoreCase(strOS)) {
							vTM = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgDSOUtil", "pgGetTestMethods",
									argsTM);
						} else {
							vTM = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgIPMProductData",
									"pgGetTestMethods", argsTM);
						}
						if (!vTM.isEmpty()) {
							strTM = (String) vTM.get(0);
							if (UIUtil.isNullOrEmpty(strTM)) {
								strTM = DomainConstants.EMPTY_STRING;
							}
							strTM = strTM.replaceAll("\n", "|\n");
						}
						strTMLogic = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGTMLOGIC);
						strOtherTMNumber = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODNUMBER);
						if (UIUtil.isNullOrEmpty(strOtherTMNumber)) {
							strOtherTMNumber = DomainConstants.EMPTY_STRING;
						}
						strOtherTMNumber = StringHelper.filterLessAndGreaterThanSign(strOtherTMNumber);
						strTsetMethodSpe = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODSPECIFICS);
						if (UIUtil.isNullOrEmpty(strTsetMethodSpe)) {
							strTsetMethodSpe = DomainConstants.EMPTY_STRING;
						}
						strTsetMethodSpe = StringHelper.filterLessAndGreaterThanSign(strTsetMethodSpe);
						strOrigin = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODORIGIN);
						vReferenceDoc = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgIPMProductData",
								"pgGetReferenceDocGCAS", argsTM);
						strReferenceDoc = (String) vReferenceDoc.get(0);
						performanceCharacteristic.setTestMethod(StringHelper.validateString1(strTM));
						performanceCharacteristic.setTestMethodLogic( StringHelper.validateString1(strTMLogic));
						performanceCharacteristic.setOrigin(StringHelper.validateString1(strOrigin));
						performanceCharacteristic.setOtherTestMethodNumber(StringHelper.validateString1(strOtherTMNumber));
						performanceCharacteristic.setTestMethodSpecifics(StringHelper.validateString1(strTsetMethodSpe));
						performanceCharacteristic.setReferenceDocument(StringHelper.validateString1(strReferenceDoc));
						strSampling = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAMPLING);
						if (UIUtil.isNullOrEmpty(strSampling)) {
							strSampling = DomainConstants.EMPTY_STRING;
						}
						strSampling = StringHelper.filterLessAndGreaterThanSign(strSampling);
						strRetestingUOM = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGRETESTINGUOM);
						strRetesting = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTINGRETESTING);
						strSSubGroup = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGSUBGROUP);
						if (UIUtil.isNullOrEmpty(strSSubGroup)) {
							strSSubGroup = DomainConstants.EMPTY_STRING;
						}
						strSSubGroup = StringHelper.filterLessAndGreaterThanSign(strSSubGroup);
						strPlantTestingLvl = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTING);
						performanceCharacteristic.setSampling(StringHelper.validateString1(strSampling) );
						performanceCharacteristic.setSubgroup(StringHelper.validateString1(strSSubGroup) );
						performanceCharacteristic.setPlantTestingLevel(StringHelper.validateString1(strPlantTestingLvl));
						performanceCharacteristic.setPlantTestingRetesting(StringHelper.validateString1(strRetesting));
						performanceCharacteristic.setUnitOfMeasureResting(StringHelper.validateString1(strRetestingUOM));
						strLowerSpecificationLimit = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT);
						strLowerRoutineReleaseLimit = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERROUTINRRELEASELIMIT);
						strLowerTarget = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERTARGET);
						strTarget = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGTARGET);
						strUpperTarget = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERTARGET);
						strUpperRoutineReleaseLimit = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERROUTINERELEASELIMIT);
						strUpperSpecificationLimit = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT);
						strContext = context.getUser();
						performanceCharacteristic.setLowerSpecLimit(StringHelper.validateString1(strLowerSpecificationLimit));
						performanceCharacteristic.setLowerRoutineReleaseLimit(StringHelper.validateString1(strLowerRoutineReleaseLimit));
						performanceCharacteristic.setLowerTarget(StringHelper.validateString1(strLowerTarget));
						performanceCharacteristic.setTarget(StringHelper.validateString1(strUpperTarget));
						performanceCharacteristic.setUpperTarget(StringHelper.validateString1(strUpperTarget));
						performanceCharacteristic.setUpperRoutineReleaseLimit(StringHelper.validateString1(strUpperRoutineReleaseLimit));
						performanceCharacteristic.setUpperSpecLimit(StringHelper.validateString1(strUpperSpecificationLimit));
						strReportToNearest = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGREPORTTONEAREST);
						strUnitofMeasureMasterList = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGUNITOFMEASURE);
						strReportType = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGREPORTTYPE);
						performanceCharacteristic.setUOM(StringHelper.validateString1(strUnitofMeasureMasterList));

						performanceCharacteristic.setReportToNearest(StringHelper.validateString1(strReportToNearest));

						performanceCharacteristic.setReportType(StringHelper.validateString1(strReportType));
						strRoutineReleaseCriteria = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGROUTINERELEASECRITERIA);
						if (UIUtil.isNullOrEmpty(strRoutineReleaseCriteria)) {
							strRoutineReleaseCriteria = DomainConstants.EMPTY_STRING;
						}
						strRoutineReleaseCriteria = StringHelper.filterLessAndGreaterThanSign(strRoutineReleaseCriteria);
						strReleaseCriteria = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGRELEASECRITERIA);
						if (UIUtil.isNullOrEmpty(strReleaseCriteria)) {
							strReleaseCriteria = DomainConstants.EMPTY_STRING;
						}
						strReleaseCriteria =StringHelper.filterLessAndGreaterThanSign(strReleaseCriteria);
						performanceCharacteristic.setReleaseCriteria(StringHelper.validateString1(strReleaseCriteria));
						strBasis = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASIS);
						if (UIUtil.isNullOrEmpty(strBasis)) {
							strBasis = DomainConstants.EMPTY_STRING;
						}
						strBasis = StringHelper.filterLessAndGreaterThanSign(strBasis);
						strActionRequiredList = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGACTIONREQUIRED);
						strCharstopgPLICriticalityFactor = (String) mpTMLogic
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGCRITICALITYFACTOR);
						performanceCharacteristic.setActionRequired(StringHelper.validateString1(strActionRequiredList));
						performanceCharacteristic.setCriticalityFactor(StringHelper.validateString1(strCharstopgPLICriticalityFactor));
						performanceCharacteristic.setBasis(StringHelper.validateString1(strBasis));
						strApplication = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGAPPLICATION);
						if (UIUtil.isNullOrEmpty(strApplication)) {
							strApplication = DomainConstants.EMPTY_STRING;
						}
						strApplication = StringHelper.filterLessAndGreaterThanSign(strApplication);
						strPCharstopgPLITestGroup = (String) mpTMLogic.get(pgV3Constants.SELECT_ATTRIBUTE_PGTESTGROUP);
						performanceCharacteristic.setTestGroup(StringHelper.validateString1(strPCharstopgPLITestGroup));
						performanceCharacteristic.setApplication(StringHelper.validateString1(strApplication));
						lsPerChara.add(performanceCharacteristic);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ContextUtil.popContext(context);
		}
		long endTime = new Date().getTime();
		System.out.println(
				"Total Time has taken by the getPerformanceChracteristicDSO Method is-->" + (endTime - startTime));
		return perCharacteristic;

	}

	/**
	 * - Master Characteristics Enhancements(Display Master Characteristics on the
	 * Performance Characteristic Table) :
	 * 
	 * @param context
	 * @param args
	 * @return MapList with Product Data and it's EBOM and their Master
	 *         characteristics
	 * @throws Exception
	 */
	public MapList getMicroChar(Context context, String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}
		MapList objList = null;
		HashMap paramMap = (HashMap) JPO.unpackArgs(args);
		String objectId = (String) paramMap.get("objectId");
		DomainObject doObj = DomainObject.newInstance(context, objectId);
		boolean hasReadAccess = doObj.checkAccess(context, (short) AccessConstants.cRead);
		if (hasReadAccess) {
			String partType = DomainConstants.EMPTY_STRING;
			StringList objectSelects = new StringList(6);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainObject.SELECT_NAME);
			objectSelects.add(DomainObject.SELECT_TYPE);
			objectSelects.add(DomainObject.SELECT_POLICY);
			objectSelects.add(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
			objectSelects.add(pgV3Constants.ATTR_REFERENCE_TYPE);
			StringList relSelects = new StringList(2);
			relSelects.add(DomainObject.SELECT_RELATIONSHIP_ID);
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
			String addNewRow = (String) paramMap.get("AddRow");
			String strSwitchMode = (String) paramMap.get("SwitchMode");
			String strSelectedTable = (String) paramMap.get("selectedTable");
			strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~') + 1,
					strSelectedTable.length());
			String strTypeSym = EnoviaResourceBundle.getProperty(context,
					"emxCPN.Characteristic.table." + strSelectedTable);
			String type = PropertyUtil.getSchemaProperty(context, strTypeSym);

			final String derivedFilterSelection = (String) paramMap.get("pgVPDCPNCharacteristicDerivedFilter");
			String rangeAll = EnoviaResourceBundle.getProperty(context,
					"emxCPN.MasterCharacteristics.DerivedRange.All");
			String rangeLocal = EnoviaResourceBundle.getProperty(context,
					"emxCPN.MasterCharacteristics.DerivedRange.Local");
			String rangeReferenced = EnoviaResourceBundle.getProperty(context,
					"emxCPN.MasterCharacteristics.DerivedRange.Referenced");
			if (rangeAll.equalsIgnoreCase(derivedFilterSelection)
					|| rangeLocal.equalsIgnoreCase(derivedFilterSelection)) {
				String relPattern = CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC + pgV3Constants.SYMBOL_COMMA
						+ pgV3Constants.RELATIONSHIP_SHARED_TABLE;
				String typePattern = CPNCommonConstants.TYPE_SHARED_TABLE + pgV3Constants.SYMBOL_COMMA + type;
				objList = new MapList();
				objList = doObj.getRelatedObjects(context, relPattern, typePattern, objectSelects, relSelects, false,
						true, (short) 1, null, null);
				Map mpTemp = new HashMap();
				String strCharType = DomainConstants.EMPTY_STRING;
				String strType = DomainConstants.EMPTY_STRING;
				int iobjListsize=objList.size();
				for (int x = iobjListsize - 1; x >= 0; x--) {
					mpTemp = (Map) objList.get(x);
					strType = (String) mpTemp.get(DomainConstants.SELECT_TYPE);
					if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
						strCharType = (String) mpTemp
								.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
						if (strCharType != null && !strCharType.equals(type)) {
							objList.remove(x);
						}
					}
				}
				objList.sort(pgV3Constants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending", "integer");

				if (objList != null && !objList.isEmpty()) {
					Map objListPack = new HashMap();
					objListPack.put("objList", objList);
					String argsobjList[] = JPO.packArgs(objListPack);
					objList = (MapList) PDFPOCHelper.executeIntermediatorClassMethod(context, "pgGetCharacteristicList",
							argsobjList);
				}

				if (addNewRow != null && addNewRow.equals(pgV3Constants.TRUE)
						&& (strSwitchMode == null || "".equals(strSwitchMode))) {
					HashMap m = new HashMap();
					m.put((DomainConstants.SELECT_ID), "BLANK");
					objList.add(m);
				}
				MapList mlTempList = new MapList();
				;
				Map mpTempList = new HashMap();
				for (int iSeq = 0; iSeq < objList.size(); iSeq++) {
					mpTempList = new HashMap();
					mpTempList = (Map) objList.get(iSeq);
					mpTempList.put("Sequence", Integer.toString(iSeq + 1));
					mlTempList.add(mpTempList);
				}
				objList.clear();
				objList.addAll(mlTempList);
			}
			String strReferenceType = (String) doObj.getInfo(context,
					"attribute[" + pgV3Constants.ATTR_REFERENCE_TYPE + "]");
			if (rangeAll.equalsIgnoreCase(derivedFilterSelection)
					|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection)) {
				if (UIUtil.isNotNullAndNotEmpty(strReferenceType) && "R".equalsIgnoreCase(strReferenceType)) {

					MapList charList = getMasterCharacteristics(context, args);
					Map charMap = new HashMap();
					String strType = null;
					String strCharType = null;
					Iterator charListItrtr = charList.iterator();
					while (charListItrtr.hasNext()) {
						charMap = (Map) charListItrtr.next();
						if (charMap != null && !charMap.isEmpty()) {
							strType = (String) charMap.get(DomainConstants.SELECT_TYPE);
							if (strType != null && strType.equals(type)) {
								objList.add(charMap);
							}
							if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
								strCharType = (String) charMap
										.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
								if (strCharType != null && strCharType.equals(type)) {
									objList.add(charMap);

								}
							}
						}
					}
				}
			}
			makeCharacteristicsAddedBySharedTableReadonly(context, objList);
			partType = doObj.getInfo(context, DomainConstants.SELECT_TYPE);
			if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(partType)
					&& (rangeAll.equalsIgnoreCase(derivedFilterSelection)
							|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection))) {
				objList = getEbomPartsCharacterstics(context, paramMap, doObj, objList, derivedFilterSelection);
			}
		}
		return objList;
	}

	/**
	 * Add Shared table characteristics
	 * 
	 * @param
	 * @param objList
	 * @throws Exception
	 */
	private void makeCharacteristicsAddedBySharedTableReadonly(Context context, MapList objList) throws Exception {
		Map objMap = null;
		String strType = null;
		int objListsize=objList.size();
		for (int i = 0; i < objListsize; i++) {
			objMap = new HashMap();
			objMap = (Map) objList.get(i);
			strType = (String) objMap.get(DomainConstants.SELECT_TYPE);
			if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
				objMap.put("RowEditable", "readonly");
			}
		}
	}

	/**
	 * Function Added to traverse through the Product Data EBOM Structure and return
	 * it's Customer , Inner Pack and Consumer unit and their Master's
	 * Characterstics.
	 * 
	 * @param context
	 * @param paramMap
	 * @param finishedPartDO
	 * @param objList
	 * @param derivedFilterSelection
	 * @return MapList with the connected EBOM Part and their Masters Characterstics
	 * @throws Exception
	 */
	private MapList getEbomPartsCharacterstics(Context context, Map paramMap, DomainObject finishedPartDO,
			MapList objList, String derivedFilterSelection) throws Exception {
		MapList ebomCharList = new MapList();
		MapList returnList = new MapList();
		MapList tempMapList = null;
		Map partMap = new HashMap();
		Map ebomCharListMap = null;
		String[] newArguments = null;
		StringList strlObjectSelectable = new StringList(3);
		strlObjectSelectable.add(DomainConstants.SELECT_ID);
		strlObjectSelectable.add(DomainConstants.SELECT_TYPE);
		strlObjectSelectable.add(DomainConstants.SELECT_NAME);
		StringList strlRelSelectable = new StringList(3);
		strlRelSelectable.add(DomainRelationship.SELECT_FROM_TYPE);
		strlRelSelectable.add(DomainRelationship.SELECT_TO_TYPE);
		strlRelSelectable.add(DomainRelationship.SELECT_FROM_ID);
		String ebomPartId = DomainConstants.EMPTY_STRING;
		String ebomPartName = DomainConstants.EMPTY_STRING;
		String strDerivedPath = DomainConstants.EMPTY_STRING;
		String relFromType = DomainConstants.EMPTY_STRING;
		String relToType = DomainConstants.EMPTY_STRING;
		String relFromId = DomainConstants.EMPTY_STRING;
		String relFromName = DomainConstants.EMPTY_STRING;
		String strParse = DomainConstants.EMPTY_STRING;
		String strParseId = DomainConstants.EMPTY_STRING;
		String sOid = DomainConstants.EMPTY_STRING;
		String rangeAll = EnoviaResourceBundle.getProperty(context, "emxCPN.MasterCharacteristics.DerivedRange.All");
		if (objList != null && !objList.isEmpty() && rangeAll.equalsIgnoreCase(derivedFilterSelection)) {
			returnList.addAll(objList);
		}
		StringList lsRestrictedTypes=new StringList(3);
		lsRestrictedTypes.add(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
		lsRestrictedTypes.add(pgV3Constants.TYPE_PGINNERPACKUNITPART);
		lsRestrictedTypes.add(pgV3Constants.TYPE_PGCONSUMERUNITPART);
		String strTypes=StringHelper.convertObjectToString(lsRestrictedTypes);
		MapList conectedEBOMPartList = finishedPartDO.getRelatedObjects(context, DomainObject.RELATIONSHIP_EBOM,
				strTypes,
				strlObjectSelectable, // Object Select
				strlRelSelectable, // rel Select
				false, // get To
				true, // get From
				(short) 0, // recurse level
				DomainConstants.EMPTY_STRING, // where Clause
				null, // relationshipWhere Clause
				0);// return all
		Iterator ebomItr = conectedEBOMPartList.iterator();
		int itempMapListsize=tempMapList.size();;
		while (ebomItr.hasNext()) {
			partMap = (Map) ebomItr.next();
			ebomPartId = (String) partMap.get(DomainConstants.SELECT_ID);
			ebomPartName = (String) partMap.get(DomainConstants.SELECT_NAME);
			relFromType = (String) partMap.get(DomainRelationship.SELECT_FROM_TYPE);
			relToType = (String) partMap.get(DomainRelationship.SELECT_TO_TYPE);
			relFromId = (String) partMap.get(DomainRelationship.SELECT_FROM_ID);
			if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(relFromType)
					&& (pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(relToType)
							|| pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(relToType))) {
				continue;
			}
			if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equals(relFromType)
					&& pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(relToType)) {
				returnList.clear();
				ebomPartId = relFromId;
				if (UIUtil.isNotNullAndNotEmpty(ebomPartId)) {
					ebomPartName = new DomainObject(ebomPartId).getInfo(context, DomainConstants.SELECT_NAME);
				}
				}

			paramMap.put(pgV3Constants.OBJECT_ID, ebomPartId);
			paramMap.put("pgVPDCPNCharacteristicDerivedFilter", rangeAll);
			newArguments = JPO.packArgs(paramMap);
			ebomCharList.addAll(getMicroChar(context, newArguments));
			Iterator ebomCharListItr = ebomCharList.iterator();
			StringList tempList =null;
			while (ebomCharListItr.hasNext()) {
				tempMapList = new MapList();
				tempList = new StringList();
				ebomCharListMap = new HashMap();
				ebomCharListMap = (Map) ebomCharListItr.next();
				strDerivedPath = (String) ebomCharListMap.get("derivedPath");

				if (null == strDerivedPath) {
					ebomCharListMap.put("derivedPath", ebomPartName);
					ebomCharListMap.put("derivedPathId", ebomPartId);
					ebomCharListMap.put("disableSelection", "true");
					ebomCharListMap.put("RowEditable", "readonly");
					ebomCharListMap.put("derivedCharacteristic", "true");
					returnList.add(ebomCharListMap);
				} else {
					strParse = (String) ebomCharListMap.get("derivedPath");
					strParseId = (String) ebomCharListMap.get("derivedPathId");
					sOid = strParseId.substring(strParseId.lastIndexOf("'") + 1, strParseId.length());
					if (tempList.isEmpty()) {
						tempList.addElement(sOid);
					}
					if (tempList.contains(sOid)) {
						tempMapList.add(ebomCharListMap);
					}
					tempMapList.sort(pgV3Constants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending",
							"integer");
					itempMapListsize=tempMapList.size();
					for (int j = 0; j < itempMapListsize; j++) {
						returnList.add((Map) tempMapList.get(j));
					}
				}
			}
			ebomCharList.clear();
			if (relFromType.equals(pgV3Constants.TYPE_PGCUSTOMERUNITPART)
					&& relToType.equals(pgV3Constants.TYPE_FINISHEDPRODUCTPART)) {
				break;
			}
		}
		return returnList;
	}

	/**
	 * Code for getting master characteristics from the context of Reference
	 * 
	 * @param context
	 * @param args
	 * @return MapList with master characteristics from the context of Reference
	 * @throws Exception
	 */
	public MapList getMasterCharacteristics(Context context, String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}
		MapList objList = new MapList();
		String objectId = (String) PDFPOCHelper.executepgDSOCommonUtilsMethod(context, args, "objectId");
		String addNewRow = (String) PDFPOCHelper.executepgDSOCommonUtilsMethod(context, args, "AddRow");
		String strSwitchMode = (String) PDFPOCHelper.executepgDSOCommonUtilsMethod(context, args, "SwitchMode");
		String strSelectedTable = (String) PDFPOCHelper.executepgDSOCommonUtilsMethod(context, args, "selectedTable");
		strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~') + 1, strSelectedTable.length());
		String strTypeSym = EnoviaResourceBundle.getProperty(context,
				"emxCPN.Characteristic.table." + strSelectedTable);
		String type = PropertyUtil.getSchemaProperty(context, strTypeSym);
		if (UIUtil.isNotNullAndNotEmpty(objectId)) {
			DomainObject doObj = DomainObject.newInstance(context, objectId);
			String masterPartIdSelect = getMasterPartSelect(context, (DomainConstants.SELECT_ID));
			String masterPartNameSelect = getMasterPartSelect(context, SELECT_NAME);
			String masterPartTypeSelect = getMasterPartSelect(context, SELECT_TYPE);
			String masterPartCurrentSelect = getMasterPartSelect(context, SELECT_CURRENT);
			StringList objSelects = (StringList) PDFPOCHelper.createSelects((DomainConstants.SELECT_ID), SELECT_NAME,
					"attribute[" + pgV3Constants.STR_RELEASE_PHASE + "]", masterPartIdSelect, masterPartNameSelect,
					masterPartTypeSelect, masterPartCurrentSelect);
			String strRefPartStage = DomainConstants.EMPTY_STRING;
			Map objAndMasterMap = doObj.getInfo(context, objSelects);
			if (objAndMasterMap != null && !objAndMasterMap.isEmpty()) {
				String masterPartId = (String) objAndMasterMap.get(masterPartIdSelect);
				String strMasterPartType = DomainConstants.EMPTY_STRING;
				String strisFPP = DomainConstants.EMPTY_STRING;
				if (UIUtil.isNotNullAndNotEmpty(masterPartId)) {
					strMasterPartType = (String) objAndMasterMap.get(masterPartTypeSelect);
				} else {
					strMasterPartType = pgV3Constants.TYPE_FINISHEDPRODUCTPART;
				}
				strRefPartStage = (String) objAndMasterMap.get("attribute[" + pgV3Constants.STR_RELEASE_PHASE + "]");
				String strMasterCurrent = (String) objAndMasterMap.get(masterPartCurrentSelect);
				if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strMasterCurrent)
						|| pgV3Constants.STATE_COMPLETE.equalsIgnoreCase(strMasterCurrent)) {
					if (UIUtil.isNotNullAndNotEmpty(masterPartId)) {
						objList = getCharacteristicsList(context, masterPartId, type, addNewRow, strSwitchMode);
					}
					objList = updatePathForImmediateMaster(context, objList, objAndMasterMap);
				}
				String partFamilyId = doObj.getInfo(context, "to[" + pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM + "].from.id");
				if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
					MapList parentCharacteristicsList = new MapList();
					parentCharacteristicsList = getParentPartFamilyCharacteristicsForPDF(context, partFamilyId,
							strMasterPartType);
					objList.addAll(parentCharacteristicsList);
					objList = markDerivedCharacteristics(objList);
					objList = disableCheckBoxForDerivedChars(objList);
					objList = makeDerivedCharsNonEditable(objList);
				}
			}
		}
		return objList;
	}
	private MapList markDerivedCharacteristics(MapList objList) {
		return addSettingToObjects(objList, "derivedCharacteristic", "true");
	}
	private MapList disableCheckBoxForDerivedChars(MapList objList) {
		return addSettingToObjects(objList, "disableSelection", "true");
	}
	private MapList makeDerivedCharsNonEditable(MapList objList) {
		return addSettingToObjects(objList, "RowEditable", "readonly");
	}
	/**
	 * Retrieve Characteristics information for the selected types
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */

	private MapList getCharacteristicsList(Context context, String objectId, String type, String addNewRow,
			String strSwitchMode) throws Exception {
		String relPattern = CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC + ","
				+ pgV3Constants.RELATIONSHIP_SHARED_TABLE;
		MapList objList = new MapList();
		String typePattern = CPNCommonConstants.TYPE_SHARED_TABLE + "," + type;
		StringList objectSelects = (StringList) PDFPOCHelper.createSelects((DomainConstants.SELECT_ID), SELECT_NAME, SELECT_TYPE,
				CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
		StringList relSelects = (StringList) PDFPOCHelper.createSelects(SELECT_RELATIONSHIP_ID,
				pgV3Constants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
		DomainObject doMasterPart = DomainObject.newInstance(context, objectId);
		objList = doMasterPart.getRelatedObjects(context, relPattern, typePattern, objectSelects, relSelects, false,
				true, (short) 1, null, null);
		Map mpTemp = new HashMap();
		MapList mlTemp = new MapList();
		String strType = DomainConstants.EMPTY_STRING;
		String strCharType = DomainConstants.EMPTY_STRING;
		int iobjListsize=objList.size();
		for (int x = iobjListsize - 1; x >= 0; x--) {
			mpTemp = (Map) objList.get(x);
			strType = (String) mpTemp.get(DomainConstants.SELECT_TYPE);
			if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
				strCharType = (String) mpTemp.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
				if (strCharType != null && !strCharType.equals(type)) {
					objList.remove(x);
				}
			}
		}
		objList.sort(pgV3Constants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending", "integer");
		if (addNewRow != null && addNewRow.equals("true") && (UIUtil.isNotNullAndNotEmpty(strSwitchMode))) {
			HashMap m = new HashMap();
			m.put((DomainConstants.SELECT_ID), "BLANK");
			objList.add(m);
		}
		MapList mlTempList = new MapList();
		Map mpTempList = new HashMap();
		iobjListsize=objList.size();
		for (int iSeq = 0; iSeq < iobjListsize; iSeq++) {
			mpTempList = (Map) objList.get(iSeq);
			mpTempList.put("Sequence", Integer.toString(iSeq + 1));
			mlTempList.add(mpTempList);
		}
		objList.clear();
		objList.addAll(mlTempList);
		makeCharacteristicsAddedBySharedTableReadonly(context, objList);
		return objList;
	}

	/**
	 * Get Part Family Characteristics For PDF
	 * 
	 * @param context
	 * @param partFamilyId
	 * @param strMode
	 * @return MapList with parent Characteristics
	 * @throws Exception
	 */
	private MapList getParentPartFamilyCharacteristicsForPDF(Context context, String partFamilyId,
			String strMasterPartType) throws Exception {
		MapList partFamilyList = getAllParentPartFamiliesForPDF(context, partFamilyId, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
		MapList parentCharacteristicsList = getValidCharacteristicsFromParentPFsForPDF(context, partFamilyList,
				strMasterPartType);
		return parentCharacteristicsList;
	}

	/**
	 * Get All the Parent Parts Part Families For PDF
	 * 
	 * @param context
	 * @param partFamilyId
	 * @param path
	 * @param strMode
	 * @return MapList
	 * @throws Exception
	 */
	public MapList getAllParentPartFamiliesForPDF(Context context, String partFamilyId, String path, String strId)
			throws Exception {
		StringList objectSelects = (StringList) PDFPOCHelper.createSelects(SELECT_NAME, (DomainConstants.SELECT_ID));
		MapList partFamilyList = new MapList();
		Map pfMap = null;
		String pfId = null;
		String pfName = null;
		if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
			DomainObject doPartFamily = DomainObject.newInstance(context, partFamilyId);
			String inputPFName = doPartFamily.getInfo(context, SELECT_NAME);
			String partFamilyType = pgV3Constants.TYPE_PARTFAMILY;
			String subClassRel = pgV3Constants.RELATIONSHIP_SUBCLASS;
			String localPath = DomainConstants.EMPTY_STRING;
			String strPathId = DomainConstants.EMPTY_STRING;
			if (UIUtil.isNullOrEmpty(path)) {
				localPath = inputPFName;
				strPathId = partFamilyId;
			} else {
				localPath = path;
				strPathId = strId;
			}
			partFamilyList = doPartFamily.getRelatedObjects(context, subClassRel, partFamilyType, objectSelects, null,
					true, false, (short) 1, null, null, 0);
			MapList tempList = new MapList();
			Iterator pfIterator = partFamilyList.iterator();
			while (pfIterator.hasNext()) {
				pfMap = new HashMap();
				pfMap = (Map) pfIterator.next();
				pfId = (String) pfMap.get((DomainConstants.SELECT_ID));
				pfName = (String) pfMap.get(DomainConstants.SELECT_NAME);
				localPath += "->" + pfName;
				strPathId += "~" + pfId;
				pfMap.put("path", localPath);
				pfMap.put("pathId", strPathId);
				tempList.addAll(getAllParentPartFamiliesForPDF(context, pfId, localPath, strPathId));
			}
			partFamilyList.addAll(tempList);
		}
		return partFamilyList;
	}

	/**
	 * Retrieve Parent Part Family Characteristics information for path
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private MapList getValidCharacteristicsFromParentPFsForPDF(Context context, MapList partFamilyList,
			String strMasterPartType) throws Exception {
		MapList validCharacteristics = new MapList();
		MapList classifiedItems = new MapList();
		MapList characteristics = new MapList();
		Map partFamilyMap = new HashMap();
		Map classifiedItemMap = new HashMap();
		String strClassfiedItemType = DomainConstants.EMPTY_STRING;
		String strMasterPartCurrent = DomainConstants.EMPTY_STRING;
		int partFamilyListSize=partFamilyList.size();
		int classifiedItemsSize=0;
		for (int i = 0; i < partFamilyListSize; i++) {
			partFamilyMap = (Map) partFamilyList.get(i);
			StringList objectSelects = (StringList) PDFPOCHelper.createSelects(SELECT_NAME, (DomainConstants.SELECT_ID), SELECT_TYPE,
					SELECT_POLICY, SELECT_CURRENT);
			classifiedItems = getClassifiedItems(context, objectSelects, partFamilyMap);
			objectSelects.add(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
			if (classifiedItems != null && !classifiedItems.isEmpty()) {
				classifiedItemsSize=classifiedItems.size();
				for (int j = 0; j < classifiedItemsSize; j++) {
					classifiedItemMap = (Map) classifiedItems.get(j);
					if (classifiedItemMap != null && !classifiedItemMap.isEmpty()) {
						strClassfiedItemType = (String) classifiedItemMap.get(SELECT_TYPE);
						strMasterPartCurrent = (String) classifiedItemMap.get(SELECT_CURRENT);
						if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strMasterPartCurrent)
								|| pgV3Constants.STATE_COMPLETE.equalsIgnoreCase(strMasterPartCurrent)) {
							if (UIUtil.isNotNullAndNotEmpty(strMasterPartType)
									&& strClassfiedItemType.equalsIgnoreCase(strMasterPartType)) {
								characteristics = getCharacteristicsForPD(context, objectSelects, classifiedItemMap);
								characteristics = addPathToCharacteristics(partFamilyMap, classifiedItemMap,
										characteristics);
								validCharacteristics.addAll(characteristics);
							} else if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strMasterPartType)) {
								characteristics = getCharacteristicsForPD(context, objectSelects, classifiedItemMap);
								characteristics = addPathToCharacteristics(partFamilyMap, classifiedItemMap,
										characteristics);
								validCharacteristics.addAll(characteristics);
							}
						}
					}
				}
			}
		}
		return validCharacteristics;
	}
	private MapList getClassifiedItems(Context context,StringList objectSelects, Map partFamilyMap) throws FrameworkException {
		String pfId = (String)partFamilyMap.get((DomainConstants.SELECT_ID));
		DomainObject parentPartFamily = DomainObject.newInstance(context, pfId);
		MapList classifiedItems = parentPartFamily.getRelatedObjects(context,
				pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM,pgV3Constants.TYPE_PRODUCTDATA,objectSelects, null,
				false, true, (short)1, pgV3Constants.SELECT_ATTRIBUTE_REFERENCETYPE+" == 'M'", null);
		return classifiedItems;
	}
	private MapList getCharacteristicsForPD(Context context,StringList objectSelects, Map classifiedItemMap) throws Exception {
		String classifiedItemId = (String) classifiedItemMap.get((DomainConstants.SELECT_ID));
		DomainObject classifiedItem = DomainObject.newInstance(context,	classifiedItemId);
		StringList relSelects = (StringList)PDFPOCHelper.createSelects(DomainObject.SELECT_RELATIONSHIP_ID,pgV3Constants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
		String typePattern = pgV3Constants.TYPE_CHARACTERISTIC + pgV3Constants.SYMBOL_COMMA +pgV3Constants.TYPE_SHAREDTABLE;
		String relPattern = pgV3Constants.RELATIONSHIP_CHARACTERISTIC + pgV3Constants.SYMBOL_COMMA +pgV3Constants.RELATIONSHIP_SHAREDTABLE;
		MapList characteristics = classifiedItem.getRelatedObjects(context,
				relPattern, typePattern, objectSelects, relSelects,
				false, true, (short) 1, null, null);
		return characteristics;
	}

	/**
	Added the code to fix the Part family Issue
	 * @param partFamilyMap
	 * @param classifiedItemMap	
	 * @param characteristics
	 * @return MapList with Characteristics and it's Path
	 * @throws Exception
	 */
	private MapList addPathToCharacteristics(Map partFamilyMap,Map classifiedItemMap, MapList characteristics) throws Exception{
		String path = getPartFamilyPath(partFamilyMap, classifiedItemMap);
		StringList slPath = new StringList();
		slPath = FrameworkUtil.splitString(path, pgV3Constants.SYMBOL_PIPE);
		String strPath = (String)slPath.get(0);
		String strPathId = (String)slPath.get(1);
		MapList characteristicsWithPath = addPathToIndividualCharacteristics(characteristics, strPath, strPathId);
		return characteristicsWithPath;
	}
	/**
	 * Used to create part family path
	 * @param partFamilyMap
	 * @param classifiedItemMap	
	 * @param characteristics
	 * @return MapList with Characteristics and it's Path
	 * @throws Exception
	 */
	private String getPartFamilyPath(Map partFamilyMap, Map classifiedItemMap) {
		String returnPath = DomainConstants.EMPTY_STRING;
		String returnPathId = DomainConstants.EMPTY_STRING;
		try{
			String strMode = (String)partFamilyMap.get("Mode");
			String pfId = (String)partFamilyMap.get((DomainConstants.SELECT_ID));
			String pfName = (String)partFamilyMap.get(DomainConstants.SELECT_NAME);
			String classifiedItemId = (String)classifiedItemMap.get((DomainConstants.SELECT_ID));
			String classifiedItemName = (String)classifiedItemMap.get(DomainConstants.SELECT_NAME);
			String previousPath = (String)partFamilyMap.get("path");
			String previousPathId = (String)partFamilyMap.get("pathId");
			if (UIUtil.isNotNullAndNotEmpty(previousPath)) {
				returnPath = previousPath + "->" + classifiedItemName;
				returnPathId = previousPathId +"~"+classifiedItemId;
			} else {
				returnPath =  pfName + "->" + classifiedItemName;
				returnPathId = pfId +"~"+classifiedItemId;
			}
			returnPath = returnPath+pgV3Constants.SYMBOL_PIPE+returnPathId;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnPath;
	}
	/**
	 * Add path information to characteristics
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */
	private MapList addPathToIndividualCharacteristics(MapList characteristics, String path, String strPathId) {
		MapList mlPath = addSettingToObjects(characteristics, "derivedPathId", strPathId);
		return addSettingToObjects(mlPath, "derivedPath", path);
	}
	/**
	 * Create setting list
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */
	private MapList addSettingToObjects(MapList objList, String settingName, String settingValue) {
		MapList returnList = new MapList();
		Iterator charIterator = objList.iterator();
		Map objMap = null;
		while(charIterator.hasNext()) {
			objMap = new HashMap();
			objMap = (Map)charIterator.next();
			objMap.put(settingName, settingValue);
			returnList.add(objMap);
		}
		return returnList;
	}
	/**
	 *  Update the Derived path for the Immediate Master of the Product Data Part (Reference)
	 * @param context
	 * @param characteristics
	 * @param objAndMasterMap
	 * @return MapList
	 */
	private MapList updatePathForImmediateMaster(Context context, MapList characteristics, Map objAndMasterMap) {
		String path = DomainConstants.EMPTY_STRING;
		String strPathId = DomainConstants.EMPTY_STRING;
		try{
			String masterPartIdSelect = getMasterPartSelect(context, (DomainConstants.SELECT_ID));
			String masterPartNameSelect = getMasterPartSelect(context, SELECT_NAME);
			String strMode = (String)objAndMasterMap.get("Mode");
			String objLink = DomainConstants.EMPTY_STRING;
			String masterLink = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(strMode) && "PDF".equals(strMode))
			{
				objLink = (String)objAndMasterMap.get(DomainConstants.SELECT_NAME);
				String strid = (String)objAndMasterMap.get((DomainConstants.SELECT_ID));
				String strMasterPFId = (String)objAndMasterMap.get(masterPartIdSelect);
				String strMasterPFName = (String)objAndMasterMap.get(masterPartNameSelect);
				if(UIUtil.isNotNullAndNotEmpty(strMasterPFId)){
					DomainObject doObj = DomainObject.newInstance(context, strMasterPFId);
					String partFamilyId = doObj.getInfo(context,"to["+pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+"].from.id");
					String partFamilyName = doObj.getInfo(context,"to["+pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+"].from.name");
					masterLink = strMasterPFName;
					String PFLink = partFamilyName;
					path = objLink + "->" + masterLink;
					strPathId = strid+"~"+strMasterPFId;
					if(UIUtil.isNotNullAndNotEmpty(partFamilyId)){
						path = PFLink + "->" + masterLink;
						strPathId = partFamilyId+"~"+strMasterPFId;
					}
					else{
						path = masterLink;
						strPathId = strMasterPFId;
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return addPathToIndividualCharacteristics(characteristics, path,strPathId);
	}
	/**
	 * Create Master Part Select
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */
	private String getMasterPartSelect(Context context, String finalSelect) {
		StringBuffer masterPartSelectBuf = new StringBuffer();
		masterPartSelectBuf.append("to[")
		.append(pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM)
		.append("].frommid[")
		.append(pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE)
		.append("].torel.to.")
		.append(finalSelect);
		return masterPartSelectBuf.toString();
	}
}
