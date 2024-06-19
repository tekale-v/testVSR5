package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.UniqueFormulaIdentifier;
import com.pdfview.impl.FPP.UniqueFormulaIdentifiers;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;

public class GetUniqueFormulaIdentifier {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetUniqueFormulaIdentifier(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Method to retrieve the values of Unique Formula Identifier
	 * 
	 * @return throws exception
	 */

	public UniqueFormulaIdentifiers getComponent() {
		UniqueFormulaIdentifiers ufis = new UniqueFormulaIdentifiers();
		String strUFIforChild = DomainConstants.EMPTY_STRING;
		String strUFIObj = DomainConstants.EMPTY_STRING;
		Map mpObjMap = null;
		Map paramMap = new HashMap();
		paramMap.put("objectId", _OID);
		Map progamMap = new HashMap();
		progamMap.put("paramMap", paramMap);
		try {

			List<UniqueFormulaIdentifier> lsUfi = ufis.getUniqueFormulaIdentifier();
			String[] args = JPO.packArgs(progamMap);
			MapList mlGCASObjects = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "pgIPMProductData",
					"getTopLevelObjectforFPP", args);
			if (mlGCASObjects!=null && !mlGCASObjects.isEmpty()) {
				int mlGCASObjectssize=mlGCASObjects.size();
				for (int i = 0; i < mlGCASObjectssize; i++) {
					UniqueFormulaIdentifier ufi1 = new UniqueFormulaIdentifier();
					mpObjMap = (Map) mlGCASObjects.get(i);
					strUFIforChild = (String) mpObjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGUNIQUEFORUMULAIDENTIFIER);
					strUFIObj = (String) mpObjMap.get(DomainConstants.SELECT_NAME);
					if (UIUtil.isNotNullAndNotEmpty(strUFIforChild)) {
						ufi1.setUfiName(strUFIforChild);
						ufi1.setUfiObject(strUFIObj);
						lsUfi.add(ufi1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ufis;
	}
}
