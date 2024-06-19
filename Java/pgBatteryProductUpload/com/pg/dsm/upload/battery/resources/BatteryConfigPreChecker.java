/*
 **   BatteryConfigPreChecker.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Utility Class to perform battery data load pre-checks.
 **
 */
package com.pg.dsm.upload.battery.resources;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;
import com.pg.dsm.upload.battery.models.config.BatteryProductFeature;
import com.pg.dsm.upload.battery.models.config.BatteryProducts;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BatteryConfigPreChecker {
    List<String> preCheckList;
    String inputExcelFilePath;
    String batteryProductIdentifier;
    String batteryProductSchemaType;
    BatteryProduct batteryProduct;

    private BatteryConfigPreChecker(Builder builder) {
        this.inputExcelFilePath = builder.inputExcelFilePath;
        this.preCheckList = builder.preCheckList;
        this.batteryProductIdentifier = builder.batteryProductIdentifier;
        this.batteryProductSchemaType = builder.batteryProductSchemaType;
        this.batteryProduct = builder.batteryProduct;
    }

    public List<String> getPreCheckList() {
        return preCheckList;
    }

    public String getInputExcelFilePath() {
        return inputExcelFilePath;
    }

    public String getBatteryProductIdentifier() {
        return batteryProductIdentifier;
    }

    public String getBatteryProductSchemaType() {
        return batteryProductSchemaType;
    }

    public BatteryProduct getBatteryProduct() {
        return batteryProduct;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());

        BatteryPropertyConfig batteryPropertyConfig;
        BatteryProducts batteryProducts;
        List<String> preCheckList;
        BatteryProduct batteryProduct;

        String inputExcelFilePath;
        String batteryProductIdentifier;
        String batteryProductSchemaType;

        List<BatteryProductFeature> batteryProductFeatureList;

        public Builder(BatteryProducts batteryProducts, BatteryPropertyConfig batteryPropertyConfig) {
            preCheckList = new ArrayList<>();
            this.batteryProducts = batteryProducts;
            this.batteryPropertyConfig = batteryPropertyConfig;
            this.inputExcelFilePath = batteryPropertyConfig.getInputFileNamePath();
        }

        public BatteryConfigPreChecker build() {
            performAllChecks();
            return new BatteryConfigPreChecker(this);
        }

        public void performAllChecks() {
            // check if file exist. return file path.
            if (checkInputExcelFileExistence()) {
                // perform excel pre-checks.
                performExcelPreChecks(new File(inputExcelFilePath));
            }
        }

        public boolean checkInputExcelFileExistence() {
            boolean bInputExcelExist = false;
            logger.info("Check Existence of Input Excel Template in input folder: " + inputExcelFilePath);
            File inputExcelFile = new File(inputExcelFilePath);
            if (!inputExcelFile.exists()) {
                preCheckList.add(batteryPropertyConfig.getErrorNoInputFile().concat(inputExcelFile.getName()));
            } else {
                bInputExcelExist = true;
                logger.info("Input Excel File Exist in input folder: " + inputExcelFilePath);
            }
            return bInputExcelExist;
        }

        public void performExcelPreChecks(File inputExcelFile) {
            if (inputExcelFile.exists()) {
                try {
                    Workbook workbook = new XSSFWorkbook(new FileInputStream(inputExcelFile));
                    int excelInputTabPosition = batteryPropertyConfig.getExcelInputTabPosition();
                    logger.info("Excel Template Input Sheet Tab Position: " + excelInputTabPosition);
                    Sheet sheet = workbook.getSheetAt(excelInputTabPosition);
                    String sheetName = sheet.getSheetName();
                    logger.info("Excel Template Input Sheet Name: " + sheet.getSheetName());
                    if (batteryPropertyConfig.getExcelTabDPP().equals(sheetName)) {
                        this.batteryProduct = batteryProducts.getBatteryProducts().get(0);

                        batteryProductFeatureList = batteryProduct.getBatteryProductFeatures();
                        batteryProductIdentifier = batteryProduct.getIdentifier();
                        batteryProductSchemaType = batteryProduct.getSchema();
                    }
                    if (batteryPropertyConfig.getExcelTabRM().equals(sheetName)) {
                        this.batteryProduct = batteryProducts.getBatteryProducts().get(1);

                        batteryProductFeatureList = batteryProduct.getBatteryProductFeatures();
                        batteryProductIdentifier = batteryProduct.getIdentifier();
                        batteryProductSchemaType = batteryProduct.getSchema();
                    }

                    getExcelSheetPreChecks(sheet);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }


        /**
         * Method to get excel sheet pre checks
         *
         * @param sheet Sheet
         * @return void
         * @since DSM 2018x.6
         */
        private void getExcelSheetPreChecks(Sheet sheet) {
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            logger.info(physicalNumberOfRows);
            if (physicalNumberOfRows != 0) {
                logger.info("Sheet is not empty");
                Iterator<Row> rowIterator = sheet.rowIterator();
                XSSFRow xssfRow;
                while (rowIterator.hasNext()) {
                    xssfRow = ((XSSFRow) rowIterator.next());
                    if (xssfRow.getRowNum() != 0)
                        break;
                    getExcelSheetHeaderPreCheck(xssfRow);
                    if (physicalNumberOfRows == 1) {
                        logger.info("Sheet has only 1 row");
                        preCheckList.add(batteryPropertyConfig.getErrorNoRecords());
                    }
                }
            } else {
                logger.info("Sheet is empty");
                preCheckList.add(batteryPropertyConfig.getErrorEmptySheet());
            }
        }

        /**
         * Method to get excel sheet header checks
         *
         * @param xssfRow - XSSFRow
         * @return void
         * @since DSM 2018x.6
         */
        private void getExcelSheetHeaderPreCheck(XSSFRow xssfRow) {
            if (xssfRow.getRowNum() == 0) {
                if (!checkIfRowIsEmpty(xssfRow)) {
                    if (batteryProductFeatureList.size() != xssfRow.getLastCellNum()) {
                        preCheckList.add(batteryPropertyConfig.getErrorIncorrectColumnCount());
                    }
                    logger.info("First Row is not empty");
                    getExcelSheetHeaderNameCheck(xssfRow);
                } else {
                    logger.info("First Row is empty");
                }
            }
        }

        /**
         * Method to get excel sheet header name check
         *
         * @param xssfRow - XSSFRow
         * @return void
         * @since DSM 2018x.6
         */
        private void getExcelSheetHeaderNameCheck(XSSFRow xssfRow) {
            int numberOfXMLColumns = batteryProductFeatureList.size();
            int numberOfExcelColumns = xssfRow.getLastCellNum();
            DataFormatter formatter = new DataFormatter();
            BatteryProductFeature batteryProductFeature;
            String columnName;
            XSSFCell cell;
            String cellValue;
            StringBuilder messageBuilder;
            for (int col = 0; col < numberOfExcelColumns; col++) {
                if (col < numberOfXMLColumns) {
                    messageBuilder = new StringBuilder();
                    batteryProductFeature = batteryProductFeatureList.get(col);
                    columnName = batteryProductFeature.getName();
                    cell = xssfRow.getCell(col);
                    cellValue = formatter.formatCellValue(cell);
                    if (UIUtil.isNotNullAndNotEmpty(cellValue) && !columnName.equals(cellValue.trim())) {
                        messageBuilder.append(BatteryConstants.Basic.CONST_COLUMN_COLON.getValue());
                        messageBuilder.append(columnName);
                        messageBuilder.append(batteryPropertyConfig.getErrorMismatchColumnPosition());
                        messageBuilder.append(batteryProductFeature.getOrder());
                        preCheckList.add(messageBuilder.toString());
                    }
                }
            }
        }

        /**
         * Method to check whether row is empty or not
         *
         * @param row - XSSFRow
         * @return boolean
         * @since DSM 2018x.6
         */
        private boolean checkIfRowIsEmpty(XSSFRow row) {
            if (row == null) {
                return true;
            }
            if (row.getLastCellNum() <= 0) {
                return true;
            }
            Cell cell;
            for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
                cell = row.getCell(cellNum);
                if (cell != null && cell.getCellType() != CellType.BLANK && UIUtil.isNotNullAndNotEmpty(cell.toString())) {
                    return false;
                }
            }
            return true;
        }
    }
}
