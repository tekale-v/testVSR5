package com.pg.dsm.rollup_event.common.resources;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;

import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;

public class RollupRuleConfigurator {

    Config config;

    private RollupRuleConfigurator(Builder builder) {
        this.config = builder.config;
    }

    public Config getConfig() {
        return config;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Config config;
        Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public RollupRuleConfigurator build() {
            String xmlContent = readPageAsString();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                this.config = (Config) unmarshaller.unmarshal(new StringReader(xmlContent));
            } catch (JAXBException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
            return new RollupRuleConfigurator(this);
        }

        public String readPageAsString() {
            String content = DomainConstants.EMPTY_STRING;
            try {
                Page page = new Page(RollupConstants.Basic.ROLLUP_RULE_CONFIG_XML_PAGE.getValue());
                page.open(context);
                content = page.getContents(context);
                page.close(context);
            } catch (MatrixException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
            return content;
        }
    }
}
