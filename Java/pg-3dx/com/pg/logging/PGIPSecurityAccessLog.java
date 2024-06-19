package com.pg.logging;

import java.util.Map;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class PGIPSecurityAccessLog {

	private static final Logger _LOG_ACCESS = Logger.getLogger("AccessCheck");

	/**
	 * Logs the access information to log file when user tries to read a particular business object.
	 * 
	 * @param context
	 *            The enovia <code>Context</code> object
	 * @param strObjID
	 *            object Id of the business object. CANNOT be NULL or EMPTY<br>
	 * @param strType
	 *            Type of the business object. CAN BE NULL or EMPTY<br>
	 * @param strName
	 *            Name of the business object. CAN BE NULL or EMPTY<br>
	 * @param strRev
	 *            Revision of the business object. CAN BE NULL or EMPTY<br>
	 * @param strSource
	 *            Free text string stating the web-page accessed to read the business objects. CAN BE NULL or EMPTY<br>
	 *            Example = "PROPERTIES PAGE, SUMMARY TABLE, etc."
	 */
	public static void writeAccessLog(Context context, String strObjID, String strType, String strName, String strRev, String strSource) {
		try {
			String contextUser = context.getUser();
			String _writeToLog4j = EnoviaResourceBundle.getProperty(context, "EmxExportControl.LogConfig.CreateLog4jLog");

			// Get User Location Start
			// String strLoginLocation = JPO.invoke(context, "EXCPersonLicenseCheck", null, "getLoginLocationfromMap", null, String.class);
			String strLoginLocation = "NO_LOCATION_DEFINED";
			Map<?, ?> mapEXCLocation = (Map<?, ?>) PersonUtil.getPersonProperty(context, "EXCLocation");
			if (mapEXCLocation != null) {
				strLoginLocation = (String) mapEXCLocation.get("login_location");
			}
			if (UIUtil.isNullOrEmpty(strLoginLocation)) {
				strLoginLocation = "NO_LOCATION_DEFINED";
			}
			// Get User Location End

			if (UIUtil.isNullOrEmpty(strType) || UIUtil.isNullOrEmpty(strName)) {
				if (UIUtil.isNotNullAndNotEmpty(strObjID)) {
					DomainObject domObject = DomainObject.newInstance(context, strObjID);
					StringList slSelects = new StringList(3);
					slSelects.addElement(DomainConstants.SELECT_TYPE);
					slSelects.addElement(DomainConstants.SELECT_NAME);
					slSelects.addElement(DomainConstants.SELECT_REVISION);
					Map<?, ?> objInfoMap = domObject.getInfo(context, slSelects);
					strType = (String) objInfoMap.get(DomainConstants.SELECT_TYPE);
					strName = (String) objInfoMap.get(DomainConstants.SELECT_NAME);
					strRev = (String) objInfoMap.get(DomainConstants.SELECT_REVISION);
				}
			}

			if (_LOG_ACCESS.isDebugEnabled() && !_writeToLog4j.equalsIgnoreCase("false")) {
				_LOG_ACCESS.debug("User :: " + contextUser + " :: Location :: " + strLoginLocation + " :: " + strType + " " + strName + " " + strRev
						+ " :: Accessed the Object" + (UIUtil.isNotNullAndNotEmpty(strSource) ? " from :: " + strSource : ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
