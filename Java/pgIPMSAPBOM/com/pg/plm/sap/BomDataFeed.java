package com.pg.plm.sap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.plm.util.BOMArrayComparator;
import com.pg.plm.util.DateUtil;
//import com.pg.plm.util.MatrixContext;
import com.pg.plm.util.PLMConstants;
import com.pg.plm.util.SubRowsComparator;


import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.MQLCommand;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;
/**
 * 
 * @author komatineni.vn
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 * 
 * System.out.println("MAT NUMBER: " + "0000000000"+saBOMData[i][MATERIAL_NUMBER]);
					tTemp.setValue("0000000000"+saBOMData[i][MATERIAL_NUMBER], "MATNR"); //Material Number
					tTemp.setValue(saBOMData[i][BOM_USAGE], "STLAN"); // BOM Usage
					tTemp.setValue(saBOMData[i][VALID_FROM_DATE_PARENT], "DATUV"); // Parent Effective Date
					tTemp.setValue(saBOMData[i][ALT_BOM], "STLAL"); // ALT BOM
					tTemp.setValue("", "WERKS"); // PLANT -- not used
					tTemp.setValue(saBOMData[i][BOM_TEXT], "STKTX"); // ALT BOM TEXT
					tTemp.setValue(saBOMData[i][ALT_BOM_TEXT], "ZTEXT"); // ALT BOM TEXT

					tTemp.setValue(saBOMData[i][CONFIRMED_QUANTITY], "BMENG"); // BOM Base Qty
					tTemp.setValue(saBOMData[i][BOM_STATUS], "STLST"); // BOM STATUS
					tTemp.setValue("0000000000"+saBOMData[i][COMPONENT_NAME], "IDNRK"); // Comp Name
					tTemp.setValue(saBOMData[i][ITEM_CATEGORY], "POSTP"); // Item Category
					tTemp.setValue((i+1)*10, "POSNR"); // Incrementing Number
					tTemp.setValue(saBOMData[i][VALID_FROM_DATE_CHILD], "DATUV_I"); // Child Effective Date
					tTemp.setValue(saBOMData[i][SORT_STRING], "SORTF"); // Position Indicator
					tTemp.setValue(saBOMData[i][SUBSTITUTE_FLAG], "ALPGR"); // Alt Item Group -- Substitute combination numbers AA-ZZ
					tTemp.setValue(saBOMData[i][USAGE_PROBABILITY], "EWAHR"); // Usage Probability
					tTemp.setValue(saBOMData[i][RANGE_FLAG], "ZRANGE_FLAG"); // Range Flag
					tTemp.setValue(saBOMData[i][TARGET], "MENGE"); // Component Quantity -- BOM Target?
					tTemp.setValue(saBOMData[i][UPPER_LIMIT], "ZUPPER_LIMIT"); // Upper Limit -- Max?
					tTemp.setValue(saBOMData[i][QUANTITY], "ZTARGET_LIMIT"); // Target --- Target?
					tTemp.setValue(saBOMData[i][LOWER_LIMIT], "ZLOWER_LIMIT"); // Lower Limit -- Min?

 */



public class BomDataFeed {
	public static final int MATERIAL_NUMBER = 0;
	public static final int BOM_USAGE = 1;
	public static final int ALT_BOM = 2;
	public static final int VALID_FROM_DATE_PARENT = 3;
	public static final int BOM_TEXT = 4;
	public static final int ALT_BOM_TEXT = 5;
	public static final int CONFIRMED_QUANTITY = 6;
	public static final int BOM_STATUS = 7;
	public static final int BOM_ITEM_NUMBER = 8;
	public static final int COMPONENT_NAME = 9;
	public static final int QUANTITY = 10;
	public static final int ITEM_CATEGORY = 11;
	public static final int VALID_FROM_DATE_CHILD = 12;
	public static final int SORT_STRING = 13;
	//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
	public static final int OPT_COMPONENT = 24;
	//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
	public static final int SUBSTITUTE_FLAG = 14;
	public static final int USAGE_PROBABILITY = 15;
	public static final int RANGE_FLAG = 16;
	public static final int UPPER_LIMIT = 17;
	public static final int TARGET = 18;
	public static final int LOWER_LIMIT = 19;
	public static final int FIND_NUMBER = 20;
	public static final int PHASE_NAME = 21;
	//Modified by PLM V2 to add OBJECT_ID in result
	//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
	//Modified by Mani on 6/20/2012 to fix SAP BOM Issues - Start	
	//public static final int BOM_ARRAY_SIZE = 24;
	public static final int BOM_ARRAY_SIZE = 25;	
	//Modified by Mani on 6/20/2012 to fix SAP BOM Issues - End
	//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
	public static final int OBJECT_ID = 22;
	//Modification ends
	//Added by Mani on 6/20/2012 to fix SAP BOM Issues - Start
	public static final int DISPLAY_OBJECT_ID = 23;
	//Added by Mani on 6/20/2012 to fix SAP BOM Issues - End
	public static final String DUMMY_PLANT = "3151"; 

	public static final String VALUE_ALT_BOM = "ZZ";
	public static final String VALUE_ALT_BOM_TEXT = "";
	public static final String VALUE_BOM_TEXT = "";
	public static final String VALUE_BOM_STATUS = "1";
	public static final String VALUE_ITEM_CATEGORY = "L";
	public static final String VALUE_USAGE_PROBABILITY = "100";
	public static final String VALUE_SUBSTITUTE_FLAG = "X";
	public static final String VALUE_RANGE_FLAG = "X";
	public static final String PGPHASE_EXPAND_TYPES = "pgFinishedProduct,pgFormulatedProduct,pgRawMaterial,pgPackingMaterial,pgPackingSubassembly";

	public static final String TYPE_DESIGNATED_SITES = "pgSAPDesignatedSites";
	public static final String NAME_DESIGNATED_SITES = "DesignatedSitesObj";
	public static final String REV_DESIGNATED_SITES = "-"; 
	
	//Modified by PLM V2 - Changed Access Modifier from Private to public as in application, private is not accessible 
	public String sType = "";
	public String sName = "";
	public String sRev = "";
	public String _originator;
	
	public String sParentGCAS = "";
	public String sParentRev = "";
	
	public String sSapMessage = "";
	public boolean bFailure = false;
	public Context _ctx = null;
	public ArrayList alBOM = new ArrayList();
	public boolean bAbortBOM = false;
	
	public String _CurrentObject;
	public String _ConversationId;
	public String _SapLogRequestId = "";
	public String _requestTypeLog = "SAP BOM Data Feed"; 
	public String _system = "PLM SAP BOM FEED";
	public String _Response;
	public String _requestUser = "PLM SAP BOM FEED";
	public long _progress;
	public String _progressDesc; 
	public String _ErrorMessage;
	
	public boolean _bExecuteRFC = true;
	public boolean _bPrintData = true;
	public boolean _bSitesListActive = false;
	public String _DesignatedSites = "";
	public String _AbortReason = "";
	public String _BOMUsage = "0";
	public String _PlantString = "";
	
	public char _cAltItem1 = '9';
	public char _cAltItem2 = 'A';
	public String _ParentMatlNumber = "";
	
	//Added by Mani on 6/20/2012 to fix SAP BOM Issues - Start
	private boolean bCreatingSubsRows = false;
	private String strSubsDispId = "";
	//Added by Mani on 6/20/2012 to fix SAP BOM Issues - End
	
	//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
	String strFirstlevelObjId = "";
	String strFirstlevelObjIdCopy = "";
	HashMap hmParentObjects = new HashMap();
	HashMap hmChildObjects = new HashMap();
	int iChildObjCount =0;
	//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
	
