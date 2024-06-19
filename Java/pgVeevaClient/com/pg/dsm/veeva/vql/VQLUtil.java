/*
 **   VQLUtil.java
 **   Description - Introduced as part of Veeva integration.      
 **   VQL Util class
 **
 */
package com.pg.dsm.veeva.vql;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.utils.URIBuilder;

import com.pg.dsm.veeva.util.Veeva;

public class VQLUtil {

	/** 
	 * @about To format URI
	 * @param Properties - properties object   
	 * @return URI
	 * @throws URISyntaxException
	 * @since DSM 2018x.3
	 */
	public static URI getBaseURI(Properties properties) throws URISyntaxException {
		URI uri = new URIBuilder()
				.setScheme(properties.getProperty("veeva.host.scheme"))
				.setHost(properties.getProperty("veeva.host.uri"))
				.setPath(properties.getProperty("veeva.rest.api.version"))
				.build();
		return uri;
	}
	/** 
	 * @about To format URI
	 * @param String - queryString  
	 * @param List - user list
	 * @return String
	 * @since DSM 2018x.3
	 */
	public static String getUserQuery(String queryString, List<Long> users) {
		StringBuilder queryBuilder = new StringBuilder(queryString);
		Iterator<?> itr = users.iterator();
		while(itr.hasNext()) {
			Long user = (Long)itr.next();
			queryBuilder.append("WHERE id = "+String.valueOf(user));
			queryBuilder.append(Veeva.CONST_KEYWORD_SPACE);
			queryBuilder.append("OR");
		}
		if(queryBuilder.length()>0) {
			queryBuilder.setLength(queryBuilder.length()-3);
		}
		return queryBuilder.toString();
	}
}
