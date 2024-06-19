package com.pdfview.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.cpn.util.CPNUIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class EnoviaHelper {

	static Map mapTypeAbbreviation = null;

	/**
	 * Retrieve Material State of the given object
	 * 
	 * @param context
	 * @param sObjectId
	 * @param alValue
	 * @return
	 * @throws Exception
	 */
	public String getMaterialState_backup(Context context, String sObjectId, ArrayList<String> alValue)
			throws Exception {
		String sMaterialState = DomainConstants.EMPTY_STRING;
		
		if (alValue != null && !alValue.isEmpty()) {
			String strState = alValue.get(0);
			try {
				DomainObject dom = DomainObject.newInstance(context, sObjectId);
				String sPolicy = dom.getInfo(context, DomainConstants.SELECT_POLICY);
				String strLanguage = context.getSession().getLanguage();
				sMaterialState = EnoviaResourceBundle.getStateI18NString(context, sPolicy, strState, strLanguage);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			} finally {
				if (UIUtil.isNullOrEmpty(sMaterialState))
					sMaterialState = DomainConstants.EMPTY_STRING;
			}
		}
		return sMaterialState;
	}

	/**
	 * Check Access Expression
	 * 
	 * @param context
	 * @param dm               -BusinessObject
	 * @param accessExpression -string expression
	 * @return
	 */
	public static boolean hasExpressionFilterAccess(Context context, BusinessObject dm, String accessExpression) {
		boolean isAccess = false;
		try {
			isAccess = UINavigatorUtil.hasExpressionFilterAccess(context, dm, accessExpression);
		} catch (MatrixException e) {

			e.printStackTrace();
		}
		return isAccess;
	}

	/**
	 * 
	 * @param context
	 * @param sObjectId
	 * @param alValue
	 * @return
	 * @throws Exception
	 */
	public String hideBasicStatus_backup(Context context, String sObjectId, ArrayList<String> alValue)
			throws Exception {
		String basicStatus = DomainConstants.EMPTY_STRING;
		try {
			DomainObject partObj = DomainObject.newInstance(context, sObjectId);
			StringList slSelects = new StringList(1);
			slSelects.addElement(DomainConstants.SELECT_TYPE);

			Map mapRelatedInfo = partObj.getInfo(context, slSelects);

			String sType = ((String) mapRelatedInfo.get(pgV3Constants.SELECT_TYPE));

			if (!pgV3Constants.TYPE_PGMASTERPACKAGINGMATERIALPART.equalsIgnoreCase(sType)
					&& !pgV3Constants.TYPE_MASTERPRODUCTPART.equalsIgnoreCase(sType)) {
				basicStatus = (String) alValue.get(0);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (UIUtil.isNullOrEmpty(basicStatus))
				basicStatus = DomainConstants.EMPTY_STRING;
		}
		return basicStatus;
	}

	/**
	 * Retrieve Display Name
	 * 
	 * @param context
	 * @param sOid    -Object id
	 * @param alValue
	 * @return String -Display name
	 */
	public static String getTypeName(Context context, String sOid, ArrayList<String> alValue) {
		String sResults = DomainConstants.EMPTY_STRING;
		if (alValue != null && !alValue.isEmpty()) {
			String sType = alValue.get(0);
			try {
				sResults = getTypeName(context, sType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sResults;
	}

	/**
	 * Retrieve Display Name
	 * 
	 * @param context
	 * @param sType   -Object Type
	 * @return String -Display name
	 */
	public static String getTypeName(Context context, String sType) {
		String sResults = DomainConstants.EMPTY_STRING;
		try {
			sResults = UINavigatorUtil.getAdminI18NString("Type", sType, context.getSession().getLanguage());
		} catch (MatrixException e) {

			e.printStackTrace();
		}
		return sResults;
	}

	/**
	 * Retrieve State name based on object policy
	 * @param context
	 * @param ObjState -state of the object
	 * @param ObjPolicy-policy of the boject
	 * @return String-state name
	 */
	public static String getStateName(Context context, String ObjState, String ObjPolicy) {
		String strObjectDisplay = DomainConstants.EMPTY_STRING;
		try {
			strObjectDisplay = EnoviaResourceBundle.getStateI18NString(context, ObjPolicy, ObjState,
					context.getLocale().getLanguage());
		} catch (MatrixException e) {

			e.printStackTrace();
		}
		return strObjectDisplay;
	}

	/**
	 * Retrieve policy state
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return String -Object state name
	 */
	public static String getStateName(Context context, String sOid, ArrayList<String> alValue) {
		String strObjectDisplay = DomainConstants.EMPTY_STRING;
		if (alValue != null && !alValue.isEmpty()) {
			String ObjState = alValue.get(0);
			String ObjPolicy = alValue.get(1);
			try {
				strObjectDisplay = getStateName(context, ObjState, ObjPolicy);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		return strObjectDisplay;
	}

	/**
	 * Retrieve input Person name
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return String -Person Name
	 */
	public static String getOwnerName(Context context, String sOid, ArrayList<String> alValue) {
		String strOwnerName = DomainConstants.EMPTY_STRING;
		if (alValue != null && !alValue.isEmpty()) {
			String sOwner = alValue.get(0);
			try {
				strOwnerName = PersonUtil.getFullName(context, sOwner);
			} catch (MatrixException e) {

				e.printStackTrace();
			}
		}
		return strOwnerName;
	}

	/**
	 * Retrieve context user roles
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return string- role data
	 */
	public static String getUserRole(Context context, String sOid, ArrayList<String> alValue) {
		StringBuilder sResults = new StringBuilder();
		try {
			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)) {
				sResults.append(pgV3Constants.ROLE_PGCONTRACTSUPPLIER + pgV3Constants.SYMBOL_COMMA);
			}
			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)) {
				sResults.append(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER + pgV3Constants.SYMBOL_COMMA);
			}
			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTPACKER)) {
				sResults.append(pgV3Constants.ROLE_PGCONTRACTPACKER + pgV3Constants.SYMBOL_COMMA);
			}
			if (context.isAssigned(pgV3Constants.ROLE_IPMWAREHOUSEREADER)) {
				sResults.append(pgV3Constants.ROLE_IPMWAREHOUSEREADER + pgV3Constants.SYMBOL_COMMA);
			}
			if (!UIUtil.isNotNullAndNotEmpty(sResults.toString())) {
				sResults.append(PDFConstant.ROLE_INTERNAL_USER);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return sResults.toString();
	}

	/**
	 * Retrieve Type Abbreviation Name
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return String -Abbreviation Name
	 */
	public static String getTypeAbbreviation(Context context, String sOid, ArrayList<String> alValue) {
		String sResults = DomainConstants.EMPTY_STRING;
		if (alValue != null && !alValue.isEmpty()) {
			String sType = alValue.get(0);
			try {
				sResults = (String) getTypeDisplayName(context, sType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sResults;
	}

	/**
	 * Get short type name
	 * 
	 * @param context
	 * @param obj
	 * @return String -Short name of the type
	 */
	public static Object getTypeDisplayName(Context context, Object obj) {
		StringList slDisplayName = new StringList(1);
		Object DisplayName = null;
		
		if (mapTypeAbbreviation == null || mapTypeAbbreviation.isEmpty()) {
			mapTypeAbbreviation = new HashMap();
			mapTypeAbbreviation.put("pgApprovedSupplierList", "ASL");
			mapTypeAbbreviation.put("pgArtwork", "ART");
			mapTypeAbbreviation.put("pgBaseFormula", "BSF");
			mapTypeAbbreviation.put("ECR", "CR");
			mapTypeAbbreviation.put("pgCommonPerformanceSpecification", "CPST");
			mapTypeAbbreviation.put("pgConsumerDesignBasis", "CDB");
			mapTypeAbbreviation.put("pgFinishedProduct", "FP");
			mapTypeAbbreviation.put("pgIllustration", "ILST");
			mapTypeAbbreviation.put("pgLogisticSpec", "LOG");
			mapTypeAbbreviation.put("pgMakingInstructions", "MI");
			mapTypeAbbreviation.put("pgMasterPackingMaterial", "MPMS");
			mapTypeAbbreviation.put("pgMasterRawMaterial", "MRMS");
			mapTypeAbbreviation.put("pgMasterFinishedProduct", "MPS");
			mapTypeAbbreviation.put("pgPackingMaterial", "MATL");
			mapTypeAbbreviation.put("pgRawMaterial", "MATL");
			mapTypeAbbreviation.put("Shared Table", "CPS");
			mapTypeAbbreviation.put("pgPhase", "FIL");
			mapTypeAbbreviation.put("pgNonGCASPart", "NGCAS");
			mapTypeAbbreviation.put("pgPackingInstructions", "PI");
			mapTypeAbbreviation.put(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY, "PSUB");
			mapTypeAbbreviation.put("pgProcessStandard", "PROS");
			mapTypeAbbreviation.put("pgApplianceProduct", "APP");
			mapTypeAbbreviation.put("pgAssembledProduct", "ASP");
			mapTypeAbbreviation.put("pgFormulatedProduct", "FC");
			mapTypeAbbreviation.put("pgQualitySpecification", "QUAL");
			mapTypeAbbreviation.put("pgStackingPattern", "SPS");
			mapTypeAbbreviation.put("pgStandardOperatingProcedure", "SOP");
			mapTypeAbbreviation.put("pgSupplierInformationSheet", "SIS");
			mapTypeAbbreviation.put("pgTestMethod", "TM");
			mapTypeAbbreviation.put("Safety And Regulatory Characteristic", "SRS");
			mapTypeAbbreviation.put("pgFormulatedMaterial", "FMATL");
			mapTypeAbbreviation.put("pgMaterial", "MATL");
			mapTypeAbbreviation.put("pgPPMConstituent", "Constituent");
			mapTypeAbbreviation.put("Packaging Material Part", "PMP");
			mapTypeAbbreviation.put(pgV3Constants.TYPE_TESTMETHOD, "TMS");
		}
		if (obj instanceof StringList) {
			StringList slActualType = (StringList) obj;
			if (slActualType != null && !slActualType.isEmpty()) {
				String KeyValue =DomainConstants.EMPTY_STRING;
				for (Object key : slActualType) {
					KeyValue = (String) mapTypeAbbreviation.get(key);
					slDisplayName.add(KeyValue);
				}
			}
			DisplayName = slDisplayName;
		} else if (obj instanceof String) {
			String KeyValue = (String) mapTypeAbbreviation.get((String) obj);
			DisplayName = KeyValue;
		}
		if (null == DisplayName) {
			DisplayName = DomainConstants.EMPTY_STRING;
		}
		return DisplayName;
	}

	/**
	 * Retrieve Type name based on specification sub type
	 * 
	 * @param context
	 * @param objectId -object id
	 * @param alValue
	 * @return String -type name
	 * @throws Exception
	 */
	public String getTypeBasedSpecificationSubType(Context context, String objectId, ArrayList<String> alValue)
			throws Exception {
		String sType = DomainConstants.EMPTY_STRING;
		if (alValue != null && !alValue.isEmpty()) {
			Map<String, String> specMap = new HashMap();
			specMap.put(DomainConstants.SELECT_TYPE, alValue.get(0));
			specMap.put(pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE, alValue.get(1));
			specMap.put(pgV3Constants.ATTRIBUTE_PGCSSTYPE, alValue.get(2));
			sType = getSpecificationSubType(context, specMap);
		}
		return sType;
	}

	/**
	 * Retrieve Specification Sub Type
	 * 
	 * @param context
	 * @param specMap
	 * @return String -Type name
	 */
	public String getSpecificationSubType(Context context, Map<String, String> specMap) {
		String strSpecSubType = DomainConstants.EMPTY_STRING;
		try {
			if (null != specMap && !specMap.isEmpty()) {
				String strType = specMap.get(DomainConstants.SELECT_TYPE);
				String pgAssemblyType = specMap.get(pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE);
				String pgCSSType = specMap.get(pgV3Constants.ATTRIBUTE_PGCSSTYPE);
				if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strType)) {
					if (UIUtil.isNullOrEmpty(pgAssemblyType)) {
						strSpecSubType = "Finished Product";
					} else if (PDFConstant.PGASSEMBLY_TYPE_FWIP.equals(pgAssemblyType)) {
						strSpecSubType = PDFConstant.PGASSEMBLY_TYPE_FWIP;
					} else if (PDFConstant.SUBASSEMBLY_TYPE_PURCHASED.equals(pgAssemblyType)) {
						strSpecSubType = PDFConstant.SUBASSEMBLY_TYPE_PURCHASED;
					} else if ("Purchased and/or Produced Subassembly".equals(pgAssemblyType)) {
						strSpecSubType = "Purchased and/or Produced Subassembly";
					} else {
						strSpecSubType = pgAssemblyType;
					}
				}
				if (pgV3Constants.TYPE_PGQUALITYSPECIFICATION.equals(strType)) {
					strSpecSubType = pgCSSType;
				}
				if (pgV3Constants.TYPE_PGTESTMETHOD.equals(strType)) {
					if ("TAMU".equals(pgCSSType)) {
						strSpecSubType = "TAMU";
					} else {
						strSpecSubType = DomainConstants.EMPTY_STRING;
					}
				}
				if (pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strType)) {
					strSpecSubType = "Packaging";
				}
				if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strType)) {
					strSpecSubType = "Raw";
				}
				if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strType)) {
					if (StringUtils.isNotBlank(pgAssemblyType)) {
						strSpecSubType = pgAssemblyType;
					} else {
						strSpecSubType = "PSUB";
					}
				}
				if (pgV3Constants.TYPE_TESTMETHOD.equals(strType)) {
					strSpecSubType = pgAssemblyType;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception in getSpecificationSubType Method ! " + e.getMessage());
		}
		return strSpecSubType;
	}

	/**
	 * Retrieve last updated user name
	 * 
	 * @param context
	 * @param sOid    -obejct id
	 * @param alValue
	 * @return String- last updated user name
	 */
	public static String getLastUpdateUser(Context context, String sOid, ArrayList<String> alValue) {
		String strLastUpdateUser = DomainConstants.EMPTY_STRING;
		try {
			HashMap reqMap = new HashMap();
			reqMap.put(PDFConstant.OBJECT_ID, sOid);
			HashMap paramMapLast = new HashMap();
			paramMapLast.put("requestMap", reqMap);
			String[] argsLast = JPO.packArgs(paramMapLast);
			strLastUpdateUser = (String) PDFPOCHelper.executeMainClassMethod(context, "emxCPNProductData",
					"getLastUpdatedUserForOwnership", argsLast);
		} catch (Exception e) {
			System.out.println("Exception in getLastUpdateUser Method ! " + e.getMessage());
		}
		return strLastUpdateUser;
	}

	/**
	 * This method gets Source field value of Reference Document Table.It is created
	 * to avoid StringIndexOutOfBound Exception given by "showConnectionOfRefDoc"
	 * method of emxCPNCommonDocumentUIBase JPO.
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return String -Source field value
	 * @throws Exception
	 */
	public static String getSourceName(Context context, String sOid, ArrayList<String> alValue) throws Exception {
		DomainObject dmObj = DomainObject.newInstance(context, sOid);
		Map charMap = null;
		StringList strFinalList = new StringList();
		StringList strCharList = new StringList();
		int n =0;
		String strType =DomainConstants.EMPTY_STRING;
		String strFinalType = DomainConstants.EMPTY_STRING;
		String strSource = CPNUIUtil.getProperty(context, "emxCPN.Table.Label.Direct");
		StringList strSelectList = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_TYPE);
		MapList charList = dmObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,
				pgV3Constants.TYPE_CHARACTERISTIC, strSelectList, null, false, true, (short) 1, null, null, 0);
		if (charList != null && !charList.isEmpty()) {
			Iterator charListIterator = charList.iterator();
			while (charListIterator.hasNext()) {
				charMap = (Map) charListIterator.next();
				strType = (String) charMap.get(DomainConstants.SELECT_TYPE);
				n = strType.lastIndexOf(" ");
				if (n < 0) {
					n = strType.length();
				}
				strType = strType.substring(0, n);

				if (!strCharList.contains(strType)) {
					strCharList.add(strType);
				}
			}
			if (strCharList.size() > 1) {
				int strCharListSize=strCharList.size();
				for (int i = 0; i < strCharListSize; i++) {
					strFinalType = strFinalType + "," + strCharList.get(i);
				}
				strFinalType = strFinalType.substring(1, strFinalType.length());
			} else {
				strFinalType = (String) strCharList.get(0);
			}
			strFinalList.add(strFinalType);
			strCharList.clear();
			strFinalType = DomainConstants.EMPTY_STRING;
		} else {
			strFinalList.add(strSource);
		}
		return StringHelper.convertObjectToString(strFinalList);
	}

	/**
	 * Retrieve Product form name
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return
	 */
	public static String getProductFormName(Context context, String sOid, ArrayList<String> alValue) {
		String strProductForm = DomainConstants.EMPTY_STRING;
		try {
			if (alValue != null && !alValue.isEmpty()) {
				String objectId = alValue.get(0);
				HashMap mpParam = new HashMap();
				DomainObject dom = DomainObject.newInstance(context, objectId);
				StringList slObjectSelects = new StringList(1);
				slObjectSelects.add(DomainConstants.SELECT_TYPE);

				Map<String, String> mapObjData = dom.getInfo(context, slObjectSelects);
				String strTType = (String) mapObjData.get(DomainConstants.SELECT_TYPE);
				mpParam.put(PDFConstant.OBJECT_ID, objectId);
				Map settings = new HashMap();
				if (strTType.equalsIgnoreCase(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART)
						|| strTType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPART)
						|| strTType.equalsIgnoreCase(pgV3Constants.TYPE_DEVICEPRODUCTPART)) {
					settings.put("AttributeName", "ProductFormForProduct");
				} else {
					settings.put("AttributeName", "ProductForm");
				}
				Map fieldMap = new HashMap();
				fieldMap.put("settings", settings);
				mpParam.put("fieldMap", fieldMap);

				String[] argsSubs = JPO.packArgs(mpParam);
				strProductForm = (String) PDFPOCHelper.executeMainClassMethod(context, "pgDSOCPNProductData",
						"getConnectedProductForm", argsSubs);
			}
		} catch (Exception e) {
			System.out.println("Exception in getProductFormName Method ! " + e.getMessage());
		}

		return strProductForm;
	}

	/**
	 * Replace pipe to comma (if multiple values coming)
	 * 
	 * @param context
	 * @param sOid
	 * @param alValue
	 * @return String -filtered output string
	 */
	public static String filterContents(Context context, String sOid, ArrayList<String> alValue) {
		String strContent = DomainConstants.EMPTY_STRING;
		if (alValue != null && !alValue.isEmpty()) {
			strContent = alValue.get(0);
			strContent = strContent.replaceAll("\\|", PDFConstant.SYMBOL_COMMA);
		}
		return strContent;
	}

	/**
	 * This method loads all abbreviated types in map
	 * @param context
	 * @param strTypeName
	 * @return
	 */
	public static String loadShortType(Context context, String strTypeName) {
		HashMap<String, String> hmShortMap = new HashMap<String, String>();
		String shortTypeName = DomainConstants.EMPTY_STRING;
		try {
			String strAttrValue = null;
			String strType = null;
			String strLongTypeName = null;
			String strShortTypeName = null;
			Attribute attribute = null;
			StringList slShortName = new StringList();
			BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					pgV3Constants.PGTYPEMAPPING, PDFConstant.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
			if (boConfig.exists(context)) {
				attribute = boConfig.getAttributeValues(context, pgV3Constants.ATTRIBUTE_PGTYPEMAPPING);
				strAttrValue = attribute.getValue().trim();
				StringList slTypeName = FrameworkUtil.split(strAttrValue, PDFConstant.SYMBOL_COMMA);
				int iTypeNameCount = slTypeName.size();
				for (int iCount = 0; iCount < iTypeNameCount; iCount++) {
					strType = slTypeName.get(iCount).toString();
					if (UIUtil.isNotNullAndNotEmpty(strType)) {
						slShortName = FrameworkUtil.split(strType, ":");
						if (!slShortName.isEmpty()) {
							strLongTypeName = slShortName.get(0).toString();
							strShortTypeName = slShortName.get(1).toString();
							if (strLongTypeName != null && strShortTypeName != null) {
								hmShortMap.put(strLongTypeName, strShortTypeName);
							}
						}
					}
				}
			}
			shortTypeName = hmShortMap.get(strTypeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shortTypeName;
	}

}
