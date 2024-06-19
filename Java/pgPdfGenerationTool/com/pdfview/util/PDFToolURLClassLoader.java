/*
Java File Name: URLClassLoader
Clone From/Reference: NA
Purpose: This file is used to load the jar
*/

package com.pdfview.util;

import java.net.URLClassLoader;

public class PDFToolURLClassLoader {

	private static URLClassLoader _classLoader = null;

	
	/**
	 * @description: Method to get Class Loader
	
	 * @return URLClassLoader
	 */
	public static URLClassLoader getClassLoader() {
		return _classLoader;
	}
}
