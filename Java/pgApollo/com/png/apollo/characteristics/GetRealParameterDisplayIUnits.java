/*
 * Added by APOLLO Team
 * For Characteristic Related Webservice
 */

package com.png.apollo.characteristics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.enovia.characteristic.restapp.util.ENOCharacteristicsRestAppUtil;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;

import matrix.util.MatrixException;
import matrix.util.StringList;

@Path("GetRealParameterDisplayIUnits")
public class GetRealParameterDisplayIUnits extends RestService{
	
protected CacheControl cacheControl;
	
	public GetRealParameterDisplayIUnits() {
	    this.cacheControl = new CacheControl();
	    this.cacheControl.setNoCache(true);
	    this.cacheControl.setNoStore(true);
	}
	
	@GET
	public Object getDisplayIUnits(@Context HttpServletRequest req) {
	
		Response localResponse = null;
		HashMap localHashMap = null;
		MapList returnMapList = new MapList();
		MapList displayUnitList = new MapList();
		matrix.db.Context context = null;
		try {
			context=authenticate(req);
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return Response.status(401).entity("login error").build();
		}
		
		try {
				StringBuilder localStringBuilder = new StringBuilder();
				MapList mapList = DomainObject.findObjects(context, "pgPLIUnitofMeasureMasterList", "*", "current==Active", StringList.create("name"));
				Iterator itr = mapList.iterator();
				int i = 0;
				Map map1 = null;
				String strName = DomainConstants.EMPTY_STRING;
				StringList strNameList = null;
				while(itr.hasNext()){
					map1 = (Map) itr.next();
					strName = (String) map1.get("name");
					strNameList = StringList.create(new String[] { strName, strName });
					localHashMap = new HashMap();
					localHashMap.put(String.valueOf(i++), strNameList);
					displayUnitList.add(localHashMap);
				}
				Map map = new HashMap();
				
				map.put("id", "RealParameter");
				map.put("standard", "Real");
				map.put("displayUnits", displayUnitList);
				returnMapList.add(map);
				localStringBuilder.append(ENOCharacteristicsRestAppUtil.transformToJSON(returnMapList));
				localResponse = Response.status(200).entity(localStringBuilder.toString()).cacheControl(this.cacheControl).build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return localResponse;	  
		}
}