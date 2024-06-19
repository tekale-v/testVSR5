/*
 **   BatteryConstants.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to define all constants needs for battery data load.
 **
 */

package com.pg.dsm.upload.battery.enumeration;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.AttributeType;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatteryConstants {
    public enum Basic {
        CURRENT_DIR("user.dir"),
        MATRIX_URL("LOGIN_MATRIX_HOST"),
        MATRIX_USR("CONTEXT.USER"),
        MATRIX_PDW("CONTEXT.PASSWORD"),

        // for loading logger properties file.
        HOME_DIR_DUPLICATE("resources"),
        BASE_DIR_DUPLICATE("pgBatteryProductUpload"),
        CONFIG_FOLDER_DUPLICATE("config"),
        CONFIG_LOGGER_FILE_DUPLICATE("pgBatteryProducts-log4j.properties"),
        CONFIG_PROPERTY_FILE_DUPLICATE("pgBatteryProducts.properties"),
        LOGS_FOLDER_DUPLICATE("logs"),
        INPUT_FOLDER_DUPLICATE("input"),

        HOME_DIR("HOME.DIRECTORY"),
        CONFIG_FOLDER("HOME.DIRECTORY.CONFIG.FOLDER"),
        INPUT_FOLDER_NAME("HOME.DIRECTORY.INPUT.FOLDER"),
        OUTPUT_FOLDER_NAME("HOME.DIRECTORY.OUTPUT.FOLDER"),
        PROCESSED_FOLDER("HOME.DIRECTORY.PROCESSED.FOLDER"),
        RETRY_FOLDER("HOME.DIRECTORY.RETRY.FOLDER"),
        LOGS_FOLDER("HOME.DIRECTORY.LOGS.FOLDER"),

        DEBUG_LOG_FILE_CONFIG("logfileDebug.name"),
        ERROR_LOG_FILE_CONFIG("logfileError.name"),
        DEBUG_LOG_FILE_EXTENSION(".log"),
        ERROR_LOG_FILE_EXTENSION("Error.log"),


        BATTERY_PRODUCTS_CONFIG_XML_FILE_NAME("BATTERY.PRODUCTS.CONFIG.XML.FILE.NAME"),

        CONT_DATE_FORMAT_FOR_PREFIX("dd-MM-yyyy_HHmmss"),

        INPUT_EXCEL_FILE_EXTENSION("USER.INPUT.TEMPLATE.FILE.EXTENSION"),
        BUSINESS_RULES_XML_FILE_NAME("BUSINESS.RULES.CONFIG.XML.FILE.NAME"),
        CONFIG_LOGGER_FILE("CUSTOM.LOGGER.CONFIG.PROPERTY.FILE.NAME"),
        CONFIG_PROPERTY_FILE_NAME("CUSTOM.RESOURCE.PROPERTY.FILE.NAME"),

        BUSINESS_RULES_EXCEL_FILE_NAME("BUSINESS.RULES.CONFIG.EXCEL.FILE.NAME"),
        BUSINESS_RULES_TEMPLATE_XML_FILE_NAME("BUSINESS.RULES.CONFIG.XML.TEMPLATE.FILE.NAME"),

        EXCEL_INPUT_TEMPLATE_FILE_NAME("EXCEL.INPUT.TEMPLATE.FILE.NAME"),
        EXCEL_PRE_CHECK_NO_INPUT_FILE("EXCEL.PRE.CHECK.NO.INPUT.FILE"),
        EXCEL_PRE_CHECK_MORE_INPUT_FILE_COUNT("EXCEL.PRE.CHECK.MORE.INPUT.FILE.COUNT"),

        EXCEL_TAB_IDENTIFIER_DPP("EXCEL.TAB.IDENTIFIER.DPP"),
        EXCEL_TAB_IDENTIFIER_RM("EXCEL.TAB.IDENTIFIER.RM"),

        EXCEL_USER_INPUT_TAB_POSITION("USER.INPUT.TEMPLATE.FILE.TAB.POSITION"),
        EXCEL_USER_INPUT_SHEET_NO_RECORDS("EXCEL.INPUT.SHEET.NO.RECORDS"),
        EXCEL_USER_INPUT_SHEET_COLUMNS_INCORRECT("EXCEL.INPUT.SHEET.COLUMNS.INCORRECT"),
        CONST_COLUMN_COLON("Column: "),
        EXCEL_USER_INPUT_TEMPLATE_CONFIG_XML("USER.INPUT.TEMPLATE.CONFIG.XML"),
        EXCEL_USER_INPUT_SHEET_COLUMNS_POSITION_MISMATCH_MESSAGE("EXCEL.INPUT.SHEET.COLUMNS.POSITION.MISMATCH"),
        EXCEL_USER_INPUT_SHEET_EMPTY("EXCEL.INPUT.SHEET.EMPTY"),
        MILLI_SECONDS("ms"),
        MINUTES("min"),
        SECONDS("sec"),
        SYMBOL_COMMA(","),
        SYMBOL_UNDERSCORE("_"),
        SYMBOL_SPACE(" "),
        SYMBOL_PIPE("|"),
        SYMBOL_COLON(":"),

        MICROSOFT_EXCEL_FILE_EXTENSION_XLSX(".xlsx"),
        EXCEL_INPUT_GCAS_LENGTH_ERROR_MESSAGE("EXCEL.INPUT.GCAS.LENGTH.ERROR.MESSAGE"),
        EXCEL_INPUT_GCAS_NOT_FOUND("EXCEL.INPUT.GCAS.NOT.FOUND"),
        FIELD_TYPE_ATTRIBUTE("attribute"),
        FIELD_TYPE_BASIC("basic"),

        ATTRIBUTE_NAME("attr_name"),
        ATTRIBUTE_SELECT("attr_select"),
        ATTRIBUTE_CHOICES("attr_choices"),
        ATTRIBUTE_DEFAULT_VAL("attr_default_val"),
        ATTRIBUTE_MAX_LENGTH("attr_max_length"),
        IS_ATTRIBUTE_MULTI_LINE("attr_multi_line"),
        IS_ATTRIBUTE_SINGLE_VAL("attr_single_val"),
        IS_ATTRIBUTE_MULTI_VAL("attr_multi_val"),
        IS_ATTRIBUTE_RANGE_VAL("attr_range_val"),

        ERROR_EXCEL_COLUMN_DPP("ERROR.EXCEL.COLUMN.DPP"),
        ERROR_EXCEL_COLUMN_REVISION("ERROR.EXCEL.COLUMN.REVISION"),
        ERROR_EXCEL_COLUMN_VALIDATION_FAILURE_MESSAGE("ERROR.EXCEL.COLUMN.VALIDATION.FAILURE.MESSAGE"),
        ERROR_EXCEL_COLUMN_RM("ERROR.EXCEL.COLUMN.RM"),
        ERROR_MESSAGE_NET_WEIGHT_VALUE_MUST_BE_NUMERIC("ERROR.MESSAGE.NET.WEIGHT.VALUE.MUST.BE.NUMERIC"),

        ERROR_EXCEL_COLUMN_NAME("ERROR.EXCEL.COLUMN.NAME"),

        IDENTIFIER_KEYWORD_RESTORE("IDENTIFIER.KEYWORD.RESTORE"),
        IDENTIFIER_KEYWORD_ERROR("IDENTIFIER.KEYWORD.ERROR"),
        IDENTIFIER_KEYWORD_INPUT("IDENTIFIER.KEYWORD.INPUT"),
        IDENTIFIER_KEYWORD_LOG("IDENTIFIER.KEYWORD.LOG"),

        PICKLIST_NAMES("picklistNames"),
        PICKLIST_REVISIONS("picklistRevisions"),
        PICKLIST_IDS("picklistIds"),

        VALUE_YES("Yes"),
        VALUE_NO("No"),

        ERROR_MESSAGE_POWER_SOURCE_RANGE_VALUE_MISMATCH("ERROR.MESSAGE.POWER.SOURCE.RANGE.VALUE.MISMATCH"),
        ERROR_MESSAGE_BATTERY_TYPE_VALUE_EMPTY("ERROR.MESSAGE.BATTERY.TYPE.VALUE.EMPTY"),
        ERROR_MESSAGE_BATTERY_TYPE_VALUE_DOES_NOT_EXIST("ERROR.MESSAGE.BATTERY.TYPE.VALUE.DOES.NOT.EXIST"),
        ERROR_MESSAGE_BATTERY_TYPE_CANNOT_UPDATE_WHEN_POWER_SOURCE_RANGE_IS_ROLLUP("ERROR.MESSAGE.BATTERY.TYPE.CANNOT.UPDATE.WHEN.POWER.SOURCE.RANGE.IS.ROLLUP"),
        ERROR_MESSAGE_BATTERIES_SHIPPED_INSIDE_CANNOT_UPDATE_WHEN_POWER_SOURCE_RANGE_IS_ROLLUP("ERROR.MESSAGE.BATTERIES.SHIPPED.INSIDE.CANNOT.UPDATE.WHEN.POWER.SOURCE.RANGE.IS.ROLLUP"),
        ERROR_MESSAGE_BATTERIES_SHIPPED_INSIDE_VALUE_MUST_BE_NUMERIC("ERROR.MESSAGE.BATTERIES.SHIPPED.INSIDE.VALUE.MUST.BE.NUMERIC"),
        ERROR_MESSAGE_BATTERIES_REQUIRED_VALUE_MUST_BE_NUMERIC("ERROR.MESSAGE.BATTERIES.REQUIRED.VALUE.MUST.BE.NUMERIC"),
        ERROR_MESSAGE_BATTERIES_REQUIRED_VALUE_MANDATORY_WHEN_POWER_SOURCE("ERROR.MESSAGE.BATTERIES.REQUIRED.VALUE.MANDATORY.WHEN.POWER.SOURCE"),
        ERROR_MESSAGE_BATTERIES_REQUIRED_VALUE_NOT_REQUIRED_WHEN_POWER_SOURCE_RANGE_IS_ROLLUP("ERROR.MESSAGE.BATTERIES.REQUIRED.VALUE.NOT.REQUIRED.WHEN.POWER.SOURCE.RANGE.IS.ROLLUP"),
        ERROR_MESSAGE_IS_BATTERY_VALUE_SHOULD_BE_YES_OR_NO("ERROR.MESSAGE.IS.BATTERY.VALUE.SHOULD.BE.YES.OR.NO"),
        ERROR_MESSAGE_BATTERY_TYPE_CANNOT_UPDATE_WHEN_IS_BATTERY_VALUE_IS("ERROR.MESSAGE.BATTERY.TYPE.CANNOT.UPDATE.WHEN.IS.BATTERY.VALUE.IS"),
        POWER_SOURCE_RANGE_VALUES_ALLOWED("POWER.SOURCE.RANGE.VALUES.ALLOWED"),
        POWER_SOURCE_RANGE_VALUES_NOT_ALLOWED("POWER.SOURCE.RANGE.VALUES.NOT.ALLOWED"),
        POWER_SOURCE_RANGE_VALUES("POWER.SOURCE.RANGE.VALUES"),
        IS_BATTERY_VALUES("ISBATTERY.VALUES"),
        FILE_EXTENSION_XLSM("xlsm");

        private final String value;

        /**
         * Constructor
         *
         * @param value - String
         * @since DSM 2018x.6
         */
        Basic(String value) {
            this.value = value;
        }

        /**
         * Method to get value
         *
         * @since DSM 2018x.6
         */
        public String getValue() {
            return value;
        }
    }

    public enum Attribute {
        POWER_SOURCE("attribute_pgPowerSource"),
        BATTERY_TYPE("attribute_pgBatteryType"),
        BATTERIES_SHIPPED_INSIDE_DEVICE("attribute_pgBatteriesShippedInsideDevice"),
        NUMBER_OF_BATTERIES_REQUIRED("attribute_pgNumberOfBatteriesRequired"),
        NET_WEIGHT("attribute_pgNetWeight"),
        IS_BATTERY("attribute_pgIsTheProductABattery");

        private static final Map<String, Attribute> attributeMap = Arrays.stream(values()).collect(Collectors.toMap(Enum::toString, Function.identity()));
        private final String name;

        /**
         * Constructor
         *
         * @param name - String
         * @since DSM 2018x.6
         */
        Attribute(String name) {
            this.name = name;
        }

        public static Attribute get(String name) {
            return attributeMap.get(name);
        }

        /**
         * Method to attribute schema name.
         *
         * @param context - Context
         * @return String - Attribute name
         * @since DSM 2018x.6
         */
        public String getAttribute(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }

        /**
         * Method to attribute schema select expression.
         *
         * @param context - Context
         * @return String - Attribute name
         * @since DSM 2018x.6
         */
        public String getAttributeSelect(Context context) {
            return DomainObject.getAttributeSelect(this.getAttribute(context));
        }

        /**
         * Method to attribute default value.
         *
         * @param context - Context
         * @return String - Attribute default value.
         * @throws matrix.util.MatrixException - exception
         * @since DSM 2018x.6
         */
        public String getDefaultValue(Context context) throws MatrixException {
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            return attributeType.getDefaultValue(context);
        }

        /**
         * Method to attribute default choices.
         *
         * @param context - Context
         * @return StringList
         * @throws MatrixException
         * @since DSM 2018x.6
         */
        public StringList getChoices(Context context) throws MatrixException {
            StringList choices = null;
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            attributeType.open(context);
            choices = attributeType.getChoices(context);
            attributeType.close(context);
            return choices;
        }

        /**
         * Method to attribute Maximum Length.
         *
         * @param context - Context
         * @return int - attribute maximum length.
         * @throws MatrixException - exception
         * @since DSM 2018x.6
         */
        public int getMaxLength(Context context) throws MatrixException {
            int maxLength = 0;
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            attributeType.open(context);
            maxLength = attributeType.getMaxLength();
            attributeType.close(context);
            return maxLength;
        }

        /**
         * Method to check attribute multi selection.
         *
         * @param context - Context
         * @return boolean - true or false.
         * @throws MatrixException - exception
         * @since DSM 2018x.6
         */
        public boolean isMultiLine(Context context) throws MatrixException {
            boolean multiLine = false;
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            attributeType.open(context);
            multiLine = attributeType.isMultiLine();
            attributeType.close(context);
            return multiLine;
        }

        /**
         * Method to check attribute multi Value.
         *
         * @param context - Context
         * @return boolean - true or false.
         * @throws MatrixException - exception
         * @since DSM 2018x.6
         */
        public boolean isMultiVal(Context context) throws MatrixException {
            boolean multiVal = false;
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            attributeType.open(context);
            multiVal = attributeType.isMultiVal();
            attributeType.close(context);
            return multiVal;
        }

        /**
         * Method to check attribute single selection.
         *
         * @param context - Context
         * @return boolean - true or false.
         * @throws MatrixException - exception
         * @since DSM 2018x.6
         */
        public boolean isSingleVal(Context context) throws MatrixException {
            boolean singleVal = false;
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            attributeType.open(context);
            singleVal = attributeType.isSingleVal();
            attributeType.close(context);
            return singleVal;
        }

        /**
         * Method to check attribute range value.
         *
         * @param context - Context
         * @return boolean - true or false.
         * @throws MatrixException - exception
         * @since DSM 2018x.6
         */
        public boolean isRangeVal(Context context) throws MatrixException {
            boolean rangeVal = false;
            AttributeType attributeType = new AttributeType(this.getAttribute(context));
            attributeType.open(context);
            rangeVal = attributeType.isRangeVal();
            attributeType.close(context);
            return rangeVal;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
