/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: UIPDFFactory
   Purpose: JAVA class created to for abstract class.
 **/

package com.pg.irm.pdf;

import com.pg.irm.pdf.views.GPSTask;

import matrix.db.Context;

public class PDFFactory {

	/**
	 * @Desc method to get type wise class instance
	 * @param context
	 * @param type    -type name
	 * @param args
	 * @return -Object
	 * @throws Exception
	 */
	public static PDFType getPDFType(Context context, String type, String[] args) throws Exception {
		if (PDFConstants.TYPE_GPS_ASSESSMENT_TASK.equals(type)) {
			return new GPSTask();
		}
		return null;
	}

}
