/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM (Sogeti)
   JAVA Name: GPSTask
   Purpose: JAVA class created to do generate GPAAssesmentTask related pdf file.
 **/
package com.pg.irm.pdf.views;

import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Starts
import com.matrixone.apps.domain.util.StringUtil;
//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Ends
import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.output.XMLOutputter;
import com.matrixone.jdom.transform.XSLTransformer;
import com.pg.irm.pdf.PDFConstants;
import com.pg.irm.pdf.PDFType;
import com.pg.irm.pdf.ui.FormUI;
import com.pg.irm.pdf.ui.TableUI;
import com.pg.irm.pdf.util.ITextUtil;
import com.pg.irm.pdf.util.StringHelper;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.util.StringList;

public class GPSTask extends PDFType implements PDFConstants {

	private ServletContext _appCtx = null;
	private PageContext _pageCtx = null;
	private HttpServletRequest _servletReq = null;
	private Context _ctx = null;
	private String _timeStamp = null;
	private String _languageStr = null;

	protected Element _element;
	String _category = EMPTY_STRING;

	// Constructor
	public GPSTask() {
	}

	//Initialization
	public void init(Context context, javax.servlet.ServletContext servletContext,
			javax.servlet.jsp.PageContext pageContext, javax.servlet.http.HttpServletRequest servletRequest,
			String timeStamp, String languageStr) {
		this._ctx = context;
		this._appCtx = servletContext;
		this._pageCtx = pageContext;
		this._servletReq = servletRequest;
		this._timeStamp = timeStamp;
		this._languageStr = languageStr;

	}
	/**
	 * @Desc Method used to generate PDF file
	 * @param context
	 * @param args
	 * @return -string generated file path
	 * @throws Exception
	 */
	public String generate(Context context, String[] args) throws Exception {

		String sObjectId = args[0];
		String sViewType = args[1];
		String sContextUser = args[2];

		//22x Upgrade Modification Start
		String appPath = new StringBuilder(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH)).append(File.separator).append(PDFConstants.CONST_PDF_BASE_FOLDER).toString();
		//22x Upgrade Modification End
		StringList selectList = new StringList(2);
		selectList.add(DomainConstants.SELECT_TYPE);
		selectList.add(SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
		// Added for requirement 35469
		selectList.add(SELECT_ATTRIBUTE_BUSINESS_AREA);
		//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Starts
		selectList.add(SELECT_ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA);
		selectList.add(SELECT_ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS);
		selectList.add(SELECT_ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE);
		//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Ends
		DomainObject obj = DomainObject.newInstance(context, sObjectId);
		Map objectMap = obj.getInfo(context, selectList);
		String type = (String) objectMap.get(DomainConstants.SELECT_TYPE);

		String category = (String) objectMap.get(SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
		_category = StringHelper.getRequestCategoryShortName(category);

		StringBuffer buffer = new StringBuffer();
		//22x Upgrade Modification Start
		buffer.append(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH));
		buffer.append(File.separator);
		buffer.append(PDFConstants.CONST_PDF_BASE_FOLDER);
		//22x Upgrade Modification End
		buffer.append(File.separator);
		buffer.append(PDFConstants.CONSTANT_FOLDER_FONTS);
		buffer.append(File.separator);
		String wrkDir = buffer.toString();

		GPSTaskCategory categoryObj = new GPSTaskCategory(context, _category);
		Map pageConfigMap = categoryObj.loadConfigPage();
		Map categoryMap = (Map)pageConfigMap.get(_category);

		// Added for requirement 35469
		// for additional fields which are not configured in the table (table like.. context of use)
		Map tableDataMap = TableUI.getTableDataMap(_ctx, _appCtx, _pageCtx, _servletReq, _timeStamp, _languageStr, (Map) categoryMap.get(PDFConstants.CONSTANT_TABLES), sObjectId);
		if(tableDataMap.containsKey(XML_CONFIG_TABLE_NAME_CONTEXT_OF_USE)) {
			MapList contextOfUseTableData = (MapList) tableDataMap.get(XML_CONFIG_TABLE_NAME_CONTEXT_OF_USE);
			// business area is (sort of) a filter which is going to be the same for all the data shown in the table.
			// append business area key-value pair.
			contextOfUseTableData.forEach(map -> ((Map) map).put(XML_CONFIG_TABLE_COLUMN_NAME_TO_BE_ASSESSED_BUSINESS_AREA, objectMap.get(SELECT_ATTRIBUTE_BUSINESS_AREA)));
		}
		//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Starts
		if(tableDataMap.containsKey(XML_CONFIG_TABLE_NAME_IMPORT_CONTEXT_OF_USE)) {
			MapList importTableData = (MapList) tableDataMap.get(XML_CONFIG_TABLE_NAME_IMPORT_CONTEXT_OF_USE);
			// append form field key-value pair.	
			importTableData.forEach(map -> ((Map) map).put(XML_CONFIG_TABLE_COLUMN_NAME_PGISMATERIALIMPORTEDINTOEEA, objectMap.get(SELECT_ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA)));
			importTableData.forEach(map -> ((Map) map).put(XML_CONFIG_TABLE_COLUMN_NAME_PGFINISHEDPRODUCTIMPORTEDAS, objectMap.get(SELECT_ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS)));
			importTableData.forEach(map -> ((Map) map).put(XML_CONFIG_TABLE_COLUMN_NAME_PGISMATERIALCLASSIFIEDMIXTUREORSUBSTANCE, objectMap.get(SELECT_ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE)));
		}
		//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Ends
		return generate(context, args, categoryMap,
				tableDataMap,
				FormUI.getFormDataMap(_ctx,_appCtx,_pageCtx,_servletReq,_timeStamp,_languageStr,
						(Map)categoryMap.get(PDFConstants.CONSTANT_FORMS), 
						sObjectId));
	}

