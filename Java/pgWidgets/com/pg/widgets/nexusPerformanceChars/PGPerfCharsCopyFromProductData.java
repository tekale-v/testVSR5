package com.pg.widgets.nexusPerformanceChars;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.widgets.structuredats.PGStructuredATSConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class PGPerfCharsCopyFromProductData {

	private static final Logger logger = Logger.getLogger(PGPerfCharsCopyFromProductData.class.getName());
	static final String STRING_STATUS = "status";

	public String copyCharacteristicsFromProductData(Context context, Map mpRequestMap) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		boolean bResult = false;
		String strReturnVal = null;
		String strobjectId = (String) mpRequestMap.get("objectId");
		String RowIds = (String) mpRequestMap.get("RowIds");
		String strFilter = (String) mpRequestMap.get("pgVPDCPNCharacteristicDerivedFilter");
		String strSelectedTable = (String) mpRequestMap.get("selectedTable");
		StringList slList = FrameworkUtil.split(RowIds, PGStructuredATSConstants.CONSTANT_STRING_PIPE);
		HashMap argsMap = new HashMap();
		argsMap.put("RowIds", slList);
		argsMap.put("objectId", strobjectId);
		argsMap.put("pgVPDCPNCharacteristicDerivedFilter", strFilter);
		argsMap.put("selectedTable", strSelectedTable);
		try {
			String strArgs[] = JPO.packArgs(argsMap);
			bResult = copyCharacteristicsFromProductData(context, strArgs);
			if (bResult) {
				output.add(PGPerfCharsConstants.KEY_STATUS, PGPerfCharsConstants.VALUE_SUCCESS);
				strReturnVal = output.build().toString();
			} else {
				output.add(PGPerfCharsConstants.KEY_STATUS, PGPerfCharsConstants.VALUE_FAIL);
				strReturnVal = output.build().toString();
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_COPYFROM_PROD_DATA, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return output.build().toString();
		}
		return strReturnVal;
	}

	/**
	 * This method is to Clone and Connect 'Performance Characteristic' objects from
	 * 'Product Data' objects
	 * 
	 * @param context
	 * @param args    : Program Args
	 * @return 'true' if process completes successfully
	 * @throws Exception
	 */
	public boolean copyCharacteristicsFromProductData(Context context, String args[]) throws Exception {
		boolean bResult = false;
		String strSelectCharFromPart = DomainConstants.EMPTY_STRING;
		String strSelectCharFromSharedTable = DomainConstants.EMPTY_STRING;
		String strSelectCharSequence = DomainConstants.EMPTY_STRING;
		String strSelectCharSequenceFromSharedTable = DomainConstants.EMPTY_STRING;
		try {
			ContextUtil.startTransaction(context, true);
			Map mProgramMap = JPO.unpackArgs(args);
			String strProdId = (String) mProgramMap.get("objectId");
			StringList slRowIds = (StringList) mProgramMap.get("RowIds");
			StringList slCharIds = new StringList();
			StringList slTMIds = new StringList();
			StringList slTMRDIds = new StringList();
			String strRefDocObjPrevState = DomainConstants.EMPTY_STRING;
			String REL_CHARACTERISTIC = PropertyUtil.getSchemaProperty(context, "relationship_Characteristic");
			String REL_SHARED_CHARACTERISTIC = PropertyUtil.getSchemaProperty(context,
					"relationship_SharedCharacteristic");
			if (UIUtil.isNotNullAndNotEmpty(strProdId)) {
				DomainObject dobjProd = DomainObject.newInstance(context, strProdId);
				Set setCharObjIds = new LinkedHashSet();
				strSelectCharFromPart = new StringBuilder("from[").append(REL_CHARACTERISTIC).append("].to.id")
						.toString();
				strSelectCharFromSharedTable = new StringBuilder("from[").append(REL_SHARED_CHARACTERISTIC)
						.append("].to.id").toString();
				strSelectCharSequence = new StringBuilder("from[").append(REL_CHARACTERISTIC)
						.append("]." + PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE)
						.toString();
				strSelectCharSequenceFromSharedTable = new StringBuilder("from[").append(REL_SHARED_CHARACTERISTIC)
						.append("]." + PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE)
						.toString();

				StringList slSelects = new StringList();
				slSelects.add(DomainConstants.SELECT_ID);
				slSelects.add(strSelectCharFromPart);
				slSelects.add(strSelectCharFromSharedTable);
				slSelects.add(strSelectCharSequence);
				slSelects.add(strSelectCharSequenceFromSharedTable);
				String strObjIds[] = (String[]) slRowIds.toArray(new String[slRowIds.size()]);

				StringList slMultiSelect = new StringList(4);
				slMultiSelect.add(strSelectCharFromPart);
				slMultiSelect.add(strSelectCharFromSharedTable);
				slMultiSelect.add(strSelectCharSequence);
				slMultiSelect.add(strSelectCharSequenceFromSharedTable);
				MapList mlCharInfo = DomainObject.getInfo(context, strObjIds, slSelects, slMultiSelect);
				slSelects.remove(DomainConstants.SELECT_ID);
				MapList mlProdCharInfo = DomainObject.getInfo(context, new String[] { strProdId }, slSelects,
						slMultiSelect);

				Map mCharacteristicInfo = null;
				String strObjectId = DomainConstants.EMPTY_STRING;
				DomainObject doObj = null;
				StringList slCharFromPart = new StringList();
				StringList slCharFromSharedTable = new StringList();
				StringList slCharSequence = new StringList();
				StringList slCharSequenceFromSharedTable = new StringList();

				for (int i = 0; i < mlCharInfo.size(); i++) {
					mCharacteristicInfo = (Map) mlCharInfo.get(i);
					strObjectId = (String) mCharacteristicInfo.get(DomainConstants.SELECT_ID);
					doObj = DomainObject.newInstance(context, strObjectId);
					slCharFromPart = (StringList) mCharacteristicInfo.get(strSelectCharFromPart);
					slCharFromSharedTable = (StringList) mCharacteristicInfo.get(strSelectCharFromSharedTable);
					slCharSequence = (StringList) mCharacteristicInfo.get(strSelectCharSequence);
					slCharSequenceFromSharedTable = (StringList) mCharacteristicInfo
							.get(strSelectCharSequenceFromSharedTable);
					if (doObj.isKindOf(context, DomainConstants.TYPE_PART) && slCharFromPart != null
							&& !slCharFromPart.isEmpty()) {

						if (((HashMap) mlProdCharInfo.get(0)).size() != 0) {
							slCharFromPart = sortCharacteristicsBySequence(slCharFromPart, slCharSequence);
						}
						setCharObjIds.addAll(slCharFromPart);
					} else if (doObj.isKindOf(context, PGPerfCharsConstants.TYPE_SHARED_TABLE)
							&& slCharFromSharedTable != null && !slCharFromSharedTable.isEmpty()) {
						if (((HashMap) mlProdCharInfo.get(0)).size() != 0) {
							slCharFromSharedTable = sortCharacteristicsBySequence(slCharFromSharedTable,
									slCharSequenceFromSharedTable);
						}
						setCharObjIds.addAll(slCharFromSharedTable);
					}
				}

				Iterator itrCharObjIds = setCharObjIds.iterator();
				StringList slObjSelects = new StringList();
				slObjSelects.add(DomainConstants.SELECT_ID);
				slObjSelects.add(DomainConstants.SELECT_LAST_ID);
				slObjSelects.add(PGPerfCharsConstants.SELECT_LAST_CURRENT);
				slObjSelects.add(PGPerfCharsConstants.SELECT_LAST_PREVIOUS_ID);
				slObjSelects.add(PGPerfCharsConstants.SELECT_LAST_PREVIOUS_CURRENT);
				String strCharObjId = "";
				String sPCAutoName = "";
				String strRefDocObjId = "";
				String strRefDocObjCurrent = DomainConstants.EMPTY_STRING;
				String strType = "";
				BusinessObject boNewCharacteric = new BusinessObject();
				DomainObject doCharateristic = DomainObject.newInstance(context);
				DomainObject dobjNewCharacteric = DomainObject.newInstance(context);
				String strLwrSpecLimit = DomainConstants.EMPTY_STRING;
				String strUpperSpecLimit = DomainConstants.EMPTY_STRING;
				String strReleaseCriteria = DomainConstants.EMPTY_STRING;
				StringList slAttribute = new StringList();
				slAttribute.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
				slAttribute.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
				slAttribute.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_RELEASECRITERIA + "]");
				slAttribute.addElement("attribute[" + PGPerfCharsConstants.ATTR_PG_PLANTTESTINGTEXT + "]");
				slAttribute.addElement("to[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].from.attribute["
						+ PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE + "].value");
				String strPlantTestingText = DomainConstants.EMPTY_STRING;
				String strOriginatingSource = DomainConstants.EMPTY_STRING;

				Map mCharInfo;
				while (itrCharObjIds.hasNext()) {
					Map<String, String> mpAttributeValue = new HashMap();
					Map attributeUpdate = new HashMap();
					strCharObjId = (String) itrCharObjIds.next();
					if (UIUtil.isNotNullAndNotEmpty(strCharObjId)) {
						slTMIds.clear();
						slTMRDIds.clear();
						doCharateristic.setId(strCharObjId);
						sPCAutoName = new StringBuilder("auto_").append(Calendar.getInstance().getTimeInMillis())
								.toString();// Generating Auto-Name using time stamp
						if (PGPerfCharsConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC 
								.equals((String) (doCharateristic.getInfo(context, DomainConstants.SELECT_TYPE)))) {
							MapList mlCharRelatedObjs = doCharateristic.getRelatedObjects(context,
									DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // relationship Pattern
									DomainConstants.QUERY_WILDCARD, // type Pattern
									slObjSelects, // objectSelects
									null, // relSelects
									true, // getTo
									true, // getFrom
									(short) 1, // recurse
									null, // objectWhere
									null, // relWhere
									0);
							boNewCharacteric = doCharateristic.cloneObject(context, sPCAutoName, "0",
									PGPerfCharsConstants.VAULT_ESERVICE_PRODUCTION, false);
							dobjNewCharacteric = DomainObject.newInstance(context, boNewCharacteric);
							slCharIds.add((String) dobjNewCharacteric.getId(context));
							Iterator itrCharRelatedObjs = mlCharRelatedObjs.iterator();
							while (itrCharRelatedObjs.hasNext()) {
								mCharInfo = (Map) itrCharRelatedObjs.next();
								strRefDocObjId = (String) mCharInfo.get(DomainConstants.SELECT_LAST_ID);
								strRefDocObjCurrent = (String) mCharInfo.get(PGPerfCharsConstants.SELECT_LAST_CURRENT);
								strRefDocObjPrevState = (String) mCharInfo
										.get(PGPerfCharsConstants.SELECT_LAST_PREVIOUS_CURRENT);
								if (UIUtil.isNotNullAndNotEmpty(strRefDocObjId)
										&& !strRefDocObjId.equals((String) mCharInfo.get(DomainConstants.SELECT_ID))) {
									if (!PGPerfCharsConstants.STATE_RELEASE.equals(strRefDocObjCurrent)
											&& PGPerfCharsConstants.STATE_RELEASE.equals(strRefDocObjPrevState)) {
										strRefDocObjId = (String) mCharInfo
												.get(PGPerfCharsConstants.SELECT_LAST_PREVIOUS_ID);
										strRefDocObjCurrent = strRefDocObjPrevState;
									}
								}
								strType = (String) mCharInfo.get(DomainConstants.SELECT_TYPE);
								if (UIUtil.isNotNullAndNotEmpty(strRefDocObjId)
										&& !"Obsolete".equalsIgnoreCase(strRefDocObjCurrent)) {
									if (pgV3Constants.TYPE_PGTESTMETHOD.equalsIgnoreCase(strType)
											|| PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION
													.equalsIgnoreCase(strType)) {
										slTMIds.add(strRefDocObjId);
									} else if (PGPerfCharsConstants.TYPE_PG_STANDARD_OPERATING_PROCEDURE
											.equalsIgnoreCase(strType)
											|| PGPerfCharsConstants.TYPE_PG_QUALITY_SPECIFICATION
													.equalsIgnoreCase(strType)
											|| PGPerfCharsConstants.TYPE_PG_ILLUSTRATION.equalsIgnoreCase(strType)) {
										slTMRDIds.add(strRefDocObjId);
									}
								}
							}
							Set<String> TMIds = new HashSet<>(slTMIds);
							Set<String> TMRDIds = new HashSet<>(slTMRDIds);

							// Connect all the TM and TMRD to the new cloned Characteristic.
							if (null != slTMIds && !slTMIds.isEmpty())
								DomainRelationship.connect(context, dobjNewCharacteric,
										DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, false,
										(String[]) TMIds.toArray(new String[TMIds.size()]));
							if (null != slTMRDIds && !slTMRDIds.isEmpty())
								DomainRelationship.connect(context, dobjNewCharacteric,
										DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, false,
										(String[]) TMRDIds.toArray(new String[TMRDIds.size()]));

							mpAttributeValue = doCharateristic.getInfo(context, slAttribute);
							if (mpAttributeValue != null && !mpAttributeValue.isEmpty()) {
								strLwrSpecLimit = mpAttributeValue
										.get("attribute[" + PGPerfCharsConstants.ATTR_PG_LOWERSPECIFICATIONLIMIT + "]");
								strUpperSpecLimit = mpAttributeValue
										.get("attribute[" + PGPerfCharsConstants.ATTR_PG_UPPERSPECIFICATIONLIMIT + "]");
								strReleaseCriteria = mpAttributeValue
										.get("attribute[" + PGPerfCharsConstants.ATTR_PG_RELEASECRITERIA + "]");
								strOriginatingSource = mpAttributeValue
										.get("to[" + PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC + "].from.attribute["
												+ PGPerfCharsConstants.ATTR_PGORIGINATINGSOURCE + "].value");
								String strTypeinfo = dobjProd.getInfo(context, DomainConstants.SELECT_TYPE);
								String strAllowedTypeList = FrameworkProperties.getProperty(context,
										"emxCPN.ProductDataCreation.PerformanceCharacteristic.ValidateTypeforRoutineReleaseupdate");

								StringList slAllowedTypeList = FrameworkUtil.split(strAllowedTypeList, "|");
								String strSymbolicnameofType = FrameworkUtil.getAliasForAdmin(context, "type",
										strTypeinfo, true);

								if (UIUtil.isNotNullAndNotEmpty(strSymbolicnameofType)
										&& slAllowedTypeList.contains(strSymbolicnameofType)) {
									if (strLwrSpecLimit != null) {
										attributeUpdate.put(PGPerfCharsConstants.ATTR_PG_LOWERROUTINERELEASELIMIT,
												strLwrSpecLimit);
									}
									if (strUpperSpecLimit != null) {
										attributeUpdate.put(PGPerfCharsConstants.ATTR_PG_UPPERROUTINERELEASELIMIT,
												strUpperSpecLimit);
									}
									if (strReleaseCriteria != null) {
										attributeUpdate.put(PGPerfCharsConstants.ATTR_PG_ROUTINERELEASECRITERIA,
												strReleaseCriteria);
									}
								}
								if (UIUtil.isNotNullAndNotEmpty(strOriginatingSource)
										&& !PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equals(strOriginatingSource)) {
									strPlantTestingText = mpAttributeValue
											.get("attribute[" + PGPerfCharsConstants.ATTR_PG_PLANTTESTINGTEXT + "]");
									if (UIUtil.isNotNullAndNotEmpty(strPlantTestingText)) {
										attributeUpdate.put(PGPerfCharsConstants.ATTR_PG_PLANTTESTINGRETESTING,
												strPlantTestingText);
										attributeUpdate.put(PGPerfCharsConstants.ATTR_PG_PLANTTESTINGTEXT,
												DomainConstants.EMPTY_STRING);
									}
								}
								dobjNewCharacteric.setAttributeValues(context, attributeUpdate);
							}
						}
					}
				}
				// Connect the cloned Characteristics to the Product Data Part.
				if (null != slCharIds && !slCharIds.isEmpty())
					DomainRelationship.connect(context, dobjProd, REL_CHARACTERISTIC, true,
							(String[]) slCharIds.toArray(new String[slCharIds.size()]));
				String strCharRelId = DomainConstants.EMPTY_STRING;
				String strSelectCharRelId = new StringBuilder("to[").append(REL_CHARACTERISTIC).append("].id")
						.toString();
				if ((null == mlProdCharInfo || ((HashMap) mlProdCharInfo.get(0)).size() == 0) && null != slCharIds
						&& null != slCharSequence && !slCharIds.isEmpty()
						&& slCharSequence.size() == slCharIds.size()) {
					String strChardId = DomainConstants.EMPTY_STRING;
					DomainObject doChar = null;
					DomainRelationship doRel = null;
					String seq = DomainConstants.EMPTY_STRING;
					for (int iSize = 0; iSize < slCharIds.size(); iSize++) {
						strChardId = (String) slCharIds.get(iSize);
						doChar = DomainObject.newInstance(context, strChardId);
						strCharRelId = doChar.getInfo(context, strSelectCharRelId);
						if (UIUtil.isNotNullAndNotEmpty(strCharRelId)) {
							doRel = new DomainRelationship();
							seq = (String) slCharSequence.get(iSize);
							doRel = new DomainRelationship(strCharRelId);
							if (UIUtil.isNotNullAndNotEmpty(seq)) {
								doRel.setAttributeValue(context,
										PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, seq);
							}
						}
					}
				}
				bResult = true;
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception ex) {
			ContextUtil.abortTransaction(context);
			
			throw ex;
		}
		return bResult;
	}

	/**
	 * sorts Characteristics by sequence in ascending order
	 * 
	 * @param slCharFromPart
	 * @param slCharSequence
	 * @return
	 */
	public StringList sortCharacteristicsBySequence(StringList slCharFromPart, StringList slCharSequence) {
		MapList connectedChar = new MapList();
		Map charSequenceMapping = null;
		for (int k = 0; k < slCharSequence.size(); k++) {
			charSequenceMapping = new HashMap();
			charSequenceMapping.put(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
					(String) slCharSequence.get(k));
			charSequenceMapping.put(DomainConstants.SELECT_ID, slCharFromPart.get(k));
			connectedChar.add(charSequenceMapping);
		}
		connectedChar.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending",
				"integer");
		slCharFromPart = new StringList();
		String strTempCharId = null;
		for (int iSize = 0; iSize < connectedChar.size(); iSize++) {
			Map mpChar = (Map) connectedChar.get(iSize);
			strTempCharId = (String) mpChar.get(DomainConstants.SELECT_ID);
			if (UIUtil.isNotNullAndNotEmpty(strTempCharId)) {
				slCharFromPart.add(strTempCharId);
			}
		}
		return slCharFromPart;
	}
}