	//Commented below constructor by PLM V2 team - As we will use context sent by JPO and not use MatrixContext class
	/*public BomDataFeed(String sType, String sName, String sRev) {
		this.sType = sType;
		this.sName= sName;
		this.sRev = sRev;
		
		sParentGCAS = sName;
		sParentRev = sRev;
		
		_CurrentObject = sName;
		_ConversationId = "BomFeed_" + System.currentTimeMillis();
		
		_progress = 1000;
		_progressDesc = "BomDataFeed initializing";
		try {
			this._ctx = MatrixContext.getIntegrationMatrixContext();
			_requestUser = _ctx.getUser();
			
			//Since the SAP BOM feed is now triggered from CSS, the type is wrong
			//Need to find the correct object to trigger
			StringBuffer sbMQL = new StringBuffer();
			sbMQL.append("temp query bus \"Part,Product Data\" ");
			sbMQL.append(sName).append(" ").append(sRev).append(" select type dump |;");
			
			MQLCommand mqlCommand = new MQLCommand();
			mqlCommand.executeCommand(_ctx, sbMQL.toString());
			
			String sResults = mqlCommand.getResult();
			if (!sType.equals(""))
				sType = sResults.substring(0, sResults.indexOf("|"));
			this.sType = sType;	
		} catch (Exception e) {
			_ErrorMessage = e.toString();
			e.printStackTrace();
		}
		
	}*/
	
	public BomDataFeed(Context ctx, String sType, String sName, String sRev) {
		this.sType = sType;
		this.sName= sName;
		this.sRev = sRev;
		
		sParentGCAS = sName;
		sParentRev = sRev;
		
		_CurrentObject = sName;
		_ConversationId = "BomFeed_" + System.currentTimeMillis();
		
		_progress = 1000;
		_progressDesc = "BomDataFeed initializing";
		try {
			this._ctx = ctx;
			_requestUser = _ctx.getUser();
			
			//Since the SAP BOM feed is now triggered from CSS, the type is wrong
			//Need to find the correct object to trigger
			StringBuffer sbMQL = new StringBuffer();
			sbMQL.append("temp query bus Part ");
			sbMQL.append(sName).append(" ").append(sRev).append(" select type dump |;");
			
			MQLCommand mqlCommand = new MQLCommand();
			mqlCommand.executeCommand(_ctx, sbMQL.toString());
			
			String sResults = mqlCommand.getResult();
			if (!sType.equals(""))
				sType = sResults.substring(0, sResults.indexOf("|"));
			this.sType = sType;	
		} catch (Exception e) {
			_ErrorMessage = e.toString();
			e.printStackTrace();
		}
		
	}
		
	public void setExecuteBoolean(boolean b) {
		this._bExecuteRFC = b;
	}
	
	public void setPrintData(boolean b) {
		this._bPrintData = b;
	}
	
