package com.pdfview.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationAttribute;
import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationType;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FOP.FormulationProcessSubstitute;
import com.pdfview.impl.FOP.FormulationProcessSubstitutes;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetFormulationProcessSubstitutes extends StringHelper {

	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";
	CalenderHelper calenderHelper = null;

	public GetFormulationProcessSubstitutes(Context context, String sOID) {
		_context = context;
		_OID = sOID;
		calenderHelper = new CalenderHelper(context);
	}

	/**
	 * Retrieve Formulation Process Substitutes Data
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */
	public FormulationProcessSubstitutes getComponent() {
		return getFormulaSubstitutes(_context, _OID);
	}

	public FormulationProcessSubstitutes getFormulaSubstitutes(Context context, String strObjId) {
		FormulationProcessSubstitutes formulationProcessSubstitutes = new FormulationProcessSubstitutes();
		try {
			List<FormulationProcessSubstitute> formulationProcessSubstituteList = formulationProcessSubstitutes
					.getFormulationProcessSubstitute();
			long startTime = new Date().getTime();
			MapList mlFinalSub = new MapList();
			String EMPTY_STRING = DomainConstants.EMPTY_STRING;
			Map mpFormulaSubstitutes = new HashMap();
			Map mpFormulaSubstitutes1 = new HashMap();
			Map mFOProcessId = new HashMap();
			String strIdd = EMPTY_STRING;
			String strValidUntilDate = EMPTY_STRING;
			final String REL_MAN_EQUE = pgV3Constants.RELATIONSHIP_MANUFACTUREREQUIVALENT;
			final String POL_MAN_EQUE = pgV3Constants.POLICY_MANUFACTUREREQUIVALENT;
			final String ATTR_PREF_NAME = pgV3Constants.ATTRIBUTE_PREFERREDNAME;
			StringBuffer sbMatFunc = null;
			String sMaterVal = EMPTY_STRING;
			StringList objDetails = null;
			String sInstructions = EMPTY_STRING;
			String sDescription = EMPTY_STRING;
			String OID = EMPTY_STRING;
			DomainObject dobj = null;
			Map objDetailsMap = null;
			String sId = EMPTY_STRING;
			String sType = EMPTY_STRING;
			MapList SubInPLBOM = null;
			StringList slSubFor = null;
			StringBuffer sbSFRev = null;
			String subRelId = EMPTY_STRING;
			String subRelandAtr = EMPTY_STRING;
			DomainObject dObjSubFor = null;
			StringList slSFDetails = null;
			Map mpSFDetails = null;
			StringList slDifConSelects = null;
			Map mpDifContDetails = null;
			sbSFRev = new StringBuffer();
			String subForName = EMPTY_STRING;
			String subForType = EMPTY_STRING;
			String subForRev = EMPTY_STRING;
			String subForId = EMPTY_STRING;
			String subForTitle = EMPTY_STRING;
			String subForWetPercent = EMPTY_STRING;
			String sSFObjPrefName = EMPTY_STRING;
			String subName = EMPTY_STRING;
			String subId = EMPTY_STRING;
			String SubTitle = EMPTY_STRING;
			String subPN = EMPTY_STRING;
			DomainObject dobjsubForId = null;
			StringBuffer sbName = null;
			StringBuffer strExportBuffer = null;
			StringBuffer sbMinPercent = null;
			StringBuffer sbMaxPercent = null;
			StringBuffer sbWetPercent = null;
			StringBuffer SbTitle = null;
			StringBuffer sbQtyDiff = null;
			StringBuffer sbPhase = null;
			StringBuffer sbType = null;
			StringBuffer sbRev = null;
			StringBuffer sbValidUntilDate = null;
			String subfromId = EMPTY_STRING;
			StringList sRMPUniqueList = null;
			String sNotNew = EMPTY_STRING;
			String sPhase = EMPTY_STRING;
			String sMinPer = EMPTY_STRING;
			String sMinPerMain = EMPTY_STRING;
			String sMaxPer = EMPTY_STRING;
			String sMaxPerMain = EMPTY_STRING;
			String sWetPer = EMPTY_STRING;
			String sQtyDiff = EMPTY_STRING;
			String strType = EMPTY_STRING;
			String strTypeValue = EMPTY_STRING;
			String EBOMObjPrefName = EMPTY_STRING;
			String subPN1 = EMPTY_STRING;
			int integerPlaces = 0;
			int decimalPlaces = 0;
			StringBuffer sbWetMin = null;
			StringBuffer sbWetMax = null;
			StringBuffer sbTargetWet = null;
			String strSubInPLBOMRelId = null;
			DecimalFormat decimalformatter = new DecimalFormat(PDFConstant.PATTERN_DECIMALFORMAT);
			DomainObject doObjsubId = null;
			Map mpSubId = null;
			StringBuffer sbSubForName = new StringBuffer();
			StringBuffer sbSubForTitle = new StringBuffer();
			StringBuffer sbSubForWetPercent = new StringBuffer();
			String strMinWetWeight = EMPTY_STRING;
			String strMaxWetWeight = EMPTY_STRING;
			String strTargetWetWeight = EMPTY_STRING;
			String phaseWithLowestSeqNo = EMPTY_STRING;
			Map mapPlantData = null;
			StringBuffer sbSubPn = null;
			boolean isPushContext = false;
			Double floatQtyAdj = null;
			Double absfloatQtyAdj = null;
			String strfloatQtyAdj = EMPTY_STRING;
			HashMap hm = null;
			
			try {
				DomainObject domObjectId = DomainObject.newInstance(context, strObjId);
				StringList objectSelects = new StringList(2);
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_REVISION);

				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isPushContext = true;
				Map mMasterData = null;
				MapList mlMasterData = domObjectId.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PLANNEDFOR,
						pgV3Constants.TYPE_FORMULATIONPROCESS, objectSelects, null, false, true, (short) 1, null, null,
						0);
				if (mlMasterData != null && !mlMasterData.isEmpty()) {
					mlMasterData.addSortKey("revision", "descending", "String");
					mlMasterData.sort();
					mMasterData = (Map) mlMasterData.get(0);
					strIdd = (String) mMasterData.get(DomainConstants.SELECT_ID);
				}
				mFOProcessId = new HashMap();
				mFOProcessId.put("objectId", strIdd);
				String[] args = JPO.packArgs(mFOProcessId);
				mlFinalSub = (MapList) PDFPOCHelper.executeMainClassMethod(context, "enoGLSFormulationSubstitutes",
						"getSubstituteDetails", args);
				if ((mlFinalSub != null) && (!mlFinalSub.isEmpty())) {
					objDetails = new StringList(8);
					objDetails.add(DomainConstants.SELECT_NAME);
					objDetails.add(DomainConstants.SELECT_TYPE);
					objDetails.add(DomainConstants.SELECT_REVISION);
					objDetails.add(DomainConstants.SELECT_ID);
					objDetails.add(DomainConstants.SELECT_POLICY);
					objDetails.add(pgV3Constants.SELECT_ATTRIBUTE_SUBSTITUTEINSTRUCTIONS);
					objDetails.add(DomainConstants.SELECT_DESCRIPTION);
					objDetails.add(pgV3Constants.SELECT_ATTRIBUTE_PGVALIDUNTILDATE);
					StringList subList = null;
					int counter = 0;
					DomainRelationship domainRelationship =null;
					for (Iterator iter = mlFinalSub.iterator(); iter.hasNext();) {
						
						mpFormulaSubstitutes1 = (Map) iter.next();
						OID = (String) mpFormulaSubstitutes1.get(DomainConstants.SELECT_ID);
						dobj = DomainObject.newInstance(context,OID);
						objDetailsMap = dobj.getInfo(context, objDetails);
						sId = (String) objDetailsMap.get(DomainConstants.SELECT_ID);
						sType = (String) objDetailsMap.get(DomainConstants.SELECT_TYPE);
						strValidUntilDate = (String) objDetailsMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGVALIDUNTILDATE);
						sInstructions = (String) objDetailsMap
								.get(pgV3Constants.SELECT_ATTRIBUTE_SUBSTITUTEINSTRUCTIONS);
						sInstructions=filterLessAndGreaterThanSign(sInstructions);
						formulationProcessSubstitutes.setInstructions(sInstructions);
						if (UIUtil.isNotNullAndNotEmpty(strValidUntilDate)) {
							strValidUntilDate = calenderHelper.getFormattedDate(strValidUntilDate);

						}
						sDescription = (String) objDetailsMap.get(DomainConstants.SELECT_DESCRIPTION);
						sDescription=filterLessAndGreaterThanSign(sDescription);
						formulationProcessSubstitutes.setDescription(sDescription);
						SubInPLBOM = (MapList) mpFormulaSubstitutes1.get("SubInFBOM");
						slSubFor = (StringList) mpFormulaSubstitutes1.get("SubForParts");
						sbSubForName = new StringBuffer();
						sbSubForTitle = new StringBuffer();
						sbSubForWetPercent = new StringBuffer();
						counter = 0;
						for (Iterator ItrSubFor = slSubFor.iterator(); ItrSubFor.hasNext();) {
							subRelandAtr = (String) ItrSubFor.next();
							subList = FrameworkUtil.split(subRelandAtr, PDFConstant.SYMBOL_COLON);
							subRelId = (String) subList.get(0);
							dObjSubFor = DomainObject.newInstance(context, subRelId);
							slSFDetails = new StringList(6);
							slSFDetails.add(DomainConstants.SELECT_NAME);
							slSFDetails.add(DomainConstants.SELECT_ID);
							slSFDetails.add(DomainConstants.SELECT_TYPE);
							slSFDetails.add(DomainConstants.SELECT_REVISION);
							slSFDetails.add(DomainConstants.SELECT_POLICY);
							slSFDetails.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
							mpSFDetails = dObjSubFor.getInfo(context, slSFDetails);
							subForName = (String) mpSFDetails.get(DomainConstants.SELECT_NAME);

							subForType = (String) mpSFDetails.get(DomainConstants.SELECT_TYPE);
							subForRev = (String) mpSFDetails.get(DomainConstants.SELECT_REVISION);
							subForId = (String) mpSFDetails.get(DomainConstants.SELECT_ID);

							subForTitle = (String) mpSFDetails.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
							subForTitle=StringHelper.filterLessAndGreaterThanSign(subForTitle);
							slDifConSelects = new StringList(8);
							slDifConSelects.add("to[" + pgV3Constants.RELATIONSHIP_PLBOM + "]."
									+ pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
							slDifConSelects.add("to[" + pgV3Constants.RELATIONSHIP_PLBOM + "]."
									+ pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
							slDifConSelects.add("from[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].to["
									+ pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].from["
									+ pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].to["
									+ pgV3Constants.TYPE_COSMETICFORMULATION + "]."
									+ pgV3Constants.SELECT_ATTRIBUTE_TITLE);
							slDifConSelects.add("to[" + REL_MAN_EQUE + "].from[" + pgV3Constants.TYPE_RAWMATERIALPART
									+ "]." + pgV3Constants.SELECT_ATTRIBUTE_TITLE);
							slDifConSelects.add("to[" + REL_MAN_EQUE + "].from[" + pgV3Constants.TYPE_RAWMATERIALPART
									+ "].attribute[" + ATTR_PREF_NAME + "]");
							slDifConSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
							slDifConSelects.add("attribute[" + ATTR_PREF_NAME + "]");
							slDifConSelects.add(DomainConstants.SELECT_POLICY);
							dobjsubForId = DomainObject.newInstance(context, subForId);
							mpDifContDetails = dobjsubForId.getInfo(context, slDifConSelects);
							if (subList.size() == 4) {
								strSubInPLBOMRelId = (String) subList.get(3);
							} else {
								strSubInPLBOMRelId = (String) mpFormulaSubstitutes1.get("EBOM ID");
							}
							if (UIUtil.isNotNullAndNotEmpty(strSubInPLBOMRelId)) {
								domainRelationship = DomainRelationship.newInstance(context,
										strSubInPLBOMRelId);
								subForWetPercent = domainRelationship.getAttributeValue(context,
										pgV3Constants.ATTRIBUTE_QUANTITY);
							}

							if (UIUtil.isNotNullAndNotEmpty(subForWetPercent)) {
								integerPlaces = subForWetPercent.indexOf('.');
								decimalPlaces = subForWetPercent.length() - integerPlaces - 1;
								if (decimalPlaces > 6) {
									subForWetPercent = String
											.valueOf(decimalformatter.format(Double.parseDouble(subForWetPercent)));
								}
								if (subForWetPercent.equals("0.0")) {
									subForWetPercent = EMPTY_STRING;
								}
							}
							if (counter == 0) {
								sbSubForName.append(subForName);
								sbSubForTitle.append(subForTitle);
								sbSubForWetPercent.append(subForWetPercent);
							} else {
								sbSubForName.append(subForName);
								sbSubForTitle.append(subForTitle);
								sbSubForWetPercent.append(subForWetPercent);
							}
							counter++;
							sbSFRev.append(subForRev);
							sSFObjPrefName = null;
							if (FormulationType.FORMULATION_PART.getType(context).equals(subForType)) {
								sSFObjPrefName = (String) mpDifContDetails
										.get("from[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].to["
												+ pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].from["
												+ pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].to["
												+ pgV3Constants.TYPE_COSMETICFORMULATION + "]."
												+ pgV3Constants.SELECT_ATTRIBUTE_TITLE);
							} else {
								if (POL_MAN_EQUE.equals((String) mpDifContDetails.get(DomainConstants.SELECT_POLICY))) {
									sSFObjPrefName = (String) mpDifContDetails
											.get("to[" + REL_MAN_EQUE + "].from[" + pgV3Constants.TYPE_RAWMATERIALPART
													+ "]." + pgV3Constants.SELECT_ATTRIBUTE_TITLE);
									if (UIUtil.isNullOrEmpty(sSFObjPrefName)) {
										sSFObjPrefName = (String) mpDifContDetails.get(
												"to[" + REL_MAN_EQUE + "].from[" + pgV3Constants.TYPE_RAWMATERIALPART
														+ "].attribute[" + ATTR_PREF_NAME + "]");
									}
								} else {
									sSFObjPrefName = (String) mpDifContDetails
											.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
									if (UIUtil.isNullOrEmpty(sSFObjPrefName)) {
										sSFObjPrefName = (String) mpDifContDetails
												.get("attribute[" + ATTR_PREF_NAME + "]");
									}
								}
							}
						}
						if (sType.equals(FormulationType.PARENT_SUB.getType(context))) {
							int count = 0;
							for (Iterator iter1 = SubInPLBOM.iterator(); iter1.hasNext();) {
								count++;
								subName = null;
								subId = null;
								sbName = new StringBuffer();
								sbSubPn = new StringBuffer();
								strExportBuffer = new StringBuffer();
								sbMinPercent = new StringBuffer();
								sbMaxPercent = new StringBuffer();
								sbWetPercent = new StringBuffer();
								SbTitle = new StringBuffer();
								sbQtyDiff = new StringBuffer();
								sbPhase = new StringBuffer();
								sbType = new StringBuffer();
								sbRev = new StringBuffer();
								sbValidUntilDate = new StringBuffer();
								subfromId = null;
								sbMatFunc = new StringBuffer();
								sMaterVal = DomainConstants.EMPTY_STRING;
								sbWetMin = new StringBuffer();
								sbWetMax = new StringBuffer();
								sbTargetWet = new StringBuffer();

								sRMPUniqueList = new StringList();
								mpFormulaSubstitutes = (Map) iter1.next();
								sNotNew = (String) mpFormulaSubstitutes.get("notNew");
								subName = (String) mpFormulaSubstitutes.get(DomainConstants.SELECT_NAME);
								strType = (String) mpFormulaSubstitutes.get(DomainConstants.SELECT_TYPE);
								subId = (String) mpFormulaSubstitutes.get(DomainConstants.SELECT_ID);
								SubTitle = (String) mpFormulaSubstitutes.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
								if (UIUtil.isNotNullAndNotEmpty(SubTitle)) {
									SubTitle = filterLessAndGreaterThanSign(SubTitle);
								}
								sMinPer = (String) mpFormulaSubstitutes
										.get("attribute[Minimum Actual Percent Wet].value");
								sMaxPer = (String) mpFormulaSubstitutes
										.get("attribute[Maximum Actual Percent Wet].value");
								sWetPer = (String) mpFormulaSubstitutes.get("attribute[Quantity].value");
								sQtyDiff = (String) mpFormulaSubstitutes.get("attribute[Quantity Adjustment].value");
								strMinWetWeight = (String) mpFormulaSubstitutes
										.get("attribute[pgMinimumActualWeightWet].inputvalue");
								strMaxWetWeight = (String) mpFormulaSubstitutes
										.get("attribute[pgMaximumActualWeightWet].inputvalue");
								strTargetWetWeight = (String) mpFormulaSubstitutes
										.get("attribute[Target Weight Wet].value");

								doObjsubId = DomainObject.newInstance(context, subId);
								mpSubId = doObjsubId.getInfo(context, slDifConSelects);
								if (mpFormulaSubstitutes.containsKey(
										"attribute[" + pgV3Constants.ATTRIBUTE_PROCESSINGNOTE + "].value")) {
									subPN = (String) mpFormulaSubstitutes
											.get("attribute[" + pgV3Constants.ATTRIBUTE_PROCESSINGNOTE + "].value");
									if (UIUtil.isNotNullAndNotEmpty(subPN)) {
										subPN = filterLessAndGreaterThanSign(subPN);
										sbSubPn.append(subPN);
									} else {
										subPN = DomainConstants.EMPTY_STRING;
										sbSubPn.append(subPN);
									}
								} else {
									subPN1 = (String) mpFormulaSubstitutes1
											.get("attribute[" + pgV3Constants.ATTRIBUTE_PROCESSINGNOTE + "].value");
									if (UIUtil.isNotNullAndNotEmpty(subPN1)) {
										subPN1 = filterLessAndGreaterThanSign(subPN1);
										sbSubPn.append(subPN1);
									} else {
										subPN1 = DomainConstants.EMPTY_STRING;
										sbSubPn.append(subPN1);
									}
								}
								if (UIUtil.isNullOrEmpty(sNotNew) && (!sRMPUniqueList.contains(subId))) {
									sbName.append(subName);
									SbTitle.append(SubTitle);
								} else if (UIUtil.isNotNullAndNotEmpty(sNotNew) && (!sRMPUniqueList.contains(subId))) {
									sbName.append(subName);
									SbTitle.append(subForTitle);
								}
								sRMPUniqueList.add(subId);
								subfromId = (String) mpFormulaSubstitutes.get("from.id");
								sPhase = (String) mpFormulaSubstitutes.get("phase");
								if (UIUtil.isNotNullAndNotEmpty(sPhase)) {
									sbPhase.append(sPhase);
								} else {
									phaseWithLowestSeqNo = getPhaseWithLowestSeqNo(context, subfromId);
									sbPhase.append(phaseWithLowestSeqNo);
								}
								if (mpFormulaSubstitutes
										.containsKey("attribute[Minimum Actual Percent Wet].inputvalue")) {
									sMinPer = (String) mpFormulaSubstitutes
											.get("attribute[Minimum Actual Percent Wet].inputvalue");
									if (UIUtil.isNotNullAndNotEmpty(sMinPer)) {
										integerPlaces = sMinPer.indexOf('.');
										decimalPlaces = sMinPer.length() - integerPlaces - 1;
										if (decimalPlaces > 6)
											sMinPer = String
													.valueOf(decimalformatter.format(Double.parseDouble(sMinPer)));
										sbMinPercent.append(sMinPer);
									} else
										sbMinPercent.append(DomainConstants.EMPTY_STRING);
								} else {
									sMinPerMain = (String) mpFormulaSubstitutes
											.get("attribute[Minimum Actual Percent Wet].value");
									if (UIUtil.isNotNullAndNotEmpty(sMinPerMain)) {
										integerPlaces = sMinPerMain.indexOf('.');
										decimalPlaces = sMinPerMain.length() - integerPlaces - 1;
										if (decimalPlaces > 6)
											sMinPerMain = String
													.valueOf(decimalformatter.format(Double.parseDouble(sMinPerMain)));
										if (sMinPerMain.equals("0.0")) {
											sMinPerMain = EMPTY_STRING;
										}
										sbMinPercent.append(sMinPerMain);

									} else

										sbMinPercent.append(DomainConstants.EMPTY_STRING);

								}

								if (mpFormulaSubstitutes
										.containsKey("attribute[Maximum Actual Percent Wet].inputvalue")) {
									sMaxPer = (String) mpFormulaSubstitutes
											.get("attribute[Maximum Actual Percent Wet].inputvalue");
									if (UIUtil.isNotNullAndNotEmpty(sMaxPer)) {
										integerPlaces = sMaxPer.indexOf('.');
										decimalPlaces = sMaxPer.length() - integerPlaces - 1;
										if (decimalPlaces > 6)
											sMaxPer = String
													.valueOf(decimalformatter.format(Double.parseDouble(sMaxPer)));

										sbMaxPercent.append(sMaxPer);

									} else
										sbMaxPercent.append(DomainConstants.EMPTY_STRING);

								} else {
									sMaxPerMain = (String) mpFormulaSubstitutes
											.get("attribute[Maximum Actual Percent Wet].value");
									if (UIUtil.isNotNullAndNotEmpty(sMaxPerMain)) {
										integerPlaces = sMaxPerMain.indexOf('.');
										decimalPlaces = sMaxPerMain.length() - integerPlaces - 1;
										if (decimalPlaces > 6)
											sMaxPerMain = String
													.valueOf(decimalformatter.format(Double.parseDouble(sMaxPerMain)));
										if (sMaxPerMain.equals("0.0")) {
											sMaxPerMain = EMPTY_STRING;
										}
										sbMaxPercent.append(sMaxPerMain);
									} else {
										sbMaxPercent.append(DomainConstants.EMPTY_STRING);
									}
								}
								if (mpFormulaSubstitutes.containsKey("attribute[Quantity].inputvalue")) {
									sWetPer = (String) mpFormulaSubstitutes.get("attribute[Quantity].inputvalue");
									if (UIUtil.isNotNullAndNotEmpty(sWetPer)) {
										integerPlaces = sWetPer.indexOf('.');
										decimalPlaces = sWetPer.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											sWetPer = String
													.valueOf(decimalformatter.format(Double.parseDouble(sWetPer)));
										}

										sbWetPercent.append(sWetPer);
									} else {
										sbWetPercent.append(DomainConstants.EMPTY_STRING);
									}
								} else {
									sWetPer = (String) mpFormulaSubstitutes.get("attribute[Quantity].value");
									if (UIUtil.isNotNullAndNotEmpty(sWetPer)) {
										integerPlaces = sWetPer.indexOf('.');
										decimalPlaces = sWetPer.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											sWetPer = String
													.valueOf(decimalformatter.format(Double.parseDouble(sWetPer)));
										}
										sbWetPercent.append(sWetPer);
									} else {
										sbWetPercent.append(DomainConstants.EMPTY_STRING);
									}
								}
								sQtyDiff = (String) mpFormulaSubstitutes.get("attribute[Quantity Adjustment].value");
								if (UIUtil.isNotNullAndNotEmpty(sQtyDiff)) {
									floatQtyAdj = new Double(0.0);
									floatQtyAdj = Double.parseDouble(sQtyDiff);
									absfloatQtyAdj = Math.abs(floatQtyAdj);
									strfloatQtyAdj = String.format("%.6f", new BigDecimal(absfloatQtyAdj));
									hm = new HashMap();
									hm.put("strfloatQtyAdj", strfloatQtyAdj);
									hm.put("DECIMAL_SIX", pgV3Constants.FORMAT_DECIMAL_SIX);
									String args1[] = JPO.packArgs(hm);
									strfloatQtyAdj = (String) PDFPOCHelper.executeIntermediatorClassMethod(context,
											"getFormatedDecimalValue", args1);
									if (sQtyDiff.equals("0.0")) {
										sQtyDiff = EMPTY_STRING;
									} else {
										sQtyDiff = strfloatQtyAdj;
									}
									sbQtyDiff.append(sQtyDiff);
								} else {
									sbQtyDiff.append(pgV3Constants.SYMBOL_HYPHEN);
								}
								if (mpFormulaSubstitutes
										.containsKey("attribute[pgMinimumActualWeightWet].inputvalue")) {
									strMinWetWeight = (String) mpFormulaSubstitutes
											.get("attribute[pgMinimumActualWeightWet].inputvalue");
									if (UIUtil.isNotNullAndNotEmpty(strMinWetWeight)) {
										integerPlaces = strMinWetWeight.indexOf('.');
										decimalPlaces = strMinWetWeight.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											strMinWetWeight = String.valueOf(
													decimalformatter.format(Double.parseDouble(strMinWetWeight)));
										}
										sbWetMin.append(strMinWetWeight);
									} else {
										sbWetMin.append(DomainConstants.EMPTY_STRING);
									}
								} else {
									strMinWetWeight = (String) mpFormulaSubstitutes
											.get("attribute[pgMinimumActualWeightWet].value");
									if (UIUtil.isNotNullAndNotEmpty(strMinWetWeight)) {
										integerPlaces = strMinWetWeight.indexOf('.');
										decimalPlaces = strMinWetWeight.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											strMinWetWeight = String.valueOf(
													decimalformatter.format(Double.parseDouble(strMinWetWeight)));
										}
										if (strMinWetWeight.equals("0.0")) {
											strMinWetWeight = EMPTY_STRING;
										}
										sbWetMin.append(strMinWetWeight);
									} else {
										sbWetMin.append(DomainConstants.EMPTY_STRING);
									}
								}

								if (mpFormulaSubstitutes
										.containsKey("attribute[pgMaximumActualWeightWet].inputvalue")) {
									strMaxWetWeight = (String) mpFormulaSubstitutes
											.get("attribute[pgMaximumActualWeightWet].inputvalue");
									if (UIUtil.isNotNullAndNotEmpty(strMaxWetWeight)) {
										integerPlaces = strMaxWetWeight.indexOf('.');
										decimalPlaces = strMaxWetWeight.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											strMaxWetWeight = String.valueOf(
													decimalformatter.format(Double.parseDouble(strMaxWetWeight)));
										}
										sbWetMax.append(strMaxWetWeight);

									} else {
										sbWetMax.append(DomainConstants.EMPTY_STRING);
									}
								} else {
									strMaxWetWeight = (String) mpFormulaSubstitutes
											.get("attribute[pgMaximumActualWeightWet].value");
									if (UIUtil.isNotNullAndNotEmpty(strMaxWetWeight)) {
										integerPlaces = strMaxWetWeight.indexOf('.');
										decimalPlaces = strMaxWetWeight.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											strMaxWetWeight = String.valueOf(
													decimalformatter.format(Double.parseDouble(strMaxWetWeight)));
										}
										if (strMaxWetWeight.equals("0.0")) {
											strMaxWetWeight = EMPTY_STRING;
										}
										sbWetMax.append(strMaxWetWeight);
									} else {

										sbWetMax.append(DomainConstants.EMPTY_STRING);
									}
								}

								if (mpFormulaSubstitutes.containsKey("attribute[Target Weight Wet].value")) {
									strTargetWetWeight = (String) mpFormulaSubstitutes
											.get("attribute[Target Weight Wet].value");
									if (UIUtil.isNotNullAndNotEmpty(strTargetWetWeight)) {
										integerPlaces = strTargetWetWeight.indexOf('.');
										decimalPlaces = strTargetWetWeight.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											strTargetWetWeight = String.valueOf(
													decimalformatter.format(Double.parseDouble(strTargetWetWeight)));

										}
										if (strTargetWetWeight.equals("0.0")) {
											strTargetWetWeight = EMPTY_STRING;
										}
										sbTargetWet.append(strTargetWetWeight);
									} else {
										sbTargetWet.append(DomainConstants.EMPTY_STRING);
									}
								} else {
									strTargetWetWeight = (String) mpFormulaSubstitutes
											.get("attribute[Target Weight Wet].inputvalue");
									if (UIUtil.isNotNullAndNotEmpty(strTargetWetWeight)) {
										integerPlaces = strTargetWetWeight.indexOf('.');
										decimalPlaces = strTargetWetWeight.length() - integerPlaces - 1;
										if (decimalPlaces > 6) {
											strTargetWetWeight = String.valueOf(
													decimalformatter.format(Double.parseDouble(strTargetWetWeight)));
										}
										sbTargetWet.append(strTargetWetWeight);
									} else {
										sbTargetWet.append(DomainConstants.EMPTY_STRING);
									}
								}

								strTypeValue = UINavigatorUtil.getAdminI18NString("Type", strType,
										context.getSession().getLanguage());
								if (UIUtil.isNotNullAndNotEmpty(strTypeValue)) {
									sbType.append(strTypeValue);
									sMaterVal = getMaterialFunction(context, "");
								} else
									sbType.append(UINavigatorUtil.getAdminI18NString("Type", sType,
											context.getSession().getLanguage()));
								if (UIUtil.isNotNullAndNotEmpty(sMaterVal)) {
									sbMatFunc.append(sMaterVal);
								} else {
									sbMatFunc.append(DomainConstants.EMPTY_STRING);
								}

								EBOMObjPrefName = DomainConstants.EMPTY_STRING;
								if (FormulationType.FORMULATION_PART.getType(context).equals(sType)) {
									EBOMObjPrefName = (String) mpSubId
											.get("from[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].to["
													+ pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].from["
													+ pgV3Constants.RELATIONSHIP_FORMULATIONPROCESS + "].to["
													+ pgV3Constants.TYPE_COSMETICFORMULATION + "]."
													+ pgV3Constants.SELECT_ATTRIBUTE_TITLE);
								} else {
									if (POL_MAN_EQUE.equals((String) mpSubId.get(DomainConstants.SELECT_POLICY))) {
										EBOMObjPrefName = (String) mpSubId.get(
												"to[" + REL_MAN_EQUE + "].from[" + pgV3Constants.TYPE_RAWMATERIALPART
														+ "]." + pgV3Constants.SELECT_ATTRIBUTE_TITLE);
										if (UIUtil.isNullOrEmpty(EBOMObjPrefName)) {
											EBOMObjPrefName = (String) mpSubId.get("to[" + REL_MAN_EQUE + "].from["
													+ pgV3Constants.TYPE_RAWMATERIALPART + "].attribute["
													+ ATTR_PREF_NAME + "]");
										}
									} else {
										EBOMObjPrefName = (String) mpSubId.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
										EBOMObjPrefName = EBOMObjPrefName.replaceAll("[<]", "#LESS_THAN");
										EBOMObjPrefName = EBOMObjPrefName.replaceAll("[>]", "#GREATER_THAN");

										if (UIUtil.isNullOrEmpty(EBOMObjPrefName)) {
											EBOMObjPrefName = (String) mpSubId.get("attribute[" + ATTR_PREF_NAME + "]");
											EBOMObjPrefName = EBOMObjPrefName.replaceAll("[<]", "#LESS_THAN");
											EBOMObjPrefName = EBOMObjPrefName.replaceAll("[>]", "#GREATER_THAN");
										}
									}
								}
								strExportBuffer.append(EBOMObjPrefName);
								FormulationProcessSubstitute formulationProcessSubstitute = new FormulationProcessSubstitute();
								formulationProcessSubstitute.setName(StringHelper.validateString1(sbName.toString()));

								formulationProcessSubstitute
										.setTitle(StringHelper.validateString1(strExportBuffer.toString()));
								formulationProcessSubstitute.setType(StringHelper.validateString1(sbType.toString()));
								formulationProcessSubstitute
										.setMinPercent(StringHelper.validateString1(sbMinPercent.toString()));
								formulationProcessSubstitute
										.setWetPercent(StringHelper.validateString1(sbWetPercent.toString()));
								formulationProcessSubstitute
										.setMaxPercent(StringHelper.validateString1(sbMaxPercent.toString()));
								formulationProcessSubstitute
										.setTargetWetWeight(StringHelper.validateString1(sbTargetWet.toString()));
								formulationProcessSubstitute
										.setWetWeightMin(StringHelper.validateString1(sbWetMin.toString()));
								formulationProcessSubstitute
										.setWetWeightMax(StringHelper.validateString1(sbWetMax.toString()));
								formulationProcessSubstitute
										.setWetDiffPercent(StringHelper.validateString1(sbQtyDiff.toString()));
								formulationProcessSubstitute
										.setValidUntilDate(StringHelper.validateString1(strValidUntilDate));
								formulationProcessSubstitute
										.setCertifications(getPLIMaterialCertifications(context, subId));
								formulationProcessSubstitute
										.setProcessingNote(StringHelper.validateString1(sbSubPn.toString()));

								if (count == 1) {
									formulationProcessSubstitute
											.setSustituteForName(StringHelper.validateString1(sbSubForName.toString()));
									formulationProcessSubstitute.setSustituteForTitle(
											StringHelper.validateString1(sbSubForTitle.toString()));
									formulationProcessSubstitute.setSustituteForWetPercent(
											StringHelper.validateString1(sbSubForWetPercent.toString()));
									formulationProcessSubstituteList.add(formulationProcessSubstitute);
								}
							}
							strValidUntilDate = DomainConstants.EMPTY_STRING;
						}

					}

				}
				mlFinalSub.clear();
			} catch (Exception exception) {
				throw exception;
			} finally {
				if (isPushContext) {
					ContextUtil.popContext(context);
					isPushContext = false;
				}
			}

			long endTime = new Date().getTime();
			System.out
					.println("Total Time has taken by the getFormulaSubstitutes Method is-->" + (endTime - startTime));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return formulationProcessSubstitutes;
	}

	private String getPhaseWithLowestSeqNo(Context context, String parentSubId) throws Exception {

		String phaseWithLowestSeqNo = DomainConstants.EMPTY_STRING;
		String strSequence = DomainConstants.EMPTY_STRING;
		String sequenceNumber = DomainConstants.EMPTY_STRING;
		String strCommand1 = "print bus $1 select $2 $3 $4";

		String phaseList = MqlUtil.mqlCommand(context, strCommand1, parentSubId,
				"to.attribute[".concat(FormulationAttribute.PRIMARY_REPLACEMENT_INGREDIENT.getAttribute(context))
						.concat("].value"),
				"to.fromrel.from.name",
				"to.fromrel.from.to[" + pgV3Constants.RELATIONSHIP_PLBOM + "].attribute[Find Number].value");
		StringList slPhasePRIList = FrameworkUtil.split(phaseList, "\n");
		int size = slPhasePRIList.size() - 1;
		int iNoOfIterations = size / 3; // Here 3 is number of selectables in strCommand1
		int iCounter = 1;

		int lowestSeqNo = 0;
		StringList slPRIList =null;
		StringList slPhaseList =null;
		StringList slSeqNoList =null;
		while (iCounter <= iNoOfIterations) {
			slPRIList = FrameworkUtil.split((String) slPhasePRIList.get(iCounter), "=");
			slPhaseList = FrameworkUtil.split((String) slPhasePRIList.get(iCounter + iNoOfIterations), "=");
			slSeqNoList = FrameworkUtil.split((String) slPhasePRIList.get(iCounter + 2 * iNoOfIterations),
					"=");

			String isPrimaryIngredient = ((String) slPRIList.get(1)).trim();
			if (UIUtil.isNotNullAndNotEmpty(isPrimaryIngredient) && "TRUE".equals(isPrimaryIngredient)) {
				if (lowestSeqNo == 0) {
					phaseWithLowestSeqNo = ((String) slPhaseList.get(1)).trim();
					strSequence = ((String) slSeqNoList.get(1)).trim();
					if (UIUtil.isNullOrEmpty(strSequence) || strSequence.equalsIgnoreCase("NaN")) {
						strSequence = pgV3Constants.EXPERIMENTAL_CODE;
					}
					lowestSeqNo = Integer.parseInt(strSequence);
				} else {
					sequenceNumber = ((String) slSeqNoList.get(1)).trim();
					if (UIUtil.isNullOrEmpty(sequenceNumber) || sequenceNumber.equalsIgnoreCase("NaN")) {
						sequenceNumber = pgV3Constants.EXPERIMENTAL_CODE;
					}
					if (lowestSeqNo > Integer.parseInt(sequenceNumber)) {
						phaseWithLowestSeqNo = ((String) slPhaseList.get(1)).trim();
						lowestSeqNo = Integer.parseInt(sequenceNumber);
					}
				}
			}
			iCounter++;
		}

		return phaseWithLowestSeqNo;
	}

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

			ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
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

	/**
	 * Retrive Material Certifications BOM/Substitute Data
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
						pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS, objectSelects, null, false, true, (short) 1, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
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
}
