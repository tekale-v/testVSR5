package com.pg.dsm.gendoc.util;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.gendoc.enumeration.CloudConstants;

import java.util.Map;

public class GenDocProcessResponse {

    int returnInteger;
    String returnMessage;
    boolean isTimedOut;

    /**
     * @param responseMap
     */
    public GenDocProcessResponse(Map<Object, Object> responseMap) {
        this.returnInteger = responseMap.containsKey(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue()) ? (Integer) responseMap.get(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue()) : 0;
        this.returnMessage = responseMap.containsKey(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue()) ? (String) responseMap.get(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue()) : DomainConstants.EMPTY_STRING;
        this.isTimedOut = responseMap.containsKey(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue()) ? (Boolean) responseMap.get(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue()) : Boolean.FALSE;
    }

    public int getReturnInteger() {
        return returnInteger;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public boolean isTimedOut() {
        return isTimedOut;
    }
}
