/*
Java File Name: CDataContentHandler
Clone From/Reference: NA
Purpose: This file is used to Commenting the XML Character
*/

package com.pdfview.util;

import java.io.Writer;
import java.util.regex.Pattern;

//import org.apache.xml.serialize.XMLSerializer;

@SuppressWarnings("deprecation")
public class CDataContentHandler /*extends XMLSerializer */{

	private static final Pattern XML_CHARS = Pattern.compile("[<>]");

	/**
	 * @description: Constructor to initialize values
	 * 
	 * @param writer
	 */
	public CDataContentHandler(Writer writer) {

		//this.setOutputCharStream(writer);
	}

}