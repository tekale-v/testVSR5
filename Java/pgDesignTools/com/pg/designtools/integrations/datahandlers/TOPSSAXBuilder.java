package com.pg.designtools.integrations.datahandlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.JDOMException;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.util.List;
import matrix.util.MatrixException;
import matrix.util.StringList;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.framework.ui.UIUtil;

public class TOPSSAXBuilder implements IHandler{
	
	 HashMap<String, Object> hmReferencexml = new HashMap<>();
	 HashMap<String, Object> hmCombinedxml = new HashMap<>();
	 SAXHandler saxhandler = new SAXHandler();
	 CommonProductData cpd = new CommonProductData();
	 String filename;
	 String strRefDocFileName = "";
	 String fileformat ;
	 File fileRoot;
	 String encFileName;
	 boolean doSave = false;
	 Context context;
	 boolean hasOne = false;
	 boolean isCombinedXML = false;
	 
	 Element eleShipper;
	 Element elePriPack;
	 Element eleIPack;
	 
	 Element elePriPacklength;
	 Element elePriPackwidth;	 
	 Element elePriPackheight;
	 
	 Element eleIPacklength;
	 Element eleIPackwidth;	 
	 Element eleIPackheight;
	 Element eleIPackitemCount0;
	 
	 Element eleShipperlength;
	 Element eleShipperwidth;	 
	 Element eleShipperheight;
	 Element eleShipperitemCount0;
	
	@Override
	public String process() {
		return null;
	}

	/**
	 * Save TOPS XML - This is wrapper for all methods for Reference/Combined xml document updation
	 * @param domIPM
	 * @param tempfileRoot
	 * @param objTag
	 * @return
	 * @throws MatrixException 
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	public boolean processIPMDocReferenceFile(DomainObject domIPM,File tempfileRoot,String objTag,String strPalletType) throws MatrixException, JDOMException, IOException {
		context = PRSPContext.get();
		fileRoot = tempfileRoot;
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile domIPM:: "+domIPM);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile fileRoot path :: "+fileRoot.getAbsolutePath());
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> processIPMDocReferenceFile fileRoot name::"+fileRoot.getName());
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> processIPMDocReferenceFile strPalletType::"+strPalletType);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile objTag:: "+objTag);
		if(domIPM!=null)
		{
			hasOne = true;
			MapList filesList = domIPM.getAllFormatFiles(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile filesList "+filesList);
			int filesLSize = filesList.size();
			if (filesLSize > 1) 
			{
				hasOne = false;
				Iterator<?> fileItr1 = filesList.iterator();
				Map<?, ?> filemapObj1 ; 
				while (fileItr1.hasNext())
				{
					filemapObj1 = (Map<?, ?>)fileItr1.next();
					filename       = (String)filemapObj1.get("filename");
					fileformat       = (String)filemapObj1.get("format");
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile filename "+filename);
					//START: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
					if (filename.indexOf(DataConstants.CONSTANT_REFERENCE) >= 0 || filename.indexOf(DataConstants.CONSTANT_COMBINED) >= 0) 
					{
						if (filename.indexOf(DataConstants.CONSTANT_COMBINED) >= 0) {
							isCombinedXML = true;
						}else {
							isCombinedXML = false;
						}
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> processIPMDocReferenceFile isCombinedXML:: "+isCombinedXML);
						FileList files = new FileList();
						matrix.db.File file = new matrix.db.File(filename, fileformat);
						
						List filelist = new List();
						filelist.add(file);
						
				        files.addAll(filelist);
				        domIPM.checkoutFiles(context, false, fileformat, files, fileRoot.getAbsolutePath() + java.io.File.separator);

						String strIPMDocName=domIPM.getInfo(context, DomainConstants.SELECT_NAME);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile strIPMDocName::"+strIPMDocName);
					    //Added condition so that only name of the combined xml would be updated in strRefDocFileName. 
					    if (isCombinedXML) {
						 strRefDocFileName = filename;
					    }
						 
				        VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile after checkout ");
						Element eleRoot = saxhandler.getRootElement(fileRoot, filename);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processIPMDocReferenceFile eleRoot:: "+eleRoot.getName());
						
						setIPMNameInRefXML(eleRoot,strIPMDocName);
						setDocRevision(eleRoot,domIPM);
						processCubeSpecComments(eleRoot,objTag);
						processPriPackElements(eleRoot);
						processIPackElements(eleRoot);
						processShipperElements(eleRoot);
						compareAndUpdateReferenceXMLValues();
						//START: DTCLD-425 Update the pallet name in the xml files, in case there is mismatch between xml and actual value
						compareAndUpdatePalletDetails(eleRoot,strPalletType);
						//END: DTCLD-425
						//For updating extName and extAddlInfo tags in Combined XML
						if (isCombinedXML) {
							setNameInCombinedXML(eleRoot, strIPMDocName);
							updateextAddlInfoTag(eleRoot, objTag);
						}
						saxhandler.writeUpdatedInfoInFile();
						
						Map mpFileRelatedArguments = new HashMap();
						//Identify the type of xml
						mpFileRelatedArguments.put("strTypeOfXML",((isCombinedXML)?DataConstants.CONSTANT_IPS_COMBINED_XML:DataConstants.CONSTANT_REFERENCE_XML));
						mpFileRelatedArguments.put("filename",filename);
						mpFileRelatedArguments.put("fileRoot",fileRoot);
						mpFileRelatedArguments.put("fileformat",fileformat);
						VPLMIntegTraceUtil.trace(context, ">>processIPMDocReferenceFile mpFileRelatedArguments ::"+mpFileRelatedArguments);
						//END: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
						doSave = cpd.reviseVersionAndGrantRevokeAccess(context, domIPM, mpFileRelatedArguments,null,null) ;
					}
				}
			}
		}
		return doSave;
	}
	
	/**
	 * Method to update the pallet information on Reference/Combined XML
	 * @param eleRoot
	 * @throws FrameworkException 
	 */
	private void compareAndUpdatePalletDetails(Element eleRoot,String strTUPPalletType) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of compareAndUpdatePalletDetails method");
		VPLMIntegTraceUtil.trace(context, ">>> compareAndUpdatePalletDetails strTUPPalletType::"+strTUPPalletType);
		
