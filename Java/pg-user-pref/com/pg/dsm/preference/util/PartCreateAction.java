/*
    Added by DSM (Sogeti) for 2022x-01 Feb CW - Requirement #45664,#45665
 */
package com.pg.dsm.preference.util;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.DIPreference;
import com.pg.dsm.preference.models.DSMUserPreferenceConfig;
import com.pg.dsm.preference.models.DefaultCreatePartTypePreference;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class PartCreateAction {

    private static final Logger logger = Logger.getLogger(PartCreateAction.class.getName());

    public void propagateUPTAttributesOnPartCreation(Context context, String[] args) throws Exception {
        Instant startTime = Instant.now();
        if (null != args) {
            String objectType = args[0];
            String objectOid = args[1];
            if (UIUtil.isNotNullAndNotEmpty(objectType) && UIUtil.isNotNullAndNotEmpty(objectOid)) {
                logger.log(Level.INFO, "Set Preference on Incoming Object > | {0} | {1} |", new Object[]{objectType, objectOid});
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                DSMUserPreferenceConfig dsmUserPreferenceConfig = userPreferenceUtil.getDSMUserPreferenceConfig(context);
                if (!isCATIADesignerPart(context, objectOid, dsmUserPreferenceConfig)) {
                    userPreferenceUtil.propagetUPTAttributes(context, args);
                }
            }
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("User Preferences - Part Update - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void setPreferencesOnPartCreation(Context context, String[] args) throws Exception {
        Instant startTime = Instant.now();
        if (null != args) {
            String objectType = args[0];
            String objectOid = args[1];
            if (UIUtil.isNotNullAndNotEmpty(objectType) && UIUtil.isNotNullAndNotEmpty(objectOid)) {
                logger.log(Level.INFO, "Set Preference on Incoming Object > | {0} | {1} |", new Object[]{objectType, objectOid});
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                DSMUserPreferenceConfig dsmUserPreferenceConfig = userPreferenceUtil.getDSMUserPreferenceConfig(context);
                if (!isCATIADesignerPart(context, objectOid, dsmUserPreferenceConfig)) {
                    userPreferenceUtil.updateObjectPreferences(context, args);
                }
            }
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("User Preferences - Part Update - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }


    /**
     * @param context
     * @param sType
     * @param sObjectId
     * @param dsmUserPreferenceConfig
     * @throws Exception
     */
    private void setDIPreferences(Context context, String sType, String sObjectId, DSMUserPreferenceConfig dsmUserPreferenceConfig) throws Exception {
        Map mAttributeValues = new HashMap();
        try {
            DIPreference diPreference = new DIPreference(context);
            String sPartTypePrefVal = diPreference.getPartTypePreferredValue();
            String sPhasePrefVal = diPreference.getPhasePreferredValue();
            String sMaturityStatusPrefVal = diPreference.getManufacturingStatusPreferredValue();
            String sReleaseCriteriaPrefVal = diPreference.getReleaseStatusCriteriaPreferredValue();
            String sPackagingMaterialTypePrefVal = diPreference.getPackagingMaterialTypePreferredValue();
            String sClassPrefVal = diPreference.getClassPreferredValue();
            String sReportedFuncPrefVal = diPreference.getReportedFunctionPreferredValue();
            DomainObject domainObject = DomainObject.newInstance(context, sObjectId);

            mAttributeValues = setManufacturingStatus(context, sType, domainObject, sPartTypePrefVal, sMaturityStatusPrefVal, sPhasePrefVal);
            boolean isConnectReportFunction = false;

            if (UIUtil.isNotNullAndNotEmpty(sReleaseCriteriaPrefVal) &&
                    (dsmUserPreferenceConfig.getAllowedTypesForReleaseCriteria().contains(sType))) {
                mAttributeValues.put(PreferenceConstants.Attribute.STRUCTURED_RELEASE_CRITERIA_REQUIRED.getName(context), sReleaseCriteriaPrefVal);
            }
            if (UIUtil.isNotNullAndNotEmpty(sClassPrefVal) &&
                    (dsmUserPreferenceConfig.getAllowedTypesForClass().contains(sType))) {
                mAttributeValues.put(pgV3Constants.ATTRIBUTE_PGCLASS, sClassPrefVal);
            }
            if (UIUtil.isNotNullAndNotEmpty(sPackagingMaterialTypePrefVal) &&
                    dsmUserPreferenceConfig.getAllowedTypesForPackagingMaterial().contains(sType)) {
                String pgToPackMaterialType = PropertyUtil.getSchemaProperty(context, "relationship_pgToPackMaterialType");
                String sPackagingMaterialTypePreferredID = diPreference.getPackagingMaterialTypePreferredID();
                String policy = domainObject.getInfo(context, DomainConstants.SELECT_POLICY);
                if (UIUtil.isNotNullAndNotEmpty(sPackagingMaterialTypePreferredID) && pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(policy)) {
                    connectPickListObject(context, domainObject, sPackagingMaterialTypePreferredID, pgToPackMaterialType);
                } else {
                    mAttributeValues.put(pgV3Constants.ATTRIBUTE_PGPACKAGINGMATERIALTYPE, sPackagingMaterialTypePrefVal);
                }
            }
            if (UIUtil.isNotNullAndNotEmpty(sReportedFuncPrefVal) && (sPartTypePrefVal.equals(sType))) {
                isConnectReportFunction = true;
            }
            if (UIUtil.isNotNullAndNotEmpty(sPartTypePrefVal)
                    && PreferenceConstants.Type.TYPE_FORMULATION.getName(context).equalsIgnoreCase(sPartTypePrefVal)
                    && pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sType)) {
                isConnectReportFunction = true;
            }

            if (isConnectReportFunction) {
                connectPickListObject(context, domainObject, sReportedFuncPrefVal, pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION);
            }

            if (!mAttributeValues.isEmpty()) {
                domainObject.setAttributeValues(context, mAttributeValues);
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: ", e);
            throw e;
        }
    }

    /**
     * @param context
     * @param sType
     * @param domainObject
     * @param sPartTypePrefVal
     * @param sMaturityStatusPrefVal
     * @param sPhasePrefVal
     * @return
     * @throws Exception
     */
    public Map setManufacturingStatus(Context context, String sType, DomainObject domainObject, String sPartTypePrefVal, String sMaturityStatusPrefVal, String sPhasePrefVal) throws Exception {
        Map mAttributeValues = new HashMap();
        String getPhaseFromParent = DomainConstants.EMPTY_STRING;
        try {
            if (UIUtil.isNotNullAndNotEmpty(sPartTypePrefVal) &&
                    (sType.equals(sPartTypePrefVal) && (!domainObject.isKindOf(context, PreferenceConstants.Type.TYPE_CHANGE_ACTION.getName(context)))) &&
                    UIUtil.isNotNullAndNotEmpty(sMaturityStatusPrefVal) &&
                    UIUtil.isNotNullAndNotEmpty(sPhasePrefVal)) {
                String phase = domainObject.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
                if (UIUtil.isNotNullAndNotEmpty(phase) && (sPhasePrefVal.equals(phase))) {
                    mAttributeValues.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, sMaturityStatusPrefVal);
                }
            } else if (sType.equals(pgV3Constants.TYPE_FORMULATIONPART)) {
                getPhaseFromParent = "to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE;
                String sFormulationPhase = domainObject.getInfo(context, getPhaseFromParent);
                if (UIUtil.isNotNullAndNotEmpty(sFormulationPhase) && sPhasePrefVal.equals(sFormulationPhase)) {
                    mAttributeValues.put(pgV3Constants.STR_RELEASE_PHASE, sFormulationPhase);
                    mAttributeValues.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, sMaturityStatusPrefVal);
                }
            } else if (sType.equals(pgV3Constants.TYPE_FORMULATIONPROCESS)) {
                getPhaseFromParent = "to[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].from." + pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE;
                String sFormulationPartPhase = domainObject.getInfo(context, getPhaseFromParent);
                if (UIUtil.isNotNullAndNotEmpty(sFormulationPartPhase) && sPhasePrefVal.equals(sFormulationPartPhase)) {
                    mAttributeValues.put(pgV3Constants.STR_RELEASE_PHASE, sFormulationPartPhase);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.INFO, "Error occured while executing method pgIPMUtil_Deferred:setManufacturingStatus: ", ex);
        }
        return mAttributeValues;
    }

    /**
     * @param context
     * @param domainObject
     * @param sToSideObject
     * @param relRelationship
     * @throws Exception
     */
    private void connectPickListObject(Context context, DomainObject domainObject, String sToSideObject, String relRelationship) throws Exception {
        String existingReportedFunctionConnectionId = domainObject.getInfo(context, "from[" + relRelationship + "].id");
        if (UIUtil.isNotNullAndNotEmpty(existingReportedFunctionConnectionId)) {
            DomainRelationship.disconnect(context, existingReportedFunctionConnectionId);
        }
        DomainRelationship.connect(context, domainObject, relRelationship, DomainObject.newInstance(context, sToSideObject));
    }


    /**
     * @param context
     * @param sType
     * @param sObjectId
     * @param dsmUserPreferenceConfig
     * @throws Exception
     */
    private void setDefaultPartPreferences(Context context, String sType, String sObjectId, DSMUserPreferenceConfig dsmUserPreferenceConfig) throws Exception {
        try {
            String policyIPMSpec = PropertyUtil.getSchemaProperty(context, "policy_IPMSpecification");
            DomainObject domainObject = DomainObject.newInstance(context, sObjectId);
            String policy = domainObject.getInfo(context, DomainConstants.SELECT_POLICY);

            if (UIUtil.isNotNullAndNotEmpty(policy) &&
                    (pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(policy) ||
                            pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equals(policy))) {
                String allowedTypesForPlantConnectionForEquivalent = dsmUserPreferenceConfig.getAllowedTypesForPlantConnectionForEquivalent();
                String allowedTypesForShareWithMembersEquivalent = dsmUserPreferenceConfig.getAllowedTypesForShareWithMembersForEquivalent();
                if (UIUtil.isNotNullAndNotEmpty(allowedTypesForShareWithMembersEquivalent)) {
                    setPlantAndShareWithMemberPreferences(context, sType, sObjectId, allowedTypesForPlantConnectionForEquivalent, allowedTypesForShareWithMembersEquivalent);
                }
            } else if (UIUtil.isNotNullAndNotEmpty(policy) &&
                    (policyIPMSpec.equals(policy))) {
                String allowedTypesForPlantConnectionForTechSpec = dsmUserPreferenceConfig.getAllowedTypesForPlantConnectionForTechSpec();
                String allowedTypesForShareWithMembersTechSpec = dsmUserPreferenceConfig.getAllowedTypesForShareWithMembersForTechSpec();
                if (UIUtil.isNotNullAndNotEmpty(allowedTypesForPlantConnectionForTechSpec) &&
                        UIUtil.isNotNullAndNotEmpty(allowedTypesForShareWithMembersTechSpec)) {
                    setPlantAndShareWithMemberPreferences(context, sType, sObjectId, allowedTypesForPlantConnectionForTechSpec, allowedTypesForShareWithMembersTechSpec);
                }

            } else {
                String allowedTypesForPlantConnectionForProductPart = dsmUserPreferenceConfig.getAllowedTypesForPlantConnectionForProductPart();
                String allowedTypesForShareWithMembersProductPart = dsmUserPreferenceConfig.getAllowedTypesForShareWithMembersForProductPart();
                if (UIUtil.isNotNullAndNotEmpty(allowedTypesForPlantConnectionForProductPart) &&
                        UIUtil.isNotNullAndNotEmpty(allowedTypesForShareWithMembersProductPart)) {
                    setPlantAndShareWithMemberPreferences(context, sType, sObjectId, allowedTypesForPlantConnectionForProductPart, allowedTypesForShareWithMembersProductPart);
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: ", e);
            throw e;
        }
    }

    /**
     * @param context
     * @param sType
     * @param sObjectId
     * @throws Exception
     */
    private void setPlantAndShareWithMemberPreferences(Context context, String sType, String sObjectId, String allowedTypesForPlantConnection, String allowedTypesForShareWithMembers) throws Exception {
        try {
            DefaultCreatePartTypePreference defaultCreatePartTypePreference = new DefaultCreatePartTypePreference(context);
            String preferencePlantsId = defaultCreatePartTypePreference.getPreferredDefaultPlantID();
            if (UIUtil.isNotNullAndNotEmpty(preferencePlantsId) && (allowedTypesForPlantConnection.contains(sType))) {
                StringList slPlantIdList = FrameworkUtil.split(preferencePlantsId, PreferenceConstants.Basic.SYMBOL_PIPE.get());
                if (!slPlantIdList.isEmpty()) {
                    String sPlantsId;
                    for (int i = 0; i < slPlantIdList.size(); i++) {
                        sPlantsId = (String) slPlantIdList.get(i);
                        populatePreferencesOnBusinessApp(context, pgV3Constants.TYPE_PLANT, sPlantsId, sObjectId, sType);
                    }
                }
            }
            String preferenceShareWithMembersId = defaultCreatePartTypePreference.getPreferredDefaultSharingMemberID();
            if (UIUtil.isNotNullAndNotEmpty(preferenceShareWithMembersId) && (allowedTypesForShareWithMembers.contains(sType))) {
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                userPreferenceUtil.applySharingMemberPreferences(context, sObjectId, preferenceShareWithMembersId);
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: ", e);
            throw e;
        }
    }


    /**
     * @param context
     * @param sPreferenceType
     * @param sPreferenceValueOID
     * @param sObjectId
     * @param sType
     * @throws Exception
     */
    private void populatePreferencesOnBusinessApp(Context context, String sPreferenceType, String sPreferenceValueOID, String sObjectId, String sType) throws Exception {
        try {
            if (pgV3Constants.TYPE_PLANT.equals(sPreferenceType)) {
                if (UIUtil.isNotNullAndNotEmpty(sPreferenceValueOID) && UIUtil.isNotNullAndNotEmpty(sObjectId)) {
                    DomainRelationship domRel = DomainRelationship.connect(context, DomainObject.newInstance(context, sPreferenceValueOID), DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, DomainObject.newInstance(context, sObjectId));
                    setPlantRelAttributeValue(context, domRel, sType);
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: ", e);
            throw e;
        }
    }

    /**
     * @param context
     * @param domRel
     * @param sType
     * @throws Exception
     */
    private void setPlantRelAttributeValue(Context context, DomainRelationship domRel, String sType) throws Exception {
        try {
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            DSMUserPreferenceConfig dsmUserPreferenceConfig = userPreferenceUtil.getDSMUserPreferenceConfig(context);
            String allowedTypesAndDefaultValueForAuthorizedToUse = dsmUserPreferenceConfig.getAllowedTypesAndDefaultValueForAuthorizedToUse();
            if (UIUtil.isNotNullAndNotEmpty(allowedTypesAndDefaultValueForAuthorizedToUse)) {
                String attributeName = PreferenceConstants.Attribute.AUTHORIZED_TO_USE.getName(context);
                setPlantRelAttributeValueForSpecificType(context, domRel, sType, allowedTypesAndDefaultValueForAuthorizedToUse, attributeName);
            }
            String allowedTypesAndDefaultValueForAuthorizedToProduce = dsmUserPreferenceConfig.getAllowedTypesAndDefaultValueForAuthorizedToProduce();
            if (UIUtil.isNotNullAndNotEmpty(allowedTypesAndDefaultValueForAuthorizedToProduce)) {
                String attributeName = PreferenceConstants.Attribute.AUTHORIZED_TO_PRODUCE.getName(context);
                setPlantRelAttributeValueForSpecificType(context, domRel, sType, allowedTypesAndDefaultValueForAuthorizedToProduce, attributeName);
            }
            String allowedTypesAndDefaultValueForAuthorized = dsmUserPreferenceConfig.getAllowedTypesAndDefaultValueForAuthorized();
            if (UIUtil.isNotNullAndNotEmpty(allowedTypesAndDefaultValueForAuthorized)) {
                String attributeName = PreferenceConstants.Attribute.AUTHORIZED_TO_VIEW.getName(context);
                setPlantRelAttributeValueForSpecificType(context, domRel, sType, allowedTypesAndDefaultValueForAuthorized, attributeName);
            }
            String allowedTypesAndDefaultValueForActivated = dsmUserPreferenceConfig.getAllowedTypesAndDefaultValueForActivated();
            if (UIUtil.isNotNullAndNotEmpty(allowedTypesAndDefaultValueForActivated)) {
                String attributeName = PreferenceConstants.Attribute.IS_ACTIVATED.getName(context);
                setPlantRelAttributeValueForSpecificType(context, domRel, sType, allowedTypesAndDefaultValueForActivated, attributeName);
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: ", e);
            throw e;
        }
    }


    /**
     * @param context
     * @param domRel
     * @param sType
     * @param allowedTypesAndDefaultValue
     * @param attributeName
     * @throws Exception
     */
    private void setPlantRelAttributeValueForSpecificType(Context context, DomainRelationship domRel, String sType, String allowedTypesAndDefaultValue, String attributeName) throws Exception {
        try {
            StringList configList = FrameworkUtil.split(allowedTypesAndDefaultValue, PreferenceConstants.Basic.SYMBOL_PIPE.get());
            if (null != configList && !configList.isEmpty()) {
                StringList typeValue;
                String type;
                String value;
                for (String config : configList) {
                    if (UIUtil.isNotNullAndNotEmpty(config)) {
                        typeValue = StringUtil.split(config, PreferenceConstants.Basic.SYMBOL_TILDE.get());
                        if (null != typeValue && !typeValue.isEmpty()) {
                            type = PropertyUtil.getSchemaProperty(context, typeValue.get(0));
                            value = typeValue.get(1);
                            if (UIUtil.isNotNullAndNotEmpty(type) && type.equals(sType)) {
                                domRel.setAttributeValue(context, attributeName, value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: ", e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws FrameworkException
     */
    public void isCATIADesignerPart(Context context, String[] args) throws FrameworkException {
        String objectOid = args[0];
        if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
            UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
            DSMUserPreferenceConfig config = preferenceUtil.getDSMUserPreferenceConfig(context);
            boolean isCATPart = isCATIADesignerPart(context, objectOid, config.getSkipPartViaDesigner());
            logger.log(Level.INFO, "Is CATIA Designer Part: " + isCATPart);
        }
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    private boolean isCATIADesignerPart(Context context, String objectOid, DSMUserPreferenceConfig dsmUserPreferenceConfig) throws FrameworkException {
        return isCATIADesignerPart(context, objectOid, dsmUserPreferenceConfig.getSkipPartViaDesigner());
    }

    /**
     * @param context
     * @param objectOid
     * @param catiaPartConfig
     * @return
     * @throws FrameworkException
     */
    private boolean isCATIADesignerPart(Context context, String objectOid, String catiaPartConfig) throws FrameworkException {
        boolean isDesignerPart = Boolean.FALSE;
        if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
            DomainObject domainObject = DomainObject.newInstance(context, objectOid);
            String incomingType = domainObject.getInfo(context, DomainObject.SELECT_TYPE);
            StringList configList = StringUtil.split(catiaPartConfig, PreferenceConstants.Basic.SYMBOL_PIPE.get());
            if (null != configList && !configList.isEmpty()) {
                StringList allConfig;
                String fromTypes;
                String relationship;
                String toType;
                StringList fromTypeList;
                for (String config : configList) {
                    if (UIUtil.isNotNullAndNotEmpty(config)) {
                        allConfig = StringUtil.split(config, PreferenceConstants.Basic.SYMBOL_COLON.get());
                        if (null != allConfig && allConfig.size() > 2) {
                            fromTypes = allConfig.get(0);
                            relationship = allConfig.get(1);
                            toType = allConfig.get(2);
                            fromTypeList = StringUtil.split(fromTypes, PreferenceConstants.Basic.SYMBOL_TILDE.get());
                            if (null != fromTypeList && !fromTypeList.isEmpty()) {
                                for (String fromType : fromTypeList) {
                                    if (UIUtil.isNotNullAndNotEmpty(fromType)
                                            && UIUtil.isNotNullAndNotEmpty(toType)
                                            && UIUtil.isNotNullAndNotEmpty(relationship)) {
                                        fromType = PropertyUtil.getSchemaProperty(context, fromType);
                                        if (fromType.equalsIgnoreCase(incomingType)) {
                                            logger.log(Level.INFO, "Incoming type: " + incomingType);
                                            logger.log(Level.INFO, "Configured from-type is equal to incoming-type");
                                            if (hasRelatedVPMReference(context, domainObject, relationship, toType)) {
                                                isDesignerPart = Boolean.TRUE;
                                                logger.log(Level.INFO, "It is a CATIA Designer Part - User Preferences setting values must not be updated.");
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                logger.log(Level.WARNING, "Incorrect From Type configuration for CATIA Designer Part on user preference config object pgDSMUserPreferencesConfig (attribute_pgUserPrefSettingSkipPartViaDesigner)");
                            }
                        } else {
                            logger.log(Level.WARNING, "Incorrect configuration for CATIA Designer Part on user preference config object pgDSMUserPreferencesConfig (attribute_pgUserPrefSettingSkipPartViaDesigner)");
                        }
                    }
                }
            } else {
                logger.log(Level.WARNING, "No configuration for CATIA Designer Part on user preference config object pgDSMUserPreferencesConfig (attribute_pgUserPrefSettingSkipPartViaDesigner)");
            }
        }
        return isDesignerPart;
    }

    /**
     * @param context
     * @param domainObject
     * @param relationship
     * @param toType
     * @return
     * @throws FrameworkException
     */
    private boolean hasRelatedVPMReference(Context context, DomainObject domainObject, String relationship, String toType) throws FrameworkException {
        boolean hasVPMReference = Boolean.FALSE;
        MapList objectList = domainObject.getRelatedObjects(context, // context
                PropertyUtil.getSchemaProperty(context, relationship), // relationship
                PropertyUtil.getSchemaProperty(context, toType), // type
                StringList.create(DomainConstants.SELECT_ID), // object selects
                DomainConstants.EMPTY_STRINGLIST, // rel selects
                Boolean.FALSE, // to side
                Boolean.TRUE, // from side
                (short) 1, // recurse level
                DomainConstants.EMPTY_STRING, // object where clause
                DomainConstants.EMPTY_STRING, // rel where clause
                1); // limit
        if (null != objectList && !objectList.isEmpty()) { // if at-least one object is connected.
            hasVPMReference = Boolean.TRUE;
        } else {
            logger.log(Level.INFO, "Does not have related VPMReference object.");
        }
        return hasVPMReference;
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void setSharingMembersPreferencesForArtWorkCreation(Context context, String[] args) throws Exception {
        Instant startTime = Instant.now();
        String sPersonId = DomainConstants.EMPTY_STRING;
        if (null != args) {
            String sObjectId = args[0];
            String sType = args[1];
            if (UIUtil.isNotNullAndNotEmpty(sType)
                    && UIUtil.isNotNullAndNotEmpty(sObjectId)
                    && pgV3Constants.TYPE_PGARTWORK.equals(sType)) {
                try {
                    DefaultCreatePartTypePreference defaultCreatePartTypePreference = new DefaultCreatePartTypePreference(context);
                    String preferenceShareWithMembersId = defaultCreatePartTypePreference.getPreferredDefaultSharingMemberID();
                    if (UIUtil.isNotNullAndNotEmpty(preferenceShareWithMembersId)) {
                        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                        userPreferenceUtil.applySharingMemberPreferences(context, sObjectId, preferenceShareWithMembersId);
                    }
                } catch (Exception e) {
                    logger.log(Level.INFO, "Error: ", e);
                    throw e;
                }
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info("User Preferences sharing with member on Art work - Part Update - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        }

    }
	
	//Added by DSM (Sogeti) for 22x CW-06 Requirement#47860 - Start
	
	/**
     * Added by IRM (DSM) Sogeti for 2022x-06 Dec CW - Requirement 57860
     *
     * @param context
     * @param args
     * @throws Exception
     */
	
	public void propagateUPTAttributesOnRevisePart(Context context, String[] args) throws Exception {
        Instant startTime = Instant.now();
        if (null != args) {
            String objectType = args[0];
            String objectOid = args[1];
            if (UIUtil.isNotNullAndNotEmpty(objectType) && UIUtil.isNotNullAndNotEmpty(objectOid)) {
                logger.log(Level.INFO, "Set Preference on Incoming Object > | {0} | {1} |", new Object[]{objectType, objectOid});
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                DSMUserPreferenceConfig dsmUserPreferenceConfig = userPreferenceUtil.getDSMUserPreferenceConfig(context);
                if (!isCATIADesignerPart(context, objectOid, dsmUserPreferenceConfig)) {
                    userPreferenceUtil.updateUPTAttrOnReviseCopyProdData(context, args);
                }
            }
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("User Preferences - Part Update - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }
	//Added by DSM (Sogeti) for 22x CW-06 Requirement#47860 - End
}
