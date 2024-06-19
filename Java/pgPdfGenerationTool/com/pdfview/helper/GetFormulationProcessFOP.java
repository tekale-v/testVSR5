package com.pdfview.helper;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationAttribute;
import com.dassault_systemes.enovia.formulation.custom.virtualintermediates.model.VirtualIntermediate;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FOP.FormulationProcess;
import com.pdfview.impl.FOP.FormulationProcessData;
import com.pdfview.impl.FOP.FormulationProcesses;
import com.pdfview.impl.FOP.FormulationProcessesFormula;
import com.pdfview.impl.FOP.FormulationProcessesFormulas;
import com.pg.v3.custom.pgV3Constants;

public class GetFormulationProcessFOP {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";
	
	public GetFormulationProcessFOP(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}
	public FormulationProcessData getComponent() throws Exception {
		return getFormulaIngredients(_context, _OID);
	}

	/**
	 * @Desc Show Formula Ingredients Table for FOP Type -PDF views
	 * @param        Context- Context user
	 * @param String - Object ID
	 * @return StringBuilder- Formula Ingredient Table
	 * @throws Exception
	 */
	public FormulationProcessData getFormulaIngredients(Context context, String strObjId) {

		FormulationProcessData FormulationProcessData = new FormulationProcessData();
		try {
			FormulationProcessesFormulas formulationProcessesFormulas = FormulationProcessData
					.getFormulationProcessesFormulas();
			FormulationProcesses formulationProcesses = FormulationProcessData.getFormulationProcesses();
			List<FormulationProcess> formulationProcessList = formulationProcesses.getFormulationProcess();
			List<FormulationProcessesFormula> formulationProcessesFormulasList = formulationProcessesFormulas
					.getFormulationProcessesFormula();
			MapList mlFormulaIngredients = new MapList();
			Map mpFormulaIngredients = new HashMap();
			Map mpObjDetails = new HashMap();
			boolean findNumberIsNotEmpty = true;
			String strName = DomainConstants.EMPTY_STRING;
			String strId = DomainConstants.EMPTY_STRING;
			String strType = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			String strMin = DomainConstants.EMPTY_STRING;
			String strMax = DomainConstants.EMPTY_STRING;
			String strLoss = DomainConstants.EMPTY_STRING;
			String strQuantity = DomainConstants.EMPTY_STRING;
			String strWeightWet = DomainConstants.EMPTY_STRING;
			String strWeightDry = DomainConstants.EMPTY_STRING;
			String strIdd = DomainConstants.EMPTY_STRING;
			String strRelId = DomainConstants.EMPTY_STRING;
			String strDryPer = DomainConstants.EMPTY_STRING;
			String strSequenceNumber = DomainConstants.EMPTY_STRING;
			String strVirtualName = DomainConstants.EMPTY_STRING;
			String strviName = DomainConstants.EMPTY_STRING;
			String strTitle = DomainConstants.EMPTY_STRING;
			String strProcessName = DomainConstants.EMPTY_STRING;
			String strFormulaTypee = DomainConstants.EMPTY_STRING;
			String strgetFormulaNamee = DomainConstants.EMPTY_STRING;
			String strRel = pgV3Constants.RELATIONSHIP_PLBOM;
			StringList objectSelects = new StringList(8);
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
			String strMaterialFunction = DomainConstants.EMPTY_STRING;
			StringList relSelects = new StringList();
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MIN_PERCENT_ACTUALWEIGHTWET+".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MAX_PERCENT_ACTUALWEIGHTWET +".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_LOSS + ".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY + ".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT + ".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY + ".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TOTAL + ".inputvalue");
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PROCESSINGNOTE);
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
			String strFindNum = DomainConstants.EMPTY_STRING;
			String strSequenceNum = DomainConstants.EMPTY_STRING;
			String strSequenceNum2 = DomainConstants.EMPTY_STRING;
			String EMPTY_STRING = DomainConstants.EMPTY_STRING;
			String strComments = DomainConstants.EMPTY_STRING;
			relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_VIRTUALINTERMEDIATEPHYSICALID);
			relSelects.add(DomainObject.SELECT_RELATIONSHIP_ID);
			Map mpArgs = null;
			Map programMap = null;
			MapList mlArgs = new MapList();
			DomainObject doObj = null;
			boolean isPushContext = false;
			
			int integerPlaces = 0;
			int decimalPlaces = 0;
			try {
				DomainObject domObjectId = DomainObject.newInstance(context, strObjId);

				MapList mlMasterData = domObjectId.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PLANNEDFOR,
						pgV3Constants.TYPE_FORMULATIONPROCESS, objectSelects, null, false, true, (short) 1, null, null,
						0);
				if (mlMasterData != null && !mlMasterData.isEmpty()) {
					FormulationProcess formulationProcess = new FormulationProcess();
					FormulationProcessesFormula formulationProcessesFormula = new FormulationProcessesFormula();

					mlMasterData.addSortKey("revision", "descending", "String");
					mlMasterData.sort();
					Map mMasterData = (Map) mlMasterData.get(0);
					strIdd = (String) mMasterData.get(DomainConstants.SELECT_ID);
					String FormulationTitle = (String) mMasterData.get(DomainConstants.SELECT_NAME);
					String[] parts = FormulationTitle.split(pgV3Constants.SYMBOL_HYPHEN);
					String FormulationName = parts[0];
					String FormulationRevision = (String) mMasterData.get(DomainConstants.SELECT_REVISION);
					String FormulationProcessName = (String) mMasterData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
					String FormulationStage = (String) mMasterData.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
					String FormulationState = (String) mMasterData.get(DomainConstants.SELECT_CURRENT);
					String FormulationType = (String) mMasterData.get(DomainConstants.SELECT_TYPE);
					Map mpArgss = new HashMap();
					Map mpFormulaType = new HashMap();
					Map programMapp = new HashMap();
					MapList mlArgss = new MapList();
					mpArgss.put("id", strObjId);
					mpFormulaType.put("table", "CPNFormulationPartsandProcesses");
					mlArgss.add(mpArgss);
					programMapp.put("objectList", mlArgss);
					programMapp.put("paramList", mpFormulaType);
					String[] strArgsD = JPO.packArgs(programMapp);
					Vector VecTitleProcessName = (Vector) PDFPOCHelper.executeMainClassMethod(context,
							"pgEnginuityUtil", "getTitle", strArgsD);
					if (VecTitleProcessName.size() > 0) {
						strProcessName = (String) VecTitleProcessName.get(0);
						strProcessName = StringHelper.filterLessAndGreaterThanSign(strProcessName);

					}
					Vector VecFormulaType = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgEnginuityUtil",
							"getFormulaType", strArgsD);
					if (VecFormulaType.size() > 0) {
						strFormulaTypee = (String) VecFormulaType.get(0);
					}
					Vector VecgetFormulaName = (Vector) PDFPOCHelper.executeMainClassMethod(context, "pgEnginuityUtil",
							"getFormulaName", strArgsD);
					if (VecgetFormulaName.size() > 0) {
						strgetFormulaNamee = (String) VecgetFormulaName.get(0);
						strgetFormulaNamee = StringHelper.filterLessAndGreaterThanSign(strgetFormulaNamee);

					}
					StringList slFormulationTYPE = (StringList) PDFPOCHelper.executeMainClassMethod(context,
							"enoGLSFormulationProcessUI", "getFormulationType", strArgsD);
					String strReturn = DomainConstants.EMPTY_STRING;
					String strTestMethod = DomainConstants.EMPTY_STRING;
					if (slFormulationTYPE.size() == 1) {
						strReturn += (String) slFormulationTYPE.get(0);
					} else {
						for (Iterator iterator2 = slFormulationTYPE.iterator(); iterator2.hasNext();) {
							strTestMethod = (String) iterator2.next();
							strReturn += "<BR/>" + strTestMethod;
						}
					}
					formulationProcess.setFormulaName(StringHelper.validateString1(FormulationName));
					formulationProcess.setRevision(FormulationRevision);
					formulationProcess.setFormulaNameProcessName(strProcessName);
					formulationProcess.setFormulationType(strReturn);
					formulationProcess.setPhase(FormulationStage);
					formulationProcess.setState(FormulationState);
					formulationProcess.setFormulaType(strFormulaTypee);
					formulationProcess.setFormulaName(strgetFormulaNamee);
					formulationProcess.setName(FormulationTitle);
					formulationProcess.setType(FormulationType);
					formulationProcess.setTitle(strProcessName);
					formulationProcessList.add(formulationProcess);
				}
				DomainObject domObjectIdd = DomainObject.newInstance(context, strIdd);
				mlFormulaIngredients = domObjectIdd.getRelatedObjects(context, // Context
						strRel, // relPattern
						pgV3Constants.SYMBOL_STAR, // typePattern
						objectSelects, // objectSelects
						relSelects, // relationshipSelects
						false, // getTo - Get Parent Data
						true, // getFrom - Get Child Data
						(short) 0, // recurseToLevel
						DomainConstants.EMPTY_STRING, // objectWhere
						DomainConstants.EMPTY_STRING); // relationshipWhere
				int nMapMPP = mlFormulaIngredients.size();
				Map mpFormulaIngredientss = new HashMap();
				MapList mlFormulaIngredientSort = new MapList();
				String strSortSequence = DomainConstants.EMPTY_STRING;
				String strSortSequence1 = DomainConstants.EMPTY_STRING;
				for (int i = 0; i < nMapMPP; i++) {
					findNumberIsNotEmpty = true;
					mpFormulaIngredientss = (Map) mlFormulaIngredients.get(i);
					strFindNum = (String) mpFormulaIngredientss.get(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
					if (UIUtil.isNullOrEmpty(strFindNum) || strFindNum.equalsIgnoreCase("NaN")) {
						strFindNum = pgV3Constants.EXPERIMENTAL_CODE;
						findNumberIsNotEmpty = false;
					}
					strType = (String) mpFormulaIngredientss.get(DomainConstants.SELECT_TYPE);
					if (strType.equals(pgV3Constants.TYPE_FORMULATIONPHASE)) {
						strSequenceNum = strFindNum;
						strSortSequence = strFindNum.concat("00");
						strSequenceNum2 = strSequenceNum;
						strSortSequence1 = strSortSequence;
					} else {
						strSequenceNum = strSequenceNum2.concat(pgV3Constants.SYMBOL_DOT).concat(strFindNum);
						strSortSequence = Integer
								.toString(Integer.valueOf(strSortSequence1) + Integer.valueOf(strFindNum));
					}
					if (!findNumberIsNotEmpty)
						strSequenceNum = pgV3Constants.SYMBOL_SPACE;
					mpFormulaIngredientss.put(PDFConstant.SEQUENCE_NUMBER, strSequenceNum);
					mpFormulaIngredientss.put("Temp", strSortSequence);
					mlFormulaIngredientSort.add(mpFormulaIngredientss);
				}
				mlFormulaIngredientSort.addSortKey("Temp", "ascending", "Real");
				mlFormulaIngredientSort.sort();
				if ((mlFormulaIngredientSort != null) && (!mlFormulaIngredientSort.isEmpty())) {
					int nMapMP = mlFormulaIngredientSort.size();
					ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					isPushContext = true;
					String[] strArgsDSO = null;
					for (int i = 0; i < nMapMP; i++) {
						FormulationProcessesFormula formulationProcessesFormula = new FormulationProcessesFormula();
						mpFormulaIngredients = (Map) mlFormulaIngredientSort.get(i);
						strId = (String) mpFormulaIngredients.get(DomainConstants.SELECT_ID);
						strRelId = (String) mpFormulaIngredients.get(DomainObject.SELECT_RELATIONSHIP_ID);
						strSequenceNumber = (String) mpFormulaIngredients.get(PDFConstant.SEQUENCE_NUMBER);
						doObj = DomainObject.newInstance(context, strId);
						mpArgs = new HashMap();
						programMap = new HashMap();
						mlArgs = new MapList();
						mpArgs.put(DomainConstants.SELECT_ID, strId);
						mpArgs.put(DomainObject.SELECT_RELATIONSHIP_ID, strRelId);
						mpArgs.put(FormulationAttribute.VIRTUAL_INTERMEDIATE_PHYSICAL_ID.getAttributeSelect(context),
								strId);
						mlArgs.add(mpArgs);
						programMap.put("objectList", mlArgs);
						strArgsDSO = JPO.packArgs(programMap);
						mpObjDetails = (Map) doObj.getInfo(context, objectSelects);
						strTitle = (String) mpObjDetails.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						strTitle = StringHelper.filterLessAndGreaterThanSign(strTitle);
						strName = (String) mpObjDetails.get(DomainConstants.SELECT_NAME);
						strType = (String) mpObjDetails.get(DomainConstants.SELECT_TYPE);
						strValue = UINavigatorUtil.getAdminI18NString("Type", strType,
								context.getSession().getLanguage());
						
						strMin = (String) mpFormulaIngredients
								.get(pgV3Constants.SELECT_ATTRIBUTE_MIN_PERCENT_ACTUALWEIGHTWET);
						strMax = (String) mpFormulaIngredients
								.get(pgV3Constants.SELECT_ATTRIBUTE_MAX_PERCENT_ACTUALWEIGHTWET);
						strLoss = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
						strQuantity = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						strWeightWet = (String) mpFormulaIngredients
								.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
						strWeightDry = (String) mpFormulaIngredients
								.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
						strDryPer = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
						strComments = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_PROCESSINGNOTE);

						if (UIUtil.isNullOrEmpty(strComments)) {
							strComments = DomainConstants.EMPTY_STRING;
						}
						strComments = StringHelper.filterLessAndGreaterThanSign(strComments);
						strMaterialFunction = (String) getMaterialFunction(context, strRelId);
						strVirtualName = (String) mpFormulaIngredients
								.get(pgV3Constants.SELECT_ATTRIBUTE_VIRTUALINTERMEDIATEPHYSICALID);
						if (mpFormulaIngredients.containsKey(
								pgV3Constants.SELECT_ATTRIBUTE_MIN_PERCENT_ACTUALWEIGHTWET+".inputvalue")) {
							strMin = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_MIN_PERCENT_ACTUALWEIGHTWET+".inputvalue");
							strMin=NumericHelper.getDecimalFormattedValues(strMin);
						}

						if (mpFormulaIngredients.containsKey(pgV3Constants.SELECT_ATTRIBUTE_MAX_PERCENT_ACTUALWEIGHTWET+".inputvalue")) {
							strMax = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_MAX_PERCENT_ACTUALWEIGHTWET+".inputvalue");
							strMax=NumericHelper.getDecimalFormattedValues(strMax);
						}
						if (mpFormulaIngredients
								.containsKey(pgV3Constants.SELECT_ATTRIBUTE_LOSS)) {
							strLoss = (String) mpFormulaIngredients
									.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
							strLoss=NumericHelper.getDecimalFormattedValues(strLoss);
						}
						if (mpFormulaIngredients
								.containsKey(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT)) {
							strWeightWet = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
							strWeightWet=NumericHelper.getDecimalFormattedValues(strWeightWet);
						}
						if (mpFormulaIngredients
								.containsKey(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY+".inputvalue")) {
							strQuantity = (String) mpFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY+".inputvalue");
							strQuantity=NumericHelper.getDecimalFormattedValues(strQuantity);
						}
						if (mpFormulaIngredients
								.containsKey(pgV3Constants.SELECT_ATTRIBUTE_TOTAL)) {
							strDryPer = (String) mpFormulaIngredients
									.get(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
							strDryPer=NumericHelper.getDecimalFormattedValues(strDryPer);
						}
						if (mpFormulaIngredients
								.containsKey(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY)) {
							strWeightDry = (String) mpFormulaIngredients
									.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
							strWeightDry=NumericHelper.getDecimalFormattedValues(strWeightDry);
							
						}
						if (UIUtil.isNotNullAndNotEmpty(strVirtualName)) {
							VirtualIntermediate VI = new VirtualIntermediate(context, strVirtualName);
							strviName = VI.getName();
						}
						formulationProcessesFormula.setName(strName);
						formulationProcessesFormula.setType(strValue);
						formulationProcessesFormula.setSeqNumber(strSequenceNumber);
						formulationProcessesFormula.setTitle(strTitle);
						formulationProcessesFormula.setMinPercentage(strMin);
						formulationProcessesFormula.setWetPercentage(strQuantity);
						formulationProcessesFormula.setMaxPercentage(strMax);
						formulationProcessesFormula.setTargetWetWeight(strWeightWet);
						formulationProcessesFormula.setDryPercentage(strDryPer);
						formulationProcessesFormula.setTargetDryWeight(strWeightDry);
						formulationProcessesFormula.setProcessingLoss(strLoss);
						formulationProcessesFormula.setMaterialFunction(strMaterialFunction);
						formulationProcessesFormula.setVirtualIntermediateName(strviName);
						formulationProcessesFormula.setProcessingNote(strComments);
						strviName = DomainConstants.EMPTY_STRING;
						formulationProcessesFormulasList.add(formulationProcessesFormula);
					}

				}
			} catch (Exception exception) {
				throw exception;
			} finally {
				if (isPushContext) {
					ContextUtil.popContext(context);
					isPushContext = false;
				}
			}

			mlFormulaIngredients.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FormulationProcessData;
	}

	public String getMaterialFunction(Context context, String strRelId) throws Exception {
		String sApplication = DomainConstants.EMPTY_STRING;
		String sMatFun = DomainConstants.EMPTY_STRING;
		StringList sAppList = null;
		StringList sAppMatIds = null;
		String[] sValueArray = null;
		MapList mApplications = null;
		Map objMap = null;
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
	
}
