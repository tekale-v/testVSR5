package com.pdfview.helper;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.CountryOfSale;
import com.pdfview.impl.FPP.CountryOfSales;
import com.pdfview.impl.FPP.DerivedParts;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.Context;
import matrix.util.StringList;

public class GetCountryOfSale {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetCountryOfSale(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public CountryOfSales getComponent() {
		return simpleReportIPSFORPDF(_context, _OID);
	}

	public CountryOfSales simpleReportIPSFORPDF(Context _context, String strObjectId) {
		CountryOfSales countryOfSales = new CountryOfSales();
		String sRev = DomainConstants.EMPTY_STRING;
		String sGcas = DomainConstants.EMPTY_STRING;
		DomainObject dobj = null;
		try {
			StringList slbusSelect = new StringList(5);
			slbusSelect.add(DomainConstants.SELECT_NAME);
			slbusSelect.add(DomainConstants.SELECT_REVISION);
			slbusSelect.add(DomainConstants.SELECT_TYPE);
			slbusSelect.add(DomainConstants.SELECT_CURRENT);
			slbusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			dobj = DomainObject.newInstance(_context, strObjectId);
			Map mapAttributeInfo = dobj.getInfo(_context, slbusSelect);
			if (mapAttributeInfo != null && !mapAttributeInfo.isEmpty()) {
				sGcas = (String) mapAttributeInfo.get(DomainConstants.SELECT_NAME);
				sRev = (String) mapAttributeInfo.get(DomainConstants.SELECT_REVISION);
				countryOfSales = fetchCountriesForPDF(_context, sGcas, sRev, countryOfSales);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return countryOfSales;
	}

	public CountryOfSales fetchCountriesForPDF(Context context, String gcas, String ver, CountryOfSales countryOfSales)
			throws Exception {

		try {
			if (gcas.isEmpty()) {
				return countryOfSales;
			}
			if (null == ver || ver.isEmpty()) {
				ver = pgV3Constants.SYMBOL_STAR;
			}
			StringList lsTypes = new StringList(5);
			lsTypes.add(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
			lsTypes.add(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY);
			lsTypes.add(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
			lsTypes.add(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
			lsTypes.add(pgV3Constants.TYPE_FABRICATEDPART);
			String strTypePattern = StringHelper.convertObjectToString(lsTypes);

			String state = DomainConstants.EMPTY_STRING;
			StringList objectSelects = new StringList(6);

			objectSelects.add(DomainConstants.SELECT_LAST_ID);
			objectSelects.add(pgV3Constants.SELECT_LAST_LAST);
			objectSelects.add(pgV3Constants.SELECT_LAST_CURRENT);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			Map mpIPS = null;
			MapList mlIPS = DomainObject.findObjects(context, strTypePattern, gcas, ver, pgV3Constants.SYMBOL_STAR,
					pgV3Constants.VAULT_ESERVICEPRODUCTION, DomainConstants.EMPTY_STRING, false, objectSelects);
			String strIPS = DomainConstants.EMPTY_STRING;
			if (mlIPS.size() > 0) {
				for (Iterator ipsIterator = mlIPS.iterator(); ipsIterator.hasNext();) {
					mpIPS = (Map) ipsIterator.next();
					strIPS = (String) mpIPS.get(DomainConstants.SELECT_ID);
					ver = (String) mpIPS.get(DomainConstants.SELECT_REVISION);
					state = (String) mpIPS.get(DomainConstants.SELECT_CURRENT);
				}
			} else {
				String whereCondFO = pgV3Constants.SELECT_ATTRIBUTE_PGLEGACYGCAS + "=='" + gcas.trim() + "'";
				StringList objSelect = new StringList(4);
				objSelect.add(pgV3Constants.SELECT_NAME);
				objSelect.add(DomainConstants.SELECT_LAST_ID);
				objSelect.add(pgV3Constants.SELECT_LAST_LAST);
				objSelect.add(pgV3Constants.SELECT_LAST_CURRENT);
				MapList maplistFC = DomainObject.findObjects(context, pgV3Constants.TYPE_PGFINISHEDPRODUCT,
						pgV3Constants.VAULT_ESERVICEPRODUCTION, whereCondFO, objSelect);
				if (maplistFC != null && !maplistFC.isEmpty()) {
					mpIPS = (Map) maplistFC.get(0);
					gcas = (String) mpIPS.get(pgV3Constants.SELECT_NAME);
					strIPS = (String) mpIPS.get(DomainConstants.SELECT_LAST_ID);
					state = (String) mpIPS.get(pgV3Constants.SELECT_LAST_CURRENT);
					ver = (String) mpIPS.get(pgV3Constants.SELECT_LAST_LAST);
				} else {
					return countryOfSales;
				}
			}
			String legacyGCAS = DomainConstants.EMPTY_STRING;
			DomainObject dobj = DomainObject.newInstance(context, strIPS);
			StringList atrStrList = new StringList(4);
			atrStrList.add(pgV3Constants.ATTRIBUTE_PGCOSRUNDATE);
			atrStrList.add(pgV3Constants.ATTRIBUTE_PGLEGACYGCAS);
			atrStrList.add(pgV3Constants.ATTRIBUTE_PGLEGACYFPC);
			atrStrList.add(pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS);
			AttributeList atrList = dobj.getAttributeValues(context, atrStrList);
			String lastCalDate = DomainConstants.EMPTY_STRING;
			String fpcCode = DomainConstants.EMPTY_STRING;
			String strIncludeInCOS = DomainConstants.EMPTY_STRING;
			Attribute atr =null;
			for (Iterator iterator = atrList.iterator(); iterator.hasNext();) {
				atr = (Attribute) iterator.next();
				if (pgV3Constants.ATTRIBUTE_PGCOSRUNDATE.equalsIgnoreCase(atr.getName()))
					lastCalDate = atr.getValue();
				else if (pgV3Constants.ATTRIBUTE_PGLEGACYGCAS.equalsIgnoreCase(atr.getName()))
					legacyGCAS = atr.getValue();
				else if (pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS.equalsIgnoreCase(atr.getName()))
					strIncludeInCOS = atr.getValue();

				if (legacyGCAS != null && !legacyGCAS.isEmpty()) {
					fpcCode = (String) dobj.getInfo(context, DomainObject.SELECT_NAME);
				} else {
					if (pgV3Constants.ATTRIBUTE_PGLEGACYFPC.equalsIgnoreCase(atr.getName()))
						fpcCode = atr.getValue();
				}
			}
			if (legacyGCAS!=null && !legacyGCAS.isEmpty()) {
				gcas = legacyGCAS;
			}

			if (lastCalDate==null || lastCalDate.isEmpty()) {
				return null;
			}
			lastCalDate = convertDateToCSSFormate(lastCalDate);
			StringList objSelect = new StringList(2);
			objSelect.add(DomainConstants.SELECT_ID);
			objSelect.add(DomainConstants.SELECT_NAME);

			StringList relSelect = new StringList(1);
			relSelect.add(PDFConstant.SELECT_ATTRIBUTE_PGCOSRESTRICTION);

			MapList mlCountry = dobj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
					pgV3Constants.TYPE_COUNTRY, objSelect, relSelect, true, true, (short) 1, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);

			countryOfSales.setLastcosdatetime(lastCalDate);
			if (pgV3Constants.FALSE.equalsIgnoreCase(strIncludeInCOS)) {
				CountryOfSale countryOfSale = new CountryOfSale();
				countryOfSale.setCountryname(PDFConstant.STRING_NO_COUNTRIES_COMPONENTS);
				countryOfSale.setIscountryrestricted(DomainConstants.EMPTY_STRING);
				countryOfSales.getCountryOfSale().add(countryOfSale);
			} else if ((null == mlCountry) || (mlCountry.isEmpty())) {
				CountryOfSale countryOfSale = new CountryOfSale();
				countryOfSale.setCountryname(PDFConstant.NO_MARKET);
				countryOfSale.setIscountryrestricted(DomainConstants.EMPTY_STRING);
				countryOfSales.getCountryOfSale().add(countryOfSale);
			} else {
				Map mpCountry = null;
				String nmCountry = DomainConstants.EMPTY_STRING;
				String pgCOSRestriction = DomainConstants.EMPTY_STRING;
				mlCountry.sort(DomainConstants.SELECT_NAME, "ascending", "String");
				for (Iterator mlCountryItr = mlCountry.iterator(); mlCountryItr.hasNext();) {
					mpCountry = (Map) mlCountryItr.next();
					nmCountry = (String) mpCountry.get(DomainConstants.SELECT_NAME);
					pgCOSRestriction = (String) mpCountry
							.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
					CountryOfSale countryOfSale = new CountryOfSale();
					countryOfSale.setCountryname(nmCountry);
					countryOfSale.setIscountryrestricted(pgV3Constants.KEY_YES.equalsIgnoreCase(pgCOSRestriction) ? "Y" : "");
					countryOfSales.getCountryOfSale().add(countryOfSale);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return countryOfSales;
	}

	public String convertDateToCSSFormate(String date) throws Exception {
		if (null == date || date.isEmpty()) {
			return DomainConstants.EMPTY_STRING;
		}
		SimpleDateFormat smdfOfPLM = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
		SimpleDateFormat smdfOfCSS = new SimpleDateFormat("ddMMMyyyy  hh:mm:ss aaa zzz");
		String modifyDate = smdfOfCSS.format(smdfOfPLM.parse(date));
		return modifyDate;
	}
}
