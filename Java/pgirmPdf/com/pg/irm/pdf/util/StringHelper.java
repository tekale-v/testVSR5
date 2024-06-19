/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: StringHelper
   Purpose: JAVA class created to do string manipulation.
 **/
package com.pg.irm.pdf.util;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.irm.pdf.PDFConstants;

public class StringHelper implements PDFConstants {

	/**
	 * @Desc Method to get string data removing html content
	 * @param inputHtmlHerfString - input data with html tags
	 * @return String- actual data as string
	 */
	public static String getHrefRemovedData(String inputHtmlHerfString) {
		String strRegEx = "<[^>]*>";
		if (UIUtil.isNotNullAndNotEmpty(inputHtmlHerfString)) {
			inputHtmlHerfString = inputHtmlHerfString.replaceAll(strRegEx, "");
		} else {
			inputHtmlHerfString = EMPTY_STRING;
		}
		return inputHtmlHerfString;
	}

	/**
	 * @Desc Method to remove special characters from map keys
	 * @param inputMap
	 * @return Map -removed key space data
	 */
	public static Map removeAllSpacesFromElementNameTag(Map<String, Object> inputMap) {
		Map returnMap = new HashMap();
		if (inputMap != null && inputMap.size() > 0) {
			for (String key : inputMap.keySet()) {
				returnMap.put(removeAllSpaces(key), (String)inputMap.get(key));
			}
		}
		return returnMap;
	}

	/**
	 * @Desc Method to remove special characters from string
	 * @param lowerkey
	 * @return string -removed special key and return string
	 */
	public static String removeAllSpaces(String key) {
		return key.replaceAll("\\s+","");
	}

	/**
	 * @Desc Method to get category from data
	 * @param category
	 * @return String
	 * @throws Exception
	 */
	public static String getRequestCategoryShortName(String category) throws Exception {
		return FrameworkUtil.split(category.trim(), SYMBOL_HYPHEN_STRICT).get(0).trim();
	}
}
