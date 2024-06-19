package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class IPSecurityPreference {
    private static final Logger logger = Logger.getLogger(IPSecurityPreference.class.getName());
    String businessUseClassName;
    String businessUseClassID;
    String highlyRestrictedClassName;
    String highlyRestrictedClassID;
    String primaryOrgName;
    String primaryOrgID;

    /**
     * @param context
     * @throws MatrixException
     */
    public IPSecurityPreference(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        UserPreferenceUtil util = new UserPreferenceUtil();
        this.businessUseClassName = util.getPreferredBusinessUseClass(context);
        this.businessUseClassID = util.getPreferredBusinessUseClassID(context);
        this.highlyRestrictedClassName = util.getPreferredHighlyRestrictedClass(context);
        this.highlyRestrictedClassID = util.getPreferredHighlyRestrictedClassID(context);
        this.primaryOrgName = util.getPreferredPrimaryOrgName(context);
        this.primaryOrgID = util.getPreferredPrimaryOrgID(context);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("IPSecurityPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    public String getBusinessUseClassName() {
        return businessUseClassName;
    }

    public String getBusinessUseClassID() {
        return businessUseClassID;
    }

    public String getHighlyRestrictedClassName() {
        return highlyRestrictedClassName;
    }

    public String getHighlyRestrictedClassID() {
        return highlyRestrictedClassID;
    }

    public String getPrimaryOrgName() {
        return primaryOrgName;
    }

    public String getPrimaryOrgID() {
        return primaryOrgID;
    }

    @Override
    public String toString() {
        return "IPSecurityPreference{" +
                "businessUseClassName='" + businessUseClassName + '\'' +
                ", businessUseClassID='" + businessUseClassID + '\'' +
                ", highlyRestrictedClassName='" + highlyRestrictedClassName + '\'' +
                ", highlyRestrictedClassID='" + highlyRestrictedClassID + '\'' +
                ", primaryOrgName='" + primaryOrgName + '\'' +
                ", primaryOrgID='" + primaryOrgID + '\'' +
                '}';
    }
}
