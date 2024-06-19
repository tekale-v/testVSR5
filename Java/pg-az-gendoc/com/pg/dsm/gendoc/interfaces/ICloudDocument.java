/*
 **   ICloudDocument.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Interface class
 **
 */
package com.pg.dsm.gendoc.interfaces;

public interface ICloudDocument {
    String getApproverColumn();

    void setApproverColumn(String approverColumn);

    String getDateColumn();

    void setDateColumn(String dateColumn);

    String getApproverRole();

    void setApproverRole(String approverRole);

    String getSignatureMeaning();

    void setSignatureMeaning(String signatureMeaning);

    String getTaskApprovalDateLatest();

    void getTaskApprovalDateLatest(String taskApprovalDateLatest);

    String getTempFolderName();

    void getTempFolderName(String tempFolderName);

    boolean isLoaded();

    String getTimeStamp();

    void setTimeStamp(String timeStamp);

    ICloudConfig getCloudConfig();

    void setCloudConfig(ICloudConfig cloudConfig);

    String getWorkspace();

    void setWorkspace(String workspace);

    String getWorkspaceName();

    void setWorkspaceName(String workspaceName);

    String getAbsoluteCheckOutDir();

    void setAbsoluteCheckOutDir(String absoluteCheckOutDir);

    String getAbsoluteCheckOutDirName();

    void setAbsoluteCheckOutDirName(String absoluteCheckOutDirName);

    String getRelativeUploadDirName();

    void setRelativeUploadDirName(String relativeUploadDirName);

    String getRelativeUploadSubDir();

    void setRelativeUploadSubDir(String relativeUploadSubDir);

    String getAbsoluteDownloadDir();

    void setAbsoluteDownloadDir(String absoluteDownloadDir);

    String getObjectOid();

    void setObjectOid(String objectOid);

    String getObjectType();

    void setObjectType(String objectType);

    String getObjectName();

    void setObjectName(String objectName);

    String getObjectRevision();

    void setObjectRevision(String objectRevision);

    String getObjectCurrentState();

    void setObjectCurrentState(String objectCurrentState);

    String getObjectPolicy();

    void setObjectPolicy(String objectPolicy);

    String getOriginator();

    void setOriginator(String originator);

    String getProject();

    void setProject(String project);

    String getOrganization();

    void setOrganization(String organization);

    String getAssemblyType();

    void setAssemblyType(String assemblyType);

    String getAuthoringApplication();

    void setAuthoringApplication(String authoringApplication);

    String getiPClassification();

    void setiPClassification(String iPClassification);

    String getSecurityStatus();

    void setSecurityStatus(String securityStatus);

    String getEffectivityDate();

    void setEffectivityDate(String effectivityDate);

    String getExpirationDate();

    void setExpirationDate(String expirationDate);

    String getReleaseDate();

    void setReleaseDate(String releaseDate);

    String getApprovedDate();

    void setApprovedDate(String approvedDate);

    String getArchivedDate();

    void setArchivedDate(String archivedDate);

    String getTitle();

    void setTitle(String title);

    String getSapType();

    void setSapType(String sapType);

    String getStatus();

    void setStatus(String status);

    String getOriginatingSource();

    void setOriginatingSource(String originatingSource);

    String getProductViewForm();

    void setProductViewForm(String productViewForm);

    String getProductRenderLanguage();

    void setProductRenderLanguage(String productRenderLanguage);

    String getViewIdentifier();

    void setViewIdentifier(String viewIdentifier);

    String getContextUser();

    void setContextUser(String contextUser);

    String getLocale();

    void setLocale(String locale);

    String getLanguage();

    void setLanguage(String language);

    String getProductDataTypes();

    void setProductDataTypes(String productDataTypes);

    String getUnStructuredTypes();

    void setUnStructuredTypes(String unStructuredTypes);

    String getRenderSoftwareInstalled();

    void setRenderSoftwareInstalled(String renderSoftwareInstalled);

    String getFtpNeeded();

    void setFtpNeeded(String ftpNeeded);

    String getGenDocErrorMessage();

    void setGenDocErrorMessage(String genDocErrorMessage);

    String getFileName();

    void setFileName(String fileName);

    String getObjectNameForFile();

    void setObjectNameForFile(String objectNameForFile);

    String getXmlFileName();

    void setXmlFileName(String xmlFileName);

    String getPdfFileName();

    void setPdfFileName(String pdfFileName);

    String getHtmlFileName();

    void setHtmlFileName(String htmlFileName);

    String getOutputPdfFileName();

    void setOutputPdfFileName(String outputPdfFileName);

    String getTempContentPdfFileName();

    void setTempContentPdfFileName(String tempContentPdfFileName);

    boolean isGenDoc();

    void setGenDoc(boolean genDoc);

    boolean isGenDocStructured();

    void setGenDocStructured(boolean genDocStructured);

    boolean isGenDocUnStructured();

    void setGenDocUnStructured(boolean genDocUnStructured);

    boolean isGenDocMEPorSEP();

    void setGenDocMEPorSEP(boolean genDocMEPorSEP);

    boolean isATSorPOA();

    void setATSorPOA(boolean isATSorPOA);

    public String getWorkDir();

    public void setWorkDir(String workDir);

    public String getWorkDirFile();

    public void setWorkDirFile(String workDirFile);

    public String getWorkDirFilePath();

    public void setWorkDirFilePath(String workDirFilePath);

    public String getAbsoluteMergeDir();

    public void setAbsoluteMergeDir(String workDirFilePath);

    public String getWorkDirBlobPath();

    public void setWorkDirBlobPath(String workDirBlobPath);

    public String getInWorkDir();

    public void setInWorkDir(String inWorkDir);

	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
	String getRootFolderPrefix();

	void setRootFolderPrefix(String rootFolderPrefix);
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

}
