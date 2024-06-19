package com.pg.dsm.ws.restapp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
	/**
	 * @param objIPSProduct
	 * @return
	 */
	public static String jaxbObjectToJSON(Object objIPSProduct) {
		String sw = "";
		try {

			ObjectMapper mapper = new ObjectMapper();
			sw = mapper.writeValueAsString(objIPSProduct);

		} catch (Exception e) {
			Logger.getLogger(JSONUtils.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		}
		return sw;
	}

	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
		Map retMap = new HashMap();

		if (json != JSONObject.NULL) {
			retMap = toMap(json);
		}
		return retMap;
	}

	public static List jsonToList(JSONArray jsonArray) throws JSONException {
		List retList = new ArrayList();

		if (jsonArray != JSONObject.NULL) {
			retList = toList(jsonArray);
		}
		return retList;
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map map = new HashMap();

		Iterator keysItr = object.keys();
		while (keysItr.hasNext()) {
			String key = (String) keysItr.next();
			Object value = object.get(key);

			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List list = new ArrayList();
		for (int i = 0; i < array.length(); ++i) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}
