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
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GetStabilityResults {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetStabilityResults(Context context, String sOID){
		_context = context;
		_OID = sOID;
	}
	public StabilityResults getComponent() {
		
		return getStabilityResultsData(_context, _OID);
	
}
	
	/**
	 * Retrieve Stability Results Data
	 * @param context
	 * @param args
	 * @return StringBuilder
	 * @throws MatrixException
	 */
	private StabilityResults getStabilityResultsData(Context context, String strObjectId) {
		
		StabilityResults stabilityResults = new StabilityResults();
		List<StabilityResult> stabilityResultsList=stabilityResults.getStabilityResults();
		MapList mlStabilityResults = new MapList();
		try {
			if(StringHelper.validateString(strObjectId)) {
				HashMap programMap  = new HashMap();				
				programMap.put("objectId",strObjectId);
				programMap.put("type",pgV3Constants.TYPE_PGSTABILITYRESULTS);
				String[] args=JPO.packArgs(programMap);
				JPO.unpackArgs(args);
				mlStabilityResults=(MapList)PDFPOCHelper.executeMainClassMethod(context, "pgFPPRollup", "getAllTableData",args );
				String strProdPhysicalId = DomainConstants.EMPTY_STRING;
				String sProductPartName = DomainConstants.EMPTY_STRING;
				String sRevision  = DomainConstants.EMPTY_STRING;
				String sTitle = DomainConstants.EMPTY_STRING;
				StringList objectsSelect=new StringList();
				objectsSelect.add(DomainConstants.SELECT_NAME);
				objectsSelect.add(DomainConstants.SELECT_REVISION);
				objectsSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				Map mConnectedStabilityResults=null;
				DomainObject domProductPart = DomainObject.newInstance(context);
				if(mlStabilityResults!=null && !mlStabilityResults.isEmpty()) {
					
					int immlStabilityResults = mlStabilityResults.size();
					for (int i = 0; i < immlStabilityResults; i++) {
						StabilityResult stabilityResult=new StabilityResult();
						mConnectedStabilityResults = (Map)mlStabilityResults.get(i);
						strProdPhysicalId =StringHelper.convertObjectToString(mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID));
						if(!UIUtil.isNullOrEmpty(strProdPhysicalId)) {
							domProductPart.setId(strProdPhysicalId);
							mConnectedStabilityResults = domProductPart.getInfo(context,objectsSelect);
							sProductPartName = StringHelper.convertObjectToString(mConnectedStabilityResults.get(DomainConstants.SELECT_NAME));
							sRevision = StringHelper.convertObjectToString(mConnectedStabilityResults.get(DomainConstants.SELECT_REVISION));
							sTitle = StringHelper.convertObjectToString(mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
						}
						
						stabilityResult.setName(sProductPartName);
						stabilityResult.setRevision(sRevision);
						stabilityResult.setTitle(sTitle);
						stabilityResult.setExpirationdate(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPIRATIONDATE)));
						stabilityResult.setShelflife(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGTOTALSHELFLIFE)));
						stabilityResult.setTempreturegroup(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGTEMPERATUREGROUP)));
						stabilityResult.setHumiditygroup(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGHUMIDITYGROUP)));
						stabilityResult.setTransportfreezeprotection(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGTRANSPORTFREEZEPROTECTION)));
						stabilityResult.setTransportheatprotection(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGTRANSPORTHEATPROTECTION)));
						stabilityResult.setBoundrycondition(StringHelper.validateString1((String)mConnectedStabilityResults.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOUNDARYCONDITIONS)));
						stabilityResult.setStabilityreport(StringHelper.validateString1((String)mConnectedStabilityResults.get(DomainConstants.SELECT_NAME)));
						
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
