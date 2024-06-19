package com.pg.dsm.preference.template.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.template.entity.Template;
import com.pg.dsm.preference.template.interfaces.IUserPreferenceTemplateCreateSteps;

import matrix.db.Context;

public class UserPreferenceTemplateCreateStepsExecutor {
    boolean executed;
    String error;
    String objectID;

    private UserPreferenceTemplateCreateStepsExecutor(Execute execute) {
        this.executed = execute.executed;
        this.error = execute.error;
        this.objectID = execute.objectID;
    }

    public boolean isExecuted() {
        return executed;
    }

    public String getError() {
        return error;
    }

    public String getObjectID() {
        return objectID;
    }

    public static class Execute {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        boolean executed;
        String error;
        String objectID;

        public Execute(Context context) {
            this.context = context;
        }

        public UserPreferenceTemplateCreateStepsExecutor now(Template template) {
            try {
                run(template);
                this.executed = Boolean.TRUE;
            } catch (Exception e) {
                this.executed = Boolean.FALSE;
                this.error = e.getMessage();
                logger.log(Level.WARNING, "Error creating user preference template object: " + e);
            }
            return new UserPreferenceTemplateCreateStepsExecutor(this);
        }

        private void run(Template template) throws Exception {
            IUserPreferenceTemplateCreateSteps templateCreate = new UserPreferenceTemplateCreateSteps(this.context, template);
            UserPreferenceTemplateCreateUtil userPreferenceTemplateCreateUtil = new UserPreferenceTemplateCreateUtil();
            String objectName = userPreferenceTemplateCreateUtil.getObjectName(this.context, template);
            logger.log(Level.INFO, "User Preference Template objectName: " + objectName);
            this.objectID = templateCreate.getCreatedID(objectName, template.getType(), template.getPolicy());
            logger.log(Level.INFO, "User Preference Template Object created: " + this.objectID);
            if (UIUtil.isNotNullAndNotEmpty(this.objectID)) {
                DomainObject domainObject = DomainObject.newInstance(this.context, this.objectID);
                if (null != domainObject) {
                    if(UIUtil.isNotNullAndNotEmpty(template.getDescription())){
                        domainObject.setDescription(context,template.getDescription());
                    }
                    templateCreate.updateSecurityPreferences(domainObject);
                    templateCreate.updateChangeActionPreferences(domainObject);
                    templateCreate.updatePlantPreferences(domainObject);
                    templateCreate.updateSharingMemberPreferences(domainObject);
                    templateCreate.updateTemplateSharingMemberPreferences(domainObject);
                    templateCreate.updatePackagingPreferences(domainObject);
                    templateCreate.updateProductPreferences(domainObject);
                    templateCreate.updateRawMaterialPreferences(domainObject);
                    templateCreate.updateTechnicalSpecificationPreferences(domainObject);
                    templateCreate.updateExplorationPreferences(domainObject);
					//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
                    templateCreate.updateManufacturingEquivalent(domainObject);
                    templateCreate.updateSupplierEquivalent(domainObject);
					//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
                }
            } else {
                throw new Exception("Failed to create template object");
            }
        }
    }

}
