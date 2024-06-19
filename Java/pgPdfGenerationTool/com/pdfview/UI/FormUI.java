package com.pdfview.UI;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIForm;
import com.pdfview.helper.StringHelper;

import matrix.db.Context;
import matrix.util.StringList;

public class FormUI implements Serializable {
	private static final long serialVersionUID = -4891962759711072339L;

	public static Map getFormData(Context _ctx, String form, String program,String objectId) throws Exception {
		String _timeStamp = com.matrixone.apps.framework.ui.UITable.getTimeStamp();
		String _languageStr="en-US,en;q=0.9,mr;q=0.8";
		HashMap pageMap = new HashMap();
		pageMap.put("form", form);
		pageMap.put("objectId", objectId);
		pageMap.put("program", program);
		pageMap.put("languageStr",_languageStr);
		Map columnValueMap = new HashMap();
		System.out.println("_ctx User:" + _ctx.getUser());
		Vector userRoleList = PersonUtil.getAssignments(_ctx);
		HashMap formData = null;
		
		String transactionType = "NO";
		boolean updateTransaction = (transactionType != null && transactionType.equalsIgnoreCase("update"));
		String header = "";

		UIForm uiT = new UIForm();
		try {

			ContextUtil.startTransaction(_ctx, updateTransaction);
			formData = uiT.setFormData(pageMap, _ctx, _timeStamp, userRoleList, false);
			formData = uiT.getFormData(_timeStamp);
			MapList fields = uiT.getFormFields(formData);
			int maxCols = ((Integer) formData.get("max cols")).intValue();
			int iFieldssize=fields.size();
			for (int j = 0; j < iFieldssize; j++) {
				HashMap fieldTemp = (HashMap) fields.get(j);
				String strTempaccess = (String) fieldTemp.get("hasAccess");
				if (uiT.isGroupHolderField(fieldTemp) || uiT.isGroupField(fieldTemp)) {
					if (strTempaccess != null && !"".equals(strTempaccess) && strTempaccess.equalsIgnoreCase("false"))
						fields.remove(fieldTemp);
				}
			}
			iFieldssize=fields.size();
			for (int i = 0; i < iFieldssize; i++) {
				HashMap field = (HashMap) fields.get(i);
				if (!uiT.hasAccessToFiled(field))
					continue;

				String fieldName = uiT.getName(field);
				String fieldLabel = uiT.getFieldLabel(field);
				String fieldValue = "";
				String fieldValueDisplay = "";

				StringList objectIcons = new StringList();
				StringList fieldHrefList = new StringList();

				StringList fieldValueList = uiT.getFieldValues(field);
				StringList fieldValueDisplayList = uiT.getFieldDisplayValues(field);
				
				if (fieldValueList != null && !fieldValueList.isEmpty()) {
					fieldValue = (String) fieldValueList.firstElement();
					fieldValue=StringHelper.getHrefRemovedData(fieldValue);
					columnValueMap.put(fieldName, fieldValue);
				}
				
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnValueMap;
	}

}