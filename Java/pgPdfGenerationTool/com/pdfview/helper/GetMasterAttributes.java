package com.pdfview.helper;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.APP.MasterAttributes;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetMasterAttributes {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";
	CalenderHelper calenderHelper = null;

	public GetMasterAttributes(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public MasterAttributes getComponent() throws Exception {
		calenderHelper = new CalenderHelper(_context);
		return getMasterAttributesData(_context, _OID);
	}

	public MasterAttributes getMasterAttributesData(Context context, String OID) throws Exception {
		MasterAttributes masterAttributes = new MasterAttributes();
		DomainObject partObj = DomainObject.newInstance(context, OID);
		String strPartOriginSource = (String) partObj.getInfo(context,
				pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		String strMasterType = (String) partObj.getInfo(context, DomainConstants.SELECT_TYPE);
		StringList relselectStmts = new StringList(2);
		StringList slselectStmts = new StringList(2);
		slselectStmts.addElement(DomainConstants.SELECT_ID);
		slselectStmts.addElement(DomainConstants.SELECT_NAME);
		relselectStmts.addElement("frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id");
		relselectStmts.addElement("frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.name");
		MapList mlMasterMapList = new MapList();
		MapList mlInfo = new MapList();
		Map mpInfo = new HashMap();
		Map mMasterMap = new HashMap();
		String strMasterid = null;
		String strMasterName = null;
		
		if (strPartOriginSource.equals(pgV3Constants.DSM_ORIGIN)) {
			mlMasterMapList = partObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM,
					pgV3Constants.TYPE_PARTFAMILY, slselectStmts, relselectStmts, true, false, (short) 1, null, null);
			if (!mlMasterMapList.isEmpty()) {

				mMasterMap = (Map) mlMasterMapList.get(0);
				strMasterid = (String) mMasterMap
						.get("frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id");
				strMasterName = (String) mMasterMap
						.get("frommid[" + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.name");
			}
		} else {

			if (!strMasterType.contains(pgV3Constants.TYPE_MASTER)) {
				mlMasterMapList = partObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGMASTER,
						pgV3Constants.SYMBOL_STAR, slselectStmts, null, true, false, (short) 1, null, null);
			}
			if (!mlMasterMapList.isEmpty()) {
				mMasterMap = (Map) mlMasterMapList.get(0);
				strMasterid = (String) mMasterMap.get(DomainObject.SELECT_ID);
				strMasterName = (String) mMasterMap.get(DomainObject.SELECT_NAME);
			}
		}
		if (UIUtil.isNotNullAndNotEmpty(strMasterid)) {
			mpInfo = getMasterSelectableMap(context, strMasterid);
			mlInfo.add(mpInfo);
		}
		if (null != mlInfo && !mlInfo.isEmpty()) {
			Map object1 = new HashMap();
			object1 = (Map) mlInfo.get(0);
			String strMasterClassification = (String) object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
			if (UIUtil.isNotNullAndNotEmpty(strMasterClassification)
					&& strMasterClassification.equalsIgnoreCase(pgV3Constants.RESTRICTED)) {
				strMasterClassification = pgV3Constants.BUSINESS_USE;
			}

			String strMasterPrintingProcess = (String) object1
					.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS + "].to.name");
			strMasterPrintingProcess = StringHelper.convertObjectToString(strMasterPrintingProcess);

			String strMasterDecorationDetails = (String) object1
					.get(pgV3Constants.SELECT_ATTRIBUTE_PGDECORATIONDETAILS);
			Map reqMapp = new HashMap();
			reqMapp.put("objectId", strMasterid);
			Map paramMapLastt = new HashMap();
			paramMapLastt.put("requestMap", reqMapp);

			String[] argsLastt = JPO.packArgs(paramMapLastt);
			String strLastUpdateUsers = (String) PDFPOCHelper.executeMainClassMethod(context, "emxCPNProductData",
					"getLastUpdatedUserForOwnership", argsLastt);
			if (StringHelper.validateString(strLastUpdateUsers)) {
				masterAttributes.setLastUpdateUsers(strLastUpdateUsers);
			}
			masterAttributes.setMasterName(strMasterName);
			String strOwnerMaster = (String) object1.get(DomainConstants.SELECT_OWNER);
			strOwnerMaster = PersonUtil.getFullName(context, strOwnerMaster);
			masterAttributes.setMasterTitle(StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE)));
			masterAttributes.setMasterDescription(
					StringHelper.convertObjectToString(object1.get(DomainConstants.SELECT_DESCRIPTION)));
			masterAttributes.setMasterType(
					UINavigatorUtil.getAdminI18NString("Type",
							StringHelper.convertObjectToString(object1.get(DomainConstants.SELECT_TYPE)),
							context.getSession().getLanguage()));
			masterAttributes.setMasterOrginator(
					StringHelper.convertObjectToString(object1.get(DomainConstants.SELECT_ORIGINATOR)));
			masterAttributes.setMasterRev(
					StringHelper.convertObjectToString(object1.get(DomainConstants.SELECT_REVISION)));
			masterAttributes.setMasterOriginated(getFormattedDate(
					StringHelper.convertObjectToString(object1.get(DomainConstants.SELECT_ORIGINATED))));
			masterAttributes.setMasterSegment(StringHelper.convertObjectToString(
					object1.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.name")));
			masterAttributes.setMasterStage(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE)));
			masterAttributes.setMasterOwner(StringHelper.convertObjectToString(strOwnerMaster));
			masterAttributes.setMasterEffectiveDate(getFormattedDate(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE))));
			masterAttributes.setMasterExpirationDate(getFormattedDate(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE))));
			masterAttributes.setMasterReleaseDate(getFormattedDate(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE))));
			masterAttributes.setMasterPreviousRevDate(getFormattedDate(StringHelper.convertObjectToString(
					object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE))));
			masterAttributes.setMasterManufacturingStatus(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGLIFECYCLESTATUS)));
			masterAttributes.setMasterReasonForChange(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_REASONFORCHANGE)));
			masterAttributes.setMasterLocalDescription(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGLOCALDESCRIPTION)));
			masterAttributes.setMasterOtherNames(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES)));
			masterAttributes.setMasterComments(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT)));
			masterAttributes.setMasterObsoleteDate(getFormattedDate(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE))));
			masterAttributes.setMasterObsoleteComment(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT)));
			masterAttributes.setMasterBrand(StringHelper.convertObjectToString(
					object1.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND + "].to.name")));
			String masterIsBattery=StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGISBATTERY));
			
			masterIsBattery=masterIsBattery.equalsIgnoreCase(pgV3Constants.FALSE)?pgV3Constants.KEY_NO_VALUE:pgV3Constants.KEY_YES_VALUE;
			
			masterAttributes.setMasterIsBattery(masterIsBattery);
			masterAttributes.setMasterContainsBattery(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONTAINSBATTERY)));
			masterAttributes.setMasterBatteryType(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE)));
			masterAttributes.setMasterStoragelimits(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS)));
			masterAttributes.setMasterBaseUnitOfMeasure(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE)));
			masterAttributes.setMasterStorageInfo(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEINFORMATION)));
			masterAttributes.setMasterStorageTemp(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS)));
			masterAttributes.setMasterShippingInfo(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS)));
			masterAttributes.setMasterShippingHazard(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGHAZARDCLASSIFICATION)));
			masterAttributes.setMasterTechnology(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY)));
			masterAttributes.setMasterDensityUOM(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGDENSITYUOM)));
			masterAttributes.setMasterOnShelf(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGONSHELFPRODUCTDENSITY)));
			masterAttributes.setMasterSAPBOMQuantity(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY)));
			masterAttributes.setMasterBaseQuantity(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY)));
			masterAttributes.setMasterIntendedMarkets(StringHelper.convertObjectToString(
					object1.get("from[" + pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE + "].to.name")));
			masterAttributes.setMasterProductExtraVarient(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXTRAVARIANT)));
			masterAttributes.setMasterTemplate(StringHelper
					.convertObjectToString(object1.get("from[" + pgV3Constants.RELATIONSHIP_TEMPLATE + "].to.name")));
			masterAttributes.setMasterPartFamily(
					StringHelper.convertObjectToString(object1.get("to[" + pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM
							+ "].from[" + pgV3Constants.TYPE_PARTFAMILY + "].name")));
			masterAttributes.setMasterReportedFunction(StringHelper.convertObjectToString(object1
					.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION + "].to.name")));
			masterAttributes.setMasterClass(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGCLASS)));
			masterAttributes.setMasterSubClass(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGSUBCLASS)));
			masterAttributes.setMasterODH(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONHEIGHT)));
			masterAttributes.setMasterODL(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONLENGTH)));
			masterAttributes.setMasterODW(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONWIDTH)));
			masterAttributes.setMasterIDW(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONWIDTH)));
			masterAttributes.setMasterIDL(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONLENGTH)));
			masterAttributes.setMasterIDH(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONHEIGHT)));
			masterAttributes.setMasterDIMENSIONUOM(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGDIMENSIONUOM)));
			masterAttributes.setMasterPackagingMaterialType(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGMATERIALTYPE)));
			masterAttributes.setMasterPackComponentType(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE)));
			masterAttributes.setMasterPackagingSize(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE)));
			masterAttributes.setMasterPackagingSizeUOM(
					StringHelper.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM)));
			masterAttributes.setMasterPackagingTechnology(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGTECHNOLOGY)));
			masterAttributes.setMasterLabelingInfo(StringHelper
					.convertObjectToString(object1.get(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION)));
			masterAttributes.setMasterClassification(strMasterClassification);
			masterAttributes.setMasterPrintingProcess(strMasterPrintingProcess);
			masterAttributes.setMasterDecorationDetails(strMasterDecorationDetails);
		}
		return masterAttributes;
	}

	public static Map getMasterSelectableMap(Context context, String strMasterID) throws MatrixException {
		StringList busSelect = new StringList(40);
		busSelect.add(DomainConstants.SELECT_DESCRIPTION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		busSelect.add(DomainConstants.SELECT_TYPE);
		busSelect.add(DomainConstants.SELECT_ORIGINATOR);
		busSelect.add(DomainConstants.SELECT_REVISION);
		busSelect.add(DomainConstants.SELECT_ORIGINATED);
		busSelect.add(DomainConstants.SELECT_OWNER);
		busSelect.add("attribute[" + pgV3Constants.STR_RELEASE_PHASE + "]");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.name");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLOCALDESCRIPTION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_REASONFORCHANGE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLIFECYCLESTATUS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISBATTERY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCONTAINSBATTERY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEINFORMATION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND + "].to.name");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGHAZARDCLASSIFICATION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGDENSITYUOM);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGONSHELFPRODUCTDENSITY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINTENDEDMARKETS);
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE + "].to.name");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXTRAVARIANT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLIFECYCLESTATUS);
		busSelect.add("to[" + pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM + "].from[Part Family].name");
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION + "].to.name");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCLASS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSUBCLASS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONHEIGHT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONLENGTH);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONWIDTH);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONWIDTH);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONLENGTH);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONHEIGHT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGDIMENSIONUOM);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGMATERIALTYPE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGTECHNOLOGY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS + "].to.name");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGDECORATIONDETAILS);
		busSelect.add("to[" + pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + "].from.name");
		busSelect.add("to[" + pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + "].from.to["
				+ pgV3Constants.RELATIONSHIP_CHANGEACTION + "].from.name");
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_TEMPLATE + "].to.name");
		DomainObject partSpecObj1 = DomainObject.newInstance(context, strMasterID);
		Map mapInfo = (Map) partSpecObj1.getInfo(context, busSelect);
		return mapInfo;
	}

	public String getFormattedDate(String strDate) throws MatrixException {
		return calenderHelper.getFormattedDate(strDate);
	}
}
