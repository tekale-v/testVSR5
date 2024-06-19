/*
Java File Name: DHUtility
Clone From/Reference: NA
Purpose: This file is used to validate Input text
*/

package com.pdfview.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.text.BadLocationException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.exception.PDFToolBusyException;
import com.pdfview.exception.PDFToolCustomException;
import com.pdfview.exception.PDFToolWebServiceException;
import com.pg.v3.custom.pgV3Constants;

public class DHUtility {

	/**
	 * @description: Method to check empty value
	 * 
	 * @param text
	 * @return boolean
	 */
	public static boolean isEmptyText(String text) {
		return StringUtils.isBlank(text);
	}

	/**
	 * @description: Method to check valid text
	 * 
	 * @param text
	 * @return boolean
	 */
	public static boolean isValidText(String text) {
		return !StringUtils.isBlank(text);
	}

	/**
	 * @description: Method to check valid map
	 * 
	 * @param map
	 * @return boolean
	 */
	public static boolean isValidMap(Map map) {
		boolean isValidMap = false;
		if (null != map && !map.isEmpty()) {
			isValidMap = true;
		}
		return isValidMap;
	}

	/**
	 * @description: Method to check valid test
	 * 
	 * @param texts
	 * @return boolean
	 */
	public static boolean validateTextList(String... texts) {
		boolean result = true;
		for (String text : texts) {
			if (isEmptyText(text)) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * @description: Method to check validate list
	 * 
	 * @param list
	 * @return boolean
	 */
	public static boolean validateList(List list) {
		boolean result = false;
		if (null != list && !list.isEmpty()) {
			result = true;
		}
		return result;
	}

	/**
	 * @description: Method to get end time
	 * 
	 * @param start
	 * @return double
	 */
	public static double getEndTimeInSec(long start) {
		long finishTime = System.nanoTime() - start;
		double endInSecs = finishTime / 1e9;
		double endRounOff = Math.round(endInSecs * 100000.0) / 100000.0;
		return endRounOff;
	}

	/**
	 * @description: Method to get end time
	 * 
	 * @param start
	 * @return long
	 */
	public static long getEndTimeMiliSec(long start) {
		long finishTime = System.currentTimeMillis() - start;
		return finishTime;
	}

	/**
	 * @description: Method to get date in gregorian Format
	 * 
	 * @param date
	 * @return String
	 */
	public static String gregorianFormat(String date) throws Exception {
		String value = DomainConstants.EMPTY_STRING;
		if (!isEmptyText(date)) {
			value = formatDate(date, "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd hh:mm:ss a");
		}
		return value;
	}

	/**
	 * @description: Method to get format date
	 * 
	 * @param date
	 * @param initDateFormat
	 * @param endDateFormat
	 * @return String
	 * @throws Exception
	 */
	public static String formatDate(String date, String initDateFormat, String endDateFormat) throws Exception {

		if (UIUtil.isNullOrEmpty(date))
			return null;

		Date initDate = null;
		try {
			initDate = parseDate(date);
		} catch (Exception e) {

			throw e;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
		String parsedDate = formatter.format(initDate);

		return parsedDate;
	}

	/**
	 * @description: Method added to parse any possible date formats.
	 * 
	 * @param strDate
	 * @return Date
	 * @throws Exception
	 */
	public static Date parseDate(String strDate) throws Exception {
		if (strDate != null && !strDate.isEmpty()) {
			SimpleDateFormat[] formats = new SimpleDateFormat[] { new SimpleDateFormat("MM-dd-yyyy"),
					new SimpleDateFormat("yyyyMMdd"), new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a"),
					new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a"),
					new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a") };

			Date parsedDate = null;
			int iformatslength=formats.length;
			for (int i = 0; i < iformatslength; i++) {
				try {
					parsedDate = formats[i].parse(strDate);
					return parsedDate;
				} catch (ParseException e) {
					continue;
				}
			}
		}
		throw new Exception("Unknown date format: '" + strDate + "'");
	}

	/**
	 * @description: Method added to set Date Formats.
	 * 
	 * @param isElementDate
	 * @param value
	 * @return Object
	 */
	public static Object setDateFormate(String isElementDate, Object value) {
		String date = DomainConstants.EMPTY_STRING;
		if (pgV3Constants.TRUE.equalsIgnoreCase(isElementDate) && null != value) {
			try {
				date = DHUtility.gregorianFormat(value.toString());
			} catch (Exception exception) {
				exception.getMessage();
			}
		} else {
			return value;
		}
		return date;
	}

	/**
	 * @description: Method added to get error code.
	 * 
	 * @param exception
	 * @return String
	 */
	public static String getErrorCode(Object exception) {
		String result = ErrorCodeConstants.E_ZERO_ZERO_ZERO;
		if (exception instanceof JAXBException) {
			result = ErrorCodeConstants.E_SEVEN_ZERO_ZERO;
		} else if (exception instanceof IOException) {
			result = ErrorCodeConstants.E_SIX_ZERO_ZERO;
		} else if (exception instanceof ClassNotFoundException) {
			result = ErrorCodeConstants.E_EIGHT_ZERO_ZERO;
		} else if (exception instanceof MatrixException) {
			result = ErrorCodeConstants.E_FIVE_ZERO_ZERO;
		} else if (exception instanceof TransformerException) {
			result = ErrorCodeConstants.E_FOUR_ONE_ZERO;
		} else if (exception instanceof NoSuchMethodException) {
			result = ErrorCodeConstants.E_FOUR_TWO_ZERO;
		} else if (exception instanceof InstantiationException) {
			result = ErrorCodeConstants.E_FOUR_THREE_ZERO;
		} else if (exception instanceof IllegalAccessException) {
			result = ErrorCodeConstants.E_FOUR_FOUR_ZERO;
		} else if (exception instanceof InvocationTargetException) {
			result = ErrorCodeConstants.E_FOUR_FIVE_ZERO;
		} else if (exception instanceof SecurityException) {
			result = ErrorCodeConstants.E_FOUR_SIX_ZERO;
		} else if (exception instanceof IllegalArgumentException) {
			result = ErrorCodeConstants.E_FOUR_SEVEN_ZERO;
		} else if (exception instanceof SQLException) {
			result = ErrorCodeConstants.E_FOUR_EIGHT_ZERO;
		} else if (exception instanceof PDFToolBusyException) {
			result = ErrorCodeConstants.E_ZERO_ONE_TWO;
		} else if (exception instanceof NoSuchFieldException) {
			result = ErrorCodeConstants.E_FOUR_NINE_ZERO;
		} else if (exception instanceof NoSuchAlgorithmException) {
			result = ErrorCodeConstants.E_FIVE_ONE_ZERO;
		} else if (exception instanceof NoSuchProviderException) {
			result = ErrorCodeConstants.E_FIVE_TWO_ZERO;
		} else if (exception instanceof InvalidKeyException) {
			result = ErrorCodeConstants.E_FIVE_THREE_ZERO;
		} else if (exception instanceof FileNotFoundException) {
			result = ErrorCodeConstants.E_FIVE_FOUR_ZERO;
		} else if (exception instanceof UnsupportedEncodingException) {
			result = ErrorCodeConstants.E_FIVE_FIVE_ZERO;
		} else if (exception instanceof NoSuchPaddingException) {
			result = ErrorCodeConstants.E_FIVE_SIX_ZERO;
		} else if (exception instanceof IllegalBlockSizeException) {
			result = ErrorCodeConstants.E_FIVE_SEVEN_ZERO;
		} else if (exception instanceof BadPaddingException) {
			result = ErrorCodeConstants.E_FIVE_EIGHT_ZERO;
		} else if (exception instanceof DatatypeConfigurationException) {
			result = ErrorCodeConstants.E_FIVE_NINE_ZERO;
		} else if (exception instanceof ParseException) {
			result = ErrorCodeConstants.E_SIX_ONE_ZERO;
		} else if (exception instanceof BadLocationException) {
			result = ErrorCodeConstants.E_SIX_ONE_FIVE;
		} else if (exception instanceof PDFToolWebServiceException) {
			result = ErrorCodeConstants.E_SIX_TWO_ZERO;
		} else if (exception instanceof PDFToolCustomException) {
			result = ErrorCodeConstants.E_SIX_THREE_ZERO;
		} else if (exception instanceof ArrayIndexOutOfBoundsException) {
			result = ErrorCodeConstants.E_FIVE_FOUR_ZERO;
		}
		return result;
	}
}