	public String[] getPlantData(String TSType, String TSName, String TSRev) {
		String[] saReturn = null;
		try {
			Vector vPlants = new Vector();
			
			StringList slSelectables = new StringList();
			slSelectables.add("attribute[Status]");
			slSelectables.add("to[Manufacturing Responsibility].from.name");
			slSelectables.add("to[Manufacturing Responsibility].attribute[pgIsAuthorizedtoProduce]");
			slSelectables.add("to[Manufacturing Responsibility].attribute[pgIsActivated]");
			BusinessObject boTS = new BusinessObject(TSType, TSName, TSRev,
				"eService Production");
			boTS.open(_ctx);
			BusinessObjectWithSelect bows = boTS.select(_ctx, slSelectables);
			boTS.close(_ctx);
			StringList slPlants = bows.getSelectDataList("to[Manufacturing Responsibility].from.name");
			StringList slAuthorized = bows.getSelectDataList("to[Manufacturing Responsibility].attribute[pgIsAuthorizedtoProduce]");
			StringList slActivated = bows.getSelectDataList("to[Manufacturing Responsibility].attribute[pgIsActivated]");
			String sStatus = bows.getSelectData("attribute[Status]");
			
			if (sStatus.equalsIgnoreCase("planning")) {
				saReturn = new String[1];
				saReturn[0] = DUMMY_PLANT;
			} else {
				if (slPlants != null) {
					Iterator itrPlants = slPlants.iterator();
					Iterator itrAuthorized = slAuthorized.iterator();
					Iterator itrActivated = slActivated.iterator();
					
					while (itrPlants.hasNext()) {
						String sPlant = (String) itrPlants.next();
						String sAuthorized = (String) itrAuthorized.next();
						
						if (sAuthorized.equalsIgnoreCase("true"))
							vPlants.add(sPlant.substring(sPlant.indexOf("~")+1));
					}
					
					saReturn = new String[vPlants.size()];
					Iterator itrAuthPlants = vPlants.iterator();
					
					int i=0;
					while (itrAuthPlants.hasNext()) {
						String sTemp = (String) itrAuthPlants.next();
						if(!_PlantString.equals("")) {
							_PlantString = _PlantString + "," + sTemp;
						} else {
							_PlantString = sTemp;
						}
						saReturn[i++] = sTemp;
					}
					
					//Truncate this string so it fits in log db
					if (_PlantString.length() > 255) {
						_PlantString = _PlantString.substring(0,254);
					}
				}
		
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return saReturn;
	}


	
	public String[][] getBOMData(String TSType, String TSName,
			String TSRev) {
		String[][] saBOM = null;
		
		try {
			String sValidFromDate = "";
			String sBOMBaseQty = "";
			String sBOMUsage = "";

			StringList slSelectables = new StringList();
			slSelectables.add("name");
			slSelectables.add("revision");
			slSelectables.add("current");
			slSelectables.add("attribute[Originator]");
			slSelectables.add("attribute[Status]");
			slSelectables.add("attribute[pgAssemblyType]");
			slSelectables.add("attribute[pgFinishedProductCode]");
			slSelectables.add("attribute[Effectivity Date]");
			slSelectables.add("attribute[pgBOMBaseQuantity]");
			slSelectables.add("from[Authorized Temporary Specification].to.name");
			//Added by PLM V2 team - to add objectid in final result
			slSelectables.add("id");

			BusinessObject boTS = new BusinessObject(TSType, TSName, TSRev,
					"eService Production");
			boTS.open(_ctx);

			// Get the TS Data
			BusinessObjectWithSelect bows = boTS.select(_ctx, slSelectables);
			_originator = bows.getSelectData("attribute[Originator]");
			sBOMBaseQty = bows.getSelectData("attribute[pgBOMBaseQuantity]");
			if (bows.getSelectData("attribute[Status]").equals("PLANNING"))
				sBOMUsage = "2";
			else if (bows.getSelectData("attribute[Status]").equals("EXPERIMENTAL")) {
				_AbortReason = "Aborting BOM because status is EXPERIMENTAL";
				bAbortBOM = true;
			} else 
				sBOMUsage = "3";
			
			_BOMUsage = sBOMUsage;
			
			sValidFromDate = bows.getSelectData("attribute[Effectivity Date]");
			sValidFromDate = DateUtil.convertPLMDateToSAP(sValidFromDate);

//			9/17/2010 7:39:59 AM


			String sParentPgAssemblyType = bows.getSelectData("attribute[pgAssemblyType]");
			String sParentPgFPC = bows.getSelectData("attribute[pgFinishedProductCode]");
			if (TSType.equals("pgFinishedProduct")) {
				if (sParentPgAssemblyType == null
						|| sParentPgAssemblyType.equals("")
						|| sParentPgAssemblyType
								.equals("FWIP-Finished Work in Process")) {
					_ParentMatlNumber = TSName; // Assigned TSName instead FPC  - V4.0 on 01/02/2013
				}  else {
					_ParentMatlNumber = TSName;
				}
			} else if (TSType.equals("pgPackingSubassembly")) {
				if (sParentPgAssemblyType.equals("Purchased Subassembly")){
					_AbortReason = "Aborting BOM because this is a Purchased Subassembly";
					bAbortBOM = true;
				} else {
					_ParentMatlNumber = TSName;
				}
			} else {
				_ParentMatlNumber = TSName;
			}
			// Commented by V4.0 on 01/02/2013 - End
			
				if (!bAbortBOM) {				
				// Get the BOM data
				ExpansionWithSelect expand = boTS.expandSelect(_ctx, "EBOM",
						"pgPhase", slSelectables, slSelectables, false, true,
						(short) 1, "", "", false);

				RelationshipWithSelectList _relSelectList = expand
						.getRelationships();
				RelationshipWithSelectItr relItr = new RelationshipWithSelectItr(
						_relSelectList);

				StringList slEBOMRel = new StringList();
				slEBOMRel.add("attribute[pgQuantity]");
				slEBOMRel.add("attribute[pgMinQuantity]");
				slEBOMRel.add("attribute[pgMaxQuantity]");
				slEBOMRel.add("attribute[pgCalcQuantity]");
				slEBOMRel.add("attribute[pgMinCalcQuantity]");
				slEBOMRel.add("attribute[pgMaxCalcQuantity]");
				slEBOMRel.add("attribute[Find Number]");
				slEBOMRel.add("attribute[pgPositionIndicator]");
				slEBOMRel.add("id");
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
				slEBOMRel.add("attribute[pgOptionalComponent]");
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

				StringList slEBOMTarget = new StringList();
				slEBOMTarget.add("type");
				slEBOMTarget.add("name");
				slEBOMTarget.add("revision");
				slEBOMTarget.add("attribute[pgAssemblyType]");
				slEBOMTarget.add("attribute[pgFinishedProductCode]");
				slEBOMTarget.add("attribute[Effectivity Date]");
				slEBOMTarget.add("from[Authorized Temporary Specification].to.name");
				//Added by PLM V2 team
				slEBOMTarget.add("id");
				
				String[] saBOMRow = null;
				// Expand from the TS to get the pgPhase object
				while (relItr.next()) {
					RelationshipWithSelect relSelect = relItr.obj();
					BusinessObjectWithSelect boSelect = relSelect.getTarget();
					Vector vBoKeys = boSelect.getSelectKeys();
					Vector vBoData = boSelect.getSelectValues();

					String sPgPhaseName = (String) vBoData.elementAt(vBoKeys
							.indexOf("name"));
					String sPgPhaseRev = (String) vBoData.elementAt(vBoKeys
							.indexOf("revision"));
							//Modified by PLM V2 team
					String sPgPhaseID = (String) vBoData.elementAt(vBoKeys
							.indexOf("id"));
					BusinessObject boPgPhase = new BusinessObject("pgPhase",
							sPgPhaseName, sPgPhaseRev, PLMConstants.VAULT_PRODUCTION);

					ExpansionWithSelect exPgPhase = boPgPhase.expandSelect(_ctx,
							"EBOM", PGPHASE_EXPAND_TYPES, slEBOMTarget,
							slEBOMRel, false, true, (short) 1, "", "", false);

					RelationshipWithSelectList relSelectListPgPhase = exPgPhase
							.getRelationships();
					RelationshipWithSelectItr relPgPhaseItr = new RelationshipWithSelectItr(
							relSelectListPgPhase);
					while (relPgPhaseItr.next()) {
						String sFindNumber = "";
						String ComponentName = "";
						String sQuantity = "";
						String sSubstFlag = "";
						String sUpperLim = "";
						String sTarget = "";
						String sLowerLim = "";
						String sValidFromDateComp = "";
						String sBOMNumber = "";
						String sPosIndicator = "";
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
						String strOptComponent= "";
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
						

						RelationshipWithSelect relSelectEBOM = relPgPhaseItr
								.obj();
						BusinessObjectWithSelect boSelectTS = relSelectEBOM
								.getTarget();
						Vector vBoKeysTS = boSelectTS.getSelectKeys();
						Vector vBoDataTS = boSelectTS.getSelectValues();

						Vector vRelKeysTS = relSelectEBOM.getSelectKeys();
						Vector vRelDataTS = relSelectEBOM.getSelectValues();

						String sTSType = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("type"));
						String sTSName = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("name"));
						String sTSRev = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("revision"));
						String sPgAssemblyType = (String) vBoDataTS
								.elementAt(vBoKeysTS.indexOf("attribute[pgAssemblyType]"));
						String sPgFPC = (String) vBoDataTS.elementAt(vBoKeysTS
								.indexOf("attribute[pgFinishedProductCode]"));
								//Added by PLM V2 team
						String sTSID = (String) vBoDataTS.elementAt(vBoKeysTS
								.indexOf("id"));
								//Addition ends								
						sFindNumber = (String) vRelDataTS.elementAt(vRelKeysTS
								.indexOf("attribute[Find Number]"));
								
						
						String sComponentNumber = "";
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
						if(!strFirstlevelObjId.equals(""))
							hmParentObjects.put(strFirstlevelObjId,iChildObjCount);
						strFirstlevelObjId = sTSID;
						iChildObjCount = 1;
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
//Modified by PLM V2 - Added sTSID as parameter
						if (sTSType.equals("pgBaseFormula")) {
							getBaseFormulaRows(sTSName, sTSRev, 
									sFindNumber,sPgPhaseName,TSName,  sValidFromDate, sBOMBaseQty,
									sBOMUsage, sTSID);
						} else {
							buildIndividualRow(TSName, sValidFromDate, 
									sBOMBaseQty, sPgPhaseName, sFindNumber, 
									sSubstFlag, sBOMNumber, vBoKeysTS, 
									vBoDataTS, vRelKeysTS, vRelDataTS, 
									sTSType, sTSName, sPgAssemblyType, sPgFPC, sTSRev,
									sBOMUsage,sTSID);
						}
					}

					if (bAbortBOM)
						break;
				}
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
				hmParentObjects.put(strFirstlevelObjId,iChildObjCount);
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
				
			}
			boTS.close(_ctx);
			if (bAbortBOM) {
//				updateSapLog(_SapLogRequestId, sType, sName, sRev, _ParentMatlNumber, 
//	           			Integer.parseInt(sBOMUsage), _PlantString, _AbortReason, "false", ""+ _progress);
				saBOM = null;
			} else {
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts				
				alBOM = getBOMAlternateSubstituteGrouping(_ctx, alBOM);			
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends				
				saBOM = convertArrayListToArray(alBOM, BOM_ARRAY_SIZE);
			}
		
		} catch (Exception e) {
			_ErrorMessage = e.toString();
			e.printStackTrace();
		}

		if (saBOM != null)
			Arrays.sort(saBOM, new BOMArrayComparator());
		
