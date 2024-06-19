/*
 **   Artwork.java
 **   Description - Introduced as part of Veeva integration.      
 **   Artwork implementation bean.
 **
 */
package com.pg.dsm.veeva.helper.enovia.bo;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.dsm.veeva.vql.xml.binder.bo.Select;

import matrix.db.Context;

public class Artwork {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	boolean isCreated;
	ArtworkImpl artworkImpl;
	String artworkID;
	Context context;
	Properties properties;
	String artworkName;

	private String artworkCreationOrUpdationExceptionMessage; 
	/** 
	 * @about - Constructor
	 * @param - Process - Process object
	 * @since DSM 2018x.3
	 */
	private Artwork(Process process) {
		this.artworkID = process.artworkID;
		this.context = process.context;
		this.properties = process.properties;
		this.isCreated = process.isCreated;
		
		this.artworkImpl = process.artworkImpl;
		this.artworkCreationOrUpdationExceptionMessage = process.artworkCreationOrUpdationExceptionMessage;
		this.artworkName = process.artworkName;
	}
	/** 
	 * @about getter method 
	 * @return ArtworkImpl
	 * @since DSM 2018x.3
	 */
	public ArtworkImpl getArtworkImpl() {
		return artworkImpl;
	}
	/** 
	 * @about getter method 
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkCreationOrUpdationExceptionMessage() {
		return artworkCreationOrUpdationExceptionMessage;
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
	 * @return Properties
	 * @since DSM 2018x.3
	 */
	public Properties getProperties() {
		return properties;
	}
	/** 
	 * @about getter method 
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getObjectId() {
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
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isCreated() {
		return isCreated;
	}
	/** 
	 * @about setter method 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}
	
	/** 
	 * @about getter method 
	 * @return String
	 * @since DSM 2018x.5
	 */
	public String getArtworkName() {
		return artworkName;
	}

	/** 
	 * @about method to perform connect and create document
	 * @param  File[] - files
	 * @param String - path
	 * @param String - document id
	 * @throws Exception
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void createAndConnectIPMDoc(File[] renditionFiles, String renditionFilePath, String documentID) throws Exception {
		artworkImpl.createAndConnectIPMDoc(context, artworkID, properties, renditionFiles, renditionFilePath, documentID);
	}

	/** 
	 * @about setter method 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setArtworkState() throws Exception {
		if(Utility.isNotNullEmpty(artworkID)){							
			String command = "mod bus $1 current $2";
         	MqlUtil.mqlCommand(context, command,artworkID,Veeva.STATE_RELEASE);		
         	logger.info("Artwork state is set to Release");
		}		
	}
	public static class Process {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		String errorMessage;
		boolean isCreated;
		String artworkID;
		Context context;
		DocumentProperty documentProperty;
		List<Select> selects;
		ArtworkImpl artworkImpl;
		Properties properties;
		private String artworkCreationOrUpdationExceptionMessage; 
		String artworkName = DomainConstants.EMPTY_STRING;
		
		/** 
		 * @about - Constructor
		 * @param Context - Context
		 * @param Properties - object
		 * @param DocumentProperty - bean
		 * @param List - select bean
		 * @param String - artwork id
		 * @param String - execution type
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Process(Context context, Properties properties, DocumentProperty documentProperty, List<Select> selects) {
			this.context = context;
			this.properties = properties;
			this.documentProperty = documentProperty;
			this.selects = selects;
			this.isCreated = false;
			this.errorMessage = Veeva.EMPTY_STRING;
			this.artworkImpl = new ArtworkImpl();
			this.artworkCreationOrUpdationExceptionMessage = Veeva.EMPTY_STRING;
		}
		/** 
		 * @about Builder method to create or update artwork
		 * @return Artwork
		 * @since DSM 2018x.3
		 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
		 */
		public Artwork perform() {
					try {
						logger.info("Schedule extraction - proceed to create new artwork");
						logger.info("Schedule extraction - proceed to create new artwork");
						artworkID = artworkImpl.createArtwork(context, properties, documentProperty, selects);
						this.isCreated = true;
						artworkName = DomainObject.newInstance(context, artworkID).getInfo(context, DomainConstants.SELECT_NAME);
						logger.info("Schedule extraction - proceed to create new artwork - successful");
					} catch(Exception e) {
						this.isCreated = false;
						logger.error("Exception in Schedule extraction - new artwork creation "+e);
						artworkCreationOrUpdationExceptionMessage = e.getMessage();
					}
				
			return new Artwork(this);
		}
	}
}
