package com.pg.dsm.rollup_event.rollup.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.util.StringList;

public class RollupUtil {

    private static final Logger logger = Logger.getLogger(RollupUtil.class.getName());

    public static Rollup getRollup(String rollupIdentifier, Config rollupRuleConfiguration) {
        Rollup rollupRule = null;
        List<Rollup> rollupList = rollupRuleConfiguration.getRollup();
        for (Rollup rollup : rollupList) {
            if (rollup.getEventName().equalsIgnoreCase(rollupIdentifier)
                    || rollup.getIdentifier().equalsIgnoreCase(rollupIdentifier)
                    || rollup.getAttributeName().equalsIgnoreCase(rollupIdentifier)) {
                rollupRule = rollup;
            }
        }
        return rollupRule;
    }

    public static Rule getRollupRule(Rollup rollupConfig, String ruleIdentifier) {
        Rule rollupRule = null;
        List<Rule> rules = rollupConfig.getRules();
        for (Rule rule : rules) {
            if (rule.getIdentifier().equalsIgnoreCase(ruleIdentifier)) {
                rollupRule = rule;
            }
        }
        return rollupRule;
    }

    public static String getProductPartTypePattern() {
        StringBuilder sbProductPartsType = new StringBuilder();
        sbProductPartsType.append(pgV3Constants.TYPE_DEVICEPRODUCTPART);
        sbProductPartsType.append(pgV3Constants.SYMBOL_COMMA);
        sbProductPartsType.append(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
        sbProductPartsType.append(pgV3Constants.SYMBOL_COMMA);
        sbProductPartsType.append(pgV3Constants.TYPE_FORMULATIONPART);
        return sbProductPartsType.toString();
    }

    public static String getNotObsoleteCondition() {
        StringBuilder sbNotObsolete = new StringBuilder();
        sbNotObsolete.append(RollupConstants.Basic.SYMBOL_NOT.getValue())
                .append(pgV3Constants.CONST_OPEN_BRACKET)
                .append(DomainConstants.SELECT_CURRENT)
                .append(pgV3Constants.CONST_SYMBOL_EQUAL)
                .append(pgV3Constants.STATE_OBSOLETE)
                .append(pgV3Constants.CONST_CLOSED_BRACKET);

        return sbNotObsolete.toString();
    }

    public static StringList getObjectSelects() {

        StringList slObjectSelects = new StringList(3);
        slObjectSelects.add(DomainConstants.SELECT_ID);
        slObjectSelects.add(DomainConstants.SELECT_TYPE);
        slObjectSelects.add(DomainConstants.SELECT_NAME);

        return slObjectSelects;
    }

    /**
     * This method returns true if provided input file is exits
     *
     * @param filePath - Holds input file path
     * @return boolean - True/False
     * This method is added by DSM (Sogeti) for defect# 43716
     */
    public static boolean isInputFileExist(String filePath) {
        boolean isFileExists = false;
        File file = new File(filePath);
        if (file.exists()) {
            isFileExists = true;
            boolean isReadable = file.setReadable(true, false);

            boolean isWritable = file.setWritable(true, false);

            boolean isExecutable = file.setExecutable(true, false);

            logger.log(Level.INFO, "Readable, Writable, Executable access set: {0} {1} {2}", new Object[]{isReadable, isWritable, isExecutable});

        }
        return isFileExists;
    }

    /**
     * This method returns StringList of FPP Data
     *
     * @param filePath - Holds input file path
     * @throws IOException This method is added by DSM (Sogeti) for defect# 43716
     * @returns List holds FPP data
     */
    public static List<String> getInputFileContentAsList(String filePath) throws IOException {
        List<String> linesList = new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line -> linesList.add(line));
        } catch (IOException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return linesList;
    }

}
