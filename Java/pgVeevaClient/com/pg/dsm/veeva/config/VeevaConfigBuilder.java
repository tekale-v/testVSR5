/*
 **   VeevaConfigBuilder.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to load Veeva Config object bean for Veeva extraction.
 **
 */
package com.pg.dsm.veeva.config;


import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

public class VeevaConfigBuilder implements Veeva {
	DomainObject busObj;
	String objectId;
	Context context;
	boolean isLoaded;
	
	/** 
	 * @about Constructor
	 * @param Load - (builder class)
	 * @since DSM 2018x.3
	 */
	private VeevaConfigBuilder(Load load) {
		this.context = load.context;
		this.isLoaded = load.isLoaded;
		this.busObj = load.busObj;
	}
	/** 
	 * @about Getter method - to config object
	 * @return DomainObject
	 * @since DSM 2018x.3
	 */
	public DomainObject getObj() throws FrameworkException {
		return busObj;
	}
	/** 
	 * @about Method to check loader bean
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	public static class Load {
		
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		
		String objectId;
		Context context;
		boolean isLoaded;
		DomainObject busObj;
		
		/** 
		 * @about Constructor
		 * @param Context 
		 * @since DSM 2018x.3
		 */
		public Load(Context context) {
			this.context = context;
			this.isLoaded = false;
		}
		/** 
		 * @about Builder method
		 * @return VeevaConfigBuilder
		 * @throws MatrixException
		 * @since DSM 2018x.3
		 */
		public VeevaConfigBuilder load() throws MatrixException {
			try {
				BusinessObject configBus = new BusinessObject(TYPE_PGCONFIGURATIONADMIN, MATRIX_VEEVA_CONFIG_OBJECT_NAME, SYMBOL_HYPHEN, VAULT_ESERVICEPRODUCTION);
				objectId = configBus.getObjectId(context);
				if(Utility.isNotNullEmpty(objectId)) {
					this.isLoaded = true;
					busObj = DomainObject.newInstance(context, objectId);
				}
			} catch(FrameworkException e) {
				this.isLoaded = false;
				logger.error("************FAILED >>> Unable to find Matrix-Veeva config business object "+e);
			}
			return new VeevaConfigBuilder(this);
		}
	}
}
