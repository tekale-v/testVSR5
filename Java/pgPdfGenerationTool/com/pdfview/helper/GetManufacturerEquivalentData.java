package com.pdfview.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.DPP.EquivalentsData;
import com.pdfview.impl.DPP.ManufacturerEquivalents;
import com.pdfview.impl.FPP.Note;
import com.pdfview.impl.FPP.Notes;
import com.pg.v3.custom.pgV3Constants;

public class GetManufacturerEquivalentData extends PDFConstant {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetManufacturerEquivalentData(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public ManufacturerEquivalents getComponent() throws MatrixException {
		ManufacturerEquivalents manufacturerEquivalents = new ManufacturerEquivalents();
		List<EquivalentsData> equivalentsData = manufacturerEquivalents.getEquivalentsData();
		getComponentEquivalentsTable(_context, _OID, equivalentsData, SELECT_MANUFACTURER);
		return manufacturerEquivalents;
	}

	/**
	 * This method gets Component Equivalent Table for SEP/MEP
	 * 
	 * @param Context - Context user
	 * @param String  - Object ID
	 * @return StringBuilder - returns list of equivalent SEP/MEP
	 **/
	public static void getComponentEquivalentsTable(Context context, String strObjectId,
			List<EquivalentsData> equivalentsData, String actionType) {
		long startTime = new Date().getTime();
		String strName = DomainConstants.EMPTY_STRING;
		String strRev = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strState = DomainConstants.EMPTY_STRING;
		String strId = DomainConstants.EMPTY_STRING;
		String strPolicy = DomainConstants.EMPTY_STRING;
		StringList slManufacturerorSupplier = new StringList();
		StringList slSEPSelectables = new StringList(1);
		slSEPSelectables.add(DomainConstants.SELECT_DESCRIPTION);
		DomainObject doObj = null;
		Map mpSEPPack = new HashMap();
		mpSEPPack.put("objectId", strObjectId);
		String[] args = JPO.packArgs(mpSEPPack);
		Map mpMEPPack = new HashMap();
		mpMEPPack.put("objectId", strObjectId);
		mpMEPPack.put("isMPN", DomainConstants.EMPTY_STRING);
		String[] args1 = JPO.packArgs(mpMEPPack);
		Map mpMEPSEPData = new HashMap();
		Map mapSEPAttributeInfo = new HashMap();
		MapList mlMEPSEPData = null;
		if (actionType.equals(SELECT_MANUFACTURER)) {
			mlMEPSEPData = (MapList) PDFPOCHelper.executeMainClassMethod(context, "jpo.componentcentral.sep.Part",
					"getEnterpriseManufacturerEquivalents", args1);
		} else {
			mlMEPSEPData = (MapList) PDFPOCHelper.executeMainClassMethod(context, "jpo.componentcentral.sep.Part",
					"getInProcessSEPs", args);
		}

		try {
			if (((mlMEPSEPData != null) && (!mlMEPSEPData.isEmpty()))) {
				int inMEPSEPDataSize = mlMEPSEPData.size();
				for (int i = 0; i < inMEPSEPDataSize; i++) {
					EquivalentsData equivalents = new EquivalentsData();
					mpMEPSEPData = (Map) mlMEPSEPData.get(i);
					strId = (String) mpMEPSEPData.get(DomainConstants.SELECT_ID);
					doObj = DomainObject.newInstance(context, strId);
					strName = (String) mpMEPSEPData.get(DomainConstants.SELECT_NAME);
					strRev = (String) mpMEPSEPData.get(DomainConstants.SELECT_REVISION);
					strState = (String) mpMEPSEPData.get(DomainConstants.SELECT_CURRENT);
					strPolicy = (String) mpMEPSEPData.get(DomainConstants.SELECT_POLICY);
					strState = EnoviaHelper.getStateName(context, strState, strPolicy);
					strType = (String) mpMEPSEPData.get(DomainConstants.SELECT_TYPE);
					strType = EnoviaResourceBundle.getTypeI18NString(context, strType,
							context.getSession().getLanguage());
					if (actionType.equals(SELECT_MANUFACTURER)) {
						slManufacturerorSupplier = doObj.getInfoList(context,
								"to[" + pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY + "].from.name");
					} else {
						slManufacturerorSupplier = doObj.getInfoList(context,
								"to[" + pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY + "].from.name");
					}
					mapSEPAttributeInfo = doObj.getInfo(context, slSEPSelectables);
					
					equivalents.setName(StringHelper.validateString1(strName));
					equivalents.setRevision(StringHelper.validateString1(strRev));
					equivalents.setType(StringHelper.validateString1(strType));
					equivalents.setDescription((String) mapSEPAttributeInfo.get(DomainConstants.SELECT_DESCRIPTION));
					equivalents.setState(StringHelper.validateString1(strState));
					equivalents.setEquivalent(StringHelper.validateString1(slManufacturerorSupplier));
					equivalentsData.add(equivalents);
				}
				mlMEPSEPData.clear();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		long endTime = new Date().getTime();
		System.out.println(
				"Total Time has taken by the  getComponentEquivalentsTable Method is-->" + (endTime - startTime));

	}

}
