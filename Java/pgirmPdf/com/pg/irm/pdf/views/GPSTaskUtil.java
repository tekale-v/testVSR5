/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: GPSTaskUtil
   Purpose: JAVA class created to do GPS Task Util.
 **/
package com.pg.irm.pdf.views;

import com.matrixone.apps.domain.DomainConstants;

import matrix.util.StringList;

public class GPSTaskUtil {
	
	/**
	 * @return -StringList common attribute select table
	 * @throws Exception
	 */
	public static StringList getSelectableForAttributesTable() throws Exception {

		StringList basic = new StringList();
		basic.addElement(DomainConstants.SELECT_TYPE);
		basic.addElement(DomainConstants.SELECT_NAME);
		basic.addElement(DomainConstants.SELECT_REVISION);
		basic.addElement(DomainConstants.SELECT_DESCRIPTION);
		basic.addElement(DomainConstants.SELECT_OWNER);
		basic.addElement(DomainConstants.SELECT_CURRENT);
		basic.addElement(DomainConstants.SELECT_VAULT);
		basic.addElement("attribute["+DomainConstants.ATTRIBUTE_ORIGINATOR+"]");
		return basic;
	}

}
