package com.pg.dsm.preference.template.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;
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
import com.pg.dsm.preference.template.entity.User;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
import com.pg.dsm.preference.template.entity.ManufacturingEquivalent;
import com.pg.dsm.preference.template.entity.SupplierEquivalent;
//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

import matrix.db.Context;


public class UserPreferenceTemplateCreateDataTransformer {
    boolean transformed;
    String error;
    User user;

    private UserPreferenceTemplateCreateDataTransformer(Transform transform) {
        this.transformed = transform.transformed;
        this.error = transform.error;
        this.user = transform.user;
    }

    public boolean isTransformed() {
        return transformed;
    }

    public String getError() {
        return error;
    }

    public User getUser() {
        return user;
    }

    public static class Transform {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        boolean transformed;
        String error;
        User user;

        public Transform(Context context) {
            this.context = context;
        }

        public UserPreferenceTemplateCreateDataTransformer now(Map<Object, Object> objectMap) {
            try {
                run(objectMap);
                this.transformed = Boolean.TRUE;
            } catch (Exception e) {
                this.transformed = Boolean.FALSE;
                this.error = e.getMessage();
                logger.log(Level.WARNING, "Error loading template data into bean: " + e);
            }
            return new UserPreferenceTemplateCreateDataTransformer(this);
        }

