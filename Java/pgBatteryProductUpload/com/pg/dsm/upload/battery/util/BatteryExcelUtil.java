/*
 **   BatteryExcelUtil.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Utility Class to carry out Excel IO operations.
 **
 */
package com.pg.dsm.upload.battery.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.interfaces.ProductPartBean;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;
import com.pg.dsm.upload.battery.models.config.BatteryProductFeature;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BatteryExcelUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    String filePath;
    BatteryPropertyConfig batteryPropertyConfig;

    public BatteryExcelUtil(String filePath, BatteryPropertyConfig batteryPropertyConfig) {
        this.filePath = filePath;
        this.batteryPropertyConfig = batteryPropertyConfig;
    }

    public Map<String, List<String>> getNameRevisionFromExcel() {
        Instant startTime = Instant.now();
        Map<String, List<String>> nameRevisionMap = new HashMap<>();

        List<String> nameList = new ArrayList<>();
        List<String> revisionList = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            Sheet sheet = workbook.getSheetAt(batteryPropertyConfig.getExcelInputTabPosition());
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            logger.info("Excel Total rows:" + physicalNumberOfRows);
            DataFormatter formatter = new DataFormatter();
            XSSFRow xssfRow;

            String xssfCellValue;
            Iterator<Row> rowIterator = sheet.rowIterator();

            while (rowIterator.hasNext()) {
                xssfRow = ((XSSFRow) rowIterator.next());
                if (!checkIfRowIsEmpty(xssfRow)) {
                    for (int col = 0; col < 2; col++) {
                        xssfCellValue = formatter.formatCellValue(xssfRow.getCell(col));
                        if (col == 0) {
                            nameList.add(xssfCellValue);
                        }
                        if (col == 1) {
                            revisionList.add(xssfCellValue);
                        }
                    }
                }
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info(String.format("Get all Name & Revision from Input Excel took|%s %s|%s %s|%s %s",duration.toMillis(),BatteryConstants.Basic.MILLI_SECONDS.getValue(),duration.getSeconds(),BatteryConstants.Basic.SECONDS.getValue(),duration.toMinutes(),BatteryConstants.Basic.MINUTES.getValue()));
            nameRevisionMap.put(DomainConstants.SELECT_NAME, nameList);
            nameRevisionMap.put(DomainConstants.SELECT_REVISION, revisionList);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return nameRevisionMap;
    }

    /**
     * Method to check whether excel row empty or not.
     *
     * @param row - XSSFRow
     * @return boolean
     * @since DSM 2018x.6
     */
    private boolean checkIfRowIsEmpty(XSSFRow row) {
        if (row == null) {
            return true;
        }
        if (row.getRowNum() == 0) {
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

    public List<BatteryProduct> getExcelDataBean(Map<String, ProductPartBean> productPartBeanMap, BatteryProduct batteryProductXML) {
        Instant startTime = Instant.now();
        List<BatteryProduct> batteryProductList = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            int inputExcelTabPosition = batteryPropertyConfig.getExcelInputTabPosition();
            Sheet sheet = workbook.getSheetAt(inputExcelTabPosition);

            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            logger.info("Excel Total rows:" + physicalNumberOfRows);
            DataFormatter formatter = new DataFormatter();
            Iterator<Row> rowIterator = sheet.rowIterator();
            XSSFRow xssfRow;
            XSSFCell xssfCell;
            String cellValueStr;
            BatteryProductFeature batteryProductFeatureXML;
            BatteryProductFeature batteryProductFeatureXL;
            Method attributeSetterMethod;
            BatteryProduct batteryProductXL;
            List<BatteryProductFeature> batteryProductFeaturesXL;

            List<BatteryProductFeature> batteryProductFeaturesXML = batteryProductXML.getBatteryProductFeatures();
            int featuresCount = batteryProductFeaturesXML.size();

            while (rowIterator.hasNext()) {
                xssfRow = ((XSSFRow) rowIterator.next());
                if (xssfRow.getRowNum() != 0 && !checkIfRowIsEmpty(xssfRow)) {
                    batteryProductXL = new BatteryProduct();
                    BeanUtils.copyProperties(batteryProductXL, batteryProductXML);
                    batteryProductFeaturesXL = new ArrayList<>();

                    for (int featureCounter = 0; featureCounter < featuresCount; featureCounter++) {
                        xssfCell = xssfRow.getCell(featureCounter);
                        batteryProductFeatureXML = batteryProductFeaturesXML.get(featureCounter);
                        cellValueStr = formatter.formatCellValue(xssfCell);
                        batteryProductFeatureXML.setValue(cellValueStr);
                        batteryProductFeatureXL = new BatteryProductFeature();
                        BeanUtils.copyProperties(batteryProductFeatureXL, batteryProductFeatureXML);
                        attributeSetterMethod = batteryProductXL.getClass().getDeclaredMethod(batteryProductFeatureXL.getSetter(), String.class);
                        attributeSetterMethod.invoke(batteryProductXL, batteryProductFeatureXL.getValue());
                        batteryProductFeaturesXL.add(batteryProductFeatureXL);
                    }
                    String tempKeyString = generateNameRevisionKey(batteryProductXL);
                    if (productPartBeanMap.containsKey(tempKeyString)) {
                        batteryProductXL.setBeanExist(true);
                        ProductPartBean productPartBean = productPartBeanMap.get(tempKeyString);
                        batteryProductXL.setProductPartBean(productPartBean);
                    } else {
                        batteryProductXL.setBeanExist(false);
                    }
                    batteryProductXL.setBatteryProductFeatures(batteryProductFeaturesXL);
                    batteryProductList.add(batteryProductXL);
                }
            }
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info(String.format("Converting Excel & DB Data to Bean took|%s %s|%s %s|%s %s",duration.toMillis(),BatteryConstants.Basic.MILLI_SECONDS.getValue(),duration.getSeconds(),BatteryConstants.Basic.SECONDS.getValue(),duration.toMinutes(),BatteryConstants.Basic.MINUTES.getValue()));
        
        return batteryProductList;
    }

    /**
     * Method to generate Name and Revision key.
     *
     * @param batteryProduct BatteryProduct - bean object
     * @return String
     * @since DSM 2018x.6
     */
    private String generateNameRevisionKey(BatteryProduct batteryProduct) {
        StringBuilder identifierBuilder = new StringBuilder();
        identifierBuilder.append(batteryProduct.getName());
        identifierBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        identifierBuilder.append(batteryProduct.getRevision());
        return identifierBuilder.toString();
    }

    public void createRestoreExcel(List<BatteryProduct> batteryProductList) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            // create sheet name with product identifier (DPP/RM)
            XSSFSheet sheet = workbook.createSheet(batteryProductList.get(0).getIdentifier());
            CellStyle excelRowCellStyle = workbook.createCellStyle();
            excelRowCellStyle.setWrapText(true);
            createRestoreExcelReportHeaders(sheet, getRestoreExcelReportHeaders(batteryProductList));

            Row excelRow;
            Cell excelRowCell;
            int insertionRow = 1;
            List<String> excelColumnIdentifiers = getExcelColumnIdentifiers(batteryProductList);

            Method attributeGetterMethod;
            ProductPartBean productPartBean;
            for (BatteryProduct batteryProduct : batteryProductList) {
                if (batteryProduct.isBeanExist()) {
                    productPartBean = batteryProduct.getProductPartBean();
                    excelRow = sheet.createRow(insertionRow);
                    for (int i = 0; i < excelColumnIdentifiers.size(); i++) {
                        excelRowCell = excelRow.createCell(i);
                        attributeGetterMethod = productPartBean.getClass().getDeclaredMethod("get".concat(excelColumnIdentifiers.get(i)));
                        excelRowCell.setCellValue((String) attributeGetterMethod.invoke(productPartBean));
                    }
                    insertionRow++;
                }
            }

            createRestoreExcelWorkbook(workbook);
        } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to create Restore Excel Workbook
     *
     * @param workbook - XSSFWorkbook
     * @throws IOException - exception
     * @since DSM 2018x.6
     */
    private void createRestoreExcelWorkbook(XSSFWorkbook workbook) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
            logger.info("RESTORE Excel File was created at " + filePath);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Method to get Excel Column Identifiers
     *
     * @param batteryProductList - List<BatteryProduct> - list of bean object
     * @return List<String>
     * @since DSM 2018x.6
     */
    private List<String> getExcelColumnIdentifiers(List<BatteryProduct> batteryProductList) {
        List<String> identifierList = new ArrayList<>();
        BatteryProduct batteryProduct = batteryProductList.get(0);
        List<BatteryProductFeature> batteryProductFeatureList = batteryProduct.getBatteryProductFeatures();
        for (BatteryProductFeature batteryProductFeature : batteryProductFeatureList) {
            identifierList.add(batteryProductFeature.getIdentifier());
        }
        return identifierList;
    }

    /**
     * Method to create Restore Excel Report Headers
     *
     * @param sheet      - Sheet
     * @param headerList - List<String>
     * @since DSM 2018x.6
     */
    private void createRestoreExcelReportHeaders(Sheet sheet, List<String> headerList) {
        Row excelRow = sheet.createRow(0);
        Cell excelRowCell;
        for (int i = 0; i < headerList.size(); i++) {
            excelRowCell = excelRow.createCell(i);
            excelRowCell.setCellValue(headerList.get(i));
        }
    }

    /**
     * Method to Restore Excel Report Headers
     *
     * @param batteryProductList - List<BatteryProductFeature> - list of bean object
     * @return List<String>
     * @since DSM 2018x.6
     */
    private List<String> getRestoreExcelReportHeaders(List<BatteryProduct> batteryProductList) {
        List<String> headerList = new ArrayList<>();
        BatteryProduct batteryProduct = batteryProductList.get(0);
        List<BatteryProductFeature> batteryProductFeatureList = batteryProduct.getBatteryProductFeatures();
        for (BatteryProductFeature batteryProductFeature : batteryProductFeatureList) {
            headerList.add(batteryProductFeature.getName());
        }
        return headerList;
    }

    public void createErrorExcel(List<BatteryProduct> batteryProductList) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(batteryProductList.get(0).getIdentifier());
            CellStyle excelRowCellStyle = workbook.createCellStyle();
            excelRowCellStyle.setWrapText(true);
            createErrorExcelReportHeaders(sheet);

            Row excelRow;
            Cell excelCell;
            int insertionRow = 1;
            List<String> errorMessageList;
            List<String> tempList;
            for (BatteryProduct batteryProduct : batteryProductList) {
                errorMessageList = new ArrayList<>();
                tempList = batteryProduct.getErrorMessageList();
                if (null != tempList) {
                    errorMessageList.addAll(tempList);
                }
                if (!errorMessageList.isEmpty()) {
                    excelRow = sheet.createRow(insertionRow);
                    excelCell = excelRow.createCell(0);
                    excelCell.setCellValue(batteryProduct.getName());

                    excelCell = excelRow.createCell(1);
                    excelCell.setCellValue(batteryProduct.getRevision());

                    excelCell = excelRow.createCell(2);
                    excelCell.setCellValue(String.join("\n", errorMessageList));
                    insertionRow++;
                }
            }
            createErrorExcelWorkbook(workbook);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to create Error Excel Report
     *
     * @param sheet - Sheet
     * @since DSM 2018x.6
     */
    private void createErrorExcelReportHeaders(Sheet sheet) {
        Row excelRow = sheet.createRow(0);
        Cell excelCellFOPName = excelRow.createCell(0);
        // Name - column
        excelCellFOPName.setCellValue(batteryPropertyConfig.getErrorExcelNameColumn());
        Cell excelCellFOPRev = excelRow.createCell(1);

        // Revision - column
        excelCellFOPRev.setCellValue(batteryPropertyConfig.getErrorExcelRevisionColumn());
        Cell excelCellMessages = excelRow.createCell(2);

        // Validation Failure Message - column
        excelCellMessages.setCellValue(batteryPropertyConfig.getErrorExcelValidationFailureMessageColumn());
    }

    /**
     * Method to create Error Excel Workbook
     *
     * @param workbook - XSSFWorkbook
     * @throws IOException - exception
     * @since DSM 2018x.6
     */
    private void createErrorExcelWorkbook(XSSFWorkbook workbook) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
            logger.error("ERROR Excel File was created at " + filePath);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
