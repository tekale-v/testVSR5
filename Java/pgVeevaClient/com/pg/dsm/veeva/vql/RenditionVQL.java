/*
 **   RenditionVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Rendition Implementation class
 **
 */
package com.pg.dsm.veeva.vql;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.vql.xml.binder.Query;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;

public class RenditionVQL extends VQL {
	Query query;
	Properties properties;
	URI uri;
	public RenditionVQL(Configurator configurator) throws URISyntaxException {
		this.query = configurator.getVeevaConfigXML().getQueries().get(0).getQuery().get(3);
		this.properties = configurator.getProperties();
		setBaseURI();
	}
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
	public org.apache.http.Header[] getHeader() {
		org.apache.http.Header[] headers = {
				new BasicHeader(HttpHeaders.ACCEPT, "application/json")};
		return headers;
	}
	@Override
	public String getCustomQueryString() throws FrameworkException, ParseException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getApi() {
		// TODO Auto-generated method stub
		return query.getApi();
	}
	public List<Rendition> getConfiguredRenditions() {
		List<Rendition> configuredRenditions = new ArrayList<Rendition>();
		List<Rendition> renditions = query.getRenditions().get(0).getRendition();
		for(int i=0; i<renditions.size(); i++) {
			Rendition rendition = renditions.get(i);
			String value = rendition.getValue();
			if("y".equals(value)) {
				configuredRenditions.add(rendition);
			}
		}
		//https://sb-pg-promomats.veevavault.com/api/v14.0/objects/documents/#ID/renditions/viewable_rendition__v
		return configuredRenditions;
	}
}
