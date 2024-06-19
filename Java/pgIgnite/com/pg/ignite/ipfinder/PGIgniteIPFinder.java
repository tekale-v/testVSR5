
package com.pg.ignite.ipfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.ignite.util.PGIgniteCacheUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;

public class PGIgniteIPFinder {
	private static final Logger logger = LoggerFactory.getLogger(PGIgniteIPFinder.class.getName());
	private static final String OBJECT_NAME_IGNITE_ADDRS = "pgIgniteAddrs";
	private static final String PAGE_DESCRIPTION = "Page object to store Ignite Server Node IP Addresses";
	private static final String PASSPORT_URL = System.getenv("PASSPORT_URL");
	private static final String TYPE_PGCACHEMANAGER = PropertyUtil.getSchemaProperty(null, "type_pgCacheManager");
	private static final String POLICY_PGAPPMANAGERPOLICY = PropertyUtil.getSchemaProperty(null, "policy_pgAppManagerPolicy");
	private static final String VAULT_ESERVICEPRODUCTION = PropertyUtil.getSchemaProperty(null, "vault_eServiceProduction");

	public static void addIgniteNodeAddrsInDatabase(Context context, Collection<String> colIpAddrs) throws MatrixException {
		logger.info("ENTRY");
		long lCachingStartTime = System.currentTimeMillis();
		updateIgniteNodeAddrsInDatabase(context, colIpAddrs);
		long lCachingEndTime = System.currentTimeMillis();
		outPutMilisecsInHrsMinsSecsMilisec(lCachingStartTime, lCachingEndTime);
		logger.info("EXIT");
	}

	//
	public static List<String> getCurrentIgniteNodeAddrsFromDatabase(Context context) throws MatrixException {
		List<String> listExistingIgniteIPAddrs = null;
		JsonObject jsonObjUrlToIgniteNodeIPMapping = PGIgniteCacheUtil.convertStringToJsonObject(getUrlToIgniteNodeIPMappingFromDatabase(context));
		if (jsonObjUrlToIgniteNodeIPMapping != null) {
			logger.info("jsonObj = {}", jsonObjUrlToIgniteNodeIPMapping);
			JsonArray jsonArrCurrentNodeIPAddrs = jsonObjUrlToIgniteNodeIPMapping.getJsonArray(PASSPORT_URL);
			listExistingIgniteIPAddrs = convertJsonArrayToCollectionOfStrings(jsonArrCurrentNodeIPAddrs);
		}
		if (listExistingIgniteIPAddrs == null || listExistingIgniteIPAddrs.isEmpty()) {
			listExistingIgniteIPAddrs = new ArrayList<String>(1);
			listExistingIgniteIPAddrs.add("127.0.0.1");
		}
		logger.info("Ignite IP Addrs From Database -- listExistingIgniteIPAddrs = {}", listExistingIgniteIPAddrs);
		return listExistingIgniteIPAddrs;
	}
	//

	/**
	 * { "https://abcdef.ghi.jkl/enovia" : ["192.168.0.2", "127.0.0.1", "10.201.71.171"], "https://mnopqr.stu.vwx/3dspace" : ["192.168.0.3",
	 * "127.0.0.1", "10.201.71.172"] }
	 * {"https://LP5-K6V-DSA.dsone.3ds.com/3dspace":["0:0:0:0:0:0:0:1","127.0.0.1","192.168.115.2","2401:4900:1c17:8d2a:7049:c306:8128:7f97","2401:4900:1c17:8d2a:805c:f7ce:e335:bb37"]}
	 * 
	 * @param context
	 * @param colIPAddrs
	 * @throws MatrixException
	 * @throws IOException
	 */
	private static void updateIgniteNodeAddrsInDatabase(Context context, Collection<String> colIPAddrs) throws MatrixException {
		setUrlToIgniteNodeIPMappingInDatabase(context, createUrlToIgniteNodeIPMappingWithNewIpAddrs(getUrlToIgniteNodeIPMappingFromDatabase(context), colIPAddrs));
	}

