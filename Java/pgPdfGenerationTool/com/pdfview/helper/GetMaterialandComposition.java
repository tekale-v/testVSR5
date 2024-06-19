package com.pdfview.helper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

import com.dassault_systemes.enovia.apps.materialcomposition.enumeration.MATCSchema;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FOP.MaterialsComposition;
import com.pdfview.impl.FOP.MaterialsCompositions;
import com.pg.v3.custom.pgV3Constants;

public class GetMaterialandComposition {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetMaterialandComposition(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * @Desc Show Substances and Materials table for PDF
	 * @param        Context- Context user
	 * @param String - Object ID
	 * @return StringBuilder- Substances and Materials Table
	 * @throws FrameworkException
	 * @throws Exception
	 */
	public MaterialsCompositions getComponent() throws FrameworkException {
		MaterialsCompositions MaterialsCompositions = new MaterialsCompositions();
		List<MaterialsComposition> materialsCompositions = MaterialsCompositions.getMaterialandComposition();
		String strName = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strId = DomainConstants.EMPTY_STRING;
		String strisTargetMaterial = DomainConstants.EMPTY_STRING;
		String strSubstanName = DomainConstants.EMPTY_STRING;
		String strQSTarget = DomainConstants.EMPTY_STRING;
		String strDescr = DomainConstants.EMPTY_STRING;
		String strConId = DomainConstants.EMPTY_STRING;
		DomainRelationship relObj = null;
		String strapp = DomainConstants.EMPTY_STRING;
		StringList appList = null;
		StringList appIds = null;
		String[] valueArray = null;
		StringList selectList = new StringList(1);
		selectList.add(DomainConstants.SELECT_NAME);
		StringBuilder strBuilder = null;
		Map objMap = null;
		MapList mapApplications = null;
		String strTitle = null;
		String strValue = DomainConstants.EMPTY_STRING;
		String strMin = null;
		String strTarget = DomainConstants.EMPTY_STRING;
		String strMax = DomainConstants.EMPTY_STRING;
		String strQS = DomainConstants.EMPTY_STRING;
		String strSubCAS = DomainConstants.EMPTY_STRING;
		String strRev = DomainConstants.EMPTY_STRING;
		String strUOM = DomainConstants.EMPTY_STRING;
		String strIsContaminant = DomainConstants.EMPTY_STRING;
		String strFunction = DomainConstants.EMPTY_STRING;
		String strState = DomainConstants.EMPTY_STRING;
		String strUsageFlag = DomainConstants.EMPTY_STRING;
		String strPolicy = DomainConstants.EMPTY_STRING;
		String strCurrentState = DomainConstants.EMPTY_STRING;
		Map mapObjDetails = null;
		Map mapObject = null;
		DomainObject doObj = null;
		boolean isPushContext = false;
		StringList objectSelects = new StringList(6);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_TYPE);
		objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MARKETINGNAME);
		objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER);
		Map mpArgs = new HashMap();
		DomainObject domContextPart =null;
		Vector usageFlagVector =null;
		Vector qsTargetVector = null;
		mpArgs.put("objectId", _OID);
		String strInternalMaterialFor = DomainConstants.EMPTY_STRING;
		StringList slQuantities = DomainConstants.EMPTY_STRINGLIST;
		BigDecimal dQuantites = new BigDecimal(0.0);
		StringBuilder sbQuantitySelect = new StringBuilder("from[")
				.append(pgV3Constants.RELATIONSHIP_COMPONENT_SUBSTANCE).append("].")
				.append(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
		mpArgs.put("expandLevel", "0");
		DomainObject domObjectSub = DomainObject.newInstance(_context, _OID);
		String strObjType = domObjectSub.getInfo(_context, DomainConstants.SELECT_TYPE);
		String[] strArgsDSO = JPO.packArgs(mpArgs);
		try {

			String strContext = _context.getUser();
			if (pgV3Constants.PERSON_USER_AGENT.equals(strContext)) {
				ContextUtil.popContext(_context);
				isPushContext = true;
			}
			MapList mlSubstancesMaterialsObjects = (MapList) PDFPOCHelper.executeMainClassMethod(_context,
					"emxCPNMaterial", "getConsolidatedComposition", strArgsDSO);
			if (mlSubstancesMaterialsObjects != null && !mlSubstancesMaterialsObjects.isEmpty()) {
				int mlSubstancesMaterialsObjectsSize=mlSubstancesMaterialsObjects.size();
				int ilQuantitiessize=0;
				for (int i = 0; i < mlSubstancesMaterialsObjectsSize; i++) {
					MaterialsComposition materialsComposition = new MaterialsComposition();

					mapObject = new HashMap();
					mapObject = (Map) mlSubstancesMaterialsObjects.get(i);
					strId = (String) mapObject.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strId)) {
						doObj = DomainObject.newInstance(_context, strId);
						mapObjDetails = new HashMap();
						mapObjDetails = (Map) doObj.getInfo(_context, objectSelects);
						strName = (String) mapObject.get(DomainConstants.SELECT_NAME);
						strType = (String) mapObject.get(DomainConstants.SELECT_TYPE);
						strTitle = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						if (UIUtil.isNotNullAndNotEmpty(strTitle)) {
							strTitle = StringHelper.filterLessAndGreaterThanSign(strTitle);
						}
						strMin = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_MINIMUMWEIGHT);
						strTarget = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						strMax = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_MAXIMUMWEIGHT);
						strUOM = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
						strQS = (String) mapObject.get(MATCSchema.Attribute.FILL.getAttributeSelect(_context));
						strIsContaminant = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_ISCONTAMINANT);
						strisTargetMaterial = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_ISTARGETMATERIAL);
						strSubstanName = (String) mapObject.get(pgV3Constants.SELECT_ATTRIBUTE_SUBSTANCENAME);
						if (UIUtil.isNotNullAndNotEmpty(strSubstanName)) {
							strSubstanName = StringHelper.filterLessAndGreaterThanSign(strSubstanName);

						}
						strDescr = (String) mapObject.get("description");
						if (UIUtil.isNullOrEmpty(strDescr)) {
							strDescr = DomainConstants.EMPTY_STRING;
						}
						strDescr = StringHelper.filterLessAndGreaterThanSign(strDescr);
						strConId = (String) mapObject.get(PDFConstant.ID_CONNECTION);
						if (UIUtil.isNotNullAndNotEmpty(strConId)) {
							relObj = new DomainRelationship(strConId);
							strapp = relObj.getAttributeValue(_context, "Application");
							appList = new StringList(FrameworkUtil.split(strapp, pgV3Constants.SYMBOL_COMMA));
							appIds = new StringList();
							Iterator appsItr = appList.iterator();
							while (appsItr.hasNext()) {
								appIds.add(((String) appsItr.next()).trim());
							}
							valueArray = (String[]) appIds.toArray(new String[] {});
							mapApplications = DomainObject.getInfo(_context, valueArray, selectList);
							strBuilder = new StringBuilder();
							Iterator it = mapApplications.iterator();
							while (it.hasNext()) {
								objMap = (Map) it.next();
								strBuilder.append(objMap.get(DomainConstants.SELECT_NAME));
								if (it.hasNext())
									strBuilder.append(pgV3Constants.SYMBOL_COMMA);
							}
							strFunction = strBuilder.toString();
							if (UIUtil.isNotNullAndNotEmpty(strFunction)) {
								strFunction = StringHelper.filterLessAndGreaterThanSign(strFunction);

							}
						}
						strSubCAS = (String) mapObjDetails.get(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER);
						strRev = (String) mapObject.get(DomainConstants.SELECT_REVISION);
						strState = (String) mapObject.get(DomainConstants.SELECT_CURRENT);
						strPolicy = (String) mapObject.get(DomainConstants.SELECT_POLICY);
						strCurrentState = EnoviaResourceBundle.getStateI18NString(_context, strPolicy, strState,
								_context.getLocale().getLanguage());
						strValue = UINavigatorUtil.getAdminI18NString("Type", strType,
								_context.getSession().getLanguage());
						strQSTarget = (strQS.equals(pgV3Constants.CAPS_TRUE)) ? "QS"
								: (strisTargetMaterial.equals(pgV3Constants.CAPS_TRUE)) ? PDFConstant.TARGET_COMPONENT
										: DomainConstants.EMPTY_STRING;
						if (strMin.equals("0.0") || pgV3Constants.CONST_TRUE.equalsIgnoreCase(strIsContaminant))
							strMin = DomainConstants.EMPTY_STRING;
						if (pgV3Constants.CONST_TRUE.equalsIgnoreCase(strIsContaminant)) {
							strTarget = DomainConstants.EMPTY_STRING;
						} else if (pgV3Constants.TYPE_INTERNALMATERIAL.equalsIgnoreCase(strType)) {
							domContextPart = DomainObject.newInstance(_context, _OID);
							strInternalMaterialFor = domContextPart.getInfo(_context,
									"attribute[" + pgV3Constants.ATTRIBUTE_INTERNAL_MATERIAL_FOR + "]");
							if (pgV3Constants.CONSTANT_PRODUCT.equalsIgnoreCase(strInternalMaterialFor)) {
								slQuantities = domContextPart.getInfoList(_context, sbQuantitySelect.toString());
								slQuantities.sort();
								ilQuantitiessize=slQuantities.size();
								for (int j = 0; j < ilQuantitiessize; j++) {
									dQuantites = dQuantites.add(new BigDecimal((String) slQuantities.get(j)));
								}
								if (dQuantites.compareTo(new BigDecimal(0.00)) != 0) {
									strTarget = String.valueOf(dQuantites);
								}
							}
						}
						if (strMax.equals("0.0"))
							strMax = DomainConstants.EMPTY_STRING;
						materialsComposition.setName(strName);
						materialsComposition.setType(strValue);
						materialsComposition.setTitle(StringHelper.validateString1(strTitle));
						materialsComposition.setMinQuantity(strMin);
						materialsComposition.setDry(strTarget);
						materialsComposition.setMaxQuantity(strMax);
						materialsComposition.setQuantityUofM(strUOM);
						materialsComposition.setIsContaminant(strIsContaminant);
						if ((pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(strObjType))
								|| (pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(strObjType))) {
							usageFlagVector = getSubstanceMaterialTableUsageFlag(_context, mapObject);
							if ((usageFlagVector != null) && (!usageFlagVector.isEmpty())) {
								strUsageFlag = (String) usageFlagVector.get(0);
							}
							qsTargetVector = getSubstanceMaterialTableQSTarget(_context, mapObject);
							if ((qsTargetVector != null) && (!qsTargetVector.isEmpty())) {
								strQSTarget = (String) qsTargetVector.get(0);
							}
							materialsComposition.setqSTarget(strQSTarget);
							materialsComposition.setUsageFlags(strUsageFlag);
						} else {
							materialsComposition.setqSTarget(strQSTarget);
						}
						materialsComposition.setApplicationsFunctions(strFunction);
						materialsComposition.setcASNumber(strSubCAS);
						materialsComposition.setState(strCurrentState);
						materialsComposition.setRevision(strRev);
						materialsComposition.setDescription(strDescr);
						materialsCompositions.add(materialsComposition);
					}
				}
			}
			if (isPushContext) {
				ContextUtil.pushContext(_context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING);
				isPushContext = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isPushContext) {
				ContextUtil.popContext(_context);
				isPushContext = false;
			}
		}
		return MaterialsCompositions;

	}

	/**
	 * @Desc Get attribute - field Usage Flag for Substance & Material
	 *       Table
	 * @param     Context- Matrix user
	 * @param Map - info map
	 * @return Vector - containing field data.
	 * @throws Exception
	 */
	public Vector getSubstanceMaterialTableUsageFlag(Context context, Map infoMap) throws Exception {

		MapList objectList = new MapList();
		objectList.add(infoMap);

		Map argsMap = new HashMap();
		argsMap.put("objectList", objectList);
		return (Vector) JPO.invoke(context, "emxCPNMaterial", null, "getUsageFlags", JPO.packArgs(argsMap),
				Object.class);

	}

	/**
	 * Get attribute - field QS Target for Substance & Material
	 *       Table
	 * @param     Context- Matrix user
	 * @param Map - info map
	 * @return Vector - containing field data.
	 * @throws Exception
	 */
	public Vector getSubstanceMaterialTableQSTarget(Context context, Map infoMap) throws Exception {

		MapList objectList = new MapList();
		objectList.add(infoMap);
		Map argsMap = new HashMap();
		argsMap.put("objectList", objectList);
		return (Vector) JPO.invoke(context, "emxCPNMaterial", null, "getQSTargetFromColumn", JPO.packArgs(argsMap),
				Object.class);
	}

}
