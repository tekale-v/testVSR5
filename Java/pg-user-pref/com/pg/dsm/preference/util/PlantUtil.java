package com.pg.dsm.preference.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class PlantUtil {
    private static final Logger logger = Logger.getLogger(PlantUtil.class.getName());

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getActivePlantsJSON(Context context) throws FrameworkException {
        JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
        JsonArrayBuilder jsonArr = Json.createArrayBuilder();
        MapList plantList = getActivePlants(context);
        plantList.sort(DomainConstants.SELECT_NAME, PreferenceConstants.Basic.ASCENDING.get(), "String");
        if (null != plantList && !plantList.isEmpty()) {
            jsonArr.add(appendBlankSelectJSON());
            JsonObjectBuilder json;
            for (Object object : plantList) {
                json = Json.createObjectBuilder();
                json.add(DomainConstants.SELECT_ID, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_ID));
                json.add(DomainConstants.SELECT_NAME, (String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
                json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), (String) ((Map<Object, Object>) object).get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                jsonArr.add(json);
            }
        } else {
            logger.log(Level.INFO, "There are no Plant object(s) in database");
        }
        jsonOutput.add(PreferenceConstants.Basic.OUTPUT.get(), jsonArr.build());
        return jsonOutput.build().toString();
    }

    /**
     * @return
     */
    private JsonObjectBuilder appendBlankSelectJSON() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add(DomainConstants.SELECT_ID, DomainConstants.EMPTY_STRING);
        json.add(DomainConstants.SELECT_NAME, DomainConstants.EMPTY_STRING);
        json.add(PreferenceConstants.Basic.PHYSICAL_ID.get(), DomainConstants.EMPTY_STRING);
        return json;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public MapList getActivePlants(Context context) throws FrameworkException {
        String stateActive = PropertyUtil.getSchemaProperty(context, DomainConstants.SELECT_POLICY, DomainConstants.TYPE_ORGANIZATION, "state_Active");
        return DomainObject.findObjects(context,                        // context pattern
                pgV3Constants.TYPE_PLANT,                               // type pattern
                DomainConstants.QUERY_WILDCARD,                         // name pattern
                DomainConstants.QUERY_WILDCARD,                         // revision pattern
                DomainConstants.QUERY_WILDCARD,                         // owner pattern
                pgV3Constants.VAULT_ESERVICEPRODUCTION,                 // vault pattern
                DomainConstants.SELECT_CURRENT + "==" + stateActive,        // object where clause
                false,                                  // expand type
                StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, PreferenceConstants.Basic.PHYSICAL_ID.get()));
    }
}
