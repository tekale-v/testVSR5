package com.pdfview.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.APP.CountryClearance;
import com.pdfview.impl.APP.CountryClearances;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;

public class GetCountryClearance {

	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetCountryClearance(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public CountryClearances getComponent() {
		return getCountryOfSalesDataListForFCPDF(_context, _OID);
	}

	/**
	 * This method returns the maplist of Country clearance data which will be
	 * displayed in pdf generated
	 * 
	 * @param context
	 * @param strObjectId
	 * @return MapList
	 */
	public CountryClearances getCountryOfSalesDataListForFCPDF(Context context, String objId) {
		long startTime = new Date().getTime();
		CountryClearances countrycleances = new CountryClearances();
		List<CountryClearance> countryClearanceList = countrycleances.getCountryClearance();
		try {
			Map programMap = new HashMap();
			Map mpCC = null;
			programMap.put("parentOID", objId);
			String[] methodargs = JPO.packArgs(programMap);
			MapList mlCountryOfSalesTemp = (MapList) PDFPOCHelper.executeMainClassMethod(context, "pgCountriesOfSale",
					"getPgCountryClearanceData", methodargs);
			if (mlCountryOfSalesTemp != null && !mlCountryOfSalesTemp.isEmpty()) {
				Iterator itrCountryCler = mlCountryOfSalesTemp.iterator();
				while (itrCountryCler.hasNext()) {
					CountryClearance countryClearance = new CountryClearance();
					mpCC = (Map) itrCountryCler.next();
					countryClearance.setCountry((String) mpCC.get(DomainConstants.SELECT_NAME));
					countryClearance.setOverallClearance(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGOVERALLCLEARANCESTATUS + "]"));
					countryClearance.setPsraApproveStatus(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPSRAAPPROVALSTATUS + "]"));
					countryClearance.setClearanceNumber(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCTNUMBER + "]"));
					countryClearance.setRegStatus(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGREGISTRATIONSTATUS + "]"));
					countryClearance.setRegExpirDate(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGREGISTRATIONENDDATE + "]"));
					countryClearance.setPlantRestriction(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPLANTRESTRICTION + "]"));
					countryClearance.setClearanceComments(
							(String) mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCLEARANCECOMMENT + "]"));
					countryClearance.setCountryProductRegistrationNumber((String) mpCC
							.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOUNTRYPRODUCTREGISTRATIONNUMBER + "]"));
					countryClearance.setProductRegClassification((String) mpCC
							.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPRODUCTREGULATORYCLASSIFICATION + "]"));
					countryClearance.setPackingSite(
							(String) mpCC.get("attribute[" + PDFConstant.ATTRIBUTE_PGPACKINGSITE + "]"));
					countryClearance.setManufacturingSite(
							(String) mpCC.get("attribute[" + PDFConstant.ATTRIBUTE_PGMANUFACTURINGSITE + "]"));
					countryClearance.setRegistrationRenewalLeadTime((String) mpCC
							.get("attribute[" + PDFConstant.ATTRIBUTE_PGREGISTRATIONRENEWALLEADTIME + "]"));
					countryClearance.setRegistrationRenewalStatus(
							(String) mpCC.get("attribute[" + PDFConstant.ATTRIBUTE_PGREGISTRATIONRENEWALSTATUS + "]"));
					countryClearanceList.add(countryClearance);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println(
				"Total Time has taken by the  getCountryOfSalesDataListForFCPDF Method is-->" + (endTime - startTime));
		return countrycleances;
	}
}
