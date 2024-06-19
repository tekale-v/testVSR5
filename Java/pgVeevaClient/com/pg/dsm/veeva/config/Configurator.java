/*
 **   Configurator.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to load all resources bean.
 **
 */

package com.pg.dsm.veeva.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.xml.binder.VeevaConfigXML;
import com.pg.util.EncryptCrypto;

import matrix.db.BusinessObject;
import matrix.db.Context;

public class Configurator {

	boolean isLoaded;
	private Properties properties = new Properties();
	private Context context;
	private DomainObject matrixVeevaBusObj;
	private VeevaConfig veevaConfig;
	private VeevaConfigXML veevaConfigXML;
	private ExtractionRequiredFolders extractionRequiredFolders;
	private boolean isContextPushed;
	private String uiUserName;
	private String uiUserID;
	private String uiUserEmail;

	/** 
	 * @about - Private Constructor
	 * @param - Builder - builder object
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	private Configurator(Builder builder) {
		this.properties = builder.properties;
		this.context = builder.context;
		this.matrixVeevaBusObj = builder.matrixVeevaBusObj;
		this.veevaConfig = builder.veevaConfig;
		this.veevaConfigXML = builder.veevaConfigXML;
		this.extractionRequiredFolders = builder.extractionRequiredFolders;
		this.isLoaded = builder.isLoaded;
		this.isContextPushed = builder.isContextPushed;
		this.uiUserName = builder.uiUserName;
		this.uiUserID = builder.uiUserID;
		this.uiUserEmail = builder.uiUserEmail;
	}
	/** 
	 * @about Getter method - to get logged in user name
	 * @return String - user name string
	 * @since DSM 2018x.3
	 */
	public String getUiUserName() {
		return uiUserName;
	}
	/** 
	 * @about Getter method - to get logged in user id
	 * @return String - user id string
	 * @since DSM 2018x.3
	 */
	public String getUiUserID() {
		return uiUserID;
	}
	/** 
	 * @about Getter method - to get logged in user email
	 * @return String - user email string
	 * @since DSM 2018x.3
	 */
	public String getUiUserEmail() {
		return uiUserEmail;
	}
	/** 
	 * @about Getter method - to properties obj
	 * @return Properties - properties obj
	 * @since DSM 2018x.3
	 */
	public Properties getProperties() {
		return properties;
	}
	/** 
	 * @about Getter method - to check if context is pushed
	 * @return boolean - true/false
	 * @since DSM 2018x.3
	 */
	public boolean isContextPushed() {
		return isContextPushed;
	}
	/** 
	 * @about Getter method - to get context
	 * @return Context - matrix context
	 * @since DSM 2018x.3
	 */
	public Context getContext() {
		return context;
	}
	/** 
	 * @about Getter method - to get matrix-veeva DomainObject
	 * @return DomainObject
	 * @since DSM 2018x.3
	 */
	public DomainObject getMatrixVeevaBusObj() {
		return matrixVeevaBusObj;
	}
	/** 
	 * @about Getter method - to get matrix-veeva config bean object
	 * @return VeevaConfig
	 * @since DSM 2018x.3
	 */
	public VeevaConfig getVeevaConfig() {
		return veevaConfig;
	}
	/** 
	 * @about Getter method - to get matrix-veeva config xml bean object
	 * @return VeevaConfigXML
	 * @since DSM 2018x.3
	 */
	public VeevaConfigXML getVeevaConfigXML() {
		return veevaConfigXML;
	}
	/** 
	 * @about Getter method - to get folders bean object
	 * @return ExtractionRequiredFolders
	 * @since DSM 2018x.3
	 */
	public ExtractionRequiredFolders getExtractionRequiredFolders() {
		return extractionRequiredFolders;
	}
	/** 
	 * @about Getter method - to get if configurator is loaded
	 * @return boolean 
	 * @since DSM 2018x.3
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	public static class Builder {

		private final Logger logger = Logger.getLogger(this.getClass().getName());
		private Properties properties = new Properties();
		private Context context;
		private DomainObject matrixVeevaBusObj;
		private VeevaConfig veevaConfig;
		private VeevaConfigXML veevaConfigXML;
		private ExtractionRequiredFolders extractionRequiredFolders;
		private boolean isLoaded;

		private String uiUserName;
		private String uiUserID;
		private String uiUserEmail;
		private boolean isContextPushed;

		/** 
		 * @about - Constructor
		 * @param - String - execution type
		 * @since DSM 2018x.3
		 */
		public Builder() {
			this.isLoaded = false;
			this.isContextPushed = false;
		}

