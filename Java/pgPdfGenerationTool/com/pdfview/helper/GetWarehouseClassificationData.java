package com.pdfview.helper;

import java.util.HashMap;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.pdfview.impl.FPP.WarehouseClassification;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;

public class GetWarehouseClassificationData {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetWarehouseClassificationData(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Method to retrieve the values of Warehousing classification
	 * @return
	 * throws exception
	 */

	public WarehouseClassification getComponent() {
		WarehouseClassification warehouseClassification = new WarehouseClassification();
		DomainObject domainObject = null;
		try {
			if (StringHelper.validateString(_OID)) {
				String sWarehousingClassification = DomainConstants.EMPTY_STRING;
				String sCorrosive = DomainConstants.EMPTY_STRING;
				HashMap requestMap = new HashMap();
				requestMap.put("objectId", _OID);
				HashMap programMap = new HashMap();
				programMap.put("requestMap", requestMap);
				String[] args = JPO.packArgs(programMap);
				sWarehousingClassification = (String) PDFPOCHelper.executeMainClassMethod(_context, "pgIPMProductData",
						"getWarehouseClassification", args);
				sWarehousingClassification = StringHelper.filterLessAndGreaterThanSign(sWarehousingClassification);
				domainObject = DomainObject.newInstance(_context, _OID);
				sCorrosive = domainObject.getInfo(_context, pgV3Constants.SELECT_ATTRIBUTE_PGCORROSIVE);
				if (!sCorrosive.isEmpty() || !sWarehousingClassification.isEmpty()) {
					warehouseClassification.setWarehousingClassificationData(sWarehousingClassification);
					warehouseClassification.setCorrosive(sCorrosive);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return warehouseClassification;
	}

}
