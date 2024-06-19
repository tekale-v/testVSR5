package com.pg.pdf.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

import java.util.logging.Logger;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;

import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.pdf.bean.input.xml.Field;
import com.pg.pdf.bean.output.xml.OutPutFieldDetails;
import com.pg.pdf.bean.output.xml.OutPutXML;
import com.pg.pdf.bean.output.xml.OutPutXMLSections;
import com.pg.pdf.enumerations.PDFConstants;
import com.pg.pdf.util.PDFUtil;
import com.pg.v3.custom.pgV3Constants;

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
public class Component extends PDFConstants{
	Context sContext;
	public Component(Context context){
		this.sContext = context;
	}
	
	private  final Logger logger = Logger.getLogger(Component.class.getName());
	@SuppressWarnings("unchecked")
	public MapList fetchObjectDetails(Context context,String objectId,StringList slAttributeSelects){
		MapList mpObj = new MapList();
		try {
			mpObj= BusinessUtil.getInfoList(context, BusinessUtil.toStringList(objectId), slAttributeSelects);
			slAttributeSelects.clear();
			slAttributeSelects.setSize(0);
		}
		catch (Exception e) {
			logger.info("Exception occurred in Component -method fetchObjectDetails"+ e.getMessage());
			
		}
		return mpObj;
	}
	
	/**
	 * Added by IRM pdf views 2018x.6 Feb_CW for Requirements 40810
     * @param context
     * @param objectId
     * @param slAttributeSelects
     * @return
     */
    public Map<Object, Object> fetchObjectInfo(Context context, String objectId, StringList slAttributeSelects) {
        Map<Object, Object> retMap = null;
        try {
            retMap = BusinessUtil.getInfo(context, objectId, slAttributeSelects);
            slAttributeSelects.clear();
            slAttributeSelects.setSize(0);
        } catch (Exception e) {
            logger.info("Exception occurred in Component -method fetchObjectInfo" + e.getMessage());

        }
        return retMap;
    }

	public OutPutXMLSections getFieldValuesinSection(Context context,List<Field> listofField,OutPutXMLSections newSection,Map<String, String> hmArgMap, String constantTable){
		try{
			List<OutPutFieldDetails> outPutFieldsList = new ArrayList<>();
			StringList slAttributeSelects = new StringList();
			StringList slRelAttributeSelects = new StringList();
			String sFieldSelectable; 
			String sAllowedViews; 
			Map<String, Map<String, String>> hmRequestMap = new HashMap<>();
			hmRequestMap.put(CONST_PARAMMAP, hmArgMap);

			//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
                        String viewName = (String)hmArgMap.get(CONST_VIEW);
            //Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
			int fieldSize = listofField.size();
			for(int i=0;i<fieldSize;i++){
				Field objField = listofField.get(i);
				sFieldSelectable = objField.getFieldSelectable();
				sAllowedViews = objField.getFieldView();
				if(validateViews(hmArgMap.get(CONST_VIEW),sAllowedViews)){
					OutPutFieldDetails outPutFields = new OutPutFieldDetails();
					outPutFields.setFieldName(objField.getFieldName());
					if(sFieldSelectable.equalsIgnoreCase(CONSTANT_PROGRAM) && UIUtil.isNotNullAndNotEmpty(hmArgMap.get(CONSTANT_OBJECT_ID)) && constantTable.equalsIgnoreCase(CONSTANT_FORM)){
						String strFieldValue =  JPO.invoke(context, objField.getFieldProgram(), null, objField.getFieldMethod(),
								JPO.packArgs(hmRequestMap), String.class);
						if(UIUtil.isNotNullAndNotEmpty(objField.getPdfViewDisplayMethod())){
						PDFUtil pdfUtil = new PDFUtil(context,outPutFields);
						Method objColumnSetterMethod = pdfUtil.getClass().getDeclaredMethod(objField.getPdfViewDisplayMethod(), String.class);
						 objColumnSetterMethod.invoke(pdfUtil, strFieldValue);
						}
						else{
						outPutFields.setFieldValue(strFieldValue);
						}
					}else{
						if(!sFieldSelectable.equalsIgnoreCase(CONSTANT_PROGRAM)){
							sFieldSelectable = getRelationshipSelectable(objField,hmArgMap);

							//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
                            if(objField.isUsePushContext()) {
                                final String usePushContextOnWhichView = objField.getUsePushContextOnWhichView();
                                if(usePushContextOnWhichView.contains(viewName)) {
                                    outPutFields.setUsePushContext(Boolean.TRUE);
                                    outPutFields.setUsePushContextOnWhichView(usePushContextOnWhichView);
                                    outPutFields.setUsePushContextSelectValue(sFieldSelectable);
                                }
                            } //Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
							slAttributeSelects.add(sFieldSelectable);	
							outPutFields.setFieldSelectable(objField.getFieldSelectable());
							outPutFields.setPdfViewDisplayMethod(objField.getPdfViewDisplayMethod());
							outPutFields.setFieldValue(DomainConstants.EMPTY_STRING);
						}
					}
					outPutFields.setFieldId(hmArgMap.get(CONSTANT_OBJECT_ID));
					outPutFieldsList.add(outPutFields);
				}
			}
			setSectionSelctables(newSection,slAttributeSelects,slRelAttributeSelects);//need to check 
			newSection.setFieldDetails(outPutFieldsList);
		}catch(Exception e){
			logger.info("Exception occurred in Component -method getFieldValuesinSection"+ e.getMessage());
			
		}
		return newSection;
	}
	private void setSectionSelctables(OutPutXMLSections newSection,
			StringList slAttributeSelects, StringList slRelAttributeSelects) {
		int lSize=slAttributeSelects.size();
		int lRelSize=slRelAttributeSelects.size();
		if(!slAttributeSelects.isEmpty() && lSize>0){
			newSection.setSlSelectableList(slAttributeSelects);
		}
		if(!slRelAttributeSelects.isEmpty() && lRelSize>0){
			newSection.setSlRelSelectableList(slRelAttributeSelects);
		}
	}

