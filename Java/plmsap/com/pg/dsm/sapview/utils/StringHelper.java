package com.pg.dsm.sapview.utils;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class StringHelper {
	/**
	 * 
	 */
	private StringHelper() {
	}

	private static final Logger logger = Logger.getLogger(StringHelper.class.getName());

	/**
	 * @param object
	 * @return
	 */
	public static StringList convertToStringList(Object object) {
		StringList slObjectInfo = new StringList();
		if (object != null) {
			if (object instanceof StringList) {
				slObjectInfo = (StringList) object;
			} else {
				slObjectInfo.addElement((String) object);
			}
		}
		return slObjectInfo;
	}

	/**
	 * @param objectMap
	 * @param key
	 * @return
	 */
	public static String convertToString(Map<Object, Object> objectMap, String key) {
		return (objectMap.containsKey(key)) ? (String) objectMap.get(key) : DomainConstants.EMPTY_STRING;
	}

	/**
	 * @param objectMap
	 * @param key
	 * @return
	 */
	public static boolean convertToBoolean(Map<Object, Object> objectMap, String key) {
		boolean b = Boolean.FALSE;
		if (objectMap != null && objectMap.containsKey(key)) {
			String value = (String) objectMap.get(key);
			if (UIUtil.isNotNullAndNotEmpty(value) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(value)) {
				b = Boolean.TRUE;
			}
		}
		return b;
	}

	/**
	 * @param outputLine
	 * @param splitChar
	 * @return
	 */
	public static StringList split(String outputLine, String splitChar) {
		StringList splitedData = null;
		try {
			splitedData = StringUtil.splitString(outputLine, splitChar);
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		return splitedData;
	}
}
