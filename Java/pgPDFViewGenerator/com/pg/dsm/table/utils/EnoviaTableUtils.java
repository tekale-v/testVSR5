package com.pg.dsm.table.utils;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.ui.form.FormUI;
import com.pg.dsm.ui.table.TableUtils;

import matrix.db.Context;

/**
 * @author DSM Sogeti
 *
 */
public class EnoviaTableUtils {
	
	/**
	 * private constructor
	 */
	private EnoviaTableUtils() {}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getNotesTableData(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgIPMTablesJPO:getTableCharacteristicSequenceData");
		utils.setTable("pgDSOFinishedProductNotesTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getPlants(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgIPMProductData:getPlants");
		utils.setTable("pgDSOPlantDataSummary");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getATSDSO(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgDSOCPNProductData:getRelatedATSSummary");
		utils.setTable("pgRelatedATSListTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getFinalIngredients(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgIPMProductData:getTableData");
		utils.setTable("pgIPMFinalIngredientsTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();
		otherInputParameters.put("parentRelName", "relationship_pgProductComposition");		
		return utils.getTableData(otherInputParameters);
	}
	
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getDerivedTo(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("emxCommonPart:getDerivedToParts");
		utils.setTable("ENCDerivedToParts");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getDerivedFrom(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("emxCommonPart:getDerivedFromParts");
		utils.setTable("ENCDerivedFromParts");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getMaterialProduced(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgDSOUtil:getRelatedDefineMaterials");
		utils.setTable("pgMaterialsProducedTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getIPSecurity(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgIPSecurityCommonUtil:getObjectClassesAll");
		utils.setTable("LCClassificationList");
		HashMap<String,String> otherInputParameters=new HashMap<>();
		otherInputParameters.put("type", "IP Control Class");	
		otherInputParameters.put("reportFormat", "CSV");
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getSecurityControl(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgIPSecurityCommonUtil:getObjectClassesAll");
		utils.setTable("LCClassificationList");
		HashMap<String,String> otherInputParameters=new HashMap<>();
		otherInputParameters.put("type", "Security Control Class");		
		return utils.getTableData(otherInputParameters);
	}
	
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getDGC(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("emxCPNProductData:getConnectedDangerousGoods");
		utils.setTable("pgDangerousGoodsSummary");
		HashMap<String,String> otherInputParameters=new HashMap<>();			
		return utils.getTableData(otherInputParameters);
	}
	

	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getAlternate(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("emxPart:getAlternateParts");
		utils.setTable("ENCAlternatePartsSummary");		
		HashMap<String,String> otherInputParameters=new HashMap<>();
		otherInputParameters.put("parentOID", objectId);		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getRegistrationTableDetails(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgFPPRollup:getAllTableData");
		utils.setTable("pgFPPRegistrationDetailsTable");		
		HashMap<String,String> otherInputParameters=new HashMap<>();
		otherInputParameters.put("parentOID", objectId);
		otherInputParameters.put("type", "pgMarketRegistration");
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getBatteryRollUp(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgFPPRollup:getAllTableData");
		utils.setTable("pgProductBatteryViewTable");		
		HashMap<String,String> otherInputParameters=new HashMap<>();
		otherInputParameters.put("parentOID", objectId);
		otherInputParameters.put("type", "pgBattery");
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static Map getBatteryRollUpFormDetails(Context context,String objectId) {
		FormUI formUtil = new FormUI(context,objectId);
		formUtil.setForm("pgBatteryCalculationForm");	
		HashMap<String,String> otherInputParameters=new HashMap<>();
		return formUtil.getFormData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getProducingFormula(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgDSOUtil:getRelatedFormulas");
		utils.setTable("pgProducingFormulaTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getGlobalHarmonizedStandardData(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgAWL_Util:getGHSCopyList");
		utils.setTable("pgGlobalHarmonizedStandardTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getCertificationTable(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgDSMMaterialCertificationsUtil:getCertificationClaims");
		utils.setTable("pgCertificationsSummary");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getARTRelatedParts(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("emxCPNProductDataSearch:getSpecRelatedParts");
		utils.setTable("CPNProductDataList");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getATSRelParts(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgDSOCPNProductData:getRelatedPartsSummary");
		utils.setTable("pgRelatedPartsListTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getATSRevision(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("emxCommonUtilBase:getAllRevisions");
		utils.setTable("CPNProductDataViewTableList");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	public static MapList getSAPBOM(Context context,String objectId) {
		TableUtils utils=new TableUtils(context, objectId);
		utils.setProgram("pgIPMSAPBOMJPO:getTableData");
		utils.setTable("pgIPMFlatBOMViewTable");
		HashMap<String,String> otherInputParameters=new HashMap<>();		
		return utils.getTableData(otherInputParameters);
	}
	
	
}
