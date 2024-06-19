/*
 * PGCacheUtil.java
 * 
 * Added by Dashboard Team
 * This Java code has the logic to get all the Business Area, Platform and Chassis picklist object info from the database and will be returned in the form of JSON. 
 * Which will be stored in server cache
 * 
 */

package com.pg.cache;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.json.JsonArrayBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.CacheUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGCacheUtil {
	private static final String COMMA_SEPARATOR = ",";
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_VALUE = "value";
	public static final String CACHE_PAGE_NAME = "pgCache";
	public static final String PREFIX_SERVER_CACHE = "pgCache_";
	private static final String CACHE_PROPERTY_TS = "timestamp";
	private static final String VALUE_KILOBYTES = "262144";
	private static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	private static final String CACHE_PROPERTY_DATA = "data";
	private static final boolean IS_DEBUG_MODE = true;
	private static final Logger logger = Logger.getLogger(PGCacheUtil.class.getName());

	private PGCacheUtil() {
	}

	/**
	 * Method to get picklist TS data from server cache
	 * 
	 * @param context
	 *            : eMatrix context object
	 * @return String : Time stamp info from cache
	 * @throws FrameworkException
	 */
	public static String getCacheTimestamp(Context context, String strTypes) throws FrameworkException {
		JsonObjectBuilder jsonObjBldrResponse = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrTS = Json.createArrayBuilder();
		StringList slTypes = StringUtil.split(strTypes, COMMA_SEPARATOR);
		for (String strTypeName : slTypes) {
			jsonArrTS.add(getTimestampFromServerCache(context, strTypeName));
		}
		jsonObjBldrResponse.add(CACHE_PROPERTY_DATA, jsonArrTS);
		return jsonObjBldrResponse.build().toString();
	}

	/**
	 * This method returns server cache timestamp for a type
	 * 
	 * @param context
	 * @param strTypeName
	 * @return
	 * @throws FrameworkException
	 */
	public static String getCacheTypeTimestamp(Context context, String strTypeName) throws FrameworkException {
		JsonObjectBuilder jsonObjBldrResponse = Json.createObjectBuilder();
		jsonObjBldrResponse.add(CACHE_PROPERTY_DATA, getTimestampFromServerCache(context, strTypeName));
		return jsonObjBldrResponse.build().toString();
	}

	/**
	 * This method returns server cache timestamp for a type
	 * 
	 * @param context
	 * @param strTypeName
	 * @return
	 * @throws FrameworkException
	 */
	private static JsonObjectBuilder getTimestampFromServerCache(Context context, String strTypeName) throws FrameworkException {
		JsonObjectBuilder jsonObjBldrTemp = Json.createObjectBuilder();
		JsonObject jsonObjServerCache = (JsonObject) CacheUtil.getCacheObject(context, PREFIX_SERVER_CACHE + strTypeName);
		if(jsonObjServerCache != null) {
		JsonValue jsonValueCacheTimestamp = jsonObjServerCache.get(CACHE_PROPERTY_TS);
		jsonObjBldrTemp.add(JSON_KEY_NAME, strTypeName);
		jsonObjBldrTemp.add(JSON_KEY_VALUE, jsonValueCacheTimestamp);}
		return jsonObjBldrTemp;
	}

	/**
	 * Method to get picklist info from sever cache for indexedDB stores
	 * 
	 * @param context
	 *            : eMatrix context object
	 * @param strStoreName
	 *            : String store name
	 * @return String : Picklist info for indexedDB stores
	 * @throws MatrixException
	 */
	public static String getCacheForType(Context context, String strTypeName) throws MatrixException {
		JsonObjectBuilder jsonObjBldrResponse = Json.createObjectBuilder();
		JsonObject jsonObjServerCache = (JsonObject) CacheUtil.getCacheObject(context, PREFIX_SERVER_CACHE + strTypeName);
		JsonArray objectsArr = null;
		if (jsonObjServerCache == null) {
			if (IS_DEBUG_MODE) {
				logger.info("RELOADING CACHE");
			}
			jsonObjServerCache = reloadServerCacheFromPage(context, strTypeName);
		}
		if (IS_DEBUG_MODE) {
			logger.info("PICKING FROM CACHE");
		}
		if (jsonObjServerCache != null) {
			objectsArr = (JsonArray) jsonObjServerCache.get(CACHE_PROPERTY_DATA);
		} else {
			return DomainConstants.EMPTY_STRING;
		}
		jsonObjBldrResponse.add(CACHE_PROPERTY_DATA, objectsArr);
		return jsonObjBldrResponse.build().toString();
	}

	/**
	 * @param context
	 * @throws MatrixException
	 */
	public static void reloadAllServerCacheFromPage(Context context) throws MatrixException {
		JsonObject jsonObjCache = null;
		Page pageCache = new Page(CACHE_PAGE_NAME);
		if (pageCache.exists(context)) {
			pageCache.open(context);
			String strPageModified = Long.toString(pageCache.getModificationTime(context));
			String strPageContent = pageCache.getContents(context);
			pageCache.close(context);
			JsonObject jsonPageContent = getJsonFromJsonString(strPageContent);
			for (PGCachedTypes pgCachedPLTypes : PGCachedTypes.values()) {
				String strCachedTypeName = pgCachedPLTypes.name();
				JsonObject jsonCurrentServerCacheData = (JsonObject) CacheUtil.getCacheObject(context, PREFIX_SERVER_CACHE + strCachedTypeName);
				if (jsonCurrentServerCacheData == null || hasCacheExpired(jsonCurrentServerCacheData, strPageModified)) {
					JsonArray jsonObjectsDataArray = jsonPageContent.getJsonArray(PREFIX_SERVER_CACHE + strCachedTypeName);
					JsonObjectBuilder jsonNewServerCacheData = Json.createObjectBuilder();
					jsonNewServerCacheData.add(CACHE_PROPERTY_DATA, jsonObjectsDataArray);
					jsonNewServerCacheData.add(CACHE_PROPERTY_TS, strPageModified);
					jsonObjCache = jsonNewServerCacheData.build();
					CacheUtil.setCacheObject(context, PREFIX_SERVER_CACHE + strCachedTypeName, jsonObjCache);
				}
			}
		} else {
			logger.log(Level.WARNING, "Cannot reload server cache because page, {0} does not exist", CACHE_PAGE_NAME);
		}
	}

	/**
	 * Method to reload the cache from Page object for current picklist data field
	 * 
	 * @param context:
	 *            eMatrix context
	 * @param strCachedTypeName
	 *            : String cache field name
	 * @throws MatrixException
	 */
	public static JsonObject reloadServerCacheFromPage(Context context, String strCachedTypeName) throws MatrixException {
		JsonObject jsonObjCache = null;
		Page pageCache = new Page(CACHE_PAGE_NAME);
		if (pageCache.exists(context)) {
			pageCache.open(context);
			String strPageModified = Long.toString(pageCache.getModificationTime(context));
			JsonObject jsonCurrentServerCacheData = (JsonObject) CacheUtil.getCacheObject(context, PREFIX_SERVER_CACHE + strCachedTypeName);
			if (jsonCurrentServerCacheData == null || hasCacheExpired(jsonCurrentServerCacheData, strPageModified)) {
				String strPageContent = pageCache.getContents(context);
				JsonObject jsonPageContent = getJsonFromJsonString(strPageContent);
				JsonArray jsonObjectsDataArray = jsonPageContent.getJsonArray(PREFIX_SERVER_CACHE + strCachedTypeName);
				if(jsonObjectsDataArray!=null) {
				JsonObjectBuilder jsonNewServerCacheData = Json.createObjectBuilder();
				jsonNewServerCacheData.add(CACHE_PROPERTY_DATA, jsonObjectsDataArray);
				jsonNewServerCacheData.add(CACHE_PROPERTY_TS, strPageModified);
				jsonObjCache = jsonNewServerCacheData.build();
				CacheUtil.setCacheObject(context, PREFIX_SERVER_CACHE + strCachedTypeName, jsonObjCache);
				}
			}
			pageCache.close(context);
		} else {
			logger.log(Level.WARNING, "Cannot reload server cache because page, {0} does not exist", CACHE_PAGE_NAME);
		}
		return jsonObjCache;
	}

	/**
	 * Method to check whether the cache is valid based on cache time stamp from Page object time stamp
	 * 
	 * @param jsonCacheData
	 *            : JsonObject from cache
	 * @param strPageTS
	 *            : String time stamp from page
	 * @return
	 */
	public static boolean hasCacheExpired(JsonObject jsonCacheData, String strPageTS) {
		String strTSInServerCacheMap = jsonCacheData.getString(CACHE_PROPERTY_TS);
		return (UIUtil.isNullOrEmpty(strTSInServerCacheMap)) || (!strTSInServerCacheMap.equals(strPageTS));
	}

	/**
	 * Method to convert String json to JsonObject
	 * 
	 * @param strJsonString
	 *            : String json
	 * @return : JsonObject created from String json
	 */
	private static JsonObject getJsonFromJsonString(String strJsonString) {
		Map<String, String> configMap = new HashMap<>();
		configMap.put(MAX_STRING_LENGTH, VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);
		try (
				StringReader srJsonString = new StringReader(strJsonString);
				JsonReader jsonReader = factory.createReader(srJsonString)
		) {
			return jsonReader.readObject();
		}
	}

	/**
	 * Method to convert Time in MilliSeconds to Hours, Mins. and MilliSecs.
	 * 
	 * @param lStartTimeInMiliSecs
	 *            Start Time of operation in milliseconds
	 * @param lEndTimeInMiliSecs
	 *            End Time of operation in milliseconds
	 */
	public static void outPutMilisecsInHrsMinsSecsMilisec(long lStartTimeInMiliSecs, long lEndTimeInMiliSecs) {
		long lTime = lEndTimeInMiliSecs - lStartTimeInMiliSecs;
		long lMilliSecs = lTime % 1000;
		lTime = lTime / 1000;
		long lSecs = lTime % 60;
		lTime = lTime / 60;
		long lMins = lTime % 60;
		lTime = lTime / 60;
		long lHours = lTime;
		logger.log(Level.INFO, () -> String.format("Time taken to cache : %d hours %d mins %d secs %d millisecs", lHours, lMins, lSecs, lMilliSecs));
	}
}
