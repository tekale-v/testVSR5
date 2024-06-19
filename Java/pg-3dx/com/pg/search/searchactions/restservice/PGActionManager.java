package com.pg.search.searchactions.restservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import com.dassault_systemes.enovia.e6wv2.foundation.ServiceJson;
import com.dassault_systemes.enovia.e6wv2.foundation.db.ContextUtil;
import com.dassault_systemes.enovia.e6wv2.foundation.db.PropertyUtil;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.CSRFToken;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Dataobject;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Servicedata;
import com.dassault_systemes.platform.restServices.ModelerBase;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.lifecycle.CalculateSequenceNumber;
import com.matrixone.servlet.Framework;

import matrix.db.JPO;
import matrix.util.StringList;

@ApplicationPath(value = "/pg/searchactions/common")
public class PGActionManager extends ModelerBase {
	@Override
	public Class<?>[] getServices() {
		// TODO Auto-generated method stub
		return new Class[] { PGActionManager.class };
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path(value = "/")
	@Produces(value = { "application/json" })
	public static Response deleteParts(@Context HttpServletRequest httpServletRequest, @Context UriInfo uriInfo, String strInputData) throws Exception {
		@SuppressWarnings("unused")
		String strURIPath = uriInfo.getPath();
		Servicedata outputServiceData = new Servicedata();
		Response response;
		int nStatusCode = 200;
		matrix.db.Context context = null;
		Locale locale = null;
		StringList slObjectsNotDeleted = new StringList();
		boolean bErrorForDeletingObjects = false;
		StringList slObjectsForDeletion = new StringList();
		try {
			if (Framework.isLoggedIn((HttpServletRequest) httpServletRequest)) {
				context = Framework.getContext((HttpSession) httpServletRequest.getSession(false));
			}
			locale = context.getLocale();
			Servicedata inputServiceData = ServiceJson.readServicedatafromJson(strInputData);
			List<Dataobject> inputDataList = inputServiceData.getData();
			Iterator<Dataobject> iter = inputDataList.iterator();
			Dataobject dataobject = null;
			String strObjPhysIdJoined = null;
			while (iter.hasNext()) {
				dataobject = (Dataobject) iter.next();
				strObjPhysIdJoined = dataobject.getId();
				// System.out.println("-------- " + strObjPhysId);
				String[] strArrObjectsForDeletion = strObjPhysIdJoined.split(",");
				slObjectsForDeletion = new StringList(strArrObjectsForDeletion);
				// Constructing argument for JPO invocation
				HashMap<String, Object> argMap = new HashMap<>();
				Map<String, String> tempMap = null;
				MapList objectList = new MapList();
				Map<String, Object> requestValuesMap = new HashMap<>();
				requestValuesMap.put("emxTableRowId", strArrObjectsForDeletion);
				argMap.put("RequestValuesMap", requestValuesMap);
				argMap.put("uiType", "table");
				for (int i = 0; i < strArrObjectsForDeletion.length; i++) {
					tempMap = new HashMap<String, String>();
					tempMap.put("id", strArrObjectsForDeletion[i]);
					objectList.add(tempMap);
				}
				argMap.put("ObjectList", objectList);
				String[] methodArgs = JPO.packArgs(argMap);
				Map<?, ?> resultMap = null;
				resultMap = JPO.invoke(context, "emxAEFUtil", null, "deleteSelectedObjects", methodArgs, Map.class);
				bErrorForDeletingObjects = (Boolean) resultMap.get("errorOccured");
				slObjectsNotDeleted = (StringList) resultMap.get("erroredList");
			}
			CSRFToken csrfToken = ContextUtil.getCSRFKey(context, httpServletRequest);
			outputServiceData = new Servicedata();
			outputServiceData.setSuccess(Boolean.valueOf(true));
			outputServiceData.setCsrf(csrfToken);
		} catch (Exception exception) {
			System.out.println("---3.Custom Search Action---EXCEPTION ENCOUNTERED---" + exception);
			String strErrorMsg = exception.getMessage().trim();
			String strCustomErrorMsg = null;
			if (strErrorMsg.indexOf("delete") > 0) {
				strCustomErrorMsg = EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", context.getLocale(), "emxComponents.Common.NoDeleteAccess");
				strErrorMsg = strCustomErrorMsg + "\n" + strErrorMsg;
			} else if (strErrorMsg.indexOf("fromdisconnect") > 0 || strErrorMsg.indexOf("todisconnect") > 0) {
				strCustomErrorMsg = EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", context.getLocale(), "emxComponents.Common.DisconnectObjectFailed");
				strErrorMsg = strCustomErrorMsg + "\n" + strErrorMsg;
			} else {
				strCustomErrorMsg = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", context.getLocale(),
						"emxFramework.GenericDelete.DeletionNotAllowedMessage");
				strErrorMsg = strCustomErrorMsg + "\n" + strErrorMsg;
			}
			outputServiceData = new Servicedata();
			nStatusCode = 500;
			String strError = PropertyUtil.getTranslatedValue(context, (String) "Foundation", (String) "emxFoundation.Widget.Error.SystemError", (Locale) locale);
			outputServiceData.setSuccess(Boolean.valueOf(false));
			outputServiceData.setError(strError);
			outputServiceData.setInternalError(strErrorMsg);
		} catch (Error error) {
			System.out.println("---3.Custom Search Action---ERROR ENCOUNTERED---" + error);
			String strErrorMsg = error.getMessage().trim();
			outputServiceData = new Servicedata();
			nStatusCode = 500;
			strInputData = PropertyUtil.getTranslatedValue(context, (String) "Foundation", (String) "emxFoundation.Widget.Error.SystemError", (Locale) locale);
			outputServiceData.setSuccess(Boolean.valueOf(false));
			outputServiceData.setError(strInputData);
			outputServiceData.setInternalError(strErrorMsg);
		}
		JSONObject infoJSON = new JSONObject();
		if (bErrorForDeletingObjects) {
			// Collecting error messages from trigger
			CalculateSequenceNumber calcInstance = new CalculateSequenceNumber();
			StringList taskList = calcInstance.getClientTasks(context);
			String xml = com.matrixone.apps.domain.util.FrameworkUtil.join(taskList, "");
			HashMap<?, ?> errorFromTriggersMap = calcInstance.getErrorMap(xml);
			String strErrorQueryObject = calcInstance.createAndSaveErrorXML(context, slObjectsNotDeleted, errorFromTriggersMap);
			Set<String> setObjectsNotDeleted = (Set<String>) errorFromTriggersMap.keySet();
			slObjectsForDeletion.removeAll(setObjectsNotDeleted);
			infoJSON.put("errorQueryObject", strErrorQueryObject);
		}
		context.clearClientTasks();
		infoJSON.put("deletedObjectList", slObjectsForDeletion);
		String strInfo = infoJSON.toString();
		outputServiceData.setInfo(strInfo);
		outputServiceData.setStatusCode(Integer.valueOf(nStatusCode));
		CacheControl cacheCntl = new CacheControl();
		cacheCntl.setNoCache(true);
		cacheCntl.setNoStore(true);
		cacheCntl.setMaxAge(0);
		cacheCntl.setMustRevalidate(true);
		String strOutputDataJson = ServiceJson.generateJsonStringfromJAXB((Servicedata) outputServiceData, System.currentTimeMillis());
		response = Response.status((int) nStatusCode).entity((Object) strOutputDataJson).header("Content-Type", (Object) "application/json").cacheControl((CacheControl) cacheCntl)
				.build();
		return response;
	}
}
