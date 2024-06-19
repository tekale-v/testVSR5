/*
 **   VeevaVQL.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to load Veeva VQL Queries object bean for Veeva extraction.
 **
 */
package com.pg.dsm.veeva.config;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.VQL;
import com.pg.dsm.veeva.vql.factory.AuthenticationVQLFactory;
import com.pg.dsm.veeva.vql.factory.DocumentDataSetVQLFactory;
import com.pg.dsm.veeva.vql.factory.DocumentPropertyVQLFactory;
import com.pg.dsm.veeva.vql.factory.DocumentsVQLFactory;
import com.pg.dsm.veeva.vql.factory.RenditionVQLFactory;
import com.pg.dsm.veeva.vql.factory.UsersEmailVQLFactory;
import com.pg.dsm.veeva.vql.factory.VQLFactory;

public class VeevaVQL {

	List<String> errorMessages = new ArrayList<String>();
	boolean isLoaded;
	private VQL authenticationVQL; 
	private VQL documentsVQL;
	private VQL documentDataSetVQL;
	private VQL usersEmailVQL;
	private VQL documentDataPropertyVQL;
	private VQL renditionVQL;

	/** 
	 * @about Constructor
	 * @param Builder - builder class
	 * @since DSM 2018x.3
	 */
	private VeevaVQL(Builder builder) throws Exception {
		this.authenticationVQL = builder.authenticationVQL;
		this.documentsVQL = builder.documentsVQL;
		this.documentDataSetVQL = builder.documentDataSetVQL;
		this.usersEmailVQL = builder.usersEmailVQL;
		this.documentDataPropertyVQL = builder.documentDataPropertyVQL;
		this.renditionVQL = builder.renditionVQL;
		this.errorMessages = builder.errorMessages;
		this.isLoaded = builder.isLoaded;
	}
	/** 
	 * @about Getter method to get authentication vql query
	 * @return VQL - vql object
	 * @since DSM 2018x.3
	 */
	public VQL getAuthenticationVQL() {
		return authenticationVQL;
	}
	/** 
	 * @about Getter method to get documents vql query
	 * @return VQL - vql object
	 * @since DSM 2018x.3
	 */
	public VQL getDocumentsVQL() {
		return documentsVQL;
	}
	/** 
	 * @about Getter method to get document data set vql query
	 * @return VQL - vql object
	 * @since DSM 2018x.3
	 */
	public VQL getDocumentDataSetVQL() {
		return documentDataSetVQL;
	}
	/** 
	 * @about Getter method to get users email vql query
	 * @return VQL - vql object
	 * @since DSM 2018x.3
	 */
	public VQL getUsersEmailVQL() {
		return usersEmailVQL;
	}
	/** 
	 * @about Getter method to get document property vql query
	 * @return VQL - vql object
	 * @since DSM 2018x.3
	 */
	public VQL getDocumentDataPropertyVQL() {
		return documentDataPropertyVQL;
	}
	/** 
	 * @about Getter method to get rendition vql query
	 * @return VQL - vql object
	 * @since DSM 2018x.3
	 */
	public VQL getRenditionVQL() {
		return renditionVQL;
	}
	/** 
	 * @about Getter method to list of messages
	 * @return List 
	 * @since DSM 2018x.3
	 */
	public List<String> getErrorMessages() {
		return errorMessages;
	}
	/** 
	 * @about Method to check loader bean
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	public static class Builder {

		List<String> errorMessages = new ArrayList<String>();
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		private VQL authenticationVQL; 
		private VQL documentsVQL;
		private VQL documentDataSetVQL;
		private VQL usersEmailVQL;
		private VQL documentDataPropertyVQL;
		private VQL renditionVQL;

		Configurator configurator;
		boolean isLoaded;
		
		/** 
		 * @about Constructor
		 * @param Configurator
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder(Configurator configurator) {
			this.configurator = configurator;
			this.isLoaded = false;
		}
		/** 
		 * @about Setter method - to set authentication VQL bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setAuthenticationVQL() throws Exception {
			try {
				this.authenticationVQL = VQLFactory.getVQL(new AuthenticationVQLFactory(configurator));
				this.isLoaded = true;
			} catch(Exception e) {
				logger.error(Veeva.VQL_QUERY_ERROR_AUTHENTICATION);
				errorMessages.add(Veeva.VQL_QUERY_ERROR_AUTHENTICATION);
				this.isLoaded = false;
			}
			return this;
		}
		/** 
		 * @about Setter method - to set documents VQL bean
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder setDocumentsVQL() throws Exception {
			if(this.isLoaded) {
					try {
						this.documentsVQL = VQLFactory.getVQL(new DocumentsVQLFactory(configurator));
					} catch(Exception e) {
						logger.error(Veeva.VQL_QUERY_ERROR_DOCUMENTS);
						errorMessages.add(Veeva.VQL_QUERY_ERROR_DOCUMENTS);
					}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set document dataset VQL bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setDocumentDataSetVQL() throws Exception {
			if(this.isLoaded) {
				try {
					this.documentDataSetVQL= VQLFactory.getVQL(new DocumentDataSetVQLFactory(configurator));
				} catch(Exception e) {
					logger.error(Veeva.VQL_QUERY_ERROR_DOCUMENT_DATASET);
					errorMessages.add(Veeva.VQL_QUERY_ERROR_DOCUMENT_DATASET);
				}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set user email VQL bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setUsersEmailVQL() throws Exception {
			if(this.isLoaded) {
				try {
					this.usersEmailVQL = VQLFactory.getVQL(new UsersEmailVQLFactory(configurator));
				} catch(Exception e) {
					logger.error(Veeva.VQL_QUERY_ERROR_USERS_EMAIL);
					errorMessages.add(Veeva.VQL_QUERY_ERROR_USERS_EMAIL);
				}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set document property VQL bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setDocumentDataPropertyVQL() throws Exception {
			if(this.isLoaded) {
				try {
					this.documentDataPropertyVQL = VQLFactory.getVQL(new DocumentPropertyVQLFactory(configurator));
				} catch(Exception e) {
					logger.error(Veeva.VQL_QUERY_ERROR_DOCUMENT_PROPERTY);
					errorMessages.add(Veeva.VQL_QUERY_ERROR_DOCUMENT_PROPERTY);
				}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set rendition VQL bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setRenditionVQL() throws Exception {
			if(this.isLoaded) {
				try {
					this.renditionVQL= VQLFactory.getVQL(new RenditionVQLFactory(configurator));
				} catch(Exception e) {
					logger.error(Veeva.VQL_QUERY_ERROR_RENDITION);
					errorMessages.add(Veeva.VQL_QUERY_ERROR_RENDITION);
				}
			}
			return this;
		}
		/** 
		 * @about Builder method
		 * @return VeevaVQL
		 * @since DSM 2018x.3
		 */
		public VeevaVQL build() throws Exception {
			return new VeevaVQL(this);
		}
	}
}
