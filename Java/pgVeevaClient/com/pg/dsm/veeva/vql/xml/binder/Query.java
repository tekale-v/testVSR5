/*
 **   Query.java
 **   Description - Introduced as part of Veeva integration.      
 **   JAXB bean 
 **
 */
package com.pg.dsm.veeva.vql.xml.binder;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"headers",
		"querybuilder",
		"renditions"
}) 
@XmlRootElement(name = "query")
public class Query {
	
	@XmlAttribute(name = "name")
    protected String name;
	
	@XmlAttribute(name = "api")
    protected String api;
	
	@XmlAttribute(name = "method")
    protected String method;
	
	@XmlAttribute(name = "param")
    protected String param;
	
	@XmlElement(required = true)
	protected List<Renditions> renditions;
	
	public List<Renditions> getRenditions() {
		if (renditions == null) {
			renditions = new ArrayList<Renditions>();
		}
		return renditions;
	}
		
	@XmlElement(required = true)
	protected List<Headers> headers;
	
	public List<Headers> getHeaders() {
		if (headers == null) {
			headers = new ArrayList<Headers>();
		}
		return headers;
	}
	
	@XmlElement(required = true)
	protected List<QueryBuilder> querybuilder;
	
	public List<QueryBuilder> getQueryBuilder() {
		if (querybuilder == null) {
			querybuilder = new ArrayList<QueryBuilder>();
		}
		return querybuilder;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}
	

}
