package com.pdfview.helper;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.pdfview.impl.FPP.WeightsAndDimensions;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class GetWeightsAndDimensions {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	/**
	 * @param context
	 * @param sOID
	 */
	public GetWeightsAndDimensions(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Method to retrieve the values of Weight & Dimensions table
	 * 
	 * @return throws exception
	 */
	public WeightsAndDimensions getComponent() {
		WeightsAndDimensions WDDim = new WeightsAndDimensions();
		try {

			DomainObject domainObject = DomainObject.newInstance(_context, _OID);
			StringList slbusSelect = new StringList(2);
			slbusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGWDSTATIUS);
			slbusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGUNITOFMEASURESYSTEM);
			Map<String, String> specInfoMap = domainObject.getInfo(_context, slbusSelect);
			String strpgWDStatius = specInfoMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGWDSTATIUS);
			String strpgUnitOfMeasureSystem = specInfoMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGUNITOFMEASURESYSTEM);
			strpgWDStatius = StringHelper.validateString1(strpgWDStatius);
			strpgUnitOfMeasureSystem = StringHelper.validateString1(strpgUnitOfMeasureSystem);
			WDDim.setwDStatus(strpgWDStatius);
			WDDim.setUnitofMeasureSystem(strpgUnitOfMeasureSystem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return WDDim;
	}
}
