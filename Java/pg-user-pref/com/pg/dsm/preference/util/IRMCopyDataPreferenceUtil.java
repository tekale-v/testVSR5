package com.pg.dsm.preference.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.IRMUPTConstants;
import com.pg.dsm.preference.models.IRMTemplate;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;


//Added by IRM(Sogeti) 2022x.04 Dec CW Requirement 47851 
public class IRMCopyDataPreferenceUtil {
    private static final Logger logger = Logger.getLogger(IRMCopyDataPreferenceUtil.class.getName());

    public IRMCopyDataPreferenceUtil() {
    }

    public void addPreferenceOnDocument(Context context, String[] args) throws Exception {
		boolean isCtxPushed = Boolean.FALSE;
		try {
        String strDocId = args[0];
        if (UIUtil.isNotNullAndNotEmpty(strDocId)) {
				UserPreferenceUtilIRM userPreferenceUtilIRMObj = new UserPreferenceUtilIRM();
				DomainObject domDocId = DomainObject.newInstance(context, strDocId);
				String type = domDocId.getInfo(context, DomainConstants.SELECT_TYPE);
        	String strUPIId = PropertyUtil.getGlobalRPEValue(context,"UPT_PID");
            IRMTemplate irmTemplateObj = userPreferenceUtilIRMObj.setIRMUserPreferenceAttributes(context, strDocId,strUPIId);
            Map attrMap = userPreferenceUtilIRMObj.getIRMAttributePreferencesMap(context, irmTemplateObj);
				boolean allowedPolicy = userPreferenceUtilIRMObj.allowdPolicyForType(context, type, irmTemplateObj.getUptPolicy());
				if (allowedPolicy) {
            domDocId.setAttributeValues(context, attrMap);
					ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					isCtxPushed = Boolean.TRUE;
            domDocId.setPolicy(context, irmTemplateObj.getUptPolicy());
					
					ContextUtil.popContext(context);
					isCtxPushed = Boolean.FALSE;
            domDocId.setDescription(context, irmTemplateObj.getTemplateDesc());
            userPreferenceUtilIRMObj.connectSecurityClassification(context, domDocId, irmTemplateObj);
					if (!pgV3Constants.TYPE_PGPKGSTUDYPROTOCOL.equals(domDocId.getTypeName(context))) {
                userPreferenceUtilIRMObj.applyRegion(context, String.join("|", irmTemplateObj.getUptRegion()), domDocId);
					}
            userPreferenceUtilIRMObj.createRouteBasedOnIRMApprovalPreference(context, args);
        }
			}
		}catch (Exception e) {
			logger.log(Level.INFO, "Error: " + e);
		} finally {
			if (isCtxPushed) {
				ContextUtil.popContext(context);
			}
		}
    }
}
