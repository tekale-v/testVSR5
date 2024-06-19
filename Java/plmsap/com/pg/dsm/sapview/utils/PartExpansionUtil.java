package com.pg.dsm.sapview.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sapview.alternate.CATIAAlternatePart;
import com.pg.dsm.sapview.alternate.CATIAAlternatePartContext;
import com.pg.dsm.sapview.beans.bo.Part;
import com.pg.dsm.sapview.beans.bo.SAPPartBean;
import com.pg.dsm.sapview.config.SAPConfig;
import com.pg.dsm.sapview.config.SAPConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class PartExpansionUtil {
	private Context ctx;
	private SAPConfig config;
	private StringList validParts;
	private SAPPartBean part;
	private SAPPartBean clonePart;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 * @param conf
	 */
	public PartExpansionUtil(SAPConfig conf, SAPPartBean productPart) {
		this.ctx = conf.getContext();
		this.config = conf;
		this.validParts = new StringList();
		this.part = productPart;
	}

	/**
	 * @param productPart
	 * @return
	 */
	public PartExpansionUtil execute() {
		this.clonePart = this.part.copy();

		if (pgV3Constants.TYPE_PGASSEMBLEDPRODUCT.equalsIgnoreCase(this.part.getType())) {
			try {
				DomainObject domApolloAPPObject = DomainObject.newInstance(this.ctx, this.part.getId());

				String sAuthoringApplication = domApolloAPPObject.getInfo(this.ctx,
						pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);

				if (pgV3Constants.RANGE_PGAUTHORINGAPPLICATION_LPD.equalsIgnoreCase(sAuthoringApplication)) {
					this.getApolloAPPBOMAlternatesSubForBOMeDelivery(this.part);
				} else {
					this.getParentBOMAndSubstitutePartForBOMeDelivery();
				}
			} catch (FrameworkException e) {
				logger.log(Level.WARNING, null, e);
			}
		} else if (pgV3Constants.TYPE_PGTRANSPORTUNITPART.equalsIgnoreCase(this.part.getType())) {
			this.getTUPParentFPPForBOMeDelivery(this.part.getId());
		} else {
			this.getParentBOMAndSubstitutePartForBOMeDelivery();
		}
		return this;
	}

	/**
	 * @return the validParts
	 */
	public StringList getValidParts() {
		return validParts;
	}

	/**
	 * @param validParts the validParts to set
	 */
	public void appendValidParts(String validDeliveryPart) {
		if (UIUtil.isNotNullAndNotEmpty(validDeliveryPart) && !this.validParts.contains(validDeliveryPart))
			this.validParts.add(validDeliveryPart);
	}

	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	private void getParentBOMAndSubstitutePartForBOMeDelivery() {

		StringList parentBOMAndSubstitutesParts = this.getParentPartForBOMeDelivery(this.clonePart.getId());
		this.appendParts(parentBOMAndSubstitutesParts);

		if (!this.config.getProperties().getSapSubstituteNotAllowedTypes().contains(this.clonePart.getType())) {
			StringList parentSubstitutesParts = this.getSubstituteParts(this.clonePart.getId());
			this.appendParts(parentSubstitutesParts);
		}

	}

	/**
	 * @param objId
	 * @return
	 */
	private StringList getTUPsConnectedFPPsForBOMeDelivery(String objId) {
		StringList lsConnectedFPPIds = new StringList();

		MapList mlTUPsFPP = this.getTUPsConnectedFPPs(objId);
		if (mlTUPsFPP != null && !mlTUPsFPP.isEmpty()) {
			for (Iterator iterator = mlTUPsFPP.iterator(); iterator.hasNext();) {
				Map objMap = (Map) iterator.next();
				
				String objFPPId = (String) objMap.get(DomainConstants.SELECT_ID);
				String sCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
				String isPrimaryTUP = (String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGISPRIMARY);
				if (this.partIsInValidState(sCurrent) && UIUtil.isNotNullAndNotEmpty(isPrimaryTUP) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(isPrimaryTUP)) {
					lsConnectedFPPIds.addElement(objFPPId);
				}
			}
		}
		return lsConnectedFPPIds;
	}

	/**
	 * @param appPart
	 */
	private void getApolloAPPBOMAlternatesSubForBOMeDelivery(SAPPartBean appPart) {
		StringList validParentParts = new StringList();
		try {
			CATIAAlternatePartContext.setContext(this.ctx);

			CATIAAlternatePart catiaAlternatePart = new CATIAAlternatePart.Expand(appPart.getId()).filter().load();
			List<Part> qualifiedWhereUsedParts = catiaAlternatePart.getQualifiedWhereUsedParts();

			if (null != qualifiedWhereUsedParts && !qualifiedWhereUsedParts.isEmpty()) {
				String partType;
				String partId;
				for (Part partBean : qualifiedWhereUsedParts) {
					partType = partBean.getType();
					partId = partBean.getId();
					if (this.config.getProperties().getSapParentTypes().contains(partType)
							&& !validParentParts.contains(partId)) {
						validParentParts.add(partId);
					}
				}
			}

			CATIAAlternatePartContext.removeContext();
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		this.appendParts(validParentParts);
	}

	/**
	 * @param partIds
	 */
	private void appendParts(StringList partIds) {
		if (partIds != null && !partIds.isEmpty()) {
			for (Iterator iterator = partIds.iterator(); iterator.hasNext();) {
				String id = (String) iterator.next();
				this.appendValidParts(id);
			}
		}
	}

	/**
	 * @param context
	 * @param objectId
	 * @return
	 * @throws FrameworkException
	 */
	private StringList getParentPartForBOMeDelivery(String objectId) {
		MapList list = this.removeInvalidParentStructure(this.expandParentBOM(objectId));

		list = this.removeCOPAndCOPCaseParentParts(list);

		list = this.removeCOPBulkParentProductParts(list);

		return this.getParentBOMAndSubstituteParts(list);
	}

	/**
	 * @param context
	 * @param objectId
	 * @return
	 * @throws FrameworkException
	 */
	private StringList getSubstituteParts(String objectId) {
		StringList returnList = new StringList();

		Map mapSubstitutes = this.getSubstituteDetails(objectId);

		if (mapSubstitutes != null && !mapSubstitutes.isEmpty()) {
			StringList slId = StringHelper.convertToStringList(mapSubstitutes
					.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id"));
			StringList slType = StringHelper.convertToStringList(mapSubstitutes
					.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.type"));
			StringList slCurrent = StringHelper.convertToStringList(mapSubstitutes
					.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.current"));
			Map objMap = new HashMap();
			for (int i = 0; i < slId.size(); i++) {
				objMap.put(DomainConstants.SELECT_ID, slId.get(i));
				objMap.put(DomainConstants.SELECT_TYPE, slType.get(i));
				if (this.isLastParentPart(objMap)) {
					if (!returnList.contains(slId.get(i)) && this.partIsInValidState(slCurrent.get(i)))
						returnList.add(slId.get(i));
				} else {
					if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equals(slType.get(i))
							&& this.partIsInValidState(slCurrent.get(i))) {
						StringList slCUPSubBOMCompIds = this.getCUPReshiperBOMComponents(slId.get(i));
						for (String id : slCUPSubBOMCompIds) {
							if (!returnList.contains(id))
								returnList.add(id);
						}
					} if (pgV3Constants.TYPE_FABRICATEDPART.equals(slType.get(i))) {
					    returnList.add(slId.get(i));
					}
					this.clonePart = new SAPPartBean(objMap, this.config);
					StringList subParentParts = this.getParentPartForBOMeDelivery(slId.get(i));
					for (int j = 0; j < subParentParts.size(); j++) {
						if (!returnList.contains(subParentParts.get(j)))
							returnList.add(subParentParts.get(j));
					}
				}
			}
		}
		return returnList;
	}

	/**
	 * @param current
	 * @return
	 */
	private boolean partIsInValidState(String current) {
		boolean isValidState = Boolean.FALSE;
		if (DomainConstants.STATE_PART_RELEASE.equalsIgnoreCase(current))
			isValidState = Boolean.TRUE;
		return isValidState;
	}

	/**
	 * @param context
	 * @param objectId
	 * @return
	 */
	private Map getSubstituteDetails(String objectId) {
		StringList selecTables = new StringList(6);
		selecTables.add("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id");
		selecTables.add("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.type");
		selecTables.add("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.current");
		selecTables.add(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_ID);
		selecTables.add(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_TYPE);
		selecTables.add(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_CURRENT);

		Map mapSubstitutes = new HashMap();
		try {
			DomainObject domips = DomainObject.newInstance(this.ctx, objectId);
			Map mapTmpSubstitutes = BusinessUtil.getInfoList(this.ctx, domips.getObjectId(), selecTables);

			if (mapTmpSubstitutes != null && !mapTmpSubstitutes.isEmpty()) {
				StringList slId = StringHelper.convertToStringList(
						mapTmpSubstitutes.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id"));
				StringList slType = StringHelper.convertToStringList(mapTmpSubstitutes
						.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.type"));
				StringList slCurrent = StringHelper.convertToStringList(mapTmpSubstitutes
						.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.current"));

				if (mapTmpSubstitutes.containsKey(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_ID)) {
					StringList slFBOMSubId = StringHelper.convertToStringList(
							mapTmpSubstitutes.get(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_ID));
					StringList slFBOMSubType = StringHelper.convertToStringList(
							mapTmpSubstitutes.get(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_TYPE));
					StringList slFBOMSubCurrent = StringHelper.convertToStringList(
							mapTmpSubstitutes.get(SAPConstants.RELATIONSHIP_FBOM_SUBSTITUTE_CURRENT));
					for (int i = 0; i < slFBOMSubId.size(); i++) {
						String sFBOMId = slFBOMSubId.get(i);
						String sFBOMType = slFBOMSubType.get(i);
						String sFBOMCurrent = slFBOMSubCurrent.get(i);
						slId.add(sFBOMId);
						slType.add(sFBOMType);
						slCurrent.add(sFBOMCurrent);
					}
				}
				mapSubstitutes.put("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id", slId);
				mapSubstitutes.put("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.type", slType);
				mapSubstitutes.put("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.current",
						slCurrent);
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		return mapSubstitutes;
	}

	/**
	 * @param context
	 * @param list
	 * @return
	 * @throws FrameworkException
	 */
	private StringList getParentBOMAndSubstituteParts(MapList list) {
		StringList returnList = new StringList();

		for (int i = 0; i < list.size(); i++) {
			Map map = (Map) list.get(i);
			String sId = (String) map.get(DomainConstants.SELECT_ID);
			String sType = (String) map.get(DomainConstants.SELECT_TYPE);
			String sCurrent = (String) map.get(DomainConstants.SELECT_CURRENT);
			if (this.partIsInValidState(sCurrent)) {
				if (this.config.getProperties().getSapParentTypes().contains(sType) && !returnList.contains(sId)) {
					if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sType)) {
						StringList substituteTypes = StringHelper
								.convertToStringList(map.get("frommid[EBOM Substitute].to.type"));
						if (substituteTypes == null || substituteTypes.isEmpty()
								|| !substituteTypes.contains(pgV3Constants.TYPE_PGCUSTOMERUNITPART))
							returnList.add(sId);
					} else {
						returnList.add(sId);
					}
					continue;
				}

				if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equals(sType)) {
					StringList slCUPSubBOMCompIds = this.getCUPReshiperBOMComponents(sId);
					for (String id : slCUPSubBOMCompIds) {
						if (!returnList.contains(id))
							returnList.add(id);
					}
				} else if (pgV3Constants.TYPE_PGTRANSPORTUNITPART.equals(sType)) {
					StringList lsConnectedFPPIds = this.getTUPsConnectedFPPsForBOMeDelivery(sId);

					if (lsConnectedFPPIds != null && !lsConnectedFPPIds.isEmpty()) {
						for (String id : lsConnectedFPPIds) {
							if (!returnList.contains(id)) {
								returnList.addElement(id);
							}
						}
					}
				}
			}
		}
		return returnList;
	}

	/**
	 * @param sId
	 * @return
	 */
	private StringList getCUPReshiperBOMComponents(String sId) {
		StringList returnList = new StringList();
		Map mapSubstitutes = this.getSubstituteDetails(sId);

		StringList slId = StringHelper.convertToStringList(
				mapSubstitutes.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id"));
		StringList slType = StringHelper.convertToStringList(
				mapSubstitutes.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.type"));
		StringList slCurrent = StringHelper.convertToStringList(
				mapSubstitutes.get("to[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.current"));
		for (int j = 0; j < slId.size(); j++) {
			if (this.config.getProperties().getSapParentTypes().contains(slType.get(j))
					&& !returnList.contains(slId.get(j)) && this.partIsInValidState(slCurrent.get(j))) {
				returnList.add(slId.get(j));
			}
		}
		return returnList;
	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	private MapList expandParentBOM(String id) {
		if (UIUtil.isNotNullAndNotEmpty(id)) {
			try {
				DomainObject domObj = DomainObject.newInstance(this.ctx, id);
				return domObj.getRelatedObjects(this.ctx,
						pgV3Constants.RELATIONSHIP_EBOM + "," + pgV3Constants.RELATIONSHIP_PLBOM + ","
								+ pgV3Constants.RELATIONSHIP_PLANNEDFOR, // relationshipPattern
						DomainConstants.QUERY_WILDCARD, // typePattern
						true, // getTo
						false, // getFrom
						(short) 0, // recurseToLevel
						this.getBusSelectable(), // objectSelects
						this.getRelSelectable(), // relationshipSelects
						DomainConstants.EMPTY_STRING, // objectWhere
						null, // relationshipWhere
						null, // PostRelPattern
						null, // PostTypePattern
						null);
			} catch (FrameworkException e) {
				logger.log(Level.WARNING, null, e);
			}
		}
		return null;
	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	private MapList getTUPsConnectedFPPs(String id) {
		if (UIUtil.isNotNullAndNotEmpty(id)) {
			try {
				StringList relSelect=new StringList(1);
				relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISPRIMARY);
				DomainObject domObj = DomainObject.newInstance(this.ctx, id);
				return domObj.getRelatedObjects(this.ctx, pgV3Constants.RELATIONSHIP_PGTRANSPORTUNIT, // relationshipPattern
						pgV3Constants.TYPE_FINISHEDPRODUCTPART, // typePattern
						true, // getTo
						false, // getFrom
						(short) 1, // recurseToLevel
						this.getBusSelectable(), // objectSelects
						relSelect, // relationshipSelects
						DomainConstants.EMPTY_STRING, // objectWhere
						null, // relationshipWhere
						null, // PostRelPattern
						null, // PostTypePattern
						null);
			} catch (FrameworkException e) {
				logger.log(Level.WARNING, null, e);
			}
		}
		return null;
	}

	/**
	 * @param objId
	 */
	private void getTUPParentFPPForBOMeDelivery(String objId) {
		StringList lsConnectedFPPIds = this.getTUPsConnectedFPPsForBOMeDelivery(objId);
		this.appendParts(lsConnectedFPPIds);
	}

	/**
	 * @return
	 */
	private StringList getBusSelectable() {
		StringList list = new StringList(4);
		list.add(DomainConstants.SELECT_ID);
		list.add(DomainConstants.SELECT_NAME);
		list.add(DomainConstants.SELECT_TYPE);
		list.add(DomainConstants.SELECT_CURRENT);
		return list;
	}

	/**
	 * @return
	 */
	private StringList getRelSelectable() {
		StringList list = new StringList(1);
		list.add("frommid[EBOM Substitute].to.type");
		return list;
	}

	/**
	 * This method will remove child of the parent types
	 *
	 * @param objectList
	 * @return Modified for Defect# 44589
	 */
	private MapList removeInvalidParentStructure(MapList objectList) {
		MapList mlValidObjList = new MapList();
		if (objectList != null && !objectList.isEmpty()) {
			int parentTypeLevel = 0;
			boolean parentTypeIsFound = Boolean.FALSE;

			for (Iterator iterator = objectList.iterator(); iterator.hasNext();) {
				Map busMap = (Map) iterator.next();

				int level = Integer.parseInt((String) busMap.get(DomainConstants.SELECT_LEVEL));

				if (parentTypeIsFound && parentTypeLevel < level) {
					continue;
				}

				if (this.isLastParentPart(busMap)) {
					parentTypeIsFound = Boolean.TRUE;
					mlValidObjList.add(busMap);
					parentTypeLevel = level;
					continue;
				} else if (parentTypeLevel >= level) {
					parentTypeIsFound = Boolean.FALSE;
					parentTypeLevel = level;
				}
				mlValidObjList.add(busMap);
			}
		}
		return mlValidObjList;
	}

	/**
	 * @param busMap
	 * @return
	 */
	private boolean isLastParentPart(Map busMap) {
		boolean isLastParentPart = Boolean.FALSE;
		String strType = (String) busMap.get(DomainConstants.SELECT_TYPE);
		String strId = (String) busMap.get(DomainConstants.SELECT_ID);

		if (this.config.getProperties().getSapParentTypes().contains(strType)) {
			try {

				if (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType)) {
					DomainObject domObj = DomainObject.newInstance(this.ctx, strId);
					String strExpandBOMonSAPBOMasFed = domObj.getInfo(this.ctx,
							pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);

					boolean isExpandIsTrue = Boolean.FALSE;

					if (UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed)
							&& pgV3Constants.TRUE.equalsIgnoreCase(strExpandBOMonSAPBOMasFed)) {
						isExpandIsTrue = Boolean.TRUE;
					}

					if (!isExpandIsTrue) {
						isLastParentPart = Boolean.TRUE;
					}
				} else {
					isLastParentPart = Boolean.TRUE;
				}
			} catch (FrameworkException e) {
				logger.log(Level.WARNING, null, e);
			}
		}
		return isLastParentPart;
	}

	/**
	 * @param objectList
	 */
	private MapList removeCOPAndCOPCaseParentParts(MapList objectList) {
		MapList mlValidObjList = new MapList();

		boolean isCOPInCOPCase = Boolean.FALSE;
		boolean parentTypeIsFound = Boolean.FALSE;
		int parentTypeLevel = 0;

		boolean isFirstCOP = Boolean.FALSE;
		int firstCOPLevel = 0;

		if (this.clonePart.getType().contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)) {
			isFirstCOP = Boolean.TRUE;
		}

		for (Iterator iterator = objectList.iterator(); iterator.hasNext();) {
			Map busMap = (Map) iterator.next();

			int level = Integer.parseInt((String) busMap.get(DomainConstants.SELECT_LEVEL));
			String strType = (String) busMap.get(DomainConstants.SELECT_TYPE);

			if (parentTypeIsFound && parentTypeLevel < level) {
				continue;
			}

			if (isFirstCOP && firstCOPLevel >= level) {
				isFirstCOP = Boolean.FALSE;
				firstCOPLevel = 0;
			}

			if (isFirstCOP && pgV3Constants.TYPE_PGCONSUMERUNITPART.contains(strType) && firstCOPLevel == level - 1) {
				isCOPInCOPCase = Boolean.TRUE;
			} else if (!isFirstCOP && pgV3Constants.TYPE_PGCONSUMERUNITPART.contains(strType)) {
				isFirstCOP = Boolean.TRUE;
				firstCOPLevel = level;
			} else {
				isCOPInCOPCase = Boolean.FALSE;
			}

			if (isCOPInCOPCase) {
				parentTypeIsFound = Boolean.TRUE;
				parentTypeLevel = level;
				continue;
			} else if (parentTypeLevel >= level) {
				parentTypeIsFound = Boolean.FALSE;
				parentTypeLevel = level;
			}
			mlValidObjList.add(busMap);
		}
		return mlValidObjList;
	}

	/**
	 * @param objectList
	 */
	private MapList removeCOPBulkParentProductParts(MapList objectList) {
		MapList mlValidObjList = new MapList();

		boolean isBulkParentIsProductPart = Boolean.FALSE;
		boolean parentTypeIsFound = Boolean.FALSE;
		int parentTypeLevel = 0;

		boolean isCOPBulk = Boolean.FALSE;

		if (this.clonePart.getType().contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)
				&& this.isPartCOPBulk(this.part.getId())) {
			isCOPBulk = Boolean.TRUE;
		}

		for (Iterator iterator = objectList.iterator(); iterator.hasNext();) {
			Map busMap = (Map) iterator.next();

			int level = Integer.parseInt((String) busMap.get(DomainConstants.SELECT_LEVEL));
			String strType = (String) busMap.get(DomainConstants.SELECT_TYPE);
			String strId = (String) busMap.get(DomainConstants.SELECT_ID);

			if (parentTypeIsFound && parentTypeLevel < level) {
				continue;
			}

			if (isCOPBulk && this.isTypeValidForHalb(strType)) {
				isBulkParentIsProductPart = Boolean.TRUE;
			} else if (!isCOPBulk && pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strType)
					&& this.isPartCOPBulk(strId)) {
				isCOPBulk = Boolean.TRUE;
			} else {
				isBulkParentIsProductPart = Boolean.FALSE;
			}

			if (isBulkParentIsProductPart) {
				parentTypeIsFound = Boolean.TRUE;
				parentTypeLevel = level;
				continue;
			} else if (parentTypeLevel >= level) {
				parentTypeIsFound = Boolean.FALSE;
				parentTypeLevel = level;
				isCOPBulk = Boolean.FALSE;
			}
			mlValidObjList.add(busMap);
		}
		return mlValidObjList;
	}

	/**
	 * @return
	 */
	private boolean isPartCOPBulk(String sId) {
		boolean isPartBulk = Boolean.FALSE;
		try {
			DomainObject domObj = DomainObject.newInstance(this.ctx, sId);
			String assType = domObj.getInfo(this.ctx, pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			if (UIUtil.isNotNullAndNotEmpty(assType)
					&& pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(assType)) {
				isPartBulk = Boolean.TRUE;
			}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		return isPartBulk;
	}

	/**
	 * @param sType
	 * @return
	 */
	private boolean isTypeValidForHalb(String sType) {
		boolean isValid = Boolean.FALSE;
		StringList list = new StringList();
		list.add(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
		list.add(pgV3Constants.TYPE_DEVICEPRODUCTPART);
		list.add(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
		if (list.contains(sType)) {
			isValid = Boolean.TRUE;
		}
		return isValid;
	}
}
