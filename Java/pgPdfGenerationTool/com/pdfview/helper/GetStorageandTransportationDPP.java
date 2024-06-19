package com.pdfview.helper;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.DPP.StorageTransportationData;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetStorageandTransportationDPP {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetStorageandTransportationDPP(Context context, String sOID) {
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
		String strPowerSource = DomainConstants.EMPTY_STRING;
		String strBatteryType = DomainConstants.EMPTY_STRING;
		String strChildrenProd = DomainConstants.EMPTY_STRING;
		String strProdExpChild = DomainConstants.EMPTY_STRING;
		String strChildSafeDesgn = DomainConstants.EMPTY_STRING;
		String strDngGoodsClass = DomainConstants.EMPTY_STRING;
		String strMaxCUSize = DomainConstants.EMPTY_STRING;
		String strCOUnitLable = DomainConstants.EMPTY_STRING;
		String strCUUnitLable = DomainConstants.EMPTY_STRING;
		String strEvpRate = DomainConstants.EMPTY_STRING;
		String strResAcdity = DomainConstants.EMPTY_STRING;
		String strResAlknity = DomainConstants.EMPTY_STRING;
		String strShpHzdClass = DomainConstants.EMPTY_STRING;
		String strShpInfo = DomainConstants.EMPTY_STRING;
		String strStoInfo = DomainConstants.EMPTY_STRING;
		String strIntendMarkets = DomainConstants.EMPTY_STRING;

		String strWarehousingClassification = DomainConstants.EMPTY_STRING;
		String strUNNumber = DomainConstants.EMPTY_STRING;
		String strProperShippingName = DomainConstants.EMPTY_STRING;

		String strShippingInfo = DomainConstants.EMPTY_STRING;
		String strLabellingInfo = DomainConstants.EMPTY_STRING;
		String strStorageTempLimits = DomainConstants.EMPTY_STRING;
		String strStorageHumidityLimits = DomainConstants.EMPTY_STRING;
		String strComments = DomainConstants.EMPTY_STRING;

		String strIsProductBattery = DomainConstants.EMPTY_STRING;
		String strDoesProductContainBattery = DomainConstants.EMPTY_STRING;
		String strBatteriesInsideDevice = DomainConstants.EMPTY_STRING;
		String strBatteriesOutsideDevice = DomainConstants.EMPTY_STRING;
		String strAreBatteriesRequired = DomainConstants.EMPTY_STRING;
		String strBatteryChemicalComposition = DomainConstants.EMPTY_STRING;
		String strBatteryLithium = DomainConstants.EMPTY_STRING;
		String strBatteryWhRating = DomainConstants.EMPTY_STRING;
		String strBatteryVoltage = DomainConstants.EMPTY_STRING;
		String strBatteryWeight = DomainConstants.EMPTY_STRING;
		String strBatteryWeightUOM = DomainConstants.EMPTY_STRING;
		String strBatteryCapacity = DomainConstants.EMPTY_STRING;
		String strCellsForLiMetal = DomainConstants.EMPTY_STRING;
		String strButtonForLiMetal = DomainConstants.EMPTY_STRING;
		String strBatterySize = DomainConstants.EMPTY_STRING;
		String strGramsOfLithiumUOM = DomainConstants.EMPTY_STRING;
		String strLithiumBatteryEnergyUOM = DomainConstants.EMPTY_STRING;
		String strLithiumBatteryVoltageUOM = DomainConstants.EMPTY_STRING;
		String strTypicalCapacityUOM = DomainConstants.EMPTY_STRING;

		StringList slIntendMarkets = new StringList();
		StringList slShpHzdClass = new StringList();

		try {

			Map mpArgsPack = new HashMap();
			mpArgsPack.put("objectId", _OID);
			String[] strArgs = JPO.packArgs(mpArgsPack);

			
			if (StringHelper.validateString(_OID)) {
				StringList slObjectSelects = new StringList();
				slObjectSelects.add(DomainConstants.SELECT_TYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGINTENDEDMARKETS);
				slObjectSelects.add(PDFConstant.ATTRIBUTE_MARKETING_NAME);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSCLASSIFICATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXCONSUMERUNITSIZE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERUNITLABELING);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVAPORATIONRATE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCONSUMERUNITLABELING);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVEACIDITY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVERALKALINITY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPUNNUMBER);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPROPERSHIPPINGNAME);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGWAREHOUSECLASIFICATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPAKAGINGTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHAZARDCLASS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGGROUP);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEINFORMATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRIMARYPACKAGINGTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSECONDARYPACKAGINGTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGISTHEPRODUCTABATTERY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTCONTAINABATTERY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAREBATTERIESREQUIRED);
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

				if (slIntendMarkets != null && !slIntendMarkets.isEmpty()) {
					strIntendMarkets = FrameworkUtil.join(slIntendMarkets, pgV3Constants.SYMBOL_COMMA);
				}
				if (slShpHzdClass != null && !slShpHzdClass.isEmpty()) {
					strShpHzdClass = FrameworkUtil.join(slShpHzdClass, pgV3Constants.SYMBOL_COMMA);
				}

				if (!mpAttributeInfo.isEmpty()) {

					strTechnology = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
					strTechnology = StringHelper.filterLessAndGreaterThanSign(strTechnology);
					strPowerSource = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
					strBatteryType = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE);
					strChildrenProd = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
					strProdExpChild = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
					strChildSafeDesgn = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);

					strDngGoodsClass = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSCLASSIFICATION);
					strDngGoodsClass = StringHelper.filterLessAndGreaterThanSign(strDngGoodsClass);
					strMaxCUSize = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXCONSUMERUNITSIZE);
					if (strMaxCUSize.equalsIgnoreCase(PDFConstant.CONST_FALSE))
						strMaxCUSize = PDFConstant.CONST_NO;
					else if (strMaxCUSize.equalsIgnoreCase(PDFConstant.CONST_TRUE))
						strMaxCUSize = PDFConstant.CONST_YES;
					strCOUnitLable = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONSUMERUNITLABELING);
					strCOUnitLable = StringHelper.filterLessAndGreaterThanSign(strCOUnitLable);
					strCUUnitLable = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERUNITLABELING);
					strCUUnitLable = StringHelper.filterLessAndGreaterThanSign(strCUUnitLable);
					strEvpRate = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVAPORATIONRATE);
					strResAcdity = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVEACIDITY);
					strResAlknity = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVERALKALINITY);
					strShpInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);

					strShpInfo = StringHelper.filterLessAndGreaterThanSign(strShpInfo);
					strStoInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
					strStoInfo = StringHelper.filterLessAndGreaterThanSign(strStoInfo);

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
					strUNNumber = StringHelper.filterLessAndGreaterThanSign(strUNNumber);
					strProperShippingName = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGPROPERSHIPPINGNAME);
					strProperShippingName = StringHelper.filterLessAndGreaterThanSign(strProperShippingName);

					strShippingInfo = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
					strShippingInfo = StringHelper.filterLessAndGreaterThanSign(strShippingInfo);
					strLabellingInfo = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
					strLabellingInfo = StringHelper.filterLessAndGreaterThanSign(strLabellingInfo);
					strStorageTempLimits = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
					strStorageTempLimits = StringHelper.filterLessAndGreaterThanSign(strStorageTempLimits);
					strStorageHumidityLimits = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
					strStorageHumidityLimits = StringHelper.filterLessAndGreaterThanSign(strStorageHumidityLimits);
					strComments = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
					if (UIUtil.isNotNullAndNotEmpty(strComments)) {
						strComments = StringHelper.filterLessAndGreaterThanSign(strComments);
					}
					strIsProductBattery = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGISTHEPRODUCTABATTERY);
					strDoesProductContainBattery = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTCONTAINABATTERY);
					strBatteriesInsideDevice = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE);
					strBatteriesOutsideDevice = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE);
					strAreBatteriesRequired = (String) mpAttributeInfo
							.get(pgV3Constants.SELECT_ATTRIBUTE_PGAREBATTERIESREQUIRED);

					if (strObjType.equalsIgnoreCase(pgV3Constants.TYPE_DEVICEPRODUCTPART)
							&& UIUtil.isNotNullAndNotEmpty(strBatteryType)) {
						strBatterySize = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, pgV3Constants.TYPE_PGPLIBATTERYSIZE, "");
						strButtonForLiMetal = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, pgV3Constants.TYPE_PGPLIYESNO, "");
						strBatteryLithium = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLIBATTERYLITHUM);
						strBatteryWhRating = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLIBATTERYWHRATING);
						strBatteryVoltage = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLIBATTERYVOLTAGE);
						strBatteryWeight = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLBATTERYWEIGHT);
						strBatteryWeightUOM = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLBATTERYWEIGHTUOM);
						strBatteryCapacity = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLIBATTERYCAPACITY);
						strCellsForLiMetal = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", pgV3Constants.ATTRIBUTE_PGPLIBATTERYCELLS);
						strGramsOfLithiumUOM = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", PDFConstant.ATTRIBUTE_PGPLBATTERYWEIGHTLIUOM);
						strLithiumBatteryEnergyUOM = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(
								_context, strBatteryType, "", PDFConstant.ATTRIBUTE_PGPLBATTERYENRUOM);
						strLithiumBatteryVoltageUOM = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(
								_context, strBatteryType, "", PDFConstant.ATTRIBUTE_PGPLBATTERYVOLUOM);
						strTypicalCapacityUOM = (String) PDFPOCHelper.executepgDSOCPNProductDataClassMethod(_context,
								strBatteryType, "", PDFConstant.ATTRIBUTE_PGPLBATTERYTCUOM);
					}
					storageandTransportation.setTechnology(strTechnology);
					storageandTransportation.setPowerSource(strPowerSource);
					storageandTransportation.setBatteryType(strBatteryType);
					storageandTransportation.setBatteryweight(strBatteryWeight);
					storageandTransportation.setBatteryweightUnitofMeasure(strBatteryWeightUOM);
					storageandTransportation.setBatterychemicalcomposition(strBatteryChemicalComposition);
					storageandTransportation.setGramsofLithiumpercellbattery(strBatteryLithium);
					storageandTransportation.setGramsofLithiumpercellbatteryUnitofMeasure(strGramsOfLithiumUOM);
					storageandTransportation.setLithiumBatteryEnergy(strBatteryWhRating);
					storageandTransportation.setLithiumBatteryVoltageUnitofMeasure(strLithiumBatteryEnergyUOM);
					storageandTransportation.setLithiumBatteryVoltage(strBatteryVoltage);
					storageandTransportation.setLithiumBatteryVoltageUnitofMeasure(strLithiumBatteryVoltageUOM);
					storageandTransportation.setBatterySize(strBatterySize);
					storageandTransportation.setIsabuttonrequiredforLiMetal(strButtonForLiMetal);
					storageandTransportation.setNumberofCellsrequiredforLiMetal(strCellsForLiMetal);
					storageandTransportation.setTypicalCapacitymAh(strBatteryCapacity);
					storageandTransportation.setTypicalCapacityUnitofMeasure(strTypicalCapacityUOM);
					storageandTransportation.setIstheproductabattery(strIsProductBattery);
					storageandTransportation.setDoestheproductcontainabattery(strDoesProductContainBattery);
					storageandTransportation.setNumberofcellsbatteriesshippedinsideDevice(strBatteriesInsideDevice);
					storageandTransportation.setNumberofcellsbatteriesshippedoutsideDevice(strBatteriesOutsideDevice);
					storageandTransportation.setArebatteriesrequired(strAreBatteriesRequired);
					storageandTransportation.setIsProductMarketedasChildrensProduct(strChildrenProd);
					storageandTransportation.setDoestheProductRequireChildSafeDesign(strChildSafeDesgn);
					storageandTransportation.setIsProductExposedtoChildren(strProdExpChild);
					storageandTransportation.setWarehousingClassification(strWarehousingClassification);
					storageandTransportation.setEvaporationRate(strEvpRate);
					storageandTransportation.setReserveAcidity(strResAcdity);
					storageandTransportation.setReserveAlkalinity(strResAlknity);
					storageandTransportation.setShippingHazardClassification(strShpHzdClass);
					storageandTransportation.setShippingInformation(strShpInfo);
					storageandTransportation.setStorageConditions(strStoInfo);
					storageandTransportation.setIntendedMarkets(strIntendMarkets);
					storageandTransportation.setNominalBatteryWeight(strBatteryWeight);
					storageandTransportation.setNominalBatteryVoltage(strBatteryVoltage);
					storageandTransportation.setBatteryVoltageUoM(strLithiumBatteryVoltageUOM);
					storageandTransportation.setNumberofcells(strCellsForLiMetal);
					storageandTransportation.setGramsofLithiumpercell(strBatteryLithium);
					storageandTransportation.setGramsofLithiumpercellbatteryUnitofMeasure(strGramsOfLithiumUOM);
					storageandTransportation.setIsthisaButtonBattery(strButtonForLiMetal);
				}
				mpAttributeInfo.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return storageandTransportation;
	}

}
