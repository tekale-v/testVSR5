package com.pg.dsm.preference.util;

import java.util.Map;

import matrix.db.Context;

public class DSMUPT {
    Context context;
    String objectOid;

    public DSMUPT(Context context, String objectOid) {
        this.context = context;
        this.objectOid = objectOid;
    }

    public Map<Object, Object> getAttributeMap() throws Exception {
        DSMUPTUtil util = new DSMUPTUtil();
        return util.getAttributeInfoAsMap(this.context, this.objectOid);
    }

    public String getAttributeJson() throws Exception {
        DSMUPTUtil util = new DSMUPTUtil();
        return util.getAttributeInfoAsJson(this.context, this.objectOid);
    }

}
