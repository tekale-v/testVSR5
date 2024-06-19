package com.pg.fis.integration.common.services;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PGFISGenericServicesDetails {

	private static final Logger logger = Logger.getLogger(PGFISGenericServicesDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISGenericServicesDetails";
	private static final String PAYLOAD_ERROR = "Payload Error - Cloud object Id is missing.";
	private static final String REFERENCE_UPDATED_SUCCESSUFULLY = "References Updated Successfully.";
	public static final String KEY_PHYSICAL_ID  = "physicalid";

	/**
	 * REST method to update Reference attribute on given physical Id
	 * @param context
	 * @param pyisicalId - Business Object physicalId
	 * @body updateReferenceMap - payload Map with values to be updated
	 * @return response in JSON format
	 * @throws MatrixException
	 * @throws Exception       when operation fails
	 */
	public static Response updateReference(Context context, String physicalId, Map<String, Object> updateReferenceMap)
			throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		boolean isContextPushed = false;
		try {
			if (UIUtil.isNullOrEmpty(physicalId) || updateReferenceMap == null || updateReferenceMap.isEmpty()) {
				output.add(PGFISWSConstants.STRING_MESSAGE, "Payload Error");
				return Response.status(Response.Status.BAD_REQUEST).entity(output.build().toString()).build();
			}
			String cloudObjURI = (String) updateReferenceMap.get("uri");
			String cloudObjSource = (String) updateReferenceMap.get("source");
			String cloudObjIdentifire = (String) updateReferenceMap.get("identifier");
			String cloudGrpObjURI = (String) updateReferenceMap.get("groupuri");
			String contractorGroupURI = (String) updateReferenceMap.get("contractorgroupuri");
			if (UIUtil.isNullOrEmpty(cloudObjURI)) {
				output.add(PGFISWSConstants.STRING_MESSAGE, PAYLOAD_ERROR);
				return Response.status(Response.Status.BAD_REQUEST).entity(output.build().toString()).build();
			}
			Map<String, String> attrMap = new HashMap<>();
			attrMap.put(PGFISWSConstants.ATTRIBUTE_REFERENCE_URI, cloudObjURI);
			if (UIUtil.isNotNullAndNotEmpty(cloudObjSource))
				attrMap.put(PGFISWSConstants.ATTRIBUTE_REFERENCE_SOURCE, cloudObjSource);
			if (UIUtil.isNotNullAndNotEmpty(cloudObjIdentifire))
				attrMap.put(PGFISWSConstants.ATTRIBUTE_REFERENCE_IDENTIFIER, cloudObjIdentifire);
			if (UIUtil.isNotNullAndNotEmpty(cloudGrpObjURI))
				attrMap.put(PGFISWSConstants.ATTRIBUTE_PG_GROUP_REFERENCE_URI, cloudGrpObjURI);
			if (UIUtil.isNotNullAndNotEmpty(contractorGroupURI))
				attrMap.put(PGFISWSConstants.ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI, contractorGroupURI);

			ContextUtil.startTransaction(context, true);
			DomainObject domainObject = DomainObject.newInstance(context, physicalId);
			domainObject.open(context);
			//Pushing content to allow Modification to context object, as context user not having modify access to attributes.
			ContextUtil.pushContext(context);
			isContextPushed = true;
			domainObject.setAttributeValues(context, attrMap);
			if (isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
			domainObject.close(context);
			ContextUtil.commitTransaction(context);
			output.add(PGFISWSConstants.STRING_MESSAGE, REFERENCE_UPDATED_SUCCESSUFULLY);
			output.add(KEY_PHYSICAL_ID, physicalId);
		} catch (Exception e) {
			if (context.isTransactionActive()) {
				ContextUtil.abortTransaction(context);
			}
			logger.log(Level.SEVERE, ERROR_Logger, e);
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, e.getMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		} finally {
			if (context != null && isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}

}
