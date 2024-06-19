package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class ChangeActionPreference {
    private static final Logger logger = Logger.getLogger(ChangeActionPreference.class.getName());
    Context context;
    String routeTemplatePrimaryOrgName;
    String routeTemplatePrimaryOrgOID;
    String routeTemplateInWorkName;
    String routeTemplateInWorkOID;
    String routeTemplateInApprovalName;
    String routeTemplateInApprovalOID;
    String changeTemplateName;
    String changeTemplateOID;

    /**
     * @param context
     * @throws MatrixException
     */
    public ChangeActionPreference(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        this.context = context;
        UserPreferenceUtil util = new UserPreferenceUtil();
        this.routeTemplatePrimaryOrgName = util.getPreferredRouteTemplatePrimaryOrgName(this.context);
        this.routeTemplatePrimaryOrgOID = util.getPreferredRouteTemplatePrimaryOrgID(this.context);

        if (UIUtil.isNullOrEmpty(this.routeTemplatePrimaryOrgName)) { // populate primary org from IP Security.
            this.routeTemplatePrimaryOrgName = Preferences.IPSecurityPreference.PRIMARY_ORGANIZATION.getName(context);
            this.routeTemplatePrimaryOrgOID = Preferences.IPSecurityPreference.PRIMARY_ORGANIZATION.getID(context);
        }

        this.routeTemplateInWorkName = util.getPreferredInWorkRouteTemplateName(this.context);
        this.routeTemplateInWorkOID = util.getPreferredInWorkRouteTemplateID(this.context);
        this.routeTemplateInApprovalName = util.getPreferredInApprovalRouteTemplateName(this.context);
        this.routeTemplateInApprovalOID = util.getPreferredInApprovalRouteTemplateID(this.context);
        this.changeTemplateName = util.getPreferredChangeTemplateName(this.context);
        this.changeTemplateOID = util.getPreferredChangeTemplateID(this.context);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("ChangeActionPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    public Context getContext() {
        return context;
    }

    public String getRouteTemplatePrimaryOrgName() {
        return routeTemplatePrimaryOrgName;
    }

    public String getRouteTemplatePrimaryOrgOID() {
        return routeTemplatePrimaryOrgOID;
    }

    public String getRouteTemplateInWorkName() {
        return routeTemplateInWorkName;
    }

    public String getRouteTemplateInWorkOID() {
        return routeTemplateInWorkOID;
    }

    public String getRouteTemplateInApprovalName() {
        return routeTemplateInApprovalName;
    }

    public String getRouteTemplateInApprovalOID() {
        return routeTemplateInApprovalOID;
    }

    public String getChangeTemplateName() {
        return changeTemplateName;
    }

    public String getChangeTemplateOID() {
        return changeTemplateOID;
    }

    @Override
    public String toString() {
        return "ChangeActionPreference{" +
                "context=" + context +
                ", routeTemplatePrimaryOrgName='" + routeTemplatePrimaryOrgName + '\'' +
                ", routeTemplatePrimaryOrgOID='" + routeTemplatePrimaryOrgOID + '\'' +
                ", routeTemplateInWorkName='" + routeTemplateInWorkName + '\'' +
                ", routeTemplateInWorkOID='" + routeTemplateInWorkOID + '\'' +
                ", routeTemplateInApprovalName='" + routeTemplateInApprovalName + '\'' +
                ", routeTemplateInApprovalOID='" + routeTemplateInApprovalOID + '\'' +
                ", changeTemplateName='" + changeTemplateName + '\'' +
                ", changeTemplateOID='" + changeTemplateOID + '\'' +
                '}';
    }
}
