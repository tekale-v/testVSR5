package com.pdfview.helper;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.i18nNow;
import com.pdfview.impl.FPP.PackingUnit;
import com.pdfview.impl.FPP.PackingUnits;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetPackingUnit {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetPackingUnit(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public PackingUnits getComponent() {

		PackingUnits pUnits = new PackingUnits();
		List<PackingUnit> lsTpUnit = pUnits.getPackingUnit();

		int inValue = 0;
		int p = 0;
		Map Argmap = new HashMap();
		Map mapObject = null;
		Map commomArgs = null;
		Map argmapNCUPU = null;

		Map columnMapGTIN = null;
		Map settingMapGTIN = null;
		Map argmapGTIN = null;
		Map mapCI = null;

		Map mapWeightAttributeInfo = null;
		MapList commomML1 = null;
		MapList mlCI = null;
		MapList commomML = null;
		MapList mlBOM = null;
		HashMap paramListGTIN = null;

		String sGTIN = DomainConstants.EMPTY_STRING;

		String strId = DomainConstants.EMPTY_STRING;
		String strRelId = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strName = DomainConstants.EMPTY_STRING;
		String strLevel = DomainConstants.EMPTY_STRING;
		String strAttrQty = DomainConstants.EMPTY_STRING;
		String strDepth = DomainConstants.EMPTY_STRING;
		String strWidth = DomainConstants.EMPTY_STRING;
		String strHeight = DomainConstants.EMPTY_STRING;
		String strNoOfCuUnit = DomainConstants.EMPTY_STRING;
		String strDM = DomainConstants.EMPTY_STRING;
		String strNWPCU = DomainConstants.EMPTY_STRING;
		String strNetWetUoM = DomainConstants.EMPTY_STRING;
		String strGrossWgt = DomainConstants.EMPTY_STRING;
		String strGrossWth = DomainConstants.EMPTY_STRING;
		String strCIId = DomainConstants.EMPTY_STRING;
		String sRelPartFamilyReference = DomainConstants.EMPTY_STRING;
		String sRelClassifiedItem = DomainConstants.EMPTY_STRING;
		StringList mlPUT = new StringList();
		StringList slRelSelects = null;
		StringList slUOM = null;
		StringList strPGDUoM = null;
		int mlCIsize = 0;
		StringList UOM = new StringList();

		StringList slCI = new StringList();
		StringList mlAUOM = new StringList();
		StringList mlDM = new StringList();
		StringList mlNCUPU = new StringList();
		StringList busSelect = new StringList(1);
		busSelect.add(DomainConstants.SELECT_NAME);
		StringList relSelect = new StringList(2);
		relSelect.add(DomainRelationship.SELECT_NAME);
		relSelect.add(DomainRelationship.SELECT_ID);
		StringList slWeigth = new StringList(4);
		slWeigth.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTUNITOFMEASURE);
		slWeigth.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNITUOM);
		slWeigth.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTREAL);
		slWeigth.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNITREAL);
		Argmap.put("objectId", _OID);
		String[] argsFPP = JPO.packArgs(Argmap);
		try {
			mlBOM = new MapList();
			mlBOM = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "pgVPDFinishedProductPart",
					"getPackingUnitWeightsAndDiamensionsData", argsFPP);
			
			int mlBOMsize = mlBOM.size();
			if (mlBOM != null && mlBOMsize > 0) {
				commomML1 = new MapList();
				for (int i = 0; i < mlBOMsize; i++) {
					mapObject = new HashMap();
					mapObject = (Map) mlBOM.get(i);
					strId = (String) mapObject.get(DomainConstants.SELECT_ID);
					strType = (String) mapObject.get(DomainConstants.SELECT_TYPE);
					strName = (String) mapObject.get(DomainConstants.SELECT_NAME);
					strLevel = (String) mapObject.get(DomainConstants.SELECT_LEVEL);
					strAttrQty = (String) mapObject.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
					commomArgs = new HashMap();
					commomArgs.put(DomainConstants.SELECT_ID, strId);
					commomArgs.put(DomainConstants.SELECT_TYPE, strType);
					commomArgs.put(DomainConstants.SELECT_NAME, strName);
					commomArgs.put(DomainConstants.SELECT_LEVEL, strLevel);
					commomArgs.put(DomainConstants.SELECT_ATTRIBUTE_QUANTITY, strAttrQty);
					commomML1.add(commomArgs);
				}
				argmapNCUPU = new HashMap();
				argmapNCUPU.put("objectList", commomML1);
				String[] argsFPPNCUPU = JPO.packArgs(argmapNCUPU);
				mlNCUPU = (StringList) PDFPOCHelper.executeMainClassMethod(_context, "pgVPDFinishedProductPart",
						"getNumberPerCustomerUnit", argsFPPNCUPU);
				inValue = mlNCUPU.size();
				String[] argsFPPPUT = null;
				String[] argsFPPAUOM = null;
				String[] argsFPPGTIN = null;
				String[] argsFPPDM = null;
				Vector vGTIN = null;
				Hashtable htRelData = null;
				DomainObject domainObj= null;
				DomainObject bomObject = null;
				DomainRelationship domRel = null;
				StringTokenizer stDM = null;
				int ilCIsize=0;
				int ilUOMsize=0;
				for (int i = 0; i < mlBOMsize; i++) {
					PackingUnit pUnit = new PackingUnit();
					mapObject = new HashMap();
					mapObject = (Map) mlBOM.get(i);
					strId = (String) mapObject.get(DomainConstants.SELECT_ID);
					strType = (String) mapObject.get(DomainConstants.SELECT_TYPE);
					strName = (String) mapObject.get(DomainConstants.SELECT_NAME);
					strLevel = (String) mapObject.get(DomainConstants.SELECT_LEVEL);
					strAttrQty = (String) mapObject.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
					commomArgs = new HashMap();
					commomArgs.put(DomainConstants.SELECT_ID, strId);
					commomArgs.put(DomainConstants.SELECT_TYPE, strType);
					commomArgs.put(DomainConstants.SELECT_NAME, strName);
					commomArgs.put(DomainConstants.SELECT_LEVEL, strLevel);
					commomArgs.put(DomainConstants.SELECT_ATTRIBUTE_QUANTITY, strAttrQty);
					commomML = new MapList();
					commomML.add(commomArgs);
					argsFPPPUT = getArgsMapData(commomML, "name");

					mlPUT = (StringList) PDFPOCHelper.executeMainClassMethod(_context, "pgVPDFinishedProductPart",
							"getCalculationsAndRollupColumnVal", argsFPPPUT);
					domainObj= DomainObject.newInstance(_context,strId);
					mapWeightAttributeInfo = new HashMap();
					mapWeightAttributeInfo = domainObj.getInfo(_context, slWeigth);
					argsFPPAUOM = getArgsMapData(commomML, "attribute_pgAlternateUnitOfMeasure");
					mlAUOM = (StringList) PDFPOCHelper.executeMainClassMethod(_context, "pgVPDFinishedProductPart",
							"getAlternateUnitOfMeasure", argsFPPAUOM);
					argmapGTIN = new HashMap();
					argmapGTIN.put("objectList", commomML);
					paramListGTIN = new HashMap();
					paramListGTIN.put("languageStr", "Stren");
					paramListGTIN.put("objectId", _OID);
					paramListGTIN.put("table", "pgVPDPackingUnitWeightAndDimensionsTable");
					argmapGTIN.put("paramList", paramListGTIN);
					columnMapGTIN = new HashMap();
					settingMapGTIN = new HashMap();
					settingMapGTIN.put("CPNFieldType", "attribute_pgGTIN");
					columnMapGTIN.put("settings", settingMapGTIN);
					argmapGTIN.put("columnMap", columnMapGTIN);
					argmapGTIN.put("reportFormat", "CSV");
					argsFPPGTIN = JPO.packArgs(argmapGTIN);
				
						vGTIN = (Vector) PDFPOCHelper.executeMainClassMethod(_context, "pgDSOCPNProductData",
								"getGTINValue", argsFPPGTIN);
					
					if (vGTIN!=null && !vGTIN.isEmpty()) {
						sGTIN = (String) vGTIN.get(0);
					}

					argsFPPDM = getArgsMapData(commomML, DomainConstants.EMPTY_STRING);
					mlDM = (StringList) PDFPOCHelper.executeMainClassMethod(_context, "pgVPDFinishedProductPart",
							"getPackagingDimensions", argsFPPDM);
					strDM = (String) mlDM.get(0);
					stDM = new StringTokenizer(strDM, pgV3Constants.SYMBOL_PIPE);

					if (stDM.hasMoreTokens()) {
						strDepth = (String) stDM.nextToken();
						strWidth = (String) stDM.nextToken();
						strHeight = (String) stDM.nextToken();
					}

					sRelPartFamilyReference = pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE;
					sRelClassifiedItem = pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM;
					slCI = new StringList();
					mlCI = new MapList();

					mlCI = domainObj.getRelatedObjects(_context, sRelClassifiedItem, pgV3Constants.SYMBOL_STAR, busSelect, relSelect, true,
							false, (short) 1, null, null, 0);

					if (mlCI != null && !mlCI.isEmpty()) {
						mlCIsize = mlCI.size();
						for (int intCount = 0; intCount < mlCIsize; intCount++) {
							mapCI = new HashMap();
							mapCI = (Map) mlCI.get(intCount);
							strRelId = (String) mapCI.get(DomainRelationship.SELECT_ID);
							slCI.add(strRelId);
						}
					}
					slRelSelects = new StringList();
					slRelSelects.add("frommid[" + sRelPartFamilyReference + "].torel.to.attribute["
							+ pgV3Constants.ATTRIBUTE_PGDIMENSIONUOM + "]");
					slUOM = new StringList();
					strPGDUoM = new StringList();
					if (slCI != null && !slCI.isEmpty()) {
						ilCIsize=slCI.size();
						for (int k = 0; k < ilCIsize; k++) {
							strCIId = (String) slCI.get(k);
							domRel = DomainRelationship.newInstance(_context, strCIId);
							htRelData = domRel.getRelationshipData(_context, slRelSelects);
							UOM = (StringList) htRelData.get("frommid[" + sRelPartFamilyReference
									+ "].torel.to.attribute[" + pgV3Constants.ATTRIBUTE_PGDIMENSIONUOM + "]");
							slUOM.addAll(UOM);
						}
					}
					if (slUOM != null && !slUOM.isEmpty()) {
						ilUOMsize=slUOM.size();
						for (int l = 0; l < ilUOMsize; l++) {
							strPGDUoM.add(slUOM.get(l));
						}
					}
					strGrossWgt = (String) mapWeightAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTREAL);

					strGrossWth = (String) mapWeightAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTUNITOFMEASURE);

					if (mlNCUPU != null && p < inValue) {
						strNoOfCuUnit = (String) mlNCUPU.get(p);
						p++;
					}
					strNWPCU = (String) mapWeightAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNITREAL);

					strNetWetUoM = (String) mapWeightAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNITUOM);

					pUnit.setPackingUnitType(StringHelper.removedAndNBSP(StringHelper.validateString1(mlPUT)));
					pUnit.setaUOM(StringHelper.validateString1(mlAUOM));
					pUnit.setgTIN(StringHelper.validateString1(sGTIN));
					pUnit.setDepth(StringHelper.validateString1(strDepth));
					pUnit.setWidth(StringHelper.validateString1(strWidth));
					pUnit.setHeight(StringHelper.validateString1(strHeight));
					pUnit.setDimensionUnitofMeasure(StringHelper.validateString1(strPGDUoM));
					pUnit.setGrossWeight(StringHelper.validateString1(strGrossWgt));
					pUnit.setUnitofMeasure(StringHelper.validateString1(strGrossWth));
					pUnit.setNumberofConsumerUnitsperUnit(StringHelper.validateString1(strNoOfCuUnit));
					pUnit.setNetWeightofProductinConsumerUnit(StringHelper.validateString1(strNWPCU));
					lsTpUnit.add(pUnit);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pUnits;
	}

	Map argmapPUT = new HashMap();
	HashMap paramList = new HashMap();
	Map columnMap = new HashMap();
	Map settingMap = new HashMap();

	public String[] getArgsMapData(MapList commomML, String CPNFieldTypeValue) {
		String[] argsData = null;
		try {
			argmapPUT.clear();
			paramList.clear();
			columnMap.clear();
			settingMap.clear();
			argmapPUT.put("objectList", commomML);
			paramList = new HashMap();
			paramList.put("languageStr", "Stren");
			argmapPUT.put("paramList", paramList);
			settingMap.put("CPNFieldType", CPNFieldTypeValue);
			columnMap.put("settings", settingMap);
			argmapPUT.put("columnMap", columnMap);
			argmapPUT.put("reportFormat", "CSV");
			argsData = JPO.packArgs(argmapPUT);
		} catch (Exception e) {
			System.out.println("Exception in getArgsMapData Method ! " + e.getMessage());
		}
		return argsData;
	}
}
