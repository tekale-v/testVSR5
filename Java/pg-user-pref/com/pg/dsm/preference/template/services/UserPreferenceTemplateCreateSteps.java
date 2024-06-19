package com.pg.dsm.preference.template.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.entity.Change;
import com.pg.dsm.preference.template.entity.Exploration;
import com.pg.dsm.preference.template.entity.Member;
import com.pg.dsm.preference.template.entity.Packaging;
import com.pg.dsm.preference.template.entity.Plant;
import com.pg.dsm.preference.template.entity.Product;
import com.pg.dsm.preference.template.entity.RawMaterial;
import com.pg.dsm.preference.template.entity.Security;
import com.pg.dsm.preference.template.entity.TechnicalSpecification;
import com.pg.dsm.preference.template.entity.Template;
import com.pg.dsm.preference.template.entity.TemplateMember;
import com.pg.dsm.preference.template.interfaces.IUserPreferenceTemplateCreateSteps;
import com.pg.v3.custom.pgV3Constants;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
import com.pg.dsm.preference.template.entity.ManufacturingEquivalent;
import com.pg.dsm.preference.template.entity.SupplierEquivalent;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

import matrix.db.Context;

public class UserPreferenceTemplateCreateSteps implements IUserPreferenceTemplateCreateSteps {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;
    Template template;

    public UserPreferenceTemplateCreateSteps(Context context, Template template) {
        this.context = context;
        this.template = template;
    }

    @Override
    public String getCreatedID(String objectName, String type, String policy) throws Exception {
        String objectId = DomainConstants.EMPTY_STRING;
        UserPreferenceTemplateCreator templateCreator = new UserPreferenceTemplateCreator.Create(this.context).now(objectName, type, policy);
        if (templateCreator.isCreated()) {
            objectId = templateCreator.getObjectId();
        }
        logger.log(Level.INFO, "User Preference Template objectId@: " + objectId);
        return objectId;
    }