		return saBOM;
	}
	
	public void buildIndividualRow(String TSName, String sValidFromDate,
			String sBOMBaseQty, String sPgPhaseName, String sFindNumber, 
			String sSubstFlag, String sBOMNumber, Vector vBoKeysTS, Vector vBoDataTS,
			Vector vRelKeysTS, Vector vRelDataTS, String sTSType, String sTSName,
			String sPgAssemblyType, String sPgFPC, String sTSRev, String sBOMUsage, String sTSID) 
	throws MatrixException {
		String[] saBOMRow;
		String sQuantity;
		String sUpperLim;
		String sTarget;
		String sLowerLim;
		String sValidFromDateComp;
		String sPosIndicator;
		String sComponentNumber;
		
		//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
		String strOptComponent= "";
		//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
		// Modified by V4.0 on 01/02/2013 - Start

		/* if (sTSType.equals("pgFinishedProduct")) {
			if (sPgAssemblyType == null
					|| sPgAssemblyType.equals("")
					|| sPgAssemblyType
							.equals("FWIP-Finished Work in Process")) {
				sComponentNumber = sTSName;

			} else {
				sComponentNumber = sTSName;
			}
		} else {
			sComponentNumber = sTSName;
		}
 */
		// Modified by V4.0 on 01/02/2013 - End
		sComponentNumber = sTSName;
		
		//For Substitutes
		String sRelId = (String) vRelDataTS
				.elementAt(vRelKeysTS.indexOf("id"));
		
		sQuantity = (String) vRelDataTS.elementAt(
				vRelKeysTS.indexOf("attribute[pgCalcQuantity]"));
		sTarget = (String) vRelDataTS.elementAt(
				vRelKeysTS.indexOf("attribute[pgCalcQuantity]"));
		sLowerLim = (String) vRelDataTS.elementAt(
				vRelKeysTS.indexOf("attribute[pgMinCalcQuantity]"));
		sUpperLim = (String) vRelDataTS.elementAt(
				vRelKeysTS.indexOf("attribute[pgMaxCalcQuantity]"));
		sPosIndicator = (String) vRelDataTS.elementAt(
				vRelKeysTS.indexOf("attribute[pgPositionIndicator]"));
		//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
		strOptComponent = (String) vRelDataTS.elementAt(
				vRelKeysTS.indexOf("attribute[pgOptionalComponent]"));
		//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
		

		sValidFromDateComp = (String) vBoDataTS.elementAt(
				vBoKeysTS.indexOf("attribute[Effectivity Date]"));
		sValidFromDateComp = DateUtil.convertPLMDateToSAP(sValidFromDateComp);

		String sATS = null;
		if (vBoKeysTS.indexOf("from[Authorized Temporary Specification].to.name") != -1)
			sATS = (String) vBoDataTS.elementAt(
					vBoKeysTS.indexOf("from[Authorized Temporary Specification].to.name"));

		if (sTSType.startsWith("pgMaster")) {
			//Add in the Master Row
			//Change 9/17/2010 -- Don't send the master as a row
			//Send the first individual as a non-sub, and the remaining as subs
			//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts	
			/*buildBOMArray(sPgPhaseName, sFindNumber,
					TSName, sBOMUsage, sValidFromDate,
					sComponentNumber, sQuantity, sSubstFlag,
					sUpperLim, sTarget, sLowerLim,
					sValidFromDateComp, sBOMNumber, sBOMBaseQty,
					sPosIndicator, sRelId, sTSName, sTSRev, true, sTSID);*/
			buildBOMArray(sPgPhaseName, sFindNumber,
					TSName, sBOMUsage, sValidFromDate,
					sComponentNumber, sQuantity, sSubstFlag,
					sUpperLim, sTarget, sLowerLim,
					sValidFromDateComp, sBOMNumber, sBOMBaseQty,
					sPosIndicator, sRelId, sTSName, sTSRev, true, sTSID, "");
			
			//Add the indidivuals as subs
			getIndividualsFromMaster(TSName, sValidFromDate, sBOMBaseQty, 
					sPgPhaseName, sFindNumber, sBOMNumber, sTSType,
					sTSName, sTSRev, sBOMUsage, sQuantity, sUpperLim,
					sTarget, sLowerLim, sValidFromDateComp, sPosIndicator, true, sTSID);

		
		} else
			//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts	
			/*buildBOMArray(sPgPhaseName, sFindNumber,
					TSName, sBOMUsage, sValidFromDate,
					sComponentNumber, sQuantity, sSubstFlag,
					sUpperLim, sTarget, sLowerLim,
					sValidFromDateComp, sBOMNumber, sBOMBaseQty,
					sPosIndicator, sRelId, sTSName, sTSRev, false, sTSID);*/
			buildBOMArray(sPgPhaseName, sFindNumber,
					TSName, sBOMUsage, sValidFromDate,
					sComponentNumber, sQuantity, sSubstFlag,
					sUpperLim, sTarget, sLowerLim,
					sValidFromDateComp, sBOMNumber, sBOMBaseQty,
					sPosIndicator, sRelId, sTSName, sTSRev, false, sTSID, strOptComponent);
					
		//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

	}

	public void getIndividualsFromMaster(String TSName, String sValidFromDate, String sBOMBaseQty, 
			String sPgPhaseName, String sFindNumber, String sBOMNumber, String sMasterTSType,
			String sMasterTSName, String sMasterTSRev, String sBOMUsage, String sQuantity,
			String sUpperLim, String sTarget, String sLowerLim, 
			String sValidFromDateComp, String sPosIndicator, boolean bIncrementAltItem, String sTSID) throws MatrixException {
		String[] saBOMRow;
		String sComponentNumber;
		StringList slIndSelectables = new StringList();
		slIndSelectables.add("type");
		slIndSelectables.add("name");
		slIndSelectables.add("revision");
		slIndSelectables.add("attribute[pgFinishedProductCode]");
		slIndSelectables.add("attribute[pgAssemblyType]");
		slIndSelectables.add("attribute[Effectivity Date]");
		slIndSelectables.add("from[Authorized Temporary Specification].to.name");
		slIndSelectables.add("id");
		
		slIndSelectables.add("last.attribute[Effectivity Date]");
		slIndSelectables.add("last.current");
		slIndSelectables.add("last.revision");
		slIndSelectables.add("last.attribute[pgAssemblyType]");
		slIndSelectables.add("last.from[Authorized Temporary Specification].to.name");
		slIndSelectables.add("last.from[pgMaster].to.name");		
		
		slIndSelectables.add("last.from[pgMaster].to.name");
		slIndSelectables.add("last.previous.attribute[Effectivity Date]");
		slIndSelectables.add("last.previous.current");
		slIndSelectables.add("last.previous.revision");
		slIndSelectables.add("last.previous.from[pgMaster].to.name");
		slIndSelectables.add("last.previous.from[Authorized Temporary Specification].to.name");
		
		
//		BusinessObject boMaster = new BusinessObject(sMasterTSType, sMasterTSName, sMasterTSRev, 
//				PLMConstants.VAULT_PRODUCTION);

//		BusinessObjectList bolMaster = boMaster.getRevisions(_ctx);
		
		StringBuffer sbMQL = new StringBuffer();
		sbMQL.append("temp query bus ").append(sMasterTSType).append(" ");
		sbMQL.append(sMasterTSName).append(" * ").append(" select id dump |;");
		
		MQLCommand mqlCommand = new MQLCommand();
		mqlCommand.executeCommand(_ctx, sbMQL.toString());
		
		String sResults = mqlCommand.getResult();
		String[] saResults = sResults.split("\n");
		BusinessObjectList bolMasters = new BusinessObjectList();

		for (int i=0; i<saResults.length; i++) {
			String[] saTemp = saResults[i].split("\\|");
			String sId = saTemp[3];
			BusinessObject boTemp = new BusinessObject(sId);
			bolMasters.add(boTemp);
		}

		Iterator itrMasters = bolMasters.iterator();
		
		TreeSet tsIndividuals = new TreeSet();
		
		int i=0;
		while (itrMasters.hasNext()) {
			BusinessObject boCurrentMaster = (BusinessObject) itrMasters.next();
			boCurrentMaster.open(_ctx);
			ExpansionWithSelect exInd = boCurrentMaster.expandSelect(_ctx,
					"pgMaster", "*", slIndSelectables,
					slIndSelectables, true, true, (short) 1, "", "", false);
			boCurrentMaster.close(_ctx);
			
			RelationshipWithSelectList relSelectListInd = exInd.getRelationships();
			RelationshipWithSelectItr relIndItr = new RelationshipWithSelectItr(
					relSelectListInd);

			String sIndSubFlag = "";			
			while (relIndItr.next()) {
				sIndSubFlag=_cAltItem1 + "" + _cAltItem2;			
				
				RelationshipWithSelect relSelectInd = relIndItr.obj();
				BusinessObjectWithSelect boIndTS = relSelectInd.getTarget();
				
				Vector vBoKeysInd = boIndTS.getSelectKeys();
				Vector vBoDataInd = boIndTS.getSelectValues();
				Vector vRelDataTS = relSelectInd.getSelectValues();
				Vector vRelKeysTS = relSelectInd.getSelectKeys();
				
				String sRelId = (String) vRelDataTS.elementAt(vRelKeysTS.indexOf("id"));
				
				String sIndType = (String) vBoDataInd.elementAt(vBoKeysInd.indexOf("type"));
				String sIndName = (String) vBoDataInd.elementAt(vBoKeysInd.indexOf("name"));
				String sIndRev = (String) vBoDataInd.elementAt(vBoKeysInd.indexOf("revision"));
				//Added by PLM V2 team - To send object id in final string array
				sTSID = (String) vBoDataInd.elementAt(vBoKeysInd.indexOf("id"));
				String sIsATS = "";
				if (vBoKeysInd.indexOf("from[Authorized Temporary Specification].to.name")> -1)
					sIsATS = (String) vBoDataInd.elementAt(
						vBoKeysInd.indexOf("from[Authorized Temporary Specification].to.name"));
				
				String sIndAssemblyType = (String) vBoDataInd.elementAt(
						vBoKeysInd.indexOf("attribute[pgAssemblyType]"));
				String sIndFPC = (String) vBoDataInd.elementAt(
						vBoKeysInd.indexOf("attribute[pgFinishedProductCode]"));
				
				//Date may have already been converted, so check the lenght of the string first
				if (sValidFromDateComp.length() > 10)
					sValidFromDateComp = DateUtil.convertPLMDateToSAP(sValidFromDateComp);
				// Commented by V4.0 on 01/02/2013 - Start

				/*	if (sIndType.equals("pgFinishedProduct")) {
					if (sIndAssemblyType == null
							|| sIndAssemblyType.equals("")
							|| sIndAssemblyType
									.equals("FWIP-Finished Work in Process")) {
						sComponentNumber = sIndName;
					} else {
						sComponentNumber = sIndName;
					}
				} else {
					sComponentNumber = sIndName;
				} */
				// Commented by V4.0 on 01/02/2013 - End

				sComponentNumber = sIndName;

				sValidFromDateComp = DateUtil.convertPLMDateToSAP(
						getEffDateForLatestInd(sIndType, sIndName, sMasterTSName));

				if (sValidFromDateComp!= null && !sValidFromDateComp.equals("")) {
					//Only add this individual if it hasn't been added already. Ths
					//Check is in place because we we are now pulling individuals
					//from all versions of the master
					if (!tsIndividuals.contains(sIndName)) {
				//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
						/*buildBOMArray(sPgPhaseName, sFindNumber,
								TSName, sBOMUsage, sValidFromDate,
								sComponentNumber, sQuantity, sIndSubFlag,
								sUpperLim, sTarget, sLowerLim,
								sValidFromDateComp, sBOMNumber, sBOMBaseQty,
								sPosIndicator,sRelId, sMasterTSName, sMasterTSRev, false, sTSID);*/
						buildBOMArray(sPgPhaseName, sFindNumber,
								TSName, sBOMUsage, sValidFromDate,
								sComponentNumber, sQuantity, sIndSubFlag,
								sUpperLim, sTarget, sLowerLim,
								sValidFromDateComp, sBOMNumber, sBOMBaseQty,
								sPosIndicator,sRelId, sMasterTSName, sMasterTSRev, false, sTSID, "");
				//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends		
						tsIndividuals.add(sIndName);
					}
				}

				i++;
			}
		}
		
		if (bIncrementAltItem)
			if (i>0) {				
				//SET THE ALT ITEM GROUP VALUE TO PROPER VALUES HERE
				//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
				/*if (_cAltItem2 == 'Z') {
					_cAltItem2 = 'A';
					_cAltItem1--;
				} else {
					_cAltItem2++;				
				}*/
				//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
			} else {
				_AbortReason = "Aborting because there are no individuals connected to Master " + sMasterTSName 
						+ " " + sMasterTSRev;
				bAbortBOM = true;
			}			
	}
	
	public String getEffDateForLatestInd(String sType, String sName, String sMasterName) {
		String sEffDate = "";
		String sValidState = "Release";
		try {
			//Use a temp query to find all the versions because
			//we may be missing the revision chain because it
			//won't leave placeholders for missing selects
			StringList slSelectables = new StringList();
			slSelectables.add("from[Authorized Temporary Specification].to.name");
			slSelectables.add("from[pgMaster].to.name");
			slSelectables.add("to[pgMaster].from.name");
			slSelectables.add("attribute[Effectivity Date]");
			slSelectables.add("attribute[pgSAPType]");
			
			StringBuffer sbMql = new StringBuffer();
			sbMql.append("temp query bus ").append(sType).append(" ");
			sbMql.append(sName).append(" * select id current dump | ");
			
			MQLCommand mqlCommand = new MQLCommand();
			mqlCommand.executeCommand(_ctx, sbMql.toString());
			
			String sResults = mqlCommand.getResult();
			String[] saResults = sResults.split("\n");
			
			int iHighestVer = 0;
			String sId = "";
			for (int i=0; i<saResults.length; i++) {
				String[] saTemp = saResults[i].split("\\|");
				String sState = saTemp[4];
				String sVer = saTemp[2];
				
				if (Integer.parseInt(sVer) > iHighestVer) {
					if (sState.equals(sValidState)) {
						iHighestVer = Integer.parseInt(sVer);
						sId = saTemp[3];
					}
				}
			}
			
			BusinessObject boHighestVer = new BusinessObject(sId);
			boHighestVer.open(_ctx);
			BusinessObjectWithSelect bows = boHighestVer.select(_ctx, slSelectables);
			
			String sATS = bows.getSelectData("from[Authorized Temporary Specification].to.name"); 
			String sDocType = bows.getSelectData("attribute[pgSAPType]");
			if (!sDocType.equalsIgnoreCase("doc") || !sType.startsWith("pgMaster")) {
				if (sATS == null || sATS.equals("")) {
					String sMaster = bows.getSelectData("from[pgMaster].to.name");
					if (sMaster == null || sMaster.equals("")) 
						sMaster = bows.getSelectData("from[pgMaster].to.name");
					
					if (sMaster.equals(sMasterName))
						sEffDate=bows.getSelectData("attribute[Effectivity Date]");
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sEffDate;
	}
	
	public ArrayList getBaseFormulaRows(String sBSFName, String sBSFRev, 
			String sFindNumber, String sPgPhaseName, String sParentName, String sParentEffectiveDate, 
			String sParentBOMBaseQty, String sBOMUsage, String sTSID) {
		String[][] saReturn = null;
		try {
			BusinessObject boBSF = new BusinessObject("pgBaseFormula", sBSFName, sBSFRev,"eService Production" );
			StringList slEBOMRel = new StringList();
			//Modified by PLM V2 team - Added pgCalcQuantity, pgMinCalcQuantity and pgMaxCalcQuantity in slEBOMRel, as it is fetched in below code
			slEBOMRel.add("attribute[pgQuantity]");
			slEBOMRel.add("attribute[pgCalcQuantity]");
			slEBOMRel.add("attribute[pgMinQuantity]");
			slEBOMRel.add("attribute[pgMinCalcQuantity]");
			slEBOMRel.add("attribute[pgMaxQuantity]");
			slEBOMRel.add("attribute[pgMaxCalcQuantity]");
			slEBOMRel.add("attribute[Find Number]");
			slEBOMRel.add("attribute[pgPositionIndicator]");
			slEBOMRel.add("id");
			//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
			slEBOMRel.add("attribute[pgOptionalComponent]");
			//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

			StringList slEBOMTarget = new StringList();
			slEBOMTarget.add("type");
			slEBOMTarget.add("name");
			slEBOMTarget.add("revision");
			slEBOMTarget.add("attribute[pgAssemblyType]");
			slEBOMTarget.add("attribute[pgFinishedProductCode]");
			slEBOMTarget.add("attribute[Effectivity Date]");
			slEBOMTarget.add("from[Authorized Temporary Specification].to.name");
			slEBOMTarget.add("id");
			
			// Get the BOM data
			ExpansionWithSelect expand = boBSF.expandSelect(_ctx, "EBOM",
					"Product Data", slEBOMTarget, slEBOMRel, false, true,
					(short) 1, "", "", false);

			RelationshipWithSelectList _relSelectList = expand
					.getRelationships();
			RelationshipWithSelectItr relItr = new RelationshipWithSelectItr(
					_relSelectList);

			RelationshipWithSelectList relSelectListPgPhase = expand.getRelationships();
			RelationshipWithSelectItr relPgPhaseItr = new RelationshipWithSelectItr(
					relSelectListPgPhase);
			while (relPgPhaseItr.next()) {
				String sRowFindNumber = "";
				String sQuantity = "";
				String sCalcQuantity = "";
				String sCalcUpperLimit = "";
				String sCalcLowerLimit = "";
				String sSubstFlag = "";
				String sUpperLim = "";
				String sTarget = "";
				String sLowerLim = "";
				String sValidFromDateComp = "";
				String sBOMNumber = "";
				String sPosIndicator = "";
				
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
				String strOptComponent = "";
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

				RelationshipWithSelect relSelectEBOM = relPgPhaseItr
						.obj();
				BusinessObjectWithSelect boSelectTS = relSelectEBOM
						.getTarget();
				Vector vBoKeysTS = boSelectTS.getSelectKeys();
				Vector vBoDataTS = boSelectTS.getSelectValues();

				Vector vRelKeysTS = relSelectEBOM.getSelectKeys();
				Vector vRelDataTS = relSelectEBOM.getSelectValues();
				
				//For Subs
				String sRelId = (String) vRelDataTS.elementAt(vRelKeysTS.indexOf("id"));
				
				String sTSType = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("type"));
				String sTSName = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("name"));
				String sTSRev = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("revision"));
				String sPgAssemblyType = (String) vBoDataTS
						.elementAt(vBoKeysTS.indexOf("attribute[pgAssemblyType]"));
				String sPgFPC = (String) vBoDataTS.elementAt(vBoKeysTS
						.indexOf("attribute[pgFinishedProductCode]"));
				//Modified by PLM V2 team - Get object id
				sTSID = (String) vBoDataTS.elementAt(vBoKeysTS.indexOf("id"));
				sRowFindNumber = (String) vRelDataTS.elementAt(vRelKeysTS
						.indexOf("attribute[Find Number]"));
				
				String sComponentNumber = "";
				
				/* if (sTSType.equals("pgFinishedProduct")) {
					if (sPgAssemblyType == null
							|| sPgAssemblyType.equals("")
							|| sPgAssemblyType
									.equals("FWIP-Finished Work in Process")) {
						sComponentNumber = sPgFPC;
					} else {
						sComponentNumber = sTSName;
					}
				} else {
					sComponentNumber = sTSName;
				}
				*/

				sComponentNumber = sTSName;
				
//				sQuantity = (String) vRelDataTS.elementAt(vRelKeysTS
//						.indexOf("attribute[pgQuantity]"));				
//				sTarget = (String) vRelDataTS.elementAt(vRelKeysTS
//						.indexOf("attribute[pgQuantity]"));
//				sLowerLim = (String) vRelDataTS.elementAt(vRelKeysTS
//						.indexOf("attribute[pgMinQuantity]"));
				
				sQuantity = (String) vRelDataTS.elementAt(vRelKeysTS
						.indexOf("attribute[pgCalcQuantity]"));				
				sTarget = (String) vRelDataTS.elementAt(vRelKeysTS
						.indexOf("attribute[pgCalcQuantity]"));
				sLowerLim = (String) vRelDataTS.elementAt(vRelKeysTS
						.indexOf("attribute[pgMinCalcQuantity]"));
				sUpperLim = (String) vRelDataTS.elementAt(vRelKeysTS
						.indexOf("attribute[pgMaxCalcQuantity]"));
				sPosIndicator = (String) vRelDataTS
						.elementAt(vRelKeysTS
								.indexOf("attribute[pgPositionIndicator]"));
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
				strOptComponent = (String) vRelDataTS
						.elementAt(vRelKeysTS
								.indexOf("attribute[pgOptionalComponent]"));
				//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

				sValidFromDateComp = (String) vBoDataTS
						.elementAt(vBoKeysTS.indexOf("attribute[Effectivity Date]"));
				sValidFromDateComp = DateUtil.convertPLMDateToSAP(sValidFromDateComp);

				if (sTSType.startsWith("pgMaster")) {
					//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
					/*buildBOMArray(sPgPhaseName, sRowFindNumber,
							sParentName, sBOMUsage, sParentEffectiveDate,
							sComponentNumber, sQuantity, sSubstFlag,
							sUpperLim, sTarget, sLowerLim,
							sValidFromDateComp, sBOMNumber, sParentBOMBaseQty,
							sPosIndicator, sRelId, sTSName, sTSRev, false, sTSID);*/
					buildBOMArray(sPgPhaseName, sRowFindNumber,
							sParentName, sBOMUsage, sParentEffectiveDate,
							sComponentNumber, sQuantity, sSubstFlag,
							sUpperLim, sTarget, sLowerLim,
							sValidFromDateComp, sBOMNumber, sParentBOMBaseQty,
							sPosIndicator, sRelId, sTSName, sTSRev, false, sTSID, "");
					

					//Add the indidivuals as subs
					getIndividualsFromMaster(sParentName,sParentEffectiveDate, sParentBOMBaseQty, 
							sPgPhaseName, sFindNumber, sBOMNumber, sTSType,
							sTSName, sTSRev, sBOMUsage, sQuantity, sUpperLim,
							sTarget, sLowerLim, sValidFromDateComp, sPosIndicator, true, sTSID);
				} else {
					//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
					/*buildBOMArray(sPgPhaseName, sRowFindNumber,
							sParentName, sBOMUsage, sParentEffectiveDate,
							sComponentNumber, sQuantity, sSubstFlag,
							sUpperLim, sTarget, sLowerLim,
							sValidFromDateComp, sBOMNumber, sParentBOMBaseQty,
							sPosIndicator, sRelId, sTSName, sTSRev, false, sTSID);*/
					buildBOMArray(sPgPhaseName, sRowFindNumber,
							sParentName, sBOMUsage, sParentEffectiveDate,
							sComponentNumber, sQuantity, sSubstFlag,
							sUpperLim, sTarget, sLowerLim,
							sValidFromDateComp, sBOMNumber, sParentBOMBaseQty,
							sPosIndicator, sRelId, sTSName, sTSRev, false, sTSID, strOptComponent);
					//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
				}
			}
		} catch (Exception e) {
			_ErrorMessage = e.toString();
			e.printStackTrace();
		}
		
		return alBOM;
	}
	
	public String[][] getSubs(Context ctx, String sRelId) {
		String[][] saReturn = null;

		if (sRelId.equals(""))
			return saReturn;
		try {
			StringBuffer sbMql = new StringBuffer();

			sbMql.append("print connection ").append(sRelId);
			sbMql.append(" select frommid[EBOM Substitute].id dump |");
			MQLCommand mqlCom = new MQLCommand();
			mqlCom.executeCommand(ctx, sbMql.toString());
			String sMqlRes = mqlCom.getResult();

			ArrayList<String[]> alSubRows = new ArrayList<String[]>();
			
			if (!sMqlRes.equals("")) {
				String[] saIds = (sMqlRes.trim().split("\\|", -1));
				if (!saIds[0].equals("")) {
					for (int i = 0; i < saIds.length; i++) {						
						StringBuffer sbPrintConn = new StringBuffer();
						sbPrintConn.append("print connection ").append(saIds[i]);
						sbPrintConn.append(" select attribute[pgPositionIndicator]");
						sbPrintConn.append(" attribute[pgMinCalcQuantity]");
						sbPrintConn.append(" attribute[pgCalcQuantity]");
						sbPrintConn.append(" attribute[pgMaxCalcQuantity]");
						sbPrintConn.append(" attribute[pgQuantityUnitOfMeasure]");
						sbPrintConn.append(" attribute[pgSubstituteCombinationNumber]");
						sbPrintConn.append(" to.name");
						sbPrintConn.append(" to.type");
						sbPrintConn.append(" to.attribute[pgAssemblyType]");
						sbPrintConn.append(" to.attribute[pgFinishedProductCode]");
						sbPrintConn.append(" to.attribute[Effectivity Date]");
						sbPrintConn.append(" to.revision");
						sbPrintConn.append(" to.attribute[pgSAPType]");
						//Added by Mani on 6/20/2012 to fix SAP BOM Issue - Start
						sbPrintConn.append(" to.id");
						//Added by Mani on 6/20/2012 to fix SAP BOM Issue - End
						sbPrintConn.append(" to.from[Authorized Temporary Specification].to.name");
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
						sbPrintConn.append(" attribute[pgOptionalComponent]");
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
						 
						sbPrintConn.append(" dump |");

						MQLCommand mqlSubsCom = new MQLCommand();
						mqlSubsCom.executeCommand(ctx, sbPrintConn.toString());
						String sMqlSubsRes = mqlSubsCom.getResult();
						
						String[] saParts = (sMqlSubsRes.trim().split("\\|", -1));
						String sSapType = saParts[12];
						String sType = saParts[7];
						String sATS = "";
						//Modified by Mani on 6/20/2012 to fix SAP BOM Issues
						//if (saParts.length == 14 )
						//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
						//if (saParts.length == 15 )
						if (saParts.length == 16 )	
						//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends	
							sATS = saParts[14];
						
						if (sType.startsWith("pgMaster")||(!sSapType.equalsIgnoreCase("doc") && (sATS.equals(""))))
							alSubRows.add(saParts);
					}
				}
				if (alSubRows.size()>0) {
					//saReturn = convertArrayListToArray(alSubRows, 11);
					saReturn = convertArrayListToArray(alSubRows, 15);
					Arrays.sort(saReturn, new SubRowsComparator());
					

					for (int i=0; i<saReturn.length; i++) {
						saReturn[i][5]=_cAltItem1 + "" + _cAltItem2;
					}
				}
			}
		} catch (Exception e) {
			_ErrorMessage = e.toString();
			e.printStackTrace();
		}
				
		return saReturn;
	}

	public String[][] convertArrayListToArray(ArrayList alGeneric, int iArraySize) {
		String[][] saReturn = null;
		try {
			if (alGeneric != null) {
				Iterator itr = alGeneric.iterator();

				saReturn = new String[alGeneric.size()][iArraySize];
				int i = 0;
				while (itr.hasNext()) {
					String[] saTemp = (String[]) itr.next();
					saReturn[i] = saTemp;
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			saReturn = null;
		}

		return saReturn;
	}

	public static void printList(String[][] saList) {
		for (int i = 0; i < saList.length; i++) {
			System.out.print(saList[i][PHASE_NAME]);
			System.out.print(" -- ");
			System.out.print(saList[i][FIND_NUMBER]);
			System.out.print(" -- ");
			System.out.print(saList[i][SUBSTITUTE_FLAG]);
			System.out.print(" -- ");
			System.out.println(saList[i][COMPONENT_NAME]);
		}
	}

	//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
	/*public void buildBOMArray(String sPgPhase, String sFindNumber,
			String sName, String sBomUsage, String sValidFromDate,
			String sComponentName, String sQuantity, String sSubstituteFlag,
			String sUpperLimit, String sTarget, String sLowerLimit,
			String sValidFromDateComponent, String sBOMNumber,
			String sBOMBaseQuantity, String sPositionIndicator, String sRelId,
			String sGCAS, String sVersion, boolean bOnlySendChildren, String sTSID) { */
		public void buildBOMArray(String sPgPhase, String sFindNumber,
				String sName, String sBomUsage, String sValidFromDate,
				String sComponentName, String sQuantity, String sSubstituteFlag,
				String sUpperLimit, String sTarget, String sLowerLimit,
				String sValidFromDateComponent, String sBOMNumber,
				String sBOMBaseQuantity, String sPositionIndicator, String sRelId,
				String sGCAS, String sVersion, boolean bOnlySendChildren, String sTSID, String strOptComponent) {
	//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends		
		//If sTarget is blank, abort the BOM
		
		
		//Added by Mani on 6/19/2012 to handle data issue where attribute pgCalcQuantity is not Migrated - Start
		/*if (sTarget.equals("")) {
			_AbortReason = "Aborting BOM because target is null for " + sComponentName;
			bAbortBOM = true;
		}*/
		boolean bIgnoreBlankTarget = false;
		if (sTarget.equals("")) {
			_AbortReason = "Aborting BOM because target is null for " + sComponentName;
			bIgnoreBlankTarget = true;
		}
		//Added by Mani on 6/19/2012 to handle data issue where attribute pgCalcQuantity is not Migrated - End
		
		String[] saTemp = new String[BOM_ARRAY_SIZE];		

		// Set the constant value fields
		saTemp[ALT_BOM] = VALUE_ALT_BOM;
		saTemp[BOM_STATUS] = VALUE_BOM_STATUS;
		saTemp[ITEM_CATEGORY] = VALUE_ITEM_CATEGORY;
//		saTemp[BOM_TEXT] = VALUE_BOM_TEXT;
		saTemp[BOM_TEXT] = sParentGCAS + "." + sParentRev;		
		saTemp[ALT_BOM_TEXT] = VALUE_ALT_BOM_TEXT;

		// Set the variable fields
		if (!sSubstituteFlag.equals("")) {
			saTemp[SUBSTITUTE_FLAG] = sSubstituteFlag;
			saTemp[USAGE_PROBABILITY] = VALUE_USAGE_PROBABILITY;
		} else {
			saTemp[SUBSTITUTE_FLAG] = "";
			saTemp[USAGE_PROBABILITY] = "";
		}

		saTemp[MATERIAL_NUMBER] = _ParentMatlNumber;
		saTemp[BOM_USAGE] = sBomUsage;
		saTemp[VALID_FROM_DATE_PARENT] = sValidFromDate;
		saTemp[COMPONENT_NAME] = sComponentName;		
		saTemp[QUANTITY] = sQuantity;
		saTemp[UPPER_LIMIT] = sUpperLimit;
		//Added by Mani on 6/19/2012 to handle data issue where attribute pgCalcQuantity is not Migrated - Start
		if(bIgnoreBlankTarget) {
			saTemp[TARGET] = "-9.99";
		} else {
			saTemp[TARGET] = sTarget;	
		}
		//Added by Mani on 6/19/2012 to handle data issue where attribute pgCalcQuantity is not Migrated - End
		saTemp[LOWER_LIMIT] = sLowerLimit;
		saTemp[FIND_NUMBER] = sFindNumber;
		saTemp[VALID_FROM_DATE_CHILD] = sValidFromDateComponent;
		saTemp[PHASE_NAME] = sPgPhase;
		saTemp[CONFIRMED_QUANTITY] = sBOMBaseQuantity;
		saTemp[SORT_STRING] = sPositionIndicator;
		//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
		saTemp[OPT_COMPONENT] = strOptComponent;
		//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends
		saTemp[BOM_ITEM_NUMBER] = sBOMNumber;
		saTemp[OBJECT_ID] = sTSID;
		//Added by Mani on 6/20/2012 to fix SAP BOM Issues - Start
		if(bCreatingSubsRows) {
			saTemp[DISPLAY_OBJECT_ID] = strSubsDispId;
		} else {
			saTemp[DISPLAY_OBJECT_ID] = sTSID;	
		}
		//Added by Mani on 6/20/2012 to fix SAP BOM Issues - End
		if (saTemp[UPPER_LIMIT] == null || saTemp[UPPER_LIMIT].equals("")) {
			saTemp[RANGE_FLAG] = "";
		} else 
			saTemp[RANGE_FLAG] = VALUE_RANGE_FLAG;
		
		//Need to see if this Row has subs for it. If it does. set the Alt_Item_Group for this
		//Row to the same value as the subs use
		String[][] saSubs = getSubs(_ctx, sRelId);
		
		if (saSubs!=null) {
			saTemp[SUBSTITUTE_FLAG] = _cAltItem1 + "" + _cAltItem2;
			saTemp[USAGE_PROBABILITY] = VALUE_USAGE_PROBABILITY;			
		}
		
		if (!bOnlySendChildren){
			alBOM.add(saTemp);
			//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
			if(strFirstlevelObjIdCopy.equals(""))
				strFirstlevelObjIdCopy = strFirstlevelObjId;
			else if(!strFirstlevelObjIdCopy.equals(strFirstlevelObjId))
				strFirstlevelObjIdCopy = strFirstlevelObjId;
			else
				iChildObjCount++;
			
			hmChildObjects.put(saTemp[DISPLAY_OBJECT_ID],strFirstlevelObjId);
			//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
		}
		
		boolean bIncrementAltItem = true;
		if (saSubs!=null) {			
			for (int i=0; i<saSubs.length; i++) {
				//Modified by DSM(Sogeti) 2015x.1 for SAP BOM As Fed Defect Id-1357 on 5-Apr-2016 -Starts
				//Added by Mani on 6/20/2012 to fix SAP BOM Issues - Start
				/*if(i == 0) {*/
					bCreatingSubsRows = true;
				/*}*/
				//Modified by DSM(Sogeti) 2015x.1 for SAP BOM As Fed Defect Id-1357 on 5-Apr-2016 -Ends
				strSubsDispId = saSubs[i][13];							
				//Added by Mani on 6/20/2012 to fix SAP BOM Issues - End
				String sTSName = saSubs[i][6];				
				String sTSType = saSubs[i][7];				
				String sTSRev = saSubs[i][11];				
				String sPgAssemblyType = saSubs[i][8];
				String sPgFPC = saSubs[i][9];
				String sValidFromDateSub = saSubs[i][10];
				sValidFromDateSub = DateUtil.convertPLMDateToSAP(sValidFromDateSub);
				String sSubComponentNumber = "";

				//If the sub is a master, don't display the master...display the individuals on the master
				if (sTSType.startsWith("pgMaster")) {
					try {
						sTarget = saSubs[i][2];
						//Added by DSM(Sogeti) 2015x.1 for SAP BOM As Fed Defect Id-1357 on 5-Apr-2016 -Starts
						bCreatingSubsRows = false;
						//Added by DSM(Sogeti) 2015x.1 for SAP BOM As Fed Defect Id-1357 on 5-Apr-2016 -Ends
						getIndividualsFromMaster(sName, sValidFromDate, sBOMBaseQuantity, sPgPhase,
							sFindNumber, sBOMNumber, sTSType,sTSName, sTSRev, 
							sBomUsage, sQuantity, sUpperLimit, sTarget, sLowerLimit,
							sValidFromDateComponent,sPositionIndicator, false, sTSID);
					} catch (MatrixException me) {
						me.printStackTrace();
					}
					bIncrementAltItem = false;
				} else {
					/* if (sTSType.equals("pgFinishedProduct")) {
						if (sPgAssemblyType == null
								|| sPgAssemblyType.equals("")
								|| sPgAssemblyType
										.equals("FWIP-Finished Work in Process")) {
							sSubComponentNumber = sPgFPC;
						} else {
							sSubComponentNumber = sTSName;
						}
					} else {
						sSubComponentNumber = sTSName;
					} */
					
					sSubComponentNumber = sTSName;
					//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Starts
					/*buildBOMArray(sPgPhase, sFindNumber,
							sName, sBomUsage, sValidFromDate,
							sSubComponentNumber, saSubs[i][2], saSubs[i][5],
							saSubs[i][3], saSubs[i][2], saSubs[i][1],
							sValidFromDateSub, sBOMNumber,
							sBOMBaseQuantity, saSubs[i][0], "", sGCAS, sVersion, false, sTSID);*/
					buildBOMArray(sPgPhase, sFindNumber,
						sName, sBomUsage, sValidFromDate,
						sSubComponentNumber, saSubs[i][2], saSubs[i][5],
						saSubs[i][3], saSubs[i][2], saSubs[i][1],
						sValidFromDateSub, sBOMNumber,
						sBOMBaseQuantity, saSubs[i][0], "", sGCAS, sVersion, false, sTSID, saSubs[i][14]);
					//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

				}
				
				//Added by Mani on 6/20/2012 to fix SAP BOM Issues - Start
				if(i == saSubs.length - 1) {
					bCreatingSubsRows = false;
					strSubsDispId = "";
				}
				//Added by Mani on 6/20/2012 to fix SAP BOM Issues - End

			} 
			//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
			//SET THE ALT ITEM GROUP VALUE TO PROPER VALUES HERE
			/*if (bIncrementAltItem)
				if (_cAltItem2 == 'Z') {
					_cAltItem2 = 'A';
					_cAltItem1--;
				} else {
					_cAltItem2++;				
				}*/
			//Modified by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
		}
	}

	public void setRequestId(String s) {
		_SapLogRequestId = s;
	}

	public String getRequestId() {
		return _SapLogRequestId;
	}
	
	public void printRowData(String[] saBOMData, int i) {
		System.out.println("MATNR -- 0000000000"+saBOMData[MATERIAL_NUMBER]); //Material Number
		System.out.println("STLAN -- " + saBOMData[BOM_USAGE]); // BOM Usage
		System.out.println("DATUV -- " + saBOMData[VALID_FROM_DATE_PARENT]); // Parent Effective Date
		System.out.println("STLAL -- " + saBOMData[ALT_BOM]); // ALT BOM
		System.out.println("WERKS -- "); // PLANT -- not used
		System.out.println("STKTX --" + saBOMData[BOM_TEXT]); // ALT BOM TEXT
		System.out.println("ZTEXT -- " + saBOMData[ALT_BOM_TEXT]); // ALT BOM TEXT

		System.out.println("BMENG -- " + saBOMData[CONFIRMED_QUANTITY]); // BOM Base Qty
		System.out.println("STLST -- " + saBOMData[BOM_STATUS]); // BOM STATUS
		System.out.println("IDNRK -- 0000000000"+saBOMData[COMPONENT_NAME]); // Comp Name
		System.out.println("POSTP -- " + saBOMData[ITEM_CATEGORY]); // Item Category
		System.out.println("POSNR -- " + i); // Incrementing Number
		System.out.println("DATUV_I -- " + saBOMData[VALID_FROM_DATE_CHILD]); // Child Effective Date
		System.out.println("SORTF -- " + saBOMData[SORT_STRING]); // Position Indicator
		System.out.println("ALPGR -- " + saBOMData[SUBSTITUTE_FLAG]); // Alt Item Group -- Substitute combination numbers AA-ZZ
		System.out.println("EWAHR -- " + saBOMData[USAGE_PROBABILITY]); // Usage Probability
		System.out.println("ZRANGE_FLAG -- " + saBOMData[RANGE_FLAG]); // Range Flag
		System.out.println("MENGE -- " + saBOMData[TARGET]); // Component Quantity -- BOM Target?
		System.out.println("ZUPPER_LIMIT -- " + saBOMData[UPPER_LIMIT]); // Upper Limit -- Max?
		System.out.println("ZTARGET_LIMIT -- " + saBOMData[QUANTITY]); // Target --- Target?
		System.out.println("ZLOWER_LIMIT --" + saBOMData[LOWER_LIMIT]); // Lower Limit -- Min?
		
		System.out.println("--------------");
	}
	//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Starts
	public ArrayList getBOMAlternateSubstituteGrouping(Context ctx, ArrayList alBOM){		
		
		ArrayList tempAllBOM = new ArrayList();
		String strParentObjId = "";
		String strObjId = "";
		int ichildObjCount = 0;
		String strTempId="";
		boolean bIncGrouping = true;		
		
		if (alBOM != null) {			
			Iterator itr = alBOM.iterator();			
			while (itr.hasNext()) {
				String[] saTempBOM = (String[]) itr.next();
				strObjId = saTempBOM[DISPLAY_OBJECT_ID];
				strParentObjId = (String)hmChildObjects.get(strObjId);
				ichildObjCount = (int)hmParentObjects.get(strParentObjId);
				if(ichildObjCount>1){
					if(!bIncGrouping && !strTempId.equals(strParentObjId)){
						if (_cAltItem2 == 'Z') {
							_cAltItem2 = 'A';
							_cAltItem1--;
						} else {
							_cAltItem2++;				
						}
					}
					bIncGrouping=false;
					saTempBOM[SUBSTITUTE_FLAG]=_cAltItem1 + "" + _cAltItem2;
				}else{
					saTempBOM[SUBSTITUTE_FLAG]="";
				}
				tempAllBOM.add(saTempBOM);
				strTempId=strParentObjId;
			}
		}
		return tempAllBOM;		
	}
	//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed defect 11749 - Ends
	
}
