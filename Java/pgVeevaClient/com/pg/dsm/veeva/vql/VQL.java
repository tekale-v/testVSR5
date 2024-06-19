/*
 **   VQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   VQL Abstract class
 **
 */
package com.pg.dsm.veeva.vql;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.vql.xml.binder.Query;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;

public abstract class VQL {
	/** 
	 * @about method to Jaxb Query bean  
	 * @return Query
	 */
	public abstract Query getQuery();
	/** 
	 * @about method to URI  
	 * @return URI
	 */
	public abstract URI getBaseURI() throws URISyntaxException;
	/** 
	 * @about method to build query string  
	 * @return String
	 */
	public abstract String getCustomQueryString() throws FrameworkException, ParseException;
	/** 
	 * @about method to build query header  
	 * @return String
	 */
	public abstract org.apache.http.Header[] getHeader();
	/** 
	 * @about method to get api
	 * @return String
	 */
	public abstract String getApi();
	/** 
	 * @about method to get list of configured renditions
	 * @return List
	 */
	public abstract List<Rendition> getConfiguredRenditions();
		
}
