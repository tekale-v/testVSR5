package com.pg.widgets.structuredats;

import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;

import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;
import matrix.util.StringList;
import matrix.util.MatrixException;

public class PGStructuredATSAlternateUtil
{
    private static final Logger logger = Logger.getLogger(PGStructuredATSAlternateUtil.class.getName());	
	/**
	 * Apply Alternate to Structured ATS
	 * @param context
	 * @param mpRequestMap
	 * @return 
	 * @throws Exception
	 */		
	public String connectAlternateToSATS(Context context, Map mpRequestMap) throws Exception
	{
		String strSATSId = null;
		String strTargetRMPId = null;
		String strRelpgATSOperationId = null;
		String strAlternateConnectionId = null;
		String strSourceRMPId = null;
		String strConnectATSCtxRelId = null;
		String strAction = null;
		HashMap attributeMap= new HashMap();
		try 
		{
			strAction = (String)mpRequestMap.get("Action"); 
			PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
			strSourceRMPId =(String)mpRequestMap.get("sourceRMPobjectId");	// getting RMP Source ID
			strSATSId =(String)mpRequestMap.get("structuredATSobjectId");	// getting Structured ATS ID
			strTargetRMPId =(String)mpRequestMap.get("targetRMPId");
			strRelpgATSOperationId = pgStructuredATSObj.connectATSOpsRelations( context, strSATSId, strTargetRMPId);
			
			strAlternateConnectionId = getAlternateRelId(context,strSourceRMPId);
			if(UIUtil.isNotNullAndNotEmpty(strAction) && "connect".equalsIgnoreCase(strAction))
			{
			//connect ATSOperation to target rel with ATSContext relationship
			attributeMap.put(PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION,PGStructuredATSConstants.STRING_ALTERNATE);
			strConnectATSCtxRelId=pgStructuredATSObj.connectATSCtxRelations(context,strRelpgATSOperationId,strAlternateConnectionId,strAction,attributeMap);
			strConnectATSCtxRelId=PGStructuredATSConstants.VALUE_SUCCESS;
			}
			else if ((UIUtil.isNotNullAndNotEmpty(strAction) && "disconnect".equalsIgnoreCase(strAction)))
			{
				DomainRelationship.disconnect(context, strRelpgATSOperationId);
				strConnectATSCtxRelId=PGStructuredATSConstants.VALUE_SUCCESS;
			}
		} catch(Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_ALTERNATE_UTIL, excep);
			strConnectATSCtxRelId=PGStructuredATSConstants.VALUE_FAILED;
		}
		return strConnectATSCtxRelId;	
	}
	public String getAlternateRelId(Context context, String strSourceRMPId) throws Exception
	{
		MapList mlAlternateRelDetails = null;
		Map mpAlternateRelDetails = null;
		String strAlternateConnectionId = null;
		
		StringList slBusSelect = new StringList(DomainConstants.SELECT_ID);
		StringList slRelSelect = new StringList(DomainRelationship.SELECT_ID);
		try
		{
			DomainObject dObj = DomainObject.newInstance(context,strSourceRMPId);
			mlAlternateRelDetails =  dObj.getRelatedObjects(context,
									PGStructuredATSConstants.RELATIONSHIP_ALTERNATE, // Rel Pattern
									"*", // Type Pattern
									slBusSelect, // bus selectables
									slRelSelect, // rel Selects
									true, // get to
									false, // get from
									(short) 1, // Recursion level
									null, // Object Where Clause
									null); // Rel Where Clause
		if (null!=mlAlternateRelDetails && mlAlternateRelDetails.size()>0)
		{
			Iterator itrAlternateRelDetails = mlAlternateRelDetails.iterator();
			while(itrAlternateRelDetails.hasNext())
			{
				mpAlternateRelDetails = (Map)itrAlternateRelDetails.next();
				strAlternateConnectionId = (String) mpAlternateRelDetails.get(DomainRelationship.SELECT_ID);
			}
		}
		}catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_ALTERNATE_UTIL, excep);
		}
		return strAlternateConnectionId;
	}
}
    
	

	
	