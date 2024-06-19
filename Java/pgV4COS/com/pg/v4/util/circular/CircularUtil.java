/*
 * Name: CircularUtil.java
 * About: Contains utility methods to find circular reference.
 * Since: 18x.5
 */
package com.pg.v4.util.circular;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.circular.beans.PartBean;

import com.pg.v4.util.enumeration.CircularConstant;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CircularUtil {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Default Constructor
	 *
	 * @since DSM 2018x.5
	 */
	public CircularUtil() {
		logger.info("CircularUtil - Constructor");
	}

	/**
	 * Method to check if circular reference exists in a given assembly structure.
	 *
	 * @param context - Context
	 * @param objectId - String
	 * @param objectList - MapList
	 * @return boolean - true/false
	 * @since DSM 2018x.5
	 */
	public boolean isCircularExist(Context context, String objectId, MapList objectList) {
		boolean isCircular = false;
		PartBean partMasterBean = getPartMasterBean(context, objectId);
		if(null != partMasterBean) {
			partMasterBean = getPartMasterBeanWithBOMStructure(partMasterBean, objectList);

			logger.info("Part Master bean with BOM Structure created !");
			isCircular = getBOMCircularCheck(partMasterBean);
			logger.info("Circular reference check performed successfully !");
		}
		logger.info("Is Circular Reference Exist: "+isCircular);
		return isCircular;
	}

	/**
	 * Method to create a part master bean.
	 *
	 * @param context - Context
	 * @param objectId - String
	 * @param level - String
	 * @return PartBean - bean object
	 * @throws FrameworkException, MatrixException - exception
	 * @since DSM 2018x.5
	 */
	private PartBean getPartMasterBean(Context context, String objectId) {
		PartBean partBean = null;
		try {
			DomainObject dObj = DomainObject.newInstance(context, objectId);
			Map dObjInfo = dObj.getInfo(context, getPartBeanMappingSelectable());
			dObjInfo.put(DomainConstants.SELECT_LEVEL, CircularConstant.LEVEL_ZERO.getValue());
			dObjInfo.put(CircularConstant.RELATIONSHIP.getValue(), DomainConstants.RELATIONSHIP_EBOM);
			partBean = getPartBean(dObjInfo);
			logger.info("Part Master Bean created -> " + partBean.getName());
		} catch (FrameworkException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return partBean;
	}

	/**
	 * Method to get list of all mapping selects.
	 *
	 * @return StringList - list of select.
	 * @since DSM 2018x.5
	 */
	private StringList getPartBeanMappingSelectable() {
		StringList selectList = new StringList();
		selectList.addElement(DomainConstants.SELECT_TYPE);
		selectList.addElement(DomainConstants.SELECT_NAME);
		selectList.addElement(DomainConstants.SELECT_REVISION);
		selectList.addElement(DomainConstants.SELECT_ID);
		selectList.addElement(DomainConstants.SELECT_LEVEL);
		selectList.addElement(CircularConstant.RELATIONSHIP.getValue());
		return selectList;
	}
	/**
	 * Method to convert BOM structure (MapList) to a bean.
	 *
	 * @param partMaster - PartBean
	 * @param bomDataMapList - MapList
	 * @return PartBean - bean object
	 * @since DSM 2018x.5
	 */
	private PartBean getPartMasterBeanWithBOMStructure(PartBean partMaster, MapList bomDataMapList) {

		Table<Integer, Integer, PartBean> dataTable = HashBasedTable.create();

		Iterator iterator = bomDataMapList.iterator();

		int level;
		Map dataMap;
		PartBean currentPartBean;
		PartBean parentPartBean;
		List<PartBean> existingChildren;
		List<PartBean> newChildren;
		while (iterator.hasNext()) {
			dataMap = (Map) iterator.next();
			level = Integer.parseInt((String) dataMap.get(DomainConstants.SELECT_LEVEL));
			currentPartBean = getPartBean(dataMap);

			if (level == 1) {

				// clear the table whenever level is 1.
				dataTable.clear();

				// for level 1 Part - set the Parent as Top-Level Parent bean.
				currentPartBean.setParent(partMaster);
				currentPartBean.setParentExist(true);

				// get top-level Parent child.
				existingChildren = partMaster.getChildren();
				if (existingChildren != null) {
					// if top-level Parent has child. then append this current Part also as child.
					existingChildren.add(currentPartBean);
					partMaster.setChildren(existingChildren);
				} else {
					// if top-level parent does not have child, then add the current Part as child.
					newChildren = new ArrayList<>();
					newChildren.add(currentPartBean);
					partMaster.setChildren(newChildren);
					partMaster.setChildExist(true);
				}
			}

			// find parent at level-1
			parentPartBean = dataTable.row(level - 1).get(level - 1);
			if (parentPartBean != null) {

				// set current part parent.
				currentPartBean.setParent(parentPartBean);
				currentPartBean.setParentExist(true);

				// get parent's child.
				existingChildren = parentPartBean.getChildren();

				if (existingChildren != null) {
					// if parent has child, then append current part also as child and reset the child on parent.
					existingChildren.add(currentPartBean);
					parentPartBean.setChildren(existingChildren);
				} else {
					// if top-level parent does not have child, then add the current Part as child.
					newChildren = new ArrayList<>();
					newChildren.add(currentPartBean);
					parentPartBean.setChildren(newChildren);
					parentPartBean.setChildExist(true);
				}
			}
			dataTable.put(level, level, currentPartBean);
		}
		dataTable.clear();
		return partMaster;
	}

	/**
	 * Method to get PartBean object from a Map.
	 *
	 * @param infoMap - Map
	 * @return PartBean - bean object
	 * @since DSM 2018x.5
	 */
	private PartBean getPartBean(Map infoMap) {

		Map<String, String> beanMap = new HashMap<>();

		beanMap.put(DomainConstants.SELECT_ID, (String)infoMap.get(DomainConstants.SELECT_ID));
		beanMap.put(DomainConstants.SELECT_TYPE, (String)infoMap.get(DomainConstants.SELECT_TYPE));
		beanMap.put(DomainConstants.SELECT_NAME, (String)infoMap.get(DomainConstants.SELECT_NAME));
		beanMap.put(DomainConstants.SELECT_REVISION, (String)infoMap.get(DomainConstants.SELECT_REVISION));
		beanMap.put(CircularConstant.RELATIONSHIP.getValue(), (String)infoMap.get(CircularConstant.RELATIONSHIP.getValue()));
		beanMap.put(DomainConstants.SELECT_LEVEL, (String)infoMap.get(DomainConstants.SELECT_LEVEL));

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.convertValue(beanMap, PartBean.class);
	}

	/**
	 * Method to print BOM Structure like MQL.
	 *
	 * @param part - PartBean
	 * @since DSM 2018x.5
	 */
	private void printStructureLikeMQL(PartBean part) {
		logger.info(printLikeMQLIndentation(part));
	}

	/**
	 * Method to indent and print BOM Structure like MQL.
	 *
	 * @param part - PartBean
	 * @return String - string
	 * @since DSM 2018x.5
	 */
	private String printLikeMQLIndentation(PartBean part) {
		int level = Integer.parseInt(part.getLevel());
		StringBuffer stringBuffer = new StringBuffer();
		if (level != 1) {
			for (int i = 1; i <= level; i++) {
				stringBuffer.append(StringUtils.SPACE);
			}
		}
		stringBuffer.append(printLikeMQLTNR(part));
		return stringBuffer.toString();
	}

	/**
	 * Method to print BOM Structure TNR like MQL.
	 *
	 * @param part - PartBean
	 * @return String - string
	 * @since DSM 2018x.5
	 */
	private String printLikeMQLTNR(PartBean part) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(part.getLevel());
		stringBuffer.append(StringUtils.SPACE);
		stringBuffer.append(part.getRelationship());
		stringBuffer.append(StringUtils.SPACE);
		stringBuffer.append(CircularConstant.TO_SIDE.getValue());
		stringBuffer.append(StringUtils.SPACE);
		stringBuffer.append(part.getType());
		stringBuffer.append(StringUtils.SPACE);
		stringBuffer.append(part.getName());
		stringBuffer.append(StringUtils.SPACE);
		stringBuffer.append(part.getRevision());
		return stringBuffer.toString();
	}

	/**
	 * Method to expand and print BOM Structure TNR like MQL.
	 *
	 * @param part - PartBean
	 * @since DSM 2018x.5
	 */
	private void printBOMStructureLikeMQL(PartBean part) {
		if (part.isChildExist()) {
			List<PartBean> children = part.getChildren();
			children.stream().forEach(child -> {
				printStructureLikeMQL(child);
				printBOMStructureLikeMQL(child);
			});
		}
	}

	/**
	 * Method to print BOM Parents.
	 *
	 * @param part - PartBean
	 * @param parentList - List<String>
	 * @since DSM 2018x.5
	 */
	private void printBOMParents(PartBean part, List<String> parentList) {
		logger.info("Iterating ->" +part.getName() + " " +part.getRevision() + " -> parents -> " + parentList);
	}

	/**
	 * Method to print BOM Child.
	 *
	 * @param part - PartBean
	 * @param childrenList - List<String>
	 * @since DSM 2018x.5
	 */
	private void printBOMChildren(PartBean part, List<String> childrenList) {
		logger.info("Iterating ->" +part.getName() + " "+part.getRevision()+ " -> children -> " + childrenList);
	}

	/**
	 * Method to get BOM Parent recursively.
	 *
	 * @param partBean - PartBean
	 * @param parentBeanList - List<PartBean>
	 * @return List<PartBean> - list of part
	 * @since DSM 2018x.5
	 */
	private List<PartBean> getBOMParentRecursively(PartBean partBean, List<PartBean> parentBeanList) {
		if (partBean.isParentExist()) {
			PartBean parent = partBean.getParent();
			parentBeanList.add(parent);
			getBOMParentRecursively(parent, parentBeanList);
		}
		return parentBeanList;
	}

	/**
	 * Method to get BOM Child recursively.
	 *
	 * @param part - PartBean
	 * @param childrenBeanList - List<PartBean>
	 * @return List<PartBean> - list of part
	 * @since DSM 2018x.5
	 */
	private List<PartBean> getBOMChildrenRecursively(PartBean part, List<PartBean> childrenBeanList) {
		if (part.isChildExist()) {
			List<PartBean> children = part.getChildren();
			for (PartBean child : children) {
				childrenBeanList.add(child);
				getBOMChildrenRecursively(child, childrenBeanList);
			}
		}
		return childrenBeanList;
	}


	/**
	 * Method to check if circular reference exist in a BOM Structure.
	 *
	 * @param partBean - PartBean
	 * @return boolean - true/false
	 * @since DSM 2018x.5
	 */
	private boolean getBOMCircularCheck(PartBean partBean) {
		boolean isCircular = false;

		if (partBean.isChildExist()) {

			List<PartBean> children = partBean.getChildren();

			// below variable are used in iterator.
			List<PartBean> parentBeanList;
			List<PartBean> childBeanList;
			List<String> parentIDList;
			List<String> childIDList;
			PartBean childPartBean;
			boolean isParentConnectedAsChild;
			Collection<String> commonElements;

			Iterator<PartBean> childrenItr = children.iterator();
			while (childrenItr.hasNext()) {
				parentBeanList = new ArrayList<>();
				childBeanList = new ArrayList<>();
				childPartBean = childrenItr.next();

				// get current part's parent recursively (up-ward direction)
				parentBeanList = getBOMParentRecursively(childPartBean, parentBeanList);

				// to check if the current part is connected as child in its own assembly.
				parentBeanList.add(childPartBean);


				// get current part's child recursively (down-ward direction)
				childBeanList = getBOMChildrenRecursively(childPartBean, childBeanList);

				// compare with object id.
				parentIDList = parentBeanList.stream().map(m -> m.getId()).collect(Collectors.toList());
				childIDList = childBeanList.stream().map(m -> m.getId()).collect(Collectors.toList());
				isParentConnectedAsChild = !Collections.disjoint(parentIDList, childIDList);
				commonElements = CollectionUtils.intersection(parentIDList, childIDList);

				if (isParentConnectedAsChild) {
					printBOMParents(childPartBean, parentIDList);
					printBOMChildren(childPartBean, childIDList);
					logger.info(commonElements + ">>>>>>> Connected in its own assembly as Child >>>>>> Circulr Reference Found-: "+isParentConnectedAsChild);
					isCircular = true;
					break;
				}
				getBOMCircularCheck(childPartBean);
			}
		}
		return isCircular;
	}
}
