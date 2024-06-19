package com.pdfview.helper;

import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.Ghs;
import com.pdfview.impl.FPP.Ghss;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetGHSForAPP {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetGHSForAPP(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public Ghss getComponent() throws FrameworkException {
		return getGlobalHarmonizedStandardData(_context, _OID);
	}

	/**
	 * Retrive Global Harmonized Standard Data
	 * 
	 * @param context
	 * @param args
	 * @return StringBuilder
	 * @throws FrameworkException 
	 * @throws MatrixException
	 */

	private Ghss getGlobalHarmonizedStandardData(Context context, String strObjectId) throws FrameworkException {
		Ghss Ghss = new Ghss();
		List<Ghs> ghss = Ghss.getGhs();
		DomainObject domainObject = null;
		boolean isPushContext = false;
		try {
			ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			isPushContext = true;
			if (StringHelper.validateString(strObjectId)) {
				StringList objectSelects = new StringList(8);
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_NAME);
				objectSelects.add(DomainConstants.SELECT_CURRENT);
				objectSelects.add(DomainConstants.SELECT_REVISION);
				objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				objectSelects.add(DomainConstants.SELECT_DESCRIPTION);
				objectSelects.add("from[" + pgV3Constants.RELATIONSHIP_COPYLISTCOUNTRY + "].to.name");
				objectSelects.add("from[" + pgV3Constants.RELATIONSHIP_COPYLISTLOCALLANGUAGE + "].to.name");
				domainObject = DomainObject.newInstance(context, strObjectId);
				MapList mlGlobalHarmonziedStandardInfo = domainObject.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_PGGLOBALHARMONIZEDSTANDARD, pgV3Constants.TYPE_COPYLIST,
						objectSelects, null, false, true, (short) 1, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				if (mlGlobalHarmonziedStandardInfo != null && !mlGlobalHarmonziedStandardInfo.isEmpty()) {
					String copyListObjectId = DomainConstants.EMPTY_STRING;
					String copyListObjectName = DomainConstants.EMPTY_STRING;
					String copyListObjectState = DomainConstants.EMPTY_STRING;
					String copyListObjectRevision = DomainConstants.EMPTY_STRING;
					String copyListObjectDescription = DomainConstants.EMPTY_STRING;
					String copyListObjectTitle = DomainConstants.EMPTY_STRING;
					String copyListObjectContries = DomainConstants.EMPTY_STRING;
					String copyListObjectLanguages = DomainConstants.EMPTY_STRING;
					int imlGlobalHarmonziedStandardInfo = mlGlobalHarmonziedStandardInfo.size();
					Map mapGlobalHarmonziedStandardInfo = null;

					for (int i = 0; i < imlGlobalHarmonziedStandardInfo; i++) {
						Ghs ghs = new Ghs();
						mapGlobalHarmonziedStandardInfo = (Map) mlGlobalHarmonziedStandardInfo.get(i);
						copyListObjectId = (String) mapGlobalHarmonziedStandardInfo.get(DomainConstants.SELECT_ID);
						copyListObjectName = (String) mapGlobalHarmonziedStandardInfo.get(DomainConstants.SELECT_NAME);
						copyListObjectState = (String) mapGlobalHarmonziedStandardInfo
								.get(DomainConstants.SELECT_CURRENT);
						copyListObjectRevision = (String) mapGlobalHarmonziedStandardInfo
								.get(DomainConstants.SELECT_REVISION);
						copyListObjectDescription = (String) mapGlobalHarmonziedStandardInfo
								.get(DomainConstants.SELECT_DESCRIPTION);
						copyListObjectTitle = (String) mapGlobalHarmonziedStandardInfo
								.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						copyListObjectContries = StringHelper.convertObjectToString(mapGlobalHarmonziedStandardInfo
								.get("from[" + pgV3Constants.RELATIONSHIP_COPYLISTCOUNTRY + "].to.name"));
						copyListObjectLanguages = StringHelper.convertObjectToString(mapGlobalHarmonziedStandardInfo
								.get("from[" + pgV3Constants.RELATIONSHIP_COPYLISTLOCALLANGUAGE + "].to.name"));
						ghs.setName(copyListObjectName);
						ghs.setTitle(StringHelper.wrapStringWord(copyListObjectTitle));
						ghs.setRev(copyListObjectRevision);
						ghs.setDesc(copyListObjectDescription);
						ghs.setState(copyListObjectState);
						ghs.setCountries(StringHelper.wrapStringWord(copyListObjectContries).replace(PDFConstant.CONST_BREAK_CLOSE, DomainConstants.EMPTY_STRING));
						ghs.setLanguagues(StringHelper.wrapStringWord(copyListObjectLanguages));
						ghss.add(ghs);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isPushContext) {
				ContextUtil.popContext(context);
				isPushContext = false;
			}
		}
		return Ghss;
	}

}
