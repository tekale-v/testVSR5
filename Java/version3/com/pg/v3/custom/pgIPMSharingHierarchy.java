package com.pg.v3.custom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
//Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
import com.matrixone.apps.framework.ui.UIUtil;
//Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  28-Oct-2016 - Ends


public class pgIPMSharingHierarchy extends HttpServlet {
	public Context context = null;
	  
	public void init() {
		System.out.println("Initializing Sharing Hierarchy servlet........");
		try {
			context = new Context("localhost");
			/*context.setUser("creator");
			context.setPassword("");
			context.connect();*/
			ContextUtil.pushContext(context, "User Agent", "", "");	
		} catch (MatrixException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String strpgSupplierSharingHierarchy = "";
		String strpgCMSharingHierarchy = "";
		try {
			StringList slSelectObject = new StringList(2);
			slSelectObject.add(pgV3Constants.SELECT_ATTRIBUTE_PGIPMCMSHARINGHIERARCHY);
			slSelectObject.add(pgV3Constants.SELECT_ATTRIBUTE_PGIPMSUPPLIERSHARINGHIERARCHY);
			
			MapList mlConfigObjectList = DomainObject.findObjects(context, pgV3Constants.TYPE_PGCONFIGURATIONADMIN, "pgV3ConfigurationMapping", "-", "*", pgV3Constants.VAULT_ESERVICEPRODUCTION,
																"", false, slSelectObject);
			if(null != mlConfigObjectList && mlConfigObjectList.size()>0){
				Map mConfigObjectMap = (Map)mlConfigObjectList.get(0);
				strpgCMSharingHierarchy = (String)mConfigObjectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPMCMSHARINGHIERARCHY);
				strpgSupplierSharingHierarchy = (String)mConfigObjectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPMSUPPLIERSHARINGHIERARCHY);
				pgV3Util shInit = pgV3Util.getInstance();
				if(null != strpgSupplierSharingHierarchy && !("").equals(strpgSupplierSharingHierarchy)){
					HashMap hmSupplierSharingHierarchy = parseXML(strpgSupplierSharingHierarchy);
					shInit.setSupplierMap(hmSupplierSharingHierarchy);
				}
				if(null != strpgCMSharingHierarchy && !("").equals(strpgCMSharingHierarchy)){
					HashMap hmManufacturerSharingHierarchy = parseXML(strpgCMSharingHierarchy);
					shInit.setManufacturerMap(hmManufacturerSharingHierarchy);
				}
			}
			ContextUtil.popContext(context);
			
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}

	/**
	 * Method to parse the data from file and form xml data
	 * @param String
	 * @return HashMap
	 **/
	
