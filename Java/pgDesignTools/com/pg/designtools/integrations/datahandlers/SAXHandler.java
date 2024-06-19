package com.pg.designtools.integrations.datahandlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.JDOMException;
import com.matrixone.jdom.input.SAXBuilder;
import com.matrixone.jdom.output.Format;
import com.matrixone.jdom.output.XMLOutputter;
import com.matrixone.util.MxXMLUtils;
import com.pg.designtools.datamanagement.DataConstants;

public class SAXHandler {
	Document docXML;
	File fMarkupXML;
	
	/**
	 * Save TOPS XML - This method determines the root element from Reference xml
	 * @param fileRoot
	 * @param filename
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public Element getRootElement(File fileRoot, String filename) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder(false);
		fMarkupXML = new File(fileRoot, filename);

		docXML = builder.build(fMarkupXML);
		return docXML.getRootElement();

	}
	
	/**
	 * Save TOPS XML - This method Writes updated info into the file
	 * @throws IOException
	 */
	public void writeUpdatedInfoInFile() throws IOException {
		// writing updated information into file
	    XMLOutputter  xmlOutputter = MxXMLUtils.getOutputter(true,DataConstants.CONSTANT_UTF_8);
	    BufferedWriter buf= new BufferedWriter(new FileWriter(fMarkupXML));
	    xmlOutputter.output(docXML, buf);
	    buf.flush();
	    buf.close();
	}
	//START: Added for DTCLD-718: For creation and checkin of Combined XML file
	/**
	* @param file
	* @return Document
	* @throws Exception
	*/
	public static Document loadXMLDocument(File file) throws Exception {
        SAXBuilder saxBuilder = new SAXBuilder();
        return saxBuilder.build(file);
    }

	/**
	* This method clones a given xml, in this case the reference xml  
	* @param originalDocument
	* @return Document
	*/
	public static Document cloneXMLDocument(Document originalDocument) {
        return new Document((Element) originalDocument.getRootElement().clone());
    }

	/**
	* This method copies the content under the root tag of an xml into another xml, in this case copies the contents of Simple xml into Combined xml
	* @param targetDocument
	* @param sourceDocument
	*/
	public static void mergeXMLDocuments(Document targetDocument, Document sourceDocument) {
        Element rootElementTarget = targetDocument.getRootElement();
        List<Element> elementsUnderRootSource = sourceDocument.getRootElement().getChildren();

        for (Element element : elementsUnderRootSource) {
            rootElementTarget.addContent((Element) element.clone());
        }
    }

	/**
	* This method writes content into an xml and saves it, in this case the Combined xml
	* @param document
	* @param outputFile
	* @throws IOException
	*/
	public static void writeXMLDocument(Document document, File outputFile) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        xmlOutputter.output(document, fileWriter);
        fileWriter.close();
    }
	
	/**
	* Helper method to check if the Combined xml file exists in the given directory
	* @param fileDir
	* @param fileName
	* @return boolean
	*/
	public static boolean checkIfCombinedXmlExists(String fileDir, String fileName) {
	    File combinedXmlFile = getCombinedXmlFile(fileDir, fileName);
	    return combinedXmlFile.exists();
	}
	
	/**
	* Helper method to get the Combined xml file existing in the given directory
	* @param fileDir
	* @param fileName
	* @return File
	*/
	public static File getCombinedXmlFile(String fileDir, String fileName) {
	    return new File(new StringBuilder().append(fileDir).append(DataConstants.FORWARD_SLASH).append(fileName).toString());
	}
	
	/**
	* This method prepends the content of a given xml into another, in this case prepends the tags of Reference xml into Combined xml
	* @param combinedXmlDoc
	* @param clonedDoc
	*/
	public static void prependToCombinedXml(Document combinedXmlDoc, Document clonedDoc) {
	    Element rootElementCombined = clonedDoc.getRootElement();
	    List<Element> elementsUnderRootCloned = combinedXmlDoc.getRootElement().getChildren();

	    for (Element element : elementsUnderRootCloned) {
	        rootElementCombined.addContent( (Element) element.clone());
	    }
	}
	//END: Added for DTCLD-718: For creation and checkin of Combined XML file 
}
