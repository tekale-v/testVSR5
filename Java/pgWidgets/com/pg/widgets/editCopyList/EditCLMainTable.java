package com.pg.widgets.editCopyList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.awl.dao.ArtworkContent;
import com.matrixone.apps.awl.dao.CopyList;
import com.matrixone.apps.awl.enumeration.AWLAttribute;
import com.matrixone.apps.awl.enumeration.AWLPolicy;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.awl.enumeration.AWLState;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.pg.widgets.rtautil.RTAUtil;
import com.pg.widgets.rtautil.RTAUtilConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class EditCLMainTable {

	private static final Logger logger = Logger.getLogger(EditCLMainTable.class.getName());
	
	public static StringList getAllMAinTableSelects(Context context) {

		StringList selectList = StringList.create(DomainConstants.SELECT_ID, EditCLConstants.KEY_LAST,
				PGWidgetConstants.KEY_REVISIONS, "revisions.id", DomainConstants.SELECT_NAME,
				DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT,
				AWLAttribute.MARKETING_NAME.getSel(context), AWLAttribute.TRANSLATE.getSel(context),
				AWLAttribute.INLINE_TRANSLATION.getSel(context),
				"from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "|to.attribute["
						+ AWLAttribute.IS_BASE_COPY.get(context) + "]=='Yes'].to.attribute[Copy Text_RTE]",
				"type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGSUBCOPYTYPE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECATEGORY + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACEBRAND + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACLASSIFICATION + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTAPLANTCODE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACOPYELEMENTCATEGORY + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACOPYELEMENTTYPE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTADESCRIPTION + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTAFIXEDORVARIABLE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECOUNTRY + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTAEUCLASSIFICATION + "]",
				"attribute[" + EditCLConstants.ATTR_INSTRUCTIONS + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACEPRODUCTFORM + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECROSSSELL + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTAMCCUCODATA + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITONE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITTWO + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITTHREE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECLWARNING + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTAMCPACKAGINGCOMPONENTTYPE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTAMCSIZE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGConfirmAuthorApproverAssignee + "]",
				EditCLConstants.SEL_ATTRIBUTE_PGRTAMCCOMMENT, "attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]");

		return selectList;
	}
	
	public static String getCopyListEditData(Context context, String strCopyListId) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strCopyListId);
		strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
		EditCLUtil.MCSURL = jsonInputData.getString(EditCLConstants.KEY_MCSURL);
		CopyList copylist = new CopyList(strCopyListId);
		DomainObject doCopyList = DomainObject.newInstance(context, strCopyListId);
		Map<?, ?> mCLInfo = BusinessUtil.getInfo(context, strCopyListId,
				StringList.create(DomainConstants.SELECT_CURRENT, EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO));
		String strCopyListCurrent = (String) mCLInfo.get(DomainConstants.SELECT_CURRENT);

		String strCLMotherChildInfo = (String) mCLInfo.get(EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO);

		HashMap<String, Object> mapFinalMCInfo = new HashMap<>();
		HashMap<String, Object> mapFinalLCInfo = new HashMap<>();
		String strOutput = null;

		// Master copy details
		StringList selectListMCE = getAllMAinTableSelects(context);
		StringList mceRelSelects = StringList.create(AWLAttribute.INSTANCE_SEQUENCE.getSel(context), "relID",
				AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));

		long start1 = System.currentTimeMillis();
		MapList mlCopyListMCs = copylist.getArtworkMasters(context, selectListMCE, mceRelSelects,
				DomainConstants.EMPTY_STRING);
		long end1 = System.currentTimeMillis();

		logger.log(Level.INFO, AWLUtil.strcat(
				"Time diff to execute copylist.getArtworkMasters called in getMCDetails :: ", (end1 - start1), " ms"));

		logger.log(Level.INFO, "mlCopyListMCs", mlCopyListMCs);
		StringList strMCIdsList = new StringList();

		// local copy details
		StringList lceRelSelects = StringList.create(AWLAttribute.NOTES.getSel(context),
				"attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
		StringList selectListLCE = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
				DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT,
				EditCLConstants.KEY_LAST, PGWidgetConstants.KEY_REVISIONS, "revisions.id",
				"attribute[" + AWLAttribute.IS_BASE_COPY.get(context) + "]",
				AWLAttribute.MARKETING_NAME.getSel(context), "attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]",
				"attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]",
				AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context),
				"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id",
				"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type",
				"from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id",
				"attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTADISTRIBTYPE + "]",
				EditCLConstants.SEL_ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER);
		MapList mlCopyListLCs = null;

		long start2 = System.currentTimeMillis();
		if (!strCopyListCurrent.equals(AWLState.OBSOLETE.get(context, AWLPolicy.COPY_LIST))) {
			mlCopyListLCs = doCopyList.getRelatedObjects(context, AWLRel.ARTWORK_ASSEMBLY.get(context),
					AWLType.ARTWORK_ELEMENT.get(context), selectListLCE, // Object Select
					lceRelSelects, // rel Select
					false, // get To
					true, // get From
					(short) 0, // recurse level
					null, // where Clause
					null, 0);
		} else {
			mlCopyListLCs = doCopyList.getRelatedObjects(context, AWLRel.ARTWORK_ASSEMBLY_HISTORY.get(context),
					AWLType.ARTWORK_ELEMENT.get(context), selectListLCE, // Object Select
					lceRelSelects, // rel Select
					false, // get To
					true, // get From
					(short) 0, // recurse level
					null, // where Clause
					null, 0);
		}
		long end2 = System.currentTimeMillis();
		logger.log(Level.INFO,
				AWLUtil.strcat("Time diff to execute doCopyList.getRelatedObjects called in getMCDetails :: ",
						(end2 - start2), " ms"));

		long start3 = System.currentTimeMillis();
		
		validityDateCheck(context, mlCopyListMCs, mlCopyListLCs);
		formatCopyElementsForCL(context, mlCopyListMCs, mlCopyListLCs, mapFinalMCInfo, mapFinalLCInfo, strMCIdsList,
				copylist);
		long end3 = System.currentTimeMillis();
		logger.log(Level.INFO, AWLUtil.strcat("Time diff to execute getCopyListMCDetails called in getMCDetails :: ",
				(end3 - start3), " ms"));

		long start5 = System.currentTimeMillis();
		if (BusinessUtil.isNotNullOrEmpty(strCLMotherChildInfo)) {
			motherChildCEContentDifference(context, mapFinalLCInfo, strCLMotherChildInfo);
		}
		long end5 = System.currentTimeMillis();
		logger.log(Level.INFO,
				AWLUtil.strcat("Time diff to execute for loop called in getMCDetails :: ", (end5 - start5), " ms"));

		long start6 = System.currentTimeMillis();
		strOutput = formatResponseForMainTable(context, mapFinalMCInfo, mapFinalLCInfo, strMCIdsList).build()
				.toString();
		long end6 = System.currentTimeMillis();
		logger.log(Level.INFO,
				AWLUtil.strcat("Time diff to execute getFinalMCLCDetailsInJSONFormat :: ", (end6 - start6), " ms"));
		return strOutput;
	}

	private static void validityDateCheck(Context context, MapList mlCopyListMCs, MapList mlCopyListLCs) throws Exception {
		String strValidateDate = null;
		Date date =null;
		Date sytemTodaysDate = null;
		Date sCEValidityDate =null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		for (int i = 0; i < mlCopyListMCs.size(); i++) {
			Map mMCEInfo = (Map<?, ?>) mlCopyListMCs.get(i);
			strValidateDate = (String)mMCEInfo.get(RTAUtilConstants.SELECT_VALIDITY_DATE); 
			if(BusinessUtil.isNotNullOrEmpty(strValidateDate)){
				date = new Date();
				sytemTodaysDate = dateFormat.parse(dateFormat.format(date));
				sCEValidityDate = dateFormat.parse(strValidateDate);
				if(sytemTodaysDate.after(sCEValidityDate))
				{
					mMCEInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, PGWidgetConstants.STRING_TRUE);
				}else {
					mMCEInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, PGWidgetConstants.STRING_FALSE);
				}
			}
		}
		for (int i = 0; i < mlCopyListLCs.size(); i++) {
			Map mLCEInfo = (Map<?, ?>) mlCopyListLCs.get(i);
			strValidateDate = (String)mLCEInfo.get(RTAUtilConstants.SELECT_VALIDITY_DATE); 
			if(BusinessUtil.isNotNullOrEmpty(strValidateDate)){
				date = new Date();
				sytemTodaysDate = dateFormat.parse(dateFormat.format(date));
				sCEValidityDate = dateFormat.parse(strValidateDate);
				if(sytemTodaysDate.after(sCEValidityDate))
				{
					mLCEInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, PGWidgetConstants.STRING_TRUE);
				}else {
					mLCEInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, PGWidgetConstants.STRING_FALSE);
				}
			}
		}
	}

	public static void formatCopyElementsForCL(Context context, MapList mlCopyListMCs, MapList mlCopyListLCs,
			HashMap<String, Object> mapFinalMCInfo, HashMap<String, Object> mapFinalLCInfo, StringList strMCIdsList,
			CopyList copylist) throws Exception {
		Map<?, ?> mMCInfo = null;
		HashMap<Object, Object> mapInfo = null;
		StringList strLCIdsList = new StringList();
		String strArtworkMasterSelectable = ArtworkContent.getArtworkMasterIdSel(context);
		int iCopyListMCsSize = mlCopyListMCs.size();
		HashMap<String, Object> mapMCInfo = new HashMap<>();
		for (int i = 0; i < iCopyListMCsSize; i++) {
			mapInfo = new HashMap<>();
			mMCInfo = (Map<?, ?>) mlCopyListMCs.get(i);
			String strMCId = (String) mMCInfo.get(DomainConstants.SELECT_ID);
			MapList mlMCLang = new MapList();
			MapList mlLCsMapped = new MapList();
			HashMap<String, Object> mapLangInfo = new HashMap<>();
			if (mlCopyListLCs != null && !mlCopyListLCs.isEmpty()) {
				for (int j = 0; j < mlCopyListLCs.size(); j++) {
					mapInfo = new HashMap<>();
					Map mLCInfo = (Map<?, ?>) mlCopyListLCs.get(j);
					String strLCId = (String) mLCInfo.get(DomainConstants.SELECT_ID);
					if (strMCId.equals((String) mLCInfo.get(strArtworkMasterSelectable))) {
						mlMCLang.add(mLCInfo);
						String strMCLCIdsKey = strMCId + PGWidgetConstants.KEY_UNDERSCORE + strLCId;
						if (!strLCIdsList.contains(strMCLCIdsKey)) {
							strLCIdsList.add(strMCLCIdsKey);
							fillLCDetails(context, mLCInfo, strLCId, mapInfo, mapLangInfo);
							mlLCsMapped.add(mapInfo);
						}
					}
					mapMCInfo.put(EditCLConstants.KEY_LANGUAGEINFO, mapLangInfo);
					mapFinalMCInfo.put(strMCId, mapMCInfo);
					mapFinalLCInfo.put(strMCId, mlLCsMapped);
				}
			}
			if (!strMCIdsList.contains(strMCId)) {
				strMCIdsList.add(strMCId);
				fillMCDetails(context, mapFinalMCInfo, mMCInfo, strMCId, mlMCLang);
			}
		}
	}

	private static void fillMCDetails(Context context, HashMap<String, Object> mapFinalMCInfo, Map<?, ?> mapPassed,
			String strMCId, MapList mlMCLang) throws Exception, FrameworkException {
		String strFlag;
		StringList slMCLang;
		StringList slMCNotes;
		String strMCNotes;
		String strImage = null;
		Map mMCInfo = new HashMap();
		mMCInfo.putAll(mapPassed);
		strFlag = EditCLUtil.getCopyElementType(context, mMCInfo);
		slMCLang = BusinessUtil.toStringList(mlMCLang, AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));
		slMCNotes = BusinessUtil.toStringList(mlMCLang, AWLAttribute.NOTES.getSel(context));
		strMCNotes = EditCLUtil.getNotesForMC(slMCNotes);
		JsonArrayBuilder jsonArrRevData = EditCLUtil.getFormattedRevisions(mMCInfo.get(PGWidgetConstants.KEY_REVISIONS),
				mMCInfo);
