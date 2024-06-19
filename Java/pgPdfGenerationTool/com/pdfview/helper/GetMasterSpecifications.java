package com.pdfview.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.pdfview.impl.FPP.MasterSpecification;
import com.pdfview.impl.FPP.MasterSpecifications;
import com.pg.v3.custom.pgV3Constants;

public class GetMasterSpecifications {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";
	CalenderHelper calenderHelper = null;

	public GetMasterSpecifications(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public MasterSpecifications getComponent() throws Exception {
		calenderHelper = new CalenderHelper(_context);
		return getMasterSpecificationData(_context, _OID);
	}
	/**
	 * Method to retrieve the values of Master Specification
	 * @return context, object Id
	 * throws exception
	 */

	public static MasterSpecifications getMasterSpecificationData(Context context, String objectId) {
		MasterSpecifications masterSpecifications = new MasterSpecifications();
		try {

			List<MasterSpecification> masterSpecificationList = masterSpecifications.getMasterSpecification();
			DomainObject partObject = DomainObject.newInstance(context, objectId);

			StringList objectSelects = new StringList(3);
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCETYPE);
			Map mObjectDetails = partObject.getInfo(context, objectSelects);
			String strTypes = (String) mObjectDetails.get(DomainConstants.SELECT_TYPE);
			String strReferenceTypeAttribute = (String) mObjectDetails
					.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCETYPE);
			MapList mlSubClass = new MapList();
			Map argMapMaster = new HashMap();
			argMapMaster.put("objectId", objectId);
			argMapMaster.put("parentRelName", pgV3Constants.RELATIONSHIP_PARTSPECIFICATION);
			argMapMaster.put("Mode", "PDF");
			String[] argMaster = JPO.packArgs(argMapMaster);
			if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTypes)) {
				argMapMaster.remove("Mode");
				argMapMaster.put("Mode", DomainConstants.EMPTY_STRING);
				mlSubClass = (MapList) PDFPOCHelper.executeMainClassMethod(context, "emxPartFamily",
						"getMasterSpecifications", argMaster);
			} else {
				if ("R".equals(strReferenceTypeAttribute)) {
					mlSubClass = (MapList) PDFPOCHelper.executeMainClassMethod(context, "emxPartFamily",
							"getConnectedMasterSpecifications", argMaster);
				}
			}

			if (null != mlSubClass && !mlSubClass.isEmpty()) {
				Map mpMasterSpecItr = null;
				StringList slSelects = new StringList(4);
				slSelects.add(DomainConstants.SELECT_NAME);
				slSelects.add(DomainConstants.SELECT_TYPE);
				slSelects.add(DomainConstants.SELECT_CURRENT);
				slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				Map argsMapMasterSpec = new HashMap();
				String strId = DomainConstants.EMPTY_STRING;
				String strType = DomainConstants.EMPTY_STRING;
				String strDSOType = DomainConstants.EMPTY_STRING;
				argsMapMasterSpec.put("objectList", mlSubClass);
				String[] argsForMasterSpec = JPO.packArgs(argsMapMasterSpec);
				StringList slMasterSpecSubtype = (StringList) PDFPOCHelper.executeMainClassMethod(context,
						"emxCPNProductData", "getSpecificationSubtype", argsForMasterSpec);
				int i = 0;
				Map mapInfo = new HashMap();
				DomainObject partSpecObject =null;
				for (Iterator iterator = mlSubClass.iterator(); iterator.hasNext();) {
					MasterSpecification masterSpecification = new MasterSpecification();
					mpMasterSpecItr = (Map) iterator.next();
					strId = (String) mpMasterSpecItr.get(DomainConstants.SELECT_ID);
					partSpecObject = DomainObject.newInstance(context, strId);
					mapInfo = (Map) partSpecObject.getInfo(context, slSelects);
					strType = (String) mapInfo.get(DomainConstants.SELECT_TYPE);
					strDSOType = UINavigatorUtil.getAdminI18NString("Type", strType,
							context.getSession().getLanguage());

					masterSpecification.setName((String) mapInfo.get(DomainConstants.SELECT_NAME));
					masterSpecification.setType(strDSOType);
					masterSpecification.setTitle((String) mapInfo.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
					masterSpecification.setState((String) mapInfo.get(DomainConstants.SELECT_CURRENT));
					masterSpecification.setSpecificationSubType(slMasterSpecSubtype.get(i));
					masterSpecificationList.add(masterSpecification);
					i++;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return masterSpecifications;
	}
}
