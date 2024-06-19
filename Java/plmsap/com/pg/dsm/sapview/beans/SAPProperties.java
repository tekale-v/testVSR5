package com.pg.dsm.sapview.beans;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 *
 */
public class SAPProperties {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private String sapParentTypes;
	private String sapAlternateTypes;
	private String sapIntermediateTypes;
	private String sapOutputLogsPath;
	private boolean isPropertiesLoaded;
	private String sapFetchQueryTypes;
	private String sapFetchQueryWhere;
	private String sapInputFilePath;
	private String sapChildTypes;
	private String sapOutputLogsFileName;
	private String sapSubstituteNotAllowedTypes;
	/**
	 * @param properties
	 */
	public SAPProperties(Properties properties) {
		try {
			this.setPropertiesLoaded(Boolean.FALSE);
			this.setSapParentTypes(properties.getProperty("SAP_Parent_Types"));
			this.setSapAlternateTypes(properties.getProperty("SAP_Alternate_Types"));
			this.setSapIntermediateTypes(properties.getProperty("SAP_Intermediate_Types"));
			this.setSapOutputLogsPath(properties.getProperty("Output_Logs_Folder_Path"));
			this.setSapFetchQueryTypes(properties.getProperty("Fetch_Query_Types"));
			this.setSapFetchQueryWhere(properties.getProperty("Fetch_Query_Where"));
			this.setSapInputFilePath(properties.getProperty("Input_File_Path"));
			this.setSapChildTypes(properties.getProperty("SAP_Child_Types"));
			this.setSapOutputLogsFileName(properties.getProperty("Output_Logs_File_Name"));
			this.setSapSubstituteNotAllowedTypes(properties.getProperty("SAP_Not_Allowed_Substitute_Types"));
			this.setPropertiesLoaded(Boolean.TRUE);
		} catch (Exception e) {
			this.setPropertiesLoaded(Boolean.FALSE);
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * @return the sapParentTypes
	 */
	public String getSapParentTypes() {
		return sapParentTypes;
	}

	/**
	 * @param sapParentTypes the sapParentTypes to set
	 */
	public void setSapParentTypes(String sapParentTypes) {
		this.sapParentTypes = sapParentTypes;
	}

	/**
	 * @return the sapAlternateTypes
	 */
	public String getSapAlternateTypes() {
		return sapAlternateTypes;
	}

	/**
	 * @param sapAlternateTypes the sapAlternateTypes to set
	 */
	public void setSapAlternateTypes(String sapAlternateTypes) {
		this.sapAlternateTypes = sapAlternateTypes;
	}

	/**
	 * @return the sapIntermediateTypes
	 */
	public String getSapIntermediateTypes() {
		return sapIntermediateTypes;
	}

	/**
	 * @param sapIntermediateTypes the sapIntermediateTypes to set
	 */
	public void setSapIntermediateTypes(String sapIntermediateTypes) {
		this.sapIntermediateTypes = sapIntermediateTypes;
	}

	/**
	 * @return the sapOutputLogsPath
	 */
	public String getSapOutputLogsPath() {
		return sapOutputLogsPath;
	}

	/**
	 * @param sapOutputLogsPath the sapOutputLogsPath to set
	 */
	public void setSapOutputLogsPath(String sapOutputLogsPath) {
		this.sapOutputLogsPath = sapOutputLogsPath;
	}

	/**
	 * @return the isPropertiesLoaded
	 */
	public boolean isPropertiesLoaded() {
		return isPropertiesLoaded;
	}

	/**
	 * @param isPropertiesLoaded the isPropertiesLoaded to set
	 */
	public void setPropertiesLoaded(boolean isPropertiesLoaded) {
		this.isPropertiesLoaded = isPropertiesLoaded;
	}

	/**
	 * @return the sapFetchQueryTypes
	 */
	public String getSapFetchQueryTypes() {
		return sapFetchQueryTypes;
	}

	/**
	 * @param sapFetchQueryTypes the sapFetchQueryTypes to set
	 */
	public void setSapFetchQueryTypes(String sapFetchQueryTypes) {
		this.sapFetchQueryTypes = sapFetchQueryTypes;
	}

	/**
	 * @return the sapFetchQueryWhere
	 */
	public String getSapFetchQueryWhere() {
		return sapFetchQueryWhere;
	}

	/**
	 * @param sapFetchQueryWhere the sapFetchQueryWhere to set
	 */
	public void setSapFetchQueryWhere(String sapFetchQueryWhere) {
		this.sapFetchQueryWhere = sapFetchQueryWhere;
	}

	/**
	 * @return the sapInputFilePath
	 */
	public String getSapInputFilePath() {
		return sapInputFilePath;
	}

	/**
	 * @param sapInputFilePath the sapInputFilePath to set
	 */
	public void setSapInputFilePath(String sapInputFilePath) {
		this.sapInputFilePath = sapInputFilePath;
	}

	/**
	 * @return the sapChildTypes
	 */
	public String getSapChildTypes() {
		return sapChildTypes;
	}

	/**
	 * @param sapChildTypes the sapChildTypes to set
	 */
	public void setSapChildTypes(String sapChildTypes) {
		this.sapChildTypes = sapChildTypes;
	}

	/**
	 * @return the sapOutputLogsFileName
	 */
	public String getSapOutputLogsFileName() {
		return sapOutputLogsFileName;
	}

	/**
	 * @param sapOutputLogsFileName the sapOutputLogsFileName to set
	 */
	public void setSapOutputLogsFileName(String sapOutputLogsFileName) {
		this.sapOutputLogsFileName = sapOutputLogsFileName;
	}

	/**
	 * @return the sapSubstituteNotAllowedTypes
	 */
	public String getSapSubstituteNotAllowedTypes() {
		return sapSubstituteNotAllowedTypes;
	}

	/**
	 * @param sapSubstituteNotAllowedTypes the sapSubstituteNotAllowedTypes to set
	 */
	public void setSapSubstituteNotAllowedTypes(String sapSubstituteNotAllowedTypes) {
		this.sapSubstituteNotAllowedTypes = sapSubstituteNotAllowedTypes;
	}
	
}