//		// This block is used for copying the previous instance ID to new MCE while rev switch.
//		if (!instanceSequence.equals("0") && !instanceSequence.equals(DomainConstants.EMPTY_STRING)) {
//			Map attributeMap = new HashMap();
//			DomainRelationship domainRelationship = DomainRelationship.newInstance(context,
//					(String) mMCInfo.get("id[connection]"));
//			attributeMap.put("Instance Sequence", instanceSequence);
//			domainRelationship.setAttributeValues(context, (String) mMCInfo.get("id[connection]"), attributeMap);
//			instanceSequence = DomainConstants.EMPTY_STRING;
//		}
		HashMap<String, Object> mapInfo = new HashMap(); // IMP: used putAll here to break the values passed by reference
		mapInfo.putAll((HashMap<String, Object>) mapFinalMCInfo.get(strMCId));

		mapInfo.put(DomainConstants.SELECT_ID, strMCId);
		mapInfo.put(DomainConstants.SELECT_NAME, mMCInfo.get(DomainConstants.SELECT_NAME));
		mapInfo.put(DomainConstants.SELECT_TYPE, mMCInfo.get(DomainConstants.SELECT_TYPE));
		mapInfo.put(DomainConstants.SELECT_REVISION, mMCInfo.get(DomainConstants.SELECT_REVISION));
		mapInfo.put(DomainConstants.SELECT_CURRENT, mMCInfo.get(DomainConstants.SELECT_CURRENT));
		mapInfo.put(EditCLConstants.KEY_TITLE, mMCInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
		mapInfo.put(EditCLConstants.KEY_FLAG, strFlag);
		mapInfo.put(PGWidgetConstants.KEY_REVISIONS, jsonArrRevData.build().toString());
		mapInfo.put(EditCLConstants.KEY_LAST, mMCInfo.get(EditCLConstants.KEY_LAST));
		if (strFlag.equals(EditCLConstants.STR_GRAPHICTYPE)) {
			try {
				String[] arrayOfString = new String[2];
				arrayOfString[0] = strMCId;
				arrayOfString[1] = EditCLUtil.MCSURL;
				strImage = JPO.invoke(context, "AWLGraphicsElementUI", null, "getGraphicImageURLforPOAAction",
						arrayOfString, String.class);
			} catch (Exception e) {
				throw e;
			}
			mapInfo.put(EditCLConstants.KEY_CONTENT, strImage);
		} else {
			mapInfo.put(EditCLConstants.KEY_CONTENT, mMCInfo
					.get("from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].to.attribute[Copy Text_RTE]"));
		}
		mapInfo.put(EditCLConstants.KEY_LANGUAGES, FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
		mapInfo.put(EditCLConstants.STR_LANG, FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
		mapInfo.put(EditCLConstants.STR_INSSEQ, mMCInfo.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)));
		mapInfo.put(EditCLConstants.STR_ORDER, mMCInfo.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
		mapInfo.put(EditCLConstants.STR_NOTES, strMCNotes);
		mapInfo.put(EditCLConstants.KEY_PGSUBCOPYTYPE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGSUBCOPYTYPE + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTACECATEGORY,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECATEGORY + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTACEBRAND,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACEBRAND + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTACLASSIFICATION,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACLASSIFICATION + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTAPLANTCODE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAPLANTCODE + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTACOPYELEMENTCATEGORY,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACOPYELEMENTCATEGORY + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTACOPYELEMENTTYPE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACOPYELEMENTTYPE + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTADESCRIPTION,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTADESCRIPTION + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTAFIXEDORVARIABLE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAFIXEDORVARIABLE + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTACECOUNTRY,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECOUNTRY + "]"));
		mapInfo.put(EditCLConstants.KEY_PGRTAEUCLASSIFICATION,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAEUCLASSIFICATION + "]"));
		mapInfo.put(EditCLConstants.ATTR_INSTRUCTIONS,
				mMCInfo.get("attribute[" + EditCLConstants.ATTR_INSTRUCTIONS + "]"));
		
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTACEPRODUCTFORM,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACEPRODUCTFORM + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTACECROSSSELL,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECROSSSELL + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTAMCCUCODATA,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAMCCUCODATA + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITONE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITONE + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITTWO,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITTWO + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITTHREE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECONSUMERBENEFITTHREE + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTACECLWARNING,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECLWARNING + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTAMCPACKAGINGCOMPONENTTYPE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAMCPACKAGINGCOMPONENTTYPE + "]"));
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTAMCSIZE,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAMCSIZE + "]"));
		
		mapInfo.put(EditCLConstants.KEY_PGConfirmAuthorApproverAssignee,
				mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGConfirmAuthorApproverAssignee + "]"));

		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTAMCCOMMENT,
				mMCInfo.get(EditCLConstants.SEL_ATTRIBUTE_PGRTAMCCOMMENT));
		
		String strValidityDate = (String) mMCInfo.get("attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]");
		mapInfo.put(EditCLConstants.ATTR_VALIDITY_DATE, strValidityDate);
		mapInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, mMCInfo.get(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED));
		
		Map MCMap = new HashMap();
		MCMap.put(EditCLConstants.STR_MCIDS, strMCId);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonMCString = objectMapper.writeValueAsString(MCMap);
		String baseCopiesOut = RTAUtil.getBaseCopiesForMCEs(context, jsonMCString);
		List<Map<String, String>> mapList = objectMapper.readValue(baseCopiesOut, List.class);
		Map<String, String> map = mapList.get(0);
		mapInfo.put(RTAUtilConstants.KEY_LOCAL_BASE_COPY, map.get(DomainConstants.SELECT_ID));
		mapInfo.put(EditCLConstants.KEY_LOCAL_BASE_COPY_LANGUAGE, map.get(EditCLConstants.KEY_LANGUAGES));

		mapFinalMCInfo.put(strMCId, mapInfo);
	}

	private static void fillLCDetails(Context context, Map mLCInfo, String strLCId, Map mapInfo,
			HashMap<String, Object> mapLangInfo) throws Exception {
		JsonArrayBuilder jsonArrRevData = EditCLUtil.getFormattedRevisions(mLCInfo.get(PGWidgetConstants.KEY_REVISIONS),
				mLCInfo);
		mapInfo.put(PGWidgetConstants.KEY_REVISIONS, jsonArrRevData.build().toString());
		mapInfo.put(EditCLConstants.KEY_LAST, mLCInfo.get(EditCLConstants.KEY_LAST));
		mapInfo.put(DomainConstants.SELECT_ID, strLCId);
		mapInfo.put(DomainConstants.SELECT_NAME, mLCInfo.get(DomainConstants.SELECT_NAME));
		mapInfo.put(DomainConstants.SELECT_REVISION, mLCInfo.get(DomainConstants.SELECT_REVISION));
		mapInfo.put(DomainConstants.SELECT_TYPE, mLCInfo.get(DomainConstants.SELECT_TYPE));
		mapInfo.put(EditCLConstants.KEY_ISBASECOPY, mLCInfo.get(AWLAttribute.IS_BASE_COPY.getSel(context)));
		mapInfo.put(EditCLConstants.KEY_PARENTTYPE,
				mLCInfo.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type"));
		mapInfo.put(EditCLConstants.KEY_PARENT_ID,
				mLCInfo.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id"));
		mapInfo.put(DomainConstants.SELECT_CURRENT, mLCInfo.get(DomainConstants.SELECT_CURRENT));
		mapInfo.put(EditCLConstants.KEY_TITLE, mLCInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
		String strValidityDate = (String) mLCInfo.get("attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]");
		mapInfo.put(EditCLConstants.ATTR_VALIDITY_DATE, strValidityDate);
		String strDstribType = (String) mLCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTADISTRIBTYPE + "]");
		mapInfo.put(EditCLConstants.KEY_PGRTADISTRIBTYPE, strDstribType);
		mapInfo.put(EditCLConstants.KEY_CONTENT, mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
		mapInfo.put(EditCLConstants.KEY_LANGUAGES, mLCInfo.get(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context)));
		String strNotes = (String) mLCInfo.get(AWLAttribute.NOTES.getSel(context));
		mapInfo.put(EditCLConstants.STR_NOTES, strNotes);
		mapInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, mLCInfo.get(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED));
		// strLangId = (String) mLCInfo.get("from[" +
		// AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
		String strLangId = PGWidgetUtil.extractMultiValueSelect(mLCInfo,
				"from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
		mapInfo.put(EditCLConstants.STR_LANGUAGE_ID, strLangId);
		String strAdditionalIfCluster = (String) mLCInfo.get(EditCLConstants.SEL_ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER);
		mapInfo.put(EditCLConstants.ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER, strAdditionalIfCluster);		
		if (com.matrixone.apps.cpn.util.BusinessUtil.isNotNullOrEmpty(strLangId)) {
			mapInfo.put(strLangId, mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
			mapLangInfo.put(strLangId, mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
			mapLangInfo.put(strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.STR_NOTES, strNotes);
			mapLangInfo.put(strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.ATTR_VALIDITY_DATE,
					strValidityDate);
			mapLangInfo.put(strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.KEY_PGRTADISTRIBTYPE,
					strDstribType);
			mapLangInfo.put(strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.ATTRIBUTE_PGRTALCADDITIONALIFCLUSTER,
					strAdditionalIfCluster);			
		}
	}

	public static JsonArrayBuilder formatResponseForMainTable(Context context,
			Map<String, Object> mapFinalMCInfo, Map<String, Object> mapFinalLCInfo, StringList strMCIdsList)
			throws Exception {
		JsonArrayBuilder jsonArrCopyListData = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrErrCopyListData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonObjectBuilder jsonObjErrKeyVariant = Json.createObjectBuilder();
		JsonObjectBuilder jsonLCObjKeyVariant = null;
		JsonArrayBuilder jsonArrData = null;
		String strKey = null;
		String strValue = DomainConstants.EMPTY_STRING;
		Map<?, ?> mapMCData = null;
		Map<?, ?> mapLCData = null;
		MapList mlLCData = new MapList();
		try {
			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {
				jsonArrData = Json.createArrayBuilder();
				jsonObjKeyVariant = Json.createObjectBuilder();
				strKey = entry.getKey();
				mapMCData = (Map<?, ?>) mapFinalMCInfo.get(strKey);
				mlLCData = (MapList) mapFinalLCInfo.get(strKey);

				for (int j = 0; j < mlLCData.size(); j++) {
					mapLCData = (Map<?, ?>) mlLCData.get(j);
					jsonLCObjKeyVariant = Json.createObjectBuilder();
					for (Entry<?, ?> entryMCLC : mapLCData.entrySet()) {
						strKey = (String) entryMCLC.getKey();
						strValue = (String) entryMCLC.getValue();
						if (BusinessUtil.isNotNullOrEmpty(strValue)) {
							jsonLCObjKeyVariant.add(strKey, strValue);
						} else {
							jsonLCObjKeyVariant.add(strKey, DomainConstants.EMPTY_STRING);
						}
					}
					jsonArrData.add(jsonLCObjKeyVariant.build());
				}
				jsonObjKeyVariant.add(EditCLConstants.KEY_LCES, jsonArrData.build());

				for (Entry<?, ?> entryMCLC : mapMCData.entrySet()) {
					Object objValue = entryMCLC.getValue();
					Object objKey = entryMCLC.getKey();
					if (objValue instanceof Map) {
						Map<?, ?> temp = (Map<?, ?>) objValue;
						for (Entry<?, ?> entryMCLCTemp : temp.entrySet()) {
							strKey = (String) entryMCLCTemp.getKey();
							strValue = (String) entryMCLCTemp.getValue();
							if (BusinessUtil.isNotNullOrEmpty(strValue)) {
								jsonObjKeyVariant.add(strKey, strValue);
							} else {
								jsonObjKeyVariant.add(strKey, DomainConstants.EMPTY_STRING);
							}
						}
					} else if (objValue instanceof String) {
						if (BusinessUtil.isNotNullOrEmpty((String)objValue)) {
							jsonObjKeyVariant.add((String) objKey, (String) objValue);
						} else {
							jsonObjKeyVariant.add((String) objKey, DomainConstants.EMPTY_STRING);
						}
					}else if (objValue instanceof StringList) {
						if (BusinessUtil.isNotNullOrEmpty((StringList)objValue)) {
							strValue = PGWidgetUtil.getStringFromSL((StringList)objValue, PGWidgetConstants.KEY_COMMA_SEPARATOR);
								jsonObjKeyVariant.add((String) objKey, strValue);
						} else {
							jsonObjKeyVariant.add((String) objKey, DomainConstants.EMPTY_STRING);
						}
					}
				}
				jsonArrCopyListData.add(jsonObjKeyVariant.build());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonArrErrCopyListData.add(jsonObjErrKeyVariant);
		}
		return jsonArrCopyListData;
	}

	private static void motherChildCEContentDifference(Context context, Map<String, Object> mapFinalMCInfo,
			String strCLMotherChildInfo) throws Exception {
		try {
			String strLangId = null;
			String strLCId = null;
			String strMCId = null;
			String strMotherLangId = null;
			String strKey = null;
			String strMotherContent = null;
			String strChildContent = null;

			MapList mapListData = new MapList();
			Map mapMCData = new HashMap();

			HashMap mCLLangMotherChildInfo = getCLLangMotherChildInfo(context, strCLMotherChildInfo);

			HashMap mMotherChildInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_MOTHERCHILD))
					? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_MOTHERCHILD)
					: new HashMap<>();
			HashMap mChildMotherInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_CHILDMOTHER))
					? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_CHILDMOTHER)
					: new HashMap<>();

			Map<String, String> mLCCopyTextInfo = null;
			Map<String, Map<String, String>> mLCLangInfo = new HashMap();

			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {

				strKey = entry.getKey();

				Object dataFromMap = mapFinalMCInfo.get(strKey);
				if (dataFromMap instanceof Map) {
					mLCCopyTextInfo = new HashMap<String, String>();
					mapMCData = (Map<?, ?>) mapFinalMCInfo.get(strKey);
					mLCCopyTextInfo.put(EditCLConstants.KEY_CONTENT, (String) mapMCData.get(EditCLConstants.KEY_CONTENT));
					mLCCopyTextInfo.put(EditCLConstants.STR_LANGUAGE_ID, (String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID));
					mLCCopyTextInfo.put(EditCLConstants.KEY_PARENT_ID, (String) mapMCData.get(EditCLConstants.KEY_PARENT_ID));
					mLCLangInfo.put((String) mapMCData.get(DomainConstants.SELECT_ID), mLCCopyTextInfo);
				} else if (dataFromMap instanceof MapList) {
					mapListData = (MapList) dataFromMap;
					for (int i = 0; i < mapListData.size(); i++) {
						mLCCopyTextInfo = new HashMap<String, String>();
						mapMCData = (Map<?, ?>) mapListData.get(i);
						mLCCopyTextInfo.put(EditCLConstants.KEY_CONTENT, (String) mapMCData.get(EditCLConstants.KEY_CONTENT));
						mLCCopyTextInfo.put(EditCLConstants.STR_LANGUAGE_ID,
								(String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID));
						mLCCopyTextInfo.put(EditCLConstants.KEY_PARENT_ID, (String) mapMCData.get(EditCLConstants.KEY_PARENT_ID));
						mLCLangInfo.put((String) mapMCData.get(DomainConstants.SELECT_ID), mLCCopyTextInfo);
					}
				}
			}
			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {
				strKey = entry.getKey();
				Object dataFromMap = mapFinalMCInfo.get(strKey);
				if (dataFromMap instanceof Map) {
					mapMCData = (Map<?, ?>) mapFinalMCInfo.get(strKey);
					strLCId = (String) mapMCData.get(DomainConstants.SELECT_ID);
					strLangId = (String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID);
					if (mChildMotherInfo.containsKey(strLangId)) {
						HashMap<String, String> innerMapResult = (HashMap<String, String>) mChildMotherInfo
								.get(strLangId);
						if (innerMapResult != null) {
							strMotherLangId = innerMapResult.get(DomainConstants.SELECT_ID);
						}
						strMCId = (String) mapMCData.get(EditCLConstants.KEY_PARENT_ID);
						strChildContent = (String) mapMCData.get(EditCLConstants.KEY_CONTENT);
						for (Entry<String, Map<String, String>> contentEntry : mLCLangInfo.entrySet()) {
							mLCCopyTextInfo = mLCLangInfo.get(contentEntry.getKey());
							if (BusinessUtil.isNotNullOrEmpty(strMotherLangId) && BusinessUtil.isNotNullOrEmpty(strMCId)
									&& strMotherLangId.equals(mLCCopyTextInfo.get(EditCLConstants.STR_LANGUAGE_ID))
									&& strMCId.equals(mLCCopyTextInfo.get(EditCLConstants.KEY_PARENT_ID))) {
								strMotherContent = (String) mLCCopyTextInfo.get(EditCLConstants.KEY_CONTENT);
								break;
							}
						}
						if (strChildContent != null && !strChildContent.equals(strMotherContent)) {
							mapMCData.put(EditCLConstants.KEY_MOTHERCONTENT, strMotherContent);
							mapMCData.put(EditCLConstants.KEY_CONTENTMISMATCH, AWLConstants.RANGE_TRUE);
						} else {
							mapMCData.put(EditCLConstants.KEY_CONTENTMISMATCH, AWLConstants.RANGE_FALSE);
						}
					}
				} else if (dataFromMap instanceof MapList) {
					mapListData = (MapList) dataFromMap;
					for (int i = 0; i < mapListData.size(); i++) {
						strMotherContent = null;
						mapMCData = (Map<?, ?>) mapListData.get(i);
						strLCId = (String) mapMCData.get(DomainConstants.SELECT_ID);
						strLangId = (String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID);
						if (mChildMotherInfo.containsKey(strLangId)) {
							// strMotherLangId = (String)mChildMotherInfo.get(strLangId);
							HashMap<String, String> innerMapResult = (HashMap<String, String>) mChildMotherInfo
									.get(strLangId);
							if (innerMapResult != null) {
								strMotherLangId = innerMapResult.get(DomainConstants.SELECT_ID);
							}
							strMCId = (String) mapMCData.get(EditCLConstants.KEY_PARENT_ID);
							strChildContent = (String) mapMCData.get(EditCLConstants.KEY_CONTENT);
							for (Entry<String, Map<String, String>> contentEntry : mLCLangInfo.entrySet()) {
								mLCCopyTextInfo = mLCLangInfo.get(contentEntry.getKey());
								if (BusinessUtil.isNotNullOrEmpty(strMotherLangId)
										&& BusinessUtil.isNotNullOrEmpty(strMCId)
										&& strMotherLangId.equals(mLCCopyTextInfo.get(EditCLConstants.STR_LANGUAGE_ID))
										&& strMCId.equals(mLCCopyTextInfo.get(EditCLConstants.KEY_PARENT_ID))) {
									strMotherContent = (String) mLCCopyTextInfo.get(EditCLConstants.KEY_CONTENT);
									break;
								}
							}
							if (strChildContent != null && !strChildContent.equals(strMotherContent)) {
								mapMCData.put(EditCLConstants.KEY_MOTHERCONTENT, strMotherContent);
								mapMCData.put(EditCLConstants.KEY_CONTENTMISMATCH, AWLConstants.RANGE_TRUE);
							} else {
								mapMCData.put(EditCLConstants.KEY_CONTENTMISMATCH, AWLConstants.RANGE_FALSE);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			JsonArrayBuilder jsonArrErrCopyListData = Json.createArrayBuilder();
			JsonObjectBuilder jsonObjErrKeyVariant = Json.createObjectBuilder();
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}

	}

	private static HashMap getCLLangMotherChildInfo(Context context, String strCLLangMotherChildInfo) throws Exception {
		HashMap mCLLangMotherChildInfo = new HashMap();
		try {
			if (BusinessUtil.isNotNullOrEmpty(strCLLangMotherChildInfo)) {
				HashMap mMotherChildInfo = new HashMap();
				HashMap mChildMotherInfo = new HashMap();
				StringList slLangSel = StringList.create(DomainConstants.SELECT_NAME, DomainConstants.SELECT_ID);
				StringList slMotherChild = new StringList();
//				for(String string : strCLLangMotherChildInfo) {
				StringList slSplit = FrameworkUtil.split(strCLLangMotherChildInfo, "$");
				slMotherChild.addAll(slSplit);
//				}
				StringList slChild = null;
				String strMother = null;
				String strChild = null;
				Map mLangInfo = null;
				Map mMotherLangInfo = null;
				MapList mlLangInfo = null;
				Iterator itr = null;
				for (String strMotherChild : (List<String>) slMotherChild) {
					strMother = strMotherChild.substring(0, strMotherChild.indexOf(":"));
					strChild = strMotherChild.substring(strMotherChild.indexOf(":") + 1, strMotherChild.length());
					slChild = FrameworkUtil.split(strChild, ",");
					mlLangInfo = BusinessUtil.getInfo(context, slChild, slLangSel);
					mMotherChildInfo.put(strMother, mlLangInfo);
					itr = mlLangInfo.iterator();
					mMotherLangInfo = new HashMap();
					mMotherLangInfo.put(DomainConstants.SELECT_ID, strMother);
					while (itr.hasNext()) {
						mLangInfo = (Map) itr.next();
						mChildMotherInfo.put((String) mLangInfo.get(DomainConstants.SELECT_ID), mMotherLangInfo);
					}
				}
				mCLLangMotherChildInfo.put(EditCLConstants.KEY_CHILDMOTHER, mChildMotherInfo);
				mCLLangMotherChildInfo.put(EditCLConstants.KEY_MOTHERCHILD, mMotherChildInfo);
			}
		} catch (Exception ex) {
			throw ex;
		}
		return mCLLangMotherChildInfo;
	}

}
