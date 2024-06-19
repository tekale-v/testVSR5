package com.pg.fis.integration.util;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;

import java.util.Properties;
import matrix.db.Context;
import matrix.util.StringList;

public class PGFISIntegrationUtil {

	public PGFISIntegrationUtil(Context context) {

	}

	/**
	 * Stack trace to string
	 * 
	 * @param ex
	 * @return
	 */
	public static String getExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		return sw.toString(); // stack trace as a string
	}

	public static MapList findObjectAndRetunDetails(Context context, String strType, String strName,
			StringList selectable, String strWhere) throws Exception {

		MapList result = new MapList();
		try {
			result = DomainObject.findObjects(context, 
					strType, // Type Pattern
					strName, // Name pattern
					DomainConstants.QUERY_WILDCARD, // Rev pattern
					DomainConstants.QUERY_WILDCARD, // owner pattern
					PGFISWSConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern
					strWhere, // where expression
					false,   // expand type
					selectable); // bus selects
		} catch (Exception exp) {
			throw new Exception(exp.toString());
		}
		return result;

	}

	public static void releasePart(Context context, String objectId) throws Exception {
		try {
			DomainObject dmObj = DomainObject.newInstance(context, objectId);
			dmObj.setAttributeValue(context, PGFISWSConstants.ATTRIBUTE_REASON_FOR_CHANGE, PGFISWSConstants.CONST_REVISE_FOR_FIS);
			dmObj.promote(context);
			dmObj.promote(context);
			dmObj.promote(context);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

	}
	
	/**
	 * This is a utility method to get the value from Page file property
	 * @param context
	 * @param strPageName : Page File name
	 * @param strKey : Property Key
	 * @return : returns value of the given property key
	 * @throws Exception
	 */
	public static String getPageProperty(Context context, String strPageName, String strKey)throws Exception {
		String strValue = DomainConstants.EMPTY_STRING;
		try{
			String isPageExists	= MqlUtil.mqlCommand(context, "list page $1", strPageName);
			String strProperties= UIUtil.isNotNullAndNotEmpty(isPageExists) ? MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageName) : "";
			if(UIUtil.isNotNullAndNotEmpty(strProperties) && UIUtil.isNotNullAndNotEmpty(strKey)){
				Properties properties = new Properties();
				properties.load(new StringReader(strProperties));
				strValue = properties.getProperty(strKey);
			}
		}catch(Exception ex){
			throw ex;
		}
		return strValue;
	}

}
