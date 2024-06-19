package com.pg.enovia.mos.restapp.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.enovia.mos.config.MosConfig;
import com.pg.enovia.mos.enumeration.MOSConstants;
import com.pg.enovia.mos.restapp.bean.Column;
import com.pg.enovia.mos.restapp.bean.ObjectBean;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.beans.pgCOSDetailBean;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class MosComponentAppUtils {
	private MosConfig conf = null;
	/**
	 * Private constractor 
	 */
	private MosComponentAppUtils(Context context) throws JAXBException, MatrixException {
		this.conf = new MosConfig();
		this.conf.parseColumnPreferences(context, MOSConstants.CONFIG_PAGE.getValue()).getRootObject();
	}

	/**
	 * @param objIPSProduct
	 * @return
	 */
	public static String jaxbObjectToJSON(Object objIPSProduct) {
		String sw = "";
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			sw = mapper.writeValueAsString(objIPSProduct);

		} catch (Exception e) {
			Logger.getLogger(MosComponentAppUtils.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		}
		return sw;
	}

	/**
	 * @param context
	 * @param parentOID
	 * @return
	 */
	public static pgCOSDetailBean getAllMosDetails(Context context, String parentOID) {
		HashMap<String,HashMap<String,String>> paramMap = new HashMap<>();
		HashMap<String,String> requestMap = new HashMap<>();
		requestMap.put("parentOID", parentOID);
		paramMap.put("requestMap", requestMap);
		
		pgCOSDetailBean objIPSProduct = null;
		try {
			objIPSProduct = JPO.invoke(context, "pgCountriesOfSale", null, "getpgCOSDetail", JPO.packArgs(paramMap),
					pgCOSDetailBean.class);
		} catch (Exception e) {
			Logger.getLogger(MosComponentAppUtils.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		}
		return objIPSProduct;
	}
	
	/**
	 * @param context
	 * @param parentId
	 * @return
	 */
	public static List<ObjectBean> getMosDetails(Context context, String parentId) throws JAXBException, MatrixException {
		pgCOSDetailBean objIPSProduct = getAllMosDetails(context, parentId);
		MosComponentAppUtils uitl=new MosComponentAppUtils(context);
		return uitl.getChildObjectDetail(context, objIPSProduct);
	}
	
	/**
	 * @param context
	 * @param list
	 * @param objIPSProduct
	 */
	public List<ObjectBean> getChildObjectDetail(Context context, pgCOSDetailBean objIPSProduct) throws JAXBException, MatrixException {
		List<pgCOSDetailBean> childrenPD = objIPSProduct.getChildrenPD();
		List<ObjectBean> beanList = new ArrayList<>();
		StringList childStructure=null;
		if (null != childrenPD && !childrenPD.isEmpty()) {
			for (int i = 0; i < childrenPD.size(); i++) {
				childStructure = StringUtil.split(childrenPD.get(i).getIntermediateID()+"/"+childrenPD.get(i).getId()+"/"+childrenPD.get(i).getParentid(), "/");
				addChildData(context, childStructure, 0, beanList);
			}
		}
		return beanList;
	}

	/**
	 * @param context
	 * @param strIPS
	 * @param parentOID
	 * @param objIPSProduct
	 * @return
	 */
	private void addChildData(Context context, StringList objectIdList, int index, List<ObjectBean> beanList)
			throws JAXBException, MatrixException {
		if (objectIdList.size() > index) {
			if (beanList.isEmpty() && UIUtil.isNotNullAndNotEmpty(objectIdList.get(index))) {
				ObjectBean bean = getObjectDetails(context, objectIdList.get(index));
				beanList.add(bean);
				addChildData(context, objectIdList, index + 1, bean.getChild());
			} else {
				if(UIUtil.isNotNullAndNotEmpty(objectIdList.get(index))) {
					boolean isFoundInStructure = false;
					for (int i = 0; i < beanList.size(); i++) {
						ObjectBean bean = beanList.get(i);
						if (bean.getId().equals(objectIdList.get(index))) {
							isFoundInStructure = true;
							addChildData(context, objectIdList, index + 1, bean.getChild());
							break;
						}
					}
					if (!isFoundInStructure) {
						ObjectBean bean = getObjectDetails(context, objectIdList.get(index));
						beanList.add(bean);
						addChildData(context, objectIdList, index + 1, bean.getChild());
					}
				}else {
					addChildData(context, objectIdList, index + 1, beanList);
				}
			}
		}
		
	}

	/**
	 * @param context
	 * @param objectId
	 * @return
	 * @throws IOException 
	 * @throws MatrixException 
	 * @throws JAXBException 
	 */
	public ObjectBean getObjectDetails(Context context, String objectId){
		StringList localStringList = new StringList();
		ObjectBean bean=new ObjectBean();
		localStringList.add(DomainConstants.SELECT_ID);
		List<Column> column = conf.getRootObject().getPreference().get(0).getColumns().getColumn();
		Map<String, String> mpProgramMap = new HashMap<>();
		Column clm =null;
		for (int i = 0; i < column.size(); i++) {
			clm = column.get(i);
			if (!UIUtil.isNullOrEmpty(clm.getProgram())) {
				mpProgramMap.put(clm.getDispExpr(), clm.getProgram());
			} else {
				localStringList.add(clm.getExpr());
			}
		}
		try {
			DomainObject localDomainObject = DomainObject.newInstance(context, objectId);

			Map localMap = localDomainObject.getInfo(context, localStringList);
			String strType = (String) localMap.get(DomainConstants.SELECT_TYPE);
			HashMap<String,String> paramMap = new HashMap<>();
			paramMap.put("objectId", objectId);
			if (!mpProgramMap.isEmpty()) {
				Iterator<Map.Entry<String,String>> iterator = mpProgramMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String,String> me2 = iterator.next();
					String[] programname= mpProgramMap.get(me2.getKey()).split(":");
						String value = JPO.invoke(context, programname[0], null, programname[1], JPO.packArgs(paramMap),
								String.class);
						localMap.put(me2.getKey(), value);
				}
			}
			localMap.put(DomainConstants.SELECT_TYPE, UINavigatorUtil.getAdminI18NString("Type", strType, context.getSession().getLanguage()));
			udpateIntoBean(localMap, bean);
			bean.setOnClickUrl(LinkCreationForUI.getLinkInformation(context,strType,bean.getId()));
		} catch (Exception e) {
			Logger.getLogger(MosComponentAppUtils.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		}
		return bean;

	}

	/**
	 * @param localMap
	 * @param bean
	 */
	private void udpateIntoBean(Map localMap, ObjectBean bean) {
		bean.setTitle((String) localMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));

		bean.setIsArtExist(updateFCARTExistStatus((String) localMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST)));
		bean.setIsFCExist(updateFCARTExistStatus((String) localMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST)));
		bean.setIsNonReleasedArtExist(
				updateFCARTExistStatus((String) localMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST)));
		bean.setCountry((String) localMap.get("country"));
		bean.setId((String) localMap.get(DomainConstants.SELECT_ID));
		bean.setName((String) localMap.get(DomainConstants.SELECT_NAME));
		bean.setRevision((String) localMap.get(DomainConstants.SELECT_REVISION));
		bean.setType((String) localMap.get(DomainConstants.SELECT_TYPE));
	}
	
	/**
	 * @param strIsExist
	 * @return
	 */
	private String updateFCARTExistStatus(String strIsExist) {
		String strValue = "";
		if (UIUtil.isNotNullAndNotEmpty(strIsExist)) {
			if (strIsExist.equalsIgnoreCase(pgV3Constants.KEY_TRUE)) {
				strValue = pgV3Constants.KEY_YES_VALUE;
			} else {
				strValue = pgV3Constants.KEY_NO_VALUE;
			}
		}
		return strValue;
	}
	
	/**
	 * @param context
	 * @param domObj
	 * @return
	 * @throws FrameworkException
	 */
	public static boolean isPartSetProduct(Context context, DomainObject domObj) throws FrameworkException {
		boolean bCheckSetProduct = false;
		if(pgV3Constants.KEY_YES.equalsIgnoreCase(domObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
			bCheckSetProduct = true;
		}
		return bCheckSetProduct;
	}
}
