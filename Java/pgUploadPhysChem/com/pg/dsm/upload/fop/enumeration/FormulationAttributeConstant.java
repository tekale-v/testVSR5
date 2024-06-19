/*
 **   FormulationAttributeConstant.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the attribute schema constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

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

public enum FormulationAttributeConstant {
    COMMENT("attribute_Comment"),
    COLOR("attribute_pgColor"),
    COLOR_INTENSITY("attribute_pgColorIntensity"),
    ODOUR("attribute_Odour"),
    HEAT_OF_COMBUSTION("attribute_pgHeatOfCombustion"),
    CAN_CONSTRUCTION("attribute_pgCanConstruction"),
    GUAGE_PRESSURE("attribute_pgGaugePressure"),
    AEROSOL_TYPE("attribute_pgAerosolType"),
    IS_AEROSOL_TEST_DATA_NEEDED("attribute_pgIsAerosolTestDataNeeded"),
    IGNITION_DISTANCE("attribute_pgIgnitionDistance"),
    ENCLOSED_SPACE_IGNITION("attribute_pgEnclosedSpaceIgnition"),
    FLAME_HEIGHT("attribute_pgFlameHeight"),
    FLAME_DURATION("attribute_pgFlameDuration"),
    VAPOR_PRESSURE("attribute_VaporPressure"),
    VAPOR_DENSITY("attribute_VaporDensity"),
    RELATIVE_DENSITY_OR_SPECIFIC_GRAVITY("attribute_pgRelativeDensity/SpecificGravity"),
    RESERVE_ALKALINITY("attribute_pgReserveAlkalinity"),
    RESERVE_ACIDITY("attribute_pgReserveAcidity"),
    AEROSOL_CAN_CORROSIVE_TO_METALS("attribute_pgAerosolCanCorrosiveToMetals"),
    TECHNICAL_BASIS_FOR_THE_CORROSIVE_TO_METALS("attribute_pgTechnicalBasisForTheCorrosiveToMetals"),
    AEROSOL_CONDUCTIVITY_OF_THE_CONTENTS("attribute_pgAerosolConductivityOfTheContents"),
    FLAMMABLE_LIQUID_ABSORBED_OR_CONTAINED_WITHIN_THE_SOLID("attribute_pgFlammableLiquidAbsorbedOrContainedWithInTheSolid"),
    BURN_RATE("attribute_pgBurnRate"),
    PRODUCT_OXIDIZER("attribute_pgProductOxidizer"),
    OXIDIZER_SODIUM_PER_CARBONATE("attribute_pgOxidizerSodiumPerCarbonate"),
    OXIDIZER_HYDROGEN_PEROXIDE("attribute_pgOxidizerHydrogenPeroxide"),
    CLOSED_CUP_FLASH_POINT("attribute_pgClosedCupFlashpoint"),
    CLOSED_CUP_FLASH_POINT_VALUE("attribute_pgClosedCupFlashpointValue"),
    BOILING_POINT("attribute_pgBoilingPoint"),
    BOILING_POINT_VALUE("attribute_pgBoilingPointValue"),
    PRODUCT_SUSTAIN_COMBUSTION("attribute_pgProductSustainCombustion"),
    PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE("attribute_pgProductPotentialToIncreaseBurningRate"),
    ORGANIC_PEROXIDE("attribute_pgOrganicPeroxide"),
    AVAILABLE_OXYGEN_CONTENT("attribute_pgAvailableOxygenContent"),
    EVAPORATION_RATE("attribute_pgEvaporationRate"),
    LIQUID_CORROSIVE_TO_METAL("attribute_pgLiquidCorrosiveToMetal"),
    CONDUCTIVITY_OF_THE_LIQUID("attribute_pgConductivityoftheLiquid"),
    PRODUCT_HAVE_ANY_SELF_REACTIVE_PROPERTIES("attribute_pgProductHaveAnySelfReactiveProperties"),
    HEAT_OF_DECOMPOSITION("attribute_pgHeatofDecomposition"),
    SELF_ACCELERATING_DECOMPOSITION_TEMPERATURE("attribute_pgSelfAcceleratingDecompositionTemperature"),
    CONTENT_CONDUCTIVITY("attribute_pgContentConductivity"),
    CORROSIVE_TO_METALS("attribute_pgCorrosivetoMetals"),
    SUSTAIN_COMBUSTION("attribute_pgSustainCombustion"),
    OXIDIZER("attribute_pgOxidizer"),
    BY_VOLUME("attribute_pgbyVolume"),
    BY_WEIGHT("attribute_pgbyWeight"),
    FLAMMABLE_OR_NON_FLAMMABLE("attribute_pgFlammableOrNonFlammable"),
    PERCENTAGE_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER("attribute_pgPercentByWeightOfFlammablePropellantInAerosolContainer"),
    DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS("attribute_pgDoesBaseProductContainByVolumeWaterMiscibleAlcohols"),
    DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISO_PROPANOL("attribute_pgDoesTheBaseProductContainEthanolPlusISOPropanol"),
    DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION("attribute_pgDoesBaseProductSustainCombustion"),
    DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT("attribute_pgDoesBaseProductHaveAFirePoint"),
    DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_NON_FLAMMABLE_GAS_PROPELLANT("attribute_pgDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant"),
    DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_FLAMMABLE_GAS_PROPELLANT("attribute_pgDoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant"),
    PROPELLANT_EMULSIFIED_FOR_LIFE_OF_PRODUCT("attribute_pgPropellantemulsifiedforlifeofproduct"),
    PROPELLANT_IS_NON_FLAMMABLE("attribute_pgPropellantisnonflammable"),
    WT_PARAMETERIZED("attribute_pgWTParameterized"),
    KST_DUST_DEFLAGRATION_INDEX("attribute_pgKstDustDeflagrationIndex"),
    MAX_EXPLOSION_PRESSURE("attribute_pgpmaxmaxexplosionpressure"),
    PH("attribute_pgPH"),
    PH_MIN("attribute_pgpHMin"),
    PH_MAX("attribute_pgpHMax"),
    PH_DILUTION("attribute_pgPHDilution"),
    RESERVE_ALKALINITY_ACIDITY_UOM("attribute_pgReserveAlkalinityUoM"),
    RESERVE_ALKALINITY_TITRATION_ENDPOINT("attribute_pgReserveAlkalinityTitrationEndPoint"),
    PH_DATA_AVAILABLE("attribute_pgPHDataAvailable"),
    KINEMATIC_VISCOSITY("attribute_pgKinematicViscosity"),
    IS_LIQUID_AN_AQEOUS_SOLUTION("attribute_pgIsTheLiquidanAqeousSolution"),
    PRODUCT_TYPE("attribute_pgProductType"),
	//Modified as part of 2018x.6 - Starts
	ROLLUP_FLAG("attribute_pgPhysicalChemicalRollupFlag");
	//Modified as part of 2018x.6 - Ends

    private static final Map<String, FormulationAttributeConstant> attributeMap = Arrays.stream(values()).collect(Collectors.toMap(Enum::toString, Function.identity()));

    private final String name;

    /**
     * Constructor
     *
     * @param name - String
     * @since DSM 2018x.5
     */
    FormulationAttributeConstant(String name) {
        this.name = name;
    }

    public static FormulationAttributeConstant get(String name) {
        return attributeMap.get(name);
    }

    /**
     * Method to attribute schema name.
     *
     * @param context - Context
     * @return String - Attribute name
     * @since DSM 2018x.5
     */
    public String getAttribute(Context context) {
        return PropertyUtil.getSchemaProperty(context, this.name);
    }

    /**
     * Method to attribute schema select expression.
     *
     * @param context - Context
     * @return String - Attribute name
     * @since DSM 2018x.5
     */
    public String getAttributeSelect(Context context) {
    	return DomainObject.getAttributeSelect(this.getAttribute(context));
    }

    /**
     * Method to attribute default value.
     *
     * @param context - Context
     * @return String - Attribute default value.
     * @throws MatrixException - exception
     * @since DSM 2018x.5
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
     * @since DSM 2018x.5
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
     * @since DSM 2018x.5
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
     * @since DSM 2018x.5
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
     * @since DSM 2018x.5
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
     * @since DSM 2018x.5
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
     * @since DSM 2018x.5
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
