package com.pg.dsm.rollup_event.common.resources;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.dsm.rollup_event.common.config.phys_chem.PhysChemAttribute;
import com.pg.dsm.rollup_event.common.config.phys_chem.PhysChemConfig;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;

import matrix.db.AttributeType;
import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;

public class RollupPhysChemConfigurator {
    PhysChemConfig physChemConfig;

    private RollupPhysChemConfigurator(Builder builder) {
        this.physChemConfig = builder.physChemConfig;
    }

    public PhysChemConfig getPhysChemObject() {
        return physChemConfig;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        PhysChemConfig physChemConfig;
        Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public RollupPhysChemConfigurator build() {
            String xmlContent = readPageAsString();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(PhysChemConfig.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                this.physChemConfig = (PhysChemConfig) unmarshaller.unmarshal(new StringReader(xmlContent));
                loadAttributeInfo();
            } catch (JAXBException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
            return new RollupPhysChemConfigurator(this);
        }

        public String readPageAsString() {
            String content = DomainConstants.EMPTY_STRING;
            try {
                Page page = new Page(RollupConstants.Basic.ROLLUP_PHYS_CHEM_CONFIG_PAGE.getValue());
                page.open(context);
                content = page.getContents(context);
                page.close(context);
            } catch (MatrixException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
            return content;
        }

        private void loadAttributeInfo() {
            if (null != physChemConfig) {
                List<PhysChemAttribute> physChemAttributes = physChemConfig.getPhysChemAttributes();
                String schemaType;
                String schemaName;
                AttributeType attributeType;
                for (PhysChemAttribute physChemAttribute : physChemAttributes) {
                    schemaType = physChemAttribute.getSchemaType();
                    schemaName = physChemAttribute.getSchemaName();
                    physChemAttribute.setAttributeActualName(PropertyUtil.getSchemaProperty(context, schemaName));
                    if (RollupConstants.Basic.SCHEMA_TYPE_ATTRIBUTE.getValue().equals(schemaType)) {

                        // if flag (updateCustomDefaultValue) is false - meaning update db value as default value
                        if (!physChemAttribute.isUpdateCustomDefaultValue()) {
                            attributeType = new AttributeType(physChemAttribute.getAttributeActualName());
                            try {
                                physChemAttribute.setAttributeDefaultValue(attributeType.getDefaultValue(context));
                                physChemAttribute.setAttributeSelectExpression(DomainObject.getAttributeSelect(physChemAttribute.getAttributeActualName())
                                        + RollupConstants.Basic.SUFFIX_INPUT_VALUE.getValue());
                            } catch (MatrixException e) {
                                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
                            }
                        }
                    }
                }
            }
        }
    }
}
