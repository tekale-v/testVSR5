/*
 **   UsersEmailVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Users Email Implementation class
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
import org.apache.log4j.Logger;


import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.xml.binder.Query;
import com.pg.dsm.veeva.vql.xml.binder.QueryBuilder;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;
import com.pg.dsm.veeva.vql.xml.binder.bo.BusinessObject;
import com.pg.dsm.veeva.vql.xml.binder.bo.Selectable;

public class UsersEmailVQL extends VQL {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	Query query;
	Properties properties;
	URI uri;
	String customQueryString;
	public UsersEmailVQL(Configurator configurator) throws URISyntaxException, FrameworkException, ParseException {
		this.query = configurator.getVeevaConfigXML().getQueries().get(0).getQuery().get(5);
		this.properties = configurator.getProperties();
		setBaseURI();
		setCustomQueryString();
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
	/** 
	 * @about method to Jaxb Query bean  
	 * @return Query
	 */
	public void setCustomQueryString() throws FrameworkException, ParseException {
		QueryBuilder queryBuilder = query.getQueryBuilder().get(0);
		BusinessObject bo = queryBuilder.getBusinessobject().get(0);
		Selectable selectable = bo.getSelectable().get(0);
		
		StringBuilder builder = new StringBuilder();
		builder.append(queryBuilder.getKey());
		builder.append(queryBuilder.getOperand());
		builder.append(Veeva.CONST_KEYWORD_STRING);
		
		StringBuilder selectStatement = new StringBuilder();
		selectStatement.append(selectable.getSelects());
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		
		List<com.pg.dsm.veeva.vql.xml.binder.bo.Select> selects = selectable.getSelect();
		for(int i=0; i<selects.size(); i++) {
			com.pg.dsm.veeva.vql.xml.binder.bo.Select select = selects.get(i);
			selectStatement.append(select.getKey());	
			selectStatement.append(Veeva.CONST_KEYWORD_COMMA);
		}
		
		if(selectStatement.length()>0) {
			selectStatement.setLength(selectStatement.length()-1);
		}
		
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		selectStatement.append(selectable.getFrom());
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		selectStatement.append(selectable.getType());
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		
		logger.info("Users Email VQL custom query >> "+selectStatement.toString());
		
		this.customQueryString = selectStatement.toString();
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
