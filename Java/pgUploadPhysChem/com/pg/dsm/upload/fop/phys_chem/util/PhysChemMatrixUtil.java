/*
 **   PhysChemMatrixUtil.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Utility class to database operation.
 **
 */

package com.pg.dsm.upload.fop.phys_chem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.fop.enumeration.FormulationAttributeConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationPolicyConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationRelationshipConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationStateConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationTypeConstant;
import com.pg.dsm.upload.fop.phys_chem.factory.FormulationPartFactory;
import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemContext;
import com.pg.dsm.upload.fop.phys_chem.models.bo.BusinessAreaBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductCategoryPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductFormBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductTechnologyPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChem;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChemBean;
import com.pg.dsm.upload.fop.phys_chem.services.fop.FormulationPartService;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PhysChemMatrixUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private StringList businessObjectSelectList = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_NAME, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT);

    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    public PhysChemMatrixUtil() {
        logger.info("Constructor");
    }

    /**
     * Method to find Formulation Part.
     *
     * @param name            - String - fop name
     * @param revision        - String  - fop revision
     * @param sBusWhereClause String  - where clause
     * @param busSelects      StringList  - bus select
     * @return MapList - all formulation part
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public MapList findFormulationPart(String name, String revision, String sBusWhereClause, StringList busSelects) throws FrameworkException {
        Context context = PhysChemContext.getContext();
        return DomainObject.findObjects(
                context,
				//Modify as per 2018x.6 - Starts
                new StringBuilder(FormulationTypeConstant.FORMULATION_PART.getType(context)).append(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue()).append(FormulationTypeConstant.ASSEMBLEDPRODUCTPART_PART.getType(context)).toString(), // typePattern
				//Modify as per 2018x.6 - Ends
                name, // name pattern
                revision, // revision pattern
                DomainConstants.QUERY_WILDCARD, // owner pattern
                context.getVault().getName(), // vault pattern
                sBusWhereClause, // where expression
                false, // expandType
                busSelects);// objectSelects
    }

    /**
     * Method to read pgUploadPhysChem.xml file as a string.
     *
     * @return String
     * @since DSM 2018x.5
     */
    public String readPhysChemXMLFileAsString() {
        String xmlContent = DomainConstants.EMPTY_STRING;
        File file = new File(PhysChemFolderUtil.getPhysChemXMLConfigFilePath());
        try (FileInputStream inputStream = new FileInputStream(file)) {
            xmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return xmlContent;
    }

    /**
     * Method to get physical chemical bean object.
     *
     * @return PhysChemBean - bean object
     * @since DSM 2018x.5
     */
    public PhysChemBean getPhysChemBean(Properties physChemProperties) {
        PhysChemBean physChemBean = null;
        String xmlContent = readPhysChemXMLFileAsString();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PhysChemBean.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            physChemBean = (PhysChemBean) unmarshaller.unmarshal(new StringReader(xmlContent));

            String picklistTypeBusinessArea = physChemProperties.getProperty(FormulationGeneralConstant.PICKLIST_TYPE_BUSINESS_AREA.getValue());
            String picklistTypeProductCategoryPlatform = physChemProperties.getProperty(FormulationGeneralConstant.PICKLIST_TYPE_PRODUCT_CATEGORY_PLATFORM.getValue());
            String picklistTypeProductTechnologyPlatform = physChemProperties.getProperty(FormulationGeneralConstant.PICKLIST_TYPE_PRODUCT_TECHNOLOGY_PLATFORM.getValue());

            // set required business area, product category platform, product technology platform.
            physChemBean.setRequiredAerosolBusinessAreaBean(getBusinessAreaBean(picklistTypeBusinessArea, physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_BUSINESS_AREA.getValue())));
            physChemBean.setRequiredSolidBusinessAreaBean(getBusinessAreaBean(picklistTypeBusinessArea, physChemProperties.getProperty(FormulationGeneralConstant.SOLID_BUSINESS_AREA.getValue())));
            physChemBean.setRequiredLiquidBusinessAreaBean(getBusinessAreaBean(picklistTypeBusinessArea, physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_BUSINESS_AREA.getValue())));

            physChemBean.setRequiredAerosolProductCategoryPlatformBean(getProductCategoryPlatformBean(picklistTypeProductCategoryPlatform, physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_PRODUCT_CATEGORY_PLATFORM.getValue())));
            physChemBean.setRequiredSolidProductCategoryPlatformBean(getProductCategoryPlatformBean(picklistTypeProductCategoryPlatform, physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_CATEGORY_PLATFORM.getValue())));
            physChemBean.setRequiredLiquidProductCategoryPlatformBean(getProductCategoryPlatformBean(picklistTypeProductCategoryPlatform, physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_CATEGORY_PLATFORM.getValue())));

            physChemBean.setRequiredAerosolProductTechnologyPlatformBean(getProductTechnologyPlatformBean(picklistTypeProductTechnologyPlatform, physChemProperties.getProperty(FormulationGeneralConstant.AEROSOL_PRODUCT_TECHNOLOGY_PLATFORM.getValue())));
            physChemBean.setRequiredSolidProductTechnologyPlatformBean(getProductTechnologyPlatformBean(picklistTypeProductTechnologyPlatform, physChemProperties.getProperty(FormulationGeneralConstant.SOLID_PRODUCT_TECHNOLOGY_PLATFORM.getValue())));
            physChemBean.setRequiredLiquidProductTechnologyPlatformBean(getProductTechnologyPlatformBean(picklistTypeProductTechnologyPlatform, physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_PRODUCT_TECHNOLOGY_PLATFORM.getValue())));
			//Modify as per 2018x.6 - Starts
            physChemBean.setRequiredLiquidBusinessAreaReserveAlkalinityBean(getBusinessAreaBean(picklistTypeBusinessArea, physChemProperties.getProperty(FormulationGeneralConstant.LIQUID_BUSINESS_AREA_REVERSE_ALKALINITY.getValue())));
			//Modify as per 2018x.6 - Ends
        } catch (JAXBException | FrameworkException e) {
            logger.error(e.getMessage());
        }
        return physChemBean;
    }

    /**
     * Method to load property entries from file.
     *
     * @return Properties - properties
     * @since DSM 2018x.5
     */
    public Properties loadPhysChemPropertyFile() {
        Properties properties = new Properties();
        try (InputStream inStream = new FileInputStream(PhysChemFolderUtil.getPhysChemPropertyConfigFilePath())) {
            properties.load(inStream);
        } catch (Exception e) {
            logger.error("************FAILED >>> Unable to load pgUploadPhysChem.properties " + e);
        }
        return properties;
    }

    /**
     * Method to get the context
     *
     * @return Context
     * @since DSM 2018x.5
     */
    public Context getContext(Properties properties) {
        Context context = null;
        try {
            context = new Context(properties.getProperty(FormulationGeneralConstant.MATRIX_URL.getValue()));
            context.setUser(properties.getProperty(FormulationGeneralConstant.MATRIX_USR.getValue()));
            context.setPassword(properties.getProperty(FormulationGeneralConstant.MATRIX_PDW.getValue()));
            context.connect();
            if (context.isConnected())
                logger.info("Matrix connection established|User|" + context.getUser());
        } catch (Exception e) {
            logger.error("Exception:" + e.getMessage());
        }
        return context;
    }

    /**
     * Method to get Formulation Part Data
     *
     * @param nameList     - List<String> - fop name list
     * @param revisionList - List<String> - fop revision list
     * @return Map<String, IFormulationPart>
     * @throws FrameworkException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException - exceptions
     * @since DSM 2018x.5
     */
    public Map<String, IFormulationPart> getFormulationPartData(List<String> nameList, List<String> revisionList, Properties physChemProperties) throws FrameworkException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Instant startTime = Instant.now();
        String picklistTypeBusinessArea = physChemProperties.getProperty(FormulationGeneralConstant.PICKLIST_TYPE_BUSINESS_AREA.getValue());
        String picklistTypeProductCategoryPlatform = physChemProperties.getProperty(FormulationGeneralConstant.PICKLIST_TYPE_PRODUCT_CATEGORY_PLATFORM.getValue());
        String picklistTypeProductTechnologyPlatform = physChemProperties.getProperty(FormulationGeneralConstant.PICKLIST_TYPE_PRODUCT_TECHNOLOGY_PLATFORM.getValue());
        DomainObject dObj = DomainObject.newInstance(PhysChemContext.getContext());
        StringList busSelects = getFormulationPartBusAttributeSelects();
        MapList objectList = findFormulationPart(String.join(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue(), nameList), String.join(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue(), revisionList), DomainConstants.EMPTY_STRING, busSelects);
        Map tempMap;
        for (Object eachObj : objectList) {
            tempMap = (Map) eachObj;
            dObj.setId((String) tempMap.get(DomainConstants.SELECT_ID));
            tempMap.put(FormulationGeneralConstant.CONST_CONNECTED_PRODUCT_FORMS.getValue(), getConnectedProductForms(dObj));
            tempMap.put(FormulationGeneralConstant.CONST_CONNECTED_BUSINESS_AREA.getValue(), getConnectedBusinessArea(dObj, picklistTypeBusinessArea));
            tempMap.put(FormulationGeneralConstant.CONST_CONNECTED_PRODUCT_CATEGORY_PLATFORM.getValue(), getConnectedProductCategoryPlatforms(dObj, picklistTypeProductCategoryPlatform));
            tempMap.put(FormulationGeneralConstant.CONST_CONNECTED_PRODUCT_TECHNOLOGY_PLATFORM.getValue(), getConnectedProductTechnologyPlatforms(dObj, picklistTypeProductTechnologyPlatform));

        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("DB Query to Get all FOP object Info took|" + duration.toMillis() + " " + FormulationGeneralConstant.CONST_MILLI_SECONDS.getValue() + "|" + duration.getSeconds() + " " + FormulationGeneralConstant.CONST_SECONDS.getValue() + "|" + duration.toMinutes() + " " + FormulationGeneralConstant.CONST_MINUTES.getValue());
        return convertMapListToBeanMap(objectList);
    }

    /**
     * Method to get connected Business Area.
     *
     * @param formulationObj - DomainObject - formulation part  domain object.
     * @return List<BusinessAreaBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<BusinessAreaBean> getConnectedBusinessArea(DomainObject formulationObj, String businessAreaType) throws FrameworkException {
        List<BusinessAreaBean> businessAreaBeanList = new ArrayList<>();
        Context context = PhysChemContext.getContext();

        MapList objectList = getExpansionObjects(context,
                formulationObj,
                FormulationRelationshipConstant.DOCUMENT_TO_BUSINESS_AREA.getRelationship(context),
                PropertyUtil.getSchemaProperty(context, businessAreaType),
                false, true);

        Map<?, ?> tempMap;
        ObjectMapper objectMapper;
        for (Object eachObj : objectList) {
            tempMap = (Map<?, ?>) eachObj;
            objectMapper = new ObjectMapper();
            businessAreaBeanList.add(objectMapper.convertValue(tempMap, BusinessAreaBean.class));
        }

        logger.info("Connected Business Area: " + businessAreaBeanList.size());
        return businessAreaBeanList;
    }

    /**
     * Method to get connected Product Category Platform(s).
     *
     * @param formulationObj - DomainObject - formulation part  domain object.
     * @return List<ProductCategoryPlatformBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<ProductCategoryPlatformBean> getConnectedProductCategoryPlatforms(DomainObject formulationObj, String productCategoryPlatformType) throws FrameworkException {
        List<ProductCategoryPlatformBean> productCategoryPlatformBeansList = new ArrayList<>();
        Context context = PhysChemContext.getContext();

        MapList objectList = getExpansionObjects(context,
                formulationObj,
                FormulationRelationshipConstant.DOCUMENT_TO_PLATFORM.getRelationship(context),
                PropertyUtil.getSchemaProperty(context, productCategoryPlatformType),
                false, true);

        Map<?, ?> tempMap;
        ObjectMapper objectMapper;
        for (Object eachObj : objectList) {
            tempMap = (Map<?, ?>) eachObj;
            objectMapper = new ObjectMapper();
            productCategoryPlatformBeansList.add(objectMapper.convertValue(tempMap, ProductCategoryPlatformBean.class));
        }
        logger.info("Connected Product Category Platform: " + productCategoryPlatformBeansList.size());
        return productCategoryPlatformBeansList;
    }

    /**
     * Method to get connected Product Technology Platform(s).
     *
     * @param formulationObj - DomainObject - formulation part domain object.
     * @return List<ProductTechnologyPlatformBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<ProductTechnologyPlatformBean> getConnectedProductTechnologyPlatforms(DomainObject formulationObj, String productTechnologyPlatformType) throws FrameworkException {
        List<ProductTechnologyPlatformBean> productTechnologyPlatformBeanList = new ArrayList<>();
        Context context = PhysChemContext.getContext();

        MapList objectList = getExpansionObjects(context,
                formulationObj,
                FormulationRelationshipConstant.DOCUMENT_TO_PLATFORM.getRelationship(context),
                PropertyUtil.getSchemaProperty(context, productTechnologyPlatformType),
                false, true);

        Map<?, ?> tempMap;
        ObjectMapper objectMapper;
        for (Object eachObj : objectList) {
            tempMap = (Map<?, ?>) eachObj;
            objectMapper = new ObjectMapper();
            productTechnologyPlatformBeanList.add(objectMapper.convertValue(tempMap, ProductTechnologyPlatformBean.class));
        }
        logger.info("Connected Product Technology Platform: " + productTechnologyPlatformBeanList.size());
        return productTechnologyPlatformBeanList;
    }

    /**
     * Method to convert FOP Map list to Bean Map
     *
     * @param objectList - MapList - List of object
     * @return Map<String, IFormulationPart>
     * @throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException  - exception
     * @since DSM 2018x.5
     */
    public Map<String, IFormulationPart> convertMapListToBeanMap(MapList objectList) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Map<String, IFormulationPart> dataMap = new HashMap<>();
        Instant startTime = Instant.now();
        FormulationPartService fopService = FormulationPartFactory.getFormulationPartService();
        Map<?, ?> tempMap;
        for (Object eachObj : objectList) {
            tempMap = (Map<?, ?>) eachObj;
            dataMap.put(generateNameRevisionKey(tempMap), fopService.getFormulationPart(tempMap));
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Converting MapList to Flat Map took|" + duration.toMillis() + " " + FormulationGeneralConstant.CONST_MILLI_SECONDS.getValue() + "|" + duration.getSeconds() + " " + FormulationGeneralConstant.CONST_SECONDS.getValue() + "|" + duration.toMinutes() + " " + FormulationGeneralConstant.CONST_MINUTES.getValue());
        return dataMap;
    }

    /**
     * Method to generate fop name and revision key
     *
     * @param map - Map<?, ?>
     * @return String
     * @since DSM 2018x.5
     */
    public String generateNameRevisionKey(Map<?, ?> map) {
        StringBuilder identifierBuilder = new StringBuilder();
        identifierBuilder.append(map.get(DomainConstants.SELECT_NAME));
        identifierBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue());
        identifierBuilder.append(map.get(DomainConstants.SELECT_REVISION));
        return identifierBuilder.toString();
    }

    /**
     * Method to get the Product Form connect tor the FOP
     *
     * @param formulationObj - DomainObject
     * @return List<ProductFormBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<ProductFormBean> getConnectedProductForms(DomainObject formulationObj) throws FrameworkException {
        Context context = PhysChemContext.getContext();
        MapList objectList = formulationObj.getRelatedObjects(context, // context
                FormulationRelationshipConstant.OWNING_PRODUCT_LINE.getRelationship(context), // relationship pattern
                FormulationTypeConstant.PLI_PRODUCT_FORM.getType(context), // type pattern
                businessObjectSelectList, // object select
                StringList.create(DomainConstants.SELECT_RELATIONSHIP_ID), // relationship select
                true, // getTo
                false, // getFrom
                (short) 1, // recurseToLevel
                DomainConstants.EMPTY_STRING, // object where
                DomainConstants.EMPTY_STRING, // relationship where
                0); // limit

        List<ProductFormBean> productFormList = new ArrayList<>();
        Map<?, ?> tempMap;
        ObjectMapper objectMapper;
        for (Object eachObj : objectList) {
            tempMap = (Map<?, ?>) eachObj;
            objectMapper = new ObjectMapper();
            productFormList.add(objectMapper.convertValue(tempMap, ProductFormBean.class));
        }
        logger.info("Connected Product Forms: " + productFormList.size());
        return productFormList;
    }

    /**
     * Method to get Formulation Part Attribute Bus Selects.
     *
     * @return StringList
     * @since DSM 2018x.5
     */
    public StringList getFormulationPartBusAttributeSelects() {
        StringList busSelects = new StringList();
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_REVISION);
        busSelects.addElement(DomainConstants.SELECT_POLICY);
        busSelects.addElement(DomainConstants.SELECT_VAULT);
        busSelects.addElement(DomainConstants.SELECT_CURRENT);
        FormulationAttributeConstant[] attributes = FormulationAttributeConstant.values();
        for (FormulationAttributeConstant attribute : attributes) {
            busSelects.addElement(attribute.getAttributeSelect(PhysChemContext.getContext()));
        }
        return busSelects;
    }

    /**
     * Method to get
     *
     * @param physChemAttributeList - List<PhysChem>
     * @throws MatrixException - exception
     * @since DSM 2018x.5
     */
    public void getAttributeOrPicklistInfoInitialized(List<PhysChem> physChemAttributeList) throws MatrixException {
        Map<String, Map<String, Object>> attributesSchemaMap = getAttributeSchemaInfoMap();
        Map<String, Map<String, StringList>> picklistColumnsMap = getPicklistInfoMap(physChemAttributeList);
        String select;
        String columnType;
        Map<String, Object> schemaMap;
        Map<String, StringList> picklistColumnMap;
        String picklistName;
        for (PhysChem physChem : physChemAttributeList) {
            select = physChem.getSelect();
            columnType = physChem.getType();
            if (FormulationGeneralConstant.CONST_BASIC.getValue().equalsIgnoreCase(columnType)) {
                // do-nothing.
                logger.info("Its a basic field like type, name revision etc..");
            }
            if (FormulationGeneralConstant.CONST_ATTR.getValue().equalsIgnoreCase(columnType) && UIUtil.isNotNullAndNotEmpty(select) && attributesSchemaMap.containsKey(select)) {
                schemaMap = attributesSchemaMap.get(select);
                physChem.setAttributeName((String) schemaMap.get(FormulationGeneralConstant.CONST_ATTR_NAME.getValue()));
                physChem.setAttributeSelect((String) schemaMap.get(FormulationGeneralConstant.CONST_ATTR_SELECT.getValue()));
                physChem.setAttributeDefaultValue((String) schemaMap.get(FormulationGeneralConstant.CONST_ATTR_DEFAULT_VAL.getValue()));
                physChem.setAttributeRanges((StringList) schemaMap.get(FormulationGeneralConstant.CONST_ATTR_CHOICES.getValue()));
                physChem.setAttributeMaxLength((int) schemaMap.get(FormulationGeneralConstant.CONST_ATTR_MAX_LENGTH.getValue()));
                physChem.setAttributeMultiline((boolean) schemaMap.get(FormulationGeneralConstant.CONST_IS_ATTR_MULTI_LINE.getValue()));
                physChem.setAttributeSingleValue((boolean) schemaMap.get(FormulationGeneralConstant.CONST_IS_ATTR_SINGLE_VAL.getValue()));
                physChem.setAttributeMultiValue((boolean) schemaMap.get(FormulationGeneralConstant.CONST_IS_ATTR_MULTI_VAL.getValue()));
                physChem.setAttributeRangeValue((boolean) schemaMap.get(FormulationGeneralConstant.CONST_IS_ATTR_RANGE_VAL.getValue()));
            }
            if (physChem.isPicklist()) {
                picklistName = physChem.getPicklistName();
                if (UIUtil.isNotNullAndNotEmpty(picklistName) && picklistColumnsMap.containsKey(picklistName)) {
                    picklistColumnMap = picklistColumnsMap.get(picklistName);
                    physChem.setPicklistNames(picklistColumnMap.get(FormulationGeneralConstant.CONST_PICKLIST_NAMES.getValue()));
                    physChem.setPicklistRevisions(picklistColumnMap.get(FormulationGeneralConstant.CONST_PICKLIST_REVISIONS.getValue()));
                    physChem.setPicklistIds(picklistColumnMap.get(FormulationGeneralConstant.CONST_PICKLIST_IDS.getValue()));
                }
            }
        }
    }

    /**
     * Method to get Attribute schema map information
     *
     * @return Map<String, Map < String, Object>>
     * @throws MatrixException - exception
     * @since DSM 2018x.5
     */
    public Map<String, Map<String, Object>> getAttributeSchemaInfoMap() throws MatrixException {
        Context context = PhysChemContext.getContext();
        Map<String, Map<String, Object>> attributesSchemaMap = new HashMap<>();
        FormulationAttributeConstant[] attributes = FormulationAttributeConstant.values();
        Map<String, Object> schemaMap;
        for (FormulationAttributeConstant attribute : attributes) {
            schemaMap = new HashMap<>();
            schemaMap.put(FormulationGeneralConstant.CONST_ATTR_NAME.getValue(), attribute.getAttribute(context));
            schemaMap.put(FormulationGeneralConstant.CONST_ATTR_SELECT.getValue(), attribute.getAttributeSelect(context));
            schemaMap.put(FormulationGeneralConstant.CONST_ATTR_CHOICES.getValue(), attribute.getChoices(context));
            schemaMap.put(FormulationGeneralConstant.CONST_ATTR_DEFAULT_VAL.getValue(), attribute.getDefaultValue(context));
            schemaMap.put(FormulationGeneralConstant.CONST_ATTR_MAX_LENGTH.getValue(), attribute.getMaxLength(context));
            schemaMap.put(FormulationGeneralConstant.CONST_IS_ATTR_MULTI_LINE.getValue(), attribute.isMultiLine(context));
            schemaMap.put(FormulationGeneralConstant.CONST_IS_ATTR_SINGLE_VAL.getValue(), attribute.isSingleVal(context));
            schemaMap.put(FormulationGeneralConstant.CONST_IS_ATTR_MULTI_VAL.getValue(), attribute.isMultiVal(context));
            schemaMap.put(FormulationGeneralConstant.CONST_IS_ATTR_RANGE_VAL.getValue(), attribute.isRangeVal(context));
            attributesSchemaMap.put(attribute.toString(), schemaMap);
        }
        return attributesSchemaMap;
    }

    /**
     * Method to get Pick List Information
     *
     * @param physChemList - List<PhysChem> - Bean object
     * @return Map<String, Map < String, Object>>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public Map<String, Map<String, StringList>> getPicklistInfoMap(List<PhysChem> physChemList) throws FrameworkException {
        Map<String, Map<String, StringList>> picklistColumnsMap = new HashMap<>();
        StringList objectSelects = new StringList();
        objectSelects.addElement(DomainConstants.SELECT_TYPE);
        objectSelects.addElement(DomainConstants.SELECT_NAME);
        objectSelects.addElement(DomainConstants.SELECT_REVISION);
        objectSelects.addElement(DomainConstants.SELECT_ID);

        Iterator<PhysChem> columnIterator = physChemList.iterator();
        PhysChem physChem;
        String picklistName;
        while (columnIterator.hasNext()) {
            physChem = columnIterator.next();
            if (physChem.isPicklist()) {
                picklistName = physChem.getPicklistName();
                if (UIUtil.isNotNullAndNotEmpty(picklistName)) {
                    logger.info("Search Picklist objects of type: " + picklistName);
                    picklistColumnsMap.put(picklistName, getPicklistInfo(picklistName, DomainConstants.EMPTY_STRING, objectSelects));
                } else {
                    logger.error(physChem.getName() + " - column - 'picklist' setting is 'true' in XML but 'picklistName' setting is blank");
                }
            }
        }
        return picklistColumnsMap;
    }

    /**
     * Method to find Pick List value
     *
     * @param typePattern   - String - types
     * @param where         - String - where clause
     * @param objectSelects - StringList - object select
     * @return Map<String, Map < String, Object>>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public MapList findPicklist(String typePattern, String where, StringList objectSelects) throws FrameworkException {
        Context context = PhysChemContext.getContext();
        StringBuffer sbWhere = new StringBuffer();
        if (FormulationTypeConstant.PLI_PRODUCT_FORM.getType(context).equals(typePattern)) {
            String state = FormulationStateConstant.PICKLIST_ITEM_ACTIVE.getState(context, FormulationPolicyConstant.PICKLIST_ITEM.getPolicy(context));
            sbWhere.append(FormulationAttributeConstant.PRODUCT_TYPE.getAttributeSelect(context)).append(FormulationGeneralConstant.PICK_LIST_WHERE_FORMULATED_AND_CURRENT.getValue()).append(state);
        }
        return DomainObject.findObjects(
                context,//Context context,
                typePattern, //String typePattern,
                DomainConstants.QUERY_WILDCARD, //String namePattern,
                DomainConstants.QUERY_WILDCARD, //String revPattern,
                DomainConstants.QUERY_WILDCARD, //String ownerPattern,
                DomainConstants.QUERY_WILDCARD, //String vaultPattern,
                sbWhere.toString(), //String whereExpression,
                false, //boolean expandType,
                objectSelects); //StringList objectSelects)
    }

    /**
     * Method to get pick list information
     *
     * @param typePattern   - String - types
     * @param where         - String - where clause
     * @param objectSelects - StringList - object select
     * @return Map<String, StringList>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public Map<String, StringList> getPicklistInfo(String typePattern, String where, StringList objectSelects) throws FrameworkException {
        MapList pickList = findPicklist(typePattern, where, objectSelects);

        Map<String, StringList> picklistInfo = new HashMap<>();
        StringList nameList = new StringList();
        StringList revisionList = new StringList();
        StringList idList = new StringList();

        if (pickList != null && !pickList.isEmpty()) {
            logger.info(typePattern + " - Number of Picklist objects found:" + pickList.size());
            Iterator<?> itr = pickList.iterator();
            Map<?, ?> tempMap;
            while (itr.hasNext()) {
                tempMap = (Map<?, ?>) itr.next();
                nameList.addElement((String) tempMap.get(DomainConstants.SELECT_NAME));
                revisionList.addElement((String) tempMap.get(DomainConstants.SELECT_REVISION));
                idList.addElement((String) tempMap.get(DomainConstants.SELECT_ID));
            }
        }
        picklistInfo.put(FormulationGeneralConstant.CONST_PICKLIST_NAMES.getValue(), nameList);
        picklistInfo.put(FormulationGeneralConstant.CONST_PICKLIST_REVISIONS.getValue(), revisionList);
        picklistInfo.put(FormulationGeneralConstant.CONST_PICKLIST_IDS.getValue(), idList);
        return picklistInfo;
    }

    /**
     * Method to search given Business Area(s) in database and return list of bean objects.
     *
     * @param businessAreaNames - String - pipe separated Business Area(s) name.
     * @return List<BusinessAreaBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<BusinessAreaBean> getBusinessAreaBean(String businessAreaType, String businessAreaNames) throws FrameworkException {
        List<BusinessAreaBean> businessAreaBeanList = new ArrayList<>();
        if (UIUtil.isNotNullAndNotEmpty(businessAreaNames)) {

            MapList objectList = searchObjects(PhysChemContext.getContext(),
                    businessAreaType,
                    String.join(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue(), StringUtil.split(businessAreaNames, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue())));

            Map<?, ?> tempMap;
            ObjectMapper objectMapper;
            for (Object eachObj : objectList) {
                tempMap = (Map<?, ?>) eachObj;
                objectMapper = new ObjectMapper();
                businessAreaBeanList.add(objectMapper.convertValue(tempMap, BusinessAreaBean.class));
            }
        }
        logger.info("Search Business Area Found: " + businessAreaBeanList.size());
        return businessAreaBeanList;
    }

    /**
     * Method to search given Product Category Platform(s) in database and return list of bean objects.
     *
     * @param productCategoryPlatformNames - String - pipe separated Product Category Platform(s) name
     * @return List<ProductCategoryPlatformBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<ProductCategoryPlatformBean> getProductCategoryPlatformBean(String productCategoryPlatformType, String productCategoryPlatformNames) throws FrameworkException {
        List<ProductCategoryPlatformBean> productCategoryPlatformList = new ArrayList<>();
        if (UIUtil.isNotNullAndNotEmpty(productCategoryPlatformNames)) {
            MapList objectList = searchObjects(PhysChemContext.getContext(),
                    productCategoryPlatformType,
                    String.join(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue(), StringUtil.split(productCategoryPlatformNames, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue())));

            Map<?, ?> tempMap;
            ObjectMapper objectMapper;
            for (Object eachObj : objectList) {
                tempMap = (Map<?, ?>) eachObj;
                objectMapper = new ObjectMapper();
                productCategoryPlatformList.add(objectMapper.convertValue(tempMap, ProductCategoryPlatformBean.class));
            }
        }
        logger.info("Search Product Category Platform Found: " + productCategoryPlatformList.size());

        return productCategoryPlatformList;
    }

    /**
     * Method to search given Product Technology Platform(s) in database and return list of bean objects.
     *
     * @param productTechnologyPlatformNames - String - pipe separated Product Technology Platform(s) name
     * @return List<ProductTechnologyPlatformBean>
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public List<ProductTechnologyPlatformBean> getProductTechnologyPlatformBean(String productTechnologyPlatformType, String productTechnologyPlatformNames) throws FrameworkException {

        List<ProductTechnologyPlatformBean> productTechnologyPlatformBeanList = new ArrayList<>();
        if (UIUtil.isNotNullAndNotEmpty(productTechnologyPlatformNames)) {
            MapList objectList = searchObjects(PhysChemContext.getContext(),
                    productTechnologyPlatformType,
                    String.join(FormulationGeneralConstant.CONST_SYMBOL_COMMA.getValue(), StringUtil.split(productTechnologyPlatformNames, FormulationGeneralConstant.CONST_SYMBOL_PIPE.getValue())));

            Map<?, ?> tempMap;
            ObjectMapper objectMapper;
            for (Object eachObj : objectList) {
                tempMap = (Map<?, ?>) eachObj;
                objectMapper = new ObjectMapper();
                productTechnologyPlatformBeanList.add(objectMapper.convertValue(tempMap, ProductTechnologyPlatformBean.class));
            }
        }
        logger.info("Search Product Technology Platform Found: " + productTechnologyPlatformBeanList.size());
        return productTechnologyPlatformBeanList;
    }

    /**
     * Method to get connected Product Platform(s) value.
     *
     * @param busObj          - DomainObject - formulation part domain object.
     * @param strRelationship - String
     * @param strType         - String
     * @param getTo         - boolean
     * @param getFrom         - boolean
     * @return MapList
     * @throws FrameworkException
     * @since DSM 2018x.5
     */
    public MapList getExpansionObjects(Context context, DomainObject busObj, String strRelationship, String strType, boolean getTo, boolean getFrom) throws FrameworkException {
        return busObj.getRelatedObjects(context, //context
                strRelationship, // relationship pattern
                strType, // type pattern
                businessObjectSelectList, // object select
                DomainConstants.EMPTY_STRINGLIST, // relationship select
                getTo, // getTo
                getFrom, // getFrom
                (short) 1, // recurseToLevel
                DomainConstants.EMPTY_STRING, // object where
                DomainConstants.EMPTY_STRING, // relationship where
                0);// limit

    }

    /**
     * Method to find connected object to formulation part.
     *
     * @param context - context
     * @param strType - String
     * @param strName - String
     * @return MapList
     * @throws FrameworkException
     * @since DSM 2018x.5
     */
    public MapList searchObjects(Context context, String strType, String strName) throws FrameworkException {
        return DomainObject.findObjects(
                context,//Context context,
                PropertyUtil.getSchemaProperty(context, strType), //String typePattern,
                strName, //String namePattern,
                DomainConstants.QUERY_WILDCARD, //String revPattern,
                DomainConstants.QUERY_WILDCARD, //String ownerPattern,
                context.getVault().getName(), //String vaultPattern,
                DomainConstants.EMPTY_STRING, //String whereExpression,
                false,                    //boolean expandType,
                businessObjectSelectList);   //StringList objectSelects
    }
}
