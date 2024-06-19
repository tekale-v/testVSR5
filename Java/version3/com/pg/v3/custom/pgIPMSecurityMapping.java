package com.pg.v3.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;


public class pgIPMSecurityMapping extends HttpServlet {
	public Context context = null;
	//P&G: Modified by V2 HashMap to load emxCSSSecurityMapping properties file data - START
	public static HashMap hmSecurityMappingData = null;
	public static HashMap hmSecurityMappingAttributeData = null;
	//P&G: Modified by V2 HashMap to load emxCSSSecurityMapping properties file data - END
	  
	public void init() {
		System.out.println("Initializing Security Mapping servlet........");
		try {
			context = new Context("localhost");
			/*context.setUser("creator");
			context.setPassword("");
			context.connect();*/
			ContextUtil.pushContext(context, "User Agent", "", "");	
			if(hmSecurityMappingData==null) {
				loadAllSecurityMappingData(context);
			}
			System.out.println("Loaded CSS Security Mapping Data successfully........");
			if(hmSecurityMappingAttributeData==null) {
				hmSecurityMappingAttributeData = loadAllSecurityMappingAttributeData(context);
			}
			System.out.println("Loaded CSS Security Mapping Attribute Data successfully........");
		} catch (MatrixException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  catch(Exception ex){
			ex.printStackTrace();
		} finally {
			try {
				ContextUtil.popContext(context);
			} catch (FrameworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void loadAllSecurityMappingData(Context context) throws MatrixException {
		String strArrTypesRev [] = {"pgSecurityGroup_pgBUToPLMCategory","Business Unit_pgBUToPLMCategory","pgBrand_pgBrandtoPLMCategory","Business Unit_pgBUtoBrand","pgBrand_pgBrandtopgPLICSSBrand","pgPLICSSRegion_pgBURegiontoPLMRegion","pgPLICSSRegion_pgBURegiontoPLMArea","pgSecurityGroup_pgPLMCategorytoPLMSector","pgSecurityGroup_pgPLMCategorytoPLMSubSector","Business Unit_pgPLRelatedData"};
		hmSecurityMappingData = new HashMap();
		for(String str:strArrTypesRev) {
			String []args = str.split("_");
			loadEachTypeData(context,args[0],args[1]);
		}
		System.out.println("hmSecurityMappingData :"+hmSecurityMappingData.size());
	}
	
	public HashMap loadAllSecurityMappingAttributeData(Context context) throws Exception {
		HashMap hmSecurityMappingAttributeDataTemp = new HashMap();
		String strPGCSSSecurityMappingLine = null;
		StringTokenizer stPGCSSSecurityMappingLine = null;
		String strPGCSSSecurityMappingKey = null;
		String strPGCSSSecurityMappingValue = null;
		BusinessObject boV2SecMapping = null;
		String strPGCSSSecurityMappingFull  = null;
		try {

			boV2SecMapping = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, "pgV2Configuration", "pgCSSSecurityMapping", "eService Production");
			if(boV2SecMapping.exists(context)) {
				boV2SecMapping.open(context);
				matrix.db.Attribute attrpgCSSSecurityMapping = boV2SecMapping.getAttributeValues(context, "pgCSSSecurityMapping"); 
				if (attrpgCSSSecurityMapping != null) { 
					strPGCSSSecurityMappingFull = (String)attrpgCSSSecurityMapping.getValue(); 
				}
				if(null != strPGCSSSecurityMappingFull && !"".equals(strPGCSSSecurityMappingFull)) {
					StringTokenizer stPGCSSSecurityMappingFull = new StringTokenizer(strPGCSSSecurityMappingFull, "\n");
					while (stPGCSSSecurityMappingFull.hasMoreTokens()) {
						strPGCSSSecurityMappingLine = stPGCSSSecurityMappingFull.nextToken();
						if (strPGCSSSecurityMappingLine != null && !"null".equals(strPGCSSSecurityMappingLine) && !"".equals(strPGCSSSecurityMappingLine) && !strPGCSSSecurityMappingLine.startsWith("#")) {
							stPGCSSSecurityMappingLine	 = new StringTokenizer(strPGCSSSecurityMappingLine, "=");
							while (stPGCSSSecurityMappingLine.hasMoreTokens()) {
								strPGCSSSecurityMappingKey = stPGCSSSecurityMappingLine.nextToken().trim();
								strPGCSSSecurityMappingValue = stPGCSSSecurityMappingLine.nextToken().trim();
								if (strPGCSSSecurityMappingKey != null && !"null".equals(strPGCSSSecurityMappingKey) && !"".equals(strPGCSSSecurityMappingKey)) {
									hmSecurityMappingAttributeDataTemp.put(strPGCSSSecurityMappingKey, strPGCSSSecurityMappingValue);
								} 
							}
						}
					}
				}

				boV2SecMapping.close(context);
			}
		} catch (Exception ex) {
			boV2SecMapping.close(context);
			ex.printStackTrace();
			throw ex;
		} 
		//Modified for defect 5180 - Commented SOP line as memory out of bound exception is thrown at this line
		/*System.out.println("hmSecurityMappingAttributeData MAP -->>:"+hmSecurityMappingAttributeData);
		System.out.println("hmSecurityMappingAttributeData SIZE -->>:"+hmSecurityMappingAttributeData.size());*/
		return hmSecurityMappingAttributeDataTemp;
	}
	
	
	public static void loadEachTypeData(Context context,String strType,String strRel)
	{			
		String strRev  = "-";
		StringList objectSelects = new StringList(3);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_ID);		
		objectSelects.add("relationship["+strRel+"]");

		if("Business Unit".equals(strType)) {
			strRev = "";
		} 


		MapList mlPLMList = null;
		try {
			mlPLMList = DomainObject.findObjects(context, strType,null , strRev, null, "eService Production", "", false,objectSelects);
			//MapList mlPLMbrandList = DomainObject.findObjects(context,"Business Unit", "eService Production", whereExp, objectSelects);

			if(mlPLMList != null && mlPLMList.size() > 0)
			{
				Map mp = null;
				String strToobjName = "";
				String strFromobjName = "";
				String strRelExist = "";
				String strObjID = "";
				String ATTRIBUTE_PGBRANDONCATEGORY = PropertyUtil.getSchemaProperty("attribute_pgBrandOnCategory");
				StringList slRelSelect = new StringList();
				//Modified for defect 5180 - remove .value
				//slRelSelect.add("attribute["+ATTRIBUTE_PGBRANDONCATEGORY+"].value");
				slRelSelect.add("attribute["+ATTRIBUTE_PGBRANDONCATEGORY+"]");
				//String strRelWhere = "attribute[isCategoryWithoutBrand].value==TRUE";
				String pgBrandOnCategoryRelAttribute = "";
				String strBUName = "";
				Map BUobject = null;
				for(int i=0; i< mlPLMList.size(); i++)
				{
					mp = (Map) mlPLMList.get(i);
					if(mp!=null) 
					{
						strRelExist = (String)mp.get("relationship["+strRel+"]");
						if(null!=strRelExist && strRelExist.equals("True")){
							strObjID = (String)mp.get(DomainObject.SELECT_ID);
							DomainObject domStd = DomainObject.newInstance(context,strObjID);
							strFromobjName = (String)mp.get(DomainObject.SELECT_NAME);
							//StringList slRelatedDataList = null;
							StringList slRelatedDataList = new StringList();
							if("pgSecurityGroup".equals(strType) && "pgBUToPLMCategory".equals(strRel)) {
								//Modified for defect 5180 - Change getInfo to getRelated
								//slRelatedDataList = domStd.getInfoList(context, "to["+strRel+"].from.name");
								StringList slObjectSelect = new StringList(1);
								slObjectSelect.addElement(DomainConstants.SELECT_NAME);
								MapList mlRelatedDataList = domStd.getRelatedObjects( context,
										strRel,
										"*",
										slObjectSelect,
										new StringList(),
										true,
										false,
										(short)1,
										null,
										null
										);
								if(null != mlRelatedDataList && mlRelatedDataList.size() > 0)
								{
									for(int iCount=0;iCount<mlRelatedDataList.size();iCount++)
									{
										Map mapTemp = (Map)mlRelatedDataList.get(iCount);
										slRelatedDataList.addElement((String)mapTemp.get(DomainConstants.SELECT_NAME));
									}
								}
								//Modified for defect 5180 - Change getInfo to getRelated

								MapList mlSecCategoryData = domStd.getRelatedObjects( context,
										strRel,
										pgV3Constants.TYPE_BUSINESSUNIT,
										objectSelects,
										slRelSelect,
										true,
										false,
										(short)1,
										null,
										null
										);
								Iterator  itr = mlSecCategoryData.iterator();
								
								while(itr.hasNext()){
									BUobject = (Map) itr.next();
									strBUName = (String)BUobject.get(DomainObject.SELECT_NAME);
									//Modified for defect 5180 - remove .value from attribute
									//pgBrandOnCategoryRelAttribute = (String)BUobject.get("attribute["+ATTRIBUTE_PGBRANDONCATEGORY+"].value");
									pgBrandOnCategoryRelAttribute = (String)BUobject.get("attribute["+ATTRIBUTE_PGBRANDONCATEGORY+"]");
									hmSecurityMappingData.put("pgBUToPLMCategoryRelAttribute."+strBUName+"."+strFromobjName,pgBrandOnCategoryRelAttribute);
								}


							} 
							else {
								//Modified for defect 5180 - Change getInfo to getRelated
								//slRelatedDataList =  domStd.getInfoList(context, "from["+strRel+"].to.name");
								StringList slObjectSelect = new StringList(1);
								slObjectSelect.addElement(DomainConstants.SELECT_NAME);
								MapList mlRelatedDataList = domStd.getRelatedObjects( context,
										strRel,
										"*",
										slObjectSelect,
										new StringList(),
										false,
										true,
										(short)1,
										null,
										null
										);
								if(null != mlRelatedDataList && mlRelatedDataList.size() > 0)
								{
									for(int iCount=0;iCount<mlRelatedDataList.size();iCount++)
									{
										Map mapTemp = (Map)mlRelatedDataList.get(iCount);
										slRelatedDataList.addElement((String)mapTemp.get(DomainConstants.SELECT_NAME));
									}
								}
								//Modified for defect 5180 - Change getInfo to getRelated
							}
							for(int j=0; j<slRelatedDataList.size(); j++){
								if(j>0){

									strToobjName = strToobjName +","+(String)slRelatedDataList.get(j);
								}else{
									strToobjName = (String)slRelatedDataList.get(j);
								}
							}
							if("pgSecurityGroup".equals(strType) && "pgBUToPLMCategory".equals(strRel)) {
								hmSecurityMappingData.put("pgPLMCategorytoBU"+"."+strFromobjName, strToobjName);
							} else {
								hmSecurityMappingData.put(strRel+"."+strFromobjName, strToobjName);
							}

						}
					}
				}				
			}			
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	//P&G: Modified by V2 to remove references of emxCSSSecurityMapping properties file
	
}
