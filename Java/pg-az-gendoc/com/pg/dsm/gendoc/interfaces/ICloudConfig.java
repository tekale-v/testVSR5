/*
 **   ICloudConfig.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Interface class
 **
 */
package com.pg.dsm.gendoc.interfaces;

public interface ICloudConfig {
    boolean isLoaded();

    void setLoaded(boolean loaded);

    String getConfigType();

    void setConfigType(String configType);

    String getConfigName();

    void setConfigName(String configName);

    String getConfigRevision();

    void setConfigRevision(String configRevision);

    String getConfigCurrentState();

    void setConfigCurrentState(String configCurrentState);

    String getConfigOid();

    void setConfigOid(String configOid);

    String getCloudBlobUploadScript();

    void setCloudBlobUploadScript(String cloudBlobUploadScript);

    String getCloudBlobDownloadScript();

    void setCloudBlobDownloadScript(String cloudBlobDownloadScript);

    String getCloudBlobStorageContainerName();

    void setCloudBlobStorageContainerName(String cloudBlobStorageContainerName);

    String getCloudEndpointsProtocolParameter();

    void setCloudEndpointsProtocolParameter(String cloudEndpointsProtocolParameter);

    String getCloudEndpointsProtocolValue();

    void setCloudEndpointsProtocolValue(String cloudEndpointsProtocolValue);

    String getCloudAccountNameParameter();

    void setCloudAccountNameParameter(String cloudAccountNameParameter);

    String getCloudAccountNameValue();

    void setCloudAccountNameValue(String cloudAccountNameValue);

    String getCloudAccountKeyParameter();

    void setCloudAccountKeyParameter(String cloudAccountKeyParameter);

    String getCloudAccountKeyValue();

    void setCloudAccountKeyValue(String cloudAccountKeyValue);

    String getFileExtensionsForCloud();

    void setFileExtensionsForCloud(String fileExtensionsForCloud);

    String getFileExtensionsForIText();

    void setFileExtensionsForIText(String fileExtensionsForIText);

    String getBlobUploadPath();

    void setBlobUploadPath(String blobUploadPath);

    String getBlobDownloadPath();

    void setBlobDownloadPath(String blobDownloadPath);

    String getLocalDownloadPath();

    void setLocalDownloadPath(String localDownloadPath);

    String getUseAzcopyOrSDK();

    void setUseAzcopyOrSDK(String useAzcopyOrSDK);

    int getSleepInterval();

    void setSleepInterval(int sleepInterval);

    int getTimer();

    void setTimer(int timer);

    String getCloudGenDocCustomLoggerConfigFilePath();

    void setCloudGenDocCustomLoggerConfigFilePath(String cloudGenDocCustomLoggerConfigFilePath);

	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433 - Starts
	String getCharactersNotAllowedForFileName();

	void setCharactersNotAllowedForFileName(String charactersNotAllowedForFileName);
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433 - Ends

	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	String getCharactersNotAllowedForObjectName();

	void setCharactersNotAllowedForObjectName(String charactersNotAllowedForObjectName);
	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

}
