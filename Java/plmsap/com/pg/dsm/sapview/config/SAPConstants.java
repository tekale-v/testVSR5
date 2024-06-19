package com.pg.dsm.sapview.config;

import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class SAPConstants {
	/**
	 * 
	 */
	private SAPConstants() {
	}

	public static final String SELECT_ATTRIBUTE_PGBOMEDELIVERY = "attribute["
			+ SAPViewConstant.ATTRIBUTE_PGBOMEDELIVERY.getValue() + "]";
	public static final String SELECT_ATTRIBUTE_PGBOMEDELIVERYPARENT = "attribute["
			+ SAPViewConstant.ATTRIBUTE_PGBOMEDELIVERYPARENT.getValue() + "]";
	public static final String ATTRIBUTE_PGCONFIGCOMMON_SAPCRONISACTIVE = "pgConfigCommonCOSIsActive";
	public static final String ATTRIBUTE_PGCONFIGCOMMON_SAPCRONRETRYCOUNT = "pgConfigCommonCOSRetryCount";
	public static final String ATTRIBUTE_PGCONFIGCOMMON_SAPCRONADMINMAILID = "pgConfigCommonCOSAdminMailId";
	public static final String RELATIONSHIP_FBOM_SUBSTITUTE_ID = "to[FBOM].from.to[FBOM Substitute].fromrel.from.id";
	public static final String RELATIONSHIP_FBOM_SUBSTITUTE_TYPE = "to[FBOM].from.to[FBOM Substitute].fromrel.from.type";
	public static final String RELATIONSHIP_FBOM_SUBSTITUTE_CURRENT = "to[FBOM].from.to[FBOM Substitute].fromrel.from.current";	
	//Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704) - Starts.
	public static final String SELECT_ATTRIBUTE_JCO_CLIENT_SNC_MODE = "attribute[" + PropertyUtil.getSchemaProperty("attribute_pgJcoClientSNCMode") + "]";
	public static final String SELECT_ATTRIBUTE_JCO_CLIENT_SNC_NAME = "attribute[" + PropertyUtil.getSchemaProperty("attribute_pgJcoClientSNCName") + "]";
	public static final String SELECT_ATTRIBUTE_JCO_CLIENT_SNC_SERVICE_LIBRARY = "attribute[" + PropertyUtil.getSchemaProperty("attribute_pgJcoClientSNCServiceLibrary") + "]";
	public static final String SELECT_ATTRIBUTE_JCO_CLIENT_SNC_PARTNER_NAME = "attribute[" + PropertyUtil.getSchemaProperty("attribute_pgJcoClientSNCPartnerName") + "]";
	//Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704) - Ends.
}
