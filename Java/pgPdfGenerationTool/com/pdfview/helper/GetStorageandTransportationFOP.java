package com.pdfview.helper;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FOP.StorageTransportationData;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetStorageandTransportationFOP {

	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetStorageandTransportationFOP(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Retrieve Storage, Transportation, Labeling Assessment Table Data
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */
	public StorageTransportationData getComponent() {
		StorageTransportationData storageandTransportation = new StorageTransportationData();
		String strTechnology = DomainConstants.EMPTY_STRING;
		String strChildrenProd = DomainConstants.EMPTY_STRING;
		String strProdExpChild = DomainConstants.EMPTY_STRING;
		String strChildSafeDesgn = DomainConstants.EMPTY_STRING;
		String strShpHzdClass = DomainConstants.EMPTY_STRING;
		String strShpInfo = DomainConstants.EMPTY_STRING;
		String strStoInfo = DomainConstants.EMPTY_STRING;
		String strIntendMarkets = DomainConstants.EMPTY_STRING;
		String strWarehousingClassification = DomainConstants.EMPTY_STRING;
		String strStorageTempLimits = DomainConstants.EMPTY_STRING;
		String strStorageHumidityLimits = DomainConstants.EMPTY_STRING;
		String strStandardCost = DomainConstants.EMPTY_STRING;
		String strBusinessArea = DomainConstants.EMPTY_STRING;
		String strProductCategoryPlatform = DomainConstants.EMPTY_STRING;
		StringList slIntendMarkets = new StringList();
		StringList slShpHzdClass = new StringList();
		StringList slBusinessArea = new StringList();
		StringList slProductCategoryPlatform = new StringList();
		try {
			Map mpArgsPack = new HashMap();
			mpArgsPack.put("objectId", _OID);
			String[] strArgs = JPO.packArgs(mpArgsPack);

			if (StringHelper.validateString(_OID)) {
				StringList slObjectSelects = new StringList(20);
				slObjectSelects.add(DomainConstants.SELECT_TYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGINTENDEDMARKETS);
				slObjectSelects.add(PDFConstant.ATTRIBUTE_MARKETING_NAME);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGWAREHOUSECLASIFICATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPAKAGINGTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHAZARDCLASS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGGROUP);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEINFORMATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTANDARDCOST);
				slObjectSelects.add(PDFConstant.RELATIONSHIP_OWNINGPRODUCTLINE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
				DomainObject domainObject = DomainObject.newInstance(_context, _OID);
				Map mpAttributeInfo = domainObject.getInfo(_context, slObjectSelects);
				String strObjType = (String) mpAttributeInfo.get(DomainConstants.SELECT_TYPE);
				slIntendMarkets = domainObject.getInfoList(_context,
						"from[" + pgV3Constants.RELATIONSHIP_PGINTENDEDMARKETS + "].to.name");
				slShpHzdClass = domainObject.getInfoList(_context,
						"from[" + pgV3Constants.RELATIONSHIP_PGTOSHIPPINGHAZARDCLASSSIFICATION + "].to.name");
				slBusinessArea = domainObject.getInfoList(_context,
						"from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA + "].to.name");
				slProductCategoryPlatform = domainObject.getInfoList(_context,
						"from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM + "].to.name");

				if (slIntendMarkets != null && !slIntendMarkets.isEmpty()) {
					strIntendMarkets = FrameworkUtil.join(slIntendMarkets, pgV3Constants.SYMBOL_COMMA);
				}
				if (slShpHzdClass != null && !slShpHzdClass.isEmpty()) {
					strShpHzdClass = FrameworkUtil.join(slShpHzdClass, pgV3Constants.SYMBOL_COMMA);
				}
				if (slBusinessArea != null && !slBusinessArea.isEmpty()) {
					strBusinessArea = FrameworkUtil.join(slBusinessArea, pgV3Constants.SYMBOL_PIPE);
				}
				if (slProductCategoryPlatform != null && !slProductCategoryPlatform.isEmpty()) {
					strProductCategoryPlatform = FrameworkUtil.join(slProductCategoryPlatform,
							pgV3Constants.SYMBOL_PIPE);
				}
				if (mpAttributeInfo != null && !mpAttributeInfo.isEmpty()) {
					strTechnology = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
					strTechnology = StringHelper.filterLessAndGreaterThanSign(strTechnology);
					strChildrenProd = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
					strProdExpChild = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
					strChildSafeDesgn = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);
					strShpInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
					strShpInfo = StringHelper.filterLessAndGreaterThanSign(strShpInfo);
					strStoInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
					strStoInfo = StringHelper.filterLessAndGreaterThanSign(strStoInfo);
					strStandardCost = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTANDARDCOST);
					Map hmRequestMap = new HashMap();
					Map hmSettingsMap = new HashMap();
					Map hmProgramMap = new HashMap();
					Map hmFieldMap = new HashMap();
					hmSettingsMap.put("RelationshipName", "relationship_pgToWarehousingClassification");
					hmFieldMap.put("settings", hmSettingsMap);
					hmProgramMap.put("fieldMap", hmFieldMap);
					hmRequestMap.put("objectId", _OID);
					hmRequestMap.put("mode", "view");
					hmProgramMap.put("requestMap", hmRequestMap);
					strWarehousingClassification = (String) JPO.invoke(_context, "pgPLPicklist", null,
							"getPicklistValuesForListbox", JPO.packArgs(hmProgramMap), String.class);
					strWarehousingClassification = StringHelper
							.filterLessAndGreaterThanSign(strWarehousingClassification);
					strStorageTempLimits = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
					strStorageTempLimits = StringHelper.filterLessAndGreaterThanSign(strStorageTempLimits);
					strStorageHumidityLimits = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
					strStorageHumidityLimits = StringHelper.filterLessAndGreaterThanSign(strStorageHumidityLimits);
					storageandTransportation.setTechnology(strTechnology);
					storageandTransportation.setIsProductMarketedasChildrensProduct(strChildrenProd);
					storageandTransportation.setDoestheProductRequireChildSafeDesign(strChildSafeDesgn);
					storageandTransportation.setIsProductExposedtoChildren(strProdExpChild);
					storageandTransportation.setWarehousingClassification(strWarehousingClassification);
					storageandTransportation.setShippingHazardClassification(strShpHzdClass);
					storageandTransportation.setShippingInformation(strShpInfo);
					storageandTransportation.setStorageConditions(strStoInfo);
					storageandTransportation.setIntendedMarkets(strIntendMarkets);
					storageandTransportation.setBusinessArea(strBusinessArea);
					storageandTransportation.setProductCategoryPlatform(strProductCategoryPlatform);
					storageandTransportation.setStandardCost(strStandardCost);
					storageandTransportation.setStorageTemperatureLimits(strStorageTempLimits);
					storageandTransportation.setStorageHumidityLimits(strStorageHumidityLimits);
					mpAttributeInfo.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return storageandTransportation;
	}

}
