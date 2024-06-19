package com.pg.dsm.ws.enumeration;

/**
 * @author DSM(Sogeti)
 *
 */
public enum DSMConstants {
	ENO_CSRF_TOKEN("ENO_CSRF_TOKEN"), 
	RESPONSE_JSON("application/json"), 
	OBJECT_ID("objectId"),
	PROGRAM_NAME("program"),
	TABLE_CONFIG("tableconfig"),
	OBJECT_LIST("objectList"),
	LICENSE_KEY("license"),
	MOS_CONFIG_OBJECT_TYPE("pgConfigurationAdmin"),
	MOS_CONFIG_OBJECT_NAME("pgAgGridConfiguration"),
	MOS_CONFIG_OBJECT_REVISION("-"),
	AG_GRID_LINCENSE_ATTRIBUTE("pgConfigCommonAttr"),
	OBJECT("object"),
	ACTION_SERVICE("actionservice"),
	COMMAND("command");
	private final String value;

	DSMConstants(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
