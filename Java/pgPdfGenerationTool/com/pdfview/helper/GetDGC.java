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
import com.pdfview.impl.FPP.Dgc;
import com.pdfview.impl.FPP.Dgcs;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetDGC {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetDGC(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public Dgcs getComponent() {

		Dgcs dgcs = new Dgcs();
		List<Dgc> dgclist = dgcs.getDgs();
		boolean isPushContext = false;
		try {
			ContextUtil.pushContext(_context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			isPushContext = true;
			if (StringHelper.validateString(_OID)) {
				HashMap programMap = new HashMap();
				programMap.put("objectId", _OID);
				programMap.put("type", pgV3Constants.TYPE_PGDANGEROUSGOODS);
				String[] args = JPO.packArgs(programMap);
				MapList mlDangerousGoodsClassifications = (MapList) PDFPOCHelper.executeMainClassMethod(_context,
						"pgFPPRollup", "getAllTableData", args);
				if (mlDangerousGoodsClassifications != null && !mlDangerousGoodsClassifications.isEmpty()) {
					
					String sProductPartName = DomainConstants.EMPTY_STRING;
					String strProdPhysicalId = DomainConstants.EMPTY_STRING;
					String sRevision = DomainConstants.EMPTY_STRING;
					String sTitle = DomainConstants.EMPTY_STRING;
					String sDGDescription = DomainConstants.EMPTY_STRING;
					String sUNNumber = DomainConstants.EMPTY_STRING;
					String sProperShippingName = DomainConstants.EMPTY_STRING;
					String sHazardClass = DomainConstants.EMPTY_STRING;
					String sPackagingGroup = DomainConstants.EMPTY_STRING;
					String sDGMarksRequirements = DomainConstants.EMPTY_STRING;
					String sDGMarksRequirementsOnCOP = DomainConstants.EMPTY_STRING;
					String sDGMarksRequirementsOnCUP = DomainConstants.EMPTY_STRING;
					String sShippedLimitQuantity = DomainConstants.EMPTY_STRING;
					String sUNSpecificationPackaging = DomainConstants.EMPTY_STRING;
					String sMaxConsumerUnitPart = DomainConstants.EMPTY_STRING;
					String sMaxCustomerUnitPart = DomainConstants.EMPTY_STRING;
					StringList objectsSelect = new StringList();
					objectsSelect.add(DomainConstants.SELECT_NAME);
					objectsSelect.add(DomainConstants.SELECT_REVISION);
					objectsSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
					Map productPartInfo = null;
					Map mapDGC = null;
					DomainObject domProductPart = DomainObject.newInstance(_context);
					int mlDangerousGoodsClassificationsSize=mlDangerousGoodsClassifications.size();
					for (int i = 0; i < mlDangerousGoodsClassificationsSize; i++) {
						Dgc dgc = new Dgc();
						mapDGC = (Map) mlDangerousGoodsClassifications.get(i);
						strProdPhysicalId = StringHelper.convertObjectToString(
								mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID));
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
						sDGDescription = StringHelper
								.convertObjectToString(mapDGC.get(DomainConstants.SELECT_DESCRIPTION));
						sUNNumber = StringHelper
								.convertObjectToString(mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGPUNNUMBER));
						sProperShippingName = StringHelper
								.convertObjectToString(mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGPROPERSHIPPINGNAME));
						sHazardClass = StringHelper.convertObjectToString(mapDGC.get(pgV3Constants.SELECT_HAZARDCLASS));
						sPackagingGroup = StringHelper
								.convertObjectToString(mapDGC.get(pgV3Constants.SELECT_PACKINGGROUP));
						sDGMarksRequirements = StringHelper.convertObjectToString(
								mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERPACKAGINGREQUIREMENTS));
						sDGMarksRequirementsOnCOP = StringHelper
								.convertObjectToString(mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGDGCOPLABELREQUIRED));
						sDGMarksRequirementsOnCUP = StringHelper
								.convertObjectToString(mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGDGCUPLABELREQUIRED));
						sShippedLimitQuantity = StringHelper.convertObjectToString(
								mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPMENTLIMITEDQUANTITY));
						sUNSpecificationPackaging = StringHelper.convertObjectToString(
								mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXCONSUMERUNITSIZE));
						sMaxConsumerUnitPart = StringHelper.convertObjectToString(
								mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONSUMERWEIGHTVOLUME));
						sMaxCustomerUnitPart = StringHelper.convertObjectToString(
								mapDGC.get(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERWEIGHTVOLUME));
						dgc.setProductPartName(sProductPartName);
						dgc.setProductPartRevision(sRevision);
						dgc.setProductPartTitle(sTitle);
						dgc.setdGDescription(sDGDescription);
						dgc.setuNNumber(sUNNumber);
						dgc.setProperShippingName(sProperShippingName);
						dgc.setHazardClass(sHazardClass);
						dgc.setPackingGroup(sPackagingGroup);
						dgc.setShippedLimitedQuantity(sShippedLimitQuantity);
						dgc.setuNSpecificationRequired(sUNSpecificationPackaging);
						dgc.setMaxConsumerUnit(sMaxConsumerUnitPart);
						dgc.setMaxCustomerUnit(sMaxCustomerUnitPart);
						dgc.setOtherPackagingRequirements(sDGMarksRequirements);
						dgc.setdGConsumerUnit(sDGMarksRequirementsOnCOP);
						dgc.setdGCustomerUnit(sDGMarksRequirementsOnCUP);
						dgclist.add(dgc);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isPushContext = false;
			}
		}
		return dgcs;
	}
}
