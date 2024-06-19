package com.pdfview.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.text.WordUtils;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pg.v3.custom.pgV3Constants;

import matrix.util.StringList;

public class StringHelper {
	/**
	 * Check if input string is valid.
	 * 
	 * @param strVerifyString
	 * @return
	 */
	public static boolean validateString(String strVerifyString) {
		boolean isStringDesired = false;
		if (null != strVerifyString && !"".equals(strVerifyString) && !"null".equalsIgnoreCase(strVerifyString)) {
			isStringDesired = true;
		}
		return isStringDesired;
	}

	/**
	 * Method to concate from string input value
	 * 
	 * @param String
	 * @return String
	 * @throws Exception if the operation fails
	 */
	public static String convertObjectToString(Object obj) {
		String convertedString = DomainConstants.EMPTY_STRING;
		if (obj != null) {
			if (obj instanceof StringList) {
				convertedString = FrameworkUtil.join((StringList) obj, pgV3Constants.SYMBOL_COMMA+pgV3Constants.SYMBOL_SPACE);
			} else if (obj instanceof String) {
				convertedString = (String) obj;
			}
		}
		return convertedString;
	}

	/**
	 * Method to split and wrap the string
	 * 
	 * @param String
	 * @return String
	 * @throws Exception if the operation fails
	 */
	public static String wrapStringWord(String strwordWrap) {
		return WordUtils.wrap(strwordWrap, 30, "\n", true);
	}
	/**
	 * Method to split and wrap the string
	 * 
	 * @param String
	 * @return String
	 * @throws Exception if the operation fails
	 */
	public static String filterLessAndGreaterThanSign(String strSentence) {
		if (UIUtil.isNotNullAndNotEmpty(strSentence)) {
			strSentence = strSentence.replaceAll("[<]", "#LESS_THAN");
			strSentence = strSentence.replaceAll("[>]", "#GREATER_THAN");
		}
		return strSentence;
	}
	
	/**
	 * Check pased StringList object contains valid String
	 * @param obj
	 * @return
	 */
	public static String validateString1(Object obj){
		String strReturn = DomainConstants.EMPTY_STRING;
		String strTestMethod = DomainConstants.EMPTY_STRING;
		if(null !=obj && !"null".equals(obj)) {
			if (obj instanceof String) {
				strReturn = (String) obj;
			}
			else if(obj instanceof StringList) {
				StringList listObject = (StringList) obj;
				if(listObject.size()==1){
					strReturn+=(String)listObject.get(0);
				} else{
					for (Iterator iterator2 = listObject.iterator(); iterator2.hasNext();){
						strTestMethod = (String) iterator2.next();
						strReturn+="<br/>"+strTestMethod;
					}
				}
			}
		}
		return strReturn;
	}
	/**
	 * @Desc this method is removing html tags and get string data
	 * @param inputHtmlHerfString
	 * @return String
	 */
	public static String getHrefRemovedData(String inputHtmlHerfString) {
		String strRegEx = "<[^>]*>"; 
		if(UIUtil.isNotNullAndNotEmpty(inputHtmlHerfString)) {
			inputHtmlHerfString= inputHtmlHerfString.replaceAll(strRegEx, DomainConstants.EMPTY_STRING);	
		}
		return inputHtmlHerfString;
	}
	/**
	 * @Desc This method is removing &nbsp; from html string data
	 * @param inputString
	 * @return String
	 */
	public static String removedAndNBSP(String inputString) {
		String strRegEx = "&amp;nbsp;"; 
		String strRegEx1 = "&nbsp;"; 
		if(UIUtil.isNotNullAndNotEmpty(inputString)) {
			inputString= inputString.replaceAll(strRegEx, DomainConstants.EMPTY_STRING);	
			inputString= inputString.replaceAll(strRegEx1, DomainConstants.EMPTY_STRING);	
		}
		return inputString;
	}
	/**
	 * @Desc This method is replacing special characters from map keys
	 * @param inputMap
	 * @return
	 */
	public static Map replaceSpecialCharactersFromKey(Map<String, Object> inputMap) {
		Map returnMap = new HashMap();
		if (inputMap != null && inputMap.size() > 0) {
			for (String key : inputMap.keySet()) {
				if(!UIUtil.isNullOrEmpty((String) inputMap.get(key))) {
					returnMap.put(removeSpecialCharacters(key), inputMap.get(key));
				}
			}
		}
		return returnMap;
	}

	/**
	 * @Desc This method is replacing special characters from string
	 * @param lowerkey
	 * @return
	 */
	public static String removeSpecialCharacters(String lowerkey) {
		return lowerkey.replaceAll(pgV3Constants.SYMBOL_SPACE, DomainConstants.EMPTY_STRING);
	}
	
	
	/**
	 * @Desc This method is wrapping string data to fit into table columns
	 * @param strwordWrap
	 * @return
	 * @throws Exception
	 */
	public static String wrapStringInTable(String strwordWrap) throws Exception{
		StringBuilder sb = new StringBuilder();
		int iFirstFullStopIndex = 12;
		try{
			if (strwordWrap != null && (strwordWrap.length() > 12)){
				while(strwordWrap.length() > 12){
					sb.append(strwordWrap.substring(0, 12));
					sb.append("<br></br>");
					strwordWrap = strwordWrap.substring(iFirstFullStopIndex);
				}
				if(strwordWrap.length() > 0){
					sb.append(strwordWrap);
				}
				strwordWrap = sb.toString();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return strwordWrap;
	}
	/**
	 * @Desc This method is converting TreeSet to StringList
	 * @param tSet
	 * @return
	 */
	public static StringList convertArrayListToStringList(TreeSet<String> tSet) {
		StringList results = new StringList(tSet.size());

		for (String sTemp : tSet) {
			results.add(DomainConstants.EMPTY_STRING);
		}

		int i = 0;

		for (String sTemp : tSet) {
			results.set(i, sTemp);
			i++;
		}
		return results;

	}
	/**
	 * @Desc method removing <br/><br /> tags from input string 
	 * @param inputStr
	 * @return
	 */
	public static String removeBRTags(String inputStr) {
		inputStr=inputStr.replace(PDFConstant.CONST_LESSTHAN, DomainConstants.EMPTY_STRING).replace(PDFConstant.CONST_BREAK_CLOSE_SPACE, DomainConstants.EMPTY_STRING).replace(PDFConstant.CONST_CLOSE_BREAK, DomainConstants.EMPTY_STRING);
		return inputStr;
	}
}
