/*
 **   DocumentDataSetVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Document Data Set Implementation class
 **
 */
package com.pg.dsm.veeva.vql;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.xml.binder.Query;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;

public class DocumentDataSetVQL extends VQL {
	Query query;
	Properties properties;
	URI uri;
	public DocumentDataSetVQL(Configurator configurator) throws URISyntaxException {
		this.query = configurator.getVeevaConfigXML().getQueries().get(0).getQuery().get(4);
		this.properties = configurator.getProperties();
		setBaseURI();
	}
	/** 
	 * @about method to Jaxb Query bean  
	 * @return Query
	 */
	@Override
	public Query getQuery() {
		return this.query;
	}
	@Override
	public URI getBaseURI() throws URISyntaxException {
		return this.uri;
	}
	public void setBaseURI() throws URISyntaxException {
		this.uri = VQLUtil.getBaseURI(properties);
	}
	@Override
	public String getCustomQueryString() throws FrameworkException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}
	public org.apache.http.Header[] getHeader() {
		org.apache.http.Header[] headers = {
				new BasicHeader(HttpHeaders.ACCEPT, "application/json")};
		return headers;
	}
	@Override
	public String getApi() {
		// TODO Auto-generated method stub
		return query.getApi();
	}
	@Override
	public List<Rendition> getConfiguredRenditions() {
		// TODO Auto-generated method stub
		return null;
	}
}
