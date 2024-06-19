/*
 **   DigitalSpec.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class.
 **
 */
package com.pg.dsm.gendoc.models;

import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class DigitalSpec implements ICloudDocument {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
	String rootFolderPrefix;
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - End
	boolean isATSorPOA;
	boolean genDoc;
	boolean genDocStructured;
	boolean genDocUnStructured;
	boolean genDocMEPorSEP;
	String timeStamp;
	ICloudConfig cloudConfig;
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
    String originator;
    String project;
    String organization;
    String assemblyType;
    String authoringApplication;
    String iPClassification;
    String securityStatus;
    String effectivityDate;
    String expirationDate;
    String releaseDate;
    String approvedDate;
    String archivedDate;
    String title;
    String sapType;
    String status;
    String originatingSource;
    String productViewForm;
    String productRenderLanguage;
    String viewIdentifier;
    String contextUser;
    String locale;
    String language;
    String productDataTypes;
    String unStructuredTypes;
    String renderSoftwareInstalled;
    String ftpNeeded;
    String genDocErrorMessage;
    String fileName;
    String tempFolderName;
    String objectNameForFile;
    String xmlFileName;
    String pdfFileName;
    String htmlFileName;
    String outputPdfFileName;
    String tempContentPdfFileName;

    String workDir;
    String workDirFile;
    String workDirFilePath;
    String absoluteDownloadDir;
    String absoluteMergeDir;
    String workDirBlobPath;

    String inWorkDir;

    Context context;
    boolean loaded;

	/**
	 * @param context
	 * @param objectMap
	 * @param cloudConfig
	 */
	public DigitalSpec(Context context, Map<String, String> objectMap, ICloudConfig cloudConfig) {
		// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
		Instant startTime = Instant.now();
		try {
			this.context = context;
			this.cloudConfig = cloudConfig;
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			this.rootFolderPrefix = refineObjectNameForBlob(objectMap.get(CloudConstants.Basic.GEN_DOC_BLOB_ROOT_FOLDER_NAME.getValue()));
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
			setContextUser(objectMap.get(CloudConstants.Basic.INPUT_CONTEXT_USER.getValue()));
			setViewIdentifier(objectMap.get(CloudConstants.Basic.INPUT_VIEW_IDENTIFIER.getValue()));
			setTimeStamp();
			setObjectInfo(objectMap);

            setPropertyVariable();
            setWorkspace();
            setDirectories(objectMap);
            setTempFolderName();

            // custom identifier.
            setObjectNameForFile();
            setFileName(objectMap);
            setXmlFileName();
            setPdfFileName();
            setHtmlFileName();
            setOutputPdfFileName();

            setGenDoc();
            setGenDocUnStructured();
            setGenDocStructured();
            setATSorPOA();
            setGenDocMEPorSEP();
            setTempContentPdfFileName();

            setTempWorkDirectories();
            setWorkDirBlobPath();
            this.loaded = true;
        } catch (Exception e) {
            this.loaded = false;
            logger.info("DigitalSpec instance fail to load");
            logger.error(e);
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info(String.format("Digital Spec Instantiation - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
    }

    /**
     * @param objectMap
     */
    public void setObjectInfo(Map<String, String> objectMap) {
        // basics.
        setObjectOid(objectMap.get(DomainConstants.SELECT_ID));
        setObjectType(objectMap.get(DomainConstants.SELECT_TYPE));
        setObjectName(objectMap.get(DomainConstants.SELECT_NAME));
        setObjectRevision(objectMap.get(DomainConstants.SELECT_REVISION));
        setObjectCurrentState(objectMap.get(DomainConstants.SELECT_CURRENT));
        setObjectPolicy(objectMap.get(DomainConstants.SELECT_POLICY));
        setOriginator(objectMap.get(DomainConstants.SELECT_ORIGINATOR));
        setProject(objectMap.get(DomainConstants.SELECT_PROJECT));
        setOrganization(objectMap.get(DomainConstants.SELECT_ORGANIZATION));
        // attributes.
        setAssemblyType(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));
        setAuthoringApplication(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION));
        setProductViewForm(objectMap.get("attribute[" + CPNCommonConstants.ATTRIBUTE_PRODUCT_DATA_VIEW_FORMNAME + "]"));
        setProductRenderLanguage(objectMap.get("attribute[" + CPNCommonConstants.ATTRIBUTE_PRODUCT_DATA_RENDER_LANGUAGE + "]"));

		setiPClassification(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION));
		setSecurityStatus(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSECURITYSTATUS));
		setEffectivityDate(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE));
		setExpirationDate(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE));
		setReleaseDate(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
		setApprovedDate(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_APPROVED_DATE));
		setArchivedDate(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE));
		setTitle(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
		setSapType(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE));
		setStatus(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS));
		setOriginatingSource(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE));
	}

	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	private String refineObjectNameForBlob(String blobName) {
		String characters = cloudConfig.getCharactersNotAllowedForObjectName();
		StringList characterList = StringUtil.split(characters, CloudConstants.Basic.SYMBOL_COMMA.getValue());
		for (String character : characterList) {
			if (blobName.contains(character)) {
				blobName = blobName.replace(character, DomainConstants.EMPTY_STRING);
			}
		}
		blobName = blobName.replace(CloudConstants.Basic.SYMBOL_COMMA.getValue(), DomainConstants.EMPTY_STRING);
		logger.info(String.format("Folder to be created with name: %s", blobName));
		return blobName;
	}
	// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

	public void setWorkDirBlobPath() {
		StringBuilder builder = new StringBuilder(cloudConfig.getBlobUploadPath());
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
		builder.append(rootFolderPrefix);
		builder.append(CloudConstants.Basic.SYMBOL_FORWARD_SLASH.getValue());
		builder.append(objectOid);
		builder.append(CloudConstants.Basic.SYMBOL_FORWARD_SLASH.getValue());
		setWorkDirBlobPath(builder.toString());
	}

    public String getWorkDirBlobPath() {
        return workDirBlobPath;
    }

    public void setWorkDirBlobPath(String workDirBlobPath) {
        this.workDirBlobPath = workDirBlobPath;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getWorkDirFile() {
        return workDirFile;
    }

    public void setWorkDirFile(String workDirFile) {
        this.workDirFile = workDirFile;
    }

    public String getWorkDirFilePath() {
        return workDirFilePath;
    }

    public void setWorkDirFilePath(String workDirFilePath) {
        this.workDirFilePath = workDirFilePath;
    }

    @Override
    public String getAbsoluteDownloadDir() {
        return absoluteDownloadDir;
    }

    @Override
    public void setAbsoluteDownloadDir(String absoluteDownloadDir) {
        this.absoluteDownloadDir = absoluteDownloadDir;
    }

    public String getAbsoluteMergeDir() {
        return absoluteMergeDir;
    }

    public void setAbsoluteMergeDir(String absoluteMergeDir) {
        this.absoluteMergeDir = absoluteMergeDir;
    }

	private String buildTempWorkSubDir() {
		String property = CloudConstants.Basic.IO_TEMP_DIRECTORY.getValue();
		String ioTempDirectory = System.getProperty(property);
		StringBuilder builder = new StringBuilder(ioTempDirectory);
		builder.append(File.separator);
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		builder.append(rootFolderPrefix);
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - End
		builder.append(File.separator);
		builder.append(objectOid);
		builder.append(File.separator);
		return builder.toString();
	}

	public String buildWorkDirFileName() {
		StringBuilder builder = new StringBuilder(buildTempWorkSubDir());
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		builder.append(rootFolderPrefix);
		if (!rootFolderPrefix.startsWith(CloudConstants.Basic.KEYWORD_PART.getValue())) {
			builder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
			builder.append(objectNameForFile);
			builder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
			builder.append(objectRevision.replaceAll(pgV3Constants.SYMBOL_SPACE, DomainConstants.EMPTY_STRING));
		}
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
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

        String sWorkDirFilePath = buildWorkDirFileName();
        StringBuilder builder = new StringBuilder(buildTempWorkSubDir());
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
            File newFile = new File(sWorkDirFilePath);
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
            String downloadOutPath = newFile.getParentFile().getPath().concat(File.separator).concat(CloudConstants.Basic.DOWNLOAD_DIR_NAME.getValue()).concat(File.separator);
            File downloadDir = new File(downloadOutPath);
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

                setAbsoluteDownloadDir(downloadOutPath);
            } else {
                setAbsoluteDownloadDir(downloadOutPath);
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

	public void setBlobReferenceFolderTextFilePath() {
		String property = CloudConstants.Basic.IO_TEMP_DIRECTORY.getValue();
		String ioTempDirectory = System.getProperty(property);
		StringBuilder builder = new StringBuilder(ioTempDirectory);
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		builder.append(rootFolderPrefix);
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
		builder.append(File.separator);
		builder.append(objectOid);
		builder.append(File.separator);
		builder.append(workspaceName);
		builder.append(CloudConstants.Basic.SYMBOL_UNDERSCORE);
		builder.append(timeStamp);
		builder.append(CloudConstants.Basic.SYMBOL_DOT);
		builder.append(CloudConstants.Basic.FILE_EXTENSION_TXT);
	}

    @Override
    public String getApproverColumn() {
        return null;
    }

    @Override
    public void setApproverColumn(String approverColumn) {
        // do nothing
    }

    @Override
    public String getDateColumn() {
        return null;
    }

    @Override
    public void setDateColumn(String dateColumn) {
        // do nothing
    }

    @Override
    public String getApproverRole() {
        return null;
    }

    @Override
    public void setApproverRole(String approverRole) {
        // do nothing
    }

    @Override
    public String getSignatureMeaning() {
        return null;
    }

    @Override
    public void setSignatureMeaning(String signatureMeaning) {
        // do nothing
    }

    @Override
    public String getTaskApprovalDateLatest() {
        return null;
    }

    @Override
    public void getTaskApprovalDateLatest(String taskApprovalDateLatest) {
        // do nothing
    }

    @Override
    public String getTempFolderName() {
        return null;
    }

    @Override
    public void getTempFolderName(String tempFolderName) {
        // do nothing
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setATSorPOA() {
        boolean bExpressionResult = pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equalsIgnoreCase(objectType) || pgV3Constants.TYPE_PGARTWORK.equals(objectType);
        setATSorPOA(bExpressionResult);
    }

    public void setTempContentPdfFileName() {
        setTempContentPdfFileName(objectNameForFile.concat(CloudConstants.Basic.TEMP_PDF_FILE_NAME.getValue()));
    }

    public void setGenDocMEPorSEP() {
        boolean bExpressionResult = (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(objectPolicy)
                || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(objectPolicy))
                && pgV3Constants.PDFVIEW_GENDOC.equals(viewIdentifier);
        setGenDocMEPorSEP(bExpressionResult);
    }

    public void setGenDocStructured() {
        boolean bExpressionResult = (pgV3Constants.PDFVIEW_GENDOC.equals(viewIdentifier) && !(unStructuredTypes.contains(objectType)
                && !(objectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))) && (!((pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(objectPolicy)
                || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(objectPolicy))
                && (pgV3Constants.PDFVIEW_GENDOC.equals(viewIdentifier))));

        setGenDocStructured(bExpressionResult);
    }

    public void setGenDocUnStructured() {
        boolean bExpressionResult = pgV3Constants.PDFVIEW_GENDOC.equals(viewIdentifier)
                && ((unStructuredTypes.contains(objectType)
                && !objectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))
                || (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(objectPolicy))
                || (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(objectPolicy)))
                && !"".equalsIgnoreCase(fileName) && null != fileName;
        setGenDocUnStructured(bExpressionResult);
    }

    public void setGenDoc() {
        boolean bExpressionResult = pgV3Constants.PDFVIEW_GENDOC.equals(viewIdentifier) && ((productDataTypes.contains(objectType) || unStructuredTypes.contains(objectType))
                && !objectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))
                || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(objectPolicy)
                || pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(objectPolicy);
        setGenDoc(bExpressionResult);
    }

    public void setObjectNameForFile() {
        this.objectNameForFile = objectName.replaceAll(pgV3Constants.SYMBOL_SPACE, DomainConstants.EMPTY_STRING);
    }

    public void setOutputPdfFileName() {
        StringBuilder builder = new StringBuilder();
        builder.append(objectNameForFile);
        builder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        builder.append(CloudConstants.Basic.KEYWORD_REV.getValue());
        builder.append(objectRevision);
        builder.append(CloudConstants.Basic.DOT_FILE_EXTENSION_PDF.getValue());
        setOutputPdfFileName(builder.toString());
    }

    public void setHtmlFileName() {
        StringBuilder builder = new StringBuilder();
        builder.append(objectNameForFile);
        builder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        builder.append(CloudConstants.Basic.KEYWORD_REV.getValue());
        builder.append(objectRevision);
        builder.append(CloudConstants.Basic.DOT_FILE_EXTENSION_HTM.getValue());
        setHtmlFileName(builder.toString());
    }

    public void setPdfFileName() {
        StringBuilder builder = new StringBuilder();
        builder.append(objectNameForFile);
        builder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        builder.append(CloudConstants.Basic.KEYWORD_REV.getValue());
        builder.append(objectRevision);
        builder.append(CloudConstants.Basic.DOT_FILE_EXTENSION_PDF.getValue());
        setPdfFileName(builder.toString());
    }

    public void setXmlFileName() {
        StringBuilder builder = new StringBuilder();
        builder.append(objectNameForFile);
        builder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
        builder.append(CloudConstants.Basic.KEYWORD_REV.getValue());
        builder.append(objectRevision);
        builder.append(CloudConstants.Basic.DOT_FILE_EXTENSION_XML.getValue());
        setXmlFileName(builder.toString());
    }

	public void setTimeStamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CloudConstants.Basic.DATE_FORMAT_FOR_TIMESTAMP.getValue());
		this.timeStamp = simpleDateFormat.format(new Date());
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669.
		this.timeStamp = getUUID();
	}


	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
	public String getUUID() {
		return java.util.UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

    public void setWorkspace() throws MatrixException {
        this.workspace = context.createWorkspace();
    }

	public void setDirectories(Map<String, String> objectMap) {
		File file = new File(workspace);
		this.workspaceName = file.getName();
		String path = file.getPath();
		String oid = objectMap.get(DomainConstants.SELECT_ID);
		this.absoluteCheckOutDir = path.concat(File.separator).concat(oid).concat(File.separator);
		this.absoluteCheckOutDirName = oid;
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		this.relativeUploadDirName = rootFolderPrefix.concat(pgV3Constants.SYMBOL_UNDERSCORE).concat(timeStamp);
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
		this.relativeUploadSubDir = relativeUploadDirName.concat(File.separator).concat(oid).concat(File.separator);
	}

    public void setTempFolderName() {
        String sessionId = context.getSession().getSessionId();
        if (sessionId.indexOf(":") != -1) {
            sessionId = sessionId.substring(0, sessionId.indexOf(":"));
        }
        String tempFolder = sessionId + System.currentTimeMillis() + objectOid + "_" + viewIdentifier;
        this.tempFolderName = tempFolder.replace(':', '_');
    }

    public void setPropertyVariable() throws FrameworkException {
        String cpnResource = CloudConstants.Basic.CPN_RESOURCE.getValue();
        this.locale = context.getLocale().toString();
        this.language = context.getLocale().getLanguage();
        this.productDataTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
        this.unStructuredTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
        unStructuredTypes = unStructuredTypes.replace(",pgDSOAffectedFPPList", "");
        this.renderSoftwareInstalled = EnoviaResourceBundle.getProperty(context, cpnResource, context.getLocale(), "emxCPN.RenderPDF");
        this.ftpNeeded = EnoviaResourceBundle.getProperty(context, cpnResource, context.getLocale(), "emxCPN.RenderPDF.iText.NoFTP");
        this.genDocErrorMessage = EnoviaResourceBundle.getProperty(context, CloudConstants.Basic.CPN_STRING_RESOURCE.getValue(), context.getLocale(), "emxCPN.PDF.PDFGenError");
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ICloudConfig getCloudConfig() {
        return cloudConfig;
    }

    public void setCloudConfig(ICloudConfig cloudConfig) {
        this.cloudConfig = cloudConfig;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public String getAbsoluteCheckOutDir() {
        return absoluteCheckOutDir;
    }

    public void setAbsoluteCheckOutDir(String absoluteCheckOutDir) {
        this.absoluteCheckOutDir = absoluteCheckOutDir;
    }

    public String getAbsoluteCheckOutDirName() {
        return absoluteCheckOutDirName;
    }

    public void setAbsoluteCheckOutDirName(String absoluteCheckOutDirName) {
        this.absoluteCheckOutDirName = absoluteCheckOutDirName;
    }

    public String getRelativeUploadDirName() {
        return relativeUploadDirName;
    }

    public void setRelativeUploadDirName(String relativeUploadDirName) {
        this.relativeUploadDirName = relativeUploadDirName;
    }

    public String getRelativeUploadSubDir() {
        return relativeUploadSubDir;
    }

    public void setRelativeUploadSubDir(String relativeUploadSubDir) {
        this.relativeUploadSubDir = relativeUploadSubDir;
    }

    public String getObjectOid() {
        return objectOid;
    }

    public void setObjectOid(String objectOid) {
        this.objectOid = objectOid;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectRevision() {
        return objectRevision;
    }

    public void setObjectRevision(String objectRevision) {
        this.objectRevision = objectRevision;
    }

    public String getObjectCurrentState() {
        return objectCurrentState;
    }

    public void setObjectCurrentState(String objectCurrentState) {
        this.objectCurrentState = objectCurrentState;
    }

    public String getObjectPolicy() {
        return objectPolicy;
    }

    public void setObjectPolicy(String objectPolicy) {
        this.objectPolicy = objectPolicy;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getAssemblyType() {
        return assemblyType;
    }

    public void setAssemblyType(String assemblyType) {
        this.assemblyType = assemblyType;
    }

    public String getAuthoringApplication() {
        return authoringApplication;
    }

    public void setAuthoringApplication(String authoringApplication) {
        this.authoringApplication = authoringApplication;
    }

    public String getiPClassification() {
        return iPClassification;
    }

    public void setiPClassification(String iPClassification) {
        this.iPClassification = iPClassification;
    }

    public String getSecurityStatus() {
        return securityStatus;
    }

    public void setSecurityStatus(String securityStatus) {
        this.securityStatus = securityStatus;
    }

    public String getEffectivityDate() {
        return effectivityDate;
    }

    public void setEffectivityDate(String effectivityDate) {
        this.effectivityDate = effectivityDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(String archivedDate) {
        this.archivedDate = archivedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSapType() {
        return sapType;
    }

    public void setSapType(String sapType) {
        this.sapType = sapType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOriginatingSource() {
        return originatingSource;
    }

    public void setOriginatingSource(String originatingSource) {
        this.originatingSource = originatingSource;
    }

    public String getProductViewForm() {
        return productViewForm;
    }

    public void setProductViewForm(String productViewForm) {
        this.productViewForm = productViewForm;
    }

    public String getProductRenderLanguage() {
        return productRenderLanguage;
    }

    public void setProductRenderLanguage(String productRenderLanguage) {
        this.productRenderLanguage = productRenderLanguage;
    }

    public String getViewIdentifier() {
        return viewIdentifier;
    }

    public void setViewIdentifier(String viewIdentifier) {
        this.viewIdentifier = viewIdentifier;
    }

    public String getContextUser() {
        return contextUser;
    }

    public void setContextUser(String contextUser) {
        this.contextUser = contextUser;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProductDataTypes() {
        return productDataTypes;
    }

    public void setProductDataTypes(String productDataTypes) {
        this.productDataTypes = productDataTypes;
    }

    public String getUnStructuredTypes() {
        return unStructuredTypes;
    }

    public void setUnStructuredTypes(String unStructuredTypes) {
        this.unStructuredTypes = unStructuredTypes;
    }

    public String getRenderSoftwareInstalled() {
        return renderSoftwareInstalled;
    }

    public void setRenderSoftwareInstalled(String renderSoftwareInstalled) {
        this.renderSoftwareInstalled = renderSoftwareInstalled;
    }

    public String getFtpNeeded() {
        return ftpNeeded;
    }

    public void setFtpNeeded(String ftpNeeded) {
        this.ftpNeeded = ftpNeeded;
    }

    public String getGenDocErrorMessage() {
        return genDocErrorMessage;
    }

    public void setGenDocErrorMessage(String genDocErrorMessage) {
        this.genDocErrorMessage = genDocErrorMessage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(Map<String, String> objectMap) {
        setFileName(CloudGenDocUtil.getStringFromStringList(objectMap, DomainConstants.SELECT_FILE_NAME));
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectNameForFile() {
        return objectNameForFile;
    }

    public void setObjectNameForFile(String objectNameForFile) {
        this.objectNameForFile = objectNameForFile;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    public void setXmlFileName(String xmlFileName) {
        this.xmlFileName = xmlFileName;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getHtmlFileName() {
        return htmlFileName;
    }

    public void setHtmlFileName(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }

    public String getOutputPdfFileName() {
        return outputPdfFileName;
    }

    public void setOutputPdfFileName(String outputPdfFileName) {
        this.outputPdfFileName = outputPdfFileName;
    }

    public String getTempContentPdfFileName() {
        return tempContentPdfFileName;
    }

    public void setTempContentPdfFileName(String tempContentPdfFileName) {
        this.tempContentPdfFileName = tempContentPdfFileName;
    }

    public boolean isGenDoc() {
        return genDoc;
    }

    public void setGenDoc(boolean genDoc) {
        this.genDoc = genDoc;
    }

    public boolean isGenDocStructured() {
        return genDocStructured;
    }

    public void setGenDocStructured(boolean genDocStructured) {
        this.genDocStructured = genDocStructured;
    }

    public boolean isGenDocUnStructured() {
        return genDocUnStructured;
    }

    public void setGenDocUnStructured(boolean genDocUnStructured) {
        this.genDocUnStructured = genDocUnStructured;
    }

    public boolean isGenDocMEPorSEP() {
        return genDocMEPorSEP;
    }

    public void setGenDocMEPorSEP(boolean genDocMEPorSEP) {
        this.genDocMEPorSEP = genDocMEPorSEP;
    }

    @Override
    public boolean isATSorPOA() {
        return isATSorPOA;
    }

    @Override
    public void setATSorPOA(boolean isATSorPOA) {
        this.isATSorPOA = isATSorPOA;
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
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
}
