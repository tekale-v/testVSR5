package com.pg.widgets.changeMgmtCompareReport;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;

import matrix.util.StringList;

@ApplicationPath("/changeMgmtCompareReport")
public class PGCompareReportModeler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {
			PGCompareReportServices.class
			};
	}

	public static void map2JsonBuilder(JsonObjectBuilder jsonObj, Map<String, Object> mapObj) {
		for (Map.Entry<String,Object> entry : mapObj.entrySet()) {
			String key = entry.getKey();
			Object objValue = entry.getValue();
			if(objValue instanceof String) {
				jsonObj.add(key, (String) objValue);
			}else if(objValue instanceof StringList) {
				StringList objValueList = (StringList) objValue;
				JsonArrayBuilder jsonArr = Json.createArrayBuilder();
				for (String stringVal : objValueList) {
					jsonArr.add(stringVal);
				}
				jsonObj.add(key, jsonArr);
			}
			else if(objValue instanceof HashMap<?, ?>) {
				JsonObjectBuilder jsonObjChild = Json.createObjectBuilder();
				map2JsonBuilder(jsonObjChild,(HashMap) objValue);
				jsonObj.add(key, jsonObjChild);
			}else if(objValue instanceof MapList) {
				MapList objValueList = (MapList) objValue;
				JsonArrayBuilder jsonObjChild = Json.createArrayBuilder();
				mapList2JsonArray(jsonObjChild, objValueList);
				jsonObj.add(key, jsonObjChild);
			}else{
				if(null!=objValue)
				{
				jsonObj.add(key, objValue.toString());
				}
				else
				{
					jsonObj.add(key, DomainConstants.EMPTY_STRING);
				}
			}
		}
	}
	public static void mapList2JsonArray(JsonArrayBuilder outArr, MapList mlObj) {
		
		JsonObjectBuilder jsonObj;
		Map<String, Object> mapObj;
		for(int i=0;i<mlObj.size();i++)
		{
			jsonObj = Json.createObjectBuilder();
			mapObj = 	(Map<String, Object>) mlObj.get(i);
			map2JsonBuilder(jsonObj, mapObj);
			outArr.add(jsonObj);			
		}
		
	}
	/** This method is used to return StringList for selected Object 
     * @param Object
     * @return StringList
     */

    public static StringList returnStringListForObject(Object obj)
    {
    	StringList sl = new StringList();
    	if(obj != null)
    	{
	    	if(obj instanceof StringList)
	    	{
	    		sl = (StringList)obj;
	    	}
	    	else
	    	{
	    		sl.addElement((String)obj);
	    	}
    	}
    	return sl;
    }
}
