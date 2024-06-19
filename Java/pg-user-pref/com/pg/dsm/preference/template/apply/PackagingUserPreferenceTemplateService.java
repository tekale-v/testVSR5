package com.pg.dsm.preference.template.apply;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.config.xml.BaseUoM;
import com.pg.dsm.preference.config.xml.ClassType;
import com.pg.dsm.preference.config.xml.ComponentType;
import com.pg.dsm.preference.config.xml.MaterialType;
import com.pg.dsm.preference.config.xml.PackagingPreferenceConfig;
import com.pg.dsm.preference.config.xml.ReportedFunction;
import com.pg.dsm.preference.config.xml.StructureReleaseCriteria;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class PackagingUserPreferenceTemplateService {
    boolean applied;
    String error;

    private PackagingUserPreferenceTemplateService(Apply apply) {
        this.applied = apply.applied;
        this.error = apply.error;
    }

    public boolean isApplied() {
        return applied;
    }

    public String getError() {
        return error;
    }

    public static class Apply {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        PackagingPreferenceConfig packagingPreferenceConfig;
        boolean applied;
        Context context;
        String error;

        public Apply(Context context, PackagingPreferenceConfig packagingPreferenceConfig) {
            this.context = context;
            this.packagingPreferenceConfig = packagingPreferenceConfig;
            this.applied = Boolean.FALSE;
        }

        public PackagingUserPreferenceTemplateService now(DomainObject domainObject, String objectOid, String symbolicType, String uptOid) throws Exception {
            try {
                applyNow(domainObject, objectOid, symbolicType, uptOid);
                this.applied = Boolean.TRUE;
            } catch (Exception e) {
                this.applied = Boolean.FALSE;
                this.error = e.getMessage();
                logger.log(Level.WARNING, "Error Applying Packaging User Preference Template:" + e);
                throw e;
            }
            return new PackagingUserPreferenceTemplateService(this);
        }

        private void applyNow(DomainObject domainObject, String objectOid, String symbolicType, String uptOid) throws Exception {
            try {
                Map<String, String> attributeMap = new HashMap<>();

                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                PackagingUserTemplate packagingUserTemplate = userPreferenceUtil.getPackagingUserTemplate(context, uptOid);

                String partType = packagingUserTemplate.getType(); // get type from preferences
                if (UIUtil.isNotNullAndNotEmpty(partType)) {
                    partType = FrameworkUtil.getAliasForAdmin(this.context, DomainConstants.SELECT_TYPE, partType, true);
                    if (UIUtil.isNotNullAndNotEmpty(partType) && symbolicType.equalsIgnoreCase(partType)) { // if incoming type is equal to packaging preference - part type.
                        // if type is FPP-HALB, then do not apply phase & mfg status
                        if (!isFPPHALB(domainObject, symbolicType)) {
                            // apply phase, mfg status and reported function (as these values are type specific)
                            if (userPreferenceUtil.isPreferencePhaseMatchWithPhaseAttribute(this.context, domainObject, packagingUserTemplate.getPhase())) {
                                String manufacturingStatus = packagingUserTemplate.getMfgStatus();
                                logger.log(Level.INFO, "Mfg Status: {0}", manufacturingStatus);
                                if (UIUtil.isNotNullAndNotEmpty(manufacturingStatus)) {
                                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, manufacturingStatus);
                                }
                            }
                        }
                    }
                }
                // apply reported function
                this.applyReportedFunction(userPreferenceUtil, domainObject, symbolicType, packagingUserTemplate.getReportedFunction());

                // apply structure release criteria
                String releaseCriteria = packagingUserTemplate.getReleaseCriteria();
                if (UIUtil.isNotNullAndNotEmpty(releaseCriteria)) {
                    Map<String, String> map = this.applyStructureReleaseCriteria(symbolicType, releaseCriteria);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }
                // apply class
                String classValue = packagingUserTemplate.getClassType();
                if (UIUtil.isNotNullAndNotEmpty(classValue)) {
                    Map<String, String> map = this.applyClass(symbolicType, classValue);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }

                // apply packaging component type
                String packagingComponentType = packagingUserTemplate.getComponentType();
                if (UIUtil.isNotNullAndNotEmpty(packagingComponentType)) {
                    Map<String, String> map = this.applyPackagingComponentType(symbolicType, packagingComponentType);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }

                // apply packaging material type
                String packagingMaterialType = packagingUserTemplate.getMaterialType();
                if (UIUtil.isNotNullAndNotEmpty(packagingMaterialType)) {
                    Map<String, String> map = this.applyPackagingMaterialType(symbolicType, packagingMaterialType);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }

                // apply base unit of measure.
                String baseUnitOfMeasure = packagingUserTemplate.getBaseUoM();
                if (UIUtil.isNotNullAndNotEmpty(baseUnitOfMeasure)) {
                    Map<String, String> map = this.applyBaseUOM(symbolicType, baseUnitOfMeasure);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }
                logger.log(Level.INFO, "Packaging Preference attribute Context User: {0}", this.context.getUser());
                logger.log(Level.INFO, "Packaging Preference attribute Map: {0}", attributeMap);
                if (null != attributeMap && !attributeMap.isEmpty()) {
                    domainObject.setAttributeValues(this.context, attributeMap);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error Applying Packaging Preference:" + e);
                throw e;
            }
        }

        private boolean isFPPHALB(DomainObject domainObject, String symbolicType) throws FrameworkException {
            boolean halb = Boolean.FALSE;
            if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(PropertyUtil.getSchemaProperty(this.context, symbolicType))) {
                String sapType = domainObject.getInfo(this.context, pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
                if ("HALB".equalsIgnoreCase(sapType)) {
                    halb = Boolean.TRUE;
                }
            }
            return halb;
        }

        private void applyReportedFunction(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String reportedFunctionValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(reportedFunctionValue)) {
                boolean apply = Boolean.FALSE;
                ReportedFunction reportedFunction = this.packagingPreferenceConfig.getReportedFunction();
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
         * @param symbolicType
         * @param structureReleaseCriteriaValue
         * @return
         */
        private Map<String, String> applyStructureReleaseCriteria(String symbolicType, String structureReleaseCriteriaValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(structureReleaseCriteriaValue)) {
                boolean apply = Boolean.FALSE;
                StructureReleaseCriteria structureReleaseCriteria = this.packagingPreferenceConfig.getStructureReleaseCriteria();
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

        /**
         * @param symbolicType
         * @param classValue
         * @return
         */
        private Map<String, String> applyClass(String symbolicType, String classValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(classValue)) {
                boolean apply = Boolean.FALSE;
                ClassType classType = this.packagingPreferenceConfig.getClassType();
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
         * @param packagingComponentTypeValue
         * @return
         */
        private Map<String, String> applyPackagingComponentType(String symbolicType, String packagingComponentTypeValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(packagingComponentTypeValue)) {
                boolean apply = Boolean.FALSE;
                ComponentType componentType = this.packagingPreferenceConfig.getComponentType();
                String allowedTypes = componentType.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    map.put(pgV3Constants.ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE, packagingComponentTypeValue);
                }
            }
            return map;
        }

        /**
         * @param symbolicType
         * @param packagingMaterialTypeValue
         * @return
         */
        private Map<String, String> applyPackagingMaterialType(String symbolicType, String packagingMaterialTypeValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(packagingMaterialTypeValue)) {
                boolean apply = Boolean.FALSE;
                MaterialType materialType = this.packagingPreferenceConfig.getMaterialType();
                String allowedTypes = materialType.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    map.put(pgV3Constants.ATTRIBUTE_PGPACKAGINGMATERIALTYPE, packagingMaterialTypeValue);
                }
            }
            return map;
        }

        /**
         * @param symbolicType
         * @param baseUOMValue
         * @return
         */
        private Map<String, String> applyBaseUOM(String symbolicType, String baseUOMValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(baseUOMValue)) {
                boolean apply = Boolean.FALSE;
                BaseUoM baseUoM = this.packagingPreferenceConfig.getBaseUoM();
                String allowedTypes = baseUoM.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    map.put(pgV3Constants.ATTRIBUTE_PGBASEUNITOFMEASURE, baseUOMValue);
                }
            }
            return map;
        }
    }
}
