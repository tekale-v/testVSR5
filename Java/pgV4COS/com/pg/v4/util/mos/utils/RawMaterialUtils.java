/*
 **   RawMaterialUtils.java
 **   Description - Added by (DSM Sogeti) for Apr CW 2022 - MOS Defect 46042
 **   About - For MOS UI
 **
 */
package com.pg.v4.util.mos.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti)
 *
 */
public class RawMaterialUtils {
	
	/**
	 * Its helper class so constructor should be private
	 */
	private RawMaterialUtils() {}
	
	private static Logger logger = Logger.getLogger(RawMaterialUtils.class.getName());
	
	/**
	 * @param context
	 * @param lsAllConnectedProdParts
	 * @param processProdPartId
	 * @return
	 */
	public static Map getConnetedLatestReleasedProdPart(Context context, List lsAllConnectedProdParts, String processProdPartId, Set<String> doNotProcessIds) {
		Map mapRevsioninfo =null;
		if(!doNotProcessIds.contains(processProdPartId)) {
			MapList lsPartRevisions = getPartRevisions(context, processProdPartId);
			if (lsPartRevisions != null && !lsPartRevisions.isEmpty()) {
				doNotProcessIds.addAll(getObjectIdsFromMapList(lsPartRevisions));
				for (int i = lsPartRevisions.size() - 1; i >= 0; i--) {
					Map tmpObj = (Map) lsPartRevisions.get(i);
					String strid = (String) tmpObj.get(DomainConstants.SELECT_ID);
					String current = (String) tmpObj.get(DomainConstants.SELECT_CURRENT);
					if (DomainConstants.STATE_PART_RELEASE.equalsIgnoreCase(current) && lsAllConnectedProdParts.contains(strid)) {
						mapRevsioninfo = tmpObj;
						break;
					}
				}
			}
		}
		return mapRevsioninfo;
	}

	/**
	 * to get latest released revision
	 * 
	 * @param context
	 * @param strIPS
	 * @return
	 * @throws Exception
	 */
	public static MapList getPartRevisions(Context context, String strIPS) {
		StringList obselect = new StringList(2);
		obselect.add(DomainConstants.SELECT_ID);
		obselect.add(DomainConstants.SELECT_CURRENT);
		obselect.add(DomainConstants.SELECT_TYPE);
		obselect.add("attribute[" + pgV3Constants.ATTRIBUTE_TITLE + "]");
		
		MapList mlObjRevisions = null;
		try {
			DomainObject dom = DomainObject.newInstance(context, strIPS);
			mlObjRevisions = dom.getRevisionsInfo(context, obselect, new StringList());
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null ,e.getMessage());
		}
		return mlObjRevisions;
	}

	/**
	 * @param objList
	 * @return
	 */
	public static List<String> getObjectIdsFromMapList(MapList objList) {
		return (List<String>) objList.stream()
				.map(data -> ((Map<Object, Object>) data).get(DomainConstants.SELECT_ID))
				.collect(Collectors.toList());
	}
}
