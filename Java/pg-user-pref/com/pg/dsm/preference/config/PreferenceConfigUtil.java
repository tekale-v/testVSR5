package com.pg.dsm.preference.config;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.pg.dsm.preference.config.xml.PreferenceConfig;

public class PreferenceConfigUtil {
    private static final Logger logger = Logger.getLogger(PreferenceConfigUtil.class.getName());

    /**
     * @param xmlContent
     * @return
     * @throws JAXBException
     */
    public PreferenceConfig getUserPreferenceConfig(String xmlContent) throws JAXBException {
        Instant startTime = Instant.now();
        PreferenceConfig preferenceConfig = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PreferenceConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            preferenceConfig = (PreferenceConfig) unmarshaller.unmarshal(new StringReader(xmlContent));
        } catch (JAXBException e) {
            throw e;
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "PreferenceConfig - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return preferenceConfig;
    }
}
