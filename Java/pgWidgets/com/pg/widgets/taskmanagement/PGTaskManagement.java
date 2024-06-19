package com.pg.widgets.taskmanagement;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.program.Task;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;

public class PGTaskManagement{
	
	private static final Logger logger = Logger.getLogger(PGTaskManagement.class.getName());
	
	public static String getRelatedObjectData(Context context, String strInput) throws FrameworkException {
		JsonArrayBuilder jsonArrOutputInputs = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrOutputDeliverables = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrOutputAttachments = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrOutputContext = Json.createArrayBuilder();
		boolean isContextPushed = false;
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			MapList mlObjectList = PGWidgetUtil.getRelatedObjectsMapList(context, jsonInput);
			for (int i = 0; i < mlObjectList.size(); i++) {
				Map mapTemp = (Map) mlObjectList.get(i);

				String strRelName = (String) mapTemp.get(PGWidgetConstants.SELECT_CONNECTION_NAME);
				String strPhysicalid = FrameworkUtil.getPIDfromOID(context,
						(String) mapTemp.get(DomainConstants.SELECT_ID));

				boolean hasCheckoutAccess = (Boolean
						.valueOf((String) mapTemp.get(CommonDocument.SELECT_HAS_CHECKOUT_ACCESS))).booleanValue();
				boolean hasCheckinAccess = (Boolean
						.valueOf((String) mapTemp.get(CommonDocument.SELECT_HAS_CHECKIN_ACCESS))).booleanValue();
				boolean canCheckin = (hasCheckinAccess && hasCheckoutAccess)
						|| (context.getUser().equalsIgnoreCase((String) mapTemp.get(DomainConstants.SELECT_OWNER))
								&& hasCheckinAccess);
				JsonObjectBuilder jsonDataElem = Json.createObjectBuilder();
				if (DomainConstants.RELATIONSHIP_TASK_DELIVERABLE.equals(strRelName)
						|| DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT.equals(strRelName)
						|| Task.RELATIONSHIP_CONTRIBUTES_TO.equals(strRelName)) {
					jsonDataElem.add(PGWidgetConstants.KEY_DATA_ELEMENTS, Json.createObjectBuilder()
							.add(DomainConstants.SELECT_ID, strPhysicalid)
							.add(PGWidgetConstants.KEY_OBJECT_ID, (String) mapTemp.get(DomainConstants.SELECT_ID))
							.add(PGWidgetConstants.KEY_CAN_CHECKIN, canCheckin).add(PGWidgetConstants.KEY_CAN_DELETE,
									Boolean.valueOf((String) mapTemp.get(PGWidgetConstants.SELECT_HAS_DELETE_ACCESS))));

					if (DomainConstants.RELATIONSHIP_TASK_DELIVERABLE.equals(strRelName))
						jsonArrOutputDeliverables.add(jsonDataElem.build());
					else if (DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT.equals(strRelName))
						jsonArrOutputAttachments.add(jsonDataElem.build());
					else if (Task.RELATIONSHIP_CONTRIBUTES_TO.equals(strRelName))
						jsonArrOutputContext.add(jsonDataElem.build());
				} else {

					jsonDataElem.add(DomainConstants.SELECT_ID, strPhysicalid)
							.add(PGWidgetConstants.KEY_OBJECT_ID, (String) mapTemp.get(DomainConstants.SELECT_ID))
							.add(DomainConstants.SELECT_TYPE, (String) mapTemp.get(DomainConstants.SELECT_TYPE))
							.add(PGWidgetConstants.KEY_REL_ID,
									(String) mapTemp.get(PGWidgetConstants.SELECT_CONNECTION_ID));

					String strHasFiles = PGWidgetConstants.STRING_CAPITAL_FALSE;
					// context pushed for functional requirement, to be able to display connected
					// files. It is duly popped
					ContextUtil.pushContext(context);
					isContextPushed = true;
					CommonDocument comDoc = new CommonDocument((String) mapTemp.get(DomainConstants.SELECT_ID));
					MapList mlFiles = comDoc.getAllFiles(context);
					if (mlFiles != null && !mlFiles.isEmpty()) {
						strHasFiles = PGWidgetConstants.STRING_CAPITAL_TRUE;
					}
					String strTitle = (String) mapTemp.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
					jsonDataElem.add(PGWidgetConstants.KEY_DATA_ELEMENTS, Json.createObjectBuilder()
							.add(PGWidgetConstants.SELECT_PHYSICAL_ID, strPhysicalid)
							.add(PGWidgetConstants.KEY_OBJECT_ID, (String) mapTemp.get(DomainConstants.SELECT_ID))
							.add(DomainConstants.SELECT_TYPE, (String) mapTemp.get(DomainConstants.SELECT_TYPE))
							.add(DomainConstants.SELECT_NAME, (String) mapTemp.get(DomainConstants.SELECT_NAME))
							.add(DomainConstants.SELECT_REVISION, (String) mapTemp.get(DomainConstants.SELECT_REVISION))
							.add("title",
									PGWidgetConstants.DENIED.equals(strTitle) ? PGWidgetConstants.KEY_NO_ACCESS
											: strTitle)
							.add(PGWidgetConstants.KEY_HAS_FILES, strHasFiles)
							.add(PGWidgetConstants.KEY_CAN_CHECKIN, canCheckin).add(PGWidgetConstants.KEY_CAN_DELETE,
									Boolean.valueOf((String) mapTemp.get(PGWidgetConstants.SELECT_HAS_DELETE_ACCESS))));

					jsonArrOutputInputs.add(jsonDataElem.build());
				}

			}
			logger.log(Level.INFO, "jsonArrOutputInputs :: " + jsonArrOutputInputs.build());
			logger.log(Level.INFO, "jsonArrOutputDeliverables :: " + jsonArrOutputDeliverables.build());
			logger.log(Level.INFO, "jsonArrOutputAttachments :: " + jsonArrOutputAttachments.build());
			logger.log(Level.INFO, "jsonArrOutputContext :: " + jsonArrOutputContext.build());
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_INPUTS, jsonArrOutputInputs)
					.add(PGWidgetConstants.KEY_DELIVERABLES, jsonArrOutputDeliverables)
					.add(PGWidgetConstants.KEY_ATTACHMENTS, jsonArrOutputAttachments)
					.add(PGWidgetConstants.KEY_CONTEXT, jsonArrOutputContext).build().toString();
		} catch (Exception e) {
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
		finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
}