	public String getRelationshipSelectable(Field objField,Map<String, String> hmArgMap){
		String sFieldSelectable=objField.getFieldSelectable();
		String sRelationshipSelectable = objField.getFieldrelationship();
		if(UIUtil.isNotNullAndNotEmpty(sRelationshipSelectable)){
			StringList slRelationshipSelectable=StringUtil.split(sRelationshipSelectable, SYMBOL_PIPE);
			sFieldSelectable = sFieldSelectable.replaceAll(slRelationshipSelectable.get(0),sRelationshipSelectable);
			int startIndex = sFieldSelectable.indexOf(SYMBOL_OPEN_FLOWER_BRACKET);
			int endIndex = sFieldSelectable.indexOf(SYMBOL_CLOSED_FLOWER_BRACKET);
			if(startIndex>0 && endIndex>0){
				sFieldSelectable = sFieldSelectable.replace(sFieldSelectable.substring(startIndex, endIndex+1),hmArgMap.get(sFieldSelectable.substring(startIndex+1, endIndex)));
			}
		}
		return sFieldSelectable;

	}
	public boolean validateViews(String formView,String strCategory){
		boolean isValidView = false;
		try {
			String strTempCategory;
			StringTokenizer stCategory = new StringTokenizer(strCategory, SYMBOL_PIPE);
			while (stCategory.hasMoreTokens()) {
				strTempCategory = stCategory.nextToken();
				if (formView.equalsIgnoreCase(strTempCategory)) {
					isValidView = true;
					break;
				}
			}
		} catch (Exception e) {
			logger.info("Exception occurred in Component -method validateViews"+ e.getMessage());
			
		}
		return isValidView;
	}
	public void fetchselectables(Context context,StringList slSelectableList,
			String objId, OutPutXML outPutXMLsections) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, FrameworkException {
		if(!BusinessUtil.isNullOrEmpty(slSelectableList)){
			Component comp = new Component(context);
			MapList mpObjList = comp.fetchObjectDetails(context,objId,slSelectableList);
			slSelectableList.clear();
			slSelectableList.setSize(0);
			int outputSectionsize = outPutXMLsections.getSectionDetails().size();
			OutPutXMLSections outPutXML;
			for(int i=0;i<outputSectionsize;i++){
				outPutXML = outPutXMLsections.getSectionDetails().get(i);
				getSelectables(outPutXML,mpObjList);
			}
		}	
	}

	public void getSelectables(OutPutXMLSections outPutXML, MapList mpObjList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, FrameworkException {
		
		List<OutPutFieldDetails> opFiledDetails = outPutXML.getFieldDetails();
		String fieldVal;
		StringList fieldVals;
		Map mpObj;
		Object objIds;
		OutPutFieldDetails objField;
		String sSelectable;
		PDFUtil pdfUtil;
		Method objColumnSetterMethod;
		int mapObjectSize;
		for(int j=0;j<opFiledDetails.size();j++){
			objField = opFiledDetails.get(j); 
			sSelectable = objField.getFieldSelectable();
			if(UIUtil.isNotNullAndNotEmpty(sSelectable) && !sSelectable.contains(CONSTANT_PROGRAM)){
				fieldVal = DomainConstants.EMPTY_STRING;
				mapObjectSize = mpObjList.size();
				for (int mapIt = 0; mapIt < mapObjectSize; mapIt++)
				{					
					mpObj = (Map)mpObjList.get(mapIt);
					objIds = mpObj.get(sSelectable);
					if ( objIds instanceof StringList )
					{
						fieldVals    = (StringList) mpObj.get(sSelectable);
						//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
						fieldVal = getFieldValue(fieldVals);
						//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
					}
					if ( objIds instanceof String )
					{
						
						fieldVal = (String) mpObj.get(sSelectable);
						
					}
					if(UIUtil.isNotNullAndNotEmpty(objField.getPdfViewDisplayMethod())){
						pdfUtil = new PDFUtil(sContext,objField);
						objColumnSetterMethod = pdfUtil.getClass().getDeclaredMethod(objField.getPdfViewDisplayMethod(), String.class);
						objColumnSetterMethod.invoke(pdfUtil,fieldVal);
						}
					else{
					objField.setFieldValue(fieldVal);
					}
					objField.setFieldValue(replaceNoAccessValues(objField.getFieldValue()));
				}
			}
		}

	}
	
	/**
	 * Added by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	 * Replace No Access value instead of "#DENIED!"
	 * @param value
	 * @return
	 */
	private String replaceNoAccessValues(String value) {
		if(UIUtil.isNotNullAndNotEmpty(value) && value.contains(PDFConstants.DENIED)) {
			value = pgV3Constants.NO_ACCESS; 
		} 
		return value;
	}
	
	/**
	 * Added by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	 * Return Field Value 
	 * @param value
	 * @return
	 */
	private String getFieldValue(StringList fieldValue) { 
		StringBuilder sbFieldValue = new StringBuilder();
		for (String value : fieldValue) {
			if(sbFieldValue.toString().length()>0)
				sbFieldValue.append(",").append(replaceNoAccessValues(value));	
			else
				sbFieldValue.append(replaceNoAccessValues(value));	
		}
		return sbFieldValue.toString();
		
	}
	
	

}
