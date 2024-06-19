package com.pg.dsm.preference.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.enovia.changeaction.impl.ChangeActionUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.config.xml.DefaultActivated;
import com.pg.dsm.preference.config.xml.DefaultAuthorizedToProduce;
import com.pg.dsm.preference.config.xml.DefaultAuthorizedToUse;
import com.pg.dsm.preference.config.xml.DefaultAuthorizedToView;
import com.pg.dsm.preference.config.xml.DefaultCreateChangeAction;
import com.pg.dsm.preference.config.xml.DefaultCreatePartFamily;
import com.pg.dsm.preference.config.xml.DefaultPlant;
import com.pg.dsm.preference.config.xml.DefaultPlantAttribute;
import com.pg.dsm.preference.config.xml.DefaultProductPart;
import com.pg.dsm.preference.config.xml.DefaultSharingMember;
import com.pg.dsm.preference.config.xml.DefaultTechnicalSpecPart;
import com.pg.dsm.preference.config.xml.PreferenceConfig;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class DefaultCreatePartPreferenceSetting {
    private static final Logger logger = Logger.getLogger(DefaultCreatePartPreferenceSetting.class.getName());
    Context context;
    PreferenceConfig preferenceConfig;

    public DefaultCreatePartPreferenceSetting(Context context, PreferenceConfig preferenceConfig) {
        this.context = context;
        this.preferenceConfig = preferenceConfig;
    }

    /**
     * @param objectType
     * @param objectOid
     * @throws Exception
     */
    public void apply(String objectType, String objectOid, String symbolicType, String objectPolicy) throws Exception {
        DomainObject domainObject = DomainObject.newInstance(this.context, objectOid);
        this.applyDefaultCreateTechnicalSpecification(domainObject, objectOid, objectPolicy, symbolicType);
        this.applyDefaultCreateProduct(domainObject, objectOid, symbolicType, objectPolicy);
        this.applyDefaultCreateChangeAction(domainObject, objectOid, symbolicType);
    }

    /**
     * @param domainObject
     * @param objectOid
     * @param objectPolicy
     * @param symbolicType
     * @throws Exception
     */
    private void applyDefaultCreateTechnicalSpecification(DomainObject domainObject, String objectOid, String objectPolicy, String symbolicType) throws Exception {
        if (isTechnicalSpecAllowedPolicy(objectPolicy)) { // all technical spec types have the same policy.
            DefaultTechnicalSpecPart defaultTechnicalSpecPart = this.preferenceConfig.getDefaultCreatePartConfig().getDefaultTechnicalSpecPart();
            DefaultPlant defaultPlant = defaultTechnicalSpecPart.getDefaultPlant();
            DefaultSharingMember defaultSharingMember = defaultTechnicalSpecPart.getDefaultSharingMember();
            this.applyDefaultPlantAndSharingMembers(defaultPlant, defaultSharingMember, domainObject, objectOid, symbolicType, objectPolicy);
        }
    }

    boolean isTechnicalSpecAllowedPolicy(String objectPolicy) throws FrameworkException {
        String policySymbolic = FrameworkUtil.getAliasForAdmin(this.context, DomainConstants.SELECT_POLICY, objectPolicy, true);
        String allowedPolicies = this.preferenceConfig.getDefaultCreatePartConfig().getDefaultTechnicalSpecPart().getDefaultSharingMember().getAllowedPolicies();
        return StringUtil.split(allowedPolicies, pgV3Constants.SYMBOL_COMMA).contains(policySymbolic);
    }

    /**
     * @param domainObject
     * @param objectOid
     * @param symbolicType
     * @throws Exception
     */
    private void applyDefaultCreateProduct(DomainObject domainObject, String objectOid, String symbolicType, String objectPolicy) throws Exception {
        DefaultProductPart defaultProductPart = this.preferenceConfig.getDefaultCreatePartConfig().getDefaultProductPart();
        String types = defaultProductPart.getTypes();
        if (StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) { // if allowed type (either Product or Packaging)
            DefaultPlant defaultPlant = defaultProductPart.getDefaultPlant();
            DefaultSharingMember defaultSharingMember = defaultProductPart.getDefaultSharingMember();
            this.applyDefaultPlantAndSharingMembers(defaultPlant, defaultSharingMember, domainObject, objectOid, symbolicType, objectPolicy);
        }
    }

    /**
     * @param domainObject
     * @param objectOid
     * @param symbolicType
     * @throws Exception
     */
    private void applyDefaultCreateChangeAction(DomainObject domainObject, String objectOid, String symbolicType) throws Exception {
        // applying sharing member on CA (when Change Template on Preference is Fast Track).
        DefaultCreateChangeAction defaultCreateChangeAction = this.preferenceConfig.getDefaultCreateChangeAction();
        String types = defaultCreateChangeAction.getTypes();
        if (StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) { // if allowed type (Change Action)
            DefaultSharingMember defaultSharingMember = defaultCreateChangeAction.getDefaultSharingMember();
            this.applyDefaultSharingMemberOnly(defaultSharingMember, domainObject, objectOid, symbolicType);
        }
    }

    /**
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public boolean isFastTrackOrStandaloneCA(DomainObject domainObject) throws FrameworkException {
        boolean apply = Boolean.FALSE;
        String selectChangeOrderID = "to[" + ChangeActionUtil.getRel_ChangeOrder(this.context) + "].from[" + ChangeActionUtil.getType_CO(this.context) + "].id";
        String changeOrderID = domainObject.getInfo(this.context, selectChangeOrderID);
        if (UIUtil.isNotNullAndNotEmpty(changeOrderID)) { // when CA is created with CO.
            DomainObject changeOrder = DomainObject.newInstance(this.context, changeOrderID);
            String selectChangeTemplateDefaultPolicy = "to[Change Instance].from.attribute[Default Policy]";
            String changeTemplateDefaultPolicy = changeOrder.getInfo(this.context, selectChangeTemplateDefaultPolicy);
            if (PreferenceConstants.Basic.FAST_TRACK_CHANGE.get().equalsIgnoreCase(changeTemplateDefaultPolicy)) { // if CO's connected Change Template is Fast Track.
                apply = Boolean.TRUE;
                logger.log(Level.INFO, "It is fast track change");
            }
        } else {
            apply = Boolean.TRUE; // when CA is created w/o CO.
            logger.log(Level.INFO, "It is Standalone CA without CO");
        }
        return apply;
    }

    /**
     * @return
     * @throws MatrixException
     */
    public boolean isChangeTemplateFastTrack() throws MatrixException {
        boolean isFastTrack = Boolean.FALSE;
        String changeTemplateID = Preferences.ChangeActionPreference.CHANGE_TEMPLATE.getID(this.context);
        if (UIUtil.isNotNullAndNotEmpty(changeTemplateID)) {
            logger.log(Level.INFO, "Change Template from Preference: " + changeTemplateID);
            String selectDefaultPolicy = DomainObject.getAttributeSelect(PreferenceConstants.Attribute.DEFAULT_POLICY.getName(this.context));
            DomainObject changeTemplateObj = DomainObject.newInstance(this.context, changeTemplateID);
            String defaultPolicy = changeTemplateObj.getInfo(this.context, selectDefaultPolicy);
            if (PreferenceConstants.Basic.FAST_TRACK_CHANGE.get().equalsIgnoreCase(defaultPolicy)) {
                isFastTrack = Boolean.TRUE;
            }
        }
        return isFastTrack;
    }

    /**
     * @param domainObject
     * @param objectOid
     * @param symbolicType
     * @throws Exception
     */
    private void applyDefaultCreatePartFamily(DomainObject domainObject, String objectOid, String symbolicType) throws Exception {
        DefaultCreatePartFamily defaultCreatePartFamily = this.preferenceConfig.getDefaultCreatePartFamily();
        String types = defaultCreatePartFamily.getTypes();
        if (StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) { // if allowed type (Part Family)
            logger.log(Level.INFO, "Incoming type is of Part Family");
            DefaultSharingMember defaultSharingMember = defaultCreatePartFamily.getDefaultSharingMember();
            // get members which are already given access.
            this.applyDefaultSharingMemberOnly(defaultSharingMember, domainObject, objectOid, symbolicType);
        }
    }


    /**
     * @param defaultSharingMember
     * @param domainObject
     * @param objectOid
     * @param symbolicType
     * @throws Exception
     */
    public void applyDefaultSharingMemberOnly(DefaultSharingMember defaultSharingMember, DomainObject domainObject, String objectOid, String symbolicType) throws Exception {
        try {
            String sharingMemberIDs = Preferences.DefaultCreatePartPreference.DEFAULT_SHARING_MEMBERS.getID(this.context);
            if (UIUtil.isNotNullAndNotEmpty(sharingMemberIDs)) {
                if (StringUtil.split(defaultSharingMember.getAllowedTypes(), pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                    this.applyDefaultSharingMembers(defaultSharingMember, objectOid, symbolicType, sharingMemberIDs); // apply share with members from preferences.
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error Applying Default Plant & Sharing Member Preference:" + e);
            throw e;
        }
    }

    /**
     * @param defaultPlant
     * @param defaultSharingMember
     * @param domainObject
     * @param objectOid
     * @param symbolicType
     * @throws Exception
     */
    private void applyDefaultPlantAndSharingMembers(DefaultPlant defaultPlant, DefaultSharingMember defaultSharingMember, DomainObject domainObject, String objectOid, String symbolicType, String objectPolicy) throws Exception {
        try {
            String preferredDefaultPlantIDs = Preferences.DefaultCreatePartPreference.DEFAULT_PLANTS.getID(this.context);
            if (UIUtil.isNotNullAndNotEmpty(preferredDefaultPlantIDs)) {
                if (!isEquivalents(objectPolicy)) {
                    if (StringUtil.split(defaultPlant.getAllowedTypes(), pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) { // if allowed types (for plants)
                        this.applyDefaultPlants(domainObject, defaultPlant, symbolicType, preferredDefaultPlantIDs); // apply plants from preferences
                    }
                }
            }
            String sharingMemberIDs = Preferences.DefaultCreatePartPreference.DEFAULT_SHARING_MEMBERS.getID(this.context);
            if (UIUtil.isNotNullAndNotEmpty(sharingMemberIDs)) {
                if (StringUtil.split(defaultSharingMember.getAllowedTypes(), pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) {
                    this.applyDefaultSharingMembers(defaultSharingMember, objectOid, symbolicType, sharingMemberIDs); // apply share with members from preferences.
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error Applying Default Plant & Sharing Member Preference:" + e);
            throw e;
        }
    }

    /**
     * @param domainObject
     * @param defaultPlant
     * @param symbolicType
     * @throws MatrixException
     */
    private void applyDefaultPlants(DomainObject domainObject, DefaultPlant defaultPlant, String symbolicType, String preferredDefaultPlantIDs) throws
            MatrixException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(preferredDefaultPlantIDs)) { // if user has set plants in the preference.
                Map<String, String> attributeMap = new HashMap<>();
                // authorized to view
                DefaultAuthorizedToView defaultAuthorizedToView = defaultPlant.getDefaultAuthorizedToView();
                List<DefaultPlantAttribute> defaultPlantAttributes = defaultAuthorizedToView.getDefaultPlantAttributes();
                Optional<DefaultPlantAttribute> defaultPlantAttributeOptional = defaultPlantAttributes.stream().filter(defaultPlantAttribute -> symbolicType.equalsIgnoreCase(defaultPlantAttribute.getType())).findFirst();
                if (defaultPlantAttributeOptional.isPresent()) {
                    DefaultPlantAttribute defaultPlantAttribute = defaultPlantAttributeOptional.get();
                    attributeMap.put(PropertyUtil.getSchemaProperty(this.context, defaultAuthorizedToView.getName()), defaultPlantAttribute.getValue());
                }

                // authorized to produce
                DefaultAuthorizedToProduce defaultAuthorizedToProduce = defaultPlant.getDefaultAuthorizedToProduce();
                defaultPlantAttributes = defaultAuthorizedToProduce.getDefaultPlantAttributes();
                defaultPlantAttributeOptional = defaultPlantAttributes.stream().filter(defaultPlantAttribute -> symbolicType.equalsIgnoreCase(defaultPlantAttribute.getType())).findFirst();
                if (defaultPlantAttributeOptional.isPresent()) {
                    DefaultPlantAttribute defaultPlantAttribute = defaultPlantAttributeOptional.get();
                    attributeMap.put(PropertyUtil.getSchemaProperty(this.context, defaultAuthorizedToProduce.getName()), defaultPlantAttribute.getValue());
                }

                // authorized to use
                DefaultAuthorizedToUse defaultAuthorizedToUse = defaultPlant.getDefaultAuthorizedToUse();
                defaultPlantAttributes = defaultAuthorizedToUse.getDefaultPlantAttributes();
                defaultPlantAttributeOptional = defaultPlantAttributes.stream().filter(defaultPlantAttribute -> symbolicType.equalsIgnoreCase(defaultPlantAttribute.getType())).findFirst();
                if (defaultPlantAttributeOptional.isPresent()) {
                    DefaultPlantAttribute defaultPlantAttribute = defaultPlantAttributeOptional.get();
                    attributeMap.put(PropertyUtil.getSchemaProperty(this.context, defaultAuthorizedToUse.getName()), defaultPlantAttribute.getValue());
                }

                // activated
                DefaultActivated defaultActivated = defaultPlant.getDefaultActivated();
                defaultPlantAttributes = defaultActivated.getDefaultPlantAttributes();
                defaultPlantAttributeOptional = defaultPlantAttributes.stream().filter(defaultPlantAttribute -> symbolicType.equalsIgnoreCase(defaultPlantAttribute.getType())).findFirst();
                if (defaultPlantAttributeOptional.isPresent()) {
                    DefaultPlantAttribute defaultPlantAttribute = defaultPlantAttributeOptional.get();
                    attributeMap.put(PropertyUtil.getSchemaProperty(this.context, defaultActivated.getName()), defaultPlantAttribute.getValue());
                }
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                logger.info("_______________ - Attach Plant Start - _______________");
                boolean disconnectExisting = isDisconnectPlant(domainObject);
                userPreferenceUtil.refreshRelationshipWithAttribute(this.context,
                        domainObject,
                        preferredDefaultPlantIDs,
                        userPreferenceUtil.getRelatedPlants(this.context, domainObject),
                        DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY,
                        false,
                        attributeMap,
                        disconnectExisting);
                logger.info("_______________ - Attach Plant End - _______________");
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error Applying Default Plant Preference:" + e);
            throw e;
        }
    }


    private boolean isDisconnectPlant(DomainObject domainObject) throws FrameworkException {
        String typeTechSpec = PropertyUtil.getSchemaProperty(this.context, "type_TechnicalSpecification");
        String typeTestMethodSpec = PropertyUtil.getSchemaProperty(this.context, "type_TestMethodSpecification");
        return (domainObject.isKindOf(this.context, typeTechSpec) || domainObject.isKindOf(this.context, typeTestMethodSpec));
    }

    /**
     * @param domainObject
     * @param preferencePlantIDs
     * @return
     * @throws FrameworkException
     */
    StringList getPlantsToConnect(DomainObject domainObject, String preferencePlantIDs) throws FrameworkException {
        // filter out the plants which are already connected. (for copy).
        StringList toConnectList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(preferencePlantIDs)) {
            logger.log(Level.INFO, "Plants from Preference: {0}", preferencePlantIDs);
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            StringList preferencePlantList = StringUtil.split(preferencePlantIDs, PreferenceConstants.Basic.SYMBOL_PIPE.get());
            StringList relatedPlantList = userPreferenceUtil.getRelatedPlantList(this.context, domainObject);
            logger.log(Level.INFO, "Plants which is already connected: {0}", relatedPlantList);
            if (null != relatedPlantList && !relatedPlantList.isEmpty()) {
                for (String plantID : relatedPlantList) {
                    if (!preferencePlantList.contains(plantID)) {
                        toConnectList.add(plantID);
                    }
                }
            } else {
                toConnectList.addAll(preferencePlantList);
            }
        }
        return toConnectList;
    }

    /**
     * @param defaultSharingMember
     * @param objectOid
     * @param symbolicType
     * @throws Exception
     */
    private void applyDefaultSharingMembers(DefaultSharingMember defaultSharingMember, String objectOid, String symbolicType, String sharingMemberIDs) throws Exception {
        try {
            String allowedTypes = defaultSharingMember.getAllowedTypes();
            if (StringUtil.split(allowedTypes, pgV3Constants.SYMBOL_COMMA).contains(symbolicType)) { // if incoming type is present in configured allowed types of Tech Spec.
                if (UIUtil.isNotNullAndNotEmpty(sharingMemberIDs)) { // if user has set (share with members) in preferences.
                    logger.log(Level.INFO, "Sharing Member from preference: " + sharingMemberIDs);
                    UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                    userPreferenceUtil.applySharingMemberPreferences(this.context, objectOid, sharingMemberIDs);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error Applying Default Sharing Member Preference:" + e);
            throw e;
        }
    }

    private boolean isEquivalents(String policy) {
        return (pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(policy) || pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equals(policy));
    }
}
