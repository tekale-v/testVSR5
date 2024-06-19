//package com.dassault_systemes.biovia.formulation;
package com.pg.fis.integration.util;
//import javax.ws.rs.core.Response;

public class HttpCallResponse {

    /**
     * HTTP Status Code for the response
     */
    private int statusCode;

    /**
     * Text body of the response
     */
    private String reponse;


    /**
     * Total time for the response
     */
    private long responseTime;



    public int getStatus() {
        return statusCode;
    }

    public void setStatus( int status) {
        this.statusCode = status;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
