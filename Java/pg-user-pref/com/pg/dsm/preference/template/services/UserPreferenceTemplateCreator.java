package com.pg.dsm.preference.template.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessInterface;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.util.MatrixException;

public class UserPreferenceTemplateCreator {
    boolean created;
    String error;
    String objectId;

    private UserPreferenceTemplateCreator(Create create) {

        this.created = create.created;
        this.error = create.error;
        this.objectId = create.objectId;
    }

    public boolean isCreated() {
        return created;
    }

    public String getError() {
        return error;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public static class Create {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        boolean created;
        String error;
        String objectId;

        public Create(Context context) {
            this.context = context;
        }

        public UserPreferenceTemplateCreator now(String objectName, String type, String policy) {
            try {
                execute(objectName, type, policy);
                this.created = Boolean.TRUE;
            } catch (Exception e) {
                this.created = Boolean.FALSE;
                this.error = e.getMessage();
                logger.log(Level.WARNING, "Error creating user preference template object: " + e);
            }
            return new UserPreferenceTemplateCreator(this);
        }


        private void execute(String objectName, String type, String policy) throws MatrixException {
            //get Auto Name with Prefix from number Generator Object
            if (UIUtil.isNotNullAndNotEmpty(objectName)) {
                //create Template Object
                DomainObject templateObj = DomainObject.newInstance(context);
                templateObj.createObject(context, type, objectName, "1", policy, pgV3Constants.VAULT_ESERVICEPRODUCTION);
                this.objectId = templateObj.getInfo(context, DomainConstants.SELECT_ID);
                if (UIUtil.isNotNullAndNotEmpty(this.objectId)) {
                    Vault vault = context.getVault();
                    BusinessInterface businessInterfaceUPTDSM = new BusinessInterface(UserPreferenceTemplateConstants.Interface.INTERFACE_UPT_DSM.getName(context), vault);
                    templateObj.addBusinessInterface(context, businessInterfaceUPTDSM);
                    logger.log(Level.INFO, "User Preference Template Interface added on newly created objectId");
                }
            } else {
                this.created = Boolean.FALSE;
                this.error = "Unable to get auto-name";
            }
        }
    }
}
