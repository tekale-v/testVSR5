package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.DerivedFromPartsData;
import com.pdfview.impl.FPP.DerivedParts;
import com.pdfview.impl.FPP.DerivedToPartsData;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetDerivedParts {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetDerivedParts(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public DerivedParts getComponent() {
		DerivedParts derivedParts = new DerivedParts();
		List<DerivedFromPartsData> lsDerivedFromParts = derivedParts.getDerivedFromPartsData();
		List<DerivedToPartsData> lsDeriveTOParts = derivedParts.getDerivedToPartsData();
		CalenderHelper cal = new CalenderHelper(_context);
		try {
			if (StringHelper.validateString(_OID)) {
				DomainObject domainObject = DomainObject.newInstance(_context, _OID);
				String strType = (String) domainObject.getInfo(_context, DomainConstants.SELECT_TYPE);
				Map Argmap = new HashMap();
				Argmap.put("objectId", _OID);
				Argmap.put("expandLevel", "1");
				String[] argsFPP = JPO.packArgs(Argmap);
				MapList mlDerivedFrom = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "emxCommonPart",
						"getDerivedFromParts", argsFPP);
				MapList mlDerivedTo = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "emxCommonPart",
						"getDerivedToParts", argsFPP);
				StringList slSelectables = new StringList(8);
				slSelectables.add(DomainConstants.SELECT_TYPE);
				slSelectables.add(DomainConstants.SELECT_NAME);
				slSelectables.add(DomainConstants.SELECT_REVISION);
				slSelectables.add(DomainConstants.SELECT_OWNER);
				slSelectables.add(DomainConstants.SELECT_ORIGINATED);
				slSelectables.add(DomainConstants.SELECT_DESCRIPTION);
				slSelectables.add(DomainConstants.SELECT_CURRENT);
				slSelectables.add(DomainConstants.SELECT_POLICY);
				StringList busSelect = new StringList(5);
				busSelect.add(DomainConstants.SELECT_NAME);
				busSelect.add(DomainConstants.SELECT_REVISION);
				busSelect.add(DomainConstants.SELECT_DESCRIPTION);
				busSelect.add(DomainConstants.SELECT_CURRENT);
				busSelect.add(DomainConstants.SELECT_ID);
				StringList relSelect = new StringList(1);
				relSelect.add(DomainRelationship.SELECT_NAME);
				Map mapObject = new HashMap();
				Map mpDerivedToData = new HashMap();
				Map mpdesigResData = new HashMap();
				String relPattern = DomainConstants.EMPTY_STRING;
				String strId = DomainConstants.EMPTY_STRING;
				String strName = DomainConstants.EMPTY_STRING;
				String strLevel = DomainConstants.EMPTY_STRING;
				String strRev = DomainConstants.EMPTY_STRING;
				String strDerivedObjDescr = DomainConstants.EMPTY_STRING;
				String strOwner = DomainConstants.EMPTY_STRING;
				String strOriginated = DomainConstants.EMPTY_STRING;
				String derivedObjDescription = DomainConstants.EMPTY_STRING;
				String derivedObjState = DomainConstants.EMPTY_STRING;
				String derivedObjPolicy = DomainConstants.EMPTY_STRING;
				String strObjectDisplay = DomainConstants.EMPTY_STRING;
				String strRelName = DomainConstants.EMPTY_STRING;
				Map mpObjLevel = new HashMap();
				MapList mlObjListLevel = new MapList();
				Map mpLevel = new HashMap();
				Map mpDerivedFromData = new HashMap();
				DomainObject derivedToObject = null;
				DomainObject derivedFromObject = null;
				boolean havingDerivedFromData = false;
				StringBuffer derivedObjName = new StringBuffer();
				StringList derivedObjId = new StringList();
				StringBuffer derivedObjRDO = new StringBuffer();
				StringBuffer sbRev = new StringBuffer();
				if (mlDerivedFrom != null && !mlDerivedFrom.isEmpty()) {
					int mlDerivedFromSize=mlDerivedFrom.size();
					for (int i = 0; i < mlDerivedFromSize; i++) {
						mapObject = (Map) mlDerivedFrom.get(i);
						strId = (String) mapObject.get("derivedId");
						if (StringHelper.validateString(strId)) {
							havingDerivedFromData = true;
							break;
						}
					}
				}
				boolean havingDerivedToData = false;
				if (mlDerivedTo != null && !mlDerivedTo.isEmpty()) {
					int mlDerivedToSize=mlDerivedTo.size();
					for (int i = 0; i < mlDerivedToSize; i++) {
						mapObject = (Map) mlDerivedTo.get(i);
						strId = (String) mapObject.get(DomainObject.SELECT_ID);
						if (StringHelper.validateString(strId)) {
							havingDerivedToData = true;
							break;
						}
					}
				}
				MapList tempml = new MapList();
				if (mlDerivedTo != null && !mlDerivedTo.isEmpty()) {
					int mlDerivedToSize=mlDerivedTo.size();
					for (int i = 0; i < mlDerivedToSize; i++) {
						mapObject = (Map) mlDerivedTo.get(i);
						strId = (String) mapObject.get(DomainConstants.SELECT_ID);
						if (StringHelper.validateString(strId)) {
							derivedToObject = DomainObject.newInstance(_context, strId);
							strName = derivedToObject.getInfo(_context, DomainConstants.SELECT_NAME);
							mapObject.put("fromName", strName);
							tempml.add(mapObject);
						}
					}
				}

				if (havingDerivedFromData) {
					relPattern = pgV3Constants.RELATIONSHIP_DERIVED;
					if (mlDerivedFrom != null && !mlDerivedFrom.isEmpty()) {
						int mlDerivedFromSize=mlDerivedFrom.size();
						int mlDerivedFromObjectsSize=0;
						MapList mlDerivedFromObjects =null;
						MapList mldesigResObjects =null;
						Map derivedMap =null;
						String[] argsLevel = null;
						Vector vLevel = null;
						int sLevel = 0;
						int mldesigResObjectssize=0;
						for (int i = 0; i < mlDerivedFromSize; i++) {
							DerivedFromPartsData fromDerivedPartsData = new DerivedFromPartsData();
							mapObject = (Map) mlDerivedFrom.get(i);
							strId = (String) mapObject.get(DomainConstants.SELECT_ID);
							strLevel = (String) mapObject.get(DomainConstants.SELECT_LEVEL);
							derivedObjName = new StringBuffer();
							derivedObjId = new StringList();
							derivedObjRDO = new StringBuffer();
							sbRev = new StringBuffer();
							if (StringHelper.validateString(strId)) {
								derivedFromObject = DomainObject.newInstance(_context, strId);
								mlDerivedFromObjects = derivedFromObject.getRelatedObjects(_context, // Context
										relPattern, // relPattern
										pgV3Constants.SYMBOL_STAR, // typePattern
										busSelect, // objectSelects
										null, // relationshipSelects
										true, // getTo - Get Parent Data
										false, // getFrom - Get Child Data
										(short) 1, // recurseToLevel
										null, // objectWhere
										null, // relationshipWhere
										0);
								if (mlDerivedFromObjects != null && !mlDerivedFromObjects.isEmpty()) {
									mlDerivedFromObjectsSize=mlDerivedFromObjects.size();
									for (int intCount = 0; intCount < mlDerivedFromObjectsSize; intCount++) {
										mpDerivedToData = (Map) mlDerivedFromObjects.get(intCount);
										strName = (String) mpDerivedToData.get(DomainConstants.SELECT_NAME);
										strRev = (String) mpDerivedToData.get(DomainConstants.SELECT_REVISION);
										strId = (String) mpDerivedToData.get(DomainConstants.SELECT_ID);
										derivedObjId.add(strId);
										derivedObjName.append(strName + "<br />");
										if (intCount == (mlDerivedFromObjects.size() - 1)) {
											sbRev.append(strRev);
										} else {
											sbRev.append(strRev);
											sbRev.append(pgV3Constants.SYMBOL_COMMA+pgV3Constants.SYMBOL_SPACE);
										}
									}
								}
								mldesigResObjects = derivedFromObject.getRelatedObjects(_context,
										pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION, pgV3Constants.SYMBOL_STAR, busSelect, relSelect,
										false, true, (short) 1, null, null, 0);
								if (mldesigResObjects != null && !mldesigResObjects.isEmpty()) {
									mldesigResObjectssize=mldesigResObjects.size();
									for (int intCount = 0; intCount < mldesigResObjectssize; intCount++) {
										mpdesigResData = (Map) mldesigResObjects.get(intCount);
										strName = (String) mpdesigResData.get(DomainConstants.SELECT_NAME);
										if (intCount == (mldesigResObjectssize - 1)) {
											derivedObjRDO.append(strName);
										} else {
											derivedObjRDO.append(strName);
											derivedObjRDO.append(pgV3Constants.SYMBOL_COMMA+pgV3Constants.SYMBOL_SPACE);
										}
									}
								}
								derivedMap = derivedFromObject.getInfo(_context, slSelectables);
								
								strName = (String) derivedMap.get(DomainConstants.SELECT_NAME);
								fromDerivedPartsData.setFromSourceName(StringHelper.validateString1(strName.toString()));
								fromDerivedPartsData.setFromDerivedName(StringHelper.validateString1(StringHelper.removeBRTags(derivedObjName.toString())));
								strDerivedObjDescr = (String) derivedMap.get(DomainConstants.SELECT_DESCRIPTION);
								if (UIUtil.isNullOrEmpty(strDerivedObjDescr)) {
									strDerivedObjDescr = DomainConstants.EMPTY_STRING;
								} else if (UIUtil.isNotNullAndNotEmpty(strDerivedObjDescr)) {
									strDerivedObjDescr = StringHelper.filterLessAndGreaterThanSign(strDerivedObjDescr);
								}
								fromDerivedPartsData.setFromDerivedDescription(StringHelper.validateString1(strDerivedObjDescr));
								derivedObjState = (String) derivedMap.get(DomainConstants.SELECT_CURRENT);
								derivedObjPolicy = (String) derivedMap.get(DomainConstants.SELECT_POLICY);
								strObjectDisplay = EnoviaResourceBundle.getStateI18NString(_context, derivedObjPolicy,
										derivedObjState, _context.getLocale().getLanguage());
								fromDerivedPartsData
										.setFromDerivedPartState(StringHelper.validateString1(strObjectDisplay));
								fromDerivedPartsData.setFromDerivedPartSecurityCategory(
										StringHelper.validateString1(derivedObjRDO.toString()));
								fromDerivedPartsData
										.setFromSourcePartRevision(StringHelper.validateString1(sbRev.toString()));
								mpObjLevel = new HashMap();
								mpObjLevel.put(DomainConstants.SELECT_LEVEL, strLevel);
								mlObjListLevel = new MapList();
								mlObjListLevel.add(mpObjLevel);
								mpLevel = new HashMap();
								mpLevel.put("objectList", mlObjListLevel);
								argsLevel = JPO.packArgs(mpLevel);
								vLevel = (Vector) PDFPOCHelper.executeMainClassMethod(_context, "emxCommonPart",
										"getLevel", argsLevel);
								sLevel=0;
								fromDerivedPartsData.setFromGenerationNumber(sLevel + "");
								strOwner = (String) derivedMap.get(DomainConstants.SELECT_OWNER);
								fromDerivedPartsData.setFromCreator(StringHelper.validateString1(strOwner));
								strOriginated = (String) derivedMap.get(DomainConstants.SELECT_ORIGINATED);
								strOriginated = cal.getFormattedDate(strOriginated);
								fromDerivedPartsData.setFromCreateDate(StringHelper.validateString1(strOriginated));
							}
							lsDerivedFromParts.add(fromDerivedPartsData);
						}
					}
				}

				if (havingDerivedToData) {

					relPattern = pgV3Constants.RELATIONSHIP_DERIVED;
					tempml.addSortKey("fromName", "ascending", "String");
					tempml.sort();
					if (tempml != null && !tempml.isEmpty()) {
						int tempmlSize=tempml.size();
						int mlDerivedToObjectsSize=0;
						MapList mlDerivedToObjects =null;
						MapList mlSecurityCategory =null;
						Map derivedMap =null;
						int mlSecurityCategorySize=0;
						String[] argsLevel = null;
						Vector vLevel = null;
						int sLevel = 0;
						for (int i = 0; i < tempmlSize; i++) {
							DerivedToPartsData toDerivedPartsData = new DerivedToPartsData();
							mapObject = (Map) tempml.get(i);
							strId = (String) mapObject.get(DomainConstants.SELECT_ID);
							strLevel = (String) mapObject.get(DomainConstants.SELECT_LEVEL);
							derivedObjName = new StringBuffer();
							derivedObjRDO = new StringBuffer();
							sbRev = new StringBuffer();
							if (StringHelper.validateString(strId)) {
								derivedToObject = DomainObject.newInstance(_context, strId);
								mlDerivedToObjects = derivedToObject.getRelatedObjects(_context, relPattern,
										pgV3Constants.SYMBOL_STAR, busSelect, relSelect, true, false, (short) 1, null, null, 0);
								if (mlDerivedToObjects != null && !mlDerivedToObjects.isEmpty()) {
									mlDerivedToObjectsSize=mlDerivedToObjects.size();
									for (int intCount = 0; intCount < mlDerivedToObjectsSize; intCount++) {
										mpDerivedFromData = (Map) mlDerivedToObjects.get(intCount);
										strRelName = (String) mpDerivedFromData.get(DomainRelationship.SELECT_NAME);
										strName = (String) mpDerivedFromData.get(DomainConstants.SELECT_NAME);
										strRev = (String) mpDerivedFromData.get(DomainConstants.SELECT_REVISION);
										if (intCount == (mlDerivedToObjectsSize - 1)) {
											sbRev.append(strRev);
											derivedObjName.append(strName);
										} else {
											sbRev.append(strRev);
											sbRev.append(pgV3Constants.SYMBOL_COMMA+pgV3Constants.SYMBOL_SPACE);
											derivedObjName.append(strName);
											derivedObjName.append(pgV3Constants.SYMBOL_SPACE);
										}
									}
								}
								
								mlSecurityCategory = derivedToObject.getRelatedObjects(_context,
										pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION, pgV3Constants.SYMBOL_STAR, busSelect, relSelect,
										false, true, (short) 1, null, null, 0);
								if (mlSecurityCategory != null && !mlSecurityCategory.isEmpty()) {
									mlSecurityCategorySize=mlSecurityCategory.size(); 
									for (int intCount = 0; intCount < mlSecurityCategorySize; intCount++) {
										mpDerivedFromData = (Map) mlSecurityCategory.get(intCount);
										strName = (String) mpDerivedFromData.get(DomainConstants.SELECT_NAME);
										derivedObjRDO.append(strName);
									}
								}
								derivedMap = derivedToObject.getInfo(_context, slSelectables);
								strName = (String) derivedMap.get(DomainConstants.SELECT_NAME);
								if(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strType)){
									toDerivedPartsData.setToDerivedName(StringHelper.validateString1(derivedObjName.toString()));
									toDerivedPartsData.setToSourceName(StringHelper.validateString1(strName));
								}
								else{
									toDerivedPartsData.setToDerivedName(StringHelper.validateString1(strName));
									toDerivedPartsData.setToSourceName(StringHelper.validateString1(derivedObjName.toString()));
								}
								derivedObjDescription = (String) derivedMap.get(DomainConstants.SELECT_DESCRIPTION);
								if (UIUtil.isNullOrEmpty(derivedObjDescription)) {
									derivedObjDescription = DomainConstants.EMPTY_STRING;
								} else if (UIUtil.isNotNullAndNotEmpty(derivedObjDescription)) {
									derivedObjDescription = StringHelper
											.filterLessAndGreaterThanSign(derivedObjDescription);
								}
								toDerivedPartsData
										.setToDerivedDescription(StringHelper.validateString1(derivedObjDescription));
								derivedObjState = (String) derivedMap.get(DomainConstants.SELECT_CURRENT);
								derivedObjPolicy = (String) derivedMap.get(DomainConstants.SELECT_POLICY);
								strObjectDisplay = EnoviaResourceBundle.getStateI18NString(_context, derivedObjPolicy,
										derivedObjState, _context.getLocale().getLanguage());
								toDerivedPartsData
										.setToDerivedPartState(StringHelper.validateString1(strObjectDisplay));
								toDerivedPartsData.setToDerivedPartSecurityCategory(derivedObjRDO.toString());
								toDerivedPartsData
										.setToSourcePartRevision(StringHelper.validateString1(sbRev.toString()));
								mpObjLevel = new HashMap();
								mpObjLevel.put(DomainConstants.SELECT_LEVEL, strLevel);
								mlObjListLevel = new MapList();
								mlObjListLevel.add(mpObjLevel);
								mpLevel = new HashMap();
								mpLevel.put("objectList", mlObjListLevel);
								argsLevel = JPO.packArgs(mpLevel);
								vLevel = (Vector) PDFPOCHelper.executeMainClassMethod(_context, "emxCommonPart",
										"getLevel", argsLevel);
								sLevel = 0;
								toDerivedPartsData.setToGenerationNumber(sLevel + "");
								strOwner = (String) derivedMap.get(DomainConstants.SELECT_OWNER);
								toDerivedPartsData.setToCreator(StringHelper.validateString1(strOwner));
								strOriginated = (String) derivedMap.get(DomainConstants.SELECT_ORIGINATED);
								strOriginated = cal.getFormattedDate(strOriginated);
								toDerivedPartsData.setToCreateDate(StringHelper.validateString1(strOriginated));
							}
							lsDeriveTOParts.add(toDerivedPartsData);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return derivedParts;
	}
}
