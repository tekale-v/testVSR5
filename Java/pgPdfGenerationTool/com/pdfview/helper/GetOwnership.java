package com.pdfview.helper;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.Ownership;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetOwnership {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetOwnership(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}
	/**
	 * Method to retrieve the values of oWNERsHIP TABLE
	 * @return
	 * throws exception
	 */

	public Ownership getComponent() {

		Ownership ownership = new Ownership();
		
		try {
			StringList slObjectSelects = new StringList(3);
			slObjectSelects.add(PDFConstant.ATTRIBUTE_PGPDTEMPLATE);
			slObjectSelects.add(pgV3Constants.SELECT_MODIFIED);
			slObjectSelects.add(pgV3Constants.SELECT_ORIGINATOR);
			DomainObject domainObject = DomainObject.newInstance(_context, _OID);
			Map mpAttributeInfo = domainObject.getInfo(_context, slObjectSelects);

			String segment=(String)mpAttributeInfo.get(PDFConstant.ATTRIBUTE_PGPDTEMPLATE);
			String modified=(String)mpAttributeInfo.get(pgV3Constants.SELECT_MODIFIED);
			String originator=(String)mpAttributeInfo.get(pgV3Constants.SELECT_ORIGINATOR);
			HashMap reqMap = new HashMap();
			reqMap.put("objectId", _OID);
			HashMap paramMapLast = new HashMap();
			paramMapLast.put("requestMap", reqMap);
			String[] argsLast = JPO.packArgs(paramMapLast);
			String strLastUpdateUser = (String)PDFPOCHelper.executeMainClassMethod(_context, "emxCPNProductData", "getLastUpdatedUserForOwnership", argsLast);
			
			ownership.setLastUpdateUser(strLastUpdateUser);
			ownership.setOriginator(originator);
			ownership.setSegment(segment);
			ownership.setLastUpdateDate(modified);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ownership;
	}
}
