/*
 **   DocumentPropertyVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Document Property Implementation class
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
import com.pg.dsm.veeva.vql.xml.binder.conditional.Equation;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Logical;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Where;

public class DocumentPropertyVQL extends VQL {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	Query query;
	Properties properties;
	URI uri;
	String customQueryString;
	public DocumentPropertyVQL(Configurator configurator) throws URISyntaxException, FrameworkException, ParseException {
		this.query = configurator.getVeevaConfigXML().getQueries().get(0).getQuery().get(2);
		this.properties = configurator.getProperties();
		setBaseURI();
		setCustomQueryString();
		logger.info("Document Property VQL Query: "+this.customQueryString);
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

		selectStatement.append(buildRelationshipSelectable());
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		selectStatement.append(selectable.getFrom());
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		selectStatement.append(selectable.getType());

		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		Where where = queryBuilder.getWhere().get(0);
		selectStatement.append(where.getKey());	
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
		Logical logical = where.getLogical().get(0);
		selectStatement.append(buildLogicalEquationQuery(logical.getEquations()));
		this.customQueryString = selectStatement.toString();
	}
	@Override
	public String getCustomQueryString() throws FrameworkException, ParseException {
		return this.customQueryString;
	}
	public List<com.pg.dsm.veeva.vql.xml.binder.rel.Selectable> getRelSelectable() {
		return query.getQueryBuilder().get(0).getRelationships().get(0).getRelationship().get(0).getSelectable();
	}
	public String buildRelationshipSelectable() {
		StringBuilder builder = new StringBuilder();
		com.pg.dsm.veeva.vql.xml.binder.rel.Selectable selectable = getRelSelectable().get(0);

		builder.append(Veeva.CONST_OPEN_BRACKET);
		builder.append(selectable.getSelects());
		builder.append(Veeva.CONST_KEYWORD_SPACE);

		List<com.pg.dsm.veeva.vql.xml.binder.rel.Select> selects = selectable.getSelect();
		for(int i=0; i<selects.size(); i++) {
			com.pg.dsm.veeva.vql.xml.binder.rel.Select select = selects.get(i);
			builder.append(select.getKey());	
			builder.append(Veeva.CONST_KEYWORD_COMMA);
		}
		if( builder.length() > 0 )
			builder.deleteCharAt( builder.length() - 1 );

		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(selectable.getFrom());
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(selectable.getRel());
		builder.append(Veeva.CONST_CLOSED_BRACKET);


		return builder.toString();
	}

	public String buildLogicalEquationQuery(List<Equation> equations) {

		StringBuilder builder = new StringBuilder();
		for(int i=0; i<equations.size(); i++) {
			Equation equal = equations.get(i);
			builder.append(equal.getKey());
			builder.append(Veeva.CONST_KEYWORD_SPACE);
			builder.append(equal.getOperator());
			builder.append(Veeva.CONST_KEYWORD_SPACE);
			builder.append(equal.getValue());
			builder.append(Veeva.CONST_KEYWORD_SPACE);

			String postOperand = equal.getPostoperand();
			if(!"".equals(postOperand)) {
				builder.append(postOperand);
				builder.append(Veeva.CONST_KEYWORD_SPACE);
			} else {
				builder.append(Veeva.CONST_KEYWORD_SPACE);
			}
		}
		if( builder.length() > 0 )
			builder.deleteCharAt( builder.length() - 1 );
		return builder.toString();

	}
	@Override
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
