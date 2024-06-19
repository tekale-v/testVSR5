/*
 **   DocumentsVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Documents Implementation class
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
import com.pg.dsm.veeva.config.VeevaConfig;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.xml.binder.Query;
import com.pg.dsm.veeva.vql.xml.binder.QueryBuilder;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;
import com.pg.dsm.veeva.vql.xml.binder.bo.BusinessObject;
import com.pg.dsm.veeva.vql.xml.binder.bo.Selectable;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Between;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Equation;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Historical;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Logical;
import com.pg.dsm.veeva.vql.xml.binder.conditional.Where;

import matrix.db.Context;

public class DocumentsVQL extends VQL {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	Query query;
	Properties properties;
	VeevaConfig veevaConfig;
	Context context;
	String customQueryString;
	public DocumentsVQL(Configurator configurator) throws FrameworkException, ParseException {
		this.query = configurator.getVeevaConfigXML().getQueries().get(0).getQuery().get(1);
		this.properties = configurator.getProperties();
		this.veevaConfig = configurator.getVeevaConfig();
		this.context = configurator.getContext();
		setCustomQueryString();
		logger.info("Documents VQL Query: "+this.customQueryString);
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
		return VQLUtil.getBaseURI(properties);
	}
	@Override
	public org.apache.http.Header[] getHeader() {
		org.apache.http.Header[] headers = {
				new BasicHeader(HttpHeaders.ACCEPT, "application/json")};
		return headers;
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
		return builder.toString();

	}
	public String buildHistoricalBetweenQuery(List<Between> betweens) throws FrameworkException {

		StringBuilder builder = new StringBuilder();
		for(int i=0; i<betweens.size(); i++) {
			Between between = betweens.get(i);
			builder.append(between.getKey());
			builder.append(Veeva.CONST_KEYWORD_SPACE);
			builder.append(between.getOperator());
			builder.append(Veeva.CONST_KEYWORD_SPACE);

			builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
			builder.append(veevaConfig.getFromApprovedForDistributionDate());
			builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
			
			builder.append(Veeva.CONST_KEYWORD_SPACE);
			builder.append(between.getOperand());
			builder.append(Veeva.CONST_KEYWORD_SPACE);

			builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
			builder.append(veevaConfig.getToApprovedForDistributionDate());
			builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
			

			String postOperand = between.getPostoperand();
			if(!"".equals(postOperand)) {
				builder.append(postOperand);
				builder.append(Veeva.CONST_KEYWORD_SPACE);
			} else {
				builder.append(Veeva.CONST_KEYWORD_SPACE);
			}
		}
		return builder.toString();
	}
	@Override
	public String getCustomQueryString() throws FrameworkException, ParseException {
		return this.customQueryString;
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

		List<com.pg.dsm.veeva.vql.xml.binder.bo.Select> selects = selectable.getSelect();
		for(int i=0; i<selects.size(); i++) {
			com.pg.dsm.veeva.vql.xml.binder.bo.Select select = selects.get(i);
			selectStatement.append(Veeva.CONST_KEYWORD_SPACE);
			selectStatement.append(select.getKey());
			selectStatement.append(Veeva.CONST_KEYWORD_COMMA);
		}

		if(selectStatement.length() > 0 )
			selectStatement.deleteCharAt(selectStatement.length() - 1);

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
		selectStatement.append(Veeva.CONST_KEYWORD_SPACE);


		if(excludeBetweenClause(logical)) {
			if(selectStatement.length()>0) {
				selectStatement.setLength(selectStatement.length()-5);
			}
		} else {
			Historical historical = where.getHistorical().get(0);
			selectStatement.append(buildHistoricalBetweenQuery(historical.getBetweens().get(0).getBetweens()));
		}
		String buildGreaterLesserThanQuery = buildGreaterThanQuery();
		
		selectStatement.append(buildGreaterLesserThanQuery);
		System.out.println(selectStatement.toString());
				
		this.customQueryString = selectStatement.toString();
	}
	public String buildGreaterThanQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append(Veeva.SYMBOL_VEEVA_VQL_AND);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.VEEVA_LAST_MODIFIED_DATE_KEY);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.SYMBOL_GREATHEN_THAN);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
		builder.append(veevaConfig.getVeevaFormatStartDate());
		builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.SYMBOL_VEEVA_VQL_AND);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.VEEVA_LAST_MODIFIED_DATE_KEY);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.SYMBOL_LESSER_THAN_AND_EQUAL);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
		builder.append(veevaConfig.getVeevaFormatEnDate());
		builder.append(Veeva.CONST_SYMBOL_SINGLE_QUOTE);
		builder.append(Veeva.CONST_KEYWORD_SPACE);
		return builder.toString();
	}
	public boolean excludeBetweenClause(Logical logical) {
		return properties.getProperty("veeva.rest.documents.query.exclude.between.clause").equals("yes");
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
