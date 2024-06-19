package com.pg.dsm.preference.usage;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.config.xml.PackagingPreferenceConfig;
import com.pg.dsm.preference.config.xml.PreferenceConfig;
import com.pg.dsm.preference.config.xml.ProductPreferenceConfig;
import com.pg.dsm.preference.config.xml.RawMaterialPreferenceConfig;
import com.pg.dsm.preference.services.PreferenceConfigLoader;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;

public class ProductData {

    String type;
    String phase;
    String segmentName;
    String segmentID;

    String changeTemplateName;
    String changeTemplateOID;
    String primaryOrgName;
    String primaryOrgID;

    private ProductData(Load builder) {
        this.phase = builder.phase;
        this.segmentName = builder.segmentName;
        this.segmentID = builder.segmentID;
        this.type = builder.type;
        this.changeTemplateName = builder.changeTemplateName;
        this.changeTemplateOID = builder.changeTemplateOID;
        this.primaryOrgName = builder.primaryOrgName;
        this.primaryOrgID = builder.primaryOrgID;
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

    public String getType() {
        return type;
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

    @Override
    public String toString() {
        return "ProductData{" +
                "type='" + type + '\'' +
                ", phase='" + phase + '\'' +
                ", segmentName='" + segmentName + '\'' +
                ", segmentID='" + segmentID + '\'' +
                ", changeTemplateName='" + changeTemplateName + '\'' +
                ", changeTemplateOID='" + changeTemplateOID + '\'' +
                ", primaryOrgName='" + primaryOrgName + '\'' +
                ", primaryOrgID='" + primaryOrgID + '\'' +
                '}';
    }

    public static class Load {
        private final Logger logger = Logger.getLogger(this.getClass().getName());

        Context context;
        String productPreferenceType;
        String packagingPreferenceType;
        String rawMaterialPreferenceType;
        String type;
        String phase;
        String segmentName;
        String segmentID;

        String changeTemplateName;
        String changeTemplateOID;
        String primaryOrgName;
        String primaryOrgID;

        public Load(Context context) {
            this.context = context;
            this.type = DomainConstants.EMPTY_STRING;
            this.phase = DomainConstants.EMPTY_STRING;
            this.segmentName = DomainConstants.EMPTY_STRING;
            this.segmentID = DomainConstants.EMPTY_STRING;

            this.changeTemplateName = DomainConstants.EMPTY_STRING;
            this.changeTemplateOID = DomainConstants.EMPTY_STRING;
            this.primaryOrgName = DomainConstants.EMPTY_STRING;
            this.primaryOrgID = DomainConstants.EMPTY_STRING;
        }

        public ProductData now(String incomingType) {
            try {
                logger.log(Level.INFO, "Incoming Type: " + incomingType);
                build(incomingType);

            } catch (Exception e) {

            }
            return new ProductData(this);
        }

        private void build(String incomingType) throws MatrixException {

            this.productPreferenceType = Preferences.ProductPreference.PART_TYPE.getName(this.context);
            this.packagingPreferenceType = Preferences.PackagingPreference.PART_TYPE.getName(this.context);
            this.rawMaterialPreferenceType = Preferences.RawMaterialPreference.PART_TYPE.getName(this.context);
            this.type = incomingType;

            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(this.context);

            String productTypes = DomainConstants.EMPTY_STRING;
            String packagingTypes = DomainConstants.EMPTY_STRING;
            String rawMaterialTypes = DomainConstants.EMPTY_STRING;
            String symbolicType = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, incomingType, true);

            if (preferenceConfigLoader.isLoaded()) {
                PreferenceConfig preferenceConfig = preferenceConfigLoader.getPreferenceConfig();
                ProductPreferenceConfig productPreferenceConfig = preferenceConfig.getProductPreferenceConfig();
                PackagingPreferenceConfig packagingPreferenceConfig = preferenceConfig.getPackagingPreferenceConfig();
                RawMaterialPreferenceConfig rawMaterialPreferenceConfig = preferenceConfig.getRawMaterialPreferenceConfig();
                productTypes = productPreferenceConfig.getTypes();
                packagingTypes = packagingPreferenceConfig.getTypes();
                rawMaterialTypes = rawMaterialPreferenceConfig.getTypes();
            }
            if (incomingType.equalsIgnoreCase(this.productPreferenceType)) {
                this.phase = Preferences.ProductPreference.PHASE.getName(this.context);
            } else if (incomingType.equalsIgnoreCase(this.packagingPreferenceType)) {
                this.phase = Preferences.PackagingPreference.PHASE.getName(this.context);
            } else if (incomingType.equalsIgnoreCase(this.rawMaterialPreferenceType)) {
                this.phase = Preferences.RawMaterialPreference.PHASE.getName(this.context);
            }
            if (StringUtil.split(productTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                // get segment from Product Preference.
                this.segmentName = Preferences.ProductPreference.SEGMENT.getName(this.context);
                this.segmentID = Preferences.ProductPreference.SEGMENT.getID(this.context);
            } else if (StringUtil.split(packagingTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                // get segment from Product Preference.
                this.segmentName = Preferences.PackagingPreference.SEGMENT.getName(this.context);
                this.segmentID = Preferences.PackagingPreference.SEGMENT.getID(this.context);
            } else if (StringUtil.split(rawMaterialTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                // get segment from Product Preference.
                this.segmentName = Preferences.RawMaterialPreference.SEGMENT.getName(this.context);
                this.segmentID = Preferences.RawMaterialPreference.SEGMENT.getID(this.context);
            }
            this.changeTemplateName = Preferences.ChangeActionPreference.CHANGE_TEMPLATE.getName(this.context);
            this.changeTemplateOID = Preferences.ChangeActionPreference.CHANGE_TEMPLATE.getID(this.context);

            this.primaryOrgName = Preferences.IPSecurityPreference.PRIMARY_ORGANIZATION.getName(this.context);
            this.primaryOrgID = Preferences.IPSecurityPreference.PRIMARY_ORGANIZATION.getID(this.context);
        }
    }
}
