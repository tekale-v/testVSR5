package com.pg.dsm.ui.form;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIComponent;
import com.matrixone.apps.framework.ui.UIForm;
import com.pg.dsm.ui.table.helper.StringHelper;

import matrix.db.Context;
import matrix.util.StringList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormUI {

	/**
	 * 
	 */
	String timeStamp ;
	String languageStr="en-US,en;q=0.9,mr;q=0.8";
	private Context ctx;
	private String objectId;
	private String program;
	private String form;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	public FormUI(Context context,String objectId) {
		this.ctx = context;
		this.timeStamp = com.matrixone.apps.framework.ui.UIComponent.getTimeStamp();
		this.objectId = objectId;
	}


	@SuppressWarnings("unchecked")
	public  Map<String, String> getFormData(Map<String,String> otherInputParameters) {
		
		HashMap<String,String> pageMap = new HashMap<>();
		pageMap.put("form", form);
		pageMap.put("objectId", objectId);
		pageMap.put("program", program);
		pageMap.put("languageStr",languageStr);
		pageMap.putAll(otherInputParameters);
		Map<String,String> columnValueMap = new HashMap<>();
		
		HashMap<String,String> formData = null;
		

		UIForm uiT = new UIForm();
		try {
			uiT.setFormData(pageMap, ctx, timeStamp, PersonUtil.getAssignments(ctx), false);
			formData = uiT.getFormData(timeStamp);
			MapList fields = uiT.getFormFields(formData);
			int iFieldssize=fields.size();
			for (int j = 0; j < iFieldssize; j++) {
				HashMap<String,String> fieldTemp = (HashMap<String,String>) fields.get(j);
				String strTempaccess = fieldTemp.get("hasAccess");
					if ((uiT.isGroupHolderField(fieldTemp) || uiT.isGroupField(fieldTemp)) && strTempaccess != null && !"".equals(strTempaccess) && "false".equalsIgnoreCase(strTempaccess))
						fields.remove(fieldTemp);
			}
			iFieldssize=fields.size();
			String fieldName;
			String fieldValue;
			StringList fieldValueList;
			for (int i = 0; i < iFieldssize; i++) {
				HashMap<String,String> field = (HashMap<String,String>) fields.get(i);
				if (!uiT.hasAccessToFiled(field))
					continue;

				fieldName = UIComponent.getName(field);


				fieldValueList = uiT.getFieldValues(field);			
				if (fieldValueList != null && !fieldValueList.isEmpty()) {
					fieldValue = fieldValueList.firstElement();
					fieldValue=StringHelper.getHrefRemovedData(fieldValue);
					columnValueMap.put(fieldName, fieldValue);
				}
				
			
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		return columnValueMap;
	}


	public String getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}


	public String getLanguageStr() {
		return languageStr;
	}


	public void setLanguageStr(String languageStr) {
		this.languageStr = languageStr;
	}


	public String getProgram() {
		return program;
	}


	public void setProgram(String program) {
		this.program = program;
	}


	public String getForm() {
		return form;
	}


	public void setForm(String form) {
		this.form = form;
	}

}