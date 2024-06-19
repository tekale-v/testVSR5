/*
 **   RawMaterial.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to update battery info of type Raw Material.
 **
 */
package com.pg.dsm.upload.battery.services;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.interfaces.Product;
import com.pg.dsm.upload.battery.interfaces.ProductPartBean;
import com.pg.dsm.upload.battery.models.RawMaterialBean;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;
import com.pg.dsm.upload.battery.models.config.BatteryProductFeature;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import com.pg.dsm.upload.battery.util.BatteryExcelUtil;
import com.pg.dsm.upload.battery.util.MatrixUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RawMaterial implements Product {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    BatteryProduct batteryProductConfig;
    Context context;
    BatteryPropertyConfig batteryPropertyConfig;

    public RawMaterial(Context context, BatteryPropertyConfig batteryPropertyConfig, BatteryProduct batteryProductConfig) {
        this.context = context;
        this.batteryPropertyConfig = batteryPropertyConfig;
        this.batteryProductConfig = batteryProductConfig;
    }

    /**
     * Method to get name, revision from input excel
     *
     * @return Map<String, List < String>>
     * @since DSM 2018x.6
     */
    @Override
    public Map<String, List<String>> getNameRevisionFromExcel() {
        BatteryExcelUtil batteryExcelUtil = new BatteryExcelUtil(batteryPropertyConfig.getInputFileNamePath(), batteryPropertyConfig);
        return batteryExcelUtil.getNameRevisionFromExcel();
    }

    /**
     * Method to search object by name revision.
     *
     * @return MapList
     * @since DSM 2018x.6
     */
    @Override
    public MapList searchObject(String type, Map<String, List<String>> nameRevisionMap) {
        MatrixUtil matrixUtil = new MatrixUtil(context);
        return matrixUtil.searchObject(type, nameRevisionMap, getBusAttributeSelects());
    }

    /**
     * Method to convert MapList to Bean.
     *
     * @return Map<String, ProductPartBean>
     * @since DSM 2018x.6
     */
    @Override
    public Map<String, ProductPartBean> getProductPartBeanMap(MapList objectList) {
        Map<String, ProductPartBean> dataMap = new HashMap<>();
        Instant startTime = Instant.now();

        Map<?, ?> tempMap;
        for (Object eachObj : objectList) {
            tempMap = (Map<?, ?>) eachObj;
            dataMap.put(generateNameRevisionKey(tempMap), new RawMaterialBean(context, tempMap));
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Converting MapList to Flat Map took|" + duration.toMillis() + " " + BatteryConstants.Basic.MILLI_SECONDS.getValue() + "|" + duration.getSeconds() + " " + BatteryConstants.Basic.SECONDS.getValue() + "|" + duration.toMinutes() + " " + BatteryConstants.Basic.MINUTES.getValue());
        return dataMap;
    }

    /**
     * Method to read Excel Data to Bean.
     *
     * @return List<BatteryProduct>
     * @since DSM 2018x.6
     */
    @Override
    public List<BatteryProduct> getExcelDataBean(Map<String, ProductPartBean> productPartBeanMap) {
        BatteryExcelUtil batteryExcelUtil = new BatteryExcelUtil(batteryPropertyConfig.getInputFileNamePath(), batteryPropertyConfig);
        return batteryExcelUtil.getExcelDataBean(productPartBeanMap, batteryProductConfig);
    }

    /**
     * Method to perform excel data validations.
     *
     * @return void
     * @since DSM 2018x.6
     */
    @Override
    public void performValidations(List<BatteryProduct> batteryProductList) {
        try {
            Class<?> classRawMaterialVerifier = Class.forName(RawMaterialVerifier.class.getName());
            Class<?>[] classRawMaterialVerifierConstructorArgs = new Class[3];
            classRawMaterialVerifierConstructorArgs[0] = BatteryProduct.class;
            classRawMaterialVerifierConstructorArgs[1] = BatteryProductFeature.class;
            classRawMaterialVerifierConstructorArgs[2] = BatteryPropertyConfig.class;

            BatteryProduct batteryProduct;
            List<BatteryProductFeature> batteryProductFeatures;
            List<String> batteryProductErrorMessageList;

            Iterator<BatteryProduct> batteryProductIterator = batteryProductList.iterator();
            while (batteryProductIterator.hasNext()) {
                batteryProduct = batteryProductIterator.next();
                batteryProductFeatures = batteryProduct.getBatteryProductFeatures();
                batteryProductErrorMessageList = new ArrayList<>();
                if (batteryProduct.isBeanExist()) {
                    batteryProductErrorMessageList.addAll(performValidation(batteryProduct, batteryProductFeatures, classRawMaterialVerifier, classRawMaterialVerifierConstructorArgs));
                } else {
                    batteryProductErrorMessageList.add(batteryPropertyConfig.getErrorGCASNotFound());
                }
                if (!batteryProductErrorMessageList.isEmpty()) {
                    batteryProduct.setErrorMessageList(batteryProductErrorMessageList);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to perform excel data validation (intermediate)
     *
     * @return List<String>
     * @since DSM 2018x.6
     */
    private List<String> performValidation(BatteryProduct batteryProduct, List<BatteryProductFeature> batteryProductFeatures, Class<?> classRawMaterialVerifier, Class<?>[] classRawMaterialVerifierConstructorArgs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<String> batteryProductErrorMessageList = new ArrayList<>();
        String validationMethod;
        RawMaterialVerifier rawMaterialVerifier;
        Method validationMethodObj;
        List<String> batteryProductFeatureErrorMessageList;
        for (BatteryProductFeature batteryProductFeature : batteryProductFeatures) {
            validationMethod = batteryProductFeature.getValidationMethod();

            // call if there is validation method configured.
            if (UIUtil.isNotNullAndNotEmpty(validationMethod)) {
                rawMaterialVerifier = (RawMaterialVerifier) classRawMaterialVerifier.getDeclaredConstructor(classRawMaterialVerifierConstructorArgs).newInstance(batteryProduct, batteryProductFeature, batteryPropertyConfig);
                validationMethodObj = classRawMaterialVerifier.getDeclaredMethod(validationMethod);
                batteryProductFeatureErrorMessageList = (List<String>) validationMethodObj.invoke(rawMaterialVerifier);

                if (null != batteryProductFeatureErrorMessageList && !batteryProductFeatureErrorMessageList.isEmpty()) {
                    batteryProductErrorMessageList.addAll(batteryProductFeatureErrorMessageList);
                }
            }
        }
        return batteryProductErrorMessageList;
    }

    /**
     * Method to create restore excel file.
     *
     * @return void
     * @since DSM 2018x.6
     */
    @Override
    public void createRestoreExcel(List<BatteryProduct> batteryProductList) {
        // create restore file name with same as input file name additionally prefix with timestamp & the word restore.
        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(batteryPropertyConfig.getProcessedFolder());
        filePathBuilder.append(batteryPropertyConfig.getTimeStamp());
        filePathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(batteryPropertyConfig.getRestoreKeyword());
        filePathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(FilenameUtils.removeExtension(new File(batteryPropertyConfig.getInputFileNamePath()).getName()));
        filePathBuilder.append(BatteryConstants.Basic.MICROSOFT_EXCEL_FILE_EXTENSION_XLSX.getValue());

        BatteryExcelUtil batteryExcelUtil = new BatteryExcelUtil(filePathBuilder.toString(), batteryPropertyConfig);
        batteryExcelUtil.createRestoreExcel(batteryProductList);
    }

    /**
     * Method to create error excel file.
     *
     * @return void
     * @since DSM 2018x.6
     */
    @Override
    public void createErrorExcel(List<BatteryProduct> batteryProductList) {
        // create restore file name with same as input file name additionally prefix with timestamp & the word restore.
        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(batteryPropertyConfig.getRetryFolder());
        filePathBuilder.append(batteryPropertyConfig.getTimeStamp());
        filePathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(batteryPropertyConfig.getErrorKeyword());
        filePathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(FilenameUtils.removeExtension(new File(batteryPropertyConfig.getInputFileNamePath()).getName()));
        filePathBuilder.append(BatteryConstants.Basic.MICROSOFT_EXCEL_FILE_EXTENSION_XLSX.getValue());

        BatteryExcelUtil batteryExcelUtil = new BatteryExcelUtil(filePathBuilder.toString(), batteryPropertyConfig);
        batteryExcelUtil.createErrorExcel(batteryProductList);
    }


    /**
     * Method to perform database update.
     *
     * @return void
     * @since DSM 2018x.6
     */
    @Override
    public void updateProductFeatures(List<BatteryProduct> batteryProductList) {
        List<BatteryProductFeature> batteryProductFeatures;
        Map<String, String> attributeMap;

        logger.info("Performing database UPDATE for given Input Excel File: " + batteryPropertyConfig.getInputFileNamePath());
        logger.info("Total Number of Records to be updated in database are: " + batteryProductList.size());
        try {
            DomainObject batteryObject = DomainObject.newInstance(context);
            for (BatteryProduct batteryProduct : batteryProductList) {
                batteryProductFeatures = batteryProduct.getBatteryProductFeatures();

                attributeMap = new HashMap<>();
                for (BatteryProductFeature batteryProductFeature : batteryProductFeatures) {
                    // update only attribute
                    if (BatteryConstants.Basic.FIELD_TYPE_ATTRIBUTE.getValue().equals(batteryProductFeature.getType())) {
                        attributeMap.put(batteryProductFeature.getAttributeName(), batteryProductFeature.getValue());
                    }
                }
                if (batteryProduct.isBeanExist()) {
                	
                	if(pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(batteryProduct.getProductPartBean().getType()) && pgV3Constants.STATE_RELEASE.equalsIgnoreCase(batteryProduct.getProductPartBean().getCurrentState())) {	
                	
                		attributeMap.put(pgV3Constants.ATTRIBUTE_PGBATTERYROLLUPFLAG, pgV3Constants.KEY_TRUE);
                	}
                	batteryObject.setId(batteryProduct.getProductPartBean().getId());
                    batteryObject.setAttributeValues(context, attributeMap);
                    logger.info("Record updated successfully: " + batteryObject.getName() + " " + batteryObject.getRevision());
                }
            }
        } catch (FrameworkException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to move given input file out of (input) folder.
     *
     * @return void
     * @since DSM 2018x.6
     */
    @Override
    public void moveGivenInputExcelFile(String destinationFolderName) {
        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(destinationFolderName);
        filePathBuilder.append(batteryPropertyConfig.getTimeStamp());
        filePathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(batteryPropertyConfig.getInputKeyword());
        filePathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        filePathBuilder.append(new File(batteryPropertyConfig.getInputFileNamePath()).getName());
        try {
            File sourceFile = new File(batteryPropertyConfig.getInputFileNamePath());
            if (sourceFile.exists()) {
                File destinationFile = new File(filePathBuilder.toString());
                FileUtils.moveFile(sourceFile, destinationFile);
                logger.info("Given Input Excel File moved out of (input) folder " + batteryPropertyConfig.getInputFileNamePath());
            }
        } catch (Exception e) {
            logger.error("Exception occurred in moving given Input Excel file out of (input) folder" + e.getMessage());
        }
    }

    /**
     * Method to get Attribute Bus Selects.
     *
     * @return StringList
     * @since DSM 2018x.6
     */
    public StringList getBusAttributeSelects() {
        StringList busSelects = new StringList();
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_REVISION);
        busSelects.addElement(DomainConstants.SELECT_POLICY);
        busSelects.addElement(DomainConstants.SELECT_VAULT);
        busSelects.addElement(DomainConstants.SELECT_CURRENT);
        busSelects.addElement(BatteryConstants.Attribute.BATTERY_TYPE.getAttributeSelect(context));
        busSelects.addElement(BatteryConstants.Attribute.IS_BATTERY.getAttributeSelect(context));

        return busSelects;
    }

    /**
     * Method to generate fop name and revision key
     *
     * @param map - Map<?, ?>
     * @return String
     * @since DSM 2018x.6
     */
    public String generateNameRevisionKey(Map<?, ?> map) {
        StringBuilder identifierBuilder = new StringBuilder();
        identifierBuilder.append(map.get(DomainConstants.SELECT_NAME));
        identifierBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        identifierBuilder.append(map.get(DomainConstants.SELECT_REVISION));
        return identifierBuilder.toString();
    }

    /**
     * Method to check if any validation failed.
     *
     * @param batteryProductList - List<BatteryProduct> - bean object
     * @return boolean
     * @since DSM 2018x.6
     */
    public boolean isVerificationPassed(List<BatteryProduct> batteryProductList) {
        boolean bCheck = false;
        List<String> checkList = new ArrayList<>();
        List<String> tempList;
        for (BatteryProduct batteryProduct : batteryProductList) {
            tempList = batteryProduct.getErrorMessageList();
            if (null != tempList) {
                checkList.addAll(tempList);
            }
        }
        if (checkList.isEmpty()) {
            bCheck = true;
        }
        return bCheck;
    }
}