	private static String createUrlToIgniteNodeIPMappingWithNewIpAddrs(String strExistingUrlToIgniteNodeIPMapping, Collection<String> collNewIgniteNodeIPAddrs) {
		// Using set to create a unique list of IP Addresses
		TreeSet<String> setIpAddrs = new TreeSet<>(collNewIgniteNodeIPAddrs);
		JsonObject jsonObjExistingUrlToIgniteNodeIPMapping = PGIgniteCacheUtil.convertStringToJsonObject(strExistingUrlToIgniteNodeIPMapping);
		// Add json content into a new JsonObj for editing
		JsonObjectBuilder newJsonObjBuilder = Json.createObjectBuilder();
		
		if (jsonObjExistingUrlToIgniteNodeIPMapping != null) {
			newJsonObjBuilder = cloneJsonObjectIntoJsonObjectBuilder(jsonObjExistingUrlToIgniteNodeIPMapping);
			JsonArray jsonArr = jsonObjExistingUrlToIgniteNodeIPMapping.getJsonArray(PASSPORT_URL);
			setIpAddrs.addAll(convertJsonArrayToCollectionOfStrings(jsonArr));
		}
		
		JsonArrayBuilder newJab = Json.createArrayBuilder();
		setIpAddrs.forEach(newJab::add);
		newJsonObjBuilder.add(PASSPORT_URL, newJab);
		String strUpdatedUrlToIgniteNodeIPMapping = newJsonObjBuilder.build().toString();
		logger.info("Updated Url To Ignite-Node-IP-Mapping = {}", strUpdatedUrlToIgniteNodeIPMapping);
		return strUpdatedUrlToIgniteNodeIPMapping;
	}

	private static JsonObjectBuilder cloneJsonObjectIntoJsonObjectBuilder(JsonObject jsonObj) {
		JsonObjectBuilder newJsonObjBuilder = Json.createObjectBuilder();
		jsonObj.entrySet().forEach(s -> newJsonObjBuilder.add(s.getKey(), s.getValue()));
		return newJsonObjBuilder;
	}

	private static List<String> convertJsonArrayToCollectionOfStrings(JsonArray jsonArr) {
		List<String> listOfString = new ArrayList<>(1);
		if (jsonArr != null) {
			int nSize = jsonArr.size();
			if (nSize > 0) {
				listOfString = new ArrayList<>(nSize);
				for (JsonString contact : jsonArr.getValuesAs(JsonString.class)) {
					listOfString.add(contact.getString());
				}
			}
		}
		return listOfString;
	}

	// Call new method from the following method to get info from bus obj or page
	private static void setUrlToIgniteNodeIPMappingInDatabase(Context context, String strContent) throws MatrixException {
		// setUrlToIgniteNodeIPMappingInPage(context, strContent);
		setUrlToIgniteNodeIPMappingInBusObj(context, strContent);
	}

	// Call new method from the following method to get info from bus obj or page
	private static String getUrlToIgniteNodeIPMappingFromDatabase(Context context) throws MatrixException {
		// return getUrlToIgniteNodeIPMappingFromPage(context);
		return getUrlToIgniteNodeIPMappingFromBusObj(context);
	}

	// 3dspace Page Object specific code STARTS
	@SuppressWarnings("unused")
	private static void setUrlToIgniteNodeIPMappingInPage(Context context, String strContent) throws MatrixException {
		Page page = getPageObjectForIgnite(context);
		if (!"".equals(strContent)) {
			page.open(context);
			page.setContents(context, strContent);
			page.update(context);
			logger.info("PAGE OBJECT, {} SUCCESSFULLY UPDATED WITH NEW DATA", OBJECT_NAME_IGNITE_ADDRS);
		} else {
			logger.info("PAGE OBJECT {} NOT MODIFIED. IT ALREADY HAS LATEST DATA", OBJECT_NAME_IGNITE_ADDRS);
		}
		page.close(context);
	}

	@SuppressWarnings("unused")
	private static String getUrlToIgniteNodeIPMappingFromPage(Context context) throws MatrixException {
		Page page = getPageObjectForIgnite(context);
		page.open(context);
		String strUrlToIgniteNodeIPMapping = page.getContents(context);
		page.close(context);
		return strUrlToIgniteNodeIPMapping;
	}

