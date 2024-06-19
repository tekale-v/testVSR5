/*
Java File Name: RegistryUtility
Clone From/Reference: NA
Purpose: This file is used to loads a registry file from registry.java
and then unmarshalls it into JAXB classes
*/

package com.pdfview.util;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.registry.RegisteredItem;
import com.pdfview.registry.Registry;

import matrix.db.Context;

public class RegistryUtility {

	/**
	 * @description: This method used to load registry
	 * 
	 * @param registryFile
	 * @return Registry
	 * @throws PDFToolCustomException
	 */
	private static Registry loadRegistry(String registryFile) throws PDFToolCustomException {
		Registry results = null;

		Class clazz = null;
		try {
			clazz = ReflectionUtility.getClass("com.pdfview.registry.Registry");

			JAXBContext jc = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			// Load each XML file into a jaxb class
			File elementsXML = new File(registryFile);
			results = (Registry) unmarshaller.unmarshal(elementsXML);
		} catch (ClassNotFoundException | JAXBException exception) {
			String errorcode = DHUtility.getErrorCode(exception);
			throw new PDFToolCustomException(exception.getMessage(), errorcode);
		}
		return results;
	}

	/**
	 * @description: This method used to finds an entry from a list of entries in
	 *               the registry
	 * 
	 * @param sConfigurationPacketName
	 * @return RegisteredItems
	 * @throws PDFToolCustomException
	 */
	public static RegisteredItem findEntry(Context context,String sConfigurationPacketName) throws PDFToolCustomException {
		RegisteredItem results = null;
		String sAppPath = AppPathUtility.getConfigDirectory(context);
		String sRegistryFile = sAppPath + "/ConfigurationFiles/Registry.xml";
		Registry registry = loadRegistry(sRegistryFile);

		List<RegisteredItem> listItems = registry.getRegisteredItems().getRegisteredItem();
		for (RegisteredItem registeredItem : listItems) {

			if (registeredItem.getConfigurationPacketName().equals(sConfigurationPacketName)) {
				results = registeredItem;
				break;
			}
		}

		return results;
	}
}
