package com.pdfview.helper;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.pdfview.impl.FPP.StorageTransportationData;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class GetStorageTransportationData {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetStorageTransportationData(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}
	/**
	 * Method to retrieve the values of Storage and Tranportation form
	 * @return
	 * throws exception
	 */

	public StorageTransportationData getComponent() {
		StorageTransportationData storageTransportationData = new StorageTransportationData();
		String strPowerSource = DomainConstants.EMPTY_STRING;
		String strBatteryType = DomainConstants.EMPTY_STRING;
		String strCOUnitLable = DomainConstants.EMPTY_STRING;
		String strCUUnitLable = DomainConstants.EMPTY_STRING;
		String strShpInfo = DomainConstants.EMPTY_STRING;
		String strStoInfo = DomainConstants.EMPTY_STRING;
		String strLabellingInfo = DomainConstants.EMPTY_STRING;
		String strStorageTempLimits = DomainConstants.EMPTY_STRING;
		String strStorageHumidityLimits = DomainConstants.EMPTY_STRING;
		try {
			if (StringHelper.validateString(_OID)) {
				StringList slObjectSelects = new StringList(10);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCONSUMERUNITLABELING);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERUNITLABELING);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
				slObjectSelects.add(DomainConstants.SELECT_TYPE);
				DomainObject domainObject = DomainObject.newInstance(_context, _OID);
				Map mpAttributeInfo = domainObject.getInfo(_context, slObjectSelects);
				if (mpAttributeInfo!=null && !mpAttributeInfo.isEmpty()) {
					strPowerSource = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
					strBatteryType = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE);
					strCOUnitLable = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONSUMERUNITLABELING);
					strCUUnitLable = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERUNITLABELING);
					strShpInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
					strLabellingInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
					strStoInfo = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
					strStorageTempLimits = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
					strStorageHumidityLimits = (String) mpAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
					
					strCUUnitLable = StringHelper.filterLessAndGreaterThanSign(strCUUnitLable);
					strShpInfo = StringHelper.filterLessAndGreaterThanSign(strShpInfo);
					strLabellingInfo = StringHelper.filterLessAndGreaterThanSign(strLabellingInfo);
					strStoInfo = StringHelper.filterLessAndGreaterThanSign(strStoInfo);
					strStorageTempLimits = StringHelper.filterLessAndGreaterThanSign(strStorageTempLimits);
					strStorageHumidityLimits = StringHelper.filterLessAndGreaterThanSign(strStorageHumidityLimits);
					strCOUnitLable = StringHelper.filterLessAndGreaterThanSign(strCOUnitLable);

					storageTransportationData.setPowerSource(strPowerSource);
					storageTransportationData.setBatteryType(strBatteryType);
					storageTransportationData.setConsumerUnitLabeling(strCOUnitLable);
					storageTransportationData.setCustomerUnitLabeling(strCUUnitLable);
					storageTransportationData.setShippingInformation(strShpInfo);
					storageTransportationData.setLabelingInformation(strLabellingInfo);
					storageTransportationData.setStorageConditions(strStoInfo);
					storageTransportationData.setStorageTemperatureLimits(strStorageTempLimits);
					storageTransportationData.setStorageHumidityLimits(strStorageHumidityLimits);
					mpAttributeInfo.clear();
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return storageTransportationData;
	}
}
