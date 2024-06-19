/*Created by DSM for Requirement 47389 2022x.04 Dec CW 2023
 Purpose: Contains constant values for on-demand GenDoc Generation
*/
package com.pg.dsm.gendocondemand.config;

import com.matrixone.apps.domain.util.PropertyUtil;


public class GenDocConstants {
	
	private GenDocConstants() {
	}
		public static final String ATTRIBUTE_PGCONFIGISACTIVE = PropertyUtil.getSchemaProperty(null,"attribute_pgConfigCommonIsActive");
		public static final String ATTRIBUTE_PGCONFIGCOMMONADMINMAILID = PropertyUtil.getSchemaProperty(null,"attribute_pgConfigCommonAdminMailId");
		public static final String ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT = PropertyUtil.getSchemaProperty(null,"attribute_pgConfigCommonRetryCount");
		public static final String ATTRIBUTE_PGCONFIGCOMMONDSTARTEDDATE = PropertyUtil.getSchemaProperty(null,"attribute_pgConfigCommonStartedDate");
		
}		
