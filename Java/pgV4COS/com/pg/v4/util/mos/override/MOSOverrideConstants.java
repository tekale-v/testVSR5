/*
 **   MOSOverrideConstants.java
 **   Description - Introduced as part of MOS Override - by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
 **
 */
package com.pg.v4.util.mos.override;

import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.v3.custom.pgV3Constants;

/**
 * @author DSM(Sogeti)
 *
 */
public class MOSOverrideConstants {
	
	private MOSOverrideConstants() {}
	
	public static final String ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN = PropertyUtil.getSchemaProperty("attribute_pgHasMOSPartialOverridden");
	public static final String SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN = "attribute["+ ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN + "]";
	public static final String ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE = PropertyUtil.getSchemaProperty("attribute_pgMOSOverrideRequestType");
	public static final String SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE = "attribute["+ ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE + "]";
	public static final String ATTRIBUTE_PGMOSPOAOVERRIDELRR = PropertyUtil.getSchemaProperty("attribute_pgMOSPOAOverrideLRR");
	public static final String SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR = "attribute[" + ATTRIBUTE_PGMOSPOAOVERRIDELRR+ "]";
	public static final String KEY_FULL_OVERRIDE = "Full Override";
	public static final String KEY_POA_OVERRIDE = "POA Override";
	public static final String POLICY_PG_COS_OVERRIDE = PropertyUtil.getSchemaProperty("policy_pgCOSOverride");

	public static final String ATTRIBUTE_OVERRIDE_COUNTRIES = PropertyUtil.getSchemaProperty("attribute_pgCOSOverrideCountries");
	public static final String SELECT_ATTRIBUTE_OVERRIDE_COUNTRIES = "attribute["+ ATTRIBUTE_OVERRIDE_COUNTRIES + "]";
	public static final String MOS_OVERRIDE_VALID_TYPES = new StringBuffer(pgV3Constants.TYPE_FINISHEDPRODUCTPART)
			.append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_FABRICATEDPART)
			.append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART).toString();
}
