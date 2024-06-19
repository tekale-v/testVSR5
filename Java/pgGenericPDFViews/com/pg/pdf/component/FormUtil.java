package com.pg.pdf.component;

import java.util.List;
import java.util.Map;

import matrix.db.Context;

import com.pg.pdf.bean.input.xml.Field;
import com.pg.pdf.bean.input.xml.Table;
import com.pg.pdf.bean.output.xml.OutPutXMLSections;
import com.pg.pdf.enumerations.PDFConstants;

import java.util.logging.Logger;

public class FormUtil extends PDFConstants{	
	Context context;
	public FormUtil(Context context){
		this.context = context;
	}
	public FormUtil(){
		
	}
	private  final Logger logger = Logger.getLogger(FormUtil.class.getName());
	public OutPutXMLSections processFormFields(Table objTable, Context context, OutPutXMLSections newSection, Map<String, String> requestMap) {
		logger.info("Enter  FormUtil -method processFormFields");
		List<Field> listofField = objTable.getFields().get(0).getFields();
		Component com = new Component(context);
		newSection = com.getFieldValuesinSection(context,listofField,newSection,requestMap,CONSTANT_FORM);
		logger.info("Exit  FormUtil -method processFormFields");
		return newSection;
	}
}
