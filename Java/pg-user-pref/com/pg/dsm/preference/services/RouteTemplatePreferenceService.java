package com.pg.dsm.preference.services;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class RouteTemplatePreferenceService {

    boolean isConnected;
    String errorMessage;

    private RouteTemplatePreferenceService(Connector connector) {
        this.isConnected = connector.isConnected;
        this.errorMessage = connector.errorMessage;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Connector {
        private final Logger logger = Logger.getLogger(this.getClass().getName());

        Context context;
        String objectOid;
        boolean isConnected;
        String errorMessage;

        public Connector(Context context, String objectOid) {
            this.context = context;
            this.objectOid = objectOid;
        }

        public RouteTemplatePreferenceService connect() {
            try {
                performConnectOperation();
                this.isConnected = true;

            } catch (Exception e) {
                this.errorMessage = e.getMessage();
                this.isConnected = false;
            }
            return new RouteTemplatePreferenceService(this);
        }

        void performConnectOperation() throws Exception {

            StringList objectSelects = new StringList();
            objectSelects.add(DomainConstants.SELECT_TYPE);
            objectSelects.add(DomainConstants.SELECT_POLICY);
            objectSelects.add(DomainConstants.SELECT_CURRENT);
            objectSelects.add(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(this.context));


            boolean isContextPushed = false;

            try {

                // need to push context for connecting Route Template and Change Action. (behavior similar to UI)
                ContextUtil.pushContext(this.context, PropertyUtil.getSchemaProperty(this.context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                isContextPushed = true;
                Map relAttributeMap = new HashMap();

                String typeChangeAction = PreferenceConstants.Type.TYPE_CHANGE_ACTION.getName(this.context);

                DomainObject domainObject = DomainObject.newInstance(this.context, this.objectOid);

                Map objectInfo = domainObject.getInfo(this.context, objectSelects);
                String policy = (String) objectInfo.get(DomainConstants.SELECT_POLICY);
                String policyNameSymbolic = FrameworkUtil.getAliasForAdmin(this.context, "Policy", policy, true);
                String type = (String) objectInfo.get(DomainConstants.SELECT_TYPE);
                String current = (String) objectInfo.get(DomainConstants.SELECT_CURRENT);
                String pgUPTPhyID = (String) objectInfo.get(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getSelect(this.context));
                logger.log(Level.INFO, "pgUPTPhyID from Stand Alone CA: " + pgUPTPhyID);
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                MapList userPreferenceTemplateDetails = userPreferenceUtil.getUserPreferenceTemplateDetails(context, this.objectOid, pgUPTPhyID);
                relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_POLICY, policyNameSymbolic);

                if (typeChangeAction.equals(type) && ChangeConstants.STATE_CHANGE_ACTION_PREPARE.equalsIgnoreCase(current)) {
                    // get the route templates from preferences.
                    StringList routeTemplateInWorkOID = userPreferenceUtil.getRouteTemplateInWorkOID(context, userPreferenceTemplateDetails);
                    logger.log(Level.INFO, "get list of routeTemplateInWorkOID to connect CA: " + routeTemplateInWorkOID);
                    if (null != routeTemplateInWorkOID && !routeTemplateInWorkOID.isEmpty()) {
                        String inWorkStateSymbolic = FrameworkUtil.reverseLookupStateName(this.context, policy, ChangeConstants.STATE_CHANGE_ACTION_INWORK);
                        relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, inWorkStateSymbolic);
                        String flag = UserPreferenceTemplateConstants.Basic.ROUTE_TEMPLATE.get();
                        // connect Change Action with Review - Route Templates
                        connect(domainObject, routeTemplateInWorkOID, relAttributeMap, flag);
                    }

                    StringList routeTemplateInApprovalOID = userPreferenceUtil.getRouteTemplateInApprovalOID(context, userPreferenceTemplateDetails);
                    logger.log(Level.INFO, "get list of routeTemplateInApprovalOID to connect CA: " + routeTemplateInApprovalOID);
                    if (null != routeTemplateInApprovalOID && !routeTemplateInApprovalOID.isEmpty()) {
                        String inApprovalStateSymbolic = FrameworkUtil.reverseLookupStateName(this.context, policy, ChangeConstants.STATE_CHANGE_ACTION_INAPPROVAL);
                        relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, inApprovalStateSymbolic);
                        String flag = UserPreferenceTemplateConstants.Basic.ROUTE_TEMPLATE.get();
                        // connect Change Action with Approval - Route Templates
                        connect(domainObject, routeTemplateInApprovalOID, relAttributeMap, flag);
                    }
                    if (UIUtil.isNullOrEmpty(pgUPTPhyID)) {
                        StringList informedUserOID = userPreferenceUtil.getInformedUserOID(context, userPreferenceTemplateDetails);
                        logger.log(Level.INFO, "get list of informedUserOID to connect CA: " + informedUserOID);
                        if (null != informedUserOID && !informedUserOID.isEmpty()) {
                            String flag = UserPreferenceTemplateConstants.Basic.INFORMED_USER.get();
                            // connect Change Action with Informed Users
                            connect(domainObject, informedUserOID, relAttributeMap, flag);
                            applyInformedUserAsSharingMembersForCA(this.context,this.objectOid, informedUserOID);
                        }
                    }
                }
            } catch (MatrixException e) {
                logger.log(Level.WARNING, "Error connecting CA with user Preference Route Template(s): " + e);
                throw e;
            } finally {
                if (isContextPushed) {
                    ContextUtil.popContext(this.context);
                }
            }
        }

        /**
         *
         * @param context
         * @param objectOid
         * @param informedUserOID
         * @throws Exception
         */
        public static void applyInformedUserAsSharingMembersForCA(Context context, String objectOid, StringList informedUserOID) throws Exception {
            for (String memberID : informedUserOID) {
                String personName;
                if (UIUtil.isNotNullAndNotEmpty(memberID) && UIUtil.isNotNullAndNotEmpty(objectOid)) {
                    personName = DomainObject.newInstance(context, memberID).getInfo(context, DomainConstants.SELECT_NAME);
                    personName += "_PRJ";
                    DomainAccess.createObjectOwnership(context, objectOid, "-", personName, PreferenceConstants.Basic.DSM_SHARING_MEMBER_ACCESSES_FOR_CA_INFORMEDUSER.get(), DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                }
            }
        }
        /**
         * @param domainObject
         * @param templateList
         * @param relAttributeMap
         * @throws FrameworkException
         */
        void connect(DomainObject domainObjectCA, StringList templateList, Map relAttributeMap, String flag) throws FrameworkException {
            String stateProduction = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, DomainConstants.POLICY_ROUTE_TEMPLATE, "state_Production");
            String routeBasePurposeSelect = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE);
            StringList selects = new StringList();
            selects.add(DomainConstants.SELECT_CURRENT);
            selects.add(routeBasePurposeSelect);
            Map<Object, Object> routeTemplateMap;
            String routeBasePurpose;
            String state;

            DomainObject domainObject = DomainObject.newInstance(this.context);
            DomainRelationship domainRelationship;
            for (String oid : templateList) {
                if (UIUtil.isNotNullAndNotEmpty(oid)) {
                    domainObject.setId(oid);
                    if (UserPreferenceTemplateConstants.Basic.ROUTE_TEMPLATE.get().equals(flag)) {
                        routeTemplateMap = domainObject.getInfo(this.context, selects);
                        if (null != routeTemplateMap && !routeTemplateMap.isEmpty()) {
                            state = (String) routeTemplateMap.get(DomainConstants.SELECT_CURRENT);
                            if (stateProduction.equalsIgnoreCase(state)) {
                                routeBasePurpose = (String) routeTemplateMap.get(routeBasePurposeSelect);
                                try {
                                    relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_PURPOSE, routeBasePurpose);
                                    domainRelationship = DomainRelationship.connect(this.context, domainObjectCA,
                                            DomainObject.RELATIONSHIP_OBJECT_ROUTE, domainObject);
                                    domainRelationship.setAttributeValues(this.context, relAttributeMap);
                                } catch (Exception e) {
                                    logger.log(Level.WARNING, "Error connecting CA with user Preference Template: " + e);
                                }
                            }
                        }
                    }
                    if (UserPreferenceTemplateConstants.Basic.INFORMED_USER.get().equals(flag)) {
                        try {
                            DomainRelationship.connect(this.context, domainObjectCA, UserPreferenceTemplateConstants.Relationship.RELATIONSHIP_CHANGE_FOLLOWER.getName(context), domainObject);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Error connecting CA with user Preference Informed Users: " + e);
                        }
                    }
                }
            }
        }
    }
}
