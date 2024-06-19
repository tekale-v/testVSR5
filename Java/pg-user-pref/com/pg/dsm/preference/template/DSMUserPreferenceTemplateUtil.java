package com.pg.dsm.preference.template;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.DSMUPTConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.models.PackagingPreference;
import com.pg.dsm.preference.models.ProductPreference;
import com.pg.dsm.preference.models.RawMaterialPreference;
import com.pg.dsm.preference.template.entity.Member;
import com.pg.dsm.preference.template.entity.Plant;
import com.pg.dsm.preference.template.entity.Template;
import com.pg.dsm.preference.template.entity.TemplateMember;
import com.pg.dsm.preference.template.entity.User;
import com.pg.dsm.preference.template.services.UserPreferenceTemplateCreateDataTransformer;
import com.pg.dsm.preference.template.services.UserPreferenceTemplateCreateStepsExecutor;
import com.pg.dsm.preference.util.UserPreferenceTemplateUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.SelectConstants;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class DSMUserPreferenceTemplateUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String PART_TYPE="pgUPTPartType";
	private static final String PART_CATEGORY="pgUPTPartCategory";

    public static boolean atleastOneKeyHasValue(Map<Object, Object> objectMap) {
        boolean hasValue = Boolean.FALSE;
        for (Object value : objectMap.values()) {
            if (value != null && UIUtil.isNotNullAndNotEmpty(String.valueOf(value))) {
                hasValue = true;
                break;
            }
        }
        return hasValue;
    }

    public static List<Member> getSharingMembers(MapList objectList) {
        List<Member> members = new ArrayList<>();
        for (Object object : objectList) {
            members.add(new Member((Map<Object, Object>) object));
        }
        return members;
    }

    public static List<Plant> getPlants(MapList objectList) {
        List<Plant> plants = new ArrayList<>();
        for (Object object : objectList) {
            plants.add(new Plant((Map<Object, Object>) object));
        }
        return plants;
    }

    public static List<TemplateMember> getTemplateSharingMembers(MapList objectList) {
        List<TemplateMember> members = new ArrayList<>();
        for (Object object : objectList) {
            members.add(new TemplateMember((Map<Object, Object>) object));
        }
        return members;
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public Map<Object, Object> getCreateTemplateDataMap(Context context, String[] args) throws Exception {
        Map<Object, Object> objectMap = new HashMap<>();
        if (args.length > 0) {
            HashMap programMap = (HashMap) JPO.unpackArgs(args); // JSP payload map.
            if (null != programMap && !programMap.isEmpty()) {
                Map<String, String[]> paramMap = (Map<String, String[]>) programMap.get("paramMap"); // JSP payload map.
                logger.log(Level.INFO, ">> paramMap: " + paramMap.keySet());
                if (null != paramMap && !paramMap.isEmpty()) {
                    Map<Object, Object> securityDataMap = getSecurityData(context, paramMap); // security data
                    logger.log(Level.INFO, ">> securityDataMap: " + securityDataMap);

                    Map<Object, Object> changeActionDataMap = getChangeActionData(context, paramMap); // change action data
                    logger.log(Level.INFO, ">> changeActionDataMap: " + changeActionDataMap);

                    Map<Object, Object> templateSpecificDataMap = getTemplateSpecificData(paramMap); // template specific data (like: template name, auto-name etc..)
                    logger.log(Level.INFO, ">> templateSpecificDataMap: " + templateSpecificDataMap);

                    MapList sharingMembersList = getShareWithMembersDataList(context, paramMap);// share with members (list)
                    logger.log(Level.INFO, ">> sharingMembersList: " + sharingMembersList);

                    MapList sharingTemplateMembersList = getShareTemplateWithMembersDataList(context, paramMap);// share template with members (list)
                    logger.log(Level.INFO, ">> sharingTemplateMembersList: " + sharingTemplateMembersList);

                    MapList plantDataList = getPlantData(paramMap); // plant data (list)
                    logger.log(Level.INFO, ">> plantDataList: " + plantDataList);

                    String partCategory = getPartCategory(paramMap);
                    logger.log(Level.INFO, ">> partCategory: " + partCategory);
                    if (UIUtil.isNotNullAndNotEmpty(partCategory)) {
                        if (PreferenceConstants.Basic.PACKAGING.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> packagingDataMap = getPackagingData(context, paramMap);
                            logger.log(Level.INFO, ">> packagingDataMap: " + packagingDataMap);
                            objectMap.put(PreferenceConstants.Basic.PACKAGING_DATA.get(), packagingDataMap); // put packaging data
                        }
                        if (PreferenceConstants.Basic.PRODUCT.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> productDataMap = getProductData(context, paramMap);
                            logger.log(Level.INFO, ">> productDataMap: " + productDataMap);
                            objectMap.put(PreferenceConstants.Basic.PRODUCT_DATA.get(), productDataMap); // put product data
                        }
                        if (PreferenceConstants.Basic.RAW_MATERIAL.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> rawMaterialDataMap = getRawMaterialData(context, paramMap);
                            logger.log(Level.INFO, ">> rawMaterialDataMap: " + rawMaterialDataMap);
                            objectMap.put(PreferenceConstants.Basic.RAW_MATERIAL_DATA.get(), rawMaterialDataMap); // put raw material data
                        }
                        if (PreferenceConstants.Basic.TECHNICAL_SPEC.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> technicalSpecificationDataMap = getTechnicalSpecificationData(context, paramMap);
                            logger.log(Level.INFO, ">> technicalSpecificationDataMap: " + technicalSpecificationDataMap);
                            objectMap.put(PreferenceConstants.Basic.TECHNICAL_SPECIFICATION_DATA.get(), technicalSpecificationDataMap); // put raw material data
                        }

                        if (PreferenceConstants.Basic.Exploration.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> explorationData = getExplorationData(context, paramMap);
                            logger.log(Level.INFO, ">> explorationData: " + explorationData);
                            objectMap.put(PreferenceConstants.Basic.EXPLORATION_DATA.get(), explorationData); // put raw material data
                        }

						//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
                        if (PreferenceConstants.Basic.ManufacturingEquivalent.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> manufacturingEquivalentData = getMEPData(context, paramMap);
                            logger.log(Level.INFO, ">> manufacturingEquivalentData: " + manufacturingEquivalentData);
                            objectMap.put(PreferenceConstants.Basic.MANUFACTURING_EQUIVALENT_DATA.get(), manufacturingEquivalentData); // put raw material data
                        }

                        if (PreferenceConstants.Basic.SupplierEquivalent.get().equalsIgnoreCase(partCategory)) {
                            Map<Object, Object> supplierEquivalentData = getSEPData(context, paramMap);
                            logger.log(Level.INFO, ">> supplierEquivalentData: " + supplierEquivalentData);
                            objectMap.put(PreferenceConstants.Basic.SUPPLIER_EQUIVALENT_DATA.get(), supplierEquivalentData); // put raw material data
                        }
						//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
                        objectMap.put(PreferenceConstants.Basic.SECURITY_DATA.get(), securityDataMap); // put security data
                        objectMap.put(PreferenceConstants.Basic.CHANGE_ACTION_DATA.get(), changeActionDataMap); // put change action data
                        objectMap.put(PreferenceConstants.Basic.TEMPLATE_SPECIFIC_DATA.get(), templateSpecificDataMap); // put template specific data
                        objectMap.put(PreferenceConstants.Basic.SHARING_MEMBERS_DATA.get(), sharingMembersList); // put sharing members data
                        objectMap.put(PreferenceConstants.Basic.SHARING_TEMPLATE_MEMBERS_DATA.get(), sharingTemplateMembersList); // put sharing template members data
                        objectMap.put(PreferenceConstants.Basic.PLANTS_DATA.get(), plantDataList); // put plants data
                    } else {
                        logger.log(Level.WARNING, "Part Category is null or empty");
                    }
                } else {
                    logger.log(Level.WARNING, "Packed Map is null or empty");
                }
            } else {
                logger.log(Level.WARNING, "Program Map is null or empty");
            }
        } else {
            logger.log(Level.WARNING, "Argument is empty");
        }
        logger.log(Level.INFO, ">> Create Template Data Map: " + objectMap);
        return objectMap;
    }

    public void createTemplate(Context context, String[] args) throws Exception {
        if (null != args && args.length > 0) {
            Map<Object, Object> objectMap = getCreateTemplateDataMap(context, args);
            if (null != objectMap && !objectMap.isEmpty()) {
                logger.log(Level.INFO, "Create Template Payload: " + objectMap.keySet());
                UserPreferenceTemplateCreateDataTransformer dataTransformer = new UserPreferenceTemplateCreateDataTransformer.Transform(context).now(objectMap);
                if (dataTransformer.isTransformed()) {
                    logger.log(Level.INFO, "Create Template Payload Data Transformation successfull");
                    User user = dataTransformer.getUser();
                    if (null != user) {
                        Template template = user.getTemplate();
                        if (null != template) {
                            UserPreferenceTemplateCreateStepsExecutor createStepsExecutor = new UserPreferenceTemplateCreateStepsExecutor.Execute(context).now(template);
                            if (createStepsExecutor.isExecuted()) {
                                String objectID = createStepsExecutor.getObjectID();
                                logger.log(Level.INFO, "Newly created UPT ID: " + objectID);
                            } else {
                                logger.log(Level.WARNING, "Template object creation failed");
                            }
                        } else {
                            logger.log(Level.WARNING, "Transformed data template bean has problem");
                        }
                    } else {
                        logger.log(Level.WARNING, "Transformed data user bean has problem");
                    }
                } else {
                    logger.log(Level.WARNING, "Error in transforming create template payload data.");
                }
            } else {
                logger.log(Level.WARNING, "Converted Map from args is empty");
            }
        } else {
            logger.log(Level.WARNING, "No enough arguments for create template");
        }
    }

    public Map<Object, Object> getPackagingData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {

            // part category.
            UserPreferenceTemplateConstants.PackagingFields partCategoryField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.PackagingFields partTypeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);

            // phase
            UserPreferenceTemplateConstants.PackagingFields phaseField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("Phase");
            String phaseFieldName = phaseField.getFieldName();
            objectMap.put(phaseFieldName, (paramMap.containsKey(phaseFieldName)) ? (String) paramMap.get(phaseFieldName)[0] : DomainConstants.EMPTY_STRING);

            // manufacturing Status
            UserPreferenceTemplateConstants.PackagingFields manufacturingStatusField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ManufacturingStatus");
            String manufacturingStatusFieldName = manufacturingStatusField.getFieldName();
            objectMap.put(manufacturingStatusFieldName, (paramMap.containsKey(manufacturingStatusFieldName)) ? (String) paramMap.get(manufacturingStatusFieldName)[0] : DomainConstants.EMPTY_STRING);


            // release Criteria
            UserPreferenceTemplateConstants.PackagingFields releaseCriteriaField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ReleaseCriteria");
            String releaseCriteriaFieldName = releaseCriteriaField.getFieldName();
            objectMap.put(releaseCriteriaFieldName, (paramMap.containsKey(releaseCriteriaFieldName)) ? (String) paramMap.get(releaseCriteriaFieldName)[0] : DomainConstants.EMPTY_STRING);

            // class Name
            UserPreferenceTemplateConstants.PackagingFields classNameField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ClassName");
            String classPhysicalID = classNameField.getFieldPhysicalID();
            objectMap.put(classPhysicalID, (paramMap.containsKey(classPhysicalID)) ? (String) paramMap.get(classPhysicalID)[0] : DomainConstants.EMPTY_STRING);

            // reported Function (get physical id)
            UserPreferenceTemplateConstants.PackagingFields reportedFunctionField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("ReportedFunction");
            String reportedFunctionPhysicalID = reportedFunctionField.getFieldPhysicalID();
            objectMap.put(reportedFunctionPhysicalID, (paramMap.containsKey(reportedFunctionPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(reportedFunctionPhysicalID)[0]) : DomainConstants.EMPTY_STRING);

            // segment (get physical id)
            UserPreferenceTemplateConstants.PackagingFields segmentField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("Segment");
            String segmentPhysicalID = segmentField.getFieldPhysicalID();
            objectMap.put(segmentPhysicalID, (paramMap.containsKey(segmentPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(segmentPhysicalID)[0]) : DomainConstants.EMPTY_STRING);

            // packaging Component Type
            UserPreferenceTemplateConstants.PackagingFields packagingComponentTypeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PackagingComponentType");
            String packagingComponentTypePhysicalID = packagingComponentTypeField.getFieldPhysicalID();
            objectMap.put(packagingComponentTypePhysicalID, (paramMap.containsKey(packagingComponentTypePhysicalID)) ? (String) paramMap.get(packagingComponentTypePhysicalID)[0] : DomainConstants.EMPTY_STRING);

            // packaging Material Type
            UserPreferenceTemplateConstants.PackagingFields packagingMaterialTypeField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("PackagingMaterialType");
            String packagingMaterialTypePhysicalID = packagingMaterialTypeField.getFieldPhysicalID();
            objectMap.put(packagingMaterialTypePhysicalID, (paramMap.containsKey(packagingMaterialTypePhysicalID)) ? (String) paramMap.get(packagingMaterialTypePhysicalID)[0] : DomainConstants.EMPTY_STRING);

            // base Unit Of Measure
            UserPreferenceTemplateConstants.PackagingFields baseUnitOfMeasureField = UserPreferenceTemplateConstants.PackagingFields.getByFieldIdentifier("BaseUnitOfMeasure");
            String baseUnitOfMeasurePhysicalID = baseUnitOfMeasureField.getFieldPhysicalID();
            objectMap.put(baseUnitOfMeasurePhysicalID, (paramMap.containsKey(baseUnitOfMeasurePhysicalID)) ? (String) paramMap.get(baseUnitOfMeasurePhysicalID)[0] : DomainConstants.EMPTY_STRING);
        }
        logger.log(Level.INFO, "Create Template  Packaging objectMap" + objectMap);
        return objectMap;
    }

    public Map<Object, Object> getProductData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {
            // part category.
            UserPreferenceTemplateConstants.ProductFields partCategoryField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.ProductFields partTypeField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);
            // phase
            UserPreferenceTemplateConstants.ProductFields phaseField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("Phase");
            String phaseFieldName = phaseField.getFieldName();
            objectMap.put(phaseFieldName, (paramMap.containsKey(phaseFieldName)) ? (String) paramMap.get(phaseFieldName)[0] : DomainConstants.EMPTY_STRING);
            // manufacturing Status
            UserPreferenceTemplateConstants.ProductFields manufacturingStatusField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ManufacturingStatus");
            String manufacturingStatusFieldName = manufacturingStatusField.getFieldName();
            objectMap.put(manufacturingStatusFieldName, (paramMap.containsKey(manufacturingStatusFieldName)) ? (String) paramMap.get(manufacturingStatusFieldName)[0] : DomainConstants.EMPTY_STRING);

            // release Criteria
            UserPreferenceTemplateConstants.ProductFields releaseCriteriaField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ReleaseCriteria");
            String releaseCriteriaFieldName = releaseCriteriaField.getFieldName();
            objectMap.put(releaseCriteriaFieldName, (paramMap.containsKey(releaseCriteriaFieldName)) ? (String) paramMap.get(releaseCriteriaFieldName)[0] : DomainConstants.EMPTY_STRING);

            // class Name
            UserPreferenceTemplateConstants.ProductFields classNameField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ClassName");
            String classPhysicalID = classNameField.getFieldPhysicalID();
            objectMap.put(classPhysicalID, (paramMap.containsKey(classPhysicalID)) ? (String) paramMap.get(classPhysicalID)[0] : DomainConstants.EMPTY_STRING);

            // reported Function (get physical id)
            UserPreferenceTemplateConstants.ProductFields reportedFunctionField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ReportedFunction");
            String reportedFunctionPhysicalID = reportedFunctionField.getFieldPhysicalID();
            objectMap.put(reportedFunctionPhysicalID, (paramMap.containsKey(reportedFunctionPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(reportedFunctionPhysicalID)[0]) : DomainConstants.EMPTY_STRING);

            // segment (get physical id)
            UserPreferenceTemplateConstants.ProductFields segmentField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("Segment");
            String segmentPhysicalID = segmentField.getFieldPhysicalID();
            objectMap.put(segmentPhysicalID, (paramMap.containsKey(segmentPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(segmentPhysicalID)[0]) : DomainConstants.EMPTY_STRING);

            // business area
            UserPreferenceTemplateConstants.ProductFields businessAreaField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("BusinessArea");
            String businessAreaFieldPhysicalID = businessAreaField.getFieldPhysicalID();
            objectMap.put(businessAreaFieldPhysicalID, (paramMap.containsKey(businessAreaFieldPhysicalID)) ? (String) paramMap.get(businessAreaFieldPhysicalID)[0] : DomainConstants.EMPTY_STRING);// get only one business area.
            logger.log(Level.INFO, "Create Template  PRD objectMap  business area" + objectMap);

            // product Category Platform
            UserPreferenceTemplateConstants.ProductFields productCategoryPlatformField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ProductCategoryPlatform");
            String productCategoryPlatformFieldPhysicalID = productCategoryPlatformField.getFieldPhysicalID();
            objectMap.put(productCategoryPlatformFieldPhysicalID, (paramMap.containsKey(productCategoryPlatformFieldPhysicalID)) ? getPipeSeparatedPhysicalIDFromObjectID(context, String.join(pgV3Constants.SYMBOL_PIPE, (String[]) paramMap.get(productCategoryPlatformFieldPhysicalID))) : DomainConstants.EMPTY_STRING);
            logger.log(Level.INFO, "Create Template  PRD objectMap  product Category Platform" + objectMap);

            // product Compliance
            UserPreferenceTemplateConstants.ProductFields productComplianceField = UserPreferenceTemplateConstants.ProductFields.getByFieldIdentifier("ProductCompliance");
            String productComplianceFieldPhysicalID = productComplianceField.getFieldPhysicalID();
            objectMap.put(productComplianceFieldPhysicalID, (paramMap.containsKey(productComplianceFieldPhysicalID)) ? (String) paramMap.get(productComplianceFieldPhysicalID)[0] : DomainConstants.EMPTY_STRING);
            logger.log(Level.INFO, "Create Template  PRD objectMap  product Compliance" + objectMap);
        }
        logger.log(Level.INFO, "Create Template  Product objectMap with physical Id " + objectMap);
        return objectMap;
    }

    public String getMultiValuefieldsData(Map<String, String[]> paramMap, String key) {
        int length = paramMap.get(key).length;
        StringBuilder stringBuilder = new StringBuilder();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                stringBuilder.append(PreferenceConstants.Basic.SYMBOL_PIPE.get());
                stringBuilder.append(paramMap.get(key)[i].trim());
            }
        }
        return stringBuilder.deleteCharAt(0).toString().trim();
    }

    public Map<Object, Object> getRawMaterialData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {
            // part category.
            UserPreferenceTemplateConstants.RawMaterialFields partCategoryField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.RawMaterialFields partTypeField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);

            // phase
            UserPreferenceTemplateConstants.RawMaterialFields phaseField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("Phase");
            String phaseFieldName = phaseField.getFieldName();
            objectMap.put(phaseFieldName, (paramMap.containsKey(phaseFieldName)) ? (String) paramMap.get(phaseFieldName)[0] : DomainConstants.EMPTY_STRING);

            // manufacturing Status
            UserPreferenceTemplateConstants.RawMaterialFields manufacturingStatusField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ManufacturingStatus");
            String manufacturingStatusFieldName = manufacturingStatusField.getFieldName();
            objectMap.put(manufacturingStatusFieldName, (paramMap.containsKey(manufacturingStatusFieldName)) ? (String) paramMap.get(manufacturingStatusFieldName)[0] : DomainConstants.EMPTY_STRING);

            // release Criteria
            UserPreferenceTemplateConstants.RawMaterialFields releaseCriteriaField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ReleaseCriteria");
            String releaseCriteriaFieldName = releaseCriteriaField.getFieldName();
            objectMap.put(releaseCriteriaFieldName, (paramMap.containsKey(releaseCriteriaFieldName)) ? (String) paramMap.get(releaseCriteriaFieldName)[0] : DomainConstants.EMPTY_STRING);

            // TO-DO: get the physical id for all the below fields which are pick-list objects.

            // class Name
            UserPreferenceTemplateConstants.RawMaterialFields classNameField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ClassName");
            String classPhysicalID = classNameField.getFieldPhysicalID();
            objectMap.put(classPhysicalID, (paramMap.containsKey(classPhysicalID)) ? (String) paramMap.get(classPhysicalID)[0] : DomainConstants.EMPTY_STRING);

            // reported Function (get physical id)
            UserPreferenceTemplateConstants.RawMaterialFields reportedFunctionField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ReportedFunction");
            String reportedFunctionPhysicalID = reportedFunctionField.getFieldPhysicalID();
            objectMap.put(reportedFunctionPhysicalID, (paramMap.containsKey(reportedFunctionPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(reportedFunctionPhysicalID)[0]) : DomainConstants.EMPTY_STRING);

            // segment (get physical id)
            UserPreferenceTemplateConstants.RawMaterialFields segmentField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("Segment");
            String segmentPhysicalID = segmentField.getFieldPhysicalID();
            objectMap.put(segmentPhysicalID, (paramMap.containsKey(segmentPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(segmentPhysicalID)[0]) : DomainConstants.EMPTY_STRING);

            // business area
            UserPreferenceTemplateConstants.RawMaterialFields businessAreaField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("BusinessArea");
            String businessAreaFieldPhysicalID = businessAreaField.getFieldPhysicalID();
            objectMap.put(businessAreaFieldPhysicalID, (paramMap.containsKey(businessAreaFieldPhysicalID)) ? (String) paramMap.get(businessAreaFieldPhysicalID)[0] : DomainConstants.EMPTY_STRING);

            // product Category Platform
            UserPreferenceTemplateConstants.RawMaterialFields productCategoryPlatformField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("ProductCategoryPlatform");
            String productCategoryPlatformFieldPhysicalID = productCategoryPlatformField.getFieldPhysicalID();
            objectMap.put(productCategoryPlatformFieldPhysicalID, (paramMap.containsKey(productCategoryPlatformFieldPhysicalID)) ? getPipeSeparatedPhysicalIDFromObjectID(context, String.join(pgV3Constants.SYMBOL_PIPE, (String[]) paramMap.get(productCategoryPlatformFieldPhysicalID))) : DomainConstants.EMPTY_STRING);

            // material Function * (get physical id)
            UserPreferenceTemplateConstants.RawMaterialFields materialFunctionField = UserPreferenceTemplateConstants.RawMaterialFields.getByFieldIdentifier("MaterialFunction");
            String materialFunctionFieldPhysicalID = materialFunctionField.getFieldPhysicalID();
            objectMap.put(materialFunctionFieldPhysicalID, (paramMap.containsKey(materialFunctionFieldPhysicalID)) ? getPipeSeparatedPhysicalIDFromObjectID(context, String.join(pgV3Constants.SYMBOL_PIPE, (String[]) paramMap.get(materialFunctionFieldPhysicalID))) : DomainConstants.EMPTY_STRING);
        }
        logger.log(Level.INFO, "Create Template Raw Material objectMap with physical Id " + objectMap);
        return objectMap;
    }

    public Map<Object, Object> getTechnicalSpecificationData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {
            // part category.
            UserPreferenceTemplateConstants.TechnicalSpecificationFields partCategoryField = UserPreferenceTemplateConstants.TechnicalSpecificationFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.TechnicalSpecificationFields partTypeField = UserPreferenceTemplateConstants.TechnicalSpecificationFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);

            // segment
            UserPreferenceTemplateConstants.TechnicalSpecificationFields segmentField = UserPreferenceTemplateConstants.TechnicalSpecificationFields.getByFieldIdentifier("Segment");
            String segmentPhysicalID = segmentField.getFieldPhysicalID();
            objectMap.put(segmentPhysicalID, (paramMap.containsKey(segmentPhysicalID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(segmentPhysicalID)[0].trim()) : DomainConstants.EMPTY_STRING);
        }
        logger.log(Level.INFO, "Create Template Technical Specifications objectMap with physical Id " + objectMap);
        return objectMap;
    }


    public Map<Object, Object> getExplorationData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {
            // part category.
            UserPreferenceTemplateConstants.ExplorationFields partCategoryField = UserPreferenceTemplateConstants.ExplorationFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.ExplorationFields partTypeField = UserPreferenceTemplateConstants.ExplorationFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);

        }
        logger.log(Level.INFO, "Create Template Technical Specifications objectMap with physical Id " + objectMap);
        return objectMap;
    }

	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    public Map<Object, Object> getMEPData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {
            // part category.
            UserPreferenceTemplateConstants.MEPFields partCategoryField = UserPreferenceTemplateConstants.MEPFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.MEPFields partTypeField = UserPreferenceTemplateConstants.MEPFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);

            // vendor
            UserPreferenceTemplateConstants.MEPFields vendorField = UserPreferenceTemplateConstants.MEPFields.getByFieldIdentifier("Vendor");
            String vendorFieldOID = vendorField.getFieldOID();
            String vendorFieldPhysicalId = vendorField.getFieldPhysicalID();
            objectMap.put(vendorFieldOID, (paramMap.containsKey(vendorFieldOID)) ? (String) paramMap.get(vendorFieldOID)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(vendorFieldPhysicalId, (paramMap.containsKey(vendorFieldOID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(vendorFieldOID)[0]) : DomainConstants.EMPTY_STRING);
        }
        logger.log(Level.INFO, "Create Template Manufacturing Equivalent objectMap with physical Id " + objectMap);
        return objectMap;
    }


    public Map<Object, Object> getSEPData(Context context, Map<String, String[]> paramMap) throws MatrixException {
        Map<Object, Object> objectMap = new HashMap<>();
        if (!paramMap.isEmpty()) {
            // part category.
            UserPreferenceTemplateConstants.SEPFields partCategoryField = UserPreferenceTemplateConstants.SEPFields.getByFieldIdentifier("PartCategory");
            String partCategoryFieldName = partCategoryField.getFieldName();
            objectMap.put(partCategoryFieldName, (paramMap.containsKey(partCategoryFieldName)) ? (String) paramMap.get(partCategoryFieldName)[0] : DomainConstants.EMPTY_STRING);

            // part type
            UserPreferenceTemplateConstants.SEPFields partTypeField = UserPreferenceTemplateConstants.SEPFields.getByFieldIdentifier("PartType");
            String partTypeFieldName = partTypeField.getFieldName();
            objectMap.put(partTypeFieldName, (paramMap.containsKey(partTypeFieldName)) ? (String) paramMap.get(partTypeFieldName)[0] : DomainConstants.EMPTY_STRING);

            // vendor
            UserPreferenceTemplateConstants.SEPFields vendorField = UserPreferenceTemplateConstants.SEPFields.getByFieldIdentifier("Vendor");
            String vendorFieldOID = vendorField.getFieldOID();
            String vendorFieldPhysicalId = vendorField.getFieldPhysicalID();
            objectMap.put(vendorFieldOID, (paramMap.containsKey(vendorFieldOID)) ? (String) paramMap.get(vendorFieldOID)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(vendorFieldPhysicalId, (paramMap.containsKey(vendorFieldOID)) ? getPhysicalIDFromObjectID(context, (String) paramMap.get(vendorFieldOID)[0]) : DomainConstants.EMPTY_STRING);

        }
        logger.log(Level.INFO, "Create Template Supplier Equivalent objectMap with physical Id " + objectMap);
        return objectMap;
    }
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

    public Map<Object, Object> getTemplateSpecificData(Map<String, String[]> paramMap) {
        Map<Object, Object> objectMap = new HashMap<>();
        logger.log(Level.INFO, "ParamMap Keys:" + paramMap.keySet());
        if (!paramMap.isEmpty()) {
            UserPreferenceTemplateConstants.TemplateFields[] fields = UserPreferenceTemplateConstants.TemplateFields.values();
            String fieldName;
            for (UserPreferenceTemplateConstants.TemplateFields field : fields) {
                fieldName = field.get();
                objectMap.put(fieldName, (paramMap.containsKey(fieldName)) ? (String) paramMap.get(fieldName)[0] : DomainConstants.EMPTY_STRING);
            }
        }
        logger.log(Level.INFO, "objectMap::" + objectMap);
        return objectMap;
    }

    public Map<Object, Object> getSecurityData(Context context, Map<String, String[]> paramMap) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        if (!paramMap.isEmpty()) {
            objectMap.putAll(getSecurityFieldData(context, userPreferenceUtil, paramMap, "PrimaryOrganization"));
            objectMap.putAll(getSecurityFieldData(context, userPreferenceUtil, paramMap, "BusinessUse"));
            objectMap.putAll(getSecurityFieldData(context, userPreferenceUtil, paramMap, "HighlyRestricted"));
        }
        return objectMap;
    }

    private Map<Object, Object> getChangeActionFieldData(Context context, UserPreferenceUtil userPreferenceUtil, Map<String, String[]> paramMap, String fieldIdentifier) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        UserPreferenceTemplateConstants.ChangeActionFields field = UserPreferenceTemplateConstants.ChangeActionFields.getByFieldIdentifier(fieldIdentifier);
        if (null != field) {
            String fieldName = field.getFieldName();
            String fieldDisplayName = field.getFieldDisplayName();
            String fieldOID = field.getFieldOID();
            String fieldPhysicalID = field.getFieldPhysicalID();

            objectMap.put(fieldName, (paramMap.containsKey(fieldName)) ? (String) paramMap.get(fieldName)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(fieldDisplayName, (paramMap.containsKey(fieldDisplayName)) ? (String) paramMap.get(fieldDisplayName)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(fieldOID, (paramMap.containsKey(fieldOID)) ? (String) paramMap.get(fieldOID)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(fieldPhysicalID, (paramMap.containsKey(fieldOID)) ? userPreferenceUtil.getPipeOrCommaSeparatedPhysicalIDFromObjectID(context, (String) paramMap.get(fieldOID)[0]) : DomainConstants.EMPTY_STRING);
        }
        logger.log(Level.INFO, "getChangeActionFieldData objectMap::" + objectMap);
        return objectMap;
    }

    /**
     * Field wherever has (name, displayName, OID)
     *
     * @param context
     * @param userPreferenceUtil
     * @param paramMap
     * @param fieldIdentifier
     * @return
     * @throws FrameworkException
     */
    private Map<Object, Object> getSecurityFieldData(Context context, UserPreferenceUtil userPreferenceUtil, Map<String, String[]> paramMap, String fieldIdentifier) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        UserPreferenceTemplateConstants.SecurityFields field = UserPreferenceTemplateConstants.SecurityFields.getByFieldIdentifier(fieldIdentifier);
        if (null != field) {
            String fieldName = field.getFieldName();
            String fieldDisplayName = field.getFieldDisplayName();
            String fieldOID = field.getFieldOID();
            String fieldPhysicalID = field.getFieldPhysicalID();

            objectMap.put(fieldName, (paramMap.containsKey(fieldName)) ? (String) paramMap.get(fieldName)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(fieldDisplayName, (paramMap.containsKey(fieldDisplayName)) ? (String) paramMap.get(fieldDisplayName)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(fieldOID, (paramMap.containsKey(fieldOID)) ? (String) paramMap.get(fieldOID)[0] : DomainConstants.EMPTY_STRING);
            objectMap.put(fieldPhysicalID, (paramMap.containsKey(fieldOID)) ? userPreferenceUtil.getPipeSeparatedPhysicalIDFromObjectID(context, (String) paramMap.get(fieldOID)[0]) : DomainConstants.EMPTY_STRING);
        }
        return objectMap;
    }

    public Map<Object, Object> getChangeActionData(Context context, Map<String, String[]> paramMap) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        if (!paramMap.isEmpty()) {
            objectMap.putAll(getChangeActionFieldData(context, userPreferenceUtil, paramMap, "InWorkRouteTemplate"));
            objectMap.putAll(getChangeActionFieldData(context, userPreferenceUtil, paramMap, "InApprovalRouteTemplate"));
            objectMap.putAll(getChangeActionFieldData(context, userPreferenceUtil, paramMap, "ChangeTemplate"));
            objectMap.putAll(getChangeActionFieldData(context, userPreferenceUtil, paramMap, "InformedUser"));
        }
        return objectMap;
    }


    private MapList getShareTemplateWithMembersFieldData(Context context, UserPreferenceUtil userPreferenceUtil, Map<String, String[]> paramMap, UserPreferenceTemplateConstants.ShareTemplateWithMembersFields field) throws FrameworkException {
        MapList objectList = new MapList();
        if (null != field) {
            String fieldName = field.getFieldName();
            String fieldOID = field.getFieldOID();
            String fieldPhysicalID = field.getFieldPhysicalID();
            StringList nameList = new StringList();
            if (paramMap.containsKey(fieldName)) {
                nameList = StringUtil.split(paramMap.get(fieldName)[0], "|");
            }

            StringList oIDList = new StringList();
            if (paramMap.containsKey(fieldOID)) {
                oIDList = StringUtil.split(paramMap.get(fieldOID)[0], "|");
            }
            StringList physicalIDList = new StringList();
            if (paramMap.containsKey(fieldOID)) {
                physicalIDList = StringUtil.split(userPreferenceUtil.getPipeSeparatedPhysicalIDFromObjectID(context, paramMap.get(fieldOID)[0]), "|");
            }

            int size = nameList.size();
            Map<String, String> objectMap;
            for (int i = 0; i < size; i++) {
                objectMap = new HashMap<>();
                objectMap.put(DomainConstants.SELECT_NAME, nameList.get(i));
                objectMap.put(DomainConstants.SELECT_ID, oIDList.get(i));
                objectMap.put(PreferenceConstants.Basic.PHYSICAL_ID.get(), physicalIDList.get(i));
                objectList.add(objectMap);
            }
        }
        return objectList;
    }

    private MapList getShareWithMembersFieldData(Context context, UserPreferenceUtil userPreferenceUtil, Map<String, String[]> paramMap, UserPreferenceTemplateConstants.ShareWithMembersFields field) throws FrameworkException {
        MapList objectList = new MapList();
        if (null != field) {
            String fieldName = field.getFieldName();
            String fieldDisplayName = field.getFieldDisplayName();
            String fieldOID = field.getFieldOID();
            String fieldPhysicalID = field.getFieldPhysicalID();
            StringList nameList = new StringList();
            if (paramMap.containsKey(fieldName)) {
                nameList = StringUtil.split(paramMap.get(fieldName)[0], "|");
            }
            // TO-DO: Display Name may not be required. Do cleanup later..
            StringList displayNameList = new StringList();
            if (paramMap.containsKey(fieldDisplayName)) {
                displayNameList = StringUtil.split(paramMap.get(fieldDisplayName)[0], "|");
            }
            StringList oIDList = new StringList();
            if (paramMap.containsKey(fieldOID)) {
                oIDList = StringUtil.split(paramMap.get(fieldOID)[0], "|");
            }
            StringList physicalIDList = new StringList();
            if (paramMap.containsKey(fieldOID)) {
                physicalIDList = StringUtil.split(userPreferenceUtil.getPipeSeparatedPhysicalIDFromObjectID(context, paramMap.get(fieldOID)[0]), "|");
            }

            int size = nameList.size();
            Map<String, String> objectMap;
            for (int i = 0; i < size; i++) {
                objectMap = new HashMap<>();
                objectMap.put(DomainConstants.SELECT_NAME, nameList.get(i));
                objectMap.put(DomainConstants.SELECT_ID, oIDList.get(i));
                objectMap.put(PreferenceConstants.Basic.PHYSICAL_ID.get(), physicalIDList.get(i));
                objectList.add(objectMap);
            }
        }
        return objectList;
    }

    public MapList getShareTemplateWithMembersDataList(Context context, Map<String, String[]> paramMap) throws FrameworkException {
        MapList objectList = new MapList();
        if (!paramMap.isEmpty()) {
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            UserPreferenceTemplateConstants.ShareTemplateWithMembersFields field = UserPreferenceTemplateConstants.ShareTemplateWithMembersFields.getByFieldIdentifier("ShareTemplateWithMembers");
            objectList = getShareTemplateWithMembersFieldData(context, userPreferenceUtil, paramMap, field);
            logger.info("Share Template WithMembers DataList" + objectList);
        }
        return objectList;
    }

    public MapList getShareWithMembersDataList(Context context, Map<String, String[]> paramMap) throws FrameworkException {
        MapList objectList = new MapList();
        if (!paramMap.isEmpty()) {
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            UserPreferenceTemplateConstants.ShareWithMembersFields field = UserPreferenceTemplateConstants.ShareWithMembersFields.getByFieldIdentifier("ShareWithMembers");
            objectList = getShareWithMembersFieldData(context, userPreferenceUtil, paramMap, field);
            logger.info("Share With Members Data List" + objectList);
        }
        return objectList;
    }

    /**
     * @param paramMap
     * @return
     */
    public MapList getPlantData(Map<String, String[]> paramMap) {
        MapList objectList = new MapList();
        int count = getPlantCount(paramMap);
        if (count > 0) {
            Map<Object, Object> objectMap;
            for (int x = 1; x <= count; x++) {
                objectMap = new HashMap<>();
                objectMap.put(DomainConstants.SELECT_NAME, paramMap.get(UserPreferenceTemplateConstants.PlantFields.PLANT.get() + x)[0]);
                objectMap.put(DomainConstants.SELECT_ID, paramMap.get(UserPreferenceTemplateConstants.PlantFields.PLANT_OID.get() + x)[0]);
                objectMap.put(pgV3Constants.ATTRIBUTE_PGISACTIVATED, paramMap.get(UserPreferenceTemplateConstants.PlantFields.ACTIVATED.get() + x)[0]);
                objectMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE, paramMap.get(UserPreferenceTemplateConstants.PlantFields.AUTHORIZED_TO_USE.get() + x)[0]);
                objectMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE, paramMap.get(UserPreferenceTemplateConstants.PlantFields.AUTHORIZED_TO_PRODUCE.get() + x)[0]);
                objectMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOVIEW, paramMap.get(UserPreferenceTemplateConstants.PlantFields.AUTHORIZED.get() + x)[0]);
                objectList.add(objectMap);
                logger.info("UPTUtil: getPlantData:- Plant object List " + objectList);

            }
        }
        return objectList;
    }

    /**
     * @param paramMap
     * @return
     */
    private int getPlantCount(Map<String, String[]> paramMap) {
        int count = 0;
        if (null != paramMap && !paramMap.isEmpty()) {
            if (paramMap.containsKey("plantCountField")) {
                String[] plantCountField = paramMap.get("plantCountField");
                if (null != plantCountField && plantCountField.length > 0) {
                    count = Integer.parseInt(plantCountField[0]);
                }
            }
        }
        return count;
    }

    private String getPartCategory(Map<String, String[]> paramMap) {
        String category = DomainConstants.EMPTY_STRING;
        if (null != paramMap && !paramMap.isEmpty()) {
            if (paramMap.containsKey("partCategory")) {
                String[] partCategories = paramMap.get("partCategory");
                if (null != partCategories && partCategories.length > 0) {
                    category = partCategories[0];
                }
            }
        }
        return category;
    }

    public void connectPlantToDSMTemplate(Context context, Map<String, String[]> paramMap) throws Exception {

        java.util.Set<String> plantMapKeySet = paramMap.keySet();
        // Process the table data
        for (String paramName : paramMap.keySet()) {
            if (paramName.startsWith("plantName") && paramName.matches("plantName\\d+")) {
                String[] paramValues = paramMap.get(paramName);
                // Process the plantName values
                for (String PlantNames : paramValues) {
                    //PlantparamMap.put("plantName", PlantNames);
                }
                // Retrieve the corresponding values for other columns
                String activatedParam = "activated" + paramName.substring(9);
                String[] activatedValues = paramMap.get(activatedParam);
                for (String activated : activatedValues) {
                    //PlantparamMap.put("activated", activated);
                }
                // Process the activated values
                String authorizedToUseParam = "authorizedToUse" + paramName.substring(9);
                String[] authorizedToUseValues = paramMap.get(authorizedToUseParam);
                for (String authorizedToUse : authorizedToUseValues) {
                    //PlantparamMap.put("authorizedToUse", authorizedToUse);
                }
                // Process the authorizedToUse values
                String authorizedToProduceParam = "authorizedToProduce" + paramName.substring(9);
                String[] authorizedToProduceValues = paramMap.get(authorizedToProduceParam);
                for (String authorizedToProduce : authorizedToProduceValues) {
                    //PlantparamMap.put("authorizedToProduce", authorizedToProduce);
                }
                // Process the authorizedToProduce values
                String authorizedParam = "authorized" + paramName.substring(9);
                String[] authorizedValues = paramMap.get(authorizedParam);
                for (String authorized : authorizedValues) {
                    //PlantparamMap.put("authorized", authorized);
                }
            }
        }
    }

    private String getPhysicalIDFromObjectID(Context context, String objectId) throws MatrixException {
        String physicalId = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            DomainObject domainObject = DomainObject.newInstance(context, objectId);
            physicalId = domainObject.getInfo(context, DomainConstants.SELECT_PHYSICAL_ID);
        }
        return physicalId;
    }

    public String getPipeSeparatedPhysicalIDFromObjectID(Context context, String objectId) throws FrameworkException {
        String ids = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            StringList physicalIDList = StringUtil.split(objectId, pgV3Constants.SYMBOL_PIPE);
            MapList objectList = DomainObject.getInfo(context, physicalIDList.toStringArray(), StringList.create(PreferenceConstants.Basic.PHYSICAL_ID.get()));
            List<String> iDList = new ArrayList<>();
            if (null != objectList && !objectList.isEmpty()) {
                Map<Object, Object> objectMap;
                Iterator iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    iDList.add((String) objectMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
                }
            }
            ids = String.join(pgV3Constants.SYMBOL_PIPE, iDList);
        }
        return ids;
    }

    public String getDSMUserPreferenceTemplateInfoJson(Context context, String objectId) throws Exception {
        Map<Object, Object> infoMap = getDSMUserPreferenceTemplateInfoMap(context, objectId);
        String jsonString = convertToJSONString(infoMap);
        return jsonString;
    }

    public Map<Object, Object> getDSMUserPreferenceTemplateInfoMap(Context context, String objectId) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            StringList objectSelects = new StringList();
            objectSelects.add(DomainConstants.SELECT_NAME);
            objectSelects.add(DomainConstants.SELECT_ID);
            objectSelects.add(DomainConstants.SELECT_POLICY);
            objectSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
            DSMUPTConstants.Attributes[] attributes = DSMUPTConstants.Attributes.values();
            for (DSMUPTConstants.Attributes attribute : attributes) {
                objectSelects.add(attribute.getSelect(context));
            }
            DomainObject domainObject = DomainObject.newInstance(context, objectId);
            Map<Object, Object> objectMap = domainObject.getInfo(context, objectSelects);
            resultMap.putAll(getAttributeInfoWithPhysicalID(context, objectMap));
            resultMap.putAll(getAttributeInfoWithoutPhysicalID(context, objectMap));
        }
        logger.log(Level.INFO, "DSM UPT: " + resultMap);
        return resultMap;
    }

    public JsonObject convertToJSON(Map<Object, Object> attributeInfo) {
        JsonObjectBuilder resultMapBuilder = Json.createObjectBuilder();
        for (Map.Entry<Object, Object> entry : attributeInfo.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof List) {
                JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                List<Map<Object, Object>> infoList = (List<Map<Object, Object>>) value;
                for (Map<Object, Object> infoMap : infoList) {
                    JsonObjectBuilder infoMapBuilder = Json.createObjectBuilder();
                    for (Map.Entry<Object, Object> infoEntry : infoMap.entrySet()) {
                        infoMapBuilder.add(infoEntry.getKey().toString(), infoEntry.getValue().toString());
                    }
                    jsonArrayBuilder.add(infoMapBuilder);
                }
                resultMapBuilder.add(key, jsonArrayBuilder);
            }
        }
        return resultMapBuilder.build();
    }

    public String convertToJSONString(Map<Object, Object> resultMap) throws Exception {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        for (Map.Entry<Object, Object> entry : resultMap.entrySet()) {
            if (entry.getValue() instanceof List) {
                JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
                List<Map<Object, Object>> attributeList = (List<Map<Object, Object>>) entry.getValue();
                for (Map<Object, Object> attributeMap : attributeList) {
                    JsonObjectBuilder tempJsonObjectBuilder = Json.createObjectBuilder();
                    for (Map.Entry<Object, Object> attrEntry : attributeMap.entrySet()) {
                        tempJsonObjectBuilder.add(attrEntry.getKey().toString(), attrEntry.getValue().toString());
                    }
                    jsonArrayBuilder.add(tempJsonObjectBuilder);
                }
                jsonObjectBuilder.add(entry.getKey().toString(), jsonArrayBuilder);
            } else {
                jsonObjectBuilder.add(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        JsonObject jsonObject = jsonObjectBuilder.build();
        return jsonObject.toString();
    }

    public Map<Object, Object> getAttributeInfoWithPhysicalID(Context context, Map<Object, Object> objectMap) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        List<String> singlePhysicalIDAttributes = List.of(
                DSMUPTConstants.Attributes.UPT_PRIMARY_ORGANIZATION.getSelect(context),
                DSMUPTConstants.Attributes.UPT_CHANGE_TEMPLATE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_REPORTED_FUNCTION.getSelect(context),
                DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context)
        );
        List<String> multiplePhysicalIDAttributes = List.of(
                DSMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context),
                DSMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context),
                DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_WORK.getSelect(context),
                DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_APPROVAL.getSelect(context),
                DSMUPTConstants.Attributes.UPT_MATERIAL_FUNCTION.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PRODUCT_CATEGORY_PLATFORM.getSelect(context),
                DSMUPTConstants.Attributes.UPT_INFORMED_USERS.getSelect(context),
                DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context)
        );
        Map<String, StringList> attributeToListMap = new HashMap<>();
        for (String attribute : singlePhysicalIDAttributes) {
            StringList idList = objectMap.containsKey(attribute) ? getStringListFromMap(objectMap, attribute) : new StringList();
            attributeToListMap.put(attribute, idList);
        }
        for (String attribute : multiplePhysicalIDAttributes) {
            StringList idList = objectMap.containsKey(attribute) ? getStringListFromMap(objectMap, attribute) : new StringList();
            attributeToListMap.put(attribute, idList);
        }
        StringList physicalIDList = new StringList();
        for (StringList idList : attributeToListMap.values()) {
            physicalIDList.addAll(idList);
        }
        physicalIDList.removeIf(String::isEmpty);
        List<String> uniquePhysicalIDs = physicalIDList.stream().distinct().collect(Collectors.toList());
        StringList selectList = StringList.create(
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_PHYSICAL_ID
        );
        MapList infoList = DomainObject.getInfo(context, uniquePhysicalIDs.toArray(new String[0]), selectList);
        if (infoList != null && !infoList.isEmpty()) {
            Map<String, List<Map<Object, Object>>> attributeInfoMap = new HashMap<>();
            for (String attribute : singlePhysicalIDAttributes) {
                attributeInfoMap.put(attribute, new ArrayList<>());
            }
            for (String attribute : multiplePhysicalIDAttributes) {
                attributeInfoMap.put(attribute, new ArrayList<>());
            }
            for (Object info : infoList) {
                Map<Object, Object> tempMap = (Map<Object, Object>) info;
                String tempPhysicalId = (String) tempMap.get(DomainConstants.SELECT_PHYSICAL_ID);
                String tempObjectId = (String) tempMap.get(DomainConstants.SELECT_ID);
                for (String attribute : attributeToListMap.keySet()) {
                    StringList idList = attributeToListMap.get(attribute);
                    if (idList.contains(tempPhysicalId) || idList.contains(tempObjectId)) {
                        attributeInfoMap.get(attribute).add(tempMap);
                    }
                }
            }
            for (String attribute : singlePhysicalIDAttributes) {
                resultMap.put(attribute, attributeInfoMap.get(attribute));
            }
            for (String attribute : multiplePhysicalIDAttributes) {
                resultMap.put(attribute, attributeInfoMap.get(attribute));
            }
        }
        return resultMap;
    }

    public Map<Object, Object> getAttributeInfoWithoutPhysicalID(Context context, Map<Object, Object> objectMap) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        String[] attributes = {
                DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context),
                DSMUPTConstants.Attributes.UPT_RELEASE_CRITERIA_REQUIRED.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PACKAGING_COMPONENT_TYPE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PACKAGING_MATERIAL_TYPE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_BASE_UNIT_OF_MEASURE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_CLASS.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PRODUCT_COMPLIANCE_REQUIRED.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context),
                DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context),
                DSMUPTConstants.Attributes.UPT_MIGRATED.getSelect(context)
        };
        for (String attribute : attributes) {
            resultMap.put(attribute, objectMap.getOrDefault(attribute, DomainConstants.EMPTY_STRING));
        }
        // basic info
        String[] basicInfoAttributes = {
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_POLICY,
                DomainConstants.SELECT_PHYSICAL_ID
        };
        for (String attribute : basicInfoAttributes) {
            resultMap.put(attribute, objectMap.getOrDefault(attribute, DomainConstants.EMPTY_STRING));
        }
        return resultMap;
    }

    private Object getOrDefault(Map<Object, Object> objectMap, String key) {
        return objectMap.getOrDefault(key, DomainConstants.EMPTY_STRING);
    }


    public Map<Object, Object> getAttributeInfoWhichHoldsPhysicalID(Context context, Map<Object, Object> objectMap) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();

        // following attribute hold (single) physical id - start
        String primaryOrganization = DSMUPTConstants.Attributes.UPT_PRIMARY_ORGANIZATION.getSelect(context);
        String changeTemplate = DSMUPTConstants.Attributes.UPT_CHANGE_TEMPLATE.getSelect(context);
        String reportedFunction = DSMUPTConstants.Attributes.UPT_REPORTED_FUNCTION.getSelect(context);
        String segment = DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context);
        // following attribute hold (single) physical id - end

        // following attribute hold (multiple) physical id - start
        String businessUse = DSMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context);
        String highlyRestricted = DSMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context);
        String businessArea = DSMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context);
        String inWorkRouteTemplate = DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_WORK.getSelect(context);
        String inApprovalRouteTemplate = DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_APPROVAL.getSelect(context);
        String materialFunction = DSMUPTConstants.Attributes.UPT_MATERIAL_FUNCTION.getSelect(context);
        String productPlatform = DSMUPTConstants.Attributes.UPT_PRODUCT_CATEGORY_PLATFORM.getSelect(context);
        String informedUser = DSMUPTConstants.Attributes.UPT_INFORMED_USERS.getSelect(context);
        String shareWithMember = DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context);
        // following attribute hold (multiple) physical id - end

        StringList primaryOrganizationList = (objectMap.containsKey(primaryOrganization)) ? getStringListFromMap(objectMap, primaryOrganization) : new StringList(); // primary organization (single)
        StringList changeTemplateList = (objectMap.containsKey(changeTemplate)) ? getStringListFromMap(objectMap, changeTemplate) : new StringList(); // change template (single)
        StringList reportedFunctionList = (objectMap.containsKey(reportedFunction)) ? getStringListFromMap(objectMap, reportedFunction) : new StringList(); // reported function (single)
        StringList segmentList = (objectMap.containsKey(segment)) ? getStringListFromMap(objectMap, segment) : new StringList(); // segment (single)

        StringList businessUseList = (objectMap.containsKey(businessUse)) ? getStringListFromMap(objectMap, businessUse) : new StringList(); // business use (multiple)
        StringList highlyRestrictedList = (objectMap.containsKey(highlyRestricted)) ? getStringListFromMap(objectMap, highlyRestricted) : new StringList(); // highly restricted (multiple)
        StringList businessAreaList = (objectMap.containsKey(businessArea)) ? getStringListFromMap(objectMap, businessArea) : new StringList(); // business area (multiple)
        StringList inWorkRouteTemplateList = (objectMap.containsKey(inWorkRouteTemplate)) ? getStringListFromMap(objectMap, inWorkRouteTemplate) : new StringList(); // in work route template (multiple)
        StringList inApprovalRouteTemplateList = (objectMap.containsKey(inApprovalRouteTemplate)) ? getStringListFromMap(objectMap, inApprovalRouteTemplate) : new StringList(); // in approval route template (multiple)
        StringList materialFunctionList = (objectMap.containsKey(materialFunction)) ? getStringListFromMap(objectMap, materialFunction) : new StringList(); // material function (multiple)
        StringList productPlatformList = (objectMap.containsKey(productPlatform)) ? getStringListFromMap(objectMap, productPlatform) : new StringList(); // product category platform (multiple)
        StringList informedUserList = (objectMap.containsKey(informedUser)) ? getStringListFromMap(objectMap, informedUser) : new StringList(); // informed users (multiple - object id)
        StringList shareWithMemberList = (objectMap.containsKey(shareWithMember)) ? getStringListFromMap(objectMap, shareWithMember) : new StringList(); // share with members (multiple - physical id)

        StringList physicalIDList = new StringList();
        physicalIDList.addAll(businessUseList);
        physicalIDList.addAll(highlyRestrictedList);
        physicalIDList.addAll(businessAreaList);
        physicalIDList.addAll(inWorkRouteTemplateList);
        physicalIDList.addAll(inApprovalRouteTemplateList);
        physicalIDList.addAll(materialFunctionList);
        physicalIDList.addAll(productPlatformList);
        physicalIDList.addAll(informedUserList);
        physicalIDList.addAll(reportedFunctionList);
        physicalIDList.addAll(segmentList);
        physicalIDList.addAll(primaryOrganizationList);
        physicalIDList.addAll(changeTemplateList);

        physicalIDList.removeIf(el -> el.isEmpty()); // remove empty values
        List<String> uniqueList = physicalIDList.stream().distinct().collect(Collectors.toList()); // remove duplicates.

        List<Map<Object, Object>> businessUseInfoList = new ArrayList<>();
        List<Map<Object, Object>> highlyRestrictedInfoList = new ArrayList<>();
        List<Map<Object, Object>> businessAreaInfoList = new ArrayList<>();
        List<Map<Object, Object>> inWorkRouteTemplateInfoList = new ArrayList<>();
        List<Map<Object, Object>> inApprovalRouteTemplateInfoList = new ArrayList<>();
        List<Map<Object, Object>> materialFunctionInfoList = new ArrayList<>();
        List<Map<Object, Object>> productPlatformInfoList = new ArrayList<>();
        List<Map<Object, Object>> informedUserInfoList = new ArrayList<>();

        List<Map<Object, Object>> reportedFunctionInfoList = new ArrayList<>();
        List<Map<Object, Object>> segmentInfoList = new ArrayList<>();
        List<Map<Object, Object>> primaryOrganizationInfoList = new ArrayList<>();
        List<Map<Object, Object>> changeTemplateInfoList = new ArrayList<>();

        StringList selectList = StringList.create(DomainConstants.SELECT_TYPE, DomainConstants.SELECT_NAME, DomainConstants.SELECT_ID, DomainConstants.SELECT_PHYSICAL_ID);
        MapList infoList = DomainObject.getInfo(context, uniqueList.toArray(new String[uniqueList.size()]), selectList);
        if (null != infoList && !infoList.isEmpty()) {
            Iterator iterator = infoList.iterator();
            Map<Object, Object> tempMap;
            String tempPhysicalId;
            String tempObjectId;
            while (iterator.hasNext()) {
                tempMap = (Map<Object, Object>) iterator.next();
                tempPhysicalId = (String) tempMap.get(DomainConstants.SELECT_PHYSICAL_ID);
                tempObjectId = (String) tempMap.get(DomainConstants.SELECT_ID);
                if (businessUseList.contains(tempPhysicalId) || businessUseList.contains(tempObjectId)) {
                    businessUseInfoList.add(tempMap);
                }
                if (highlyRestrictedList.contains(tempPhysicalId) || highlyRestrictedList.contains(tempObjectId)) {
                    highlyRestrictedInfoList.add(tempMap);
                }
                if (businessAreaList.contains(tempPhysicalId) || businessAreaList.contains(tempObjectId)) {
                    businessAreaInfoList.add(tempMap);
                }
                if (inWorkRouteTemplateList.contains(tempPhysicalId) || inWorkRouteTemplateList.contains(tempObjectId)) {
                    inWorkRouteTemplateInfoList.add(tempMap);
                }
                if (inApprovalRouteTemplateList.contains(tempPhysicalId) || inApprovalRouteTemplateList.contains(tempObjectId)) {
                    inApprovalRouteTemplateInfoList.add(tempMap);
                }
                if (materialFunctionList.contains(tempPhysicalId) || materialFunctionList.contains(tempObjectId)) {
                    materialFunctionInfoList.add(tempMap);
                }
                if (productPlatform.contains(tempPhysicalId) || productPlatform.contains(tempObjectId)) {
                    productPlatformInfoList.add(tempMap);
                }
                if (informedUserList.contains(tempPhysicalId) || informedUserList.contains(tempObjectId)) {
                    informedUserInfoList.add(tempMap);
                }
                if (reportedFunctionList.contains(tempPhysicalId) || reportedFunctionList.contains(tempObjectId)) {
                    reportedFunctionInfoList.add(tempMap);
                }
                if (segmentList.contains(tempPhysicalId) || segmentList.contains(tempObjectId)) {
                    segmentInfoList.add(tempMap);
                }
                if (primaryOrganizationList.contains(tempPhysicalId) || primaryOrganizationList.contains(tempObjectId)) {
                    primaryOrganizationInfoList.add(tempMap);
                }
                if (changeTemplateList.contains(tempPhysicalId) || changeTemplateList.contains(tempObjectId)) {
                    changeTemplateInfoList.add(tempMap);
                }
            }
        }
        resultMap.put(UserPreferenceTemplateConstants.Basic.REPORTED_FUNCTION.get(), reportedFunctionInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.SEGMENT.get(), segmentInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.PRIMARY_ORGANIZATION.get(), primaryOrganizationInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.CHANGE_TEMPLATE.get(), changeTemplateInfoList);

        resultMap.put(UserPreferenceTemplateConstants.Basic.BUSINESS_USE_LIST.get(), businessUseInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.HIGHLY_RESTRICTED_LIST.get(), highlyRestrictedInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.BUSINESS_AREA_LIST.get(), businessAreaInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.IN_WORK_ROUTE_TEMPLATE_LIST.get(), inWorkRouteTemplateInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.IN_APPROVAL_ROUTE_TEMPLATE_LIST.get(), inApprovalRouteTemplateInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.MATERIAL_FUNCTION_LIST.get(), materialFunctionInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.PRODUCT_CATEGORY_PLATFORM_LIST.get(), productPlatformInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.INFORMED_USER_LIST.get(), informedUserInfoList);
        resultMap.put(UserPreferenceTemplateConstants.Basic.SHARE_WITH_MEMBER_LIST.get(), DomainObject.getInfo(context, shareWithMemberList.toStringArray(), selectList));

        return resultMap;
    }


    public Map<Object, Object> getAttributeInfoWhichHoldsName(Context context, Map<Object, Object> objectMap, boolean e) throws Exception {
        Map<Object, Object> resultMap = new HashMap<>();
        String releasePhase = DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context);
        String mfgStatus = DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context);
        String releaseCriteriaRequired = DSMUPTConstants.Attributes.UPT_RELEASE_CRITERIA_REQUIRED.getSelect(context);
        String packagingComponentType = DSMUPTConstants.Attributes.UPT_PACKAGING_COMPONENT_TYPE.getSelect(context);
        String packagingMaterialType = DSMUPTConstants.Attributes.UPT_PACKAGING_MATERIAL_TYPE.getSelect(context);
        String unitOfMeasure = DSMUPTConstants.Attributes.UPT_BASE_UNIT_OF_MEASURE.getSelect(context);
        String classType = DSMUPTConstants.Attributes.UPT_CLASS.getSelect(context);
        String productComplianceRequired = DSMUPTConstants.Attributes.UPT_PRODUCT_COMPLIANCE_REQUIRED.getSelect(context);
        String partType = DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context);
        String partCategory = DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context);
        String migrated = DSMUPTConstants.Attributes.UPT_MIGRATED.getSelect(context);

        resultMap.put(releasePhase, objectMap.containsKey(releasePhase) ? objectMap.get(releasePhase) : DomainConstants.EMPTY_STRING);
        resultMap.put(mfgStatus, objectMap.containsKey(mfgStatus) ? objectMap.get(mfgStatus) : DomainConstants.EMPTY_STRING);
        resultMap.put(releaseCriteriaRequired, objectMap.containsKey(releaseCriteriaRequired) ? objectMap.get(releaseCriteriaRequired) : DomainConstants.EMPTY_STRING);
        resultMap.put(packagingComponentType, objectMap.containsKey(packagingComponentType) ? objectMap.get(packagingComponentType) : DomainConstants.EMPTY_STRING);
        resultMap.put(packagingMaterialType, objectMap.containsKey(packagingMaterialType) ? objectMap.get(packagingMaterialType) : DomainConstants.EMPTY_STRING);
        resultMap.put(unitOfMeasure, objectMap.containsKey(unitOfMeasure) ? objectMap.get(unitOfMeasure) : DomainConstants.EMPTY_STRING);
        resultMap.put(classType, objectMap.containsKey(classType) ? objectMap.get(classType) : DomainConstants.EMPTY_STRING);
        resultMap.put(productComplianceRequired, objectMap.containsKey(productComplianceRequired) ? objectMap.get(productComplianceRequired) : DomainConstants.EMPTY_STRING);
        resultMap.put(partType, objectMap.containsKey(partType) ? objectMap.get(partType) : DomainConstants.EMPTY_STRING);
        resultMap.put(partCategory, objectMap.containsKey(partCategory) ? objectMap.get(partCategory) : DomainConstants.EMPTY_STRING);
        resultMap.put(migrated, objectMap.containsKey(migrated) ? objectMap.get(migrated) : DomainConstants.EMPTY_STRING);

        // basic info
        resultMap.put(DomainConstants.SELECT_NAME, objectMap.containsKey(DomainConstants.SELECT_NAME) ? objectMap.get(DomainConstants.SELECT_NAME) : DomainConstants.EMPTY_STRING);
        resultMap.put(DomainConstants.SELECT_ID, objectMap.containsKey(DomainConstants.SELECT_ID) ? objectMap.get(DomainConstants.SELECT_ID) : DomainConstants.EMPTY_STRING);
        resultMap.put(DomainConstants.SELECT_POLICY, objectMap.containsKey(DomainConstants.SELECT_POLICY) ? objectMap.get(DomainConstants.SELECT_POLICY) : DomainConstants.EMPTY_STRING);
        resultMap.put(DomainConstants.SELECT_PHYSICAL_ID, objectMap.containsKey(DomainConstants.SELECT_PHYSICAL_ID) ? objectMap.get(DomainConstants.SELECT_PHYSICAL_ID) : DomainConstants.EMPTY_STRING);

        return resultMap;
    }


    public StringList getStringListFromMap(Map<Object, Object> objectMap, String select) throws Exception {
        StringList objectList = new StringList();
        Object result = objectMap.get(select);
        if (null != result) {
            if (result instanceof StringList) {
                objectList = (StringList) result;
            } else if (result.toString().contains(SelectConstants.cSelectDelimiter)) {
                objectList = StringUtil.splitString(result.toString(), SelectConstants.cSelectDelimiter);
            } else if (result.toString().contains(pgV3Constants.SYMBOL_COMMA)) {
                objectList = StringUtil.split(result.toString(), pgV3Constants.SYMBOL_COMMA);
            } else {
                objectList.add(result.toString());
            }
        }
        return objectList;
    }

    /**
     * This method returns all the attributes of User Preference Templates as Map.
     *
     * @param context
     * @param uptPhyID : User Preference Template Id.
     * @return Map : Containing all the attributes of UPT.
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Map<Object, Object> getUPTAttrValuesMap(Context context, String uptPhyID) throws Exception {
        Map<Object, Object> mUPTAttrValues = new HashMap<>();
        try {
            logger.log(Level.INFO, "UPT ID: " + uptPhyID);
            if (UIUtil.isNotNullAndNotEmpty(uptPhyID)) {
                DomainObject domUPT = DomainObject.newInstance(context, uptPhyID);
                mUPTAttrValues = domUPT.getAttributeMap(context, true);
            } else {
                logger.log(Level.WARNING, "UPT ID is empty");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        logger.log(Level.INFO, "UPT Attribute Map : " + mUPTAttrValues);
        return mUPTAttrValues;
    }

    /**
     * This method returns given attribute value if attribute is multi value then returns all values separated by comma.
     *
     * @param mUPTAttrMap    : UPT Object Information Map.
     * @param strUPTAttrName : Attribute Name.
     * @return StringList : Containing Attribute vales.
     * @throws Exception
     */
    public StringList getAttributeValueFromMap(Map<Object, Object> mUPTAttrMap, String strUPTAttrName) throws Exception {
        StringList slUPTAttrValue = new StringList();
        Object objUPTAttrInfo = null;
        logger.log(Level.INFO, "UPT Attribute Name : " + mUPTAttrMap);
        logger.log(Level.INFO, "UPT Attribute Map : " + strUPTAttrName);
        try {
            objUPTAttrInfo = mUPTAttrMap.get(strUPTAttrName);
            if (null != objUPTAttrInfo) {
                if (objUPTAttrInfo instanceof StringList) {
                    slUPTAttrValue = (StringList) objUPTAttrInfo;
                } else if (objUPTAttrInfo.toString().contains(SelectConstants.cSelectDelimiter)) {
                    slUPTAttrValue = StringUtil.splitString(objUPTAttrInfo.toString(), SelectConstants.cSelectDelimiter);
                } else if (objUPTAttrInfo.toString().contains(pgV3Constants.SYMBOL_COMMA)) {
                    slUPTAttrValue = StringUtil.split(objUPTAttrInfo.toString(), pgV3Constants.SYMBOL_COMMA);
                } else {
                    slUPTAttrValue.add(objUPTAttrInfo.toString());
                }
            }
            logger.log(Level.INFO, "UPT Attribute Value : " + slUPTAttrValue);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return slUPTAttrValue;
    }

    /**
     * @param context
     * @param sToObjectId   : To Object.
     * @param sFromObjectId : From object to which reference document will connect.
     * @return boolean : status.
     * @throws Exception if the operation fails.
     * @Generic method to propagate SOV from source object to target object.
     * @author JPL4
     */
    @SuppressWarnings("unchecked")
    public boolean propogateSOVOnObject(Context context, String sToObjectId, String sFromObjectId) throws Exception {
        logger.log(Level.INFO, "sToObjectId : " + sToObjectId);
        logger.log(Level.INFO, "sFromObjectId : " + sFromObjectId);
        final String OWNERSHIP_ACCESS_READER = "Reader";
        boolean iRet = true;
        if (UIUtil.isNotNullAndNotEmpty(sFromObjectId)) {
            Map<String, String> mSOVToApply = new HashMap<String, String>();
            DomainObject domFrom = DomainObject.newInstance(context, sFromObjectId);
            Map<Object, Object> mapFromObjSOV = domFrom.getInfo(context, new StringList(DomainConstants.SELECT_OWNERSHIP));
            logger.log(Level.INFO, "mapFromObjSOV : " + mapFromObjSOV);
            try {
                if (null != mapFromObjSOV && mapFromObjSOV.size() > 0) {
                    StringList slFromObjSOVList = (StringList) mapFromObjSOV.get(DomainConstants.SELECT_OWNERSHIP);
                    logger.log(Level.INFO, "slFromObjSOVList : " + slFromObjSOVList);
                    if (null != slFromObjSOVList && slFromObjSOVList.size() > 0) {
                        for (String sFromObjSOVValue : slFromObjSOVList) {
                            StringList slFromObjSOV = FrameworkUtil.split(sFromObjSOVValue, pgV3Constants.SYMBOL_PIPE);
                            if (null != slFromObjSOV && slFromObjSOV.size() > 2) {
                                mSOVToApply.put(sFromObjSOVValue, OWNERSHIP_ACCESS_READER);
                            }
                        }
                        logger.log(Level.INFO, "mSOVToApply : " + mSOVToApply);
                        updateObjectOwnership(context, sToObjectId, mSOVToApply, true, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                    }
                }
            } catch (Exception e) {
                iRet = false;
                logger.log(Level.WARNING, "Exception in Method propogateSOVFromObject():: " + e);
                throw e;
            }
        }
        logger.log(Level.INFO, "iRet : " + iRet);
        return iRet;
    }

    /**
     * Generic method to get all Pick List object connected to Part.
     *
     * @param context
     * @param strPartID : Part ID.
     * @return MapList : Containing all PickList object details.
     */
    private MapList getConnectedPickListObjects(Context context, String strPartID) throws Exception {
        logger.log(Level.INFO, "strPartID : " + strPartID);
        MapList mlPickListObjList = new MapList();
        if (UIUtil.isNotNullAndNotEmpty(strPartID)) {
            //Add Type Pattern.
            Pattern pTypePattern = new Pattern(PreferenceConstants.Type.TYPE_PICKLIST_PLATFORM.getName(context));
            pTypePattern.addPattern(PreferenceConstants.Type.TYPE_PICKLIST_REPORTED_FUNCTION.getName(context));
            pTypePattern.addPattern(PreferenceConstants.Type.TYPE_PICKLIST_MATERIAL_FUNCTION_GLOBAL.getName(context));
            pTypePattern.addPattern(PreferenceConstants.Type.TYPE_PICKLIST_SEGMENT.getName(context));
            pTypePattern.addPattern(PreferenceConstants.Type.TYPE_PICKLIST_BUSINESS_AREA.getName(context));
            logger.log(Level.INFO, "pTypePattern : " + pTypePattern.getPattern());
            //Add Relationship Pattern.
            Pattern pRelPattern = new Pattern(PreferenceConstants.Relationship.RELATIONSHIP_PICKLIST_DOCUMENT_TO_BUSINESSAREA.getName(context));
            pRelPattern.addPattern(PreferenceConstants.Relationship.RELATIONSHIP_PICKLIST_TEMPLATES_TO_SEGMENT.getName(context));
            pRelPattern.addPattern(PreferenceConstants.Relationship.RELATIONSHIP_PICKLIST_MATERIAL_FUNCTIONALITY.getName(context));
            pRelPattern.addPattern(PreferenceConstants.Relationship.RELATIONSHIP_PICKLIST_DOCUMENT_TO_PLATFORM.getName(context));
            pRelPattern.addPattern(PreferenceConstants.Relationship.RELATIONSHIP_PICKLIST_REPORTED_FUNCTION.getName(context));
            logger.log(Level.INFO, "pRelPattern : " + pRelPattern.getPattern());
            //Create part domain object.
            DomainObject domPart = DomainObject.newInstance(context, strPartID);
            try {
                //Get part connected Pick List objects.
                mlPickListObjList = domPart.getRelatedObjects(context,    //Context
                        pRelPattern.getPattern(),                        // relationship pattern
                        pTypePattern.getPattern(),                        // type pattern
                        new StringList(DomainObject.SELECT_ID),        // Object selects
                        new StringList(DomainRelationship.SELECT_ID),    // relationship selects
                        false,                                            // from
                        true,                                            // to
                        (short) 1,                                        // expand level
                        null,                                            // object where
                        null,                                            // relationship where
                        0);                                            // limit

                logger.log(Level.INFO, "mlPickListObjList : " + mlPickListObjList);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception in Method getPickListObjects():: " + e);
                throw e;
            }
        }
        return mlPickListObjList;
    }


    /**
     * Update the SOVs (Secondary Ownership Vectors) of the given object.
     * All SOVs that are not in the given Map are deleted.
     * The others are updated if necessary.
     *
     * @param context
     * @param sObjectId          Object on which to update the SOVs
     * @param mSOV2Apply         Map of <"Organization|Project|Comment", "Access">
     * @param isMapLogicalAccess true if Access in the Map is the LogicalAccess, and false if it is the PhysicalAccess
     * @param sComment           The comment to check when deleting SOVs.
     *                           If null then no check is done and ALL SOV not in mSOV2Apply are deleted.
     *                           If not null then only SOV with this comment and not in mSOV2Apply are deleted.
     * @throws Exception that can appeared in the method. They have to be manage by the caller.
     */
    public void updateObjectOwnership(Context context, String sObjectId, Map<String, String> mSOV2Apply, boolean isMapLogicalAccess, String sComment) throws Exception {
        if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
            DomainObject domObject = DomainObject.newInstance(context, sObjectId);

            @SuppressWarnings("unchecked")
            Map<Object, Object> mObjectSOV = domObject.getInfo(context, new StringList(DomainConstants.SELECT_OWNERSHIP));
            StringList slObjectSOV = (StringList) mObjectSOV.get(DomainConstants.SELECT_OWNERSHIP);
            logger.log(Level.INFO, "slObjectSOV : " + slObjectSOV);
            Map<String, String> mCommonSOV = new HashMap<String, String>();

            // Delete SOVs not in Apply list
            for (String sActualSOV : slObjectSOV) {
                // Get Access to allow comparison
                StringList slActualSOV = FrameworkUtil.split(sActualSOV, pgV3Constants.SYMBOL_PIPE);
                if (null != slActualSOV && slActualSOV.size() > 2) {
                    if (!mSOV2Apply.containsKey(sActualSOV)) {
                        if (UIUtil.isNullOrEmpty(sComment)
                                || (UIUtil.isNotNullAndNotEmpty(sComment) && sComment.equals(slActualSOV.get(2)))) {
                            try {
                                // This SOV doesn't exist in the new list of SOVs, so delete it
                                HashMap<String, Object> hmParam = new HashMap<String, Object>();
                                hmParam.put("objectId", sObjectId);
                                hmParam.put("actualSOV", slActualSOV);
                                deleteSOV(context, hmParam);
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "Exception:"+e);
                            }
                        }
                    } else {
                        // Create a Map with common ownership and Access
                        String sAccess = DomainAccess.getObjectOwnershipAccess(context,
                                sObjectId,
                                slActualSOV.get(0), // Organization
                                slActualSOV.get(1), // Project
                                slActualSOV.get(2)); // Comment
                        if (isMapLogicalAccess) {
                            sAccess = DomainAccess.getLogicalName(context, sObjectId, sAccess);
                        }

                        mCommonSOV.put(sActualSOV, sAccess);
                    }
                }
            }

            // Add SOVs coming from Apply list
            for (String sNewSOV : mSOV2Apply.keySet()) {
                String sNewAccess = mSOV2Apply.get(sNewSOV);
                if (null != sNewAccess) {
                    //StringList slNewSOV = FrameworkUtil.split(sNewSOV, CDVConstants.SYMB_PIPE);
                    try {
                        HashMap<String, Object> hmParam = new HashMap<String, Object>();
                        hmParam.put("commonSOV", mCommonSOV);
                        hmParam.put("newSOV", sNewSOV);
                        hmParam.put("newAccess", sNewAccess);
                        hmParam.put("objectId", sObjectId);
                        addSOV(context, hmParam);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Exception in Method updateObjectOwnership():: " + e);
                        throw e;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void addSOV(Context context, HashMap<String, Object> paramMap) throws Exception {
        HashMap<String, String> mCommonSOV = (HashMap<String, String>) paramMap.get("commonSOV");
        String sNewSOV = (String) paramMap.get("newSOV");
        String sNewAccess = (String) paramMap.get("newAccess");
        String sObjectId = (String) paramMap.get("objectId");
        logger.log(Level.INFO, "mCommonSOV : " + mCommonSOV);
        logger.log(Level.INFO, "sNewSOV : " + sNewSOV);
        logger.log(Level.INFO, "sNewAccess : " + sNewAccess);
        logger.log(Level.INFO, "sObjectId : " + sObjectId);
        try {
            StringList slNewSOV = FrameworkUtil.split(sNewSOV, pgV3Constants.SYMBOL_PIPE);
            logger.log(Level.INFO, "slNewSOV : " + slNewSOV);
            MqlUtil.mqlCommand(context, "history off", true, new String[0]);
            if (null != mCommonSOV && mCommonSOV.containsKey(sNewSOV)) {
                // SOV already exist
                String sActualAccess = mCommonSOV.get(sNewSOV);
                logger.log(Level.INFO, "sActualAccess : " + sActualAccess);
                if (!sNewAccess.equals(sActualAccess)) {
                    // The Access exist but has to be updated
                    DomainAccess.createObjectOwnership(context,
                            sObjectId,
                            slNewSOV.get(0), // Organization
                            slNewSOV.get(1), // Project
                            sNewAccess, // Access
                            slNewSOV.get(2), // Comment
                            false); // Update
                    // ATTENTION: when Update mode=true, a remove + add is done
                    // BUT the add is done without read and show access
                    // So we must NOT use the Update mode to true.
                }
            } else {
                // It is a new ownership
                DomainAccess.createObjectOwnership(context,
                        sObjectId,
                        slNewSOV.get(0), // Organization
                        slNewSOV.get(1), // Project
                        sNewAccess, // Access
                        slNewSOV.get(2)); // Comment
            }
            MqlUtil.mqlCommand(context, "history on", true, new String[0]);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception in Method addSOV():: " + e);
            throw e;
        }
    }

    public void deleteSOV(Context context, HashMap<String, Object> paramMap) throws Exception {
        String sObjectId = (String) paramMap.get("objectId");
        StringList slActualSOV = (StringList) paramMap.get("actualSOV");
        logger.log(Level.INFO, "sObjectId : " + sObjectId);
        logger.log(Level.INFO, "slActualSOV : " + slActualSOV);
        try {
            MqlUtil.mqlCommand(context, "history off", true, new String[0]);
            DomainAccess.deleteObjectOwnership(context,
                    sObjectId,
                    slActualSOV.get(0), // Organization
                    slActualSOV.get(1), // Project
                    slActualSOV.get(2)); // Comment
            MqlUtil.mqlCommand(context, "history on", true, new String[0]);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception in Method deleteSOV():: " + e);
            throw e;
        }
    }

    /**
     * This method returns all the User Preference Templates information based selected user type and Category.
     *
     * @param context
     * @param strPartType : User Input Type.
     * @return MapList - Containing User Preferences templates information.
     */
    public MapList getUPTListForType(Context context, String strPartType) throws Exception {
        MapList mlUPTInfoList = new MapList();

        if (UIUtil.isNotNullAndNotEmpty(strPartType)) {
            try {
                //Get Part type and Part Category.
                final String ATTRIBUTE_UPT_PART_TYPE = UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(context);
                final String ATTRIBUTE_UPT_PART_CATEGORY = UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(context);
                final String UPT_TYPE = UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(context);
                //Add where clause for to select template.
                StringBuffer sbWhere = new StringBuffer();
                sbWhere.append(DomainObject.getAttributeSelect(ATTRIBUTE_UPT_PART_TYPE));
                sbWhere.append(" == '");
                sbWhere.append(strPartType);
                sbWhere.append("'");
                //Get Part Category.
                String sPartCategory = UserPreferenceTemplateUtil.getPartCategory(context, strPartType);
                if (UIUtil.isNotNullAndNotEmpty(sPartCategory)) {
                    sbWhere.append(" || (");
                    sbWhere.append(DomainObject.getAttributeSelect(ATTRIBUTE_UPT_PART_TYPE));
                    sbWhere.append(" == '");
                    sbWhere.append(DomainConstants.EMPTY_STRING);
                    sbWhere.append("' && ");
                    sbWhere.append(DomainObject.getAttributeSelect(ATTRIBUTE_UPT_PART_CATEGORY));
                    sbWhere.append(" == '");
                    sbWhere.append(sPartCategory);
                    sbWhere.append("')");
                }
                //Add UPT selects.
                StringList slUPTSelects = new StringList(6);
                slUPTSelects.add(DomainConstants.SELECT_TYPE);
                slUPTSelects.add(DomainConstants.SELECT_NAME);
                slUPTSelects.add(DomainConstants.SELECT_REVISION);
                slUPTSelects.add(DomainConstants.SELECT_ID);
                slUPTSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
                slUPTSelects.add("attribute[" + DomainConstants.QUERY_WILDCARD + "].value");
                //Get all the User Preference Templates based selected user type and Category.
                mlUPTInfoList = DomainObject.findObjects(context,                //context
                        UPT_TYPE,                                                //type
                        DomainConstants.QUERY_WILDCARD,                            //name
                        DomainConstants.QUERY_WILDCARD,                            //revision
                        DomainConstants.QUERY_WILDCARD,                            //owner
                        pgV3Constants.VAULT_ESERVICEPRODUCTION,                    //vault
                        sbWhere.toString(),                                        //where clause
                        false,                                                    //expand type
                        slUPTSelects);                                            //object select
            } catch (Exception e) {
                throw e;
            }
        }
        return mlUPTInfoList;
    }

    /**
	 * This method returns all the User Preference Templates id based on Part Type.
	 * @param context.
	 * @param strPartType : User Input Type.
	 * @return MapList - Containing User Preferences templates Information.
	 */
	public MapList getUserPreferenceTemplates(Context context, Map<String, String> ioParams) throws Exception {
		logger.log(Level.INFO, "Inside getUserPreferenceTemplates");
		MapList mlUPTInfo = new MapList();
		Instant startTime = Instant.now();
		try {
			logger.log(Level.INFO, "ioParams :: " + ioParams);
			//Add Object where clause.
			String sbUPTWhere=getUPTWhere(context, ioParams);
			mlUPTInfo=findUserPreferenceTemplate(context, sbUPTWhere);
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.log(Level.INFO, "Method getUserPreferenceTemplates() - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred in method getUserPreferenceTemplates() : " + e);
		}
		return mlUPTInfo;
	}

	/**
	 * This method returns where clause to get UPT.
	 * @param context.
	 * @param strSelectedType : User Selected Part Type.
	 * @return String - Returns where clause.
	 * @throws Exception. 
	 */	
	private String getUPTWhere(Context context, Map<String, String> ioParams) throws Exception {
		Instant startTime = Instant.now();
		StringBuffer sbWhere = new StringBuffer();
		//Add UPT Where Clause.
		sbWhere.append("(");
		sbWhere.append(DomainConstants.SELECT_POLICY);
		sbWhere.append(" == '");
		sbWhere.append(UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_DSM.getName(context));
		sbWhere.append("')");
		//Get Part Type and Category.
		if(null !=ioParams && ioParams.size() > 0) {
			String sPartType=ioParams.get(PART_TYPE);
			String sPartCategory=ioParams.get(PART_CATEGORY);
			logger.log(Level.INFO, "sPartType :: " + sPartType);
			logger.log(Level.INFO, "sPartCategory :: " + sPartCategory);
			//Add Object where clause.

			if(UIUtil.isNotNullAndNotEmpty(sPartCategory)) {
				sbWhere.append(" && (");	
				sbWhere.append(DomainObject.getAttributeSelect(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(context)));
				sbWhere.append(" == '");
				sbWhere.append(sPartType);
				sbWhere.append("' && ");			
				sbWhere.append(DomainObject.getAttributeSelect(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(context)));
				sbWhere.append(" == '");
				sbWhere.append(sPartCategory);
				sbWhere.append("')");
			}
		}
		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		logger.log(Level.INFO, "Method getUPTWhereClause() - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
		return sbWhere.toString();
	}

	/**
	 * This method returns all the UPT based where clause.
	 * @param context.
	 * @param sUPTWhereClause : Object Where Clause.
	 * @return MapList - Returns User Preferences template Information.
	 * @throws Exception. 
	 */
	@SuppressWarnings("unchecked")
	public MapList findUserPreferenceTemplate(Context context, String strUPTWhere) {
		Instant startTime = Instant.now();
		logger.log(Level.INFO, "Inside findUPTObjects");
		logger.log(Level.INFO, "strUPTWhere :: " + strUPTWhere);
		MapList mlUPTInfo = new MapList();
		//Add Object where clause.
		String strCtxUserName = context.getUser();
		logger.log(Level.INFO, "strCtxUserName :: " + strCtxUserName);
		try {
			//Add UPT Selects.
			StringList objectSelects = new StringList(8);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_OWNER);
			objectSelects.add(DomainConstants.SELECT_OWNERSHIP);
			objectSelects.add(DomainObject.getAttributeSelect(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(context)));
			objectSelects.add(DomainObject.getAttributeSelect(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(context)));
			//Get User Preference Type.
			String sType=UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(context);
			//Get all the User Preference Templates based selected user type.
			MapList mlUserPrefTemplate =DomainObject.findObjects(context,	//context
					sType,													//type
					DomainConstants.QUERY_WILDCARD,							//name					
					DomainConstants.QUERY_WILDCARD,							//revision
					DomainConstants.QUERY_WILDCARD,							//owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION,					//vault
					strUPTWhere,											//where clause
					false,													//expand type
					objectSelects);											//object select 

			Iterator<Map<Object, Object>> objIt = mlUserPrefTemplate.iterator();
			while (objIt.hasNext()) {
				Map<Object, Object> mapTemplate = (Map<Object, Object>) objIt.next();
				String strUPTOwner=(String)mapTemplate.get(DomainConstants.SELECT_OWNER);
				if(!strCtxUserName.equals(strUPTOwner)) {
					StringList slOwnershipList = (StringList) mapTemplate.get(DomainConstants.SELECT_OWNERSHIP);
					if(null !=slOwnershipList && !slOwnershipList.contains(DomainConstants.EMPTY_STRING)) {
						if(isCtxUserHasOwnership(slOwnershipList, strCtxUserName)) {
							mlUPTInfo.add(mapTemplate);
						}
					}
				}else {
					mlUPTInfo.add(mapTemplate);
				}
			}
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.log(Level.INFO, "Method findUPTObjects() - took |" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred in method findUPTObjects() : " + e);
		}
		return mlUPTInfo;
	}

	/**
	 * This method check if context user has ownership of UPT or not.
	 * @param slOwnershipList : Ownership Vector.
	 * @param strCtxUserName : Context User Name.
	 * @return boolean : If user has access then return true.
	 * @throws Exception. 
     */
	public boolean isCtxUserHasOwnership(StringList slOwnershipList, String strCtxUserName) throws Exception {
		boolean hasOwnership=false;
		for (String sActualSOV : slOwnershipList) {
			StringList slActualSOV = FrameworkUtil.split(sActualSOV, "|");
			if(null !=slActualSOV && !slActualSOV.contains(DomainConstants.EMPTY_STRING)) {
				if(slActualSOV.get(1).startsWith(strCtxUserName+"_")) {
					hasOwnership=true;
					break;
				}
			}
        }
		return hasOwnership;
    }
}
