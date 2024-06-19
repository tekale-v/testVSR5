package com.pg.dsm.preference.usage;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.config.xml.PackagingPreferenceConfig;
import com.pg.dsm.preference.config.xml.PreferenceConfig;
import com.pg.dsm.preference.config.xml.ProductPreferenceConfig;
import com.pg.dsm.preference.config.xml.RawMaterialPreferenceConfig;
import com.pg.dsm.preference.config.xml.TechnicalSpecPreferenceConfig;
import com.pg.dsm.preference.models.ChangeActionPreference;
import com.pg.dsm.preference.models.DefaultCreatePartTypePreference;
import com.pg.dsm.preference.models.IPSecurityPreference;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CreateProductScreen {
    boolean loaded;
    String productPreferenceType;
    String packagingPreferenceType;
    String rawMaterialPreferenceType;
    String defaultTypeOnCreateProduct;
    String defaultDisplayTypeOnCreateProduct;
    String defaultTypeOnCreateSpec;
    String defaultDisplayTypeOnCreateSpec;
    String defaultTypeOnCreateEquivalent;
    String defaultDisplayTypeOnCreateEquivalent;
    String phase;
    String segmentName;
    String segmentID;
    String changeTemplateName;
    String changeTemplateOID;
    String primaryOrgName;
    String primaryOrgID;
    StringList technicalSpecTypeList;
    StringList productTypeList;

    public CreateProductScreen(Builder builder) {
        this.loaded = builder.loaded;
        this.defaultTypeOnCreateProduct = builder.defaultTypeOnCreateProduct;
        this.defaultDisplayTypeOnCreateProduct = builder.defaultDisplayTypeOnCreateProduct;
        this.defaultTypeOnCreateSpec = builder.defaultTypeOnCreateSpec;
        this.defaultDisplayTypeOnCreateSpec = builder.defaultDisplayTypeOnCreateSpec;
        this.defaultTypeOnCreateEquivalent = builder.defaultTypeOnCreateEquivalent;
        this.defaultDisplayTypeOnCreateEquivalent = builder.defaultDisplayTypeOnCreateEquivalent;
        this.phase = builder.phase;
        this.segmentName = builder.segmentName;
        this.segmentID = builder.segmentID;
        this.changeTemplateName = builder.changeTemplateName;
        this.changeTemplateOID = builder.changeTemplateOID;
        this.primaryOrgName = builder.primaryOrgName;
        this.primaryOrgID = builder.primaryOrgID;
        this.productPreferenceType = builder.productPreferenceType;
        this.packagingPreferenceType = builder.packagingPreferenceType;
        this.rawMaterialPreferenceType = builder.rawMaterialPreferenceType;
        this.technicalSpecTypeList = builder.technicalSpecTypeList;
        this.productTypeList = builder.productTypeList;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public String getPhase() {
        return phase;
    }

    public String getSegmentName() {
        return segmentName;
    }

    public String getSegmentID() {
        return segmentID;
    }

    public String getDefaultTypeOnCreateProduct() {
        return defaultTypeOnCreateProduct;
    }

    public String getDefaultTypeOnCreateSpec() {
        return defaultTypeOnCreateSpec;
    }

    public String getDefaultTypeOnCreateEquivalent() {
        return defaultTypeOnCreateEquivalent;
    }

    public String getChangeTemplateName() {
        return changeTemplateName;
    }

    public String getChangeTemplateOID() {
        return changeTemplateOID;
    }

    public String getPrimaryOrgName() {
        return primaryOrgName;
    }

    public String getPrimaryOrgID() {
        return primaryOrgID;
    }

    public String getDefaultDisplayTypeOnCreateProduct() {
        return defaultDisplayTypeOnCreateProduct;
    }

    public String getDefaultDisplayTypeOnCreateSpec() {
        return defaultDisplayTypeOnCreateSpec;
    }

    public String getDefaultDisplayTypeOnCreateEquivalent() {
        return defaultDisplayTypeOnCreateEquivalent;
    }

    public String getProductPreferenceType() {
        return productPreferenceType;
    }

    public String getPackagingPreferenceType() {
        return packagingPreferenceType;
    }

    public String getRawMaterialPreferenceType() {
        return rawMaterialPreferenceType;
    }

    public StringList getTechnicalSpecTypeList() {
        return technicalSpecTypeList;
    }

    public StringList getProductTypeList() {
        return productTypeList;
    }

    @Override
    public String toString() {
        return "CreateProductScreen{" +
                "loaded=" + loaded +
                ", productPreferenceType='" + productPreferenceType + '\'' +
                ", packagingPreferenceType='" + packagingPreferenceType + '\'' +
                ", rawMaterialPreferenceType='" + rawMaterialPreferenceType + '\'' +
                ", defaultTypeOnCreateProduct='" + defaultTypeOnCreateProduct + '\'' +
                ", defaultDisplayTypeOnCreateProduct='" + defaultDisplayTypeOnCreateProduct + '\'' +
                ", defaultTypeOnCreateSpec='" + defaultTypeOnCreateSpec + '\'' +
                ", defaultDisplayTypeOnCreateSpec='" + defaultDisplayTypeOnCreateSpec + '\'' +
                ", defaultTypeOnCreateEquivalent='" + defaultTypeOnCreateEquivalent + '\'' +
                ", defaultDisplayTypeOnCreateEquivalent='" + defaultDisplayTypeOnCreateEquivalent + '\'' +
                ", phase='" + phase + '\'' +
                ", segmentName='" + segmentName + '\'' +
                ", segmentID='" + segmentID + '\'' +
                ", changeTemplateName='" + changeTemplateName + '\'' +
                ", changeTemplateOID='" + changeTemplateOID + '\'' +
                ", primaryOrgName='" + primaryOrgName + '\'' +
                ", primaryOrgID='" + primaryOrgID + '\'' +
                '}';
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        PreferenceConfig preferenceConfig;
        String productPreferenceType;
        String packagingPreferenceType;
        String rawMaterialPreferenceType;
        String defaultTypeOnCreateProduct;
        String defaultDisplayTypeOnCreateProduct;
        String defaultTypeOnCreateSpec;
        String defaultDisplayTypeOnCreateSpec;
        String defaultTypeOnCreateEquivalent;
        String defaultDisplayTypeOnCreateEquivalent;
        String phase;
        String segmentName;
        String segmentID;
        String changeTemplateName;
        String changeTemplateOID;
        String primaryOrgName;
        String primaryOrgID;

        StringList technicalSpecTypeList;
        StringList productTypeList;

        boolean loaded;

        public Builder(Context context, PreferenceConfig preferenceConfig) {
            this.context = context;
            this.preferenceConfig = preferenceConfig;
            this.defaultTypeOnCreateProduct = DomainConstants.EMPTY_STRING;
            this.defaultTypeOnCreateSpec = DomainConstants.EMPTY_STRING;
            this.defaultTypeOnCreateEquivalent = DomainConstants.EMPTY_STRING;

            this.phase = DomainConstants.EMPTY_STRING;

            this.segmentName = DomainConstants.EMPTY_STRING;
            this.segmentID = DomainConstants.EMPTY_STRING;

            this.changeTemplateName = DomainConstants.EMPTY_STRING;
            this.changeTemplateOID = DomainConstants.EMPTY_STRING;

            this.primaryOrgName = DomainConstants.EMPTY_STRING;
            this.primaryOrgID = DomainConstants.EMPTY_STRING;

            this.technicalSpecTypeList = new StringList();
            this.productTypeList = new StringList();
        }

        public CreateProductScreen build() throws MatrixException {
            try {
                this.getPreferences();
                this.loaded = Boolean.TRUE;
            } catch (MatrixException e) {
                logger.log(Level.WARNING, "Error Applying Packaging Preference:" + e);
                this.loaded = Boolean.FALSE;
                throw e;
            }
            return new CreateProductScreen(this);
        }

        private void getPreferences() throws MatrixException {

            DefaultCreatePartTypePreference defaultTypePreference = new DefaultCreatePartTypePreference(context);
            this.defaultTypeOnCreateProduct = defaultTypePreference.getPreferredDefaultTypeOnCreateProduct();
            this.defaultTypeOnCreateSpec = defaultTypePreference.getPreferredDefaultTypeOnCreateSpec();
            this.defaultTypeOnCreateEquivalent = defaultTypePreference.getPreferredDefaultTypeOnCreateEquivalent();

            this.defaultDisplayTypeOnCreateProduct = defaultTypePreference.getPreferredDefaultTypeOnCreateProductDisplay();
            this.defaultDisplayTypeOnCreateSpec = defaultTypePreference.getPreferredDefaultTypeOnCreateSpecDisplay();
            this.defaultDisplayTypeOnCreateEquivalent = defaultTypePreference.getPreferredDefaultTypeOnCreateEquivalentDisplay();

            this.productPreferenceType = Preferences.ProductPreference.PART_TYPE.getName(this.context);
            this.packagingPreferenceType = Preferences.PackagingPreference.PART_TYPE.getName(this.context);
            this.rawMaterialPreferenceType = Preferences.RawMaterialPreference.PART_TYPE.getName(this.context);

            ProductPreferenceConfig productPreferenceConfig = preferenceConfig.getProductPreferenceConfig();
            PackagingPreferenceConfig packagingPreferenceConfig = preferenceConfig.getPackagingPreferenceConfig();
            RawMaterialPreferenceConfig rawMaterialPreferenceConfig = preferenceConfig.getRawMaterialPreferenceConfig();
            TechnicalSpecPreferenceConfig technicalSpecPreferenceConfig = preferenceConfig.getTechnicalSpecPreferenceConfig();
            String technicalSpecificationTypes = technicalSpecPreferenceConfig.getTypes();
            StringList technicalSpecificationTypesList = StringUtil.split(technicalSpecificationTypes, pgV3Constants.SYMBOL_COMMA);
            for (String technicalSpecificationType : technicalSpecificationTypesList) {
                technicalSpecTypeList.add(PropertyUtil.getSchemaProperty(this.context, technicalSpecificationType));
            }
            String productTypes = productPreferenceConfig.getTypes();
            StringList productTypesList = StringUtil.split(productTypes, pgV3Constants.SYMBOL_COMMA);
            for (String productType : productTypesList) {
                productTypeList.add(PropertyUtil.getSchemaProperty(this.context, productType));
            }
            String packagingTypes = packagingPreferenceConfig.getTypes();
            StringList packagingTypesList = StringUtil.split(packagingTypes, pgV3Constants.SYMBOL_COMMA);
            for (String packagingType : packagingTypesList) {
                productTypeList.add(PropertyUtil.getSchemaProperty(this.context, packagingType));
            }
            String rawMaterialTypes = rawMaterialPreferenceConfig.getTypes();
            StringList rawMaterialTypesList = StringUtil.split(rawMaterialTypes, pgV3Constants.SYMBOL_COMMA);
            for (String rawMaterialType : rawMaterialTypesList) {
                productTypeList.add(PropertyUtil.getSchemaProperty(this.context, rawMaterialType));
            }
            productTypeList.remove(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
            productTypeList.remove(pgV3Constants.TYPE_FORMULATIONPART);

            if (UIUtil.isNotNullAndNotEmpty(this.defaultTypeOnCreateProduct)) { // only When type is set on (default create product)
                String types = productPreferenceConfig.getTypes(); // get types configured as product
                if (isTypeMatch(types, this.defaultTypeOnCreateProduct)) { // When type set on (default create product) is of (Product)
                    // When type set on (default create product) matches with types configured on product preference. or
                    String partTypeName = this.productPreferenceType;
                    if (this.defaultTypeOnCreateProduct.equalsIgnoreCase(partTypeName)) {
                        // When type set on (default create product) matches with type set on (Product Preference). then get phase from (Product Preference).
                        this.phase = Preferences.ProductPreference.PHASE.getName(this.context);
                    }
                    // get segment from Product Preference.
                    this.segmentName = Preferences.ProductPreference.SEGMENT.getName(this.context);
                    this.segmentID = Preferences.ProductPreference.SEGMENT.getID(this.context);
                }
                types = packagingPreferenceConfig.getTypes(); // get types configured as packaging
                if (isTypeMatch(types, this.defaultTypeOnCreateProduct)) { // When type set on (default create product) is of (Packaging)
                    // When type set on (default create product) matches with types configured on packaging preference.
                    String partTypeName = this.packagingPreferenceType;
                    if (this.defaultTypeOnCreateProduct.equalsIgnoreCase(partTypeName)) {
                        this.phase = Preferences.PackagingPreference.PHASE.getName(this.context);
                    }
                    this.segmentName = Preferences.PackagingPreference.SEGMENT.getName(this.context);
                    this.segmentID = Preferences.PackagingPreference.SEGMENT.getID(this.context);
                }
                types = rawMaterialPreferenceConfig.getTypes(); // get types configured as raw material
                if (isTypeMatch(types, this.defaultTypeOnCreateProduct)) { // When type set on (default create product) is of (Raw Material)
                    // When type set on (default create product) matches with types configured on raw-material preference.
                    String partTypeName = this.rawMaterialPreferenceType;
                    if (this.defaultTypeOnCreateProduct.equalsIgnoreCase(partTypeName)) {
                        this.phase = Preferences.RawMaterialPreference.PHASE.getName(this.context);
                    }
                    this.segmentName = Preferences.RawMaterialPreference.SEGMENT.getName(this.context);
                    this.segmentID = Preferences.RawMaterialPreference.SEGMENT.getID(this.context);
                }
            }
            ChangeActionPreference changeActionPreference = new ChangeActionPreference(this.context);
            this.changeTemplateName = changeActionPreference.getChangeTemplateName();
            this.changeTemplateOID = changeActionPreference.getChangeTemplateOID();

            IPSecurityPreference ipSecurityPreference = new IPSecurityPreference(this.context);
            this.primaryOrgName = ipSecurityPreference.getPrimaryOrgName();
            this.primaryOrgID = ipSecurityPreference.getPrimaryOrgID();
        }

        boolean isTypeMatch(String types, String partType) throws FrameworkException {
            return StringUtil.split(types, pgV3Constants.SYMBOL_COMMA)
                    .contains(FrameworkUtil.getAliasForAdmin(this.context, DomainConstants.SELECT_TYPE, partType, true));
        }
    }
}
