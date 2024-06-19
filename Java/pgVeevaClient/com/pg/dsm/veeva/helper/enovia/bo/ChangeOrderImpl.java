/*
 **   ChangeOrderImpl.java
 **   Description - Introduced as part of Veeva integration.      
 **   ChangeOrderImpl Implementation class.
 **
 */
package com.pg.dsm.veeva.helper.enovia.bo;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.dassault_systemes.enovia.dcl.DCLConstants;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeOrder;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.veeva.util.Veeva;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

public class ChangeOrderImpl {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context: matrix context
	 * @param properties: holds veeva properties
	 * @return returns change order domainobject
	 * @throws Exception
	 * Creates CO and update attributes on CO
	 */
	public DomainObject createChangeOrder(Context context, Properties properties) throws Exception {
		logger.info("Entered createChangeOrder Method");
		String objectId = DomainConstants.EMPTY_STRING;
		DomainObject changeOrderObj = DomainObject.newInstance(context);
		boolean isContextPushed = false;
		try {
			ContextUtil.startTransaction(context, true);
			isContextPushed = true;

			String strMQLcommand = "print bus $1 $2 $3 select $4 dump $5"; 
			logger.info("Get Change Template Query:"+strMQLcommand);
			String sChangeTemplateID = MqlUtil.mqlCommand(context, strMQLcommand,Veeva.TYPE_CHANGE_TEMPLATE, (String)properties.get("veeva.change.template.name"), (String)properties.get("veeva.change.template.rev"), Veeva.SELECT_ID, Veeva.SYMBOL_PIPE);
			logger.info("Change Template ID:"+sChangeTemplateID);

			ChangeOrder change = new ChangeOrder();
			objectId = change.create(context, Veeva.EMPTY_STRING, Veeva.EMPTY_STRING, context.getUser(), sChangeTemplateID, null);
			logger.info("CO Created with object ID:"+objectId);
			changeOrderObj.setId(objectId);
			logger.info("CO Business object set");
			changeOrderObj.setPrimaryOwnership(context, Veeva.PROJECT, Veeva.ORGANIZATION);
			logger.info("Set Primary Organization on CO Business object");
			changeOrderObj.setDescription(context, properties.getProperty("veeva.changeorder.description"));
			logger.info("Set Description on CO Business object");

			changeOrderObj.setAttributeValue(context, Veeva.ATTRIBUTE_ORIGINATOR, context.getUser());
			logger.info("Set Originator on CO Business object");

			ContextUtil.commitTransaction(context);
		} catch(Exception e) {
			logger.error("Exception occured in createChangeOrder method "+e);
			throw e;
		} finally {
			if(isContextPushed)
				ContextUtil.abortTransaction(context);
		}
		logger.info("Exit createChangeOrder Method");
		return changeOrderObj;
	}

	/**
	 * @param context: matrix context
	 * @param changeOrderID: holds change order id
	 * @param artworkID: holds artwork id
	 * @return boolean true or false
	 * @throws MatrixException
	 * Creates CA and connects artwork object
	 */
	public boolean connectArtwork(Context context, String changeOrderID, String artworkID) throws MatrixException {
		logger.info("Entered connectArtwork Method");
		boolean isConnected = false;
		boolean isTransactionStarted = false;
		try {
			logger.info("--------CO/CA start----------");
			String[] coids = new String[] {changeOrderID}; 
			Map<String, String[]> jpoArgs = new HashMap<String, String[]>(); 
			jpoArgs.put(DCLConstants.NEW_OBJECTID,coids ); 
			jpoArgs.put(DCLConstants.OBJECTID, new String[] {artworkID}); 
			ContextUtil.startTransaction(context, true);
			isTransactionStarted = true;
			logger.info("--------CO/CA----------"+coids);
			logger.info("--------artworkID----------"+artworkID);
			logger.info("--------jpoArgs----------"+jpoArgs);
			JPO.invoke(context, "ENODCLDocumentUIBase", null, "connectChangeOrderToDocument", JPO.packArgs(jpoArgs),
					Map.class);
			logger.info("CA creation and artwork connection is successfull");
			isConnected = true;
		} catch(Exception e) {
			isConnected = false;
			if(isTransactionStarted)
				ContextUtil.abortTransaction(context);
			throw e;
		} finally {
			if(isTransactionStarted)
				ContextUtil.commitTransaction(context);
		}
		logger.info("Exit connectArtwork Method");
		return isConnected;
	}
}
