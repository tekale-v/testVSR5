package com.pg.enovia.mos.enumeration;

public enum MOSConstants {
	CONFIG_PAGE("MosComponentConfigPage"),
	INTERMEDIATELEVEL("intermediate"),
	PARTLEVEL("part"),
	PARENTPARTLEVEL("parentPart"),
	CONST_SUBMITFORM("submitForm"),
	CONST_SUBMITFORMIPMS("submitFormIPMS"),
	CONST_SUBMITFORMART("submitFormART"),
	CONST_SUBMITFORMFC("submitFormFC"),
	CONST_SUBMITFORMIRMS("submitFormIRMS"),
	ENO_CSRF_TOKEN("ENO_CSRF_TOKEN"),
	RESPONSE_JSON("application/json");
	private final String value;
	MOSConstants(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
