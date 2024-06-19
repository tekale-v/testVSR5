/*
Java File Name: DataHandlerConfigurator
Clone From/Reference: NA
Purpose:  This File is used for generate XML file
*/

package com.pdfview.framework;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import com.pdfview.exception.PDFToolCustomException;

import matrix.util.MatrixException;

public interface PDFToolConfigurator {
	public void setRequestedComponents(List<String> alRequestedComponents);
	public String getTableNamesData();
	public String generateXML() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, JAXBException, 
	NoSuchFieldException,MatrixException, SQLException,TransformerException,PDFToolCustomException;
	
}
