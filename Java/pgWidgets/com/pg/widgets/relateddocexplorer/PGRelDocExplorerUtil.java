package com.pg.widgets.relateddocexplorer;

import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.integrations.datahandlers.JSONHandler;
import com.pg.designtools.util.CommonDocumentHandler;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

/**
 * @since 2018x.6
 * @author 
 *
 */
 public class PGRelDocExplorerUtil {
	
	 public PGRelDocExplorerUtil(Context context) {
			PRSPContext.set(context);
		}
	 
	 static final String KEY_DATA  = "data";
	 static final String KEY_HEADER_INFO  = "headerInfo";
	 
	 /**
	  * Method to get all the related documents info in a JSON
	  * @param context : eMatrix context
	  * @param strObjectId : String object id of object
	  * @return : String JSON response with all related data information for document
	 * @throws Throwable 
	  */
	 String getRelatedDocuments(Context context, String strObjectId,String strHeaderSelects,String strInputExpandLevel) throws Throwable{	
		
			JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
			
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				
			StringList slObjSelects=new StringList(7);
			slObjSelects.add(DomainConstants.SELECT_TYPE);
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add(DomainConstants.SELECT_REVISION);
			slObjSelects.add(DomainConstants.SELECT_OWNER);
			slObjSelects.add(DomainConstants.SELECT_POLICY);
			slObjSelects.add(DomainConstants.SELECT_FILE_NAME);
			slObjSelects.add(DataConstants.SELECT_PHYSICALID);
			
			DomainObject doObj = DomainObject.newInstance(context, strObjectId);
			Map<String,Object>mpObjInfo=doObj.getInfo(context, slObjSelects);
						
			if(!mpObjInfo.isEmpty()) {
				String strType=(String)mpObjInfo.get(DomainConstants.SELECT_TYPE);
				String strPolicy=(String)mpObjInfo.get(DomainConstants.SELECT_POLICY);
				
				MapList mlRelatedDocs = new MapList();
				JSONHandler jsonHandler=new JSONHandler();

				
				if(!DataConstants.TYPE_ARTIOSCAD_COMPONENT.equals(strType)) {
					//method to get the type for document as per type
					Pattern typePattern=getTypeNameForDocFromType(strType,strPolicy);
					
					//method to get the relationship for document as per type
					Pattern relPattern=getRelationshipNameForDocFromType(strType,strPolicy);
					
					//method to get the direction for document as per type
					String strToSide=getToDirectionofDocFromType(strType,strPolicy);
					
					//method to get the direction for document as per type
					String strExpandLevel=getExpandLevelForDocFromType(strType,strPolicy,strInputExpandLevel);
					
					String strFromSide=DataConstants.CONSTANT_TRUE;
					if(strToSide.equals(DataConstants.CONSTANT_TRUE))
						strFromSide=DataConstants.CONSTANT_FALSE;
					
					StringList slDocSelects=new StringList(6);
					slDocSelects.add(DomainConstants.SELECT_FILE_NAME);
					slDocSelects.add(DataConstants.SELECT_PHYSICALID);
					slDocSelects.add(DomainConstants.SELECT_NAME);
					slDocSelects.add(DomainConstants.SELECT_TYPE);
					slDocSelects.add(DomainConstants.SELECT_OWNER);
					slDocSelects.add(DomainConstants.SELECT_REVISION);
					
					//create new map which will be input for generic getRelatedObjects method
					Map<String,Object>mpInputData=new HashMap();
					mpInputData.put(DomainConstants.SELECT_ID, strObjectId);
					mpInputData.put(DomainConstants.SELECT_TYPE,strType);
					mpInputData.put(DataConstants.CONSTANT_TYPE_PATTERN,typePattern);
					mpInputData.put(DataConstants.CONSTANT_REL_PATTERN, relPattern);
					mpInputData.put(DataConstants.CONSTANT_OBJ_SELECTS, slDocSelects);
					mpInputData.put(DataConstants.CONSTANT_REL_SELECTS, new StringList());
					mpInputData.put(DataConstants.CONSTANT_TO, strToSide);
					mpInputData.put(DataConstants.CONSTANT_FROM,strFromSide);
					mpInputData.put(DataConstants.CONSTANT_OBJ_WHERE,"");
					mpInputData.put(DataConstants.CONSTANT_REL_WHERE, "");
					mpInputData.put(DataConstants.CONSTANT_EXPAND_LEVEL, strExpandLevel);
					
					CommonDocumentHandler comonDocHandler=new CommonDocumentHandler();
					mlRelatedDocs= comonDocHandler.getRelatedObjects(context,mpInputData);
				}
				
				if(checkCurrentObjectForDoc(context,doObj,strType,strPolicy)) {
					mlRelatedDocs=addSelfDocumentsToMapList(mpObjInfo,mlRelatedDocs);
				}
				
				//to create separate entries for files
				mlRelatedDocs=formatMapListForMultiFiles(mlRelatedDocs);

			
				if (UIUtil.isNotNullAndNotEmpty(strHeaderSelects)) {
					//convert type to user friendly one
					mpObjInfo=getUserFriendlyTypeName(context, mpObjInfo);
					JsonObject jsonHeaderInfo = jsonHandler.convertMapToJsonObj(Json.createObjectBuilder().build(), mpObjInfo);
					jsonObjOutput.add(KEY_HEADER_INFO, jsonHeaderInfo);
				}
				
				if(!mlRelatedDocs.isEmpty()) {
					//method to change the key "format.file.name" to fileName, as widget doesnt display data with key having fullstop in it
					mlRelatedDocs=modifyFileNameKey(mlRelatedDocs);
					mlRelatedDocs=modifyTypeName(context,mlRelatedDocs);
					JsonArray jsonArray = jsonHandler.convertMapListToJsonFlatTable(mlRelatedDocs);
					jsonObjOutput.add(KEY_DATA, jsonArray);
				}
			}
		}
		return jsonObjOutput.build().toString();
	}
	 
	 /**
	 * Method to change the value of type key to user friendly type name for given Maplist
	 * @param context
	 * @param MapList 
	 * @return : MapList with correct map
	 * @throws FrameworkException 
	 */ 
	 private MapList modifyTypeName(Context context,MapList mlRelatedDocs) throws FrameworkException {
		Map mpTemp = null;
		Map mpInfoMap;
		MapList mlFinalDocsInfo=new MapList();
		
		for(int i=0;i<mlRelatedDocs.size();i++) {
			mpInfoMap=(Map) mlRelatedDocs.get(i);
			
			mpTemp=getUserFriendlyTypeName(context,mpInfoMap);
			mlFinalDocsInfo.add(mpTemp);
		}
		return mlFinalDocsInfo;
	}
	
	/**
	 * Generic method to change the value of type key to user friendly type name 
	 * @param context
	 * @param Map 
	 * @return : Map with updated details
	 * @throws FrameworkException 
	 */ 
	 private Map getUserFriendlyTypeName(Context context, Map mpInfoMap) throws FrameworkException {
		 Map mpTemp=new HashMap();
		 Object[] arrKeys;
		 String strSimpleTypeName;
		 String strType;
		 
		 if(mpInfoMap.containsKey(DomainConstants.SELECT_TYPE)) {
			arrKeys=mpInfoMap.keySet().toArray();
			for(int j=0;j<arrKeys.length;j++) {
				if(arrKeys[j].equals(DomainConstants.SELECT_TYPE)) {
					//get the User friendly name form stringresource
					strType=(String) mpInfoMap.get(DomainConstants.SELECT_TYPE);
					if(strType.contains(" "))
						strType=strType.replace(" ", "_");
			
					strSimpleTypeName=EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Type."+strType, context.getLocale());
					if(UIUtil.isNotNullAndNotEmpty(strSimpleTypeName))
						mpTemp.put(DomainConstants.SELECT_TYPE,strSimpleTypeName);
					else
						mpTemp.put(DomainConstants.SELECT_TYPE,mpInfoMap.get(DomainConstants.SELECT_TYPE));
				}else {
					mpTemp.put(arrKeys[j],mpInfoMap.get(arrKeys[j]));
				}
			}
		}else {
			mpTemp=mpInfoMap;
		}
	  return mpTemp;
	}

	/**
	 * Method to change the file name key (format.file.name)to fileName, as json considers . as invalid char, hence data is not displayed for this key
	 * @param MapList 
	 * @return : MapList with correct map
	 */ 
	 private MapList modifyFileNameKey(MapList mlRelatedDocs) {
		Map mpTemp = null;
		Map mpInfoMap;
		MapList mlFinalDocsInfo=new MapList();
		Object[] arrKeys;
		for(int i=0;i<mlRelatedDocs.size();i++) {
			mpInfoMap=(Map) mlRelatedDocs.get(i);
			if(mpInfoMap.containsKey(DomainConstants.SELECT_FILE_NAME)) {
				mpTemp=new HashMap();
					
				arrKeys=mpInfoMap.keySet().toArray();
				for(int j=0;j<arrKeys.length;j++) {
					if(arrKeys[j].equals(DomainConstants.SELECT_FILE_NAME)) {
						mpTemp.put(DataConstants.KEY_FILE_NAME,mpInfoMap.get(DomainConstants.SELECT_FILE_NAME));
					}else {
						mpTemp.put(arrKeys[j],mpInfoMap.get(arrKeys[j]));
					}
				}
				mlFinalDocsInfo.add(mpTemp);
			}else {
				mlFinalDocsInfo.add(mpInfoMap);
			}
		}
		return mlFinalDocsInfo;
	}

	/**
	 * Method to format the maplist. In case there are multiple files connected, then different maps would be created
	 * @param MapList 
	 * @return : MapList with correct map
	 */ 
	private MapList formatMapListForMultiFiles(MapList mlRelatedDocs) {
		MapList mlFormattedDocList=new MapList();
			
		if(!mlRelatedDocs.isEmpty()) {
			Map mpObj;
			
			StringList slDocs=null;
			Object docs;
			StringBuilder sbFileNames;
				
			for(int i=0;i<mlRelatedDocs.size();i++) {
				mpObj=(Map)mlRelatedDocs.get(i);
					
				slDocs=new StringList();
				docs=mpObj.get(DomainConstants.SELECT_FILE_NAME);
				if(docs instanceof String) {
					slDocs=StringUtil.split((String)docs, DataConstants.SEPARATOR_COMMA);
				}else if(docs instanceof StringList) {
					slDocs=(StringList)docs;
				}
				if(slDocs.size()>1) {
					//create StringBuilder with pipe separated values
					sbFileNames=new StringBuilder();
						
					for(int j=0;j<slDocs.size();j++) {
						if(sbFileNames.length()==0)
							sbFileNames.append(slDocs.get(j).trim());
						else {
							sbFileNames.append(DataConstants.SEPARATOR_PIPE).append(slDocs.get(j).trim());
						}
					}
					mlFormattedDocList.add(addDetailsToMap(mpObj,sbFileNames.toString()));
				}else {
					mlFormattedDocList.add(mpObj);
				}
			}
		}
		return mlFormattedDocList;
	}
	
	/**
	 * Method to add the details to Map
	 * @param Map where info is present
	 * @param String document names
	 * @return : Map
	 */
	private Map addDetailsToMap(Map mpObj, String slDocName) {
		Map mpTemp=new HashMap();
		mpTemp.put(DomainConstants.SELECT_NAME, mpObj.get(DomainConstants.SELECT_NAME));
		mpTemp.put(DomainConstants.SELECT_TYPE, mpObj.get(DomainConstants.SELECT_TYPE));
		mpTemp.put(DomainConstants.SELECT_REVISION, mpObj.get(DomainConstants.SELECT_REVISION));
		mpTemp.put(DomainConstants.SELECT_OWNER,mpObj.get(DomainConstants.SELECT_OWNER));
		mpTemp.put(DataConstants.SELECT_PHYSICALID, mpObj.get(DataConstants.SELECT_PHYSICALID));
		mpTemp.put(DataConstants.KEY_FILE_NAME, slDocName);
		return mpTemp;
	}

	/**
	 * Method to add the information of documents checked-in to the object directly
	 * @param Map containing the information to be added
	 * @param MapList 
	 * @return : MapList with correct map
	 */ 
	private MapList addSelfDocumentsToMapList(Map<String, Object> mpObjInfo, MapList mlRelatedDocs) {

	StringList slDocs = new StringList();
	Object docs=mpObjInfo.get(DomainConstants.SELECT_FILE_NAME);
	if(!docs.toString().equals("[]")) {
		if(docs instanceof String) {
			slDocs.add((String)docs);
		}else if(docs instanceof StringList) {
			slDocs=(StringList)docs;
		}
	}
	
	if(!slDocs.isEmpty()) {
		
		//create StringBuffer with pipe separated values
		StringBuilder sbFileNames=new StringBuilder();
		
		for(int i=0;i<slDocs.size();i++) {
			if(sbFileNames.length()==0)
				sbFileNames.append(slDocs.get(i).trim());
			else {
				sbFileNames.append(DataConstants.SEPARATOR_PIPE).append(slDocs.get(i).trim());
			}
		}
		
		mlRelatedDocs.add(addDetailsToMap(mpObjInfo, sbFileNames.toString()));
	}
	return mlRelatedDocs;
}
	
	/**
	 * Method to validate whether we should check the current object for checked-in documents
	 * @param String type
	 * @param String policy 
	 * @return :boolean
	 * @throws FrameworkException 
	 */ 
	 private boolean checkCurrentObjectForDoc(Context context,DomainObject doObj,String strType, String strPolicy) throws FrameworkException {
		boolean bCheck=false;
		if(DataConstants.TYPE_PG_STACKINGPATTERN.equals(strType) || DataConstants.POLICY_EC_PART.equals(strPolicy)
				|| DataConstants.TYPE_ARTIOSCAD_COMPONENT.equals(strType) || DataConstants.POLICY_PRODUCT_DATA_SPECIFICATION.equals(strPolicy)
				|| doObj.isKindOf(context, DataConstants.TYPE_DOCUMENTS))
			bCheck=true;
		return bCheck;
	}

	/**
	 * Method to get the expansion level for different type of objects
	 * @param String type
	 * @param String policy 
	 * @return :String
	 */ 
	private String getExpandLevelForDocFromType(String strType,String strPolicy,String strInputExpandLevel) {
		String strExpandLevel="";
		if(DataConstants.TYPE_VPMREFERENCE.equals(strType)) {
			strExpandLevel="";
		}
		else if(DataConstants.TYPE_SIMULATION.equals(strType)) {
			strExpandLevel=DataConstants.CONSTANT_ALL;
		}
		else if(DataConstants.TYPE_PG_TRANSPORTUNIT.equals(strType)) {
			strExpandLevel=DataConstants.CONSTANT_TWO;
		}
		else if(DataConstants.POLICY_EC_PART.equals(strPolicy)) {
			if(DataConstants.CONSTANT_ALL.equalsIgnoreCase(strInputExpandLevel)) {
				//To display reference document of reference document, expand level is set to 3
				strExpandLevel=DataConstants.CONSTANT_THREE;
			}else
				strExpandLevel=DataConstants.CONSTANT_ONE;
		}
		else if(DataConstants.TYPE_PG_STACKINGPATTERN.equals(strType) || DataConstants.POLICY_PRODUCT_DATA_SPECIFICATION.equals(strPolicy)) {
			strExpandLevel=DataConstants.CONSTANT_ONE;
		}
		return strExpandLevel;
	}

	/**
	 * Method to validate whether documents are connected at to side for different type of objects
	 * @param String type
	 * @param String policy 
	 * @return :String
	 */ 
	private String getToDirectionofDocFromType(String strType,String strPolicy) {
		String strDocSide="";
		if(DataConstants.TYPE_VPMREFERENCE.equals(strType) || DataConstants.TYPE_SIMULATION.equals(strType) )
			strDocSide="";
		else if(DataConstants.TYPE_PG_STACKINGPATTERN.equals(strType) || DataConstants.POLICY_EC_PART.equals(strPolicy) || DataConstants.POLICY_PRODUCT_DATA_SPECIFICATION.equals(strPolicy))
			strDocSide=DataConstants.CONSTANT_TRUE;

		return strDocSide;
	}

	/**
	 * Method to get the relationship Pattern for different type of objects
	 * @param String type
	 * @param String policy 
	 * @return :Pattern
	 */ 
	private Pattern getRelationshipNameForDocFromType(String strType,String strPolicy) {
		Pattern relPattern=new Pattern("");
		
	 if(DataConstants.TYPE_PG_TRANSPORTUNIT.equals(strType) || DataConstants.POLICY_EC_PART.equals(strPolicy)) {
			relPattern=new Pattern(DataConstants.REL_PART_SPECIFICATION);
			relPattern.addPattern(DataConstants.REL_REFERENCE_DOCUMENT);
			relPattern.addPattern(DataConstants.REL_EBOM);
		}else if(DataConstants.TYPE_PG_STACKINGPATTERN.equals(strType) || DataConstants.POLICY_PRODUCT_DATA_SPECIFICATION.equals(strPolicy)) {
			relPattern=new Pattern(DataConstants.REL_REFERENCE_DOCUMENT);
		}
		return relPattern;
	}

	/**
	 * Method to get the type Pattern for different type of objects
	 * @param String type
	 * @param String policy 
	 * @return :Pattern
	 */
	private Pattern getTypeNameForDocFromType(String strType,String strPolicy) {
		Pattern typePattern=new Pattern("");
		if(DataConstants.TYPE_PG_STACKINGPATTERN.equals(strType)) {
			typePattern=new Pattern(DataConstants.TYPE_PGIPMDOCUMENT);
			typePattern.addPattern(DataConstants.TYPE_DOCUMENTS);
		}else if(DataConstants.TYPE_PG_TRANSPORTUNIT.equals(strType)) {
			typePattern=new Pattern(DataConstants.TYPE_PG_STACKINGPATTERN);
			typePattern.addPattern(DataConstants.TYPE_PACKING_INSTRUCTION);
			typePattern.addPattern(DataConstants.TYPE_DOCUMENTS);
		}else if(DataConstants.POLICY_EC_PART.equals(strPolicy)) {
			typePattern=new Pattern(DataConstants.TYPE_DOCUMENTS);
			typePattern.addPattern(DataConstants.TYPE_PRODUCT_DATA_PART);
			typePattern.addPattern(DataConstants.TYPE_SHAPE_PART);
		}else if(DataConstants.POLICY_PRODUCT_DATA_SPECIFICATION.equals(strPolicy)) {
			typePattern=new Pattern(DataConstants.TYPE_DOCUMENTS);
		}
		return typePattern;
	}
}