	/**
	 * @Desc Method used to generate PDF file
	 * @param context - Context
	 * @param tableDataMap - Map
	 * @param columnMap - Map
	 * @return void
	 * @throws Exception
	 * @since - 18x.3
	 */
	public void createTableXMLElement(Context context, Map tableDataMap, Map columnMap) throws Exception {
		
		StringList columnList = (StringList)columnMap.get(CONSTANT_COLUMNS);
		String minorElement = (String)columnMap.get(CONSTANT_MINOR);
		String tableName = (String)columnMap.get(CONSTANT_NAME);
		MapList dataList = (MapList)tableDataMap.get(tableName);
		if (dataList != null && dataList.size() > 0) {
			//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Starts
			if(tableName.equals(XML_CONFIG_TABLE_NAME_IMPORT_CONTEXT_OF_USE)) {
				createTableXMLElementForICOU(dataList, columnMap);				
			}
			//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Ends
			else{
			Element inputs = new XMLElement((String)columnMap.get(CONSTANT_MAJOR));
			Iterator itr = dataList.iterator();
			while (itr.hasNext()) {
				Map<String, Object> tmp = (Map) itr.next();
				Element input = new XMLElement(minorElement, StringHelper.removeAllSpacesFromElementNameTag(tmp), columnList);
				inputs.addContent(input);
			}
			_element.addContent(inputs);
			}
		}
	} 
	
	//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Starts
	 private void createTableXMLElementForICOU(MapList dataList, Map columnMap) {
         
         Element inputs = new XMLElement((String)columnMap.get(CONSTANT_MAJOR));
         StringList slDirectICOU=StringUtil.split(IMPORT_DIRECT_COLUMN_LIST, SYMBOL_COMMA);
         StringList slIndirectCategories = StringUtil.split(IMPORT_INDIRECT_CATEGORIES, SYMBOL_COMMA);

         Iterator itr = dataList.iterator();
         
         Element inputsIndirect = new XMLElement(TABLE_IMPORT_COU_INDIRECT_MAJOR);
         StringList slInDirectICOU=StringUtil.split(IMPORT_INDIRECT_COLUMN_LIST, SYMBOL_COMMA);
		 Map<String, Object> contextOfUseMap;
         while (itr.hasNext()) {
               contextOfUseMap = (Map) itr.next();
               inputs.addContent(new XMLElement((String)columnMap.get(CONSTANT_MINOR), StringHelper.removeAllSpacesFromElementNameTag(contextOfUseMap), slDirectICOU));
               if(slIndirectCategories.contains(_category)){
               inputsIndirect.addContent(new XMLElement(TABLE_IMPORT_COU_INDIRECT_MINOR, StringHelper.removeAllSpacesFromElementNameTag(contextOfUseMap), slInDirectICOU));
               }
         }
         _element.addContent(inputs);
         if(slIndirectCategories.contains(_category)){
         _element.addContent(inputsIndirect);
         }
    }
	//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Ends

