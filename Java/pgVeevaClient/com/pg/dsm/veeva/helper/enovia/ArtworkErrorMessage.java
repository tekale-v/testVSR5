/*
 **   ArtworkErrorMessage.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean To capture validation/exception messages.
 **
 */
package com.pg.dsm.veeva.helper.enovia;

import java.util.List;

public class ArtworkErrorMessage {
	private String artworkName;
	private String documentNumber;
	private String documentID;
	private String gcas;
	private String errorCode;
	private String errorMessage;
	private String errorCustomMessage;
	private String artworkId;
	private List<String> validationErrorMessages;
	
	/** 
	 * @about Constructor
	 * @param Builder - builder class
	 * @since DSM 2018x.3
	 */
	private ArtworkErrorMessage(Builder builder) {
		this.artworkName = builder.artworkName;
		this.documentNumber = builder.documentNumber;
		this.documentID = builder.documentID;
		this.gcas = builder.gcas;
		this.errorCode = builder.errorCode;
		this.errorMessage = builder.errorMessage;
		this.errorCustomMessage = builder.errorCustomMessage;	
		this.artworkId = builder.artworkId;
		this.validationErrorMessages = builder.validationErrorMessages;
	}
	/** 
	 * @about Getter method to get artwork id
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkId() {
		return artworkId;
	}
	public List<String> getValidationErrorMessages() {
		return validationErrorMessages;
	}
	public void setArtworkId(String artworkId) {
		this.artworkId = artworkId;
	}
	/** 
	 * @about Getter method to get artwork name
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkName() {
		return artworkName;
	}
	/** 
	 * @about Setter method to set artwork name
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setArtworkName(String artworkName) {
		this.artworkName = artworkName;
	}
	/** 
	 * @about Getter method to get document number
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}
	/** 
	 * @about Setter method to set document number
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	/** 
	 * @about Getter method to get document id
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getDocumentID() {
		return documentID;
	}
	/** 
	 * @about Setter method to set document id
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}
	/** 
	 * @about Getter method to get gcas name
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getGcas() {
		return gcas;
	}
	/** 
	 * @about Setter method to set gcas name
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcas(String gcas) {
		this.gcas = gcas;
	}
	/** 
	 * @about Getter method to get error code
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/** 
	 * @about Setter method to set error code
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/** 
	 * @about Getter method to get error message
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/** 
	 * @about Setter method to set error message
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/** 
	 * @about Getter method to get custom error message
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getErrorCustomMessage() {
		return errorCustomMessage;
	}
	/** 
	 * @about Setter method to set custom error message
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setErrorCustomMessage(String errorCustomMessage) {
		this.errorCustomMessage = errorCustomMessage;
	}
	public static class Builder {
		
		private String artworkName;
		private String artworkId;
		private String documentNumber;
		private String documentID;
		private String gcas;
		private String errorCode;
		private String errorMessage;
		private String errorCustomMessage;
		private List<String> validationErrorMessages;
		
		/** 
		 * @about Constructor
		 * @since DSM 2018x.3
		 */
		public Builder() {
		}
		/** 
		 * @about Setter method to set list of validation messages
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setValidationErrorMessages(List<String> validationErrorMessages) {
			this.validationErrorMessages = validationErrorMessages;
			return this;
		}
		/** 
		 * @about Setter method to set artwork name
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setArtworkName(String artworkName) {
			this.artworkName = artworkName;
			return this;
		}
		/** 
		 * @about Setter method to set artwork id
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setArtworkId(String artworkId) {
			this.artworkId = artworkId;
			return this;
		}
		/** 
		 * @about Setter method to set document number
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setDocumentNumber(String documentNumber) {
			this.documentNumber = documentNumber;
			return this;
		}
		/** 
		 * @about Setter method to set document id
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setDocumentID(String documentID) {
			this.documentID = documentID;
			return this;
		}
		/** 
		 * @about Setter method to set gcas
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setGcas(String gcas) {
			this.gcas = gcas;
			return this;
		}
		/** 
		 * @about Setter method to set error code
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setErrorCode(String errorCode) {
			this.errorCode = errorCode;
			return this;
		}
		/** 
		 * @about Setter method to set error message
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
			return this;
		}
		/** 
		 * @about Setter method to set custom error message
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setCustomErrorMessage(String errorCustomMessage) {
			this.errorCustomMessage = errorCustomMessage;
			return this;
		}
		public ArtworkErrorMessage build() {
			return new ArtworkErrorMessage(this);
		}
	}
}
