package com.pg.designtools.integrations.datahandlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.StackingPattern;
import com.pg.designtools.datamanagement.TransportUnitPart;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.integrations.tops.Bundle;
import com.pg.designtools.integrations.tops.Dims;
import com.pg.designtools.integrations.tops.IPack;
import com.pg.designtools.integrations.tops.IPackInfo;
import com.pg.designtools.integrations.tops.Input;
import com.pg.designtools.integrations.tops.MCOP;
import com.pg.designtools.integrations.tops.MCUP;
import com.pg.designtools.integrations.tops.MIP;
import com.pg.designtools.integrations.tops.Output;
import com.pg.designtools.integrations.tops.OutputIPack;
import com.pg.designtools.integrations.tops.OutputInfo;
import com.pg.designtools.integrations.tops.OutputPriPack;
import com.pg.designtools.integrations.tops.OutputShipper;
import com.pg.designtools.integrations.tops.OutputUnitload;
import com.pg.designtools.integrations.tops.OutputUnitloadInfo;
import com.pg.designtools.integrations.tops.PriPack;
import com.pg.designtools.integrations.tops.PriPackInfo;
import com.pg.designtools.integrations.tops.Root;
import com.pg.designtools.integrations.tops.RootInfo;
import com.pg.designtools.integrations.tops.Shipper;
import com.pg.designtools.integrations.tops.ShipperInfo;
import com.pg.designtools.integrations.tops.TUP;
import com.pg.designtools.integrations.tops.Unitload;
import com.pg.designtools.integrations.tops.UnitloadInfo;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringItr;
import matrix.util.StringList;

public class SimpleXML {
	private XStream xstream;
	private Root root;
	private File simpleXMLFile;
	private Shipper inputshipper;
	private Unitload inputUnitload;
	private PriPack inputPriPack;
	private IPack inputIPack;
	private OutputShipper outputShipper;
	private OutputUnitload outputUnitload;
	private OutputPriPack outputPriPack;
	private OutputIPack outputIPack;
	private MCOP mcop;
	private MCUP mcup;
	private TUP tup;
	private MIP mip;
	DomainObject domExistingTUPObj;

	String strTUPBOMInfo;
	DomainObject domSPS = null;
	String strExtDesignDesc;
	String strExtSecPackConfig;

	Input input;
    File xmlFile ;
    String fullFilePath;

	DataConstants.customTOPSExceptions errorInvalidSPSName = DataConstants.customTOPSExceptions.ERROR_400_INVALID_SPS_NAME;
	DataConstants.customTOPSExceptions errorInvalidSPSOrigination = DataConstants.customTOPSExceptions.ERROR_400_INVALID_SPS_ORIGINATION;
	DataConstants.customTOPSExceptions errorNoRootTagPresent = DataConstants.customTOPSExceptions.ERROR_400_NO_ROOT_TAG_IN_XML;
	DataConstants.customTOPSExceptions errorNoModifyAccessOnSPS = DataConstants.customTOPSExceptions.ERROR_400_NO_MODIFY_ACCESS_ON_SPS;
	DataConstants.customTOPSExceptions errorDiffInDBXMLAttrValues = DataConstants.customTOPSExceptions.ERROR_400_ATTR_VALUE_DIFFERENCE_IN_DB_XML_UNITTYPE;
	
	public SimpleXML() {
		super();
	}

	public SimpleXML(File fileToBeUploaded) {
		simpleXMLFile = fileToBeUploaded;
		xstream = new XStream();
		xstream.registerConverter(new BooleanConverter("1", "0", false));
		xstream.ignoreUnknownElements();
	}

