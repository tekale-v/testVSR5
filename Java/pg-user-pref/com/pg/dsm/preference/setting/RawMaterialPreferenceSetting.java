package com.pg.dsm.preference.setting;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.config.xml.BusinessArea;
import com.pg.dsm.preference.config.xml.ClassType;
import com.pg.dsm.preference.config.xml.MaterialFunction;
import com.pg.dsm.preference.config.xml.ProductCategoryPlatform;
import com.pg.dsm.preference.config.xml.RawMaterialPreferenceConfig;
import com.pg.dsm.preference.config.xml.ReportedFunction;
import com.pg.dsm.preference.config.xml.StructureReleaseCriteria;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.RawMaterialPreference;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class RawMaterialPreferenceSetting {
    boolean applied;

    private RawMaterialPreferenceSetting(Apply apply) {
        this.applied = apply.applied;
    }

    public boolean isApplied() {
        return applied;
    }

    public static class Apply {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        RawMaterialPreferenceConfig rawMaterialPreferenceConfig;
        boolean applied;

        public Apply(Context context, RawMaterialPreferenceConfig rawMaterialPreferenceConfig) {
            this.context = context;
            this.rawMaterialPreferenceConfig = rawMaterialPreferenceConfig;
            this.applied = Boolean.FALSE;
        }

        public RawMaterialPreferenceSetting now(DomainObject domainObject, String objectOid, String symbolicType) throws Exception {
            try {
                applyNow(domainObject, objectOid, symbolicType);
                this.applied = Boolean.TRUE;
            } catch (Exception e) {
                this.applied = Boolean.FALSE;
                logger.log(Level.WARNING, "Error Applying Raw Material Preference:" + e);
                throw e;
            }
            return new RawMaterialPreferenceSetting(this);
        }

        private void applyNow(DomainObject domainObject, String objectOid, String symbolicType) throws Exception {
            try {
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                Map<String, String> attributeMap = new HashMap<>();
                RawMaterialPreference rawMaterialPreference = new RawMaterialPreference(this.context);
                String partType = rawMaterialPreference.getPartType();
                if (UIUtil.isNotNullAndNotEmpty(partType)) {
                    partType = FrameworkUtil.getAliasForAdmin(this.context, DomainConstants.SELECT_TYPE, partType, true);
                    if (UIUtil.isNotNullAndNotEmpty(partType) && symbolicType.equalsIgnoreCase(partType)) { // if incoming type is equal to packaging preference - part type.}
                        // apply phase, mfg status and reported function (as these values are type specific)
                        if (userPreferenceUtil.isPreferencePhaseMatchWithPhaseAttribute(this.context, domainObject, rawMaterialPreference.getPhase())) {
                            String manufacturingStatus = rawMaterialPreference.getManufacturingStatus();
                            logger.log(Level.INFO, "Mfg Status: {0}", manufacturingStatus);
                            if (UIUtil.isNotNullAndNotEmpty(manufacturingStatus)) {
                                attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, manufacturingStatus);
                            }
                        }
                    }
                }
                // apply reported function
                this.applyReportedFunction(userPreferenceUtil, domainObject, symbolicType, rawMaterialPreference.getReportedFunction());

                // apply structure release criteria (attribute)
                String releaseCriteria = rawMaterialPreference.getReleaseCriteria();
                if (UIUtil.isNotNullAndNotEmpty(releaseCriteria)) {
                    Map<String, String> map = this.applyStructureReleaseCriteria(symbolicType, releaseCriteria);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }

                // apply class (attribute)
                String classValue = rawMaterialPreference.getClassValue();
                if (UIUtil.isNotNullAndNotEmpty(classValue)) {
                    Map<String, String> map = this.applyClass(symbolicType, classValue);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }
                logger.log(Level.INFO, "Packaging Preference attribute Context User: {0}", this.context.getUser());
                logger.log(Level.INFO, "Raw Material Preference attribute Map: {0}", attributeMap);
                if (null != attributeMap && !attributeMap.isEmpty()) {
                    domainObject.setAttributeValues(this.context, attributeMap);
                }

                // apply business area
                this.applyBusinessArea(userPreferenceUtil, domainObject, symbolicType, rawMaterialPreference.getBusinessAreaID());// business area physical id is stored on preference.

                // apply product category platform
                this.applyProductCategoryPlatform(userPreferenceUtil, domainObject, symbolicType, rawMaterialPreference.getProductCategoryPlatformID()); // Product Category Platform physical id is stored on preference.

                // apply function
                this.applyMaterialFunctionGlobal(userPreferenceUtil, domainObject, symbolicType, rawMaterialPreference.getMaterialFunction());// Material Function object id is stored.

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error Applying Raw Material Preference:" + e);
                throw e;
            }
        }

        /**
         * @param userPreferenceUtil
         * @param domainObject
         * @param symbolicType
         * @param reportedFunctionValue
         * @throws FrameworkException
         */
        private void applyReportedFunction(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String reportedFunctionValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(reportedFunctionValue)) {
                boolean apply = Boolean.FALSE;
                ReportedFunction reportedFunction = this.rawMaterialPreferenceConfig.getReportedFunction();
                String allowedTypes = reportedFunction.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    userPreferenceUtil.applyReportedFunction(this.context, domainObject, reportedFunctionValue);
                }
            }
        }

        /**
         * @param userPreferenceUtil
         * @param domainObject
         * @param symbolicType
         * @param materialFunctionGlobalValue
         * @throws FrameworkException
         */
        private void applyMaterialFunctionGlobal(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String materialFunctionGlobalValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(materialFunctionGlobalValue)) {
                boolean apply = Boolean.FALSE;
                MaterialFunction materialFunction = this.rawMaterialPreferenceConfig.getMaterialFunction();
                String allowedTypes = materialFunction.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    userPreferenceUtil.applyMaterialFunctionGlobal(this.context, domainObject, materialFunctionGlobalValue); // Material Function object id is stored.
                }
            }
        }

        /**
         * @param userPreferenceUtil
         * @param domainObject
         * @param symbolicType
         * @param businessAreaValue
         * @throws FrameworkException
         */
        private void applyBusinessArea(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String businessAreaValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(businessAreaValue)) {
                boolean apply = Boolean.FALSE;
                BusinessArea businessArea = this.rawMaterialPreferenceConfig.getBusinessArea();
                String allowedTypes = businessArea.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    userPreferenceUtil.applyBusinessArea(this.context, domainObject, businessAreaValue);
                }
            }
        }

        /**
         * @param userPreferenceUtil
         * @param domainObject
         * @param symbolicType
         * @param productCategoryPlatformValue
         * @throws FrameworkException
         */
        private void applyProductCategoryPlatform(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String productCategoryPlatformValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(productCategoryPlatformValue)) {
                boolean apply = Boolean.FALSE;
                ProductCategoryPlatform productCategoryPlatform = this.rawMaterialPreferenceConfig.getProductCategoryPlatform();
                String allowedTypes = productCategoryPlatform.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    userPreferenceUtil.applyProductCategoryPlatform(this.context, domainObject, productCategoryPlatformValue); // Product Category Platform physical id is stored on preference.
                }
            }
        }

        /**
         * @param symbolicType
         * @param classValue
         * @return
         */
        private Map<String, String> applyClass(String symbolicType, String classValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(classValue)) {
                boolean apply = Boolean.FALSE;
                ClassType classType = this.rawMaterialPreferenceConfig.getClassType();
                String allowedTypes = classType.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    map.put(pgV3Constants.ATTRIBUTE_PGCLASS, classValue);
                }
            }
            return map;
        }

        /**
         * @param symbolicType
         * @param structureReleaseCriteriaValue
         * @return
         */
        private Map<String, String> applyStructureReleaseCriteria(String symbolicType, String structureReleaseCriteriaValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(structureReleaseCriteriaValue)) {
                boolean apply = Boolean.FALSE;
                StructureReleaseCriteria structureReleaseCriteria = this.rawMaterialPreferenceConfig.getStructureReleaseCriteria();
                String allowedTypes = structureReleaseCriteria.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    map.put(PreferenceConstants.Attribute.STRUCTURE_RELEASE_CRITERIA_REQUIRED.getName(this.context), structureReleaseCriteriaValue);
                }
            }
            return map;
        }
    }
}
