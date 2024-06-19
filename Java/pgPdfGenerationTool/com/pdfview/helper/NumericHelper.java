package com.pdfview.helper;

import java.text.DecimalFormat;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pg.v3.custom.pgV3Constants;

public class NumericHelper {
	/**
	 * Retrieve decimal formatted values
	 * @param strMax
	 * @return
	 */
	static DecimalFormat decimalformatter =null;
	public static String getDecimalFormattedValues(String strMax) {
		if(decimalformatter==null) {
			decimalformatter = new DecimalFormat(PDFConstant.PATTERN_DECIMALFORMAT);
		}
		if (UIUtil.isNotNullAndNotEmpty(strMax)) {
			int integerPlaces = strMax.indexOf(pgV3Constants.SYMBOL_DOT);
			int decimalPlaces = strMax.length() - integerPlaces - 1;
			if (decimalPlaces > 6) {
				strMax = String.valueOf(decimalformatter.format(Double.parseDouble(strMax)));
			}
		} else {
			strMax = DomainConstants.EMPTY_STRING;
		}
		return strMax;
	}
}
