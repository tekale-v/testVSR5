package com.pdfview.dataExtract;

import java.io.File;
import java.nio.file.Files;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.i18nNow;

import matrix.util.StringList;

public class PDFViewFileExtract {
	i18nNow i18nObject = new i18nNow();

	/**
	 * @description: This is a sub method invoked by getPDF() - to delete PDF View
	 *               from temp location
	 * 
	 * @param fSource : temp source file
	 * @return boolean
	 */
	public boolean deleteTempPDF(File fSource) {
		boolean bFlag = false;
		try {
			if (!fSource.isFile()) {
				for (File f : fSource.listFiles()) {
					f.delete();
				}
			}
			bFlag = Files.deleteIfExists(fSource.toPath());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bFlag;
	}

	/**
	 * @description: This method is used to build the selectables
	 * 
	 * @param
	 * @return StringList
	 */
	public StringList getBasicObjectSelectList() {
		StringList slObjectSelects = new StringList(4);
		slObjectSelects.addElement(DomainConstants.SELECT_ID);
		slObjectSelects.addElement(DomainConstants.SELECT_TYPE);
		slObjectSelects.addElement(DomainConstants.SELECT_NAME);
		slObjectSelects.addElement(DomainConstants.SELECT_REVISION);
		return slObjectSelects;
	}

}
