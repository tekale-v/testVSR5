package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.Certification;
import com.pdfview.impl.FPP.Certifications;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

public class GetCertifications {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	CalenderHelper calenderHelper = null;

	public GetCertifications(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public Certifications getComponent() {
		return getCertificationsData(_context, _OID);
	}

	/**
	 * Retrieve Certifications Data
	 * 
	 * @param context
	 * @param args
	 * @return Certifications
	 * @throws MatrixException
	 */
	private Certifications getCertificationsData(Context context, String strObjectId) {
		Certifications certifications = new Certifications();
		List<Certification> certificationList = certifications.getCertification();

		try {
			if (StringHelper.validateString(strObjectId)) {
				HashMap programMap = new HashMap();
				programMap.put("objectId", strObjectId);
				programMap.put("type", pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS);
				String[] args = JPO.packArgs(programMap);
				JPO.unpackArgs(args);
				MapList mlAllTableData = (MapList) PDFPOCHelper.executeMainClassMethod(context, "pgFPPRollup",
						"getAllTableData", args);
				String sProductPart = DomainConstants.EMPTY_STRING;
				String sCertificationClaim = DomainConstants.EMPTY_STRING;
				String sCountry = DomainConstants.EMPTY_STRING;
				String strProdPhysicalId = DomainConstants.EMPTY_STRING;
				if (mlAllTableData != null && !mlAllTableData.isEmpty()) {
					Map sStabilityResultsMap = null;
					int imlAllTableDataSize = mlAllTableData.size();
					DomainObject domProductPart = DomainObject.newInstance(context);
					for (int i = 0; i < imlAllTableDataSize; i++) {
						sStabilityResultsMap = (Map) mlAllTableData.get(i);
						strProdPhysicalId = StringHelper.convertObjectToString(
								sStabilityResultsMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID));
						if (!UIUtil.isNullOrEmpty(strProdPhysicalId)) {
							Certification certification = new Certification();
							domProductPart.setId(strProdPhysicalId);
							sProductPart = domProductPart.getInfo(context, DomainConstants.SELECT_NAME);
							sCertificationClaim = StringHelper
									.convertObjectToString(sStabilityResultsMap.get(DomainConstants.SELECT_NAME));
							sCountry = StringHelper.convertObjectToString(sStabilityResultsMap
									.get("tomid[" + pgV3Constants.RELATIONSHIP_PGCOUNTRIESCERTIFIED + "].from.name"));
							certification.setProductPart(sProductPart);
							certification.setCertificationClaim(sCertificationClaim);
							certification.setCountry(sCountry);
							certificationList.add(certification);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return certifications;
	}
}
