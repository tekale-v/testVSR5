/*
 **   MOSOverrideExpiration.java
 **   Description - Introduced as part of MOS Override - by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
 **   About - For MOS Override Expiration Functionality
 **
 */
package com.pg.v4.util.mos.override.ctrlm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.mos.override.MOSOverrideConstants;

import matrix.db.Context;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti)
 *
 */
public class MOSOverrideExpiration {
	
	private Context ctx;
	private DomainObject domMOSOverride;
	private boolean isOverrideObjectLoaded;
	private StringList overrideSelectTable;
	private StringList partSelectTable;
	private String date;
	private boolean isProcessFinished;
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 */
	public MOSOverrideExpiration(Context context) {
		this.ctx = context;
		this.preLoad();
	}

	/**
	 * 
	 */
	private void preLoad() {
		this.loadOverrideSelectTables();
		this.loadPartSelectTables();
		this.setDate();
	}
	
	/**
	 * 
	 */
	private void setDate() {
		if (UIUtil.isNullOrEmpty(this.date)) {
			Date dateVal = Calendar.getInstance().getTime();
			DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			this.date = dateFormatter.format(dateVal);
		}
	}
	/**
	 * 
	 */
	private void loadOverrideSelectTables() {
		if (this.overrideSelectTable == null) {
			this.overrideSelectTable = new StringList(3);
			this.overrideSelectTable.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDEFPP);
			this.overrideSelectTable.addElement(DomainConstants.SELECT_ID);
			this.overrideSelectTable.addElement(DomainConstants.SELECT_CURRENT);
			this.overrideSelectTable.addElement(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE);
		}
	}

	/**
	 * 
	 */
	private void loadPartSelectTables() {
		if (this.partSelectTable == null) {
			this.partSelectTable = new StringList(6);
			partSelectTable.addElement(DomainConstants.SELECT_ID);
			partSelectTable.addElement(DomainConstants.SELECT_REVISION);
			partSelectTable.addElement(DomainConstants.SELECT_CURRENT);
			partSelectTable.addElement(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN);
			partSelectTable.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
			partSelectTable.addElement("previous.current");
		}
	}

	/**
	 * @return the isOverrideObjectLoaded
	 */
	public boolean isOverrideObjectLoaded() {
		return isOverrideObjectLoaded;
	}

	/**
	 * @return the isProcessFinished
	 */
	public boolean isProcessFinished() {
		return isProcessFinished;
	}

	/**
	 * @param sObjectId
	 */
	public void loadOverrideObject(String sObjectId) {
		this.isOverrideObjectLoaded = Boolean.FALSE;
		try {
			this.domMOSOverride = DomainObject.newInstance(this.ctx, sObjectId);
			this.isOverrideObjectLoaded = Boolean.TRUE;
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
	}
	
	/**
	 * 
	 */
	public void startExpirationProcess() {
		this.isProcessFinished = Boolean.FALSE;
		
		try {
			Map overrideObjMap = this.domMOSOverride.getInfo(this.ctx, this.overrideSelectTable);

			String objectWhere = DomainConstants.SELECT_CURRENT + " != '" + pgV3Constants.STATE_INWORK + "'";
			String partWhere = DomainConstants.SELECT_CURRENT + " != '" + pgV3Constants.STATE_OBSOLETE + "'";

			MapList mlFPP = this.domMOSOverride.getRelatedObjects(this.ctx, // context
					pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relationshipPattern
					MOSOverrideConstants.MOS_OVERRIDE_VALID_TYPES, // typePattern
					this.partSelectTable, // objectSelects
					null, // relationshipSelects
					true, // getTo
					false, // getFrom
					(short) 1, // recurseToLevel
					partWhere, // objectWhere
					null, // relationshipWhere
					0); // limit

			if (null != mlFPP && !mlFPP.isEmpty()) {
				
				// Added by DSM(Sogeti)-2018x.6 Jan 2022 CW for Defect - 45405 - Starts
				String sFPPRevisionToProcess = getObjectRevision((String) overrideObjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDEFPP));
				// Added by DSM(Sogeti)-2018x.6 Jan 2022 CW for Defect - 45405 - Ends
				
				String sOverrideObjId = (String) overrideObjMap.get(DomainConstants.SELECT_ID);
				boolean isRequestForPOAOverride = ((String) overrideObjMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE)).equalsIgnoreCase(MOSOverrideConstants.KEY_POA_OVERRIDE);
				
				Map<String,String> hmAttributes;
				
				
				for (int i = 0; i < mlFPP.size(); i++) {
					
					Map mpFPP = (Map) mlFPP.get(i);
					String sRevision = (String) mpFPP.get(DomainConstants.SELECT_REVISION);

					boolean isPartHasActivePOAOverride = ((String) mpFPP.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN)).equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE);
					
					String sId = (String) mpFPP.get(DomainConstants.SELECT_ID);
					
					DomainObject domIps = DomainObject.newInstance(this.ctx, sId);
					
					MapList mlConnectedMOSOverride = domIps.getRelatedObjects(this.ctx,
							pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEOVERRIDE, // relationship // pattern
							pgV3Constants.TYPE_PGCOSOVERRIDE, // Type pattern
							this.overrideSelectTable, // object selects
							DomainConstants.EMPTY_STRINGLIST, // rel selects
							false, // to side
							true, // from side
							(short) 1, // recursion level
							objectWhere, // object where clause
							DomainConstants.EMPTY_STRING, // rel where clause
							0);// limit

					boolean updateOverrideFlag = Boolean.FALSE;
					boolean updateCOSFlag = Boolean.FALSE;
					if (isRequestForPOAOverride) {
						if (isPartHasActivePOAOverride) {
							boolean isPassed = hasAnotherActivePOAOverrideRequest(mlConnectedMOSOverride, sOverrideObjId);
							if (!isPassed) {
								updateOverrideFlag = Boolean.TRUE;
							}
							updateCOSFlag = Boolean.TRUE;
						}
					} else {
						if (!isPartHasActivePOAOverride && (sFPPRevisionToProcess.equalsIgnoreCase(sRevision) || this.isLatestActiveOverrideRequest(this.ctx, sFPPRevisionToProcess, domIps, mlConnectedMOSOverride))) {
							updateOverrideFlag = Boolean.TRUE;
							updateCOSFlag = Boolean.TRUE;
						}
					}
					hmAttributes = new HashMap<String, String>();
					if (updateOverrideFlag) {
						hmAttributes.put(isRequestForPOAOverride?MOSOverrideConstants.ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN:pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN, pgV3Constants.KEY_NO_VALUE);
					}
					
					if(updateCOSFlag) {
						hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE,pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
						hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, this.date);
					}
					
					if(updateCOSFlag || updateOverrideFlag) {
						domIps.setAttributeValues(this.ctx, hmAttributes);
					}
				}
			}
			this.isProcessFinished = Boolean.TRUE;
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
			this.isProcessFinished = Boolean.FALSE;
		}
	}

	/**
	 * @throws FrameworkException
	 */
	public void obsoleteOverrideObject() throws FrameworkException {
		this.domMOSOverride.setState(this.ctx, DomainConstants.STATE_PART_OBSOLETE);
	}
	/**
	 * @param mlConnectedMOSOverride
	 * @param sOverrideObjId
	 * @return
	 */
	private boolean hasAnotherActivePOAOverrideRequest(MapList mlConnectedMOSOverride, String sOverrideObjId) {
		boolean isActive = Boolean.FALSE;
		for (Iterator iterator = mlConnectedMOSOverride.iterator(); iterator.hasNext();) {
			Map object = (Map) iterator.next();
			String sCurrent = (String) object.get(DomainConstants.SELECT_CURRENT);
			String sId = (String) object.get(DomainConstants.SELECT_ID);
			String sOverrideType = (String) object.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE);
			if ((!sOverrideObjId.equalsIgnoreCase(sId)) && pgV3Constants.STATE_APPROVED.equalsIgnoreCase(sCurrent)
					&& MOSOverrideConstants.KEY_POA_OVERRIDE.equalsIgnoreCase(sOverrideType)) {
				isActive = Boolean.TRUE;
				break;
			}
		}
		return isActive;
	}

	// Added by DSM(Sogeti)-2018x.6 Jan 2022 CW for Defect - 45405 - Starts
	/**
	 * Description: getting override requested original part revision to un-mark has
	 * override to false.
	 * 
	 * @param sOverrideFPPObj
	 * @return
	 */
	private String getObjectRevision(String sOverrideFPPObj) {
		String sRev = DomainConstants.EMPTY_STRING;
		if (UIUtil.isNotNullAndNotEmpty(sOverrideFPPObj)) {
			String[] fppValue = sOverrideFPPObj.split(" ");
			if (fppValue != null && fppValue.length == 2) {
				sRev = fppValue[1];
			}
		}
		return sRev;
	}

	/**
	 * Description: checking FPP has more approved override request connected or
	 * not.
	 * 
	 * @param context
	 * @param sORFPPRevision
	 * @param domFPP
	 * @return
	 * @throws FrameworkException
	 */
	private boolean isLatestActiveOverrideRequest(Context context, String sORFPPRevision, DomainObject domFPP,
			MapList mlFPPMOSOverride) throws FrameworkException {
		boolean isPartValidForUnMarking = Boolean.FALSE;
		if (mlFPPMOSOverride != null && !mlFPPMOSOverride.isEmpty()) {
			MapList theRevisions = domFPP.getRevisionsInfo(context, new StringList(DomainConstants.SELECT_REVISION),
					new StringList());
			List<String> revisionList = (List<String>) theRevisions.stream()
					.map(data -> ((Map<Object, Object>) data).get(DomainConstants.SELECT_REVISION))
					.collect(Collectors.toList());

			int sORRevisionIndex = revisionList.indexOf(sORFPPRevision);
			isPartValidForUnMarking = isLastActiveRequest(sORRevisionIndex, revisionList, mlFPPMOSOverride);
		}
		return isPartValidForUnMarking;
	}

	/**
	 * @param sORRevisionIndex
	 * @param revisionList
	 * @param mlFPPMOSOverride
	 * @return
	 */
	private boolean isLastActiveRequest(int sORRevisionIndex, List<String> revisionList, MapList mlFPPMOSOverride) {
		boolean isLastActiveOR = Boolean.TRUE;
		for (Iterator iterator = mlFPPMOSOverride.iterator(); iterator.hasNext();) {
			Map objMap = (Map) iterator.next();
			String sRevision = getObjectRevision((String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOSOVERRIDEFPP));
			if (revisionList.indexOf(sRevision) > sORRevisionIndex) {
				isLastActiveOR = Boolean.FALSE;
				break;
			}
		}
		return isLastActiveOR;
	}
	// Added by DSM(Sogeti)-2018x.6 Jan 2022 CW for Defect - 45405 - Ends

}
