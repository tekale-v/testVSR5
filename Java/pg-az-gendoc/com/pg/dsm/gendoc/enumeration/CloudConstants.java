/*
 **   CloudConstants.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains constants specifically for this feature.
 **
 */
package com.pg.dsm.gendoc.enumeration;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;

public class CloudConstants {
    public enum Basic {

        DIGITAL_SPEC_CLOUD_GEN_DOC_CONFIG_OBJECT("pgDigitalSpecCloudGenDocConfig"),
        INNOVATION_RECORD_CLOUD_GEN_DOC_CONFIG_OBJECT("pgInnovationRecordCloudGenDocConfig"),

		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669.
		DATE_FORMAT_FOR_TIMESTAMP("yyyyMMddHHmmssSSS"),
		SYMBOL_UNDERSCORE("_"),
		SYMBOL_EQUAL("="),
		SYMBOL_COLON(":"),
		SYMBOL_SEMI_COLON(";"),
		SYMBOL_COMMA(","),
		SYMBOL_DOT("."),
		SYMBOL_HYPHEN("-"),

        FILE_EXTENSION_TXT("txt"),
        FILE_EXTENSION_PDF("pdf"),
        STRING_REVISION("-Rev"),
        STRING_PDF(".pdf"),

        SUCCESS_KEY_WORD("success"),
        ERROR_DOCUMENT_ERROR("error - input Document object is null"),
        ERROR_HYPHEN("error - "),
        TEMP_PDF_FILE_NAME("TempContentFile.PDF"),
        ERROR_BLOB_CLIENT_INSTANCE("Error Getting Instance of Cloud Blob Client"),

        DOT_FILE_EXTENSION_PDF(".pdf"),
        DOT_FILE_EXTENSION_HTM(".htm"),
        DOT_FILE_EXTENSION_XML(".xml"),
        KEYWORD_REV("Rev"),
        KEYWORD_ERROR("error"),
        KEYWORD_TIMEOUT("timeout"),

        KEYWORD_TRUE("True"),

        FILE_NAME("fileName"),
        FILES_COUNT("filesCount"),
        CHECKOUT_PATH("checkOutPath"),
        RELATIVE_CHECKOUT_DIR("relativeCheckOutDir"),
        RELATIVE_CHECKOUT_DIR_NAME("relativeCheckOutDirName"),
        ABSOLUTE_CHECKOUT_DIR("absoluteCheckOutDir"),
        ABSOLUTE_CHECKOUT_DIR_NAME("absoluteCheckOutDirName"),
        ABSOLUTE_CHECKOUT_FILE_PATH("absoluteCheckOutFilePath"),
        FILE_EXTENSION("fileExtension"),

        BLOB_UPLOAD_RELATIVE_FILE_PATH("blobUploadRelativeFilePath"),
        BLOB_UPLOAD_RELATIVE_FILE_DIR("blobUploadRelativeFileDir"),
        BLOB_UPLOAD_RELATIVE_DIR("blobUploadRelativeDir"),
        UPLOAD_RESPONSE("uploadResponse"),
        DOWNLOAD_RESPONSE("downloadResponse"),
        ABSOLUTE_DOWNLOAD_FILE_PATH("absoluteDownloadFilePath"),
        ABSOLUTE_DOWNLOAD_DIR("absoluteDownloadDir"),

        GEN_DOC_RETURN_TYPE("strReturnType"),
        GEN_DOC_RETURN_ERROR_MESSAGE("strErrorMsg"),
        UPLOAD_DOWNLOAD_COUNT_MISMATCH("Cloud File upload and download count mismatch"),
        GEN_DOC_RETURN_NUMERICAL_STRING_ONE("1"),
        ERROR_PDF_HEADER_CREATION_FAILED("PDF Header Creation was not Successful"),
        ERROR_DOWNLOAD_UPLOAD_COUNT_MISMATCH("Download & Checkout count does not match"),
        ERROR_PDF_CONVERT_FAILED("PDF Convert was not successful"),
        ERROR_WORK_TEXT_FILE_UPLOAD_FAILED("GenDoc Work Text File upload was not successful"),
        ERROR_CLOUD_INPUT_OUTPUT_COUNT_MISMATCH("Cloud Input File count does not match with actual Upload count"),
        ERROR_CLOUD_CONTAINER_INSTANCE_CREATION_FAILED("Get instance of Cloud Container was not successful"),
        ERROR_FILE_SIZE_ZERO_KB("File size is 0 KB"),
        REFERENCE_DOCUMENT_REVISION("1"),

