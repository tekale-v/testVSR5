package com.pg.table.util;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.cpd.util.JsonHelper;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;


import matrix.db.Context;
import matrix.db.Page;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GenerateTables implements Serializable{
	
	private List<Field> projectDetailsTable;
	private List<Field> peopleTable;
	private List<Field> preRouteTable;
	private  static String sPageFileContents;

	 public class Field {
		   private String field;
	       private String id;
	       private String Label;
	       private String Type;
	       private String Val;
	       private String Property;
	       private String sClass;
	       private String method;
	       private String event;
	       private String createElement;
	       private String createChooser;
	       private String name;
	       private String massUpdate;
		   private String autoName;
		   private String showCheckboxForHeader;
		   private String width;
		   private String trigger;
		   //Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38996  - Starts
		   private String maxlength;
		   //Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38996  - Ends
	    
	    public String getShowCheckboxForHeader() {
			return showCheckboxForHeader;
		}
		public void setShowCheckboxForHeader(String showCheckboxForHeader) {
			this.showCheckboxForHeader = showCheckboxForHeader;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCreateChooser() {
			return createChooser;
		}
		public void setCreateChooser(String createChooser) {
			this.createChooser = createChooser;
		}
		public String getEvent() {
			return event;
		}
		public void setEvent(String event) {
			this.event = event;
		}
		public String getCreateElement() {
			return createElement;
		}
		public void setCreateElement(String createElement) {
			this.createElement = createElement;
		}
	       public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getLabel() {
			return Label;
		}
		public void setLabel(String label) {
			this.Label = label;
		}
		public String getType() {
			return Type;
		}
		public void setType(String type) {
			this.Type = type;
		}
		public String getVal() {
			return Val;
		}
		public void setVal(String val) {
			this.Val = val;
		}
		public String getProperty() {
			return Property;
		}
		public void setProperty(String property) {
			this.Property = property;
		}
		public String getsClass() {
			return sClass;
		}
		public void setsClass(String Class) {
			this.sClass = Class;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getMassUpdate() {
			return massUpdate;
		}
		public void setMassUpdate(String massUpdate) {
			this.massUpdate = massUpdate;
		}
		public String getAutoName() {
			return autoName;
		}
		public void setAutoName(String autoName) {
			this.autoName = autoName;
		}
		public String getWidth() {
			return width;
		}
		public void setWidth(String width) {
			this.width = width;
		}
		public String getTrigger() {
			return trigger;
		}
		public void setTrigger(String trigger) {
			this.trigger = trigger;
		}
		//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38996  - Starts
		public String getMaxlength() {
			return maxlength;
		}
		public void setMaxlength(String trigger) {
			this.maxlength = maxlength;
		}
		//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38996  - Ends
		Field() {
	       }
	 }
	 private  class ParseMultiGenXML extends DefaultHandler {
	        private boolean forProjectDetailsTable;
	        List<Field> projectDetailsTable;
	        private boolean forPeopleTable;
	        List<Field> peopleTable;
	        private boolean forPreRouteMember;
	        List<Field> preRouteTable;
	        private Context context;
	      
	        private ParseMultiGenXML(Context context) {
	        	this.context = context;
	            this.projectDetailsTable = new ArrayList();
	            this.peopleTable = new ArrayList();
	            this.preRouteTable = new ArrayList();
	        }

	        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
	            boolean z = true;
	            this.forProjectDetailsTable = "ProjectDetails".equals(str3) ? true : this.forProjectDetailsTable;
	            this.forPeopleTable = "People".equals(str3) ? true : this.forPeopleTable;
	            this.forPreRouteMember = "PreRoute".equals(str3) ? true : this.forPreRouteMember;

	            if ("column".equals(str3)) {
	                Field field = new Field();
	                field.field = attributes.getValue("field");
	                field.id = attributes.getValue("id");
	                field.Label = BusinessUtil.isNotNullOrEmpty(attributes.getValue("label"))?EnoviaResourceBundle.getProperty(context, "emxProgramCentralStringResource",context.getLocale(), attributes.getValue("label")):attributes.getValue("label");
	                field.Type = attributes.getValue("type");
	                field.Val = attributes.getValue("value");
	                field.Property = attributes.getValue("property");
	                field.sClass = attributes.getValue("class");
	                field.method = attributes.getValue("method");
	                field.createElement = attributes.getValue("createElement");
	                field.event = attributes.getValue("event");
	                field.createChooser = attributes.getValue("createChooser");
	                field.name = attributes.getValue("name");
	                field.massUpdate = attributes.getValue("massUpdate");
					field.autoName = attributes.getValue("autoName");
					field.showCheckboxForHeader = attributes.getValue("showCheckboxForHeader");
					field.width = attributes.getValue("width");
					field.trigger = attributes.getValue("trigger");
					//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38996  - Starts
					field.maxlength = attributes.getValue("maxlength");
					//Added by RTA Capgemini Offshore for 18x.6-OctCW Req 38996  - Ends
	                if (this.forProjectDetailsTable) {
	                    this.projectDetailsTable.add(field);
	                }else if (this.forPeopleTable) {
	                    this.peopleTable.add(field);
	                }else if (this.forPreRouteMember) {
	                    this.preRouteTable.add(field);
	                }		
	            }
	        }
	        public void endElement(String str, String str2, String str3) throws SAXException {
	            
	            this.forProjectDetailsTable = "ProjectDetails".equals(str3) ? false : this.forProjectDetailsTable;
	            this.forPeopleTable = "People".equals(str3) ? false : this.forPeopleTable;
	            this.forPreRouteMember = "PreRoute".equals(str3) ? false : this.forPreRouteMember;
	        }
	        public List<Field> getProjectDetailsTable() {
	            return this.projectDetailsTable;
	        }
	        public List<Field> getPeopleTable() {
	            return this.peopleTable;
	        }
	        public List<Field> getpreRouteTable() {
	            return this.preRouteTable;
	        }
	    }

	 private  void loadTableConfig(Context context) throws Exception {
	           Page page = new Page("pgMultiProjectUITablesConfig");
	           page.open(context);
	           sPageFileContents = page.getContents(context);
	           page.close(context);
	           ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sPageFileContents.getBytes());
	           SAXParser newSAXParser = SAXParserFactory.newInstance().newSAXParser();
	           ParseMultiGenXML parseXML = new ParseMultiGenXML(context);
	           newSAXParser.parse(byteArrayInputStream, parseXML);
	           this.projectDetailsTable = parseXML.getProjectDetailsTable();
	           this.peopleTable = parseXML.getPeopleTable();
	           this.preRouteTable = parseXML.getpreRouteTable();

	    }
	 public String getJSONForTable(Context context,String sTableName) throws Exception {
		 loadTableConfig(context);
		 LinkedHashMap  mJSON=null;
		
		if("projectDetailsTable".equals(sTableName)) {
			mJSON = convertJavaObjToJSON(this.projectDetailsTable);
		}else if("peopleTable".equals(sTableName)) {
			mJSON = convertJavaObjToJSON(this.peopleTable);
		}else if("PreRoute".equals(sTableName)) {
			mJSON = convertJavaObjToJSON(this.preRouteTable);
		}
		return new JsonHelper().getJsonString(mJSON);
	 }
	 private LinkedHashMap  convertJavaObjToJSON(Object iObject) {
		 LinkedHashMap  mJSON = new LinkedHashMap ();
		 mJSON.put("Column", iObject);
		 return mJSON; 
	 }
}
