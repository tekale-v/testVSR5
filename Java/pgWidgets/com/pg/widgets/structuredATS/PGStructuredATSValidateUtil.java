package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import static com.matrixone.apps.domain.DomainConstants.SELECT_CURRENT;
import static com.matrixone.apps.domain.DomainConstants.SELECT_ID;
import static com.matrixone.apps.domain.DomainConstants.SELECT_RELATIONSHIP_ID;

import java.util.ArrayList;
import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.png.apollo.pgApolloConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.MqlUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;


public class PGStructuredATSValidateUtil 
{
static final Logger logger = Logger.getLogger(PGStructuredATSValidateUtil.class.getName());
/**
 * This method is used to validate wrong characteristics values
 * 
 * @param context
 * @param strSATSId
 * @return String
 * @throws Exception
 */
public String validatePerformanceChar(Context context, String strSATSId) throws Exception {
	MapList mlReturnList = new MapList();
	JsonObjectBuilder output = Json.createObjectBuilder();
	Map mPickListDataForCharacteristic= null;
	Map mPickListDataForCharacteristicSpecific= null;
	Map mPickListDataForUoM = null;
	try {
		
		StringList objectSelects = new StringList();
		objectSelects.add(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_CURRENT);
		objectSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC);
		objectSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTICSPECIFIC);
		objectSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE);	
		String relPattern = PropertyUtil.getSchemaProperty(context, "relationship_pgATSOperation");
		String type = PropertyUtil.getSchemaProperty(context, "type_pgPerformanceCharacteristic");
		String typePattern = type;
		String relWhere = null;

		StringList relSelects = new StringList();
		relSelects.add(SELECT_RELATIONSHIP_ID);
		relSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
		 String strPickListDataForCharacteristic = PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTIC;
		 String strPickListDataForCharacteristicSpecific = PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTICSPECIFIC;
		 String strPickListDataForUoM = PGStructuredATSConstants.STR_PG_PLI_UNITOFMEASUREMASTERLIST;
		 Map mapProgramMap = new HashMap();
		 mapProgramMap.put(strPickListDataForUoM, PGStructuredATSConstants.STR_PG_PLI_UNITOFMEASUREMASTERLIST);
		 mapProgramMap.put(strPickListDataForCharacteristicSpecific,PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTICSPECIFIC);
		 mapProgramMap.put(strPickListDataForCharacteristic, PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTIC);
		
		Map mPickListData = JPO.invoke(context, "pgDSOUtil", null, "fetchPerfCharSATS", JPO.packArgs(mapProgramMap), Map.class);
		
		
			mPickListDataForCharacteristic= (Map) mPickListData.get(PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTIC);
			mPickListDataForCharacteristicSpecific=(Map) mPickListData.get(PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTICSPECIFIC);
			mPickListDataForUoM = (Map) mPickListData.get(PGStructuredATSConstants.STR_PG_PLI_UNITOFMEASUREMASTERLIST);
			
		if (UIUtil.isNotNullAndNotEmpty(strSATSId)) {
			DomainObject doObj = DomainObject.newInstance(context, strSATSId);
			StringBuffer typeWhere = new StringBuffer();
			typeWhere.append("type!=").append(PGStructuredATSConstants.TYPE_PG_STABILITY_RESULTS);
			MapList mlOperatedList = doObj.getRelatedObjects(context, 
					relPattern,  // Relationship 
					typePattern, // Type
					objectSelects, // Bus Selectable
					relSelects, // Rel Selectable
					false, // Get To
					true, // Get From
					(short) 1, // Recursion Level
					typeWhere.toString(), // Where Clause for Type
					relWhere, // Where Clause for Relationship
					0);
			

			for (int i = 0; i < mlOperatedList.size(); i++) {
				Map mOperatedData = (Map) mlOperatedList.get(i);
				String sChara = (String) mOperatedData.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC);
				
				String sCharaSpec = (String) mOperatedData
						.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTICSPECIFIC);
				String sUoM = (String) mOperatedData.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE);
				Map hmMap = new HashMap();
				hmMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC,sChara);
				hmMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTICSPECIFIC,sCharaSpec);
				hmMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PG_UNIT_OF_MEASURE,sUoM);
				hmMap.put(PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTIC,mPickListDataForCharacteristic);
				hmMap.put(PGStructuredATSConstants.STR_PG_PLI_CHARACTERISTICSPECIFIC,mPickListDataForCharacteristicSpecific);
				hmMap.put(PGStructuredATSConstants.STR_PG_PLI_UNITOFMEASUREMASTERLIST,mPickListDataForUoM);
				hmMap.put("mOperatedDataList",mOperatedData);
				boolean bValid = JPO.invoke(context, "emxCPNCharacteristicList", null, "validatePerfcharSATS",JPO.packArgs(hmMap), Boolean.class);
				if (!bValid) {
					mlReturnList.add(mOperatedData);
				}
			}
		}
	}
		catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return output.build().toString();
		}
	return mlReturnList.toString();

}

	/**This Method BOM operation Validations
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception  
	 */
	public String validateBOMOperations(Context context, String strSATSId) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strValidationMessage="";
		try {
			strValidationMessage = isSATSInApprovalOrRelease(context,strSATSId);
		}catch(Exception excep)
		{
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return strValidationMessage;
	}

	private String isSATSInApprovalOrRelease(Context context, String strSATSId) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strReturnMsg = "";
		try {
			DomainObject domSATSId = DomainObject.newInstance(context, strSATSId);
			String strSATSState = domSATSId.getInfo(context, DomainConstants.SELECT_CURRENT);
			
			if ((strSATSState.equals(PGStructuredATSConstants.STATE_PG_STRUCTUREDATS_IN_APPROVAL)
					|| strSATSState.equals(PGStructuredATSConstants.STATE_PG_STRUCTUREDATS_RELEASE)))
			{
				strReturnMsg= PGStructuredATSConstants.STRING_ErrMessage;
			}
			
		} catch (Exception excep) {
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return strReturnMsg;
	}

}