package com.pg.dsm.upload.market.enumeration;

import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;

public enum MarketClearanceConstant {

    SELECT_RELATIONSHIP_PRODUCT_CLEARANCE_OBJECT_NAME("relationship[" + pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE + "].to.name"),
    SELECT_ATTRIBUTE_PG_CONFIG_COMMON_ADMIN_MAIL_ID("attribute[" + pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONADMINMAILID + "]"),
    EXCEL_CONFIG_PAGE("pgUploadMarketClearance"),
    SHEET_NAME_MARKET_CLEARANCE("Market Clearance"),
    SHEET_DATA_EMPTY("The Market Clearance sheet is empty."),
    SHEET_NO_RECORDS("The Market Clearance sheet has no records."),
    COLUMNS_MISMATCH("Number of Columns in template is in-correct. Accepted number of columns should be "),
    SHEET_NAME_MISMATCH("The first sheet in the template should have the name as"),
    CONST_ORDER("order"),
    COLUMN_POSITION_MISMATCH("Column: (name) is misplaced and it should be at position (order)"),
    CONST_NA("NA"),
    ALERT_EXCEL_GENERIC_MESSAGE("Uploaded file is not a valid template, Please use the correct template"),
    UPLOAD_MARKET_CLEARANCE_COMMENT("Upload Market (MarketName) Clearance was performed by Support Team member: "),
    SELECT_LAST("last"),
    SELECT_LAST_REVISION("last.revision"),
    NOT_APPROVED("Not Approved"),
    CONST_ROW("Row"),
    CONST_GCAS("GCAS"),
    CONST_PROBLEMATIC_COLUMN_NAME("Problematic Column Name"),
    CONST_ERROR_MESSAGE("Error Message"),
    CONST_VALIDATION_ERRORS("Validation Errors"),
    VALIDATION_ERRORS_FILE_NAME("Market_Clearance_Validation_Errors.xlsx"),
    PROCESSED_EXCEL_FILE_NAME("Market_Clearance_Processed.xlsx"),
    CONST_PROCESSED_RECORDS_INFO("Processed Records Info"),
    CONST_RECORD_PROCESSED("Record Processed?"),
    CONST_RECORD_PROCESSED_FAILURE_MESSAGE("Failure Message"),
    SYMBOL_OPEN_BRACKET("("),
    SYMBOL_CLOSE_BRACKET(")"),
    SYMBOL_NOT_EQUALS("!="),
    SELECT_NEXT_CURRENT("next.current"),
    TOTAL_NUMBER_OF_RECORDS("Total number of records:"),
    NUMBER_OF_RECORDS_PROCESSED_SUCCESSFULLY("Number of records processed:"),
    NUMBER_OF_RECORDS_NOT_PROCESSED("Number of records not processed:"),
    NUMBER_OF_RECORDS_PASSED_VALIDATION("Number of records passed validation:"),
    NUMBER_OF_RECORDS_FAILED_VALIDATION("Number of records failed validation:"),
    UPLOADED_APPROX_TIME("File Upload Time (EST):"),
    CONST_MARKET_NAME("MarketName"),
    MARKET_IS_ALREADY_CONNECTED("Market is already connected"),
    SMTP_HOST("MX_SMTP_HOST"),
    MAIL_SMTP_HOST("mail.smtp.host"),
    CONST_MARKET("Market"),
    COLUMN_TYPE_ATTRIBUTE("attribute"),
    CONFIG_OBJECT_NAME("MarketClearance"),
    GENERAL_EXCEPTION_SUBJECT("Upload Market Clerance - Exception occurred while processing attached file."),
    GENERAL_EXCEPTION_MESSAGE("Processing failed with exception: "),
    CONST_CUSTOM("custom"),
    JPO_COUNTRY_CLEARANCE("pgCountryClearance"),
    METHOD_CALCULATE_COUNTRY_CLEARANCE("calculateClearanceStatus"),
    DATE_FORMAT("MM/dd/yyyy"),
    EXECUTION_TIME("Total Execution Time:"),
    SECONDS("sec."),
    CONTROL_CHARACTER_BEL("\\u0007"),
    CONTROL_CHARACTER_BEL_ESCAPE("\u0007"),
    MARKET_CLEARANCE_CALC_ERROR("Error Calculating Clearance Status"),
    LONG_DATE_FORMAT("MM/dd/yyyy hh:mm:ss aaa"),
    CONST_COS_REQUEST("COSRequest"),
    CPN_STRING_RESOURCE("emxCPNStringResource"),
    ACCEPTED_FILE_FORMAT("xlsm"),
    ACCEPTED_FILE_FORMAT_ALERT("Select only .xlsm files"),
    SUBMIT_JOB_ALERT("Background Job is submitted for processing, Email notification will be sent after completion."),
    EXCEL_PRE_CHECK_ALERT("Following errors occurred while processing the request"),
    EXECUTION_TOOK("-Execution took:"),
    EXECUTION_SECONDS("(seconds)"),
    MARKET_CLEARANCE_JOB_METHOD("perform"),
    POLICY_COUNTRY_STATE_ACTIVE("state_Active"),
    POLICY_COUNTRY_STATE_INACTIVE("state_Inactive"),
    POLICY_COUNTRY_STATE_ARCHIVED("state_Archived"),
    POLICY_COUNTRY("policy_Country");
    private final String value;
    MarketClearanceConstant(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public String getState(Context context, String sPolicyName) {
        return PropertyUtil.getSchemaProperty(context, "policy", sPolicyName, this.value);
    }
    public String getPolicy(Context context) {
        return PropertyUtil.getSchemaProperty(context, this.value);
    }
}