        private void run(Map<Object, Object> objectMap) {

            // security data.
            Map<Object, Object> securityDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.SECURITY_DATA.get())) {
                securityDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.SECURITY_DATA.get());
            }
            logger.log(Level.INFO, "Security data: " + securityDataMap);

            // change action data.
            Map<Object, Object> changeActionDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.CHANGE_ACTION_DATA.get())) {
                changeActionDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.CHANGE_ACTION_DATA.get());
            }
            logger.log(Level.INFO, "Change Action data: " + changeActionDataMap);

            // packaging data.
            Map<Object, Object> packagingDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.PACKAGING_DATA.get())) {
                packagingDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.PACKAGING_DATA.get());
            }
            logger.log(Level.INFO, "Packaging data: " + packagingDataMap);

            // product data.
            Map<Object, Object> productDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.PRODUCT_DATA.get())) {
                productDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.PRODUCT_DATA.get());
            }
            logger.log(Level.INFO, "Product data: " + productDataMap);

            // raw-material data.
            Map<Object, Object> rawMaterialDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.RAW_MATERIAL_DATA.get())) {
                rawMaterialDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.RAW_MATERIAL_DATA.get());
            }
            logger.log(Level.INFO, "Raw Material data: " + rawMaterialDataMap);

            // Technical Specification Data
            Map<Object, Object> technicalSpecificationDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.TECHNICAL_SPECIFICATION_DATA.get())) {
                technicalSpecificationDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.TECHNICAL_SPECIFICATION_DATA.get());
            }
            logger.log(Level.INFO, "Technical Specification data: " + technicalSpecificationDataMap);

            // Exploration Data
            Map<Object, Object> explorationDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.EXPLORATION_DATA.get())) {
                explorationDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.EXPLORATION_DATA.get());
            }
            logger.log(Level.INFO, "Exploration data: " + explorationDataMap);
			//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
            // MEP Data
            Map<Object, Object> MEPDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.MANUFACTURING_EQUIVALENT_DATA.get())) {
            	MEPDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.MANUFACTURING_EQUIVALENT_DATA.get());
            }
            logger.log(Level.INFO, "MEP data: " + MEPDataMap);
            
            // SEP Data
            Map<Object, Object> SEPDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.SUPPLIER_EQUIVALENT_DATA.get())) {
            	SEPDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.SUPPLIER_EQUIVALENT_DATA.get());
            }
            logger.log(Level.INFO, "SEP data: " + SEPDataMap);
			//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END            
            // plants
            MapList plantsList = new MapList();
            if (objectMap.containsKey(PreferenceConstants.Basic.PLANTS_DATA.get())) {
                plantsList = (MapList) objectMap.get(PreferenceConstants.Basic.PLANTS_DATA.get());
            }
            logger.log(Level.INFO, "Plants data: " + plantsList);

            // sharing members.
            MapList membersList = new MapList();
            if (objectMap.containsKey(PreferenceConstants.Basic.SHARING_MEMBERS_DATA.get())) {
                membersList = (MapList) objectMap.get(PreferenceConstants.Basic.SHARING_MEMBERS_DATA.get());
            }
            logger.log(Level.INFO, "Sharing Members data: " + membersList);


            // sharing template members.
            MapList templateSharingMemberList = new MapList();
            if (objectMap.containsKey(PreferenceConstants.Basic.SHARING_TEMPLATE_MEMBERS_DATA.get())) {
                templateSharingMemberList = (MapList) objectMap.get(PreferenceConstants.Basic.SHARING_TEMPLATE_MEMBERS_DATA.get());
            }
            logger.log(Level.INFO, "Sharing Template Members data: " + templateSharingMemberList);


            // template specific data.
            Map<Object, Object> templateDataMap = new HashMap<>();
            if (objectMap.containsKey(PreferenceConstants.Basic.TEMPLATE_SPECIFIC_DATA.get())) {
                templateDataMap = (Map<Object, Object>) objectMap.get(PreferenceConstants.Basic.TEMPLATE_SPECIFIC_DATA.get());

                // type, policy.
                String type = UserPreferenceTemplateConstants.Type.TYPE_USER_PREFERENCE_TEMPLATE.getName(this.context);
                logger.log(Level.INFO, "User Preference Template type: " + type);

                String policy = UserPreferenceTemplateConstants.Policy.POLICY_USER_PREFERENCE_TEMPLATE_DSM.getName(this.context);
                logger.log(Level.INFO, "User Preference Template policy: " + policy);

                templateDataMap.put(DomainConstants.SELECT_TYPE, type);
                templateDataMap.put(DomainConstants.SELECT_POLICY, policy);
            }
            logger.log(Level.INFO, "template data: " + templateDataMap);

            Packaging packaging = new Packaging(packagingDataMap);
            logger.log(Level.INFO, "Packaging data transformed");
            Product product = new Product(productDataMap);
            logger.log(Level.INFO, "Product data transformed");
            RawMaterial rawMaterial = new RawMaterial(rawMaterialDataMap);
            logger.log(Level.INFO, "RawMaterial data transformed");
            TechnicalSpecification technicalSpecification = new TechnicalSpecification(technicalSpecificationDataMap);
            logger.log(Level.INFO, "Technical Specification data transformed technicalSpecificationDataMap:" + technicalSpecificationDataMap);
            Exploration exploration = new Exploration(explorationDataMap);
            logger.log(Level.INFO, "Exploration data transformed exploration:" + exploration);
			//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
            ManufacturingEquivalent manufacturingEquivalent = new ManufacturingEquivalent(MEPDataMap);
            logger.log(Level.INFO, "ManufacturingEquivalent data transformed manufacturingEquivalentDataMap :" + manufacturingEquivalent);
            SupplierEquivalent supplierEquivalent = new SupplierEquivalent(SEPDataMap);
            logger.log(Level.INFO, "SupplierEquivalent data transformed supplierEquivalentDataMap :" + supplierEquivalent);
			//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
            List<Member> sharingMemberList = DSMUserPreferenceTemplateUtil.getSharingMembers(membersList);
            logger.log(Level.INFO, "Sharing Member List data transformed");
            List<Plant> plantList = DSMUserPreferenceTemplateUtil.getPlants(plantsList);
            logger.log(Level.INFO, "Plant List data transformed plantList" + plantList);
            List<TemplateMember> templateMemberList = DSMUserPreferenceTemplateUtil.getTemplateSharingMembers(templateSharingMemberList);
            logger.log(Level.INFO, "Sharing Template Member List data transformed");
            Security security = new Security(securityDataMap);
            logger.log(Level.INFO, "Security data transformed");
            Change change = new Change(changeActionDataMap);
            logger.log(Level.INFO, "Change Action data transformed");

            Template template = new Template(templateDataMap);
            logger.log(Level.INFO, "Template data transformed");

            template.setSecurity(security);
            template.setHasSecurity(security.isHasSecurity());
            logger.log(Level.INFO, "Template set security bean");

            template.setChange(change);
            template.setHasChange(change.isHasChange());
            logger.log(Level.INFO, "Template set change action bean");

            template.setPlantList(plantList);
            template.setHasPlant((plantList.size() > 0) ? Boolean.TRUE : Boolean.FALSE);
            logger.log(Level.INFO, "Template set plants bean");

            template.setMemberList(sharingMemberList);
            template.setHasMember((sharingMemberList.size() > 0) ? Boolean.TRUE : Boolean.FALSE);
            logger.log(Level.INFO, "Template set sharing members bean");

            template.setTemplateMemberList(templateMemberList);
            template.setHasTemplateMembers((templateMemberList.size() > 0) ? Boolean.TRUE : Boolean.FALSE);
            logger.log(Level.INFO, "Template set sharing template members bean");

            template.setPackaging(packaging);
            template.setHasPackaging(packaging.isHasPackaging());
            logger.log(Level.INFO, "Template set packaging bean");

            template.setProduct(product);
            template.setHasProduct(product.isHasProduct());
            logger.log(Level.INFO, "Template set product bean");

            template.setRawMaterial(rawMaterial);
            template.setHasRawMaterial(rawMaterial.isHasRawMaterial());
            logger.log(Level.INFO, "Template set raw material bean");

            template.setTechnicalSpecification(technicalSpecification);
            template.setHasTechnicalSpecification(technicalSpecification.isHasTechnicalSpecification());
            logger.log(Level.INFO, "Template set Technical Specification bean  ");

            template.setExploration(exploration);
            template.setHasExploration(exploration.isHasExploration());
            logger.log(Level.INFO, "Template set Exploration bean  ");
			
			//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
            template.setManufacturingEquivalent(manufacturingEquivalent);
            template.setHasManufacturingEquivalent(manufacturingEquivalent.isHasMEP());
            logger.log(Level.INFO, "Template set Manufacturing Equivalent bean  ");
            
            template.setSupplierEquivalent(supplierEquivalent);
            template.setHasSupplierEquivalent(supplierEquivalent.isHasSEP());
            logger.log(Level.INFO, "Template set Supplier Equivalent bean  ");
			//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
            
            this.user = new User();
            logger.log(Level.INFO, "Create user bean");
            this.user.setName(template.getOwner());
            this.user.setId(template.getOwnerID());
            this.user.setTemplate(template);
            logger.log(Level.INFO, "Set Template bean");
            logger.log(Level.INFO, "Transformed User: " + this.user);
        }

    }
}
