package com.prostep.pdf3d.restservices;

import javax.ws.rs.ApplicationPath;

import com.dassault_systemes.platform.restServices.ModelerBase;

/**
 * @author lockstein
 *
 */
@ApplicationPath("/prostep/pdf3d")
public class PDF3DRestServices extends ModelerBase {

   @Override
public Class[] getServices() {
        return new Class[] { ObjectInfo.class };
   }
}