        INPUT_OBJECT_ID("inputMxOid"),
        INPUT_VIEW_IDENTIFIER("inputMxViewIdentifier"),
        INPUT_CONTEXT_USER("inputMxContextUser"),

        COMPONENTS_STRING_RESOURCE("emxComponentsStringResource"),
        CPN_STRING_RESOURCE("emxCPNStringResource"),
        CPN_RESOURCE("emxCPN"),
        PROGRAM_CENTRAL_STRING_RESOURCE("emxProgramCentralStringResource"),

        IO_TEMP_DIRECTORY("java.io.tmpdir"),

        CLOUD_GENDOC_TIMEOUT("Cloud Download Timeout"),

		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669, 43171, 43433 - Starts
		KEYWORD_PART("PART"),
		KEY_GEN_DOC_PROCESS_RETURN_INTEGER("returnIntegerGenDoc"),
		KEY_GEN_DOC_PROCESS_RETURN_STRING("returnStringGenDoc"),
		GEN_DOC_BLOB_ROOT_FOLDER_NAME("rootFolderPrefix"),
		GEN_DOC_RETURN_NUMERICAL_STRING_ZERO("0"),
		CLOUD_OBJECT_INSTANTIATION_FAILED("ICloudDocument Instantiation Failed"),
		COPYING_CHECKOUT_FILES_FAILED("Failed to Copy Check-Out files to In Work Folder"),
		NOT_GEN_DOC_REQUEST("Not a GenDoc Request"),
		CONTAINS_UNSUPPORTED_EXTENSION_FILE("Contains un-supported file extension."),
		AZURE_BLOB_UPLOAD_AND_DOWNLOAD_FILES_COUNT_MISMATCH("Azure Blob Upload & Download files count mismatch"),
		ERROR_GETTING_BLOB_REFERENCE("error - Unable to get Blob reference"),
		ERROR_LOCAL_DOWNLOAD_PATH_DOES_NOT_EXIST("error - File does not exist"),
		ERROR_INPUT_JSON_IS_EMPTY_OR_NULL("error - input json is empty-null"),
		ERROR_RENAMING_CHECKOUT_FILES_FAILED("Renaming Check-Out Files Failed"),
		KEY_WORD_TIMED_OUT("TimedOut"),
		KEY_WORD_RETURN_VALUE("returnValue"),
		FILE_EXTENSION_AI("ai"),
		AZURE_BLOB_DOES_NOT_EXIST_ERROR("The specified blob does not exist"),
		SYMBOL_FORWARD_SLASH("/"),
		ERROR_RENAME_AND_REMOVE_SPECIAL_CHARACTERS_FROM_CHECKOUT_FILES_FAILED("Error while renaming and removing special characters from checkout files."),
		ERROR_REMOVE_SPECIAL_CHARACTERS_FROM_CHECKOUT_FILES_FAILED("Error while removing special characters from checkout files."),
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669, 43171, 43433 - Ends

		// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
		FAILED_TO_COPY_HTML_PDF_TO_IN_WORK_FOLDER("Failed to copy Html Pdf to In-Work folder"),
		FAILED_TO_GENERATE_HTML_TO_PDF("Failed to generate Html to Pdf using iText"),
		FAILED_TO_RENAME_CHECK_OUT_FILE("Failed to rename checkout file. File name already exist"),
		// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends.

		DOWNLOAD_DIR_NAME("download"),
		MERGE_DIR_NAME("merge"),
		IN_WORK_DIR_NAME("inwork");

        private final String name;

        Basic(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }

	public enum Attribute {
		CLOUD_GEN_DOC_BLOB_STORAGE_CONTAINER_NAME("attribute_pgCloudGenDocBlobStorageContainerName"),
		CLOUD_GEN_DOC_END_POINTS_PROTOCOL_PARAMETER("attribute_pgCloudGenDocEndPointsProtocolParameter"),
		CLOUD_GEN_DOC_END_POINTS_PROTOCOL_VALUE("attribute_pgCloudGenDocEndPointsProtocolValue"),
		CLOUD_GEN_DOC_ACCOUNT_NAME_PARAMETER("attribute_pgCloudGenDocAccountNameParameter"),
		CLOUD_GEN_DOC_ACCOUNT_NAME_VALUE("attribute_pgCloudGenDocAccountNameValue"),
		CLOUD_GEN_DOC_ACCOUNT_KEY_PARAMETER("attribute_pgCloudGenDocAccountKeyParameter"),
		CLOUD_GEN_DOC_ACCOUNT_KEY_VALUE("attribute_pgCloudGenDocAccountKeyValue"),
		GEN_DOC_FILE_EXTENSIONS_FOR_CLOUD("attribute_pgGenDocFileExtensionsForCloud"),
		GEN_DOC_FILE_EXTENSIONS_FOR_ITEXT("attribute_pgGenDocFileExtensionsForiText"),
		GEN_DOC_BLOB_UPLOAD_PATH("attribute_pgGenDocBlobUploadPath"),
		GEN_DOC_BLOB_DOWNLOAD_PATH("attribute_pgGenDocBlobDownloadPath"),
		GEN_DOC_LOCAL_DOWNLOAD_PATH("attribute_pgGenDocLocalDownloadPath"),
		GEN_DOC_SLEEP_INTERVAL("attribute_pgGenDocSleepInterval"),
		GEN_DOC_TIMER("attribute_pgGenDocTimer"),
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433
		CHARACTERS_NOT_ALLOWED_FOR_FILE_NAME("attribute_pgConfigAttrPDFSplitInpt"),
		CLOUD_GEN_DOC_CUSTOM_LOGGER_CONFIG_FILE_PATH("attribute_pgCloudGenDocCustomLoggerConfigFilePath"),
		// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
		CHARACTERS_NOT_ALLOWED_FOR_OBJECT_NAME("attribute_pgConfigCommonAttr");
		// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

        private final String name;

        Attribute(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }

        public String getSelect(Context context) {
            return DomainObject.getAttributeSelect(this.getName(context));
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum DocumentProperty {
        ID("id"),
        TYPE("type"),
        NAME("name"),
        REVISION("revision"),
        FILE_NAME("fileName"),
        FILE_PATH("filePath"),
        FILES_COUNT("filesCount"),
        CHECKOUT_PATH("checkOutPath"),
        RELATIVE_CHECKOUT_DIR("relativeCheckOutDir"),
        RELATIVE_CHECKOUT_DIR_NAME("relativeCheckOutDirName"),
        ABSOLUTE_CHECKOUT_DIR("absoluteCheckOutDir"),
        ABSOLUTE_CHECKOUT_DIR_NAME("absoluteCheckOutDirName"),
        ABSOLUTE_CHECKOUT_FILE_PATH("absoluteCheckOutFilePath"),
        FILE_EXTENSION("fileExtension"),
        BLOB_UPLOAD_RELATIVE_FILE_PATH("blobUploadRelativeFilePath"),
        BLOB_UPLOAD_RELATIVE_FILE_DIR("blobUploadRelativeFileDir"),
        BLOB_FILE_DOWNLOAD_PATH("blobFileDownloadPath"),
        BLOB_FILE_UPLOAD_PATH("blobFileUploadPath"),
        BLOB_DOWNLOAD_FILE_NAME("blobDownloadFileName"),
        BLOB_UPLOAD_RELATIVE_DIR("blobUploadRelativeDir"),
        UPLOAD_RESPONSE("uploadResponse"),
        DOWNLOAD_RESPONSE("downloadResponse"),
        ABSOLUTE_DOWNLOAD_FILE_PATH("absoluteDownloadFilePath"),
        ABSOLUTE_DOWNLOAD_DIR("absoluteDownloadDir"),
        IS_PDF_GENERATED("isPdfGenerated"),
        PDF_GENERATED_ERROR_MESSAGE("pdfGenErrorMsg"),
        LOADED("loaded"),
        TIME_OUT("timeOut"),
        IN_WORK_DIR("inWorkDir"),
        IN_WORK_DIR_NAME("inWorkDirName"),
        IN_WORK_DIR_FILE_PATH("inWorkDirFilePath");

        private final String name;

        DocumentProperty(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }
}
