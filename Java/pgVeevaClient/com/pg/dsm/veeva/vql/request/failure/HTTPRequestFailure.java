/*
 **   HTTPRequestFailure.java
 **   Description - Introduced as part of Veeva integration.      
 **   Abstract class
 **
 */
package com.pg.dsm.veeva.vql.request.failure;

import com.pg.dsm.veeva.vql.json.Response;

public abstract class HTTPRequestFailure {
	
	/** 
	 * @about method for operation name
	 * @return String
	 */
	public abstract String getOperation();
	/** 
	 * @about method for Response bean
	 * @return Response
	 */
	public abstract Response getResponse();
	/** 
	 * @about method for perform update
	 * @throws Exception
	 * @return Response
	 */
	public abstract void update() throws Exception;
	/** 
	 * @about method to get exception
	 * @return Exception
	 */
	public abstract Exception getException();
	/** 
	 * @about method to log
	 * @return void
	 */
	public abstract void log();
	/** 
	 * @about method to send email
	 * @return void
	 */
	public abstract void sendEmail();
}
