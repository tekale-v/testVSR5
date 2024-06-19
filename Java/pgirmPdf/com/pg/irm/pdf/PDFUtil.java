/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: UIPDFUtil
   Purpose: JAVA class created to generate GPAAssesmentTask related pdf file based on type name & object id.
 **/
package com.pg.irm.pdf;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import matrix.db.Context;

public class PDFUtil implements PDFConstants {

	private ServletContext _appCtx = null;
	private PageContext _pageCtx = null;
	private HttpServletRequest _servletReq = null;
	private Context _ctx = null;
	private String _timeStamp = null;
	private String _languageStr = null;

	// Constructor
	public PDFUtil() {
	}

	public void init(Context context, javax.servlet.ServletContext servletContext,
			javax.servlet.jsp.PageContext pageContext, javax.servlet.http.HttpServletRequest servletRequest,
			String timeStamp, String languageStr) {
		this._ctx = context;
		this._appCtx = servletContext;
		this._pageCtx = pageContext;
		this._servletReq = servletRequest;
		this._timeStamp = timeStamp;
		this._languageStr = languageStr;
	}

	/**
	 * @Desc generate GPAAssesmentTask related pdf file based on type name
	 * @param context
	 * @param args
	 * @return -generated file path
	 * @throws Exception
	 */
	public String generate(Context context, String[] args) throws Exception {
		long lStartTime = System.currentTimeMillis();
		String path = EMPTY_STRING;
		try {
			String type = args[3];
			PDFType pdfType = PDFFactory.getPDFType(context, type, args);
			if (pdfType != null) {
				pdfType.init(_ctx, _appCtx, _pageCtx, _servletReq, _timeStamp, _languageStr);
				path = pdfType.generate(_ctx, args);
			}
		} catch (Exception e) {
			throw e;
		}
		long lEndTime = System.currentTimeMillis();
		long lElapsedTime = lEndTime-lStartTime;
		System.out.println("For user - "+context.getUser()+" - IRM PDF Generation Time in milliseconds: ["+TimeUnit.MILLISECONDS.toMillis(lElapsedTime)+" ms] | in seconds: ["+TimeUnit.MILLISECONDS.toSeconds(lElapsedTime)+" sec] | in minutes: ["+TimeUnit.MILLISECONDS.toMinutes(lElapsedTime)+" min]");
		return path;
	}
	/**
	 * @Desc To delete temp folders.
	 * @param context
	 * @param args
	 * @return string
	 * @throws Exception
	 */
	public String deleteTempFolder(Context context, String args[]) throws Exception {
		String strPathToDel = args[0];
		boolean bDel = deleteDir(new File(strPathToDel));
		if (bDel) {
			return CONSTANT_TRUE;
		} else {
			return CONSTANT_FALSE;
		}
	}

	/**
	 * @Desc To delete temp dir.
	 * @param File
	 * @return boolean
	 */
	public boolean deleteDir(File dir) {
		boolean success = false;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	} 

}
