/*
 **   MaterialGroupExcelUtil.java
 **   Description - Introduced as part June CW 2022 for Material Group Code (MGC) - Requirement (39763, 39765, 39767, 39764)
 **   About - Utility class to convert excel (MGC codes) to xml.
 **
 */
package com.pg.dsm.sap.mgc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sap.mgc.beans.InclusionType;
import com.pg.dsm.sap.mgc.beans.MaterialGroup;
import com.pg.dsm.sap.mgc.beans.MaterialGroups;

public class MaterialGroupExcelUtil {
    private static final Logger logger = Logger.getLogger(MaterialGroupExcelUtil.class.getName());

    public static void main(String[] args) {
        try {
            String excelFilePath = args[0];
            logger.log(Level.INFO, "Given Excel File Absolute Path: {0}", excelFilePath);

            String xmlFilePath = args[1];
            logger.log(Level.INFO, "Given XML File Absolute Path: {0}", xmlFilePath);

            MaterialGroupExcelUtil excelUtil = new MaterialGroupExcelUtil();
            final List<MaterialGroup> materialGroupList = excelUtil.loadExcelDataIntoBean(excelFilePath);
            if (null != materialGroupList && !materialGroupList.isEmpty()) {
                logger.log(Level.INFO, "Convert excel data bean into XML");
                excelUtil.convertExcelToXML(materialGroupList, xmlFilePath);
            } else {
                logger.log(Level.WARNING, "Failed to load excel data into bean");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        } finally {
            System.exit(0);
        }
    }

    /**
     * Utility method to convert excel data to xml
     *
     * @param materialGroupList
     * @param xmlFilePath
     */
    public void convertExcelToXML(List<MaterialGroup> materialGroupList, String xmlFilePath) {
        if(UIUtil.isNotNullAndNotEmpty(xmlFilePath)) {
            InclusionType inclusionType = new InclusionType();
            inclusionType.setClassType("type_PackagingMaterialPart,type_PackagingAssemblyPart,type_RawMaterial,type_pgRawMaterial,type_pgPackingMaterial");
            inclusionType.setSubClass("type_PackagingMaterialPart,type_PackagingAssemblyPart,type_RawMaterial,type_pgRawMaterial,type_pgPackingMaterial");
            inclusionType.setReportFunction("type_PackagingMaterialPart,type_PackagingAssemblyPart,type_RawMaterial,type_pgRawMaterial,type_pgPackingMaterial");
            inclusionType.setPackagingComponentType("type_PackagingMaterialPart");
            inclusionType.setPackagingMaterialType("type_PackagingMaterialPart");
            inclusionType.setPackagingTechnology("type_PackagingMaterialPart");
            inclusionType.setPrimaryOrganization("type_PackagingMaterialPart,type_PackagingAssemblyPart,type_RawMaterial,type_pgRawMaterial,type_pgPackingMaterial"); // Added by DSM (Sogeti) 22x.04 for REQ 48068
            inclusionType.setChemicalGroup(DomainConstants.EMPTY_STRING);
            inclusionType.setCas(DomainConstants.EMPTY_STRING);

            List<InclusionType> inclusionTypeList = new ArrayList<>();
            inclusionTypeList.add(inclusionType);

            MaterialGroups materialGroups = new MaterialGroups();
            materialGroups.setApplicableTypes("type_PackagingMaterialPart,type_PackagingAssemblyPart,type_RawMaterial,type_pgRawMaterial,type_pgPackingMaterial");
            materialGroups.setInclusionType(inclusionTypeList);
            materialGroups.setMaterialGroup(materialGroupList);
            convertBeanToXML(materialGroups, xmlFilePath);
        } else {
            logger.log(Level.WARNING, "Provide XML File Absolute Path");
        }
    }


    /**
     * Utility method to convert bean to XML
     *
     * @param materialGroups
     * @param outputPath
     */
    private void convertBeanToXML(MaterialGroups materialGroups, String outputPath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MaterialGroups.class);
            Marshaller marshallerObj = jaxbContext.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshallerObj.marshal(materialGroups, new FileOutputStream(outputPath));
        } catch (JAXBException | FileNotFoundException e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        }
    }

    /**
     * Utility method to load excel data into bean.
     *
     * @param excelFilePath
     * @return
     */
    public List<MaterialGroup> loadExcelDataIntoBean(String excelFilePath) throws IOException {
        List<MaterialGroup> materialGroupList = new ArrayList<>();
        try {
            File excelFile = new File(excelFilePath);
            if(excelFile.exists()) {
                Workbook workbook = new XSSFWorkbook(new FileInputStream(excelFile));
                int inputExcelTabPosition = 0;
                int numberOfColumns = 34;
                Sheet sheet = workbook.getSheetAt(inputExcelTabPosition);
                int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
                logger.info("Excel Total rows:" + physicalNumberOfRows);

                DataFormatter formatter = new DataFormatter();
                Iterator<Row> rowIterator = sheet.rowIterator();

                while (rowIterator.hasNext()) {
                    final XSSFRow xssfRow = (XSSFRow) rowIterator.next();
                    if (xssfRow.getRowNum() != 0) {
                        MaterialGroup materialGroup = new MaterialGroup();
                        for (int i = 0; i < numberOfColumns; i++) {
                            final XSSFCell xssfCell = xssfRow.getCell(i);
                            final String cellValue = formatter.formatCellValue(xssfCell);
                            switch (i) {
                                case 0:
                                    materialGroup.setClassType(cellValue.trim());
                                    break;
                                case 1:
                                    materialGroup.setSubClass(cellValue.trim());
                                    break;
                                case 2:
                                    materialGroup.setReportFunction(cellValue.trim());
                                    break;
                                case 3:
                                    materialGroup.setPackagingComponentType(cellValue.trim());
                                    break;
                                case 4:
                                    materialGroup.setPackagingMaterialType(cellValue.trim());
                                    break;
                                case 5:
                                    materialGroup.setPackagingTechnology(cellValue.trim());
                                    break;
                                case 6:
                                    materialGroup.setPrimaryOrganization(cellValue.trim());
                                    break;
                                case 7:
                                    materialGroup.setChemicalGroup(cellValue.trim());
                                    break;
                                case 8:
                                    materialGroup.setCas(cellValue.trim());
                                    break;
                                case 9:
                                    materialGroup.setCode(cellValue.trim());
                                    break;
                            }
                        }
                        if (UIUtil.isNotNullAndNotEmpty(materialGroup.getCode())) {
                            materialGroupList.add(materialGroup);
                        }
                    }
                }
            } else {
                logger.log(Level.WARNING, "Provide Input Excel File Absolute Path");
            }
        } catch (IOException e) {
            throw e;
        }
        return materialGroupList;
    }
}
