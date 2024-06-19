/*
 **   InnovationRec.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class.
 **
 */
package com.pg.dsm.gendoc.models;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.StringList;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class InnovationRec implements ICloudDocument {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
    String rootFolderPrefix;
    String objectNameForFile;
    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
    String workspace;
    String workspaceName;
    String absoluteCheckOutDir;
    String absoluteCheckOutDirName;
    String relativeUploadDirName;
    String relativeUploadSubDir;

    String objectOid;
    String objectType;
    String objectName;
    String objectRevision;
    String objectCurrentState;
    String objectPolicy;
    String title;
    String iPClassification;
    String taskApprovalDateLatest;

    String timeStamp;
    String locale;
    String language;
    Context context;
    String genDocErrorMessage;
    String approverColumn;
    String dateColumn;
    String approverRole;
    String signatureMeaning;
    ICloudConfig cloudConfig;
    String renderSoftwareInstalled;
    String tempFolderName;
    String ftpInputFolder;
    String ftpOutputFolder;
    String xmlFileName;
    String pdfFileName;
    String htmlFileName;
    String tempContentPdfFileName;


    String workDir;
    String workDirFile;
    String workDirFilePath;
    String absoluteDownloadDir;
    String absoluteMergeDir;
    String workDirBlobPath;
    String inWorkDir;

    String viewIdentifier;

    boolean loaded;

    /**
     * @param context
     * @param objectMap
     * @param cloudConfig
     */
    public InnovationRec(Context context, Map<String, String> objectMap, ICloudConfig cloudConfig) {
        // Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
        Instant startTime = Instant.now();
        try {
            this.context = context;
            this.cloudConfig = cloudConfig;
            setTimeStamp();
            setPropertyVariable();
            setDefaultFolders(objectMap);
            setTempContentPdfFileName();
            setObjectInfo(objectMap);
            // Added as part of 2018x.6 July-CW (Defect #) by DSM Sogeti - Start
            setObjectNameForFile();
            setRootFolderPrefix();
            // Added as part of 2018x.6 July-CW (Defect #) by DSM Sogeti - End

            setDirectories(objectMap);
            setTempWorkDirectories();
            setWorkDirBlobPath();
            setViewIdentifier(pgV3Constants.PDFVIEW_GENDOC);
            this.loaded = true;
        } catch (Exception e) {
            this.loaded = false;
            logger.error("InnovationRec instance failed");
            logger.error(e);
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info(String.format("Innovation Rec Instantiation - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
    }

    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
    private void setRootFolderPrefix() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CloudConstants.Basic.DATE_FORMAT_FOR_TIMESTAMP.getValue());
        StringBuilder uniqueNameBuilder = new StringBuilder();
	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	uniqueNameBuilder.append(refineObjectNameForBlob());
        // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
        uniqueNameBuilder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        uniqueNameBuilder.append(this.objectRevision);
        uniqueNameBuilder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        uniqueNameBuilder.append(getUUID());
        uniqueNameBuilder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        uniqueNameBuilder.append(simpleDateFormat.format(new Date()));
        this.rootFolderPrefix = uniqueNameBuilder.toString();
    }
    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	private String refineObjectNameForBlob() {
		final String characters = cloudConfig.getCharactersNotAllowedForObjectName();
		final StringList characterList = StringUtil.split(characters, CloudConstants.Basic.SYMBOL_COMMA.getValue());
		String objectNameForBlob = objectNameForFile;
		for(String character: characterList) {
			if(objectNameForBlob.contains(character)) {
				objectNameForBlob = objectNameForBlob.replace(character, DomainConstants.EMPTY_STRING);
			}
		}
		objectNameForBlob = objectNameForBlob.replace(CloudConstants.Basic.SYMBOL_COMMA.getValue(), DomainConstants.EMPTY_STRING);
                logger.info(String.format("Folder to be created with name: %s", objectNameForBlob));
		return objectNameForBlob;
	}
	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

    public void setWorkDirBlobPath() {
        StringBuilder builder = new StringBuilder(cloudConfig.getBlobUploadPath());
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
        builder.append(rootFolderPrefix);
        builder.append(CloudConstants.Basic.SYMBOL_FORWARD_SLASH.getValue());
        builder.append(objectOid);
        builder.append(CloudConstants.Basic.SYMBOL_FORWARD_SLASH.getValue());
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
        setWorkDirBlobPath(builder.toString());
    }

    public String getWorkDirBlobPath() {
        return workDirBlobPath;
    }

    public void setWorkDirBlobPath(String workDirBlobPath) {
        this.workDirBlobPath = workDirBlobPath;
    }

    private String buildTempWorkDir() {
        String property = CloudConstants.Basic.IO_TEMP_DIRECTORY.getValue();
        String ioTempDirectory = System.getProperty(property);
        StringBuilder builder = new StringBuilder(ioTempDirectory);
        builder.append(File.separator);
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
        builder.append(rootFolderPrefix);
        builder.append(File.separator);
        builder.append(objectOid);
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
        builder.append(File.separator);
        return builder.toString();
    }

    public String buildWorkDirFileName() {
        StringBuilder builder = new StringBuilder(buildTempWorkDir());
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
        builder.append(rootFolderPrefix);
        builder.append(CloudConstants.Basic.SYMBOL_DOT.getValue());
        builder.append(CloudConstants.Basic.FILE_EXTENSION_TXT.getValue());
        return builder.toString();
    }

    public void setTempWorkDirectories() {
        // Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
        boolean writable;
        boolean executable;
        boolean readable;
        boolean bIsFolderCreated;
        boolean bIsFileCreated;

        StringBuilder builder = new StringBuilder(buildTempWorkDir());
        File folder = new File(builder.toString());
        if (!folder.exists()) {
            bIsFolderCreated = folder.mkdirs();
            if (bIsFolderCreated) {
                writable = folder.setWritable(true, false);
                executable = folder.setExecutable(true, false);
                readable = folder.setReadable(true, false);
                logger.info(String.format("Permission RWX|%s|%s|%s", readable, writable, executable));
            } else {
                logger.error("Failed to create local temp Dir");
            }
        }
        if (folder.exists()) {
            File newFile = new File(buildWorkDirFileName());
            try {
                bIsFileCreated = newFile.createNewFile();
                if (bIsFileCreated) {
                    writable = newFile.setWritable(true, false);
                    executable = newFile.setExecutable(true, false);
                    readable = newFile.setReadable(true, false);
                    logger.info(String.format("Permission RWX|%s|%s|%s", readable, writable, executable));
                } else {
                    logger.error("Failed to create Work Dir Text File");
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            setWorkDirFile(newFile.getName());
            String downOutPath = newFile.getParentFile().getPath().concat(File.separator).concat(CloudConstants.Basic.DOWNLOAD_DIR_NAME.getValue()).concat(File.separator);
            File downloadDir = new File(downOutPath);
            if (!downloadDir.exists()) {
                bIsFolderCreated = downloadDir.mkdirs();
                if (bIsFolderCreated) {
                    writable = downloadDir.setWritable(true, false);
                    executable = downloadDir.setExecutable(true, false);
                    readable = downloadDir.setReadable(true, false);
                    logger.info(String.format("Permission RWX|%s|%s|%s", readable, writable, executable));
                } else {
                    logger.error("Failed to create Download Dir");
                }
                setAbsoluteDownloadDir(downOutPath);
            } else {
                setAbsoluteDownloadDir(downOutPath);
            }
            String mergePath = newFile.getParentFile().getPath().concat(File.separator).concat(CloudConstants.Basic.MERGE_DIR_NAME.getValue()).concat(File.separator);
            File mergeDir = new File(mergePath);
            if (!mergeDir.exists()) {
                bIsFolderCreated = mergeDir.mkdirs();
                if (bIsFolderCreated) {
                    writable = mergeDir.setWritable(true, false);
                    executable = mergeDir.setExecutable(true, false);
                    readable = mergeDir.setReadable(true, false);
                    logger.info(String.format("Permission RWX|%s|%s|%s", readable, writable, executable));
                } else {
                    logger.error("Failed to create Merge Dir");
                }
                setAbsoluteMergeDir(mergePath);
            } else {
                setAbsoluteMergeDir(mergePath);
            }

            String inWorkDirPath = newFile.getParentFile().getPath().concat(File.separator).concat(CloudConstants.Basic.IN_WORK_DIR_NAME.getValue()).concat(File.separator);
            File inWorkDirFolder = new File(inWorkDirPath);
            if (!inWorkDirFolder.exists()) {

                bIsFolderCreated = inWorkDirFolder.mkdirs();
                if (bIsFolderCreated) {
                    writable = inWorkDirFolder.setWritable(true, false);
                    executable = inWorkDirFolder.setExecutable(true, false);
                    readable = inWorkDirFolder.setReadable(true, false);
                    logger.info(String.format("Permission RWX|%s|%s|%s", readable, writable, executable));
                } else {
                    logger.error("Failed to create In-Work Dir");
                }
                setInWorkDir(inWorkDirPath);
            } else {
                setInWorkDir(inWorkDirPath);
            }
            setWorkDirFilePath(newFile.getPath());
            setWorkDir(newFile.getParentFile().getPath().concat(File.separator));
            // Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
            logger.info(String.format("Temp Work Dir: %s", getWorkDir()));
            logger.info(String.format("Characters to remove from File Name are: %s", cloudConfig.getCharactersNotAllowedForFileName()));
        }
    }

    @Override
    public String getWorkDir() {
        return workDir;
    }

    @Override
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    @Override
    public String getWorkDirFile() {
        return workDirFile;
    }

    @Override
    public void setWorkDirFile(String workDirFile) {
        this.workDirFile = workDirFile;
    }

    @Override
    public String getWorkDirFilePath() {
        return workDirFilePath;
    }

    @Override
    public void setWorkDirFilePath(String workDirFilePath) {
        this.workDirFilePath = workDirFilePath;
    }

    public String getAbsoluteMergeDir() {
        return absoluteMergeDir;
    }

    public void setAbsoluteMergeDir(String absoluteMergeDir) {
        this.absoluteMergeDir = absoluteMergeDir;
    }

    public void setTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CloudConstants.Basic.DATE_FORMAT_FOR_TIMESTAMP.getValue());
        this.timeStamp = simpleDateFormat.format(new Date());
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669.
        this.timeStamp = getUUID();
    }

    public void setPropertyVariable() throws FrameworkException {
        String componentsStringResourceValue = CloudConstants.Basic.COMPONENTS_STRING_RESOURCE.getValue();
        this.locale = context.getLocale().toString();
        this.language = context.getLocale().getLanguage();
        this.genDocErrorMessage = EnoviaResourceBundle.getProperty(context, CloudConstants.Basic.CPN_STRING_RESOURCE.getValue(), locale, "emxCPN.PDF.PDFGenError");
        this.approverColumn = EnoviaResourceBundle.getProperty(context, componentsStringResourceValue, locale, "emxComponents.PDFCover.ApproverColumn");
        this.dateColumn = EnoviaResourceBundle.getProperty(context, componentsStringResourceValue, locale, "emxComponents.PDF.DateColumn");
        this.approverRole = EnoviaResourceBundle.getProperty(context, CloudConstants.Basic.PROGRAM_CENTRAL_STRING_RESOURCE.getValue(), locale, "emxProgramCentral.CommonPeopleSearch.ApproverRole");
        this.signatureMeaning = EnoviaResourceBundle.getProperty(context, componentsStringResourceValue, locale, "emxComponents.Route.SignatureMeaning");
        this.renderSoftwareInstalled = EnoviaResourceBundle.getProperty(context, CloudConstants.Basic.CPN_RESOURCE.getValue(), locale, "emxCPN.RenderPDF");
    }

    public void setDefaultFolders(Map<String, String> objectMap) {
        this.tempFolderName = objectMap.get("tempFolderName");
        this.ftpInputFolder = objectMap.get("ftpInputFolder");
        this.ftpOutputFolder = objectMap.get("ftpOutputFolder");
        this.xmlFileName = objectMap.get("xmlFileName");
        this.htmlFileName = objectMap.get("htmlFileName");
        this.pdfFileName = objectMap.get("pdfFileName");
        this.taskApprovalDateLatest = objectMap.get("taskApprovalDateLatest");
    }

    public void setObjectInfo(Map<String, String> objectMap) {
        this.objectOid = objectMap.get(DomainConstants.SELECT_ID);
        this.objectType = objectMap.get(DomainConstants.SELECT_TYPE);
        this.objectName = objectMap.get(DomainConstants.SELECT_NAME);
        this.objectRevision = objectMap.get(DomainConstants.SELECT_REVISION);
        this.objectCurrentState = objectMap.get(DomainConstants.SELECT_CURRENT);
        this.objectPolicy = objectMap.get(DomainConstants.SELECT_POLICY);
        this.title = objectMap.get("attribute[" + DomainConstants.ATTRIBUTE_TITLE + "]");
        this.iPClassification = objectMap.get("iPClassification");
        this.title = objectMap.get("docTitle");
    }

    public void setDirectories(Map<String, String> objectMap) {
        this.workspace = objectMap.get("workspace");
        File file = new File(workspace);
        this.workspaceName = file.getName();
        String path = file.getPath();
        String oid = objectMap.get(DomainConstants.SELECT_ID);
        // in case of irm - files are checkout directly in workspace. it does not create a object-id folder.
        this.absoluteCheckOutDir = path.concat(File.separator).concat(File.separator);
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
        this.absoluteCheckOutDirName = oid;
        this.relativeUploadDirName = rootFolderPrefix.concat(pgV3Constants.SYMBOL_UNDERSCORE).concat(timeStamp);
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
        this.relativeUploadSubDir = relativeUploadDirName.concat(File.separator).concat(oid).concat(File.separator);
    }

    @Override
    public String getApproverColumn() {
        return approverColumn;
    }

    @Override
    public void setApproverColumn(String approverColumn) {
        this.approverColumn = approverColumn;
    }

    @Override
    public String getDateColumn() {
        return dateColumn;
    }

    @Override
    public void setDateColumn(String dateColumn) {
        this.dateColumn = dateColumn;
    }

    @Override
    public String getApproverRole() {
        return approverRole;
    }

    @Override
    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }

    @Override
    public String getSignatureMeaning() {
        return signatureMeaning;
    }

    @Override
    public void setSignatureMeaning(String signatureMeaning) {
        this.signatureMeaning = signatureMeaning;
    }

    @Override
    public String getTaskApprovalDateLatest() {
        return taskApprovalDateLatest;
    }

    @Override
    public void getTaskApprovalDateLatest(String taskApprovalDateLatest) {
        this.taskApprovalDateLatest = taskApprovalDateLatest;
    }

    @Override
    public String getTempFolderName() {
        return tempFolderName;
    }

    @Override
    public void getTempFolderName(String tempFolderName) {
        this.tempFolderName = tempFolderName;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public ICloudConfig getCloudConfig() {
        return cloudConfig;
    }

    @Override
    public void setCloudConfig(ICloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }

    @Override
    public String getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String getAbsoluteCheckOutDir() {
        return absoluteCheckOutDir;
    }

    @Override
    public void setAbsoluteCheckOutDir(String absoluteCheckOutDir) {
        this.absoluteCheckOutDir = absoluteCheckOutDir;
    }

    @Override
    public String getAbsoluteCheckOutDirName() {
        return absoluteCheckOutDirName;
    }

    @Override
    public void setAbsoluteCheckOutDirName(String absoluteCheckOutDirName) {
        this.absoluteCheckOutDirName = absoluteCheckOutDirName;
    }

    @Override
    public String getRelativeUploadDirName() {
        return relativeUploadDirName;
    }

    @Override
    public void setRelativeUploadDirName(String relativeUploadDirName) {
        this.relativeUploadDirName = relativeUploadDirName;
    }

    @Override
    public String getRelativeUploadSubDir() {
        return relativeUploadSubDir;
    }

    @Override
    public void setRelativeUploadSubDir(String relativeUploadSubDir) {
        this.relativeUploadSubDir = relativeUploadSubDir;
    }

    @Override
    public String getAbsoluteDownloadDir() {
        return absoluteDownloadDir;
    }

    @Override
    public void setAbsoluteDownloadDir(String absoluteDownloadDir) {
        this.absoluteDownloadDir = absoluteDownloadDir;
    }

    @Override
    public String getObjectOid() {
        return objectOid;
    }

    @Override
    public void setObjectOid(String objectOid) {
        this.objectOid = objectOid;
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    @Override
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    @Override
    public String getObjectRevision() {
        return objectRevision;
    }

    @Override
    public void setObjectRevision(String objectRevision) {
        this.objectRevision = objectRevision;
    }

    @Override
    public String getObjectCurrentState() {
        return objectCurrentState;
    }

    @Override
    public void setObjectCurrentState(String objectCurrentState) {
        this.objectCurrentState = objectCurrentState;
    }

    @Override
    public String getObjectPolicy() {
        return objectPolicy;
    }

    @Override
    public void setObjectPolicy(String objectPolicy) {
        this.objectPolicy = objectPolicy;
    }

    @Override
    public String getOriginator() {
        return null;
    }

    @Override
    public void setOriginator(String originator) {
        // do nothing
    }

    @Override
    public String getProject() {
        return null;
    }

    @Override
    public void setProject(String project) {
        // do nothing
    }

    @Override
    public String getOrganization() {
        return null;
    }

    @Override
    public void setOrganization(String organization) {
        // do nothing
    }

    @Override
    public String getAssemblyType() {
        return null;
    }

    @Override
    public void setAssemblyType(String assemblyType) {
        // do nothing
    }

    @Override
    public String getAuthoringApplication() {
        return null;
    }

    @Override
    public void setAuthoringApplication(String authoringApplication) {
        // do nothing
    }

    @Override
    public String getiPClassification() {
        return iPClassification;
    }

    @Override
    public void setiPClassification(String iPClassification) {
        this.iPClassification = iPClassification;
    }

    @Override
    public String getSecurityStatus() {
        return null;
    }

    @Override
    public void setSecurityStatus(String securityStatus) {
        // do nothing
    }

    @Override
    public String getEffectivityDate() {
        return null;
    }

    @Override
    public void setEffectivityDate(String effectivityDate) {
        // do nothing
    }

    @Override
    public String getExpirationDate() {
        return null;
    }

    @Override
    public void setExpirationDate(String expirationDate) {
        // do nothing
    }

    @Override
    public String getReleaseDate() {
        return null;
    }

    @Override
    public void setReleaseDate(String releaseDate) {
        // do nothing
    }

    @Override
    public String getApprovedDate() {
        return null;
    }

    @Override
    public void setApprovedDate(String approvedDate) {
        // do nothing
    }

    @Override
    public String getArchivedDate() {
        return null;
    }

    @Override
    public void setArchivedDate(String archivedDate) {
        // do nothing
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSapType() {
        return null;
    }

    @Override
    public void setSapType(String sapType) {
        // do nothing
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public void setStatus(String status) {
        // do nothing
    }

    @Override
    public String getOriginatingSource() {
        return null;
    }

    @Override
    public void setOriginatingSource(String originatingSource) {
        // do nothing
    }

    @Override
    public String getProductViewForm() {
        return null;
    }

    @Override
    public void setProductViewForm(String productViewForm) {
        // do nothing
    }

    @Override
    public String getProductRenderLanguage() {
        return null;
    }

    @Override
    public void setProductRenderLanguage(String productRenderLanguage) {
        // do nothing
    }

    @Override
    public String getViewIdentifier() {
        return viewIdentifier;
    }

    @Override
    public void setViewIdentifier(String viewIdentifier) {
        this.viewIdentifier = viewIdentifier;
    }

    @Override
    public String getContextUser() {
        return null;
    }

    @Override
    public void setContextUser(String contextUser) {
        // do nothing
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getProductDataTypes() {
        return null;
    }

    @Override
    public void setProductDataTypes(String productDataTypes) {
        // do nothing
    }

    @Override
    public String getUnStructuredTypes() {
        return null;
    }

    @Override
    public void setUnStructuredTypes(String unStructuredTypes) {
        // do nothing
    }

    @Override
    public String getRenderSoftwareInstalled() {
        return renderSoftwareInstalled;
    }

    @Override
    public void setRenderSoftwareInstalled(String renderSoftwareInstalled) {
        this.renderSoftwareInstalled = renderSoftwareInstalled;
    }

    @Override
    public String getFtpNeeded() {
        return null;
    }

    @Override
    public void setFtpNeeded(String ftpNeeded) {
        // do nothing
    }

    @Override
    public String getGenDocErrorMessage() {
        return genDocErrorMessage;
    }

    @Override
    public void setGenDocErrorMessage(String genDocErrorMessage) {
        this.genDocErrorMessage = genDocErrorMessage;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public void setFileName(String fileName) {
        // do nothing
    }

    @Override
    public String getObjectNameForFile() {
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
        return this.objectNameForFile;
    }

    @Override
    public void setObjectNameForFile(String objectNameForFile) {
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669.
        this.objectNameForFile = objectNameForFile;
    }

    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
    public void setObjectNameForFile() {
        setObjectNameForFile(objectName.replaceAll(pgV3Constants.SYMBOL_SPACE, DomainConstants.EMPTY_STRING));
    }
    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

    @Override
    public String getXmlFileName() {
        return xmlFileName;
    }

    @Override
    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    @Override
    public String getPdfFileName() {
        return pdfFileName;
    }

    @Override
    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    @Override
    public String getHtmlFileName() {
        return htmlFileName;
    }

    @Override
    public void setHtmlFileName(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }

    @Override
    public String getOutputPdfFileName() {
        return null;
    }

    @Override
    public void setOutputPdfFileName(String outputPdfFileName) {
        // do nothing
    }

    @Override
    public String getTempContentPdfFileName() {
        return tempContentPdfFileName;
    }

    @Override
    public void setTempContentPdfFileName(String tempContentPdfFileName) {
        this.tempContentPdfFileName = tempContentPdfFileName;
    }

    public void setTempContentPdfFileName() {
        setTempContentPdfFileName(FilenameUtils.removeExtension(pdfFileName).concat("TempContentFile").concat(CloudConstants.Basic.SYMBOL_UNDERSCORE.getValue()).concat(timeStamp).concat(".pdf"));
    }

    @Override
    public boolean isGenDoc() {
        return false;
    }

    @Override
    public void setGenDoc(boolean genDoc) {
        // do nothing
    }

    @Override
    public boolean isGenDocStructured() {
        return false;
    }

    @Override
    public void setGenDocStructured(boolean genDocStructured) {
        // do nothing
    }

    @Override
    public boolean isGenDocUnStructured() {
        return false;
    }

    @Override
    public void setGenDocUnStructured(boolean genDocUnStructured) {
        // do nothing
    }

    @Override
    public boolean isGenDocMEPorSEP() {
        return false;
    }

    @Override
    public void setGenDocMEPorSEP(boolean genDocMEPorSEP) {
        // do nothing
    }

    @Override
    public boolean isATSorPOA() {
        return false;
    }

    @Override
    public void setATSorPOA(boolean isATSorPOA) {
        // do nothing
    }

    public String getInWorkDir() {
        return inWorkDir;
    }

    public void setInWorkDir(String inWorkDir) {
        this.inWorkDir = inWorkDir;
    }

    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
    @Override
    public String getRootFolderPrefix() {
        return rootFolderPrefix;
    }

    @Override
    public void setRootFolderPrefix(String rootFolderPrefix) {
        this.rootFolderPrefix = rootFolderPrefix;
    }

    public String getUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
    // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
}
