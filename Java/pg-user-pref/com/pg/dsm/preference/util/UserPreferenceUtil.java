package com.pg.dsm.preference.util;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.pg.dsm.preference.template.services.UserPreferenceTemplateCreateUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dassault_systemes.enovia.bom.ReleasePhase;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeAction;
import com.dassault_systemes.enovia.changeaction.interfaces.IProposedChanges;
import com.dassault_systemes.enovia.formulation.interfaces.ENOIFormulation;
import com.dassault_systemes.enovia.formulation.interfaces.factory.ENOFormulationFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.engineering.EngineeringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.config.xml.PackagingPreferenceConfig;
import com.pg.dsm.preference.config.xml.PreferenceConfig;
import com.pg.dsm.preference.config.xml.ProductPreferenceConfig;
import com.pg.dsm.preference.config.xml.RawMaterialPreferenceConfig;
import com.pg.dsm.preference.enumeration.DSMUPTConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.models.DSMUserPreferenceConfig;
import com.pg.dsm.preference.models.IRMApprovalPreference;
import com.pg.dsm.preference.models.Member;
import com.pg.dsm.preference.models.UserGroup;
import com.pg.dsm.preference.services.PreferenceConfigLoader;
import com.pg.dsm.preference.services.RouteTemplatePreferenceService;
import com.pg.dsm.preference.services.SimpleRoute;
import com.pg.dsm.preference.setting.DefaultCreatePartPreferenceSetting;
import com.pg.dsm.preference.setting.IPSecurityControlSetting;
import com.pg.dsm.preference.setting.IPSecurityControlSettingProductCopy;
import com.pg.dsm.preference.setting.PackagingPreferenceSetting;
import com.pg.dsm.preference.setting.ProductPreferenceSetting;
import com.pg.dsm.preference.setting.RawMaterialPreferenceSetting;
import com.pg.dsm.preference.template.apply.PackagingUserTemplate;
import com.pg.dsm.preference.template.apply.ProductUserTemplate;
import com.pg.dsm.preference.template.apply.RawMaterialUserTemplate;
import com.pg.dsm.preference.template.repository.CommonPickList;
import com.pg.dsm.preference.template.repository.ExplorationPickList;
import com.pg.dsm.preference.template.repository.PackagingPickList;
import com.pg.dsm.preference.template.repository.ProductPickList;
import com.pg.dsm.preference.template.repository.RawMaterialPickList;
import com.pg.dsm.preference.template.repository.TechSpecPickList;
import com.pg.dsm.preference.usage.CreateProductScreen;
import com.pg.dsm.preference.usage.ProductData;
import com.pg.v3.custom.pgV3Constants;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
import com.pg.dsm.preference.template.repository.MEPPickList;
import com.pg.dsm.preference.template.repository.SEPPickList;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.SelectConstants;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class UserPreferenceUtil {
    private static final Logger logger = Logger.getLogger(UserPreferenceUtil.class.getName());

    public UserPreferenceUtil() {
    }

    /**
     * @param context
     * @param user
     * @param key
     * @param value
     * @throws Exception
     */
    public void setUserPreference(Context context, String user, String key, String value) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(key)) {
            if (!"".equals(value)) {
                value = value.replace(PreferenceConstants.Basic.SYMBOL_PIPE.get(), PreferenceConstants.Basic.SYMBOL_SEMICOLON.get());
                PropertyUtil.setAdminProperty(context, DomainConstants.TYPE_PERSON, user, key, value);
            } else {
                PropertyUtil.removeAdminProperty(context, DomainConstants.TYPE_PERSON, user, key);
            }
        }
    }

    /**
     * @param context
     * @param key
     * @param value
     * @throws FrameworkException
     */
    public void setUserPreference(Context context, String key, String value) throws FrameworkException {
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        if (UIUtil.isNullOrEmpty(value)) {
            value = emptyKey;
        }
        if (UIUtil.isNotNullAndNotEmpty(value)) {
            PreferenceManagement prefMgmt = new PreferenceManagement(context);
            prefMgmt.setAdminPropertyWithoutCache(context, context.getUser(), key, value);
        }
    }

    public String getAdminPropertyWithoutCache(Context context, String property) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // username
                property); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    public String getAdminPropertyWithoutCache(Context context, PreferenceManagement preferenceManagement, String property) throws FrameworkException {
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // username
                property); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    public String getAdminPropertyWithoutCache(Context context, String user, String property) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                user, // username
                property); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public Map<Object, Object> getDSMUserPreferenceConfigObjectID(Context context) throws FrameworkException {
        String name = PreferenceConstants.Basic.DSM_USER_PREFERENCE_CONFIG_OBJECT_NAME.get();
        String type = pgV3Constants.TYPE_PGCONFIGURATIONADMIN;
        StringList selectList = new StringList();
        selectList.add(DomainConstants.SELECT_ID);
        selectList.add(DomainConstants.SELECT_NAME);
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PART_TYPE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PHASE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_MANUFACTURING_STATUS.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_RELEASE_CRITERIA.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PACKAGING_MATERIAL_TYPE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SEGMENT.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_CLASS.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_REPORTED_FUNCTION.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.IRM_ROUTE_TASK_DUE_DAY.getSelect(context));

        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_EQUIVALENT.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_EQUIVALENT.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_TECH_SPEC.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_TECH_SPEC.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_PLANT_CONNECTION_FOR_PRODUCT_PART.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_FOR_SHARE_WITH_MEMBERS_CONNECTION_FOR_PRODUCT_PART.getSelect(context));

        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED_TO_USE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED_TO_PRODUCE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_AUTHORIZED.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_TYPES_AND_DEFAULT_VALUE_FOR_ACTIVATED.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.SKIP_PART_VIA_DESIGNER.getSelect(context));

        selectList.add(PreferenceConstants.Attribute.ALLOWED_PART_TYPES_FOR_PACKAGING_PREFERENCE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_PART_TYPES_FOR_PRODUCT_PREFERENCE.getSelect(context));
        selectList.add(PreferenceConstants.Attribute.ALLOWED_PART_TYPES_FOR_RAW_MATERIAL_PREFERENCE.getSelect(context));

        MapList resultList = DomainObject.findObjects(context,  // context pattern
                type,                                // type pattern
                name.trim(),                         // name pattern
                DomainConstants.QUERY_WILDCARD,      // revision pattern
                DomainConstants.QUERY_WILDCARD,      // owner pattern
                pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                DomainConstants.EMPTY_STRING,        // object where clause
                false,                               // expand type
                selectList);                         // object selects
        if (null != resultList && resultList.size() > 1) {
            logger.log(Level.WARNING, "More than one config object found");
            throw new FrameworkException("More than one '".concat(type).concat("' exists with name ").concat(name));
        } else {
            return (null != resultList && !resultList.isEmpty()) ? ((Map<Object, Object>) resultList.get(0)) : new HashMap<>();
        }
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public DSMUserPreferenceConfig getDSMUserPreferenceConfig(Context context) throws FrameworkException {
        DSMUserPreferenceConfig config = null;
        Map<Object, Object> objectMap = getDSMUserPreferenceConfigObjectID(context);
        if (null != objectMap && !objectMap.isEmpty()) {
            config = new DSMUserPreferenceConfig(context, objectMap);
        }
        return config;
    }

    /**
     * @param context
     * @param type
     * @param name
     * @return
     * @throws FrameworkException
     */
    public String getObjectOid(Context context, String type, String name) throws FrameworkException {
        String id = DomainConstants.EMPTY_STRING;
        try {
            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),    // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    DomainConstants.EMPTY_STRING,           // where expression
                    false,                                  // expandType
                    StringList.create(DomainConstants.SELECT_ID));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    id = (String) ((Map) iterator.next()).get(DomainConstants.SELECT_ID);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return id;
    }

    /**
     * @param context
     * @param type
     * @param name
     * @return
     * @throws FrameworkException
     */
    public String getProductionRouteTemplate(Context context, String type, String name) throws FrameworkException {
        String id = DomainConstants.EMPTY_STRING;
        try {
            String stateProduction = PropertyUtil.getSchemaProperty(context, DomainConstants.SELECT_POLICY, DomainConstants.POLICY_ROUTE_TEMPLATE, "state_Production");
            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),                                   // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    DomainConstants.SELECT_CURRENT + "==" + stateProduction,                       // where expression
                    false,                                  // expandType
                    StringList.create(DomainConstants.SELECT_ID));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    id = (String) ((Map) iterator.next()).get(DomainConstants.SELECT_ID);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return id;
    }

    public Map<Object, Object> getPersonObjectOid(Context context, String type, String name) throws FrameworkException {
        MapList objectList = DomainObject.findObjects(context, // context
                type.trim(),                                   // typePattern
                name.trim(),                                   // name pattern
                DomainConstants.QUERY_WILDCARD,         // revision pattern
                DomainConstants.QUERY_WILDCARD,         // owner pattern
                pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                DomainConstants.EMPTY_STRING,           // where expression
                false,                                  // expandType
                StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, pgV3Constants.SELECT_ATTRIBUTE_FIRSTNAME, pgV3Constants.SELECT_ATTRIBUTE_LASTNAME));// objectSelects

        if (null != objectList && objectList.size() > 1) {
            logger.log(Level.WARNING, "More than one config object found");
            throw new FrameworkException("More than one '".concat(type).concat("' exists with name ").concat(name));
        } else {
            return (null != objectList && !objectList.isEmpty()) ? ((Map<Object, Object>) objectList.get(0)) : new HashMap<>();
        }
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public String getObjectOid(Context context, String[] args) throws Exception {
        String id = DomainConstants.EMPTY_STRING;
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String type = (String) programMap.get(DomainConstants.SELECT_TYPE);
            String name = (String) programMap.get(DomainConstants.SELECT_NAME);

            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),                                   // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    DomainConstants.EMPTY_STRING,           // where expression
                    false,                                  // expandType
                    StringList.create(DomainConstants.SELECT_ID));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    id = (String) ((Map) iterator.next()).get(DomainConstants.SELECT_ID);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return id;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getPreferredPrimaryOrgAndChangeTemplateMap(Context context, String[] args) throws MatrixException {
        return getPreferredPrimaryOrgAndChangeTemplateMap(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getPreferredPrimaryOrgAndChangeTemplateMap(Context context) throws MatrixException {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(PreferenceConstants.Basic.CHANGE_TEMPLATE_NAME.get(), getPreferredChangeTemplateName(context));
        returnMap.put(PreferenceConstants.Basic.CHANGE_TEMPLATE_OID.get(), getPreferredChangeTemplateID(context));
        returnMap.put(PreferenceConstants.Basic.PRIMARY_ORG_NAME.get(), getPreferredPrimaryOrgName(context));
        returnMap.put(PreferenceConstants.Basic.PRIMARY_ORG_OID.get(), getPreferredPrimaryOrgID(context));
        return returnMap;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getPreferredPrimaryOrgMap(Context context, String[] args) throws MatrixException {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(PreferenceConstants.Basic.PRIMARY_ORG_NAME.get(), getPreferredPrimaryOrgName(context));
        returnMap.put(PreferenceConstants.Basic.PRIMARY_ORG_OID.get(), getPreferredPrimaryOrgID(context));
        return returnMap;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getPreferredChangeTemplateMap(Context context, String[] args) throws MatrixException {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(PreferenceConstants.Basic.CHANGE_TEMPLATE_NAME.get(), getPreferredChangeTemplateName(context));
        returnMap.put(PreferenceConstants.Basic.CHANGE_TEMPLATE_OID.get(), getPreferredChangeTemplateID(context));
        return returnMap;
    }

    /**
     * @param context
     * @param type
     * @param names
     * @return
     * @throws FrameworkException
     */
    public String getPipeSeparatedIDs(Context context, String type, String names) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(names) && UIUtil.isNotNullAndNotEmpty(type)) {
            StringList nameList = StringUtil.split(names, pgV3Constants.SYMBOL_PIPE);
            StringBuilder oidBuilder = new StringBuilder();
            String oid;
            for (String name : nameList) {
                if (UIUtil.isNotNullAndNotEmpty(name)) {
                    oid = getObjectOid(context, type, name);
                    if (UIUtil.isNotNullAndNotEmpty(oid)) {
                        oidBuilder.append(oid);
                        oidBuilder.append(pgV3Constants.SYMBOL_PIPE);
                    }
                }
            }
            if (oidBuilder.length() > 0) {
                oidBuilder.setLength(oidBuilder.length() - 1);
                ids = oidBuilder.toString();
            }
        }
        return ids;
    }

    /**
     * @param context
     * @param type
     * @param names
     * @return
     * @throws FrameworkException
     */
    public String getRouteTemplatesPipeSeparatedIDs(Context context, String type, String names) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(names) && UIUtil.isNotNullAndNotEmpty(type)) {
            StringList nameList = StringUtil.split(names, pgV3Constants.SYMBOL_PIPE);
            String oid;
            StringBuilder oidBuilder = new StringBuilder();
            for (String name : nameList) {
                if (UIUtil.isNotNullAndNotEmpty(name)) {
                    oid = getProductionRouteTemplate(context, type, name);
                    if (UIUtil.isNotNullAndNotEmpty(oid)) {
                        oidBuilder.append(oid);
                        oidBuilder.append(pgV3Constants.SYMBOL_PIPE);
                    }
                }
            }
            if (oidBuilder.length() > 0) {
                oidBuilder.setLength(oidBuilder.length() - 1);
                ids = oidBuilder.toString();
            }
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public String getPreferredPrimaryOrgID(Context context) throws MatrixException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context, // context
                    PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context), // type
                    getPreferredPrimaryOrgName(context)); // names - pipe separated
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredPrimaryOrgName(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.PRIMARY_ORGANIZATION.get());  // preference property key
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredChangeTemplateName(Context context) throws FrameworkException {
        String changeTemplateName = PropertyUtil.getAdminProperty(context,  // context
                DomainConstants.TYPE_PERSON,                    // type
                context.getUser(),                              // user name
                PreferenceConstants.Preferences.PREFERRED_CHANGE_TEMPLATE.get());          // preference property key
        return UIUtil.isNotNullAndNotEmpty(changeTemplateName) ? changeTemplateName.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }


    /**
     * @param context
     * @param args
     * @return
     * @throws MatrixException
     */
    public String getPreferredChangeTemplateID(Context context, String[] args) throws MatrixException {
        return getPreferredChangeTemplateID(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public String getPreferredChangeTemplateID(Context context) throws MatrixException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context, // context
                    PreferenceConstants.Type.TYPE_CHANGE_TEMPLATE.getName(context), // type
                    getPreferredChangeTemplateName(context)); // preference key
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * Added by DSM (Sogeti) for 2022x.01 Req #45340
     *
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public StringList includeRoutesTemplateForSelectedOrg(Context context, String[] args) throws Exception {
        Map programMap = (Map) JPO.unpackArgs(args);
        StringList includeList = new StringList();
        try {
            MapList mlMapList = new MapList();
            StringList slObjectSelects = new StringList();
            slObjectSelects.addElement(DomainConstants.SELECT_ID);
            String sPrimaryOrganization = (String) programMap.get("PrimaryOrganization");
            StringBuffer buff = new StringBuffer(64);
            buff.append("(");
            buff.append("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.name == \"");
            buff.append(sPrimaryOrganization);
            buff.append("\"");
            buff.append(")");
            String sWhereExp = buff.toString();

            mlMapList = DomainObject.findObjects(context,   // context
                    DomainConstants.TYPE_ROUTE_TEMPLATE,    // type pattern
                    DomainConstants.QUERY_WILDCARD,         // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault
                    sWhereExp,                              // where expression
                    DomainConstants.EMPTY_STRING,            // rel where
                    false,                                  // expand
                    slObjectSelects,                        // object selects
                    (short) 0);                             // limit
            Map mapListTemp;
            String sID;
            Iterator mapListItr = mlMapList.iterator();
            while (mapListItr.hasNext()) {
                mapListTemp = (Map) mapListItr.next();
                sID = (String) mapListTemp.get(DomainConstants.SELECT_ID);
                includeList.add(sID);
            }
            return includeList;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public StringList getOrgRelatedRouteTemplateIDs(Context context, String[] args) throws Exception {
        Map programMap = (Map) JPO.unpackArgs(args);
        String orgName = (String) programMap.get("PrimaryOrganization");
        String state = (String) programMap.get("vState");
        String purpose = (String) programMap.get("vPurpose");
        return getOrgRelatedRouteTemplateIDs(context, orgName, state, purpose);
    }

    /**
     * @param context
     * @param orgNames
     * @return
     * @throws FrameworkException
     */
    public StringList getOrgRelatedRouteTemplateIDs(Context context, String orgNames, String state, String purpose) throws FrameworkException {
        StringList objectList = new StringList();
        String type = PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context);
        String orgIDs = getPipeSeparatedIDs(context, type, orgNames);
        if (UIUtil.isNotNullAndNotEmpty(orgIDs)) {
            StringList orgList = StringUtil.split(orgIDs, pgV3Constants.SYMBOL_PIPE);
            if (null != orgList && !orgList.isEmpty()) {
                MapList templateList;
                for (String orgID : orgList) {
                    if (UIUtil.isNotNullAndNotEmpty(orgID)) {
                        templateList = getOrgRelatedRouteTemplates(context, orgID);
                        if (null != templateList && !templateList.isEmpty()) {
                            objectList.addAll(getOrgRelatedRouteTemplateIDs(context, templateList, state, purpose));
                        }
                    }
                }
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param templateList
     * @param state
     * @param purpose
     * @return
     */
    public StringList getOrgRelatedRouteTemplateIDs(Context context, MapList templateList, String state, String purpose) {
        StringList objectList = new StringList();
        if (null != templateList && !templateList.isEmpty()) {
            Iterator iterator = templateList.iterator();
            Map<Object, Object> objectMap;
            while (iterator.hasNext()) {
                objectMap = (Map<Object, Object>) iterator.next();
                if (state.equalsIgnoreCase((String) objectMap.get(DomainConstants.SELECT_CURRENT)) && purpose.equalsIgnoreCase((String) objectMap.get(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE)))) {
                    objectList.add((String) objectMap.get(DomainConstants.SELECT_ID));
                }
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param orgName
     * @return
     * @throws FrameworkException
     */
    public StringList getOrgRelatedRouteTemplateIDs(Context context, String orgName) throws FrameworkException {
        StringList objectList = new StringList();
        String type = PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context);
        if (UIUtil.isNotNullAndNotEmpty(orgName)) {
            String orgID = getObjectOid(context, type, orgName);
            if (UIUtil.isNotNullAndNotEmpty(orgID)) {
                MapList templateList = getOrgRelatedRouteTemplates(context, orgID);
                if (null != templateList && !templateList.isEmpty()) {
                    Iterator iterator = templateList.iterator();
                    Map<Object, Object> objectMap;
                    while (iterator.hasNext()) {
                        objectMap = (Map<Object, Object>) iterator.next();
                        objectList.add((String) objectMap.get(DomainConstants.SELECT_ID));
                    }
                }
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param orgID
     * @return
     * @throws FrameworkException
     */
    public MapList getOrgRelatedRouteTemplates(Context context, String orgID) throws FrameworkException {
        DomainObject domainObject = DomainObject.newInstance(context, orgID);
        return domainObject.getRelatedObjects(context, // context
                pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION, // relationship Pattern
                DomainConstants.TYPE_ROUTE_TEMPLATE, // type Pattern
                StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT, DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE)), // object select
                DomainConstants.EMPTY_STRINGLIST, // relationship select
                true, // getTo
                false, // getFrom
                (short) 1, // recurse level
                DomainConstants.EMPTY_STRING, // object where expression
                DomainConstants.EMPTY_STRING, // relationship where expression
                0);// limit
    }

    /**
     * Get Primary Org short code.
     *
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public String getPrimaryOrgShortCode(Context context, String[] args) throws Exception {
        Map programMap = (Map) JPO.unpackArgs(args);
        String orgID = (String) programMap.get(DomainConstants.SELECT_ID);
        DomainObject domainObject = DomainObject.newInstance(context, orgID);
        String orgCode = domainObject.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGSHORTCODE);
        return UIUtil.isNotNullAndNotEmpty(orgCode) ? orgCode : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteTemplatePrimaryOrgName(Context context) throws FrameworkException {
        String orgName = PropertyUtil.getAdminProperty(context,                                            // context
                DomainConstants.TYPE_PERSON,                        // type
                context.getUser(),                                  // user name
                PreferenceConstants.Preferences.PREFERRED_ROUTE_TEMPLATE_PRIMARY_ORG.get());     // preference property key
        return UIUtil.isNotNullAndNotEmpty(orgName) ? orgName.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteTemplatePrimaryOrgID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context, // context
                    PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context), // type
                    getPreferredRouteTemplatePrimaryOrgName(context)); // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredInWorkRouteTemplateName(Context context) throws FrameworkException {
        String orgName = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(),  // user
                PreferenceConstants.Preferences.PREFERRED_ROUTE_TEMPLATE_IN_WORK.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(orgName) ? orgName.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredInWorkRouteTemplateID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getRouteTemplatesPipeSeparatedIDs(context, // context
                    DomainConstants.TYPE_ROUTE_TEMPLATE, // type
                    getPreferredInWorkRouteTemplateName(context)); // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredInApprovalRouteTemplateName(Context context) throws FrameworkException {
        String orgName = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(orgName) ? orgName.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredInApprovalRouteTemplateID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getRouteTemplatesPipeSeparatedIDs(context,                                            // context
                    DomainConstants.TYPE_ROUTE_TEMPLATE,                // type
                    getPreferredInApprovalRouteTemplateName(context));  // names - pipe separated
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getChangeActionPreferencesMap(Context context, String[] args) throws MatrixException {
        return getChangeActionPreferencesMap(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getChangeActionPreferencesMap(Context context) throws MatrixException {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(PreferenceConstants.Basic.ROUTE_TEMPLATE_PRIMARY_ORG_NAME.get(), getPreferredRouteTemplatePrimaryOrgName(context));
        returnMap.put(PreferenceConstants.Basic.ROUTE_TEMPLATE_IN_WORK_NAME.get(), getPreferredInWorkRouteTemplateName(context));
        returnMap.put(PreferenceConstants.Basic.ROUTE_TEMPLATE_IN_APPROVAL_NAME.get(), getPreferredInApprovalRouteTemplateName(context));
        returnMap.put(PreferenceConstants.Basic.CHANGE_TEMPLATE_NAME.get(), getPreferredChangeTemplateName(context));

        returnMap.put(PreferenceConstants.Basic.ROUTE_TEMPLATE_PRIMARY_ORG_OID.get(), getPreferredRouteTemplatePrimaryOrgID(context));
        returnMap.put(PreferenceConstants.Basic.ROUTE_TEMPLATE_IN_WORK_OID.get(), getPreferredInWorkRouteTemplateID(context));
        returnMap.put(PreferenceConstants.Basic.ROUTE_TEMPLATE_IN_APPROVAL_OID.get(), getPreferredInApprovalRouteTemplateID(context));
        returnMap.put(PreferenceConstants.Basic.CHANGE_TEMPLATE_OID.get(), getPreferredChangeTemplateID(context));

        return returnMap;
    }


    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredBusinessUseClass(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(),  // user name
                PreferenceConstants.Preferences.BUSINESS_CLASS.get()); // preference key
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredBusinessUseClassID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,   // context
                    PreferenceConstants.Type.TYPE_IP_CONTROL.getName(context),      // type
                    getPreferredBusinessUseClass(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredHighlyRestrictedClass(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(),  // user name
                PreferenceConstants.Preferences.HIGHLY_RESTRICTED_CLASS.get()); // preference key
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredHighlyRestrictedClassID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,     // context
                    PreferenceConstants.Type.TYPE_IP_CONTROL.getName(context),      // type
                    getPreferredHighlyRestrictedClass(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMClassification(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IRM_PREFERRED_CLASSIFICATION.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMTitle(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user name
                PreferenceConstants.Preferences.IRM_PREFERRED_TITLE.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMDescription(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, //type
                context.getUser(), // user name
                PreferenceConstants.Preferences.IRM_PREFERRED_DESCRIPTION.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMPolicy(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(),  // user name
                PreferenceConstants.Preferences.IRM_PREFERRED_POLICY.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMRegionName(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user name
                PreferenceConstants.Preferences.IRM_PREFERRED_REGION.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMSharingMembers(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_SHARING_MEMBERS.get());
    }

    public String getPreferredIRMSharingMembers(Context context, String user) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, user, PreferenceConstants.Preferences.IRM_PREFERRED_SHARING_MEMBERS.get());
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredIRMRegionID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,                                                    // context
                    PreferenceConstants.Type.TYPE_REGION.getName(context),      // type
                    getPreferredIRMRegionName(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredIRMSharingMembersID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,                                                    // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredIRMSharingMembers(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredIRMSharingMembersID(Context context, String user) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,                                                    // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredIRMSharingMembers(context, user));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public boolean isBusinessUseClassificationSelected(Context context) throws FrameworkException {
        return (PreferenceConstants.Basic.BUSINESS_USE.get().equalsIgnoreCase(getPreferredIRMClassification(context))) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public boolean isHighlyRestrictedClassificationSelected(Context context) throws FrameworkException {
        return (PreferenceConstants.Basic.HIGHLY_RESTRICTED.get().equalsIgnoreCase(getPreferredIRMClassification(context))) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getSecurityCategoryClassificationName(Context context) throws FrameworkException {
        String securityClass = DomainConstants.EMPTY_STRING;
        if (isBusinessUseClassificationSelected(context)) {
            securityClass = getPreferredBusinessUseClass(context);
        }
        if (isHighlyRestrictedClassificationSelected(context)) {
            securityClass = getPreferredHighlyRestrictedClass(context);
        }
        return securityClass;
    }

    public String getSecurityCategoryClassificationOID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,                                                    // context
                    PreferenceConstants.Type.TYPE_IP_CONTROL_CLASS.getName(context),      // type
                    getSecurityCategoryClassificationName(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getIRMAttributePreferencesMap(Context context, String[] args) throws MatrixException {
        return getIRMAttributePreferencesMap(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getIRMAttributePreferencesMap(Context context) throws MatrixException {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(PreferenceConstants.Basic.BUSINESS_USE_CLASS.get(), getPreferredBusinessUseClass(context));
        returnMap.put(PreferenceConstants.Basic.HIGHLY_RESTRICTED_CLASS.get(), getPreferredHighlyRestrictedClass(context));
        returnMap.put(PreferenceConstants.Basic.IRM_PREFERRED_TITLE.get(), getPreferredIRMTitle(context));
        returnMap.put(PreferenceConstants.Basic.IRM_PREFERRED_DESCRIPTION.get(), getPreferredIRMDescription(context));
        returnMap.put(PreferenceConstants.Basic.IRM_PREFERRED_POLICY.get(), getPreferredIRMPolicy(context));
        returnMap.put(PreferenceConstants.Basic.IRM_PREFERRED_REGION_NAME.get(), getPreferredIRMRegionName(context));
        returnMap.put(PreferenceConstants.Basic.IRM_PREFERRED_REGION_OID.get(), getPreferredIRMRegionID(context));
        returnMap.put(PreferenceConstants.Basic.IRM_PREFERRED_CLASSIFICATION.get(), getPreferredIRMClassification(context));
        returnMap.put(PreferenceConstants.Basic.SECURITY_CATEGORY_CLASSIFICATION_NAME.get(), getSecurityCategoryClassificationName(context));
        returnMap.put(PreferenceConstants.Basic.SECURITY_CATEGORY_CLASSIFICATION_OID.get(), getSecurityCategoryClassificationOID(context));
        returnMap.put(PreferenceConstants.Basic.IS_BUSINESS_USE_CLASS.get(), String.valueOf(isBusinessUseClassificationSelected(context)));
        returnMap.put(PreferenceConstants.Basic.IS_HIGHLY_RESTRICTED_CLASS.get(), String.valueOf(isHighlyRestrictedClassificationSelected(context)));
        return returnMap;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws FrameworkException
     */
    public Map<String, String> getRouteTemplateMap(Context context, String[] args) throws FrameworkException {
        return getRouteTemplateMap(context);
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public Map<String, String> getRouteTemplateMap(Context context) throws FrameworkException {
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put(PreferenceConstants.Basic.PREFERRED_ROUTE_TEMPLATE_IN_WORK_NAME.get(), getPreferredInWorkRouteTemplateName(context));
        returnMap.put(PreferenceConstants.Basic.PREFERRED_ROUTE_TEMPLATE_IN_WORK_OID.get(), getPreferredInWorkRouteTemplateID(context));
        returnMap.put(PreferenceConstants.Basic.PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL_NAME.get(), getPreferredInApprovalRouteTemplateName(context));
        returnMap.put(PreferenceConstants.Basic.PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL_OID.get(), getPreferredInApprovalRouteTemplateID(context));
        return returnMap;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultPlantName(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(),  // user name
                PreferenceConstants.Preferences.PREFERRED_DEFAULT_PLANTS.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultPlantID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,                                                    // context
                    PreferenceConstants.Type.TYPE_PLANT.getName(context),      // type
                    getPreferredDefaultPlantName(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultTypeOnEquivalent(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user name
                PreferenceConstants.Preferences.PREFERRED_DEFAULT_TYPE_ON_CREATE_MEP_SEP.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultTypeOnProduct(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), //user name
                PreferenceConstants.Preferences.PREFERRED_DEFAULT_TYPE_ON_CREATE_PRODUCT.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultTypeOnSpec(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user name
                PreferenceConstants.Preferences.PREFERRED_DEFAULT_TYPE_ON_CREATE_SPEC.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultSharingMemberName(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, //context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user name
                PreferenceConstants.Preferences.PREFERRED_DEFAULT_SHARING_MEMBERS.get()); // preference key
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultSharingMemberID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context,        // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredDefaultSharingMemberName(context));  // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDefaultSharingMemberNameDisplay(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPersonNames(context,    // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredDefaultSharingMemberName(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredIRMSharingMembersDisplay(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPersonNames(context,    // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredIRMSharingMembers(context));    // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @param type
     * @param names
     * @return
     * @throws FrameworkException
     */
    public String getPipeSeparatedPersonNames(Context context, String type, String names) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(names) && UIUtil.isNotNullAndNotEmpty(type)) {
            StringList nameList = StringUtil.split(names, pgV3Constants.SYMBOL_PIPE);
            StringBuilder oidBuilder = new StringBuilder();
            Map<Object, Object> personMap;
            for (String name : nameList) {
                personMap = getPersonObjectOid(context, type, name);
                if (null != personMap && !personMap.isEmpty()) {
                    oidBuilder.append(((String) personMap.get(pgV3Constants.SELECT_ATTRIBUTE_FIRSTNAME)).concat(" ").concat((String) personMap.get(pgV3Constants.SELECT_ATTRIBUTE_LASTNAME)));
                    oidBuilder.append(pgV3Constants.SYMBOL_PIPE);
                }
            }
            if (oidBuilder.length() > 0) {
                oidBuilder.setLength(oidBuilder.length() - 1);
                ids = oidBuilder.toString();
            }
        }
        return ids;
    }

    /**
     * @param context
     * @param type
     * @return
     */
    public String getTypeActualDisplayName(Context context, String type) throws FrameworkException {
        String actualName = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Type." + type, context.getLocale());
        if (UIUtil.isNotNullAndNotEmpty(actualName) && actualName.startsWith("emx")) {
            actualName = type;
        }
        return actualName;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteInstruction(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_INSTRUCTION.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteAction(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_ACTION.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteTaskRecipientMembers(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_TASK_RECIPIENT_MEMBERS.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public List<Member> getPreferredRouteTaskRecipientMemberList(Context context) throws FrameworkException {
        List<Member> resultList = new ArrayList<>();
        String memberString = getPreferredRouteTaskRecipientMembers(context);
        if (UIUtil.isNotNullAndNotEmpty(memberString)) {
            StringList memberList = StringUtil.split(memberString, PreferenceConstants.Basic.SYMBOL_SEMICOLON.get());
            String oid;
            Map<Object, Object> personMap;
            StringList memberRoleList;
            String fullName;
            for (String memberRole : memberList) {
                memberRoleList = StringUtil.split(memberRole, "~");
                if (memberRoleList.size() > 1) {
                    personMap = getPersonObjectOid(context, DomainConstants.TYPE_PERSON, memberRoleList.get(0).trim());
                    if (null != personMap && !personMap.isEmpty()) {
                        oid = (String) personMap.get(DomainConstants.SELECT_ID);
                        if (UIUtil.isNotNullAndNotEmpty(oid)) {
                            fullName = ((String) personMap.get(pgV3Constants.SELECT_ATTRIBUTE_LASTNAME)).concat("; ").concat((String) personMap.get(pgV3Constants.SELECT_ATTRIBUTE_FIRSTNAME));
                            resultList.add(new Member(oid, (String) personMap.get(DomainConstants.SELECT_NAME), fullName, memberRoleList.get(1).trim()));
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public List<UserGroup> getPreferredRouteTaskRecipientUserGroupList(Context context) throws FrameworkException {
        List<UserGroup> resultList = new ArrayList<>();
        String userGroupsString = getPreferredRouteTaskRecipientUserGroups(context);
        if (UIUtil.isNotNullAndNotEmpty(userGroupsString)) {
            StringList userGroupsList = StringUtil.split(userGroupsString, PreferenceConstants.Basic.SYMBOL_SEMICOLON.get());
            String objectOid;
            for (String userGroup : userGroupsList) {
                objectOid = getObjectOid(context, DomainConstants.TYPE_GROUP, userGroup.trim());
                if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                    resultList.add(new UserGroup(objectOid, userGroup.trim()));
                }
            }
        }
        return resultList;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredRouteTaskRecipientUserGroups(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_TASK_RECIPIENT_USER_GROUPS.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getIsPreferredRouteTaskRecipientUserGroup(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_USER_GROUPS.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public boolean isPreferredRouteTaskRecipientUserGroup(Context context) throws FrameworkException {
        String value = getIsPreferredRouteTaskRecipientUserGroup(context);
        return (UIUtil.isNotNullAndNotEmpty(value) && pgV3Constants.TRUE.equalsIgnoreCase(value)) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getIsPreferredRouteTaskRecipientMembers(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user
                PreferenceConstants.Preferences.IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_MEMBERS.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public boolean isPreferredRouteTaskRecipientMembers(Context context) throws FrameworkException {
        String value = getIsPreferredRouteTaskRecipientMembers(context);
        return (UIUtil.isNotNullAndNotEmpty(value) && pgV3Constants.TRUE.equalsIgnoreCase(value)) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIPartType(Context context) throws FrameworkException {
        String value = PropertyUtil.getAdminProperty(context, // context
                DomainConstants.TYPE_PERSON, // type
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_PART_TYPE.get()); // preference property name
        return UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIPhase(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_PHASE.get()); // preference property name
        value = (UIUtil.isNotNullAndNotEmpty(value) && value.startsWith("Hypothetical") && value.contains(pgV3Constants.SYMBOL_SPACE)) ? value.replace(pgV3Constants.SYMBOL_SPACE, pgV3Constants.SYMBOL_HYPHEN) : value;
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIManufacturingStatus(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // username
                PreferenceConstants.Preferences.DI_PREFERRED_MANUFACTURING_STATUS.get()); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIStructureReleaseCriteria(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_RELEASE_CRITERIA_STATUS.get()); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIPackagingMaterialType(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_PACKAGING_MATERIAL_TYPE.get()); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDISegment(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_SEGMENT.get()); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIClass(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_CLASS.get()); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPreferredDIReportedFunction(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        String value = preferenceManagement.getAdminPropertyWithoutCache(context, // context
                context.getUser(), // user name
                PreferenceConstants.Preferences.DI_PREFERRED_REPORTED_FUNCTION.get()); // preference property name
        String emptyKey = EnoviaResourceBundle.getProperty(context, DataConstants.CONST_COMPONENTS_STRING_RESOURCE, context.getLocale(), "emxComponents.EmptyValue");
        value = (UIUtil.isNotNullAndNotEmpty(value) && emptyKey.equalsIgnoreCase(value)) ? DomainConstants.EMPTY_STRING : value;
        value = UIUtil.isNotNullAndNotEmpty(value) ? value.replace(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), pgV3Constants.SYMBOL_PIPE) : DomainConstants.EMPTY_STRING;
        return value;
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void createRouteBasedOnIRMApprovalPreference(Context context, String[] args) throws Exception {
        try {
            String objectOid = args[1];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                DomainObject domainObject = DomainObject.newInstance(context, objectOid);
                if (domainObject.isKindOf(context, PreferenceConstants.Type.TYPE_IRM_DOCUMENT.getName(context))) {
                    logger.log(Level.INFO, "Context user is: " + context.getUser());
                    IRMApprovalPreference approvalPreference = new IRMApprovalPreference(context);
                    boolean isUserGroupOn = approvalPreference.isPreferredRouteTaskRecipientUserGroups();
                    boolean isMemberOn = approvalPreference.isPreferredRouteTaskRecipientMembers();
                    String policy = domainObject.getInfo(context, DomainConstants.SELECT_POLICY);
                    String policySignatureReference = PropertyUtil.getSchemaProperty(context, "policy_pgPKGSignatureReferenceDoc");
                    // preference approval route shall be created only for signature reference policy.
                    if ((isMemberOn || isUserGroupOn) && policySignatureReference.equalsIgnoreCase(policy)) {
                        Instant startTime = Instant.now();
                        SimpleRoute simpleRoute = new SimpleRoute.Creator(context, approvalPreference, objectOid).create();
                        Instant endTime = Instant.now();
                        Duration duration = Duration.between(startTime, endTime);
                        logger.info("User Preferences - IRM Route Creation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
                        if (!simpleRoute.isCreated()) {
                            String errorMessage = simpleRoute.getErrorMessage();
                            logger.log(Level.WARNING, "Route Creation Failed for Object ID: " + objectOid.concat(" with error:").concat(errorMessage));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating route based on irm approval preference: " + e);
        }
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void applySharingMemberOnIRMDocument(Context context, String[] args) throws Exception {
        try {
            String objectOid = args[1];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                DomainObject domainObject = DomainObject.newInstance(context, objectOid);
                if (domainObject.isKindOf(context, PreferenceConstants.Type.TYPE_IRM_DOCUMENT.getName(context))) {
                    logger.log(Level.INFO, "Context user before Getting Sharing Members from Preference: " + context.getUser());
                    String sharingMemberIDs = DomainConstants.EMPTY_STRING;
                    String contextUser = context.getUser();
                    if (UIUtil.isNotNullAndNotEmpty(contextUser) && pgV3Constants.PERSON_USER_AGENT.equalsIgnoreCase(contextUser)) {
                        logger.log(Level.INFO, "Context user is User Agent: " + contextUser);
                        String loggedInUser = PropertyUtil.getRPEValue(context, ContextUtil.MX_LOGGED_IN_USER_NAME, false);
                        logger.log(Level.INFO, "Get Logged-in user: " + loggedInUser);
                        if (UIUtil.isNotNullAndNotEmpty(loggedInUser)) {
                            sharingMemberIDs = getPreferredIRMSharingMembersID(context, loggedInUser);
                            logger.log(Level.INFO, "Get Logged-in user sharing member pref: " + sharingMemberIDs);
                        }
                    } else {
                        sharingMemberIDs = Preferences.IRMAttributePreference.SHARING_MEMBERS.getID(context);
                    }
                    logger.log(Level.INFO, "Context user after getting Sharing Members from Preference: " + context.getUser() + " | " + sharingMemberIDs);
                    if (UIUtil.isNotNullAndNotEmpty(sharingMemberIDs)) { // if user has set (share with members) in preferences.
                        applySharingMemberPreferencesIRM(context, objectOid, sharingMemberIDs);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error applying sharing members on irm doc: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws Exception
     */
    public List<String> getAcessMemberUserList(Context context, String objectOid) throws Exception {
        MapList summaryList = DomainAccess.getAccessSummaryList(context, objectOid);
        logger.log(Level.INFO, "Access Member MapList: " + summaryList);
        List<String> accessMemberList = new ArrayList<>();
        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        String owner = domainObject.getInfo(context, DomainConstants.SELECT_OWNER);
        if (null != summaryList && !summaryList.isEmpty()) {
            Iterator iterator = summaryList.iterator();
            Map<Object, Object> accessMap;
            String userName;
            while (iterator.hasNext()) {
                accessMap = (Map<Object, Object>) iterator.next();
                userName = (String) accessMap.get("username");
                if (UIUtil.isNotNullAndNotEmpty(userName) && !userName.equalsIgnoreCase(owner)) {
                    accessMemberList.add(userName);
                }
            }
        }
        logger.log(Level.INFO, "Access Member User Names: " + accessMemberList);
        return accessMemberList;
    }

    /**
     * @param context
     * @param type
     * @param phase
     * @return
     * @throws Exception
     */
    public StringList getManufacturingStatus(Context context, String type, String phase) throws Exception {
        StringList manufacturingStatusOptions = new StringList();
        StringBuilder subSetBuilder = new StringBuilder(pgV3Constants.DSM_ORIGIN);
        if (UIUtil.isNotNullAndNotEmpty(type) && UIUtil.isNotNullAndNotEmpty(phase)) {
            String pickListName = PreferenceConstants.Type.TYPE_PICKLIST_LIFE_CYCLE_STATUS.getName(context);
            pickListName = pickListName.substring(5);
            subSetBuilder.append(pickListName);
            phase = (UIUtil.isNullOrEmpty(phase)) ? PreferenceConstants.Basic.PRODUCTION.get() : phase;
            phase = (phase.startsWith(PreferenceConstants.Basic.HYPOTHETICAL.get()) && phase.contains(pgV3Constants.SYMBOL_SPACE)) ? phase.replace(pgV3Constants.SYMBOL_SPACE, pgV3Constants.SYMBOL_HYPHEN) : phase;
            if (PreferenceConstants.Basic.HYPOTHETICAL_PRIVATE.get().equals(phase) || PreferenceConstants.Basic.HYPOTHETICAL_PUBLIC.get().equals(phase) || PreferenceConstants.Basic.DEVELOPMENT.get().equals(phase)) {
                phase = PreferenceConstants.Basic.MANUFACTURING_STATUS_EXPERIMENTAL.get();
            }
            subSetBuilder.append(phase);
            if (PreferenceConstants.Type.TYPE_ASSEMBLED_PRODUCT_PART.getName(context).equalsIgnoreCase(type) && PreferenceConstants.Basic.PRODUCTION.get().equalsIgnoreCase(phase)) {
                subSetBuilder.append(PreferenceConstants.Basic.KEY_APP.get());
            }
            HashMap programMap = new HashMap();
            HashMap fieldMap = new HashMap();
            HashMap settingMap = new HashMap();
            settingMap.put("pgPicklistSubset", subSetBuilder.toString());
            fieldMap.put("settings", settingMap);
            programMap.put("fieldMap", fieldMap);
            manufacturingStatusOptions = JPO.invoke(context, // context
                    PreferenceConstants.Basic.JPO_PICKLIST.get(), // jpo name
                    null, // constructor args
                    PreferenceConstants.Basic.METHOD_PICKLIST_SUBSET_RANGE.get(), // method name
                    JPO.packArgs(programMap), // method args
                    StringList.class); // return type.
        }
        logger.log(Level.INFO, PreferenceConstants.Basic.SYMBOL_PIPE.get() + type + PreferenceConstants.Basic.SYMBOL_PIPE.get() + phase + PreferenceConstants.Basic.SYMBOL_PIPE.get() + subSetBuilder.toString());
        return manufacturingStatusOptions;
    }

    /**
     * @param context
     * @param type
     * @return
     * @throws Exception
     */
    public Map getPhase(Context context, String type) throws Exception {
        Map phaseInfoMap = new HashMap();
        if (UIUtil.isNotNullAndNotEmpty(type)) {
            if (PreferenceConstants.Type.TYPE_FORMULATION.getName(context).equalsIgnoreCase(type)) {
                ENOIFormulation impl = ENOFormulationFactory.getIMPLObj();
                phaseInfoMap = impl.getPhases(context, type.indexOf("type_") > -1 ? PropertyUtil.getSchemaProperty(context, type) : type);
            } else {
                phaseInfoMap = ReleasePhase.getPhaseList(context, type);
            }
        }
        return phaseInfoMap;
    }

    /**
     * @param context
     * @return
     * @throws Exception
     */
    public boolean isEBPUser(Context context) throws Exception {
        boolean isEBP = true;
        String personObjectID = PersonUtil.getPersonObjectID(context);
        if (UIUtil.isNotNullAndNotEmpty(personObjectID)) {
            String attributeSecurityEmployeeTypeSelect = PreferenceConstants.Attribute.ATTRIBUTE_PG_SECURITY_EMPLOYEE_TYPE.getSelect(context);
            DomainObject domainObject = DomainObject.newInstance(context, personObjectID);
            String employeeTypeValue = domainObject.getInfo(context, attributeSecurityEmployeeTypeSelect);
            if (UIUtil.isNotNullAndNotEmpty(employeeTypeValue) && pgV3Constants.KEY_EBP.equalsIgnoreCase(employeeTypeValue)) {
                isEBP = false;
            }
        } else {
            logger.log(Level.WARNING, "Unable to retrieve Person object ID");
        }
        return isEBP;
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    public boolean isPersonEBPUser(Context context, String objectOid) throws FrameworkException {
        boolean isEBP = false;
        if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
            String attributeSecurityEmployeeTypeSelect = PreferenceConstants.Attribute.ATTRIBUTE_PG_SECURITY_EMPLOYEE_TYPE.getSelect(context);
            DomainObject domainObject = DomainObject.newInstance(context, objectOid);
            String employeeTypeValue = domainObject.getInfo(context, attributeSecurityEmployeeTypeSelect);
            if (UIUtil.isNotNullAndNotEmpty(employeeTypeValue) && pgV3Constants.KEY_EBP.equalsIgnoreCase(employeeTypeValue)) {
                isEBP = true;
            }
        } else {
            logger.log(Level.WARNING, "EBP User check Person object ID is null");
        }
        return isEBP;
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void connectCAWithPreferenceRouteTemplate(Context context, String[] args) throws Exception {
        try {
            String objectOid = args[0];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                DomainObject domainObject = DomainObject.newInstance(context, objectOid);
                //Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55187 -Start
                applySharingMemerPreferenceOnCA(context,objectOid);
                //Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55187 -End
                String type = domainObject.getInfo(context, DomainConstants.SELECT_TYPE);
                String typeChangeAction = PreferenceConstants.Type.TYPE_CHANGE_ACTION.getName(context);
                if (typeChangeAction.equalsIgnoreCase(type)) {
                    Instant startTime = Instant.now();
                    // Added by DSM (Sogeti) for 22x.03 (May CW 2023) Defect-53179 - Start
                    if (isNonDSMAuthoredFormulation(context, objectOid)) {
                        logger.info("FOP is not from  DSO originating Source So not connecting newly created CA with (Preferences) Route Template(s) >>: ");
                    } else {
                        //Modified by DSM (Sogeti) for 22x.04 (Dec CW 2023) Req-47840 - Start
                        RouteTemplatePreferenceService routeTemplate = new RouteTemplatePreferenceService.Connector(context, objectOid).connect();
                        // Modified by DSM (Sogeti) for 22x.04 (Dec CW 2023)Req-47840 -End
                        if (!routeTemplate.isConnected()) {
                            logger.log(Level.WARNING, "Error connecting newly created CA with (Preferences) Route Template(s) >>: " + routeTemplate.getErrorMessage());
                        }
                    }
                    // Added by DSM (Sogeti) for 22x.03 (May CW 2023) Defect-53179 - End
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    logger.info("User Preferences - Route Template Connector - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
                }
            }
        } catch (FrameworkException e) {
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public String getPhaseByObjectType(Context context, String[] args) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Object, Object> programMap = JPO.unpackArgs(args);
        String type = (String) programMap.get("vSelectedType");
        if (UIUtil.isNotNullAndNotEmpty(type)) {
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            Map phaseMap = userPreferenceUtil.getPhase(context, type);
            if (null != phaseMap && !phaseMap.isEmpty()) {
                StringList phaseOptions = (StringList) phaseMap.get(DataConstants.CONST_FIELD_CHOICES);
                StringList phaseDisplayOptions = (StringList) phaseMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                stringBuilder.append(String.join(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), phaseOptions));
                stringBuilder.append(PreferenceConstants.Basic.SYMBOL_PIPE.get());
                stringBuilder.append(String.join(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), phaseDisplayOptions));
            }
        }
        logger.log(Level.INFO, " | Type > " + type + " | Phase > " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public String getManufacturingStatusByTypeAndPhase(Context context, String[] args) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Map<Object, Object> programMap = JPO.unpackArgs(args);
        String type = (String) programMap.get("vSelectedType");
        String phase = (String) programMap.get("vSelectedPhase");
        if (UIUtil.isNotNullAndNotEmpty(type) && UIUtil.isNotNullAndNotEmpty(phase)) {
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            StringList manufacturingStatus = userPreferenceUtil.getManufacturingStatus(context, type, phase);
            if (null != manufacturingStatus && !manufacturingStatus.isEmpty()) {
                stringBuilder.append(String.join(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), manufacturingStatus));
            }
        }
        logger.log(Level.INFO, " | Type > " + type + " | Phase > " + phase + " | Mfg Status > " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getPackagingMaterialTypePreferredID(Context context) throws FrameworkException {
        String preferredDIPackagingMaterialTypeID = DomainConstants.EMPTY_STRING;
        String preferredDIPackagingMaterialTypeName = getPreferredDIPackagingMaterialType(context);
        if (UIUtil.isNotNullAndNotEmpty(preferredDIPackagingMaterialTypeName)) {
            preferredDIPackagingMaterialTypeID = getObjectOid(context, // context
                    PreferenceConstants.Type.TYPE_PICKLIST_PACKAGING_MATERIAL_TYPE.getName(context), // type
                    preferredDIPackagingMaterialTypeName); // name
        }
        return preferredDIPackagingMaterialTypeID;
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void setPreferencesOnCreationPart(Context context, String[] args) throws Exception {
        try {
            PartCreateAction partCreateAction = new PartCreateAction();
            partCreateAction.setPreferencesOnPartCreation(context, args);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void setSharingMembersPreferencesForArtWork(Context context, String[] args) throws Exception {
        PartCreateAction partCreateAction = new PartCreateAction();
        partCreateAction.setSharingMembersPreferencesForArtWorkCreation(context, args);
    }

    /**
     * @param context
     * @param args
     * @throws FrameworkException
     */
    public void isCATIADesignerPart(Context context, String[] args) throws FrameworkException {
        PartCreateAction partCreateAction = new PartCreateAction();
        partCreateAction.isCATIADesignerPart(context, args);
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public String getReportedFunctionByType(Context context, String[] args) throws Exception {
        StringBuilder resultBuilder = new StringBuilder();
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String type = (String) programMap.get("vSelectedType");
            if (UIUtil.isNotNullAndNotEmpty(type)) {
                String result = getReportedFunctionByType(context, type);
                resultBuilder.append(result);
            }
        } catch (Exception e) {
            throw e;
        }
        return resultBuilder.toString();
    }

    /**
     * @param context
     * @param type
     * @return
     * @throws MatrixException
     */
    public String getReportedFunctionByType(Context context, String type) throws MatrixException {
        StringBuilder resultBuilder = new StringBuilder();
        try {
            Map<Object, Object> resultMap = getReportedFunctionRangeByType(context, type);
            if (null != resultMap && !resultMap.isEmpty()) {
                StringList fieldChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES);
                StringList fieldDisplayChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                resultBuilder.append(String.join(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), fieldChoices));
                resultBuilder.append(PreferenceConstants.Basic.SYMBOL_PIPE.get());
                resultBuilder.append(String.join(PreferenceConstants.Basic.SYMBOL_SEMICOLON.get(), fieldDisplayChoices));
            }
        } catch (MatrixException e) {
            throw e;
        }
        return resultBuilder.toString();
    }

    /**
     * @param context
     * @param type
     * @return
     * @throws MatrixException
     */
    public Map<Object, Object> getReportedFunctionRangeByType(Context context, String type) throws MatrixException {
        Map<Object, Object> resultMap = new HashMap<>();
        try {
            Map settings = new HashMap();
            settings.put("pgPicklistState", "");
            settings.put("pgPicklistSubset", "Packaging Parts Reported Function");
            settings.put("pgPicklistName", PreferenceConstants.Type.TYPE_PICKLIST_REPORTED_FUNCTION.getName(context));
            settings.put("Input Type", "combobox");
            settings.put("IncludeBlank", "true");
            Map settingsMap = new HashMap();
            settingsMap.put("settings", settings);
            Map paramMap = new HashMap();
            paramMap.put("fieldMap", settingsMap);
            StringList fieldDisplayChoices = new StringList();
            StringList fieldChoices = new StringList();

            if (UIUtil.isNotNullAndNotEmpty(type)) {
                logger.log(Level.INFO, "Fetch Reported Function for type: {0}", type);
                type = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, type, true);
                StringList allowedTypeList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "emxCPN.ReportedFunction.Range.Subset.allowedTypes"), ",");
                if (null != allowedTypeList && allowedTypeList.contains(type)) {
                    resultMap = JPO.invoke(context, "pgDSOUtil_mxJPO", null, "getPicklistSubsetRangeMap", JPO.packArgs(paramMap), Map.class);
                    if (resultMap != null && !resultMap.isEmpty()) {
                        fieldDisplayChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                        fieldChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES);
                        if (fieldDisplayChoices != null && fieldChoices != null) {
                            fieldDisplayChoices.add(DomainConstants.EMPTY_STRING);
                            fieldChoices.add(DomainConstants.EMPTY_STRING);
                            resultMap.clear();
                            resultMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, fieldDisplayChoices);
                            resultMap.put(DataConstants.CONST_FIELD_CHOICES, fieldChoices);
                        }
                    }
                } else {
                    DSMUserPreferenceConfig preferenceConfig = getDSMUserPreferenceConfig(context);
                    allowedTypeList = StringUtil.split(preferenceConfig.getAllowedTypesForReportedFunction(), pgV3Constants.SYMBOL_COMMA);
                    if (null != allowedTypeList && allowedTypeList.contains(type)) {
                        resultMap = JPO.invoke(context, "pgPLPicklist_mxJPO", null, "getPicklistRangeMap", JPO.packArgs(paramMap), Map.class);
                    }
                }
            } else {
                logger.log(Level.WARNING, "Type argument is null or empty");
            }
        } catch (MatrixException e) {
            throw e;
        }
        return resultMap;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getSegment(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        CacheManagement cacheManagement = new CacheManagement(context);
        @SuppressWarnings("unchecked")
        Map<Object, Object> resultMap = (Map<Object, Object>) cacheManagement.getPickListItems(context, DataConstants.CONST_PICKLIST_SEGMENT);
        if (null != resultMap && !resultMap.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Segments range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @param cacheManagement
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getSegment(Context context, CacheManagement cacheManagement) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<Object, Object> resultMap = (Map<Object, Object>) cacheManagement.getPickListItems(context, DataConstants.CONST_PICKLIST_SEGMENT);
        if (null != resultMap && !resultMap.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Segments range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @param cacheManagement
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getClasses(Context context, CacheManagement cacheManagement) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<Object, Object> resultMap = (Map<Object, Object>) cacheManagement.getPickListItems(context, DataConstants.CONST_PICKLIST_CLASS);
        if (null != resultMap && !resultMap.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Classes range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getClasses(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        CacheManagement cacheManagement = new CacheManagement(context);
        @SuppressWarnings("unchecked")
        Map<Object, Object> resultMap = (Map<Object, Object>) cacheManagement.getPickListItems(context, DataConstants.CONST_PICKLIST_CLASS);
        if (null != resultMap && !resultMap.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Classes range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @param cacheManagement
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getPackagingMaterialType(Context context, CacheManagement cacheManagement) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<Object, Object> resultMap = (Map<Object, Object>) cacheManagement.getPickListItems(context, DataConstants.CONST_PICKLIST_PACKMATERIALTYPE);
        if (null != resultMap && !resultMap.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Packaging Material Type range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getPackagingMaterialType(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        CacheManagement cacheManagement = new CacheManagement(context);
        @SuppressWarnings("unchecked")
        Map<Object, Object> resultMap = (Map<Object, Object>) cacheManagement.getPickListItems(context, DataConstants.CONST_PICKLIST_PACKMATERIALTYPE);
        if (null != resultMap && !resultMap.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Packaging Material Type range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getReleaseCriteriaStatus(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList resultList = JPO.invoke(context, DataConstants.CONST_DSOUTIL_JPO, null, DataConstants.CONST_BLANK_YESNO_METHOD, null, StringList.class);
        if (null != resultList && !resultList.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, resultList);
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, resultList);
        } else {
            logger.log(Level.WARNING, "Release Criteria range is empty");
        }
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getPackagingComponentType(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        try {
            Map settings = new HashMap();
            settings.put("pgPicklistName", "pgPLIPackComponentType");
            Map settingsMap = new HashMap();
            settingsMap.put("settings", settings);
            Map paramMap = new HashMap();
            paramMap.put("fieldMap", settingsMap);
            Map resultMap = JPO.invoke(context, "pgDSOUtil_mxJPO", null, "getPicklistRangeMapForDirectAttr", JPO.packArgs(paramMap), Map.class);
            if (null != resultMap && !resultMap.isEmpty()) {
                rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching Packaging Component Type " + e);
            throw e;
        }
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, StringList> getBaseUnitOfMeasure(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        try {
            Map settings = new HashMap();
            settings.put("pgPicklistName", "pgPLIBUOM");
            Map settingsMap = new HashMap();
            settingsMap.put("settings", settings);
            Map paramMap = new HashMap();
            paramMap.put("fieldMap", settingsMap);
            Map resultMap = JPO.invoke(context, "pgDSOUtil_mxJPO", null, "getPicklistRangeMapForDirectAttr", JPO.packArgs(paramMap), Map.class);
            if (null != resultMap && !resultMap.isEmpty()) {
                rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
                rangeMap.put(DataConstants.CONST_FIELD_CHOICES, (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES));
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching Base UoM " + e);
            throw e;
        }
        return rangeMap;
    }

    /**
     * @param context
     * @param type
     * @param phase
     * @return
     * @throws Exception
     */
    public Map<String, StringList> getManufacturingStatusRange(Context context, String type, String phase) throws Exception {
        Map<String, StringList> rangeMap = new HashMap<>();
        try {
            StringList resultList = getManufacturingStatus(context, type, phase);
            if (null != resultList && !resultList.isEmpty()) {
                rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, resultList);
                rangeMap.put(DataConstants.CONST_FIELD_CHOICES, resultList);
            } else {
                logger.log(Level.WARNING, "Mfg Status range is empty");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error fetching Mfg Status " + e);
            throw e;
        }
        return rangeMap;
    }

    /**
     * @param context
     * @param partTypes
     * @return
     * @throws FrameworkException
     */
    public Map<String, StringList> getPartTypeRanges(Context context, String partTypes) throws FrameworkException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList typeDisplayChoices = new StringList();
        StringList typeChoices = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(partTypes)) {
            StringList typeList = StringUtil.split(partTypes, pgV3Constants.SYMBOL_COMMA);
            Map<String, String> typeMap = new HashMap<>();
            String typeName;
            String typeDisplayName;
            for (String symbolicType : typeList) {
                if (UIUtil.isNotNullAndNotEmpty(symbolicType)) {
                    typeName = PropertyUtil.getSchemaProperty(context, symbolicType);
                    typeDisplayName = getTypeActualDisplayName(context, typeName);
                    typeMap.put(typeName, typeDisplayName);
                    typeDisplayChoices.add(typeDisplayName);
                }
            }
            typeDisplayChoices.sort();
            for (String displayType : typeDisplayChoices) {
                for (Map.Entry<String, String> entry : typeMap.entrySet()) {
                    if (entry.getValue().equals(displayType)) {
                        typeChoices.add(entry.getKey());
                        break;
                    }
                }
            }
        }
        rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, typeDisplayChoices);
        rangeMap.put(DataConstants.CONST_FIELD_CHOICES, typeChoices);
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public MapList findBusinessArea(Context context) throws FrameworkException {
        return DomainObject.findObjects(context,//context
                PreferenceConstants.Type.TYPE_PICKLIST_BUSINESS_AREA.getName(context), // type
                DomainConstants.QUERY_WILDCARD, // name
                PreferenceConstants.Basic.SYMBOL_HYPHEN.get(), // revision
                DomainConstants.QUERY_WILDCARD, // owner
                pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault
                "current==Active", // where clause
                Boolean.FALSE, // expand type
                StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, PreferenceConstants.Basic.PHYSICAL_ID.get())); // object selects
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public Map<String, StringList> getBusinessAreaRanges(Context context) throws FrameworkException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList fieldDisplayChoices = new StringList();
        StringList fieldChoices = new StringList();
        MapList resultList = findBusinessArea(context);
        if (null != resultList && !resultList.isEmpty()) {
            resultList.sort("name", "ascending", "String");
            Iterator iterator = resultList.iterator();
            Map<Object, Object> resultMap;
            while (iterator.hasNext()) {
                resultMap = (Map<Object, Object>) iterator.next();
                fieldDisplayChoices.add((String) resultMap.get(DomainConstants.SELECT_NAME));
                fieldChoices.add((String) resultMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
            }
        }
        rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, fieldDisplayChoices);
        rangeMap.put(DataConstants.CONST_FIELD_CHOICES, fieldChoices);
        return rangeMap;
    }

    /**
     * @param context
     * @param objectId
     * @return
     * @throws FrameworkException
     */
    public MapList getProductCategoryPlatformByBusinessArea(Context context, String objectId) throws FrameworkException {
        MapList objectList = new MapList();
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            DomainObject domainObject = DomainObject.newInstance(context, objectId);
            String relationship = PropertyUtil.getSchemaProperty(context, "relationship_pgPlatformToBusinessArea");
            String ATTRIBUTE_PG_CHASSIS_TYPE = PropertyUtil.getSchemaProperty("attribute_pgChassisType");

            String pcp = PreferenceConstants.Basic.PRODUCT_CATEGORY_PLATFORM.get();
            String where = (PreferenceConstants.Attribute.ATTRIBUTE_PLATFORM_TYPE.getSelect(context) + "==\"" + pcp + "\"");

            StringList objectSelects = new StringList(4);
            objectSelects.add(DomainConstants.SELECT_NAME);
            objectSelects.add(DomainConstants.SELECT_ID);
            objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
            //objectSelects.add("attribute[" + ATTRIBUTE_PG_PLATFORM_TYPE + "]");
            //objectSelects.add("attribute[" + ATTRIBUTE_PG_CHASSIS_TYPE + "]");

            objectList = domainObject.getRelatedObjects(context,
                    relationship,
                    PreferenceConstants.Type.TYPE_PICKLIST_PLATFORM.getName(context),
                    objectSelects,
                    DomainConstants.EMPTY_STRINGLIST,
                    true,
                    false,
                    (short) 1,
                    where,
                    DomainConstants.EMPTY_STRING,
                    0);
        }
        return objectList;
    }

    /**
     * @param context
     * @param inputList
     * @return
     * @throws FrameworkException
     * @throws JSONException
     */
    public String getProductCategoryPlatformByBusinessAreaJson(Context context, StringList inputList) throws FrameworkException, JSONException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        for (String id : inputList) {
            if (UIUtil.isNotNullAndNotEmpty(id)) {
                logger.log(Level.INFO, "Input Business Area ID: " + id);
                MapList objectList = getProductCategoryPlatformByBusinessArea(context, id);
                if (null != objectList && !objectList.isEmpty()) {
                    logger.log(Level.INFO, "Number of related Product Category Platform: " + objectList.size());
                    objectList.sort("name", "ascending", "String");
                    jsonArr.add(appendBlankSelect());
                    JsonObjectBuilder json;
                    for (Object object : objectList) {
                        json = Json.createObjectBuilder();
                        json.add(DomainConstants.SELECT_ID, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
                        json.add(DomainConstants.SELECT_NAME, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
                        json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), (String) ((Map<Object, Object>) object).get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                        jsonArr.add(json);
                    }

                } else {
                    logger.log(Level.INFO, "Business Area has no related Product Category Platform");
                }
            }
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    /**
     * @param context
     * @param id
     * @return
     * @throws FrameworkException
     * @throws JSONException
     */
    public String getProductCategoryPlatformByBusinessAreaJson(Context context, String id) throws FrameworkException, JSONException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        if (UIUtil.isNotNullAndNotEmpty(id)) {
            logger.log(Level.INFO, "Input Business Area: " + id);
            MapList objectList = getProductCategoryPlatformByBusinessArea(context, id);
            if (null != objectList && !objectList.isEmpty()) {
                logger.log(Level.INFO, "Number of related Product Category Platform: " + objectList.size());
                objectList.sort("name", "ascending", "String");
                JsonObjectBuilder json;
                for (Object object : objectList) {
                    json = Json.createObjectBuilder();
                    json.add(DomainConstants.SELECT_ID, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
                    json.add(DomainConstants.SELECT_NAME, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
                    json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), (String) ((Map<Object, Object>) object).get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                    jsonArr.add(json);
                }

            } else {
                logger.log(Level.INFO, "Business Area has no related Product Category Platform");
            }
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public String getProductCategoryPlatformByBusinessAreaJson(Context context, String[] args) throws Exception {
        String result = DomainConstants.EMPTY_STRING;
        Map<Object, Object> programMap = JPO.unpackArgs(args);
        String id = (String) programMap.get(DomainConstants.SELECT_ID);
        if (UIUtil.isNotNullAndNotEmpty(id)) {
            StringList inputList = StringUtil.split(id, pgV3Constants.SYMBOL_PIPE);
            if (null != inputList && !inputList.isEmpty()) {
                result = getProductCategoryPlatformByBusinessAreaJson(context, inputList);
            }
        }
        logger.log(Level.INFO, "Product Category Platform: " + result);
        return result;
    }

    /**
     * @param context
     * @param objectId
     * @return
     * @throws FrameworkException
     */
    public Map<String, StringList> getProductCategoryPlatformByBusinessAreaRange(Context context, String objectId) throws FrameworkException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList fieldDisplayChoices = new StringList();
        StringList fieldChoices = new StringList();
        MapList resultList = getProductCategoryPlatformByBusinessArea(context, objectId);
        if (null != resultList && !resultList.isEmpty()) {
            resultList.sort("name", "ascending", "String");
            Iterator iterator = resultList.iterator();
            Map<Object, Object> resultMap;
            while (iterator.hasNext()) {
                resultMap = (Map<Object, Object>) iterator.next();
                fieldDisplayChoices.add((String) resultMap.get(DomainConstants.SELECT_NAME));
                fieldChoices.addAll((String) resultMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
            }
        }
        rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, fieldDisplayChoices);
        rangeMap.put(DataConstants.CONST_FIELD_CHOICES, fieldChoices);
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getBusinessArea(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PRODUCT_BUSINESS_AREA.get());
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getRelatedProductCategoryPlatform(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.PRODUCT_CATEGORY_PLATFORM.get());
    }

    /**
     * @param context
     * @param businessArea
     * @return
     * @throws FrameworkException
     */
    public String getBusinessAreaName(Context context, String businessArea) throws FrameworkException {
        String names = DomainConstants.EMPTY_STRING;
        try {
            names = getBusinessAreaPipeSeparatedName(context, businessArea);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return names;
    }

    public String getPipeSeparatedNameFromID(Context context, String objectID) throws FrameworkException {
        String names = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectID)) {
            StringList objectList = StringUtil.split(objectID, pgV3Constants.SYMBOL_PIPE);
            if (null != objectList && !objectList.isEmpty()) {
                MapList mapList = DomainObject.getInfo(context, objectList.toStringArray(), StringList.create(DomainConstants.SELECT_NAME));
                if (null != mapList && !mapList.isEmpty()) {
                    List<String> nameList = new ArrayList<>();
                    Iterator iterator = mapList.iterator();
                    Map<Object, Object> objectMap;
                    while (iterator.hasNext()) {
                        objectMap = (Map<Object, Object>) iterator.next();
                        String name = (String) objectMap.get(DomainConstants.SELECT_NAME);
                        nameList.add(name);
                    }
                    names = String.join(pgV3Constants.SYMBOL_PIPE, nameList);
                }
            }
        }
        return names;
    }

    /**
     * @param context
     * @param businessArea
     * @return
     * @throws FrameworkException
     */
    public String getBusinessAreaPipeSeparatedName(Context context, String businessArea) throws FrameworkException {
        String names = DomainConstants.EMPTY_STRING;
        StringBuilder nameBuilder = new StringBuilder();
        if (UIUtil.isNotNullAndNotEmpty(businessArea)) {
            StringList objectList = StringUtil.split(businessArea, pgV3Constants.SYMBOL_PIPE);
            if (null != objectList && !objectList.isEmpty()) {
                MapList mapList = DomainObject.getInfo(context, objectList.toArray(new String[objectList.size()]), StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, PreferenceConstants.Basic.PHYSICAL_ID.get()));
                if (null != mapList && !mapList.isEmpty()) {
                    Iterator iterator = mapList.iterator();
                    Map<Object, Object> objectMap;
                    String name;
                    while (iterator.hasNext()) {
                        objectMap = (Map<Object, Object>) iterator.next();
                        name = (String) objectMap.get(DomainConstants.SELECT_NAME);
                        nameBuilder.append(name);
                        nameBuilder.append(pgV3Constants.SYMBOL_PIPE);
                    }
                }
            }
        }
        if (nameBuilder.length() > 0) {
            nameBuilder.setLength(nameBuilder.length() - 1);
            names = nameBuilder.toString();
        }
        return names;
    }

    /**
     * @param context
     * @param productCategoryPlatform
     * @return
     * @throws FrameworkException
     */
    public String getProductCategoryPlatformID(Context context, String productCategoryPlatform) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getProductCategoryPlatformPipeSeparatedIDs(context, // context
                    PreferenceConstants.Type.TYPE_PICKLIST_PLATFORM.getName(context), // type
                    productCategoryPlatform); // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getMaterialFunctionID(Context context, String materialFunction) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedIDs(context, // context
                    PreferenceConstants.Type.TYPE_PICKLIST_MATERIAL_FUNCTION_GLOBAL.getName(context), // type
                    materialFunction); // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }


    /**
     * @param context
     * @param type
     * @param names
     * @return
     * @throws FrameworkException
     */
    public String getProductCategoryPlatformPipeSeparatedIDs(Context context, String type, String names) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(names) && UIUtil.isNotNullAndNotEmpty(type)) {
            StringList nameList = StringUtil.split(names, pgV3Constants.SYMBOL_PIPE);
            String oid;
            StringBuilder oidBuilder = new StringBuilder();
            for (String name : nameList) {
                if (UIUtil.isNotNullAndNotEmpty(name)) {
                    oid = getRelatedProductCategoryPlatform(context, type, name);
                    if (UIUtil.isNotNullAndNotEmpty(oid)) {
                        oidBuilder.append(oid);
                        oidBuilder.append(pgV3Constants.SYMBOL_PIPE);
                    }
                }
            }
            if (oidBuilder.length() > 0) {
                oidBuilder.setLength(oidBuilder.length() - 1);
                ids = oidBuilder.toString();
            }
        }
        return ids;
    }

    /**
     * @param context
     * @param type
     * @param name
     * @return
     * @throws FrameworkException
     */
    public String getRelatedProductCategoryPlatform(Context context, String type, String name) throws FrameworkException {
        String id = DomainConstants.EMPTY_STRING;
        try {
            String where = (PreferenceConstants.Attribute.ATTRIBUTE_PLATFORM_TYPE.getSelect(context) + "==\"" + PreferenceConstants.Basic.PRODUCT_CATEGORY_PLATFORM.get() + "\"");
            logger.log(Level.INFO, "Find PCP Where: " + where);
            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),                                   // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    where,                       // where expression
                    false,                                  // expandType
                    StringList.create(DomainConstants.SELECT_ID, PreferenceConstants.Basic.PHYSICAL_ID.get()));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    id = (String) ((Map) iterator.next()).get(DomainConstants.SELECT_ID);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return id;
    }

    /**
     * @param context
     * @param objectList
     * @return
     * @throws FrameworkException
     */
    public Map<String, StringList> getProductCategoryPlatformByBusinessAreaRange(Context context, StringList objectList) throws FrameworkException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList fieldDisplayChoices = new StringList();
        StringList fieldChoices = new StringList();

        for (String objectId : objectList) {
            MapList resultList = getProductCategoryPlatformByBusinessArea(context, objectId);
            if (null != resultList && !resultList.isEmpty()) {
                resultList.sort("name", "ascending", "String");
                Iterator iterator = resultList.iterator();
                Map<Object, Object> resultMap;
                while (iterator.hasNext()) {
                    resultMap = (Map<Object, Object>) iterator.next();
                    fieldDisplayChoices.addAll((String) resultMap.get(DomainConstants.SELECT_NAME));
                    fieldChoices.addAll((String) resultMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                }
            }
        }
        rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, fieldDisplayChoices);
        rangeMap.put(DataConstants.CONST_FIELD_CHOICES, fieldChoices);
        return rangeMap;
    }

    /**
     * @param context
     * @return
     */
    public PreferenceConfigLoader getPreferenceConfigLoader(Context context) {
        return new PreferenceConfigLoader.Load(context).now();
    }

    public Map<String, StringList> getProductComplianceRequiredRanges(Context context) throws MatrixException {
        Map<String, StringList> rangeMap = new HashMap<>();
        String attributeName = PreferenceConstants.Attribute.IS_PRODUCT_COMPLIANCE_REQUIRED.getName(context);
        AttributeType attributeType = new AttributeType(attributeName);
        attributeType.open(context);
        StringList choices = attributeType.getChoices();
        if (null != choices && !choices.isEmpty()) {
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, choices);
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, choices);
        }
        return rangeMap;
    }

    public void getUserPreferencesConfigJson(Context context) {
        try {
            PreferenceConfigLoader preferenceConfigLoader = new PreferenceConfigLoader.Load(context).now();
            if (preferenceConfigLoader.isLoaded()) {
                PreferenceConfig preferenceConfig = preferenceConfigLoader.getPreferenceConfig();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = ow.writeValueAsString(preferenceConfig);
                logger.log(Level.INFO, "User Preferences Config Json: " + json);
            }
        } catch (JsonProcessingException e) {
            logger.log(Level.WARNING, "Error: " + e);
        }
    }

    /**
     * @param context
     * @param args
     */
    public void getUserPreferenceConfigPage(Context context) {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            logger.log(Level.INFO, "User Preference Config Page: " + preferenceConfigLoader.getPreferenceConfig());
        } else {
            logger.log(Level.WARNING, "Failed to load User Preference Config Page with error: " + preferenceConfigLoader.getErrorMessage());
        }
    }

    /**
     * @param xmlContent
     * @return
     * @throws JAXBException
     */
    public PreferenceConfig getUserPreferenceConfig(String xmlContent) throws JAXBException {
        Instant startTime = Instant.now();
        PreferenceConfig preferenceConfig = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PreferenceConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            preferenceConfig = (PreferenceConfig) unmarshaller.unmarshal(new StringReader(xmlContent));
        } catch (JAXBException e) {
            throw e;
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "PreferenceConfig - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return preferenceConfig;
    }

    /**
     * @param context
     * @param domainObject
     * @param businessAreaIDs
     * @throws FrameworkException
     */
    public void applyBusinessArea(Context context, DomainObject domainObject, String businessAreaIDs, boolean flag) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(businessAreaIDs)) {
            StringList toConnectList = getBusinessAreaToConnect(context, domainObject, businessAreaIDs);
            String[] toConnectArr = toConnectList.toArray(String[]::new);
            if (toConnectArr.length > 0) {
                DomainRelationship.connect(context, domainObject, pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA, true, toConnectArr);
            }
        }
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public MapList getRelatedBusinessArea(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelects = new StringList();
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
        StringList relSelects = new StringList();
        relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
        MapList relatedObjects = domainObject.getRelatedObjects(context,
                pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA,
                PreferenceConstants.Type.TYPE_PICKLIST_BUSINESS_AREA.getName(context),
                objectSelects,
                relSelects,
                false,
                true,
                (short) 1,
                DomainConstants.EMPTY_STRING,
                DomainConstants.EMPTY_STRING,
                0);

        return relatedObjects;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public MapList getRelatedPlants(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelects = new StringList();
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
        StringList relSelects = new StringList();
        relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
        MapList relatedObjects = domainObject.getRelatedObjects(context,
                DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY,
                PreferenceConstants.Type.TYPE_PLANT.getName(context),
                objectSelects,
                relSelects,
                true,
                false,
                (short) 1,
                DomainConstants.EMPTY_STRING,
                DomainConstants.EMPTY_STRING,
                0);

        return relatedObjects;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public StringList getConnectedProductCategoryPlatform(Context context, DomainObject domainObject) throws FrameworkException {
        StringList infoList = domainObject.getInfoList(context, PreferenceConstants.Basic.RELATED_PRODUCT_CATEGORY_PLATFORM.get());
        return (null == infoList) ? new StringList() : infoList;
    }

    /**
     * @param context
     * @param domainObject
     * @param productCategoryPlatformIDs
     * @throws FrameworkException
     */
    public void applyProductCategoryPlatform(Context context, DomainObject domainObject, String productCategoryPlatformIDs, boolean flag) throws FrameworkException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(productCategoryPlatformIDs)) {
                StringList toConnectList = getProductCategoryPlatformToConnect(context, domainObject, productCategoryPlatformIDs);
                if (null != toConnectList && !toConnectList.isEmpty()) {
                    // loop is required as - it needs to update rel attribute as well.
                    DomainRelationship domainRelationship;
                    DomainObject platformCategoryPlatformObj = DomainObject.newInstance(context);
                    for (String categoryID : toConnectList) {
                        platformCategoryPlatformObj.setId(categoryID);
                        domainRelationship = platformCategoryPlatformObj.connect(context, pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM, domainObject, true);
                        domainRelationship.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGPLATFORMTYPE, PreferenceConstants.Basic.PRODUCT_CATEGORY_PLATFORM.get());
                        logger.log(Level.INFO, "Applied Product Category Platform");
                    }
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error Connecting Product Category Platform: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param productCategoryPlatformIDs
     * @return
     * @throws FrameworkException
     */
    public StringList getProductCategoryPlatformToConnect(Context context, DomainObject domainObject, String productCategoryPlatformIDs) throws FrameworkException {
        StringList toConnectList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(productCategoryPlatformIDs)) {
            logger.log(Level.INFO, "Product category platform from preference: " + productCategoryPlatformIDs);
            StringList preferenceCategoryList = StringUtil.split(productCategoryPlatformIDs, pgV3Constants.SYMBOL_PIPE);
            StringList connectedCategoryList = getRelatedProductCategoryPlatformList(context, domainObject);
            logger.log(Level.INFO, "Product category platform which is already connected: " + connectedCategoryList);
            if (null != connectedCategoryList && !connectedCategoryList.isEmpty()) {
                for (String preferenceCategoryID : preferenceCategoryList) {
                    if (!connectedCategoryList.contains(preferenceCategoryID)) {
                        toConnectList.add(preferenceCategoryID);
                    }
                }
            } else {
                toConnectList.addAll(preferenceCategoryList);
            }
        }
        return toConnectList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public StringList getRelatedProductCategoryPlatformList(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectList = new StringList();
        MapList connectedList = getRelatedProductCategoryPlatform(context, domainObject);
        if (null != connectedList && !connectedList.isEmpty()) {
            for (Object object : connectedList) {
                objectList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public MapList getRelatedProductCategoryPlatform(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelects = new StringList();
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
        StringList relSelects = new StringList();
        relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
        MapList relatedObjects = domainObject.getRelatedObjects(context,
                pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM,
                PreferenceConstants.Type.TYPE_PICKLIST_PLATFORM.getName(context),
                objectSelects,
                relSelects,
                false,
                true,
                (short) 1,
                DomainConstants.EMPTY_STRING,
                DomainConstants.EMPTY_STRING,
                0);

        return relatedObjects;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public StringList getRelatedMaterialFunctionList(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectList = new StringList();
        MapList connectedList = getRelatedMaterialFunction(context, domainObject);
        if (null != connectedList && !connectedList.isEmpty()) {
            for (Object object : connectedList) {
                objectList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public MapList getRelatedMaterialFunction(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelects = new StringList();
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
        StringList relSelects = new StringList();
        relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
        MapList relatedObjects = domainObject.getRelatedObjects(context,
                pgV3Constants.REL_MATERIAL_FUNCTIONALITY,
                PreferenceConstants.Type.TYPE_PICKLIST_MATERIAL_FUNCTION_GLOBAL.getName(context),
                objectSelects,
                relSelects,
                false,
                true,
                (short) 1,
                DomainConstants.EMPTY_STRING,
                DomainConstants.EMPTY_STRING,
                0);

        return relatedObjects;
    }

    /**
     * @param context
     * @param domainObject
     * @param materialFunctions
     * @throws FrameworkException
     */
    public void applyMaterialFunctionGlobal(Context context, DomainObject domainObject, String materialFunctions, boolean flag) throws FrameworkException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(materialFunctions)) {
                StringList toConnectList = getMaterialFunctionToConnect(context, domainObject, materialFunctions);
                if (null != toConnectList && !toConnectList.isEmpty()) {
                    String[] toConnectArr = toConnectList.toArray(String[]::new);
                    DomainRelationship.connect(context, domainObject, pgV3Constants.REL_MATERIAL_FUNCTIONALITY, true, toConnectArr);
                    updateMaterialFunctionAttribute(context, domainObject, toConnectArr);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error Connecting Material Function: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param toConnectArr
     * @throws FrameworkException
     */
    public void updateMaterialFunctionAttribute(Context context, DomainObject domainObject, String[] toConnectArr) throws FrameworkException {
        MapList objectList = DomainObject.getInfo(context, toConnectArr, StringList.create(DomainConstants.SELECT_NAME));
        List<String> nameList = new ArrayList<>();
        if (null != objectList && !objectList.isEmpty()) {
            for (Object object : objectList) {
                nameList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
            }
        }
        if (!nameList.isEmpty()) { // update pgMaterialFunctionGlobal attribute (multi-value)
            domainObject.setAttributeValue(context, PropertyUtil.getSchemaProperty(context, "attribute_pgMaterialFunctionGlobal"), String.join(pgV3Constants.SYMBOL_COMMA, nameList));
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param materialFunctions
     * @return
     * @throws FrameworkException
     */
    public StringList getMaterialFunctionToConnect(Context context, DomainObject domainObject, String materialFunctions) throws FrameworkException {
        StringList toConnectList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(materialFunctions)) {
            logger.log(Level.INFO, "Material function from preference: " + materialFunctions);
            StringList preferenceFunctionList = StringUtil.split(materialFunctions, pgV3Constants.SYMBOL_PIPE); // material function from preference.
            StringList connectedFunctionList = getRelatedMaterialFunctionList(context, domainObject); // material function which is already connected.
            logger.log(Level.INFO, "Material function which is already connected: " + connectedFunctionList);
            if (null != connectedFunctionList && !connectedFunctionList.isEmpty()) {
                for (String preferenceFunctionID : preferenceFunctionList) {
                    if (!connectedFunctionList.contains(preferenceFunctionID)) {
                        toConnectList.add(preferenceFunctionID);
                    }
                }
            } else {
                toConnectList.addAll(preferenceFunctionList);
            }
        }
        return toConnectList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public StringList getRelatedReportedFunctionList(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectList = new StringList();
        MapList relatedReportedFunction = getRelatedReportedFunction(context, domainObject);
        if (null != relatedReportedFunction && !relatedReportedFunction.isEmpty()) {
            for (Object object : relatedReportedFunction) {
                objectList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public MapList getRelatedReportedFunction(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelects = new StringList();
        objectSelects.add(DomainConstants.SELECT_ID);
        StringList relSelects = new StringList();
        relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
        MapList relatedObjects = domainObject.getRelatedObjects(context,
                pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION,
                PreferenceConstants.Type.TYPE_PICKLIST_REPORTED_FUNCTION.getName(context),
                objectSelects,
                relSelects,
                false,
                true,
                (short) 1,
                DomainConstants.EMPTY_STRING,
                DomainConstants.EMPTY_STRING,
                0);

        return relatedObjects;
    }

    /**
     * @param context
     * @param domainObject
     * @param reportFunction
     * @throws FrameworkException
     */
    public void applyReportedFunction(Context context, DomainObject domainObject, String reportFunction, boolean flag) throws FrameworkException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(reportFunction)) { // has reported function in preferences.
                StringList toConnectList = getReportedFunctionToConnect(context, domainObject, reportFunction);
                if (null != toConnectList && !toConnectList.isEmpty()) {
                    String[] toConnectArr = toConnectList.toArray(String[]::new);
                    DomainRelationship.connect(context, domainObject, pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION, true, toConnectArr);
                    logger.log(Level.INFO, "Applied Reported function");
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error Connecting Reported Function: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param reportFunctions
     * @return
     * @throws FrameworkException
     */
    public StringList getReportedFunctionToConnect(Context context, DomainObject domainObject, String reportFunctions) throws FrameworkException {
        StringList toConnectList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(reportFunctions)) { // has reported function in preferences.
            logger.log(Level.INFO, "Reported function from preference: " + reportFunctions);
            StringList preferenceReportedFuncList = StringUtil.split(reportFunctions, pgV3Constants.SYMBOL_PIPE);
            StringList connectedReportedFuncList = getRelatedReportedFunctionList(context, domainObject);
            logger.log(Level.INFO, "Reported function which is already connected: " + connectedReportedFuncList);
            if (null != connectedReportedFuncList && !connectedReportedFuncList.isEmpty()) {
                for (String preferenceReportedFunctionID : preferenceReportedFuncList) {
                    if (!connectedReportedFuncList.contains(preferenceReportedFunctionID)) {
                        toConnectList.add(preferenceReportedFunctionID);
                    }
                }
            } else {
                toConnectList.addAll(preferenceReportedFuncList);
            }
        }
        return toConnectList;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getBusinessAreaJSON(Context context) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        MapList resultList = userPreferenceUtil.findBusinessArea(context);
        if (null != resultList && !resultList.isEmpty()) {
            resultList.sort("name", "ascending", "String");
            jsonArr.add(appendBlankSelect());
            JsonObjectBuilder json;
            for (Object object : resultList) {
                json = Json.createObjectBuilder();
                json.add(DomainConstants.SELECT_ID, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
                json.add(DomainConstants.SELECT_NAME, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), (String) ((Map<Object, Object>) object).get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.INFO, "No Business Area found");
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }

    /**
     * @return
     */
    private JsonObjectBuilder appendBlankSelect() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add(DomainConstants.SELECT_ID, DomainConstants.EMPTY_STRING);
        json.add(DomainConstants.SELECT_NAME, DomainConstants.EMPTY_STRING);
        json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), DomainConstants.EMPTY_STRING);
        return json;
    }

    /**
     * @param context
     * @param physicalId
     * @return
     * @throws FrameworkException
     */
    public String getPipeSeparatedObjectIDFromPhysicalID(Context context, String physicalId) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
            StringList physicalIDList = StringUtil.split(physicalId, pgV3Constants.SYMBOL_PIPE);
            MapList objectList = DomainObject.getInfo(context, physicalIDList.toStringArray(), StringList.create(DomainConstants.SELECT_ID));
            List<String> iDList = new ArrayList<>();
            if (null != objectList && !objectList.isEmpty()) {
                Map<Object, Object> objectMap;
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    iDList.add((String) objectMap.get(DomainConstants.SELECT_ID));
                }
            }
            ids = String.join(pgV3Constants.SYMBOL_PIPE, iDList);
        }
        return ids;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public CreateProductScreen getCreateProductScreenPreference(Context context) throws MatrixException {
        CreateProductScreen createProductScreen = null;
        PreferenceConfigLoader preferenceConfigLoader = getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            createProductScreen = new CreateProductScreen.Builder(context, preferenceConfigLoader.getPreferenceConfig()).build();
        }
        return createProductScreen;
    }

    /**
     * @param context
     * @throws MatrixException
     */
    public void getCreateProductScreenInstance(Context context) throws MatrixException {
        PreferenceConfigLoader preferenceConfigLoader = getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            CreateProductScreen createProductScreen = new CreateProductScreen.Builder(context, preferenceConfigLoader.getPreferenceConfig()).build();
            logger.log(Level.INFO, "CreateProductScreen: " + createProductScreen);
        }
    }

    /**
     * @param context
     * @param type
     * @return
     */
    public ProductData getProductDataOptions(Context context, String type) {
        ProductData productData = new ProductData.Load(context).now(type);
        return productData;
    }

    /**
     * @param context
     * @param type
     * @return
     * @throws FrameworkException
     */
    public String getProductDataOptionJson(Context context, String type) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        ProductData productDataOptions = getProductDataOptions(context, type);
        if (null != productDataOptions) {
            JsonObjectBuilder json;
            json = Json.createObjectBuilder();
            json.add("type", productDataOptions.getType());
            json.add("phase", productDataOptions.getPhase());
            json.add("segmentName", productDataOptions.getSegmentName());
            json.add("segmentID", productDataOptions.getSegmentID());
            json.add("changeTemplateName", productDataOptions.getChangeTemplateName());
            json.add("changeTemplateID", productDataOptions.getChangeTemplateOID());
            json.add("primaryOrgName", productDataOptions.getPrimaryOrgName());
            json.add("primaryOrgID", productDataOptions.getPrimaryOrgID());
            jsonArr.add(json);
        }
        jsonOutput.add("output", jsonArr.build());
        return jsonOutput.build().toString();
    }


    /**
     * @param context
     * @param cosmeticFormulationID
     * @param action
     */
    public void floatUserPreferencesOnFormulation(Context context, String cosmeticFormulationID, String action, boolean usePreference) {
        try {
            if (usePreference) {
                UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
                if (UIUtil.isNotNullAndNotEmpty(cosmeticFormulationID)) {
                    DomainObject cosmeticObj = DomainObject.newInstance(context, cosmeticFormulationID);
                    Map<Object, Object> cosmeticFormulationInfo = getCosmeticFormulationPartInfo(context, cosmeticObj);
                    boolean isFormulationPartProcessed = Boolean.FALSE;
                    if (null != cosmeticFormulationInfo && !cosmeticFormulationInfo.isEmpty()) {
                        String hasFormulationPart = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.COSMETIC_FORMULATION_HAS_FORMULATION_PART.get());
                        String hasFormulationProcess = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.COSMETIC_FORMULATION_HAS_FORMULATION_PROCESS.get());
                        String type = (String) cosmeticFormulationInfo.get(DomainConstants.SELECT_TYPE);
                        logger.log(Level.INFO, "Incoming Action Copy or Create?: {0}", action);
                        if (pgV3Constants.TYPE_COSMETICFORMULATION.equalsIgnoreCase(type)) {
                            if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
                                applySegmentOnCosmeticFormulation(context, cosmeticObj, cosmeticFormulationInfo);
                                applyPrimaryOrgOnFormulation(context, cosmeticObj, cosmeticFormulationInfo);
                                String[] cosmeticArgs = new String[]{pgV3Constants.TYPE_COSMETICFORMULATION, cosmeticFormulationID};
                                preferenceUtil.setIPSecurity(context, cosmeticArgs);
                            }
                            applySharingMembers(context, type, cosmeticFormulationID);
                            if ("TRUE".equalsIgnoreCase(hasFormulationPart)) { // float phase (only) and manufacturing status (preference) from cosmetic to formulation part
                                isFormulationPartProcessed = floatPreferenceOnFormulationPart(context, cosmeticFormulationInfo, action);
                            }
                            if ("TRUE".equalsIgnoreCase(hasFormulationProcess)) {
                                floatPreferenceOnFormulationProcess(context, cosmeticFormulationInfo, action, isFormulationPartProcessed);
                            }
                        }
                    }
                } else {
                    logger.log(Level.INFO, "Incoming Cosmetic Formulation ID is empty");
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
        }
    }

    /**
     * @param context
     * @param cosmeticFormulationInfo
     * @param action
     * @return
     * @throws Exception
     */
    public boolean floatPreferenceOnFormulationPart(Context context, Map<Object, Object> cosmeticFormulationInfo, String action) throws Exception {
        boolean isFormulationPartProcessed = Boolean.FALSE;
        String formulationPartType = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_TYPE.get());
        String uptPhysicalId = (String) cosmeticFormulationInfo.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context));
        StringList seletable = new StringList(2);
        if(UIUtil.isNotNullAndNotEmpty(uptPhysicalId)) {
            seletable.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getSelect(context));
            seletable.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_RELEASE_PHASE.getSelect(context));
            seletable.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_LIFECYCLE_STATUS.getSelect(context));
            Map info = DomainObject.newInstance(context, uptPhysicalId).getInfo(context, seletable);
        if (pgV3Constants.TYPE_FORMULATIONPART.equals(formulationPartType)) {
            UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
            String formulationPartID = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_ID.get());
            if (UIUtil.isNotNullAndNotEmpty(formulationPartID)) {
                DomainObject formulationPartObj = DomainObject.newInstance(context, formulationPartID);
                Map<Object, Object> formulationPartInfo = getFormulationPartInfo(context, formulationPartObj);

                    //String productPreferenceType = Preferences.ProductPreference.PART_TYPE.getName(context);
                    String productPreferenceType = (String) info.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getSelect(context));
                if (pgV3Constants.TYPE_FORMULATIONPART.equals(productPreferenceType)) {
                        //String productPreferencePhase = Preferences.ProductPreference.PHASE.getName(context);
                        String productPreferencePhase = (String) info.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_RELEASE_PHASE.getSelect(context));
                    String releasePhaseOnCosmetic = (String) cosmeticFormulationInfo.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
                    if (UIUtil.isNotNullAndNotEmpty(releasePhaseOnCosmetic) && releasePhaseOnCosmetic.equalsIgnoreCase(productPreferencePhase)) {
                        Map<String, String> attributeMap = new HashMap<>();
                        attributeMap.put(pgV3Constants.STR_RELEASE_PHASE, productPreferencePhase);
                            attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, (String) info.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_LIFECYCLE_STATUS.getSelect(context)));
                        formulationPartObj.setId(formulationPartID);
                            UserPreferenceTemplateUtil.addUPTPhysicalIdExtension(context, uptPhysicalId, formulationPartObj);
                        formulationPartObj.setAttributeValues(context, attributeMap);
                        logger.log(Level.INFO, "Attributes Updated on Formualtion Part: {0}", attributeMap);
                        isFormulationPartProcessed = Boolean.TRUE;
                    }
                }
                if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
                    applySegmentOnFormulationPart(context, formulationPartObj, formulationPartInfo);
                    applyPrimaryOrgOnFormulation(context, formulationPartObj, formulationPartInfo);
                }
                String[] tempArgs = new String[]{pgV3Constants.TYPE_FORMULATIONPART, formulationPartID};
                preferenceUtil.setPreferencesOnCopyPart(context, tempArgs);
            }
        } else {
            logger.log(Level.INFO, "Cosmetic Formulation does not have related Formulation Part type");
        }
        }else {
            logger.log(Level.INFO, "Incoming User Preference Template (UPT) ID is empty");
        }
        return isFormulationPartProcessed;
    }

    /**
     * @param context
     * @param cosmeticFormulationInfo
     * @param action
     * @param isFormulationPartProcessed
     * @throws MatrixException
     */
    public void floatPreferenceOnFormulationProcess(Context context, Map<Object, Object> cosmeticFormulationInfo, String action, boolean isFormulationPartProcessed) throws MatrixException {
        String formulationProcessType = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PROCESS_TYPE.get());
        if (pgV3Constants.TYPE_FORMULATIONPROCESS.equals(formulationProcessType)) {
            String formulationProcessID = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PROCESS_ID.get());
            if (UIUtil.isNotNullAndNotEmpty(formulationProcessID)) {
                DomainObject formulationProcessObj = DomainObject.newInstance(context, formulationProcessID);
                if (isFormulationPartProcessed) {
                    String releasePhaseOnCosmetic = (String) cosmeticFormulationInfo.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
                    formulationProcessObj.setAttributeValue(context, pgV3Constants.STR_RELEASE_PHASE, releasePhaseOnCosmetic);
                    logger.log(Level.INFO, "Attribute updated on Formulation Process ID: {0}", formulationProcessID);
                }
                if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
                    Map<Object, Object> formulationProcessInfo = getFormulationProcessInfo(context, formulationProcessObj);
                    applyPrimaryOrgOnFormulation(context, formulationProcessObj, formulationProcessInfo);
                }
            }
        } else {
            logger.log(Level.INFO, "Cosmetic Formulation does not have related Formulation Process type");
        }
    }

    /**
     * @param context
     * @param type
     * @param objectOid
     * @throws Exception
     */
    void applySharingMembers(Context context, String type, String objectOid) throws Exception {
        try {
            if (pgV3Constants.TYPE_COSMETICFORMULATION.equals(type)) {
                String sharingMemberIDs = Preferences.DefaultCreatePartPreference.DEFAULT_SHARING_MEMBERS.getID(context);
                if (UIUtil.isNotNullAndNotEmpty(sharingMemberIDs)) { // if user has set (share with members) in preferences.
                    applySharingMemberPreferences(context, objectOid, sharingMemberIDs);
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param objectOid
     * @param preferenceMemberIDs
     * @throws FrameworkException
     */
    public void applySharingMemberPreferences(Context context, String objectOid, String preferenceMemberIDs) throws Exception {
        boolean isCtxPushed = Boolean.FALSE;
        try {
            if (UIUtil.isNotNullAndNotEmpty(preferenceMemberIDs)) {
                StringList memberIDList = StringUtil.split(preferenceMemberIDs, PreferenceConstants.Basic.SYMBOL_PIPE.get());// multiple (share with members) are stored on preferences.
                // push context is required to provide multiple ownership access.
                ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                isCtxPushed = Boolean.TRUE;
                boolean isEBP;
                String access;
                String personName;

                List<String> preferenceMemberList = new ArrayList<>();
                for (String memberID : memberIDList) {
                	//Added by DSM for 22x CW-06 for Requirement #47975 and 47969 - START	
                	if(UIUtil.isNotNullAndNotEmpty(memberID)) {
                		personName = DomainObject.newInstance(context, memberID).getInfo(context, DomainConstants.SELECT_NAME);
                		preferenceMemberList.add(personName);
                	}
                	//Added by DSM for 22x CW-06 for Requirement #47975 and 47969 - END
                }
                logger.log(Level.INFO, "Preference Sharing Members List: {0}", preferenceMemberList);
                List<String> existingMemberList = getAcessMemberUserList(context, objectOid);
                logger.log(Level.INFO, "Existing Sharing Members List: {0}", existingMemberList);
                List<String> deleteOwnershipList = new ArrayList<>();
                for (String existingMember : existingMemberList) {
                    if (!preferenceMemberList.contains(existingMember)) {
                        deleteOwnershipList.add(existingMember);
                    }
                }
                logger.log(Level.INFO, "Delete Sharing Members List: {0}", deleteOwnershipList);

                // delete member ownership which is not present in preferences.
                for (String memberName : deleteOwnershipList) {
                    DomainAccess.deleteObjectOwnership(context, objectOid, DomainConstants.EMPTY_STRING, memberName + "_PRJ", DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                }

                for (String memberID : memberIDList) {
                	//Added by DSM for 22x CW-06 for Requirement #47975 and 47969 - START	
                	if(UIUtil.isNotNullAndNotEmpty(memberID)) {
                		isEBP = isPersonEBPUser(context, memberID);
                		access = (isEBP) ? PreferenceConstants.Basic.DSM_SHARING_MEMBER_ACCESSES_FOR_EBP.get() : pgV3Constants.ALL;
                		personName = DomainObject.newInstance(context, memberID).getInfo(context, DomainConstants.SELECT_NAME);
                		personName += "_PRJ";
                		//Added by DSM (Sogeti) for 22x CW-06 Requirement#47860 - Start
                		String resultMQL = MqlUtil.mqlCommand(context, false, "list role \"" + personName + "\"", false);
                		if(UIUtil.isNotNullAndNotEmpty(resultMQL)){ 
                			DomainAccess.createObjectOwnership(context, objectOid, "-", personName, access, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                		}
                		//Added by DSM (Sogeti) for 22x CW-06 Requirement#47860 - End
                	}
                	//Added by DSM for 22x CW-06 for Requirement #47975 and 47969 - END	
                }

                ContextUtil.popContext(context);
                isCtxPushed = Boolean.FALSE;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
    }

    /**
     * @param context
     * @param formulationObj
     * @throws MatrixException
     */
    void applySegmentOnFormulationPart(Context context, DomainObject formulationObj, Map<Object, Object> objectMap) throws MatrixException {
        if (null != objectMap && !objectMap.isEmpty()) {
            boolean connect = Boolean.FALSE;
            String productPreferenceSegmentID = Preferences.ProductPreference.SEGMENT.getID(context);
            String productPreferenceSegmentName = Preferences.ProductPreference.SEGMENT.getName(context);
            if (UIUtil.isNotNullAndNotEmpty(productPreferenceSegmentID)) { // if has segment on preference.
                String hasRelatedSegment = (String) objectMap.get((PreferenceConstants.Basic.HAS_RELATED_SEGMENT.get()));
                if ("TRUE".equalsIgnoreCase(hasRelatedSegment)) { // if Formulation Part already has Segment.
                    String segmentID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_ID.get()));
                    if (UIUtil.isNotNullAndNotEmpty(segmentID)) { // if segment on preference is not null/empty
                        if (!segmentID.equalsIgnoreCase(productPreferenceSegmentID)) { // if cosmetic formulation has segment.
                            String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_CONNECTION_ID.get()));
                            if (UIUtil.isNotNullAndNotEmpty(relID)) {
                                DomainRelationship.disconnect(context, relID);
                                logger.log(Level.INFO, "Disconnected unmatched Segment from Formulation Part");
                            }
                            connect = Boolean.TRUE;
                        }
                    }
                } else {
                    connect = Boolean.TRUE;
                }
                if (connect) {
                    // connect segment from preference.
                    DomainRelationship.connect(context, formulationObj,
                            pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT,
                            DomainObject.newInstance(context, productPreferenceSegmentID));
                    formulationObj.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGSEGMENT, productPreferenceSegmentName);
                    logger.log(Level.INFO, "Formulation Part Segment connected and attribute updated: {0}", productPreferenceSegmentName);
                }
            }
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param objectMap
     * @throws MatrixException
     */
    void applyPrimaryOrgOnFormulation(Context context, DomainObject domainObject, Map<Object, Object> objectMap) throws MatrixException {
        if (null != objectMap && !objectMap.isEmpty()) {
            boolean connect = Boolean.FALSE;
            String preferencePrimaryOrgID = Preferences.IPSecurityPreference.PRIMARY_ORGANIZATION.getID(context); // get preference primary org is present
            if (UIUtil.isNotNullAndNotEmpty(preferencePrimaryOrgID)) { // if preference primary org is present
                String hasRelatedPrimaryOrg = (String) objectMap.get(PreferenceConstants.Basic.HAS_PRIMARY_ORG.get());
                if ("TRUE".equalsIgnoreCase(hasRelatedPrimaryOrg)) { // if has primary org in preferences
                    String relatedPrimaryOrgID = (String) objectMap.get(PreferenceConstants.Basic.RELATED_PRIMARY_ORG.get());
                    if (UIUtil.isNotNullAndNotEmpty(relatedPrimaryOrgID)) { // if existing primary is valid.
                        if (!relatedPrimaryOrgID.equalsIgnoreCase(preferencePrimaryOrgID)) { // if existing primary org is not equal to preference primary org.
                            String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_PRIMARY_ORG_CONNECTION_ID.get())); // get existing primary org rel id.
                            if (UIUtil.isNotNullAndNotEmpty(relID)) { // if rel id is present.
                                DomainRelationship.disconnect(context, relID); // disconnect.
                                connect = Boolean.TRUE;
                            }
                        }
                    } else {
                        connect = Boolean.TRUE;
                    }
                } else {
                    connect = Boolean.TRUE;
                }
                if (connect) {
                    DomainRelationship.connect(context, domainObject,
                            pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION,
                            DomainObject.newInstance(context, preferencePrimaryOrgID));
                }
            }
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param objectMap
     * @throws MatrixException
     */
    void applySegmentOnCosmeticFormulation(Context context, DomainObject domainObject, Map<Object, Object> objectMap) throws MatrixException {
        if (null != objectMap && !objectMap.isEmpty()) {
            boolean connect = Boolean.FALSE;
            String productPreferenceSegmentID = Preferences.ProductPreference.SEGMENT.getID(context);
            String productPreferenceSegmentName = Preferences.ProductPreference.SEGMENT.getName(context);
            if (UIUtil.isNotNullAndNotEmpty(productPreferenceSegmentID)) { // if has segment on preference.
                String hasRelatedSegment = (String) objectMap.get((PreferenceConstants.Basic.HAS_RELATED_SEGMENT.get()));
                if ("TRUE".equalsIgnoreCase(hasRelatedSegment)) {
                    String segmentID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_ID.get()));
                    if (!segmentID.equalsIgnoreCase(productPreferenceSegmentID)) { // if cosmetic formulation has segment.
                        String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_CONNECTION_ID.get()));
                        if (UIUtil.isNotNullAndNotEmpty(relID)) {
                            DomainRelationship.disconnect(context, relID);
                        }
                        connect = Boolean.TRUE;
                    }
                } else {
                    connect = Boolean.TRUE;
                }
                if (connect) {
                    // connect segment from preference.
                    DomainRelationship.connect(context, domainObject,
                            pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT,
                            DomainObject.newInstance(context, productPreferenceSegmentID));
                    String segmentPhysicalID = (String) objectMap.get((PreferenceConstants.Basic.PHYSICAL_ID.get()));
                    domainObject.setAttributeValue(context, PropertyUtil.getSchemaProperty(context, "attribute_pgEngFrmSegment"), segmentPhysicalID);
                    logger.log(Level.INFO, "Cosmetic Formulation connected Segment & attribute updated: {0}", segmentPhysicalID);
                }
            }
        }
    }

    /**
     * @param context
     * @param cosmeticFormulationID
     * @return
     * @throws FrameworkException
     */
    Map<Object, Object> getCosmeticFormulationPartInfo(Context context, DomainObject cosmeticObj) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (null != cosmeticObj) {
            StringList objectSelects = new StringList();
            objectSelects.add(DomainConstants.SELECT_TYPE);
            objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
            objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);                  // get release phase on Cosmetic Formulation.
            objectSelects.add(PreferenceConstants.Basic.SELECT_FORMULATION_PART_ID.get());    // get Formulation Part
            objectSelects.add(PreferenceConstants.Basic.SELECT_FORMULATION_PROCESS_ID.get()); // get Formulation Process
            objectSelects.add(PreferenceConstants.Basic.SELECT_FORMULATION_PART_TYPE.get());    // get Formulation Part
            objectSelects.add(PreferenceConstants.Basic.SELECT_FORMULATION_PROCESS_TYPE.get()); // get Formulation Process
            objectSelects.add(PreferenceConstants.Basic.COSMETIC_FORMULATION_HAS_FORMULATION_PART.get());
            objectSelects.add(PreferenceConstants.Basic.COSMETIC_FORMULATION_HAS_FORMULATION_PROCESS.get());
            objectSelects.add(PreferenceConstants.Basic.HAS_RELATED_SEGMENT.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_SEGMENT_ID.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_SEGMENT_NAME.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_SEGMENT_CONNECTION_ID.get());
            objectSelects.add(PreferenceConstants.Basic.HAS_PRIMARY_ORG.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_PRIMARY_ORG.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_PRIMARY_ORG_CONNECTION_ID.get());
            objectSelects.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context));
            objectMap = cosmeticObj.getInfo(context, objectSelects);
        }
        return objectMap;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    Map<Object, Object> getFormulationPartInfo(Context context, DomainObject domainObject) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (null != domainObject) {
            StringList objectSelects = new StringList();
            objectSelects.add(DomainConstants.SELECT_TYPE);
            objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
            objectSelects.add(PreferenceConstants.Basic.HAS_RELATED_SEGMENT.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_SEGMENT_ID.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_SEGMENT_NAME.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_SEGMENT_CONNECTION_ID.get());
            objectSelects.add(PreferenceConstants.Basic.HAS_PRIMARY_ORG.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_PRIMARY_ORG_CONNECTION_ID.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_PRIMARY_ORG.get());
            objectMap = domainObject.getInfo(context, objectSelects);
        }
        return objectMap;
    }

    Map<Object, Object> getFormulationProcessInfo(Context context, DomainObject domainObject) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (null != domainObject) {
            StringList objectSelects = new StringList();
            objectSelects.add(PreferenceConstants.Basic.HAS_PRIMARY_ORG.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_PRIMARY_ORG_CONNECTION_ID.get());
            objectSelects.add(PreferenceConstants.Basic.RELATED_PRIMARY_ORG.get());
            objectMap = domainObject.getInfo(context, objectSelects);
        }
        return objectMap;
    }

    /**
     * @param context
     * @param domainObject
     * @param businessAreaIDs
     * @return
     * @throws FrameworkException
     */
    public StringList getBusinessAreaToConnect(Context context, DomainObject domainObject, String businessAreaIDs) throws FrameworkException {
        StringList toConnectList = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(businessAreaIDs)) {
            logger.log(Level.INFO, "Business area from preference: " + businessAreaIDs);
            StringList preferenceBusinessAreaList = StringUtil.split(businessAreaIDs, pgV3Constants.SYMBOL_PIPE);
            StringList connectedBusinessAreaList = getRelatedBusinessAreaList(context, domainObject);
            logger.log(Level.INFO, "Business area which is already connected: " + connectedBusinessAreaList);
            if (null != connectedBusinessAreaList && !connectedBusinessAreaList.isEmpty()) {
                for (String preferenceBusinessArea : preferenceBusinessAreaList) {
                    if (!connectedBusinessAreaList.contains(preferenceBusinessArea)) {
                        toConnectList.add(preferenceBusinessArea);
                    }
                }
            } else {
                toConnectList.addAll(preferenceBusinessAreaList);
            }
        }
        return toConnectList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public StringList getRelatedBusinessAreaList(Context context, DomainObject domainObject) throws FrameworkException {
        StringList relatedList = new StringList();
        MapList objectList = getRelatedBusinessArea(context, domainObject);
        if (null != objectList && !objectList.isEmpty()) {
            for (Object object : objectList) {
                relatedList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
            }
        }
        return relatedList;
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public StringList getRelatedPlantList(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectList = new StringList();
        MapList relatedPlants = getRelatedPlants(context, domainObject);
        if (null != relatedPlants && !relatedPlants.isEmpty()) {
            for (Object object : relatedPlants) {
                objectList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
            }
        }
        return objectList;
    }


    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void updateObjectPreferences(Context context, String[] args) throws Exception {
        String objectType = args[0];
        String objectOid = args[1];
        if (UIUtil.isNotNullAndNotEmpty(objectType) && UIUtil.isNotNullAndNotEmpty(objectOid)) {
            logger.log(Level.INFO, "Update Preference on Incoming Object > | {0} | {1} |", new Object[]{objectType, objectOid});
            DomainObject domainObject = DomainObject.newInstance(context, objectOid);
            String objectPolicy = domainObject.getInfo(context, DomainConstants.SELECT_POLICY);
            String symbolicType = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, objectType, true);
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
            if (preferenceConfigLoader.isLoaded()) {
                PreferenceConfig preferenceConfig = preferenceConfigLoader.getPreferenceConfig();
                DefaultCreatePartPreferenceSetting defaultCreatePartPreferenceSetting = new DefaultCreatePartPreferenceSetting(context, preferenceConfig);
                defaultCreatePartPreferenceSetting.apply(objectType, objectOid, symbolicType, objectPolicy);

                ProductPreferenceConfig productPreferenceConfig = preferenceConfig.getProductPreferenceConfig();
                PackagingPreferenceConfig packagingPreferenceConfig = preferenceConfig.getPackagingPreferenceConfig();
                RawMaterialPreferenceConfig rawMaterialPreferenceConfig = preferenceConfig.getRawMaterialPreferenceConfig();

                if (!isEquivalents(objectPolicy)) { // if incoming type is not MEP or SEP.
                    // handle special case for type (Formulation Process).
                    if (isProductPart(productPreferenceConfig.getTypes(), symbolicType) || pgV3Constants.TYPE_FORMULATIONPROCESS.equalsIgnoreCase(objectType)) { // if incoming type is of (Product)
                        ProductPreferenceSetting productPreferenceSetting = new ProductPreferenceSetting
                                .Apply(context, productPreferenceConfig)
                                .now(domainObject, objectOid, symbolicType);
                    }
                    if (isPackagingPart(packagingPreferenceConfig.getTypes(), symbolicType)) { // if incoming type is of (Product)
                        PackagingPreferenceSetting packagingPreferenceSetting = new PackagingPreferenceSetting
                                .Apply(context, packagingPreferenceConfig)
                                .now(domainObject, objectOid, symbolicType);
                    }
                    if (isRawMaterialPart(rawMaterialPreferenceConfig.getTypes(), symbolicType)) { // if incoming type is of (Product)
                        RawMaterialPreferenceSetting rawMaterialPreferenceSetting = new RawMaterialPreferenceSetting
                                .Apply(context, rawMaterialPreferenceConfig)
                                .now(domainObject, objectOid, symbolicType);
                    }
                }
            } else {
                logger.log(Level.WARNING, "Failed to load page pgUserPreferenceConfig with error: {0}", preferenceConfigLoader.getErrorMessage());
            }
        }
    }

    /**
     * @param types
     * @param incomingSymbolicType
     * @return
     */
    boolean isProductPart(String types, String incomingSymbolicType) {
        return StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(incomingSymbolicType);
    }

    /**
     * @param types
     * @param incomingSymbolicType
     * @return
     */
    boolean isPackagingPart(String types, String incomingSymbolicType) {
        return StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(incomingSymbolicType);
    }

    /**
     * @param types
     * @param incomingSymbolicType
     * @return
     */
    boolean isRawMaterialPart(String types, String incomingSymbolicType) {
        return StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(incomingSymbolicType);
    }


    /**
     * @param policy
     * @return
     */
    private boolean isEquivalents(String policy) {
        return (pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(policy) || pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equals(policy));
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void setPreferencesOnCopyPart(Context context, String[] args) throws Exception {
        try {
            setIPSecurity(context, args); // update security.
            PartCreateAction partCreateAction = new PartCreateAction();
            //partCreateAction.setPreferencesOnPartCreation(context, args);
            partCreateAction.propagateUPTAttributesOnPartCreation(context, args);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void setIPSecurity(Context context, String[] args) throws Exception {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            IPSecurityControlSetting ipSecurityControlSetting = new IPSecurityControlSetting.Apply(context, preferenceConfigLoader.getPreferenceConfig().getIpSecurityControlPreference()).now(args);
            if (!ipSecurityControlSetting.isApplied()) {
                logger.log(Level.WARNING, "Failed to apply IP Security");
            }
        }
    }

    /**
     * @param context
     * @param domainObject
     * @throws MatrixException
     */
    public void applyBusinessUseClass(Context context, DomainObject domainObject) throws MatrixException {
        String preferenceBusinessUse = Preferences.IPSecurityPreference.BUSINESS_USE.getID(context);
        applyIPSecurityClass(context, domainObject, preferenceBusinessUse);
    }

    /**
     * @param context
     * @param domainObject
     * @throws MatrixException
     */
    public void applyHighlyRestrictedUseClass(Context context, DomainObject domainObject) throws MatrixException {
        String preferenceBusinessUse = Preferences.IPSecurityPreference.HIGHLY_RESTRICTED.getID(context);
        applyIPSecurityClass(context, domainObject, preferenceBusinessUse);
    }

    /**
     * @param context
     * @param domainObject
     * @param preferenceIPSecurity
     * @throws FrameworkException
     */
    public void applyIPSecurityClass(Context context, DomainObject domainObject, String preferenceIPSecurity) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(preferenceIPSecurity)) {
            logger.info("_______________ - Attach IP Security Start - _______________");
            refreshRelationship(context,
                    domainObject,
                    preferenceIPSecurity,
                    getRelatedSecurityControl(context, domainObject),
                    PropertyUtil.getSchemaProperty(context, "relationship_ProtectedItem"),
                    false,
                    false);
            logger.info("_______________ - Attach IP Security End - _______________");
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param preferenceBusinessAreaIDs
     * @throws FrameworkException
     */
    public void applyBusinessArea(Context context, DomainObject domainObject, String preferenceBusinessAreaIDs) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(preferenceBusinessAreaIDs)) {
            logger.info("_______________ - Attach Business Area Start - _______________");
            refreshRelationship(context,
                    domainObject,
                    preferenceBusinessAreaIDs,
                    getRelatedBusinessArea(context, domainObject),
                    pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA,
                    true,
                    false);
            logger.info("_______________ - Attach Business Area End - _______________");
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param preferenceReportFunctionIDs
     * @throws FrameworkException
     */
    public void applyReportedFunction(Context context, DomainObject domainObject, String preferenceReportFunctionIDs) throws FrameworkException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(preferenceReportFunctionIDs)) { // has reported function in preferences.
                logger.info("_______________ - Attach Reported Function Start - _______________");
                refreshRelationship(context,
                        domainObject,
                        preferenceReportFunctionIDs,
                        getRelatedReportedFunction(context, domainObject),
                        pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION,
                        true,
                        false);
                logger.info("_______________ - Attach Reported Function End - _______________");
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error Connecting Reported Function: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param productCategoryPlatformIDs
     * @throws FrameworkException
     */
    public void applyProductCategoryPlatform(Context context, DomainObject domainObject, String productCategoryPlatformIDs) throws FrameworkException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(productCategoryPlatformIDs)) {
                logger.info("_______________ - Attach Product Category Platform Start - _______________");
                Map<String, String> relAttributeMap = new HashMap<>();
                relAttributeMap.put(pgV3Constants.ATTRIBUTE_PGPLATFORMTYPE, PreferenceConstants.Basic.PRODUCT_CATEGORY_PLATFORM.get());
                refreshRelationshipWithAttribute(context,
                        domainObject,
                        productCategoryPlatformIDs,
                        getRelatedProductCategoryPlatform(context, domainObject),
                        pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM,
                        true,
                        relAttributeMap,
                        true);
                logger.info("_______________ - Attach Product Category Platform End - _______________");
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error Connecting Product Category Platform: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param materialFunctionIDs
     * @throws FrameworkException
     */
    public void applyMaterialFunctionGlobal(Context context, DomainObject domainObject, String materialFunctionIDs) throws FrameworkException {
        try {
            if (UIUtil.isNotNullAndNotEmpty(materialFunctionIDs)) {
                logger.info("_______________ - Attach Material Function Start - _______________*");
                refreshRelationship(context,
                        domainObject,
                        materialFunctionIDs,
                        getRelatedMaterialFunction(context, domainObject),
                        pgV3Constants.REL_MATERIAL_FUNCTIONALITY,
                        true,
                        true);
                logger.info("_______________ - Attach Material Function End - _______________*");
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error Connecting Material Function: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param fromObject
     * @param preferenceValue
     * @param relatedList
     * @param relationshipName
     * @param side
     * @param relAttributeMap
     * @param disconnectExisting
     * @throws FrameworkException
     */
    public void refreshRelationshipWithAttribute(Context context, DomainObject fromObject, String preferenceValue, MapList relatedList, String relationshipName, boolean side, Map<String, String> relAttributeMap, boolean disconnectExisting) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(preferenceValue)) {
            StringList preferenceValueList = StringUtil.split(preferenceValue, pgV3Constants.SYMBOL_PIPE);
            logger.log(Level.INFO, "Preference List: {0}", preferenceValueList);
            List<String> toDisconnectList = new ArrayList<>();
            List<String> alreadyConnectedList = new ArrayList<>();
            if (null != relatedList && !relatedList.isEmpty()) {
                Map<Object, Object> objectMap;
                String id;
                String relId;
                Iterator iterator = relatedList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    id = (String) objectMap.get(DomainConstants.SELECT_ID);
                    relId = (String) objectMap.get(DomainRelationship.SELECT_RELATIONSHIP_ID);
                    if (!preferenceValueList.contains(id)) {
                        toDisconnectList.add(relId);
                    } else {
                        alreadyConnectedList.add(id);
                    }
                }
            }
            logger.log(Level.INFO, "Connected List: {0}", alreadyConnectedList);
            List<String> toConnectList = new ArrayList<>();
            for (String preferenceBusinessUseID : preferenceValueList) {
                if (!alreadyConnectedList.contains(preferenceBusinessUseID)) {
                    toConnectList.add(preferenceBusinessUseID);
                }
            }
            if (disconnectExisting) {
                logger.log(Level.INFO, "ToDisconnect List: {0}", toDisconnectList);
                if (null != toDisconnectList && !toDisconnectList.isEmpty()) {
                    String[] toDisconnectArr = toDisconnectList.toArray(String[]::new);
                    DomainRelationship.disconnect(context, toDisconnectArr);
                }
            }
            logger.log(Level.INFO, "ToConnect List: {0}", toConnectList);
            DomainRelationship domainRelationship;
            DomainObject toObject = DomainObject.newInstance(context);
            if (null != toConnectList && !toConnectList.isEmpty()) {
                for (String toConnectID : toConnectList) {
                    if (UIUtil.isNotNullAndNotEmpty(toConnectID)) {
                        toObject.setId(toConnectID);
                        domainRelationship = toObject.connect(context, relationshipName, fromObject, side);
                        domainRelationship.setAttributeValues(context, relAttributeMap);
                    }
                }
            }
        }
    }

    /**
     * @param context
     * @param domainObject
     * @param preferenceValue
     * @param relatedList
     * @param relationshipName
     * @param side
     * @param updateObjectNameOnAttribute
     * @throws FrameworkException
     */
    public void refreshRelationship(Context context, DomainObject domainObject, String preferenceValue, MapList relatedList, String relationshipName, boolean side, boolean updateObjectNameOnAttribute) throws FrameworkException {
        if (UIUtil.isNotNullAndNotEmpty(preferenceValue)) {
            StringList preferenceValueList = StringUtil.split(preferenceValue, pgV3Constants.SYMBOL_PIPE);
            logger.log(Level.INFO, "Preferences List: {0}", preferenceValueList);
            List<String> toDisconnectList = new ArrayList<>();
            List<String> alreadyConnectedList = new ArrayList<>();
            if (null != relatedList && !relatedList.isEmpty()) {
                Map<Object, Object> objectMap;
                String id;
                String relId;
                Iterator iterator = relatedList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    id = (String) objectMap.get(DomainConstants.SELECT_ID);
                    relId = (String) objectMap.get(DomainRelationship.SELECT_RELATIONSHIP_ID);
                    if (!preferenceValueList.contains(id)) {
                        toDisconnectList.add(relId);
                    } else {
                        alreadyConnectedList.add(id);
                    }
                }
            }
            logger.log(Level.INFO, "Connected List: {0}", alreadyConnectedList);
            List<String> toConnectList = new ArrayList<>();
            for (String preferenceBusinessUseID : preferenceValueList) {
                if (!alreadyConnectedList.contains(preferenceBusinessUseID)) {
                    toConnectList.add(preferenceBusinessUseID);
                }
            }
            logger.log(Level.INFO, "ToDisconnect List: {0}", toDisconnectList);
            if (null != toDisconnectList && !toDisconnectList.isEmpty()) {
                String[] toDisconnectArr = toDisconnectList.toArray(String[]::new);
                DomainRelationship.disconnect(context, toDisconnectArr);
            }
            logger.log(Level.INFO, "ToConnect List: {0}", toConnectList);
            String[] toConnectArr = toConnectList.toArray(String[]::new);
            if (toConnectArr.length > 0) {
                DomainRelationship.connect(context, domainObject, relationshipName, side, toConnectArr);
                if (updateObjectNameOnAttribute) {// in case of material function. update object name on attribute
                    updateMaterialFunctionAttribute(context, domainObject, toConnectArr);
                }
            }

        }
    }

    /**
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public MapList getRelatedSecurityControl(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelects = new StringList();
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
        StringList relSelects = new StringList();
        relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
        MapList relatedObjects = domainObject.getRelatedObjects(context,
                PropertyUtil.getSchemaProperty(context, "relationship_ProtectedItem"),
                PreferenceConstants.Type.TYPE_IP_CONTROL.getName(context),
                objectSelects,
                relSelects,
                true,
                false,
                (short) 1,
                DomainConstants.EMPTY_STRING,
                DomainConstants.EMPTY_STRING,
                0);

        return relatedObjects;
    }

    /**
     * @param context
     * @param domainObject
     * @param preferencePhase
     * @return
     * @throws FrameworkException
     */
    public boolean isPreferencePhaseMatchWithPhaseAttribute(Context context, DomainObject domainObject, String preferencePhase) throws FrameworkException {
        String attributePhaseValue = domainObject.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
        logger.log(Level.INFO, "(Preference Phase) vs (Object Phase) > {0} vs {1}", new Object[]{preferencePhase, attributePhaseValue});
        return (UIUtil.isNotNullAndNotEmpty(preferencePhase) && UIUtil.isNotNullAndNotEmpty(attributePhaseValue) && attributePhaseValue.equalsIgnoreCase(preferencePhase));
    }

    /**
     * @param context
     * @param objectOid
     * @param preferenceMemberIDs
     * @throws Exception
     */
    public void applySharingMemberPreferencesIRM(Context context, String objectOid, String preferenceMemberIDs) throws Exception {
        boolean isCtxPushed = Boolean.FALSE;
        try {
            if (UIUtil.isNotNullAndNotEmpty(preferenceMemberIDs)) {
                StringList memberIDList = StringUtil.split(preferenceMemberIDs, PreferenceConstants.Basic.SYMBOL_PIPE.get());// multiple (share with members) are stored on preferences.
                // push context is required to provide multiple ownership access.
                ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                isCtxPushed = Boolean.TRUE;
                boolean isEBP;
                String access;
                String personName;

                List<String> preferenceMemberList = new ArrayList<>();
                for (String memberID : memberIDList) {
                    personName = DomainObject.newInstance(context, memberID).getInfo(context, DomainConstants.SELECT_NAME);
                    preferenceMemberList.add(personName);
                }
                logger.log(Level.INFO, "Preference Sharing Members List: {0}", preferenceMemberList);
                List<String> existingMemberList = getAcessMemberUserList(context, objectOid);
                logger.log(Level.INFO, "Existing Sharing Members List: {0}", existingMemberList);
                List<String> deleteOwnershipList = new ArrayList<>();
                for (String existingMember : existingMemberList) {
                    if (!preferenceMemberList.contains(existingMember)) {
                        deleteOwnershipList.add(existingMember);
                    }
                }
                logger.log(Level.INFO, "Delete Sharing Members List: {0}", deleteOwnershipList);

                // delete member ownership which is not present in preferences.
                for (String memberName : deleteOwnershipList) {
                    DomainAccess.deleteObjectOwnership(context, objectOid, DomainConstants.EMPTY_STRING, memberName + "_PRJ", DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                }

                for (String memberID : memberIDList) {
                    isEBP = isPersonEBPUser(context, memberID);
                    access = (isEBP) ? PreferenceConstants.Basic.IRM_SHARING_MEMBER_ACCESSES_FOR_EBP.get() : PreferenceConstants.Basic.FULL.get();
                    personName = DomainObject.newInstance(context, memberID).getInfo(context, DomainConstants.SELECT_NAME);
                    personName += "_PRJ";
                    DomainAccess.createObjectOwnership(context, objectOid, "-", personName, access, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                }

                ContextUtil.popContext(context);
                isCtxPushed = Boolean.FALSE;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
    }

    /**
     * Added by DSM (Sogeti) for 22x.03 (May CW 2023) Defect-53179
     *
     * @param context
     * @param objectOid
     * @return
     * @throws Exception
     */
    boolean isFOPOriginatingSourceNotFromDSO(Context context, String objectOid) throws Exception {
        try {
            boolean isOriginatingSourceNotDSO = false;
            Map info = new HashMap();
            String Type = DomainConstants.EMPTY_STRING;
            String strPartId = DomainConstants.EMPTY_STRING;
            String attrOriginatingSource = DomainConstants.EMPTY_STRING;
            StringList strlObjectSelects = new StringList(1);
            strlObjectSelects.add(DomainConstants.SELECT_ID);
            strlObjectSelects.add(DomainConstants.SELECT_TYPE);
            strlObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
            StringList slTypes = new StringList(1);
            slTypes.add(pgV3Constants.TYPE_FORMULATIONPART);
            slTypes.add(pgV3Constants.TYPE_FORMULATIONPROCESS);
            slTypes.add(pgV3Constants.TYPE_COSMETICFORMULATION);
            MapList proposedItems = EngineeringUtil.getProposedItems(context, objectOid, strlObjectSelects);
            if (null != proposedItems && !proposedItems.isEmpty()) {
                Iterator itrParts = proposedItems.iterator();
                while (itrParts.hasNext()) {
                    Map mapPart = (Map) itrParts.next();
                    strPartId = (String) mapPart.get(DomainConstants.SELECT_ID);
                    if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
                        DomainObject proposedDomainObject = DomainObject.newInstance(context, strPartId);
                        info = proposedDomainObject.getInfo(context, strlObjectSelects);
                        Type = (String) info.get(DomainConstants.SELECT_TYPE);
                        attrOriginatingSource = (String) info.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
                        if (UIUtil.isNotNullAndNotEmpty(attrOriginatingSource)
                                && slTypes.contains(Type) &&
                                !attrOriginatingSource.equalsIgnoreCase("DSO")) {
                            isOriginatingSourceNotDSO = true;
                        }
                    }
                }
            }
            return isOriginatingSourceNotDSO;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws Exception
     */
    boolean isNonDSMAuthoredFormulation(Context context, String objectOid) throws Exception {
        boolean isNonDSMAuthored = Boolean.FALSE;

        StringList busSelects = new StringList();
        busSelects.add(DomainConstants.SELECT_ID);
        busSelects.add(DomainConstants.SELECT_TYPE);
        busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
        busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
        busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);

        MapList proposedList = EngineeringUtil.getProposedItems(context, objectOid, busSelects);
        if (null != proposedList && !proposedList.isEmpty()) {
            StringList inclusionTypeList = StringList.create(pgV3Constants.TYPE_FORMULATIONPART, pgV3Constants.TYPE_FORMULATIONPROCESS, pgV3Constants.TYPE_COSMETICFORMULATION);
            Iterator iterator = proposedList.iterator();
            Map<Object, Object> objectMap;
            while (iterator.hasNext()) {
                objectMap = (Map<Object, Object>) iterator.next();
                isNonDSMAuthored = isNonDSMAuthoredFormulation(objectMap, inclusionTypeList);
                if (isNonDSMAuthored) {
                    break;
                }
            }
        }
        return isNonDSMAuthored;
    }

    /**
     * @param objectMap
     * @param inclusionTypeList
     * @return
     */
    boolean isNonDSMAuthoredFormulation(Map<Object, Object> objectMap, StringList inclusionTypeList) {

        // type should be (type_FormulationPart, type_FormulationProcess, type_CosmeticFormulation)
        boolean isFormulationType = inclusionTypeList.contains((String) objectMap.get(DomainConstants.SELECT_TYPE));

        // for NGF, attribute_pgAuthoringApplication = FIS
        boolean isNGFAuthored = PreferenceConstants.Basic.FIS.get().equalsIgnoreCase((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION));

        // for Enginuity, attribute_EnginuityAuthored = TRUE
        boolean isEnginuityAuthored = pgV3Constants.CAPS_TRUE.equalsIgnoreCase((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED));

        // Enginuity
        boolean isEnginuityOriginated = pgV3Constants.ENGINUITY_AUTHORED_PGORIGINATINGSOURCE_VALUE.equalsIgnoreCase((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE));

        return isFormulationType && (isNGFAuthored || isEnginuityAuthored || isEnginuityOriginated);
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public Map<String, String> getNonDSMFormulationCopyAddMessage(Context context, String[] args) throws Exception {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(PreferenceConstants.Basic.IS_VALID.get(), String.valueOf(Boolean.FALSE));
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = (String) programMap.get(DomainConstants.SELECT_ID);
        Locale locale = context.getLocale();
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            StringList busSelects = new StringList();
            busSelects.add(DomainConstants.SELECT_ID);
            busSelects.add(DomainConstants.SELECT_TYPE);
            busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
            busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
            busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
            DomainObject domainObject = DomainObject.newInstance(context, objectId);
            Map objectMap = domainObject.getInfo(context, busSelects);
            StringList inclusionTypeList = StringList.create(pgV3Constants.TYPE_FORMULATIONPART, pgV3Constants.TYPE_FORMULATIONPROCESS, pgV3Constants.TYPE_COSMETICFORMULATION);
            boolean isNonDSM = isNonDSMAuthoredFormulation(objectMap, inclusionTypeList);
            if (isNonDSM) {
                String sMsg = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", locale, "emxFramework.Preferences.NonDSMFormulationCopyAddMessage");
                resultMap.put(PreferenceConstants.Basic.MESSAGE.get(), sMsg);
            }
            resultMap.put(PreferenceConstants.Basic.IS_VALID.get(), String.valueOf(isNonDSM)); // True or False.
        }
        return resultMap;
    }

    public String getObjectPhyscialID(Context context, String type, String name) throws FrameworkException {
        String id = DomainConstants.EMPTY_STRING;
        try {
            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),    // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    DomainConstants.EMPTY_STRING,           // where expression
                    false,                                  // expandType
                    StringList.create(PreferenceConstants.Basic.PHYSICAL_ID.get()));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    id = (String) ((Map) iterator.next()).get(PreferenceConstants.Basic.PHYSICAL_ID.get());
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return id;
    }

    public String getPreferredBusinessUseClassPhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context,   // context
                    PreferenceConstants.Type.TYPE_IP_CONTROL.getName(context),      // type
                    getPreferredBusinessUseClass(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPipeSeparatedPhyscialIDs(Context context, String type, String names) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(names) && UIUtil.isNotNullAndNotEmpty(type)) {
            StringList nameList = StringUtil.split(names, pgV3Constants.SYMBOL_PIPE);
            StringBuilder oidBuilder = new StringBuilder();
            String oid;
            for (String name : nameList) {
                if (UIUtil.isNotNullAndNotEmpty(name)) {
                    oid = getObjectPhyscialID(context, type, name);
                    if (UIUtil.isNotNullAndNotEmpty(oid)) {
                        oidBuilder.append(oid);
                        oidBuilder.append(pgV3Constants.SYMBOL_PIPE);
                    }
                }
            }
            if (oidBuilder.length() > 0) {
                oidBuilder.setLength(oidBuilder.length() - 1);
                ids = oidBuilder.toString();
            }
        }
        return ids;
    }

    public String getPreferredHighlyRestrictedClassPhyscialID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context,     // context
                    PreferenceConstants.Type.TYPE_IP_CONTROL.getName(context),      // type
                    getPreferredHighlyRestrictedClass(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredPrimaryOrgPhyscialID(Context context) throws MatrixException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context, // context
                    PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context), // type
                    getPreferredPrimaryOrgName(context)); // names - pipe separated
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredRouteTemplatePrimaryOrgPhyscialID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context, // context
                    PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context), // type
                    getPreferredRouteTemplatePrimaryOrgName(context)); // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredInWorkRouteTemplatePhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context, // context
                    DomainConstants.TYPE_ROUTE_TEMPLATE, // type
                    getPreferredInWorkRouteTemplateName(context)); // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredInApprovalRouteTemplatePhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getRouteTemplatesPipeSeparatedPhysicalIDs(context,                                            // context
                    DomainConstants.TYPE_ROUTE_TEMPLATE,                // type
                    getPreferredInApprovalRouteTemplateName(context));  // names - pipe separated
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getRouteTemplatesPipeSeparatedPhysicalIDs(Context context, String type, String names) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(names) && UIUtil.isNotNullAndNotEmpty(type)) {
            StringList nameList = StringUtil.split(names, pgV3Constants.SYMBOL_PIPE);
            String oid;
            StringBuilder oidBuilder = new StringBuilder();
            for (String name : nameList) {
                if (UIUtil.isNotNullAndNotEmpty(name)) {
                    oid = getProductionRouteTemplatePhysicalID(context, type, name);
                    if (UIUtil.isNotNullAndNotEmpty(oid)) {
                        oidBuilder.append(oid);
                        oidBuilder.append(pgV3Constants.SYMBOL_PIPE);
                    }
                }
            }
            if (oidBuilder.length() > 0) {
                oidBuilder.setLength(oidBuilder.length() - 1);
                ids = oidBuilder.toString();
            }
        }
        return ids;
    }

    public String getProductionRouteTemplatePhysicalID(Context context, String type, String name) throws FrameworkException {
        String id = DomainConstants.EMPTY_STRING;
        try {
            String stateProduction = PropertyUtil.getSchemaProperty(context, DomainConstants.SELECT_POLICY, DomainConstants.POLICY_ROUTE_TEMPLATE, "state_Production");
            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),                                   // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    DomainConstants.SELECT_CURRENT + "==" + stateProduction,                       // where expression
                    false,                                  // expandType
                    StringList.create(PreferenceConstants.Basic.PHYSICAL_ID.get()));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    id = (String) ((Map) iterator.next()).get(PreferenceConstants.Basic.PHYSICAL_ID.get());
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return id;
    }

    public String getPreferredChangeTemplatePhysicalID(Context context) throws MatrixException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context, // context
                    PreferenceConstants.Type.TYPE_CHANGE_TEMPLATE.getName(context), // type
                    getPreferredChangeTemplateName(context)); // preference key
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredDefaultPlantPhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context,                                                    // context
                    PreferenceConstants.Type.TYPE_PLANT.getName(context),      // type
                    getPreferredDefaultPlantName(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredDefaultSharingMemberPhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context,        // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredDefaultSharingMemberName(context));  // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPhysicalIDFromObjectID(Context context, String objectId) throws FrameworkException {
        String physicalId = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            physicalId = DomainObject.newInstance(context, objectId).getInfo(context, PreferenceConstants.Basic.PHYSICAL_ID.get());
        }
        return physicalId;
    }

    public String getPipeSeparatedPhysicalIDFromObjectID(Context context, String objectId) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            StringList physicalIDList = StringUtil.split(objectId, pgV3Constants.SYMBOL_PIPE);
            MapList objectList = DomainObject.getInfo(context, physicalIDList.toStringArray(), StringList.create(PreferenceConstants.Basic.PHYSICAL_ID.get()));
            List<String> iDList = new ArrayList<>();
            if (null != objectList && !objectList.isEmpty()) {
                Map<Object, Object> objectMap;
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    iDList.add((String) objectMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                }
            }
            ids = String.join(pgV3Constants.SYMBOL_PIPE, iDList);
        }
        return ids;
    }

    public String getPipeOrCommaSeparatedPhysicalIDFromObjectID(Context context, String objectId) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            StringList physicalIDList = new StringList();
            if (objectId.contains(pgV3Constants.SYMBOL_COMMA)) {
                physicalIDList = StringUtil.split(objectId, pgV3Constants.SYMBOL_COMMA);
            } else {
                physicalIDList = StringUtil.split(objectId, pgV3Constants.SYMBOL_PIPE);
            }
            MapList objectList = DomainObject.getInfo(context, physicalIDList.toStringArray(), StringList.create(PreferenceConstants.Basic.PHYSICAL_ID.get()));
            List<String> iDList = new ArrayList<>();
            if (null != objectList && !objectList.isEmpty()) {
                Map<Object, Object> objectMap;
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    iDList.add((String) objectMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                }
            }
            ids = String.join(pgV3Constants.SYMBOL_PIPE, iDList);
        }
        return ids;
    }

    public String getPreferredIRMRegionPhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context,                                                    // context
                    PreferenceConstants.Type.TYPE_REGION.getName(context),      // type
                    getPreferredIRMRegionName(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    public String getPreferredIRMSharingMembersPhysicalID(Context context) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        try {
            ids = getPipeSeparatedPhyscialIDs(context,                                                    // context
                    DomainConstants.TYPE_PERSON,      // type
                    getPreferredIRMSharingMembers(context));                        // names - pipe separated.
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return ids;
    }

    /**
     * @param context
     * @param type
     * @param name
     * @return
     * @throws MatrixException
     */
    public String getPhysicalIDFromTNR(Context context, String type, String name) throws MatrixException {
        String physicalId = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(type) && UIUtil.isNotNullAndNotEmpty(name)) {
            StringBuffer buff = new StringBuffer();
            buff.append("(current == 'Active')");
            buff.append("&& (name == '" + name + "')");
            String whereCondition = buff.toString();
            StringList objSelects = new StringList(DomainConstants.SELECT_PHYSICAL_ID);
            MapList picklistMapList = DomainObject.findObjects(context, type, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, whereCondition, false, objSelects);
            if (!picklistMapList.isEmpty() && null != picklistMapList) {
                Map picklistMap = (Map) picklistMapList.get(0);
                physicalId = (String) picklistMap.get(DomainConstants.SELECT_PHYSICAL_ID);
            }
        }
        return physicalId;
    }

    public String getPartCategory(Context context) {
        return UserPreferenceTemplateConstants.Basic.TEMPLATE_PART_CATEGORY.get();
    }

    public String getPartCategoryDisplay(Context context) {
        return UserPreferenceTemplateConstants.Basic.TEMPLATE_PART_CATEGORY_DISPLAY.get();
    }

    public String getPlantEnableTechnicalSpecificationType(Context context) {
        StringBuilder stringBuilder = new StringBuilder(7);
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_STANDARD_OPERATING_PROCEDURE.getName(context)).append(",");
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_AUTHORIZED_TEMPORARY_SPECIFICATION.getName(context)).append(",");
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_AUTHORIZED_CONFIGURATION_STANDARD.getName(context)).append(",");
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_INTERMEDIATE_ASSEMBLED_PRODUCT_SPECIFICATION.getName(context)).append(",");
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_LABORATORY_INDEX_SPECIFICATION.getName(context)).append(",");
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_MAKING_INSTRUCTION.getName(context)).append(",");
        stringBuilder.append(UserPreferenceTemplateConstants.Type.TYPE_PROCESS_STANDARD.getName(context)).append(",");
        return stringBuilder.toString();
    }

    public Map<String, StringList> getPartTypes(String configurations) {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList choices = new StringList();
        StringList displayChoices = new StringList();
        if (UIUtil.isNotNullAndNotEmpty(configurations)) {
            StringList configurationList = StringUtil.split(configurations, PreferenceConstants.Basic.SYMBOL_PIPE.get());
            if (null != configurationList && !configurationList.isEmpty()) {
                StringList typeConfigList;
                for (String configuration : configurationList) {
                    if (UIUtil.isNotNullAndNotEmpty(configuration)) {
                        typeConfigList = StringUtil.split(configuration, PreferenceConstants.Basic.SYMBOL_COLON.get());
                        if (null != typeConfigList && !typeConfigList.isEmpty()) {
                            choices.add(typeConfigList.get(0));
                            displayChoices.add(typeConfigList.get(2));
                        }
                    }
                }
            }
        }
        rangeMap.put(DataConstants.CONST_FIELD_CHOICES, choices);
        rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, displayChoices);
        return rangeMap;
    }

    public Map<String, StringList> getReportedFunctionRangeByGivenType(Context context, String type) throws MatrixException {
        Map<String, StringList> resultMap = new HashMap<>();
        try {
            Map settings = new HashMap();
            settings.put("pgPicklistState", "");
            settings.put("pgPicklistSubset", "Packaging Parts Reported Function");
            settings.put("pgPicklistName", PreferenceConstants.Type.TYPE_PICKLIST_REPORTED_FUNCTION.getName(context));
            settings.put("Input Type", "combobox");
            settings.put("IncludeBlank", "true");
            Map settingsMap = new HashMap();
            settingsMap.put("settings", settings);
            Map paramMap = new HashMap();
            paramMap.put("fieldMap", settingsMap);
            StringList fieldDisplayChoices = new StringList();
            StringList fieldChoices = new StringList();

            if (UIUtil.isNotNullAndNotEmpty(type)) {
                logger.log(Level.INFO, "Fetch Reported Function for type: {0}", type);
                type = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, type, true);
                StringList allowedTypeList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "emxCPN.ReportedFunction.Range.Subset.allowedTypes"), ",");
                if (null != allowedTypeList && allowedTypeList.contains(type)) {
                    resultMap = JPO.invoke(context, "pgDSOUtil_mxJPO", null, "getPicklistSubsetRangeMap", JPO.packArgs(paramMap), Map.class);
                    if (resultMap != null && !resultMap.isEmpty()) {
                        fieldDisplayChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
                        fieldChoices = (StringList) resultMap.get(DataConstants.CONST_FIELD_CHOICES);
                        if (fieldDisplayChoices != null && fieldChoices != null) {
                            fieldDisplayChoices.add(DomainConstants.EMPTY_STRING);
                            fieldChoices.add(DomainConstants.EMPTY_STRING);
                            resultMap.clear();
                            resultMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, fieldDisplayChoices);
                            resultMap.put(DataConstants.CONST_FIELD_CHOICES, fieldChoices);
                        }
                    }
                } else {
                    DSMUserPreferenceConfig preferenceConfig = getDSMUserPreferenceConfig(context);
                    allowedTypeList = StringUtil.split(preferenceConfig.getAllowedTypesForReportedFunction(), pgV3Constants.SYMBOL_COMMA);
                    if (null != allowedTypeList && allowedTypeList.contains(type)) {
                        resultMap = JPO.invoke(context, "pgPLPicklist_mxJPO", null, "getPicklistRangeMap", JPO.packArgs(paramMap), Map.class);
                    }
                }
            } else {
                logger.log(Level.WARNING, "Type argument is null or empty");
            }
        } catch (MatrixException e) {
            throw e;
        }
        return resultMap;
    }

    public Map<String, StringList> getPartCategoryRange() {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList configurations = StringUtil.split(UserPreferenceTemplateConstants.Basic.TEMPLATE_PART_CATEGORY_CONFIGURATION.get(), PreferenceConstants.Basic.SYMBOL_PIPE.get());
        if (null != configurations && !configurations.isEmpty()) {
            StringList config;
            StringList choices = new StringList();
            StringList displayChoices = new StringList();
            for (String configuration : configurations) {
                if (UIUtil.isNotNullAndNotEmpty(configuration)) {
                    config = StringUtil.split(configuration, PreferenceConstants.Basic.SYMBOL_COLON.get());
                    if (null != config && !config.isEmpty()) {
                        choices.add(config.get(0));
                        displayChoices.add(config.get(1));
                    }
                }
            }
            rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, displayChoices);
            rangeMap.put(DataConstants.CONST_FIELD_CHOICES, choices);
        } else {
            logger.log(Level.WARNING, "Classes range is empty");
        }
        return rangeMap;
    }

    public String getProductJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        ProductPickList productPickList = new ProductPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.PRODUCT_TYPES.get(), productPickList.getPartTypes());
        jsonOutput.add(PreferenceConstants.Basic.PRODUCT_COMPLIANCE.get(), productPickList.getComplianceRequired());
        jsonOutput.add(PreferenceConstants.Basic.REPORTED_FUNCTION.get(), productPickList.getReportedFunction());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get Product Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }

    public String getPackagingJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        PackagingPickList packagingPickList = new PackagingPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.PACKAGING_TYPES.get(), packagingPickList.getPartTypes());
        jsonOutput.add(PreferenceConstants.Basic.MATERIAL_TYPES.get(), packagingPickList.getMaterialTypes());
        jsonOutput.add(PreferenceConstants.Basic.COMPONENT_TYPES.get(), packagingPickList.getComponentTypes());
        jsonOutput.add(PreferenceConstants.Basic.UNIT_OF_MEASURES.get(), packagingPickList.getUnitOfMeasures());
        jsonOutput.add(PreferenceConstants.Basic.REPORTED_FUNCTION.get(), packagingPickList.getReportedFunction());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get Packaging Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }

    public String getTechSpecJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        TechSpecPickList techSpecPickList = new TechSpecPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.TECH_SPEC_TYPES.get(), techSpecPickList.getPartTypes());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get Technical Spec Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }

    public String getExplorationJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        ExplorationPickList explorationPickList = new ExplorationPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.EXPLORATION_TYPES.get(), explorationPickList.getPartTypes());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get Exploration Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }

    public String getRawMaterialJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        RawMaterialPickList rawMaterialPickList = new RawMaterialPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.RAW_MATERIAL_TYPES.get(), rawMaterialPickList.getPartTypes());
        jsonOutput.add(PreferenceConstants.Basic.MATERIAL_FUNCTION.get(), rawMaterialPickList.getMaterialFunction());
        jsonOutput.add(PreferenceConstants.Basic.REPORTED_FUNCTION.get(), rawMaterialPickList.getReportedFunction());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get Raw Material Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    public String getMEPJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        MEPPickList MEPPickList = new MEPPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.MEP_TYPES.get(), MEPPickList.getPartTypes());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get MEP Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }

    public String getSEPJson(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        SEPPickList SEPPickList = new SEPPickList(context);
        jsonOutput.add(PreferenceConstants.Basic.SEP_TYPES.get(), SEPPickList.getPartTypes());
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get SEP Picklist - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonOutput.build().toString();
    }
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

    public PackagingUserTemplate getPackagingUserTemplate(Context context, String userTemplateOid) throws FrameworkException {
        DomainObject domainObject = DomainObject.newInstance(context, userTemplateOid);
        Map objectInfo = getInfoMap(context, userTemplateOid, getPackagingSelectables(context));
        return new PackagingUserTemplate(context, objectInfo);
    }

    public ProductUserTemplate getProductUserTemplate(Context context, String userTemplateOid) throws Exception {
        DomainObject domainObject = DomainObject.newInstance(context, userTemplateOid);
        Map objectInfo = getInfoMap(context, userTemplateOid, getProductSelectables(context));
        return new ProductUserTemplate(context, objectInfo);
    }

    public RawMaterialUserTemplate getRawMaterialUserTemplate(Context context, String userTemplateOid) throws Exception {
        DomainObject domainObject = DomainObject.newInstance(context, userTemplateOid);
        Map objectInfo = getInfoMap(context, userTemplateOid, getRawMaterialSelectables(context));
        return new RawMaterialUserTemplate(context, objectInfo);
    }

    public Map<Object, Object> getInfoMap(Context context, String objectOid, StringList objectSelects) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        MapList infoList = DomainObject.getInfo(context, new String[]{objectOid}, objectSelects);
        if (null != infoList && !infoList.isEmpty()) {
            objectMap = (Map<Object, Object>) infoList.get(0);
        }
        return objectMap;
    }

    public StringList getPackagingSelectables(Context context) {
        StringList selectList = new StringList();
        DSMUPTConstants.PackagingAttributeGroup[] attributes = DSMUPTConstants.PackagingAttributeGroup.values();
        for (DSMUPTConstants.PackagingAttributeGroup attribute : attributes) {
            selectList.add(attribute.getSelect(context));
        }
        selectList.add(DomainConstants.SELECT_ID);
        selectList.add(DomainConstants.SELECT_NAME);
        selectList.add(DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context));
        return selectList;
    }

    public StringList getProductSelectables(Context context) {
        StringList selectList = new StringList();
        DSMUPTConstants.ProductAttributeGroup[] attributes = DSMUPTConstants.ProductAttributeGroup.values();
        for (DSMUPTConstants.ProductAttributeGroup attribute : attributes) {
            selectList.add(attribute.getSelect(context));
        }
        selectList.add(DomainConstants.SELECT_ID);
        selectList.add(DomainConstants.SELECT_NAME);
        selectList.add(DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context));
        return selectList;
    }

    public StringList getRawMaterialSelectables(Context context) {
        StringList selectList = new StringList();
        DSMUPTConstants.RawMaterialAttributeGroup[] attributes = DSMUPTConstants.RawMaterialAttributeGroup.values();
        for (DSMUPTConstants.RawMaterialAttributeGroup attribute : attributes) {
            selectList.add(attribute.getSelect(context));
        }
        selectList.add(DomainConstants.SELECT_ID);
        selectList.add(DomainConstants.SELECT_NAME);
        selectList.add(DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context));
        selectList.add(DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context));
        return selectList;
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void propagetUPTAttributes(Context context, String[] args) throws Exception {
        String objectType = args[0];
        String objectOid = args[1];
		String copyProductData = DomainConstants.EMPTY_STRING;
        if(args.length > 2){
            copyProductData= args[2];
        }
        if (UIUtil.isNotNullAndNotEmpty(objectType) && UIUtil.isNotNullAndNotEmpty(objectOid)) {
            logger.log(Level.INFO, "Propaget User Preference Template Attributes on Incoming Object > | {0} | {1} |", new Object[]{objectType, objectOid});
            StringList existingPlantListId = new StringList();
            DomainObject domainObject = DomainObject.newInstance(context, objectOid);
            String physIDSelect = UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context);
            String partCategorySelect = UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getSelect(context);
            Map objectInfo = domainObject.getInfo(context, StringList.create(DomainConstants.SELECT_POLICY, physIDSelect));
            String objectPolicy = (String) objectInfo.get(DomainConstants.SELECT_POLICY);
            String uptPhyID = (String) objectInfo.get(physIDSelect); // get the UPT physical ID stored on incoming object.
            logger.log(Level.INFO, "UPT Physical ID: " + uptPhyID);
            if (UIUtil.isNotNullAndNotEmpty(uptPhyID)) {
                DomainObject uptObject = DomainObject.newInstance(context, uptPhyID);
                String partCategoryName = uptObject.getInfo(context, partCategorySelect);
                if (UIUtil.isNotNullAndNotEmpty(partCategoryName)) {
                    logger.log(Level.INFO, "Part Category on UPT: " + partCategoryName);
                    if(UIUtil.isNotNullAndNotEmpty(objectOid)){
                   
                            DSMUPT dsmupt = new DSMUPT(context, uptPhyID);
                            String pgUPTAttributeJson = dsmupt.getAttributeJson();
                            JSONObject jsnTemplateattributeInfo = new JSONObject(pgUPTAttributeJson);
                            
                            //Set Sharing member on Product Part -Start
                            String strMemberIDs = DomainConstants.EMPTY_STRING;
                            JSONArray sharingMemberInfo = jsnTemplateattributeInfo.optJSONArray(DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context));
                            if(sharingMemberInfo.length()>0) {
                            for(int i=0; i<sharingMemberInfo.length(); i++) {
                                JSONObject jsnSharingMember = new JSONObject(sharingMemberInfo.optString(i));
                                strMemberIDs += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") +"|";
                            }
                            pgUPTApplySharingMembers(context, objectType, objectOid, strMemberIDs.substring(0, strMemberIDs.length()-1));
                            }
                        if(UIUtil.isNotNullAndNotEmpty(copyProductData) && "CopyProductData".equals(copyProductData)){
                            //Set IP Security-Start
                            String[] partArgs = new String[]{ objectType, objectOid, jsnTemplateattributeInfo.toString()};
                            pgUPTSetIPSecurity(context, partArgs);

							//Set Plant on Copy Product Data
							existingPlantListId = domainObject.getInfoList(context, "to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.id");
							String plantEnableTechnicalSpecificationType = getPlantEnableTechnicalSpecificationType(context);
							UserPreferenceTemplateCreateUtil userPreferenceTemplateCreateUtil = new UserPreferenceTemplateCreateUtil();
							if("TS".equals(partCategoryName) && plantEnableTechnicalSpecificationType.contains(objectType)) {
								//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect# 55696-Start
								removeExistingTechSpecPlant(context, domainObject, uptPhyID);
								//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect# 55696-End
								userPreferenceTemplateCreateUtil.connectPlantPreferenceToPart(context, objectOid, uptPhyID,existingPlantListId);
							} else if(!"TS".equals(partCategoryName)){
								userPreferenceTemplateCreateUtil.connectPlantPreferenceToPart(context, objectOid, uptPhyID,existingPlantListId);
							}
                        }
                    }
                    UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                    PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
                    if (preferenceConfigLoader.isLoaded()) {
                        PreferenceConfig preferenceConfig = preferenceConfigLoader.getPreferenceConfig();
                        DSMUPTConstants.PartCategory partCategory = DSMUPTConstants.PartCategory.getPartCategoryFromName(partCategoryName);
                        if (null != partCategory) {
                            String symbolicType = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, objectType, true);
                            partCategory.propagateAttributes(context, preferenceConfig, objectOid, symbolicType, domainObject, uptPhyID);
                        } else {
                            logger.log(Level.WARNING, "Part Category enum is null");
                        }
                    } else {
                        logger.log(Level.WARNING, "Failed to load page pgUserPreferenceConfig with error: {0}", preferenceConfigLoader.getErrorMessage());
                    }
                } else {
                    logger.log(Level.WARNING, "UPT Part Category is empty on incoming object");
                }
            } else {
                logger.log(Level.WARNING, "UPT Physical ID is empty on incoming object");
            }
        }
    }

    /**
     * Added by IRM (DSM) for 2022x-04 Dec CW - Requirement #
     *
     * @param context
     * @param args
     * @throws Exception
     */
    public void propateUPTAttributesOnCreationPart(Context context, String[] args) throws Exception {
        try {
            PartCreateAction partCreateAction = new PartCreateAction();
            partCreateAction.propagateUPTAttributesOnPartCreation(context, args);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception: " + e);
            throw e;
        }
    }

    /**
     * Added by IRM (DSM) for 2022x-04 Dec CW - Requirement #
     *
     * @param context
     * @param args
     * @throws Exception
     */
    public void propateUPTAttributesOnCopyPart(Context context, String[] args) throws Exception {
        try {
            setIPSecurity(context, args); // update security.
            PartCreateAction partCreateAction = new PartCreateAction();
            partCreateAction.propagateUPTAttributesOnPartCreation(context, args);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception: " + e);
            throw e;
        }
    }
  
    public CommonPickList getCommonPickList(Context context) throws MatrixException {
        return new CommonPickList(context);
    }

    public MapList getUserPreferenceTemplateDetails(Context context, String strCAId, String pgUPTPhyID) throws Exception {
        MapList mapList = new MapList();
        try {
            if (UIUtil.isNotNullAndNotEmpty(pgUPTPhyID)) {
                mapList.add(getUPTAttrValuesMap(context, pgUPTPhyID));
                logger.log(Level.INFO, "User Preference mapList For Standalone CA connection: " + mapList);
            } else {
                IChangeAction iChangeAction = com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction.getChangeAction(context, strCAId);
                List<IProposedChanges> proposedChanges = iChangeAction.getProposedChanges(context);
                String sObjectPhyId = DomainConstants.EMPTY_STRING;
                DomainObject domainObject = null;
                if (!proposedChanges.isEmpty()) {
                    for (int i = 0; i < proposedChanges.size(); i++) {
                        sObjectPhyId = proposedChanges.get(i).getWhere().getName();
                        if (UIUtil.isNotNullAndNotEmpty(sObjectPhyId)) {
                            pgUPTPhyID = DomainObject.newInstance(context, sObjectPhyId).getInfo(context, UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context));
                            if (UIUtil.isNotNullAndNotEmpty(pgUPTPhyID)) {
                                mapList.add(getUPTAttrValuesMap(context, pgUPTPhyID));
                            }
                        }
                    }
                }
                logger.log(Level.INFO, "User Preference mapList For Part Context CA connection: " + mapList);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
        }

        return mapList;
    }

    public Map<Object, Object> getUPTAttrValuesMap(Context context, String pgUPTPhyID) throws Exception {
        Map<Object, Object> attributeMap = new HashMap<>();
        try {
            StringList stringList = new StringList();
            stringList.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_WORK.getSelect(context));
            stringList.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_APPROVAL.getSelect(context));
            stringList.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_INFORMED_USERS.getSelect(context));
            stringList.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SHARE_WITH_MEMBERS.getSelect(context));
            MapList UPTMapList = DomainObject.getInfo(context, new String[]{pgUPTPhyID}, stringList);
            if (!UPTMapList.isEmpty()) {
                Iterator iterator = UPTMapList.iterator();
                while (iterator.hasNext()) {
                    attributeMap = (Map) iterator.next();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
        }

        return attributeMap;
    }

    private StringList getStringListFromMap(Map<Object, Object> objectMap, String strAttrSelect) throws Exception {
        StringList objectList = new StringList();
        logger.log(Level.INFO, "objectMap: " + objectMap);
        Object result = objectMap.get(strAttrSelect);
        if (null != result) {
            if (result instanceof StringList) {
                objectList = (StringList) result;
            } else if (result.toString().contains(SelectConstants.cSelectDelimiter)) {
                objectList = StringUtil.splitString(result.toString(), SelectConstants.cSelectDelimiter);
            } else if (result.toString().contains(",")) {
                objectList = StringUtil.split(result.toString(), ",");
            } else if (result.toString().contains("|")) {
                objectList = StringUtil.split(result.toString(), "|");
            } else {
                objectList.add(result.toString());
            }
        }
        return objectList;
    }

    public StringList removeDuplicateElementsFromList(StringList list) {
        StringList uniqueList = new StringList();
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (!uniqueList.contains(list.get(i))) {
                    uniqueList.add(list.get(i));
                }
            }
        }
        return uniqueList;
    }

    public StringList getRouteTemplateInWorkOID(Context context, MapList userPreferenceTemplateDetails) throws Exception {
        StringList pgUPTInWork = new StringList();
        if (!userPreferenceTemplateDetails.isEmpty()) {
            Map<Object, Object> objectMap = new HashMap();
            Iterator iterator = userPreferenceTemplateDetails.iterator();
            while (iterator.hasNext()) {
                objectMap = (Map) iterator.next();
                pgUPTInWork.addAll(getStringListFromMap(objectMap, UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_WORK.getSelect(context)));
            }
        }
        return removeDuplicateElementsFromList(pgUPTInWork);
    }

    public StringList getRouteTemplateInApprovalOID(Context context, MapList userPreferenceTemplateDetails) throws Exception {
        StringList pgUPTInApproval = new StringList();
        if (!userPreferenceTemplateDetails.isEmpty()) {
            Map<Object, Object> objectMap = new HashMap();
            Iterator iterator = userPreferenceTemplateDetails.iterator();
            while (iterator.hasNext()) {
                objectMap = (Map) iterator.next();
                pgUPTInApproval.addAll(getStringListFromMap(objectMap, UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_APPROVAL.getSelect(context)));
            }
        }
        return removeDuplicateElementsFromList(pgUPTInApproval);
    }

    public StringList getInformedUserOID(Context context, MapList userPreferenceTemplateDetails) throws Exception {
        StringList pgUPTInformedUsers = new StringList();
        if (!userPreferenceTemplateDetails.isEmpty()) {
            Map<Object, Object> objectMap = new HashMap();
            Iterator iterator = userPreferenceTemplateDetails.iterator();
            while (iterator.hasNext()) {
                objectMap = (Map) iterator.next();
                pgUPTInformedUsers.addAll(getStringListFromMap(objectMap, UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_INFORMED_USERS.getSelect(context)));
            }
        }
        return removeDuplicateElementsFromList(pgUPTInformedUsers);
    }

    public StringList getSharedUserOID(Context context, MapList userPreferenceTemplateDetails) throws Exception {
        StringList pgUPTSharedMember = new StringList();
        if (!userPreferenceTemplateDetails.isEmpty()) {
            Map<Object, Object> objectMap = new HashMap();
            Iterator iterator = userPreferenceTemplateDetails.iterator();
            while (iterator.hasNext()) {
                objectMap = (Map) iterator.next();
                pgUPTSharedMember.addAll(getStringListFromMap(objectMap, UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SHARE_WITH_MEMBERS.getSelect(context)));
            }
        }
        return removeDuplicateElementsFromList(pgUPTSharedMember);
    }

    /**
     * @Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55187
     * @param context
     * @param objectId
     * @throws Exception
     */
    public void applySharingMemerPreferenceOnCA(Context context,String objectId) throws Exception {
        try{
            Map<Object, Object> attributeMap = new HashMap<>();
            String pgUPTPhyID = DomainObject.newInstance(context,objectId).getInfo(context, UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context));
            if(UIUtil.isNotNullAndNotEmpty(pgUPTPhyID)){
                DomainObject domainObject = DomainObject.newInstance(context, pgUPTPhyID);
                MapList UPTMapList = DomainObject.getInfo(context, new String[]{pgUPTPhyID}, new StringList(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SHARE_WITH_MEMBERS.getSelect(context)));
                if (!UPTMapList.isEmpty()) {
                    Iterator iterator = UPTMapList.iterator();
                    while (iterator.hasNext()) {
                        attributeMap = (Map) iterator.next();
                    }
                }
                MapList userPreferenceTemplateDetails = getUserPreferenceTemplateDetails(context, objectId, pgUPTPhyID);
                logger.log(Level.INFO, "userPreferenceTemplateDetails: " + userPreferenceTemplateDetails);
                if(!userPreferenceTemplateDetails.isEmpty()) {
                    StringList SharedUserOID = getSharedUserOID(context, userPreferenceTemplateDetails);
                    logger.log(Level.INFO, "Member List for CA sharing Category: " + SharedUserOID);
                    if (null != SharedUserOID && !SharedUserOID.isEmpty()) {
                        applyPreferencesOnCASharingCategory(context, objectId, SharedUserOID);
                        logger.log(Level.INFO, "Applied Sharing Member on CA: ");
                    }
                }
            }
        }catch (Exception e) {
			logger.log(Level.INFO, "Error: "+e);
        }
    }

    /**
     * @Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55187
     * @param context
     * @param objectOid
     * @param preferenceMemberIDs
     * @throws Exception
     */
    public void applyPreferencesOnCASharingCategory(Context context, String objectOid, StringList memberIDList) throws Exception {
        boolean isCtxPushed = Boolean.FALSE;
        try {
            // push context is required to provide multiple ownership access.
            ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
            isCtxPushed = Boolean.TRUE;
            boolean isEBP;
            String access;
            String personName;
            for (String memberID : memberIDList) {
                //Added by IRM(Sogeti) for 2022x.04 Defect ID 55411 - Start
                isEBP = isPersonEBPUser(context, memberID);
                access = (isEBP) ? PreferenceConstants.Basic.DSM_SHARING_MEMBER_ACCESSES_FOR_EBP.get() : pgV3Constants.ALL;
                //Added by IRM(Sogeti) for 2022x.04 Defect ID 55411 - End
                personName = DomainObject.newInstance(context, memberID).getInfo(context, DomainConstants.SELECT_NAME);
                personName += "_PRJ";
                DomainAccess.createObjectOwnership(context, objectOid, "-", personName, access, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
            }
            ContextUtil.popContext(context);
            isCtxPushed = Boolean.FALSE;

        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
    }
 // Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47565 - Start	
    public void pgUPTfloatUserPreferencesOnFormulation(Context context, String cosmeticFormulationID, String action, String usePreference, String strUPTPhysicalID) {
    	try {
    		if(UIUtil.isNotNullAndNotEmpty(strUPTPhysicalID)){
    			UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
    			if (UIUtil.isNotNullAndNotEmpty(cosmeticFormulationID)) {
    				DSMUPT dsmupt = new DSMUPT(context, strUPTPhysicalID);
    				String pgUPTAttributeJson = dsmupt.getAttributeJson();
    				JSONObject jsnTemplateattributeInfo = new JSONObject(pgUPTAttributeJson);
    				String strMemberIDs = DomainConstants.EMPTY_STRING;
    				StringList existingPlantListId = new StringList();
    				//Map<Object, Object> pgUPTAttributeMap = dsmupt.getAttributeMap();
    				DomainObject cosmeticObj = DomainObject.newInstance(context, cosmeticFormulationID);
    				Map<Object, Object> cosmeticFormulationInfo = getCosmeticFormulationPartInfo(context, cosmeticObj);
    				boolean isFormulationPartProcessed = Boolean.FALSE;
    				if (null != cosmeticFormulationInfo && !cosmeticFormulationInfo.isEmpty()) {
    					String hasFormulationPart = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.COSMETIC_FORMULATION_HAS_FORMULATION_PART.get());
    					String hasFormulationProcess = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.COSMETIC_FORMULATION_HAS_FORMULATION_PROCESS.get());
    					String formulationPartId = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_ID.get());
    					String type = (String) cosmeticFormulationInfo.get(DomainConstants.SELECT_TYPE);
    					//Set Plant on Formulation Part
    					if(UIUtil.isNotNullAndNotEmpty(formulationPartId)){
    						UserPreferenceTemplateCreateUtil userPreferenceTemplateCreateUtil = new UserPreferenceTemplateCreateUtil();
    						existingPlantListId = DomainObject.newInstance(context,formulationPartId).getInfoList(context, "to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.id");
    						userPreferenceTemplateCreateUtil.connectPlantPreferenceToPart(context, formulationPartId, strUPTPhysicalID,existingPlantListId);
    					}

    					logger.log(Level.INFO, "Incoming Action Copy or Create?: {0}", action);
    					if (pgV3Constants.TYPE_COSMETICFORMULATION.equalsIgnoreCase(type)) {
    						if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
    							pgUPTApplySegmentOnCosmeticFormulation(context, cosmeticObj, cosmeticFormulationInfo,jsnTemplateattributeInfo);
    							pgUPTApplyPrimaryOrgOnFormulation(context, cosmeticObj, cosmeticFormulationInfo,jsnTemplateattributeInfo);
    							String[] cosmeticArgs = new String[]{pgV3Constants.TYPE_COSMETICFORMULATION, cosmeticFormulationID, jsnTemplateattributeInfo.toString()};
    							preferenceUtil.pgUPTSetIPSecurity(context, cosmeticArgs);
    							// preferenceUtil.propateUPTAttributesOnCopyPart(context,cosmeticArgs);
    						}
    	
    						JSONArray sharingMemberInfo = jsnTemplateattributeInfo.optJSONArray(DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context));
    						if(sharingMemberInfo.length()>0) {
    							for(int i=0; i<sharingMemberInfo.length(); i++) {
    								JSONObject jsnSharingMember = new JSONObject(sharingMemberInfo.optString(i));
    								strMemberIDs += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") +"|";
    							}
    							pgUPTApplySharingMembers(context, type, cosmeticFormulationID, strMemberIDs.substring(0, strMemberIDs.length()-1));
    						}
    						
    						if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
    							if ("TRUE".equalsIgnoreCase(hasFormulationPart)) { // float phase (only) and manufacturing status (preference) from cosmetic to formulation part
    								isFormulationPartProcessed = pgUPTfloatPreferenceOnFormulationPart(context, cosmeticFormulationInfo, action, jsnTemplateattributeInfo);
    							}
    							if ("TRUE".equalsIgnoreCase(hasFormulationProcess)) {
    								pgUPTFloatPreferenceOnFormulationProcess(context, cosmeticFormulationInfo, action, isFormulationPartProcessed, jsnTemplateattributeInfo);
    							}
    						}
    					}
    				}
    			} else {
    				logger.log(Level.INFO, "Incoming Cosmetic Formulation ID is empty");
    			}
    		}
    	} catch (Exception e) {
    		logger.log(Level.INFO, "Error: " + e);
    	}
    }
    
    
    /**
     * @param context
     * @param domainObject
     * @param objectMap
     * @throws MatrixException
     */
    void pgUPTApplySegmentOnCosmeticFormulation(Context context, DomainObject domainObject, Map<Object, Object> objectMap, JSONObject jsnTemplateattributeInfo) throws MatrixException {
    	if (null != objectMap && !objectMap.isEmpty()) {
    		boolean connect = Boolean.FALSE;
    		JSONObject segmentInfo = jsnTemplateattributeInfo.optJSONObject(DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context));
    		String productPreferenceSegmentID = segmentInfo.optString(DomainConstants.SELECT_ID, "");
    		String productPreferenceSegmentName = segmentInfo.optString(DomainConstants.SELECT_NAME, "");
    		if (UIUtil.isNotNullAndNotEmpty(productPreferenceSegmentID)) { // if has segment on preference.
    			String hasRelatedSegment = (String) objectMap.get((PreferenceConstants.Basic.HAS_RELATED_SEGMENT.get()));
    			if ("TRUE".equalsIgnoreCase(hasRelatedSegment)) {
    				String segmentID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_ID.get()));
    				if (!segmentID.equalsIgnoreCase(productPreferenceSegmentID)) { // if cosmetic formulation has segment.
    					String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_CONNECTION_ID.get()));
    					if (UIUtil.isNotNullAndNotEmpty(relID)) {
    						DomainRelationship.disconnect(context, relID);
    					}
    					connect = Boolean.TRUE;
    				}
    			} else {
    				connect = Boolean.TRUE;
    			}
    			if (connect) {
    				// connect segment from preference.
    				DomainRelationship.connect(context, domainObject,
    						pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT,
    						DomainObject.newInstance(context, productPreferenceSegmentID));
    				String segmentPhysicalID = (String) objectMap.get((PreferenceConstants.Basic.PHYSICAL_ID.get()));
    				domainObject.setAttributeValue(context, PropertyUtil.getSchemaProperty(context, "attribute_pgEngFrmSegment"), segmentPhysicalID);
    				logger.log(Level.INFO, "Cosmetic Formulation connected Segment & attribute updated: {0}", segmentPhysicalID);
    			}
    		}
    	} 
    }
    
    void pgUPTApplyPrimaryOrgOnFormulation(Context context, DomainObject domainObject, Map<Object, Object> objectMap , JSONObject jsnTemplateattributeInfo) throws MatrixException {
        if (null != objectMap && !objectMap.isEmpty()) {
            boolean connect = Boolean.FALSE;
            JSONObject primaryOrganizationInfo = jsnTemplateattributeInfo.optJSONObject(DSMUPTConstants.Attributes.UPT_PRIMARY_ORGANIZATION.getSelect(context));
            String preferencePrimaryOrgID = primaryOrganizationInfo.optString(DomainConstants.SELECT_ID, "");
            if (UIUtil.isNotNullAndNotEmpty(preferencePrimaryOrgID)) { // if preference primary org is present
                String hasRelatedPrimaryOrg = (String) objectMap.get(PreferenceConstants.Basic.HAS_PRIMARY_ORG.get());
                if ("TRUE".equalsIgnoreCase(hasRelatedPrimaryOrg)) { // if has primary org in preferences
                    String relatedPrimaryOrgID = (String) objectMap.get(PreferenceConstants.Basic.RELATED_PRIMARY_ORG.get());
                    if (UIUtil.isNotNullAndNotEmpty(relatedPrimaryOrgID)) { // if existing primary is valid.
                        if (!relatedPrimaryOrgID.equalsIgnoreCase(preferencePrimaryOrgID)) { // if existing primary org is not equal to preference primary org.
                            String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_PRIMARY_ORG_CONNECTION_ID.get())); // get existing primary org rel id.
                            if (UIUtil.isNotNullAndNotEmpty(relID)) { // if rel id is present.
                                DomainRelationship.disconnect(context, relID); // disconnect.
                                connect = Boolean.TRUE;
                            }
                        }
                    } else {
                        connect = Boolean.TRUE;
                    }
                } else {
                    connect = Boolean.TRUE;
                }
                if (connect) {
                    DomainRelationship.connect(context, domainObject,
                    pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION,
                    DomainObject.newInstance(context, preferencePrimaryOrgID));
                }
            }
        }
    }
   
    /**
     * @param context
     * @param cosmeticFormulationInfo
     * @param action
     * @return
     * @throws Exception
     */
    public boolean pgUPTfloatPreferenceOnFormulationPart(Context context, Map<Object, Object> cosmeticFormulationInfo, String action, JSONObject jsnTemplateattributeInfo) throws Exception {
        boolean isFormulationPartProcessed = Boolean.FALSE;
        String formulationPartType = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_TYPE.get());
        if (pgV3Constants.TYPE_FORMULATIONPART.equals(formulationPartType)) {
            UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
            String formulationPartID = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_ID.get());
            if (UIUtil.isNotNullAndNotEmpty(formulationPartID)) {
                DomainObject formulationPartObj = DomainObject.newInstance(context, formulationPartID);
                String strUPTid = jsnTemplateattributeInfo.optString(DomainConstants.SELECT_ID, "");
                UserPreferenceTemplateUtil.addUPTPhysicalIdExtension(context,strUPTid,formulationPartObj);
                Map<Object, Object> formulationPartInfo = getFormulationPartInfo(context, formulationPartObj);
                String productPreferenceType = Preferences.ProductPreference.PART_TYPE.getName(context);
                if (pgV3Constants.TYPE_FORMULATIONPART.equals(productPreferenceType)) {	
//                    String productPreferencePhase = Preferences.ProductPreference.PHASE.getName(context);
                	String productPreferencePhase = jsnTemplateattributeInfo.optString(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context), "");
                    String releasePhaseOnCosmetic = (String) cosmeticFormulationInfo.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
                    if (UIUtil.isNotNullAndNotEmpty(releasePhaseOnCosmetic) && releasePhaseOnCosmetic.equalsIgnoreCase(productPreferencePhase)) {
                        Map<String, String> attributeMap = new HashMap<>();
                        attributeMap.put(pgV3Constants.STR_RELEASE_PHASE, productPreferencePhase);
//                        attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, Preferences.ProductPreference.MANUFACTURING_STATUS.getName(context));
                        attributeMap.put(pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS, jsnTemplateattributeInfo.optString(DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context), ""));
                        formulationPartObj.setId(formulationPartID);
                        formulationPartObj.setAttributeValues(context, attributeMap);
                        logger.log(Level.INFO, "Attributes Updated on Formualtion Part: {0}", attributeMap);
                        isFormulationPartProcessed = Boolean.TRUE;
                    }
                }
                
                if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
                	pgUPTApplySegmentOnFormulationPart(context, formulationPartObj, formulationPartInfo, jsnTemplateattributeInfo);
                	pgUPTApplyPrimaryOrgOnFormulation(context, formulationPartObj, formulationPartInfo, jsnTemplateattributeInfo);
                }
				//Added by DSM for 22x CW-06 for Requirement #47972 - START
				if (PreferenceConstants.Basic.REVISE.get().equalsIgnoreCase(action)) {
                	pgUPTApplySegmentOnFormulationPart(context, formulationPartObj, formulationPartInfo, jsnTemplateattributeInfo);
                }
                //Added by DSM for 22x CW-06 for Requirement #47972 - END
                String[] tempArgs = new String[]{pgV3Constants.TYPE_FORMULATIONPART, formulationPartID, DomainConstants.EMPTY_STRING};
                try {
                	preferenceUtil.propateUPTAttributesOnCopyPart(context,tempArgs);
                }catch(Exception e) {
                	 logger.log(Level.INFO, "Error: " + e);
                }
            }
        } else {
            logger.log(Level.INFO, "Cosmetic Formulation does not have related Formulation Part type");
        }
        return isFormulationPartProcessed;
    }
    
    /**
     * @param context
     * @param formulationObj
     * @throws MatrixException
     */
    void pgUPTApplySegmentOnFormulationPart(Context context, DomainObject formulationObj, Map<Object, Object> objectMap, JSONObject jsnTemplateattributeInfo) throws MatrixException {
        if (null != objectMap && !objectMap.isEmpty()) {
            boolean connect = Boolean.FALSE;
            JSONObject segmentInfo = jsnTemplateattributeInfo.optJSONObject(DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context));
            String productPreferenceSegmentID = segmentInfo.optString(DomainConstants.SELECT_ID, "");
            String productPreferenceSegmentName = segmentInfo.optString(DomainConstants.SELECT_NAME, "");
            if (UIUtil.isNotNullAndNotEmpty(productPreferenceSegmentID)) { // if has segment on preference.
                String hasRelatedSegment = (String) objectMap.get((PreferenceConstants.Basic.HAS_RELATED_SEGMENT.get()));
                if ("TRUE".equalsIgnoreCase(hasRelatedSegment)) { // if Formulation Part already has Segment.
                    String segmentID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_ID.get()));
                    if (UIUtil.isNotNullAndNotEmpty(segmentID)) { // if segment on preference is not null/empty
                        if (!segmentID.equalsIgnoreCase(productPreferenceSegmentID)) { // if cosmetic formulation has segment.
                            String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_SEGMENT_CONNECTION_ID.get()));
                            if (UIUtil.isNotNullAndNotEmpty(relID)) {
                                DomainRelationship.disconnect(context, relID);
                                logger.log(Level.INFO, "Disconnected unmatched Segment from Formulation Part");
                            }
                            connect = Boolean.TRUE;
                        }
                    }
                } else {
                    connect = Boolean.TRUE;
                }
                if (connect) {
                    // connect segment from preference.
                    DomainRelationship.connect(context, formulationObj,
                            pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT,
                            DomainObject.newInstance(context, productPreferenceSegmentID));
                    formulationObj.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGSEGMENT, productPreferenceSegmentName);
                    logger.log(Level.INFO, "Formulation Part Segment connected and attribute updated: {0}", productPreferenceSegmentName);
                }
            }
        }
    }
    
    /**
     * @param context
     * @param domainObject
     * @param objectMap
     * @throws MatrixException
     */
    void pgUPTApplyPrimaryOrgOnFormulation(Context context, DomainObject domainObject, Map<Object, Object> objectMap) throws MatrixException {
        if (null != objectMap && !objectMap.isEmpty()) {
            boolean connect = Boolean.FALSE;
            String preferencePrimaryOrgID = Preferences.IPSecurityPreference.PRIMARY_ORGANIZATION.getID(context); // get preference primary org is present
            if (UIUtil.isNotNullAndNotEmpty(preferencePrimaryOrgID)) { // if preference primary org is present
                String hasRelatedPrimaryOrg = (String) objectMap.get(PreferenceConstants.Basic.HAS_PRIMARY_ORG.get());
                if ("TRUE".equalsIgnoreCase(hasRelatedPrimaryOrg)) { // if has primary org in preferences
                    String relatedPrimaryOrgID = (String) objectMap.get(PreferenceConstants.Basic.RELATED_PRIMARY_ORG.get());
                    if (UIUtil.isNotNullAndNotEmpty(relatedPrimaryOrgID)) { // if existing primary is valid.
                        if (!relatedPrimaryOrgID.equalsIgnoreCase(preferencePrimaryOrgID)) { // if existing primary org is not equal to preference primary org.
                            String relID = (String) objectMap.get((PreferenceConstants.Basic.RELATED_PRIMARY_ORG_CONNECTION_ID.get())); // get existing primary org rel id.
                            if (UIUtil.isNotNullAndNotEmpty(relID)) { // if rel id is present.
                                DomainRelationship.disconnect(context, relID); // disconnect.
                                connect = Boolean.TRUE;
                            }
                        }
                    } else {
                        connect = Boolean.TRUE;
                    }
                } else {
                    connect = Boolean.TRUE;
                }
                if (connect) {
                    DomainRelationship.connect(context, domainObject,
                            pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION,
                            DomainObject.newInstance(context, preferencePrimaryOrgID));
                }
            }
        }
    }
    
    /**
     * @param context
     * @param cosmeticFormulationInfo
     * @param action
     * @param isFormulationPartProcessed
     * @throws MatrixException
     */
    public void pgUPTFloatPreferenceOnFormulationProcess(Context context, Map<Object, Object> cosmeticFormulationInfo, String action, boolean isFormulationPartProcessed, JSONObject jsnTemplateattributeInfo) throws MatrixException {
        String formulationProcessType = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PROCESS_TYPE.get());
        if (pgV3Constants.TYPE_FORMULATIONPROCESS.equals(formulationProcessType)) {
            String formulationProcessID = (String) cosmeticFormulationInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PROCESS_ID.get());
            if (UIUtil.isNotNullAndNotEmpty(formulationProcessID)) {
                DomainObject formulationProcessObj = DomainObject.newInstance(context, formulationProcessID);
                if (isFormulationPartProcessed) {
                    String releasePhaseOnCosmetic = jsnTemplateattributeInfo.optString(DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context), "");
                    formulationProcessObj.setAttributeValue(context, pgV3Constants.STR_RELEASE_PHASE, releasePhaseOnCosmetic);
                    logger.log(Level.INFO, "Attribute updated on Formulation Process ID: {0}", formulationProcessID);
                }
                if (PreferenceConstants.Basic.COPY.get().equalsIgnoreCase(action)) {
                    Map<Object, Object> formulationProcessInfo = getFormulationProcessInfo(context, formulationProcessObj);
                    pgUPTApplyPrimaryOrgOnFormulation(context, formulationProcessObj, formulationProcessInfo, jsnTemplateattributeInfo);
                }
            }
        } else {
            logger.log(Level.INFO, "Cosmetic Formulation does not have related Formulation Process type");
        }
    }
    
    /**
     * @param context
     * @param type
     * @param objectOid
     * @throws Exception
     */
    void pgUPTApplySharingMembers(Context context, String type, String objectOid, String strMemberIDs) throws Exception {
        try {
            String sharingMemberIDs = strMemberIDs;
            if (!pgV3Constants.TYPE_FORMULATIONPART.equals(type) && !pgV3Constants.TYPE_FORMULATIONPROCESS.equals(type)) {
                if (UIUtil.isNotNullAndNotEmpty(sharingMemberIDs)) { // if user has set (share with members) in preferences.
                    applySharingMemberPreferences(context, objectOid, sharingMemberIDs);
                }
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
        }
    }
    
    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void pgUPTSetIPSecurity(Context context, String[] args) throws Exception {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        PreferenceConfigLoader preferenceConfigLoader = userPreferenceUtil.getPreferenceConfigLoader(context);
        if (preferenceConfigLoader.isLoaded()) {
            IPSecurityControlSettingProductCopy ipSecurityControlSetting = new IPSecurityControlSettingProductCopy.Apply(context, preferenceConfigLoader.getPreferenceConfig().getIpSecurityControlPreference()).now(args);
            if (!ipSecurityControlSetting.isApplied()) {
                logger.log(Level.WARNING, "Failed to apply IP Security");
            }
        }
    }
    /**
     * @param context
     * @param domainObject
     * @throws MatrixException
     */
    public void pgUPTApplyBusinessUseClass(Context context, DomainObject domainObject, JSONObject jsnTemplateattributeInfo) throws MatrixException, JSONException {
     //  String preferenceBusinessUse = Preferences.IPSecurityPreference.BUSINESS_USE.getID(context);
    	String strBusinessUseIds = DomainConstants.EMPTY_STRING;
    	JSONArray jsnBusinessUseInfo = jsnTemplateattributeInfo.optJSONArray(DSMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context));
        if(jsnBusinessUseInfo.length() > 0) {
            for (int i = 0; i < jsnBusinessUseInfo.length(); i++) {
                JSONObject jsnSharingMember = new JSONObject(jsnBusinessUseInfo.optString(i));
                strBusinessUseIds += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") + "|";
            }
            //   applyIPSecurityClass(context, domainObject, jsnTemplateattributeInfo);
            if (UIUtil.isNotNullAndNotEmpty(strBusinessUseIds)) {
                String businessIds = strBusinessUseIds.substring(0, strBusinessUseIds.length() - 1);
                applyIPSecurityClass(context, domainObject, businessIds);
            }
        }
    }

    /**
     * @param context
     * @param domainObject
     * @throws MatrixException
     */
    public void pgUPTApplyHighlyRestrictedUseClass(Context context, DomainObject domainObject, JSONObject jsnTemplateattributeInfo) throws MatrixException, JSONException {
     //   String preferenceBusinessUse = Preferences.IPSecurityPreference.HIGHLY_RESTRICTED.getID(context);
        logger.log(Level.INFO, "domainObject: {0}", domainObject);
    	String strHighlyRestrictedUseIds = DomainConstants.EMPTY_STRING;
    	JSONArray jsnHighlyRestrictedUseInfo = jsnTemplateattributeInfo.optJSONArray(DSMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context));
        if(jsnHighlyRestrictedUseInfo.length() > 0) {
            for (int i = 0; i < jsnHighlyRestrictedUseInfo.length(); i++) {
                JSONObject jsnSharingMember = new JSONObject(jsnHighlyRestrictedUseInfo.optString(i));
                strHighlyRestrictedUseIds += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") + "|";
            }
            // applyIPSecurityClass(context, domainObject, jsnTemplateattributeInfo);
            logger.log(Level.INFO, "strHighlyRestrictedUseIds: {0}", strHighlyRestrictedUseIds);
            if (UIUtil.isNotNullAndNotEmpty(strHighlyRestrictedUseIds)) {
                String highlyRestricted = strHighlyRestrictedUseIds.substring(0, strHighlyRestrictedUseIds.length() - 1);
                applyIPSecurityClass(context, domainObject, highlyRestricted);
            }
        }
    }
 // Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47565 - End	
	
	/**
     * @Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect# 55696
     * @param context
     * @param objectOid
     * @param uptPhyID
     * @throws Exception
     */
    public void removeExistingTechSpecPlant(Context context,DomainObject objectOid, String uptPhyID) throws Exception {
        try {
            StringList uptPlantIds = DomainObject.newInstance(context, uptPhyID).getInfoList(context, "to[" + UserPreferenceTemplateConstants.Relationship.RELATIONSHIP_USER_PREFERENCE_PLANT.getName(context) + "].from.id");
            if (!uptPlantIds.isEmpty() && uptPlantIds != null) {
                StringList objectSelects = new StringList();
                objectSelects.add(DomainConstants.SELECT_ID);
                StringList relSelects = new StringList();
                relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);
                List<String> toDisconnectList = new ArrayList<>();
                MapList relatedObjects = objectOid.getRelatedObjects(context,
                        PropertyUtil.getSchemaProperty(context, pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY),
                        pgV3Constants.TYPE_PLANT,
                        objectSelects,
                        relSelects,
                        true,
                        false,
                        (short) 1,
                        DomainConstants.EMPTY_STRING,
                        DomainConstants.EMPTY_STRING,
                        0);
                Map map = null;
                if (relatedObjects != null && relatedObjects.size() > 0) {
                    for (int i = 0; i < relatedObjects.size(); i++) {
                        map = (Map) relatedObjects.get(i);
                        if (!uptPlantIds.contains((String) map.get(DomainObject.SELECT_ID))) {
                            toDisconnectList.add((String) map.get(DomainObject.SELECT_RELATIONSHIP_ID));
                        }
                    }
                }
                if (!toDisconnectList.isEmpty()) {
                    String[] toDisconnectArr = toDisconnectList.toArray(String[]::new);
                    DomainRelationship.disconnect(context, toDisconnectArr);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error" + e);
        }
    }
    
    /**
	 * Requirement 47972.
	 * This method is to propagate user preference template attributes on Formulation Process Revise.
	 * @param context
	 * @param strCostFormId : Cosmetic Formulation ID.
	 * @param strAction : Copy or Revise as Action.
	 * @param strUPTId : UPT ID.
	 * @return void.
	 * @throws Exception 
	 */
    public void updateFormulationOnRevise(Context context, String strCostFormId, String strAction, String strUPTId) throws Exception {
    	Instant startTime = Instant.now();
    	logger.log(Level.INFO, "strCostFormId: {0}", strCostFormId);
    	logger.log(Level.INFO, "strAction: {0}", strAction);
    	logger.log(Level.INFO, "strUPTId: {0}", strUPTId);
    	if (PreferenceConstants.Basic.REVISE.get().equalsIgnoreCase(strAction)) {
    		Map<Object, Object> mCostFormInfo= getFormulationInfo(context, strCostFormId);
    		String strPartType = (String) mCostFormInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_TYPE.get());
    		String strFormulationPartId = (String) mCostFormInfo.get(PreferenceConstants.Basic.SELECT_FORMULATION_PART_ID.get());
    		String strType=(String) mCostFormInfo.get(DomainConstants.SELECT_TYPE);
    		try {
    			if (pgV3Constants.TYPE_FORMULATIONPART.equals(strPartType) && UIUtil.isNotNullAndNotEmpty(strFormulationPartId)) {
    				DomainObject domFromulationPart = DomainObject.newInstance(context, strFormulationPartId);
    				//GET UPT Information.
    				DSMUPT dsmupt = new DSMUPT(context, strUPTId);
    				String pgUPTAttributeJson = dsmupt.getAttributeJson();
    				JSONObject objectUPTJson = new JSONObject(pgUPTAttributeJson);
    				String uptOid = objectUPTJson.optString(DomainConstants.SELECT_ID, "");
    				//Get UPT Physical ID.
    				DomainObject domUPT = DomainObject.newInstance(context,uptOid);
    				String strUPTPhyId=domUPT.getInfo(context,DomainConstants.SELECT_PHYSICAL_ID);
    				logger.log(Level.INFO, "UPT Physical Id :: " + strUPTPhyId);
    				//Update UPT Physical ID on Formulation Part.
    				UserPreferenceTemplateUtil.addUPTPhysicalIdExtension(context, strUPTPhyId, domFromulationPart);
    				//Update Sharing Members on Cosmetic Formulation.
    				JSONArray sharingMemberInfo = objectUPTJson.optJSONArray(DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context));
    				String strMemberIDs = DomainConstants.EMPTY_STRING;
    				if(sharingMemberInfo.length()>0) {
    					for(int i=0; i<sharingMemberInfo.length(); i++) {
    						JSONObject jsnSharingMember = new JSONObject(sharingMemberInfo.optString(i));
    						strMemberIDs += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") +"|";
    					}
    					pgUPTApplySharingMembers(context, strType, strCostFormId, strMemberIDs.substring(0, strMemberIDs.length()-1));
    				}
    			} else {
    				logger.log(Level.INFO, "Cosmetic Formulation does not have related Formulation Part type");
    			}
    		} catch (Exception e) {
    			logger.log(Level.WARNING, "Exception occurred in method updateUserPrefTempAttrsOnFormulationPart() : " + e);
    		}
    	}
    	Instant endTime = Instant.now();
    	Duration duration = Duration.between(startTime, endTime);
    	logger.log(Level.INFO, "Method updateUserPrefTempAttrsOnFormulationPart() - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }
    
    /**
   	 * Requirement 47972.
   	 * This method is to get Cosmetic Formulation Information.
   	 * @param context
   	 * @param strCostFormId : Cosmetic Formulation ID.
   	 * @return Map<Object, Object> : Cosmetic Formulation Info.
   	 * @throws Exception 
   	 */
	@SuppressWarnings({ "unchecked", "unused" })
	Map<Object, Object> getFormulationInfo(Context context, String strCostFormId) throws Exception {
		logger.log(Level.INFO, "strCostFormId: {0}", strCostFormId);
		Map<Object, Object> mReturnFormulation = new HashMap<>();
		DomainObject domCostFormulation = DomainObject.newInstance(context, strCostFormId);
		//Get Cosmetic Formulation Revision.
		StringList slBusSelects = new StringList(6);
		slBusSelects.add(DomainConstants.SELECT_TYPE);
		slBusSelects.add(DomainConstants.SELECT_REVISION);
		//Object Where.
		String objectWhere = "revision==last.revision";
		//Bus Selects
		slBusSelects = new StringList(3);
		slBusSelects.add(DomainConstants.SELECT_TYPE);
		slBusSelects.add(DomainConstants.SELECT_REVISION);
		slBusSelects.add(DomainConstants.SELECT_ID);

		try {
			//Get CostmeticFormulation Information.
			Map<String,String> mCostFormInfo = domCostFormulation.getInfo(context, slBusSelects);
			if(null != mCostFormInfo && mCostFormInfo.size() > 0) {
				mReturnFormulation.putAll(mCostFormInfo);
			}	
			MapList mlFormulationPart = domCostFormulation.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE, 		// relationship pattern
					DomainObject.QUERY_WILDCARD, 							// type pattern
					slBusSelects, 											// Object selects
					null, 													// relationship selects
					false, 													// from
					true, 													// to
					(short) 1, 												// expand level
					objectWhere, 											// object where
					null, 													// relationship where
					0);                 						  			// limit

			logger.log(Level.INFO, "mlFormulationPart {} ", mlFormulationPart);
			Iterator<Map<String, String>> itrItem = mlFormulationPart.iterator();
			while (itrItem.hasNext()) {
				Map<String, String> mFormulationPart = itrItem.next();	
				String strType=mFormulationPart.get(DomainConstants.SELECT_TYPE);
				mReturnFormulation.put(PreferenceConstants.Basic.SELECT_FORMULATION_PART_TYPE.get(), strType);
				if (pgV3Constants.TYPE_FORMULATIONPART.equals(strType)) {
					String sFormPartId=mFormulationPart.get(DomainConstants.SELECT_ID);
					mReturnFormulation.put(PreferenceConstants.Basic.SELECT_FORMULATION_PART_ID.get(), sFormPartId);
				}
			}
			logger.log(Level.INFO, "mReturnFormulation: {0}", mReturnFormulation);
		} catch (Exception e) {
			logger.log(Level.WARNING,"Exception in Method getCosmeticFormulationPartInfo()::", e);
		}
		return mReturnFormulation;
	}
	
	/**
   	 * Requirement 22x.06 JAS 47975 and 47969.
   	 * This method is Update User Preference template attributes on MEP/SEP.
   	 * @param context
   	 * @param ioParams : contains UPT and MEP/SEP Id..
   	 * @return void.
   	 * @throws Exception 
   	 */
	public void updateUPTAttributesOnEquivalentPart(Context context, Map programMap) throws Exception {
		Instant startTime = Instant.now();
		if(null != programMap && programMap.size() > 0) {
			Map<String, String> requestMap = (HashMap) programMap.get("requestMap");
			Map<String, String> paramMap = (HashMap) programMap.get("paramMap");
			String strPartId = paramMap.get("newObjectId");
			String strUPTId = requestMap.get("pgUPTOID");
			logger.log(Level.INFO, "Part Id :: " + strPartId);
			logger.log(Level.INFO, "UPT Id :: " + strUPTId);
			try {
				if(UIUtil.isNotNullAndNotEmpty(strPartId) && UIUtil.isNotNullAndNotEmpty(strUPTId)) {
					DomainObject domPart = DomainObject.newInstance(context, strPartId);
					//Get UPT Physical ID.
    				DomainObject domUPT = DomainObject.newInstance(context,strUPTId);
    				strUPTId=domUPT.getInfo(context,DomainConstants.SELECT_PHYSICAL_ID);		
    				logger.log(Level.INFO, "UPT Physical Id :: " + strUPTId);
    				//Update UPT Physical ID on Formulation Part.
    				UserPreferenceTemplateUtil.addUPTPhysicalIdExtension(context,strUPTId,domPart);
					//Get Existing plants from MEP/SEP.
					StringList slMEPSEPExtPlants = domPart.getInfoList(context, "to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.id");
					//propagate UPT plants on MEP/SEP.
					UserPreferenceTemplateCreateUtil userPreferenceTemplateCreateUtil = new UserPreferenceTemplateCreateUtil();
					userPreferenceTemplateCreateUtil.connectPlantPreferenceToPart(context, strPartId, strUPTId, slMEPSEPExtPlants);
					//Apply Sharing Members on MEP/SEP
					applySharingMembersOnEquivalentPart(context, strPartId, strUPTId);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING,"Exception in Method propagetUPTAttributesOnMEPSEP()::", e);
			}
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.log(Level.INFO, "Method propagetUPTAttributesOnMEPSEP() - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
		}
	}

	/**
   	 * Requirement 22x.06 JAS 47975 and 47969.
   	 * This method is to update UPT sharing members on MEP/SEP.
   	 * @param context
   	 * @param strPartId : MEP/SEP ID.
   	 * @param strUPTPhyId : UPT Physical ID.
   	 * @return void.
   	 * @throws Exception 
   	 */
	private void applySharingMembersOnEquivalentPart(Context context, String strPartId, String strUPTPhyId) throws Exception {
		DSMUPT dsmupt = new DSMUPT(context, strUPTPhyId);
		//Get UPT JSON.
		String pgUPTAttributeJson = dsmupt.getAttributeJson();
		JSONObject jsnTemplateattributeInfo = new JSONObject(pgUPTAttributeJson);
		//Get Sharing member from UPT.
		String strMemberIDs = DomainConstants.EMPTY_STRING;
		JSONArray sharingMemberInfo = jsnTemplateattributeInfo.optJSONArray(DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context));
		if(sharingMemberInfo.length() > 0) {
			for(int i=0; i<sharingMemberInfo.length(); i++) {
				JSONObject jsnSharingMember = new JSONObject(sharingMemberInfo.optString(i));
				strMemberIDs += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") +"|";
			}
			//Apply Sharing members on MEP/SEP.
			if(strMemberIDs.length() > 0) {
				applySharingMemberPreferences(context, strPartId, strMemberIDs);
			}
		}
	}
	
	/**
     * Added by IRM (DSM) Sogeti for 2022x-06 Dec CW - Requirement 47860
     *
     * @param context
     * @param args
     * @throws Exception
     */
	public void propagateUPTAttributesOnRevisePart(Context context, String[] args) throws Exception {
		try {
			PartCreateAction partCreateAction = new PartCreateAction();
			partCreateAction.propagateUPTAttributesOnRevisePart(context, args);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception: " + e);
			throw e;
		}
	}
	
	/**
     * Added by IRM (DSM) Sogeti for 2022x-06 Dec CW - Requirement 47860
     * This method will update the UPT Preferences on Revise of Copy Product Data
     * @param context
     * @param args
     * @throws Exception
     */
    public void updateUPTAttrOnReviseCopyProdData (Context context, String[] args) throws Exception {
    	try {
    		String type = args[0];
    		String objectId = args[1];
    		if(UIUtil.isNotNullAndNotEmpty(type) && UIUtil.isNotNullAndNotEmpty(objectId))  {
    			logger.log(Level.INFO, "Propagate User Preference Template Attributes on Incoming Object > | {0} | {1} |", new Object[]{type, objectId});
    			DomainObject domainObject = DomainObject.newInstance(context, objectId);
    			String physIDSelect = UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(context);
    			String partCategorySelect = UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getSelect(context);
    			String strUPTPhyId = PropertyUtil.getGlobalRPEValue(context, "UPT_PID");
    			logger.log(Level.INFO, "UPT Physical ID from global RPE: " + strUPTPhyId);
    			DomainObject domUPT = DomainObject.newInstance(context,strUPTPhyId);
    			String strNewUPTPhyId =domUPT.getInfo(context,DomainConstants.SELECT_PHYSICAL_ID);
    			UserPreferenceTemplateUtil.addUPTPhysicalIdExtension(context, strNewUPTPhyId, domainObject);
    			Map objectInfo = domainObject.getInfo(context, StringList.create(DomainConstants.SELECT_POLICY, physIDSelect));
    			String objectPolicy = (String) objectInfo.get(DomainConstants.SELECT_POLICY);
    			String uptPhyID = (String) objectInfo.get(physIDSelect); // get the UPT physical ID stored on incoming object.
    			if (UIUtil.isNotNullAndNotEmpty(uptPhyID)) {
    				DomainObject uptObject = DomainObject.newInstance(context, uptPhyID);
    				String partCategoryName = uptObject.getInfo(context, partCategorySelect);
    				if (UIUtil.isNotNullAndNotEmpty(partCategoryName)) {
    					logger.log(Level.INFO, "Part Category on UPT: " + partCategoryName);
    					DSMUPT dsmupt = new DSMUPT(context, uptPhyID);
    					String pgUPTAttributeJson = dsmupt.getAttributeJson();
    					JSONObject jsnTemplateattributeInfo = new JSONObject(pgUPTAttributeJson);
    					//Set Sharing member on Product Part -Start
    					String strMemberIDs = DomainConstants.EMPTY_STRING;
    					JSONArray sharingMemberInfo = jsnTemplateattributeInfo.optJSONArray(DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context));
    					if(sharingMemberInfo.length()>0) {
    						for(int i=0; i<sharingMemberInfo.length(); i++) {
    							JSONObject jsnSharingMember = new JSONObject(sharingMemberInfo.optString(i));
    							strMemberIDs += jsnSharingMember.optString(DomainConstants.SELECT_ID, "") +"|";
    						}
    						pgUPTApplySharingMembers(context, type, objectId, strMemberIDs.substring(0, strMemberIDs.length()-1));
    					}
    					//Set Sharing member on Product Part - End
    				} else {
    					logger.log(Level.WARNING, "UPT Part Category is empty on incoming object");
    				}
    			} else {
    				logger.log(Level.WARNING, "UPT Physical ID is empty on incoming object");
    			}
    		}
    	} catch (Exception e) {
    		logger.log(Level.WARNING, "Exception in Method updateUPTAttrOnReviseCopyProdData(): " + e);
    	}
    }
	//Added by DSM (Sogeti) for 22x CW-06 Requirement#47860 - End
}

