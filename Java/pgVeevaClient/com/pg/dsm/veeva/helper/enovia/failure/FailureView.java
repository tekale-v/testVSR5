/*
 **   FailureView.java
 **   Description - Introduced as part of Veeva integration.      
 **   Failure View abstract class.
 **
 */
package com.pg.dsm.veeva.helper.enovia.failure;

import matrix.db.Context;

public abstract class FailureView {
	/** 
	 * @about abstract method to get artwork id
	 * @return String
	 * @since DSM 2018x.3
	 */
	public abstract String getArtworkID();
	
	/** 
	 * @about abstract method to get failure message
	 * @return String
	 * @since DSM 2018x.3
	 */
	public abstract String getFailureMessage();
	
	/** 
	 * @about abstract method to log message
	 * @return void
	 * @since DSM 2018x.3
	 */
	public abstract void log();
	
	/** 
	 * @about abstract method to send mail
	 * @return void
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public abstract void sendValidationErrorEmail();
	
	/** 
	 * @about abstract method to get context
	 * @return Context
	 * @since DSM 2018x.3
	 */
	public abstract Context getContext();
	
	/** 
	 * @about abstract method to perform update operation
	 * @return void
	 * @since DSM 2018x.3
	 */
	public abstract void update() throws Exception;
	
	/** 
	 * @about abstract method to send mail
	 * @return void
	 * @throws Exception 
	 * @since DSM 2018x.5
	 */
	public abstract void sendExceptionErrorEmail();
	
}
