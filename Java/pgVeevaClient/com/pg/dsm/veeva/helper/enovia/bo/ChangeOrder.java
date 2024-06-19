/*
 **   ChangeOrder.java
 **   Description - Introduced as part of Veeva integration.      
 **   ChangeOrder builder class.
 **
 */
package com.pg.dsm.veeva.helper.enovia.bo;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.helper.enovia.GCASValidator;
import com.pg.dsm.veeva.util.Utility;

import matrix.db.Context;

public class ChangeOrder {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	boolean isCreated;
	String changeOrderID;
	String artworkID;
	Context context;
	Properties properties;
	DomainObject changeOrderObj;
	
	/** 
	 * @about Constructor
	 * @param Create - builder class
	 * @since DSM 2018x.3
	 */
	private ChangeOrder(Create create) {
		this.changeOrderID = create.changeOrderID;
		this.artworkID = create.artworkID;
		this.context = create.context;
		this.properties = create.properties;
		this.changeOrderObj = create.changeOrderObj;
		this.isCreated = create.isCreated;
		
	}
	/** 
	 * @about getter method 
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getChangeOrderID() {
		return changeOrderID;
	}
	/** 
	 * @about getter method 
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkID() {
		return artworkID;
	}
	/** 
	 * @about getter method 
	 * @return Context
	 * @since DSM 2018x.3
	 */
	public Context getContext() {
		return context;
	}
	/** 
	 * @about getter method 
	 * @return Properties
	 * @since DSM 2018x.3
	 */
	public Properties getProperties() {
		return properties;
	}
	/** 
	 * @about getter method 
	 * @return DomainObject
	 * @since DSM 2018x.3
	 */
	public DomainObject getChangeOrderObj() {
		return changeOrderObj;
	}
	/** 
	 * @about getter method 
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isCreated() {
		return isCreated;
	}
	
	/** 
	 * @about method to promote
	 * @param ArtworkInfo - path
	 * @throws FrameworkException
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setCOandCAState(GCASValidator gcasValidator) throws FrameworkException {
		//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
		String strCAId = gcasValidator.getChangeActionId();
		String strCOId = gcasValidator.getChangeOrderId();
		//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
		if(Utility.isNotNullEmpty(strCAId) && Utility.isNotNullEmpty(strCOId)){							
			String command = "mod bus $1 current $2";
         	MqlUtil.mqlCommand(context, command,strCOId,Veeva.STATE_IMPLEMENTED);
         	logger.info("Change Order moved to Implemented state");
         	
			MqlUtil.mqlCommand(context, command,strCAId,Veeva.STATE_COMPLETE);
			logger.info("Change Action moved to complete state");
		}
	}
	public static class Create {
		Context context;
		Properties properties;
		String artworkID;
		String changeOrderID;
		DomainObject changeOrderObj;
		boolean isCreated;
		boolean isExist;
		/** 
		 * @about Constructor
		 * @param Context - context
		 * @param Properties - properties
		 * @param String - artwork id
		 * @since DSM 2018x.3
		 */
		public Create(Context context, Properties properties, String artworkID) {
			this.context = context;
			this.properties = properties;
			this.artworkID = artworkID;
			this.isCreated = false;
		}
		/** 
		 * @about Constructor Overloaded
		 * @param Context - context
		 * @param String - artwork id
		 * @param String - co id
		 * @since DSM 2018x.3
		 */
		public Create(Context context, String artworkID, String changeOrderID) {
			this.context = context;
			this.artworkID = artworkID;
			this.changeOrderID = changeOrderID;
		}
		/** 
		 * @about Builder method
		 * @return ChangeOrder
		 * @since DSM 2018x.3
		 */
		public ChangeOrder perform() throws Exception {
			try {
				this.changeOrderObj = new ChangeOrderImpl().createChangeOrder(context, properties);
				this.changeOrderID = changeOrderObj.getInfo(context, Veeva.SELECT_ID);
				this.isCreated = true;
			}
			catch(Exception e) {
				e.printStackTrace();
				this.isCreated = false;
				throw e;
			}
			return new ChangeOrder(this);
		}

	}  

}
