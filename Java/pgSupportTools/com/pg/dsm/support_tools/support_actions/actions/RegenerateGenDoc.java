/*
 **   RegenerateGenDoc.java
 **   Description - Introduced as part of 2018x.5 On Demand Support Tools.  
 **   To ReGenerate GenDoc on Released Parts
 **
 */
package com.pg.dsm.support_tools.support_actions.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportConstants;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class RegenerateGenDoc extends SupportAction {

	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;

	public RegenerateGenDoc(Context _context, String _supportAction, Map<String, String> _infoMap) {
		this._context = _context;
		this._supportAction = _supportAction;
		this._infoMap = _infoMap;
	}
	public RegenerateGenDoc() {}
	
	@Override
	public boolean checkState() throws Exception {
		return SupportUtil.isStateRelease(_infoMap.get(SELECT_CURRENT));
	}
	@Override
	public boolean hasAccess() throws Exception {
		return hasReadAccess();
	}
	public boolean hasReadAccess() throws Exception {
		return SupportUtil.hasAccess(_context, _infoMap.get(SELECT_ID), ACCESS_READ, ACCESS_SHOW);
	}
	@Override
	public boolean isQualified() throws Exception {
		return isTypeQualified();
	}
	
	public boolean isTypeQualified() throws Exception {
		return SupportUtil.getTypes(SupportUtil.getValue(_infoMap, SUPPORT_ACTION_TYPE)).contains(SupportUtil.getTypeSymbolicName(_context, SupportUtil.getValue(_infoMap, SELECT_TYPE)));
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about - To method is to process pre operation for GenDoc Regeneration
	 * @return String
	 * @throws Exception
	 */
	public String execute() throws Exception {
		String result = DomainConstants.EMPTY_STRING;
		try {		
			HashMap<String, String> argMap = new HashMap<>();
			argMap.put(OBJECT_ID, _infoMap.get(SELECT_ID));
			String[] methodarg = JPO.packArgs(argMap);				
			boolean isGenDocPresent  = (boolean)JPO.invoke(_context, SupportType.REGENERATE_GENDOC.getJPO(), null, SupportConstants.ISGENDOCPRESENT_METHOD, methodarg, boolean.class);
			if(isGenDocPresent){			
				disconnectAndDeleteGenDocFromObject();			
			}
			result = GENDOC_INITIATE;

		} catch(Exception e) {
			e.printStackTrace();
		}		
		return result ;
	}

	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to disconnect & deletion  disconnect the Connected Gendoc on Released object
	 * @throws Exception
	 */
	public void disconnectAndDeleteGenDocFromObject() throws Exception {
		try {
			String strId;
			String strRelId;
			Map<?, ?> mapData;

			if(SupportUtil.isNotNullEmpty(_infoMap.get(SELECT_ID))){	
				DomainObject domObj = DomainObject.newInstance(_context, _infoMap.get(SELECT_ID));
				
				StringList slbusSelect = new StringList(2);
				slbusSelect.add(SELECT_NAME);
				slbusSelect.add(SELECT_REVISION);
	
				Map<?, ?> mapAttributeInfo = domObj.getInfo(_context, slbusSelect);
				String strRev = (String) mapAttributeInfo.get(SELECT_REVISION);
				String strObjName = (String) mapAttributeInfo.get(SELECT_NAME);
				
				StringList typeSelects = new StringList(1);
				typeSelects.add(SELECT_ID);

				StringList relSelects = new StringList(1);
				relSelects.add(SELECT_RELATIONSHIP_ID);
				
				String fileName = "LR_" + strObjName.trim() + "." + strRev.trim();
				String objectWhere = "name=='" + fileName.trim() + "' && revision==Rendition";
				
				MapList docList = domObj.getRelatedObjects(_context, 
													RELATIONSHIP_REFERENCEDOCUMENT,	//relationship pattern
													TYPE_PGIPMDOCUMENT, 			//type pattern
													typeSelects, 					//object selectes
													relSelects, 					//relselects
													false, 							//toside
													true, 							//fromside
													(short) 1, 						//recursion level
													objectWhere,					//object where
													null, 							//relationshipWhere
													null, 							//includeType
													null, 							//includeRelationship
													null							//includeMap
													);

				if (docList != null && !(docList.isEmpty())) {
					mapData = (Map<?, ?>) docList.get(0);
					strId = (String) mapData.get(SELECT_ID);
					strRelId = (String) mapData.get(SELECT_RELATIONSHIP_ID);

					if(SupportUtil.isNotNullEmpty(strRelId)){
						DomainRelationship.disconnect(_context, strRelId);
					}										
					if(SupportUtil.isNotNullEmpty(strId)){						
						DomainObject.deleteObjects(_context, new String[]{strId});
					}				
				}
			}
		} catch (Exception exception) {
			throw exception;
		}
	}

	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about - Helper method to record the history when the flagging is performed.  
	 * @return void 
	 * @throws Exception
	 */
	@Override
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), PARAM_CUSTOM, SupportType.REGENERATE_GENDOC.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER))); 
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to initiate background process for Selected objects
	 * @throws Exception
	 */
	public void backgroundProcess(Context context, MapList infoList, String strSupportAction) throws Exception {
		try{
			HashMap argMap = new HashMap();
			argMap.put("objectList", infoList);
			argMap.put("SupportAction", SupportType.valueOf(strSupportAction).getDisplayName());
			argMap.put("AdlibDetails", getGenDocAdlibDetails(context));
			argMap.put("MailDetails", getMailDetails(context));	
				
			BackgroundProcess backgroundProcess = new BackgroundProcess();
			backgroundProcess.submitJob(context, SupportType.REGENERATE_GENDOC.getJPO(), SupportType.REGENERATE_GENDOC.getMethod(),  JPO.packArgsRemote(argMap), (String)null);			
		
		}catch(Exception exception) {
			throw exception;
		}
	}

	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to get Adlib details from config object
	 * @return Map 
	 * @throws Exception
	 */
	public Map getGenDocAdlibDetails(Context context) throws Exception {

		Map<String,String> mAdlibDetails = new HashMap<>();
		try{
			Map<String,String> mAdlibobjMap ;
			StringList slBusSelects = new StringList(9);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGPROTOCOLGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGFTPINPUTFOLDERPATHGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGHOSTNAMEGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGRENDERPDFGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGFTPOUTPUTFOLDERPATHGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGFTPROOTFOLDERPATHGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGADLIBUSERNAMEGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGPORTGENDOC);
			slBusSelects.addElement(SELECT_ATTRIBUTE_PGADLIBPASSWORDGENDOC);
			String strAdlibObjId ;
			
			BusinessObject busObj = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, pgV3Constants.ADLIB_CONFIGOBJECT, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);

			if (busObj.exists(context)) {
				strAdlibObjId = busObj.getObjectId(context);
				if(SupportUtil.isNotNullEmpty(strAdlibObjId)){
					DomainObject domAdlibObj = DomainObject.newInstance(context, strAdlibObjId);			
					mAdlibobjMap = domAdlibObj.getInfo(context, slBusSelects);				

					mAdlibDetails.put("pgRenderPDFGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGRENDERPDFGENDOC));
					mAdlibDetails.put("pgPortGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPORTGENDOC));
					mAdlibDetails.put("pgFTPRootFolderPathGenDoc", (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFTPROOTFOLDERPATHGENDOC));
					mAdlibDetails.put("pgProtocolGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPROTOCOLGENDOC));
					mAdlibDetails.put("pgHostNameGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGHOSTNAMEGENDOC));
					mAdlibDetails.put("pgAdlibUserNameGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGADLIBUSERNAMEGENDOC));
					mAdlibDetails.put("pgAdlibPasswordGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGADLIBPASSWORDGENDOC));
					mAdlibDetails.put("pgFTPInputFolderPathGenDoc", (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFTPINPUTFOLDERPATHGENDOC));
					mAdlibDetails.put("pgFTPOutputFolderPathGenDoc",  (String) mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFTPOUTPUTFOLDERPATHGENDOC));
				}
			}
		}catch(Exception exception) {			
			exception.printStackTrace();
			throw exception;
		}
		return mAdlibDetails;
	}
	
	/**DSM 2018x.5 - On Demand Support Tools 
	 * @about -  Method to get Mail details from Page file
	 * @return Map 
	 * @throws Exception
	 */
	public Map<String, String> getMailDetails(Context context) throws Exception {
		
		Map<String, String> mMailDetails = new HashMap<>();
		try {
			Properties properties = SupportUtil.loadSupportActionConfigPage(context);
			String strFromId = PersonUtil.getEmail(context, PERSON_USER_AGENT);
			String strToId= SupportUtil.getSupportTeamMailId(context);
			String strMailSubject = properties.getProperty("SupportAction.FailureMail.Subject");
			String strMailContent = properties.getProperty("SupportAction.FailureMail.Content");

			mMailDetails.put("strFromId",strFromId);
			mMailDetails.put("strToId",strToId);
			mMailDetails.put("strMailSubject",strMailSubject);
			mMailDetails.put("strMailContent",strMailContent);
			
		} catch (Exception exception) {
			throw exception;
		}
		return mMailDetails;
	}

}
