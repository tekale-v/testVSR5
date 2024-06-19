/*
Java File Name: AppPathUtility
Clone From/Reference: NA
Purpose: This file is used for Setting the AppPAth
*/

package com.pdfview.util;

import java.io.File;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import matrix.db.Context;


public class AppPathUtility {

	//22x Upgrade Modification Start
	public static final String CONSTANT_APP_CPN = "emxCPN";
	public static final String CONSTANT_SRV_PATH = "emxCPN.ServerPath";
	public static final String CONST_PDF_BASE_FOLDER = "pdfHtmlBase";
	//22x Upgrade Modification End
	
	/**
	 * @description: Method used to get the working directory
	 * 
	 * @return String
	 */
	public static String getConfigDirectory(Context context) {
		String strPath=DomainConstants.EMPTY_STRING;
		try {
			//22x Upgrade Modification Start
			strPath=new StringBuilder(EnoviaResourceBundle.getProperty(context, CONSTANT_APP_CPN, context.getLocale(),CONSTANT_SRV_PATH)).append(File.separator).append(CONST_PDF_BASE_FOLDER).toString();
			//22x Upgrade Modification End
		}catch (Exception e) {
			e.printStackTrace();
		}
		return strPath;
	}
}
