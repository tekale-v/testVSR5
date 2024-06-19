package com.pg.v4.util.mos.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v4.beans.pgCOSDetailBean;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti)
 *
 */
public class MosComponentAppUtils {

	private Context ctx;
	private String objId;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * @param context
	 * @param objectId
	 */
	public MosComponentAppUtils(Context context, String objectId) {
		this.ctx = context;
		this.objId = objectId;
	}

	/**
	 * To fetch market clearance details
	 * 
	 * @throws FrameworkException
	 * 
	 * @throws Exception
	 */
	public Map getMarketOfSaleDetails(String[] args) throws FrameworkException {
		Map returnMap = new HashMap();
		boolean isPushContext = false;
		try {
			// push context is required to get all components which participants for MOS calculation
			ContextUtil.pushContext(this.ctx);
			isPushContext = true;
			HashMap paramMap = JPO.unpackArgs(args);
			List tableConfigList = (List) paramMap.get("tableconfig");
			if (tableConfigList != null && !tableConfigList.isEmpty()) {
				Map confMap = (Map) tableConfigList.get(0);
				List dataList = processForMOS(confMap);
				if (dataList != null && !dataList.isEmpty()) {
					returnMap.put("objectList", dataList);
				} else {
					returnMap.put("objectList", new MapList());
				}
			} else {
				logger.log(Level.WARNING, null, "Config table details is empty or null..");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		} finally {
			if (isPushContext)
				ContextUtil.popContext(this.ctx);
		}
		return returnMap;
	}

	/**
	 * @param confMap
	 * @return
	 */
	private List processForMOS(Map confMap) {
		Part part = buildBean(confMap);
		pgCOSDetailBean objIPSProduct = getAllMosDetails();
		return processMOSAndBean(objIPSProduct, part);
	}

	/**
	 * @param objIPSProduct
	 * @param part
	 * @return
	 */
	private List<Map> processMOSAndBean(pgCOSDetailBean objIPSProduct, Part part) {
		List<pgCOSDetailBean> childrenPD = objIPSProduct.getChildrenPD();
		List<Map> objectList = new MapList();
		if (null != childrenPD && !childrenPD.isEmpty()) {
			Map<String, Map> beanMap = new HashMap();
			for (int i = 0; i < childrenPD.size(); i++) {
				pgCOSDetailBean bean = childrenPD.get(i);
				if (bean != null) {
					StringList slChilds = StringUtil
							.split(bean.getIntermediateID() + "/" + bean.getId() + "/" + bean.getParentid(), "/");
					slChilds.removeAll(Arrays.asList("", null, "null"));
					StringList lsChildStructure = new StringList();
					if (slChilds != null && !slChilds.isEmpty()) {
						for (int j = 0; j < slChilds.size(); j++) {
							Map partMap;
							if (!beanMap.containsKey(slChilds.get(j))) {
								partMap = getObjectDetails(slChilds.get(j), part);
								beanMap.put(slChilds.get(j), partMap);
							} else {
								partMap = beanMap.get(slChilds.get(j));
							}
							Map partMap1 = new HashMap();
							partMap1.putAll(partMap);
							lsChildStructure.add((String) partMap1.get("name"));
							StringList lsChildStructureClone = new StringList();
							lsChildStructureClone.addAll(lsChildStructure);
							partMap1.put("nameValue", lsChildStructureClone);
							objectList.add(partMap1);
						}
					}
				}
			}
		}
		return objectList;
	}

	/**
	 * @param context
	 * @param objectId
	 * @return
	 * @throws IOException
	 * @throws MatrixException
	 * @throws JAXBException
	 */
	private Map getObjectDetails(String objectId, Part part) {
		StringList localStringList = new StringList();
		Map localMap = new HashMap();
		localStringList.add(DomainConstants.SELECT_ID);
		try {
			DomainObject localDomainObject = DomainObject.newInstance(this.ctx, objectId);
			localMap = localDomainObject.getInfo(this.ctx, part.getBusinessSelecTables());

			HashMap<String, String> paramMap = new HashMap<>();
			paramMap.put("objectId", objectId);
			if (part.getProgramHtmlMapList() != null && !part.getProgramHtmlMapList().isEmpty()) {
				for (int i = 0; i < part.getProgramHtmlMapList().size(); i++) {
					Map<String, String> programMap = (Map) part.getProgramHtmlMapList().get(i);
					Iterator<Map.Entry<String, String>> iterator = programMap.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, String> me2 = iterator.next();
						String[] programname = programMap.get(me2.getKey()).split(":");
						String value = JPO.invoke(this.ctx, programname[0], null, programname[1],
								JPO.packArgs(paramMap), String.class);
						localMap.put(me2.getKey(), value);
					}
				}
			}
			localMap.put(DomainConstants.SELECT_ID, objectId);

			updateFilterTextIntoMap(part.getFilterTextSelecTables(), localMap);

		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		return localMap;
	}

	/**
	 * @param context
	 * @param parentOID
	 * @return
	 */
	private pgCOSDetailBean getAllMosDetails() {
		HashMap<String, HashMap<String, String>> paramMap = new HashMap<>();
		HashMap<String, String> requestMap = new HashMap<>();
		requestMap.put("parentOID", this.objId);
		paramMap.put("requestMap", requestMap);

		pgCOSDetailBean objIPSProduct = null;
		try {
			objIPSProduct = JPO.invoke(this.ctx, "pgCountriesOfSale", null, "getpgCOSDetail", JPO.packArgs(paramMap),
					pgCOSDetailBean.class);
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		return objIPSProduct;
	}

	/**
	 * @param confMap
	 */
	private Part buildBean(Map confMap) {
		Part part = new Part();
		MapList programHtmlMapList = new MapList();
		StringList singlevaluedSelectTables = new StringList();
		StringList filterTextSelectTables = new StringList();
		List configList = (List) confMap.get("columnconfig");
		for (int i = 0; i < configList.size(); i++) {
			Map columnMap = (Map) configList.get(i);
			String selectType = (String) columnMap.get("selecttype");
			String selectTable = (String) columnMap.get("selectable");
			boolean isFilterNeeded = (boolean) (columnMap.containsKey("filtertext")?columnMap.get("filtertext"):Boolean.FALSE);

			boolean isSort = columnMap.containsKey("sort") ? (boolean) columnMap.get("sort") : Boolean.FALSE;
			if (isSort) {
				part.setSortBy(selectTable);
			}
			if (isFilterNeeded)
				filterTextSelectTables.add(selectTable);

			if ("programhtml".equalsIgnoreCase(selectType)) {
				String field = (String) columnMap.get("field");
				Map map = new HashMap();
				map.put(field, selectTable);
				programHtmlMapList.add(map);
			} else {
				singlevaluedSelectTables.add(selectTable);
			}
		}
		part.setBusinessSelecTables(singlevaluedSelectTables);
		part.setProgramHtmlMapList(programHtmlMapList);
		part.setFilterTextSelecTables(filterTextSelectTables);
		return part;
	}

	/**
	 * @param filterFieldList
	 * @param localMap
	 */
	private void updateFilterTextIntoMap(StringList filterFieldList, Map localMap) {
		String strType = (String) localMap.get(DomainConstants.SELECT_TYPE);
		String objectId = (String) localMap.get(DomainConstants.SELECT_ID);
		try {
			for (int i = 0; i < filterFieldList.size(); i++) {
				String value = (String) localMap.get(filterFieldList.get(i));
				if (UIUtil.isNotNullAndNotEmpty(value)) {
					if ("TRUE".equalsIgnoreCase(value)) {
						value = "Yes";
					} else if ("FALSE".equalsIgnoreCase(value)) {
						value = "No";
					} else {
						value = "";
					}
				} else {
					value = "";
				}
				localMap.put(filterFieldList.get(i), value);
			}

			localMap.put(DomainConstants.SELECT_TYPE,
					UINavigatorUtil.getAdminI18NString("Type", strType, this.ctx.getSession().getLanguage()));
			localMap.put("onClickUrl", LinkCreationForUI.getLinkInformation(this.ctx, strType, objectId));
		} catch (MatrixException e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * @author DSM(Sogeti)
	 *
	 */
	protected class Part {
		String sortBy;
		StringList businessSelecTables;
		MapList programHtmlMapList;
		StringList filterTextSelecTables;

		/**
		 * @return the sortBy
		 */
		public String getSortBy() {
			return sortBy;
		}

		/**
		 * @param sortBy the sortBy to set
		 */
		public void setSortBy(String sortBy) {
			this.sortBy = sortBy;
		}

		/**
		 * @return the businessSelecTables
		 */
		public StringList getBusinessSelecTables() {
			return businessSelecTables;
		}

		/**
		 * @param businessSelecTables the businessSelecTables to set
		 */
		public void setBusinessSelecTables(StringList businessSelecTables) {
			this.businessSelecTables = businessSelecTables;
		}

		/**
		 * @return the programHtmlMapList
		 */
		public MapList getProgramHtmlMapList() {
			return programHtmlMapList;
		}

		/**
		 * @param programHtmlMapList the programHtmlMapList to set
		 */
		public void setProgramHtmlMapList(MapList programHtmlMapList) {
			this.programHtmlMapList = programHtmlMapList;
		}

		/**
		 * @return the filterTextSelecTables
		 */
		public StringList getFilterTextSelecTables() {
			return filterTextSelecTables;
		}

		/**
		 * @param filterTextSelecTables the filterTextSelecTables to set
		 */
		public void setFilterTextSelecTables(StringList filterTextSelecTables) {
			this.filterTextSelecTables = filterTextSelecTables;
		}

	}
}
