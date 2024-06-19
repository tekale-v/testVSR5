package com.pdfview.helper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dassault_systemes.enovia.characteristic.model.Characteristic;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.APP.Characteristics;
import com.pdfview.impl.FPP.PerformanceCharacteristic;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetDSOCharacterisctic {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetDSOCharacterisctic(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}
	/**
	 * Retrieve Characteristics Table Data for CATIA Originated Parts
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException
	 */
	public Characteristics getComponent() throws Exception{
		Characteristics performancecharacteristics = new Characteristics();
		List<PerformanceCharacteristic> perfcharclist = performancecharacteristics.getPerformanceCharacteristic();
		boolean isPushContext = false;
		DomainObject domainObject = null;
		StringList slTMList = new StringList(1);
		Map mpCharInfo = null;
		Map mpCharConnected = null;
		DomainObject domCharObject = null;	
		try{	
			
			if(StringHelper.validateString(_OID)){				
				domainObject= DomainObject.newInstance(_context,_OID);
				String strAuthApplication = domainObject.getInfo(_context, pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);			
				if(UIUtil.isNotNullAndNotEmpty(strAuthApplication) && pgV3Constants.RANGE_PGAUTHORINGAPPLICATION_LPD.equalsIgnoreCase(strAuthApplication)){
					String strId = DomainConstants.EMPTY_STRING;										
					String strChg = DomainConstants.EMPTY_STRING; 
					String strChara = DomainConstants.EMPTY_STRING;
					String strCharaSpe = DomainConstants.EMPTY_STRING;
					String strTM = DomainConstants.EMPTY_STRING;
					String strTMLogic = DomainConstants.EMPTY_STRING;
					String strOtherTMNumber = DomainConstants.EMPTY_STRING;
					String strMethodOrigin = DomainConstants.EMPTY_STRING;
					String strReferenceDoc = DomainConstants.EMPTY_STRING;
					String strTsetMethodSpecs = DomainConstants.EMPTY_STRING;
					String strSampling = DomainConstants.EMPTY_STRING;
					String strRetestingUOM = DomainConstants.EMPTY_STRING;
					String strRetesting = DomainConstants.EMPTY_STRING;
					String strSSubGroup = DomainConstants.EMPTY_STRING;
					String strPlantTestingLvl = DomainConstants.EMPTY_STRING;
					String strLowerSpecificationLimit = DomainConstants.EMPTY_STRING;
					String strLowerRoutineReleaseLimit = DomainConstants.EMPTY_STRING;
					String strLowerTarget = DomainConstants.EMPTY_STRING;
					String strTarget = DomainConstants.EMPTY_STRING;
					String strUpperTarget = DomainConstants.EMPTY_STRING;
					String strUpperRoutineReleaseLimit = DomainConstants.EMPTY_STRING;
					String strUpperSpecificationLimit = DomainConstants.EMPTY_STRING;
					String strDisplayUnit = DomainConstants.EMPTY_STRING;
					String strMeasurementPrecision = DomainConstants.EMPTY_STRING;
					String strReportType = DomainConstants.EMPTY_STRING;
					String strReleaseCriteria = DomainConstants.EMPTY_STRING;
					String strBasis = DomainConstants.EMPTY_STRING;
					String strActionRequiredList = DomainConstants.EMPTY_STRING;
					String strCharstopgPLICriticalityFactor = DomainConstants.EMPTY_STRING;
					String strApplication = DomainConstants.EMPTY_STRING;
					String strPCharstopgPLITestGroup = DomainConstants.EMPTY_STRING;
					MapList mlCharacteristicsConnected = null;
					DomainObject dom1=null;
					
					StringList s1ObjSelects = new StringList(pgV3Constants.SELECT_DESCRIPTION);
					StringList slObjectSelects = new StringList(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);					
					slObjectSelects.add(DomainConstants.SELECT_ID);
					slObjectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGROUTINERELEASECRITERIA);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTICSPECIFICS);				
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGTMLOGIC);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODNUMBER);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODSPECIFICS);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAMPLING);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSUBGROUP);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRELEASECRITERIA);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASIS);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAPPLICATION);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGACTIONREQUIRED);				
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODORIGIN);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGTESTGROUP);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRETESTINGUOM);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTINGRETESTING);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTING);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGREPORTTYPE);
					slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCRITICALITYFACTOR);
					slObjectSelects.add(PDFConstant.SELECT_ATTRIBUTE_PGMEASUREMENTPRECISION);
					slObjectSelects.add(PDFConstant.SELECT_ATTRIBUTE_PGTESTMETHODREFDOCGCAS);
					
					MapList mlCharacteristicsInfo = domainObject.getRelatedObjects(_context,
										pgV3Constants.RELATIONSHIP_PARAMETER_AGGREGATION, //relationshipPattern
										pgV3Constants.TYPE_PLM_PARAMETER, //typePattern
										slObjectSelects, //objectSelects
										null, //relationshipSelects
										false, //getTo
										true, //getFrom
										(short)1, //recurseToLevel
										null, //objectWhere
										null, //relationshipWhere
										0);				
					if(mlCharacteristicsInfo != null && !mlCharacteristicsInfo.isEmpty()){
						Characteristic charObjPLMParam = null; 
						for (Iterator iterator = mlCharacteristicsInfo.iterator(); iterator.hasNext();){
							PerformanceCharacteristic performanceCharacteristic=new PerformanceCharacteristic();
							mpCharInfo = (Map) iterator.next();							
							strId = (String)mpCharInfo.get(pgV3Constants.SELECT_ID);
							dom1= DomainObject.newInstance(_context,strId);
							 mlCharacteristicsConnected = dom1.getRelatedObjects(_context,
									pgV3Constants.RELATIONSHIP_REFERENCE_DOCUMENT, //relationshipPattern
									pgV3Constants.SYMBOL_STAR, //typePattern
									s1ObjSelects, //objectSelects
									null, //relationshipSelects
									true, //getTo
									false, //getFrom
									(short)1, //recurseToLevel
									null, //objectWhere
									null, //relationshipWhere
									0);
							for (Iterator iterator1 = mlCharacteristicsConnected.iterator(); iterator1.hasNext();){
								mpCharConnected = (Map) iterator1.next();
								strReferenceDoc = (String)mpCharConnected.get(pgV3Constants.SELECT_DESCRIPTION);
							}
							strChg = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
							strChara = (String)mpCharInfo.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
							strCharaSpe = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCHARACTERISTICSPECIFICS);
							performanceCharacteristic.setChg(StringHelper.validateString1(StringHelper.wrapStringInTable(strChg)));
							performanceCharacteristic.setCharacteristics(StringHelper.validateString1(StringHelper.wrapStringInTable(strChara)));
							performanceCharacteristic.setCharacteristicSpecifics(StringHelper.validateString1(StringHelper.wrapStringInTable(strCharaSpe)));
							if(UIUtil.isNotNullAndNotEmpty(strId)){
								domCharObject = DomainObject.newInstance(_context,strId);
								slTMList = domCharObject.getInfoList(_context, "from["+pgV3Constants.REL_CHARACTERISTICTESTMETHOD+"].to.name");								
							}														
							
							if(slTMList != null && !slTMList.isEmpty()){
								strTM=StringHelper.convertObjectToString(slTMList);
								//strTM = slTMList.toString().replaceAll(pgV3Constants.SYMBOL_COMMA,pgV3Constants.SYMBOL_NEXT_LINE).replace("[","").replace("]","");
							}else{
								strTM = DomainConstants.EMPTY_STRING;
							}
							strTMLogic = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGTMLOGIC);
							strOtherTMNumber = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODNUMBER);
							
							if(UIUtil.isNullOrEmpty(strOtherTMNumber)){
								strOtherTMNumber = DomainConstants.EMPTY_STRING; 
							}
							strOtherTMNumber = StringHelper.filterLessAndGreaterThanSign(strOtherTMNumber);
							strTsetMethodSpecs = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODSPECIFICS);
							if(UIUtil.isNullOrEmpty(strTsetMethodSpecs)){
								strTsetMethodSpecs = DomainConstants.EMPTY_STRING; 
							}
							strTsetMethodSpecs = StringHelper.filterLessAndGreaterThanSign(strTsetMethodSpecs);
							strMethodOrigin = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGMETHODORIGIN);
							performanceCharacteristic.setTestMethod(StringHelper.validateString1(strTM));
							performanceCharacteristic.setTestMethodLogic(StringHelper.validateString1(StringHelper.wrapStringInTable(strTMLogic)));
							performanceCharacteristic.setOrigin(StringHelper.validateString1(StringHelper.wrapStringInTable(strMethodOrigin)));
							performanceCharacteristic.setOtherTestMethodNumber(StringHelper.validateString1(StringHelper.wrapStringInTable(strOtherTMNumber)));
							performanceCharacteristic.setTestMethodSpecifics(StringHelper.validateString1(StringHelper.wrapStringInTable(strTsetMethodSpecs)));
							performanceCharacteristic.setReferenceDocument(StringHelper.validateString1(strReferenceDoc));
						    strSampling = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAMPLING);					
							if(UIUtil.isNullOrEmpty(strSampling)){
									strSampling = DomainConstants.EMPTY_STRING; 
							}
							
							strSampling = StringHelper.filterLessAndGreaterThanSign(strSampling);					
							strRetestingUOM = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRETESTINGUOM);
							strRetesting = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTINGRETESTING);
							strSSubGroup = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSUBGROUP);					
							if(UIUtil.isNullOrEmpty(strSSubGroup)){
								strSSubGroup = DomainConstants.EMPTY_STRING; 
							}					
							strSSubGroup = StringHelper.filterLessAndGreaterThanSign(strSSubGroup);					
							strPlantTestingLvl = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTING);
							performanceCharacteristic.setSampling(StringHelper.validateString1(StringHelper.wrapStringInTable(strSampling)));
							performanceCharacteristic.setSubgroup(StringHelper.validateString1(StringHelper.wrapStringInTable(strSSubGroup)));
							performanceCharacteristic.setPlantTestingLevel(StringHelper.validateString1(StringHelper.wrapStringInTable(strPlantTestingLvl)));
							performanceCharacteristic.setPlantTestingRetesting(StringHelper.validateString1(StringHelper.wrapStringInTable(strRetesting)));
							performanceCharacteristic.setUnitOfMeasureResting(StringHelper.validateString1(StringHelper.wrapStringInTable(strRetestingUOM)));
							if(UIUtil.isNotNullAndNotEmpty(strId)){
								charObjPLMParam = new Characteristic(_context,strId,true);
								strLowerTarget = charObjPLMParam.getMinimalValue(_context); 
								strTarget = charObjPLMParam.getNominalValue(_context); 
								strUpperTarget = charObjPLMParam.getMaximalValue(_context); 
								strUpperRoutineReleaseLimit = charObjPLMParam.getUpperRoutineReleaseLimit(_context);
								strUpperSpecificationLimit = charObjPLMParam.getUpperSpecificationLimit(_context);
								strLowerSpecificationLimit = charObjPLMParam.getLowerSpecificationLimit(_context);
      							strLowerRoutineReleaseLimit = charObjPLMParam.getLowerRoutineReleaseLimit(_context);
      							strDisplayUnit = charObjPLMParam.getDisplayUnit();
							}
							performanceCharacteristic.setLowerSpecLimit(StringHelper.validateString1(StringHelper.wrapStringInTable(strLowerSpecificationLimit)));
							performanceCharacteristic.setLowerRoutineReleaseLimit(StringHelper.validateString1(StringHelper.wrapStringInTable(strLowerRoutineReleaseLimit)));
							performanceCharacteristic.setLowerTarget(StringHelper.validateString1(StringHelper.wrapStringInTable(strLowerTarget)));
							performanceCharacteristic.setTarget(StringHelper.validateString1(StringHelper.wrapStringInTable(strTarget)));
							performanceCharacteristic.setUpperTarget(StringHelper.validateString1(StringHelper.wrapStringInTable(strUpperTarget)));
							performanceCharacteristic.setUpperRoutineReleaseLimit(StringHelper.validateString1(StringHelper.wrapStringInTable(strUpperRoutineReleaseLimit)));
							performanceCharacteristic.setUpperSpecLimit(StringHelper.validateString1(StringHelper.wrapStringInTable(strUpperSpecificationLimit)));
							strMeasurementPrecision = (String)mpCharInfo.get(PDFConstant.SELECT_ATTRIBUTE_PGMEASUREMENTPRECISION);
							strReportType = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGREPORTTYPE);
							performanceCharacteristic.setUOM(StringHelper.validateString1(StringHelper.wrapStringInTable(strDisplayUnit)));
							performanceCharacteristic.setReportToNearest(StringHelper.validateString1(StringHelper.wrapStringInTable(strMeasurementPrecision)));
							performanceCharacteristic.setReportType(StringHelper.validateString1(StringHelper.wrapStringInTable(strReportType)));
							strReleaseCriteria = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRELEASECRITERIA);
							if(UIUtil.isNullOrEmpty(strReleaseCriteria)){
								strReleaseCriteria = DomainConstants.EMPTY_STRING; 
							}			
							
							strReleaseCriteria =StringHelper.filterLessAndGreaterThanSign(strReleaseCriteria);
							strReleaseCriteria =StringHelper.filterLessAndGreaterThanSign(strReleaseCriteria);
							
							performanceCharacteristic.setReleaseCriteria(StringHelper.validateString1(StringHelper.wrapStringInTable(strReleaseCriteria)));
							strBasis = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASIS);
							if(UIUtil.isNullOrEmpty(strBasis)){
								strBasis = DomainConstants.EMPTY_STRING; 
							}
							strBasis =StringHelper.filterLessAndGreaterThanSign(strBasis);
							strActionRequiredList=(String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGACTIONREQUIRED);
							strCharstopgPLICriticalityFactor=(String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCRITICALITYFACTOR);
							performanceCharacteristic.setActionRequired(StringHelper.validateString1(StringHelper.wrapStringInTable(strActionRequiredList)));
							performanceCharacteristic.setCriticalityFactor(StringHelper.validateString1(StringHelper.wrapStringInTable(strCharstopgPLICriticalityFactor)));
							performanceCharacteristic.setBasis(StringHelper.validateString1(StringHelper.wrapStringInTable(strBasis)));
							strApplication = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGAPPLICATION);
							
							if(UIUtil.isNullOrEmpty(strApplication)){
								strApplication = DomainConstants.EMPTY_STRING; 
							}
							
							strApplication =StringHelper.filterLessAndGreaterThanSign(strApplication);
							
							strPCharstopgPLITestGroup = (String)mpCharInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGTESTGROUP);
							performanceCharacteristic.setTestGroup(StringHelper.validateString1(StringHelper.wrapStringInTable(strPCharstopgPLITestGroup)));
							performanceCharacteristic.setApplication(StringHelper.validateString1(StringHelper.wrapStringInTable(strApplication)));
							perfcharclist.add(performanceCharacteristic);						
						}						
						mlCharacteristicsInfo.clear();						
					}					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isPushContext) {
				try {
					ContextUtil.popContext(_context);
				} catch (FrameworkException e) {
					e.printStackTrace();
				}
				isPushContext = false;
			}
		}	
		return performancecharacteristics;
	}
	
}
