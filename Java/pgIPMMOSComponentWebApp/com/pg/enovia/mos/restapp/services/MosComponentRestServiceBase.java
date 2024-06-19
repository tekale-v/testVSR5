package com.pg.enovia.mos.restapp.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.CacheControl;

import com.dassault_systemes.enovia.characteristic.restapp.util.LogContext;
import com.dassault_systemes.enovia.characteristic.restapp.util.WebServiceContext;
import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.vplm.modeler.PLMCoreModelerSession;
import com.dassault_systemes.vplm.modeler.exception.PLMxModelerException;

import matrix.db.Context;
import matrix.util.MatrixException;

abstract class MosComponentRestServiceBase extends RestService
{
  protected CacheControl cacheControl;

 /**
 * 
 */
protected MosComponentRestServiceBase()
  {
    this.cacheControl = new CacheControl();
    this.cacheControl.setNoCache(true);
    this.cacheControl.setNoStore(true);
  }

  /**
 * @param paramWebServiceContext
 * @param paramLogContext
 */
protected void closeSession(WebServiceContext paramWebServiceContext, LogContext paramLogContext) {
    try {
      PLMCoreModelerSession localPLMCoreModelerSession = paramWebServiceContext.getSession();
      if (null != localPLMCoreModelerSession) {
        localPLMCoreModelerSession.closeSession(false);
        paramLogContext.println("Session closed");
      }
      else {
        paramLogContext.println("Session NOT found, so not closed !!!");
      }
    } catch (PLMxModelerException localPLMxModelerException) {
      Logger.getLogger(MosComponentRestServiceBase.class.getName()).log(Level.WARNING, "Got exception: {0}", localPLMxModelerException.toString());
    }
    try
    {
      Context localContext = paramWebServiceContext.getContext();
      if (null != localContext) {
        localContext.shutdown();
        paramLogContext.println("Context shutdown");
      }
      else {
        paramLogContext.println("Context NOT found, so not shut !!!");
      }
    } catch (MatrixException localMatrixException) {
    	Logger.getLogger(MosComponentRestServiceBase.class.getName()).log(Level.WARNING, "Got exception: {0}", localMatrixException.toString());
    }
  }
}