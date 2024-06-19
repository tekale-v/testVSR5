/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: UIPDFType
   Purpose: JAVA class created to for abstract class.
 **/

package com.pg.irm.pdf;

import matrix.db.Context;

public abstract class PDFType implements PDFConstants {

	public abstract void init(Context context, javax.servlet.ServletContext servletContext,
			javax.servlet.jsp.PageContext pageContext, javax.servlet.http.HttpServletRequest servletRequest,
			String timeStamp, String languageStr);
	public abstract String generate(Context context, String[] args) throws Exception;
	
}
