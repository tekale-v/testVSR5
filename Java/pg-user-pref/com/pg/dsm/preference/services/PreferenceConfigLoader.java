package com.pg.dsm.preference.services;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.config.xml.PreferenceConfig;
import com.pg.dsm.preference.enumeration.PreferenceConstants;

import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;

public class PreferenceConfigLoader {
    boolean isLoaded;
    String errorMessage;
    PreferenceConfig preferenceConfig;

    private PreferenceConfigLoader(Load load) {
        this.isLoaded = load.isLoaded;
        this.preferenceConfig = load.preferenceConfig;
        this.errorMessage = load.errorMessage;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public PreferenceConfig getPreferenceConfig() {
        return preferenceConfig;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Load {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        boolean isLoaded;
        PreferenceConfig preferenceConfig;
        String errorMessage;

        Context context;

        public Load(Context context) {
            this.context = context;
        }

        public PreferenceConfigLoader now() {
            try {
                loadPreference();
                this.isLoaded = Boolean.TRUE;
            } catch (JAXBException | MatrixException e) {
                this.isLoaded = Boolean.FALSE;
                this.errorMessage = e.getMessage();
                logger.log(Level.WARNING, "PreferenceConfig JAXB failed: " + e);
            }
            return new PreferenceConfigLoader(this);
        }

        String getPageContentAsString() throws MatrixException {
            String content = DomainConstants.EMPTY_STRING;
            boolean isPageOpen = false;
            Page page = null;
            try {
                page = new Page(PreferenceConstants.Basic.USER_PREFERENCE_CONFIG_PAGE.get());
                page.open(this.context);
                content = page.getContents(this.context);
                isPageOpen = true;
            } catch (MatrixException e) {
                logger.log(Level.WARNING, "Exception while reading page:", e);
                throw e;
            } finally {
                if (isPageOpen && null != page) {
                    page.close(this.context);
                }
            }
            return content;
        }

        void loadPreference() throws JAXBException, MatrixException {
            Instant startTime = Instant.now();
            try {
                String xmlContent = getPageContentAsString();
                JAXBContext jaxbContext = JAXBContext.newInstance(PreferenceConfig.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                this.preferenceConfig = (PreferenceConfig) unmarshaller.unmarshal(new StringReader(xmlContent));
            } catch (JAXBException | MatrixException e) {
                throw e;
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.log(Level.INFO, "PreferenceConfig - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        }
    }
}
