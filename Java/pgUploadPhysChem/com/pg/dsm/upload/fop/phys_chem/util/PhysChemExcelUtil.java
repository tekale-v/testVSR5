/*
 **   PhysChemExcelUtil.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Utility class to perform load excel data and perform validation.
 **
 */

package com.pg.dsm.upload.fop.phys_chem.util;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationTypeConstant;
import com.pg.dsm.upload.fop.enumeration.ProductFormAerosolCategory;
import com.pg.dsm.upload.fop.enumeration.ProductFormLiquidCategory;
import com.pg.dsm.upload.fop.enumeration.ProductFormSolidCategory;
import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemContext;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductFormBean;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChem;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChemBean;
import com.pg.dsm.upload.fop.phys_chem.services.PhysChemFileProcessor;

import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class PhysChemExcelUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Properties physChemProperties;

    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    public PhysChemExcelUtil(Properties physChemProperties) {
        this.physChemProperties = physChemProperties;
    }

    /**
     * Method to validate the excel based on the Product Form.
     *
     * @param physChemProperties - Properties
     * @param physChemBeanList   - List<PhysChemBean> - bean objects
     * @throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException - exception
     * @since DSM 2018x.5
     */
    public void performValidations(Properties physChemProperties, List<PhysChemBean> physChemBeanList) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<?> classPhysChemBeanUtil = Class.forName(PhysChemBeanUtil.class.getName());
        Class<?>[] classPhysChemBeanUtilConstructorArgs = new Class[3];
        classPhysChemBeanUtilConstructorArgs[0] = Properties.class;
        classPhysChemBeanUtilConstructorArgs[1] = PhysChemBean.class;
        classPhysChemBeanUtilConstructorArgs[2] = PhysChem.class;

        Iterator<PhysChemBean> physChemBeanIterator = physChemBeanList.iterator();
        PhysChemBean physChemBean;
        List<PhysChem> physChemList;
        List<String> physChemBeanErrorMessageList;
		//Modified as part of 2018x.6 - Starts
        IFormulationPart formulationPart;
		//Modified as part of 2018x.6 - Ends
        while (physChemBeanIterator.hasNext()) {
            physChemBean = physChemBeanIterator.next();
            physChemList = physChemBean.getPhysChem();
			//Modified as part of 2018x.6 - Starts
            formulationPart = physChemBean.getFormulationPartBean();
			//Modified as part of 2018x.6 - Ends
            physChemBeanErrorMessageList = new ArrayList<>();
            if (physChemBean.isBeanExist()) {
				//Modified as part of 2018x.6 - Starts
            	if(FormulationTypeConstant.ASSEMBLEDPRODUCTPART_PART.getType(PhysChemContext.getContext()).equalsIgnoreCase(formulationPart.getType()) 
            		&& FormulationGeneralConstant.CONST_VALUE_TRUE.getValue().equalsIgnoreCase(formulationPart.getRollupFlag())) {
            			physChemBeanErrorMessageList.add(physChemProperties.getProperty(FormulationGeneralConstant.APP_HAS_ROLLEDUP_DATA_MESSAGE.getValue()));
            	}
				//Modified as part of 2018x.6 - Ends
                physChemBeanErrorMessageList.addAll(performValidation(physChemList, physChemBean, classPhysChemBeanUtil, classPhysChemBeanUtilConstructorArgs));
            } else {
                physChemBeanErrorMessageList.add(FormulationGeneralConstant.CONST_INPUT_FORMULATED_PRODUCT_NAME_NOT_FOUND.getValue());
            }
            if (!physChemBeanErrorMessageList.isEmpty()) {
                physChemBean.setErrorMessageList(physChemBeanErrorMessageList);
            }
        }
    }

    /**
     * Method to validate each excel column
     *
     * @param physChemList                         - List<PhysChemBean> - bean objects
     * @param physChemBean                         - PhysChemBean
     * @param classPhysChemBeanUtil                - Class<?>
     * @param classPhysChemBeanUtilConstructorArgs -Class<?>[]
     * @return List<String> - list of strings
     * @throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException - exception
     * @since DSM 2018x.5
     */
    private List<String> performValidation(List<PhysChem> physChemList, PhysChemBean physChemBean, Class<?> classPhysChemBeanUtil, Class<?>[] classPhysChemBeanUtilConstructorArgs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<String> physChemBeanErrorMessageList = new ArrayList<>();
        String validationMethod;
        PhysChemBeanUtil physChemBeanUtil;
        Method validationMethodObj;
        List<String> physChemErrorMessageList;
        for (PhysChem physChem : physChemList) {
            validationMethod = physChem.getValidationMethod();
            // if the cell is not greyed out and has a validation method configured.
            if (!physChem.isGreyedOut() && UIUtil.isNotNullAndNotEmpty(validationMethod)) {
                physChemBeanUtil = (PhysChemBeanUtil) classPhysChemBeanUtil.getDeclaredConstructor(classPhysChemBeanUtilConstructorArgs).newInstance(physChemProperties, physChemBean, physChem);
                validationMethodObj = classPhysChemBeanUtil.getDeclaredMethod(validationMethod);
                physChemErrorMessageList = (List<String>) validationMethodObj.invoke(physChemBeanUtil);
                if (null != physChemErrorMessageList && !physChemErrorMessageList.isEmpty()) {
                    physChemBeanErrorMessageList.addAll(physChemErrorMessageList);
                }
            }
        }
        return physChemBeanErrorMessageList;
    }

    /**
     * Method to get the excel data.
     *
     * @param xmlPhysChemBean - PhysChemBean object
     * @return List<PhysChemBean> - List of Physical Chemical Bean objects
     * @throws MatrixException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException - exception
     * @since DSM 2018x.5
     */
    public List<PhysChemBean> getExcelData(PhysChemBean xmlPhysChemBean, String fileName) throws MatrixException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<PhysChemBean> physChemBeanList;
        List<PhysChem> xmlPhysChemList = xmlPhysChemBean.getPhysChem();
        PhysChemMatrixUtil physChemMatrixUtil = new PhysChemMatrixUtil();
        physChemMatrixUtil.getAttributeOrPicklistInfoInitialized(xmlPhysChemList);
        Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(PhysChemFolderUtil.getPhysChemInputExcelFilePath(fileName))));
        int physChemTabPosition = Integer.parseInt(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_TAB_PHYSICAL_CHEMICAL_POSITION.getValue()));
        Sheet sheet = workbook.getSheetAt(physChemTabPosition);
        Map<String, IFormulationPart> allFormulationPartDataMap = getAllFormulationPartDataMap(sheet);
        physChemBeanList = getPhysChemBeans(sheet, xmlPhysChemBean, xmlPhysChemList, allFormulationPartDataMap);
        return physChemBeanList;
    }

    /**
     * Method to get Physical Chemical Beans objects.
     *
     * @param sheet                     - Sheet
     * @param xmlPhysChemBean           - PhysChemBean object
     * @param xmlPhysChemList           - List<PhysChem> bean object
     * @param allFormulationPartDataMap Map<String, IFormulationPart> - FOP data map
     * @return List<PhysChemBean> - List of Physical Chemical Bean objects
     * @throws IllegalAccessException, NoSuchMethodException, InvocationTargetException - exception
     * @since DSM 2018x.5
     */
    private List<PhysChemBean> getPhysChemBeans(Sheet sheet, PhysChemBean xmlPhysChemBean, List<PhysChem> xmlPhysChemList, Map<String, IFormulationPart> allFormulationPartDataMap) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<PhysChemBean> physChemBeanList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.rowIterator();
        XSSFRow xssfRow;
        PhysChemBean physChemBean;
        while (rowIterator.hasNext()) {
            xssfRow = ((XSSFRow) rowIterator.next());
            if (xssfRow.getRowNum() != 0 && !checkIfRowIsEmpty(xssfRow)) {
            	physChemBean = getPhysChemBean(xssfRow, xmlPhysChemBean, xmlPhysChemList, allFormulationPartDataMap);
                physChemBeanList.add(physChemBean);
            }
        }
        return physChemBeanList;
    }

    /**
     * Method to get Physical Chemical Bean object.
     *
     * @param xssfRow                   - XSSFRow
     * @param xmlPhysChemBean           - PhysChemBean object
     * @param xmlPhysChemList           - List<PhysChem>
     * @param allFormulationPartDataMap - Map<String, IFormulationPart>
     * @return PhysChemBean - Physical Chemical Bean objects
     * @throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
     * @since DSM 2018x.5
     */
    private PhysChemBean getPhysChemBean(XSSFRow xssfRow, PhysChemBean xmlPhysChemBean, List<PhysChem> xmlPhysChemList, Map<String, IFormulationPart> allFormulationPartDataMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PhysChemBean physChemBean = new PhysChemBean();

        String cellGreyHexCode = physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_CELL_COLOR_GRAY_HEX_CODE.getValue());

        // to copy the required business area, product category platform & product technology platform beans from xmlPhysChemBean.
        BeanUtils.copyProperties(physChemBean, xmlPhysChemBean);
        String cellValueStr;
        XSSFCell xssfCell;
        XSSFCellStyle xssfCellStyle;
        XSSFColor xssfCellStyleFillForegroundColor;
        DataFormatter formatter = new DataFormatter();
        int xmlAttributeCount = xmlPhysChemList.size();
        PhysChem xmlPhysChem;
        PhysChem newPhysChem;

        Method attributeSetterMethod;
        List<PhysChem> newPhysChemList = new ArrayList<>();
        boolean isGreyedOut;
        for (int attributeCount = 0; attributeCount < xmlAttributeCount; attributeCount++) {
            isGreyedOut = false;
            xssfCell = xssfRow.getCell(attributeCount);
            xmlPhysChem = xmlPhysChemList.get(attributeCount);
            if (null != xssfCell) {
                xssfCellStyle = xssfCell.getCellStyle();
                xssfCellStyleFillForegroundColor = xssfCellStyle.getFillForegroundColorColor();
                if (null != xssfCellStyleFillForegroundColor && cellGreyHexCode.equals(xssfCellStyleFillForegroundColor.getARGBHex())) {
                    isGreyedOut = true;
                    logger.info("Skip validation & update for Column Name : " + xmlPhysChem.getName());
                }
            }
            cellValueStr = formatter.formatCellValue(xssfCell);
            xmlPhysChem.setValue(cellValueStr.trim());
            newPhysChem = new PhysChem();
            BeanUtils.copyProperties(newPhysChem, xmlPhysChem);
            // apply this setting on the new object.
            newPhysChem.setGreyedOut(isGreyedOut);
            attributeSetterMethod = physChemBean.getClass().getDeclaredMethod(xmlPhysChem.getSetter(), String.class);
            attributeSetterMethod.invoke(physChemBean, xmlPhysChem.getValue());
            newPhysChemList.add(newPhysChem);
        }
        String tempKeyString = generateNameRevisionKey(physChemBean);
        if (allFormulationPartDataMap.containsKey(tempKeyString)) {
            physChemBean.setBeanExist(true);
            IFormulationPart formulationPart = allFormulationPartDataMap.get(tempKeyString);
            ProductFormBean relevantProductFormObj = getRelevantProductForm(formulationPart.getConnectedProductFormBeans(), physChemBean.getProductForm());
            physChemBean.setProductFormObject(relevantProductFormObj);
            physChemBean.setFormulationPartBean(formulationPart);
        } else {
            physChemBean.setBeanExist(false);
        }
        physChemBean.setPhysChem(newPhysChemList);
        physChemBean.setValidateAttributeList(getAttributeToValidate(physChemBean.getProductForm()));
        return physChemBean;
    }

    /**
     * Method to get All Formulation Part.
     *
     * @param sheet - Sheet
     * @return Map<String, IFormulationPart> - Map of Formulation part values
     * @throws FrameworkException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException - exception
     * @since DSM 2018x.5
     */
    private Map<String, IFormulationPart> getAllFormulationPartDataMap(Sheet sheet) throws FrameworkException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

        List<String> nameList = new ArrayList<>();
        List<String> revisionList = new ArrayList<>();

        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        logger.info("Excel Total rows:" + physicalNumberOfRows);
        DataFormatter formatter = new DataFormatter();
        XSSFRow xssfRow;
        XSSFCell xssfCell;
        String xssfCellValue;
        Iterator<Row> rowIterator = sheet.rowIterator();
        Instant startTime = Instant.now();
        while (rowIterator.hasNext()) {
            xssfRow = ((XSSFRow) rowIterator.next());
            if (xssfRow.getRowNum() != 0 && !checkIfRowIsEmpty(xssfRow)) {
                for (int col = 0; col < 2; col++) {
                    xssfCell = xssfRow.getCell(col);
                    xssfCellValue = formatter.formatCellValue(xssfCell);
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
        logger.info("Get all FOP Name & Revision from Input Excel took|" + duration.toMillis() + " " + FormulationGeneralConstant.CONST_MILLI_SECONDS.getValue() + "|" + duration.getSeconds() + " " + FormulationGeneralConstant.CONST_SECONDS.getValue() + "|" + duration.toMinutes() + " " + FormulationGeneralConstant.CONST_MINUTES.getValue());
        PhysChemMatrixUtil physChemMatrixUtil = new PhysChemMatrixUtil();
        return physChemMatrixUtil.getFormulationPartData(nameList, revisionList, physChemProperties);
    }

    /**
     * Method to check whether excel row empty or not.
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
        Cell cell = null;
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
        	cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && UIUtil.isNotNullAndNotEmpty(cell.toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to generate Name and Revision key.
     *
     * @param physChemAttributes PhysChemBean - bean object
     * @return String
     * @since DSM 2018x.5
     */
    private String generateNameRevisionKey(PhysChemBean physChemAttributes) {
        StringBuilder identifierBuilder = new StringBuilder();
        identifierBuilder.append(physChemAttributes.getFormulatedPartName());
        identifierBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue());
        identifierBuilder.append(physChemAttributes.getRevision());
        return identifierBuilder.toString();
    }

    /**
     * Method to update the validation message list.
     *
     * @param validationMessageList List<String> - validate message list
     * @return List<String>
     * @since DSM 2018x.5
     */
    public List<String> getUpdateValidateMessageList(List<String> validationMessageList) {
        List<String> messageList = new ArrayList<>();
        if (null != validationMessageList && !validationMessageList.isEmpty()) {
            messageList.addAll(validationMessageList);
        }
        messageList.add(FormulationGeneralConstant.CONST_INPUT_FORMULATED_PRODUCT_NAME_NOT_FOUND.getValue());
        return messageList;
    }

    /**
     * Method to validate Attribute of Product Form.
     *
     * @param productForm String - product form
     * @return StringList
     * @since DSM 2018x.5
     */
    public StringList getAttributeToValidate(String productForm) {
        StringList identifiers;
        identifiers = getAttributeToValidateForProductFormSolid(productForm);
        if (identifiers.isEmpty())
            identifiers = getAttributeToValidateForProductFormLiquid(productForm);
        if (identifiers.isEmpty())
            identifiers = getAttributeToValidateForProductFormAerosol(productForm);
        return identifiers;
    }

    /**
     * Method to validate Attribute of Solid Product Form.
     *
     * @param productForm String - product form
     * @return StringList
     * @since DSM 2018x.5
     */
    public StringList getAttributeToValidateForProductFormSolid(String productForm) {
        StringList identifiers = new StringList();
        List<ProductFormSolidCategory> productFormSolids = Arrays.asList(ProductFormSolidCategory.values());
        ProductFormSolidCategory productFormSolid = productFormSolids.stream().filter(eachCategory -> eachCategory.getValue().equals(productForm)).findAny().orElse(null);
        if (null != productFormSolid) {
            identifiers = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.SOLID_ATTRIBUTES_TO_VALIDATE.getValue()), FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue());
        }
        return identifiers;
    }

    /**
     * Method to validate Attribute of Liquid Product Form.
     *
     * @param productForm - String - product form
     * @return StringList
     * @since DSM 2018x.5
     */
    public StringList getAttributeToValidateForProductFormLiquid(String productForm) {
        StringList identifiers = new StringList();
        List<ProductFormLiquidCategory> productFormLiquids = Arrays.asList(ProductFormLiquidCategory.values());
        ProductFormLiquidCategory productFormLiquid = productFormLiquids.stream().filter(eachCategory -> eachCategory.getValue().equals(productForm)).findAny().orElse(null);
        if (null != productFormLiquid) {
            identifiers = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_ATTRIBUTES_TO_VALIDATE.getValue()), FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue());
        }
        return identifiers;
    }

    /**
     * Method to validate Attribute of Aerosol Product Form.
     *
     * @param productForm - String - product form
     * @return StringList
     * @since DSM 2018x.5
     */
    public StringList getAttributeToValidateForProductFormAerosol(String productForm) {
        StringList identifiers = new StringList();
        List<ProductFormAerosolCategory> productFormAerosols = Arrays.asList(ProductFormAerosolCategory.values());
        ProductFormAerosolCategory productFormAerosol = productFormAerosols.stream().filter(eachCategory -> eachCategory.getValue().equals(productForm)).findAny().orElse(null);
        if (null != productFormAerosol) {
            identifiers = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_ATTRIBUTES_TO_VALIDATE.getValue()), FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue());
        }
        return identifiers;
    }

    /**
     * Method to get Aerosol Product Form Constant.
     *
     * @param productForm - String - product form
     * @return ProductFormAerosolCategory
     * @since DSM 2018x.5
     */
    public ProductFormAerosolCategory getProductFormAerosolConstant(String productForm) {
        List<ProductFormAerosolCategory> productFormAerosols = Arrays.asList(ProductFormAerosolCategory.values());
        return productFormAerosols.stream().filter(eachCategory -> eachCategory.getValue().equals(productForm)).findAny().orElse(null);
    }

    /**
     * Method to get Liquid Product Form Constant.
     *
     * @param productForm - String - product form
     * @return ProductFormLiquidCategory
     * @since DSM 2018x.5
     */
    public ProductFormLiquidCategory getProductFormLiquidConstant(String productForm) {
        List<ProductFormLiquidCategory> productFormLiquids = Arrays.asList(ProductFormLiquidCategory.values());
        return productFormLiquids.stream().filter(eachCategory -> eachCategory.getValue().equals(productForm)).findAny().orElse(null);
    }

    /**
     * Method to get Solid Product Form Constant.
     *
     * @param productForm - String - product form
     * @return ProductFormSolidCategory
     * @since DSM 2018x.5
     */
    public ProductFormSolidCategory getProductFormSolidConstant(String productForm) {
        List<ProductFormSolidCategory> productFormSolids = Arrays.asList(ProductFormSolidCategory.values());
        return productFormSolids.stream().filter(eachCategory -> eachCategory.getValue().equals(productForm)).findAny().orElse(null);
    }

    /**
     * Method to get Relevant Product Form Constant.
     *
     * @param productFormList      - List<ProductFormBean> - list of product form bean
     * @param inputProductFormName - String
     * @return ProductFormSolidCategory
     * @since DSM 2018x.5
     */
    public ProductFormBean getRelevantProductForm(List<ProductFormBean> productFormList, String inputProductFormName) {
        ProductFormBean productFormObj;
        List<ProductFormBean> productForms = productFormList.stream().filter(productForm -> productForm.getName().equals(inputProductFormName)).collect(Collectors.toList());
        if (null != productForms && !productForms.isEmpty()) {
            productFormObj = productForms.get(0);
        } else {
            productFormObj = new ProductFormBean();
        }
        return productFormObj;
    }

    /**
     * Method check to pass Physical Chemical Bean
     *
     * @param physChemBeanList - List<PhysChemBean> - bean object
     * @return boolean
     * @since DSM 2018x.5
     */
    public boolean isCheckPassed(List<PhysChemBean> physChemBeanList) {
        boolean bCheck = false;
        List<String> checkList = new ArrayList<>();
        List<String> tempList;
        for (PhysChemBean physChemBean : physChemBeanList) {
            tempList = physChemBean.getErrorMessageList();
            if (null != tempList) {
                checkList.addAll(tempList);
            }
        }
        if (checkList.isEmpty()) {
            bCheck = true;
        }
        return bCheck;
    }

    /**
     * Method to generate Error Excel Report
     *
     * @param physChemBeanList - List<PhysChemBean> - bean object
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    public void generateErrorExcelReport(List<PhysChemBean> physChemBeanList, String fileName) throws IOException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(FormulationGeneralConstant.CONST_ERROR_EXCEL_SHEET_NAME.getValue());
            CellStyle excelRowCellStyle = workbook.createCellStyle();
            excelRowCellStyle.setWrapText(true);
            createErrorExcelReportHeaders(sheet);

            Row excelRow;
            Cell excelCell;
            int insertionRow = 1;
            List<String> errorMessageList;
            List<String> tempList;
            for (PhysChemBean physChemBean : physChemBeanList) {
                errorMessageList = new ArrayList<>();
                tempList = physChemBean.getErrorMessageList();
                if (null != tempList) {
                    errorMessageList.addAll(tempList);
                }
                if (!errorMessageList.isEmpty()) {
                    excelRow = sheet.createRow(insertionRow);
                    excelCell = excelRow.createCell(0);
                    excelCell.setCellValue(physChemBean.getFormulatedPartName());

                    excelCell = excelRow.createCell(1);
                    excelCell.setCellValue(physChemBean.getRevision());

                    excelCell = excelRow.createCell(2);
                    excelCell.setCellValue(String.join("\n", errorMessageList));
                    insertionRow++;
                }
            }
            createErrorExcelWorkbook(workbook, fileName);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Method to create Error Excel Report
     *
     * @param sheet - Sheet
     * @since DSM 2018x.5
     */
    private void createErrorExcelReportHeaders(Sheet sheet) {
        Row excelRow = sheet.createRow(0);
        Cell excelCellFOPName = excelRow.createCell(0);
        excelCellFOPName.setCellValue(FormulationGeneralConstant.CONST_ERROR_EXCEL_COLUMN_FOP_NAME.getValue());
        Cell excelCellFOPRev = excelRow.createCell(1);
        excelCellFOPRev.setCellValue(FormulationGeneralConstant.CONST_ERROR_EXCEL_COLUMN_FOP_REVISION.getValue());
        Cell excelCellMessages = excelRow.createCell(2);
        excelCellMessages.setCellValue(FormulationGeneralConstant.CONST_ERROR_EXCEL_COLUMN_VALIDATION_MESSAGES.getValue());
    }

    /**
     * Method to create Error Excel Workbook
     *
     * @param workbook - XSSFWorkbook
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    private void createErrorExcelWorkbook(XSSFWorkbook workbook, String fileName) throws IOException {
        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(PhysChemFolderUtil.getOutputFolderPath());
        filePathBuilder.append(PhysChemContext.getTimeStamp());
        filePathBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(PhysChemFileProcessor.getFileName(fileName));
        filePathBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN_ERROR_XLSX.getValue());

        logger.info("Error excel report created at: " + filePathBuilder.toString());
        try (FileOutputStream outputStream = new FileOutputStream(filePathBuilder.toString())) {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Method to generate Restore Excel Report
     *
     * @param physChemBeanList - List<PhysChemBean> - list of bean object
     * @throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException  - exception
     * @since DSM 2018x.5
     */
    public void generateRestoreExcelReport(List<PhysChemBean> physChemBeanList, String fileName) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(FormulationGeneralConstant.CONST_RESTORE_EXCEL_SHEET_NAME.getValue());
            CellStyle excelRowCellStyle = workbook.createCellStyle();
            excelRowCellStyle.setWrapText(true);
            createRestoreExcelReportHeaders(sheet, getRestoreExcelReportHeaders(physChemBeanList));

            Row excelRow;
            Cell excelRowCell;
            int insertionRow = 1;
            List<String> excelColumnIdentifiers = getExcelColumnIdentifiers(physChemBeanList);

            Method attributeGetterMethod;
            IFormulationPart formulationPartBean;
            for (PhysChemBean physChemBean : physChemBeanList) {
                if (physChemBean.isBeanExist()) {
                    formulationPartBean = physChemBean.getFormulationPartBean();
                    excelRow = sheet.createRow(insertionRow);
                    for (int i = 0; i < excelColumnIdentifiers.size(); i++) {
                        excelRowCell = excelRow.createCell(i);
                        attributeGetterMethod = formulationPartBean.getClass().getDeclaredMethod("get".concat(excelColumnIdentifiers.get(i)));
                        excelRowCell.setCellValue((String) attributeGetterMethod.invoke(formulationPartBean));
                    }
                    insertionRow++;
                }
            }

            createRestoreExcelWorkbook(workbook, fileName);
        } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Method to get Excel Column Identifiers
     *
     * @param physChemBeanList - List<PhysChemBean> - list of bean object
     * @return List<String>
     * @since DSM 2018x.5
     */
    private List<String> getExcelColumnIdentifiers(List<PhysChemBean> physChemBeanList) {
        List<String> identifierList = new ArrayList<>();
        PhysChemBean physChemBean = physChemBeanList.get(0);
        List<PhysChem> physChemList = physChemBean.getPhysChem();
        for (PhysChem physChem : physChemList) {
            identifierList.add(physChem.getIdentifier());
        }
        return identifierList;
    }

    /**
     * Method to Restore Excel Report Headers
     *
     * @param physChemBeanList - List<PhysChemBean> - list of bean object
     * @return List<String>
     * @since DSM 2018x.5
     */
    private List<String> getRestoreExcelReportHeaders(List<PhysChemBean> physChemBeanList) {
        List<String> headerList = new ArrayList<>();
        PhysChemBean physChemBean = physChemBeanList.get(0);
        List<PhysChem> physChemList = physChemBean.getPhysChem();
        for (PhysChem physChem : physChemList) {
            headerList.add(physChem.getName());
        }
        return headerList;
    }

    /**
     * Method to create Restore Excel Report Headers
     *
     * @param sheet      - Sheet
     * @param headerList - List<String>
     * @since DSM 2018x.5
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
     * Method to create Restore Excel Workbook
     *
     * @param workbook - XSSFWorkbook
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    private void createRestoreExcelWorkbook(XSSFWorkbook workbook, String fileName) throws IOException {
        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(PhysChemFolderUtil.getOutputFolderPath());
        filePathBuilder.append(PhysChemContext.getTimeStamp());
        filePathBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(PhysChemFileProcessor.getFileName(fileName));
        filePathBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN_RESTORE_XLSM.getValue());

        logger.info("Restore excel report created at: " + filePathBuilder.toString());

        try (FileOutputStream outputStream = new FileOutputStream(filePathBuilder.toString())) {
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
