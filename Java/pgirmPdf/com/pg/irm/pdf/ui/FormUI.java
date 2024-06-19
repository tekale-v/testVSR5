/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: FormUI
   Purpose: JAVA class created to get form data.
 **/
package com.pg.irm.pdf.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIForm;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.irm.pdf.PDFConstants;
import com.pg.irm.pdf.util.StringHelper;

import matrix.db.Context;
import matrix.util.StringList;

public class FormUI implements PDFConstants, Serializable {
	private static final long serialVersionUID = -4891962759711072339L;

	/**
	 * @Desc Method to get form data in map format
	 * @param _ctx
	 * @param _appCtx      - jsp ServletContext
	 * @param _pageCtx     -jsp PageContext
	 * @param _servletReq  -jsp page request HttpServletRequest
	 * @param _timeStamp
	 * @param _languageStr
	 * @param programMap
	 * @param objectId     -object id
	 * @return table data in map format
	 */
	public static Map getFormDataMap(Context _ctx, ServletContext _appCtx, PageContext _pageCtx,
			HttpServletRequest _servletReq, String _timeStamp, String _languageStr, Map formsMap, String objectId) {
		Map<String, Map> formDataMap = new HashMap<String, Map>();
		try {
			if (formsMap != null) {
				Map formMap = null;
				HashMap pageMap = UINavigatorUtil.getRequestParameterMap(_pageCtx);
				//Added by IRM team in 2018x.6 for the Defect #39830 Starts
				StringList attributeSelects = new StringList();
				attributeSelects.addElement(PDFConstants.SELECT_ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA);
				attributeSelects.addElement(PDFConstants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS);
				attributeSelects.addElement(PDFConstants.SELECT_ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE);
				attributeSelects.addElement(PDFConstants.SELECT_ATTRIBUTE_PGOTHERDETAILS);

				DomainObject dObj = DomainObject.newInstance(_ctx);
				Map formData;
				Map<Object, Object> additionalFormData;
				//Added by IRM team in 2018x.6 for the Defect #39830 Ends
				for (Object key : formsMap.keySet()) {
					formMap = (Map) formsMap.get(key.toString());
					String form = (String) formMap.get(CONSTANT_NAME);
					pageMap.put(CONSTANT_FORM, form);
					pageMap.put(CONSTANT_OBJECT_ID, objectId);
					pageMap.put(CONST_MODE, CONST_VIEW_MODE);
				//Modified by IRM team in 2018x.6 for the Defect #39830 Starts
					formData = getFormData(_ctx, _appCtx, _pageCtx, _servletReq, _timeStamp, _languageStr,
							form, pageMap);
					dObj.setId(objectId);
					additionalFormData = dObj.getInfo(_ctx, attributeSelects);
					formData.put("pgFinishedProductImportedAs", (String)additionalFormData.get(PDFConstants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS));
					formData.put("pgIsMaterialClassifiedMixtureorSubstance", (String)additionalFormData.get(PDFConstants.SELECT_ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE));
					formData.put("pgIsMaterialImportedIntoEEA", (String)additionalFormData.get(PDFConstants.SELECT_ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA));
					formData.put("pgOtherDetails", (String)additionalFormData.get(PDFConstants.SELECT_ATTRIBUTE_PGOTHERDETAILS));
					
					formDataMap.put(key.toString(), formData);
					//Added by IRM team in 2018x.6 for the Defect #39830 Ends
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formDataMap;
	}


	/**
	 * @Desc Method to get form data in Map format
	 * @param _ctx
	 * @param _appCtx      - jsp ServletContext
	 * @param _pageCtx     -jsp PageContext
	 * @param _servletReq  -jsp page request HttpServletRequest
	 * @param _timeStamp   -
	 * @param _languageStr
	 * @param table
	 * @param program
	 * @param programMap
	 * @param objectId     -object id
	 * @return form data in MapList format
	 */
	public static Map getFormData(Context _ctx, ServletContext _appCtx, PageContext _pageCtx,
			HttpServletRequest _servletReq, String _timeStamp, String _languageStr, String form,
			HashMap pageMap) throws Exception {

		Map columnValueMap = new HashMap();
		String languageStr = _servletReq.getHeader(CONSTANT_ACCEPT_LANGUAGE);
		UIForm formViewBean = new UIForm();
		try {
			HashMap<?,?> formMap = UIForm.getForm(_ctx, form);
			if(formMap != null) {
				HashMap<?,?> formData = formViewBean.setFormData(pageMap, _ctx, _timeStamp, PersonUtil.getAssignments(_ctx), false);
				MapList fields = formViewBean.getFormFields(formData);
				HashMap fieldTemp = null;
				for (int j = 0; j < fields.size(); j++) {
					fieldTemp = (HashMap) fields.get(j);
					String strTempaccess = (String) fieldTemp.get(CONSTANT_HAS_ACCESS);
					if (formViewBean.isGroupHolderField(fieldTemp) || formViewBean.isGroupField(fieldTemp)) {
						if (strTempaccess != null && !EMPTY_STRING.equals(strTempaccess) && strTempaccess.equalsIgnoreCase(CONSTANT_FALSE))
							fields.remove(fieldTemp);
					}
				}
				int fieldSize = fields.size();
				HashMap field = null;
				StringList fieldValueList = null;
				for (int i = 0; i < fieldSize; i++) {
					field = (HashMap) fields.get(i);
					if (!formViewBean.hasAccessToFiled(field))
						continue;
					String fieldAdminType = UIForm.getSetting(field, CONST_ADMIN_TYPE);
					String isAssociatedWithUOM = getValue(field, CONST_ASSOCIATED_WITH_UOM);
					String fieldName = UIForm.getName(field);
					String fieldValue = EMPTY_STRING;
					fieldValueList = formViewBean.getFieldValues(field);
					if (fieldValueList != null && fieldValueList.size() > 0) {
						fieldValue = (String) fieldValueList.firstElement();
						fieldValue = StringHelper.getHrefRemovedData(fieldValue);
						if (UIUtil.isNotNullAndNotEmpty(fieldAdminType)) 
							fieldValue = getInternationalizedDefaultValue(_ctx, fieldValue, fieldAdminType, languageStr,null);
						if(UIUtil.isNotNullAndNotEmpty(isAssociatedWithUOM) && Boolean.valueOf(isAssociatedWithUOM)) 
							fieldValue = getValue(field, CONST_UBOM_DISPLAY_VALUE);
					}
					//Added BY IRM Team in 2018x.5 for Defect 35339 Starts
					String fieldFormat = UIForm.getSetting(field, "format");
					if("user".equalsIgnoreCase(fieldFormat)) {
						fieldValue = PersonUtil.getFullName(_ctx, fieldValue);
					}
					//Added BY IRM Team in 2018x.5 for Defect 35339 Ends
					columnValueMap.put(fieldName, fieldValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columnValueMap;
	}

	/**
	 * @Desc Method to get actual name based on Enovia Resource Bundle
	 * @param paramContext
	 * @param fieldAdminTypeAndValue -Admin type and value combination
	 * @param fieldAdminType         -Admin type (type,policy,...etc)
	 * @param languageStr            -language code
	 * @param paramString4
	 * @return string
	 * @throws Exception
	 */
	public static String getInternationalizedDefaultValue(Context paramContext, String fieldValue,
			String fieldAdminType, String languageStr, String paramString4) throws Exception {
		String fieldAdminTypeAndValue=fieldAdminType.toLowerCase() + CONST_UNDERSCORE + fieldValue;
		String str = EMPTY_STRING;
		fieldAdminTypeAndValue = PropertyUtil.getSchemaProperty(paramContext, fieldAdminTypeAndValue.trim());
		if (CONST_TYPE.equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getTypeI18NString(paramContext, fieldAdminTypeAndValue, languageStr);
		} else if (CONST_ATTRIBUTE.equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getAttributeI18NString(paramContext, fieldAdminTypeAndValue, languageStr);
		}
		/*
		 * else if ("State".equalsIgnoreCase(paramString2)) { str =
		 * EnoviaResourceBundle.getStateI18NString(paramContext, paramString4,
		 * paramString1, paramString3); }
		 */
		else if (CONST_ROLE.equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getRoleI18NString(paramContext, fieldAdminTypeAndValue, languageStr);
		} else if (CONST_POLICY.equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getMXI18NString(paramContext, fieldAdminTypeAndValue, EMPTY_STRING, languageStr, CONST_POLICY);
		} else if (CONST_GROUP.equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getMXI18NString(paramContext, fieldAdminTypeAndValue, EMPTY_STRING, languageStr, CONST_GROUP);
		} else if (CONST_VAULT.toLowerCase().equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getMXI18NString(paramContext, fieldAdminTypeAndValue, EMPTY_STRING, languageStr, CONST_VAULT);
		} else if (CONST_RELATIONSHIP.equalsIgnoreCase(fieldAdminType)) {
			str = EnoviaResourceBundle.getMXI18NString(paramContext, fieldAdminTypeAndValue, EMPTY_STRING, languageStr,
					CONST_RELATIONSHIP);
		}else {
			str=fieldValue;
		}
		return str;
	}

	static String getValue(Map paramMap, String paramString) {
		String str = (String)paramMap.get(paramString);
		return (str == null) ? "" : str.trim();
	}
}