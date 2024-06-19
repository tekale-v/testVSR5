/*
 **   PhysChemBeanUtil.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Utility class to perform data validation.
 **
 */

package com.pg.dsm.upload.fop.phys_chem.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationTypeConstant;
import com.pg.dsm.upload.fop.enumeration.ProductFormAerosolCategory;
import com.pg.dsm.upload.fop.enumeration.ProductFormLiquidCategory;
import com.pg.dsm.upload.fop.enumeration.ProductFormSolidCategory;
import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemContext;
import com.pg.dsm.upload.fop.phys_chem.models.bo.BusinessAreaBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductCategoryPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductFormBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductTechnologyPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChem;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChemBean;
import matrix.util.StringList;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PhysChemBeanUtil {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    PhysChemBean physChemBean;
    PhysChem physChem;
    Properties physChemProperties;

    /**
     * Constructor
     *
     * @param physChemBean - PhysChemBean -  bean object
     * @param physChem     - PhysChem - bean object
     * @since DSM 2018x.5
     */
    public PhysChemBeanUtil(Properties physChemProperties, PhysChemBean physChemBean, PhysChem physChem) {
        this.physChemBean = physChemBean;
        this.physChem = physChem;
        this.physChemProperties = physChemProperties;
    }

    /**
     * Method to validate formulation part name.
     *
     * @return List<String> - FOP error message list.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateFormulatedPartName() {
        List<String> checkList = new ArrayList<>();
        String inputFormulationPartName = physChem.getValue();
        if (inputFormulationPartName.length() > 8) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.FORMULATED_PART_NAME_COLUMN_LENGTH_CHECK.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate comments
     *
     * @return List<String> - error message of comments.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateComments() {
        List<String> checkList = new ArrayList<>();
        String delimiter = physChemProperties.getProperty(FormulationGeneralConstant.ATTRIBUTE_COMMENT_UPDATE_DELIMITER.getValue());
        String inputComments = physChem.getValue();
        String databaseComments = physChemBean.getFormulationPartBean().getComments();
        StringList databaseCommentList = StringUtil.split(databaseComments, delimiter);

        String updatedComment = DomainConstants.EMPTY_STRING;
        if (!databaseCommentList.contains(inputComments)) {
            StringBuilder valueBuilder = new StringBuilder();
            valueBuilder.append(databaseComments);
            valueBuilder.append(delimiter);
            valueBuilder.append(inputComments);
            updatedComment = valueBuilder.toString();
        }
        if (UIUtil.isNotNullAndNotEmpty(updatedComment)) {
            physChem.setValue(updatedComment);
        }
        return checkList;
    }

    /**
     * Method to validate Product Form.
     *
     * @return List<String> - error message of Product Form.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateProductForm() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String type = physChemBean.getFormulationPartBean().getType();
        if (UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(FormulationGeneralConstant.FOP_PRODUCT_FORM_VALUE_EMPTY.getValue());
        } else {
            // check if Product Form input value is equal to database.
            List<ProductFormBean> connectedProductFormBeans = physChemBean.getFormulationPartBean().getConnectedProductFormBeans();
            if (connectedProductFormBeans != null && type.equalsIgnoreCase(FormulationTypeConstant.FORMULATION_PART.getType(PhysChemContext.getContext()))) {
                ProductFormBean productFormBean = connectedProductFormBeans.stream().filter(eachProductForm -> eachProductForm.getName().equals(attributeValue)).findAny().orElse(null);
                if (productFormBean == null) {
                	checkList.add(physChem.getName()
                    		.concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_VALUE_MISMATCH_WITH_TEMPLATE_AND_DATABASE_MESSAGE.getValue()))
                    		.concat(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue())
                    		.concat(FormulationGeneralConstant.CONST_SYMBOL_SPACE.getValue())
                    		.concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_TEMPLATE_VALUE_MESSAGE.getValue()))
                    		.concat(FormulationGeneralConstant.CONST_SYMBOL_COLON.getValue())
                    		.concat(attributeValue)
                    		.concat(FormulationGeneralConstant.CONST_SYMBOL_SPACE.getValue())
                    		.concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_AND_DATABASE_VALUE_MESSAGE.getValue()))
                    		.concat(FormulationGeneralConstant.CONST_SYMBOL_COLON.getValue())
                    		.concat(physChemBean.getFormulationPartBean().getProductForm()));
                }
            } else {
            	physChem.setProductForm(attributeValue);
            }
        }
        return checkList;
    }

    /**
     * Method to validate color.
     *
     * @return List<String> - error message of color.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateColor() {
        List<String> checkList = new ArrayList<>();
        String attributeColorValue = physChem.getValue();
        // Color value should not be blank if Product Form is Aerosol/Liquid/Solid.
        if (UIUtil.isNullOrEmpty(attributeColorValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // check if entered value for Color column is a valid picklist object names.
        if (UIUtil.isNotNullAndNotEmpty(attributeColorValue) && physChem.isPicklist() && !physChem.getPicklistNames().contains(attributeColorValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_PICKLIST_VALUE_MISMATCH_WITH_DATABASE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate color Intensity.
     *
     * @return List<String> - error message of color Intensity.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateColorIntensity() {
        List<String> checkList = new ArrayList<>();
        String attributeColorIntensityValue = physChem.getValue();
        // value can be empty, but if value is provided, then check if it matches the picklist
        if (UIUtil.isNotNullAndNotEmpty(attributeColorIntensityValue) && physChem.isPicklist() && !physChem.getPicklistNames().contains(attributeColorIntensityValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_PICKLIST_VALUE_MISMATCH_WITH_DATABASE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Odour.
     *
     * @return List<String> - error message of Odour.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateOdour() {
        List<String> checkList = new ArrayList<>();
        String attributeOdourValue = physChem.getValue();
        //Odour value should not has bad character
        if (isBadChars(attributeOdourValue, physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_BAD_CHARACTER_ODOUR.getValue()))) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_HAS_BAD_CHARACTER.getValue())));
        }
        // Odour value should not be blank if Product Form is Aerosol/Liquid/Solid.
        if (UIUtil.isNullOrEmpty(attributeOdourValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Closed Cup Flashpoint (C) field.
     *
     * @return List<String> - error message of Closed Cup Flashpoint (C).
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateClosedCupFlashpoint() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        if (isLiquid(productForm) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Boiling Point (C) field.
     *
     * @return List<String> - error message of Boiling Point (C) field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateBoilingPoint() {
        List<String> checkList = new ArrayList<>();
        String boilingPointAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        if (isLiquid(productForm) && UIUtil.isNullOrEmpty(boilingPointAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(boilingPointAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Boiling Point Value (C) field.
     *
     * @return List<String> - error message of Boiling Point Value (C).
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateBoilingPointValue() {
        List<String> checkList = new ArrayList<>();
        String productForm = physChemBean.getProductForm();
        String boilingPointValue = physChem.getValue();
        String boilingPoint = physChemBean.getBoilingPoint();
        // Required when 'Boiling Point' = Other
        if (isLiquid(productForm) && FormulationGeneralConstant.BOILING_POINT_RANGE_VALUE_OTHER.getValue().equals(boilingPoint) && UIUtil.isNullOrEmpty(boilingPointValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // value can be empty, but if value is provided then it should be numeric.
        String valueNumericCheckMessage = getNumericCheckMessage(boilingPointValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Does the product Sustain Combustion? field.
     *
     * @return List<String> - error message of Does the product Sustain Combustion?.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateProductSustainCombustion() {
        List<String> checkList = new ArrayList<>();
        String productForm = physChemBean.getProductForm();
        String productSustainCombustion = physChem.getValue();
        // Modified as part of 2018x.6 - Starts
        String closedCupFlashpoint = physChemBean.getClosedCupFlashpoint();
        if(isLiquid(productForm) 
        		&& physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_DOES_PRODUCT_SUSTAIIN_COMBUSTION_REQUIRED_WHEN_CLOSEDCUP_FLASHPOINT.getValue()).equalsIgnoreCase(closedCupFlashpoint)
        		&& UIUtil.isNullOrEmpty(productSustainCombustion)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // Modified as part of 2018x.6 - Ends
        // compare ranges..
        if (!physChem.getAttributeRanges().contains(productSustainCombustion)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the product contain an Oxidizer as a raw material? field.
     *
     * @return List<String> - error message of Does the product contain an Oxidizer as a raw material?.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateProductOxidizer() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        if ((isSolid(physChemBean.getProductForm()) || isLiquid(attributeValue)) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Is the Oxidizer Sodium percarbonate < 60%? field.
     *
     * @return List<String> - error message of Is the Oxidizer Sodium percarbonate < 60%? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateOxidizerSodiumPerCarbonate() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        //Required when Does the product contain an Oxidizer as a raw material? = Yes
        if (isSolid(productForm)) {
            String solidOxidizerSodiumPerCarbonateSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_OXIDIZER_SODIUM_PER_CARBONATE_REQUIRED_WHEN_PRODUCT_OXIDIZER.getValue());
            StringList solidOxidizerSodiumPerCarbonateSettingList = StringUtil.split(solidOxidizerSodiumPerCarbonateSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (solidOxidizerSodiumPerCarbonateSettingList.contains(physChemBean.getProductOxidizer())
                    && UIUtil.isNullOrEmpty(attributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (isLiquid(productForm)) {
            String liquidOxidizerSodiumPerCarbonateSetting = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_OXIDIZER_SODIUM_PER_CARBONATE_REQUIRED_WHEN_PRODUCT_OXIDIZER.getValue());
            StringList liquidOxidizerSodiumPerCarbonateSettingList = StringUtil.split(liquidOxidizerSodiumPerCarbonateSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (liquidOxidizerSodiumPerCarbonateSettingList.contains(physChemBean.getProductOxidizer())
                    && UIUtil.isNullOrEmpty(attributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Is the Oxidizer Hydrogen Peroxide < 8% field.
     *
     * @return List<String> - error message of Is the Oxidizer Hydrogen Peroxide < 8% field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateOxidizerHydrogenPeroxide() {
        List<String> checkList = new ArrayList<>();
        String oxidizerHydrogenPeroxideAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String productOxidizer = physChemBean.getProductOxidizer();
        String oxidizerSodiumPerCarbonate = physChemBean.getOxidizerSodiumPerCarbonate();

        //Required when Does the product contain an Oxidizer as a raw material? = Yes and "Is the Oxidizer Sodium percarbonate < 60%?" = No
        if (isSolid(productForm)) {
            String solidOxidizerHydrogenPeroxideProductOxidizerSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_OXIDIZER_HYDROGEN_PEROXIDE_REQUIRED_WHEN_PRODUCT_OXIDIZER.getValue());
            StringList solidOxidizerHydrogenPeroxideProductOxidizerSettingList = StringUtil.split(solidOxidizerHydrogenPeroxideProductOxidizerSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            String solidOxidizerHydrogenPeroxideOxidizerSodiumPerCarbonateSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_OXIDIZER_HYDROGEN_PEROXIDE_REQUIRED_WHEN_OXIDIZER_SODIUM_PER_CARBONATE.getValue());
            StringList solidOxidizerHydrogenPeroxideOxidizerSodiumPerCarbonateSettingList = StringUtil.split(solidOxidizerHydrogenPeroxideOxidizerSodiumPerCarbonateSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            if (solidOxidizerHydrogenPeroxideProductOxidizerSettingList.contains(productOxidizer)
                    && solidOxidizerHydrogenPeroxideOxidizerSodiumPerCarbonateSettingList.contains(oxidizerSodiumPerCarbonate)
                    && UIUtil.isNullOrEmpty(oxidizerHydrogenPeroxideAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        // Required when 'Does the product contain an Oxidizer as a raw material' = Yes
        if (isLiquid(productForm)) {
            String liquidOxidizerHydrogenPeroxideProductOxidizerSetting = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_OXIDIZER_HYDROGEN_PEROXIDE_REQUIRED_WHEN_PRODUCT_OXIDIZER.getValue());
            StringList liquidOxidizerHydrogenPeroxideProductOxidizerSettingList = StringUtil.split(liquidOxidizerHydrogenPeroxideProductOxidizerSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (liquidOxidizerHydrogenPeroxideProductOxidizerSettingList.contains(productOxidizer)
                    && UIUtil.isNullOrEmpty(oxidizerHydrogenPeroxideAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (!physChem.getAttributeRanges().contains(oxidizerHydrogenPeroxideAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the product has the potential to increase the burning rate or intensity of a combustible substance? field.
     *
     * @return List<String> - error message of Does the product has the potential to increase the burning rate or intensity of a combustible substance? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateProductPotentialToIncreaseBurningRate() {
        List<String> checkList = new ArrayList<>();
        String productForm = physChemBean.getProductForm();
        String productPotentialToIncreaseBurningRateAttributeValue = physChem.getValue();
        String productOxidizer = physChemBean.getProductOxidizer();
        String oxidizerSodiumPerCarbonate = physChemBean.getOxidizerSodiumPerCarbonate();
        String oxidizerHydrogenPeroxide = physChemBean.getOxidizerHydrogenPeroxide();

        if (isSolid(productForm)) {
            String solidProductPotentialToIncreaseBurningRateProductOxidizerSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_PRODUCT_OXIDIZER.getValue());
            StringList solidProductPotentialToIncreaseBurningRateProductOxidizerSettingList = StringUtil.split(solidProductPotentialToIncreaseBurningRateProductOxidizerSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String solidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_SODIUM_PER_CARBONATE.getValue());
            StringList solidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSettingList = StringUtil.split(solidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String solidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_HYDROGEN_PEROXIDE.getValue());
            StringList solidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSettingList = StringUtil.split(solidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            if (solidProductPotentialToIncreaseBurningRateProductOxidizerSettingList.contains(productOxidizer)
                    && solidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSettingList.contains(oxidizerSodiumPerCarbonate)
                    && solidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSettingList.contains(oxidizerHydrogenPeroxide)
                    && UIUtil.isNullOrEmpty(productPotentialToIncreaseBurningRateAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (isLiquid(productForm)) {
            String liquidProductPotentialToIncreaseBurningRateProductOxidizerSetting = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_PRODUCT_OXIDIZER.getValue());
            StringList liquidProductPotentialToIncreaseBurningRateProductOxidizerSettingList = StringUtil.split(liquidProductPotentialToIncreaseBurningRateProductOxidizerSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String liquidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSetting = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_SODIUM_PER_CARBONATE.getValue());
            StringList liquidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSettingList = StringUtil.split(liquidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String liquidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSetting = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_HYDROGEN_PEROXIDE.getValue());
            StringList liquidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSettingList = StringUtil.split(liquidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            if (liquidProductPotentialToIncreaseBurningRateProductOxidizerSettingList.contains(productOxidizer)
                    && liquidProductPotentialToIncreaseBurningRateOxidizerSodiumPerCarbonateSettingList.contains(oxidizerSodiumPerCarbonate)
                    && liquidProductPotentialToIncreaseBurningRateOxidizerHydrogenPeroxideSettingList.contains(oxidizerHydrogenPeroxide)
                    && UIUtil.isNullOrEmpty(productPotentialToIncreaseBurningRateAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (!physChem.getAttributeRanges().contains(productPotentialToIncreaseBurningRateAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does product contain an Organic Peroxide as a raw material? field.
     *
     * @return List<String> - error message of Does product contain an Organic Peroxide as a raw material? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateOrganicPeroxide() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        if ((isSolid(productForm) || isLiquid(productForm)) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Available oxygen content (%) field.
     *
     * @return List<String> - error message of Available oxygen content (%) field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateAvailableOxygenContent() {
        List<String> checkList = new ArrayList<>();
        String availableOxygenContentAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String organicPeroxide = physChemBean.getOrganicPeroxide();

        if (isSolid(productForm)) {
            String solidAvailableOxygenContentOrganicPeroxideSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_AVAILABLE_OXYGEN_CONTENT_REQUIRED_WHEN_ORGANIC_PEROXIDE.getValue());
            StringList solidAvailableOxygenContentOrganicPeroxideSettingList = StringUtil.split(solidAvailableOxygenContentOrganicPeroxideSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (solidAvailableOxygenContentOrganicPeroxideSettingList.contains(organicPeroxide)
                    && UIUtil.isNullOrEmpty(availableOxygenContentAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (isLiquid(productForm)) {
            String liquidAvailableOxygenContentOrganicPeroxideSetting = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_AVAILABLE_OXYGEN_CONTENT_REQUIRED_WHEN_ORGANIC_PEROXIDE.getValue());
            StringList liquidAvailableOxygenContentOrganicPeroxideSettingList = StringUtil.split(liquidAvailableOxygenContentOrganicPeroxideSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (liquidAvailableOxygenContentOrganicPeroxideSettingList.contains(organicPeroxide)
                    && UIUtil.isNullOrEmpty(availableOxygenContentAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        String valueNumericCheckMessage = getNumericCheckMessage(availableOxygenContentAttributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Dynamic Viscosity (centipoise) [mandatory for products sold in the EU] field.
     *
     * @return List<String> - error message of Dynamic Viscosity (centipoise) [mandatory for products sold in the EU] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateKinematicViscosity() {
        List<String> checkList = new ArrayList<>();
        String kinematicViscosityAttributeValue = physChem.getValue();
        String valueNumericCheckMessage = getNumericCheckMessage(kinematicViscosityAttributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Relative Density field.
     *
     * @return List<String> - error message of Relative Density field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateRelativeDensityOrSpecificGravity() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        // for aerosol/liquid/solid value can be empty, but if value is provided - then check if it is numeric.
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if ((isAerosol(productForm) || isLiquid(productForm) || isSolid(productForm)) && UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Evaporation Rate field.
     *
     * @return List<String> - error message of Evaporation Rate field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateEvaporationRate() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Is pH data available? field.
     *
     * @return List<String> - error message of Is pH data available? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePHDataAvailable() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        if (isSolid(physChemBean.getProductForm()) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Is the liquid an aqueous solution field.
     *
     * @return List<String> - error message of Is the liquid an aqueous solution field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateIsTheLiquidanAqeousSolution() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
		//Modified as part of 2018x.6 - Starts
        if ((isLiquid(productForm) || isAerosol(productForm)) && UIUtil.isNullOrEmpty(attributeValue)) {
		//Modified as part of 2018x.6 - Ends
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate pH field.
     *
     * @return List<String> - error message of Is the liquid an aqueous solution field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePH() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String isTheLiquidanAqeousSolution = physChemBean.getIsTheLiquidanAqeousSolution();
        String phDataAvailable = physChemBean.getPHDataAvailable();

        String solidPHTargetRequiredWhenPHDataAvailable = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PH_TARGET_REQUIRED_WHEN_PH_DATA_AVAILABLE.getValue());
        String liquidPHTargetRequiredWhenPHDataAvailable = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PH_TARGET_REQUIRED_WHEN_IS_THE_LIQUIDAN_AQEOUS_SOLUTION.getValue());

        String productForm = physChemBean.getProductForm();

        //Required when "Is pH data available?" = Yes
        if (isSolid(productForm)
                && solidPHTargetRequiredWhenPHDataAvailable.equals(phDataAvailable)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
		//Modified as part of 2018x.6 - Starts
        // Required when 'Is the liquid an aqueous solution?'=  Yes
        if ((isLiquid(productForm)||isAerosol(productForm))
                && liquidPHTargetRequiredWhenPHDataAvailable.equals(isTheLiquidanAqeousSolution)
                && UIUtil.isNullOrEmpty(attributeValue)) {
		//Modified as part of 2018x.6 - Ends
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // value should be numeric and max one decimal
        if (UIUtil.isNotNullAndNotEmpty(attributeValue)) {
            try {
                double phValue = Double.parseDouble(attributeValue);
                if (phValue >= 0 && phValue <= 14 && !isPHMaxOneDecimalPlace(phValue)) {
                    checkList.add(FormulationGeneralConstant.PH_NUMERIC_DECIMAL_PLACE_CHECK.getValue());
                }
            } catch (NumberFormatException e) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
            }
        }
        return checkList;
    }

    /**
     * Method to check if Ph decimal point is 1.
     *
     * @return boolean -
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    boolean isPHMaxOneDecimalPlace(Double phVal) {
        boolean isQualifiedPH = true;
        String phValText = Double.toString(Math.abs(phVal));
        int integerPlaces = phValText.indexOf('.');
        int decimalPlaces = phValText.length() - integerPlaces - 1;
        if (decimalPlaces > 1) {
            isQualifiedPH = false;
        }
        return isQualifiedPH;
    }

    /**
     * Method to validate pH Dilution field.
     *
     * @return List<String> - error message of Is the liquid an aqueous solution field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePHDilution() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String isTheLiquidanAqeousSolution = physChemBean.getIsTheLiquidanAqeousSolution();

        String solidPHDilutionRequiredWhenPHDataAvailable = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PH_DILUTION_REQUIRED_WHEN_PH_DATA_AVAILABLE.getValue());
        String liquidPHDilutionRequiredWhenIsTheLiquidanAqeousSolution = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PH_DILUTION_REQUIRED_WHEN_IS_THE_LIQUIDAN_AQEOUS_SOLUTION.getValue());

        //Required when "Is pH data available?" = Yes
        if (isSolid(productForm)
                && solidPHDilutionRequiredWhenPHDataAvailable.equals(physChemBean.getPHDataAvailable())
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }

        //Display when 'Is the liquid an aqueous solution?'=  Yes
		//Modified as part of 2018x.6 - Starts
        if ((isLiquid(productForm) || isAerosol(productForm))
                && liquidPHDilutionRequiredWhenIsTheLiquidanAqeousSolution.equals(isTheLiquidanAqeousSolution)
                && UIUtil.isNullOrEmpty(attributeValue)) {
		//Modified as part of 2018x.6 - Ends
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // allowed ranges for liquid & aerosol are - Neat, 1% Solution,10% Solution
        if ((isAerosol(productForm) || isLiquid(productForm)) && !physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        // allowed ranges for solid are - 1% Solution,10% Solution
        if (isSolid(productForm)
                && !FormulationGeneralConstant.PH_DILUTION_RANGE_VALUE_NEAT.getValue().equals(attributeValue)
                && !physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Reserve Alkalinity, % (required if pH > 10) field.
     *
     * @return List<String> - error message of Is the liquid an aqueous solution field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateReserveAlkalinity() {
        List<String> checkList = new ArrayList<>();
        String reserveAlkalinityAttributeValue = physChem.getValue();
        String phTarget = physChemBean.getPH();
        String phMax = physChemBean.getPHMax();
        String productForm = physChemBean.getProductForm();
		// Modified as part of 2018x.6 - Starts
        IFormulationPart formulationPart = physChemBean.getFormulationPartBean();
        List<BusinessAreaBean> connectedBusinessAreaBeans = formulationPart.getConnectedBusinessAreaBeans();
        // Modified as part of 2018x.6 - Ends
        boolean isPHTargetOrPHMaxGreaterThanTen = false;

        // value should be numeric
        boolean bPhTargetNumeric = false;
        boolean bPhMaxNumeric = false;
        try {
            Double.parseDouble(phTarget);
            bPhTargetNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            Double.parseDouble(phMax);
            bPhMaxNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        if (!bPhTargetNumeric && !bPhMaxNumeric) {
            checkList.add(FormulationGeneralConstant.RESERVE_ALKALINITY_REQUIRED_PH_UNABLE_TO_CHECK.getValue());
        } else {
            isPHTargetOrPHMaxGreaterThanTen = isPhTargetOrPhMaxGreaterThanTen(phTarget, phMax);
        }
        
        //Required pH[target] or pH [Maximum] >10 or Business Area = 'Fabric Care' or 'Home Care'
        if (((isSolid(productForm) || isLiquid(productForm)) || isAerosol(productForm))
                && isPHTargetOrPHMaxGreaterThanTen
                && validateBusinessArea(connectedBusinessAreaBeans)
                && UIUtil.isNullOrEmpty(reserveAlkalinityAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        
        // Required when  pH target or pH maximum is > 10
        String valueNumericCheckMessage = getNumericCheckMessage(reserveAlkalinityAttributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to check business are value
     *
     * @return boolean
     * @since DSM 2018x.6
     */
    private boolean validateBusinessArea(List<BusinessAreaBean> connectedBusinessAreaBeans) {
    	boolean isBusinessArea = true;
    	List<BusinessAreaBean> matchingBusinessAreaList = physChemBean.getRequiredLiquidBusinessAreaReserveAlkalinityBean().stream().filter(connectedBusinessAreaBean -> connectedBusinessAreaBeans.stream().anyMatch(requiredBusinessAreaBean -> requiredBusinessAreaBean.getId().equals(connectedBusinessAreaBean.getId()))).collect(Collectors.toList());
		if (matchingBusinessAreaList.isEmpty()) {
			isBusinessArea = false;
			physChem.setGreyedOut(true);
        }
		return isBusinessArea;
	}

	/**
     * Method to check if Ph greater than 10
     *
     * @return boolean -
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    boolean isPhGreaterThanTen(String phStr) {
        boolean isGreaterThanTen = false;
        boolean isPhNumeric = false;
        Double phDoubleValue = null;
        try {
            phDoubleValue = Double.parseDouble(phStr);
            isPhNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        if (isPhNumeric && phDoubleValue > 10) {
            isGreaterThanTen = true;
        }
        return isGreaterThanTen;
    }

    /**
     * Method to check if Ph less than 4
     *
     * @return boolean -
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    boolean isPhLesserThanFour(String phStr) {
        boolean isLesserThanFour = false;
        boolean isPhNumeric = false;
        Double phDoubleValue = null;
        try {
            phDoubleValue = Double.parseDouble(phStr);
            isPhNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        if (isPhNumeric && phDoubleValue < 4) {
            isLesserThanFour = true;
        }
        return isLesserThanFour;
    }

    /**
     * Method to check if ph Target or ph Max greater than 10
     *
     * @return boolean -
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    boolean isPhTargetOrPhMaxGreaterThanTen(String phTargetStr, String phMaxStr) {
        boolean isGreaterThanTen = false;
        boolean isPhTargetNumeric = false;
        boolean isPhMaxNumeric = false;

        Double phTargetValue = null;
        Double phMaxValue = null;
        try {
            phTargetValue = Double.parseDouble(phTargetStr);
            isPhTargetNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            phMaxValue = Double.parseDouble(phMaxStr);
            isPhMaxNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        if (isPhTargetNumeric) {
            if (phTargetValue > 10) {
                isGreaterThanTen = true;
            } else {
                isGreaterThanTen = isPhGreaterThanTen(phMaxStr);
            }
        }
        if (isPhMaxNumeric) {
            if (phMaxValue > 10) {
                isGreaterThanTen = true;
            } else {
                isGreaterThanTen = isPhGreaterThanTen(phTargetStr);
            }
        }
        return isGreaterThanTen;
    }

    /**
     * Method to check if ph Target or ph Min less than 4
     *
     * @return boolean -
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    boolean isPhTargetOrPhMinLessThanFour(String phTargetStr, String phMinStr) {
        boolean isLesserThanFour = false;
        boolean isPhTargetNumeric = false;
        boolean isPhMinNumeric = false;

        Double phTargetDoubleValue = null;
        Double phMinDoubleValue = null;
        try {
            phTargetDoubleValue = Double.parseDouble(phTargetStr);
            isPhTargetNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            phMinDoubleValue = Double.parseDouble(phMinStr);
            isPhMinNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        if (isPhTargetNumeric) {
            if (phTargetDoubleValue < 4) {
                isLesserThanFour = true;
            } else {
                isLesserThanFour = isPhLesserThanFour(phMinStr);
            }
        }
        if (isPhMinNumeric) {
            if (phMinDoubleValue < 4) {
                isLesserThanFour = true;
            } else {
                isLesserThanFour = isPhLesserThanFour(phTargetStr);
            }
        }
        return isLesserThanFour;
    }

    /**
     * Method to validate Reserve Acidity, % (required if pH < 4) field.
     *
     * @return List<String> - error message of Reserve Acidity, % (required if pH < 4).
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateReserveAcidity() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String phTarget = physChemBean.getPH();
        String phMin = physChemBean.getPHMin();
        String productForm = physChemBean.getProductForm();
		// Modified as part of 2018x.6 - Starts
        IFormulationPart formulationPart = physChemBean.getFormulationPartBean();
        List<BusinessAreaBean> connectedBusinessAreaBeans = formulationPart.getConnectedBusinessAreaBeans();
        // Modified as part of 2018x.6 - Ends
        boolean bPhTargetNumeric = false;
        boolean bPhMinNumeric = false;
        boolean isLesserThanFour = false;
        try {
            Double.parseDouble(phTarget);
            bPhTargetNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            Double.parseDouble(phMin);
            bPhMinNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }

        if (!bPhTargetNumeric && !bPhMinNumeric) {
            checkList.add(FormulationGeneralConstant.RESERVE_ACIDITY_REQUIRED_PH_UNABLE_TO_CHECK.getValue());
        } else {
            isLesserThanFour = isPhTargetOrPhMinLessThanFour(phTarget, phMin);
        }
        // Required when "pH target or pH minimum value is < 4 or Business Area = 'Fabric Care' or 'Home Care'
        if ((isSolid(productForm) || isAerosol(productForm) || isLiquid(productForm))
                && isLesserThanFour
                && validateBusinessArea(connectedBusinessAreaBeans)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // Required when "pH target or pH minimum value is < 4
        // value can be empty but if provided, then it should be numeric.
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Reserve Alkalinity/Acidity Unit of Measure field.
     *
     * @return List<String> - error message of Reserve Alkalinity/Acidity Unit of Measure field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateReserveAlkalinityUoM() {
        List<String> checkList = new ArrayList<>();

        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        // Modified as part of 2018x.6 - Starts
        IFormulationPart formulationPart = physChemBean.getFormulationPartBean();
        List<BusinessAreaBean> connectedBusinessAreaBeans = formulationPart.getConnectedBusinessAreaBeans();
        // Modified as part of 2018x.6 - Ends
        String phTarget = physChemBean.getPH();
        String phMin = physChemBean.getPHMin();
        String phMax = physChemBean.getPHMax();

        boolean bPhTargetNumeric = false;
        boolean bPhMinNumeric = false;
        boolean bPhMaxNumeric = false;

        boolean isLesserThanFour;
        boolean isGreaterThanTen;

        try {
            Double.parseDouble(phTarget);
            bPhTargetNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            Double.parseDouble(phMin);
            bPhMinNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            Double.parseDouble(phMax);
            bPhMaxNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }

        if (!bPhTargetNumeric && !bPhMinNumeric && !bPhMaxNumeric) {
            checkList.add(FormulationGeneralConstant.RESERVE_ALKALINITY_UOM_REQUIRED_PH_UNABLE_TO_CHECK.getValue());
        }
        isLesserThanFour = isPhTargetOrPhMinLessThanFour(phTarget, phMin);
        isGreaterThanTen = isPhTargetOrPhMaxGreaterThanTen(phTarget, phMax);

        //Required (pH[target] or pH [Maximum] >10) or (pH[target] or pH [Minimum] < 4) or Business Area = 'Fabric Care' or 'Home Care'
        if ((isAerosol(productForm) || isSolid(productForm) || isLiquid(productForm))
                && (isLesserThanFour || isGreaterThanTen)
                && validateBusinessArea(connectedBusinessAreaBeans)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // Required when pH target or pH minimum is < 4 or pH target or pH maximum > 10.
        // range compare (warning: range can be blank also)
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }

        return checkList;
    }

    /**
     * Method to validate Reserve Alkalinity/Acidity pH titration endpoint field.
     *
     * @return List<String> - error message of Reserve Alkalinity/Acidity pH titration endpoint field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateReserveAlkalinityTitrationEndPoint() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
		// Modified as part of 2018x.6 - Starts        
        IFormulationPart formulationPart = physChemBean.getFormulationPartBean();
        List<BusinessAreaBean> connectedBusinessAreaBeans = formulationPart.getConnectedBusinessAreaBeans();
		// Modified as part of 2018x.6 - Ends

        String phTarget = physChemBean.getPH();
        String phMin = physChemBean.getPHMin();
        String phMax = physChemBean.getPHMax();

        boolean bPhTargetNumeric = false;
        boolean bPhMinNumeric = false;
        boolean bPhMaxNumeric = false;

        boolean isLesserThanFour;
        boolean isGreaterThanTen;

        try {
            Double.parseDouble(phTarget);
            bPhTargetNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            Double.parseDouble(phMin);
            bPhMinNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        try {
            Double.parseDouble(phMax);
            bPhMaxNumeric = true;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
        }
        if (!bPhTargetNumeric && !bPhMinNumeric && !bPhMaxNumeric) {
            checkList.add(FormulationGeneralConstant.RESERVE_ALKALINITY_TITRATION_END_POINT_REQUIRED_PH_UNABLE_TO_CHECK.getValue());
        }
        isLesserThanFour = isPhTargetOrPhMinLessThanFour(phTarget, phMin);
        isGreaterThanTen = isPhTargetOrPhMaxGreaterThanTen(phTarget, phMax);

        //Requird (pH[target] or pH [Maximum] >10) or (pH[target] or pH [Minimum] < 4) or Business Area = 'Fabric Care' or 'Home Care'
        if ((isAerosol(productForm) || isSolid(productForm) || isLiquid(productForm))
                && (isLesserThanFour || isGreaterThanTen)
                && validateBusinessArea(connectedBusinessAreaBeans)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Is the liquid corrosive to metal (Aluminum or Carbon Steel)? field.
     *
     * @return List<String> - error message of Is the liquid corrosive to metal (Aluminum or Carbon Steel)? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateLiquidCorrosiveToMetal() {
        List<String> checkList = new ArrayList<>();
        String productForm = physChemBean.getProductForm();
        String attributeValue = physChem.getValue();
        if (isLiquid(productForm) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Technical basis for the Corrosive to Metals determination provided field.
     *
     * @return List<String> - error message of Is the liquid corrosive to metal (Aluminum or Carbon Steel)? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateTechnicalBasisForTheCorrosiveToMetals() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String aerosolCanCorrosiveToMetals = physChemBean.getAerosolCanCorrosiveToMetals();
        //Checking excel input value for Bad chars
        if (isBadChars(attributeValue, physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_BAD_CHARACTER_TECHNICAL_BASIC_FOR_THE_CORROSIVE_TO_METAL.getValue()))) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_HAS_BAD_CHARACTER.getValue())));
        }
        //Required if "Are the Contents of the aerosol can Corrosive to Metals (Aluminum or Carbon Steel)? = Yes or No"

        String aersolTechnicalBasisForTheCorrosiveToMetals = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_TECHNICAL_BASIS_FOR_THE_CORROSIVE_TO_METALS_REQUIRED_WHEN_AEROSOL_CAN_CORROSIVE_TO_METALS.getValue());
        StringList aersolTechnicalBasisList = StringUtil.split(aersolTechnicalBasisForTheCorrosiveToMetals, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        String liquidTechnicalBasisForTheCorrosiveToMetals = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_TECHNICAL_BASIS_FOR_THE_CORROSIVE_TO_METALS_REQUIRED_WHEN_LIQUID_CORROSIVE_TO_METAL.getValue());
        StringList liquidTechnicalBasisList = StringUtil.split(liquidTechnicalBasisForTheCorrosiveToMetals, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        if (isAerosol(productForm)
                && aersolTechnicalBasisList.contains(aerosolCanCorrosiveToMetals)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // Required when 'Is the liquid corrosive to metal' =Yes
        if (isLiquid(productForm)
                && liquidTechnicalBasisList.contains(physChemBean.getLiquidCorrosiveToMetal())
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Is a Flammable Liquid absorbed or Contained within the solid? field.
     *
     * @return List<String> - error message of Is the liquid corrosive to metal (Aluminum or Carbon Steel)? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateFlammableLiquidAbsorbedOrContainedWithInTheSolid() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        if (isSolid(productForm) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(FormulationGeneralConstant.FLAMMABLE_LIQUID_ABSORBED_OR_CONTAINED_WITH_IN_THE_SOLID_REQUIRED_CHECK.getValue());
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Burn Rate, mm/sec [maximum tested value] field.
     *
     * @return List<String> - error message of Burn Rate, mm/sec [maximum tested value] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateBurnRate() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String flammableLiquidAbsorbedOrContainedWithInTheSolid = physChemBean.getFlammableLiquidAbsorbedOrContainedWithInTheSolid();

        String solidBurnRateSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_BURN_RATE_REQUIRED_WHEN_FLAMMABLE_LIQUID_ABSORBED_OR_CONTAINED_WITH_IN_THE_SOLID.getValue());
        StringList solidBurnRateSettingList = StringUtil.split(solidBurnRateSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        // Required when 'Is a Flammable Liquid absorbed or Contained within the solid?' = Yes
        if (isSolid(physChemBean.getProductForm())
                && solidBurnRateSettingList.contains(flammableLiquidAbsorbedOrContainedWithInTheSolid)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Does the product have any self reactive properties or is it thermally unstable? field.
     *
     * @return List<String> - error message of Does the product have any self reactive properties or is it thermally unstable?.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateProductHaveAnySelfReactiveProperties() {
        List<String> checkList = new ArrayList<>();
        String productHaveAnySelfReactivePropertiesAttributeValue = physChem.getValue();
        if (isSolid(physChemBean.getProductForm()) && UIUtil.isNullOrEmpty(productHaveAnySelfReactivePropertiesAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(productHaveAnySelfReactivePropertiesAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Self-Accelerating Decomposition Temperature (SADT) (C) [maximum tested value] field.
     *
     * @return List<String> - error message of Self-Accelerating Decomposition Temperature (SADT) (C) [maximum tested value] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateSelfAcceleratingDecompositionTemperature() {
        List<String> checkList = new ArrayList<>();
        String selfAcceleratingDecompositionTemperatureAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String productHaveAnySelfReactiveProperties = physChemBean.getProductHaveAnySelfReactiveProperties();
        //Required when 'Does the product show any self reactive properties or Is it thermally unstable?' = Yes
        if (isSolid(productForm)) {
            String solidSelfAcceleratingDecompositionTemperatureRequiredProductHaveAnySelfReactivePropertiesSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_SELF_ACCELERATING_DECOMPOSITION_TEMPERATURE_REQUIRED_WHEN_PRODUCT_HAVE_ANY_SELF_REACTIVE_PROPERTIES.getValue());
            StringList solidSelfAcceleratingDecompositionTemperatureRequiredProductHaveAnySelfReactivePropertiesSettingList = StringUtil.split(solidSelfAcceleratingDecompositionTemperatureRequiredProductHaveAnySelfReactivePropertiesSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (solidSelfAcceleratingDecompositionTemperatureRequiredProductHaveAnySelfReactivePropertiesSettingList.contains(productHaveAnySelfReactiveProperties)
                    && UIUtil.isNullOrEmpty(selfAcceleratingDecompositionTemperatureAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        String valueNumericCheckMessage = getNumericCheckMessage(selfAcceleratingDecompositionTemperatureAttributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Heat of Combustion (kJ/g)[calculated or tested value]
     *
     * @return List<String> - error message for Heat of Combustion (kJ/g)[calculated or tested value]
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateHeatOfCombustion() {
        List<String> checkList = new ArrayList<>();
        String heatOfCombustionAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        // value should not be blank if Product Form is Aerosol
        if (isAerosol(productForm) && UIUtil.isNullOrEmpty(heatOfCombustionAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // value should be numeric
        String valueNumericCheckMessage = getNumericCheckMessage(heatOfCombustionAttributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Heat of Decomposition (kJ/g).
     *
     * @return List<String> - error message for Heat of Decomposition (kJ/g).
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateHeatOfDecomposition() {
        List<String> checkList = new ArrayList<>();
        String heatOfDecompositionAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String productHaveAnySelfReactiveProperties = physChemBean.getProductHaveAnySelfReactiveProperties();
        // For Solid - it is Required when 'Does the product show any self reactive properties or Is it thermally unstable?'= Yes
        if (isSolid(productForm)) {
            String solidHeatOfCombustionProductHaveAnySelfReactivePropertiesSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_HEAT_OF_COMBUSTION_REQUIRED_WHEN_PRODUCT_HAVE_ANY_SELF_REACTIVE_PROPERTIES.getValue());
            StringList solidHeatOfCombustionProductHaveAnySelfReactivePropertiesSettingList = StringUtil.split(solidHeatOfCombustionProductHaveAnySelfReactivePropertiesSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (solidHeatOfCombustionProductHaveAnySelfReactivePropertiesSettingList.contains(productHaveAnySelfReactiveProperties)
                    && UIUtil.isNullOrEmpty(heatOfDecompositionAttributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        // value should be numeric
        String valueNumericCheckMessage = getNumericCheckMessage(heatOfDecompositionAttributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Can Construction field.
     *
     * @return List<String> - error message of Can Construction field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateCanConstruction() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        // value should not be blank if Product Form is Aerosol/Liquid/Solid.
        if (isAerosol(physChemBean.getProductForm()) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // value should match with attribute ranges. (warning: range has empty as one of the value)
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Gauge Pressure @ 130F/55C (kPa)[maximum value] field.
     *
     * @return List<String> - error message of Gauge Pressure @ 130F/55C (kPa)[maximum value] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateGaugePressure() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        // value should not be blank if Product Form is Aerosol
        if (isAerosol(physChemBean.getProductForm()) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Aerosol Type field.
     *
     * @return List<String> - error message of Aerosol Type field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateAerosolType() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        // value should not be blank if Product Form is Aerosol
        if (isAerosol(productForm) && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // value should compared with ranges.
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Is Aerosol test data needed? field.
     *
     * @return List<String> - error message of Is Aerosol test data needed?.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateIsAerosolTestDataNeeded() {
        List<String> checkList = new ArrayList<>();
        String isAerosolTestDataNeededAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        // value should not be blank if Product Form is Aerosol
        if (isAerosol(productForm) && UIUtil.isNullOrEmpty(isAerosolTestDataNeededAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        // value should compared with ranges.
        if (!physChem.getAttributeRanges().contains(isAerosolTestDataNeededAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Ignition Distance - cm [maximum tested value] field.
     *
     * @return List<String> - error message of Ignition Distance - cm [maximum tested value] .
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateIgnitionDistance() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String isAerosolTestDataNeeded = physChemBean.getIsAerosolTestDataNeeded();
        String aerosolType = physChemBean.getAerosolType();
        //Required when 'Is aerosol test data needed?' is set to 'Yes, All Others & Aerosol Type = 'Spray"
        String ignitionDistanceRequiredWhenAerosolTestNeeded = physChemProperties.getProperty(FormulationGeneralConstant.IGNITION_DISTANCE_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED.getValue());
        String ignitionDistanceRequiredWhenAerosolType = physChemProperties.getProperty(FormulationGeneralConstant.IGNITION_DISTANCE_REQUIRED_WHEN_AEROSOL_TYPE.getValue());

        if (isAerosol(physChemBean.getProductForm())
                && ignitionDistanceRequiredWhenAerosolTestNeeded.equals(isAerosolTestDataNeeded)
                && ignitionDistanceRequiredWhenAerosolType.equals(aerosolType)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Enclosed Space Ignition Time Equivalent (s/m3) [maximum tested value] field.
     *
     * @return List<String> - error message of Enclosed Space Ignition Time Equivalent (s/m3) [maximum tested value] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateEnclosedSpaceIgnition() {
        List<String> checkList = new ArrayList<>();

        String attributeValue = physChem.getValue();
        String isAerosolTestDataNeeded = physChemBean.getIsAerosolTestDataNeeded();
        String aerosolType = physChemBean.getAerosolType();
        //Required when 'Is aerosol test data needed?' is set to 'Yes, All Others & Aerosol Type = 'Spray"

        String enclosedIgnitionDistanceRequiredWhenAerosolTestNeeded = physChemProperties.getProperty(FormulationGeneralConstant.ENCLOSED_SPACE_IGNITION_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED.getValue());
        String enclosedIgnitionDistanceRequiredWhenAerosolType = physChemProperties.getProperty(FormulationGeneralConstant.ENCLOSED_SPACE_IGNITION_REQUIRED_WHEN_AEROSOL_TYPE.getValue());

        if (isAerosol(physChemBean.getProductForm())
                && enclosedIgnitionDistanceRequiredWhenAerosolTestNeeded.equals(isAerosolTestDataNeeded)
                && enclosedIgnitionDistanceRequiredWhenAerosolType.equals(aerosolType)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Foam Flammability Test:  Flame Height (cm) [maximum tested value] field.
     *
     * @return List<String> - error message of Foam Flammability Test:  Flame Height (cm) [maximum tested value] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateFlameHeight() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String isAerosolTestDataNeeded = physChemBean.getIsAerosolTestDataNeeded();
        String aerosolType = physChemBean.getAerosolType();
        //Required when 'Is aerosol test data needed?' is set to 'Yes, All Others & Aerosol Type = 'Spray"

        String flameHeightRequiredWhenAerosolTestNeeded = physChemProperties.getProperty(FormulationGeneralConstant.FLAME_HEIGHT_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED.getValue());
        String flameHeightRequiredWhenAerosolType = physChemProperties.getProperty(FormulationGeneralConstant.FLAME_HEIGHT_REQUIRED_WHEN_AEROSOL_TYPE.getValue());

        if (isAerosol(physChemBean.getProductForm())
                && flameHeightRequiredWhenAerosolTestNeeded.equals(isAerosolTestDataNeeded)
                && flameHeightRequiredWhenAerosolType.equals(aerosolType)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Foam Flammability Test:  Flame Duration (sec) [maximum tested value] field.
     *
     * @return List<String> - error message of Foam Flammability Test:  Flame Duration (sec) [maximum tested value] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateFlameDuration() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String isAerosolTestDataNeeded = physChemBean.getIsAerosolTestDataNeeded();
        String aerosolType = physChemBean.getAerosolType();
        //Required when 'Is aerosol test data needed?' is set to 'Yes, All Others & Aerosol Type = 'Spray"

        String flameDurationRequiredWhenAerosolTestNeeded = physChemProperties.getProperty(FormulationGeneralConstant.FLAME_DURATION_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED.getValue());
        String flameDurationRequiredWhenAerosolType = physChemProperties.getProperty(FormulationGeneralConstant.FLAME_DURATION_REQUIRED_WHEN_AEROSOL_TYPE.getValue());

        if (isAerosol(physChemBean.getProductForm())
                && flameDurationRequiredWhenAerosolTestNeeded.equals(isAerosolTestDataNeeded)
                && flameDurationRequiredWhenAerosolType.equals(aerosolType)
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Vapor Pressure (pascals) field.
     *
     * @return List<String> - error message of vapor Pressure (pascals).
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateVaporPressure() {
        List<String> checkList = new ArrayList<>();

        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        IFormulationPart formulationPart = physChemBean.getFormulationPartBean();

        List<BusinessAreaBean> connectedBusinessAreaBeans = formulationPart.getConnectedBusinessAreaBeans();
        List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans = formulationPart.getConnectedProductCategoryPlatformBeans();
        List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans = formulationPart.getConnectedProductTechnologyPlatformBeans();

        if (isAerosol(productForm)) {
            checkList.addAll(validateAerosolPlatforms(attributeValue, connectedBusinessAreaBeans, connectedProductCategoryPlatformBeans, connectedProductTechnologyPlatformBeans));
        }
        if (isSolid(productForm)) {
            checkList.addAll(validateSolidPlatforms(attributeValue, connectedBusinessAreaBeans, connectedProductCategoryPlatformBeans, connectedProductTechnologyPlatformBeans));
        }
        if (isLiquid(productForm)) {
            checkList.addAll(validateLiquidPlatforms(attributeValue, connectedBusinessAreaBeans, connectedProductCategoryPlatformBeans, connectedProductTechnologyPlatformBeans));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Vapor Density field.
     *
     * @return List<String> - error message of Vapor Density field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateVaporDensity() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        // vapor density can be blank but if value is provided then it should be numeric.
        if (isAerosol(physChemBean.getProductForm()) && UIUtil.isNotNullAndNotEmpty(attributeValue)) {
            // value should be numeric
            try {
                Double.parseDouble(attributeValue);
            } catch (NumberFormatException e) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
            }
        }
        return checkList;
    }

    /**
     * Method to validate Are the Contents of the aerosol can Corrosive to Metals (Aluminum or Carbon Steel)? field.
     *
     * @return List<String> - error message of Are the Contents of the aerosol can Corrosive to Metals (Aluminum or Carbon Steel)? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateAerosolCanCorrosiveToMetals() {
        List<String> checkList = new ArrayList<>();
        String aerosolCanCorrosiveToMetalsAttributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        if (isAerosol(productForm) && UIUtil.isNullOrEmpty(aerosolCanCorrosiveToMetalsAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(aerosolCanCorrosiveToMetalsAttributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Conductivity of the contents in the aerosol can (microsiemens/cm) field.
     *
     * @return List<String> - error message of Conductivity of the contents in the aerosol can (microsiemens/cm) field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateAerosolConductivityOfTheContents() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        // Required if "Are the Contents of the aerosol can Corrosive to Metals (Aluminum or Carbon Steel)?' = Unknown

        String aerosolConductivityOfTheContentsRequiredWhenAerosolCanCorrosiveToMetal = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_CONDUCTIVITY_OF_THE_CONTENTS_REQUIRED_WHEN_AEROSOL_CAN_CORROSIVE_TO_METALS.getValue());
        StringList aerosolConductivityOfTheContentsRequiredWhenAerosolCanCorrosiveToMetalList = StringUtil.split(aerosolConductivityOfTheContentsRequiredWhenAerosolCanCorrosiveToMetal, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        if (isAerosol(productForm)
                && aerosolConductivityOfTheContentsRequiredWhenAerosolCanCorrosiveToMetalList.contains(physChemBean.getAerosolCanCorrosiveToMetals())
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Conductivity of the liquid (microsiemens/cm) field.
     *
     * @return List<String> - error message list of Conductivity of the liquid (microsiemens/cm) field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateConductivityoftheLiquid() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        //Required when  'Is the liquid corrosive to metal' = Unknown

        String liquidConductivityOfTheContentsRequiredWhenLiquidCorrosiveToMetal = physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_CONDUCTIVITY_OF_THE_CONTENTS_REQUIRED_WHEN_LIQUID_CORROSIVE_TO_METAL.getValue());
        StringList liquidConductivityOfTheContentsRequiredWhenLiquidCorrosiveToMetalList = StringUtil.split(liquidConductivityOfTheContentsRequiredWhenLiquidCorrosiveToMetal, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        if (isLiquid(productForm)
                && liquidConductivityOfTheContentsRequiredWhenLiquidCorrosiveToMetalList.contains(physChemBean.getLiquidCorrosiveToMetal())
                && UIUtil.isNullOrEmpty(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to validate Is the propellant flammable or nonflammable? field.
     *
     * @return List<String> - error message of Is the propellant flammable or nonflammable? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateFlammableOrNonFlammable() {
        List<String> checkList = new ArrayList<>();
        String productForm = physChemBean.getProductForm();
        String attributeValue = physChem.getValue();
        String canConstructionValue = physChemBean.getCanConstruction();

        String flammableOrNonFlammableRequiredCanConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_FLAMMABLE_OR_NON_FLAMMABLE_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue());
        StringList flammableOrNonFlammableRequiredCanConstructionSettingList = StringUtil.split(flammableOrNonFlammableRequiredCanConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        if (isAerosol(productForm) && UIUtil.isNullOrEmpty(attributeValue) && flammableOrNonFlammableRequiredCanConstructionSettingList.contains(canConstructionValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Percent by weight of flammable propellant in aerosol container, % field.
     *
     * @return List<String> - error message of Percent by weight of flammable propellant in aerosol container, % field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePercentByWeightOfFlammablePropellantInAerosolContainer() {
        List<String> checkList = new ArrayList<>();
        String productForm = physChemBean.getProductForm();
        String attributeValue = physChem.getValue();
        String canConstruction = physChemBean.getCanConstruction();
        String flammableOrNonFlammable = physChemBean.getFlammableOrNonFlammable();
        if (isAerosol(productForm)) {
            String percentByWeightRequiredCanConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue());
            StringList percentByWeightRequiredCanConstructionSettingList = StringUtil.split(percentByWeightRequiredCanConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String percentByWeightRequiredFlammableOrNonFlammableSetting = physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE.getValue());
            StringList percentByWeightRequiredFlammableOrNonFlammableSettingList = StringUtil.split(percentByWeightRequiredFlammableOrNonFlammableSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            if (percentByWeightRequiredCanConstructionSettingList.contains(canConstruction)
                    && percentByWeightRequiredFlammableOrNonFlammableSettingList.contains(flammableOrNonFlammable)
                    && UIUtil.isNullOrEmpty(attributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        //Required when 'Can construction' = Plastic and 'Is the propellant flammable or nonflammable' =flammable
        String valueNumericCheckMessage = getNumericCheckMessage(attributeValue);
        if (UIUtil.isNotNullAndNotEmpty(valueNumericCheckMessage)) {
            checkList.add(valueNumericCheckMessage);
        }
        return checkList;
    }

    /**
     * Method to check is value double.
     *
     * @param valueStr - String
     * @return boolean
     * @since DSM 2018x.5
     */
    public boolean isDoubleNumericFormat(String valueStr) {
        boolean bIsNumeric = true;
        try {
            Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            bIsNumeric = false;
        }
        return bIsNumeric;
    }

    /**
     * Method to validate Does the base product contain < 50% water by volume AND contain <50% by volume water-miscible alcohols? field.
     *
     * @return List<String> - error message of Does the base product contain < 50% water by volume AND contain <50% by volume water-miscible alcohols? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateDoesBaseProductContainByVolumeWaterMiscibleAlcohols() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String canConstruction = physChemBean.getCanConstruction();
        String flammableOrNonFlammable = physChemBean.getFlammableOrNonFlammable();
        String percentByWeightOfFlammablePropellantInAerosolContainer = physChemBean.getPercentByWeightOfFlammablePropellantInAerosolContainer();

        if (isAerosol(productForm)) {
            boolean bIsNumericPercentByWeightOfFlammablePropellantInAerosolContainer = isDoubleNumericFormat(percentByWeightOfFlammablePropellantInAerosolContainer);
            if (!bIsNumericPercentByWeightOfFlammablePropellantInAerosolContainer) {
                checkList.add(FormulationGeneralConstant.DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_NUMERIC_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER.getValue());
            } else {
                String productContainByVolumeWaterMiscibleAlcoholsRequiredCanConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue());
                StringList productContainByVolumeWaterMiscibleAlcoholsRequiredCanConstructionSettingList = StringUtil.split(productContainByVolumeWaterMiscibleAlcoholsRequiredCanConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
                String productContainByVolumeWaterMiscibleAlcoholsRequiredFlammableOrNonFlammableSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE.getValue());
                StringList productContainByVolumeWaterMiscibleAlcoholsRequiredFlammableOrNonFlammableSettingList = StringUtil.split(productContainByVolumeWaterMiscibleAlcoholsRequiredFlammableOrNonFlammableSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
                String productContainByVolumeWaterMiscibleAlcoholsRequiredPercentByWeightOfFlammablePropellantInAerosolContainer = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_WHEN_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER.getValue());

                if (productContainByVolumeWaterMiscibleAlcoholsRequiredCanConstructionSettingList.contains(canConstruction)
                        && productContainByVolumeWaterMiscibleAlcoholsRequiredFlammableOrNonFlammableSettingList.contains(flammableOrNonFlammable)
                        && Integer.parseInt(percentByWeightOfFlammablePropellantInAerosolContainer) <= Integer.parseInt(productContainByVolumeWaterMiscibleAlcoholsRequiredPercentByWeightOfFlammablePropellantInAerosolContainer)
                        && UIUtil.isNullOrEmpty(attributeValue)) {
                    checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
                }
            }
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product contain > 50% water AND < 15.8% by weight (20% by volume) ethanol plus isopropanol? field.
     *
     * @return List<String> - error message of Does the base product contain > 50% water AND < 15.8% by weight (20% by volume) ethanol plus isopropanol? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateDoesTheBaseProductContainEthanolPlusISOPropanol() {
        List<String> checkList = new ArrayList<>();
        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String canConstruction = physChemBean.getCanConstruction();
        String flammableOrNonFlammable = physChemBean.getFlammableOrNonFlammable();

        boolean isAttributeValueEmpty = UIUtil.isNullOrEmpty(physChem.getValue());

        String canConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL_REQUIRED_CAN_CONSTRUCTION.getValue());
        StringList canConstructionSettingList = StringUtil.split(canConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

        // Required when 'Can construction' = Plastic
        // and 'Is the propellant flammable or nonflammable' =nonflammable
        if (isSolid(productForm) && FormulationGeneralConstant.NON_FLAMMABLE.getValue().equals(flammableOrNonFlammable) && canConstructionSettingList.contains(canConstruction) && isAttributeValueEmpty) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product (no propellant) Sustain Combustion? field.
     *
     * @return List<String> - error message of Does the base product (no propellant) Sustain Combustion? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateDoesBaseProductSustainCombustion() {
        List<String> checkList = new ArrayList<>();

        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();

        String canConstruction = physChemBean.getCanConstruction();
        String flammableOrNonFlammable = physChemBean.getFlammableOrNonFlammable();

        if (isAerosol(productForm)) {

            String canConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue());
            StringList canConstructionSettingList = StringUtil.split(canConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String percentByWeightOfFlammablePropellantInAerosolContainerSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER.getValue());
            StringList productContainByVolumeWaterMiscibleAlcoholsSettingList = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            StringList isoPropanolSettingList = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            // if aerosol is flammable
            checkList.addAll(validateDoesBaseProductSustainCombustionAerosolFlammable(
                    canConstruction,
                    flammableOrNonFlammable,
                    physChemBean.getDoesBaseProductContainByVolumeWaterMiscibleAlcohols(),
                    physChemBean.getPercentByWeightOfFlammablePropellantInAerosolContainer(),
                    canConstructionSettingList,
                    percentByWeightOfFlammablePropellantInAerosolContainerSetting,
                    productContainByVolumeWaterMiscibleAlcoholsSettingList));

            // if aerosol is non-flammable
            checkList.addAll(validateDoesBaseProductSustainCombustionAerosolNonFlammable(canConstruction,
                    flammableOrNonFlammable,
                    physChemBean.getDoesTheBaseProductContainEthanolPlusISOPropanol(),
                    canConstructionSettingList,
                    isoPropanolSettingList));
        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product have a Fire Point? field.
     *
     * @return List<String> - error message of Does the base product have a Fire Point? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateDoesBaseProductHaveAFirePoint() {
        List<String> checkList = new ArrayList<>();

        String attributeValue = physChem.getValue();
        String productForm = physChemBean.getProductForm();
        String percentByWeightOfFlammablePropellantInAerosolContainer = physChemBean.getPercentByWeightOfFlammablePropellantInAerosolContainer();
        if (isAerosol(productForm)) {
            StringList canConstructionSettingList = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String percentageWeightAerosol = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER.getValue());
            StringList waterMiscibleSettingList = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            StringList productSustainCombustionSettingList = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            StringList aersolISOPropanolSettingList = StringUtil.split(physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL.getValue()), FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            // if aerosol is flammable
            checkList.addAll(validateDoesBaseProductHaveAFirePointAerosolFlammable(percentByWeightOfFlammablePropellantInAerosolContainer,
                    percentageWeightAerosol,
                    canConstructionSettingList,
                    productSustainCombustionSettingList,
                    waterMiscibleSettingList));

            // if aerosol is non-flammable
            checkList.addAll(validateDoesBaseProductHaveAFirePointAerosolNonFlammable(canConstructionSettingList,
                    productSustainCombustionSettingList,
                    aersolISOPropanolSettingList));

        }
        if (!physChem.getAttributeRanges().contains(attributeValue)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product contain >= 50% water AND <=4% by weight of an emulsified liquefied non-flammable gas propellant? field.
     *
     * @return List<String> - error message of Does the base product contain >= 50% water AND <=4% by weight of an emulsified liquefied non-flammable gas propellant? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant() {
        List<String> checkList = new ArrayList<>();
        String doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant = physChem.getValue();
        String productForm = physChemBean.getProductForm();

        String canConstruction = physChemBean.getCanConstruction();
        String flammableOrNonFlammable = physChemBean.getFlammableOrNonFlammable();
        String doesBaseProductSustainCombustion = physChemBean.getDoesBaseProductSustainCombustion();
        String doesTheBaseProductContainEthanolPlusISOPropanol = physChemBean.getDoesTheBaseProductContainEthanolPlusISOPropanol();
        String doesBaseProductHaveAFirePoint = physChemBean.getDoesBaseProductHaveAFirePoint();

        if (isAerosol(productForm)) {
            String canConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue());
            StringList canConstructionSettingList = StringUtil.split(canConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String flammableOrNonFlammableSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE.getValue());
            StringList flammableOrNonFlammableSettingList = StringUtil.split(flammableOrNonFlammableSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String doesTheBaseProductContainEthanolPlusISOPropanolSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL.getValue());
            StringList doesTheBaseProductContainEthanolPlusISOPropanolSettingList = StringUtil.split(doesTheBaseProductContainEthanolPlusISOPropanolSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String doesBaseProductSustainCombustionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION.getValue());
            StringList doesBaseProductSustainCombustionSettingList = StringUtil.split(doesBaseProductSustainCombustionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());
            String doesBaseProductHaveAFirePointSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT.getValue());
            StringList doesBaseProductHaveAFirePointSettingList = StringUtil.split(doesBaseProductHaveAFirePointSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            // Required when 'Can construction' = Plastic
            // and 'Is the propellant flammable or nonflammable' =nonflammable
            // and 'Does the base product contain  >= 50% water AND <= 15.8% by weight (20% by volume) ethanol plus isopropanol?' =No
            // and 'Does the base product (no propellant) Sustain Combustion' = Unknown
            // and 'Does the base product have a Fire Point' =Unknown
            if (canConstructionSettingList.contains(canConstruction)
                    && flammableOrNonFlammableSettingList.contains(flammableOrNonFlammable)
                    && doesTheBaseProductContainEthanolPlusISOPropanolSettingList.contains(doesTheBaseProductContainEthanolPlusISOPropanol)
                    && doesBaseProductSustainCombustionSettingList.contains(doesBaseProductSustainCombustion)
                    && doesBaseProductHaveAFirePointSettingList.contains(doesBaseProductHaveAFirePoint)
                    && UIUtil.isNullOrEmpty(doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (!physChem.getAttributeRanges().contains(doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product contain >= 50% water AND <=4% by weight of an emulsified liquefied flammable gas propellant that remains emulsified for the life of the product? field.
     *
     * @return List<String> - error message of Does the base product contain >= 50% water AND <=4% by weight of an emulsified liquefied flammable gas propellant that remains emulsified for the life of the product? field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validateDoesBaseProductContainEmulsifiedLiquifiedFlammableGasPropellant() {
        List<String> checkList = new ArrayList<>();
        String doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant = physChem.getValue();
        String productForm = physChemBean.getProductForm();

        String canConstruction = physChemBean.getCanConstruction();
        String flammableOrNonFlammable = physChemBean.getFlammableOrNonFlammable();
        String doesBaseProductSustainCombustion = physChemBean.getDoesBaseProductSustainCombustion();
        String doesBaseProductContainByVolumeWaterMiscibleAlcohols = physChemBean.getDoesBaseProductContainByVolumeWaterMiscibleAlcohols();
        String doesBaseProductHaveAFirePoint = physChemBean.getDoesBaseProductHaveAFirePoint();

        if (isAerosol(productForm) && UIUtil.isNullOrEmpty(doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant)) {
            String canConstructionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_CAN_CONSTRUCTION.getValue());
            StringList canConstructionSettingList = StringUtil.split(canConstructionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            String flammableOrNonFlammableSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE.getValue());
            StringList flammableOrNonFlammableSettingList = StringUtil.split(flammableOrNonFlammableSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            String doesBaseProductContainByVolumeWaterMiscibleAlcoholSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS.getValue());
            StringList doesBaseProductContainByVolumeWaterMiscibleAlcoholSettingList = StringUtil.split(doesBaseProductContainByVolumeWaterMiscibleAlcoholSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            String doesBaseProductSustainCombustionSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION.getValue());
            StringList doesBaseProductSustainCombustionSettingList = StringUtil.split(doesBaseProductSustainCombustionSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            String doesBaseProductHaveAFirePointSetting = physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT.getValue());
            StringList doesBaseProductHaveAFirePointSettingList = StringUtil.split(doesBaseProductHaveAFirePointSetting, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue());

            // Required when 'Can construction' = Plastic
            // and 'Is the propellant flammable or nonflammable' =flammable
            // and 'Does the base product contain <= 50% water by volume AND contain <=50% by volume water-miscible alcohols' = No or Unknown
            // and 'Does the base product (no propellant) Sustain Combustion' = Yes or Unknown
            // and 'Does the base product have a Fire Point' =Yes
            if (canConstructionSettingList.contains(canConstruction)
                    && flammableOrNonFlammableSettingList.contains(flammableOrNonFlammable)
                    && doesBaseProductContainByVolumeWaterMiscibleAlcoholSettingList.contains(doesBaseProductContainByVolumeWaterMiscibleAlcohols)
                    && doesBaseProductSustainCombustionSettingList.contains(doesBaseProductSustainCombustion)
                    && doesBaseProductHaveAFirePointSettingList.contains(doesBaseProductHaveAFirePoint)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        }
        if (!physChem.getAttributeRanges().contains(doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant)) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue())));
        }

        return checkList;
    }

    /**
     * Method to validate pH [minimum] field.
     *
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePHMin() {
        List<String> checkList = new ArrayList<>();
        String phMinAttributeValue = physChem.getValue();
        String phTarget = physChemBean.getPH();

        // value should be numeric and max one decimal
        if (UIUtil.isNotNullAndNotEmpty(phMinAttributeValue)) {
            try {
                double phMin = Double.parseDouble(phMinAttributeValue);
                if (phMin >= 0 && phMin <= 14 && !isPHMaxOneDecimalPlace(phMin)) {
                    checkList.add(FormulationGeneralConstant.PH_NUMERIC_DECIMAL_PLACE_CHECK.getValue());
                }
                checkList.addAll(validatePHMinLessThanTarget(phTarget, phMin));
            } catch (NumberFormatException e) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
            }
        }
        return checkList;
    }

    /**
     * Method to validate if Ph Min is less than Ph Target
     *
     * @param phTarget - String
     * @param phMin    - String
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePHMinLessThanTarget(String phTarget, double phMin) {
        List<String> checkList = new ArrayList<>();
        try {
            double phTargetValue = Double.parseDouble(phTarget);
            if (phMin > phTargetValue) {
                checkList.add(FormulationGeneralConstant.PH_MIN_MUST_BE_LESSER_THAN_TARGET_CHECK.getValue());
            }
        } catch (NumberFormatException e) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate pH [maximum] field.
     *
     * @return List<String> - error message of pH [maximum] field.
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePHMax() {
        List<String> checkList = new ArrayList<>();
        String phMaxAttributeValue = physChem.getValue();
        String phTargetAttributeValue = physChemBean.getPH();
        // value should be numeric and max one decimal
        // "Value >=0 and <=14
        // allow maximum 1 decimal place
        if (UIUtil.isNotNullAndNotEmpty(phMaxAttributeValue)) {
            try {
                double phMax = Double.parseDouble(phMaxAttributeValue);
                if (phMax >= 0 && phMax <= 14 && !isPHMaxOneDecimalPlace(phMax)) {
                    checkList.add(FormulationGeneralConstant.PH_MAX_NUMERIC_DECIMAL_PLACE_CHECK.getValue());
                }
                checkList.addAll(validatePHMaxGreaterThanTarget(phTargetAttributeValue, phMax));
            } catch (NumberFormatException e) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
            }
        }
        return checkList;
    }

    /**
     * Method to validate if Ph Max is greater than Ph Target
     *
     * @param phTarget - String
     * @param phMax    - Double
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    public List<String> validatePHMaxGreaterThanTarget(String phTarget, Double phMax) {
        List<String> checkList = new ArrayList<>();
        try {
            double phTargetValue = Double.parseDouble(phTarget);
            if (!(phMax == 14 && phTargetValue == 14) && phMax < phTargetValue) {
                checkList.add(FormulationGeneralConstant.PH_MAX_MUST_BE_GREATER_THAN_TARGET_CHECK.getValue());
            }
        } catch (NumberFormatException e) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to check product form is aerosol.
     *
     * @param productForm - String
     * @return boolean
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private boolean isAerosol(String productForm) {
        return Arrays.stream(ProductFormAerosolCategory.values()).map(ProductFormAerosolCategory::getValue).collect(Collectors.toList()).contains(productForm);
    }

    /**
     * Method to check product form is solid.
     *
     * @param productForm - String
     * @return boolean
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private boolean isSolid(String productForm) {
        return Arrays.stream(ProductFormSolidCategory.values()).map(ProductFormSolidCategory::getValue).collect(Collectors.toList()).contains(productForm);
    }

    /**
     * Method to check product form is liquid.
     *
     * @param productForm - String
     * @return boolean
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private boolean isLiquid(String productForm) {
        return Arrays.stream(ProductFormLiquidCategory.values()).map(ProductFormLiquidCategory::getValue).collect(Collectors.toList()).contains(productForm);
    }

    /**
     * Method to validate Aerosol - Business Area, Product Category Platform, Product Technology Platform
     *
     * @param attributeValue                          - String
     * @param connectedBusinessAreaBeans              - List<BusinessAreaBean>
     * @param connectedProductCategoryPlatformBeans   - List<ProductCategoryPlatformBean>
     * @param connectedProductTechnologyPlatformBeans - List<ProductTechnologyPlatformBean>
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private List<String> validateAerosolPlatforms(String attributeValue,
                                                  List<BusinessAreaBean> connectedBusinessAreaBeans,
                                                  List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans,
                                                  List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans) {
        return validateVaporPressurePlatforms(attributeValue,
                physChemBean.getRequiredAerosolBusinessAreaBean(),
                connectedBusinessAreaBeans,
                physChemBean.getRequiredAerosolProductCategoryPlatformBean(),
                connectedProductCategoryPlatformBeans,
                physChemBean.getRequiredAerosolProductTechnologyPlatformBean(),
                connectedProductTechnologyPlatformBeans);
    }

    /**
     * Method to validate Solid - Business Area, Product Category Platform, Product Technology Platform
     *
     * @param attributeValue                          - String
     * @param connectedBusinessAreaBeans              - List<BusinessAreaBean>
     * @param connectedProductCategoryPlatformBeans   - List<ProductCategoryPlatformBean>
     * @param connectedProductTechnologyPlatformBeans - List<ProductTechnologyPlatformBean>
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private List<String> validateSolidPlatforms(String attributeValue,
                                                List<BusinessAreaBean> connectedBusinessAreaBeans,
                                                List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans,
                                                List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans) {
        return validateVaporPressurePlatforms(attributeValue,
                physChemBean.getRequiredSolidBusinessAreaBean(),
                connectedBusinessAreaBeans,
                physChemBean.getRequiredSolidProductCategoryPlatformBean(),
                connectedProductCategoryPlatformBeans,
                physChemBean.getRequiredSolidProductTechnologyPlatformBean(),
                connectedProductTechnologyPlatformBeans);
    }

    /**
     * Method to validate Liquid - Business Area, Product Category Platform, Product Technology Platform
     *
     * @param attributeValue                          - String
     * @param connectedBusinessAreaBeans              - List<BusinessAreaBean>
     * @param connectedProductCategoryPlatformBeans   - List<ProductCategoryPlatformBean>
     * @param connectedProductTechnologyPlatformBeans - List<ProductTechnologyPlatformBean>
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private List<String> validateLiquidPlatforms(String attributeValue,
                                                 List<BusinessAreaBean> connectedBusinessAreaBeans,
                                                 List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans,
                                                 List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans) {
        return validateVaporPressurePlatforms(attributeValue,
                physChemBean.getRequiredLiquidBusinessAreaBean(),
                connectedBusinessAreaBeans,
                physChemBean.getRequiredLiquidProductCategoryPlatformBean(),
                connectedProductCategoryPlatformBeans,
                physChemBean.getRequiredLiquidProductTechnologyPlatformBean(),
                connectedProductTechnologyPlatformBeans);
    }

    /**
     * Method to validate Vapor Pressure Platforms - Business Area, Product Category Platform, Product Technology Platform
     *
     * @param attributeValue                          - String
     * @param requiredBusinessAreaBeans               - List<BusinessAreaBean>
     * @param connectedBusinessAreaBeans              - List<ProductCategoryPlatformBean>
     * @param requiredProductCategoryPlatformBeans    - List<ProductTechnologyPlatformBean>
     * @param connectedProductCategoryPlatformBeans   - List<BusinessAreaBean>
     * @param requiredProductTechnologyPlatformBeans  - List<ProductCategoryPlatformBean>
     * @param connectedProductTechnologyPlatformBeans - List<ProductTechnologyPlatformBean>
     * @return List<String> - error message of pH [minimum].
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private List<String> validateVaporPressurePlatforms(String attributeValue,
                                                        List<BusinessAreaBean> requiredBusinessAreaBeans,
                                                        List<BusinessAreaBean> connectedBusinessAreaBeans,
                                                        List<ProductCategoryPlatformBean> requiredProductCategoryPlatformBeans,
                                                        List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans,
                                                        List<ProductTechnologyPlatformBean> requiredProductTechnologyPlatformBeans,
                                                        List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans) {
    	List<String> checkList = new ArrayList<>();
        List<BusinessAreaBean> matchingBusinessAreaList = requiredBusinessAreaBeans.stream().filter(connectedBusinessAreaBean -> connectedBusinessAreaBeans.stream().anyMatch(requiredBusinessAreaBean -> requiredBusinessAreaBean.getId().equals(connectedBusinessAreaBean.getId()))).collect(Collectors.toList());
        List<ProductCategoryPlatformBean> matchingProductCategoryPlatformList = requiredProductCategoryPlatformBeans.stream().filter(connectedProductCategoryPlatformBean -> connectedProductCategoryPlatformBeans.stream().anyMatch(requiredProductCategoryPlatformBean -> requiredProductCategoryPlatformBean.getId().equals(connectedProductCategoryPlatformBean.getId()))).collect(Collectors.toList());
        List<ProductTechnologyPlatformBean> matchingProductTechnologyPlatformList = requiredProductTechnologyPlatformBeans.stream().filter(connectedProductTechnologyPlatformBean -> connectedProductTechnologyPlatformBeans.stream().anyMatch(requiredProductTechnologyPlatformBean -> requiredProductTechnologyPlatformBean.getId().equals(connectedProductTechnologyPlatformBean.getId()))).collect(Collectors.toList());
        // vapor pressure should not be empty, when Business Area = Home Care & Product Category Platform='Air Care - Continuous" & Product Technology Platform ="Wicks or Membrane"
        if (!matchingBusinessAreaList.isEmpty() && !matchingProductCategoryPlatformList.isEmpty() && !matchingProductTechnologyPlatformList.isEmpty()) {
            if (UIUtil.isNullOrEmpty(attributeValue)) {
                checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
            }
        } else {
            physChem.setGreyedOut(true);
        }
        return checkList;
    }

    /**
     * Method to check if input is numeric
     *
     * @return String - error message if not numeric
     * @since DSM 2018x.5
     */
    private String getNumericCheckMessage(String inputValue) {
        String retMessage = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(inputValue)) {
            try {
                Double.parseDouble(inputValue);
            } catch (NumberFormatException e) {
                retMessage = physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE.getValue()));
            }
        }
        return retMessage;
    }

    /**
     * Method to check if input ranges are valid
     *
     * @return String - error message if ranges are invalid
     * @since DSM 2018x.5
     */
    @SuppressWarnings("unused")
    private String getRangeCheckMessage(String inputValue) {
        String retMessage = DomainConstants.EMPTY_STRING;
        if (!physChem.getAttributeRanges().contains(inputValue)) {
            retMessage = physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE.getValue()));
        }
        return retMessage;
    }

    /**
     * Method to validate Does the base product (no propellant) Sustain Combustion? for Aerosol - Flammable
     *
     * @param canConstruction                                               - String
     * @param flammableOrNonFlammable                                       - String
     * @param doesBaseProductContainByVolumeWaterMiscibleAlcohols           - String
     * @param percentByWeightOfFlammablePropellantInAerosolContainer        - String
     * @param canConstructionSettingList                                    - StringList
     * @param percentByWeightOfFlammablePropellantInAerosolContainerSetting - StringList
     * @param productContainByVolumeWaterMiscibleAlcoholsSettingList        - StringList
     * @return List<String> - error message of Does the base product (no propellant) Sustain Combustion? field.
     * @since DSM 2018x.5
     */
    private List<String> validateDoesBaseProductSustainCombustionAerosolFlammable(String canConstruction,
                                                                                  String flammableOrNonFlammable,
                                                                                  String doesBaseProductContainByVolumeWaterMiscibleAlcohols,
                                                                                  String percentByWeightOfFlammablePropellantInAerosolContainer,
                                                                                  StringList canConstructionSettingList,
                                                                                  String percentByWeightOfFlammablePropellantInAerosolContainerSetting,
                                                                                  StringList productContainByVolumeWaterMiscibleAlcoholsSettingList) {
        List<String> checkList = new ArrayList<>();
        // Required when 'Can construction' = Plastic
        // and 'Is the propellant flammable or nonflammable' =flammable
        // and 'Percent by weight of flammable propellant in aerosol container, %'   ≤ 10
        // and  Does the base product contain <= 50% water by volume AND contain <=50% by volume water-miscible alcohols' = No or Unknown
        if (FormulationGeneralConstant.FLAMMABLE.getValue().equals(flammableOrNonFlammable)) {
            boolean bIsNumericPercentByWeightOfFlammablePropellantInAerosolContainer = isDoubleNumericFormat(percentByWeightOfFlammablePropellantInAerosolContainer);
            if (!bIsNumericPercentByWeightOfFlammablePropellantInAerosolContainer) {
                checkList.add(FormulationGeneralConstant.DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_NUMERIC_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER.getValue());
            } else {
                if (canConstructionSettingList.contains(canConstruction)
                        && Integer.parseInt(percentByWeightOfFlammablePropellantInAerosolContainer) <= Integer.parseInt(percentByWeightOfFlammablePropellantInAerosolContainerSetting)
                        && productContainByVolumeWaterMiscibleAlcoholsSettingList.contains(doesBaseProductContainByVolumeWaterMiscibleAlcohols)
                        && UIUtil.isNullOrEmpty(physChem.getValue())) {
                    checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
                }
            }
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product (no propellant) Sustain Combustion? for Aerosol - Flammable
     *
     * @param canConstruction                                 - String
     * @param flammableOrNonFlammable                         - String
     * @param doesTheBaseProductContainEthanolPlusISOPropanol - String
     * @param canConstructionSettingList                      - StringList
     * @param isoPropanolSettingList                          - StringList
     * @return List<String> - error message of Does the base product (no propellant) Sustain Combustion? field.
     * @since DSM 2018x.5
     */
    private List<String> validateDoesBaseProductSustainCombustionAerosolNonFlammable(String canConstruction,
                                                                                     String flammableOrNonFlammable,
                                                                                     String doesTheBaseProductContainEthanolPlusISOPropanol,
                                                                                     StringList canConstructionSettingList,
                                                                                     StringList isoPropanolSettingList) {
        List<String> checkList = new ArrayList<>();
        // Required when 'Can construction' = Plastic
        // and 'Is the propellant flammable or nonflammable' =nonflammable
        // and 'Does the base product contain  >= 50% water AND <= 15.8% by weight (20% by volume) ethanol plus isopropanol?' =No
        if (FormulationGeneralConstant.NON_FLAMMABLE.getValue().equals(flammableOrNonFlammable) && canConstructionSettingList.contains(canConstruction)
                && isoPropanolSettingList.contains(doesTheBaseProductContainEthanolPlusISOPropanol)
                && UIUtil.isNullOrEmpty(physChem.getValue())) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product have a Fire Point? for Aerosol - Flammable
     *
     * @param percentByWeightOfFlammablePropellantInAerosolContainer - String
     * @param percentageWeightAerosol                                - String
     * @param canConstructionSettingList                             - StringList
     * @param productSustainCombustionSettingList                    - StringList
     * @param waterMiscibleSettingList                               - StringList
     * @return List<String> - error message of Does the base product (no propellant) Sustain Combustion? field.
     * @since DSM 2018x.5
     */
    private List<String> validateDoesBaseProductHaveAFirePointAerosolFlammable(String percentByWeightOfFlammablePropellantInAerosolContainer,
                                                                               String percentageWeightAerosol,
                                                                               StringList canConstructionSettingList,
                                                                               StringList productSustainCombustionSettingList,
                                                                               StringList waterMiscibleSettingList) {
        List<String> checkList = new ArrayList<>();
        // Required when 'Can construction' = Plastic
        // and 'Is the propellant flammable or nonflammable' =flammable
        // and 'Percent by weight of flammable propellant in aerosol container, %'   ≤ 10
        // and  Does the base product contain <= 50% water by volume AND contain <=50% by volume water-miscible alcohols' = No or Unknown
        // and 'Does the base product (no propellant) Sustain Combustion' = Yes or Unknown
        if (FormulationGeneralConstant.FLAMMABLE.getValue().equals(physChemBean.getFlammableOrNonFlammable())) {
            boolean bIsNumericPercentByWeight = isDoubleNumericFormat(percentByWeightOfFlammablePropellantInAerosolContainer);
            if (!bIsNumericPercentByWeight) {
                checkList.add(FormulationGeneralConstant.DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER_NUMERIC_CHECK.getValue());
            } else {
                if (canConstructionSettingList.contains(physChemBean.getCanConstruction())
                        && Integer.parseInt(percentByWeightOfFlammablePropellantInAerosolContainer) <= Integer.parseInt(percentageWeightAerosol)
                        && waterMiscibleSettingList.contains(physChemBean.getDoesBaseProductContainByVolumeWaterMiscibleAlcohols())
                        && productSustainCombustionSettingList.contains(physChemBean.getDoesBaseProductSustainCombustion())
                        && UIUtil.isNullOrEmpty(physChem.getValue())) {
                    checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
                }
            }
        }
        return checkList;
    }

    /**
     * Method to validate Does the base product have a Fire Point? for Aerosol - Non-Flammable
     *
     * @param canConstructionSettingList          - StringList
     * @param productSustainCombustionSettingList - StringList
     * @param aersolISOPropanolSettingList        - StringList
     * @return List<String> - error message of Does the base product (no propellant) Sustain Combustion? field.
     * @since DSM 2018x.5
     */
    private List<String> validateDoesBaseProductHaveAFirePointAerosolNonFlammable(StringList canConstructionSettingList,
                                                                                  StringList productSustainCombustionSettingList,
                                                                                  StringList aersolISOPropanolSettingList) {
        List<String> checkList = new ArrayList<>();
        // Required when 'Can construction' = Plastic
        // and 'Is the propellant flammable or nonflammable' =nonflammable
        // and 'Does the base product contain  >= 50% water AND <= 15.8% by weight (20% by volume) ethanol plus isopropanol?' =No
        // and 'Does the base product (no propellant) Sustain Combustion' =Yes or Unknown
        if (FormulationGeneralConstant.NON_FLAMMABLE.getValue().equals(physChemBean.getFlammableOrNonFlammable()) && canConstructionSettingList.contains(physChemBean.getCanConstruction())
                && aersolISOPropanolSettingList.contains(physChemBean.getDoesTheBaseProductContainEthanolPlusISOPropanol())
                && productSustainCombustionSettingList.contains(physChemBean.getDoesBaseProductSustainCombustion())
                && UIUtil.isNullOrEmpty(physChem.getValue())) {
            checkList.add(physChem.getName().concat(physChemProperties.getProperty(FormulationGeneralConstant.EXCEL_INPUT_VALUE_REQUIRED_MESSAGE.getValue())));
        }
        return checkList;
    }

    /**
     * Method to check excel column value is having bad characters
     *
     * @param sValue                  - excel column value
     * @param strInvalidBadCharacters - list of bad characters
     * @return boolean
     * @since DSM 2018x.5
     */
    private boolean isBadChars(String sValue, String strInvalidBadCharacters) {
        boolean bflag = false;
        StringList slBadChars = StringUtil.split(strInvalidBadCharacters, FormulationGeneralConstant.CONST_SYMBOL_SPACE.getValue());
        for (String strBadChar : slBadChars) {
            if (sValue.contains(strBadChar)) {
                bflag = true;
                break;
            }
        }
        return bflag;
    }
}
