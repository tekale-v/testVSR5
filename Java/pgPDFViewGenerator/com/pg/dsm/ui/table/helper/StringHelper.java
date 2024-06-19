package com.pg.dsm.ui.table.helper;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.StringUtil;

public class StringHelper {
 
  private StringHelper() {}	
  public static String getHrefRemovedData(String inputHtmlHerfString)
  {
    String strRegEx = "<[^>]*>";
    if (UIUtil.isNotNullAndNotEmpty(inputHtmlHerfString))
      inputHtmlHerfString = inputHtmlHerfString.replaceAll(strRegEx, "");
    else {
      inputHtmlHerfString = "";
    }
    return inputHtmlHerfString;
  }
  
  public static String replaceBrToNewLine(String inputHtmlHerfString)
  {
		if(UIUtil.isNotNullAndNotEmpty(inputHtmlHerfString)) 
			return inputHtmlHerfString.replaceAll("(?i)<br*/?>" , "|");
		else 
			return inputHtmlHerfString;
  }

  public static Map<String,String> removeAllSpacesFromElementNameTag(Map<String, Object> inputMap)
  {
    Map<String,String> returnMap = new HashMap<>();
    if ((inputMap != null) && (inputMap.size() > 0)) {
      for (String key : inputMap.keySet()) {
        returnMap.put(removeAllSpaces(key), (String)inputMap.get(key));
      }
    }
    return returnMap;
  }

  public static String removeAllSpaces(String key)
  {
    return key.replaceAll("\\s+", "");
  }

  public static String getRequestCategoryShortName(String category) {
    return ((String)StringUtil.split(category.trim(), "-").get(0)).trim();
  }
}