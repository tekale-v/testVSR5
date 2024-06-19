package com.pg.dsm.preference.template.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.entity.Template;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;


public class UserPreferenceTemplateCreateUtil {

    private static final Logger logger = Logger.getLogger(UserPreferenceUtil.class.getName());

    public UserPreferenceTemplateCreateUtil() {

    }

    /**
     * Compare selected type with attribute pgObjectType Value
     *
     * @param context
     * @param attrObjectType
     * @param partType
     * @return
     */
    private static boolean isTypeMatchWithObjectType(String attrObjectType, String partType) throws FrameworkException {
        boolean isTypeMatch = false;
        StringList splitAttrObjectType = null;
        splitAttrObjectType = FrameworkUtil.split(attrObjectType, ",");
        if (!splitAttrObjectType.isEmpty()) {
            for (int i = 0; i < splitAttrObjectType.size(); i++) {
                if (splitAttrObjectType.get(i).equals(partType)) {
                    isTypeMatch = true;
                    break;
                }
            }
        }
        return isTypeMatch;
    }

    /**
     * get filtered routTemplate using selected org and Type on create User Preference Template Page
     *
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public StringList getOrgAndTypeRelatedRouteTemplateIDs(Context context, String[] args) throws Exception {
        Map programMap = (Map) JPO.unpackArgs(args);
        String orgNames = (String) programMap.get("PrimaryOrganization");
        String state = (String) programMap.get("vState");
        String purpose = (String) programMap.get("vPurpose");
        String partType = (String) programMap.get("PartType");
        logger.log(Level.INFO, "Selected Part Type Create Template Page" + partType);
        return getOrgAndTypeRelatedRouteTemplateIDs(context, orgNames, state, purpose, partType);
    }

    /**
     * get Template Object Id for Include in search
     *
     * @param context
     * @param orgName
     * @return
     * @throws FrameworkException
     */
    public StringList getOrgAndTypeRelatedRouteTemplateIDs(Context context, String orgNames, String state, String purpose, String partType) throws FrameworkException {
        StringList objectList = new StringList();
        String type = PreferenceConstants.Type.TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT.getName(context);
        String orgIDs = getPipeSeparatedIDs(context, type, orgNames);
        if (UIUtil.isNotNullAndNotEmpty(orgIDs)) {
            StringList orgList = StringUtil.split(orgIDs, pgV3Constants.SYMBOL_PIPE);
            if (null != orgList && !orgList.isEmpty()) {
                MapList templateList;
                for (String orgID : orgList) {
                    if (UIUtil.isNotNullAndNotEmpty(orgID)) {
                        templateList = getOrgAndTypeRelatedRouteTemplates(context, orgID, partType);
                        if (null != templateList && !templateList.isEmpty()) {
                            objectList.addAll(getOrgAndTypeRelatedRouteTemplateIDs(context, templateList, state, purpose, partType));
                        }
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
    public MapList getOrgAndTypeRelatedRouteTemplates(Context context, String orgID, String partType) throws FrameworkException {
        DomainObject domainObject = DomainObject.newInstance(context, orgID);
        return domainObject.getRelatedObjects(context, // context
                pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION, // relationship Pattern
                DomainConstants.TYPE_ROUTE_TEMPLATE, // type Pattern
                StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT, DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE), "attribute[pgObjectType]"), // object select
                DomainConstants.EMPTY_STRINGLIST, // relationship select
                true, // getTo
                false, // getFrom
                (short) 1, // recurse level
                DomainConstants.EMPTY_STRING, // object where expression
                DomainConstants.EMPTY_STRING, // relationship where expression
                0);// limit
    }

    /**
     * @param context
     * @param templateList
     * @param state
     * @param purpose
     * @return
     */
    @SuppressWarnings("unchecked")
    public StringList getOrgAndTypeRelatedRouteTemplateIDs(Context context, MapList templateList, String state, String purpose, String partType) throws FrameworkException {
        StringList objectList = new StringList();
        if (null != templateList && !templateList.isEmpty()) {
            @SuppressWarnings("rawtypes")
            Iterator iterator = templateList.iterator();
            Map<Object, Object> objectMap;
            String attrObjectType;
            while (iterator.hasNext()) {
                objectMap = (Map<Object, Object>) iterator.next();
                attrObjectType = (String) objectMap.get("attribute[pgObjectType]");
                if (UIUtil.isNotNullAndNotEmpty(attrObjectType)) {
                    boolean isTypeMatch = isTypeMatchWithObjectType(attrObjectType, partType);
                    if (isTypeMatch && state.equalsIgnoreCase((String) objectMap.get(DomainConstants.SELECT_CURRENT)) && purpose.equalsIgnoreCase((String) objectMap.get(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE)))) {
                        objectList.add((String) objectMap.get(DomainConstants.SELECT_ID));
                    }
                }
            }
        }
        return objectList;
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
     * @param partCategory
     * @return
     * @throws FrameworkException
     */
    public String getObjectName(Context context, Template template) throws MatrixException {
        String partCategory = template.getPartCategory().trim();
        String partType = template.getPartType().trim();
        String name = template.getName();
        logger.log(Level.INFO, "User Preference Template type: " + partType);
        String objectName = DomainConstants.EMPTY_STRING;
        String autoName = FrameworkUtil.autoName(context, UserPreferenceTemplateConstants.Basic.AUTONAME_TYPE_USER_PREFERENCE_TEMPLATE.get(), null, UserPreferenceTemplateConstants.Basic.AUTONAME_POLICY_USER_PREFERENCE_TEMPLATE_DSM.get(), null, null, true, false);
        BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, UserPreferenceTemplateConstants.Basic.CONFIG_OBJ_TYPE_MAPPING.get(), "-", pgV3Constants.VAULT_ESERVICEPRODUCTION);
        if (UIUtil.isNullOrEmpty(name)) {
            if (UIUtil.isNotNullAndNotEmpty(partCategory) && boConfig.exists(context) && UIUtil.isNotNullAndNotEmpty(partType)) {
                String strAttrValue = boConfig.getAttributeValues(context, pgV3Constants.ATTRIBUTE_PGTYPEMAPPING).getValue().trim();
                StringList slTypeName = FrameworkUtil.split(strAttrValue, ",");
                String strType = null;
                String strShortTypeName = null;
                StringList slShortName = new StringList();
                for (int i = 0; i < slTypeName.size(); i++) {
                    strType = slTypeName.get(i).toString();
                    if (UIUtil.isNotNullAndNotEmpty(strType)) {
                        slShortName = FrameworkUtil.split(strType, ":");
                        if (!slShortName.isEmpty() && partType.equals(slShortName.get(0).toString())) {
                            strShortTypeName = slShortName.get(3).toString();
                            objectName = strShortTypeName + "-" + partCategory + "-" + autoName;
                            break;
                        }
                    }
                }
            } else if (UIUtil.isNotNullAndNotEmpty(partCategory)) {
                objectName = partCategory + "-" + autoName;
            }
        } else {
            objectName = name;
        }
        logger.log(Level.INFO, "User Preference Template AutoName: " + objectName);
        return objectName;
    }

    /**
     * @param context
     * @param type
     * @param name
     * @return
     * @throws FrameworkException
     */
    @SuppressWarnings("rawtypes")
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
     * @param productPartId
     * @param uptId
     * @throws Exception
     */
    public void connectPlantPreferenceToPart(Context context, String productPartId, String uptId, StringList existingPlantListId) throws Exception {
        Map<String, String> attributeMap = new HashMap<String, String>();
        if (UIUtil.isNotNullAndNotEmpty(productPartId) && UIUtil.isNotNullAndNotEmpty(uptId)) {
            DomainObject domainObjectPart = DomainObject.newInstance(context, productPartId);
            String partType = domainObjectPart.getInfo(context, DomainConstants.SELECT_TYPE);
            DomainObject domainObjectUPT = DomainObject.newInstance(context, uptId);
            StringList strObjSel = new StringList(1);
            strObjSel.add(DomainConstants.SELECT_ID);
            StringList strRelSel = new StringList(4);
            strRelSel.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
            strRelSel.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
            strRelSel.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
            strRelSel.add(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
            MapList pgUserPreferencePlant = domainObjectUPT.getRelatedObjects(context, UserPreferenceTemplateConstants.Relationship.RELATIONSHIP_USER_PREFERENCE_PLANT.getName(context), pgV3Constants.TYPE_PLANT, strObjSel, strRelSel, true, false, (short) 1, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);// object select
            if (!pgUserPreferencePlant.isEmpty()) {
                Map map = new HashMap();
                DomainRelationship rel = new DomainRelationship();
                Iterator iterator = pgUserPreferencePlant.iterator();
                while (iterator.hasNext()) {
                    map = (Map) iterator.next();
                    attributeMap = getAttributeMapforPlantPreference(context, map, productPartId, partType);
                    String uptPlantId = (String) map.get(DomainConstants.SELECT_ID);
                    try {
                        if(!existingPlantListId.contains(uptPlantId)) {
                            rel = DomainRelationship.connect(context, new DomainObject(uptPlantId), pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY, domainObjectPart);
                        rel.setAttributeValues(context, attributeMap);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public Map<String, String> getConfigObjectDetails(Context context) throws MatrixException {
        Map<String, String> map = new HashMap<>();
        BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, UserPreferenceTemplateConstants.Basic.CONFIG_OBJ_TYPE_MAPPING.get(), "-", pgV3Constants.VAULT_ESERVICEPRODUCTION);
        StringList strObjSel = new StringList(4);
        strObjSel.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_ACTIVATED.getSelect(context));
        strObjSel.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_USE_AND_PRODUCE.getSelect(context));
        strObjSel.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_VIEW.getSelect(context));
        strObjSel.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_VIEW_DEFAULT_SET.getSelect(context));
        if (boConfig.exists(context)) {
            String objectId = boConfig.getObjectId(context);
            DomainObject domainObject = DomainObject.newInstance(context, objectId);
            map = domainObject.getInfo(context, strObjSel);
        }
        logger.log(Level.INFO, "Config object Map" + map);
        return map;
    }

    /**
     * @param context
     * @param map
     * @param productPartId
     * @param partType
     * @return
     * @throws Exception
     */
    public Map<String, String> getAttributeMapforPlantPreference(Context context, Map map, String productPartId, String partType) throws Exception {
        Map<String, String> attributeMap = new HashMap<String, String>();
        String isAuthorizedtoUse = (String) map.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
        String isAuthorizedtoProduce = (String) map.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
        String isAuthorizedtoView = (String) map.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
        String isActivated = (String) map.get(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);

        Map<String, String> configObjectDetails = getConfigObjectDetails(context);
        String setIsActivatedOnAllowedTypeFromUPT = (String) configObjectDetails.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_ACTIVATED.getSelect(context));
        String setIsAuthorizedtoUseAndProduceOnAllowedTypeFromUPT = (String) configObjectDetails.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_USE_AND_PRODUCE.getSelect(context));
        String setAuthorizedToViewOnAllowedTypeFromUPT = (String) configObjectDetails.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_VIEW.getSelect(context));
        String setDefaultValForAuthorizedOnAllowedType = (String) configObjectDetails.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_TYPES_FOR_AUTHORIZED_TO_VIEW_DEFAULT_SET.getSelect(context));

        if (UIUtil.isNotNullAndNotEmpty(partType)) {
            StringList authorizedUseAndProduceAllowdType = FrameworkUtil.split(setIsAuthorizedtoUseAndProduceOnAllowedTypeFromUPT, ",");
            for (int i = 0; i < authorizedUseAndProduceAllowdType.size(); i++) {
                if (partType.equals(authorizedUseAndProduceAllowdType.get(i))) {
                    if ("TRUE".equals(isAuthorizedtoUse)) {
                        isAuthorizedtoProduce = "FALSE";
                    } else {
                        isAuthorizedtoProduce = "TRUE";
                    }
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE, isAuthorizedtoUse);
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE, isAuthorizedtoProduce);
                    break;
                }
            }

            StringList authorizedToViewAllowdType = FrameworkUtil.split(setAuthorizedToViewOnAllowedTypeFromUPT, ",");
            for (int i = 0; i < authorizedToViewAllowdType.size(); i++) {
                if (partType.equals(authorizedToViewAllowdType.get(i))) {
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOVIEW, isAuthorizedtoView);
                    break;
                }
            }

            StringList activatedAllowdType = FrameworkUtil.split(setIsActivatedOnAllowedTypeFromUPT, ",");
            for (int i = 0; i < activatedAllowdType.size(); i++) {
                if (partType.equals(activatedAllowdType.get(i))) {
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISACTIVATED, isActivated);
                    break;
                }
            }

            StringList defaultValForAuthorizedToViewAllowdType = FrameworkUtil.split(setDefaultValForAuthorizedOnAllowedType, ",");
            for (int i = 0; i < defaultValForAuthorizedToViewAllowdType.size(); i++) {
                if (partType.equals(defaultValForAuthorizedToViewAllowdType.get(i))) {
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOVIEW, "TRUE");
                    break;
                }
            }
        }
        logger.log(Level.INFO, "Plant connection on Part: attributeMap" + attributeMap);
        return attributeMap;
    }

}
