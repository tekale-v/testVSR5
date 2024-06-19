package com.pg.dsm.sap.mgc.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.ctrlm.custom.CtrlmJobContext;
import com.pg.dsm.sap.mgc.beans.Part;

import matrix.db.Context;

public class MaterialGroupUnitCase {
    private static final Logger logger = Logger.getLogger(MaterialGroupUnitCase.class.getName());

    public static void main(String[] args) {
        try {
            final Context context = CtrlmJobContext.getCtrlmContext();
            if (context.isContextSet()) {
                String objectId = args[0];
                if (UIUtil.isNotNullAndNotEmpty(objectId)) {
                    MaterialGroupUtil materialGroupUtil = new MaterialGroupUtil();
                    Part partBean = materialGroupUtil.getPartBean(context, objectId);
                    logger.log(Level.INFO, String.format("%s", partBean));
                    String materialGroupCode = materialGroupUtil.getMaterialGroupCode(context, partBean);
                    logger.log(Level.INFO, String.format("Material Group Code: %s", materialGroupCode));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        } finally {
            System.exit(0);
        }
    }
}
