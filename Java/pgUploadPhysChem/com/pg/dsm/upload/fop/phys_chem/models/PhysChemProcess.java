/*
 **   PhysChemProcess.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Class to update Formulation Part - Physical Chemical Properties.
 **
 */

package com.pg.dsm.upload.fop.phys_chem.models;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationRelationshipConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationTypeConstant;
import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChem;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChemBean;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysChemProcess {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    List<PhysChemBean> physChemBeanList;

    /**
     * Constructor
     *
     * @param physChemBeanList - List<PhysChemBean> - beans object list
     * @since DSM 2018x.5
     */
    public PhysChemProcess(List<PhysChemBean> physChemBeanList) {
        this.physChemBeanList = physChemBeanList;
    }

    /**
     * Method to update physical chemical bean object.
     *
     * @return void
     * @throws MatrixException 
     * @since DSM 2018x.5
     */
    public void updatePhysChem() throws MatrixException {
        try {
            Context context = PhysChemContext.getContext();
            DomainObject dObj = DomainObject.newInstance(context);
            Map attributeToUpdateMap;
            IFormulationPart formulationPartBean;
            for (PhysChemBean physChemBean : physChemBeanList) {
                attributeToUpdateMap = getAttributeToUpdateMap(physChemBean);
                if (physChemBean.isBeanExist()) {
                    formulationPartBean = physChemBean.getFormulationPartBean();
                    dObj.setId(formulationPartBean.getId());
                    dObj.setAttributeValues(context, attributeToUpdateMap);
					//Modified as part of 2018x.6 - Starts
                    if(FormulationTypeConstant.ASSEMBLEDPRODUCTPART_PART.getType(context).equalsIgnoreCase(formulationPartBean.getType())) {
                    	updateProductForm(dObj, physChemBean, formulationPartBean.getProductForm(), formulationPartBean.getProductFormRelId());
                    }
					//Modified as part of 2018x.6 - Ends
                    logger.info("Processed - " + physChemBean.getFormulatedPartName() + " " + physChemBean.getRevision());
                }
            }
        } catch (FrameworkException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to update product form for APP type.
     *
     * @param doAPP DomainObject - app domain object
     * @param physChemBean PhysChemBean - physical chemical bean object
     * @param dbProductForm String - product form from db
     * @param strProductFormRelId String - product form relationship id
     * @return void
     * @throws MatrixException 
     * @since DSM 2018x.6
     */
    private void updateProductForm(DomainObject doAPP, PhysChemBean physChemBean, String dbProductForm, String strProductFormRelId) throws MatrixException {
    	Context context = PhysChemContext.getContext();
    	 List<PhysChem> physChemList = physChemBean.getPhysChem();
         String physChemAttributeName;
         DomainObject doProductForm;
         String excelProductForm;
         for (PhysChem physChem : physChemList) {
        	 physChemAttributeName = physChem.getName();
            if(UIUtil.isNotNullAndNotEmpty(physChemAttributeName) && FormulationGeneralConstant.PRODUCT_FORM_FIELD_NAME.getValue().equalsIgnoreCase(physChemAttributeName)) {
            	 excelProductForm = physChem.getProductForm();
             	 if(excelProductForm.equalsIgnoreCase(dbProductForm)) {
             		logger.info(physChemBean.getFormulatedPartName() + " " + physChemBean.getRevision()+" UI and excel Product Form are same");
             	 }
                 else {
                	 if(UIUtil.isNotNullAndNotEmpty(dbProductForm) && UIUtil.isNotNullAndNotEmpty(strProductFormRelId)) {
            			 DomainRelationship.disconnect(context, strProductFormRelId);
            			 logger.info("Disconnected "+ dbProductForm +" from " + physChemBean.getFormulatedPartName() + " " + physChemBean.getRevision());
                     }
                	 try {
                		 doProductForm = DomainObject.newInstance(context, new BusinessObject(FormulationTypeConstant.PLI_PRODUCT_FORM.getType(context), excelProductForm, FormulationGeneralConstant.CONST_SYMBOL_HYPHEN.getValue(), context.getVault().getName()));
                		 DomainRelationship.connect(context, doProductForm, FormulationRelationshipConstant.OWNING_PRODUCT_LINE.getRelationship(context), doAPP);
                		 logger.info("Connected "+ excelProductForm +" from " + physChemBean.getFormulatedPartName() + " " + physChemBean.getRevision());
					} catch (FrameworkException e) {
							logger.error(e.getMessage());
						}
                	 }
             	 break;
             }
         }
		
	}
	
	/**
     * Method to get attribute list to be update
     *
     * @param physChemBean PhysChemBean - physical chemical bean object
     * @return void
     * @since DSM 2018x.5
     */
    private Map getAttributeToUpdateMap(PhysChemBean physChemBean) {
        List<PhysChem> attributesAttributeList = physChemBean.getPhysChem();
        Map<String, String> attributeKeyValueMap = new HashMap<>();
        String physChemAttributeSelect;
        for (PhysChem physChem : attributesAttributeList) {
            physChemAttributeSelect = physChem.getSelect();
            // if field type is attribute and
            // select key-value is not empty (will be empty in Product Form case) and
            // if excel cell is not greyed-out
            if (UIUtil.isNotNullAndNotEmpty(physChemAttributeSelect) && FormulationGeneralConstant.CONST_ATTR.getValue().equals(physChem.getType()) && !physChem.isGreyedOut()) {
            	attributeKeyValueMap.put(physChem.getAttributeName(), physChem.getValue());
            } 
			//Modified as part of 2018x.6 defect#36784- Starts
			else if(UIUtil.isNotNullAndNotEmpty(physChemAttributeSelect) && FormulationGeneralConstant.CONST_ATTR.getValue().equals(physChem.getType()) && physChem.isGreyedOut()) {
            	attributeKeyValueMap.put(physChem.getAttributeName(), DomainConstants.EMPTY_STRING);
            } 
			//Modified as part of 2018x.6 defect#36784- Ends
        }
        return attributeKeyValueMap;
    }
}
