package com.pg.dsm.preference.setting;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.config.xml.BusinessUseClass;
import com.pg.dsm.preference.config.xml.HighlyRestrictedClass;
import com.pg.dsm.preference.config.xml.IPSecurityControlPreference;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;

public class IPSecurityControlSetting {
    boolean applied;

    private IPSecurityControlSetting(Apply apply) {
        this.applied = apply.applied;
    }

    public boolean isApplied() {
        return applied;
    }

    public static class Apply {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        boolean applied;
        IPSecurityControlPreference ipSecurityControlPreference;
        Context context;

        public Apply(Context context, IPSecurityControlPreference ipSecurityControlPreference) {
            this.context = context;
            this.ipSecurityControlPreference = ipSecurityControlPreference;
            this.applied = Boolean.FALSE;
        }

        public IPSecurityControlSetting now(String[] args) throws Exception {
            try {
                String objectType = args[0];
                String objectOid = args[1];
                if (UIUtil.isNotNullAndNotEmpty(objectOid) && UIUtil.isNotNullAndNotEmpty(objectType)) {
                    DomainObject domainObject = DomainObject.newInstance(this.context, objectOid);
                    applyNow(domainObject, objectType);
                    this.applied = Boolean.TRUE;
                }
            } catch (Exception e) {
                this.applied = Boolean.FALSE;
                logger.log(Level.WARNING, "Error Applying IP Security Control Preference:" + e);
                throw e;
            }
            return new IPSecurityControlSetting(this);
        }

        private void applyNow(DomainObject domainObject, String objectType) throws Exception {
            try {
                String objectTypeSymbolic = FrameworkUtil.getAliasForAdmin(this.context, DomainConstants.SELECT_TYPE, objectType, true);
                logger.log(Level.INFO, "Incoming type is: {0}", objectTypeSymbolic);
                applyBusinessUseClass(domainObject, objectTypeSymbolic);
                applyHighlyRestrictedClass(domainObject, objectTypeSymbolic);

            } catch (Exception e) {
                logger.log(Level.WARNING, "Error Applying IP Security Control Preference:" + e);
                throw e;
            }
        }

        void applyBusinessUseClass(DomainObject domainObject, String objectTypeSymbolic) throws MatrixException {
            BusinessUseClass businessUseClass = this.ipSecurityControlPreference.getBusinessUseClass();
            String allowedTypes = businessUseClass.getAllowedTypes();
            if (isAllowedType(allowedTypes, objectTypeSymbolic)) {
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                userPreferenceUtil.applyBusinessUseClass(this.context, domainObject);
            }
        }

        void applyHighlyRestrictedClass(DomainObject domainObject, String objectTypeSymbolic) throws MatrixException {
            HighlyRestrictedClass highlyRestrictedClass = this.ipSecurityControlPreference.getHighlyRestrictedClass();
            String allowedTypes = highlyRestrictedClass.getAllowedTypes();
            if (isAllowedType(allowedTypes, objectTypeSymbolic)) {
                UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
                userPreferenceUtil.applyHighlyRestrictedUseClass(this.context, domainObject);
            }
        }

        boolean isAllowedType(String types, String incomingSymbolicType) {
            return StringUtil.split(types, pgV3Constants.SYMBOL_COMMA).contains(incomingSymbolicType);
        }

    }
}
