/*
 **   ExcelUtil.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Contains all utility methods for reading & validation excel.
 **
 */
package com.pg.dsm.upload.market.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.market.beans.xl.EachColumn;
import com.pg.dsm.upload.market.beans.xl.EachColumnCheck;
import com.pg.dsm.upload.market.beans.xl.EachRow;
import com.pg.dsm.upload.market.beans.xl.Excel;
import com.pg.dsm.upload.market.beans.xml.Check;
import com.pg.dsm.upload.market.beans.xml.Checks;
import com.pg.dsm.upload.market.beans.xml.Column;
import com.pg.dsm.upload.market.beans.xml.XML;
import com.pg.dsm.upload.market.enumeration.MarketClearanceConstant;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.beans.COSRequest;
import matrix.db.Context;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    boolean isLoggingEnabled;

    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    public ExcelUtil(boolean isLoggingEnabled) {
        this.isLoggingEnabled = isLoggingEnabled;
    }

    /**
     * Method to read excel into bean.
     *
     * @param context             - Context
     * @param beanXML             - XML - bean object
     * @param sAllowedTypes       - String - types allowed
     * @param sAllowedPolicies    - String - policies
     * @param sInputExcelFilePath - String - excel path
     * @return List<EachRow> - list of EachRow bean objects
     * @throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException - exception
     * @since DSM 2018x.5
     */
    public List<EachRow> readExcel(Context context, XML beanXML, String sAllowedTypes, String sAllowedPolicies, String sInputExcelFilePath) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Instant startTime = Instant.now();
        List<EachRow> excelRows = new ArrayList<>();
        int excelRowSize = excelRows.size();
        FileInputStream excelFileInputStream;
        File file = new File(sInputExcelFilePath);
        List<Column> columnConfigurationList = beanXML.getColumns().get(0).getColumns();
        excelFileInputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(excelFileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Number of Physical Rows in Market Clearance sheet|" + physicalNumberOfRows + "|last row number|" + sheet.getLastRowNum());
        }
        readExcelRow(context, sAllowedTypes, sAllowedPolicies, formatter, sheet, columnConfigurationList, excelRows);
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Upload Market Clearance Total Records|" +excelRowSize);
        }
        logger.info(new MarketClearanceUtil().getExecutionTimeString(startTime));
        return excelRows;
    }

    /**
     * Method to Method to read excel rows
     *
     * @param context                 - Context - context
     * @param sTypes                  - String
     * @param sPolicies               - String
     * @param formatter               - DataFormatter
     * @param sheet                   - Sheet
     * @param columnConfigurationList - List<Column>
     * @param rows                    - List<EachRow>
     * @return void
     * @throws InvocationTargetException, IllegalAccessException, NoSuchMethodException - exception
     * @since DSM 2018x.5
     */
    private void readExcelRow(Context context, String sTypes, String sPolicies, DataFormatter formatter, Sheet sheet, List<Column> columnConfigurationList, List<EachRow> rows) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        COSRequest objCOSRequest;
        EachRow objEachRow;
        XSSFRow xsRow;
        int columnCount = columnConfigurationList.size();
        int rowNumber;
        int rowNum;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            xsRow = ((XSSFRow) rowIterator.next());
            rowNum = xsRow.getRowNum();
            if (rowNum == 0)
                continue;
            if (!checkIfRowIsEmpty(xsRow)) {
                rowNumber = rowNum + 1;
                objEachRow = new EachRow();
                objEachRow.setRowNumber(rowNumber);
                objEachRow.setColumnCount(columnCount);
                objEachRow.setAllowedTypes(sTypes);
                objEachRow.setAllowedPolicies(sPolicies);
                readExcelColumns(context, objEachRow, formatter, xsRow, columnConfigurationList, columnCount, rowNumber);
                objCOSRequest = new COSRequest();
                BeanUtils.copyProperties(objCOSRequest, objEachRow);
                objEachRow.setCosRequest(objCOSRequest);
                rows.add(objEachRow);
            }
        }
    }

    /**
     * Method to Method to read excel columns
     *
     * @param context                 - Context - context
     * @param objEachRow              - EachRow
     * @param formatter               - DataFormatter
     * @param xsRow                   - XSSFRow
     * @param columnConfigurationList - List<Column>
     * @param columnCount             - int
     * @param rowNumber               - int
     * @return void
     * @throws InvocationTargetException, IllegalAccessException, NoSuchMethodException - exception
     * @since DSM 2018x.5
     */
    private void readExcelColumns(Context context, EachRow objEachRow, DataFormatter formatter, XSSFRow xsRow, List<Column> columnConfigurationList, int columnCount, int rowNumber) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, String> attributeKeyValueMap = new HashMap<>();
        List<EachColumn> listEachColumn = new ArrayList<>();
        List<Check> listChecks;
        List<Checks> objlistChecks;
        EachColumn objEachColumn;
        Column objColumn;
        Checks objChecks;
        Method objColumnSetterMethod;
        XSSFCell cell;
        for (int col = 0; col < columnCount; col++) {
            cell = xsRow.getCell(col);
            String cellValue = formatter.formatCellValue(cell);
            objColumn = columnConfigurationList.get(col);
            objColumn.setColumnValue(cellValue.trim());
            objColumn.setRowNumber(rowNumber);
            objEachColumn = new EachColumn();
            BeanUtils.copyProperties(objEachColumn, objColumn);
            listChecks = null;
            objlistChecks = objColumn.getChecks();
            if (objlistChecks != null && !objlistChecks.isEmpty()) {
                objChecks = objlistChecks.get(0);
                listChecks = objChecks.getChecks();
            }
            objEachColumn.setColumnCheckList(readExcelColumnChecks(listChecks, objEachColumn));
            listEachColumn.add(objEachColumn);
            objColumnSetterMethod = objEachRow.getClass().getDeclaredMethod(objEachColumn.getColumnSetter(), String.class);
            objColumnSetterMethod.invoke(objEachRow, objEachColumn.getColumnValue());
            if (MarketClearanceConstant.COLUMN_TYPE_ATTRIBUTE.getValue().equals(objColumn.getColumnType())) {
                attributeKeyValueMap.put(PropertyUtil.getSchemaProperty(context, objColumn.getSchema()), cellValue);
            }
        }
        if (!attributeKeyValueMap.isEmpty() && !listEachColumn.isEmpty()) {
            objEachRow.setAttributeKeyValueMap(attributeKeyValueMap);
            objEachRow.setColumnList(listEachColumn);
        }
    }

    /**
     * Method to read excel column checks
     *
     * @param listChecks    - List<Check>
     * @param objEachColumn - EachColumn
     * @return void
     * @throws InvocationTargetException, IllegalAccessException - exception
     * @since DSM 2018x.5
     */
    private List<EachColumnCheck> readExcelColumnChecks(List<Check> listChecks, EachColumn objEachColumn) throws InvocationTargetException, IllegalAccessException {
        List<EachColumnCheck> listEachColumnCheck = new ArrayList<>();
        if (listChecks != null) {
            EachColumnCheck objEachColumnCheck;
            for (Check objCheck : listChecks) {
                objCheck.setRowNumber(objEachColumn.getRowNumber());
                objCheck.setColumnNumber(objEachColumn.getColumnNumber());
                objCheck.setColumnName(objEachColumn.getColumnName());
                objEachColumnCheck = new EachColumnCheck();
                BeanUtils.copyProperties(objEachColumnCheck, objCheck);
                listEachColumnCheck.add(objEachColumnCheck);
            }
        }
        return listEachColumnCheck;
    }

    /**
     * Method to perform the column checks configured in page xml
     *
     * @param context  - Context - context
     * @param objExcel - Excel - bean object
     * @return void
     * @throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException - exception
     * @since DSM 2018x.5
     */
    public void performExcelChecks(Context context, Excel objExcel) throws InstantiationException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException {
        Class<?> classColumnUtil = Class.forName(ColumnUtil.class.getName());
        Class<?>[] classColumnUtilConstructorArgs = new Class[4];
        classColumnUtilConstructorArgs[0] = Context.class;
        classColumnUtilConstructorArgs[1] = EachRow.class;
        classColumnUtilConstructorArgs[2] = EachColumn.class;
        classColumnUtilConstructorArgs[3] = EachColumnCheck.class;
        List<EachRow> rows = objExcel.getRows();

        // below variables used during iteration.
        List<EachColumn> columnList;
        List<String> validationMessageListRowLevel;
        Set<String> uniqueFailedColumnNameSet;
        List<EachColumnCheck> columnCheckList;
        List<String> validationMessageListColumnLevel;
        String sMethodName;
        boolean bDisabled;
        ColumnUtil objColumnUtil;
        Method objMethod;
        boolean blnResult;

        //iterate rows bean
        for (EachRow objEachRow : rows) {
            updateGcasMarketRepeatedFlag(objExcel, objEachRow);
            columnList = objEachRow.getColumnList();
            validationMessageListRowLevel = new ArrayList<>();
            uniqueFailedColumnNameSet = new HashSet<>();
            //iterate columns bean
            for (EachColumn objEachColumn : columnList) {
                columnCheckList = objEachColumn.getColumnCheckList();
                validationMessageListColumnLevel = new ArrayList<>();
                //iterate column checks bean
                for (EachColumnCheck objEachColumnCheck : columnCheckList) {
                    sMethodName = objEachColumnCheck.getMethod();
                    bDisabled = objEachColumnCheck.isDisabled();
                    //check if its disabled.
                    if (!bDisabled) {
                        objColumnUtil = (ColumnUtil) classColumnUtil.getDeclaredConstructor(classColumnUtilConstructorArgs).newInstance(context, objEachRow, objEachColumn, objEachColumnCheck);
                        objMethod = classColumnUtil.getDeclaredMethod(sMethodName);
                        blnResult = (boolean) objMethod.invoke(objColumnUtil);
                        if (!blnResult) {
                            validationMessageListColumnLevel.add(objEachColumnCheck.getValidationMessage());
                            uniqueFailedColumnNameSet.add(objEachColumn.getColumnName());
                            if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                                logger.info("Row|" + objEachColumn.getRowNumber() + "|Column|" + objEachColumn.getColumnName() + "|Check|" + sMethodName + "|Error|" + objEachColumnCheck.getValidationMessage());
                            }
                        }
                    }
                }
                if (!validationMessageListColumnLevel.isEmpty()) {
                    objEachColumn.setValidationMessageList(validationMessageListColumnLevel);
                    validationMessageListRowLevel.addAll(validationMessageListColumnLevel);
                }
            }
            if (!validationMessageListRowLevel.isEmpty()) {
                objEachRow.setValidationMessageList(validationMessageListRowLevel);
                objEachRow.setFailedColumnNames(uniqueFailedColumnNameSet);
                if (objExcel.isPassed()) {
                    objExcel.setPassed(false);
                }
            }
        }
    }

    /**
     * Helper method to print excel validations
     *
     * @param excel - Excel - bean object
     * @return void
     * @since DSM 2018x.5
     */
    public void logValidationReport(Excel excel) {
        List<EachRow> rows = excel.getRows();
        boolean gcasHasConnectedCountry;
        boolean countryAlreadyConnected;
        List<String> validationMessageList;
        for (EachRow objEachRow : rows) {
            gcasHasConnectedCountry = objEachRow.isGcasHasConnectedCountry();
            countryAlreadyConnected = objEachRow.isCountryAlreadyConnected();
            if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                logger.info("Is Gcas Exist|" + objEachRow.isGcasObjectExist() + "|Is Market Exist|" + objEachRow.isMarketObjectExist() + "|Excel Input Market|" + objEachRow.getCOUNTRY_REQUESTED() + "|Is Gcas has connected Markets|" + gcasHasConnectedCountry + "|Is Input Market already connected|" + countryAlreadyConnected);
            }
            validationMessageList = objEachRow.getValidationMessageList();
            if (validationMessageList != null && isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                logger.info("Number of error at row|"+objEachRow.getRowNumber()+"|is|"+validationMessageList.size()+"|"+validationMessageList);
            }
        }
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Proceed to update records in database?|" +excel.isPassed());
        }
    }

    /**
     * Method to perform excel pre-checks
     *
     * @param context             - Context - context
     * @param beanXML             - XML     - bean object
     * @param sInputExcelFilePath - String  - path
     * @return void
     * @since DSM 2018x.5
     */
    public String getExcelPreChecks(Context context, XML beanXML, String sInputExcelFilePath) throws IOException {
        List<String> precheckList = new ArrayList<>();
        if (beanXML != null) {
            List<Column> configColumnList = beanXML.getColumns().get(0).getColumns();
            int numberOfConfigColumns = configColumnList.size();
            FileInputStream fis;
            File file = new File(sInputExcelFilePath);
            fis = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            String sheetName = sheet.getSheetName();
            // check if sheet name is correct
            String requiredSheetName = MarketClearanceConstant.SHEET_NAME_MARKET_CLEARANCE.getValue();
            if (!requiredSheetName.equals(sheetName)) {
                precheckList.add(MarketClearanceConstant.SHEET_NAME_MISMATCH.getValue() + pgV3Constants.SYMBOL_SPACE + requiredSheetName);
            }
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            if (isLoggingEnabled && logger.isLoggable(Level.INFO))
                logger.info("Number of Physical Rows in Market Clearance sheet|" + physicalNumberOfRows);
            // check if sheet is empty
            boolean blnEmptySheet = checkIfExcelSheetEmpty(sheet.rowIterator(), precheckList, numberOfConfigColumns);
            // check if column order is aligned
            if (!blnEmptySheet)
                checkIfExcelColumnsAligned(numberOfConfigColumns, configColumnList, sheet.rowIterator(), precheckList);
        }
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Number of failed pre-checks|" + precheckList + "|Checks|" + precheckList);
        }
        return getExcelPreChecks(precheckList);
    }

    /**
     * Method to perform excel pre-checks
     *
     * @param preCheckList - List<String> - list of validations.
     * @return String - string output
     * @since DSM 2018x.5
     */
    private String getExcelPreChecks(List<String> preCheckList) {
        StringBuilder messageBuilder = new StringBuilder();
        int preCheckListSize = preCheckList.size();
        if (preCheckListSize > 0 && !preCheckList.contains(MarketClearanceConstant.SHEET_NO_RECORDS.getValue())) {
            messageBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
            messageBuilder.append(MarketClearanceConstant.ALERT_EXCEL_GENERIC_MESSAGE.getValue());
            messageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        }
        for (String sPreCheck : preCheckList) {
            messageBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
            messageBuilder.append(sPreCheck);
            messageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        }
        return messageBuilder.toString();
    }

    /**
     * Method to check if excel sheet is empty
     *
     * @param rowIterator           - Iterator<Row> - iterator object.
     * @param preCheckList          - List<String> - list of validations.
     * @param numberOfConfigColumns - int - columns configured on xml.
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    private boolean checkIfExcelSheetEmpty(Iterator<Row> rowIterator, List<String> preCheckList, int numberOfConfigColumns) {
        boolean blnEmptySheet = true;
        boolean blnHasHeaders = true;
        if (rowIterator.hasNext()) {
            while (rowIterator.hasNext()) {
                XSSFRow excelRow = (XSSFRow) rowIterator.next();
                if (excelRow.getRowNum() == 0) {
                    if (excelRow.getPhysicalNumberOfCells() != numberOfConfigColumns) {
                        blnHasHeaders = false;
                        preCheckList.add(MarketClearanceConstant.COLUMNS_MISMATCH.getValue());
                        break;
                    }
                } else {
                    boolean isExcelRowEmpty = checkIfRowIsEmpty(excelRow);
                    if (!isExcelRowEmpty) {
                        blnEmptySheet = false;
                        break;
                    }
                }
            }
        } else {
            blnHasHeaders = false;
            preCheckList.add(MarketClearanceConstant.SHEET_DATA_EMPTY.getValue());
        }
        // if this variable is still true, then it means that the sheet has only header but no data.
        if (blnHasHeaders && blnEmptySheet) {
            preCheckList.add(MarketClearanceConstant.SHEET_NO_RECORDS.getValue());
        }
        return blnEmptySheet;
    }

    /**
     * Method to check if excel row is empty
     *
     * @param excelRow - XSSFRow - excel row object
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    private boolean checkIfRowIsEmpty(XSSFRow excelRow) {
        if (excelRow == null) {
            return true;
        }
        if (excelRow.getLastCellNum() <= 0) {
            return true;
        }
        Cell excelRowCell;
        for (int cellNum = excelRow.getFirstCellNum(); cellNum < excelRow.getLastCellNum(); cellNum++) {
            excelRowCell = excelRow.getCell(cellNum);
            if (excelRowCell != null && excelRowCell.getCellType() != CellType.BLANK && UIUtil.isNotNullAndNotEmpty(excelRowCell.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to check if excel is aligned with configured columns in xml
     *
     * @param numberOfConfigColumns - int - column count
     * @param configColumnList      - List<Column>  - column list
     * @param rowIterator           - Iterator<Row> - iterator object.
     * @param preCheckList          - List<String>  - list of validations.
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    private void checkIfExcelColumnsAligned(int numberOfConfigColumns, List<Column> configColumnList, Iterator<Row> rowIterator, List<String> preCheckList) {
        DataFormatter formatter = new DataFormatter();

        // below variables are used during iteration.
        Column objColumn;
        XSSFCell excelRowCell;
        String sExcelRowCellValue;
        int columnCount;
        XSSFRow excelRow;
        String sColumnName;

        while (rowIterator.hasNext()) {
            excelRow = (XSSFRow) rowIterator.next();
            if (excelRow.getRowNum() == 0) {
                columnCount = excelRow.getLastCellNum();
                if (numberOfConfigColumns != columnCount) {
                    preCheckList.add(MarketClearanceConstant.COLUMNS_MISMATCH.getValue());
                }
                for (int col = 0; col < columnCount; col++) {
                    if (col < configColumnList.size()) {
                        objColumn = configColumnList.get(col);
                        sColumnName = objColumn.getColumnName();
                        excelRowCell = excelRow.getCell(col);
                        sExcelRowCellValue = formatter.formatCellValue(excelRowCell);
                        if (!sColumnName.equals(sExcelRowCellValue)) {
                            preCheckList.add(MarketClearanceConstant.COLUMN_POSITION_MISMATCH.getValue().replace(DomainConstants.SELECT_NAME, sColumnName).replace(MarketClearanceConstant.CONST_ORDER.getValue(), String.valueOf(objColumn.getColumnNumber())));
                        }
                    }
                }
            } else
                break;
        }
    }

    /**
     * Method to check if excel is valid
     *
     * @param sheet         - Iterator<Row> - iterator object.
     * @param preCheckList  - List<String>  - list of validations.
     * @param xmlColumnList - List<Column>  - column list
     * @return void
     * @since DSM 2018x.5
     */
    private void performExcelPreChecks(Sheet sheet, List<String> preCheckList, List<Column> xmlColumnList) {
        boolean hasHeaders = true;
        boolean isNumberOfColumnsEqual = false;
        int numberOfExcelColumns;
        boolean isSheetEmpty = false;
        String sheetName = MarketClearanceConstant.SHEET_NAME_MARKET_CLEARANCE.getValue();
        int rowCount = sheet.getPhysicalNumberOfRows();
        if (rowCount == 1) {
            isSheetEmpty = true;
        }
        if (!sheetName.equals(sheet.getSheetName())) {
            preCheckList.add(MarketClearanceConstant.SHEET_NAME_MISMATCH.getValue() + pgV3Constants.SYMBOL_SPACE + sheetName);
        }
        DataFormatter formatter = new DataFormatter();
        int numberOfXMLColumns = xmlColumnList.size();

        // below variables are use during iteration.
        XSSFRow excelRow;
        Column objXMLColumn;
        String sXMLColumnName;
        XSSFCell excelRowCell;
        String sExcelRowCell;

        Iterator<Row> rowIterator = sheet.rowIterator();
        if (rowIterator.hasNext()) {
            while (rowIterator.hasNext()) {
                excelRow = (XSSFRow) rowIterator.next();
                numberOfExcelColumns = excelRow.getPhysicalNumberOfCells();

                if (excelRow.getRowNum() == 0) {
                    isNumberOfColumnsEqual = numberOfExcelColumns == numberOfXMLColumns;
                    if (checkIfRowIsEmpty(excelRow)) {
                        hasHeaders = false;
                    } else {
                        for (int col = 0; col < numberOfExcelColumns; col++) {
                            if (col < xmlColumnList.size()) {
                                objXMLColumn = xmlColumnList.get(col);
                                sXMLColumnName = objXMLColumn.getColumnName();
                                excelRowCell = excelRow.getCell(col);
                                sExcelRowCell = formatter.formatCellValue(excelRowCell);
                                if (UIUtil.isNotNullAndNotEmpty(sExcelRowCell) && !sXMLColumnName.equals(sExcelRowCell) && isNumberOfColumnsEqual) {
                                    preCheckList.add(MarketClearanceConstant.COLUMN_POSITION_MISMATCH.getValue().replace(DomainConstants.SELECT_NAME, sXMLColumnName).replace(MarketClearanceConstant.CONST_ORDER.getValue(), String.valueOf(objXMLColumn.getColumnNumber())));
                                }
                            }
                        }
                    }
                } else {
                    if (checkIfRowIsEmpty(excelRow)) {
                        break;
                    }
                }
            }
        } else {
            hasHeaders = false;
            isSheetEmpty = true;
        }
        if (!hasHeaders) {
            if (isSheetEmpty)
                preCheckList.add(MarketClearanceConstant.SHEET_DATA_EMPTY.getValue());
        } else {
            if (!isNumberOfColumnsEqual)
                preCheckList.add(MarketClearanceConstant.COLUMNS_MISMATCH.getValue() + numberOfXMLColumns);
            if (isSheetEmpty)
                preCheckList.add(MarketClearanceConstant.SHEET_NO_RECORDS.getValue());
        }
    }

    /**
     * Method to perform excel checks
     *
     * @param context             - Context - context
     * @param xmlBean             - XML     - xml bean object
     * @param sInputExcelFilePath - String  - file path string
     * @param identityFlag        - boolean - true/false
     * @return String - output string
     * @since DSM 2018x.5
     */
    public String getExcelPreChecks(Context context, XML xmlBean, String sInputExcelFilePath, boolean identityFlag) throws IOException {
        Instant start = Instant.now();
        List<String> preCheckList = new ArrayList<>();
        if (xmlBean != null) {
            List<Column> xmlColumnList = xmlBean.getColumns().get(0).getColumns();
            FileInputStream excelFileInputStream;
            File excelFile = new File(sInputExcelFilePath);
            excelFileInputStream = new FileInputStream(excelFile);
            Workbook excelWorkbook = new XSSFWorkbook(excelFileInputStream);
            Sheet excelSheet = excelWorkbook.getSheetAt(0);
            performExcelPreChecks(excelSheet, preCheckList, xmlColumnList);
        }
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Number of failed pre-checks|" + preCheckList.size() + "|Checks|" + preCheckList);
        }
        logger.info(new MarketClearanceUtil().getExecutionTimeString(start));
        return getExcelPreCheckMessage(preCheckList);
    }

    /**
     * Method to create create excel
     *
     * @param context               - Context - context
     * @param excelBean             - Excel   - bean object
     * @param sUserWorkspace        - String   - workspace
     * @param bClearanceRequestFlag - boolean - true/false
     * @return void
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    public void createErrorExcel(Context context, Excel excelBean, String sUserWorkspace, boolean bClearanceRequestFlag) throws IOException {
        int numberOfFailedValidationRecordCounter = 0;
        StringBuilder errorFilePathBuilder = new StringBuilder();
        errorFilePathBuilder.append(sUserWorkspace);
        errorFilePathBuilder.append(File.separator);
        errorFilePathBuilder.append(MarketClearanceConstant.VALIDATION_ERRORS_FILE_NAME.getValue());
        String filePath = errorFilePathBuilder.toString();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(MarketClearanceConstant.CONST_VALIDATION_ERRORS.getValue());

        CellStyle excelRowCellStyle = workbook.createCellStyle();
        excelRowCellStyle.setWrapText(true);

        Row excelRow = sheet.createRow(0);
        Cell rowNumber = excelRow.createCell(0);
        rowNumber.setCellValue(MarketClearanceConstant.CONST_ROW.getValue());
        Cell rowGcas = excelRow.createCell(1);
        rowGcas.setCellValue(MarketClearanceConstant.CONST_GCAS.getValue());
        Cell rowMarket = excelRow.createCell(2);
        rowMarket.setCellValue(MarketClearanceConstant.CONST_MARKET.getValue());
        Cell colName = excelRow.createCell(3);
        colName.setCellValue(MarketClearanceConstant.CONST_PROBLEMATIC_COLUMN_NAME.getValue());
        Cell errorMessage = excelRow.createCell(4);
        errorMessage.setCellValue(MarketClearanceConstant.CONST_ERROR_MESSAGE.getValue());

        List<EachRow> excelBeanRows = excelBean.getRows();
        int numberOfRows = excelBeanRows.size();

        int insertionRow = 1;

        // below variables are used during iteration.
        Set<String> failedColumnNames;
        List<EachColumn> excelBeanColumnList;
        List<EachColumnCheck> excelBeanColumnCheckList;
        String validationMessage;

        Row rowNew;
        Cell rowNumberNew;
        Cell rowGcasNew;
        Cell rowMarketNew;
        Cell colNameNew;
        Cell errorMessageNew;
        for (EachRow objEachRow : excelBeanRows) {
            failedColumnNames = objEachRow.getFailedColumnNames();
            if ((failedColumnNames != null && !failedColumnNames.isEmpty())) {
                numberOfFailedValidationRecordCounter++;
            }
            excelBeanColumnList = objEachRow.getColumnList();
            for (EachColumn objEachColumn : excelBeanColumnList) {
                excelBeanColumnCheckList = objEachColumn.getColumnCheckList();
                for (EachColumnCheck objEachColumnCheck : excelBeanColumnCheckList) {
                    validationMessage = objEachColumnCheck.getValidationMessage();
                    if (UIUtil.isNotNullAndNotEmpty(validationMessage)) {
                        rowNew = sheet.createRow(insertionRow);
                        rowNumberNew = rowNew.createCell(0);
                        rowNumberNew.setCellValue(String.valueOf(objEachRow.getRowNumber()));
                        rowGcasNew = rowNew.createCell(1);
                        rowGcasNew.setCellValue(String.valueOf(objEachRow.getGCAS()));
                        rowMarketNew = rowNew.createCell(2);
                        rowMarketNew.setCellValue(String.valueOf(objEachRow.getCOUNTRY_REQUESTED()));
                        colNameNew = rowNew.createCell(3);
                        colNameNew.setCellValue(objEachColumnCheck.getColumnName());
                        errorMessageNew = rowNew.createCell(4);
                        errorMessageNew.setCellValue(validationMessage);
                        insertionRow++;
                    } else {
                        if (bClearanceRequestFlag && insertionRow <= numberOfRows && !objEachRow.isGCASOverAllClearanceRequestPassed()) {
                            rowNew = sheet.createRow(insertionRow);
                            rowNumberNew = rowNew.createCell(0);
                            rowNumberNew.setCellValue(String.valueOf(objEachRow.getRowNumber()));
                            rowGcasNew = rowNew.createCell(1);
                            rowGcasNew.setCellValue(String.valueOf(objEachRow.getGCAS()));
                            rowMarketNew = rowNew.createCell(2);
                            rowMarketNew.setCellValue(String.valueOf(objEachRow.getCOUNTRY_REQUESTED()));
                            colNameNew = rowNew.createCell(3);
                            colNameNew.setCellValue(DomainConstants.EMPTY_STRING);
                            errorMessageNew = rowNew.createCell(4);
                            errorMessageNew.setCellValue(MarketClearanceConstant.MARKET_CLEARANCE_CALC_ERROR.getValue() + pgV3Constants.SYMBOL_SPACE + objEachRow.getCalculateClearanceStatusErrorMessage());
                            insertionRow++;
                        }
                    }
                }
            }
        }
        writeErrorExcel(excelBean, workbook, filePath, numberOfFailedValidationRecordCounter);
    }

    /**
     * Method to create create excel
     *
     * @param excelBean                             - Excel - bean object
     * @param workbook                              - XSSFWorkbook   - bean object
     * @param sFilePath                             - String   - file path
     * @param numberOfFailedValidationRecordCounter - int -
     * @return void
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    public void writeErrorExcel(Excel excelBean, XSSFWorkbook workbook, String sFilePath, int numberOfFailedValidationRecordCounter) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(sFilePath)) {
            workbook.write(outputStream);
            excelBean.setErrorFilePath(sFilePath);
            excelBean.setErrorFileCreated(true);
            excelBean.setNumberOfFailedValidationRecords(numberOfFailedValidationRecordCounter);
            int totalRecords = excelBean.getTotalRecords();
            int numberOfFailedValidationRecords = excelBean.getNumberOfFailedValidationRecords();
            int numberOfPassedValidationRecords = excelBean.getTotalRecords() - numberOfFailedValidationRecords;
            if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                logger.info("Created Error Excel Report !");
            }
            excelBean.setExcelProcessMessage(buildMessageForValidationRecords(excelBean.getExcelProcessStartTime(), totalRecords, numberOfFailedValidationRecords, numberOfPassedValidationRecords));
        } catch (IOException e) {
            excelBean.setErrorFileCreated(false);
            throw e;
        }
    }

    /**
     * Method to build message string
     *
     * @param preCheckList - List<String> - context
     * @return String
     * @since DSM 2018x.5
     */
    private String getExcelPreCheckMessage(List<String> preCheckList) {
        StringBuilder messageBuilder = new StringBuilder();
        int preCheckListSize = preCheckList.size();
        if (preCheckListSize == 1 && !preCheckList.contains(MarketClearanceConstant.SHEET_NO_RECORDS.getValue())) {
            messageBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
            messageBuilder.append(MarketClearanceConstant.ALERT_EXCEL_GENERIC_MESSAGE.getValue());
            messageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        }
        if (preCheckListSize > 1) {
            messageBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
            messageBuilder.append(MarketClearanceConstant.ALERT_EXCEL_GENERIC_MESSAGE.getValue());
            messageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        }
        for (String sMessage : preCheckList) {
            messageBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
            messageBuilder.append(sMessage);
            messageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        }
        return messageBuilder.toString();
    }

    /**
     * Method to create excel
     *
     * @param context        - Context - context
     * @param excelBean      - Excel   - bean object
     * @param sUserWorkspace - String   -  workspace
     * @return void
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    public void createProcessedExcel(Context context, Excel excelBean, String sUserWorkspace) throws IOException {
        int numberOfProcessedSucessRecordCounter = 0;
        StringBuilder errorFilePathBuilder = new StringBuilder();
        errorFilePathBuilder.append(sUserWorkspace);
        errorFilePathBuilder.append(File.separator);
        errorFilePathBuilder.append(MarketClearanceConstant.PROCESSED_EXCEL_FILE_NAME.getValue());
        String filePath = errorFilePathBuilder.toString();
        XSSFWorkbook excelWorkbook = new XSSFWorkbook();
        XSSFSheet excelWorkbookSheet = excelWorkbook.createSheet(MarketClearanceConstant.CONST_PROCESSED_RECORDS_INFO.getValue());
        CellStyle excleRowCellStyle = excelWorkbook.createCellStyle();
        excleRowCellStyle.setWrapText(true);
        Row excelRow = excelWorkbookSheet.createRow(0);
        Cell rowNumber = excelRow.createCell(0);
        rowNumber.setCellValue(MarketClearanceConstant.CONST_ROW.getValue());
        Cell rowGcas = excelRow.createCell(1);
        rowGcas.setCellValue(MarketClearanceConstant.CONST_GCAS.getValue());
        Cell rowMarket = excelRow.createCell(2);
        rowMarket.setCellValue(MarketClearanceConstant.CONST_MARKET.getValue());
        Cell colName = excelRow.createCell(3);
        colName.setCellValue(MarketClearanceConstant.CONST_RECORD_PROCESSED.getValue());
        Cell errorMessage = excelRow.createCell(4);
        errorMessage.setCellValue(MarketClearanceConstant.CONST_RECORD_PROCESSED_FAILURE_MESSAGE.getValue());
        List<EachRow> excelBeanRows = excelBean.getRows();
        int insertionRow = 1;

        Row rowNew;
        Cell rowNumberNew;
        Cell rowGcasNew;
        Cell rowMarketNew;
        Cell colNameNew;
        Cell errorMessageNew;

        for (EachRow objEachRow : excelBeanRows) {
            rowNew = excelWorkbookSheet.createRow(insertionRow);
            rowNumberNew = rowNew.createCell(0);
            rowNumberNew.setCellValue(String.valueOf(objEachRow.getRowNumber()));
            rowGcasNew = rowNew.createCell(1);
            rowGcasNew.setCellValue(String.valueOf(objEachRow.getGCAS()));
            rowMarketNew = rowNew.createCell(2);
            rowMarketNew.setCellValue(String.valueOf(objEachRow.getCOUNTRY_REQUESTED()));
            colNameNew = rowNew.createCell(3);
            colNameNew.setCellValue((objEachRow.isRecordProcessed()) ? pgV3Constants.KEY_YES_VALUE : pgV3Constants.KEY_NO_VALUE);
            errorMessageNew = rowNew.createCell(4);
            errorMessageNew.setCellValue((objEachRow.isRecordProcessed()) ? MarketClearanceConstant.CONST_NA.getValue() : objEachRow.getRecordFailureMessage());
            if (objEachRow.isRecordProcessed())
                numberOfProcessedSucessRecordCounter++;
            insertionRow++;
        }
        writeProcessedExcel(excelBean, excelWorkbook, filePath, numberOfProcessedSucessRecordCounter);
    }

    /**
     * Method to create create excel
     *
     * @param excelBean                            - Excel - bean object
     * @param excelWorkbook                        - XSSFWorkbook   - bean object
     * @param sFilePath                            - String   - file path
     * @param numberOfProcessedSucessRecordCounter - int -
     * @return void
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    public void writeProcessedExcel(Excel excelBean, XSSFWorkbook excelWorkbook, String sFilePath, int numberOfProcessedSucessRecordCounter) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(sFilePath)) {
            excelWorkbook.write(outputStream);
            excelBean.setProcessedFilePath(sFilePath);
            excelBean.setProcessedFileCreated(true);
            excelBean.setNumberOfProcessedSuccessRecords(numberOfProcessedSucessRecordCounter);
            int totalRecords = excelBean.getTotalRecords();
            int numberOfPassedRecords = excelBean.getNumberOfProcessedSuccessRecords();
            int numberOfFailedRecords = excelBean.getTotalRecords() - excelBean.getNumberOfProcessedSuccessRecords();
            if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                logger.info("Created Processed Excel Report !");
            }
            excelBean.setNumberOfProcessedFailedRecords(numberOfFailedRecords);
            excelBean.setExcelProcessMessage(buildMessageForProcessedRecords(excelBean.getExcelProcessStartTime(), totalRecords, numberOfFailedRecords, numberOfPassedRecords));
        } catch (IOException e) {
            excelBean.setProcessedFileCreated(false);
            throw e;
        }
    }

    /**
     * Method to build message for processed records
     *
     * @param uploadTime    - String
     * @param totalRecords  - int
     * @param failedRecords - int
     * @param passedRecords - int
     * @return void
     * @since DSM 2018x.5
     */
    public String buildMessageForProcessedRecords(String uploadTime, int totalRecords, int failedRecords, int passedRecords) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(MarketClearanceConstant.UPLOADED_APPROX_TIME.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(uploadTime);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        sBuilder.append(MarketClearanceConstant.TOTAL_NUMBER_OF_RECORDS.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(totalRecords);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        sBuilder.append(MarketClearanceConstant.NUMBER_OF_RECORDS_PROCESSED_SUCCESSFULLY.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(passedRecords);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        sBuilder.append(MarketClearanceConstant.NUMBER_OF_RECORDS_NOT_PROCESSED.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(failedRecords);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        return sBuilder.toString();
    }

    /**
     * Method to build message for failed records
     *
     * @param uploadTime    - String
     * @param totalRecords  - int
     * @param failedRecords - int
     * @param passedRecords - int
     * @return void
     * @since DSM 2018x.5
     */
    public String buildMessageForValidationRecords(String uploadTime, int totalRecords, int failedRecords, int passedRecords) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(MarketClearanceConstant.UPLOADED_APPROX_TIME.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(uploadTime);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        sBuilder.append(MarketClearanceConstant.TOTAL_NUMBER_OF_RECORDS.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(totalRecords);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        sBuilder.append(MarketClearanceConstant.NUMBER_OF_RECORDS_PASSED_VALIDATION.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(passedRecords);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        sBuilder.append(MarketClearanceConstant.NUMBER_OF_RECORDS_FAILED_VALIDATION.getValue());
        sBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sBuilder.append(failedRecords);
        sBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        return sBuilder.toString();
    }

    /**
     * Method to update repeated gcas & market combination flag
     *
     * @param excel      - Excel
     * @param objEachRow - EachRow
     * @return void
     * @since DSM 2018x.5
     */
    public void updateGcasMarketRepeatedFlag(Excel excel, EachRow objEachRow) {
        String gcasMarketCombo = objEachRow.getGCAS() + pgV3Constants.SYMBOL_COMMA + objEachRow.getCOUNTRY_REQUESTED();
        List<String> gcasMarketCombolist = excel.getGcasMarketCombolist();
        if (gcasMarketCombolist != null) {
            if (!gcasMarketCombolist.contains(gcasMarketCombo)) {
                gcasMarketCombolist.add(gcasMarketCombo);
                objEachRow.setGcasMarketCombinatedRepeated(false);
            } else {
                objEachRow.setGcasMarketCombinatedRepeated(true);
            }
        }
    }
}