    @Override
    public void updateSecurityPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasSecurity()) {
            Security security = this.template.getSecurity();
            if (null != security) {
                Map<Object, Object> attributeMap = new HashMap<>();
                String businessUsePhysicalID = security.getBusinessUsePhysicalID();
                if (UIUtil.isNotNullAndNotEmpty(businessUsePhysicalID)) {

                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_BUSINESS_USE_IP_CLASS.getName(this.context), FrameworkUtil.split(businessUsePhysicalID, "|"));
                }
                String highlyRestrictedPhysicalID = security.getHighlyRestrictedPhysicalID();
                if (UIUtil.isNotNullAndNotEmpty(highlyRestrictedPhysicalID)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_HIGHLY_RESTRICTED_IP_CLASS.getName(this.context), FrameworkUtil.split(highlyRestrictedPhysicalID, "|"));
                }
                String primaryOrganizationOID = security.getPrimaryOrganizationPhysicalID();
                if (UIUtil.isNotNullAndNotEmpty(primaryOrganizationOID)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PRIMARY_ORGANIZATION.getName(this.context), primaryOrganizationOID);
                }
                logger.log(Level.INFO, "Update  updateSecurityPreferences attributeMap: " + attributeMap);
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }

    @Override
    public void updateChangeActionPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasChange()) {
            Change change = this.template.getChange();
            if (null != change) {
                Map<Object, Object> attributeMap = new HashMap<>();
                attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_WORK.getName(this.context), FrameworkUtil.split(change.getInWorkRouteTemplatePhysicalID(), "|"));
                attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_ROUTE_TEMPLATE_IN_APPROVAL.getName(this.context), FrameworkUtil.split(change.getInApprovalRouteTemplatePhysicalID(), "|"));
                attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_CHANGE_TEMPLATE.getName(this.context), change.getChangeTemplatePhysicalID());
                logger.log(Level.INFO, "update  Change Action Preferences: " + change.getInformedUserPhysicalID());
                attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_INFORMED_USERS.getName(this.context), FrameworkUtil.split(change.getInformedUserPhysicalID(), "|"));
                logger.log(Level.INFO, "update  Change Action Preferences: " + attributeMap);
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }

    @Override
    public void updatePlantPreferences(DomainObject domainObject) throws Exception {
        if (this.template.isHasPlant()) {
            Map<String, String> attributeMap = new HashMap<String, String>();
            List<Plant> plantList = this.template.getPlantList();
            if (null != plantList && !plantList.isEmpty()) {
                DomainObject fromObj = new DomainObject();
                for (Plant plant : plantList) {
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISACTIVATED, plant.getActivated());
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE, plant.getAuthorizedToUse());
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE, plant.getAuthorizedToProduce());
                    attributeMap.put(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOVIEW, plant.getAuthorized());
                    DomainRelationship rel = DomainRelationship.connect(context, new DomainObject(plant.getId()), UserPreferenceTemplateConstants.Relationship.RELATIONSHIP_USER_PREFERENCE_PLANT.getName(this.context), domainObject);
                    rel.setAttributeValues(context, attributeMap);
                }
            }
        }
    }

    @Override
    public void updateSharingMemberPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasMember()) {
            List<Member> memberList = this.template.getMemberList();
            if (null != memberList && !memberList.isEmpty()) {
                String physicalId = DomainConstants.EMPTY_STRING;
                StringBuilder sbMembers = new StringBuilder();
                for (Member member : memberList) {
                    physicalId = member.getPhysicalId();
                    sbMembers.append(pgV3Constants.SYMBOL_COMMA + physicalId);
                }
                Map<Object, Object> attributeMap = new HashMap<>();
                attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SHARE_WITH_MEMBERS.getName(this.context), FrameworkUtil.split(sbMembers.deleteCharAt(0).toString(), ","));
                logger.log(Level.INFO, "Updated Template Sharing Members attributeMap " + attributeMap);
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }

    @Override
    public void updateTemplateSharingMemberPreferences(DomainObject domainObject) throws Exception {
        if (this.template.isHasTemplateMembers()) {
            List<TemplateMember> memberList = this.template.getTemplateMemberList();
            boolean isCtxPushed = Boolean.FALSE;
            try {
				ContextUtil.pushContext(this.context, PropertyUtil.getSchemaProperty(this.context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isCtxPushed = Boolean.TRUE;
                if (null != memberList && !memberList.isEmpty()) {
                    String id = DomainConstants.EMPTY_STRING;
                    String personName;
                    for (TemplateMember templateMember : memberList) {
                        id = templateMember.getId();
                        logger.log(Level.INFO, "Preference Sharing Members List: {0}", id);
                        personName = DomainObject.newInstance(this.context, id).getInfo(this.context, DomainConstants.SELECT_NAME);
                        personName += "_PRJ";
                        DomainAccess.createObjectOwnership(context, domainObject.getId(this.context), "-", personName, UserPreferenceTemplateConstants.Basic.TEMPLATE_SHARE_WITH_BASIC_ACCESS.get(), DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
                    }
                   
                    logger.log(Level.INFO, "Updated Template Sharing With others ");
                }
            } catch (Exception e) {
                logger.log(Level.INFO, "Error: " + e);
                throw e;
            } finally {
                if (isCtxPushed) {
                    ContextUtil.popContext(context);
                }
            }
        }
    }

    @Override
    public void updatePackagingPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasPackaging()) {
            Packaging packaging = this.template.getPackaging();
            if (null != packaging) {
                Map<Object, Object> attributeMap = new HashMap<>();
                String category = packaging.getCategory();
                if (UIUtil.isNotNullAndNotEmpty(category)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), category);
                }
                String type = packaging.getType();
                if (UIUtil.isNotNullAndNotEmpty(type)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), type.trim());
                }
                String phase = packaging.getPhase();
                if (UIUtil.isNotNullAndNotEmpty(phase)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_RELEASE_PHASE.getName(this.context), phase);
                }
                String mfgStatus = packaging.getMfgStatus();
                if (UIUtil.isNotNullAndNotEmpty(mfgStatus)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_LIFECYCLE_STATUS.getName(this.context), mfgStatus);
                }
                String releaseCriteria = packaging.getReleaseCriteria();
                if (UIUtil.isNotNullAndNotEmpty(releaseCriteria)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_STRUCTURE_RELEASE_CRITERIA_REQUIRED.getName(this.context), releaseCriteria);
                }
                String classType = packaging.getClassType();
                if (UIUtil.isNotNullAndNotEmpty(classType)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_CLASS.getName(this.context), classType.trim());
                }
                String reportedFunction = packaging.getReportedFunction();
                if (UIUtil.isNotNullAndNotEmpty(reportedFunction)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_REPORTED_FUNCTION.getName(this.context), reportedFunction.trim());
                }
                String segment = packaging.getSegment();
                if (UIUtil.isNotNullAndNotEmpty(segment)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SEGMENT.getName(this.context), segment.trim());
                }
                String componentType = packaging.getComponentType();
                if (UIUtil.isNotNullAndNotEmpty(componentType)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PACKAGING_COMPONENT_TYPE.getName(this.context), componentType.trim());
                }
                String materialType = packaging.getMaterialType();
                if (UIUtil.isNotNullAndNotEmpty(materialType)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PACKAGING_MATERIAL_TYPE.getName(this.context), materialType.trim());
                }
                String baseUoM = packaging.getBaseUoM();
                if (UIUtil.isNotNullAndNotEmpty(baseUoM)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_BASE_UNIT_OF_MEASURE.getName(this.context), baseUoM.trim());
                }
                logger.log(Level.INFO, "Update  product attributeMap: " + attributeMap);
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }

    @Override
    public void updateProductPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasProduct()) {
            Product product = this.template.getProduct();
            if (null != product) {
                Map<Object, Object> attributeMap = new HashMap<>();
                if (UIUtil.isNotNullAndNotEmpty(product.getCategory())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), product.getCategory());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getType())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), product.getType().trim());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getPhase())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_RELEASE_PHASE.getName(this.context), product.getPhase());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getMfgStatus())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_LIFECYCLE_STATUS.getName(this.context), product.getMfgStatus());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getReleaseCriteria())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_STRUCTURE_RELEASE_CRITERIA_REQUIRED.getName(this.context), product.getReleaseCriteria());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getClassType())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_CLASS.getName(this.context), product.getClassType().trim());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getReportedFunction())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_REPORTED_FUNCTION.getName(this.context), product.getReportedFunction().trim());
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getSegment())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SEGMENT.getName(this.context), product.getSegment().trim());
                }

                // update as multi-value attributes.
                if (UIUtil.isNotNullAndNotEmpty(product.getBusinessArea())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_BUSINESS_AREA.getName(this.context), FrameworkUtil.split(product.getBusinessArea(), "|"));
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getCategoryPlatform())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PRODUCT_CATEGORY_PLATFORM.getName(this.context), FrameworkUtil.split(product.getCategoryPlatform(), "|"));
                }
                if (UIUtil.isNotNullAndNotEmpty(product.getComplianceRequired())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_IS_PRODUCT_COMPLIANCE_REQUIRED.getName(this.context), FrameworkUtil.split(product.getComplianceRequired(), ","));
                }
                logger.log(Level.INFO, "Update  product attributeMap: " + attributeMap);
                domainObject.setAttributeValues(this.context, attributeMap);
                logger.log(Level.INFO, "Update  product attributeMap: " + attributeMap);
            }
        }
    }

    @Override
    public void updateRawMaterialPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasRawMaterial()) {
            RawMaterial rawMaterial = this.template.getRawMaterial();
            logger.log(Level.INFO, "before Update rawMaterial isHasRawMaterial: ");
            if (null != rawMaterial) {
                Map<Object, Object> attributeMap = new HashMap<>();
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getCategory())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), rawMaterial.getCategory());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getType())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), rawMaterial.getType().trim());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getPhase())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_RELEASE_PHASE.getName(this.context), rawMaterial.getPhase());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getMfgStatus())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_LIFECYCLE_STATUS.getName(this.context), rawMaterial.getMfgStatus());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getReleaseCriteria())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_STRUCTURE_RELEASE_CRITERIA_REQUIRED.getName(this.context), rawMaterial.getReleaseCriteria());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getClassType())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_CLASS.getName(this.context), rawMaterial.getClassType().trim());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getReportedFunction())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_REPORTED_FUNCTION.getName(this.context), rawMaterial.getReportedFunction().trim());
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getSegment())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SEGMENT.getName(this.context), rawMaterial.getSegment().trim());
                }

                // update as multi-value attributes.
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getBusinessArea())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_BUSINESS_AREA.getName(this.context), FrameworkUtil.split(rawMaterial.getBusinessArea(), "|"));
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getCategoryPlatform())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PRODUCT_CATEGORY_PLATFORM.getName(this.context), FrameworkUtil.split(rawMaterial.getCategoryPlatform(), "|"));
                }
                if (UIUtil.isNotNullAndNotEmpty(rawMaterial.getMaterialFunction())) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_MATERIAL_FUNCTION.getName(this.context), FrameworkUtil.split(rawMaterial.getMaterialFunction(), "|"));
                }
                domainObject.setAttributeValues(this.context, attributeMap);
                logger.log(Level.INFO, " after Update rawMaterial attributeMap: " + attributeMap);
            }
        }
    }

    @Override
    public void updateTechnicalSpecificationPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasTechnicalSpecification()) {
            TechnicalSpecification technicalSpecification = this.template.getTechnicalSpecification();
            if (null != technicalSpecification) {
                Map<Object, Object> attributeMap = new HashMap<>();
                String category = technicalSpecification.getCategory();
                if (UIUtil.isNotNullAndNotEmpty(category)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), category.trim());
                    logger.log(Level.INFO, " after Update Technical Specification attributeMap:category " + category);
                }
                String type = technicalSpecification.getType();
                if (UIUtil.isNotNullAndNotEmpty(type)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), type.trim());
                    logger.log(Level.INFO, " after Update Technical Specification attributeMap:type " + type);
                }
                String segment = technicalSpecification.getSegment();
                if (UIUtil.isNotNullAndNotEmpty(segment)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_SEGMENT.getName(this.context), segment.trim());
                }
                logger.log(Level.INFO, " after Update Technical Specification attributeMap:segment " + segment);
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }

    @Override
    public void updateExplorationPreferences(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasExploration()) {
            Exploration exploration = this.template.getExploration();
            if (null != exploration) {
                Map<Object, Object> attributeMap = new HashMap<>();
                String category = exploration.getCategory();
                if (UIUtil.isNotNullAndNotEmpty(category)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), category.trim());
                    logger.log(Level.INFO, " after Update Exploration attributeMap:category " + category);
                }
                String type = exploration.getType();
                if (UIUtil.isNotNullAndNotEmpty(type)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), type.trim());
                    logger.log(Level.INFO, " after Update Exploration attributeMap:type " + type);
                }
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }

	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    @Override
    public void updateManufacturingEquivalent(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasManufacturingEquivalent()) {
            ManufacturingEquivalent manufacturingEquivalent = this.template.getManufacturingEquivalent();
            if (null != manufacturingEquivalent) {
                Map<Object, Object> attributeMap = new HashMap<>();
                String category = manufacturingEquivalent.getCategory();
                if (UIUtil.isNotNullAndNotEmpty(category)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), category.trim());
                    logger.log(Level.INFO, " after Update Manufacturing Equivalent attributeMap:category " + category);
                }
                String type = manufacturingEquivalent.getType();
                if (UIUtil.isNotNullAndNotEmpty(type)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), type.trim());
                    logger.log(Level.INFO, " after Update Manufacturing Equivalent attributeMap:type " + type);
                }
                String vendor = manufacturingEquivalent.getVendor();
                if (UIUtil.isNotNullAndNotEmpty(vendor)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_VENDOR.getName(this.context), vendor.trim());
                    logger.log(Level.INFO, " after Update Manufacturing Equivalent attributeMap:Vendor " + vendor);
                }
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }
    

    @Override
    public void updateSupplierEquivalent(DomainObject domainObject) throws FrameworkException {
        if (this.template.isHasSupplierEquivalent()) {
            SupplierEquivalent supplierEquivalent = this.template.getSupplierEquivalent();
            if (null != supplierEquivalent) {
                Map<Object, Object> attributeMap = new HashMap<>();
                String category = supplierEquivalent.getCategory();
                if (UIUtil.isNotNullAndNotEmpty(category)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_CATEGORY.getName(this.context), category.trim());
                    logger.log(Level.INFO, " after Update Supplier Equivalent attributeMap:category " + category);
                }
                String type = supplierEquivalent.getType();
                if (UIUtil.isNotNullAndNotEmpty(type)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_PART_TYPE.getName(this.context), type.trim());
                    logger.log(Level.INFO, " after Update Supplier Equivalent attributeMap:type " + type);
                }
                String vendor = supplierEquivalent.getVendor();
                if (UIUtil.isNotNullAndNotEmpty(vendor)) {
                    attributeMap.put(UserPreferenceTemplateConstants.Attribute.ATTRIBUTE_UPT_VENDOR.getName(this.context), vendor.trim());
                    logger.log(Level.INFO, " after Update Supplier Equivalent attributeMap:Vendor " + vendor);
                }
                domainObject.setAttributeValues(this.context, attributeMap);
            }
        }
    }
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
}