	private static Page getPageObjectForIgnite(Context context) throws MatrixException {
		Page page = new Page(OBJECT_NAME_IGNITE_ADDRS);
		// TODO page.exist can be put in a variable to avoid making this db-call repeatedly
		if (!page.exists(context)) {
			Page.create(context, OBJECT_NAME_IGNITE_ADDRS, PAGE_DESCRIPTION, "", "");
			logger.info("EMPTY PAGE OBJECT {} CREATED AND UPDATED SUCCESSFULLY", OBJECT_NAME_IGNITE_ADDRS);
		}
		return page;
	}
	// 3dspace Page Object specific code ENDS

	// 3dspace BusinessObject specific code STARTS
	private static void setUrlToIgniteNodeIPMappingInBusObj(Context context, String strContent) throws MatrixException {
		try {
			DomainObject domObjIgniteAddrs = getBusObjForIgnite(context);
			if (!"".equals(strContent)) {
				domObjIgniteAddrs.setAttributeValue(context, DomainConstants.ATTRIBUTE_TITLE, strContent);
				logger.info("BUSINESS OBJECT, {} {} SUCCESSFULLY UPDATED WITH NEW DATA", TYPE_PGCACHEMANAGER, OBJECT_NAME_IGNITE_ADDRS);
			} else {
				logger.info("BUSINESS OBJECT {} {} NOT MODIFIED. IT ALREADY HAS LATEST DATA", TYPE_PGCACHEMANAGER, OBJECT_NAME_IGNITE_ADDRS);
			}
		} catch (Exception e) {
			logger.error("Error while setting Ignite Node Addrs in Business Object", e);
			throw e;
		}
	}

	private static String getUrlToIgniteNodeIPMappingFromBusObj(Context context) throws MatrixException {
		logger.info("ENTRY");
		String strUrlToIgniteNodeIPMapping = "";
		try {
			DomainObject domObjIgniteAddrs = getBusObjForIgnite(context);
			if (domObjIgniteAddrs != null) {
				strUrlToIgniteNodeIPMapping = domObjIgniteAddrs.getInfo(context, DomainConstants.SELECT_ATTRIBUTE_TITLE);
			}
		} catch (Exception e) {
			logger.error("Error while getting Url To Ignite Node IP Mapping from Business Object", e);
			throw e;
		}
		logger.info("EXIT");
		return strUrlToIgniteNodeIPMapping;
	}

	private static DomainObject getBusObjForIgnite(Context context) throws MatrixException {
		logger.info("ENTRY");
		BusinessObject busObj = new BusinessObject(TYPE_PGCACHEMANAGER, OBJECT_NAME_IGNITE_ADDRS, "", VAULT_ESERVICEPRODUCTION);
		DomainObject domObjIgniteAddrs = null;
		try {
			if (busObj.exists(context)) {
				logger.info("Bus object exists : {} with id {}", OBJECT_NAME_IGNITE_ADDRS, busObj.getObjectId(context));
				domObjIgniteAddrs = DomainObject.newInstance(context, busObj.getObjectId(context));
			} else {
				logger.info("Bus object does not exist : {}", OBJECT_NAME_IGNITE_ADDRS);
				domObjIgniteAddrs = DomainObject.newInstance(context);
				domObjIgniteAddrs.createObject(context, TYPE_PGCACHEMANAGER, OBJECT_NAME_IGNITE_ADDRS, "", POLICY_PGAPPMANAGERPOLICY, VAULT_ESERVICEPRODUCTION);
				logger.info("EMPTY BUS OBJECT {} CREATED AND UPDATED SUCCESSFULLY", OBJECT_NAME_IGNITE_ADDRS);
			}
		} catch (Exception e) {
			logger.error("Error while getting or creating Business Object for Ignite", e);
			throw e;
		}
		logger.info("EXIT");
		return domObjIgniteAddrs;
	}
	// 3dspace BusinessObject specific code ENDS
	
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
		logger.info(String.format("Time taken to cache : %d hours %d mins %d secs %d millisecs", lHours, lMins, lSecs, lMilliSecs));
	}
}
