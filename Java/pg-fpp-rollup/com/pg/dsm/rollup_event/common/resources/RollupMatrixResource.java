package com.pg.dsm.rollup_event.common.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;

import matrix.db.Context;

public class RollupMatrixResource {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    RollupPropertyResource rollupPropertyResource;

    public RollupMatrixResource(RollupPropertyResource rollupPropertyResource) {
        this.rollupPropertyResource = rollupPropertyResource;
    }

    public Context getContext() {
        Context context = null;
        try {
            //Modified by DSM (Sogeti)-2018x.6 Dec CW for CTRLM Password Management Defect ID #44674 - Starts
            context = com.pg.ctrlm.custom.CtrlmJobContext.getCtrlmContext();
            logger.log(Level.INFO, "Connected context user {0}", context.getUser());
            //Modified by DSM (Sogeti)-2018x.6 Dec CW for CTRLM Password Management Defect ID #44674 - Ends
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return context;
    }
}

