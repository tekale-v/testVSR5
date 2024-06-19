/*
 * Added by APOLLO Team
 * Webservice to populate range values in CATIA
 */

package com.png.integ.designtool.util;

import java.util.Date;

import javax.ws.rs.core.Response;

public class DesignToolUtilities
{
  public static Response.ResponseBuilder buildResponseWithNoCache(boolean bStatus, String strResult)
  {
    Response.ResponseBuilder RespBuilder = null;
    if (bStatus) {
    	RespBuilder = Response.ok(strResult, "application/json");
    } else {
    	RespBuilder = Response.serverError().entity(strResult);
    }
    RespBuilder.header("Cache-Control", "max-age=0, no-cache, no-store, must-revalidate");
    RespBuilder.header("Pragma", "no-cache");
    RespBuilder.expires(new Date(0L));
    
    return RespBuilder;
  }

}
