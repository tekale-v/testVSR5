package com.pg.dsm.rollup.util.rollup.widget.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.ws.enumeration.DSMConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class InitiateRollupProcess {

    private transient final Logger logger = Logger.getLogger(this.getClass().getName());
    private Context context;
    private String objectId;

    /**
     * @param context
     * @param objectId
     */
    public InitiateRollupProcess(Context context, String objectId) {
        this.context = context;
        this.objectId = objectId;
    }

    public Map performRollupProcess(String[] args) {
        String strNotifyUser = DomainConstants.EMPTY_STRING;
        Map<String, String> mReturnMap = new HashMap();
        try {
            HashMap<?, ?> paramMap = JPO.unpackArgs(args);
            List<?> tableConfigList = (List<?>) paramMap.get(DSMConstants.TABLE_CONFIG.getValue());
            DomainObject domFPPObj = DomainObject.newInstance(this.context, this.objectId);

            StringList slObjectSelects = new StringList();
            slObjectSelects.add(DomainConstants.SELECT_NAME);
            slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
            Map<?, ?> mpFPP = domFPPObj.getInfo(this.context, slObjectSelects);
            String strFPPName = (String) mpFPP.get(DomainConstants.SELECT_NAME);
            String strFPPSpecSubtype = (String) mpFPP.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);


            String sRollupEvent;
            String sRollupType;
            String strFeatureIdentifier;

            if (tableConfigList != null && !tableConfigList.isEmpty()) {
                Map confMap = (Map) tableConfigList.get(0);

                sRollupEvent = (String) confMap.get(RollupConstants.Basic.EVENT_NAME.getValue());
                sRollupType = (String) confMap.get(RollupConstants.Basic.ROLLUP_TYPE.getValue());
                strFeatureIdentifier = DomainConstants.EMPTY_STRING;

                String strRollupEvent = EnoviaResourceBundle.getProperty(this.context, "emxCPNStringResource",
                        this.context.getLocale(), "emxCPN.FPPRollUp.Event." + sRollupType);
                strNotifyUser = EnoviaResourceBundle.getProperty(this.context, "emxCPNStringResource",
                        this.context.getLocale(), "emxCPN.ManualRollup.AlertMessage");

                strNotifyUser = strNotifyUser.replace("<ROLLUPTYPE>", strRollupEvent);


                if (UIUtil.isNotNullAndNotEmpty(sRollupType) && UIUtil.isNotNullAndNotEmpty(strFPPName)) {

                    strFPPSpecSubtype = UIUtil.isNotNullAndNotEmpty(strFPPSpecSubtype) ? strFPPSpecSubtype : DomainConstants.EMPTY_STRING;

                    String[] strArgs = new String[5];
                    strArgs[0] = this.objectId;
                    strArgs[1] = strFPPName;
                    strArgs[2] = sRollupEvent;
                    strArgs[3] = strFeatureIdentifier;
                    strArgs[4] = strFPPSpecSubtype;

                    BackgroundProcess backgroundProcess = new BackgroundProcess();
                    backgroundProcess.submitJob(this.context, RollupConstants.Basic.MANUAL_PROGRAM.getValue(), RollupConstants.Basic.MANUAL_METHOD.getValue(), strArgs, (String) null);
                }
            }

            mReturnMap.put(pgV3Constants.RETURN, strNotifyUser);

        } catch (Exception e) {
            mReturnMap.put(pgV3Constants.RETURN, "Exception Occured in Manual Process");
            logger.log(Level.WARNING, null, e);
        }

        return mReturnMap;
    }
}