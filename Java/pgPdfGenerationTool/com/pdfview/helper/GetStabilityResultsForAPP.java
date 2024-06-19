package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.StabilityResult;
import com.pdfview.impl.FPP.StabilityResults;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;

public class GetStabilityResultsForAPP {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetStabilityResultsForAPP(Context context, String sOID){
		_context = context;
		_OID = sOID;
	}
	public StabilityResults getComponent() {
		
		return getStabilityResultsData(_context, _OID);
	
}
	
	
	/**
	 * Method to retrieve Stability results data
	 * @param context
	 * @param strObjectId
	 * @return
	 */
	private StabilityResults getStabilityResultsData(Context context, String strObjectId) {
		
		StabilityResults stabilityResults = new StabilityResults();
		List<StabilityResult> stabilityResultsList=stabilityResults.getStabilityResults();
		DomainObject domainObject = null;
		MapList mlStabilityResults = new MapList();
		try {
			if(StringHelper.validateString(strObjectId)) {
				HashMap programMap  = new HashMap();				
				programMap.put("objectId",strObjectId);
				String[] args=JPO.packArgs(programMap);
				String sMasterName =DomainConstants.EMPTY_STRING;
				String sMasterRevision =DomainConstants.EMPTY_STRING;
				String sMasterTitle =DomainConstants.EMPTY_STRING;
				String sExpirationDateForStabilityResults =DomainConstants.EMPTY_STRING;
				String sTotalShelfLife =DomainConstants.EMPTY_STRING;
				String sTemparatureGroup =DomainConstants.EMPTY_STRING;
				String sHumidityGroup =DomainConstants.EMPTY_STRING;
				String sTransportFreezeProtection =DomainConstants.EMPTY_STRING;
				String sTransportHeatProtection =DomainConstants.EMPTY_STRING;
				String sBoundaryConditions=DomainConstants.EMPTY_STRING;
				String sStabilityReport=DomainConstants.EMPTY_STRING;
				
				mlStabilityResults=(MapList)PDFPOCHelper.executeMainClassMethod(context, "pgFPPRollup", "getStabilityResultsonProductPart",args );
				DomainObject domProductPart = DomainObject.newInstance(context);
				Map productPartInfo=null;
				if(mlStabilityResults!=null && !mlStabilityResults.isEmpty()) {
					int iSize = mlStabilityResults.size();
					Map mpStabilityResultMap=null;
					for (int i = 0; i < iSize; i++) {
						StabilityResult stabilityResult=new StabilityResult();
						mpStabilityResultMap=(Map)mlStabilityResults.get(i);
						sMasterName=(String)mpStabilityResultMap.get("from.name");
						sMasterName=UIUtil.isNullOrEmpty(sMasterName)?DomainConstants.EMPTY_STRING:sMasterName;
						sMasterRevision=(String)mpStabilityResultMap.get("from.revision");
						sMasterRevision=UIUtil.isNullOrEmpty(sMasterRevision)?DomainConstants.EMPTY_STRING:sMasterRevision;
						sMasterTitle=(String)mpStabilityResultMap.get("from."+pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						sMasterTitle=UIUtil.isNullOrEmpty(sMasterTitle)?DomainConstants.EMPTY_STRING:sMasterTitle;
						sExpirationDateForStabilityResults=(String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPIRATIONDATE);
						sTotalShelfLife=(String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGTOTALSHELFLIFE);
						sTemparatureGroup=(String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGTEMPERATUREGROUP);
						sHumidityGroup=(String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGHUMIDITYGROUP);
						sTransportFreezeProtection=(String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGTRANSPORTFREEZEPROTECTION);
						sTransportHeatProtection=(String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGTRANSPORTHEATPROTECTION);
						sStabilityReport = (String)mpStabilityResultMap.get(DomainConstants.SELECT_NAME);
						sBoundaryConditions = (String)mpStabilityResultMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOUNDARYCONDITIONS);
						stabilityResult.setName(sMasterName);
						stabilityResult.setRevision(sMasterRevision);
						stabilityResult.setTitle(sMasterTitle);
						stabilityResult.setExpirationdate(sExpirationDateForStabilityResults);
						stabilityResult.setShelflife(sTotalShelfLife);
						stabilityResult.setTempreturegroup(sTemparatureGroup);
						stabilityResult.setHumiditygroup(sHumidityGroup);
						stabilityResult.setTransportfreezeprotection(sTransportFreezeProtection);
						stabilityResult.setTransportheatprotection(sTransportHeatProtection);
						stabilityResult.setBoundrycondition(sBoundaryConditions);
						stabilityResult.setStabilityreport(sStabilityReport);
						stabilityResultsList.add(stabilityResult);
					}
				}
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return stabilityResults;
	}
	
}
