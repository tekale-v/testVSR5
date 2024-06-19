package com.pg.v4.util.mos.ui;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class LinkCreationForUI {
	// Added and Modified by DSM(Sogeti) 2018x.6 for COS Defect Id 38934 Starts
	static Map<String, String> typeShortNames = new HashMap();

	static {
		typeShortNames.put(pgV3Constants.TYPE_PGFINISHEDPRODUCT, "FP");
		typeShortNames.put(pgV3Constants.TYPE_FINISHEDPRODUCTPART, "FPP");
		typeShortNames.put(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART, "PAP");
		typeShortNames.put(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY, "PSUB");
		typeShortNames.put(pgV3Constants.TYPE_PGFORMULATEDPRODUCT, "FC");
		typeShortNames.put(pgV3Constants.TYPE_PGARTWORK, "Art");
		typeShortNames.put(pgV3Constants.TYPE_POA, "Art");
		typeShortNames.put(pgV3Constants.TYPE_PGPACKINGMATERIAL, "IPMS");
		typeShortNames.put(pgV3Constants.TYPE_PACKAGINGMATERIALPART, "PMP");
		typeShortNames.put(pgV3Constants.TYPE_PGONLINEPRINTINGPART, "OPP");
		typeShortNames.put(pgV3Constants.TYPE_PGRAWMATERIAL, "IRMS");
		typeShortNames.put(pgV3Constants.TYPE_PGMASTERRAWMATERIAL, "MRMS");
		typeShortNames.put(pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL, "MPMS");
		typeShortNames.put(pgV3Constants.TYPE_FABRICATEDPART, "FAB");
		typeShortNames.put(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART, "APP");
		typeShortNames.put(pgV3Constants.TYPE_DEVICEPRODUCTPART, "DPP");
		typeShortNames.put(pgV3Constants.TYPE_FORMULATIONPART, "FOP");
		typeShortNames.put(pgV3Constants.TYPE_RAWMATERIALPART, "RMP");
		typeShortNames.put(pgV3Constants.TYPE_PGCONSUMERUNITPART, "COP");
	}

	/**
	 * @param sType
	 * @return
	 * @throws FrameworkException
	 */
	public static String getLinkInformation(Context context, String sType, String objId) throws FrameworkException {
		String returnLinkReference = "";
		String shortType = typeShortNames.get(sType);
		if ("FC".equalsIgnoreCase(shortType) || "FOP".equalsIgnoreCase(shortType) || "APP".equalsIgnoreCase(shortType)
				|| "DPP".equalsIgnoreCase(shortType) || ("COP".equalsIgnoreCase(shortType)
						&& isPartSetProduct(context, DomainObject.newInstance(context, objId)))) {
			returnLinkReference = MOSConstants.CONST_SUBMITFORMFC.getValue();
		} else if ("IRMS".equalsIgnoreCase(shortType) || "RMP".equalsIgnoreCase(shortType)
				|| "MRMS".equalsIgnoreCase(shortType)) {
			returnLinkReference = MOSConstants.CONST_SUBMITFORMIRMS.getValue();
		} else if ("IPMS".equalsIgnoreCase(shortType) || "PMP".equalsIgnoreCase(shortType)
				|| "MPMS".equalsIgnoreCase(shortType) || "OPP".equalsIgnoreCase(shortType)) {
			returnLinkReference = MOSConstants.CONST_SUBMITFORMIPMS.getValue();
		} else if ("Art".equalsIgnoreCase(shortType)) {
			returnLinkReference = MOSConstants.CONST_SUBMITFORMART.getValue();
		} else {
			returnLinkReference = MOSConstants.CONST_SUBMITFORM.getValue();
		}
		return returnLinkReference;
	}

	/**
	 * @param context
	 * @param domObj
	 * @return
	 * @throws FrameworkException
	 */
	public static boolean isPartSetProduct(Context context, DomainObject domObj) throws FrameworkException {
		boolean bCheckSetProduct = false;
		if (pgV3Constants.KEY_YES
				.equalsIgnoreCase(domObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
			bCheckSetProduct = true;
		}
		return bCheckSetProduct;
	}

	/**
	 * @author DSM(Sogeti)
	 *
	 */
	protected enum MOSConstants {

		CONST_SUBMITFORM("submitForm"), 
		CONST_SUBMITFORMIPMS("submitFormIPMS"), 
		CONST_SUBMITFORMART("submitFormART"),
		CONST_SUBMITFORMFC("submitFormFC"),
		CONST_SUBMITFORMIRMS("submitFormIRMS");
		private final String value;

		MOSConstants(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
	// Added and Modified by DSM(Sogeti) 2018x.6 for COS Defect Id 38934 Ends
}
