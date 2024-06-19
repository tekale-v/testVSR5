package com.pdfview.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;

import matrix.db.Context;
import matrix.util.MatrixException;

public class CalenderHelper {
	Context _context = null;
	String DATE_FORMAT =DomainConstants.EMPTY_STRING;
	
	CalenderHelper(Context context){
		_context=context;
		try {
			DATE_FORMAT=EnoviaResourceBundle.getProperty(_context, "eServiceSuiteCPN.defaultDateFormat");
		} catch (FrameworkException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Retrieve Formatted date
	 * @param strDate -date in string format
	 * @return String -well formated date
	 * @throws MatrixException
	 */
	public String getFormattedDate(String strDate) throws MatrixException {

		SimpleDateFormat formatter = null;
		Date tmpDate = null;
		String formatedDate = null;
		StringBuffer sbformatedDate = new StringBuffer();
		try {
			if (!UIUtil.isNullOrEmpty(strDate) && (!strDate.contains("DENIED"))) {
				if (strDate.contains(PDFConstant.DYNAMIC_ROW_SEPERATOR)) {
					String[] strDates = strDate.split(PDFConstant.DYNAMIC_ROW_SEPERATOR);
					int size=strDates.length;
					for (int i = 0; i < size; i++) {
						strDate = strDates[i];
						formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
						tmpDate = formatter.parse(strDate);
						formatter = new SimpleDateFormat(DATE_FORMAT);
						formatedDate = formatter.format(tmpDate);
						sbformatedDate.append(formatedDate).append(PDFConstant.DYNAMIC_ROW_SEPERATOR);
					}
				} else {
					formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
					tmpDate = formatter.parse(strDate);
					formatter = new SimpleDateFormat(DATE_FORMAT);
					formatedDate = formatter.format(tmpDate);
					sbformatedDate.append(formatedDate);
				}

			} else {
				return DomainConstants.EMPTY_STRING;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sbformatedDate.toString();
	}
}
