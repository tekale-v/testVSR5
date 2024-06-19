/*
 **   MatrixOperation.java
 **   Description - Introduced as part of Veeva integration.      
 **   To perform connect operation on DSM.
 **
 */
package com.pg.dsm.veeva.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.veeva.helper.enovia.ArtworkErrorMessage;
import com.pg.dsm.veeva.helper.enovia.GCASValidator;
import com.pg.dsm.veeva.helper.enovia.bo.Artwork;
import com.pg.dsm.veeva.helper.enovia.bo.ChangeOrder;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class MatrixOperation implements Veeva {

	private List<ArtworkErrorMessage> artworkErrorMessages = new ArrayList<ArtworkErrorMessage>();

	private MatrixOperation(Builder builder) {
		this.artworkErrorMessages = builder.artworkErrorMessages;
	}

	public List<ArtworkErrorMessage> getArtworkErrorMessages() {
		return artworkErrorMessages;
	}

	public void setArtworkErrorMessages(List<ArtworkErrorMessage> artworkErrorMessages) {
		this.artworkErrorMessages = artworkErrorMessages;
	}

	public static class Builder {

		private final Logger logger = Logger.getLogger(this.getClass().getName());
		private Context context;
		private Artwork artworkObj;
		private MatrixValidator matrixValidator;
		private GCASValidator gcasValidator;
		private Properties properties = new Properties();
		private List<ArtworkErrorMessage> artworkErrorMessages = new ArrayList<ArtworkErrorMessage>();
		private String documentID;
		private String documentNumber;
		private String gcas;
		private String artworkName;
		private String artworkID;
		private File[] renditionFiles;
		private String renditionFolderPath;
		private ChangeOrder changeOrderObj;
		private boolean isPassed;
		//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
		private List<ArtworkErrorMessage> artworkCreateExceptionMessages;
		//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends

		/**
		 * @about Constructor
		 * @param Matrix - bean
		 * @since DSM 2018x.3
		 */
		public Builder(Matrix matrix) {
			this.properties = matrix.getConfigurator().getProperties();
			this.context = matrix.getContext();
			this.matrixValidator = matrix.getMatrixValidator();
			this.gcasValidator = matrixValidator.getGcasValidator();

			this.documentID = matrix.getDocumentID();
			this.documentNumber = matrix.getDocumentNumber();
			//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
			this.artworkName = matrix.getArtworkName();
			this.artworkID = matrix.getArtworkId();
			this.gcas = matrix.getGcas();
            //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
			this.artworkObj = matrix.getArtwork();
			this.renditionFiles = matrix.getRenditionFiles();
			this.renditionFolderPath = matrix.getRenditionFolderPath();

			this.changeOrderObj = matrix.getChangeOrder();

			this.isPassed = matrixValidator.isPassed;
			logger.info("Matrix Operations - is Matrix Validation Passed " + String.valueOf(isPassed));
			//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
			this.artworkCreateExceptionMessages = matrix.getArtworkErrorMessages();
			//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
		}

		/**
		 * @about Method to connect template and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectTemplate() {
			logger.info("Enter MatrixOperation - method connectTemplate");
			try {
				DomainObject doArtTemplate = DomainObject.newInstance(context,
						new BusinessObject(TYPE_TEMPLATE, ARTWORK_TEMPLATE_NAME, ONE, VAULT_ESERVICEPRODUCTION));
				String[] listForOwningRegionConnection = new String[] { this.artworkID };
				if (doArtTemplate.exists(context)) {
					DomainRelationship.connect(context, doArtTemplate, RELATIONSHIP_TEMPLATE, false,
							listForOwningRegionConnection);
					logger.info("Artwork template successfully connected");
				}
				logger.info("Exit connectArtworktemplate Method");
			} catch (Exception e) {
				logger.error("Exception occured when connecting Artwork <<" + this.artworkName + ">> to a Template "
						+ e.getMessage());
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(
								properties.getProperty("veeva.artwork.template.connection.failure.error"))
						.build();

				artworkErrorMessages.add(artworkErrorMessage);
			}
			logger.info("Exit MatrixOperation - method connectTemplate");
			return this;
		}

		/**
		 * @about Method to connect gcas and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectGcas() {
			logger.info("Enter MatrixOperation - method connectGcas");
			try {
				if(this.isPassed || matrixValidator.isConnectPMP()) {
					DomainObject domART = DomainObject.newInstance(context, this.artworkID);
					String gcasId = gcasValidator.getGcasID();
					logger.info("Inside MatrixOperation - connectGcas Gcas Id >>> " + gcasId);
					if (Utility.isNotNullEmpty(gcasId)) {
						logger.info("Connect Artwork and Gcas");
						domART.addRelatedObject(context, new RelationshipType(DomainConstants.RELATIONSHIP_PART_SPECIFICATION),
								true, gcasId);
						logger.info("Gcas an Artwork connection successfull");
					}
					logger.info("Is Gcas Set " + String.valueOf(gcasValidator.isGcasSet()));
					if (gcasValidator.isGcasSet()) {
						logger.info("Gcas State " + String.valueOf(gcasValidator.getGcasState()));
						if (!DomainConstants.STATE_PART_RELEASE.equalsIgnoreCase(gcasValidator.getGcasState())) {
							String pmpLRId = gcasValidator.getGcasLatestRevisionID();
							logger.info("Inside MatrixOperation - connectGcas Gcas Latest Revsion Id >>> " + pmpLRId);
							if (Utility.isNotNullEmpty(pmpLRId)) {
								logger.info("Connect Artwork and Gcas latest revision");
								domART.addRelatedObject(context,
										new RelationshipType(DomainConstants.RELATIONSHIP_PART_SPECIFICATION), true, pmpLRId);
								logger.info("Gcas Latest Revision connection successfull");
							}
						}
					}
				}else {
					logger.info("PMP is already connected to failed view artwork <<" + this.artworkName+">> ");
				}
			} catch (Exception e) {
				logger.error("Exception occured in (method - connectGcas) when connecting Artwork <<" + this.artworkName
						+ ">> to Gcas " + e.getMessage());
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.pmp.connection.failure.error")).build();

				artworkErrorMessages.add(artworkErrorMessage);

			}
			logger.info("Exit MatrixOperation - method connectGcas");
			return this;
		}

		/**
		 * @about Method to connect primary org and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectPrimaryOrganization() {
			logger.info("Enter MatrixOperation - method connectPrimaryOrganization");
			try {
				if(this.isPassed || matrixValidator.isConnectPMP()) {
					DomainObject domArt = DomainObject.newInstance(context, this.artworkID);
					DomainObject domPrimaryOrg = DomainObject.newInstance(context);
					if (gcasValidator.isGcasHasPrimaryOrg()) {
						String primaryOrgId = (String) gcasValidator.getGcasPrimaryOrg();
						logger.info("Primary Organization Id >>> " + primaryOrgId);
						if (Utility.isNotNullEmpty(primaryOrgId)) {
							domPrimaryOrg.setId(primaryOrgId);
							DomainRelationship.connect(context, domArt, RELATIONSHIP_PGPRIMARYORGANIZATION, domPrimaryOrg);
							logger.info("Primary Org Connection Successfull");
						} else {
							logger.info("PMP is not having Primary Organization to connect to artwork");
						}
					}
				}else {
					logger.info("Processing default Primary Org Connection");
					findAndConnect(context, this.artworkID, TYPE_PGPLIORGANIZATIONCHANGEMANAGEMENT,
							RELATIONSHIP_PGPRIMARYORGANIZATION, false, properties);
					logger.info("Default Primary Org Connection Successfull");
				}

				logger.info("Exit connectPrimaryOrganization Method");
			} catch (Exception e) {
				logger.error("Exception in MatrixOperation connectPrimaryOrganization method " + e);
				e.printStackTrace();
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.primaryorg.connection.failure.error"))
						.build();

				artworkErrorMessages.add(artworkErrorMessage);
			}
			logger.info("Exit MatrixOperation - method connectPrimaryOrganization");
			return this;
		}

		/**
		 * @about Method to connect secondary org and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectSecondaryOrganization() {
			logger.info("Enter MatrixOperation - method connectSecondaryOrganization");
			logger.info("Entered connectSecondaryOrganization Method");
			try {
				if(this.isPassed || matrixValidator.isConnectPMP()) {
					if (Utility.isNotNullEmpty(this.artworkID)) {
						DomainObject domArt = DomainObject.newInstance(context, this.artworkID);
						DomainObject domSecondaryOrg = DomainObject.newInstance(context);
						if (gcasValidator.isGcasHasSecondaryOrg()) {
							String secondaryOrgId = (String) gcasValidator.getGcasSecondaryOrg();
							logger.info("Secondary Organization Id >>> " + secondaryOrgId);
							if (Utility.isNotNullEmpty(secondaryOrgId)) {
								domSecondaryOrg.setId(secondaryOrgId);
								DomainRelationship.connect(context, domArt, RELATIONSHIP_PGSECONDARYORGANIZATION,
										domSecondaryOrg);
								logger.info("Secondary Org Connection Successfull");
							}
						}
					}
					logger.info("Exit Secondary Org Connection Successfull");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception in MatrixOperation connectSecondaryOrganization method " + e);
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.secondaryorg.connection.failure.error"))
						.build();
				artworkErrorMessages.add(artworkErrorMessage);
			}
			logger.info("Exit MatrixOperation - method connectSecondaryOrganization");
			return this;
		}

		/**
		 * @about Method to connect control class and artwork
		 * @return Builder
		 * @throws Exception
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectControlClass() throws Exception {

			logger.info("Enter MatrixOperation - method connectSecondaryOrganization");

			StringBuilder sbType = new StringBuilder();
			sbType.append(TYPE_IPCONTROLCLASS);
			sbType.append(SYMBOL_COMMA);
			sbType.append(TYPE_SECURITYCONTROLCLASS);
			sbType.append(SYMBOL_COMMA);
			sbType.append(TYPE_EXPORTCONTROLCLASS);

			try {
				if (this.isPassed || matrixValidator.isConnectPMP()) {
					DomainObject domArt = DomainObject.newInstance(context, this.artworkID);
					String sPMPId = gcasValidator.getGcasID();
					if (Utility.isNotNullEmpty(sPMPId)) {
						DomainObject domPMP = DomainObject.newInstance(context, sPMPId);
						StringList slObjSelects = new StringList(1);
						slObjSelects.add(DomainConstants.SELECT_ID);
						MapList mlPMPData = domPMP.getRelatedObjects(context, RELATIONSHIP_CLASSIFIEDITEM, // relationship
								// pattern
								sbType.toString(), // Type pattern
								slObjSelects, // object selects
								null, // rel selects
								true, // to side
								false, // from side
								(short) 1, // recursion level
								null, // object where clause
								null, // rel where clause
								0);
						logger.info(">>>>>>>>>mlPMPData.>>>>>>>>>>>> " + mlPMPData);
						int iSizeOfPMPData = mlPMPData.size();
						Map<?, ?> mpPMPMap = null;
						String sClassificationId;
						StringList slClassification = new StringList(iSizeOfPMPData);
						if (iSizeOfPMPData > 0) {
							for (int i = 0; i < iSizeOfPMPData; i++) {
								mpPMPMap = (Map<?, ?>) mlPMPData.get(i);
								sClassificationId = (String) mpPMPMap.get(DomainConstants.SELECT_ID);
								slClassification.add(sClassificationId);
							}
							logger.info("processing security class connections for " + slClassification);
							String[] aClassifications = slClassification.toArray(new String[slClassification.size()]);
							DomainRelationship.connect(context, domArt, RELATIONSHIP_CLASSIFIEDITEM, false,
									aClassifications, false);
							logger.info("Security Class connection successfull");
						} else {
							logger.info("PMP is not having Security Class connections");
						}
					}
				}else {
					logger.info("Processing default Security Class connection");
					findAndConnect(context, this.artworkID, sbType.toString(), RELATIONSHIP_CLASSIFIED_ITEM, true,
							properties);
					logger.info("default Security Class connection successfull");
				}
				logger.info("Exit connectControlClass Method");
			} catch (Exception e) {
				logger.error("Exception in MatrixOperation connectControlClass method " + e.getMessage());
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.securityclass.connection.failure.error"))
						.build();
				artworkErrorMessages.add(artworkErrorMessage);
			}
			logger.info("Exit MatrixOperation - method connectSecondaryOrganization");
			return this;
		}

		/**
		 * @about Method to connect segment and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectSegment() {
			logger.info("Enter MatrixOperation - method connectSegment");
			try {
				if(this.isPassed || matrixValidator.isConnectPMP()) {
					DomainObject domArt = DomainObject.newInstance(context, this.artworkID);
					DomainObject domSegment = DomainObject.newInstance(context);
					if (gcasValidator.isGcasHasSegment()) {
						String segmentId = gcasValidator.getGcasSegment();
						logger.info("Segment Id >>> " + segmentId);
						if (Utility.isNotNullEmpty(segmentId)) {
							domSegment.setId(segmentId);
							DomainRelationship.connect(context, domArt, RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT,
									domSegment);
							logger.info("Segment Connection Successfull");
						}
					}
				} else {
					logger.info(" Processing default Segment connection ");
					findAndConnect(context, this.artworkID, TYPE_PGPLISEGMENT,
							RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT, false, properties);
					logger.info(" Default Segment connection successfull ");
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception in MatrixOperation connectSegment method " + e);
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.segment.connection.failure.error"))
						.build();
				artworkErrorMessages.add(artworkErrorMessage);
			}
			return this;
		}

		/**
		 * @about Method to connect countries and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectCountries() {
			logger.info("Enter MatrixOperation - method connectSegment");
			try {
				RelationshipType relationShipType = new RelationshipType(RELATIONSHIP_MIGRATE_POAToCOUNTRY);

				StringList slCountries = gcasValidator.getCountryList();
				boolean ignoreDuplicates = true;
				boolean isFromSide = true;
				int countryCount = slCountries.size();
				if (countryCount > 0) {
					DomainObject doArt = DomainObject.newInstance(context, this.artworkID);
					String[] aCountries = slCountries.toArray(new String[slCountries.size()]);
						DomainRelationship.connect(context, doArt, relationShipType, isFromSide, aCountries,
								ignoreDuplicates);
				} else {
					logger.info("Veeva document is not having countries defined");
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception in MatrixOperation connectCountries method " + e);
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.segment.connection.failure.error"))
						.build();
				artworkErrorMessages.add(artworkErrorMessage);

			}
			return this;
		}

		/**
		 * @about Method to connect ipm document and artwork
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder connectIPMDocument() {

			logger.info("Enter MatrixOperation - method connectIPMDocument");
			logger.info("Number of rendition files >>" + renditionFiles.length);
			try {
				if (renditionFiles.length > 0) {
					logger.info("Proceed to create and connect IPM Document");
					artworkObj.createAndConnectIPMDoc(renditionFiles, renditionFolderPath, documentID);
					logger.info("Rendition files checked-in successfully");
				}
			} catch (Exception e) {
				logger.error("Error in IPM Document connection " + e);

				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
						.setCustomErrorMessage(properties.getProperty("veeva.ipmdocument.connection.failure.error"))
						.build();
				artworkErrorMessages.add(artworkErrorMessage);
			}

			logger.info("Exit MatrixOperation - method connectIPMDocument");
			return this;
		}

		/**
		 * @about Method to promote artwork, co & ca
		 * @return Builder
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Builder promote() {
			// promote only if all validations are passed and no errors in previous steps.
			if (this.isPassed && artworkErrorMessages.isEmpty() && matrixValidator.getExceptionMessages().isEmpty()
					&& artworkCreateExceptionMessages.isEmpty()) {
				try {
					artworkObj.setArtworkState();
					gcasValidator.setArtworkPromoted(true);
				} catch (Exception e) {
					logger.error("Error in Artwork Promotion/Dynamic Sub " + e);
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(this.artworkName).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties.getProperty("veeva.artwork.state.promotion.error"))
							.build();
					artworkErrorMessages.add(artworkErrorMessage);
				}
				try {
					logger.info("CO ID :" + changeOrderObj.getChangeOrderID());
					changeOrderObj.setCOandCAState(gcasValidator);
				} catch (Exception e) {
					logger.error("Error in CA / CO State Promotion " + e);

					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(this.artworkName).setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
							.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())
							.setCustomErrorMessage(properties.getProperty("veeva.co.state.promotion.error")).build();
					artworkErrorMessages.add(artworkErrorMessage);
				}
			}
			return this;
		}

		/**
		 * @about Method to trigger dynamic subscription
		 * @return Builder
		 * @throws Exception
		 * @since DSM 2018x.3
		 */
		public Builder triggerDynamicSubscription() throws Exception {
			try {
				//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
				if (this.isPassed && gcasValidator.isArtworkPromoted()) {
					//Start modified for 22x changes - commenting Dynamic Subscription code
					/*logger.info("Processing dynamic subscriptions on Artwork Release");
					JPO.invoke(context, "emxCPNDynamicSubscription", null, "markForDynamicSubscription",
							new String[] { this.artworkID, STR_PROMOTE, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING,
									EMPTY_STRING, EMPTY_STRING },
							null);*/
					//End modified for 22x changes - commenting Dynamic Subscription code
				}
			} catch (Exception e) {
				ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
						.setArtworkName(this.artworkName).setDocumentID(this.documentID)
						.setDocumentNumber(this.documentNumber).setGcas(this.gcas)
						.setErrorCode(e.getClass().getCanonicalName()).setErrorMessage(e.getLocalizedMessage())

						// to-do: add custom message property entry for Dynamic Subscription failure.
						.setCustomErrorMessage(properties.getProperty("veeva.dynamic.subscription.perform.error"))
						.build();
				artworkErrorMessages.add(artworkErrorMessage);
				//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
			}
			return this;
		}
		
		/** 
		 * @about Method to connect artwork.
		 * @param Context - matrix context
		 * @param String  - artwork id
		 * @param String  - type
		 * @param String  - relationship name
		 * @param boolean - true/false
		 * @return void
		 * @throws Exception
		 * @since DSM 2018x.3
		 */
		public void findAndConnect(Context context, String artworkId, String type, String relationship, boolean isFromSide,
				Properties veevaProperties) throws Exception {
			try {
				String sRelName = relationship.replaceAll("\\s", "");
				StringList slObjectSelects = new StringList();
				slObjectSelects.add(DomainConstants.SELECT_ID);
				String sName = veevaProperties.getProperty("veeva.artwork.default." + sRelName);
				MapList mlObjectList = DomainObject.findObjects(context, type, // typePattern
						sName, // namepattern
						DomainConstants.QUERY_WILDCARD, // revpattern
						DomainConstants.QUERY_WILDCARD, // owner pattern
						VAULT_ESERVICEPRODUCTION, // vault pattern
						null, // where exp
						true, // expandType
						slObjectSelects); // objectSelects
				if (mlObjectList != null && !mlObjectList.isEmpty()) {
					Map<?, ?> mpObjectsMap = (Map<?, ?>) mlObjectList.get(0);
					String objectId = (String) mpObjectsMap.get(DomainConstants.SELECT_ID);
					String[] alArtwork = new String[] { artworkId };
					DomainRelationship.connect(context, new DomainObject(objectId), relationship, isFromSide, alArtwork);
				}else {
					logger.info(type+ CONST_KEYWORD_SPACE + sName + "not existing in DSM");
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception in MatrixOperation findAndConnect method "+e);
				throw e;
			}
		}

		/**
		 * @about Builder method
		 * @return MatrixOperation
		 * @since DSM 2018x.3
		 */
		public MatrixOperation perform() {
			return new MatrixOperation(this);
		}
	}
}