	/**
	 * @Desc Method used to generate PDF file
	 * @param context - Context
	 * @param formsDataMap - Map
	 * @param fieldsMap - Map
	 * @return void
	 * @throws Exception
	 * @since - 18x.3
	 */
	public void createFormXMLElement(Context context, Map formsDataMap, Map fieldsMap) throws Exception {
		
		StringList fieldsList = (StringList)fieldsMap.get(CONSTANT_FIELDS);
		String majorElement = (String)fieldsMap.get(CONSTANT_MAJOR);
		String formName = (String)fieldsMap.get(CONSTANT_NAME);
		
		// create top node - add form sections information to the top node.
		Map sectionMap = (Map)fieldsMap.get(CONSTANT_SECTIONS);
		if (sectionMap != null && sectionMap.size() > 0) {
			sectionMap.put(ELEMENT_ATTRIBUTE_CATEGORY, _category);
			_element = new XMLElement(ELEMENT_GPSASSESSMENT, sectionMap);
		}
		
		Map dataMap = (Map)formsDataMap.get(formName);
		if (dataMap != null && dataMap.size() > 0) {
			Element input = new XMLElement(majorElement, StringHelper.removeAllSpacesFromElementNameTag(dataMap), fieldsList);
			_element.addContent(input);
		}
	} 
	/**
	 * @Desc Method used to generate PDF file
	 * @param context - Context
	 * @param configMap - Map
	 * @param tableDataMap - Map
	 * @param formDataMap - Map
	 * @return void
	 * @throws Exception
	 * @since - 18x.3
	 */
	public void buildXML(Context context, Map configMap, Map tableDataMap, Map formDataMap) throws Exception {
		try {
			Map formsMap = (Map)configMap.get(PDFConstants.CONSTANT_FORMS);
			for (Object key : formsMap.keySet()) {
				createFormXMLElement(context, formDataMap, (Map)formsMap.get(key.toString()));
			}
			Map tablesMap = (Map)configMap.get(PDFConstants.CONSTANT_TABLES);
			for (Object key : tablesMap.keySet()) {
				createTableXMLElement(context, tableDataMap, (Map)tablesMap.get(key.toString()));
			}
		} catch(Exception e) {
			throw e;
		}
	}

	/**
	 * @Desc Method used to generate PDF file
	 * @param context
	 * @param args
	 * @param jsonObjectMap -JSON Map
	 * @param tableDataMap  -particular table map
	 * @param formDataMap   -particular table map
	 * @return -string generated file path
	 * @throws Exception
	 */
	public String generate(Context context, String[] args, Map configMap, Map tableDataMap, Map formDataMap)
			throws Exception {
		String id = args[0];
		DomainObject busObj = DomainObject.newInstance(context);
		busObj.setId(id);

		StringBuffer workDir = new StringBuffer();
		//22x Upgrade Modification Start
		workDir.append(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH));
		workDir.append(File.separator);
		workDir.append(PDFConstants.CONST_PDF_BASE_FOLDER);
		//22x Upgrade Modification End
		workDir.append(File.separator);
		workDir.append(PDFConstants.CONSTANT_FOLDER_FONTS);
		workDir.append(File.separator);
		
		System.out.println("________________________________IRM PDF XML configMap:-->"+configMap);
		System.out.println("________________________________IRM PDF XML tableDataMap:-->"+tableDataMap);
		System.out.println("________________________________IRM PDF XML formDataMap:-->"+formDataMap);
		
		buildXML(context, configMap, tableDataMap, formDataMap);

