package com.pg.dsm.sap.mgc.utils;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.ctrlm.custom.CtrlmJobContext;
import com.pg.dsm.sap.mgc.beans.MaterialGroup;
import com.pg.dsm.sap.mgc.beans.MaterialGroups;
import matrix.db.Context;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialGroupPageUtil {
    private static final Logger logger = Logger.getLogger(MaterialGroupPageUtil.class.getName());
    public static void main(String[] args) {
        try {
            final Context context = CtrlmJobContext.getCtrlmContext();
            if(context.isConnected()) {
                MaterialGroupUtil groupUtil = new MaterialGroupUtil();
                final String pageContent = groupUtil.getPageContentAsString(context);
                if(UIUtil.isNotNullAndNotEmpty(pageContent)) {
                    final MaterialGroups materialGroups = groupUtil.loadMaterialGroupCodes(pageContent);
                    logger.log(Level.INFO, "Applicable Type: {0}", materialGroups.getApplicableTypes());
                    final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                    logger.log(Level.INFO, "Number of records: {0}", materialGroupList.size());
                    for(MaterialGroup materialGroup: materialGroupList) {
                        logger.log(Level.INFO, materialGroup.toString());
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        } finally {
            System.exit(0);
        }
    }
}
