/*
 **   Response.java
 **   Description - Introduced as part of Veeva integration.      
 **   Abstract class
 **
 */
package com.pg.dsm.veeva.vql.json;

public abstract class Response {
	/** 
	 * @about method to get json string 
	 * @return String
	 */
	public abstract String getjString();
	/** 
	 * @about method to get json response status 
	 * @return String
	 */
	public abstract String getResponseStatus();
	/** 
	 * @about method to get json response type 
	 * @return String
	 */
	public abstract String getType();
	/** 
	 * @about method to get json response message 
	 * @return String
	 */
	public abstract String getMessage();
}
