/*
Java File Name: DataHandlerConfigUtility
Clone From/Reference: NA
Purpose: This file is used to get the details of Component
*/

package com.pdfview.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.log.SysoLogger;
import com.matrixone.apps.awl.util.UIUtil;
import com.pdfview.combinedcomponent.definition.Component;
import com.pdfview.combinedcomponent.definition.Components;
import com.pdfview.combinedcomponent.definition.Elements;
import com.pdfview.combinedelement.definition.DetailedElement;
import com.pdfview.combinedelement.definition.DetailedElements;
import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.exception.PDFToolFieldIsListException;
import com.pg.v3.custom.pgV3Constants;

public class PDFToolConfigUtility {
	public static List<String> lComponentsCreated = new ArrayList<String>();

	/**
	 * @description: Method to load a hashmap with all elementDetail configuration
	 *               files specified in a Configuration XML file -Checks for
	 *               duplicate element names and places errors in an entry named
	 *               "ERRORS"
	 * 
	 * @param sPath
	 * @param sElementConfigFiles
	 * @return HashMap
	 * @throws PDFToolCustomException
	 */
	public static HashMap<String, Object> getDetailedElementHashMap(String sPath, String sElementConfigFiles)
			throws PDFToolCustomException {
		HashMap<String, Object> results = new HashMap<String, Object>();
		results.put("ERRORS", new StringBuffer());
		try {

			String[] saConfigFiles = sElementConfigFiles.split(pgV3Constants.SYMBOL_COMMA);

			JAXBContext jc = JAXBContext.newInstance(DetailedElements.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			int size = saConfigFiles.length;
			for (int i = 0; i < size; i++) {
				File elementsXML = new File(sPath + "/ConfigurationFiles/" + saConfigFiles[i].trim());
				DetailedElements elements = (DetailedElements) unmarshaller.unmarshal(elementsXML);

				List<DetailedElement> element = elements.getDetailedElement();
				Iterator<DetailedElement> itrElements = element.iterator();
				while (itrElements.hasNext()) {
					DetailedElement deTemp = itrElements.next();
					String sKey = deTemp.getName();
					if (results.containsKey(sKey)) {
						StringBuffer sbTemp = (StringBuffer) results.get("ERRORS");
						sbTemp.append(sKey).append(" was already loaded into configuration before ");
						sbTemp.append(saConfigFiles[i]).append(" tried to load it");
						results.put("ERRORS", sbTemp);
					} else {
						results.put(sKey, deTemp);
					}

				}

			}

		} catch (Exception exception) {
			exception.printStackTrace();
			String errorcode = DHUtility.getErrorCode(exception);
			throw new PDFToolCustomException(exception.getMessage(), errorcode);
		}

		StringBuffer sbErrors = (StringBuffer) results.get("ERRORS");
		if (sbErrors.length() > 0) {
			results.put("ERRORS", sbErrors.toString());

		} else {
			results.remove("ERRORS");
		}

		return results;
	}

	/**
	 * @description: Method to load a hashmap with all detailedComponent
	 *               configuration files specified in a Configuration XML file
	 *               -Checks for duplicate component names and places errors in an
	 *               entry named "ERRORS"
	 * 
	 * @param sPath
	 * @param sConfigFile
	 * @return HashMap
	 * @throws PDFToolCustomException
	 */

	public static HashMap<String, Object> getComponentHashMap(String sPath, String sConfigFile)
			throws PDFToolCustomException {
		HashMap<String, Object> results = new HashMap<String, Object>();
		results.put("ERRORS", new StringBuffer());
		try {
			JAXBContext jc = JAXBContext.newInstance(Components.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			File elementsXML = new File(sPath + "/ConfigurationFiles/" + sConfigFile);
			Components components = (Components) unmarshaller.unmarshal(elementsXML);
			results.put("elementConfigurationFiles", components.getElementConfigurationFiles());
			results.put("enoviaVault", components.getEnoviaVault());

			List<Component> componentList = components.getComponent();

			for (Component component : componentList) {
				String sKey = component.getName();
				if (results.containsKey(sKey)) {
					StringBuffer sbTemp = (StringBuffer) results.get("ERRORS");
					sbTemp.append(sKey).append(" was already loaded into configuration before ");
					sbTemp.append(sConfigFile).append(" tried to load it");
					results.put("ERRORS", sbTemp);
				} else {
					results.put(sKey, component);
				}

			}

		} catch (Exception exception) {
			String errorcode = DHUtility.getErrorCode(exception);
			exception.printStackTrace();
			throw new PDFToolCustomException(exception.getMessage(), errorcode);
		}

		StringBuffer sbErrors = (StringBuffer) results.get("ERRORS");
		if (sbErrors.length() > 0) {
			results.put("ERRORS", sbErrors.toString());
		} else {
			results.remove("ERRORS");
		}
		return results;
	}

}