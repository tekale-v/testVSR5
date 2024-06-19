package com.pg.widgets.failureView;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;
import com.matrixone.apps.domain.DomainConstants;

import matrix.util.StringList;

@ApplicationPath("/failureView")
public class PGFailureModuler extends ModelerBase {

	@Override
	public Class<?>[] getServices() {
		return new Class<?>[] {
			PGFailureServices.class
			};
	}

	public static void map2JsonBuilder(JsonObjectBuilder jsonObj, Map<String, Object> mapObj) {
		Object objValue;
		StringList objValueList;
		JsonArrayBuilder jsonArr;
		for (String key : mapObj.keySet()) {
			objValue = mapObj.get(key);
			if(objValue instanceof String) {
				jsonObj.add(key, (String) objValue);
			}else if(objValue instanceof StringList) {
				objValueList = (StringList) objValue;
				jsonArr = Json.createArrayBuilder();
				for (String stringVal : objValueList) {
					jsonArr.add(stringVal);
				}
				jsonObj.add(key, jsonArr);
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

}
