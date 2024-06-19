package com.pg.pdf.util;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.parsers.FactoryConfigurationError;

import java.util.logging.Logger;

import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.output.XMLOutputter;
import com.matrixone.jdom.transform.XSLTransformer;

import org.w3c.dom.DOMException;


import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.jdom.transform.XSLTransformException;
import com.pg.pdf.enumerations.PDFConstants;
import com.pg.pdf.bean.input.xml.Section;
import com.pg.pdf.bean.input.xml.Table;
import com.pg.pdf.bean.input.xml.XML;
import com.pg.pdf.bean.output.xml.OutPutFieldDetails;
import com.pg.pdf.bean.output.xml.OutPutXML;
import com.pg.pdf.bean.output.xml.OutPutXMLSections;
import com.pg.pdf.bean.output.xml.OutPutXMLTableSections;
import com.pg.pdf.component.Component;
import com.pg.pdf.component.FormUtil;
import com.pg.pdf.component.TableUtil;
import com.pg.v3.custom.pgV3Constants;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;


public class PDFUtil extends PDFConstants{
	boolean isValidView;
	OutPutXML outPutXMLBean;
	OutPutFieldDetails objField; 
	Context context;
	public PDFUtil(Context context,OutPutFieldDetails objField){
		this.objField = objField;
		this.context =context;
	}
	public PDFUtil(){

	}
	Element root;
	private  final Logger logger = Logger.getLogger(PDFUtil.class.getName());
	public XML getXMLPage(Context context) throws JAXBException, MatrixException   {
		return (XML) unmarshallXMLFile(new StringReader(readConfigXMLPage(context)), XML.class);
	}

	public Object unmarshallXMLFile(StringReader reader, Class<?> cls) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(cls);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return unmarshaller.unmarshal(reader);
	}

	public String readConfigXMLPage(Context context) throws MatrixException   {
		logger.info("Enter  PDFUtil -method readConfigXMLPage");
		String out = DomainConstants.EMPTY_STRING;
		Page page = null;
		try {
			page = new Page(CONSTANT_STUDY_PROTOCOL_XML_PAGE);
			page.open(context);
			out = page.getContents(context);
		} catch (Exception e) {
			logger.info("Exception occurred in PDFUtil -method readConfigXMLPage: " + e.getMessage());
			
		} finally {
			if (Objects.requireNonNull(page).isOpen())
				page.close(context);
		}
		logger.info("Config Page XML loaded");
		logger.info("Exit PDFUtil -method readConfigXMLPage");
		return out;
	}
	public void setObjDetailsInBean(Context context, XML xml, Map<String, String> requestMap) {
		logger.info("Enter  PDFUtil -method setObjDetailsInBean");
		try {
			String objId =requestMap.get(CONSTANT_OBJECT_ID);
			StringList slSelectableList = new StringList();
			List<Section> listofSection =  xml.getSections().get(0).getSection();
			List<OutPutXMLSections> listofOPSections = new ArrayList<>();
			List<OutPutXMLTableSections> listofOPTableSections = new ArrayList<>();
			OutPutXML outPutXMLsections = new OutPutXML();
			outPutXMLsections.setSectionName(xml.getName());
			Component com = new Component(context);
			for(int i=0;i<listofSection.size();i++){
				Section objSection = listofSection.get(i);
				String sAllowedViews = objSection.getFieldView();
				this.isValidView = com.validateViews(requestMap.get(CONST_VIEW),sAllowedViews);
				//Need to check for particular view based on delimiter
				if (this.isValidView){
					OutPutXMLSections newSection = new OutPutXMLSections();
					newSection.setSection(objSection.getFieldName());
					List<Table> listofTable = objSection.getTable();
					int tableSize = listofTable.size();
					for(int j=0;j<tableSize;j++){
						Table objTable = listofTable.get(j);
						String sFormOrTable = objTable.getFieldType();
						if(UIUtil.isNotNullAndNotEmpty(sFormOrTable)){
							if (sFormOrTable.equalsIgnoreCase(CONSTANT_FORM)){
								FormUtil formUtil = new FormUtil(context);
								newSection= formUtil.processFormFields(objTable,context,newSection,requestMap);
								slSelectableList.addAll(newSection.getSlSelectableList());
								listofOPSections.add(newSection);

							}
							if (sFormOrTable.equalsIgnoreCase(CONSTANT_TABLE)){
								TableUtil tableUtil = new TableUtil(context);
								OutPutXMLTableSections tableSections =tableUtil.processTableFields(objTable,context,newSection,requestMap,objId);
								listofOPTableSections.add(tableSections);
							}
						}
					}
				}

			}
			outPutXMLsections.setSectionDetails(listofOPSections);
			com.fetchselectables(context,slSelectableList,objId,outPutXMLsections);
			slSelectableList.clear();
			slSelectableList.setSize(0);
			outPutXMLsections.setTableSections(listofOPTableSections);
			this.outPutXMLBean= outPutXMLsections;


		} catch (Exception e) {
			logger.info("Exception occurred in  PDFUtil -method setObjDetailsInBean  "+ e.getMessage());
			
		}
		logger.info("Exit  PDFUtil -method setObjDetailsInBean");
	}   



	public Document createXML() {
		logger.info("Enter  PDFUtil -method createXML");
		Document doc = new Document();
		root = new XMLElement(ELEMENT_SP);
		createSection(root);
		doc.setRootElement(root);
		logger.info("Exit  PDFUtil -method createXML");
		return doc;
	}
