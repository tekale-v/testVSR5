package com.pdfview.helper;

import java.util.List;

import com.matrixone.apps.domain.DomainConstants;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.DPP.EquivalentsData;
import com.pdfview.impl.DPP.SupplierEquivalents;

import matrix.db.Context;
import matrix.util.MatrixException;

public class GetSupplierEquivalentData extends PDFConstant {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	/**
	 * @param context
	 * @param sOID
	 */
	public GetSupplierEquivalentData(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public SupplierEquivalents getComponent() throws MatrixException {
		SupplierEquivalents supplierEquivalents = new SupplierEquivalents();

		List<EquivalentsData> equivalentsData = supplierEquivalents
				.getEquivalentsData();
		GetManufacturerEquivalentData.getComponentEquivalentsTable(_context, _OID,
				equivalentsData,SELECT_SUPPLIER);
		return supplierEquivalents;
	}

}
