/*
 **   MatrixValidator.java
 **   Description - Introduced as part of Veeva integration.      
 **   To perform validate operation on DSM.
 **
 */
package com.pg.dsm.veeva.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.helper.enovia.ArtworkErrorMessage;
import com.pg.dsm.veeva.helper.enovia.GCASValidator;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class MatrixValidator implements Veeva {

	private List<String> validationErrorMessages = new ArrayList<String>();
	// DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408,
	// 34409, 34410, 34411, 34412, 34413, 34414 - Starts
	List<ArtworkErrorMessage> artworkExceptionMessages = new ArrayList<ArtworkErrorMessage>();
	private boolean createArtwork;
	private boolean connectPMP;
	// DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408,
	// 34409, 34410, 34411, 34412, 34413, 34414 - Ends
	private GCASValidator gcasValidator;
	private boolean isLoaded;
	boolean isPassed;

	/**
	 * @about Constructor
	 * @param Builder - builder class
	 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
	 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	private MatrixValidator(Builder builder) {
		this.validationErrorMessages = builder.validationErrorMessages;
		this.gcasValidator = builder.gcasValidator;
		this.isLoaded = builder.isLoaded;
		this.artworkExceptionMessages = builder.artworkExceptionMessages;
		this.isPassed = builder.isPassed;
		this.createArtwork = builder.createArtwork;
		this.connectPMP = builder.connectPMP;
		
	}

	/**
	 * @about Getter method to get list of error messages
	 * @return List
	 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
	 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public List<ArtworkErrorMessage> getExceptionMessages() {
		return artworkExceptionMessages;
	}

	/**
	 * @about Getter method to get GCASValidator object
	 * @return List
	 * @since DSM 2018x.3
	 */
	public GCASValidator getGcasValidator() {
		return gcasValidator;
	}

	/**
	 * @about Method to check loader bean
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isLoaded() {
		return isLoaded;
	}

	/**
	 * @about Getter Method to check if all validation passed
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isPassed() {
		return isPassed;
	}

	/**
	 * @about Setter Method to check if all validation passed
	 * @param boolean
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}

	/**
	 * @about Setter Method to if bean loaded
	 * @param boolean
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	/**
	 * @about Getter method to get list of validation error messages
	 * @return List
	 * @since DSM 2018x.3
	 */
	public List<String> getValidationErrorMessages() {
		return validationErrorMessages;
	}

	/**
	 * @about Setter method to set list of validation error messages
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setValidationErrorMessages(List<String> errorMessages) {
		this.validationErrorMessages = errorMessages;
	}

	/**
	 * @about Setter method to set GCASValidator object
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setArtworkValidator(GCASValidator gcasValidator) {
		this.gcasValidator = gcasValidator;
	}

	/**
	 * @about getter method to get artwork creation flag
	 * @return boolean
	 * @since DSM 2018x.5
	 */
	public boolean isCreateArtwork() {
		return createArtwork;
	}

	/**
	 * @about setter method to set artwork creation flag
	 * @return void
	 * @since DSM 2018x.5
	 */
	public void setCreateArtwork(boolean createArtwork) {
		this.createArtwork = createArtwork;
	}

	/**
	 * @about setter method to get pmp connections flag
	 * @return void
	 * @since DSM 2018x.5
	 */
	public boolean isConnectPMP() {
		return connectPMP;
	}

	public static class Builder {

		private final Logger logger = Logger.getLogger(this.getClass().getName());

		private boolean isLoaded;
		private Configurator configurator;
		private Properties properties = new Properties();
		private DocumentProperty documentProperty;
		private GCASValidator gcasValidator;
		private String gcas;
		private Context context;
		boolean isPassed;
		private Map<?, ?> gcasInfoMap;
		Map<?, ?> gcasRelatedInfo;

		StringList objSelects = new StringList(11);

		List<String> validationErrorMessages = new ArrayList<String>();
		// DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408,
		// 34409, 34410, 34411, 34412, 34413, 34414 - Starts
		List<ArtworkErrorMessage> artworkExceptionMessages = new ArrayList<ArtworkErrorMessage>();
		private String documentID;
		private String documentNumber;
		private boolean createArtwork;
		private boolean connectPMP;
        private String sPMPAllowedStages;
		// DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408,
		// 34409, 34410, 34411, 34412, 34413, 34414 - Ends
		/**
		 * @about Constructor
		 * @param Configurator     - configurator bean
		 * @param DocumentProperty - documentProperty bean
		 * @param String           - artwok name string
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder(Configurator configurator, DocumentProperty documentProperty) throws Exception {
			this.isLoaded = false;
			this.configurator = configurator;
			this.context = this.configurator.getContext();
			this.properties = this.configurator.getProperties();
			this.documentProperty = documentProperty;
			gcasValidator = new GCASValidator();
			this.gcas = this.documentProperty.getPmp();
			gcasValidator.setGcas(gcas);

			this.objSelects.add(DomainConstants.SELECT_ID);
			this.objSelects.add(DomainConstants.SELECT_TYPE);
			this.objSelects.add(DomainConstants.SELECT_LAST_ID);
			this.objSelects.add(SELECT_LAST_CURRENT);
			this.objSelects.add(SELECT_LAST_REVISION);

			this.isPassed = true;
			this.documentID = this.documentProperty.getId();
			this.documentNumber = this.documentProperty.getDocumentNumber();
			this.createArtwork = true;
			this.connectPMP = false;
			this.sPMPAllowedStages = this.properties.getProperty("veeva.pmp.allowed.stages");
		}

		/**
		 * @about Method to perform gcas digits check
		 * @return Builder
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder performGCASNumberOfDigitsValidation() {
			logger.info("Enter MatrixValidator performGCASNumberOfDigitsValidation");
			try {
				if (this.gcas.length() != 8 || !this.gcas.matches("^[0-9]*$")) {
					gcasValidator.setGcasValid(false);
					gcasValidator.setInvalidGcasMessage(properties.getProperty("veeva.not.valid.gacs.error"));
					validationErrorMessages.add(properties.getProperty("veeva.not.valid.gacs.error"));
					gcasValidator.setGcasValid(false);
					this.isPassed = false;
					if (!properties.getProperty("veeva.create.artwork.notvalidgcas")
							.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
						this.createArtwork = false;
					}
				} else {
					gcasValidator.setGcasValid(true);
					this.isLoaded = true;
				}
			} catch (Exception e) {
				this.isLoaded = false;
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(
								properties.getProperty("veeva.artwork.gcasnumber.validation.failure.error"))
						.build();
				artworkExceptionMessages.add(artworkErrorMessage);
			}
			logger.info("Is Valid Gcas >> " + String.valueOf(gcasValidator.isGcasValid()));
			logger.info("MatrixValidator performGCASNumberOfDigitsValidation - validation "
					+ String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGCASNumberOfDigitsValidation");
			return this;
		}

		/**
		 * @about Method to perform gcas search
		 * @return Builder
		 * @throws Exception
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder performGcasSearch() throws Exception {
			logger.info("Enter MatrixValidator performGcasSearch");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasValid()) {
						String types = properties.getProperty("veeva.pmp.allowed.types");
						logger.info("Gcas allowd Types >> " + types);

						logger.info("Gcas Search Perform >>" + this.gcas);
						MapList objectList = DomainObject.findObjects(context, types, // typePattern
								this.gcas, // namepattern
								DomainConstants.QUERY_WILDCARD, // revpattern
								DomainConstants.QUERY_WILDCARD, // owner pattern
								VAULT_ESERVICEPRODUCTION, // vault pattern
								null, // where exp
								true, // expandType
								this.objSelects); // objectSelects

						if (objectList != null && objectList.size() > 0) {

							logger.info("Gcas Search count >> " + objectList.size());
							this.gcasInfoMap = (Map<?, ?>) objectList.get(0);
							logger.info("Gcas Info Map >> " + gcasInfoMap);
							gcasValidator.setGcasExist(true);
							logger.info("Is Gcas Exist " + String.valueOf(gcasValidator.isGcasExist()));

							gcasValidator.setGcasID((String) gcasInfoMap.get(DomainConstants.SELECT_LAST_ID));
							gcasValidator.setGcasRev((String) gcasInfoMap.get(SELECT_LAST_REVISION));
							gcasValidator.setGcasState((String) gcasInfoMap.get(SELECT_LAST_CURRENT));
							gcasValidator.setGcasType((String) gcasInfoMap.get(DomainConstants.SELECT_TYPE));
							gcasValidator.setGcasName(this.gcas);
							gcasValidator.setGcasSet(true);

							DomainObject obj = DomainObject.newInstance(context, gcasValidator.getGcasID());
							gcasValidator.setGcasObj(obj);
							logger.info("Set Gcas obj in bean");
							gcasValidator.setGcasObjExist(true);
							logger.info("Gcas Obj exist >>" + gcasValidator.isGcasObjExist());
							logger.info("Gcas ID >> " + gcasValidator.getGcasID());
							logger.info("Gcas Name >> " + gcasValidator.getGcasName());
							logger.info("Gcas Rev >> " + gcasValidator.getGcasRev());
							logger.info("Gcas State >> " + gcasValidator.getGcasState());
							logger.info("Gcas Type >> " + gcasValidator.getGcasType());
							logger.info("Gcas Info Set in the bean? >> " + gcasValidator.isGcasSet());

						} else {
							this.isPassed = false;
							gcasValidator.setGcasObjExist(false);
							validationErrorMessages.add(properties.getProperty("veeva.pmp.not.found.error"));
							if (!properties.getProperty("veeva.create.artwork.gcasnotfound")
									.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
								this.createArtwork = false;
							}
						}
					} else {
						gcasValidator.setGcasSet(false);
						this.isPassed = false;
						logger.info("Gcas is invalid. so Gcas Info Set in the bean? >> " + gcasValidator.isGcasSet());
					}

				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Exception occured in MatrixValidator - method performGcasSearch");
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(
									properties.getProperty("veeva.artwork.gcassearch.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info("MatrixValidator performGcasSearch - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasSearch");

			return this;
		}

		/**
		 * @about Method to check if artwork has connected gcas.
		 * @return Builder
		 * @throws Exception
		 * @since DSM 2018x.3 
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder peformGcasConnectedArtworkCheck() throws Exception {
			logger.info("Enter MatrixValidator peformGcasConnectedArtworkCheck");

			if (this.isLoaded) {
				try {
					logger.info("Is Gcas Exist " + String.valueOf(gcasValidator.isGcasExist()));
					if (gcasValidator.isGcasExist() && gcasValidator.isGcasObjExist()) {
						String types = properties.getProperty("veeva.atrwork.allowed.type");
						logger.info("Gcas artwork allowed Types >> " + types);

						StringList slObjSelects = new StringList(1);
						slObjSelects.add(DomainConstants.SELECT_NAME);

						MapList objectList = gcasValidator.getGcasObj().getRelatedObjects(context,
								DomainConstants.RELATIONSHIP_PART_SPECIFICATION, // relationship
								// pattern
								types, // Type pattern
								slObjSelects, // object selects
								null, // rel selects
								false, // to side
								true, // from side
								(short) 1, // recursion level.
								null, // object where clause
								null, // rel where clause
								0);

						if (objectList != null && !objectList.isEmpty()) {
							this.isPassed = false;
							logger.info("Gcas artwork count is >> " + objectList.size());
							gcasValidator.setGcasHasArtwork(true);

							logger.info("Gcas has artwork ? >> " + String.valueOf(gcasValidator.isGcasHasArtwork()));

							String sErrorMessage = properties.getProperty("veeva.pmp.contains.artwork.error");
							sErrorMessage = sErrorMessage.replace(Veeva.NAME_KEY, gcasValidator.getGcasName());
							sErrorMessage = sErrorMessage.replace(Veeva.REVISION_KEY, gcasValidator.getGcasRev());

							gcasValidator.setGcasHasArtworkMessage(sErrorMessage);
							logger.error("Gcas has artwork message >> " + gcasValidator.getGcasHasArtworkMessage());

							validationErrorMessages.add(gcasValidator.getGcasHasArtworkMessage());
						}
					}
				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Exception occured in MatrixValidator - method peformGcasConnectedArtworkCheck");
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(
									properties.getProperty("veeva.artwork.pmpartwork.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info(
					"MatrixValidator peformGcasConnectedArtworkCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator peformGcasConnectedArtworkCheck");

			return this;
		}

		/**
		 * @about Method to check if gcas has related connections.
		 * @return Builder
		 * @throws Exception
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder performGcasRelatedConnectionQuery() throws Exception {
			logger.info("Enter MatrixValidator performGcasRelatedConnectionQuery");
			if (this.isLoaded) {
				try {
					logger.info("Gcas has artwork ? >> " + String.valueOf(gcasValidator.isGcasHasArtwork()));
					if (gcasValidator.isGcasExist() && gcasValidator.isGcasObjExist()) {
						this.objSelects.add(SELECT_ATTRIBUTE_STATUS);
						this.objSelects.add(SELECT_ATTRIBUTE_RELEASE_PHASE);
						this.objSelects.add("from[" + RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.id");
						this.objSelects.add("from[" + RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.id");
						this.objSelects.add("to[" + RELATIONSHIP_CLASSIFIEDITEM + "].from.id");
						this.objSelects.add("from[" + RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.id");
						this.gcasRelatedInfo = gcasValidator.getGcasObj().getInfo(context, this.objSelects);
						logger.info("Gcas related info map >> " + gcasRelatedInfo);
						gcasValidator.setGcasRelatedConnectionChecked(true);
						logger.info("Gcas related query performed "
								+ String.valueOf(gcasValidator.isGcasRelatedConnectionChecked()));
					} else {
						gcasValidator.setGcasRelatedConnectionChecked(false);
					}
				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Exception occured in MatrixValidator - method performGcasRelatedConnectionQuery");
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(
									properties.getProperty("veeva.artwork.pmpconnections.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info(
					"MatrixValidator performGcasRelatedConnectionQuery - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasRelatedConnectionQuery");
			return this;
		}

		/**
		 * @about Method to set gcas release phase.
		 * @return Builder
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder setGcasReleasePhase() {
			logger.info("Enter MatrixValidator setGcasReleasePhase");
			if (this.isLoaded) {
				try {
					logger.info("Gcas related query performed "
							+ String.valueOf(gcasValidator.isGcasRelatedConnectionChecked()));
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasReleasePhase((String) gcasRelatedInfo.get(SELECT_ATTRIBUTE_RELEASE_PHASE));

						logger.info("Gcas release phase >> " + gcasValidator.getGcasReleasePhase());
					}
				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Exception occured in MatrixValidator - method setGcasReleasePhase");
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties
									.getProperty("veeva.artwork.setgcasreleasephase.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info("MatrixValidator setGcasReleasePhase - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator setGcasReleasePhase");
			return this;
		}

		/**
		 * @about Method to set gcas status
		 * @return Builder
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder setGcasStatus() {
			logger.info("Enter MatrixValidator setGcasStatus");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasStatus((String) gcasRelatedInfo.get(SELECT_ATTRIBUTE_STATUS));
						logger.info("Gcas status >> " + gcasValidator.getGcasStatus());
					}
				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Exception occured in MatrixValidator - method setGcasStatus");
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(
									properties.getProperty("veeva.artwork.setgcasstatus.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info("MatrixValidator setGcasStatus - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator setGcasStatus");
			return this;
		}

		/**
		 * @about Method to set gcas parent type
		 * @return Builder
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder setGcasParentType() {
			logger.info("Enter MatrixValidator setGcasParentType");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasParentType((String) gcasRelatedInfo.get(DomainConstants.SELECT_TYPE));
						logger.info("Gcas parent type >> " + gcasValidator.getGcasParentType());
					}

				} catch (Exception e) {
					this.isLoaded = false;
					logger.error("Exception occured in MatrixValidator - method setGcasParentType");
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(
									properties.getProperty("veeva.artwork.setgcasparenttype.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info("Exit MatrixValidator setGcasParentType");
			logger.info("MatrixValidator setGcasParentType - validation " + String.valueOf(this.isPassed));
			logger.info("MatrixValidator setGcasParentType - validation " + String.valueOf(this.isPassed));
			return this;
		}

		/**
		 * @about Method to perform gcas primary org check
		 * @return Builder
		 * @since DSM 2018x.3 
		 */
		public Builder performGcasPrimaryOrgCheck() {
			logger.info("Enter MatrixValidator performGcasPrimaryOrgCheck");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasPrimaryOrg(
								gcasRelatedInfo.containsKey("from[" + RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.id")
										? gcasRelatedInfo.get("from[" + RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.id")
										: "");

						if (null != gcasValidator.getGcasPrimaryOrg()) { // check for empty also?
							gcasValidator.setGcasHasPrimaryOrg(true);
						} else {
							gcasValidator.setGcasHasPrimaryOrg(false);
						}
						logger.info("Gcas has primary org? >> " + String.valueOf(gcasValidator.isGcasHasPrimaryOrg()));
					}

				} catch (Exception e) {
					this.isLoaded = false;
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties
									.getProperty("veeva.artwork.performgcasprimaryorgcheck.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
				}
			}
			logger.info("MatrixValidator performGcasPrimaryOrgCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasPrimaryOrgCheck");
			return this;
		}

		/**
		 * @about Method to perform gcas secondary org check
		 * @return Builder
		 * @since DSM 2018x.3 
		 */
		public Builder performGcasSecondaryOrgCheck() {
			logger.info("Enter MatrixValidator performGcasSecondaryOrgCheck");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasSecondaryOrg(
								gcasRelatedInfo.containsKey("from[" + RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.id")
										? gcasRelatedInfo
												.get("from[" + RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.id")
										: "");
						if (null != gcasValidator.getGcasSecondaryOrg()
								&& Utility.isNotNullEmpty((String) gcasValidator.getGcasSecondaryOrg())) { // check for
																											// empty
																											// also?
							gcasValidator.setGcasHasSecondaryOrg(true);
						} else {
							gcasValidator.setGcasHasSecondaryOrg(false);
						}
						logger.info(
								"Gcas has secondary org? >> " + String.valueOf(gcasValidator.isGcasHasSecondaryOrg()));
					}

				} catch (Exception e) {
					this.isLoaded = false;
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties
									.getProperty("veeva.artwork.performgcassecondaryorgcheck.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
				}
			}
			logger.info("MatrixValidator performGcasSecondaryOrgCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasSecondaryOrgCheck");
			return this;
		}

		/**
		 * @about Method to perform gcas control class check
		 * @return Builder
		 * @since DSM 2018x.3 
		 */
		public Builder performGcasSecurityClassCheck() {
			logger.info("Enter MatrixValidator performGcasSecurityClassCheck");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasSecurityClass(
								gcasRelatedInfo.containsKey("to[" + RELATIONSHIP_CLASSIFIEDITEM + "].from.id")
										? gcasRelatedInfo.get("to[" + RELATIONSHIP_CLASSIFIEDITEM + "].from.id")
										: "");

						if (null != gcasValidator.getGcasSecurityClass()
								&& Utility.isNotNullEmpty((String) gcasValidator.getGcasSecurityClass())) {
							gcasValidator.setGcasHasSecurityClass(true);
						} else {
							gcasValidator.setGcasHasSecurityClass(false);
						}
						logger.info("Gcas has security class? >> "
								+ String.valueOf(gcasValidator.isGcasHasSecurityClass()));
					}

				} catch (Exception e) {
					this.isLoaded = false;
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties.getProperty(
									"veeva.artwork.performgcassecurityclasscheck.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
				}
			}
			logger.info("MatrixValidator performGcasSecurityClassCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasSecurityClassCheck");
			return this;
		}

		/**
		 * @about Method to perform gcas segment check
		 * @return Builder
		 * @since DSM 2018x.3 
		 */
		public Builder performGcasSegmentCheck() {
			logger.info("Enter MatrixValidator performGcasSegmentCheck");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						gcasValidator.setGcasSegment(gcasRelatedInfo
								.containsKey("from[" + RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.id")
										? gcasRelatedInfo
												.get("from[" + RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.id")
												.toString()
										: "");
						if (null != gcasValidator.getGcasSegment()
								&& Utility.isNotNullEmpty((String) gcasValidator.getGcasSegment())) {
							gcasValidator.setGcasHasSegment(true);
						} else {
							gcasValidator.setGcasHasSegment(false);
						}
						logger.info("Gcas has security class? >> " + String.valueOf(gcasValidator.isGcasHasSegment()));

					}

				} catch (Exception e) {
					this.isLoaded = false;
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties
									.getProperty("veeva.artwork.performgcassegmentcheck.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
					 // DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
				}
			}
			logger.info("MatrixValidator performGcasSegmentCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasSegmentCheck");
			return this;
		}

		/**
		 * @about Method to perform gcas stage check
		 * @return Builder
		 * @since DSM 2018x.3 DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder performGcasStageCheck() {
			logger.info("Enter MatrixValidator performGcasStageCheck");
			if (this.isLoaded) {
				try {
					if (gcasValidator.isGcasRelatedConnectionChecked()) {
						String strParentType = gcasValidator.getGcasParentType();
						String strParentStatus = gcasValidator.getGcasStatus();
						String strPMPStage = gcasValidator.getGcasReleasePhase();
						String sErrorMessage;
						if (properties.getProperty("veeva.ipms.type").contains(strParentType) && (CONST_KEYWORD_SPACE.equalsIgnoreCase(strParentStatus) || UIUtil.isNullOrEmpty(strParentStatus)
								|| !sPMPAllowedStages.contains(strParentStatus)) && pgV3Constants.KEY_NO_VALUE.equalsIgnoreCase(properties.getProperty("veeva.artwork.disable.ipms.stagevalidation"))) {
								sErrorMessage = properties.getProperty("veeva.pmp.notvalid.stage.error");
								// updating error message for not valid imps stage
								validationErrorMessages.add(sErrorMessage);
								logger.error("Gcas perform stage check error: " + sErrorMessage);
								logger.error(strParentStatus + " is not allowed stage ");
								this.isPassed = false;
						} else if (!properties.getProperty("veeva.ipms.type").contains(strParentType) && (CONST_KEYWORD_SPACE.equalsIgnoreCase(strPMPStage) || UIUtil.isNullOrEmpty(strPMPStage) || !sPMPAllowedStages.contains(strPMPStage))) {
							sErrorMessage = properties.getProperty("veeva.pmp.notvalid.stage.error");
							// updating error message for not valid pmp stage
							validationErrorMessages.add(sErrorMessage);
							logger.error(strPMPStage + " is not allowed stage ");
							this.isPassed = false;
						}
					}
				} catch (Exception e) {
					this.isLoaded = false;
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties
									.getProperty("veeva.artwork.performgcasstagecheck.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			}
			logger.info("MatrixValidator performGcasStageCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performGcasStageCheck");
			return this;
		}

		/**
		 * @about Method to perform gcas state check
		 * @return Builder
		 * @since DSM 2018x.3 
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406,
		 *        34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder peformGcasStateCheck() throws Exception {
			logger.info("Enter MatrixValidator peformGcasStateCheck");
				try {
					if (this.isLoaded && gcasValidator.isGcasRelatedConnectionChecked()) {
						String strCurrent = gcasValidator.getGcasState();
						logger.info("Matrix Validator - state - " + strCurrent);

						if (properties.getProperty("veeva.pmp.notallowed.state").contains(strCurrent)) {
							validationErrorMessages.add(properties.getProperty("veeva.pmp.notvalid.state.error"));
							logger.error(gcasValidator.getGcasType() + " " + gcasValidator.getGcasName() + " "
									+ strCurrent + " is in Obsolete state ");
							this.isPassed = false;
							if (!properties.getProperty("veeva.create.artwork.obsoletestate")
									.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
								this.createArtwork = false;
							}
						} else if (!DomainConstants.STATE_PART_RELEASE.equals(strCurrent)) {
							String[] arry = getLatestRelease(context, gcasValidator.getGcasID());
							String strPMPLatestId = DomainConstants.EMPTY_STRING;
							String strPMPLatestVersion = DomainConstants.EMPTY_STRING;
							String strPMPLatestType = DomainConstants.EMPTY_STRING;
							String strPMPLatestState = DomainConstants.EMPTY_STRING;
							if (null != arry && arry.length > 1) {
								strPMPLatestId = arry[0];
								strPMPLatestVersion = arry[1];
								strPMPLatestState = arry[2];
								strPMPLatestType = arry[3];
								if ((UIUtil.isNotNullAndNotEmpty(strPMPLatestState)
										&& !STATE_OBSOLETE.equalsIgnoreCase(strPMPLatestState))
										&& (UIUtil.isNotNullAndNotEmpty(strPMPLatestType) && properties
												.getProperty("veeva.pmp.allowed.types.forartworkconnection")
												.contains(strPMPLatestType))) {
									gcasValidator.setGcasLatestRevisionID(strPMPLatestId);
								} else {
									logger.error(strPMPLatestType + Veeva.CONST_KEYWORD_SPACE + gcasValidator.getGcasName()
											+ Veeva.CONST_KEYWORD_SPACE + strPMPLatestVersion + Veeva.CONST_KEYWORD_SPACE
											+ strPMPLatestState + " pmp is allowed ");

									if (UIUtil.isNotNullAndNotEmpty(strPMPLatestState)
											&& STATE_OBSOLETE.equalsIgnoreCase(strPMPLatestState)
											&& properties.getProperty("veeva.pmp.previousrev.obsolete.ispassed")
													.equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE)) {
										validationErrorMessages
												.add(properties.getProperty("veeva.pmp.previousrev.obsolete.error"));
										this.isPassed = false;
									}
									if (UIUtil.isNotNullAndNotEmpty(strPMPLatestType)
											&& !properties.getProperty("veeva.pmp.allowed.types").contains(strPMPLatestType)
											&& properties.getProperty("veeva.pmp.previousrev.notallowedtype.ispassed")
													.equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE)) {
										validationErrorMessages.add(
												properties.getProperty("veeva.pmp.previousrev.notallowedtype.error"));
										this.isPassed = false;
									}
								}
							} else if (UIUtil.isNotNullAndNotEmpty(strCurrent)
									&& !strCurrent.equalsIgnoreCase(STATE_RELEASE)) {
								validationErrorMessages.add(properties.getProperty("veeva.pmp.state.not.release"));
								logger.error(gcasValidator.getGcasName() + " pmp not in release state");
								this.isPassed = false;
							}
							if (UIUtil.isNotNullAndNotEmpty(strPMPLatestId)
									&& !STATE_OBSOLETE.equalsIgnoreCase(strPMPLatestState)
									&& properties.getProperty("veeva.pmp.allowed.types.forartworkconnection")
											.contains(strPMPLatestType)) {
								logger.info("Previous Revision Id " + strPMPLatestId);
								DomainObject dObjPMPLR = DomainObject.newInstance(context, strPMPLatestId);
								StringList slbusSelects = new StringList(4);
								slbusSelects.add(SELECT_LAST_CURRENT);
								slbusSelects.add(DomainConstants.SELECT_TYPE);
								slbusSelects.add(SELECT_ATTRIBUTE_STATUS);
								slbusSelects.add(SELECT_ATTRIBUTE_RELEASE_PHASE);
								Map<?, ?> mpPMPLR = dObjPMPLR.getInfo(context, slbusSelects);
								if (mpPMPLR != null) {
									String strLRStage = (String) mpPMPLR.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
									String strLRStatus = (String) mpPMPLR.get(SELECT_ATTRIBUTE_STATUS);
									if (properties.getProperty("veeva.ipms.type").contains(strPMPLatestType)) {

										if ((CONST_KEYWORD_SPACE.equalsIgnoreCase(strLRStatus)  || UIUtil.isNullOrEmpty(strLRStatus) || !sPMPAllowedStages.contains(strLRStatus))
												&& pgV3Constants.KEY_NO_VALUE.equalsIgnoreCase(properties.getProperty("veeva.artwork.disable.ipms.stagevalidation"))) {
											String sErrorMessage = properties
													.getProperty("veeva.pmp.revision.notvalid.stage.error");
											sErrorMessage = sErrorMessage.replace(Veeva.REVISION_KEY, strPMPLatestVersion);
											validationErrorMessages.add(sErrorMessage);
											this.isPassed = false;
											logger.error(sErrorMessage);
											logger.error("IPMS previous revision is not in valid stage. Stage-"
													+ strLRStatus);
										}
									} else if ((CONST_KEYWORD_SPACE.equalsIgnoreCase(strLRStage) || UIUtil.isNullOrEmpty(strLRStage) || !sPMPAllowedStages.contains(strLRStage))) {
										String sErrorMessage = properties
												.getProperty("veeva.pmp.revision.notvalid.stage.error");
										sErrorMessage = sErrorMessage.replace(Veeva.REVISION_KEY, strPMPLatestVersion);

										validationErrorMessages.add(sErrorMessage);
										logger.error(
												"PMP previous revision is not in valid stage. Stage-" + strLRStage);
										this.isPassed = false;

									}

								}
							}

						}

					}

				} catch (Exception e) {
					this.isLoaded = false;
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties
									.getProperty("veeva.artwork.performgcasstatecheck.validation.failure.error"))
							.build();
					artworkExceptionMessages.add(artworkErrorMessage);
				}
			logger.info("MatrixValidator peformGcasStateCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Enter MatrixValidator peformGcasStateCheck");
			return this;
		}

		/**
		 * @about Method to perform gcas state check
		 * @param Context - context
		 * @return Builder DSM Added for 2018x.5 requirements 34404, 34405, 34406,
		 *         34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public String[] getLatestRelease(Context context, String strObjectId) throws Exception {
			logger.info("Enter MatrixValidator getLatestRelease");
			try {
				StringList slObjSelects = new StringList(4);
				slObjSelects.add(SELECT_ID);
				slObjSelects.add(SELECT_REVISION);
				slObjSelects.add(SELECT_CURRENT);
				slObjSelects.add(SELECT_TYPE);

				String strId;
				String strRevision;
				String strCurrent;
				String strType;

				String[] strArrLatestRelease = new String[4];

				DomainObject domObj = DomainObject.newInstance(context, strObjectId);
				MapList mlRevsioninfo = domObj.getRevisionsInfo(context, slObjSelects, new StringList());

				int size = mlRevsioninfo.size();
				Map<?, ?> mapRevsioninfo = null;
				for (int i = size - 1; i >= 0; i--) {
					mapRevsioninfo = (Map<?, ?>) mlRevsioninfo.get(i);

					strId = (String) mapRevsioninfo.get(SELECT_ID);
					strRevision = (String) mapRevsioninfo.get(SELECT_REVISION);
					strCurrent = (String) mapRevsioninfo.get(SELECT_CURRENT);
					strType = (String) mapRevsioninfo.get(SELECT_TYPE);
					if (STATE_OBSOLETE.equalsIgnoreCase(strCurrent) || STATE_RELEASE.equalsIgnoreCase(strCurrent)) {
						strArrLatestRelease[0] = strId;
						strArrLatestRelease[1] = strRevision;
						strArrLatestRelease[2] = strCurrent;
						strArrLatestRelease[3] = strType;
						logger.info("Latest Revision ID -- " + strArrLatestRelease);
						return strArrLatestRelease;
					}
				}
			} catch (Exception e) {
				logger.error("Exception in ArtworkImpl getLatestRelease method " + e);
				e.printStackTrace();
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties
								.getProperty("veeva.artwork.gcaslatestrevisioncheck.validation.failure.error"))
						.build();
				artworkExceptionMessages.add(artworkErrorMessage);
			}
			logger.info("MatrixValidator getLatestRelease - validation " + String.valueOf(this.isPassed));
			logger.info("Exit getLatestRelease Method - PMP not having revisions");
			return new String[0];
		}

		public boolean isGcasValid() {
			return gcasValidator.isGcasValid();
		}

		public MatrixValidator build() {
			return new MatrixValidator(this);
		}

		/**
		 * @about Method to perform country check
		 * @return Builder
		 * @since DSM 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409,
		 *        34410, 34411, 34412, 34413, 34414
		 */
		public Builder performCountryCheck() {
			logger.info("Enter MatrixValidator performCountryCheck");
			try {
				int countryCount = documentProperty.getCountries().getDocumentCountry().size();
				String strCountry;
				String strCountryId;
				String countryNotFound;
				StringList slCountries = new StringList();
				Map<?, ?> temp = null;
				for (int i = 0; i < countryCount; i++) {
					strCountry = documentProperty.getCountries().getDocumentCountry().get(i).getName();
					strCountry = strCountry.toUpperCase();
					temp = getCountryId(strCountry);
					strCountryId = (String) temp.get(SELECT_ID);
					if (Utility.isNotNullEmpty(strCountryId)) {
						slCountries.add(strCountryId);
						logger.info(strCountry + " Country connected to artwork");
					} else {
						if (this.isPassed) {
							this.connectPMP = true;
						}
						this.isPassed = false;
						countryNotFound = (String) temp.get(CONST_ERROR);
						logger.info("--------countryNotFound---------" + countryNotFound);
						validationErrorMessages.add(countryNotFound);
					}
				}
				logger.info("MatrixValidator performCountryCheck - country found list " + slCountries);
				gcasValidator.setCountryList(slCountries);
			} catch (Exception e) {
				this.isLoaded = false;
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(DomainConstants.EMPTY_STRING).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(
								properties.getProperty("veeva.artwork.countrycheck.validation.failure.error"))
						.build();
				artworkExceptionMessages.add(artworkErrorMessage);
			}
			logger.info("MatrixValidator performCountryCheck - validation " + String.valueOf(this.isPassed));
			logger.info("Exit MatrixValidator performCountryCheck");
			return this;
		}

		/**
		 * @about Method to search for country object
		 * @param String - country name
		 * @return Map
		 * @throws Exception
		 * @since DSM 2018x.5
		 */
		public Map<String, String> getCountryId(String sCountryName) throws MatrixException {
			logger.info("Entered getCountryId Method");
			String strCountryId = DomainConstants.EMPTY_STRING;
			StringBuilder sbCountryError = new StringBuilder();
			try {
				StringList slCountrySelects = new StringList(3);
				slCountrySelects.add(DomainConstants.SELECT_ID);
				slCountrySelects.add(DomainConstants.SELECT_CURRENT);
				slCountrySelects.add(DomainConstants.SELECT_NAME);

				MapList mpCountryObjs = DomainObject.findObjects(context, CPNCommonConstants.TYPE_COUNTRY, // typePattern
						sCountryName, // namepattern
						DomainConstants.QUERY_WILDCARD, // revpattern
						DomainConstants.QUERY_WILDCARD, // owner pattern
						VAULT_ESERVICEPRODUCTION, // vault pattern
						null, // where exp
						true, // expandType
						slCountrySelects); // objectSelects
				boolean bCountryFound = false;
				Map<?, ?> mpCountry = null;
				String strState;
				for (Iterator<?> itrCountry = mpCountryObjs.iterator(); itrCountry.hasNext();) {

					mpCountry = (Map<?, ?>) itrCountry.next();
					strState = (String) mpCountry.get(DomainConstants.SELECT_CURRENT);
					if (DomainConstants.STATE_PERSON_ACTIVE.equalsIgnoreCase(strState)) {
						bCountryFound = true;
						strCountryId = (String) mpCountry.get(DomainConstants.SELECT_ID);
						break;
					} else {
						logger.info(sCountryName + " Country is not active");
						sbCountryError.append(sCountryName + properties.getProperty("veeva.artwork.country.notactive"));
					}
				}
				if (!bCountryFound && sbCountryError.length() == 0) {
					logger.info(sCountryName + " not found in DSM");
					sbCountryError.append(sCountryName).append(SYMBOL_SPACE)
							.append(properties.getProperty("veeva.artwork.country.notfound"));
				}
			} catch (Exception e) {
				logger.error("Exception in MatrixOperation getCountryId method " + e);
				throw e;
			}

			Map<String, String> mpCountry = new HashMap<String, String>();
			mpCountry.put(SELECT_ID, strCountryId);
			mpCountry.put(CONST_ERROR, sbCountryError.toString());
			logger.info("Exit getCountryId Method");
			return mpCountry;
		}	
	}
}
