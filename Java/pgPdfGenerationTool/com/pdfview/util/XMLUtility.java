/*
Java File Name: XMLUtility
Clone From/Reference: NA
Purpose: This file is used to  Mashalls a JAXB object into an XML string
*/

package com.pdfview.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

//22x Upgrade changes start
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
//22x Upgrade changes end

import com.pdfview.exception.PDFToolCustomException;
import com.pg.v3.custom.pgV3Constants;

public class XMLUtility {

	/**
	 * @description: This method Mashalls a JAXB object into an XML string
	 * 
	 * @param object
	 * @return String
	 * @throws JAXBException
	 * @throws PDFToolCustomException
	 */
	public static String generateXMLString(Object object) throws JAXBException, PDFToolCustomException {
		JAXBContext carContext = JAXBContext.newInstance(object.getClass());
		Marshaller marshaller = carContext.createMarshaller();

		StringWriter sw = new StringWriter();
		
		//22x Upgrade changes start
		//	CDataContentHandler serializer = new CDataContentHandler(sw);
	 	DOMImplementationRegistry registry = null;
		DOMImplementationLS impls = null;

		//Prepare the output
		LSOutput outputFormat = null;
		try {
			registry = DOMImplementationRegistry.newInstance();
			impls =  (DOMImplementationLS)registry.getDOMImplementation("LS");

			//Prepare the output
			outputFormat = impls.createLSOutput();
			outputFormat.setCharacterStream(sw);
			outputFormat.setEncoding("UTF-8");
	    
			marshaller.marshal(object, outputFormat.getByteStream());
			
		} catch (Exception e) {
			String sObjectPackage = StringUtils.substringBeforeLast(object.getClass().getName(), pgV3Constants.SYMBOL_DOT);
			String sObjectType = StringUtils.substringAfterLast(object.getClass().getName(), pgV3Constants.SYMBOL_DOT);
			QName qName = new QName(sObjectPackage, sObjectType);
			JAXBElement root = new JAXBElement(qName, object.getClass(), object);
			try {
				marshaller.marshal(object, outputFormat.getByteStream());
		
			} catch (Exception exception) {
				throw new PDFToolCustomException(exception.getMessage(), "E600");

			}
		}
		//22x Upgrade changes end
		
		String results = sw.toString();
		return results;

	}

	/**
	 * @description: This method used for Pretty Formatting
	 * 
	 * @param sInpinputut
	 * @return String
	 * @throws TransformerException
	 */
	public static String prettyFormat(String input) throws TransformerException {

		Source xmlInput = new StreamSource(new StringReader(input));
		StringWriter stringWriter = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringWriter);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		// transformerFactory.setAttribute("indent-number", 4);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(xmlInput, xmlOutput);
		String results = xmlOutput.getWriter().toString();
		if (results.startsWith("<?")) {

			results = StringUtils.replace(results, "><", ">\n<", 1);

			return results;
		}
		return input;
	}

	/**
	 * @description: This method used to unmarshall XML file
	 * 
	 * @param sConfigFile
	 * @param clazz
	 * @return Object
	 * @throws JAXBException
	 */
	public static Object unmarshallXMLFile(String sConfigFile, Class clazz) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		File elementsXML = new File(sConfigFile);
		Object object = unmarshaller.unmarshal(elementsXML);
		return object;

	}

	/**
	 * @description: This method used to Marshall XML file
	 * 
	 * @param sFilename
	 * @param sFilename
	 * @return void
	 * @throws JAXBException
	 * @throws TransformerException
	 * @throws IOException
	 * @throws PDFToolCustomException
	 */
	public static void marshallXMLFile(String sFilename, Object object)
			throws JAXBException, TransformerException, IOException, PDFToolCustomException {
		File fTemp = new File(sFilename);
		FileUtils.writeStringToFile(fTemp, XMLUtility.prettyFormat(XMLUtility.generateXMLString(object)));

	}

}
