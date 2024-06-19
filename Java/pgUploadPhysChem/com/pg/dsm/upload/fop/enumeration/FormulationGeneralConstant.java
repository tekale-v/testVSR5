/*
 **   FormulationGeneralConstant.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the general constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

public enum FormulationGeneralConstant {

    CURRENT_DIR("user.dir"),
    HOME_DIR("pgUploadPhysChem"),
    UPLOAD_PHYS_CHEM_CONFIG_FOLDER("config"),
    UPLOAD_PHYS_CHEM_INPUT_FOLDER("input"),
    UPLOAD_PHYS_CHEM_OUTPUT_FOLDER("output"),
    UPLOAD_PHYS_CHEM_LOGS_FOLDER("logs"),

    UPLOAD_PHYS_CHEM_PROPERTY_FILE("pgUploadPhysChem.properties"),
    UPLOAD_PHYS_CHEM_LOGGER_CONFIGURATION_FILE("pgUploadPhysChem-log4j.properties"),
    UPLOAD_PHYS_CHEM_XML_CONFIGURATION_FILE("pgUploadPhysChem.xml"),
    UPLOAD_PHYS_CHEM_EXCEL_INPUT_FILE("pgUploadPhysChem.xlsm"),

    PICKLIST_TYPE_BUSINESS_AREA("Picklist.Type.BusinessArea"),
    PICKLIST_TYPE_PRODUCT_CATEGORY_PLATFORM("Picklist.Type.ProductCategoryPlatform"),
    PICKLIST_TYPE_PRODUCT_TECHNOLOGY_PLATFORM("Picklist.Type.ProductTechnologyPlatform"),

    INPUT_EXCEL_FILE_DOES_NOT_EXIST("Input excel file with name pgUploadPhysChem.xlsm does not exist in input folder"),
    INPUT_EXCEL_FILE_LOG("Input excel file with name "),
    DOES_NOT_EXIST_IN_INPUT_FOLDER(" does not exist in input folder"),
    XML_CONFIG_FILE_DOES_NOT_EXIST("XML config file with name pgUploadPhysChem.xml does not exist in config folder"),
    PROPERTY_CONFIG_FILE_DOES_NOT_EXIST("Property config file with name pgUploadPhysChem.properties does not exist in config folder"),

    AEROSOL_BUSINESS_AREA("Aerosol.VaporPressure.Required.When.BusinessArea.Name"),
    SOLID_BUSINESS_AREA("Solid.VaporPressure.Required.When.BusinessArea.Name"),
    LIQUID_BUSINESS_AREA("Liquid.VaporPressure.Required.When.BusinessArea.Name"),

    AEROSOL_PRODUCT_CATEGORY_PLATFORM("Aerosol.VaporPressure.Required.When.ProductCategoryPlatform.Name"),
    SOLID_PRODUCT_CATEGORY_PLATFORM("Solid.VaporPressure.Required.When.ProductCategoryPlatform.Name"),
    LIQUID_PRODUCT_CATEGORY_PLATFORM("Liquid.VaporPressure.Required.When.ProductCategoryPlatform.Name"),

    AEROSOL_PRODUCT_TECHNOLOGY_PLATFORM("Aerosol.VaporPressure.Required.When.ProductTechnologyPlatform.Name"),
    SOLID_PRODUCT_TECHNOLOGY_PLATFORM("Solid.VaporPressure.Required.When.ProductTechnologyPlatform.Name"),
    LIQUID_PRODUCT_TECHNOLOGY_PLATFORM("Liquid.VaporPressure.Required.When.ProductTechnologyPlatform.Name"),

    MATRIX_URL("LOGIN_MATRIX_HOST"),
    MATRIX_USR("CONTEXT_USER"),
    MATRIX_PDW("CONTEXT_PASSWORD"),
    CONST_ATTR_NAME("attr_name"),
    CONST_ATTR_SELECT("attr_select"),
    CONST_ATTR_CHOICES("attr_choices"),
    CONST_ATTR_DEFAULT_VAL("attr_default_val"),
    CONST_ATTR_MAX_LENGTH("attr_max_length"),
    CONST_IS_ATTR_MULTI_LINE("attr_multi_line"),
    CONST_IS_ATTR_SINGLE_VAL("attr_single_val"),
    CONST_IS_ATTR_MULTI_VAL("attr_multi_val"),
    CONST_IS_ATTR_RANGE_VAL("attr_range_val"),
    CONST_BASIC("basic"),
    CONST_ATTR("attribute"),
    CONST_PICKLIST_NAMES("picklistNames"),
    CONST_PICKLIST_REVISIONS("picklistRevisions"),
    CONST_PICKLIST_IDS("picklistIds"),
    CONST_CONNECTED_PRODUCT_FORMS("connectedProductForms"),
    CONST_CONNECTED_BUSINESS_AREA("connectedBusinessArea"),
    CONST_CONNECTED_PRODUCT_CATEGORY_PLATFORM("connectedProductCategoryPlatform"),
    CONST_CONNECTED_PRODUCT_TECHNOLOGY_PLATFORM("connectedProductTechnologyPlatform"),
    CONST_MILLI_SECONDS("ms"),
    CONST_MINUTES("min"),
    CONST_SECONDS("sec"),
    CONST_EXCEL_SHEET_EMPTY("Excel sheet is empty."),
    CONST_EXCEL_SHEET_NO_RECORDS("Excel sheet has no records."),
    CONST_EXCEL_COLUMNS_SIZE_INCORRECT("Number of columns is incorrect."),
    CONST_COLUMN_COLON("Column: "),
    CONST_EXCEL_COLUMN_POSITION_MISMATCH_MESSAGE(" is misplaced and it should be at position "),
    CONST_SYMBOL_UNDERSCORE("_"),
    CONST_SYMBOL_PIPE("|"),
    CONST_SYMBOL_HYPHEN("-"),

    SOLID_INPUT_BUSINESS_AREA_DOES_NOT_EXIST("Input Business Area Name for Solid in pgUploadPhysChem.properties file does not exist in database"),
    LIQUID_INPUT_BUSINESS_AREA_DOES_NOT_EXIST("Input Business Area Name for Liquid in pgUploadPhysChem.properties file does not exist in database"),
    AEROSOL_INPUT_BUSINESS_AREA_DOES_NOT_EXIST("Input Business Area Name for Aerosol in pgUploadPhysChem.properties file does not exist in database"),
    SOLID_INPUT_PRODUCT_CATEGORY_PLATFORM_DOES_NOT_EXIST("Input Product Category Platform Name for Solid in pgUploadPhysChem.properties file does not exist in database"),
    LIQUID_INPUT_PRODUCT_CATEGORY_PLATFORM_DOES_NOT_EXIST("Input Product Category Platform Name for Liquid in pgUploadPhysChem.properties file does not exist in database"),
    AEROSOL_INPUT_PRODUCT_CATEGORY_PLATFORM_DOES_NOT_EXIST("Input Product Category Platform Name for Aerosol in pgUploadPhysChem.properties file does not exist in database"),
    SOLID_INPUT_PRODUCT_TECHNOLOGY_PLATFORM_DOES_NOT_EXIST("Input Product Technology Platform Name for Solid in pgUploadPhysChem.properties file does not exist in database"),
    LIQUID_INPUT_PRODUCT_TECHNOLOGY_PLATFORM_DOES_NOT_EXIST("Input Product Technology Platform Name for Liquid in pgUploadPhysChem.properties file does not exist in database"),
    AEROSOL_INPUT_PRODUCT_TECHNOLOGY_PLATFORM_DOES_NOT_EXIST("Input Product Technology Platform Name for Aerosol in pgUploadPhysChem.properties file does not exist in database"),

    CONST_MOVED_INPUT_EXCEL_FILE_NAME("pgUploadPhysChem_Input.xlsm"),
    CONST_RESTORE_EXCEL_FILE_NAME("pgUploadPhysChem_Restore.xlsx"),
    CONST_RESTORE_EXCEL_SHEET_NAME("pgUploadPhysChemRestore"),
    CONST_ERROR_EXCEL_FILE_NAME("pgUploadPhysChem_Error.xlsx"),
    CONST_ERROR_EXCEL_SHEET_NAME("pgUploadPhysChemError"),
    CONT_DATE_FORMAT_FOR_PREFIX("dd-MM-yyyy_HHmmss"),
    CONST_ERROR_EXCEL_COLUMN_FOP_NAME("Formulated Part Name"),
    CONST_ERROR_EXCEL_COLUMN_FOP_REVISION("Revision"),
    CONST_ERROR_EXCEL_COLUMN_VALIDATION_MESSAGES("Validation Message(s)"),

    CONST_INPUT_FORMULATED_PRODUCT_NAME_NOT_FOUND("object not found"),
    ODOUR_REQUIRED_INPUT_MISMATCH("Odour column - input value not in allowed range"),
    PH_NUMERIC_DECIMAL_PLACE_CHECK("pH [Target] - maximum 1 decimal is allowed"),
    PH_MAX_NUMERIC_DECIMAL_PLACE_CHECK("pH [maximum] - maximum 1 decimal is allowed"),
    PH_MIN_MUST_BE_LESSER_THAN_TARGET_CHECK("pH [minimum] - must be less than ph Target"),
    PH_MAX_MUST_BE_GREATER_THAN_TARGET_CHECK("pH [maximum] - must be greater than ph Target"),
    PH_DILUTION_RANGE_VALUE_NEAT("Neat"),

    RESERVE_ALKALINITY_REQUIRED_PH_UNABLE_TO_CHECK("Reserve Alkalinity % - both pH[target] and pH [Maximum] value is empty, value must be provided for either pH[target] or pH [Maximum]"),
    RESERVE_ACIDITY_REQUIRED_PH_UNABLE_TO_CHECK("Reserve Acidity % - both pH[target] and pH [Minimum] value is empty, value must be provided for either pH[target] or pH [Minimum]"),
    RESERVE_ALKALINITY_TITRATION_END_POINT_REQUIRED_PH_UNABLE_TO_CHECK("Reserve Alkalinity/Acidity pH titration endpoint - value must be provided for either pH[target] or pH [Minimum] or pH [Maximum]"),

    RESERVE_ALKALINITY_UOM_RANGE_CHECK("Reserve Alkalinity/Acidity Unit of Measure - range value mismatch"),
    RESERVE_ALKALINITY_UOM_REQUIRED_PH_UNABLE_TO_CHECK("Reserve Alkalinity/Acidity Unit of Measure % - value must be provided for either pH[target] or pH [Minimum] or pH [Maximum]"),
    FLAMMABLE_LIQUID_ABSORBED_OR_CONTAINED_WITH_IN_THE_SOLID_REQUIRED_CHECK("Is a Flammable Liquid absorbed or Contained within the solid - value is required"),

    DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_NUMERIC_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER("Does the base product contain <= 50% water by volume AND contain <=50% by volume water-miscible alcohols - dependent field Percent by weight of flammable propellant in aerosol container value must be numeric"),
    DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_NUMERIC_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER("Does the base product (no propellant) Sustain Combustion - dependent field Percent by weight of flammable propellant in aerosol container value must be numeric"),


    DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER_NUMERIC_CHECK("Does the base product have a Fire Point - dependent field Percent by weight of flammable propellant in aerosol container value must be numeric"),
    CLOSED_CUP_FLASHPOINT_RANGE_VALUE_OTHER("Other"),
    BOILING_POINT_RANGE_VALUE_OTHER("Other"),

    CONST_SYMBOL_COMMA(","),
    FOP_PRODUCT_FORM_VALUE_EMPTY("FOP Product Form - value is empty"),

    SOLID_ODOUR_ACCEPTED_VALUE("Solid.Odour.Accepted.Value"),
    LIQUID_ODOUR_ACCEPTED_VALUE("Liquid.Odour.Accepted.Value"),
    AEROSOL_ODOUR_ACCEPTED_VALUE("Aerosol.Odour.Accepted.Value"),

    SOLID_HEAT_OF_COMBUSTION_REQUIRED_WHEN_PRODUCT_HAVE_ANY_SELF_REACTIVE_PROPERTIES("Solid.HeatOfCombustion.Required.When.ProductHaveAnySelfReactiveProperties"),
    IGNITION_DISTANCE_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED("Aerosol.IgnitionDistance.Required.When.IsAerosolTestDataNeeded"),
    IGNITION_DISTANCE_REQUIRED_WHEN_AEROSOL_TYPE("Aerosol.IgnitionDistance.Required.When.AerosolType"),

    ENCLOSED_SPACE_IGNITION_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED("Aerosol.EnclosedSpaceIgnition.Required.When.IsAerosolTestDataNeeded"),
    ENCLOSED_SPACE_IGNITION_REQUIRED_WHEN_AEROSOL_TYPE("Aerosol.EnclosedSpaceIgnition.Required.When.AerosolType"),

    FLAME_HEIGHT_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED("Aerosol.FlameHeight.Required.When.IsAerosolTestDataNeeded"),
    FLAME_HEIGHT_REQUIRED_WHEN_AEROSOL_TYPE("Aerosol.FlameHeight.Required.When.AerosolType"),
    FLAME_DURATION_REQUIRED_WHEN_IS_AEROSOL_TEST_DATA_NEEDED("Aerosol.FlameDuration.Required.When.IsAerosolTestDataNeeded"),
    FLAME_DURATION_REQUIRED_WHEN_AEROSOL_TYPE("Aerosol.FlameDuration.Required.When.AerosolType"),

    SOLID_PH_TARGET_REQUIRED_WHEN_PH_DATA_AVAILABLE("Solid.PHTarget.Required.When.PHDataAvailable"),
    SOLID_PH_MIN_REQUIRED_WHEN_PH_DATA_AVAILABLE("Solid.PHMin.Required.When.PHDataAvailable"),
    SOLID_PH_MAX_REQUIRED_WHEN_PH_DATA_AVAILABLE("Solid.PHMax.Required.When.PHDataAvailable"),
    SOLID_PH_DILUTION_REQUIRED_WHEN_PH_DATA_AVAILABLE("Solid.PHDilution.Required.When.PHDataAvailable"),

    LIQUID_PH_TARGET_REQUIRED_WHEN_IS_THE_LIQUIDAN_AQEOUS_SOLUTION("Liquid.PHTarget.Required.When.IsTheLiquidanAqeousSolution"),
    LIQUID_PH_MIN_REQUIRED_WHEN_IS_THE_LIQUIDAN_AQEOUS_SOLUTION("Liquid.PHMin.Required.When.IsTheLiquidanAqeousSolution"),
    LIQUID_PH_MAX_REQUIRED_WHEN_IS_THE_LIQUIDAN_AQEOUS_SOLUTION("Liquid.PHMax.Required.When.IsTheLiquidanAqeousSolution"),
    LIQUID_PH_DILUTION_REQUIRED_WHEN_IS_THE_LIQUIDAN_AQEOUS_SOLUTION("Liquid.PHDilution.Required.When.IsTheLiquidanAqeousSolution"),

    AEROSOL_TECHNICAL_BASIS_FOR_THE_CORROSIVE_TO_METALS_REQUIRED_WHEN_AEROSOL_CAN_CORROSIVE_TO_METALS("Aerosol.TechnicalBasisForTheCorrosiveToMetals.Required.When.AerosolCanCorrosiveToMetals"),
    LIQUID_TECHNICAL_BASIS_FOR_THE_CORROSIVE_TO_METALS_REQUIRED_WHEN_LIQUID_CORROSIVE_TO_METAL("Liquid.TechnicalBasisForTheCorrosiveToMetals.Required.When.LiquidCorrosiveToMetal"),

    AEROSOL_CONDUCTIVITY_OF_THE_CONTENTS_REQUIRED_WHEN_AEROSOL_CAN_CORROSIVE_TO_METALS("Aerosol.AerosolConductivityOfTheContents.Required.When.AerosolCanCorrosiveToMetals"),
    LIQUID_CONDUCTIVITY_OF_THE_CONTENTS_REQUIRED_WHEN_LIQUID_CORROSIVE_TO_METAL("Liquid.ConductivityoftheLiquid.Required.When.LiquidCorrosiveToMetal"),

    SOLID_BURN_RATE_REQUIRED_WHEN_FLAMMABLE_LIQUID_ABSORBED_OR_CONTAINED_WITH_IN_THE_SOLID("Solid.BurnRate.Required.When.FlammableLiquidAbsorbedOrContainedWithInTheSolid"),

    SOLID_OXIDIZER_SODIUM_PER_CARBONATE_REQUIRED_WHEN_PRODUCT_OXIDIZER("Solid.OxidizerSodiumPerCarbonate.Required.When.ProductOxidizer"),
    LIQUID_OXIDIZER_SODIUM_PER_CARBONATE_REQUIRED_WHEN_PRODUCT_OXIDIZER("Liquid.OxidizerSodiumPerCarbonate.Required.When.ProductOxidizer"),

    SOLID_OXIDIZER_HYDROGEN_PEROXIDE_REQUIRED_WHEN_PRODUCT_OXIDIZER("Solid.OxidizerHydrogenPeroxide.Required.When.ProductOxidizer"),
    SOLID_OXIDIZER_HYDROGEN_PEROXIDE_REQUIRED_WHEN_OXIDIZER_SODIUM_PER_CARBONATE("Solid.OxidizerHydrogenPeroxide.Required.When.OxidizerSodiumPerCarbonate"),

    LIQUID_OXIDIZER_HYDROGEN_PEROXIDE_REQUIRED_WHEN_PRODUCT_OXIDIZER("Liquid.OxidizerHydrogenPeroxide.Required.When.ProductOxidizer"),

    SOLID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_PRODUCT_OXIDIZER("Solid.ProductPotentialToIncreaseBurningRate.Required.When.ProductOxidizer"),
    SOLID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_SODIUM_PER_CARBONATE("Solid.ProductPotentialToIncreaseBurningRate.Required.When.OxidizerSodiumPerCarbonate"),
    SOLID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_HYDROGEN_PEROXIDE("Solid.ProductPotentialToIncreaseBurningRate.Required.When.OxidizerHydrogenPeroxide"),

    LIQUID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_PRODUCT_OXIDIZER("Liquid.ProductPotentialToIncreaseBurningRate.Required.When.ProductOxidizer"),
    LIQUID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_SODIUM_PER_CARBONATE("Liquid.ProductPotentialToIncreaseBurningRate.Required.When.OxidizerSodiumPerCarbonate"),
    LIQUID_PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE_REQUIRED_WHEN_OXIDIZER_HYDROGEN_PEROXIDE("Liquid.ProductPotentialToIncreaseBurningRate.Required.When.OxidizerHydrogenPeroxide"),

    SOLID_AVAILABLE_OXYGEN_CONTENT_REQUIRED_WHEN_ORGANIC_PEROXIDE("Solid.AvailableOxygenContent.Required.When.OrganicPeroxide"),
    LIQUID_AVAILABLE_OXYGEN_CONTENT_REQUIRED_WHEN_ORGANIC_PEROXIDE("Liquid.AvailableOxygenContent.Required.When.OrganicPeroxide"),

    SOLID_SELF_ACCELERATING_DECOMPOSITION_TEMPERATURE_REQUIRED_WHEN_PRODUCT_HAVE_ANY_SELF_REACTIVE_PROPERTIES("Solid.SelfAcceleratingDecompositionTemperature.Required.When.ProductHaveAnySelfReactiveProperties"),
    SOLID_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER_REQUIRED_WHEN_CAN_CONSTRUCTION("Aerosol.PercentByWeightOfFlammablePropellantInAerosolContainer.Required.When.CanConstruction"),
    SOLID_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE("Aerosol.PercentByWeightOfFlammablePropellantInAerosolContainer.Required.When.FlammableOrNonFlammable"),

    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_WHEN_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER("Aerosol.DoesBaseProductContainByVolumeWaterMiscibleAlcohols.Required.When.PercentByWeightOfFlammablePropellantInAerosolContainer"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE("Aerosol.DoesBaseProductContainByVolumeWaterMiscibleAlcohols.Required.When.FlammableOrNonFlammable"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS_REQUIRED_WHEN_CAN_CONSTRUCTION("Aerosol.DoesBaseProductContainByVolumeWaterMiscibleAlcohols.Required.When.CanConstruction"),

    AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_CAN_CONSTRUCTION("Aerosol.DoesBaseProductSustainCombustion.Required.When.CanConstruction"),
    AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER("Aerosol.DoesBaseProductSustainCombustion.Required.When.PercentByWeightOfFlammablePropellantInAerosolContainer"),
    AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS("Aerosol.DoesBaseProductSustainCombustion.Required.When.DoesBaseProductContainByVolumeWaterMiscibleAlcohols"),
    AEROSOL_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION_REQUIRED_WHEN_DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL("Aerosol.DoesBaseProductSustainCombustion.Required.When.DoesTheBaseProductContainEthanolPlusISOPropanol"),


    AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_CAN_CONSTRUCTION("Aerosol.DoesBaseProductHaveAFirePoint.Required.When.CanConstruction"),
    AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL("Aerosol.DoesBaseProductHaveAFirePoint.Required.When.DoesTheBaseProductContainEthanolPlusISOPropanol"),
    AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_PERCENT_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER("Aerosol.DoesBaseProductHaveAFirePoint.Required.When.PercentByWeightOfFlammablePropellantInAerosolContainer"),
    AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS("Aerosol.DoesBaseProductHaveAFirePoint.Required.When.DoesBaseProductContainByVolumeWaterMiscibleAlcohols"),
    AEROSOL_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT_REQUIRED_WHEN_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION("Aerosol.DoesBaseProductHaveAFirePoint.Required.When.DoesBaseProductSustainCombustion"),

    AEROSOL_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL_REQUIRED_CAN_CONSTRUCTION("Aerosol.DoesTheBaseProductContainEthanolPlusISOPropanol.Required.CanConstruction"),

    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_CAN_CONSTRUCTION("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant.Required.When.CanConstruction"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant.Required.When.FlammableOrNonFlammable"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISOPROPANOL("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant.Required.When.DoesTheBaseProductContainEthanolPlusISOPropanol"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant.Required.When.DoesBaseProductSustainCombustion"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_NON_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant.Required.When.DoesBaseProductHaveAFirePoint"),


    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_CAN_CONSTRUCTION("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant.Required.When.CanConstruction"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_FLAMMABLE_OR_NON_FLAMMABLE("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant.Required.When.FlammableOrNonFlammable"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant.Required.When.DoesBaseProductContainByVolumeWaterMiscibleAlcohols"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant.Required.When.DoesBaseProductSustainCombustion"),
    AEROSOL_DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_LIQUEFIED_FLAMMABLE_GAS_PROPELLANT_REQUIRED_WHEN_DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT("Aerosol.DoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant.Required.When.DoesBaseProductHaveAFirePoint"),


    AEROSOL_FLAMMABLE_OR_NON_FLAMMABLE_REQUIRED_WHEN_CAN_CONSTRUCTION("Aersol.FlammableOrNonFlammable.Required.When.CanConstruction"),

    AEROSOL_ATTRIBUTES_TO_VALIDATE("Aerosol.Attributes.To.Validate"),
    LIQUID_ATTRIBUTES_TO_VALIDATE("Liquid.Attributes.To.Validate"),
    SOLID_ATTRIBUTES_TO_VALIDATE("Solid.Attributes.To.Validate"),

    ATTRIBUTE_COMMENT_UPDATE_DELIMITER("Attribute.Comment.Update.Delimiter"),

    EXCEL_INPUT_VALUE_REQUIRED_MESSAGE("Excel.Input.Value.Required.Message"),
    EXCEL_INPUT_VALUE_REQUIRED_NUMERIC_MESSAGE("Excel.Input.Value.Required.Numeric.Message"),
    EXCEL_INPUT_VALUE_RANGE_MISMATCH_MESSAGE("Excel.Input.Value.Range.Mismatch.Message"),

    FORMULATED_PART_NAME_COLUMN_LENGTH_CHECK("Excel.Column.FormulatedPartName.Length.Check.Message"),

    EXCEL_VALUE_MISMATCH_WITH_DATABASE("Excel.Value.Mismatch.With.Database"),

    EXCEL_PICKLIST_VALUE_MISMATCH_WITH_DATABASE("Excel.Picklist.Value.Mismatch.With.Database"),
    FLAMMABLE("Flammable"),
    NON_FLAMMABLE("Nonflammable"),

    EXCEL_CELL_COLOR_GRAY_HEX_CODE("Excel.Cell.Gray.Hex.Code"),
    EXCEL_TAB_PHYSICAL_CHEMICAL_POSITION("Excel.Tab.PhysicalChemical.Position"),
    EXCEL_BAD_CHARACTER_ODOUR("Excel.Bad.Character.Odour"),
    EXCEL_BAD_CHARACTER_TECHNICAL_BASIC_FOR_THE_CORROSIVE_TO_METAL("Excel.Bad.Character.TechnicalBasisForTheCorrosiveToMetals"),
    EXCEL_INPUT_VALUE_HAS_BAD_CHARACTER("Excel.Input.Value.Has.Bad.Character"),
    CONST_SYMBOL_SPACE(" "),
    CONST_VALUE_TRUE("TRUE"),
    LIQUID_DOES_PRODUCT_SUSTAIIN_COMBUSTION_REQUIRED_WHEN_CLOSEDCUP_FLASHPOINT("Liquid.DoesTheProductSustainCombustion.Required.When.ClosedCupFlashPoint"),
    APP_HAS_ROLLEDUP_DATA_MESSAGE("APP.Has.Rolledup.Data.Message"),
    CONNECTED_FOP_ELIGIBLE_FOR_ROLLUP_MESSAGE("Connected.FOP.Is.Eligible.For.Rollup.Message"),
    PRODUCT_FORM_FIELD_NAME("Product Form"),
    CONST_SYMBOL_HYPHEN_ERROR_XLSX("_Error.xlsx"),
    CONST_SYMBOL_HYPHEN_INPUT_XLSM("_Input.xlsm"),
    CONST_SYMBOL_HYPHEN_RESTORE_XLSM("_Restore.xlsx"),
    DEBUG_LOG_FILE_EXTENSION(".log"),
    ERROR_LOG_FILE_EXTENSION("_Error.log"),
    DEBUG_LOG_FILE_CONFIG("logfileDebug.name"),
    ERROR_LOG_FILE_CONFIG("logfileError.name"),
	LIQUID_BUSINESS_AREA_REVERSE_ALKALINITY("Liquid.ReserveAlkalinityPercentage.Required.When.BusinessArea.Name"),
	EXCEL_VALUE_MISMATCH_WITH_TEMPLATE_AND_DATABASE_MESSAGE("Excel.Value.Mismatch.With.Template.And.Database.Message"),
	EXCEL_TEMPLATE_VALUE_MESSAGE("Excel.Template.Value.Message"),
	EXCEL_AND_DATABASE_VALUE_MESSAGE("Excel.And.Database.Value.Message"),
	CONST_SYMBOL_COLON(":"),
	CONST_FILE_EXTENSION_XLSM("xlsm"),
	PICK_LIST_WHERE_FORMULATED_AND_CURRENT(" ~~ '*Formulated*' && current ==");

    private final String value;

    /**
     * Constructor
     *
     * @param value - String
     * @since DSM 2018x.5
     */
    FormulationGeneralConstant(String value) {
        this.value = value;
    }

    /**
     * Method to get value
     *
     * @since DSM 2018x.5
     */
    public String getValue() {
        return value;
    }
}
