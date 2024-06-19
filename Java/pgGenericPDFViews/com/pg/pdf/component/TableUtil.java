package com.pg.pdf.component;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.pdf.bean.input.xml.Field;
import com.pg.pdf.bean.input.xml.Table;
import com.pg.pdf.bean.output.xml.OutPutFieldDetails;
import com.pg.pdf.bean.output.xml.OutPutXML;
import com.pg.pdf.bean.output.xml.OutPutXMLSections;
import com.pg.pdf.bean.output.xml.OutPutXMLTableSections;
import com.pg.pdf.enumerations.PDFConstants;
import com.pg.pdf.util.PDFUtil;
import com.pg.v3.custom.pgV3Constants;

public class TableUtil extends PDFConstants{
	Context context;
	boolean isValidView;
	public TableUtil(Context context){
		this.context = context; 
	}
	
	private  final Logger logger = Logger.getLogger(TableUtil.class.getName());
	public OutPutXMLTableSections processTableFields(Table objTable, Context context, OutPutXMLSections newSection,Map<String, String> requestMap,String objId) throws NoSuchMethodException,  IllegalAccessException, InvocationTargetException {
		logger.info("Enter TableUtil -method ProcessTableFields");
		OutPutXMLTableSections tableSections = new OutPutXMLTableSections();
		HashMap<String, Serializable> hmArgMap = new HashMap<>();
		HashMap<String, String> hmRequestMap = new HashMap<>();
		hmRequestMap.put(CONSTANT_OBJECT_ID, objId);
		OutPutXML outPutXMLsections = new OutPutXML();
		outPutXMLsections.setSectionName(newSection.getSection()+CONSTANT_S);
		try {
			MapList mlReturnList = JPO.invoke(context, objTable.getFieldProgram(), null, objTable.getFieldMethod(),
					JPO.packArgs(hmRequestMap), MapList.class);
			List<Field> listofField = new ArrayList<>();
			List<OutPutXMLSections> newSections = new ArrayList<>();
			for(int i=0;i<mlReturnList.size();i++){
				Map<?, ?> mRecord = (Map<?, ?>)mlReturnList.get(i);
				String sRecordId = (String) mRecord.get(DomainConstants.SELECT_ID);
				String sConnectionId = (String) mRecord.get(DomainConstants.SELECT_RELATIONSHIP_ID);
				String sConnectionName = (String) mRecord.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
				if(UIUtil.isNotNullAndNotEmpty(sConnectionId) && UIUtil.isNotNullAndNotEmpty(sConnectionName)){
					requestMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, sConnectionId);
					requestMap.put(DomainConstants.SELECT_RELATIONSHIP_NAME, sConnectionName);

				}
				if(sRecordId.contains(CONSTANT_COLON)){
					sRecordId = objId;
				}
				Component com = new Component(context);
				requestMap.put(CONSTANT_OBJECT_ID, sRecordId);
				String isIntended = objTable.getTableCategory();
				if(UIUtil.isNotNullAndNotEmpty(isIntended) && isIntended.equalsIgnoreCase(TABLE_INTENDED) ){
					String levelCheck = (String) mRecord.get(CONSTANT_LEVEL);
					if(UIUtil.isNotNullAndNotEmpty(levelCheck) && levelCheck.equals(INTEGER_ONE)){
						OutPutXMLSections subSections = new OutPutXMLSections();
						subSections.setSection(newSection.getSection()+CONSTANT_S);
						newSections.add(subSections);
					}
				}
				StringList slSelectableList = new StringList();
				for(int fieldCount=0;fieldCount<objTable.getFields().size();fieldCount++){ 
					OutPutXMLSections subSection = new OutPutXMLSections();
					subSection.setSection(objTable.getFields().get(fieldCount).getFieldName());
					String sFilter = objTable.getFields().get(fieldCount).getFilter();
					boolean setSelectables = true;
					if(UIUtil.isNotNullAndNotEmpty(sFilter)){
						String filterMap =sFilter.substring(0,sFilter.indexOf(CONSTANT_EQUAL));
						String filterName = sFilter.substring(sFilter.indexOf(CONSTANT_EQUAL)+1, sFilter.length());
						setSelectables = false;
						if (filterName.equalsIgnoreCase((String) mRecord.get(filterMap))){
							setSelectables= true;
						}
					}
					if(setSelectables){
						listofField.addAll(objTable.getFields().get(fieldCount).getFields());
						subSection= com.getFieldValuesinSection(context,objTable.getFields().get(fieldCount).getFields(), subSection,requestMap,CONSTANT_TABLE);
						newSections.add(subSection);
						slSelectableList.addAll(subSection.getSlSelectableList());
					}
					outPutXMLsections.setSectionDetails(newSections);
					//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
					if (!BusinessUtil.isNullOrEmpty(slSelectableList) && !BusinessUtil.isNullOrEmpty(subSection.getFieldDetails())) {
                       	Map<Object, Object> infoMap = com.fetchObjectInfo(context, sRecordId, slSelectableList);
						slSelectableList.clear();
                        slSelectableList.setSize(0);
						MapList infoList = getInfoUsingPushContext(com, subSection, infoMap, sRecordId);
						com.getSelectables(subSection, infoList);
                    }
					//Modified by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Ends
				}
			}
			hmArgMap.put(CONSTANT_OBJLIST, mlReturnList);
			hmArgMap.put(CONST_VIEW, requestMap.get(CONST_VIEW));
			hmRequestMap.put(CONSTANT_LANGUAGE, context.getSession().getLanguage());
			hmArgMap.put(CONSTANT_PARAMLIST, hmRequestMap);
			newSections = getTableProgramValues(context,hmArgMap,listofField,newSections);
			outPutXMLsections.setSectionDetails(newSections);
			tableSections.setOutPutXMLSections(outPutXMLsections);
		} catch (MatrixException e) {
			logger.info("Exception occurred in TableUtil -method ProcessTableFields"+ e.getMessage());
			
		}
		logger.info("Exit TableUtil -method ProcessTableFields");
		return tableSections;
	}

	/**
	 * Added by IRM pdf views 2018x.6 Feb_CW for Requirements 40810--Starts
	 * @param component
	 * @param outerSections
	 * @param infoMap
	 * @param objectOid
	 * @return
	 * @throws FrameworkException
	 */
    public MapList getInfoUsingPushContext(Component component, OutPutXMLSections outerSections, Map<Object, Object> infoMap, String objectOid) throws FrameworkException {
        boolean isContextPushed = false;
        MapList retList = new MapList();
        try {
            final List<OutPutFieldDetails> outPutFieldDetailsList = outerSections.getFieldDetails();
            StringList selectList = new StringList();
            for (OutPutFieldDetails outPutFieldDetails : outPutFieldDetailsList) {
                if (outPutFieldDetails.isUsePushContext()) {
                    selectList.add(outPutFieldDetails.getUsePushContextSelectValue());
                }
            }
            if (selectList.size() > 0) {
				// need to push context to get no access data for pdf to match with UI (as it does push context for UI code).
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                isContextPushed = true;
                final Map<Object, Object> resultMap = component.fetchObjectInfo(context, objectOid, selectList);
                Map<Object, Object> retMap = new HashMap<>(infoMap);
                retMap.putAll(resultMap);
                retList.add(retMap);

            } else {
                retList.add(infoMap);
            }

        } catch (FrameworkException e) {
            logger.info("Exception occurred in TableUtil -method getInfoUsingPushContext"+ e.getMessage());
        } finally {
            if (isContextPushed) {
                ContextUtil.popContext(context);
            }
        }
        return retList;
    }

	private List<OutPutXMLSections> getTableProgramValues(Context context, HashMap<String, Serializable> hmArgMap,
			List<Field> listofField, List<OutPutXMLSections> newSectionsList) {
		logger.info("Enter TableUtil -method getTableProgramValues");
		String sFieldSelectable;
		String sAllowedViews;
		try {
			for(int i=0;i<listofField.size();i++){
				Field objField = listofField.get(i);
				sFieldSelectable = objField.getFieldSelectable();
				sAllowedViews = objField.getFieldView();
				MapList resultList = (MapList) hmArgMap.get(CONSTANT_OBJLIST);
				String[] programResultList = new String[resultList.size()];
				Component com = new Component(context);
				this.isValidView = com.validateViews((String)hmArgMap.get(CONST_VIEW),sAllowedViews);
				//Need to check for particular view based on delimiter
				if (this.isValidView){
					Map<String, String[]> resultMap = new HashMap<>();
					if(UIUtil.isNotNullAndNotEmpty(sFieldSelectable)&& UIUtil.isNotNullAndNotEmpty(objField.getFieldMethod())&& UIUtil.isNotNullAndNotEmpty(objField.getFieldProgram())){
						if(sFieldSelectable.equalsIgnoreCase(CONST_PROGRAM_HTML)){
							StringList programResult = JPO.invoke(context, objField.getFieldProgram(), null, objField.getFieldMethod(),
									JPO.packArgs(hmArgMap), StringList.class);
							programResultList = BusinessUtil.toStringArray(programResult);
						}
						if(sFieldSelectable.equalsIgnoreCase(CONSTANT_PROGRAM)){
							//The return type of the method is Vector.
							Vector<?> programResult = JPO.invoke(context, objField.getFieldProgram(), null, objField.getFieldMethod(),
									JPO.packArgs(hmArgMap), Vector.class);
							programResultList = BusinessUtil.toStringArray(Collections.list(programResult.elements()));
						}
						Map<String,String>resultIdMap = setOutPutList(programResultList,resultList);
						resultMap.put(objField.getFieldName(), programResultList);
						int sectionSize = newSectionsList.size();
						for(int secCount=0;secCount<sectionSize;secCount++){
							List<OutPutFieldDetails> outputFieldDetails = newSectionsList.get(secCount).getFieldDetails();
							if(programResultList.length>0 && !BusinessUtil.isNullOrEmpty(outputFieldDetails)&& objField!=null){
								if(programResultList.length==newSectionsList.size())
									setOutPutFieldValueForFlatTable(resultMap,outputFieldDetails,objField,secCount);
								else
									setOutPutFiledValue(resultIdMap,outputFieldDetails, objField);
							}
							newSectionsList.get(secCount).setFieldDetails(outputFieldDetails);
						}
					}
				}
			}
		}
		catch (MatrixException e) {
			logger.info("Error occurred in TableUtil -method getTableProgramValues"+ e.getMessage());

		}
		logger.info("Exit TableUtil -method getTableProgramValues");
		return newSectionsList;
	}

	private void setOutPutFieldValueForFlatTable(
			Map<String, String[]> resultMap,
			List<OutPutFieldDetails> outputFieldDetails, Field objField,
			int secCount) {
		try{
			int fieldSize = outputFieldDetails.size();
			for(int i=0;i<fieldSize;i++){


				if(objField.getFieldName().equals(outputFieldDetails.get(i).getFieldName())){
					String[] sResult = resultMap.get(outputFieldDetails.get(i).getFieldName());
					setResult(sResult[secCount],objField,outputFieldDetails.get(i));

				}
			}
		}
		catch (Exception e) {
			logger.info("Error occurred in TableUtil -method setOutPutFieldValueForFlatTable  "+ e.getMessage());

		}

	}

	private Map<String,String> setOutPutList(String[] programResultList, MapList resultList) {
		Map<String,String> mlFinalResultList = new HashMap<>();
		int resultSize = resultList.size();
		String sRecordId;
		Map<?, ?> mRecord;
		for(int k=0;k<resultSize;k++){
			mRecord = (Map<?, ?>)resultList.get(k);
			sRecordId = (String) mRecord.get(DomainConstants.SELECT_ID);
			mlFinalResultList.put(sRecordId,programResultList[k]);
		}
		return mlFinalResultList;
	}



	private void setOutPutFiledValue(Map<String,String> resultMap, List<OutPutFieldDetails> outputFieldDetails, Field objField) {

		String sResult = DomainConstants.EMPTY_STRING;
		try {
			int resultSize= resultMap.size();
			int fieldSize= outputFieldDetails.size();
			for(int k=0;k<resultSize;k++){
				for(int i=0;i<fieldSize;i++){


					if(objField.getFieldName().equals(outputFieldDetails.get(i).getFieldName())){

						sResult = resultMap.get(outputFieldDetails.get(i).getFieldId());
						setResult(sResult,objField,outputFieldDetails.get(i));
					}
				}
			}
		} catch (Exception e) {
			logger.info("Error occurred in TableUtil -method setOutPutFiledValue  "+ e.getMessage());

		}
	}

	private void setResult(String sResult, Field objField,
			OutPutFieldDetails outPutFieldDetails) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		try {
			if(!BusinessUtil.isNullOrEmpty(sResult)){                                           
				if(UIUtil.isNotNullAndNotEmpty(objField.getPdfViewDisplayMethod())){
					PDFUtil pdfUtil = new PDFUtil(context,outPutFieldDetails);
					Method objColumnSetterMethod;

					objColumnSetterMethod = pdfUtil.getClass().getDeclaredMethod(objField.getPdfViewDisplayMethod(), String.class);

					objColumnSetterMethod.invoke(pdfUtil, sResult);

				}  
				else{
					outPutFieldDetails.setFieldValue(sResult);
				}

			}
		}catch (SecurityException e) {
			logger.info("Error occurred in TableUtil -method setResult  "+ e.getMessage());
		}
	}
}
