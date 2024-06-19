package com.pg.irm.pdf.views;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.input.SAXBuilder;
import com.pg.irm.pdf.PDFConstants;
import com.pg.irm.pdf.util.StringHelper;

import matrix.db.Context;
import matrix.db.Page;
import matrix.util.StringList;

public class GPSTaskCategory implements PDFConstants {

	private Context _context = null;
	private String _category = DomainConstants.EMPTY_STRING;
	public GPSTaskCategory(Context context, String category) {
		this._category = category;
		this._context = context;
	}

	/**
	 * @Desc Method to read page.
	 * @param context
	 * @return - string 
	 * @throws Exception
	 */
	private String readPage(Context context) throws Exception {
		String out = DomainConstants.EMPTY_STRING;
		Page page = null;
		try {
			page = new Page(CONSTANT_GPS_TASK_XML_PAGE);
			page.open(context);
			out = page.getContents(context);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(page.isOpen())
				page.close(context);
		}
		return out;
	}
	/**
	 * @Desc Method to parse columns.
	 * @param Element
	 * @return - Stringlist 
	 * @throws Exception
	 */
	private StringList parseColumns(Element elements) throws Exception {
		StringList columnList = new StringList(); 
		Iterator<Element> tablesItr = elements.getChildren().iterator();
		while(tablesItr.hasNext()) {
			Element table = (Element)tablesItr.next();
			String hide = table.getAttribute(CONSTANT_HIDE).getValue();
			if(UIUtil.isNotNullAndNotEmpty(hide)) {
				if(CONSTANT_FALSE.equals(hide)) 
					columnList.add(StringHelper.removeAllSpaces(table.getAttribute(CONSTANT_NAME).getValue()));
			}
		}
		return columnList;
	}
	/**
	 * @Desc Holder to parse table.
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	private Map parseTable(Element element) throws Exception {
		Map columnMap = new HashMap();
		columnMap.put(CONSTANT_NAME, element.getAttribute(CONSTANT_NAME).getValue());
		columnMap.put(CONSTANT_PROGRAM, element.getAttribute(CONSTANT_PROGRAM).getValue());
		columnMap.put(CONSTANT_DISPLAY, element.getAttribute(CONSTANT_DISPLAY).getValue());
		columnMap.put(CONSTANT_MAJOR, element.getAttribute(CONSTANT_MAJOR).getValue());
		columnMap.put(CONSTANT_MINOR, element.getAttribute(CONSTANT_MINOR).getValue());
		columnMap.put(CONSTANT_COLUMNS, parseColumns(element.getChild(CONSTANT_COLUMNS)));
		return columnMap;
	}
	/**
	 * @Desc Intermediate Holder to parse table.
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	private Map parseTables(Element elements) throws Exception {
		Map tableMap = new HashMap();
		List<Element> tables = elements.getChildren();
		Iterator<Element> tablesItr = tables.iterator();
		while(tablesItr.hasNext()) {
			Element table = (Element)tablesItr.next();
			tableMap.put(table.getAttribute(CONSTANT_NAME).getValue(), parseTable(table));
		}
		return tableMap;
	}
	/**
	 * @Desc Intermediate Holder to parse form sections.
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	private Map parseFormSections(Element elements) throws Exception {
		Map sectionMap = new HashMap(); 
		Iterator<Element> tablesItr = elements.getChildren().iterator();
		while(tablesItr.hasNext()) {
			Element element = (Element)tablesItr.next();
			String name = element.getAttribute(CONSTANT_NAME).getValue();
			String categories = element.getAttribute(CONSTANT_CATEGORIES).getValue();
			String hide = element.getAttribute(CONSTANT_HIDE).getValue();
			if(UIUtil.isNotNullAndNotEmpty(categories)) {
				if(PDFConstants.CONSTANT_FALSE.equals(hide)) 
					sectionMap.put(name, categories);
			}
		}
		return sectionMap;
	}
	/**
	 * @Desc Intermediate Holder to parse form fields.
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	private StringList parseFormFields(Element elements) throws Exception {
		StringList fieldList = new StringList(); 
		Iterator<Element> tablesItr = elements.getChildren().iterator();
		while(tablesItr.hasNext()) {
			Element element = (Element)tablesItr.next();
			String hide = element.getAttribute(CONSTANT_HIDE).getValue();
			if(UIUtil.isNotNullAndNotEmpty(hide)) {
				if(CONSTANT_FALSE.equals(hide)) 
					fieldList.add(StringHelper.removeAllSpaces(element.getAttribute(CONSTANT_NAME).getValue()));
			}
		}
		return fieldList;
	}
	/**
	 * @Desc Intermediate Holder to parse form
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	private Map parseForm(Element element) throws Exception {
		Map fieldMap = new HashMap();
		fieldMap.put(CONSTANT_NAME, element.getAttribute(CONSTANT_NAME).getValue());
		fieldMap.put(CONSTANT_DISPLAY, element.getAttribute(CONSTANT_DISPLAY).getValue());
		fieldMap.put(CONSTANT_MAJOR, element.getAttribute(CONSTANT_MAJOR).getValue());
		fieldMap.put(CONSTANT_FIELDS, parseFormFields(element.getChild(CONSTANT_FIELDS)));
		fieldMap.put(CONSTANT_SECTIONS, parseFormSections(element.getChild(CONSTANT_SECTIONS)));
		return fieldMap;
	}
	/**
	 * @Desc Intermediate Holder to parse form.
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	private Map parseForms(Element elements) throws Exception {
		Map formMap = new HashMap();
		List<Element> forms = elements.getChildren();
		Iterator<Element> formsItr = forms.iterator();
		while(formsItr.hasNext()) {
			Element form = (Element)formsItr.next();
			formMap.put(form.getAttribute(CONSTANT_NAME).getValue(), parseForm(form));
		}	
		return formMap;
	}

	/**
	 * @Desc Helper method to parse enovia page file
	 * @param Element
	 * @return - Map 
	 * @throws Exception
	 */
	public Map loadConfigPage() throws Exception {
		Map categoryMap = new HashMap();
		Map contentMap = new HashMap();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document docXML = builder.build(new StringReader(readPage(_context)));
			Element categories = docXML.getRootElement();

			Map tablesMap = new HashMap();
			Map formsMap = new HashMap();

			List<Element> categoriesChilds = categories.getChildren();
			Iterator<Element> categoriesChildsItr = categoriesChilds.iterator();
			while(categoriesChildsItr.hasNext()) {
				Element category = (Element)categoriesChildsItr.next();
				String name = category.getName();
				if(CONSTANT_TABLES.equalsIgnoreCase(name)) {
					tablesMap.put(CONSTANT_TABLES, parseTables(category));
				}
				if(CONSTANT_FORMS.equalsIgnoreCase(name)) {
					formsMap.put(CONSTANT_FORMS, parseForms(category));
				}
			}
			contentMap.putAll(tablesMap);
			contentMap.putAll(formsMap);

			formsMap = null;
			tablesMap = null;

			categoryMap.put(_category, contentMap);
		} catch(Exception e) {
			e.printStackTrace();
		}
		contentMap = null;
		return categoryMap;
	}	
}
