package com.pg.designtools.integrations.datahandlers;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.util.StringList;

public class JSONHandler {
	static final String KEY_PIPE_SEPARATOR="|";
	
	 /**
	  * This method converts a MapList which is a result of getRelatedObjects API, to a JsonArray
	  * @param mlInput : MapList 
	  * @return : JsonArray of expanded data
	  */
	 public JsonArray convertMapListToJsonFlatTable(MapList mlInput) {
		Stack<JsonObject> stackJsonObj = new Stack<>();
		JsonArrayBuilder jsonObjArray = Json.createArrayBuilder();
		Map<?, ?> tempMap;
		for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {

			tempMap = (Map<?, ?>) iterator.next();
			if(!stackJsonObj.empty()) {
				jsonObjArray.add(stackJsonObj.pop());	
			}
				
			stackJsonObj.push(convertMapToJsonObj(stackJsonObj.empty() ? Json.createObjectBuilder().build() : stackJsonObj.peek(), tempMap));
		}
		if(!stackJsonObj.empty()) {
			jsonObjArray.add(stackJsonObj.pop());	
		}
		return jsonObjArray.build();
	}

	 /**
	  * Method used convert Map to JsonObject
	  * @param inputJsonObj : JsonObject in which converted JsonObject from Map will be merged
	  * @param inputMap : Map to converted to JsonObject
	  * @return : JsonObject merged with converted Map and parent inputJsonObj
	  */
	public JsonObject convertMapToJsonObj(JsonObject inputJsonObj, Map<?, ?> inputMap) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet().forEach(e -> builder.add((String)e.getKey(), ((e.getValue() instanceof StringList) ? getStringFromSL((StringList) e.getValue()) : (String)e.getValue())));

		return builder.build();
	}
	 
	 	 
	 /**
	  * This method is used to convert StringList to pipe separated String
	  * @param slSelectValuelist : StringList to be converted
	  * @return : Pipe line separated String
	  */
	 String getStringFromSL(StringList slSelectValuelist) {
		
		StringBuilder sbSelectValue = new StringBuilder();
		String strSelectValue;
		String strReturnValue;
		for(int i=0; i<slSelectValuelist.size(); i++ ) {
			strSelectValue = slSelectValuelist.get(i);
			sbSelectValue.append(strSelectValue).append(KEY_PIPE_SEPARATOR);
		}
		
		strReturnValue = sbSelectValue.toString();
				
		if (UIUtil.isNotNullAndNotEmpty(strReturnValue)) {
			strReturnValue = strReturnValue.substring(0, strReturnValue.length() - 1);
		}
		
		return strReturnValue;
	}
}
