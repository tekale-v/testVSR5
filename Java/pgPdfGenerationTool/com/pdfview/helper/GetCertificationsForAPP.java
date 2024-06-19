package com.pdfview.helper;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MapList;
import com.pdfview.impl.FPP.Certification;
import com.pdfview.impl.FPP.Certifications;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class GetCertificationsForAPP {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";
	CalenderHelper calenderHelper = null;

	public GetCertificationsForAPP(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public Certifications getComponent() {
		return getCertificationTable(_context, _OID);
	}

	private Certifications getCertificationTable(Context context, String strObjectId) {
		Certifications certifications = new Certifications();
		List<Certification> certificationList = certifications.getCertification();
		long startTime = new Date().getTime();
		try {
			if (StringHelper.validateString(strObjectId)) {
				DomainObject bomObject = DomainObject.newInstance(context, strObjectId);
				StringList busSelect = new StringList(2);
				busSelect.add(DomainConstants.SELECT_ID);
				busSelect.add(DomainConstants.SELECT_NAME);
				StringList relSelect = new StringList(2);
				relSelect.add(DomainRelationship.SELECT_NAME);
				relSelect.add(DomainRelationship.SELECT_ID);
				MapList mlPLIMaterialmlCertifications = bomObject.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS,
						pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS, busSelect, relSelect, false, true, (short) 1,
						null, null, 0);
				if (mlPLIMaterialmlCertifications != null && !mlPLIMaterialmlCertifications.isEmpty()) {
					String strRelId = DomainConstants.EMPTY_STRING;
					String strRelObejctName = DomainConstants.EMPTY_STRING;
					StringList slRelSelects = new StringList();
					slRelSelects
							.add("tomid[" + pgV3Constants.RELATIONSHIP_PGCOUNTRIESCERTICATIONCLAIMED + "].from.name");
					DomainRelationship domRel = null;
					Hashtable htRelData = null;
					Map mapPLIMaterial = null;
					StringList countryList = new StringList();
					int iSize = mlPLIMaterialmlCertifications.size();
					for (int i = 0; i < iSize; i++) {
						Certification certification = new Certification();
						mapPLIMaterial = (Map) mlPLIMaterialmlCertifications.get(i);
						strRelId = (String) mapPLIMaterial.get(DomainRelationship.SELECT_ID);
						strRelObejctName = (String) mapPLIMaterial.get(DomainConstants.SELECT_NAME);
						domRel = DomainRelationship.newInstance(context, strRelId);
						htRelData = domRel.getRelationshipData(context, slRelSelects);
						countryList = (StringList) htRelData.get(
								"tomid[" + pgV3Constants.RELATIONSHIP_PGCOUNTRIESCERTICATIONCLAIMED + "].from.name");
						certification.setCertificationClaim(StringHelper.convertObjectToString(strRelObejctName));
						certification.setCountry(StringHelper.convertObjectToString(countryList));
						certificationList.add(certification);
					}
					countryList.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return certifications;
	}

}
