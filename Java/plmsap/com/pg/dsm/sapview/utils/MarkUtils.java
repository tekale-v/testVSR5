package com.pg.dsm.sapview.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.pg.dsm.sapview.beans.bo.SAPPartBean;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.util.MatrixException;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class MarkUtils {
	/**
	 * private constructor
	 */
	private MarkUtils() {
	}

	private static final Logger logger = Logger.getLogger(MarkUtils.class.getName());

	/**
	 * @return
	 */
	private static String getBOMeDeliveryAttr(Context context) {
		return PropertyUtil.getSchemaProperty(context, "attribute_pgBOMeDelivery");
	}

	/**
	 * @return
	 */
	private static String getBOMeDeliveryParentAttr(Context context) {
		return PropertyUtil.getSchemaProperty(context, "attribute_pgBOMeDeliveryParent");
	}

	/**
	 * @param context
	 * @param dObj
	 * @throws FrameworkException
	 */
	public static void markBOMeDeliveryFlag(Context context, DomainObject dObj) throws FrameworkException {
		if (dObj != null && addBOMeDeliveryInterface(context, dObj))
			dObj.setAttributeValue(context, getBOMeDeliveryAttr(context), pgV3Constants.CAPS_TRUE);
	}

	/**
	 * @param context
	 * @param dObj
	 * @throws FrameworkException
	 */
	public static void markBOMeDeliveryParentFlag(Context context, DomainObject dObj) throws FrameworkException {
		if (dObj != null && addBOMeDeliveryInterface(context, dObj))
			dObj.setAttributeValue(context, getBOMeDeliveryParentAttr(context), pgV3Constants.CAPS_TRUE);
	}

	/**
	 * @param context
	 * @param strType
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static boolean isAllowedTypeForSAPBOMeDelivery(Context context, String strType, String key) {
		boolean bAllowedType = Boolean.FALSE;
		String strBOMeDeliveryAllowedTypes = UINavigatorUtil.getI18nString("emxCPN.BOMeDelivery." + key + ".Types",
				"emxCPN", context.getLocale().getLanguage());
		if (BusinessUtil.isNotNullOrEmpty(strType) && strBOMeDeliveryAllowedTypes.contains(strType)) {
			bAllowedType = Boolean.TRUE;
		}
		return bAllowedType;
	}

	/**
	 * @param context
	 * @param strObjectId
	 */
	public static boolean addBOMeDeliveryInterface(Context context, DomainObject doObj) {
		boolean isInterfaceAdded = Boolean.FALSE;
		try {
			if (doObj != null && !checkInterfaceOnObject(context, doObj)) {
				doObj.addBusinessInterface(context, new BusinessInterface(
						SAPViewConstant.SAP_BOM_EDELIVERY_ATTR_INTERFACE.getValue(), new Vault(doObj.getVault())));
			}
			isInterfaceAdded = Boolean.TRUE;
		} catch (MatrixException e) {
			logger.log(Level.WARNING, null, e);
		}
		return isInterfaceAdded;
	}

	/**
	 * @param context
	 * @param doObj
	 * @return
	 */
	private static boolean checkInterfaceOnObject(Context context, DomainObject doObj) {
		boolean bHasInterface = Boolean.FALSE;
		if (doObj != null) {
			try {
				BusinessInterfaceList busInterfaces = doObj.getBusinessInterfaces(context);
				if (busInterfaces.toString().contains(SAPViewConstant.SAP_BOM_EDELIVERY_ATTR_INTERFACE.getValue()))
					bHasInterface = Boolean.TRUE;
			} catch (MatrixException e) {
				logger.log(Level.WARNING, null, e);
			}
		}
		return bHasInterface;
	}

	/**
	 * @param productPart
	 * @throws FrameworkException
	 */
	public static void doPartUnMarking(Context context, SAPPartBean productPart) {
		Map<String, String> mpAttributes = new HashMap<>();

		if (productPart.isBOMeDeliveryToSAP()) {
			mpAttributes.put(getBOMeDeliveryAttr(context), pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
		}

		if (productPart.isBOMeDeliveryParentToSAP()) {
			mpAttributes.put(getBOMeDeliveryParentAttr(context), pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
		}

		doAttrUpdation(context, productPart.getId(), mpAttributes);
	}

	/**
	 * @param context
	 * @param objId
	 * @param mpAttributes
	 * @throws FrameworkException
	 */
	private static void doAttrUpdation(Context context, String objId, Map<String, String> mpAttributes) {
		if (!mpAttributes.isEmpty()) {
			try {
				DomainObject domIps = DomainObject.newInstance(context, objId);
				domIps.setAttributeValues(context, mpAttributes);
			} catch (FrameworkException e) {
				logger.log(Level.WARNING, null, e);
			}
		}
	}
}
