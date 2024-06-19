/*
 * Added by APOLLO Team
 * For Characteristic Related Webservice
 */

package com.png.apollo.characteristics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.FileList;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

@Path("RangeValues")
public class RangeService extends RestService{
	
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(RangeService.class);

	@GET
	public Object getRanges(@Context HttpServletRequest req, @FormParam("attr") String paramString) {
		Map map=new HashMap();
		String output="";
		matrix.db.Context context = null;
		try {
				try {
					context=authenticate(req);
				} catch (java.io.IOException e) {
					e.printStackTrace();
					return Response.status(401).entity("login error").build();
				}
				
				String sPickListName="";
				
				switch(paramString) {
					case "attribute_pgMethodOrigin":
						sPickListName="pgPLIMethodOrigin";
						break;
					case "attribute_pgPlantTesting":
						sPickListName="pgPLIPlantTesting";
						break;
					case "attribute_pgRetestingUOM":
						sPickListName="pgPLIRetestingUOM";
						break;
					case "attribute_pgReportType":
						sPickListName="pgPLIReportType";
						break;
					case "attribute_pgActionRequired":
						sPickListName="pgPLIActionRequired";
						break;						
					case "attribute_pgCriticalityFactor":
						sPickListName="pgPLICriticalityFactor";
						break;
					case "attribute_pgTestGroup":
						sPickListName="pgPLITestGroup";
						break;
					case "attribute_pgChange":
						sPickListName="pgPLIChange";
						break;
					default:
						System.out.println("Attribute name is incorrect.");
				}
				
				HashMap myMap = new HashMap();
				HashMap fieldmap = new HashMap();
				HashMap settings = new HashMap();
				String sPLState = "";

				settings.put("pgPicklistName", sPickListName);
				settings.put("pgPicklistState", sPLState);
				fieldmap.put("settings", settings);
				myMap.put("fieldMap", fieldmap);
				map = (Map) JPO.invoke(context, "pgDSOUtil", null, "getPicklistRangeMapForDirectAttr", JPO.packArgs(myMap), Map.class);
				Set set = map.keySet();
				Iterator itr = set.iterator();
				while(itr.hasNext())
				{
					output = ""+map.get(itr.next());
				}
				output = output.substring(1, output.length() - 1);
			} catch (FrameworkException e) {
				e.printStackTrace();
			} catch (MatrixException e) {
				e.printStackTrace();
			}
		
		return Response.status(200).entity(output).build();
	}
	
	@Path("/RemoveTMLogicValue")
	@GET
	public Object updateCharacteristicSpecifics(@Context HttpServletRequest req, @FormParam("objectId") String paramString2 ) throws Exception
	{
		String output = "failed";
		try {
			matrix.db.Context ctx = authenticate(req);
			DomainObject dobj = DomainObject.newInstance(ctx, paramString2);
			dobj.setAttributeValue(ctx, "pgTMLogic", "");
			output = "SUCCESS";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Response.status(200).entity(output).build();
	}
	
	@Path("/checkLayeredProduct")
	@GET
	public Object isLayeredProductPart(@Context HttpServletRequest req, @FormParam("objectId") String paramString1) throws Exception
	{
		String output = "false";		
		try {
			String selectVPLMCONTROLLED = new StringBuilder(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(DomainConstants.RELATIONSHIP_PART_SPECIFICATION).append("].to["+pgApolloConstants.TYPE_VPMREFERENCE+"].").append(pgApolloConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED).toString();
			matrix.db.Context ctx = authenticate(req);
			DomainObject dobj = new DomainObject(paramString1);
			
			boolean bIsUpdateCharCommandVisible = false;			
			boolean bModifyAccess = FrameworkUtil.hasAccess(ctx, dobj , "modify");			
			StringList slSelects = new StringList(2);
			slSelects.addElement(DomainConstants.SELECT_TYPE);
			slSelects.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
			slSelects.addElement(selectVPLMCONTROLLED);
			Map partInfoMap = dobj.getInfo(ctx,slSelects);			
			if(null != partInfoMap){
				String strType = (String)partInfoMap.get(DomainConstants.SELECT_TYPE);			
				String strAuthoringApplication = (String)partInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
				if(strType.equals(pgApolloConstants.TYPE_ASSEMBLED_PRODUCT_PART) && strAuthoringApplication.equals(pgApolloConstants.RANGE_PGAUTHORINGAPPLICATION_LPD)){
					output = "true";
					String strVPLMControlled = 	(String)partInfoMap.get(selectVPLMCONTROLLED);					
					if(bModifyAccess && pgApolloConstants.STR_FALSE_FLAG_CAPS.equalsIgnoreCase(strVPLMControlled))
					{
						bIsUpdateCharCommandVisible = true;
					}
					output = new StringBuilder(output).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(Boolean.toString(bIsUpdateCharCommandVisible)).toString();
				}
			}			
		} catch (IOException e) {
			loggerApolloTrace.error(e.getMessage() ,e);
		}
		
		return Response.status(200).entity(output).build();
	}
	
	
	/**
	 * Check Design Param file present
	 * @param req
	 * @param strObjectId
	 * @return
	 * @throws MatrixException 
	 * @throws Exception
	 */
	@Path("/checkDesignParamFile")
	@GET
	public Object checkDesignParamFilePresent(@Context HttpServletRequest req, @FormParam("objectId") String strObjectId) throws MatrixException 
	{
		boolean bOutput = false;
		try 
		{
			matrix.db.Context context = authenticate(req);			
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				String strFileName;		
				DomainObject domObject = DomainObject.newInstance(context, strObjectId);
				StringBuffer sbFileNameDP = new StringBuffer();
				sbFileNameDP.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(domObject.getInfo(context, DomainConstants.SELECT_NAME)).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
				String strFileNameDP = sbFileNameDP.toString();
				FileList files = domObject.getFiles(context, pgApolloConstants.FORMAT_NOT_RENDERABLE);
				if(files!=null && !files.isEmpty())
				{
					for(matrix.db.File f : files)
					{
						strFileName = f.getName();
						if(UIUtil.isNotNullAndNotEmpty(strFileName) && strFileName.equals(strFileNameDP))
						{
							bOutput = true;
							break;
						}
					}
					
				}
			}			
		} catch (IOException e) {
			loggerApolloTrace.error(e.getMessage() ,e);

		}		
		String strOutput = Boolean.toString(bOutput);
		return Response.status(200).entity(strOutput).build();
	}
}
