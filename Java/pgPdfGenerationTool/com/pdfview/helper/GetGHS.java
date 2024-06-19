package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.Ghs;
import com.pdfview.impl.FPP.Ghss;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.util.StringList;

public class GetGHS {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetGHS(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public Ghss getComponent() {
		Ghss Ghss = new Ghss();
		List<Ghs> ghss = Ghss.getGhs();
		boolean isPushContext = false;
		try {
			ContextUtil.pushContext(_context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			isPushContext = true;
			if (StringHelper.validateString(_OID)) {
				HashMap programMap = new HashMap();
				programMap.put("objectId", _OID);
				programMap.put("type", pgV3Constants.TYPE_COPYLIST);
				String[] args = JPO.packArgs(programMap);
				JPO.unpackArgs(args);
				MapList mlGlobalHarmonziedStandardInfo = (MapList) PDFPOCHelper.executeMainClassMethod(_context,
						"pgFPPRollup", "getAllTableData", args);
				if (mlGlobalHarmonziedStandardInfo != null && !mlGlobalHarmonziedStandardInfo.isEmpty()) {
					Ghs ghs = new Ghs();
					String copyListObjectId = DomainConstants.EMPTY_STRING;
					String copyListObjectName = DomainConstants.EMPTY_STRING;
					String copyListObjectState = DomainConstants.EMPTY_STRING;
					String copyListObjectRevision = DomainConstants.EMPTY_STRING;
					String copyListObjectDescription = DomainConstants.EMPTY_STRING;
					String copyListObjectTitle = DomainConstants.EMPTY_STRING;
					String copyListObjectContries = DomainConstants.EMPTY_STRING;
					String copyListObjectLanguages = DomainConstants.EMPTY_STRING;
					String strProdPhysicalId = DomainConstants.EMPTY_STRING;
					String sProductPartName = DomainConstants.EMPTY_STRING;
					String sRevision = DomainConstants.EMPTY_STRING;
					String sTitle = DomainConstants.EMPTY_STRING;
					int imlEnterpisePart = mlGlobalHarmonziedStandardInfo.size();
					StringList objectsSelect = new StringList(3);
					objectsSelect.add(DomainConstants.SELECT_NAME);
					objectsSelect.add(DomainConstants.SELECT_REVISION);
					objectsSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
					Map productPartInfo = null;
					Map mpGHS = null;
					DomainObject domProductPart = DomainObject.newInstance(_context);
					for (int i = 0; i < imlEnterpisePart; i++) {
						mpGHS = (Map) mlGlobalHarmonziedStandardInfo.get(i);
						strProdPhysicalId = StringHelper.convertObjectToString(
								mpGHS.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID));
						if (!UIUtil.isNullOrEmpty(strProdPhysicalId)) {
							domProductPart.setId(strProdPhysicalId);
							productPartInfo = domProductPart.getInfo(_context, objectsSelect);
							sProductPartName = StringHelper
									.convertObjectToString(productPartInfo.get(DomainConstants.SELECT_NAME));
							sRevision = StringHelper
									.convertObjectToString(productPartInfo.get(DomainConstants.SELECT_REVISION));
							sTitle = StringHelper
									.convertObjectToString(productPartInfo.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
						}
						copyListObjectId = (String) mpGHS.get(DomainConstants.SELECT_ID);
						copyListObjectName = (String) mpGHS.get(DomainConstants.SELECT_NAME);
						copyListObjectState = (String) mpGHS.get(DomainConstants.SELECT_CURRENT);
						copyListObjectRevision = (String) mpGHS.get(DomainConstants.SELECT_REVISION);
						copyListObjectDescription = (String) mpGHS.get(DomainConstants.SELECT_DESCRIPTION);
						copyListObjectTitle = (String) mpGHS.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						copyListObjectContries = StringHelper.convertObjectToString(
								mpGHS.get("from[" + pgV3Constants.RELATIONSHIP_COPYLISTCOUNTRY + "].to.name"));
						copyListObjectLanguages = StringHelper.convertObjectToString(
								mpGHS.get("from[" + pgV3Constants.RELATIONSHIP_COPYLISTLOCALLANGUAGE + "].to.name"));
						ghs.setPpn(sProductPartName);
						ghs.setPpr(sRevision);
						ghs.setPpt(StringHelper.wrapStringWord(sTitle));
						ghs.setName(copyListObjectName);
						ghs.setTitle(copyListObjectTitle);
						ghs.setRev(copyListObjectRevision);
						ghs.setDesc(copyListObjectDescription);
						ghs.setState(copyListObjectState);
						ghs.setCountries(StringHelper.wrapStringWord(copyListObjectContries));
						ghs.setLanguagues(StringHelper.wrapStringWord(copyListObjectLanguages));
						ghss.add(ghs);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isPushContext) {
				try {
					ContextUtil.popContext(_context);
				} catch (FrameworkException e) {
					e.printStackTrace();
				}
				isPushContext = false;
			}
		}
		return Ghss;
	}
}
