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
import com.pg.dsm.preference.config.xml.BusinessArea;
import com.pg.dsm.preference.config.xml.ClassType;
import com.pg.dsm.preference.config.xml.ProductCategoryPlatform;
import com.pg.dsm.preference.config.xml.ProductComplianceRequired;
import com.pg.dsm.preference.config.xml.ProductPreferenceConfig;
import com.pg.dsm.preference.config.xml.ReportedFunction;
import com.pg.dsm.preference.config.xml.StructureReleaseCriteria;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.ProductPreference;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class ProductUserPreferenceTemplateService {
    boolean applied;
    String error;

    private ProductUserPreferenceTemplateService(Apply apply) {
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
        ProductPreferenceConfig productPreferenceConfig;
        boolean applied;
        String error;
        Context context;

        public Apply(Context context, ProductPreferenceConfig productPreferenceConfig) {
            this.context = context;
            this.productPreferenceConfig = productPreferenceConfig;
            this.applied = Boolean.FALSE;
        }

        public ProductUserPreferenceTemplateService now(DomainObject domainObject, String objectOid, String symbolicType, String uptOid) throws Exception {
            try {
                applyNow(domainObject, objectOid, symbolicType, uptOid);
                this.applied = Boolean.TRUE;
            } catch (Exception e) {
                this.applied = Boolean.FALSE;
                this.error = e.getMessage();
                logger.log(Level.WARNING, "Error Applying Product User Preference Template:" + e);
                throw e;
            }
            return new ProductUserPreferenceTemplateService(this);
        }

        private void applyNow(DomainObject domainObject, String objectOid, String symbolicType, String uptOid) throws Exception {
            try {
                String incomingType = PropertyUtil.getSchemaProperty(this.context, symbolicType);
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                ProductUserTemplate productUserTemplate = userPreferenceUtil.getProductUserTemplate(context, uptOid);
                Map<String, String> attributeMap = new HashMap<>();

                String partType = productUserTemplate.getType();
                if (UIUtil.isNotNullAndNotEmpty(partType)) {
                    partType = FrameworkUtil.getAliasForAdmin(this.context, DomainConstants.SELECT_TYPE, partType, true);
                    boolean isFormulation = isFormulationType(symbolicType);
                    if (isFormulation) {
                        if (pgV3Constants.TYPE_FORMULATIONPART.equals(incomingType)) {
                            this.applyReportedFunction(userPreferenceUtil, domainObject, symbolicType, productUserTemplate.getReportedFunction());
                        }
                    } else {
                        if (UIUtil.isNotNullAndNotEmpty(partType) && symbolicType.equalsIgnoreCase(partType)) { // if incoming type is equal to product preference - part type.}
                            // apply phase, mfg status and reported function (as these values are type specific)
                            if (userPreferenceUtil.isPreferencePhaseMatchWithPhaseAttribute(this.context, domainObject, productUserTemplate.getPhase())) {
                                String manufacturingStatus = productUserTemplate.getMfgStatus();
                                logger.log(Level.INFO, "Mfg Status: {0}", manufacturingStatus);
                                if (UIUtil.isNotNullAndNotEmpty(manufacturingStatus)) {
                                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, manufacturingStatus);
                                }
                            }
                        }
                        // apply reported function
                        this.applyReportedFunction(userPreferenceUtil, domainObject, symbolicType, productUserTemplate.getReportedFunction());
                    }
                }else {
                	// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47565 - Start	
                	this.applyReportedFunction(userPreferenceUtil, domainObject, symbolicType, productUserTemplate.getReportedFunction());
                	// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47565 - End	
                }
                // apply structure release criteria (attribute)
                String releaseCriteria = productUserTemplate.getReleaseCriteria();
                if (UIUtil.isNotNullAndNotEmpty(releaseCriteria)) {
                    Map<String, String> map = this.applyStructureReleaseCriteria(symbolicType, releaseCriteria);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }
                // apply class (attribute)
                String classValue = productUserTemplate.getClassType();
                if (UIUtil.isNotNullAndNotEmpty(classValue)) {
                    Map<String, String> map = this.applyClass(symbolicType, classValue);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }

                // apply product compliance required (attribute) - default value is No on attribute. so set as per preference.
                String productComplianceRequired = productUserTemplate.getComplianceRequired();
                if (UIUtil.isNotNullAndNotEmpty(productComplianceRequired)) {
                    Map<String, String> map = this.applyProductComplianceRequired(symbolicType, productComplianceRequired);
                    if (null != map && !map.isEmpty()) {
                        attributeMap.putAll(map);
                    }
                }
                logger.log(Level.INFO, "Packaging Preference attribute Context User: {0}", this.context.getUser());
                logger.log(Level.INFO, "Product Preference attribute Map: {0}", attributeMap);
                if (null != attributeMap && !attributeMap.isEmpty()) {
                    domainObject.setAttributeValues(this.context, attributeMap);
                }
                // apply segment (pre-populate this value on creation page - Create Product Data)
                // apply business area (relationship)
                this.applyBusinessArea(userPreferenceUtil, domainObject, symbolicType, productUserTemplate.getBusinessArea());// business area physical id is stored on preference.

                // apply product category platform (relationship)
                this.applyProductCategoryPlatform(userPreferenceUtil, domainObject, symbolicType, productUserTemplate.getCategoryPlatform()); // Product Category Platform physical id is stored on preference.

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error Applying Product Preference:" + e);
                throw e;
            }
        }

        private void applyProductCategoryPlatform(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String productCategoryPlatformValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(productCategoryPlatformValue)) {
                boolean apply = Boolean.FALSE;
                ProductCategoryPlatform productCategoryPlatform = this.productPreferenceConfig.getProductCategoryPlatform();
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

        private void applyBusinessArea(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String businessAreaValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(businessAreaValue)) {
                boolean apply = Boolean.FALSE;
                BusinessArea businessArea = this.productPreferenceConfig.getBusinessArea();
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
         * @param reportedFunctionValue
         * @throws FrameworkException
         */
        private void applyReportedFunction(UserPreferenceUtil userPreferenceUtil, DomainObject domainObject, String symbolicType, String reportedFunctionValue) throws FrameworkException {
            // apply reported function
            if (UIUtil.isNotNullAndNotEmpty(reportedFunctionValue)) {
                boolean apply = Boolean.FALSE;
                ReportedFunction reportedFunction = this.productPreferenceConfig.getReportedFunction();
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
                StructureReleaseCriteria structureReleaseCriteria = this.productPreferenceConfig.getStructureReleaseCriteria();
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

        private Map<String, String> applyClass(String symbolicType, String classValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(classValue)) {
                boolean apply = Boolean.FALSE;
                ClassType classType = this.productPreferenceConfig.getClassType();
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

        private Map<String, String> applyProductComplianceRequired(String symbolicType, String productComplianceRequiredValue) {
            Map<String, String> map = new HashMap<>();
            if (UIUtil.isNotNullAndNotEmpty(productComplianceRequiredValue)) {
                boolean apply = Boolean.FALSE;
                ProductComplianceRequired productComplianceRequired = this.productPreferenceConfig.getProductComplianceRequired();
                String allowedTypes = productComplianceRequired.getAllowedTypes();
                if (PreferenceConstants.Basic.ALL.get().equalsIgnoreCase(allowedTypes)) {
                    apply = Boolean.TRUE;
                } else {
                    if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                        apply = Boolean.TRUE;
                    }
                }
                if (apply) {
                    map.put(pgV3Constants.ATTRIBUTE_PGISPRODUCTCERTIFICATIONORLOCALSTANDARDSCOMPLIANCESTATEMENTREQUIRED, productComplianceRequiredValue);
                }
            }
            return map;
        }

        private boolean isFormulationType(String productPreferenceType, String incomingSymbolicType) {
            boolean isFOP = Boolean.FALSE;
            if (PreferenceConstants.Type.TYPE_FORMULATION.getName(this.context).equalsIgnoreCase(productPreferenceType)) {
                if (pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(PropertyUtil.getSchemaProperty(this.context, incomingSymbolicType))) {
                    isFOP = Boolean.TRUE;
                }
            }
            return isFOP;
        }

        private boolean isFormulationType(String incomingSymbolicType) {
            boolean isFOP = Boolean.FALSE;
            String incomingType = PropertyUtil.getSchemaProperty(this.context, incomingSymbolicType);
            if (pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(incomingType) || incomingType.equals(pgV3Constants.TYPE_FORMULATIONPROCESS)) {
                isFOP = Boolean.TRUE;
            }
            return isFOP;
        }


        /**
         * @param objectOid
         * @param incomingTypeSymbolic
         * @param productPreference
         * @return
         * @throws FrameworkException
         */
        private Map<String, String> whenFormulationPartOrFormulationProcess(String objectOid, String incomingTypeSymbolic, ProductPreference productPreference) throws FrameworkException {
            Map<String, String> attributeMap = new HashMap<>();
            String incomingType = PropertyUtil.getSchemaProperty(this.context, incomingTypeSymbolic);
            DomainObject formulationObj = DomainObject.newInstance(this.context, objectOid);
            if (pgV3Constants.TYPE_FORMULATIONPART.equals(incomingType)) {
                String partType = productPreference.getPartType();
                if (incomingType.equalsIgnoreCase(partType)) {
                    getFormulationPartConnectedFormulation(formulationObj);
                    String selectPhaseFromParent = "to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE;
                    String formulationPhase = formulationObj.getInfo(this.context, selectPhaseFromParent.trim());
                    String phase = productPreference.getPhase();
                    String manufacturingStatus = productPreference.getManufacturingStatus();
                    if (UIUtil.isNotNullAndNotEmpty(formulationPhase) && phase.equals(formulationPhase)) {
                        attributeMap.put(pgV3Constants.STR_RELEASE_PHASE, formulationPhase);
                        attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, manufacturingStatus);
                    }
                    UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                    // apply reported function (relationship)
                    this.applyReportedFunction(userPreferenceUtil, formulationObj, incomingTypeSymbolic, productPreference.getReportedFunction());
                }
            }
            if (incomingType.equals(pgV3Constants.TYPE_FORMULATIONPROCESS)) {
                getFormulationProcessConnectedFormulationPart(formulationObj);
                String selectPhaseFromParent = "to[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].from." + pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE;
                String formulationPhase = formulationObj.getInfo(this.context, selectPhaseFromParent.trim());
                if (UIUtil.isNotNullAndNotEmpty(formulationPhase)) {
                    attributeMap.put(pgV3Constants.STR_RELEASE_PHASE, formulationPhase);
                }
            }
            return attributeMap;
        }

        Map<Object, Object> getFormulationPartConnectedFormulation(DomainObject domainObject) throws FrameworkException {
            StringList objectSelects = new StringList();
            objectSelects.add(PreferenceConstants.Basic.HAS_FORMULATION.get());
            objectSelects.add(PreferenceConstants.Basic.SELECT_FORMULATION_RELEASE_PHASE.get());
            Map<Object, Object> objectInfo = domainObject.getInfo(this.context, objectSelects);
            return objectInfo;
        }

        Map<Object, Object> getFormulationProcessConnectedFormulationPart(DomainObject domainObject) throws FrameworkException {
            StringList objectSelects = new StringList();
            objectSelects.add(PreferenceConstants.Basic.HAS_FORMULATION_PART.get());
            objectSelects.add(PreferenceConstants.Basic.SELECT_FORMULATION_PART_RELEASE_PHASE.get());
            Map<Object, Object> objectInfo = domainObject.getInfo(this.context, objectSelects);
            return objectInfo;
        }
    }
}