	public void getRoot() throws FileNotFoundException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> getRoot START");
		try {
			xstream.processAnnotations(Root.class);
			Class<?>[] classes = new Class[] { Root.class, RootInfo.class, Input.class,Output.class, Shipper.class, PriPack.class, Unitload.class,IPack.class,Dims.class,Bundle.class,PriPackInfo.class,IPackInfo.class,ShipperInfo.class,UnitloadInfo.class,OutputInfo.class,OutputIPack.class,OutputPriPack.class,OutputShipper.class,OutputUnitload.class,OutputUnitloadInfo.class};
			XStream.setupDefaultSecurity(xstream);
			xstream.allowTypes(classes);
			root = (Root) xstream.fromXML(new FileReader(simpleXMLFile));
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> getRoot END");
		}
		catch(Exception e) {
			VPLMIntegTraceUtil.trace(PRSPContext.get(), e.getMessage());
			throw new DesignToolsIntegrationException(errorNoRootTagPresent.getExceptionCode(),
					errorNoRootTagPresent.getExceptionMessage());
		}
	}

	void validateXML(DomainObject domSPSObj) throws FileNotFoundException, MatrixException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>> validateXML simple START");
		getRoot();
		RootInfo info = root.getInfo();
		strTUPBOMInfo = info.getExtAddlInfo();
		strExtDesignDesc = info.getExtDesignDesc();
		strExtSecPackConfig = info.getExtSecPackConfig();
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>> validateXML simple strTUPBOMInfo " + strTUPBOMInfo);

		String strextName = getXMLExtName();
		
		if (strextName.contains(".") || strextName.contains("_")) {
			strextName = strextName.substring(0, strextName.length() - 4);
			strextName = strextName.replaceAll("\\s", "");
		}

		domSPS = domSPSObj;
		
		if(!domSPS.getInfo(PRSPContext.get(),DomainConstants.SELECT_NAME).equalsIgnoreCase(strextName)) {
			throw new DesignToolsIntegrationException(errorInvalidSPSName.getExceptionCode(),
					errorInvalidSPSName.getExceptionMessage());
		}
		if (!isSPSObjectValid()) {
			throw new DesignToolsIntegrationException(errorInvalidSPSName.getExceptionCode(),
					errorInvalidSPSName.getExceptionMessage());
		}
	}

	public DomainObject getSPSObjectFromXML() throws FileNotFoundException, MatrixException {
		DomainObject domSPSObjFromXML = null;
		getRoot();
		RootInfo info = root.getInfo();
		String strSPSName = info.getExtName();
		String strSPSNameFromXML = "";
		if(UIUtil.isNullOrEmpty(strSPSName))
		{
			throw new DesignToolsIntegrationException(errorInvalidSPSName.getExceptionCode(),
					errorInvalidSPSName.getExceptionMessage());
		}
		if (strSPSName.contains(".") || strSPSName.contains("_")) {
			strSPSNameFromXML = strSPSName.substring(0, strSPSName.length() - 4);
			strSPSNameFromXML = strSPSNameFromXML.replaceAll("\\s", "");
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>getSPSObjectFromXML  strSPSNameFromXML " + strSPSNameFromXML);
		domSPSObjFromXML = CommonUtility.getSPSFromTNR(DataConstants.TYPE_PG_STACKINGPATTERN, strSPSNameFromXML,
				DataConstants.CONSTANT_FIRST_REVISION);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>getSPSObjectFromXML  domSPSObjFromXML " + domSPSObjFromXML);
		if(domSPSObjFromXML==null) {
			throw new DesignToolsIntegrationException(errorInvalidSPSName.getExceptionCode(),
					errorInvalidSPSName.getExceptionMessage());
		}
		
		BusinessObject busLastRevision = domSPSObjFromXML.getLastRevision(PRSPContext.get());
		BusinessObject busLatestRevSPS = new BusinessObject(DataConstants.TYPE_PG_STACKINGPATTERN, strSPSNameFromXML,
				busLastRevision.getRevision(), DataConstants.VAULT_ESERVICE_PRODUCTION);
		domSPSObjFromXML = DomainObject.newInstance(PRSPContext.get(), busLatestRevSPS);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>getSPSObjectFromXML  domSPSObjFromXML " + domSPSObjFromXML);
		return domSPSObjFromXML;
	}

	boolean isSPSObjectValid() throws MatrixException {
		boolean isSPSObjectvalid = false;
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>> isSPSObjectValid simple  XML domSPSObj " + domSPS);
		if (domSPS != null) {
			isSPSObjectvalid = true;
			String strSPSOrigination = domSPS.getAttributeValue(PRSPContext.get(),
					DataConstants.ATTR_PG_SPS_ORIGINATION);

			if (DataConstants.RANGE_VALUE_MANUAL.equals(strSPSOrigination)) {
				throw new DesignToolsIntegrationException(errorInvalidSPSOrigination.getExceptionCode(),
						errorInvalidSPSOrigination.getExceptionMessage());
			}
		}
		return isSPSObjectvalid;
	}

	boolean isTUPBOMPresent() throws FrameworkException {
		boolean isTUPBOMPresent = false;

		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>> isTUPBOMPresent domExistingTUPObj " + domExistingTUPObj);
		if (domExistingTUPObj != null) {
			String strPartId = domExistingTUPObj.getInfo(PRSPContext.get(),
					"from[" + DomainConstants.RELATIONSHIP_EBOM + "].to.id");
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>> isTUPBOMPresent strPartId " + strPartId);
			if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
				isTUPBOMPresent = true;
			}
		}
		return isTUPBOMPresent;
	}
	
	void process() throws Exception {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>>>>>>> process simple START ");
		getInputOutput();

		StackingPattern sps = new StackingPattern();
		domExistingTUPObj = sps.getRelatedTUP(domSPS);

		HashMap<String, String> attrSPSMap = new HashMap<>();
		attrSPSMap.put(DataConstants.ATTR_PG_SECONDARY_PACK_INFO, strExtSecPackConfig);
		
		StringList slModificationAccessList = new StringList();
		slModificationAccessList.add(DataConstants.CONSTANT_ACCESS_MODIFY);
		boolean hasAccess = FrameworkUtil.hasAccess(PRSPContext.get(), domSPS,
				slModificationAccessList);
		if (!hasAccess) {
			throw new DesignToolsIntegrationException(errorNoModifyAccessOnSPS.getExceptionCode(),
					errorNoModifyAccessOnSPS.getExceptionMessage());
		}
		
		sps.setAttributes(attrSPSMap);
		domSPS.setDescription(PRSPContext.get(), strExtDesignDesc);

		TransportUnitPart objTUP = new TransportUnitPart(domSPS, domExistingTUPObj);

		if (!isTUPBOMPresent()) {
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>> process isTUPBOMPresent not present");
			objTUP.createAndConnectTUPBOMStructure(inputshipper, inputUnitload, inputPriPack, inputIPack);
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>> process strTUPBOMInfo " + strTUPBOMInfo);
		if (UIUtil.isNotNullAndNotEmpty(strTUPBOMInfo)) {
			objTUP.updateAttributesOnTUPBOMWithExtAddlInfo(SimpleXML.getExtAddlInfo(strTUPBOMInfo), this);
		} else {
			getBOMStructureFromTUP(objTUP, domExistingTUPObj);
		}
		
		//INC10305877 DTCLD-364 Start
		if(UIUtil.isNotNullAndNotEmpty(objTUP.getSbAlertMessageForDiffFoundInAttrValue())) {
			throw new DesignToolsIntegrationException(errorDiffInDBXMLAttrValues.getExceptionCode(),objTUP.getSbAlertMessageForDiffFoundInAttrValue());	
		}
		//INC10305877 DTCLD-364 End
	}

	public void getBOMStructureFromTUP(TransportUnitPart objTUP, DomainObject domExistingTUPObj)
			throws IOException, MatrixException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> getBOMStructureFromTUP domExistingTUPObj " + domExistingTUPObj);
		Pattern typePatterns = new Pattern(DataConstants.TYPE_PG_MASTERCUSTOMERUNIT);
		typePatterns.addPattern(DataConstants.TYPE_PG_MASTERINNERPACKUNIT);
		typePatterns.addPattern(DataConstants.TYPE_PG_MASTERCONSUMERUNIT);

		StringList slObjList = new StringList();
		slObjList.add(DomainConstants.SELECT_ID);
		slObjList.add(DomainConstants.SELECT_TYPE);
		slObjList.add(DomainConstants.SELECT_NAME);
		slObjList.add(DomainConstants.SELECT_REVISION);

		StringList slRelList = new StringList();
		slRelList.add(DomainRelationship.SELECT_ID);

		MapList mlTUPBOM = domExistingTUPObj.getRelatedObjects(PRSPContext.get(), // Context
				DomainConstants.RELATIONSHIP_EBOM, // Relationship Pattern
				typePatterns.getPattern(), // Type Pattern
				slObjList, // Object Selects
				slRelList, // Relationship Selects
				false, // get TO
				true, // get From
				(short) 0, // Recurrence Level
				null, // Object Where
				null, // RelationShip Where
				0);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> getBOMStructureFromTUP mlTUPBOM " + mlTUPBOM);

		int nTUPBOMSize = mlTUPBOM.size();
		Map<String, String> mpPartInfo;
		String strRelId = "";
		
		objTUP.updateAttributesonTUPBOMStructure(domExistingTUPObj, this, DataConstants.TYPE_PG_TRANSPORTUNIT,
				strRelId);

		for (int i = 0; i < nTUPBOMSize; i++) {
			mpPartInfo = (Map) mlTUPBOM.get(i);
			strRelId = mpPartInfo.get(DomainRelationship.SELECT_ID);
			BusinessObject bo = new BusinessObject(mpPartInfo.get(DomainConstants.SELECT_TYPE),
					mpPartInfo.get(DomainConstants.SELECT_NAME), mpPartInfo.get(DomainConstants.SELECT_REVISION),
					DataConstants.VAULT_ESERVICE_PRODUCTION);

			objTUP.updateAttributesonTUPBOMStructure(bo, this, mpPartInfo.get(DomainConstants.SELECT_TYPE), strRelId);
		}
	}

	public static Map<String, StringList> getExtAddlInfo(String infoTUP) {
		Map<String, StringList> mapExtAddlInfo = new HashMap<>();
		
		if (UIUtil.isNotNullAndNotEmpty(infoTUP)) {
			StringList slObjList = StringUtil.split(infoTUP, "~");
			int nObjListSizeOriginal = slObjList.size();
			StringItr itrObj = new StringItr(slObjList);
			String sType = "";
			StringList tempObjData;
			String nextRelId = "";
			String tempRelId = "";
			int nObjListSize = 0;
			while (itrObj.next()) {
				tempObjData = StringUtil.split(itrObj.value(), "|");
				sType = tempObjData.elementAt(1);
				nObjListSize++;
				if(nObjListSize==1) {
					nextRelId = tempObjData.getElement(tempObjData.size()-1);
					tempObjData.set(tempObjData.size()-1, "");
				}else if(nObjListSize<nObjListSizeOriginal) {
					tempRelId = tempObjData.getElement(tempObjData.size()-1);
					tempObjData.set(tempObjData.size()-1, nextRelId);	
					nextRelId = tempRelId;
				}else{
					tempObjData.add(tempObjData.size(), nextRelId);
				}
				mapExtAddlInfo.put(sType, tempObjData);
			}
		}
		return mapExtAddlInfo;
	}

	public void getInputOutput() {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>> getInputOutput simple START ");
		// process input
		Input inputTag = root.getInput();
		if (inputTag != null) {
			inputshipper = inputTag.getShipper();
			inputUnitload = inputTag.getUnitload();
			inputPriPack = inputTag.getPriPack();
			inputIPack = inputTag.getiPack();
		}

		// process output
		Output output = root.getOutput();
		if (output != null) {
			outputShipper = output.getShipper();
			outputUnitload = output.getUnitload();
			outputPriPack = output.getPriPack();
			outputIPack = output.getiPack();
			mcop = output.getEmMCOP();
			mcup =output.getEmMCUP();
			tup = output.getEmTUP();
			mip = output.getEmMIP();
		}		
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>>>>>> getInputOutput simple END ");
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	public Shipper getInputshipper() {
		return inputshipper;
	}

	public Unitload getInputUnitload() {
		return inputUnitload;
	}

	public PriPack getInputPriPack() {
		return inputPriPack;
	}

	public IPack getInputIPack() {
		return inputIPack;
	}

	public OutputShipper getOutputShipper() {
		return outputShipper;
	}

	public OutputUnitload getOutputUnitload() {
		return outputUnitload;
	}

	public OutputPriPack getOutputPriPack() {
		return outputPriPack;
	}

	public OutputIPack getOutputIPack() {
		return outputIPack;
	}

	public MCOP getMCOP() {
		return mcop;
	}

	public MCUP getMCUP() {
		return mcup;
	}

	public TUP getTUP() {
		return tup;
	}

	public MIP getMIP() {
		return mip;
	}

	public String getXMLExtName() {
		return root.getInfo().getExtName();
	}
	
	public String getStrExtDesignDesc() {
		return strExtDesignDesc;
	}
	
	//Save TOPS XML Code start
	/**
	 * Save TOPS XML - This method creates Root's Info tag in IPS_import xml
	 * @param context
	 * @param docName
	 * @param objTag
	 * @param domStackingPatternObj
	 * @throws FrameworkException
	 */
	public void createRootInfoTag(Context context, String docName,String objTag,DomainObject domStackingPatternObj) throws FrameworkException {									
		StringList slSPSSelectables = new StringList();
		slSPSSelectables.add(DomainConstants.SELECT_DESCRIPTION);
		slSPSSelectables.add(DataConstants.SELECT_ATTR_PG_SECONDARY_PACK_INFO);
		String strpgSPSDescription = null;
		String strpgSecondaryPackConfig = null;
		Map mpSPSObjDetails = domStackingPatternObj.getInfo(context,slSPSSelectables);
		if(mpSPSObjDetails !=null){
			strpgSPSDescription = (String)mpSPSObjDetails.get(DomainConstants.SELECT_DESCRIPTION);
			strpgSecondaryPackConfig = (String)mpSPSObjDetails.get(DataConstants.SELECT_ATTR_PG_SECONDARY_PACK_INFO);
		}
		
		xstream = new XStream();
		xstream.processAnnotations(Root.class);
		xstream.registerConverter(new BooleanConverter(DataConstants.CONSTANT_ONE,DataConstants.CONSTANT_ZERO,false));							
		root = new Root(DataConstants.CONSTANT_P_AND_G, DataConstants.CONSTANT_TXML);				
		RootInfo rootInfo = new RootInfo(DataConstants.CONSTANT_NEW, DataConstants.CONSTANT_PGEN1, DataConstants.CONSTANT_PACKAGE_DESIGN, DataConstants.TYPE_PGIPMDOCUMENT, docName, DataConstants.CONSTANT_REVISION_ZERO, objTag, strpgSPSDescription,strpgSecondaryPackConfig);
		root.setInfo(rootInfo);	
		input = new Input();
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createRootInfoTag END ");
	}
	
	public Input getinputTag() {
		return input;
	}
	
	/**
	 * save TOPS XML - This method writes the content in IPS_Import xml file
	 * @param fileRoot
	 * @param file
	 * @throws IOException
	 */
	public void writeContentInXMlFile(File fileRoot,String file) throws IOException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>  writeContentInXMlFile Start "); 
		root.setInput(input);						
		String fileGen = xstream.toXML(root);
		byte[] contentInBytes = fileGen.getBytes();

		// write the content into xml file			
		fullFilePath = fileRoot.getAbsolutePath() +java.io.File.separator + file;
		xmlFile = new File(fullFilePath);
		
		try (FileOutputStream fileOutputStream = new FileOutputStream(xmlFile)) {
			fileOutputStream.write(contentInBytes);		
		} catch (IOException e) {
			VPLMIntegTraceUtil.trace(PRSPContext.get(), e.getMessage());
		}		
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>  writeContentInXMlFile END ");
	}
	
	/**save TOPS XML
	 * @return fullFilePath
	 */
	public String getfullFilePath() {
		return fullFilePath;
	}
	
	/**save TOPS XML
	 * @return XML File Name
	 */
	public String getFileName() {
		String strXMLFileName = "";
		if(xmlFile!=null) {
			strXMLFileName = xmlFile.getName();
		}
		return strXMLFileName;
	}
}
