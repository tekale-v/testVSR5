package com.pg.widgets.structuredats;

import java.util.Map;
import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

public class PGStructuredATSBOMUtil 
{
	static final Logger logger = Logger.getLogger(PGStructuredATSBOMUtil.class.getName());
	static final String STRING_OBJECT = "ObjectId";
	static final String EXCEPTION_MESSAGE = "Exception in PGStructuredATSBOMUtil";
	static final String STRING_MESSAGE = "message";
	static final String STRING_OK = "OK";
	public static final String REL_PG_ATS_OPERATION = PropertyUtil.getSchemaProperty("relationship_pgATSOperation");

	/**
	 * Method to add componenets to BOM
	 * 
	 * @param context
	 * @param PD      ID , RM ID , BOM ID
	 * @return
	 * @throws Exception
	 */
	public Response addExistingPartsToBOM(Context context, Map mpRequestMap) throws Exception {
		String bReturn = "";
		String strOutputMsg = STRING_OK;
		String relpgATSOperationId;
		HashMap attributeMap = new HashMap();
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
				ContextUtil.startTransaction(context, true);
				PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
				System.out.println("mpRequestMap :: "+mpRequestMap);
				String strATSId = (String) mpRequestMap.get("id");
				ArrayList<Map> connectionList = (ArrayList) mpRequestMap.get("connections");
				String strRMId = null;
				String relType = null;
				String strBOMId = null;
				String strAction = "BOM";
				if (null != connectionList && !connectionList.isEmpty())
					for (Map infomap : connectionList) {
						relType = (String) infomap.get("relType");
	
						if (UIUtil.isNotNullAndNotEmpty(relType) && relType.equalsIgnoreCase(REL_PG_ATS_OPERATION)) {
							strRMId = (String) infomap.get("id");
						} else if (UIUtil.isNotNullAndNotEmpty(relType)
								&& relType.equalsIgnoreCase(DomainConstants.RELATIONSHIP_EBOM)) {
							strBOMId = (String) infomap.get("relid");
						}
					}

				// Connect with ATSOperation relationship
				relpgATSOperationId = pgStructuredATSObj.connectATSOpsRelations(context, strATSId, strRMId);
				System.out.println("relpgATSOperationId :: "+relpgATSOperationId);
				// Connect with ATSContext relationship
				bReturn = pgStructuredATSObj.connectATSCtxRelations(context, relpgATSOperationId, strBOMId, strAction, attributeMap);
				System.out.println("atscontext :: "+bReturn);
	
				ContextUtil.commitTransaction(context);
				output.add(STRING_MESSAGE, strOutputMsg);
		} catch (Exception excep) {
			ContextUtil.abortTransaction(context);
			output.add(STRING_MESSAGE, excep.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, excep.getMessage());
		}

		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}
}