		/** 
		 * @about Setter method - to set properties object
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setProperties() {
			try (InputStream inStream = new FileInputStream(Veeva.VEEVA_PROPERTIES_FILE)) {
				properties.load(inStream);
				this.isLoaded = true;
			} catch (Exception e) {
				this.isLoaded = false;
				logger.error("************FAILED >>> Unable to load veeva.properties " + e);
			}
			return this;
		}
		/** 
		 * @about Setter method - to set matrix context
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder setContext() throws Exception {
			if(this.isLoaded) {
				try {
						setContext(true);

				} catch(Exception e) {
					this.isLoaded = false;
				}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set matrix-veeva config object.
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setMatrixVeevaConfigBusinessObject() throws Exception {
			if(this.isLoaded) {
				try {
					if(isConnected()) {
						BusinessObject configBus = new BusinessObject(Veeva.TYPE_PGCONFIGURATIONADMIN,
								Veeva.MATRIX_VEEVA_CONFIG_OBJECT_NAME, Veeva.SYMBOL_HYPHEN, Veeva.VAULT_ESERVICEPRODUCTION);
						String objectId = configBus.getObjectId(this.context);
						if (Utility.isNotNullEmpty(objectId)) {
							this.matrixVeevaBusObj = DomainObject.newInstance(context, objectId);
						}
					} else {
						logger.error(
								"************FAILED >>> Matrix context is lost inside setMatrixVeevaConfigBusinessObject");
						this.isLoaded = false;
					}
				}
				catch (FrameworkException e) {
					this.isLoaded = false;
					logger.error(
							"************FAILED >>> Matrix Veeva config business object error inside - setMatrixVeevaConfigBusinessObject "
									+ e);
				}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set matrix-veeva config object bean.
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setVeevaConfig() throws Exception {
			if(this.isLoaded) {
				try {
					if(isConnected()) {
						if (matrixVeevaBusObj.exists(context)) {
							this.veevaConfig = new VeevaConfig.Builder(context, matrixVeevaBusObj)
									.setStartDate()
									.setEndDate()
									.setConfigActive()
									.setAdminEmail()
									.setRetryCount()
									.setMatrixFormatStartDate()
									.setMatrixFormatEndDate()
									.setUTCFormatStartDate()
									.setUTCFormatEndDate()
									.setVeevaFormatStartDate()
									.setVeevaFormatEndDate()
									.setFromApprovedForDistributionDate()
									.setToApprovedForDistributionDate()
									.build();


							if (veevaConfig.isLoaded()) {
								logger.info("Veeva config loaded");
								logger.info("Config Active Matrix attribute value: "+veevaConfig.getAttrConfigActive());
								logger.info("Retry count Matrix attribute value: "+veevaConfig.getAttrRetryCount());
								logger.info("Admin Email Matrix attribute value: "+veevaConfig.getAttrAdminEmail());

								logger.info("________________________________________________________________________");
								logger.info("Start date Matrix attribute value: "+veevaConfig.getAttrStartDate());
								logger.info("Start date Matrix Format: "+veevaConfig.getMtxFormatStartDate());
								logger.info("Start date UTC Format: "+veevaConfig.getUtcFormatStartDate());
								logger.info("Start date Veeva Format: "+veevaConfig.getVeevaFormatStartDate());
								logger.info("Distribution date From: "+veevaConfig.getFromApprovedForDistributionDate());

								logger.info("End date Matrix attribute value: "+veevaConfig.getAttrEndDate());
								logger.info("End date Matrix Format: "+veevaConfig.getMtxFormatEndDate());
								logger.info("End date UTC Format: "+veevaConfig.getUtcFormatEndDate());
								logger.info("End date Veeva Format: "+veevaConfig.getVeevaFormatEnDate());
								logger.info("Distribution date To: "+veevaConfig.getToApprovedForDistributionDate());
								logger.info("________________________________________________________________________");
							} else {
								logger.error("Unable to initialize Veeva config business object bean");
								if (isConnected()) {
									logger.info("Sending Email for >> Unable to initialize Veeva config business object bean");
									ConfiguratorError configuratorError = new ConfiguratorError.Builder()
											.setContext(context)
											.setEmailFromAddress(PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT))
											.setEmailToAddress(properties.getProperty("veeva.configobject.mailid"))
											.setEmailBody(properties.getProperty("veeva.configobject.initialize.failure.message"))
											.setEmailSubject(properties.getProperty("veeva.configobject.initialize.failure.subject"))
											.build();
									configuratorError.notifyUserOnConfiguratorLoadFailure();
									logger.info("Email sent >>");
								}
							} 

						} else {
							logger.error("Veeva config business object does not exist");
							if (isConnected()) {
								logger.info("Sending Email for >> Veeva config business object does not exist");
								ConfiguratorError configuratorError = new ConfiguratorError.Builder()
										.setContext(context)
										.setEmailFromAddress(PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT))
										.setEmailToAddress(properties.getProperty("veeva.configobject.mailid"))
										.setEmailBody(properties.getProperty("veeva.configobject.check.failure.message"))
										.setEmailSubject(properties.getProperty("veeva.configobject.check.failure.subject"))
										.build();
								configuratorError.notifyUserOnConfiguratorLoadFailure();
							}
						}
					}

				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Error inside setVeevaConfig " + e);
					if (isConnected()) {
						logger.info("Sending Email for >> Error inside setVeevaConfig " + e);
						ConfiguratorError configuratorError = new ConfiguratorError.Builder()
								.setContext(context)
								.setEmailFromAddress(PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT))
								.setEmailToAddress(properties.getProperty("veeva.configobject.mailid"))
								.setEmailBody(properties.getProperty("veeva.configobject.initialize.failure.message"))
								.setEmailSubject(properties.getProperty("veeva.configobject.initialize.failure.subject"))
								.build();
						configuratorError.notifyUserOnConfiguratorLoadFailure();
					}
				}
			}

			return this;
		}
		/** 
		 * @about Setter method - to set matrix-veeva config object xml (jaxb) bean.
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setVeevaConfigXML() {
			if(this.isLoaded) {
				try {
					VeevaConfigXMLBuilder veevaConfigXMLBuilder = new VeevaConfigXMLBuilder.Builder(context).load();
					if (veevaConfigXMLBuilder.isLoaded) {
						this.veevaConfigXML = veevaConfigXMLBuilder.getVeevaXMLConfig();
						if (veevaConfigXML.getQueries().size() > 0) {
							logger.info("Veeva XML Page is valid");
						} else {
							logger.error("Unable to initialize Veeva XML config object bean");
							if (isConnected()) {
								logger.info("Sending Email for >> Unable to initialize Veeva XML config object bean");
								ConfiguratorError configuratorError = new ConfiguratorError.Builder()
										.setContext(context)
										.setEmailFromAddress(PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT))
										.setEmailToAddress(properties.getProperty("veeva.configobject.mailid"))
										.setEmailBody(properties.getProperty("veeva.queries.load.failure.message"))
										.setEmailSubject(properties.getProperty("veeva.queries.load.failure.subject"))
										.build();
								configuratorError.notifyUserOnConfiguratorLoadFailure();
							}
						}
					} else {
						logger.error("Unable to load Veeva XML page object");
						if (isConnected()) {
							logger.info("Sending Email for >> Unable to load Veeva XML page object");
							ConfiguratorError configuratorError = new ConfiguratorError.Builder()
									.setContext(context)
									.setEmailFromAddress(PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT))
									.setEmailToAddress(properties.getProperty("veeva.configobject.mailid"))
									.setEmailBody(properties.getProperty("veeva.xmlPage.load.failure.message"))
									.setEmailSubject(properties.getProperty("veeva.xmlPage.load.failure.subject"))
									.build();
							configuratorError.notifyUserOnConfiguratorLoadFailure();
						}
					}

				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Error loading Veeva XML Config Page to bean " + e);
				}
			}
			return this;
		}
		/** 
		 * @about Setter method - to set folders bean.
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder setExtractionRequiredFolders() {
			if(this.isLoaded) {
				try {
					this.extractionRequiredFolders = new ExtractionRequiredFolders.Create(properties.getProperty("veeva.extraction.folder")).perform();
				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Error creating required folders " + e);
				}
			}
			return this;
		}
		/** 
		 * @about Method to set matrix context
		 * @return void
		 * @throws Exception
		 * @since DSM 2018x.3
		 */
		public void setContext(boolean flag) throws Exception {
			if(this.isLoaded) {
				logger.info("Get matrix context");
				this.context = new Context(properties.getProperty("LOGIN_MATRIX_HOST"));
				this.context.setUser(properties.getProperty("CONTEXT_USER"));
				this.context.setPassword(EncryptCrypto.decryptString(properties.getProperty("CONTEXT_PASSWORD")));
				this.context.setRole("ctx::Design Engineer.PG.Internal_PG");
				logger.info("Matrix role is set");
				try {
					logger.info("Connect to Matrix >>>");
					this.context.connect();
					logger.info("Connect to Matrix successful >>>");
				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("************FAILED >>>> Unable to connect Matrix " + e);
				}
			}
		}
		
		/** 
		 * @about Helper method to set set/push matrix context
		 * @return boolean
		 * @since DSM 2018x.3
		 */
		public boolean isConnected() {
			boolean ret = false;
				ret = this.context.isConnected();
			return ret;
		}
		/** 
		 * @about Method to load Configurator bean
		 * @return Configurator
		 * @since DSM 2018x.3
		 */
		public Configurator build() {
			return new Configurator(this);
		}
	}
}
