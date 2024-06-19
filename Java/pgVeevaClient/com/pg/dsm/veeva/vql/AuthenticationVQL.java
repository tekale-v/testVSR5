/*
 **   AuthenticationVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Authentication Implementation class
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
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.xml.binder.Query;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;
import com.pg.util.EncryptCrypto;

public class AuthenticationVQL extends VQL {

	Query query;
	Properties properties;
	URI uri;
	String customQueryString;
	public AuthenticationVQL(Configurator configurator) throws Exception {
		this.query = configurator.getVeevaConfigXML().getQueries().get(0).getQuery().get(0);
		this.properties = configurator.getProperties();
		setBaseURI();
		setCustomQueryString();
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
	public org.apache.http.Header[] getHeader() {
		org.apache.http.Header[] headers = {
				new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"),
				new BasicHeader(HttpHeaders.ACCEPT, "application/json")};
		return headers;
	}
	public void setCustomQueryString() throws Exception {
		StringBuilder credentials = new StringBuilder();
		credentials.append(Veeva.USERNAME);
		credentials.append(Veeva.CONST_SYMBOL_EQUAL);
		credentials.append(properties.getProperty("veeva.host.username"));
		credentials.append(Veeva.CONST_SYMBOL_AMPERSAND);
		credentials.append(Veeva.PASSWORD);
		credentials.append(Veeva.CONST_SYMBOL_EQUAL);
		credentials.append(EncryptCrypto.decryptString(properties.getProperty("veeva.host.password")));
		this.customQueryString=credentials.toString();
	}
	@Override
	public String getCustomQueryString() throws FrameworkException, ParseException {
		return this.customQueryString;
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