		// build payload for iText for PDF generation
		return buildPayLoadCalliText(context, args, _category, workDir.toString());
	}
	
	/**
	 * @Desc Method used to read xls page file.
	 * @param context
	 * @throws Exception
	 */
	public String readXSLPage(Context context) throws Exception {
		String out = DomainConstants.EMPTY_STRING;
		try {
			Page page = new Page(CONSTANT_GPS_TASK_XSL_PAGE);
			page.open(context);
			out = page.getContents(context);
			page.close(context);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return out;
	}
	/**
	 * @Desc Method used to generate PDF file
	 * @param context
	 * @param args
	 * @param category
	 * @param workDir  -working directory path
	 * @return -string generated file path
	 * @throws Exception
	 */
	public String buildPayLoadCalliText(Context context, String[] args, String category, String workDir) throws Exception {

		String id = args[0];
		String view = args[1];

		Document doc = new Document();
		doc.setRootElement(_element);

		System.out.println(
				"-------------------------------- IRM PDF XML Data Content Start-----------------------------");
		XMLOutputter outputtter = new XMLOutputter();
		String output = outputtter.outputString(doc);
		System.out.println(output);
		System.out.println(
				"-------------------------------- IRM PDF XML Data Content End-------------------------------");
		
		String htmlCodeData = generateHTML(context, doc);
		String pdfFileName = CONSTANT_PDF_FILE_PREFIX + Long.toString(System.currentTimeMillis())
		+ CONSTANT_PDF_FILE_EXTENSION;
		Map<String, String> pdfPayLoad = new HashMap<String, String>();
		DomainObject busObj = DomainObject.newInstance(context);
		busObj.setId(id);
		pdfPayLoad = busObj.getAttributeMap(context);
		pdfPayLoad.putAll(busObj.getInfo(context, GPSTaskUtil.getSelectableForAttributesTable()));
		pdfPayLoad.put(CONSTANT_VIEW_KIND, view.trim());
		pdfPayLoad.put(CONSTANT_TYPE_KIND, GPS_ASSESSMENT_REQUEST_CATEGORY);
		pdfPayLoad.put(CONSTANT_REQUEST_CATEGORY, category);

		Map argMap = new HashMap();
		argMap.put(DomainConstants.SELECT_ID, id);
		argMap.put(CONSTANT_HTML_DATA, htmlCodeData);
		argMap.put(CONSTANT_PDF_FILE_NAME, pdfFileName);
		argMap.put(CONSTANT_PDF_PAY_LOAD, pdfPayLoad);
		argMap.put(CONSTANT_VIEW_KIND, view.trim());
		argMap.put(CONSTANT_TYPE_KIND, GPS_ASSESSMENT_REQUEST_CATEGORY);
		argMap.put(CONSTANT_REQUEST_CATEGORY, category);
		argMap.put(CONST_WORK_DIR, workDir);

		String[] strArgs = JPO.packArgs(argMap);

		String pdfFile = EMPTY_STRING;
		try {
			ITextUtil itext = new ITextUtil(context, strArgs);
			pdfFile = (String) itext.createPdf(context, strArgs, true);
			System.out.println("Final IRM PDF File name is:" + pdfFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdfFile;
	}

	/**
	 * @Desc Method used to generate html from xml and xsl documents
	 * @param context
	 * @param doc         -xml document
	 * @param xslFilePath -xsl file path
	 * @return -string html data
	 * @throws Exception
	 */
	public String generateHTML(Context context, Document doc) throws Exception {
		return transformXMLToHTML(doc, readXSLPage(context));
	}

	/**
	 * @Desc Method used to get value from map based on input key
	 * @param map
	 * @param key
	 * @return -string value based on input key
	 * @throws Exception
	 */
	public static String getValueFromMap(Map<String, String> map, String key) throws Exception {
		String val = (String) map.get(key);
		return val == null ? "" : val.trim();
	}

	/**
	 * @Desc Method used to transform XML data into HTML
	 * @param doc         -xml document
	 * @param xslFilePath -xsl file path
	 * @return -string generated html data
	 * @throws Exception
	 */
	public String transformXMLToHTML(Document doc, String xslFilePath) throws Exception {
		XSLTransformer transformer = new XSLTransformer(new StringReader(xslFilePath));
		Document resultDoc = transformer.transform(doc);
		XMLOutputter outputtter = new XMLOutputter();
		String output = outputtter.outputString(resultDoc);
		return output;
	}
}
