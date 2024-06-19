package com.pdfview.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.AllBillOfMaterial;
import com.pdfview.impl.FPP.BillofMaterial;
import com.pdfview.impl.FPP.BillofMaterialsConsumerUnit;
import com.pdfview.impl.FPP.BillofMaterialsCustomerUnit;
import com.pdfview.impl.FPP.BillofMaterialsFPPs;
import com.pdfview.impl.FPP.BillofMaterialsInnerPack;
import com.pdfview.impl.FPP.BillofMaterialsMain;
import com.pdfview.impl.FPP.BillofMaterialsTransportUnit;
import com.pdfview.impl.FPP.Substitute;
import com.pdfview.impl.FPP.Substitutes;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.AccessConstants;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetAllBillOfMaterial {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	CalenderHelper calenderHelper = null;

	public GetAllBillOfMaterial(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public AllBillOfMaterial getComponent() {
		return getBillOfMaterialSubstituteAndSAPBOMDataFPP(_context, _OID);
	}

	/**
	 * Retrieve BOM and substitute information for FPP
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */

	private AllBillOfMaterial getBillOfMaterialSubstituteAndSAPBOMDataFPP(Context context, String IPSId) {

		AllBillOfMaterial allBillOfMaterial = new AllBillOfMaterial();

		BillofMaterialsMain billofMaterialsMain = new BillofMaterialsMain();
		BillofMaterialsCustomerUnit billofMaterialsCustomerUnit = new BillofMaterialsCustomerUnit();
		BillofMaterialsInnerPack billofMaterialsInnerPack = new BillofMaterialsInnerPack();
		BillofMaterialsConsumerUnit billofMaterialsConsumerUnit = new BillofMaterialsConsumerUnit();
		BillofMaterialsTransportUnit billofMaterialsTransportUnit = new BillofMaterialsTransportUnit();

		long startTime = new Date().getTime();
		calenderHelper = new CalenderHelper(context);
		boolean isPushContext = false;
		try {
			StringList selectStmtsRel = new StringList(12);
			selectStmtsRel.add(DomainRelationship.SELECT_TO_ID);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGOPTIONALCOMPONENT);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHT);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTUOM);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHT);
			StringList selectStmt = new StringList(9);
			selectStmt.add(DomainConstants.SELECT_NAME);
			selectStmt.add(DomainConstants.SELECT_TYPE);
			selectStmt.add(DomainConstants.SELECT_ID);
			selectStmt.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			selectStmt.add(DomainConstants.SELECT_CURRENT);
			selectStmt.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			selectStmt.add(DomainConstants.SELECT_REVISION);
			selectStmt.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
			selectStmt.add(pgV3Constants.SELECT_POLICY);
			String strParentId = DomainConstants.EMPTY_STRING;
			String strRelId = DomainConstants.EMPTY_STRING;
			String strType = DomainConstants.EMPTY_STRING;
			String strToId = DomainConstants.EMPTY_STRING;
			String strpgChange = DomainConstants.EMPTY_STRING;
			String strFindNumber = DomainConstants.EMPTY_STRING;
			String strRefDesig = DomainConstants.EMPTY_STRING;
			String strOptComp = DomainConstants.EMPTY_STRING;
			String strlevel = DomainConstants.EMPTY_STRING;
			String strEBOMData = DomainConstants.EMPTY_STRING;
			String strid = DomainConstants.EMPTY_STRING;
			String strTUName = DomainConstants.EMPTY_STRING;
			String strNet = DomainConstants.EMPTY_STRING;
			String strLoss = DomainConstants.EMPTY_STRING;
			String strNetWeight = DomainConstants.EMPTY_STRING;
			String strNetWeightUOM = DomainConstants.EMPTY_STRING;
			String strMinimum = DomainConstants.EMPTY_STRING;
			String strMaximum = DomainConstants.EMPTY_STRING;
			String strGrossWeight = DomainConstants.EMPTY_STRING;
			StringList slMainEBOM = new StringList();
			StringList slCOPEBOM = new StringList();
			StringList slCUPEBOM = new StringList();
			StringList slIPEBOM = new StringList();
			MapList mlMain = new MapList();
			MapList mlTP = new MapList();
			String strParentType = null;
			Map mpBom = new HashMap();
			DomainObject dmoBom = null;

			ArrayList alEBOMParents = getParentEBOMsWOIntermediateObjects(context, IPSId, new ArrayList(), "First");
			StringTokenizer stEBOMData =null;
			if(alEBOMParents!=null && !alEBOMParents.isEmpty()) {
				int iEBOMParents=alEBOMParents.size();
				for (int i = 0; i < iEBOMParents; i++) {
					strEBOMData = (String) alEBOMParents.get(i);
					stEBOMData = new StringTokenizer(strEBOMData, pgV3Constants.DUMP_CHARACTER);
					if (stEBOMData.hasMoreTokens()) {
						strType = (String) stEBOMData.nextToken();
						strParentId = (String) stEBOMData.nextToken();
						strRelId = (String) stEBOMData.nextToken();
						strlevel = (String) stEBOMData.nextToken();
						if ("MAIN".equals(strType)) {
							slMainEBOM.add(strParentId + pgV3Constants.DUMP_CHARACTER + strType
									+ pgV3Constants.DUMP_CHARACTER + strRelId + pgV3Constants.DUMP_CHARACTER + strlevel);
						} else if ("COP".equals(strType)) {
							slCOPEBOM.add(strParentId + pgV3Constants.DUMP_CHARACTER + strType
									+ pgV3Constants.DUMP_CHARACTER + strRelId + pgV3Constants.DUMP_CHARACTER + strlevel);
						} else if ("CUP".equals(strType)) {
							slCUPEBOM.add(strParentId + pgV3Constants.DUMP_CHARACTER + strType
									+ pgV3Constants.DUMP_CHARACTER + strRelId + pgV3Constants.DUMP_CHARACTER + strlevel);
						} else if ("IP".equals(strType)) {
							slIPEBOM.add(strParentId + pgV3Constants.DUMP_CHARACTER + strType + pgV3Constants.DUMP_CHARACTER
									+ strRelId + pgV3Constants.DUMP_CHARACTER + strlevel);
						}
					}
				}
			}
			// BOM
			if (null != slMainEBOM && slMainEBOM.size() > 0) {

				mlMain = getBOMRelData(context, slMainEBOM);

				if (null != mlMain && mlMain.size() > 0) {
					billofMaterialsMain.setBillofMaterialsFPPs(
							getEBOMTablewithData(context, mlMain, IPSId, PDFConstant.BOMMainTableNames));
					billofMaterialsMain
							.setSubstitutes(getSubstitutesData(context, mlMain, PDFConstant.SubstituteMainTableNames));
				}
			}
			// CUP BOM
			if (null != slCUPEBOM && slCUPEBOM.size() > 0) {

				mlMain = getBOMRelData(context, slCUPEBOM);

				if (null != mlMain && mlMain.size() > 0) {

					billofMaterialsCustomerUnit.setBillofMaterialsFPPs(
							getEBOMTablewithData(context, mlMain, IPSId, PDFConstant.BOMCUPTableNames));
					billofMaterialsCustomerUnit
							.setSubstitutes(getSubstitutesData(context, mlMain, PDFConstant.SubstituteCUPTableNames));
				}
			}
			// IP BOM
			if (null != slIPEBOM && slIPEBOM.size() > 0) {

				mlMain = getBOMRelData(context, slIPEBOM);

				if (null != mlMain && mlMain.size() > 0) {

					billofMaterialsInnerPack.setBillofMaterialsFPPs(
							getEBOMTablewithData(context, mlMain, IPSId, PDFConstant.BOMInnerPackTableNames));
					billofMaterialsInnerPack.setSubstitutes(
							getSubstitutesData(context, mlMain, PDFConstant.SubstituteInnerPackTableNames));
				}
			}
			// COP BOM
			if (null != slCOPEBOM && slCOPEBOM.size() > 0) {

				mlMain = getBOMRelData(context, slCOPEBOM);

				if (null != mlMain && mlMain.size() > 0) {
					billofMaterialsConsumerUnit.setBillofMaterialsFPPs(
							getEBOMTablewithData(context, mlMain, IPSId, PDFConstant.BOMCOPTableNames));
					billofMaterialsConsumerUnit
							.setSubstitutes(getSubstitutesData(context, mlMain, PDFConstant.SubstituteCOPTableNames));

				}
			}
			DomainObject doObjFinishedProductPart = DomainObject.newInstance(context, IPSId);
			StringList slObjSelects = new StringList(3);
			slObjSelects.addElement(DomainConstants.SELECT_ID);
			slObjSelects.addElement(DomainConstants.SELECT_TYPE);
			slObjSelects.addElement(DomainConstants.SELECT_NAME);
			StringList slRelSelects = new StringList(1);
			slRelSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
			MapList mlTransportUnits = new MapList();
			MapList mlConnectedEBOMParents = new MapList();
			int connectedEBOMParentsSize = 0;
			int mlToIdSize = 0;
			Map mpTransportUnit = null;
			Map mapObject = null;
			DomainObject domObject = null;
			MapList mlToId = null;
			String[] relargs = new String[1];
			Map mData = null;
			mlTransportUnits = doObjFinishedProductPart.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_PGTRANSPORTUNIT, pgV3Constants.TYPE_PGTRANSPORTUNITPART, slObjSelects,
					slRelSelects, false, true, (short) 1, null, null, 0);
			if (mlTransportUnits != null && !mlTransportUnits.isEmpty()) {
				int lTUPSize = mlTransportUnits.size();
				for (int i = 0; i < lTUPSize; i++) {
					mapObject = (Map) mlTransportUnits.get(i);
					strid = (String) mapObject.get(DomainConstants.SELECT_ID);
					strTUName = (String) mapObject.get(DomainConstants.SELECT_NAME);
					if (strid != null) {
						domObject = DomainObject.newInstance(context, strid);
						mlConnectedEBOMParents = domObject.getRelatedObjects(context, // Context
								DomainConstants.RELATIONSHIP_EBOM, // relPattern
								pgV3Constants.SYMBOL_STAR, // typePattern
								slObjSelects, // objectSelects
								slRelSelects, // relationshipSelects
								false, // getTo - Get Parent Data
								true, // getFrom - Get Child Data
								(short) 0, // recurseToLevel
								DomainConstants.EMPTY_STRING, // objectWhere
								DomainConstants.EMPTY_STRING); // relationshipWhere
					}

					if (null != mlConnectedEBOMParents && !mlConnectedEBOMParents.isEmpty()) {
						connectedEBOMParentsSize = mlConnectedEBOMParents.size();
						for (int j = 0; j < connectedEBOMParentsSize; j++) {
							mpTransportUnit = (Map) mlConnectedEBOMParents.get(j);
							strParentId = (String) mpTransportUnit.get(DomainObject.SELECT_ID);
							strParentType = (String) mpTransportUnit.get(DomainObject.SELECT_TYPE);
							strRelId = (String) mpTransportUnit.get(DomainObject.SELECT_RELATIONSHIP_ID);
							strlevel = (String) mpTransportUnit.get(DomainConstants.SELECT_LEVEL);
							relargs[0] = strRelId;
							mlToId = (MapList) DomainRelationship.getInfo(context, relargs, selectStmtsRel);
							if (null != mlToId && !mlToId.isEmpty()) {
								mlToIdSize = mlToId.size();
								for (int k = 0; k < mlToIdSize; k++) {
									mData = (Map) mlToId.get(k);
									strToId = (String) mData.get(DomainRelationship.SELECT_TO_ID);
									strpgChange = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
									strFindNumber = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
									strRefDesig = (String) mData
											.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
									strOptComp = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGOPTIONALCOMPONENT);
									strNet = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
									strLoss = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
									strNetWeight = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHT);
									strNetWeightUOM = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTUOM);
									strMinimum = (String) mData
											.get(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
									strMaximum = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
									strGrossWeight = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHT);
								}
								dmoBom = DomainObject.newInstance(context, strToId);
								mpBom = dmoBom.getInfo(context, selectStmt);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE, strpgChange);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, strFindNumber);
								mpBom.put(PDFConstant.ID_CONNECTION, strRelId);
								mpBom.put("level", strlevel);
								mpBom.put("ParentId", strParentId);
								mpBom.put("ParentType", strParentType);
								mpBom.put("RefDesig", strRefDesig);
								mpBom.put("OptComp", strOptComp);
								mpBom.put("TUName", strTUName);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE, strpgChange);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, strFindNumber);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_TOTAL, strNet);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_LOSS, strLoss);
								mpBom.put("NetWeight", strNetWeight);
								mpBom.put("NetWeightUOM", strNetWeightUOM);
								mpBom.put("strMaximum", strMaximum);
								mpBom.put("strMinimum", strMinimum);
								mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHT, strGrossWeight);
								mlTP.add(mpBom);
							}
						}
						mlTP.addSortKey("TUName", "ascending", "String");
						mlTP.addSortKey(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");
						mlTP.sort();
					}
				}
			}
			if (mlTP != null && mlTP.size() > 0) {
				billofMaterialsTransportUnit.setBillofMaterialsFPPs(
						getEBOMTablewithData(context, mlTP, IPSId, PDFConstant.BOMTUPTableNames));

			}
			billofMaterialsTransportUnit
					.setSubstitutes(getSubstitutesData(context, mlTP, PDFConstant.SubstituteTUPTableNames));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isPushContext) {
				try {
					ContextUtil.popContext(context);
				} catch (FrameworkException e) {
					e.printStackTrace();
				}
				isPushContext = false;
			}
		}
		allBillOfMaterial.setBillofMaterialsConsumerUnit(billofMaterialsConsumerUnit);
		allBillOfMaterial.setBillofMaterialsCustomerUnit(billofMaterialsCustomerUnit);
		allBillOfMaterial.setBillofMaterialsInnerPack(billofMaterialsInnerPack);
		allBillOfMaterial.setBillofMaterialsMain(billofMaterialsMain);
		allBillOfMaterial.setBillofMaterialsTransportUnit(billofMaterialsTransportUnit);
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the getBillOfMaterialSubstituteAndSAPBOMDataFPP Method is-->"
				+ (endTime - startTime));
		return allBillOfMaterial;
	}

	/**
	 * Retrieve connected substitutes data
	 * 
	 * @param context
	 * @param mlMain     -connect bom details
	 * @param headerName -header of the table
	 * @return Substitutes-sustritues object with substitute data
	 */
	public Substitutes getSubstitutesData(Context context, MapList mlMain, String headerName) {
		Substitutes substitutes = new Substitutes();
		substitutes.setHeaderName(headerName);
		List<Substitute> substituteList = substitutes.getSubstitute();
		// CUP Substitute
		try {
			Map mapFPP = null;
			MapList mlSub = null;
			Map mapSub = null;
			String strParentId = DomainConstants.EMPTY_STRING;
			for (Iterator iterator = mlMain.iterator(); iterator.hasNext();) {
				mapFPP = (Map) iterator.next();
				strParentId = (String) mapFPP.get("ParentId");
				mlSub = new MapList();
				mlSub = getSubstituteData(context, mapFPP);
				if (null != mlSub && mlSub.size() > 0) {
					for (Iterator objIterator = mlSub.iterator(); objIterator.hasNext();) {
						mapSub = (Map) objIterator.next();
						substituteList.add(getSubstituteTablewithData(context, mapSub, strParentId));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return substitutes;
	}

	/**
	 * This Private method will be executed to generate BOM Data
	 * 
	 * @param context  the eMatrix <code>Context</code> object
	 * @param MaplList mlMain holds the relationship data
	 * @return String contains the BOM Data
	 * @throws Exception if the operation fails
	 */
	private BillofMaterialsFPPs getEBOMTablewithData(Context context, MapList mlMain, String sActualParentId,
			String headerName) {
		BillofMaterialsFPPs billofMaterialsFPPs = new BillofMaterialsFPPs();
		billofMaterialsFPPs.setHeaderName(headerName);
		List<BillofMaterial> billofMaterialList = billofMaterialsFPPs.getBillofMaterial();
		long startTime = new Date().getTime();

		try {
			Map mObject = null;
			String strParentType = null;
			if (null != mlMain && mlMain.size() > 0) {
				mObject = (Map) mlMain.get(0);
				strParentType = (String) mObject.get("ParentType");
			}
			String stridConn = DomainConstants.EMPTY_STRING;
			String strid = DomainConstants.EMPTY_STRING;
			String strLevel = DomainConstants.EMPTY_STRING;
			String stParentId = DomainConstants.EMPTY_STRING;
			String strState = DomainConstants.EMPTY_STRING;
			String strStage = DomainConstants.EMPTY_STRING;
			String strRefDes = DomainConstants.EMPTY_STRING;
			String strOptComp = DomainConstants.EMPTY_STRING;
			String strTUName = DomainConstants.EMPTY_STRING;
			String strName = DomainConstants.EMPTY_STRING;
			String strChg = DomainConstants.EMPTY_STRING;
			String strTitle = DomainConstants.EMPTY_STRING;
			String strType1 = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			String strSub = DomainConstants.EMPTY_STRING;
			String strFindNumber = DomainConstants.EMPTY_STRING;
			String strQty = DomainConstants.EMPTY_STRING;
			String strBUOM = DomainConstants.EMPTY_STRING;
			String strRefDe = DomainConstants.EMPTY_STRING;
			String strComments = DomainConstants.EMPTY_STRING;
			Map mpParamMap = null;
			Map mpBOMBUOM = null;
			Map mpBOMChg = null;
			Map mpObjChg = null;
			Map mpObj = null;
			Map mpBOMSubs = null;
			Map mpParam = null;
			Map mpObjBUOM = null;
			Map mpBOMQty = null;
			Map mpParamQty = null;
			Map mpObjQty = null;
			Map mpSettingsMap = null;
			Map mpColumnMap = null;
			MapList mlObjList = null;
			MapList mlObjListQty = null;
			MapList mlObjListBUOM = null;
			MapList mlObjListChg = null;
			String strCurrentState = DomainConstants.EMPTY_STRING;
			String strPolicy = DomainConstants.EMPTY_STRING;
			String strMax = DomainConstants.EMPTY_STRING;
			String strMin = DomainConstants.EMPTY_STRING;
			String strDensityUOM = DomainConstants.EMPTY_STRING;
			String strNet = DomainConstants.EMPTY_STRING;
			String strLoss = DomainConstants.EMPTY_STRING;
			String strNetWeight = DomainConstants.EMPTY_STRING;
			String strNetWeightUOM = DomainConstants.EMPTY_STRING;
			String strProductDensity = DomainConstants.EMPTY_STRING;
			String sMatFunValue = DomainConstants.EMPTY_STRING;
			String strRev = DomainConstants.EMPTY_STRING;
			String strReleaseDate = DomainConstants.EMPTY_STRING;
			boolean hasReadAccess = false;
			Map mapMain = null;
			String[] argsChg = null;
			String[] argsSubs = null;
			Vector vEBOMChg = null;
			Vector vEBOMFindNumber = null;
			Vector vEBOMSub = null;
			HashMap mpAlternate = null;
			MapList mlObjListAlternate = null;
			HashMap mpObjAlternate = null;
			HashMap mpBOMAlternate = null;
			String[] argsAlt = null;
			String[] argsQty = null;
			Vector vEBOMQty = null;
			String strAlternate = DomainConstants.EMPTY_STRING;
			Vector vEBOMAlternate = null;
			String[] argsBUOM = null;
			Vector vEBOMBUOM = null;
			Vector vEBOMComments = null;
			Map mpParamObjMap = null;
			DomainObject domObject = DomainObject.newInstance(context);
			for (Iterator iterator = mlMain.iterator(); iterator.hasNext();) {
				BillofMaterial billofMaterial = new BillofMaterial();
				mapMain = (Map) iterator.next();
				stridConn = (String) mapMain.get(PDFConstant.ID_CONNECTION);
				strid = (String) mapMain.get(DomainConstants.SELECT_ID);
				domObject.setId(strid);
				hasReadAccess = domObject.checkAccess(context, (short) AccessConstants.cRead);
				strLevel = (String) mapMain.get(DomainConstants.SELECT_LEVEL);
				stParentId = (String) mapMain.get("ParentId");
				strState = (String) mapMain.get(DomainConstants.SELECT_CURRENT);
				strPolicy = (String) mapMain.get(DomainConstants.SELECT_POLICY);
				billofMaterial.setPolicy(strPolicy);
				strCurrentState = EnoviaResourceBundle.getStateI18NString(context, strPolicy, strState,
						context.getLocale().getLanguage());
				strStage = (String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				strRefDes = (String) mapMain.get("RefDesig");
				strOptComp = (String) mapMain.get("OptComp");
				strName = (String) mapMain.get(DomainConstants.SELECT_NAME);
				strTUName = (String) mapMain.get("TUName");
				strRev = (String) mapMain.get(DomainConstants.SELECT_REVISION);
				strMax = (String) mapMain.get("strMaximum");
				strMin = (String) mapMain.get("strMinimum");
				strDensityUOM = (String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_PGDENSITYUOM);
				strNet = (String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
				strLoss = (String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
				strNetWeight = (String) mapMain.get("NetWeight");
				strNetWeightUOM = (String) mapMain.get("NetWeightUOM");

				strProductDensity = (String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_PGONSHELFPRODUCTDENSITY);
				billofMaterial.setMaximum(strMax);
				billofMaterial.setMinimum(strMin);
				billofMaterial.setDensityUOM(strDensityUOM);
				billofMaterial.setNet(strNet);
				billofMaterial.setLoss(strLoss);
				billofMaterial.setNetWeight(strNetWeight);
				billofMaterial.setWeightUoM(strNetWeightUOM);
				billofMaterial.setPolicy(strPolicy);
				billofMaterial.setOnShelfProduct(strProductDensity);
				strReleaseDate = calenderHelper
						.getFormattedDate((String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));

				billofMaterial.setName(StringHelper.validateString1(strName));

				mlObjListChg = new MapList();
				mpObjChg = new HashMap();
				mpObjChg.put(PDFConstant.ID_CONNECTION, stridConn);
				mpObjChg.put(DomainConstants.SELECT_LEVEL, strLevel);
				mpObjChg.put(DomainConstants.SELECT_ID, strid);
				mlObjListChg.add(mpObjChg);
				mpBOMChg = new HashMap();
				mpBOMChg.put("objectList", mlObjListChg);
				mpParamObjMap = new HashMap();
				mpParamObjMap.put("BOMViewMode", DomainConstants.EMPTY_STRING);
				mpParamObjMap.put("selectedTable", DomainConstants.EMPTY_STRING);
				mpBOMChg.put("paramList", mpParamObjMap);
				argsChg = JPO.packArgs(mpBOMChg);
				vEBOMChg = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart", "getChgColumnData",
						argsChg);
				if (hasReadAccess) {
					strChg = DomainConstants.EMPTY_STRING;
					if ((vEBOMChg != null) && (vEBOMChg.size() > 0)) {
						strChg = (String) vEBOMChg.get(0);
					}
				} else {
					strChg = pgV3Constants.NO_ACCESS;
				}

				billofMaterial.setChg(StringHelper.validateString1(strChg));

				vEBOMFindNumber = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart", "getFindNumberSB",
						argsChg);
				if ((vEBOMFindNumber != null) && (!vEBOMFindNumber.isEmpty()))
					strFindNumber = (String) vEBOMFindNumber.get(0);

				billofMaterial.setfN(StringHelper.validateString1(strFindNumber));

				strTitle = (String) mapMain.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				if (UIUtil.isNullOrEmpty(strTitle)) {
					strTitle = DomainConstants.EMPTY_STRING;
				}
				if (UIUtil.isNullOrEmpty(strRefDes)) {
					strRefDes = DomainConstants.EMPTY_STRING;
				}
				strTitle = StringHelper.filterLessAndGreaterThanSign(strTitle);
				billofMaterial.setTitle(StringHelper.validateString1(strTitle));
				strType1 = (String) mapMain.get(DomainConstants.SELECT_TYPE);
				strValue = UINavigatorUtil.getAdminI18NString("Type", strType1, context.getSession().getLanguage());
				billofMaterial.setType(StringHelper.validateString1(strValue));
				mpParam = new HashMap();
				mpParam.put("reportFormat", "pdf");
				mpParam.put("objectId", stParentId);
				mlObjList = new MapList();
				mpObj = new HashMap();
				mpObj.put(PDFConstant.ID_CONNECTION, stridConn);
				mpObj.put(DomainConstants.SELECT_ID, strid);
				mpObj.put(DomainConstants.SELECT_LEVEL, strLevel);
				mlObjList.add(mpObj);
				mpBOMSubs = new HashMap();
				mpBOMSubs.put("paramList", mpParam);
				mpBOMSubs.put("objectList", mlObjList);
				argsSubs = JPO.packArgs(mpBOMSubs);
				vEBOMSub = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart",
						"getColumnSubstitutePartsFlagData", argsSubs);
				strSub = null;
				if ((vEBOMSub != null) && (!vEBOMSub.isEmpty()))
					strSub = (String) vEBOMSub.get(0);
				mpAlternate = new HashMap();
				mpAlternate.put("reportFormat", "pdf");
				mpAlternate.put("objectId", _OID);
				mlObjListAlternate = new MapList();
				mpObjAlternate = new HashMap();
				mpObjAlternate.put("id", stParentId);
				mlObjListAlternate.add(mpObjAlternate);
				mpBOMAlternate = new HashMap();
				mpBOMAlternate.put("paramList", mpAlternate);
				mpBOMAlternate.put("objectList", mlObjListAlternate);
				argsAlt = JPO.packArgs(mpBOMAlternate);
				strAlternate = DomainConstants.EMPTY_STRING;
				vEBOMAlternate = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart",
						"getColumnAlternateData", argsAlt);
				if ((vEBOMAlternate != null) && (!vEBOMAlternate.isEmpty()))
					strAlternate = (String) vEBOMAlternate.get(0);
				billofMaterial.setSubstitution(StringHelper.validateString1(strSub));
				billofMaterial.setAlternate(strAlternate);

				mpParamQty = new HashMap();
				mpParamQty.put("reportFormat", "emxPart:getEBOMsWithRelSelectablesSB");
				mlObjListQty = new MapList();
				mpObjQty = new HashMap();
				mpObjQty.put(PDFConstant.ID_CONNECTION, stridConn);
				mlObjListQty.add(mpObjQty);
				mpBOMQty = new HashMap();
				mpBOMQty.put("paramList", mpParamQty);
				mpBOMQty.put("objectList", mlObjListQty);
				argsQty = JPO.packArgs(mpBOMQty);
				vEBOMQty = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart", "getQuantity", argsQty);
				sMatFunValue = getMaterialFunction(context, stridConn);
				billofMaterial.setMeterialFunction(sMatFunValue);
				strQty = null;
				if ((vEBOMQty != null) && (!vEBOMQty.isEmpty()))
					strQty = (String) vEBOMQty.get(0);

				billofMaterial.setQty(StringHelper.validateString1(strQty));
				mpSettingsMap = new HashMap();
				mpSettingsMap.put("DSOPickListRelationshipName", "relationship_pgPDTemplatestopgPLIBUOM");
				mpSettingsMap.put("DSOPickListAttribute", "attribute_pgBaseUnitOfMeasure");
				mpColumnMap = new HashMap();
				mpColumnMap.put("settings", mpSettingsMap);
				mlObjListBUOM = new MapList();
				mpObjBUOM = new HashMap();
				mpObjBUOM.put(DomainConstants.SELECT_ID, strid);
				mlObjListBUOM.add(mpObjBUOM);
				mpParamMap = new HashMap();
				mpBOMBUOM = new HashMap();
				mpBOMBUOM.put("columnMap", mpColumnMap);
				mpBOMBUOM.put("objectList", mlObjListBUOM);
				mpBOMBUOM.put("paramList", mpParamMap);
				argsBUOM = JPO.packArgs(mpBOMBUOM);
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
				vEBOMBUOM = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxCPNProductData",
						"getColumnPickListData", argsBUOM);
				ContextUtil.popContext(context);
				strBUOM = null;
				if ((vEBOMBUOM != null) && (!vEBOMBUOM.isEmpty()))
					strBUOM = (String) vEBOMBUOM.get(0);

				billofMaterial.setBaseUnitofMeasure(StringHelper.validateString1(strBUOM));
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
				vEBOMComments = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart",
						"getCommentColumnDataFinishedProductForDSO", argsChg);
				ContextUtil.popContext(context);
				if ((vEBOMComments != null) && (!vEBOMComments.isEmpty()))
					strComments = (String) vEBOMComments.get(0);
				if (UIUtil.isNotNullAndNotEmpty(strComments)) {
					strComments = StringHelper.filterLessAndGreaterThanSign(strComments);
				} else {
					strComments = DomainConstants.EMPTY_STRING;
				}
				billofMaterial.setComments(StringHelper.validateString1(strComments));

				billofMaterial.setRevision(StringHelper.validateString1(strRev));
				billofMaterial.setReleaseDate(StringHelper.validateString1(strReleaseDate));
				billofMaterial.setCertification(getPLIMaterialCertifications(context, strid));
				billofMaterial.setState(StringHelper.validateString1(strCurrentState));

				billofMaterial.setPhase(StringHelper.validateString1(strStage));
				strRefDes = StringHelper.filterLessAndGreaterThanSign(strRefDes);

				billofMaterial.setRefDesOptional(StringHelper.validateString1(strRefDe));
				billofMaterial.setComponents(StringHelper.validateString1(strOptComp));
				billofMaterialList.add(billofMaterial);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the getEBOMTablewithData Method is-->" + (endTime - startTime));
		return billofMaterialsFPPs;
	}

	/**
	 * /** Retrive Material Certifications BOM/Substitute Data
	 * 
	 * @param context
	 * @param args
	 * @return StringBuilder
	 * @throws MatrixException
	 */
	private String getPLIMaterialCertifications(Context context, String strObjectId) throws Exception {
		DomainObject domainObject = null;
		StringList certificationsList = new StringList();
		Map mlCertificationMAP = null;
		try {
			if (StringHelper.validateString(strObjectId)) {
				StringList objectSelects = new StringList(1);
				objectSelects.add(DomainConstants.SELECT_NAME);
				domainObject = DomainObject.newInstance(context, strObjectId);

				MapList mlCertificationsInfo = domainObject.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS,
						pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS, objectSelects, null, false, true, (short) 1,
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				if (mlCertificationsInfo != null && !mlCertificationsInfo.isEmpty()) {
					int iSize = mlCertificationsInfo.size();
					for (int i = 0; i < iSize; i++) {
						mlCertificationMAP = (Map) mlCertificationsInfo.get(i);
						if (mlCertificationMAP != null && !mlCertificationMAP.isEmpty()) {
							certificationsList.add(StringHelper
									.convertObjectToString(mlCertificationMAP.get(DomainConstants.SELECT_NAME)));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FrameworkUtil.join(certificationsList, pgV3Constants.SYMBOL_COMMA);
	}

	/**
	 * Generate substitute table with actual substitute content
	 * 
	 * @param mapSub Substitute Map
	 * @param strId  Substitute object Id
	 * @return BOMSubstituteSAPBuffer.toString string with data
	 * @throws Exception
	 */
	private Substitute getSubstituteTablewithData(Context context, Map mapSub, String strId) {
		long startTime = new Date().getTime();
		Substitute substitute = new Substitute();
		try {
			StringList selectStmtSub = new StringList(7);
			selectStmtSub.add(DomainConstants.SELECT_TYPE);
			selectStmtSub.add(DomainConstants.SELECT_REVISION);
			selectStmtSub.add(DomainConstants.SELECT_NAME);
			selectStmtSub.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			selectStmtSub.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			selectStmtSub.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
			selectStmtSub.add("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION+"].to.name");
			
			String strSubId = (String) mapSub.get(DomainConstants.SELECT_ID);
			String strSubQty = (String) mapSub.get("Quantity");
			String strSubUoM = (String) mapSub.get("UoM");
			String strSubValidUntil = (String) mapSub.get("ValidUntil");
			String strSubSCN = (String) mapSub.get("SCN");
			String strSubComments = (String) mapSub.get(pgV3Constants.ATTRIBUTE_COMMENT);
			String strSubReferenceDesignator = (String) mapSub.get(pgV3Constants.ATTRIBUTE_REFERENCEDESIGNATOR);
			String strOriginatingSource = (String) mapSub.get(pgV3Constants.ATTRIBUTE_PGORIGINATINGSOURCE);
			String strBaseUnitOfMeasurePickList = (String) mapSub.get("pgBaseUnitOfMeasurePickList");
			String strBaseUnitOfMeasure = (String) mapSub.get(pgV3Constants.ATTRIBUTE_PGBASEUNITOFMEASURE);
			String strEBOMSubstituteRelId = (String) mapSub.get("EBOMSubstituteRelId");
			String strMinimum = DomainConstants.EMPTY_STRING;
			String strMaximum = DomainConstants.EMPTY_STRING;

			String strReportedFunction =  DomainConstants.EMPTY_STRING;
			String strRelId = (String) mapSub.get("EBOMSubstituteRelId");
			String relargs[]=new String[1];
			relargs[0] = strRelId;
			StringList selectStmtsRel=new StringList(2);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
			MapList mlToId = new MapList();
			Map mData = null;
			mlToId = (MapList) DomainRelationship.getInfo(context, relargs, selectStmtsRel);
			if (null != mlToId && !mlToId.isEmpty()) {
				int mlToIdSize = mlToId.size();
				for (int i = 0; i < mlToIdSize; i++) {
					mData = (Map) mlToId.get(i);
					strMinimum = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
					strMaximum = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
				}
			}
			substitute.setMaximum(strMaximum);
			substitute.setMinimum(strMinimum);
			Map mpParamName = new HashMap();
			mpParamName.put("reportFormat", "pdf");
			MapList mlObjListName = new MapList();
			Map mpObjName = new HashMap();
			mpObjName.put(DomainConstants.SELECT_ID, strSubId);
			mlObjListName.add(mpObjName);
			Map mpBOMSubsName = new HashMap();
			mpBOMSubsName.put("paramList", mpParamName);
			mpBOMSubsName.put("objectList", mlObjListName);
			String[] argsSubsName = JPO.packArgs(mpBOMSubsName);
			Vector vEBOMSubName = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart", "getModifiedName",
					argsSubsName);
			String strSubName = null;
			if ((vEBOMSubName != null) && (!vEBOMSubName.isEmpty()))
				strSubName = (String) vEBOMSubName.get(0);
			DomainRelationship relObj = new DomainRelationship(strEBOMSubstituteRelId);
			String strOptComponent = relObj.getAttributeValue(context, pgV3Constants.ATTRIBUTE_PGOPTIONALCOMPONENT);
			DomainObject dmoBomSub = DomainObject.newInstance(context, strSubId);
			Map mpBomSub = dmoBomSub.getInfo(context, selectStmtSub);
			strReportedFunction =StringHelper.convertObjectToString(mpBomSub.get("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION+"].to.name"));
			substitute.setReportedFunction(strReportedFunction);
			String strRev = (String) mpBomSub.get(DomainConstants.SELECT_REVISION);
			String strReleaseDate = calenderHelper
					.getFormattedDate((String) mpBomSub.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
			substitute.setSubstituteParts(StringHelper.validateString1(strSubName));
			substitute.setSubstitutePartsRev(StringHelper.validateString1(strRev));

			MapList mlObjListSCN = new MapList();
			Map mpObjNameSCN = new HashMap();
			mpObjNameSCN.put("SCN", strSubSCN);
			mlObjListSCN.add(mpObjNameSCN);
			Map mpBOMSubsSCN = new HashMap();
			mpBOMSubsSCN.put("objectList", mlObjListSCN);
			String[] argsSubsSCN = JPO.packArgs(mpBOMSubsSCN);
			Vector vEBOMSubSCN = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgIPMTablesJPO",
					"getSCNColumnData", argsSubsSCN);
			String strSCN = null;
			if ((vEBOMSubSCN != null) && (!vEBOMSubSCN.isEmpty()))
				strSCN = (String) vEBOMSubSCN.get(0);
			if (UIUtil.isNotNullAndNotEmpty(strSCN)) {
				strSCN = StringHelper.filterLessAndGreaterThanSign(strSCN);
			} else {
				strSCN = DomainConstants.EMPTY_STRING;
			}
			String strTitle = (String) mapSub.get("SAPDesc");
			if (UIUtil.isNullOrEmpty(strTitle)) {
				strTitle = DomainConstants.EMPTY_STRING;
			}
			strTitle = StringHelper.filterLessAndGreaterThanSign(strTitle);
			substitute.setSubstitutionCombinationNumberTitle(StringHelper.validateString1(strTitle));

			String strType2 = (String) mpBomSub.get(DomainConstants.SELECT_TYPE);
			String strValue = UINavigatorUtil.getAdminI18NString("Type", strType2, context.getSession().getLanguage());
			Map mpObjNameSST = new HashMap();
			mpObjNameSST.put("id", strSubId);
			MapList mlObjListNameSST = new MapList();
			mlObjListNameSST.add(mpObjNameSST);
			Map mpSettingsSub = new HashMap();
			mpSettingsSub.put("Column Name", "");
			Map mpColumnMap = new HashMap();
			mpColumnMap.put("settings", mpSettingsSub);
			Map mpBOMSubsSST = new HashMap();
			mpBOMSubsSST.put("objectList", mlObjListNameSST);
			mpBOMSubsSST.put("columnMap", mpColumnMap);
			String[] argsSubsSST = JPO.packArgs(mpBOMSubsSST);
			Vector vEBOMSubSpecSubType = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart",
					"getSpecificationSubtype", argsSubsSST);
			String strSpecSubType = null;
			if ((vEBOMSubSpecSubType != null) && (vEBOMSubSpecSubType.size() > 0))
				strSpecSubType = (String) vEBOMSubSpecSubType.get(0);

			substitute.setType(StringHelper.validateString1(strValue));
			substitute.setSpecificationSubType(StringHelper.validateString1(strSpecSubType));

			MapList mlObjListQty = new MapList();
			Map mpObjNameQty = new HashMap();
			mpObjNameQty.put("Quantity", strSubQty);
			mpObjNameQty.put("EBOMSubstituteRelId", strEBOMSubstituteRelId);
			mlObjListQty.add(mpObjNameQty);
			Map mpBOMSubsQty = new HashMap();
			mpBOMSubsQty.put("objectList", mlObjListQty);
			String[] argsSubsQty = JPO.packArgs(mpBOMSubsQty);
			Vector vEBOMSubQty = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgIPMTablesJPO",
					"getSubstitutePartQuantityColumnData", argsSubsQty);
			String strQty = null;
			if ((vEBOMSubQty != null) && (vEBOMSubQty.size() > 0))
				strQty = (String) vEBOMSubQty.get(0);

			substitute.setQty(StringHelper.validateString1(strQty));

			MapList mlObjListUom = new MapList();
			Map mpObjNameUoM = new HashMap();
			mpObjNameUoM.put("pgOriginatingSource", strOriginatingSource);
			mpObjNameUoM.put("pgBaseUnitOfMeasurePickList", strBaseUnitOfMeasure);
			mpObjNameUoM.put("pgBaseUnitOfMeasure", strBaseUnitOfMeasure);
			mlObjListUom.add(mpObjNameUoM);
			Map mpBOMSubsUoM = new HashMap();
			mpBOMSubsUoM.put("objectList", mlObjListUom);
			String[] argsSubsUoM = JPO.packArgs(mpBOMSubsUoM);
			Vector vEBOMSubUoM = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgDSOCPNProductData",
					"getSubstitutePartBaseUnitOfMeasureColumnData", argsSubsUoM);
			String strUoM = null;
			if ((vEBOMSubUoM != null) && (vEBOMSubUoM.size() > 0))
				strUoM = (String) vEBOMSubUoM.get(0);
			substitute.setBaseUnitofMeasure(StringHelper.validateString1(strUoM));

			MapList mlObjListValidStart = new MapList();
			Map mpObjNameValidStart = new HashMap();
			mpObjNameValidStart.put("EBOMSubstituteRelId", strEBOMSubstituteRelId);
			mlObjListValidStart.add(mpObjNameValidStart);
			Map mpBOMSubsValidStart = new HashMap();
			mpBOMSubsValidStart.put("objectList", mlObjListValidStart);
			String[] argsSubsValidStart = JPO.packArgs(mpBOMSubsValidStart);
			Vector vEBOMSubValidStart = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgDSOCPNProductData",
					"getValidStartDateColumnDataForDSO", argsSubsValidStart);
			String strValidStart = null;
			if ((vEBOMSubValidStart != null) && (vEBOMSubValidStart.size() > 0))
				strValidStart = (String) vEBOMSubValidStart.get(0);
			if (UIUtil.isNotNullAndNotEmpty(strValidStart))
				strValidStart = calenderHelper.getFormattedDate(strValidStart);

			substitute.setValidStartDate(StringHelper.validateString1(strValidStart));

			MapList mlObjListValidUntil = new MapList();
			Map mpObjNameValidUntil = new HashMap();
			mpObjNameValidUntil.put("ValidUntil", strSubValidUntil);
			mpObjNameValidUntil.put("EBOMSubstituteRelId", strEBOMSubstituteRelId);
			mlObjListValidUntil.add(mpObjNameValidUntil);
			Map mpBOMSubsValidUntil = new HashMap();
			mpBOMSubsValidUntil.put("objectList", mlObjListValidUntil);
			String[] argsSubsValidUntil = JPO.packArgs(mpBOMSubsValidUntil);
			Vector vEBOMSubValidUntil = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgDSOCPNProductData",
					"getValidUntilDateColumnDataForDSO", argsSubsValidUntil);
			String strValidUntil = null;
			if ((vEBOMSubValidUntil != null) && (vEBOMSubValidUntil.size() > 0))
				strValidUntil = (String) vEBOMSubValidUntil.get(0);
			if (UIUtil.isNotNullAndNotEmpty(strValidUntil))
				strValidUntil = calenderHelper.getFormattedDate(strValidUntil);

			substitute.setValidUntilDate(StringHelper.validateString1(strValidUntil));

			MapList mlObjListRefDes = new MapList();
			Map mpObjNameRefDes = new HashMap();
			mpObjNameRefDes.put("Reference Designator", strSubReferenceDesignator);
			mlObjListRefDes.add(mpObjNameRefDes);
			Map mpBOMSubsRefDes = new HashMap();
			mpBOMSubsRefDes.put("objectList", mlObjListRefDes);
			String[] argsSubsRefDes = JPO.packArgs(mpBOMSubsRefDes);
			Vector vEBOMSubRefDes = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgDSOCPNProductData",
					"getSubstitutePartReferenceDesignatorColumnData", argsSubsRefDes);
			String strRefDes = null;
			if ((vEBOMSubRefDes != null) && (vEBOMSubRefDes.size() > 0))
				strRefDes = (String) vEBOMSubRefDes.get(0);
			if (UIUtil.isNotNullAndNotEmpty(strRefDes)) {
				strRefDes = StringHelper.filterLessAndGreaterThanSign(strRefDes);
			} else {
				strRefDes = DomainConstants.EMPTY_STRING;
			}
			substitute.setRefDesOptional(StringHelper.validateString1(strRefDes));
			substitute.setComponents(StringHelper.validateString1(strOptComponent));
			MapList mlObjListComments = new MapList();
			Map mpObjNameComments = new HashMap();
			mpObjNameComments.put("Comment", strSubComments);
			mlObjListComments.add(mpObjNameComments);
			Map mpBOMSubsComments = new HashMap();
			mpBOMSubsComments.put("objectList", mlObjListComments);
			String[] argsSubsComments = JPO.packArgs(mpBOMSubsComments);
			Vector vEBOMSubComments = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgIPMTablesJPO",
					"getSubstitutePartCommentsColumnData", argsSubsComments);
			String strComment = null;
			if ((vEBOMSubComments != null) && (vEBOMSubComments.size() > 0))
				strComment = (String) vEBOMSubComments.get(0);
			if (UIUtil.isNotNullAndNotEmpty(strComment)) {
				strComment = StringHelper.filterLessAndGreaterThanSign(strComment);
			} else {
				strComment = DomainConstants.EMPTY_STRING;
			}
			substitute.setComments(StringHelper.validateString1(strComment));

			substitute.setRevision(StringHelper.validateString1(strRev));
			substitute.setReleaseDate(StringHelper.validateString1(strReleaseDate));

			DomainObject dmoBom = DomainObject.newInstance(context, strId);
			ContextUtil.pushContext(context);
			Map mpBom = dmoBom.getInfo(context, selectStmtSub);
			ContextUtil.popContext(context);
			String strBomName = (String) mpBom.get(DomainConstants.SELECT_NAME);
			String strBomRev = (String) mpBom.get(DomainConstants.SELECT_REVISION);

			substitute.setSubstituteFor(StringHelper.validateString1(strBomName));
			substitute.setSubstituteForRev(StringHelper.validateString1(strBomRev));

			String strBomTitle = (String) mpBom.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			if (UIUtil.isNullOrEmpty(strBomTitle)) {
				strBomTitle = DomainConstants.EMPTY_STRING;
			}
			strBomTitle = StringHelper.filterLessAndGreaterThanSign(strBomTitle);

			substitute.setTitle(StringHelper.validateString1(strBomTitle));

		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out
				.println("Total Time has taken by the getSubstituteTablewithData Method is-->" + (endTime - startTime));
		return substitute;
	}

	/**
	 * Retrieve Parent data in EBOM hierarchy for FPP
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public ArrayList getParentEBOMsWOIntermediateObjects(Context context, String strObjectId, ArrayList alEBOMParents,
			String strLevel) throws Exception {
		long startTime = new Date().getTime();
		MapList mlConnectedEBOMParents = new MapList();
		try {
			StringList busSelects = new StringList(1);
			StringList relSelects = new StringList(1);
			busSelects.addElement(DomainConstants.SELECT_ID);
			relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			String strParentType = domObject.getInfo(context, DomainConstants.SELECT_TYPE);
			mlConnectedEBOMParents = domObject.getRelatedObjects(context, // Context
					DomainConstants.RELATIONSHIP_EBOM, // relPattern
					pgV3Constants.SYMBOL_STAR, // typePattern
					busSelects, // objectSelects
					relSelects, // relationshipSelects
					false, // getTo - Get Parent Data
					true, // getFrom - Get Child Data
					(short) 1, // recurseToLevel
					DomainConstants.EMPTY_STRING, // objectWhere
					DomainConstants.EMPTY_STRING); // relationshipWhere

			Map mpConnectedEBOMParents = null;
			String strParentId = DomainConstants.EMPTY_STRING;
			String strRelId = DomainConstants.EMPTY_STRING;
			String strBomLeveL = DomainConstants.EMPTY_STRING;
			if (mlConnectedEBOMParents != null && !mlConnectedEBOMParents.isEmpty()) {
				int mlConnectedEBOMParentsSize = mlConnectedEBOMParents.size();
				for (int j = 0; j < mlConnectedEBOMParentsSize; j++) {
					mpConnectedEBOMParents = (Map) mlConnectedEBOMParents.get(j);
					strParentId = (String) mpConnectedEBOMParents.get(DomainConstants.SELECT_ID);
					strRelId = (String) mpConnectedEBOMParents.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					strBomLeveL = (String) mpConnectedEBOMParents.get(DomainConstants.SELECT_LEVEL);
					if ("First".equals(strLevel)) {
						alEBOMParents.add("MAIN" + pgV3Constants.DUMP_CHARACTER + strParentId
								+ pgV3Constants.DUMP_CHARACTER + strRelId + pgV3Constants.DUMP_CHARACTER + strBomLeveL);
						alEBOMParents = getParentEBOMsWOIntermediateObjects(context, strParentId, alEBOMParents,
								"NotFirst");
					} else {
						if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(strParentType)) {
							alEBOMParents.add(
									"COP" + pgV3Constants.DUMP_CHARACTER + strParentId + pgV3Constants.DUMP_CHARACTER
											+ strRelId + pgV3Constants.DUMP_CHARACTER + strBomLeveL);

						} else if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equals(strParentType)) {
							alEBOMParents.add(
									"CUP" + pgV3Constants.DUMP_CHARACTER + strParentId + pgV3Constants.DUMP_CHARACTER
											+ strRelId + pgV3Constants.DUMP_CHARACTER + strBomLeveL);
							alEBOMParents = getParentEBOMsWOIntermediateObjects(context, strParentId, alEBOMParents,
									"NotFirst");
						} else if (pgV3Constants.TYPE_PGINNERPACKUNITPART.equals(strParentType)) {
							alEBOMParents.add(
									"IP" + pgV3Constants.DUMP_CHARACTER + strParentId + pgV3Constants.DUMP_CHARACTER
											+ strRelId + pgV3Constants.DUMP_CHARACTER + strBomLeveL);
							alEBOMParents = getParentEBOMsWOIntermediateObjects(context, strParentId, alEBOMParents,
									"NotFirst");
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println(
				"Total Time has taken by the getParentEBOMsWOIntermediateObjects Method is-->" + (endTime - startTime));
		return alEBOMParents;
	}

	/**
	 * This Provate method will be executed to retrieve BOM Relationship Data
	 * 
	 * @param context    the eMatrix <code>Context</code> object
	 * @param StringList slMainEBOM holds the ID's
	 * @return MapList mlMain contains the Relationship data
	 * @throws Exception if the operation fails
	 */
	private MapList getBOMRelData(Context context, StringList slMainEBOM) {
		long startTime = new Date().getTime();
		MapList mlMain = new MapList();
		try {
			String strParentId = null;
			String strRelId = null;
			String strlevel = null;
			String strToId = null;
			String strpgChange = null;
			String strFindNumber = null;
			String strRefDesig = null;
			String strOptComponents = null;
			String strParentType = null;
			String strMainData = null;

			String strNet = DomainConstants.EMPTY_STRING;
			String strLoss = DomainConstants.EMPTY_STRING;
			String strNetWeight = DomainConstants.EMPTY_STRING;
			String strNetWeightUOM = DomainConstants.EMPTY_STRING;
			String strMinimum = DomainConstants.EMPTY_STRING;
			String strMaximum = DomainConstants.EMPTY_STRING;
			String strGrossWeight = DomainConstants.EMPTY_STRING;

			MapList mlToId = null;
			int mlToIdSize = 0;
			Map mData = new HashMap();
			Map mpBom = new HashMap();
			DomainObject dmoBom = null;
			StringList selectStmtsRel = new StringList(12);
			selectStmtsRel.add(DomainRelationship.SELECT_TO_ID);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGOPTIONALCOMPONENT);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHT);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTUOM);
			selectStmtsRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHT);

			StringList selectStmt = new StringList(9);
			selectStmt.add(DomainConstants.SELECT_NAME);
			selectStmt.add(DomainConstants.SELECT_TYPE);
			selectStmt.add(DomainConstants.SELECT_ID);
			selectStmt.add(DomainConstants.SELECT_POLICY);
			selectStmt.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			selectStmt.add(DomainConstants.SELECT_CURRENT);
			selectStmt.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			selectStmt.add(DomainConstants.SELECT_REVISION);
			selectStmt.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
			int islMainEBOMSize = slMainEBOM.size();
			String[] relargs = new String[1];
			StringTokenizer stMainData = null;
			for (int j = 0; j < islMainEBOMSize; j++) {
				strMainData = (String) slMainEBOM.get(j);
				stMainData = new StringTokenizer(strMainData, pgV3Constants.DUMP_CHARACTER);
				if (stMainData.hasMoreTokens()) {
					strParentId = (String) stMainData.nextToken();
					strParentType = (String) stMainData.nextToken();
					strRelId = (String) stMainData.nextToken();
					strlevel = (String) stMainData.nextToken();
					
					relargs[0] = strRelId;
					mlToId = new MapList();
					mlToId = (MapList) DomainRelationship.getInfo(context, relargs, selectStmtsRel);
					if (null != mlToId && !mlToId.isEmpty()) {
						mlToIdSize = mlToId.size();
						for (int i = 0; i < mlToIdSize; i++) {
							mData = (Map) mlToId.get(i);
							strToId = (String) mData.get(DomainRelationship.SELECT_TO_ID);
							strpgChange = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
							strFindNumber = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
							strRefDesig = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
							strOptComponents = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGOPTIONALCOMPONENT);
							strNet = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
							strLoss = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
							strNetWeight = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHT);
							strNetWeightUOM = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTUOM);
							strMinimum = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
							strMaximum = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
							strGrossWeight = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHT);
						}
					}
					dmoBom = DomainObject.newInstance(context, strToId);
					mpBom = new HashMap();
					ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
							DomainConstants.EMPTY_STRING);
					mpBom = dmoBom.getInfo(context, selectStmt);
					ContextUtil.popContext(context);
					mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE, strpgChange);
					mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, strFindNumber);
					mpBom.put(PDFConstant.ID_CONNECTION, strRelId);
					mpBom.put(DomainConstants.SELECT_LEVEL, strlevel);
					mpBom.put("ParentId", strParentId);
					mpBom.put("RefDesig", strRefDesig);
					mpBom.put("ParentType", strParentType);
					mpBom.put("Type", strOptComponents);
					mpBom.put("OptComp", strOptComponents);
					mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_TOTAL, strNet);
					mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_LOSS, strLoss);
					mpBom.put("NetWeight", strNetWeight);
					mpBom.put("NetWeightUOM", strNetWeightUOM);
					mpBom.put("strMaximum", strMaximum);
					mpBom.put("strMinimum", strMinimum);
					mpBom.put(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHT, strGrossWeight);
					mlMain.add(mpBom);
					mlMain.addSortKey("attribute[Find Number]", "ascending", "integer");
					mlMain.sort();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the getBOMRelData Method is-->" + (endTime - startTime));
		return mlMain;
	}

	/**
	 * Get substitute data
	 * 
	 * @param Map mapFPP
	 * @return MapList mlSub
	 * @throws Exception
	 */
	private MapList getSubstituteData(Context context, Map mapFPP) throws Exception {
		long startTime = new Date().getTime();
		MapList mlSub = new MapList();
		boolean isPushContext = false;
		try {
			String stridConn = (String) mapFPP.get(PDFConstant.ID_CONNECTION);
			String strLevel = (String) mapFPP.get(DomainConstants.SELECT_LEVEL);
			String strParentId = (String) mapFPP.get("ParentId");
			String strid = strParentId;
			Map mpParam = new HashMap();
			mpParam.put("reportFormat", "pdf");
			mpParam.put("objectId", strid);
			MapList mlObjList = new MapList();
			Map mpObj = new HashMap();
			mpObj.put(PDFConstant.ID_CONNECTION, stridConn);
			mpObj.put(DomainConstants.SELECT_ID, strid);
			mpObj.put(DomainConstants.SELECT_LEVEL, strLevel);
			mlObjList.add(mpObj);
			Map mpBOMSubs = new HashMap();
			mpBOMSubs.put("paramList", mpParam);
			mpBOMSubs.put("objectList", mlObjList);
			String[] argsSubs = JPO.packArgs(mpBOMSubs);
			Vector vEBOMSub = (Vector) PDFPOCHelper.executeMainClassMethod(context, "emxPart",
					"getColumnSubstitutePartsFlagData", argsSubs);
			String strSub = null;
			if ((vEBOMSub != null) && (!vEBOMSub.isEmpty()))
				strSub = (String) vEBOMSub.get(0);
			if (pgV3Constants.KEY_YES.equalsIgnoreCase(strSub)) {
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
				isPushContext = true;
				Map mpObjSl = new HashMap();
				mpObjSl.put("relId", stridConn);
				String[] argSubs = JPO.packArgs(mpObjSl);
				mlSub = (MapList) PDFPOCHelper.executeMainClassMethod(context, "emxPart",
						"getTableEBOMPartsSubstituteList", argSubs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isPushContext) {
				ContextUtil.popContext(context);
				isPushContext = false;
			}

		}
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the getSubstituteData Method is-->" + (endTime - startTime));
		return mlSub;
	}

	/**
	 * This method is used to get connected Master Type for DSO objects
	 * 
	 * @param Context - context
	 * @param String  - Object Id
	 * @return ArrayList - Connected Master objects
	 * @throws Exception
	 */
	public static ArrayList getMasterObjectsForDSO(Context context, String strObjectId) throws Exception {
		long startTime = new Date().getTime();
		ArrayList alMaster = null;
		String strConnectedIntermediateName = null;
		StringList slConnectedIntermediateIds = null;
		try {
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			StringList slRelSels = new StringList(1);
			slRelSels.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			MapList mlBusObjectData = domObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM, // Rel
																														// Pattern
					pgV3Constants.TYPE_PARTFAMILY, // Type Pattern
					null, // Object select
					slRelSels, // Rel select
					true, // to direction
					false, // from direction
					(short) 1, // recursion level
					null, // object where clause
					null); // rel where clause
			if ((null != mlBusObjectData) && (!mlBusObjectData.isEmpty())) {
				String strConnectionId = (String) ((Map) mlBusObjectData.get(0))
						.get(DomainConstants.SELECT_RELATIONSHIP_ID);
				String strMQLStmt = "print connection $1 select $2 dump $3;";
				String slSelectable = "frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id";
				if (strConnectionId != null && !"".equals(strConnectionId)) {
					strConnectedIntermediateName = MqlUtil.mqlCommand(context, strMQLStmt, strConnectionId,
							slSelectable, pgV3Constants.SYMBOL_PIPE);
				}
				if ((UIUtil.isNotNullAndNotEmpty(strConnectedIntermediateName))) {
					slConnectedIntermediateIds = FrameworkUtil.split(strConnectedIntermediateName,
							pgV3Constants.SYMBOL_PIPE);
				}
				if (slConnectedIntermediateIds != null && !slConnectedIntermediateIds.isEmpty()) {
					int iSize = slConnectedIntermediateIds.size();
					alMaster = new ArrayList(iSize);
					for (int i = 0; i < iSize; i++) {
						alMaster.add((String) slConnectedIntermediateIds.get(i));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the getMasterObjectsForDSO Method is-->" + (endTime - startTime));
		return alMaster;
	}

	/**
	 * Retrieve material function of the object
	 * @param context
	 * @param strRelId
	 * @return String -material function
	 * @throws Exception
	 */
	public String getMaterialFunction(Context context, String strRelId) throws Exception {
		String sApplication = DomainConstants.EMPTY_STRING;
		String sMatFun = DomainConstants.EMPTY_STRING;
		StringList sAppList = null;
		StringList sAppMatIds = null;
		String[] sValueArray = null;
		MapList mApplications = null;
		Map objMap = null;
		Map mData = null;
		StringBuilder strBuilder = null;
		StringList selectList = new StringList(1);
		selectList.add(DomainConstants.SELECT_NAME);
		try {
			ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
				DomainRelationship domainRelationship = DomainRelationship.newInstance(context, strRelId);
				sApplication = domainRelationship.getAttributeValue(context, pgV3Constants.ATTRIBUTE_APPLICATION);
				if (UIUtil.isNotNullAndNotEmpty(sApplication)) {
					sAppList = new StringList(FrameworkUtil.split(sApplication, pgV3Constants.SYMBOL_COMMA));
					sAppMatIds = new StringList();
					Iterator iAppItr = sAppList.iterator();
					while (iAppItr.hasNext()) {
						sAppMatIds.add(((String) iAppItr.next()).trim());
					}
					sValueArray = (String[]) sAppMatIds.toArray(new String[] {});
					mApplications = DomainObject.getInfo(context, sValueArray, selectList);
					strBuilder = new StringBuilder();
					Iterator it = mApplications.iterator();
					while (it.hasNext()) {
						objMap = (Map) it.next();
						strBuilder.append(objMap.get(DomainConstants.SELECT_NAME));
						if (it.hasNext())
							strBuilder.append(pgV3Constants.SYMBOL_COMMA);
					}
					sMatFun = strBuilder.toString();
				}
			}
		} catch (Exception exception) {
			throw exception;
		} finally {
			ContextUtil.popContext(context);
		}
		return sMatFun;
	}
}
