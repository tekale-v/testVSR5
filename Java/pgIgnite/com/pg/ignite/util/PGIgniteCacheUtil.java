
package com.pg.ignite.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientAddressFinder;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.configuration.ClientTransactionConfiguration;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pg.ignite.ipfinder.PGIgniteIPFinder;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PGIgniteCacheUtil {
	public static final String CLIENT_CACHE_NAME = "PICKLIST";
	public static final String IGNITE_CLIENT_IPADDRS = "127.0.0.1";
	public static final String IGNITE_CLIENT_PORT = "10800";
	private static final String VALUE_KILOBYTES = "262144";
	private static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	private static final Logger logger = LoggerFactory.getLogger(PGIgniteCacheUtil.class.getName());

	public static Object getCacheObject(Context context, String strCacheName) throws ClientException, Exception {
		logger.info("ENTRY: Getting cache from ignite for CacheName = {}", strCacheName);
		try (
				IgniteClient client = Ignition.startClient(getIgniteClientConfig(context));
		) {
			logger.info("IgniteClient started successfully");
			ClientCache<String, String> clientCache = client.getOrCreateCache(CLIENT_CACHE_NAME);
			String strCache = clientCache.get(strCacheName);
			if (strCache == null) {
				logger.info("Ignite-Cache for CacheNam = {} is null", strCacheName);
				logger.info("Cache will be fetched and set into ignite");
			} else {
				logger.info("Ignite-Cache for CacheName = {} found", strCacheName);
			}
			return convertStringToJsonObject(clientCache.get(strCacheName));
		} catch (Exception e) {
			logger.error("Error encountered in starting ignite client", e);
			throw e;
		}
	}

	public static void setCacheObject(Context context, String strKey, String strValue) throws ClientException, Exception {
		logger.info("ENTRY: Setting cache into ignite. key = {}, value = {}", strKey, strValue);
		try (
				IgniteClient client = Ignition.startClient(getIgniteClientConfig(context));
		) {
			logger.info("IgniteClient started successfully");
			ClientCache<String, String> clientCache = client.getOrCreateCache(CLIENT_CACHE_NAME);
			logger.info("PICKLIST ClientCache retreived successfully. Returning ClientCache");
			clientCache.put(strKey, strValue);
		} catch (Exception e) {
			logger.error("Error encountered in starting ignite client", e);
			throw e;
		}
	}
	
	/**
	 * Method to convert String json to JsonObject
	 * 
	 * @param strJsonString
	 *            : String json
	 * @return : JsonObject created from String json
	 */
	public static JsonObject convertStringToJsonObject(String strJsonString) {
		if (strJsonString != null && !"".equals(strJsonString)) {
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
		return null;
	}

	private static String[] convertIntoIgniteNodeAddrsStringArray(List<String> listNodeArrs) {
		ArrayList<String> arrayListNodeAddrPort = new ArrayList<>();
		arrayListNodeAddrPort.add(new StringBuilder(IGNITE_CLIENT_IPADDRS).append(":").append(IGNITE_CLIENT_PORT).toString());
		if (listNodeArrs != null) {
			listNodeArrs.forEach(s -> {
				arrayListNodeAddrPort.add(new StringBuilder(s).append(":").append(IGNITE_CLIENT_PORT).toString());
			});
		}
		return arrayListNodeAddrPort.toArray(new String[arrayListNodeAddrPort.size()]);
	}
	
	private static ClientAddressFinder getIgniteClientAddrsFinder(Context context) {
		return () -> {
			try {
				return convertIntoIgniteNodeAddrsStringArray(PGIgniteIPFinder.getCurrentIgniteNodeAddrsFromDatabase(context));
			} catch (MatrixException e) {
				return convertIntoIgniteNodeAddrsStringArray(null);
			}
		};
	}
	
	private static ClientConfiguration getIgniteClientConfig(Context context) {
		return new ClientConfiguration().setAddressesFinder(getIgniteClientAddrsFinder(context)).setPartitionAwarenessEnabled(true)
				.setTransactionConfiguration(new ClientTransactionConfiguration().setDefaultTxTimeout(10000).setDefaultTxConcurrency(TransactionConcurrency.OPTIMISTIC)
						.setDefaultTxIsolation(TransactionIsolation.REPEATABLE_READ));
	}
}
