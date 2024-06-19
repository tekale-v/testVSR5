package com.pdfview.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.RelatedATS;
import com.pdfview.impl.FPP.RelatedATSData;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList; 
public class GetRelatedATS {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetRelatedATS(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/**
	 * Method to retrieve the ATS data
	 * @return
	 */
	public RelatedATS getComponent() {
		RelatedATS relATS = new RelatedATS();
		List<RelatedATSData> lsATS = relATS.getRelatedATSData();
		
		Map<String,String> m1ATSMap = null;
		try {
			if(UIUtil.isNotNullAndNotEmpty(_OID)){
				String strIsATSId = DomainConstants.EMPTY_STRING;
				String strType = DomainConstants.EMPTY_STRING;
				String strName = DomainConstants.EMPTY_STRING;
				String strRevision = DomainConstants.EMPTY_STRING;
				String strState = DomainConstants.EMPTY_STRING;
				String strTitle = DomainConstants.EMPTY_STRING;
				String strATSType = DomainConstants.EMPTY_STRING;
				DomainObject specDom = null;
				MapList mlATSSorted = new MapList();
				Map ATSPack = new HashMap();
				ATSPack = new HashMap();
				ATSPack.put("objectId", _OID);
				ATSPack.put("table","pgIPMIRMSIsATS");
				String[] args = JPO.packArgs(ATSPack);				
				MapList mlATS= (MapList)PDFPOCHelper.executeMainClassMethod(_context, "pgDSOCPNProductData", "getRelatedATSSummary", args);
				Map m1ATSMapSorted = null;
				Map mpATS = new HashMap();
				Map m1ATSMapUnsorted = new HashMap();
				StringList selectStmtSub = new StringList(4);
				selectStmtSub.add(DomainConstants.SELECT_TYPE);
				selectStmtSub.add(DomainConstants.SELECT_CURRENT);
				selectStmtSub.add(DomainConstants.SELECT_REVISION);
				selectStmtSub.add(DomainConstants.SELECT_NAME);
				selectStmtSub.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				int nIsATSSize=mlATS.size();
				if(nIsATSSize>0){
					DomainObject dmoChkType = DomainObject.newInstance(_context, _OID);
					for (Iterator iterator = mlATS.iterator(); iterator.hasNext();){
						m1ATSMapUnsorted = (Map) iterator.next();
						strIsATSId = (String)m1ATSMapUnsorted.get(DomainConstants.SELECT_ID);
						specDom = DomainObject.newInstance(_context,strIsATSId);
						mpATS = specDom.getInfo(_context,selectStmtSub);
						strType = (String)mpATS.get(DomainConstants.SELECT_TYPE);
						strName = (String)mpATS.get(DomainConstants.SELECT_NAME);
						strRevision = (String)mpATS.get(DomainConstants.SELECT_REVISION);
						strState = (String)mpATS.get(DomainConstants.SELECT_CURRENT);
						strTitle = (String)mpATS.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						m1ATSMapSorted = new HashMap();
						m1ATSMapSorted.put(DomainConstants.SELECT_TYPE,strType);
						m1ATSMapSorted.put(DomainConstants.SELECT_NAME,strName);
						m1ATSMapSorted.put(DomainConstants.SELECT_CURRENT,strState);
						m1ATSMapSorted.put(DomainConstants.SELECT_REVISION,strRevision);
						m1ATSMapSorted.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE,strTitle);
						mlATSSorted.add(m1ATSMapSorted);
					}
					mlATSSorted.addSortKey(DomainConstants.SELECT_NAME, "ascending", "String");
					mlATSSorted.sort();
					for (Iterator iterator = mlATSSorted.iterator(); iterator.hasNext();){
						RelatedATSData relATSData = new RelatedATSData();
						m1ATSMap = (Map)iterator.next();
						strType = (String)m1ATSMap.get(DomainConstants.SELECT_TYPE);
						strATSType = UINavigatorUtil.getAdminI18NString("Type", strType, _context.getSession().getLanguage());
						relATSData.setType(strATSType);
						strName = (String)m1ATSMap.get(DomainConstants.SELECT_NAME);
						relATSData.setName(strName);
						strRevision =(String)m1ATSMap.get(DomainConstants.SELECT_REVISION);
						relATSData.setRevision(strRevision);
						strTitle = (String)m1ATSMap.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						if (UIUtil.isNotNullAndNotEmpty(strTitle)) {
							strTitle = StringHelper.filterLessAndGreaterThanSign(strTitle);
						}
						relATSData.setTitle(strTitle);
						strState = (String) m1ATSMap.get(DomainConstants.SELECT_CURRENT);
						relATSData.setState(strState);
						lsATS.add(relATSData);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return relATS;
	}
}
