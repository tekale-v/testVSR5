package com.pdfview.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.SapBOMAsFed;
import com.pdfview.impl.FPP.SapBOMAsFeds;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.AccessConstants;
import matrix.db.Context;
import matrix.db.JPO;

public class getSAPBOMAsFed {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public getSAPBOMAsFed(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public SapBOMAsFeds getComponent() {

		return getFlatBOM(_context, _OID);
	}

	/**
	 * Method to retrieve the BOM values
	 * @param context
	 * @param strObjectId
	 * @return
	 */
	private SapBOMAsFeds getFlatBOM(Context context, String strObjectId) {
		long startTime = new Date().getTime();
		SapBOMAsFeds sapBOMAsFeds = new SapBOMAsFeds();
		List<SapBOMAsFed> sapBOMAsFedList = sapBOMAsFeds.getSapBOMAsFed();
		boolean hasReadAccess = false;
		try {
			if (StringHelper.validateString(strObjectId)) {
				DomainObject domainObject = DomainObject.newInstance(context, strObjectId);
				String sObjType = domainObject.getInfo(context, DomainConstants.SELECT_TYPE);

				Map ArgsmapflatBOM = new HashMap();
				ArgsmapflatBOM.put("objectId", strObjectId);
				String[] argsflatBOM = JPO.packArgs(ArgsmapflatBOM);
				MapList mlSubGrp = new MapList();

				mlSubGrp = (MapList) PDFPOCHelper.executeMainClassMethod(context, "pgIPMSAPBOMJPO", "getTableData",
						argsflatBOM);
				String strName = DomainConstants.EMPTY_STRING;
				String strid = DomainConstants.EMPTY_STRING;
				String strType = DomainConstants.EMPTY_STRING;
				String strSAPType = DomainConstants.EMPTY_STRING;
				String strTitle = DomainConstants.EMPTY_STRING;
				String strValue = DomainConstants.EMPTY_STRING;
				String strSpecSubType = DomainConstants.EMPTY_STRING;
				String strBOMQUT = DomainConstants.EMPTY_STRING;
				String strBUOM = DomainConstants.EMPTY_STRING;

				String strAuthorized = DomainConstants.EMPTY_STRING;
				String strAuthorizedtoUse = DomainConstants.EMPTY_STRING;
				String strAuthorizedtoProduce = DomainConstants.EMPTY_STRING;
				String[] argsSubsSST = null;
				String strComment = DomainConstants.EMPTY_STRING;

				Map mpObjNameSST = new HashMap();
				Map mpBOMSubsSST = new HashMap();
				MapList mlObjListNameSST = new MapList();
				String strSubGrp = DomainConstants.EMPTY_STRING;
				String strOptCom = DomainConstants.EMPTY_STRING;
				DomainObject domObject = DomainObject.newInstance(context);
				Vector vEBOMSubSpecSubType =null;
				if (mlSubGrp != null && !mlSubGrp.isEmpty()) {
					Map mpFlatBOM = null;
					for (Iterator FlatBOMItr = mlSubGrp.iterator(); FlatBOMItr.hasNext();) {
						SapBOMAsFed sb = new SapBOMAsFed();
						mpFlatBOM = (Map) FlatBOMItr.next();
						strName = (String) mpFlatBOM.get(DomainConstants.SELECT_NAME);
						
						strid = (String) mpFlatBOM.get(DomainConstants.SELECT_ID);
						domObject.setId(strid);
						hasReadAccess = domObject.checkAccess(context, (short) AccessConstants.cRead);
						if (hasReadAccess) {
							strTitle = (String) mpFlatBOM.get(DomainConstants.SELECT_DESCRIPTION);
							if (UIUtil.isNullOrEmpty(strTitle)) {
								strTitle = DomainConstants.EMPTY_STRING;
							}
						} else {
							strTitle = pgV3Constants.NO_ACCESS;
						}
						strComment = (String) mpFlatBOM.get(pgV3Constants.ATTRIBUTE_COMMENT);
						strSAPType = (String) mpFlatBOM.get("SAPType");
						strBOMQUT = (String) mpFlatBOM.get("BOMQty");
						strType = (String) mpFlatBOM.get(DomainConstants.SELECT_TYPE);
						strSubGrp = (String) mpFlatBOM.get("substitute");
						strOptCom = (String) mpFlatBOM.get("OptComponent");
						strBUOM = (String) mpFlatBOM.get("SAPUOM");
						strAuthorized = (String) mpFlatBOM.get("AVPlantDetails");
						strAuthorizedtoUse = (String) mpFlatBOM.get("AUPlantDetails");
						strAuthorizedtoProduce = (String) mpFlatBOM.get("APPlantDetails");
						strTitle=StringHelper.filterLessAndGreaterThanSign(strTitle);
						sb.setName(StringHelper.validateString1(strName));
						sb.setSapdescription(StringHelper.validateString1(strTitle));
						strValue = UINavigatorUtil.getAdminI18NString("Type", strType,
								context.getSession().getLanguage());
						sb.setType(StringHelper.validateString1(strValue));
						mpObjNameSST = new HashMap();
						mpObjNameSST.put(DomainConstants.SELECT_ID, strid);
						mpObjNameSST.put(DomainConstants.SELECT_TYPE, strType);
						mlObjListNameSST = new MapList();
						mlObjListNameSST.add(mpObjNameSST);
						mpBOMSubsSST = new HashMap();
						mpBOMSubsSST.put("objectList", mlObjListNameSST);
						argsSubsSST = JPO.packArgs(mpBOMSubsSST);

						vEBOMSubSpecSubType = (Vector) PDFPOCHelper.executeMainClassMethod(context,
								"pgIPMTablesJPO", "getSpecificationSubtype", argsSubsSST);
						if ((vEBOMSubSpecSubType != null) && (!vEBOMSubSpecSubType.isEmpty()))
							strSpecSubType = (String) vEBOMSubSpecSubType.get(0);
						sb.setSpecSub(StringHelper.validateString1(strSpecSubType));
						sb.setSapType(StringHelper.validateString1(strSAPType));
						sb.setSubAltGroup(StringHelper.validateString1(strSubGrp));
						sb.setBomQuantity(StringHelper.validateString1(strBOMQUT));
						sb.setUom(StringHelper.validateString1(strBUOM));
						sb.setOptionalcomponent(StringHelper.validateString1(strOptCom));
						sb.setComments(StringHelper.validateString1(strComment));
						sb.setAuthorized(strAuthorized);
						sb.setAuthorizedProduce(strAuthorizedtoProduce);
						sb.setAuthorizedUse(strAuthorizedtoUse);
						sapBOMAsFedList.add(sb);
					}
				}
				mlSubGrp.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the  getFlatBOM Method is-->" + (endTime - startTime));
		return sapBOMAsFeds;
	}
}