private void createSection(Element root) {
		logger.info("Enter  PDFUtil -method createSection");
		try {
			int nSections = outPutXMLBean.getSectionDetails().size();
			int nTables = outPutXMLBean.getTableSections().size();
			for(int i=0;i<nSections;i++){
				Element section = new XMLElement(outPutXMLBean.getSectionDetails().get(i).getSection());
				List<OutPutFieldDetails> outPutFieldDetails = outPutXMLBean.getSectionDetails().get(i).getFieldDetails();
				int nFields = outPutFieldDetails.size();
				for(int j=0;j<nFields;j++){
					section.setAttribute(outPutFieldDetails.get(j).getFieldName(), outPutFieldDetails.get(j).getFieldValue());
				}
				root.addContent(section);
			}
			for(int j=0;j<nTables;j++){
				List<OutPutXMLSections> outPutSectionDetails = outPutXMLBean.getTableSections().get(j).getOutPutXMLSections().getSectionDetails();
				int nTSections = outPutSectionDetails.size();
				Element section = new XMLElement(outPutXMLBean.getTableSections().get(j).getOutPutXMLSections().getSectionName());
				for(int i=0;i<nTSections;i++){
					Element childsection = new XMLElement(outPutSectionDetails.get(i).getSection());
					List<OutPutFieldDetails> outPutFieldDetails = outPutSectionDetails.get(i).getFieldDetails();
					if(outPutFieldDetails!=null){
						int nFields = outPutFieldDetails.size();
						String sFiledValue;
						for(int k=0;k<nFields;k++){
							sFiledValue = outPutFieldDetails.get(k).getFieldValue();
							if(!BusinessUtil.isNotNullOrEmpty(sFiledValue)){
								sFiledValue = DomainConstants.EMPTY_STRING;
							}
							childsection.setAttribute(outPutFieldDetails.get(k).getFieldName(),sFiledValue);
						}
						section.addContent(childsection);
					}else{
						root.addContent(section);
						section = new XMLElement(outPutXMLBean.getTableSections().get(j).getOutPutXMLSections().getSectionName());
					}
				}
				root.addContent(section);
			}
		} catch (DOMException e) {
			logger.info("Exception occurred in  PDFUtil -method createSection");
			
		}
		logger.info("Exit  PDFUtil -method createSection");
	}

	public String generateHTML(Context context, Document doc) throws MatrixException, FactoryConfigurationError, XSLTransformException {
		String xslStr = readConfigXSLPage(context);
		return generateXSLTOXML(doc,xslStr);
	}
	private String generateXSLTOXML(Document doc, String xslStr) throws XSLTransformException {

		XSLTransformer transformerXSL = new XSLTransformer(new StringReader(xslStr));
		XMLOutputter  outputter = new XMLOutputter();

		String outString 	= outputter.outputString(transformerXSL.transform(doc));
		transformerXSL.transform(doc);

		return outString;


	}

	public String readConfigXSLPage(Context context) throws MatrixException {
		logger.info("Enter  PDFUtil -method readConfigXSLPage");
		String out = DomainConstants.EMPTY_STRING;
		Page page = null;
		try {
			page = new Page(CONSTANT_STUDY_PROTOCOL_XSL_PAGE);
			page.open(context);
			out = page.getContents(context);
		} catch(Exception e) {
			logger.info("Exception occurred in PDFUtil -method readConfigXSLPage: " + e.getMessage());
			
		}finally {
			if (Objects.requireNonNull(page).isOpen())
				page.close(context);
		}
		logger.info("Exit  PDFUtil -method readConfigXSLPage");
		return out;
	}
	@SuppressWarnings("unchecked")
	public String buildPayLoadCalliText(Context context, String objId,String view,String htmlCodeData,  String workDir) {
		logger.info("Enter  PDFUtil -method buildPayLoadCalliText");
		String pdfFile = EMPTY_STRING;
		try {
			Map<String, String> pdfPayLoad = getHeaderDetails();
			pdfPayLoad.put(CONSTANT_VIEW_KIND, view.trim());
			pdfPayLoad.put(CONSTANT_TYPE_KIND, STUDY_PROTOCOL);
			logger.info("PDF render started for the Object:"+getValueFromMap(pdfPayLoad, DomainConstants.SELECT_NAME)+" Rev:"+getValueFromMap(pdfPayLoad, DomainConstants.SELECT_REVISION));
			String pdfFileName = getValueFromMap(pdfPayLoad, DomainConstants.SELECT_NAME) + SYMBOL_HYPHEN_STRICT+CONSTANT_REV+getValueFromMap(pdfPayLoad, DomainConstants.SELECT_REVISION)
					+ CONSTANT_PDF_FILE_EXTENSION;
			//can't be parameterized as it is holding both String and Map 
			Map argMap = new HashMap<>();
			argMap.put(DomainConstants.SELECT_ID, objId);
			argMap.put(CONSTANT_HTML_DATA, htmlCodeData);
			argMap.put(CONSTANT_PDF_FILE_NAME, pdfFileName);
			argMap.put(CONSTANT_PDF_PAY_LOAD, pdfPayLoad);
			argMap.put(CONSTANT_VIEW_KIND, view.trim());
			argMap.put(CONSTANT_TYPE_KIND, STUDY_PROTOCOL);
			argMap.put(CONST_WORK_DIR,workDir);
			String[] strArgs = JPO.packArgs(argMap);
			ITextUtil itext = new ITextUtil(context, strArgs);
			pdfFile =  itext.createPdf(context, strArgs);
		} catch (Exception e) {
			logger.info("Exception occurred in PDFUtil -method buildPayLoadCalliText: " + e.getMessage());
			
		}
		logger.info("Exit  PDFUtil -method buildPayLoadCalliText");
		return pdfFile;
	}

	public static String getValueFromMap(Map<String, String> map, String key) {
		String val =  map.get(key);
		return val == null ? EMPTY_STRING : val.trim();
	}
	public Map<String, String> getHeaderDetails(){
		Map<String, String> pdfPayLoad = new HashMap<>();
		try{
		List<OutPutFieldDetails> outPutFieldDetails = outPutXMLBean.getSectionDetails().get(0).getFieldDetails();
		int nFields = outPutFieldDetails.size();
		for(int j=0;j<nFields;j++){
			pdfPayLoad.put(outPutFieldDetails.get(j).getFieldName(), outPutFieldDetails.get(j).getFieldValue());
		}
		}catch (Exception e){
			logger.info("Exeception occurred in PDFUtil -method getHeaderDetails"+ e.getMessage());
		}finally {
			outPutXMLBean.getSectionDetails().clear();
		}
		return pdfPayLoad;
	}
	public String createWorkdir(Context context) throws MatrixException{  
		StringBuilder workDir = new StringBuilder();
		logger.info("Enter  PDFUtil -method createWorkdir");
		try {
			//22x Upgrade Modification Start
			workDir.append(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH));
			workDir.append(File.separator);
			workDir.append(CONST_PDF_BASE_FOLDER);
			//22x Upgrade Modification End
			workDir.append(File.separator);
			workDir.append(CONSTANT_FOLDER_FONTS);
			workDir.append(File.separator);
		} catch (Exception e) {
			logger.info("Exeception occurred in PDFUtil -method createWorkdir"+ e.getMessage());
			
		}
		logger.info("Exit  PDFUtil -method createWorkdir");
		return  workDir.toString();
	}
	

	public String getClassification(String val){
		if(UIUtil.isNotNullAndNotEmpty(val)&& pgV3Constants.RESTRICTED.equalsIgnoreCase(val)){
			objField.setFieldValue(pgV3Constants.BUSINESS_USE);
		}
		return val;
	}

	public String getActualName(String val) throws MatrixException{
		if(UIUtil.isNotNullAndNotEmpty(val)){	
			val = UINavigatorUtil.getAdminI18NString(objField.getFieldName(), val, context.getSession().getLanguage());
			objField.setFieldValue(val);
		}
		return val;
	}
	
	/**
	 * This method added as part of req #40168 to display the state name as per UI
	 * @param val
	 * @return string
	 * @throws MatrixException
	 */
	public String getActualStateName(String val) throws MatrixException{
		if(UIUtil.isNotNullAndNotEmpty(val)){	
			StringList slSelectable = new StringList(2);
			slSelectable.addElement(DomainConstants.SELECT_CURRENT);
			slSelectable.addElement(DomainConstants.SELECT_POLICY);
		
			DomainObject domainObj= DomainObject.newInstance(context,val);
			Map<?,?> mMap = domainObj.getInfo(context, slSelectable);
			String sState  = (String)mMap.get(DomainConstants.SELECT_CURRENT);
			String sPolicy = (String)mMap.get(DomainConstants.SELECT_POLICY);
			val = EnoviaResourceBundle.getStateI18NString(context, sPolicy, sState,context.getLocale().getLanguage());
			objField.setFieldValue(val);
		}
		return val;
	}
	
	
}