		//for reference xml, name and palletId tags are populated  <name>PG_B01_1200x800x144</name>  <palletId>B1</palletId>
		String strXMLPalletId=eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_DB)
				.getChild(DataConstants.CONSTANT_TAG_PALLETID).getText();
		VPLMIntegTraceUtil.trace(context, ">>> compareAndUpdatePalletDetails strXMLPalletId::"+strXMLPalletId);
		
		if(! strXMLPalletId.equalsIgnoreCase(strTUPPalletType)) {
			//generate the name tag string
			String strPalletName=generatePalletNameString(strTUPPalletType);
			VPLMIntegTraceUtil.trace(context, ">>> compareAndUpdatePalletDetails strPalletName::"+strPalletName);
			
			eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_DB).
			getChild(DataConstants.CONSTANT_TAG_PALLETID).setText(strTUPPalletType);
			
			eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_DB).
			getChild(DataConstants.CONSTANT_TAG_NAME).setText(strPalletName);
			
			if(isCombinedXML) {
				//for combined xml, we need to update the pallet info on both analysis0 and CustomFlag tags
				//<palType>B1</palType> <name>PG_B01_1200x800x144</name> 
				eleRoot.getChild(DataConstants.CONSTANT_TAG_INPUT).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_PALTYPE).setText(strTUPPalletType);
				eleRoot.getChild(DataConstants.CONSTANT_TAG_INPUT).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_NAME).setText(strPalletName);
				
				eleRoot.getChild(DataConstants.CONSTANT_TAG_OUTPUT).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_PALTYPE).setText(strTUPPalletType);
				eleRoot.getChild(DataConstants.CONSTANT_TAG_OUTPUT).getChild(DataConstants.CONSTANT_TAG_UNITLOAD).getChild(DataConstants.CONSTANT_TAG_NAME).setText(strPalletName);
			}
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of compareAndUpdatePalletDetails method");
	}
	
	/**
	 * Method to generate the pallet name string
	 * @param strTUPPalletType
	 * @return String
	 * @throws FrameworkException
	 */
	private String generatePalletNameString(String strTUPPalletType) throws FrameworkException {
		//find object of type pgPLIPalletType <name> object with Active state and get its description
		VPLMIntegTraceUtil.trace(context, ">>> START of generatePalletNameString method");
		
		String strPalletName="";
		String strWhereClause=DomainConstants.SELECT_CURRENT+DataConstants.CONST_DOUBLE_EQUAL+DataConstants.STATE_ACTIVE;
		VPLMIntegTraceUtil.trace(context, ">>>generatePalletNameString strWhereClause::"+strWhereClause);
		
		StringList slSelects=new StringList(1);
		slSelects.add(DomainConstants.SELECT_DESCRIPTION);
	
		CommonUtility commonUtility=new CommonUtility(context);
		MapList mlPalletObj=commonUtility.findObjectWithWhereClause(context, DataConstants.TYPE_PG_PLI_PALLET_TYPE, strTUPPalletType, DataConstants.SEPARATOR_STAR, strWhereClause, slSelects);
		VPLMIntegTraceUtil.trace(context, ">>>generatePalletNameString mlPalletObj::"+mlPalletObj);
		
		if(null!=mlPalletObj && !mlPalletObj.isEmpty()) {
			Map mpPalletInfo=(Map)mlPalletObj.get(0);
			strPalletName=(String)mpPalletInfo.get(DomainConstants.SELECT_DESCRIPTION);
		}
		VPLMIntegTraceUtil.trace(context, ">>>generatePalletNameString pallet name::"+strPalletName);
		VPLMIntegTraceUtil.trace(context, "<<< END of generatePalletNameString method");
		return strPalletName;
	}

	/**
	 * Save TOPS XML - 
	 * @return strRefDocFileName
	 */
	public String getfilename() {
		return strRefDocFileName;
	}
	
	/**
	 * Save TOPS XML - This method sets the document revision in tag strUser in Reference and/or Combined xml
	 * @param eleRoot
	 * @param domIPM
	 * @throws FrameworkException
	 */
	private void setDocRevision(Element eleRoot, DomainObject domIPM) throws FrameworkException {
		String strDocRevision = domIPM.getInfo(context, DomainConstants.SELECT_REVISION);
		eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_CUBESPEC).getChild(DataConstants.CONSTANT_TAG_STR_USER_5).setText(strDocRevision);
		VPLMIntegTraceUtil.trace(context, ">> setDocRevision strUser5 tag updated");
	}
	
	/**
	 * Save TOPS XML - This method updates value in tag comments in Reference and/or Combined xml
	 * @param eleRoot
	 * @param objTag
	 */
	private void processCubeSpecComments(Element eleRoot,String objTag) {
		 Element eleComments = eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_CUBESPEC).getChild(DataConstants.CONSTANT_TAG_COMMENTS);
		VPLMIntegTraceUtil.trace(context, ">>> processCubeSpecComments eleComments::"+eleComments);
		 if(eleComments != null)
	     {
	     	eleComments.setText(objTag);
			//START: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
	     	if (isCombinedXML) {
	     		hmCombinedxml.put("ObjectInfo", objTag);
	     	} else {
	     		hmReferencexml.put("ObjectInfo", objTag);
	     	}
			//END: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
	     }
		   	VPLMIntegTraceUtil.trace(context, ">>> processCubeSpecComments hmReferencexml::"+hmReferencexml);
	     	VPLMIntegTraceUtil.trace(context, ">>> processCubeSpecComments hmCombinedxml::"+hmCombinedxml);
	}
	
	/**
	 * Save TOPS XML - This method reads the pripack tags value and creates a map 
	 * @param eleRoot
	 */
	private void processPriPackElements(Element eleRoot)
	{
		 elePriPack = eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_PRIPACK);
		VPLMIntegTraceUtil.trace(context, ">>> processPriPackElements elePriPack::"+elePriPack);
		// Pripack - Master Consumer Unit information
		 if(elePriPack != null)
	     {		
			 getElementPriPacklength();
			 getElementPriPackWidth();
			 getElementPriPackheight();
			 
			 HashMap mapPriPack = new HashMap();
	    	 mapPriPack.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, (elePriPacklength != null) ? XSSUtil.decodeFromURL(elePriPacklength.getText()) : "");
	    	 mapPriPack.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, (elePriPackwidth != null) ? XSSUtil.decodeFromURL(elePriPackwidth.getText()) : "");
	    	 mapPriPack.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT, (elePriPackheight != null) ? XSSUtil.decodeFromURL(elePriPackheight.getText()) : "");
	    	 VPLMIntegTraceUtil.trace(context, ">>> processPriPackElements mapPriPack::"+mapPriPack);
			//START: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data 
	    	 if (isCombinedXML) {
	     		hmCombinedxml.put("PriPack", mapPriPack);
	     	} else {
	     		hmReferencexml.put("PriPack", mapPriPack);
	     	}
			//END: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
		 }
    	 VPLMIntegTraceUtil.trace(context, ">>> processPriPackElements hmCombinedxml::"+hmCombinedxml);
    	 VPLMIntegTraceUtil.trace(context, ">>> processPriPackElements hmReferencexml::"+hmReferencexml);
	}
	
	/** Save TOPS XML - This method reads the Ipack tags value and creates a map 
	 * @param eleRoot
	 */
	private void processIPackElements(Element eleRoot)
	{
		 eleIPack = eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_IPACK);
		VPLMIntegTraceUtil.trace(context, ">> processIPackElements eleIPack::"+eleIPack);
		 // IPack - Master Intermediate Part information
	     if(eleIPack != null)
	     {
	    	 getElementIPacklength();
			 getElementIPackWidth();
			 getElementIPackheight();
			 getElementIPackQuantity();
			 
	    	 HashMap mapIPack = new HashMap();
	    	 mapIPack.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, (eleIPacklength!= null) ? XSSUtil.decodeFromURL(eleIPacklength.getText()) : "");
	    	 mapIPack.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, (eleIPackwidth!= null) ? XSSUtil.decodeFromURL(eleIPackwidth.getText()) : "");
	    	 mapIPack.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT, (eleIPackheight!= null) ? XSSUtil.decodeFromURL(eleIPackheight.getText()) : "");
			 mapIPack.put(DataConstants.ATTRIBUTE_QUANTITY,XSSUtil.decodeFromURL(eleIPackitemCount0.getText()));
			 VPLMIntegTraceUtil.trace(context, ">> processIPackElements mapIPack::"+mapIPack);
			 //START: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
			 if (isCombinedXML) {
	     		hmCombinedxml.put("IPack", mapIPack);
	     	} else {
	     		hmReferencexml.put("IPack", mapIPack);
	     	}
			//END: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
	     }
		 VPLMIntegTraceUtil.trace(context, ">> processIPackElements hmCombinedxml::"+hmCombinedxml);
		 VPLMIntegTraceUtil.trace(context, ">> processIPackElements hmReferencexml::"+hmReferencexml);
	}
	
	/** 
	 * Save TOPS XML - This method reads the Shipper tags value and creates a map 
	 * @param eleRoot
	 */
	private void processShipperElements(Element eleRoot)
	{
		  eleShipper = eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_SHIPPER);
		VPLMIntegTraceUtil.trace(context, ">> processShipperElements eleShipper::"+eleShipper);
		// Shipper - Master Customer Unit information
	     if(eleShipper != null){
	    	  getElementShipperlength();
			  getElementShipperWidth();
			  getElementShipperheight();
			  getElementShipperQuantity();
			
	    	 HashMap mapShipper = new HashMap();
	    	 
	    	 mapShipper.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, (eleShipperlength!= null) ? XSSUtil.decodeFromURL(eleShipperlength.getText()) : "");
	    	 mapShipper.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, (eleShipperwidth!= null) ? XSSUtil.decodeFromURL(eleShipperwidth.getText()) : "");
	    	 mapShipper.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT, (eleShipperheight!= null) ? XSSUtil.decodeFromURL(eleShipperheight.getText()) : "");
			 mapShipper.put(DataConstants.ATTRIBUTE_QUANTITY,XSSUtil.decodeFromURL(eleShipperitemCount0.getText()));
			 VPLMIntegTraceUtil.trace(context, ">> processShipperElements mapShipper::"+mapShipper);
			 //START: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
			 if (isCombinedXML) {
	     		hmCombinedxml.put("Shipper", mapShipper);
	     	} else {
	     		hmReferencexml.put("Shipper", mapShipper);
	     	}
			//END: Added for DTCLD-718: For creation of Combined XML file on Save TOSP XML for revised data
		 }
	     VPLMIntegTraceUtil.trace(context, ">> processShipperElements hmCombinedxml::"+hmCombinedxml);
	     VPLMIntegTraceUtil.trace(context, ">> processShipperElements hmReferencexml::"+hmReferencexml);
	}
	
	/**
	 * Save TOPS XML - This method is wrapper for logic which compares the old xml dimensions with current object for any changes done from enovia
	 * @throws FrameworkException 
	 * @throws Exception 
	 */
	private void compareAndUpdateReferenceXMLValues() throws FrameworkException{
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  compareAndUpdateReferenceXMLValues hmReferencexml "+hmReferencexml);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  compareAndUpdateReferenceXMLValues hmCombinedxml "+hmCombinedxml);
		if(!hmReferencexml.isEmpty() || !hmCombinedxml.isEmpty())
    	{
	    	// business logic for comparing the old xml value with the current object data.
	    	// Returns the values which need to be update on the XML.
	     	Map hmXMLUpdate = comparingReferenceXMLValues();
	     	VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  compareAndUpdateReferenceXMLValues hmXMLUpdate "+hmXMLUpdate);
	     	if(hmXMLUpdate != null && !hmXMLUpdate.isEmpty())
	     	{
	     		Iterator iteratorObjectInfo = hmXMLUpdate.entrySet().iterator();
				while (iteratorObjectInfo.hasNext())
				{
					Map.Entry entry =(Map.Entry)iteratorObjectInfo.next();
					String strKey  = (String)entry.getKey();
					Map mapDimensionInfo = (Map)entry.getValue();
					
					updateValuesInReferenceXMl(strKey,mapDimensionInfo);
					
				}
			}
		}
	}

	/**
	 * Save TOPS XML - This method updates values in Reference/Combined xml tags
	 * @param strKey
	 * @param mapDimensionInfo
	 */
	private void updateValuesInReferenceXMl(String strKey, Map mapDimensionInfo) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  updateValuesInReferenceXMl strKey "+strKey);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  updateValuesInReferenceXMl mapDimensionInfo "+mapDimensionInfo);
		if(UIUtil.isNotNullAndNotEmpty(strKey) && mapDimensionInfo != null && !mapDimensionInfo.isEmpty())
		{
			String strOuterDimensionLength = (String) mapDimensionInfo.get(DataConstants.ATTR_OUTER_DIMENSION_LENGTH);
			String strOuterDimensionWidth = (String) mapDimensionInfo.get(DataConstants.ATTR_OUTER_DIMENSION_WIDTH);
			String strOuterDimensionHeight = (String) mapDimensionInfo.get(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT);
			String strQTYValue = (String) mapDimensionInfo.get(DataConstants.ATTRIBUTE_QUANTITY);
			
			// Updating Pripack Information
			if("PriPack".equalsIgnoreCase(strKey) && elePriPack != null)
			{
				elePriPacklength.setText(strOuterDimensionLength);
				elePriPackwidth.setText(strOuterDimensionWidth);
				elePriPackheight.setText(strOuterDimensionHeight);
				
			}// Update IPack Information
			else if("IPack".equalsIgnoreCase(strKey) && eleIPack != null)
			{
				eleIPacklength.setText(strOuterDimensionLength);
				eleIPackwidth.setText(strOuterDimensionWidth);
				eleIPackheight.setText(strOuterDimensionHeight);
				eleIPackitemCount0.setText(strQTYValue);
				eleIPack.getChild("minItems").setText(strQTYValue);
				eleIPack.getChild("maxItems").setText(strQTYValue);
				
			}// Update Shipper Information
			else if("Shipper".equalsIgnoreCase(strKey) && eleShipper != null)
			{
				eleShipperlength.setText(strOuterDimensionLength);
				eleShipperwidth.setText(strOuterDimensionWidth);
				eleShipperheight.setText(strOuterDimensionHeight);
				eleShipperitemCount0.setText(strQTYValue);
				eleShipper.getChild("minItems").setText(strQTYValue);
				eleShipper.getChild("maxItems").setText(strQTYValue);
				
			}
		}
	}
	
	/**
	 * Method takes the XML information from the old Reference Object, and get the current object information.
	 * Compare the Dimension values and return the Object attribute value if there is any modification done on Enovia Side.
	 * @throws FrameworkException 
	 */
	 
	public Map comparingReferenceXMLValues() throws FrameworkException 
	{
		HashMap<String, Map> hmOutput = new HashMap<>();
		
		String strDimensionLength = "";
		String strDimensionWidth = "";
		String strDimensionHeight = "";
		String strXMLDimensionLength = "";
		String strXMLDimensionWidth = "";
		String strXMLDimensionHeight = "";				
		String strQTYValue = "";				
		String strXMLQTYValue = "";
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  comparingReferenceXMLValues hmReferencexml "+hmReferencexml);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  comparingReferenceXMLValues hmCombinedxml "+hmCombinedxml);
		String strObjectInfo = (isCombinedXML)?(String)hmCombinedxml.get("ObjectInfo"):(String)hmReferencexml.get("ObjectInfo");
		HashMap hmObjectStructure;
		StringList slSelects = new StringList();
		slSelects.add("attribute[" + DataConstants.ATTR_OUTER_DIMENSION_LENGTH + "]");
		slSelects.add("attribute[" + DataConstants.ATTR_OUTER_DIMENSION_WIDTH + "]");
		slSelects.add("attribute[" + DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT + "]");								
		slSelects.add("from["+DataConstants.REL_EBOM+"].attribute[" + DataConstants.ATTRIBUTE_QUANTITY + "]");				
		
		// Get the Object Id from the XML (the BOM Structure Information)
		hmObjectStructure = (HashMap) getObjectIdsFromTheStructureForTOPS(context, strObjectInfo);	
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  comparingReferenceXMLValues hmObjectStructure "+hmObjectStructure);
		if(hmObjectStructure != null && !hmObjectStructure.isEmpty())
		{
			Iterator iteratorObjectInfo = hmObjectStructure.entrySet().iterator();
			while (iteratorObjectInfo.hasNext())
			{
				Entry entry =(Entry)iteratorObjectInfo.next();
				String strKey  = (String)entry.getKey();
				String strValue = (String)entry.getValue();
				if(UIUtil.isNotNullAndNotEmpty(strKey) && UIUtil.isNotNullAndNotEmpty(strValue))
				{
					DomainObject doObj = DomainObject.newInstance(context, strValue);
					Map mapObjectInfo = doObj.getInfo(context, slSelects);
					if(mapObjectInfo != null)
					{
						Map<String, String> mapTemp = new HashMap();
						strDimensionLength = (String)mapObjectInfo.get("attribute[" + DataConstants.ATTR_OUTER_DIMENSION_LENGTH + "]");
						strDimensionWidth = (String)mapObjectInfo.get("attribute[" + DataConstants.ATTR_OUTER_DIMENSION_WIDTH + "]");
						strDimensionHeight = (String)mapObjectInfo.get("attribute[" + DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT + "]");
						strQTYValue = (String)mapObjectInfo.get("from["+DataConstants.REL_EBOM+"].attribute[" + DataConstants.ATTRIBUTE_QUANTITY + "]");								
						
						// Conversion of mm to Inch. and converting to String
						strDimensionLength = String.valueOf(cpd.convertDimensionToInch(strDimensionLength, "mm"));
						strDimensionWidth = String.valueOf(cpd.convertDimensionToInch(strDimensionWidth, "mm"));
						strDimensionHeight = String.valueOf(cpd.convertDimensionToInch(strDimensionHeight, "mm"));
						
						mapTemp.put(DataConstants.ATTR_OUTER_DIMENSION_LENGTH, strDimensionLength);
						mapTemp.put(DataConstants.ATTR_OUTER_DIMENSION_WIDTH, strDimensionWidth);
						mapTemp.put(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT, strDimensionHeight);								
						mapTemp.put(DataConstants.ATTRIBUTE_QUANTITY, strQTYValue);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  comparingReferenceXMLValues mapTemp "+mapTemp);
						HashMap hmPriPack = (isCombinedXML)?(HashMap)hmCombinedxml.get(strKey):(HashMap)hmReferencexml.get(strKey);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  comparingReferenceXMLValues hmPriPack "+hmPriPack);
						if(hmPriPack!=null){
							boolean bnCompareMisMatch = false;
							strXMLDimensionLength = (String)hmPriPack.get(DataConstants.ATTR_OUTER_DIMENSION_LENGTH);
							strXMLDimensionWidth = (String)hmPriPack.get(DataConstants.ATTR_OUTER_DIMENSION_WIDTH);
							strXMLDimensionHeight = (String)hmPriPack.get(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT);
							
							strXMLQTYValue = (String)hmPriPack.get(DataConstants.ATTRIBUTE_QUANTITY);
							
							bnCompareMisMatch =  !strDimensionLength.equalsIgnoreCase(strXMLDimensionLength) || !strDimensionWidth.equalsIgnoreCase(strXMLDimensionWidth) || !strDimensionHeight.equalsIgnoreCase(strXMLDimensionHeight) || (null!=strQTYValue && null!=strXMLQTYValue && !strQTYValue.equalsIgnoreCase(strXMLQTYValue));
							VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  comparingReferenceXMLValues bnCompareMisMatch::"+bnCompareMisMatch);
							// If there is any change from ENOViA side, set the attribute value which will be updated in XML.
							if(bnCompareMisMatch)
								hmOutput.put(strKey, mapTemp);
						}
					}
				}
			}
		}
		return hmOutput;
	}
	
	private MapList getInfoForRelIds(Context context, String[] relIds) throws FrameworkException {
		
		StringList theRelSelects = new StringList();
		theRelSelects.add(DomainConstants.SELECT_TO_ID);
        theRelSelects.add(DomainConstants.SELECT_TO_TYPE);
        
		return DomainRelationship.getInfo(context, relIds, theRelSelects);
	}
	
	/**
	 * Get Object Ids from the XML Passed. XML will contain Type Name Revision of the Object get the object ID and form a HashMap for the same.
	 * @param context
	 * @param objectListInfo
	 * @return HashMap
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	 
	public Map getObjectIdsFromTheStructureForTOPS(Context context, String objectListInfo) throws FrameworkException
	{
		HashMap<String, String> hmObjectStructure = new HashMap<>();

		String strRelId = "";
		if(UIUtil.isNotNullAndNotEmpty(objectListInfo))
		{
			StringList slObjectList = StringUtil.split(objectListInfo, "~");
			for(int i = 0; i < slObjectList.size()-1; i++)
			{
				String strObjectIndividual = slObjectList.get(i);
				if(UIUtil.isNotNullAndNotEmpty(strObjectIndividual))
				{
					StringList slObjectInfo = StringUtil.split(strObjectIndividual, "|");
					if(!slObjectInfo.isEmpty())
					{
						strRelId = slObjectInfo.get(5);
						if(UIUtil.isNotNullAndNotEmpty(strRelId))
						{
							String[] relIds = new String[1];
							relIds[0] = strRelId;
							MapList mlRelInfo = getInfoForRelIds(context,relIds); 
			        		  
							if(!mlRelInfo.isEmpty())
							{
								Map mpRelInfo = (Map) mlRelInfo.get(0);
								String strComponentId = (String)mpRelInfo.get(DomainConstants.SELECT_TO_ID);
								String strComponentType = (String)mpRelInfo.get(DomainConstants.SELECT_TO_TYPE);
								
								if(DataConstants.TYPE_PG_MASTERCUSTOMERUNIT.equalsIgnoreCase(strComponentType))
								{
									hmObjectStructure.put("Shipper", strComponentId);
								}
								else if(DataConstants.TYPE_PG_MASTERINNERPACKUNIT.equalsIgnoreCase(strComponentType))
								{
									hmObjectStructure.put("IPack", strComponentId);
								}
								else if(DataConstants.TYPE_PG_MASTERCONSUMERUNIT.equalsIgnoreCase(strComponentType))
								{
									hmObjectStructure.put("PriPack", strComponentId);
								}
							}
						}
					}
				}
			}
		}
		return hmObjectStructure;
	}	
	
	/**
	 * Save TOPS XML - This method sets the IPM Name in tag strUser1 and strUser4 in Reference/Combined xml
	 * @param eleRoot
	 * @param domIPM
	 * @throws FrameworkException
	 */
	private void setIPMNameInRefXML(Element eleRoot, String strIPMDocName) throws FrameworkException {
		eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_CUBESPEC).getChild(DataConstants.CONSTANT_TAG_STR_USER_1).setText(strIPMDocName);
		eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_CUBESPEC).getChild(DataConstants.CONSTANT_TAG_STR_USER_4).setText(strIPMDocName);
		VPLMIntegTraceUtil.trace(context, ">> setIPMNameInRefXML strUser1 and strUser4 tags updated");
	}
	
	/**
	 * This method sets the BOM info in the extAddlInfo tag in Combined/Simple xml
	 * @param eleRoot
	 * @param objTag
	 */
	private void updateextAddlInfoTag(Element eleRoot, String objTag)
	{
		eleRoot.getChild(DataConstants.CONSTANT_TAG_INFO).getChild(DataConstants.CONSTANT_TAG_EXTADDLINFO).setText(objTag);
		VPLMIntegTraceUtil.trace(context, ">> updateextAddlInfoTag extAddlInfo tag updated");
	}

	/**
	 * Save TOPS XML - This method sets the IPM Name in tag extName in Combined xml
	 * @param eleRoot
	 * @param domIPM
	 * @throws FrameworkException
	 */
	private void setNameInCombinedXML(Element eleRoot, String strIPMDocName) throws FrameworkException {
		eleRoot.getChild(DataConstants.CONSTANT_TAG_INFO).getChild(DataConstants.CONSTANT_TAG_EXTNAME).setText(strIPMDocName);
		VPLMIntegTraceUtil.trace(context, ">> setNameInCombinedXML extName tag updated");
	}
	
	/**
	 * Added for DTCLD-754: This method updates the name and relevant tags of the xml files on Copy Action
	 * @param mpFileInfo
	 * @param strClonedIPMName
	 * @param objTag
	 * @throws Exception
	 */
	public Map updateFileNameAndTagsOnCopy (Map mpFileInfo, String strClonedIPMName, String objTag) throws Exception{
		context = PRSPContext.get();
		Map mpCheckinFiles = new HashMap();
		String wsPath = (String) mpFileInfo.get("directory");
		int noOfFiles = (int) mpFileInfo.get("noOfFiles");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  updateFileNameandTagsonCopy wsPath:: " + wsPath+ "noOfFiles:: "+noOfFiles);
		mpCheckinFiles.put("directory", wsPath);
		int filesForCheckin = 0;
		
		String strSourceFileName = DomainConstants.EMPTY_STRING;
		String strFileFormat = DomainConstants.EMPTY_STRING;
		String strCurrentFileName = DomainConstants.EMPTY_STRING;
		int k = 0;
		fileRoot = new File(wsPath);
				
		for (int i=1; i<=noOfFiles; i++) {
			
			strSourceFileName = (String) mpFileInfo.get(new StringBuilder().append("filename_").append(i).toString());
			strFileFormat = (String) mpFileInfo.get(new StringBuilder().append("fileformat_").append(i).toString());
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  updateFileNameAndTagsOnCopy strFileName:: " + strSourceFileName+" strFileFormat:: "+strFileFormat);
			
			if (strSourceFileName.startsWith(DataConstants.SIMPLE_XML_PREFIX)) {
				
					filesForCheckin++;
					strCurrentFileName = renameFiles(wsPath, strSourceFileName, DataConstants.SIMPLE_XML_PREFIX, strClonedIPMName);
					if (UIUtil.isNotNullAndNotEmpty(strCurrentFileName)) {
						Element eleRoot = saxhandler.getRootElement(fileRoot, strCurrentFileName);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  updateFileNameAndTagsOnCopy eleRoot:: " + eleRoot.getName());
						k++;
						//Update extAddlInfo tag in Simple XML
						updateextAddlInfoTag(eleRoot,objTag);
						//Update extName tag in Simple XML
						setNameInCombinedXML(eleRoot, strClonedIPMName); 
					}
					mpCheckinFiles.put(new StringBuilder().append("filename_").append(k).toString(), strCurrentFileName);
					mpCheckinFiles.put(new StringBuilder().append("fileformat_").append(k).toString(), strFileFormat);
				
			} else if (strSourceFileName.startsWith(DataConstants.REFERENCE_XML_PREFIX)) {
					filesForCheckin++;
					strCurrentFileName = renameFiles(wsPath, strSourceFileName, DataConstants.REFERENCE_XML_PREFIX, strClonedIPMName);
					if (UIUtil.isNotNullAndNotEmpty(strCurrentFileName)) {
						Element eleRoot = saxhandler.getRootElement(fileRoot, strCurrentFileName);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  updateFileNameAndTagsOnCopy eleRoot:: " + eleRoot.getName());
						k++;
						//Update Cubespec comments tag in Reference XML
						eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_CUBESPEC).getChild(DataConstants.CONSTANT_TAG_COMMENTS).setText(objTag);
						//Update strUser1 and strUser4 tag in Reference XML
						setIPMNameInRefXML(eleRoot, strClonedIPMName); 
					}
					mpCheckinFiles.put(new StringBuilder().append("filename_").append(k).toString(), strCurrentFileName);
					mpCheckinFiles.put(new StringBuilder().append("fileformat_").append(k).toString(), strFileFormat);
				
				
			} else if (strSourceFileName.startsWith(DataConstants.COMBINED_XML_PREFIX)) {
				
					filesForCheckin++;
					strCurrentFileName = renameFiles(wsPath, strSourceFileName, DataConstants.COMBINED_XML_PREFIX, strClonedIPMName);
					if (UIUtil.isNotNullAndNotEmpty(strCurrentFileName)) {
						Element eleRoot = saxhandler.getRootElement(fileRoot, strCurrentFileName);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  updateFileNameAndTagsOnCopy eleRoot:: " + eleRoot.getName());
						k++;
						//Update extName tag in Combined XML
						setNameInCombinedXML(eleRoot, strClonedIPMName); 
						//Update extAddlInfo in Combined XML
						updateextAddlInfoTag(eleRoot,objTag);
						//Update Cubespec comments tag in Combined XML
						eleRoot.getChild(DataConstants.CONSTANT_TAG_ANALYSIS_0).getChild(DataConstants.CONSTANT_TAG_CUBESPEC).getChild(DataConstants.CONSTANT_TAG_COMMENTS).setText(objTag);
						//Update strUser1 and strUser4 tag in Combined XML
						setIPMNameInRefXML(eleRoot, strClonedIPMName); 
					}		
					mpCheckinFiles.put(new StringBuilder().append("filename_").append(k).toString(), strCurrentFileName);
					mpCheckinFiles.put(new StringBuilder().append("fileformat_").append(k).toString(), strFileFormat);
			}
				
			saxhandler.writeUpdatedInfoInFile();
		}
		mpCheckinFiles.put("noOfFiles", filesForCheckin);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  updateFileNameAndTagsOnCopy mpCheckinFiles:: " + mpCheckinFiles);
		return mpCheckinFiles;
	}
	
	/**
	 * Method to rename the xml files
	 */	
	public String renameFiles (String wsPath, String oldFileName, String sFilePrefix, String strClonedIPMName) {
		String strFileNewName = DomainConstants.EMPTY_STRING;
		boolean isFileRenamed = false;
		
		if(oldFileName.startsWith(DataConstants.SIMPLE_XML_PREFIX) || oldFileName.startsWith(DataConstants.REFERENCE_XML_PREFIX) || oldFileName.startsWith(DataConstants.COMBINED_XML_PREFIX)) {
			strFileNewName = new StringBuilder().append(sFilePrefix).append(DataConstants.SEPARATOR_UNDERSCORE).append(strClonedIPMName).append(DataConstants.CONSTANT_DOT).append(DataConstants.FILE_FORMAT_XML).toString();
		} else {
			strFileNewName = new StringBuilder().append(sFilePrefix).append(DataConstants.SEPARATOR_UNDERSCORE).append(strClonedIPMName).append(DataConstants.CONSTANT_DOT).append(DataConstants.FILE_FORMAT_PDF).toString();
		}
		
		//Update the name of the files
		if(UIUtil.isNotNullAndNotEmpty(strFileNewName)) {
			File oldFile = new File(new StringBuilder().append(wsPath).append(DataConstants.FORWARD_SLASH).append(oldFileName).toString());
			File newFile = new File(new StringBuilder().append(wsPath).append(DataConstants.FORWARD_SLASH).append(strFileNewName).toString());
			isFileRenamed=oldFile.renameTo(newFile);
		    VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  renameXMLFiles isFileRenamed:: "+isFileRenamed);
		}
		
	    return (isFileRenamed)?strFileNewName:DomainConstants.EMPTY_STRING;
	}
	
	/**
	 * @return
	 */
	private Element getElementPriPacklength() {
		elePriPacklength = elePriPack.getChild("minLength");
		return elePriPacklength;
	}
	
	/**
	 * @return
	 */
	private Element getElementPriPackWidth() {
		elePriPackwidth = elePriPack.getChild("minWidth");
		return elePriPackwidth;
	}
	
	/**
	 * @return
	 */
	private Element getElementPriPackheight() {
		elePriPackheight = elePriPack.getChild("minHeight");
		return elePriPackheight;
	}	
	
	/**
	 * @return
	 */
	private Element getElementIPacklength() {
		eleIPacklength = eleIPack.getChild("db").getChild("length");
		return eleIPacklength;
	}
	
	private Element getElementIPackWidth() {
		eleIPackwidth = eleIPack.getChild("db").getChild("width");
		return eleIPackwidth;
	}
	
	private Element getElementIPackheight() {
		eleIPackheight = eleIPack.getChild("db").getChild("height");
		return eleIPackheight;
	}
	
	private Element getElementIPackQuantity() {
		eleIPackitemCount0 = eleIPack.getChild("itemCount0");
		return eleIPackitemCount0;
	}
	
	private Element getElementShipperlength() {
		eleShipperlength = eleShipper.getChild("db").getChild("length");
		return eleShipperlength;
	}
	
	private Element getElementShipperWidth() {
		eleShipperwidth = eleShipper.getChild("db").getChild("width");
		return eleShipperwidth;
	}
	
	private Element getElementShipperheight() {
		eleShipperheight = eleShipper.getChild("db").getChild("height");
		return eleShipperheight;
	}
	
	private Element getElementShipperQuantity() {
		eleShipperitemCount0 = eleShipper.getChild("itemCount0");
		return eleShipperitemCount0;
	}
	
	public Boolean getHasOne() {
		return hasOne;
		
	}
	
}
