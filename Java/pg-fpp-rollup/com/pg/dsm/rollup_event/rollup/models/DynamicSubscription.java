package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.JPO;
import matrix.util.MatrixException;


public class DynamicSubscription {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Set<String> subscriptions;

    public DynamicSubscription() {
        subscriptions = new HashSet<String>();
    }

    public void add(Rollup rollupConfig) {
        Rule rollupRule = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_DS_RULE.getValue());
        if (null != rollupRule && Boolean.parseBoolean(rollupRule.getFlag())) {
            subscriptions.add(rollupRule.getInclusionType());
        }
    }

    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void notifyDSEvents(Resource resource, String strFPPObjectId) throws MatrixException {
        int iSubscriptionSize = subscriptions.size();
        if (iSubscriptionSize > 0) {
            String strFPPEventsForDynSub = subscriptions.toString().replaceAll(pgV3Constants.SYMBOL_COMMA, pgV3Constants.SYMBOL_FORWARD_SLASH);
            strFPPEventsForDynSub = strFPPEventsForDynSub.substring(strFPPEventsForDynSub.indexOf("[") + 1, strFPPEventsForDynSub.lastIndexOf("]"));
            String strDSMailBody = resource.getRollupPageProperties().getProperty("Str_DynamicSubScription_MailBody");
            strFPPEventsForDynSub = strFPPEventsForDynSub.concat(pgV3Constants.SYMBOL_SPACE).concat(strDSMailBody);
            logger.log(Level.INFO, "Event for Dynamic Subscription: {0}", strFPPEventsForDynSub);
            String strAction = "FPP_ROLLUP";
            String[] vArgs = new String[6];
            vArgs[0] = strFPPObjectId;
            vArgs[1] = strFPPEventsForDynSub;
            vArgs[2] = strAction;
            vArgs[3] = pgV3Constants.SYMBOL_COMMA;
            vArgs[4] = DomainConstants.EMPTY_STRING;
            vArgs[5] = DomainConstants.EMPTY_STRING;
            //Platform 2022x upgrade: Dynamic Subscription-Start
            JPO.invoke(resource.getContext(), "com.dassault_systemes.enovia.dynamic_subscriptions.triggers.enoGLSDynamicSubscription", null, "performDSForRollUp", vArgs, null);
            //Platform 2022x upgrade: Dynamic Subscription-End
        }
    }

    public void resetSubscriptions() {
        subscriptions.clear();
    }

}
