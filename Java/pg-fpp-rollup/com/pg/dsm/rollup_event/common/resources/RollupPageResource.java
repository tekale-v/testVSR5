package com.pg.dsm.rollup_event.common.resources;

import java.io.StringReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;

import matrix.db.Context;
import matrix.db.Page;

public class RollupPageResource {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;
    String rollupPageConfigName;

    public RollupPageResource(Context context, String rollupPageConfigName) {
        this.rollupPageConfigName = rollupPageConfigName;
        this.context = context;
    }

    public Properties getConfigProperties() {
        Page page = null;
        Properties configurations = new Properties();
        try {
            page = new Page(rollupPageConfigName);
            page.open(context);
            String content = page.getContents(context);
            configurations.load(new StringReader(content));
            page.close(context);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return configurations;
    }
}
