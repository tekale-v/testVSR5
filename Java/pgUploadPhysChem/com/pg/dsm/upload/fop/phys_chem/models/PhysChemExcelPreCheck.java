/*
 **   PhysChemExcelPreCheck.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Utility class to perform excel pre checks.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.models;

import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.phys_chem.models.bo.BusinessAreaBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductCategoryPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductTechnologyPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChem;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChemBean;
import com.pg.dsm.upload.fop.phys_chem.util.PhysChemFolderUtil;
import matrix.util.StringList;
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
import java.util.Properties;
import java.util.stream.Collectors;

public class PhysChemExcelPreCheck {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    List<String> preCheckList = new ArrayList<>();
    PhysChemBean physChemBean;
    List<PhysChem> physChemList;
    Properties physChemProperties;
    String path;

    /**
     * Constructor
     *
     * @param physChemBean - PhysChemBean - bean object
     * @since DSM 2018x.5
     */
    public PhysChemExcelPreCheck(Properties physChemProperties, PhysChemBean physChemBean, String fileName) {
        String inputExcelFilePath = PhysChemFolderUtil.getPhysChemInputExcelFilePath(fileName);
        File inputExcelFile = new File(inputExcelFilePath);
        if (inputExcelFile.exists()) {
            this.path = inputExcelFilePath;
            this.physChemBean = physChemBean;
            this.physChemList = physChemBean.getPhysChem();
            this.physChemProperties = physChemProperties;

            checkPhysChemLoggerConfigFileExist();
            checkPhysChemXMLConfigFileExist();
            checkPhysChemPropertyConfigFileExist();

            checkRequiredBusinessAreaExist();
            checkRequiredProductCategoryPlatformExist();
            checkRequiredProductTechnologyPlatform();

            performExcelPreChecks();
        } else {
            checkPhysChemInputExcelExist(fileName);
        }
    }

    /**
     * Method to check if input excel file is present in input folder, otherwise append error to list.
     *
     * @since DSM 2018x.5
     */
    public void checkPhysChemInputExcelExist(String fileName) {
        File inputExcelFile = new File(PhysChemFolderUtil.getPhysChemInputExcelFilePath(fileName));
        if (!inputExcelFile.exists()) {
        	StringBuilder fileErrorMessage = new StringBuilder();
        	fileErrorMessage.append(FormulationGeneralConstant.INPUT_EXCEL_FILE_LOG.getValue());
        	fileErrorMessage.append(fileName);
        	fileErrorMessage.append(FormulationGeneralConstant.DOES_NOT_EXIST_IN_INPUT_FOLDER.getValue());
            preCheckList.add(fileErrorMessage.toString());
        }
    }

    /**
     * Method to check if config property file is present in config folder, otherwise append error to list.
     *
     * @since DSM 2018x.5
     */
    public void checkPhysChemPropertyConfigFileExist() {
        File inputExcelFile = new File(PhysChemFolderUtil.getPhysChemPropertyConfigFilePath());
        if (!inputExcelFile.exists()) {
            preCheckList.add(FormulationGeneralConstant.PROPERTY_CONFIG_FILE_DOES_NOT_EXIST.getValue());
        }
    }

    /**
     * Method to check if config xml file is present in config folder, otherwise append error to list.
     *
     * @since DSM 2018x.5
     */
    public void checkPhysChemXMLConfigFileExist() {
        File inputExcelFile = new File(PhysChemFolderUtil.getPhysChemXMLConfigFilePath());
        if (!inputExcelFile.exists()) {
            preCheckList.add(FormulationGeneralConstant.XML_CONFIG_FILE_DOES_NOT_EXIST.getValue());
        }
    }

    /**
     * Method to check if logger config file is present in config folder, otherwise append error to list.
     *
     * @since DSM 2018x.5
     */
    public void checkPhysChemLoggerConfigFileExist() {
        File inputExcelFile = new File(PhysChemFolderUtil.getPhysChemLoggerConfigFilePath());
        if (!inputExcelFile.exists()) {
            preCheckList.add(FormulationGeneralConstant.XML_CONFIG_FILE_DOES_NOT_EXIST.getValue());
        }
    }

    /**
     * Method to get all the excel pre check.
     *
     * @return List<String> - have list of excel pre-checks
     * @since DSM 2018x.5
     */
    public List<String> getExcelPreCheck() {
        return preCheckList;
    }

    /**
     * Method to perform excel pre checks
     *
     * @return void
     * @since DSM 2018x.5
     */
    private void performExcelPreChecks() {
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(path)));
            int physChemTabPosition = Integer.parseInt(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_TAB_PHYSICAL_CHEMICAL_POSITION.getValue()));
            Sheet sheet = workbook.getSheetAt(physChemTabPosition);
            logger.info(sheet.getSheetName());
            getExcelSheetPreChecks(sheet);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to get excel sheet pre checks
     *
     * @param sheet Sheet
     * @return void
     * @since DSM 2018x.5
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
                    preCheckList.add(FormulationGeneralConstant.CONST_EXCEL_SHEET_NO_RECORDS.getValue());
                }
            }
        } else {
            logger.info("Sheet is empty");
            preCheckList.add(FormulationGeneralConstant.CONST_EXCEL_SHEET_EMPTY.getValue());
        }
    }

    /**
     * Method to get excel sheet header checks
     *
     * @param xssfRow - XSSFRow
     * @return void
     * @since DSM 2018x.5
     */
    private void getExcelSheetHeaderPreCheck(XSSFRow xssfRow) {
        if (xssfRow.getRowNum() == 0) {
            if (!checkIfRowIsEmpty(xssfRow)) {
                if (physChemList.size() != xssfRow.getLastCellNum()) {
                    preCheckList.add(FormulationGeneralConstant.CONST_EXCEL_COLUMNS_SIZE_INCORRECT.getValue());
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
     * @since DSM 2018x.5
     */
    private void getExcelSheetHeaderNameCheck(XSSFRow xssfRow) {
        int numberOfXMLColumns = physChemList.size();
        int numberOfExcelColumns = xssfRow.getLastCellNum();
        DataFormatter formatter = new DataFormatter();
        PhysChem physChem;
        String columnName;
        XSSFCell cell;
        String cellValue;
        StringBuilder messageBuilder;
        for (int col = 0; col < numberOfExcelColumns; col++) {
            if (col < numberOfXMLColumns) {
                messageBuilder = new StringBuilder();
                physChem = physChemList.get(col);
                columnName = physChem.getName();
                cell = xssfRow.getCell(col);
                cellValue = formatter.formatCellValue(cell);
                if (UIUtil.isNotNullAndNotEmpty(cellValue) && !columnName.equals(cellValue.trim())) {
                    messageBuilder.append(FormulationGeneralConstant.CONST_COLUMN_COLON.getValue());
                    messageBuilder.append(columnName);
                    messageBuilder.append(FormulationGeneralConstant.CONST_EXCEL_COLUMN_POSITION_MISMATCH_MESSAGE.getValue());
                    messageBuilder.append(physChem.getOrder());
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
     * @since DSM 2018x.5
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

    /**
     * Method to check Business Area for Solid
     *
     * @since DSM 2018x.5
     */
    private void isSolidRequiredBusinessAreaExist() {
        StringList inputBusinessAreaNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.SOLID_BUSINESS_AREA.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<BusinessAreaBean> businessAreaBeanList = physChemBean.getRequiredSolidBusinessAreaBean();
        List<String> dbBusinessAreaNames = businessAreaBeanList.stream().map(BusinessAreaBean::getName).collect(Collectors.toList());
        boolean exist = dbBusinessAreaNames.containsAll(inputBusinessAreaNames);
        inputBusinessAreaNames.removeAll(dbBusinessAreaNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.SOLID_INPUT_BUSINESS_AREA_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputBusinessAreaNames.toString()));
        }
    }

    /**
     * Method to check Business Area for Liquid
     *
     * @since DSM 2018x.5
     */
    private void isLiquidRequiredBusinessAreaExist() {
        StringList inputBusinessAreaNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_BUSINESS_AREA.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<BusinessAreaBean> businessAreaBeanList = physChemBean.getRequiredLiquidBusinessAreaBean();
        List<String> dbBusinessAreaNames = businessAreaBeanList.stream().map(BusinessAreaBean::getName).collect(Collectors.toList());
        boolean exist = dbBusinessAreaNames.containsAll(inputBusinessAreaNames);
        inputBusinessAreaNames.removeAll(dbBusinessAreaNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.LIQUID_INPUT_BUSINESS_AREA_DOES_NOT_EXIST.getValue());
        }
    }

    /**
     * Method to check Business Area for Aerosol
     *
     * @since DSM 2018x.5
     */
    private void isAerosolRequiredBusinessAreaExist() {
        StringList inputBusinessAreaNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_BUSINESS_AREA.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<BusinessAreaBean> businessAreaBeanList = physChemBean.getRequiredAerosolBusinessAreaBean();
        List<String> dbBusinessAreaNames = businessAreaBeanList.stream().map(BusinessAreaBean::getName).collect(Collectors.toList());
        boolean exist = dbBusinessAreaNames.containsAll(inputBusinessAreaNames);
        inputBusinessAreaNames.removeAll(dbBusinessAreaNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.AEROSOL_INPUT_BUSINESS_AREA_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputBusinessAreaNames.toString()));
        }
    }

    /**
     * Method to check if Business Area Exist
     *
     * @since DSM 2018x.5
     */
    private void checkRequiredBusinessAreaExist() {
        isSolidRequiredBusinessAreaExist();
        isLiquidRequiredBusinessAreaExist();
        isAerosolRequiredBusinessAreaExist();
    }

    /**
     * Method to check if Product Category Platform Exist for Aerosol
     *
     * @since DSM 2018x.5
     */
    private void isAerosolRequiredProductCategoryPlatformExist() {
        StringList inputProductCategoryPlatformNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_PRODUCT_CATEGORY_PLATFORM.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<ProductCategoryPlatformBean> productCategoryPlatformBeanList = physChemBean.getRequiredAerosolProductCategoryPlatformBean();
        List<String> dbProductCategoryPlatformNames = productCategoryPlatformBeanList.stream().map(ProductCategoryPlatformBean::getName).collect(Collectors.toList());
        boolean exist = dbProductCategoryPlatformNames.containsAll(inputProductCategoryPlatformNames);
        inputProductCategoryPlatformNames.removeAll(dbProductCategoryPlatformNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.AEROSOL_INPUT_PRODUCT_CATEGORY_PLATFORM_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputProductCategoryPlatformNames.toString()));
        }
    }

    /**
     * Method to check if Product Category Platform Exist for Liquid
     *
     * @since DSM 2018x.5
     */
    private void isLiquidRequiredProductCategoryPlatformExist() {
        StringList inputProductCategoryPlatformNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_CATEGORY_PLATFORM.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<ProductCategoryPlatformBean> productCategoryPlatformBeanList = physChemBean.getRequiredLiquidProductCategoryPlatformBean();
        List<String> dbProductCategoryPlatformNames = productCategoryPlatformBeanList.stream().map(ProductCategoryPlatformBean::getName).collect(Collectors.toList());
        boolean exist = dbProductCategoryPlatformNames.containsAll(inputProductCategoryPlatformNames);
        inputProductCategoryPlatformNames.removeAll(dbProductCategoryPlatformNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.LIQUID_INPUT_PRODUCT_CATEGORY_PLATFORM_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputProductCategoryPlatformNames.toString()));
        }
    }

    /**
     * Method to check if Product Category Platform Exist for Solid
     *
     * @since DSM 2018x.5
     */
    private void isSolidRequiredProductCategoryPlatformExist() {
        StringList inputProductCategoryPlatformNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_CATEGORY_PLATFORM.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<ProductCategoryPlatformBean> productCategoryPlatformBeanList = physChemBean.getRequiredSolidProductCategoryPlatformBean();
        List<String> dbProductCategoryPlatformNames = productCategoryPlatformBeanList.stream().map(ProductCategoryPlatformBean::getName).collect(Collectors.toList());
        boolean exist = dbProductCategoryPlatformNames.containsAll(inputProductCategoryPlatformNames);
        inputProductCategoryPlatformNames.removeAll(dbProductCategoryPlatformNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.SOLID_INPUT_PRODUCT_CATEGORY_PLATFORM_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputProductCategoryPlatformNames.toString()));
        }
    }

    /**
     * Method to check if Product Category Platform Exist
     *
     * @since DSM 2018x.5
     */
    private void checkRequiredProductCategoryPlatformExist() {
        isAerosolRequiredProductCategoryPlatformExist();
        isLiquidRequiredProductCategoryPlatformExist();
        isSolidRequiredProductCategoryPlatformExist();
    }

    /**
     * Method to check if Product Technology Platform Exist for Solid
     *
     * @since DSM 2018x.5
     */
    private void isSolidRequiredProductTechnologyPlatformExist() {
        StringList inputProductTechnologyPlatformNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_TECHNOLOGY_PLATFORM.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<ProductTechnologyPlatformBean> productTechnologyPlatformBeanList = physChemBean.getRequiredSolidProductTechnologyPlatformBean();
        List<String> dbProductTechnologyPlatformNames = productTechnologyPlatformBeanList.stream().map(ProductTechnologyPlatformBean::getName).collect(Collectors.toList());
        boolean exist = dbProductTechnologyPlatformNames.containsAll(inputProductTechnologyPlatformNames);
        inputProductTechnologyPlatformNames.removeAll(dbProductTechnologyPlatformNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.SOLID_INPUT_PRODUCT_TECHNOLOGY_PLATFORM_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputProductTechnologyPlatformNames.toString()));
        }
    }

    /**
     * Method to check if Product Technology Platform Exist for Liquid
     *
     * @since DSM 2018x.5
     */
    private void isLiquidRequiredProductTechnologyPlatformExist() {
        StringList inputProductTechnologyPlatformNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_TECHNOLOGY_PLATFORM.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<ProductTechnologyPlatformBean> productTechnologyPlatformBeanList = physChemBean.getRequiredLiquidProductTechnologyPlatformBean();
        List<String> dbProductTechnologyPlatformNames = productTechnologyPlatformBeanList.stream().map(ProductTechnologyPlatformBean::getName).collect(Collectors.toList());
        boolean exist = dbProductTechnologyPlatformNames.containsAll(inputProductTechnologyPlatformNames);
        inputProductTechnologyPlatformNames.removeAll(dbProductTechnologyPlatformNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.LIQUID_INPUT_PRODUCT_TECHNOLOGY_PLATFORM_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputProductTechnologyPlatformNames.toString()));
        }
    }

    /**
     * Method to check if Product Technology Platform Exist for Aerosol
     *
     * @since DSM 2018x.5
     */
    private void isAerosolRequiredProductTechnologyPlatformExist() {
        StringList inputProductTechnologyPlatformNames = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_PRODUCT_TECHNOLOGY_PLATFORM.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
        List<ProductTechnologyPlatformBean> productTechnologyPlatformBeanList = physChemBean.getRequiredAerosolProductTechnologyPlatformBean();
        List<String> dbProductTechnologyPlatformNames = productTechnologyPlatformBeanList.stream().map(ProductTechnologyPlatformBean::getName).collect(Collectors.toList());
        boolean exist = dbProductTechnologyPlatformNames.containsAll(inputProductTechnologyPlatformNames);
        inputProductTechnologyPlatformNames.removeAll(dbProductTechnologyPlatformNames);
        if (!exist) {
            preCheckList.add(FormulationGeneralConstant.AEROSOL_INPUT_PRODUCT_TECHNOLOGY_PLATFORM_DOES_NOT_EXIST.getValue().concat(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue()).concat(inputProductTechnologyPlatformNames.toString()));
        }
    }

    /**
     * Method to check if Product Technology Platform Exist
     *
     * @since DSM 2018x.5
     */
    private void checkRequiredProductTechnologyPlatform() {
        isSolidRequiredProductTechnologyPlatformExist();
        isLiquidRequiredProductTechnologyPlatformExist();
        isAerosolRequiredProductTechnologyPlatformExist();
    }

}