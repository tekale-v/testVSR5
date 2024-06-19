package com.pg.dsm.preference.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.dsm.preference.enumeration.DSMUPTConstants.PartCategory;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;

import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.db.Vault;
import matrix.util.StringList;

public class UserPreferenceTemplateUtil {
    private static final Logger logger = Logger.getLogger(UserPreferenceUtil.class.getName());

    /**
     * This is method add pgUPTPhysicalIDExtn and User Preference Template Physical ID on Part.
     *
     * @param context.
     * @param strUPTPhyId.
     * @param strPartId.
     * @throws Exception
     */
    public static void addUPTPhysicalIdExtension(Context context, String strUPTPhyId, DomainObject domPart) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(strUPTPhyId) && null != domPart) {
            Instant startTime = Instant.now();
            DSMUPT upt = new DSMUPT(context, strUPTPhyId);
            logger.log(Level.INFO, "Adding Extension on :: " + domPart.getName());
            logger.log(Level.INFO, "UPT Physical Id :: " + strUPTPhyId);
            //Get context Vault.
            Vault vault = context.getVault();
            //Create Business object of interface pgUPTAttributePhysicalIDExtn.
            BusinessInterface busUPTPhyIDExtn = new BusinessInterface(UserPreferenceTemplateConstants.Interface.INTERFACE_UPT_PHYSICAL_ID_EXTN.getName(context), vault);
            if (null != busUPTPhyIDExtn) {
                //Create Part Domain Object.
                //Get existing Interface list of part.
                BusinessInterfaceList InterfaceList = domPart.getBusinessInterfaces(context, false);
                logger.log(Level.INFO, "Interface List :: " + InterfaceList.toString());
                //If interface pgUPTAttributePhysicalIDExtn does not exist on part then add it.
                if (!InterfaceList.contains(busUPTPhyIDExtn)) {
                    //Add interface pgUPTAttributePhysicalIDExtn on part.
                    domPart.addBusinessInterface(context, busUPTPhyIDExtn);
                }
                //Set Physical Id of UPT on the Part
                Map<String, String> attributeMap = new HashMap<String, String>();
                attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PHYSICAL_ID.getName(context), strUPTPhyId);
                domPart.setAttributeValues(context, attributeMap);
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.log(Level.INFO, "Add UPT Physical ID Extension - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        }
    }

    /**
     * This method returns Pick List Object names list from it's physical id.
     *
     * @param context
     * @param slPickListObjectPhyId : Pick List object Physical Id.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String getPickListObjectNames(Context context, StringList slPickListObjectPhyId) throws Exception {
        String sMatFunNames = DomainConstants.EMPTY_STRING;
        logger.log(Level.INFO, "UPT Pick List Object Physical ID :: " + slPickListObjectPhyId);
        if (null != slPickListObjectPhyId && !slPickListObjectPhyId.isEmpty()) {
            //Split Physical Ids.
            try {
                String[] arrsAttrPhyIDs = slPickListObjectPhyId.toArray(String[]::new);
                //Get Names of Pick List objects.
                MapList objectList = DomainObject.getInfo(context, arrsAttrPhyIDs, StringList.create(DomainConstants.SELECT_NAME));
                List<String> nameList = new ArrayList<>();
                //Add all Names of Pick List objects in list.
                if (null != objectList && !objectList.isEmpty()) {
                    for (Object object : objectList) {
                        nameList.add((String) ((Map<Object, Object>) object).get(DomainConstants.SELECT_NAME));
                    }
                }
                //Split Name list by comma.
                if (!nameList.isEmpty()) {
                    sMatFunNames = String.join(UserPreferenceTemplateConstants.Basic.SYMB_COMMA.get(), nameList);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception occurred in method getPickListObjectNames() : " + e);
                throw e;
            }
            logger.log(Level.INFO, "Materical Function Names :: " + sMatFunNames);
        }
        return sMatFunNames;
    }

    /**
     * This method returns connected Pick List object by given relationship to Part.
     *
     * @param context
     * @param mIPClass          : IP Class Object Map.
     * @param strIPClassSelects : IP Class Select.
     * @return Map<String, String> : Containing IP Class Information.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getRestricatedIPClass(Context context, Map<Object, Object> mIPClass, String strIPClassSelects) throws Exception {
        Map<String, String> mIPClassDetails = new HashMap<>();
        Map<String, String> mReturnIPClassDetails = new HashMap<>();
        StringList slIPSelects = new StringList(2);
        slIPSelects.add(DomainConstants.SELECT_ID);
        slIPSelects.add(DomainConstants.SELECT_NAME);
        if (null != mIPClass && !mIPClass.isEmpty()) {
            //Get IP Class Information from attribute Map.
            StringList slIPClassId = getAttributeValueFromMap(context, mIPClass, DomainObject.getAttributeSelect(strIPClassSelects));
            logger.log(Level.INFO, "Business IP Class ID :: " + slIPClassId);
            if (null != slIPClassId && !slIPClassId.isEmpty()) {
                //DomainObject domIPClass = DomainObject.newInstance(context);
                StringList slName = new StringList(slIPClassId.size());
                StringList slIds = new StringList(slIPClassId.size());
                logger.log(Level.INFO, "IP Class Name List :: " + slName);
                logger.log(Level.INFO, "IP Class ID List :: " + slIds);
                //If class is single then get IP Class Information from attribute Map.
                if (slIPClassId.size() == 1) {
                    MapList mlIPClassInfo = getObjectInfo(context, slIPClassId, slIPSelects);
                    if (null != mlIPClassInfo && !mlIPClassInfo.isEmpty()) {
                        Iterator<Map<String, String>> mapIterator = mlIPClassInfo.iterator();
                        if (mapIterator.hasNext()) {
                            mIPClassDetails = (Map<String, String>) mapIterator.next();
                            slName.add(mIPClassDetails.get(DomainConstants.SELECT_NAME));
                            slIds.add(mIPClassDetails.get(DomainConstants.SELECT_ID));      
                        }
                    }
                } else {
                    MapList mlIPClassInfo = getObjectInfo(context, slIPClassId, slIPSelects);
                    if (null != mlIPClassInfo && !mlIPClassInfo.isEmpty()) {
                        Iterator<Map<String, String>> mapIterator = mlIPClassInfo.iterator();
                        while (mapIterator.hasNext()) {
                            mIPClassDetails = (Map<String, String>) mapIterator.next();
                            slName.add(mIPClassDetails.get(DomainConstants.SELECT_NAME));
                            slIds.add(mIPClassDetails.get(DomainConstants.SELECT_ID));
                        }
                    }
                }
                //Add IP class ID and Name in return map.
                if (!slName.isEmpty() && !slIds.isEmpty()) {
                    if (UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_BUSINESS_USE_IP_CLASS.getName(context).equals(strIPClassSelects)) {
                        mReturnIPClassDetails.put(UserPreferenceTemplateConstants.Basic.UPT_BUSINESS_USE_IP_CLASS_NAME.get(), String.join(UserPreferenceTemplateConstants.Basic.SYMB_PIPE.get(), slName));
                        mReturnIPClassDetails.put(UserPreferenceTemplateConstants.Basic.UPT_BUSINESS_USE_IP_CLASS_ID.get(), String.join(UserPreferenceTemplateConstants.Basic.SYMB_PIPE.get(), slIPClassId));
                    } else if (UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_HIGHLY_RESTRICTED_IP_CLASS.getName(context).equals(strIPClassSelects)) {
                        mReturnIPClassDetails.put(UserPreferenceTemplateConstants.Basic.UPT_HIGHLY_RESTRICTED_IP_CLASS_NAME.get(), String.join(UserPreferenceTemplateConstants.Basic.SYMB_PIPE.get(), slName));
                        mReturnIPClassDetails.put(UserPreferenceTemplateConstants.Basic.UPT_HIGHLY_RESTRICTED_IP_CLASS_ID.get(), String.join(UserPreferenceTemplateConstants.Basic.SYMB_PIPE.get(), slIPClassId));
                    }
                }
            }
        }
        logger.log(Level.INFO, "Business IP Class Information :: " + mReturnIPClassDetails);
        return mReturnIPClassDetails;
    }

    /**
     * This method returns multiple object information in MapList.
     *
     * @param context
     * @param slObjIds     : List of object id.
     * @param slBusSelects : Bus Selects.
     * @return MapList : List on object information.
     * @throws Exception
     */
    public static MapList getObjectInfo(Context context, StringList slObjIds, StringList slBusSelects) throws Exception {
        MapList mlReturn = new MapList();
        try {
            if (null != slObjIds && !slObjIds.isEmpty()) {
                String[] oidsArray = slObjIds.toArray(String[]::new);
                mlReturn = DomainObject.getInfo(context, oidsArray, slBusSelects);
            }
        } catch (Exception e) {
            throw e;
        }
        return mlReturn;
    }

    /**
     * @param context
     * @param userPreferenceTemplateID
     * @return
     * @throws Exception
     */
    public String getIRMUserPreferenceTemplateInfoJson(Context context, String userPreferenceTemplateID) throws Exception {
        Instant startTime = Instant.now();
        IRMUPT upt = new IRMUPT(context, userPreferenceTemplateID);
        String jsonString = upt.getAttributeJson();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get IRM User Preference Template Info Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonString;
    }

    public Map<Object, Object> getIRMUserPreferenceTemplateInfoMap(Context context, String userPreferenceTemplateID) throws Exception {
        Instant startTime = Instant.now();
        IRMUPT upt = new IRMUPT(context, userPreferenceTemplateID);
        Map<Object, Object> resultMap = upt.getAttributeMap();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get IRM User Preference Template Info Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return resultMap;
    }

    public String getDSMUserPreferenceTemplateInfoJson(Context context, String userPreferenceTemplateID) throws Exception {
        Instant startTime = Instant.now();
        DSMUPT upt = new DSMUPT(context, userPreferenceTemplateID);
        String jsonString = upt.getAttributeJson();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get DSM User Preference Template Info Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return jsonString;
    }

    public Map<Object, Object> getDSMUserPreferenceTemplateInfoMap(Context context, String userPreferenceTemplateID) throws Exception {
        Instant startTime = Instant.now();
        DSMUPT upt = new DSMUPT(context, userPreferenceTemplateID);
        Map<Object, Object> resultMap = upt.getAttributeMap();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Get DSM User Preference Template Info Json - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return resultMap;
    }
	
	/**
	 * This method returns given attribute value if attribute is multi value then returns all values separated by comma.
	 * @param context
	 * @param mUPTAttrMap : UPT Object Information Map.
	 * @param strUPTAttrName : Attribute Name.
	 * @return StringList : Containing Attribute vales.
	 * @throws Exception 
	 */
	public static StringList getAttributeValueFromMap(Context context, Map<Object, Object> mUPTAttrMap, String strUPTAttrName) throws Exception {
		StringList slUPTAttrValue = new StringList();
		Object objUPTAttrInfo=null;
		logger.log(Level.INFO, "UPT Attribute Name :: " + strUPTAttrName);
		try {
			objUPTAttrInfo = mUPTAttrMap.get(strUPTAttrName);
			if(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_INFORMED_USERS.getName(context).equals(strUPTAttrName)) {
				objUPTAttrInfo = mUPTAttrMap.get(DomainObject.getAttributeSelect(strUPTAttrName));
			}
			if (null != objUPTAttrInfo && UIUtil.isNotNullAndNotEmpty(objUPTAttrInfo.toString())) {
				if (objUPTAttrInfo instanceof StringList) {
					slUPTAttrValue = (StringList) objUPTAttrInfo;
				} else if (objUPTAttrInfo.toString().contains(SelectConstants.cSelectDelimiter)) {
					slUPTAttrValue = StringUtil.splitString(objUPTAttrInfo.toString(), SelectConstants.cSelectDelimiter);
				} else if (objUPTAttrInfo.toString().contains(UserPreferenceTemplateConstants.Basic.SYMB_COMMA.get())) {
					slUPTAttrValue = StringUtil.split(objUPTAttrInfo.toString(), UserPreferenceTemplateConstants.Basic.SYMB_COMMA.get());
				} else {
					slUPTAttrValue.add(objUPTAttrInfo.toString());
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred in method getAttributeValueFromMap() : " + e);
		}
		return slUPTAttrValue;
	}

	/**
	 * This method return attribute 'Part Category' value based on the selected Part Type.
	 * @param  context
	 * @param  String : Part Type.
	 * @return String : Part Category.
	 * @throws Exception if the operation fails.
	 */
	public static String getPartCategory(Context context, String strSelectedType) throws Exception {
		logger.log(Level.INFO, "Get Part Category for Type :: " + strSelectedType);
		Instant startTime = Instant.now();
		IRMUPT upt = new IRMUPT(context, strSelectedType);
		String strPartCategory=DomainConstants.EMPTY_STRING;
		//Get Packaging Types.
		UserPreferenceUtil util = new UserPreferenceUtil();
		Map<String, StringList> packagingTypes = util.getPartTypes(UserPreferenceTemplateConstants.Basic.PACKAGING_TYPES_CONFIGURATION.get());
		StringList packagingSchemaTypeList = packagingTypes.get(DataConstants.CONST_FIELD_CHOICES);
		logger.log(Level.INFO, "packagingSchemaTypeList :: " + packagingSchemaTypeList);

		//Get Product Types.
		Map<String, StringList> productTypes = util.getPartTypes(UserPreferenceTemplateConstants.Basic.PRODUCT_TYPES_CONFIGURATION.get());
		StringList productSchemaTypeList = productTypes.get(DataConstants.CONST_FIELD_CHOICES);
		logger.log(Level.INFO, "productSchemaTypeList :: " + productSchemaTypeList);

		//Get Raw Materials Types.
		Map<String, StringList> rawMaterialTypes = util.getPartTypes(UserPreferenceTemplateConstants.Basic.RAW_MATERIAL_TYPES_CONFIGURATION.get());
		StringList rawMaterialSchemaTypeList = rawMaterialTypes.get(DataConstants.CONST_FIELD_CHOICES);
		logger.log(Level.INFO, "rawMaterialSchemaTypeList :: " + rawMaterialSchemaTypeList);

		//Get Technical Specification Types.
		Map<String, StringList> techSpecTypes = util.getPartTypes(UserPreferenceTemplateConstants.Basic.TECHNICAL_SPECIFICATION_TYPES_CONFIGURATION.get());
		StringList techSpecSchemaTypeList = techSpecTypes.get(DataConstants.CONST_FIELD_CHOICES);
		logger.log(Level.INFO, "techSpecSchemaTypeList :: " + techSpecSchemaTypeList);

		//Get Exploration Types.
		Map<String, StringList> explorationTypes = util.getPartTypes(UserPreferenceTemplateConstants.Basic.EXPLORATION_TYPES_CONFIGURATION.get());
		StringList explorationSchemaTypeList = explorationTypes.get(DataConstants.CONST_FIELD_CHOICES);
		logger.log(Level.INFO, "explorationSchemaTypeList :: " + explorationSchemaTypeList);

		try {
			if(isTypeExistInList(strSelectedType, packagingSchemaTypeList)) {
				strPartCategory=PartCategory.PACKAGING.getName();
			}
			else if(isTypeExistInList(strSelectedType, productSchemaTypeList)) {
				strPartCategory=PartCategory.PRODUCT.getName();
			}
			else if(isTypeExistInList(strSelectedType, rawMaterialSchemaTypeList)) {
				strPartCategory=PartCategory.RAW_MATERIAL.getName();
			}
			else if(isTypeExistInList(strSelectedType, techSpecSchemaTypeList)) {
				strPartCategory=PartCategory.TECH_SPEC.getName();
			}
			else if(isTypeExistInList(strSelectedType, explorationSchemaTypeList)) {
				strPartCategory=PartCategory.EXPLORATION.getName();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred in method getPartCategory() : " + e);
			throw e;
		}
		logger.log(Level.INFO, "Part Category :: " + strPartCategory);
		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		logger.log(Level.INFO, "Method getPartCategory() - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
		return strPartCategory;
	}

	/**
	 * This method return true if selected Part Type exist in the list.
	 * @param  strSelectedType : User Selected Type
	 * @param  slTypeList : Type List.
	 * @return boolean : return true if type exist.
	 * @throws Exception if the operation fails.
	 */
	private static boolean isTypeExistInList(String strSelectedType, StringList slTypeList)throws Exception {
		logger.log(Level.INFO, "Check type "+strSelectedType+" exist in the list :: " + slTypeList);
		boolean isTypeExist=false;
		if(null != slTypeList && !slTypeList.isEmpty()) {
			for (String strType : slTypeList) {
				if (strType.equals(strSelectedType)){
					isTypeExist=true;
					break;
				}
			}
		}
		logger.log(Level.INFO, "Is Type Exist ? :: " + isTypeExist);
		return isTypeExist;
	}
}
