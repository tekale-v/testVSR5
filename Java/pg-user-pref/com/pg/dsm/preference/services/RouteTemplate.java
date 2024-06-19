package com.pg.dsm.preference.services;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.ChangeActionPreference;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class RouteTemplate {

    boolean isConnected;
    String errorMessage;

    private RouteTemplate(Connector connector) {
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

        public RouteTemplate connect() {
            try {
                performConnectOperation();
                this.isConnected = true;

            } catch (Exception e) {
                this.errorMessage = e.getMessage();
                this.isConnected = false;
            }
            return new RouteTemplate(this);
        }

        void performConnectOperation() throws MatrixException {

            StringList objectSelects = new StringList();
            objectSelects.add(DomainConstants.SELECT_TYPE);
            objectSelects.add(DomainConstants.SELECT_POLICY);
            objectSelects.add(DomainConstants.SELECT_CURRENT);

            boolean isContextPushed = false;

            try {
                // get change action preference before push context.
                ChangeActionPreference changeActionPreference = new ChangeActionPreference(this.context);
                logger.log(Level.INFO, "Change Action Preferences >> " + changeActionPreference);

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
                relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_POLICY, policyNameSymbolic);

                if (typeChangeAction.equals(type) && ChangeConstants.STATE_CHANGE_ACTION_PREPARE.equalsIgnoreCase(current)) {
                    // get the route templates from preferences.
                    String routeTemplateStr = changeActionPreference.getRouteTemplateInWorkOID();
                    if (UIUtil.isNotNullAndNotEmpty(routeTemplateStr)) {
                        StringList inWorkRouteTemplateList = StringUtil.split(routeTemplateStr, PreferenceConstants.Basic.SYMBOL_PIPE.get());
                        if (null != inWorkRouteTemplateList && !inWorkRouteTemplateList.isEmpty()) {
                            String inWorkStateSymbolic = FrameworkUtil.reverseLookupStateName(this.context, policy, ChangeConstants.STATE_CHANGE_ACTION_INWORK);
                            relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, inWorkStateSymbolic);
                            // connect Change Action with Review - Route Templates
                            connect(domainObject, inWorkRouteTemplateList, relAttributeMap);
                        }
                    }
                    routeTemplateStr = changeActionPreference.getRouteTemplateInApprovalOID();
                    if (UIUtil.isNotNullAndNotEmpty(routeTemplateStr)) {
                        StringList inApprovalRouteTemplateList = StringUtil.split(routeTemplateStr, PreferenceConstants.Basic.SYMBOL_PIPE.get());
                        if (null != inApprovalRouteTemplateList && !inApprovalRouteTemplateList.isEmpty()) {
                            String inApprovalStateSymbolic = FrameworkUtil.reverseLookupStateName(this.context, policy, ChangeConstants.STATE_CHANGE_ACTION_INAPPROVAL);
                            relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, inApprovalStateSymbolic);
                            // connect Change Action with Approval - Route Templates
                            connect(domainObject, inApprovalRouteTemplateList, relAttributeMap);
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
         * @param domainObject
         * @param templateList
         * @param relAttributeMap
         * @throws FrameworkException
         */
        void connect(DomainObject domainObject, StringList templateList, Map relAttributeMap) throws FrameworkException {
            String stateProduction = PropertyUtil.getSchemaProperty(this.context, DomainConstants.SELECT_POLICY, DomainConstants.POLICY_ROUTE_TEMPLATE, "state_Production");
            String routeBasePurposeSelect = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE);
            StringList selects = new StringList();
            selects.add(DomainConstants.SELECT_CURRENT);
            selects.add(routeBasePurposeSelect);

            Map<Object, Object> routeTemplateMap;
            String routeBasePurpose;
            String state;
            DomainObject routeTemplateObj = DomainObject.newInstance(this.context);
            DomainRelationship domainRelationship;
            for (String oid : templateList) {
                if (UIUtil.isNotNullAndNotEmpty(oid)) {
                    routeTemplateObj.setId(oid);
                    routeTemplateMap = routeTemplateObj.getInfo(this.context, selects);
                    if (null != routeTemplateMap && !routeTemplateMap.isEmpty()) {
                        state = (String) routeTemplateMap.get(DomainConstants.SELECT_CURRENT);
                        if (stateProduction.equalsIgnoreCase(state)) {
                            routeBasePurpose = (String) routeTemplateMap.get(routeBasePurposeSelect);
                            relAttributeMap.put(DomainObject.ATTRIBUTE_ROUTE_BASE_PURPOSE, routeBasePurpose);
                            domainRelationship = DomainRelationship.connect(this.context, domainObject,
                                    DomainObject.RELATIONSHIP_OBJECT_ROUTE, routeTemplateObj);
                            domainRelationship.setAttributeValues(this.context, relAttributeMap);
                        }
                    }
                }
            }
        }
    }
}
