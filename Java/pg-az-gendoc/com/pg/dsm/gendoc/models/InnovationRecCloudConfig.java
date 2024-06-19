/*
 **   InnovationRecCloudConfig.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class.
 **
 */
package com.pg.dsm.gendoc.models;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;
import matrix.db.Context;
import matrix.util.MatrixException;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InnovationRecCloudConfig implements ICloudConfig {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	Context context;
	boolean loaded;

	String configType;
	String configName;
	String configRevision;
	String configCurrentState;
	String configOid;

	String cloudBlobUploadScript;
	String cloudBlobDownloadScript;
	String cloudBlobStorageContainerName;
	String cloudEndpointsProtocolParameter;
	String cloudEndpointsProtocolValue;
	String cloudAccountNameParameter;
	String cloudAccountNameValue;
	String cloudAccountKeyParameter;
	String cloudAccountKeyValue;

	String fileExtensionsForCloud;
	String fileExtensionsForIText;
	String blobUploadPath;
	String blobDownloadPath;
	String localDownloadPath;
	String useAzcopyOrSDK;

	String cloudGenDocCustomLoggerConfigFilePath;

	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433 - Starts
	String charactersNotAllowedForFileName;
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433 - End

	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	String charactersNotAllowedForObjectName;
	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - End

	int sleepInterval;
	int timer;

	/**
	 * @param context
	 */
	public InnovationRecCloudConfig(Context context) {
		try {
			this.context = context;
			String cloudConfigOid = CloudGenDocUtil.getObjectId(context, CloudConstants.Basic.INNOVATION_RECORD_CLOUD_GEN_DOC_CONFIG_OBJECT.getValue());
			Map<String, String> objectMap = CloudGenDocUtil.getCloudConfigInfo(context, cloudConfigOid);

			if (null != objectMap) {
				setConfigOid(objectMap.get(DomainConstants.SELECT_ID));
				setConfigType(objectMap.get(DomainConstants.SELECT_TYPE));
				setConfigName(objectMap.get(DomainConstants.SELECT_NAME));
				setConfigRevision(objectMap.get(DomainConstants.SELECT_REVISION));
				setConfigCurrentState(objectMap.get(DomainConstants.SELECT_CURRENT));

				setCloudBlobStorageContainerName(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_BLOB_STORAGE_CONTAINER_NAME.getSelect(context)));
				setCloudEndpointsProtocolParameter(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_END_POINTS_PROTOCOL_PARAMETER.getSelect(context)));
				setCloudEndpointsProtocolValue(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_END_POINTS_PROTOCOL_VALUE.getSelect(context)));
				setCloudAccountNameParameter(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_NAME_PARAMETER.getSelect(context)));
				setCloudAccountNameValue(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_NAME_VALUE.getSelect(context)));
				setCloudAccountKeyParameter(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_KEY_PARAMETER.getSelect(context)));
				setCloudAccountKeyValue(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_KEY_VALUE.getSelect(context)));
				setFileExtensionsForCloud(objectMap.get(CloudConstants.Attribute.GEN_DOC_FILE_EXTENSIONS_FOR_CLOUD.getSelect(context)));
				setFileExtensionsForIText(objectMap.get(CloudConstants.Attribute.GEN_DOC_FILE_EXTENSIONS_FOR_ITEXT.getSelect(context)));
				setBlobUploadPath(objectMap.get(CloudConstants.Attribute.GEN_DOC_BLOB_UPLOAD_PATH.getSelect(context)));
				setBlobDownloadPath(objectMap.get(CloudConstants.Attribute.GEN_DOC_BLOB_DOWNLOAD_PATH.getSelect(context)));
				setLocalDownloadPath(objectMap.get(CloudConstants.Attribute.GEN_DOC_LOCAL_DOWNLOAD_PATH.getSelect(context)));
				setSleepInterval(Integer.parseInt(objectMap.get(CloudConstants.Attribute.GEN_DOC_SLEEP_INTERVAL.getSelect(context))));
				setTimer(Integer.parseInt(objectMap.get(CloudConstants.Attribute.GEN_DOC_TIMER.getSelect(context))));
				setCloudGenDocCustomLoggerConfigFilePath(objectMap.get(CloudConstants.Attribute.CLOUD_GEN_DOC_CUSTOM_LOGGER_CONFIG_FILE_PATH.getSelect(context)));

				// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433
				setCharactersNotAllowedForFileName(objectMap.get(CloudConstants.Attribute.CHARACTERS_NOT_ALLOWED_FOR_FILE_NAME.getSelect(context)));

				// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts.
				setCharactersNotAllowedForObjectName(objectMap.get(CloudConstants.Attribute.CHARACTERS_NOT_ALLOWED_FOR_OBJECT_NAME.getSelect(context)));
				// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends.
				this.loaded = true;
			}
		} catch (MatrixException e) {
			logger.log(Level.WARNING, e.getMessage());
			this.loaded = false;
		}
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	@Override
	public String getConfigType() {
		return configType;
	}

	@Override
	public void setConfigType(String configType) {
		this.configType = configType;
	}

	@Override
	public String getConfigName() {
		return configName;
	}

	@Override
	public void setConfigName(String configName) {
		this.configName = configName;
	}

	@Override
	public String getConfigRevision() {
		return configRevision;
	}

	@Override
	public void setConfigRevision(String configRevision) {
		this.configRevision = configRevision;
	}

	@Override
	public String getConfigCurrentState() {
		return configCurrentState;
	}

	@Override
	public void setConfigCurrentState(String configCurrentState) {
		this.configCurrentState = configCurrentState;
	}

	@Override
	public String getConfigOid() {
		return configOid;
	}

	@Override
	public void setConfigOid(String configOid) {
		this.configOid = configOid;
	}

	@Override
	public String getCloudBlobUploadScript() {
		return cloudBlobUploadScript;
	}

	public void setCloudBlobUploadScript(String cloudBlobUploadScript) {
		this.cloudBlobUploadScript = cloudBlobUploadScript;
	}

	@Override
	public String getCloudBlobDownloadScript() {
		return cloudBlobDownloadScript;
	}

	@Override
	public void setCloudBlobDownloadScript(String cloudBlobDownloadScript) {
		this.cloudBlobDownloadScript = cloudBlobDownloadScript;
	}

	@Override
	public String getCloudBlobStorageContainerName() {
		return cloudBlobStorageContainerName;
	}

	@Override
	public void setCloudBlobStorageContainerName(String cloudBlobStorageContainerName) {
		this.cloudBlobStorageContainerName = cloudBlobStorageContainerName;
	}

	@Override
	public String getCloudEndpointsProtocolParameter() {
		return cloudEndpointsProtocolParameter;
	}

	@Override
	public void setCloudEndpointsProtocolParameter(String cloudEndpointsProtocolParameter) {
		this.cloudEndpointsProtocolParameter = cloudEndpointsProtocolParameter;
	}

	@Override
	public String getCloudEndpointsProtocolValue() {
		return cloudEndpointsProtocolValue;
	}

	@Override
	public void setCloudEndpointsProtocolValue(String cloudEndpointsProtocolValue) {
		this.cloudEndpointsProtocolValue = cloudEndpointsProtocolValue;
	}

	@Override
	public String getCloudAccountNameParameter() {
		return cloudAccountNameParameter;
	}

	@Override
	public void setCloudAccountNameParameter(String cloudAccountNameParameter) {
		this.cloudAccountNameParameter = cloudAccountNameParameter;
	}

	@Override
	public String getCloudAccountNameValue() {
		return cloudAccountNameValue;
	}

	@Override
	public void setCloudAccountNameValue(String cloudAccountNameValue) {
		this.cloudAccountNameValue = cloudAccountNameValue;
	}

	@Override
	public String getCloudAccountKeyParameter() {
		return cloudAccountKeyParameter;
	}

	@Override
	public void setCloudAccountKeyParameter(String cloudAccountKeyParameter) {
		this.cloudAccountKeyParameter = cloudAccountKeyParameter;
	}

	@Override
	public String getCloudAccountKeyValue() {
		return cloudAccountKeyValue;
	}

	@Override
	public void setCloudAccountKeyValue(String cloudAccountKeyValue) {
		this.cloudAccountKeyValue = cloudAccountKeyValue;
	}

	@Override
	public String getFileExtensionsForCloud() {
		return fileExtensionsForCloud;
	}

	@Override
	public void setFileExtensionsForCloud(String fileExtensionsForCloud) {
		this.fileExtensionsForCloud = fileExtensionsForCloud;
	}

	@Override
	public String getFileExtensionsForIText() {
		return fileExtensionsForIText;
	}

	@Override
	public void setFileExtensionsForIText(String fileExtensionsForIText) {
		this.fileExtensionsForIText = fileExtensionsForIText;
	}

	@Override
	public String getBlobUploadPath() {
		return blobUploadPath;
	}

	@Override
	public void setBlobUploadPath(String blobUploadPath) {
		this.blobUploadPath = blobUploadPath;
	}

	@Override
	public String getBlobDownloadPath() {
		return blobDownloadPath;
	}

	@Override
	public void setBlobDownloadPath(String blobDownloadPath) {
		this.blobDownloadPath = blobDownloadPath;
	}

	@Override
	public String getLocalDownloadPath() {
		return localDownloadPath;
	}

	@Override
	public void setLocalDownloadPath(String localDownloadPath) {
		this.localDownloadPath = (UIUtil.isNotNullAndNotEmpty(localDownloadPath)) ? localDownloadPath.concat(File.separator) : System.getProperty(CloudConstants.Basic.IO_TEMP_DIRECTORY.getValue()).concat(File.separator);
	}

	@Override
	public String getUseAzcopyOrSDK() {
		return useAzcopyOrSDK;
	}

	@Override
	public void setUseAzcopyOrSDK(String useAzcopyOrSDK) {
		this.useAzcopyOrSDK = useAzcopyOrSDK;
	}

	@Override
	public int getSleepInterval() {
		return sleepInterval;
	}

	@Override
	public void setSleepInterval(int sleepInterval) {
		this.sleepInterval = sleepInterval;
	}

	@Override
	public int getTimer() {
		return timer;
	}

	@Override
	public void setTimer(int timer) {
		this.timer = timer;
	}

	@Override
	public String getCloudGenDocCustomLoggerConfigFilePath() {
		return cloudGenDocCustomLoggerConfigFilePath;
	}

	@Override
	public void setCloudGenDocCustomLoggerConfigFilePath(String cloudGenDocCustomLoggerConfigFilePath) {
		this.cloudGenDocCustomLoggerConfigFilePath = cloudGenDocCustomLoggerConfigFilePath;
	}

	@Override
	public String getCharactersNotAllowedForFileName() {
		return charactersNotAllowedForFileName;
	}

	@Override
	public void setCharactersNotAllowedForFileName(String charactersNotAllowedForFileName) {
		this.charactersNotAllowedForFileName = charactersNotAllowedForFileName;
	}

	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	@Override
	public String getCharactersNotAllowedForObjectName() {
		return this.charactersNotAllowedForObjectName;
	}

	@Override
	public void setCharactersNotAllowedForObjectName(String charactersNotAllowedForObjectName) {
		this.charactersNotAllowedForObjectName = charactersNotAllowedForObjectName;
	}// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
}