	public static HashMap parseXML(String strFile){
		LinkedHashMap hmSharingHierarchy = new LinkedHashMap();
        NodeList typeList = null;
        NodeList subTypeList = null;
        NodeList structuredList = null;
        NodeList IsFirstLevelList = null;
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
        NodeList IsOriginatingSourceList = null;
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends
        NodeList valueList = null;
        Element typeElement = null;
        Element SubTypeElement = null;
        Element structuredElement = null;
        Element IsFirstLevelElement = null;
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
        Element IsOriginatingSourceElement = null;
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends
        Element valueElement = null;
        NodeList nlTypeList = null;
        NodeList nlsubTypeList = null;
        NodeList nlStructuredList = null;
        NodeList nlIsFirstLevelList = null;
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
        NodeList nlOriginatingSourceList = null;
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends
        NodeList nlValueList = null;
        String strParentType = "";        
        String strType = "";
        String strSubType = "";
        String strStructured = "";
        String strchildStructured = "";
        String strIsFirstLevel = "";
        String strValue = "";
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
        String strOriginatingSource = "";
        //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends
       
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			try {
				docBuilder = docBuilderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Document doc = docBuilder.parse (new File(strFile));			
			Document doc = null;
			try {
				doc = docBuilder.parse(new InputSource(new ByteArrayInputStream(strFile.getBytes("utf-8"))));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// normalize text representation
			doc.getDocumentElement ().normalize ();

			NodeList listOfRecords = doc.getElementsByTagName("Record");
			int totalRecords = listOfRecords.getLength();
			Element recordElement = null;
			NodeList rootTypeList = null;
			NodeList rootTypeInfoList = null;
			Node typeInfoNode = null;
			StringBuffer sbFirstLevelStandard_Structured = new StringBuffer(3);
			StringBuffer sbFirstLevelStandard_Unstructured = new StringBuffer(3);
			for(int s=0; s<listOfRecords.getLength() ; s++){
				//Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
				strOriginatingSource = null;
				//Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends
				Node recordNode = listOfRecords.item(s);				
				if(recordNode.getNodeType() == Node.ELEMENT_NODE){
					recordElement = (Element)recordNode;
			        rootTypeList = recordElement.getElementsByTagName("RootType");
			        if(null != rootTypeList){
			        Node nRootTypeNode = rootTypeList.item(0);
			        if(nRootTypeNode.getNodeType() == Node.ELEMENT_NODE){
			        	
			        	typeList = recordElement.getElementsByTagName("Type");
			        	typeElement = (Element)typeList.item(0);
			            nlTypeList = typeElement.getChildNodes();
			            strParentType = ((Node)nlTypeList.item(0)).getNodeValue().trim().toString();
			            
			            structuredList = recordElement.getElementsByTagName("Structured");
			            structuredElement = (Element)structuredList.item(0);
			            nlStructuredList = structuredElement.getChildNodes();
			            strStructured = ((Node)nlStructuredList.item(0)).getNodeValue().trim().toString();
			            
			            IsFirstLevelList = recordElement.getElementsByTagName("IsFirstLevel");
			            IsFirstLevelElement = (Element)IsFirstLevelList.item(0);
			            nlIsFirstLevelList = IsFirstLevelElement.getChildNodes();
			            strIsFirstLevel = ((Node)nlIsFirstLevelList.item(0)).getNodeValue().trim().toString();
			            //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
			            IsOriginatingSourceList = recordElement.getElementsByTagName("OriginatingSource");
			            if(null != IsOriginatingSourceList){
			            	IsOriginatingSourceElement = (Element)IsOriginatingSourceList.item(0);
			            	if (null != IsOriginatingSourceElement) {
					            nlOriginatingSourceList = IsOriginatingSourceElement.getChildNodes();
					            strOriginatingSource = ((Node)nlOriginatingSourceList.item(0)).getNodeValue().trim().toString();
					           }
			            }
			            //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends
			            if("Y".equals(strIsFirstLevel) && "Y".equals(strStructured)){			            	
			            	sbFirstLevelStandard_Structured.append(strParentType);
			            	sbFirstLevelStandard_Structured.append("|");
			            } else if("Y".equals(strIsFirstLevel) && "N".equals(strStructured)) {
			            	sbFirstLevelStandard_Unstructured.append(strParentType);
			            	sbFirstLevelStandard_Unstructured.append("|");			       
			            }
			            
			        }
				}
					
			        rootTypeInfoList = recordElement.getElementsByTagName("TypeInfo");
			        for(int i=0; i<rootTypeInfoList.getLength() ; i++){
			            StringBuffer sbHMKey = new StringBuffer();
			            StringBuffer sbHMValue = new StringBuffer();
						strSubType = "";
						subTypeList = null;

			            sbHMKey.append(strParentType);
			            sbHMKey.append("-");
			            if(null != strStructured && "Y".equals(strStructured)){
			            	sbHMKey.append("struct");
			            }else if(null != strStructured && "N".equals(strStructured)){
			            	sbHMKey.append("unstruct");
			            }
			            sbHMKey.append("-");
			            //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Starts
			            if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource)){
			            	sbHMKey.append(strOriginatingSource);
			            	sbHMKey.append("-");
			             }
			            //Added by DSM-2015x.2 for EBP Security (Req ID-10981,10983,10984,10985) on  21-Oct-2016 - Ends

			        	typeInfoNode = rootTypeInfoList.item(i);
			        	if(typeInfoNode.getNodeType() == Node.ELEMENT_NODE){
			        		recordElement = (Element)typeInfoNode;
			            	
			        			typeList = recordElement.getElementsByTagName("Type");         
			                	typeElement = (Element)typeList.item(0);
			                    nlTypeList = typeElement.getChildNodes();
			                    strType = ((Node)nlTypeList.item(0)).getNodeValue().trim().toString();
			                    sbHMKey.append(strType);
			                    sbHMKey.append("-");
			            	
					            subTypeList = recordElement.getElementsByTagName("SubType");
					            if(null != subTypeList){
					            	SubTypeElement = (Element)subTypeList.item(0);
					            	if (null != SubTypeElement) {
					            		nlsubTypeList = SubTypeElement.getChildNodes();
					            		strSubType = ((Node)nlsubTypeList.item(0)).getNodeValue().trim().toString();				                    
										sbHMKey.append(strSubType);
										sbHMKey.append("-");
									}
					            	
					            }

			                    valueList = recordElement.getElementsByTagName("Value");
			                    valueElement = (Element)valueList.item(0);
			                    nlValueList = valueElement.getChildNodes();
			                    strValue = ((Node)nlValueList.item(0)).getNodeValue().trim().toString();

			                    structuredList = recordElement.getElementsByTagName("Structured");
			                    structuredElement = (Element)structuredList.item(0);
			                    nlStructuredList = structuredElement.getChildNodes();
			                    strchildStructured = ((Node)nlStructuredList.item(0)).getNodeValue().trim().toString();
			                    if(null != strchildStructured && "Y".equals(strchildStructured)){
			                    	sbHMKey.append("struct");
			                    }else if(null != strchildStructured && "N".equals(strchildStructured)){
			                    	sbHMKey.append("unstruct");
			                    }
			                    sbHMValue.append(strValue);
			                    hmSharingHierarchy.put(sbHMKey.toString(), sbHMValue.toString());
			                    
			        	}
			        }
				}
			}
			if(sbFirstLevelStandard_Structured.length()>0){
				hmSharingHierarchy.put("FirstLevelDoc_Structured", sbFirstLevelStandard_Structured.substring(0, sbFirstLevelStandard_Structured.length()-1).toString());
			}
			if(sbFirstLevelStandard_Unstructured.length()>0){
				hmSharingHierarchy.put("FirstLevelDoc_Unstructured", sbFirstLevelStandard_Unstructured.substring(0, sbFirstLevelStandard_Unstructured.length()-1).toString());
			}
			return hmSharingHierarchy;
	}
	
}
